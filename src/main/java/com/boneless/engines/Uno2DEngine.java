package com.boneless.engines;

import com.boneless.Card;
import com.boneless.GameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Uno2DEngine extends JPanel implements KeyListener {

    private final ArrayList<Card> handTemp = new ArrayList<>();

    public Uno2DEngine() {
        setBackground(Color.cyan);

        for (int i = 0; i < 7; i++) {
            handTemp.add(new Card());
        }

        for(Card card : handTemp){
            System.out.println(card.getColor() + " " + card.getNumber());
            System.out.println("--------");
        }
    }

    private Card createCard() {
        Card card = new Card();

        return card;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 'c') {
            System.out.println("C pressed");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
