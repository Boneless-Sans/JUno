package com.boneless;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Card extends JPanel {
    public static final int SKIP = 10;
    public static final int REVERSE = 11;
    public static final int PLUS2 = 12;
    public static final int WILD = 13;
    public static final int PLUS4 = 14;


    private final Color color;
    private final int number;

    public Card() {
        Random rand = new Random();
        number = rand.nextInt(15);

        rand.nextInt(4);
        color = Color.CYAN;
    }

    public Card(Color color, int number) {
        this.color = color;
        this.number = number;
    }

    public Color getColor() {
        return color;
    }
    public int getNumber() {
        return number;
    }
}
