package View;

import javax.swing.*;
import java.awt.*;

public class reusableComponent {
    public static JPanel inputComponent(JLabel[] labels, Component... textFields) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 20, 10));
        panel.setLayout(new GridBagLayout());
        int length = labels.length;
        for (int i = 0; i < length; i++) {
            panel.add(labels[i], createGbc(0, i));
            panel.add(textFields[i], createGbc(1, i));
        }
        return panel;
    }


    private static GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.ipadx = x % 2 != 0 ? 80 : 1;
        gbc.ipady = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }
}
