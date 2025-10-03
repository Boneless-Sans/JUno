package com.boneless.engines;

import com.boneless.Card;
import com.boneless.GameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Uno2DEngine extends JPanel{

    private final ArrayList<Card> handTemp = new ArrayList<>();

    public Uno2DEngine() {
        setBackground(Color.cyan);
        setFocusable(true);
        setLayout(new FlowLayout(FlowLayout.CENTER));

        for (int i = 0; i < 7; i++) {
            handTemp.add(new Card());
        }

        int offset = 0;
        for(Card card : handTemp){
            System.out.println(card.getColor() + " " + card.getNumber());
            System.out.println("--------");

            UnoCard2D cardPanel = new UnoCard2D(card);
            cardPanel.setPreferredSize(new Dimension(70, 100));

            add(cardPanel);
        }
    }

    private static class UnoCard2D extends JPanel {

        public UnoCard2D(Card card) {
            JPanel cardPanel = new JPanel();
            cardPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            JLabel numberText = new JLabel(String.valueOf(card.getNumber()));
            numberText.setFont(new Font("Arial", Font.BOLD, 16));
            numberText.setForeground(Color.black);

            cardPanel.add(numberText);
        }
    }

}
