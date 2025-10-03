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

//    Snapshot snap = new Snapshot(
//            gameId = 123e4567-e89b-12d3-a456-426614174000,  // unique game session id
//
//            phase = Phase.TURN_PLAY_OR_DRAW,                // we are in normal play mode
//            currentPlayerIndex = 1,                         // it’s Player 1’s turn
//
//            handSizes = [5, 7, 2],                          // Player 0 has 5, Player 1 has 7, Player 2 has 2
//    topOfDiscard = new Card(Color.BLUE, Rank.DRAW_TWO),   // card on the table is Blue +2
//    activeColor = Color.BLUE,                       // must follow BLUE unless stacking +2
//
//    pendingDrawCount = 2,                           // Player 1 must draw 2 if they can’t stack
//    clockwise = false,                              // play order is counterclockwise
//
//    legalMovesForCurrent = [ new Card(Color.BLUE, Rank.FIVE),
//                             new Card(Color.WILD, Rank.WILD_DRAW_FOUR) ],
//    // Player 1 has only these legal plays right now
//
//    winnerIndex = -1                                // no winner yet
//            );

    void createPlayerCount(int players){

    }
}

