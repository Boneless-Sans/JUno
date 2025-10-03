package com.boneless;

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

//    interface Listener {
//        void onGameStarted(Snapshot snap);
//        void onTurnBegan(Snapshot snap);
//        void onCardPlayed(int playerIndex, Card card, Snapshot snap);
//        void onColorChosen(int playerIndex, Color color, Snapshot snap);
//        void onCardsDrawn(int playerIndex, int count, Snapshot snap);
//        void onDirectionChanged(boolean clockwise, Snapshot snap);
//        void onPenaltyApplied(int playerIndex, String reason, Snapshot snap);
//        void onGameEnded(int winnerIndex, Snapshot snap);
//        void onStateChanged(Snapshot snap);
//    }

    void createPlayerCount(int players){

    }
}

