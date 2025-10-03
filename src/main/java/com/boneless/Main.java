package com.boneless;

import javax.swing.*;
import java.awt.*;

import com.boneless.GameLogic.*;
import com.boneless.engines.Uno2DEngine;
import com.boneless.engines.Uno3DEngine;

public class Main extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);

//        try {
//            if(System.getProperty("os.name").equalsIgnoreCase("windows")) {
//                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//            } else {
//                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        pickRenderer();

        setVisible(true);
    }

    private void pickRenderer() {
        setLayout(new GridLayout(2, 1));

        JButton engine3d = new JButton("3D Engine");
        JButton engine2d = new JButton("2D Engine");

        engine3d.setFocusable(false);
        engine3d.addActionListener(e -> setRenderer(1, engine3d, engine2d));

        engine2d.setFocusable(false);
        engine2d.addActionListener(e -> setRenderer(0, engine3d, engine2d));

        add(engine3d);
        add(engine2d);
    }

    private void setRenderer(int val, JButton btn3d, JButton btn2d) {
        remove(btn2d);
        remove(btn3d);

        setSize(1920,  1080);
        setLayout(new GridLayout(1, 1));
        setLocationRelativeTo(null);

        add(val == 0 ? new Uno2DEngine() : new Uno3DEngine());

        revalidate();
        repaint();
    }
}