package com.game.repository;

import java.util.HashMap;
import java.util.Map;

public class MoveTrackerRepository {

    private final Map<String, Integer> movesTracker;

    public MoveTrackerRepository () {
        movesTracker = new HashMap<> ();
        movesTracker.put ("client", 0);
        movesTracker.put ("server", 0);
        movesTracker.put ("moveCount", 0);
    }

    public int getMoveCount () {
        return movesTracker.get ("moveCount");
    }

    public int getClientPosition () {
        return movesTracker.get ("client");
    }

    public int getServerPosition () {
        return movesTracker.get ("server");
    }

    public void setMoveCount (int moveCount) {
        movesTracker.put ("moveCount", moveCount);
    }

    public void setClientPosition (int clientPosition) {
        movesTracker.put ("client", clientPosition);
    }

    public void setServerPosition (int serverPosition) {
        movesTracker.put ("server", serverPosition);
    }

}
