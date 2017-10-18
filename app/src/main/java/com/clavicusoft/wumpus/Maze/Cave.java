package com.clavicusoft.wumpus.Maze;


public class Cave {
    private int id;
    private float corX, corY;
    private CaveContent caveContent;

    /**
     * Creates a cave with id and coordinates.
     * @param id The cave's id.
     * @param corX Latitude of the cave.
     * @param corY Longitude of the cave.
     */
    public Cave(int id, float corX, float corY) {
        this.id = id;
        this.corX = corX;
        this.corY = corY;
        this.setCaveContent(CaveContent.EMPTY);
    }

    /**
     * Yields the id of the cave.
     * @return Cave´s id.
     */
    public int getId() {
        return id;
    }

    /**
     * Yields the latitude of the cave.
     * @return Cave´s latitude.
     */
    public float getCorX() {
        return corX;
    }

    /**
     * Yields the longitude of the cave.
     * @return Cave's longitude.
     */
    public float getCorY() {
        return corY;
    }

    /**
     *
     * @return
     */
    public CaveContent getCaveContent() {
        return caveContent;
    }

    /**
     * 
     * @param caveContent
     */
    public void setCaveContent(CaveContent caveContent) {
        this.caveContent = caveContent;
    }
}
