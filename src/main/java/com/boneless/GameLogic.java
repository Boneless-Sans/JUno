package com.boneless;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameLogic {
    private Card topOfDiscard;    // just the top card
    private int pendingDrawCount; // handles stacking +2 / +4
    private boolean clockwise = true;
    private int currentPlayer = 0;
    private Phase phase = Phase.AWAITING_START;


    //Value setup\\
//    enum Color { RED(0), YELLOW(1), GREEN(2), BLUE(3), WILD(4);
//
//        private final int value;
//
//        Color(int value) {
//            this.value = value;
//        }
//
//        public int getColor(){
//            return value;
//        }
//
//        public Color getColorEnum(){
//            return Color.fromValue(value);
//        }
//
//        private static final Color[] BY_VALUE = values();
//        public static Color fromValue(int v) {
//            if (v < 0 || v >= BY_VALUE.length) throw new IllegalArgumentException("bad color:" + v);
//            return BY_VALUE[v];
//        }
//    }

    enum Phase { AWAITING_START, TURN_PLAY_OR_DRAW, GAME_OVER }

    //public record Card(Color color, Rank rank) {}
    record Rules(boolean allowStacking, boolean jumpInEnabled, boolean sevenZeroEnabled, int startingHandSize) {}
    record Snapshot(UUID gameId, Phase phase, int currentPlayerIndex, List<Integer> handSizes,
                    Card topOfDiscard, int pendingDrawCount, boolean clockwise,
                    int winnerIndex) {}

    private final java.util.Random rng = new java.util.Random();

    public final List<List<Card>> hands = new ArrayList<>();

    //Play Methods\\
    //color and rank
    private Color randomStandardColor(){
        return switch (rng.nextInt(4)) {
            case 0 -> Color.RED;
            case 1 -> Color.YELLOW;
            case 2 -> Color.GREEN;
            case 3 -> Color.BLUE;
            default -> Color.black;
        };
    }

    private int randomColoredRank(){
        return rng.nextInt(14);
/*        int rank = rng.nextInt(25);
//        if (rank == 0){
//            return Rank.ZERO;
//        }
//        rank--;
//        if(rank < 18){
//            int offset = rank / 2;
//            return Rank.fromValue(1 + offset);
//        }
//        int actionIndex = (rank - 18) / 2;
//        return switch(actionIndex){
//            case 0 -> Rank.SKIP;
//            case 1 -> Rank.REVERSE;
//            default -> Rank.DRAW_TWO;
//        };
 */
    }

    //player handling
    public void createPlayers(int playerCount){
        hands.clear();
        for(int i = 0; i < playerCount; i++){
            hands.add(new ArrayList<>());
        }
    }

    //card management
    public void drawCards(List<Card> hand, int amount){
        for(int i = 0; i < amount; i++){
            hand.add(drawRandomCard());
        }
    }

    public Card drawRandomCard() {
        int r = rng.nextInt(108);
        if (r <= 8) {
            return (r <= 4) ? new Card(Color.black, Card.WILD)
                    : new Card(Color.black, Card.PLUS4);
        }
        return new Card(randomStandardColor(), randomColoredRank());
    }

//    public void startDiscard(){
//        Card firstDiscardCard = drawRandomCard();
//        while (firstDiscardCard.rank() == Rank.WILD_DRAW_FOUR || firstDiscardCard.rank() == Rank.WILD){
//            firstDiscardCard = drawRandomCard();
//        }
//        topOfDiscard = firstDiscardCard;
//        activeColor = firstDiscardCard.color();
//    }
//
//    public void playCard(int playerIndex, Card card){
//        hands.get(playerIndex).remove(card);
//
//        topOfDiscard = card;
//
//        if(!card.rank().isWild()){
//            activeColor = card.color();
//        } else {
//            activeColor = null;
//        }
//
//        if(card.rank() == Rank.DRAW_TWO){
//            pendingDrawCount = 2;
//        } else if (card.rank() == Rank.WILD_DRAW_FOUR) {
//            pendingDrawCount = 4;
//        }
//    }

    public void startGame(int playerCount) {
        createPlayers(playerCount);

        for(List<Card> card : hands){
            drawCards(card, 7);
        }

        currentPlayer = 0;
        clockwise = true;
        pendingDrawCount = 0;
        phase = Phase.TURN_PLAY_OR_DRAW;
    }
}

