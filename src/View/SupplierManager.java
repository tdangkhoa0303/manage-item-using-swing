package View;

import Entity.Supplier;
import Service.SupplierService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SupplierManager extends JPanel {
    TableModel<Supplier> SupplierTableModel;
    SupplierService SupplierService = new SupplierService();

    boolean isNew = true;

    JTable tblSupplier;

    JButton btnSupplierNew;
    JButton btnSupplierSave;
    JButton btnSupplierRemove;
    JButton btnSupplierPrev;
    JButton btnSupplierNext;

    JTextField txtSupplierCode;
    JTextField txtSupplierName;
    JTextField txtSupplierAddress;
    JCheckBox txtSupplierCollaborating;
    JTextField txtSupplierPage;

    public SupplierManager() {
        int[] indexes = {0, 1, 2, 3};
        String[] headers = {"Code", "Name", "Address", "Collaborating"};
        SupplierTableModel = new TableModel<>(headers, indexes) {
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (rowIndex < 0 || rowIndex >= this.pageSize || columnIndex < 0 || columnIndex >= headers.length)
                    return null;
                Supplier Supplier = data.get(rowIndex + (currentPage - 1) * pageSize);
                switch (columnIndex) {
                    case 0:
                        return Supplier.getCode();
                    case 1:
                        return Supplier.getName();
                    case 2:
                        return Supplier.getAddress();
                    case 3:
                        return Supplier.isCollaborating();
                }
                return null;
            }
        };
        try {
            SupplierTableModel.setData(SupplierService.getAllSupplier());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initComponent();

        btnSupplierNext.addActionListener(e -> {
            SupplierTableModel.setCurrentPage(SupplierTableModel.getCurrentPage() + 1);
            tblSupplier.updateUI();
        });

        btnSupplierPrev.addActionListener(e -> {
            SupplierTableModel.setCurrentPage(SupplierTableModel.getCurrentPage() - 1);
            tblSupplier.updateUI();
        });

        txtSupplierPage.addActionListener(e -> {
            int page = SupplierTableModel.getCurrentPage();
            try {
                page = Integer.parseInt(txtSupplierPage.getText().trim());
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Page number must be a positive number");
            }
            SupplierTableModel.setCurrentPage(page);
            tblSupplier.updateUI();
        });

        btnSupplierNew.addActionListener(e -> {
            txtSupplierCode.setText("");
            txtSupplierCode.requestFocus();
            txtSupplierName.setText("");
            txtSupplierAddress.setText("");

            txtSupplierCollaborating.setSelected(false);
            isNew = true;
            try {
                SupplierTableModel.setData(SupplierService.getAllSupplier());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnSupplierRemove.addActionListener(e -> {
            int selectedRow = tblSupplier.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row to remove.");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Are you sure to remove row " + (selectedRow + 1) + "?", "Remove Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String code = (String) SupplierTableModel.getValueAt(selectedRow, indexes[0]);
                try {
                    SupplierService.deleteSupplier(code);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                SupplierTableModel.getData().remove(selectedRow + (SupplierTableModel.getCurrentPage() - 1) * SupplierTableModel.getPageSize());
                tblSupplier.updateUI();
            }
        });

        tblSupplier.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblSupplier.getSelectedRow();
                txtSupplierCode.setText(tblSupplier.getValueAt(selectedRow, indexes[0]).toString());
                txtSupplierName.setText(tblSupplier.getValueAt(selectedRow, indexes[1]).toString());
                txtSupplierName.requestFocus();
                txtSupplierAddress.setText(tblSupplier.getValueAt(selectedRow, indexes[2]).toString());
                txtSupplierCollaborating.setSelected((Boolean) tblSupplier.getValueAt(selectedRow, indexes[3]));
                txtSupplierCode.setEditable(false);
                isNew = false;
            }
        });

        btnSupplierSave.addActionListener(e -> {
            StringBuilder errors = new StringBuilder();
            String code = txtSupplierCode.getText().trim();
            if (isNew) {
                for (Supplier supplier : SupplierTableModel.getData())
                    if (supplier.getCode().equals(code)) {
                        errors.append("The code [").append(code).append("] is existed.\n");
                        txtSupplierCode.requestFocus();
                    }

            }
            String name = txtSupplierName.getText().trim();
            if (!name.matches("[a-zA-Z0-9\\s]{3,30}")) {
                errors.append("Invalid name [digits & alpha characters only, min: 3, max: 30]\n");
                txtSupplierName.requestFocus();
            }
            String address = txtSupplierAddress.getText().trim();
            int price = 0;

            boolean isCollaborating = txtSupplierCollaborating.isSelected();
            if (errors.length() > 0) {
                JOptionPane.showMessageDialog(this, errors);
                return;
            }
            if (isNew) {
                Supplier supplier = new Supplier(code, name, address, isCollaborating);
                try {
                    SupplierService.insertSupplier(supplier);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                SupplierTableModel.getData().add(supplier);
            } else {
                int selectedRow = tblSupplier.getSelectedRow();
                Supplier supplier = new Supplier(code, name, address, isCollaborating);
                try {
                    SupplierService.updateSupplier(supplier);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                SupplierTableModel.getData().set((selectedRow + (SupplierTableModel.getCurrentPage() - 1) * 5), supplier);
            }
            tblSupplier.updateUI();
        });
    }

    void initComponent() {
        JPanel left_panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Table Area
        tblSupplier = new JTable(SupplierTableModel);
        tblSupplier.setPreferredScrollableViewportSize(new Dimension(500, 150));
        tblSupplier.setRowHeight(30);
        tblSupplier.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane tablePanel = new JScrollPane(tblSupplier);
        tablePanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Table Page Navigator
        JPanel tableNav = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSupplierNext = new JButton("Next");
        btnSupplierPrev = new JButton("Previous");
        txtSupplierPage = new JTextField(" 1");
        txtSupplierPage.setPreferredSize(new Dimension(30, 30));
        JLabel lbPage = new JLabel("/ " + SupplierTableModel.getPageCount());
        tableNav.add(btnSupplierPrev);
        tableNav.add(txtSupplierPage);
        tableNav.add(lbPage);
        tableNav.add(btnSupplierNext);

        // Task Button
        JPanel button_container = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSupplierNew = new JButton("New");
        btnSupplierSave = new JButton("Save");
        btnSupplierRemove = new JButton("Remove");
        button_container.add(btnSupplierNew);
        button_container.add(btnSupplierSave);
        button_container.add(btnSupplierRemove);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        left_panel.add(tablePanel, gbc);

        gbc.gridy = 1;
        gbc.ipady = 10;
        left_panel.add(tableNav, gbc);

        gbc.gridy = 2;
        gbc.ipady = 10;
        left_panel.add(button_container, gbc);

        txtSupplierCode = new JTextField();
        txtSupplierName = new JTextField();
        txtSupplierAddress = new JTextField();
        txtSupplierCollaborating = new JCheckBox("Collaborating", false);


        JLabel[] labels = {new JLabel("Code"), new JLabel("Name"), new JLabel("Address"), new JLabel("Collaborating")};
        JPanel right_panel = reusableComponent.inputComponent(labels, txtSupplierCode, txtSupplierName, txtSupplierAddress, txtSupplierCollaborating);

        this.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
        this.add(left_panel);
        this.add(right_panel);
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
    }
}


