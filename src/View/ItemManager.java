package View;

import Entity.Item;
import Service.ItemService;
import Service.SupplierService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ItemManager extends JPanel {
    TableModel<Item> itemTableModel;
    ItemService itemService = new ItemService();
    SupplierService supplierService = new SupplierService();

    boolean isNew = true;

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
    JTextField txtItemSupplying;
    JTextField txtItemPage;

    public ItemManager() {
        int[] indexes = {0, 1, 2, 3, 4, 5};
        String[] headers = {"Code", "Name", "Supplier Code", "Unit", "Price", "Supplying"};
        itemTableModel = new TableModel<>(headers, indexes) {
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (rowIndex < 0 || rowIndex >= this.pageSize || columnIndex < 0 || columnIndex >= headers.length)
                    return null;
                Item item = data.get(rowIndex + (currentPage - 1) * pageSize);
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
                        return item.getSupplying();
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
            tblItem.updateUI();
        });

        btnItemPrev.addActionListener(e -> {
            itemTableModel.setCurrentPage(itemTableModel.getCurrentPage() - 1);
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
            itemTableModel.setData(itemService.);
        });
    }

    void initComponent() {
        JPanel left_panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Table Area
        tblItem = new JTable(itemTableModel);
        tblItem.setPreferredScrollableViewportSize(new Dimension(500, 150));
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
        JLabel lbPage = new JLabel("/ " + itemTableModel.getPageCount());
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
        txtItemSupplying = new JTextField();
        JPanel name_container = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 8;
        gbc.ipadx = 80;
        gbc.weightx = 100;
        name_container.add(txtItemName, gbc);
        gbc = new GridBagConstraints();
        gbc.ipady = 7;
        name_container.add(btnItemFind, gbc);

        JLabel[] labels = {new JLabel("Code"), new JLabel("Name"), new JLabel("Supplier Code"), new JLabel("Unit"), new JLabel("Price"), new JLabel("Supplying")};
        JPanel right_panel = reusableComponent.inputComponent(labels, txtItemCode, name_container, txtItemSupCode, txtItemUnit, txtItemPrice, txtItemSupplying);

        this.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
        this.add(left_panel);
        this.add(right_panel);
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
    }
}
