package com.boneless;

import com.boneless.util.Uno3DEngine;

import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);

        Uno3DEngine engine3D = new Uno3DEngine();
        add(engine3D);

        setVisible(true);
    }
}