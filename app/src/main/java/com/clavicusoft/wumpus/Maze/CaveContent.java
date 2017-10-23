package com.clavicusoft.wumpus.Maze;


public enum CaveContent {
    PLAYER(1), EMPTY(1), BAT(2), PIT(3), WUMPUS(4);

    private int value;

    CaveContent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
