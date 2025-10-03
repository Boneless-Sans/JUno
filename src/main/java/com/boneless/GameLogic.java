package com.boneless;

import java.awt.image.ComponentColorModel;
import java.util.*;

public class GameLogic {
    enum Color { RED(0), YELLOW(1), GREEN(2), BLUE(3), WILD(4);

        private final int value;

        Color(int value) {
            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }
    enum Rank  { ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), SKIP(10), REVERSE(11), DRAW_TWO(12), WILD(13), WILD_DRAW_FOUR(14)
    private final int value;

        Rank(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }
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

    public getColor {}{

    }


}

