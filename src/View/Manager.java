package View;

import javax.swing.*;
import java.awt.*;

public class Manager extends JFrame {

    public Manager() {
        initComponents();
    }

    public static void main(String[] args) {
        Manager c = new Manager();
        c.setSize(1000, 350);
        c.setDefaultCloseOperation(EXIT_ON_CLOSE);
        c.setTitle("Item Management");
        c.setResizable(false);
        c.setVisible(true);
    }

    void initComponents() {
        JTabbedPane container = new JTabbedPane();
        container.add("Item Manager", new ItemManager());
        container.add("Supplier Manager", new SupplierManager());
        Container c = getContentPane();
        c.add(container);
    }

}
