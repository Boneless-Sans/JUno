package com.boneless.engines;

import com.boneless.Card;
import com.boneless.GameLogic;
import com.boneless.Main;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

import static com.boneless.Main.GAME;

public class Uno2DEngine extends JPanel{

    public Uno2DEngine() {
        setBackground(Color.darkGray);
        setFocusable(true);
        setLayout(new BorderLayout());

        GAME = new GameLogic();
        GAME.startGame(2); //todo: menu

        JPanel playerHand = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playerHand.setOpaque(false);

//        for(Card card : Main.GAME.){
//            //System.out.println(card.getColor() + " " + card.getNumber());
//            //System.out.println("--------");
//
//
//        }
        for(Card card : GAME.hands.get(0)) {
            UnoCard2D cardPanel = new UnoCard2D(card);
            cardPanel.setPreferredSize(new Dimension(140, 200));

            playerHand.add(cardPanel);
        }

        add(playerHand, BorderLayout.SOUTH);
    }

    private static class UnoCard2D extends JPanel {
        private final int number;
        private final Color color;

        public UnoCard2D(Card card) {
            number = card.getNumber();
            color = card.getColor();
        }

        @Override
        public void paintComponent(Graphics g) {
            //super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);

            //draw background
            g2d.setColor(Color.white);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15,15);

            int offset = 5;
            g2d.setColor(color);
            g2d.fillRoundRect(offset, offset, getWidth() - offset*2, getHeight() - offset*2, 15,15);

            if(number <= 9) {
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
}