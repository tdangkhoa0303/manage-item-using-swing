package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Manager extends JFrame {
    JTabbedPane container;

    public Manager() {
        initComponents();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(container, "Are you sure to exit the program?", "Exit Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    System.exit(10);
            }
        });
    }

    public static void main(String[] args) {
        Manager c = new Manager();
        c.setSize(1000, 400);
        c.setDefaultCloseOperation(EXIT_ON_CLOSE);
        c.setTitle("Item Management");
        c.setResizable(false);
        c.setVisible(true);
    }

    void initComponents() {
        container = new JTabbedPane();
        ItemManager itemManager = new ItemManager();
        SupplierManager supplierManager = new SupplierManager(itemManager.txtItemSupCode);
        container.add("Item Manager", itemManager);
        container.add("Supplier Manager", supplierManager);
        Container c = getContentPane();
        c.add(container);
    }
}
