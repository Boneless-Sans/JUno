package com.boneless;

import java.util.*;

public class GameLogic {
    private Card topOfDiscard;    // just the top card
    private Color activeColor;    // current active color
    private int pendingDrawCount; // handles stacking +2 / +4
    private boolean clockwise = true;
    private int currentPlayer = 0;

    enum Color { RED(0), YELLOW(1), GREEN(2), BLUE(3), WILD(4);

        private final int value;

        Color(int value) {
            this.value = value;
        }

        public int getColor(){
            return value;
        }

        public Color getColorEnum(){
            return Color.fromValue(value);
        }

        private static final Color[] BY_VALUE = values();
        public static Color fromValue(int v) {
            if (v < 0 || v >= BY_VALUE.length) throw new IllegalArgumentException("bad color:" + v);
            return BY_VALUE[v];
        }
    }
    enum Rank  { ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), SKIP(10), REVERSE(11), DRAW_TWO(12), WILD(13), WILD_DRAW_FOUR(14);
    private final int value;

        Rank(int value){
            this.value = value;
        }

        public int getRank(){
            return value;
        }

        public Rank getRankEnum(){
            return Rank.fromValue(value);
        }

        public boolean isWild() {
            return this == WILD || this == WILD_DRAW_FOUR;
        }

        private static final Rank[] BY_VALUE = values();
        public static Rank fromValue(int v) {
            if (v < 0 || v >= BY_VALUE.length) throw new IllegalArgumentException("bad rank:" + v);
            return BY_VALUE[v];
        }
    }
    enum Phase { AWAITING_START, TURN_PLAY_OR_DRAW, GAME_OVER }

    record Card(Color color, Rank rank) {}
    record Rules(boolean allowStacking, boolean jumpInEnabled, boolean sevenZeroEnabled, int startingHandSize) {}
    record Snapshot(UUID gameId, Phase phase, int currentPlayerIndex, List<Integer> handSizes,
                    Card topOfDiscard, Color activeColor, int pendingDrawCount, boolean clockwise,
                    int winnerIndex) {}

    private final java.util.Random rng = new java.util.Random();

    private final List<List<Card>> hands = new ArrayList<>();

    private Color randomStandardColor(){
        return Color.fromValue(rng.nextInt(4));
    }

    private Rank randomStandardRank(){
        return Rank.fromValue(rng.nextInt(4));
    }

    private Rank randomColoredRank(){
        int w = rng.nextInt(25);
        if (w == 0){
            return Rank.ZERO;
        }
        w--;
        if(w < 18){
            int offset = w / 2;
            return Rank.fromValue(1 + offset);
        }
        int actionIndex = (w - 18) / 2;
        return switch(actionIndex){
            case 0 -> Rank.SKIP;
            case 1 -> Rank.REVERSE;
            default -> Rank.DRAW_TWO;
        };
    }

    public Card drawRandomCard() {
        int r = rng.nextInt(108);
        if (r < 8) {
            return (r < 4) ? new Card(Color.WILD, Rank.WILD)
                    : new Card(Color.WILD, Rank.WILD_DRAW_FOUR);
        }
        return new Card(randomStandardColor(), randomColoredRank());
    }

    public void createPlayers(int playerCount){
        hands.clear();
        for(int i = 0; i < playerCount; i++){
            hands.add(new ArrayList<>());
        }
    }

    public void dealStartingHands(int cardsPerPlayer){
        for(int i = 0; i < cardsPerPlayer; i++){
            for(int j = 0; j < hands.size(); j++){
                hands.get(j).add(drawRandomCard());
            }
        }
    }

    public void startDiscord(){
        Card firstDiscardCard = drawRandomCard();
        while (firstDiscardCard.rank() == Rank.WILD_DRAW_FOUR || firstDiscardCard.rank() == Rank.WILD){
            firstDiscardCard = drawRandomCard();
        }
        topOfDiscard = firstDiscardCard;
        activeColor = firstDiscardCard.color();
    }

    public void playCard(int playerIndex, Card card){
        hands.get(playerIndex).remove(card);

        topOfDiscard = card;

        if(!card.rank().isWild()){
            activeColor = card.color();
        } else {
            activeColor = null;
        }

        if(card.rank() == Rank.DRAW_TWO){
            pendingDrawCount =+ 2;
        } else if (card.rank() == Rank.WILD_DRAW_FOUR) {
            pendingDrawCount =+ 4;
        }
    }
}

