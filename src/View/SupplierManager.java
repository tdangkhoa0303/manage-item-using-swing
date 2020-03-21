package View;

import Entity.Supplier;
import Service.SupplierService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SupplierManager extends JPanel {
    TableModel<Supplier> supplierTableModel;
    SupplierService SupplierService = new SupplierService();

    boolean isNew = true;

    JTable tblSupplier;

    JButton btnSupplierNew;
    JButton btnSupplierSave;
    JButton btnSupplierRemove;
    JButton btnSupplierPrev;
    JButton btnSupplierNext;

    JTextField txtSupplierPage;
    JTextField txtSupplierCode;
    JTextField txtSupplierName;
    JTextField txtSupplierAddress;
    JCheckBox txtSupplierCollaborating;
    JLabel lbPage;

    public SupplierManager(JComboBox<String> txtSupCode) {
        int[] indexes = {0, 1, 2, 3};
        String[] headers = {"Code", "Name", "Address", "Colloborating"};
        supplierTableModel = new TableModel<>(headers, indexes) {
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (rowIndex < 0 || rowIndex >= this.data.size() || columnIndex < 0 || columnIndex >= headers.length)
                    return null;
                int index = rowIndex + (currentPage - 1) * pageSize;
                if (index >= data.size()) return null;
                Supplier supplier = data.get(index);
                switch (columnIndex) {
                    case 0:
                        return supplier.getCode();
                    case 1:
                        return supplier.getName();
                    case 2:
                        return supplier.getAddress();
                    case 3:
                        return supplier.isCollaborating();
                }
                return null;
            }
        };
        try {
            supplierTableModel.setData(SupplierService.getAllSupplier());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initComponent();

        btnSupplierNext.addActionListener(e -> {
            supplierTableModel.setCurrentPage(supplierTableModel.getCurrentPage() + 1);
            txtSupplierPage.setText(" " + supplierTableModel.getCurrentPage() + "");
            tblSupplier.updateUI();
        });

        btnSupplierPrev.addActionListener(e -> {
            supplierTableModel.setCurrentPage(supplierTableModel.getCurrentPage() - 1);
            txtSupplierPage.setText(" " + supplierTableModel.getCurrentPage() + "");
            tblSupplier.updateUI();
        });

        txtSupplierPage.addActionListener(e -> {
            int page = supplierTableModel.getCurrentPage();
            try {
                page = Integer.parseInt(txtSupplierPage.getText().trim());
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Page number must be a positive number");
            }
            supplierTableModel.setCurrentPage(page);
            tblSupplier.updateUI();
        });

        btnSupplierNew.addActionListener(e -> {
            txtSupplierCode.setText("");
            txtSupplierCode.setEditable(true);
            txtSupplierCode.requestFocus();
            txtSupplierName.setText("");
            txtSupplierAddress.setText("");
            txtSupplierCollaborating.setSelected(false);
            isNew = true;
        });

        btnSupplierRemove.addActionListener(e -> {
            int selectedRow = tblSupplier.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row to remove.");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Are you sure to remove row " + (selectedRow + 1) + "?", "Remove Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String code = (String) supplierTableModel.getValueAt(selectedRow, indexes[0]);
                supplierTableModel.getData().remove(selectedRow + (supplierTableModel.getCurrentPage() - 1) * supplierTableModel.getPageSize());
                txtSupCode.removeItem(code);
                try {
                    SupplierService.deleteSupplier(code);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                lbPage.setText("/ " + supplierTableModel.getPageCount());
                if (Integer.parseInt(txtSupplierPage.getText().trim()) > supplierTableModel.getPageCount()) {
                    txtSupplierPage.setText(" " + supplierTableModel.getPageCount());
                    supplierTableModel.setCurrentPage(supplierTableModel.getPageCount());
                }
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
                for (Supplier supplier : supplierTableModel.getData())
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
                supplierTableModel.getData().add(supplier);
                lbPage.setText("/ " + supplierTableModel.getPageCount());
                txtSupCode.addItem(code);
                try {
                    SupplierService.insertSupplier(supplier);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                int selectedRow = tblSupplier.getSelectedRow();
                Supplier supplier = new Supplier(code, name, address, isCollaborating);
                supplierTableModel.getData().set((selectedRow + (supplierTableModel.getCurrentPage() - 1) * 5), supplier);
                try {
                    SupplierService.updateSupplier(supplier);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            btnSupplierNew.doClick();
            tblSupplier.updateUI();
        });
    }

    void initComponent() {
        JPanel left_panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Table Area
        tblSupplier = new JTable(supplierTableModel);
        tblSupplier.setPreferredScrollableViewportSize(new Dimension(500, 140));
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
        lbPage = new JLabel("/ " + supplierTableModel.getPageCount());
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
        txtSupplierCollaborating = new JCheckBox("Colloborating", false);


        JLabel[] labels = {new JLabel("Code"), new JLabel("Name"), new JLabel("Address"), new JLabel("Colloborating")};
        JPanel right_panel = reusableComponent.inputComponent(labels, txtSupplierCode, txtSupplierName, txtSupplierAddress, txtSupplierCollaborating);

        this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        left_panel.setPreferredSize(new Dimension(550, 300));
        right_panel.setBorder(new TitledBorder("Supplier Detail"));
        right_panel.setPreferredSize(new Dimension(400, 320));
        this.add(left_panel);
        this.add(right_panel);
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
    }
}


