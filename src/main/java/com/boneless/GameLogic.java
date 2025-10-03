package com.boneless;

import java.awt.image.ComponentColorModel;
import java.util.*;

public class GameLogic {
    enum Color { RED, YELLOW, GREEN, BLUE, WILD }
    enum Rank  { ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR }
    enum Phase { AWAITING_START, TURN_PLAY_OR_DRAW, GAME_OVER }

    record Card(Color color, Rank rank) {}
    record Rules(boolean allowStacking, boolean jumpInEnabled, boolean sevenZeroEnabled, int startingHandSize) {}
    record Snapshot(UUID gameId, Phase phase, int currentPlayerIndex, List<Integer> handSizes,
                    Card topOfDiscard, Color activeColor, int pendingDrawCount, boolean clockwise,
                    int winnerIndex) {}

    private final List<List<Card>> hands = new ArrayList<>();

    void createPlayers(int playerCount){
        for(int i = 0; i < playerCount; i++){
            hands.add(new ArrayList<>());
        }
    }

    private List<Card> buildDeck(){
        List<Card> deck = new ArrayList<>();

        for(Color c : List.of(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.WILD)){
            deck.add(new Card(c, Rank.ZERO));

            for(int i = 0; i < 2; i++){
                deck.add(new Card(c, Rank.ONE));
                deck.add(new Card(c, Rank.TWO));
                deck.add(new Card(c, Rank.THREE));
                deck.add(new Card(c, Rank.FOUR));
                deck.add(new Card(c, Rank.FIVE));
                deck.add(new Card(c, Rank.SIX));
                deck.add(new Card(c, Rank.SEVEN));
                deck.add(new Card(c, Rank.EIGHT));
                deck.add(new Card(c, Rank.NINE));
                deck.add(new Card(c, Rank.SKIP));
                deck.add(new Card(c, Rank.REVERSE));
                deck.add(new Card(c, Rank.DRAW_TWO));
                }
            }

        for(int i = 0; i < 4; i++){
            deck.add(new Card(Color.WILD, Rank.WILD));
            deck.add(new Card(Color.WILD, Rank.WILD_DRAW_FOUR));
        }

        return deck;
    }


}

