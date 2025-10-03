package com.boneless;

import javax.swing.*;
import java.util.Random;

public class Card extends JPanel {

    /*
    0-9 normal cards
    10 skip
    11 reverse
    12 wild
    13 plus 4
    14 plus 2
     */

    private final String color;
    private final int number;

    public Card() {
        Random rand = new Random();
        number = rand.nextInt(11);
        color = switch (rand.nextInt(4)) {
            case 0 -> "green";
            case 1 -> "blue";
            case 2 -> "yellow";
            default -> "red";
        };
    }

    public Card(String color, int number) {
        this.color = color;
        this.number = number;
    }

    public String getColor() {
        return color;
    }
    public int getNumber() {
        return number;
    }
}
