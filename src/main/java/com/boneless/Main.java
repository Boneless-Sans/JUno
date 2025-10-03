package com.boneless;

import javax.swing.*;
import java.awt.*;

import com.boneless.GameLogic.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);

        try {
            if(System.getProperty("os.name").equalsIgnoreCase("windows")) {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        pickRenderer();

        setVisible(true);
    }

    private void pickRenderer() {
        setLayout(new GridLayout(2,1));

        JButton engine3d = new JButton("3D Engine");
        engine3d.setFocusable(false);

        JButton engine2d = new JButton("2D Engine");
        engine2d.setFocusable(false);

        add(engine3d);
        add(engine2d);
    }
}