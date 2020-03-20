package View;

import Entity.Item;
import Service.ItemService;
import Service.SupplierService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Vector;

public class ItemManager extends JPanel {
    TableModel<Item> itemTableModel;
    ItemService itemService = new ItemService();
    SupplierService supplierService = new SupplierService();

    boolean isNew = true;
    boolean isFind = false;

    JTable tblItem;

    JButton btnItemNew;
    JButton btnItemSave;
    JButton btnItemRemove;
    JButton btnItemFind;
    JButton btnItemPrev;
    JButton btnItemNext;

    JTextField txtItemCode;
    JTextField txtItemName;
    JComboBox<String> txtItemSupCode;
    JTextField txtItemUnit;
    JTextField txtItemPrice;
    JCheckBox txtItemSupplying;
    JLabel lbPage;
    JTextField txtItemPage;

    public ItemManager() {
        int[] indexes = {0, 1, 2, 3, 4, 5};
        String[] headers = {"Code", "Name", "Supplier Code", "Unit", "Price", "Supplying"};
        itemTableModel = new TableModel<>(headers, indexes) {
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (rowIndex < 0 || rowIndex >= this.pageSize || columnIndex < 0 || columnIndex >= headers.length)
                    return null;
                int index = rowIndex + (currentPage - 1) * pageSize;
                if (index >= data.size()) return null;
                Item item = data.get(index);
                switch (columnIndex) {
                    case 0:
                        return item.getCode();
                    case 1:
                        return item.getName();
                    case 2:
                        return item.getSupCode();
                    case 3:
                        return item.getUnit();
                    case 4:
                        return item.getPrice();
                    case 5:
                        return item.isSupplying();
                }
                return null;
            }
        };
        try {
            itemTableModel.setData(itemService.getAllItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initComponent();

        btnItemNext.addActionListener(e -> {
            itemTableModel.setCurrentPage(itemTableModel.getCurrentPage() + 1);
            txtItemPage.setText(" " + itemTableModel.getCurrentPage() + "");
            tblItem.updateUI();
        });

        btnItemPrev.addActionListener(e -> {
            itemTableModel.setCurrentPage(itemTableModel.getCurrentPage() - 1);
            txtItemPage.setText(" " + itemTableModel.getCurrentPage() + "");
            tblItem.updateUI();
        });

        txtItemPage.addActionListener(e -> {
            int page = itemTableModel.getCurrentPage();
            try {
                page = Integer.parseInt(txtItemPage.getText().trim());
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Page number must be a positive number");
            }
            itemTableModel.setCurrentPage(page);
            tblItem.updateUI();
        });

        btnItemFind.addActionListener(e -> {
            String keyword = txtItemName.getText().trim();
            try {
                Vector<Item> result = itemService.searchItemByName(keyword);
                if (result.size() > 0) {
                    itemTableModel.setData(result);
                    txtItemPage.setText(" 1");
                    lbPage.setText("/ " + itemTableModel.getPageCount());

                } else JOptionPane.showMessageDialog(this, "No item has been found!");
                tblItem.updateUI();
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            isFind = true;
        });

        btnItemNew.addActionListener(e -> {
            txtItemCode.setText("");
            txtItemCode.requestFocus();
            txtItemName.setText("");
            txtItemSupCode.setSelectedIndex(0);
            txtItemUnit.setText("");
            txtItemPrice.setText("");
            txtItemSupplying.setText("");
            isNew = true;
            if (isFind) {
                try {
                    itemTableModel.setData(itemService.getAllItem());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnItemRemove.addActionListener(e -> {
            int selectedRow = tblItem.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row to remove.");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Are you sure to remove row " + (selectedRow + 1) + "?", "Remove Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String code = (String) itemTableModel.getValueAt(selectedRow, indexes[0]);
                itemTableModel.getData().remove(selectedRow + (itemTableModel.getCurrentPage() - 1) * itemTableModel.getPageSize());
                try {
                    itemService.deleteItem(code);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                lbPage.setText("/ " + itemTableModel.getPageCount());
                if (Integer.parseInt(txtItemPage.getText().trim()) > itemTableModel.getPageCount())
                    txtItemPage.setText(" " + itemTableModel.getPageCount());
                tblItem.updateUI();
            }
        });

        tblItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblItem.getSelectedRow();
                txtItemCode.setText(tblItem.getValueAt(selectedRow, indexes[0]).toString());
                txtItemName.setText(tblItem.getValueAt(selectedRow, indexes[1]).toString());
                txtItemName.requestFocus();
                txtItemSupCode.setSelectedItem(tblItem.getValueAt(selectedRow, indexes[2]));
                txtItemUnit.setText(tblItem.getValueAt(selectedRow, indexes[3]).toString());
                txtItemPrice.setText(tblItem.getValueAt(selectedRow, indexes[4]).toString());
                txtItemSupplying.setSelected((Boolean) tblItem.getValueAt(selectedRow, indexes[5]));
                txtItemCode.setEditable(false);
                isNew = false;
            }
        });

        btnItemSave.addActionListener(e -> {
            StringBuilder errors = new StringBuilder();
            String code = txtItemCode.getText().trim();
            if (!code.matches("E\\d{4}")) {
                errors.append("Invalid code [(E)xxxx] - x is digit\n");
                txtItemCode.requestFocus();
            } else if (isNew) {
                for (Item item : itemTableModel.getData())
                    if (item.getCode().equals(code)) {
                        errors.append("The code [").append(code).append("] is existed.\n");
                        txtItemCode.requestFocus();
                    }
            }
            String name = txtItemName.getText().trim();
            String supCode = Objects.requireNonNull(txtItemSupCode.getSelectedItem()).toString();
            String unit = txtItemUnit.getText().trim();
            int price = 0;
            String priceString = txtItemPrice.getText().trim();
            if (!priceString.matches("^[0-9]+$")) {
                errors.append("Invalid price [price must be a positive number]\n");
                txtItemPrice.requestFocus();
            } else {
                price = Integer.parseInt(priceString);
            }
            boolean isSupplying = txtItemSupplying.isSelected();
            if (errors.length() > 0) {
                JOptionPane.showMessageDialog(this, errors);
                return;
            }
            if (isNew) {
                Item item = new Item(code, name, supCode, unit, price, isSupplying);
                itemTableModel.getData().add(item);
                lbPage.setText("/ " + itemTableModel.getPageCount());
                try {
                    itemService.insertItem(item);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                int selectedRow = tblItem.getSelectedRow();
                Item item = new Item(code, name, supCode, unit, price, isSupplying);
                itemTableModel.getData().set((selectedRow + (itemTableModel.getCurrentPage() - 1) * 5), item);
                try {
                    itemService.updateItem(item);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            tblItem.updateUI();
        });
    }

    void initComponent() {
        JPanel left_panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Table Area
        tblItem = new JTable(itemTableModel);
        tblItem.setPreferredScrollableViewportSize(new Dimension(500, 140));
        tblItem.setRowHeight(30);
        tblItem.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane tablePanel = new JScrollPane(tblItem);
        tablePanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Table Page Navigator
        JPanel tableNav = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnItemNext = new JButton("Next");
        btnItemPrev = new JButton("Previous");
        txtItemPage = new JTextField(" 1");
        txtItemPage.setPreferredSize(new Dimension(30, 30));
        lbPage = new JLabel("/ " + itemTableModel.getPageCount());
        tableNav.add(btnItemPrev);
        tableNav.add(txtItemPage);
        tableNav.add(lbPage);
        tableNav.add(btnItemNext);

        // Task Button
        JPanel button_container = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnItemNew = new JButton("New");
        btnItemSave = new JButton("Save");
        btnItemRemove = new JButton("Remove");
        button_container.add(btnItemNew);
        button_container.add(btnItemSave);
        button_container.add(btnItemRemove);

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

        btnItemFind = new JButton("Find");
        txtItemCode = new JTextField();
        txtItemName = new JTextField();
        try {
            txtItemSupCode = new JComboBox<>(supplierService.getAllSupplierCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtItemUnit = new JTextField();
        txtItemPrice = new JTextField();
        txtItemSupplying = new JCheckBox("Supplying", false);
        JPanel name_container = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 7;
        gbc.weightx = 100;
        name_container.add(txtItemName, gbc);
        gbc = new GridBagConstraints();
        gbc.ipady = 7;
        name_container.add(btnItemFind, gbc);

        JLabel[] labels = {new JLabel("Code"), new JLabel("Name"), new JLabel("Supplier Code"), new JLabel("Unit"), new JLabel("Price"), new JLabel("Supplying")};
        JPanel right_panel = reusableComponent.inputComponent(labels, txtItemCode, name_container, txtItemSupCode, txtItemUnit, txtItemPrice, txtItemSupplying);

        this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        left_panel.setPreferredSize(new Dimension(550, 300));
        right_panel.setPreferredSize(new Dimension(400, 300));
        this.add(left_panel);
        this.add(right_panel);
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
    }
}
