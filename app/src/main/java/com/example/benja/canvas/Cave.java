package com.example.benja.canvas;


public class Cave {
    private int id;
    private float corX, corY;

    public Cave(int id, float corX, float corY) {
        this.id = id;
        this.corX = corX;
        this.corY = corY;
    }

    public int getId() {
        return id;
    }

    public float getCorX() {
        return corX;
    }

    public float getCorY() {
        return corY;
    }
}
