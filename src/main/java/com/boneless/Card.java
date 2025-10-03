package com.boneless;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Card extends JPanel {

    /*
    0-9 normal cards
    10 skip
    11 reverse
    12 plus 2
    13 wild
    14 plus 4
     */

    private final String color;
    private final int number;

    public Card() { //todo: maybe have the renderer and logic handle special cards
        Random rand = new Random();
        number = rand.nextInt(15);

        color = switch (rand.nextInt(5)) {
            case 0 -> "green";
            case 1 -> "blue";
            case 2 -> "yellow";
            case 3 -> "red";
            default -> "black";
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
