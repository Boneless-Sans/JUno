package com.boneless.engines;

import com.boneless.Card;
import com.boneless.GameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Uno2DEngine extends JPanel{

    private final ArrayList<Card> handTemp = new ArrayList<>();

    public Uno2DEngine() {
        setBackground(Color.cyan);
        setFocusable(true);
        setLayout(new BorderLayout());

        JPanel playerHand = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playerHand.setOpaque(false);

        for (int i = 0; i < 7; i++) {
            handTemp.add(new Card());
        }

        for(Card card : handTemp){
            System.out.println(card.getColor() + " " + card.getNumber());
            System.out.println("--------");

            UnoCard2D cardPanel = new UnoCard2D(card);
            cardPanel.setPreferredSize(new Dimension(140, 200));

            playerHand.add(cardPanel);
        }

        add(playerHand, BorderLayout.SOUTH);
    }

    private static class UnoCard2D extends JPanel {
        private final int number;
        private final String color;

        public UnoCard2D(Card card) {
            number = card.getNumber();
            color = card.getColor();
        }

        @Override
        public void paintComponent(Graphics g) {
            //super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);

            Color backgroundColor = switch (color) {
                case "red" -> Color.red;
                case "green" -> Color.green;
                case "blue" -> Color.blue;
                case "yellow" -> Color.yellow;
                default -> Color.black;
            };

            //draw background
            g2d.setColor(Color.white);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15,15);

            int offset = 5;
            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(offset, offset, getWidth() - offset*2, getHeight() - offset*2, 15,15);

            //draw top left number
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(String.valueOf(number));
            int textHeight = fm.getAscent();
            int x = 10;
            int y = 30;
            g2d.drawString(String.valueOf(number), x, y);

            //draw bottom right text
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int x2 = panelWidth - 30;
            int y2 = panelHeight - 35;

            AffineTransform oldRotation = g2d.getTransform();
            g2d.rotate(Math.PI, x2, y2);

            g2d.drawString(String.valueOf(number), x2 - textWidth, y2);
            g2d.setTransform(oldRotation);
        }
    }

}
