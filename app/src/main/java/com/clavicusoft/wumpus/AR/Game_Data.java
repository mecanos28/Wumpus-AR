package com.clavicusoft.wumpus.AR;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.clavicusoft.wumpus.Database.AdminSQLite;
import com.clavicusoft.wumpus.Maze.CaveContent;
import com.clavicusoft.wumpus.Maze.Graph;

public class Game_Data {

    private int game_ID;
    private Graph graph;
    private CaveContent[] caveContents;
    private Context game_Context;
    private int currentCave;

    public int getGame_ID() {
        return game_ID;
    }

    public Graph getGraph() {
        return graph;
    }

    public int getCurrentCave() {
        return currentCave;
    }

    /**
     * Creates a new Game_Data object.
     *
     * @param context Game's context.
     * @param game_ID ID of the current game.
     * @param currentCave Current cave number.
     */
    public Game_Data (Context context, int game_ID, int currentCave) {
        this.game_ID = game_ID;
        this.game_Context = context;
        this.currentCave = currentCave;
        setInitialData();
    }

    /**
     * Initializes the data.
     */
    public void setInitialData () {
        int graph_ID = getGraphID();
        if (graph_ID != -1) {
            Boolean graph_Created = createGraph(graph_ID);
            if (graph_Created) {
                setCaveContent();
            }
        }
    }

    /**
     * Gets the graph_ID from the current game.
     *
     * @return Graph's DB ID.
     */
    public int getGraphID() {
        AdminSQLite admin = new AdminSQLite(game_Context, "WumpusDB", null, 7);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT graph_id FROM GAME WHERE id = " +
                String.valueOf(game_ID) + ";", null);

        int result = -1;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    /**
     * Creates a Graph with the graph_ID information.
     *
     * @param graph_ID Graph's DB ID.
     * @return True if the graph was creates | False if the Graph was not created.
     */
    public Boolean createGraph(int graph_ID) {
        Boolean result = false;

        AdminSQLite admin = new AdminSQLite(game_Context, "WumpusDB", null, 7);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor cell = db.rawQuery("SELECT GRAPH.relations, GRAPH.number_of_caves FROM GRAPH " +
                "WHERE GRAPH.id = " + graph_ID + ";", null);
        if (cell.moveToFirst()) {
            graph = new Graph(cell.getInt(1));
            graph.stringToArray(cell.getString(0));
            result = true;
        }
        cell.close();

        return result;
    }

    /**
     * Gets the cave content from the DB and stores it.
     */
    public void setCaveContent() {
        caveContents = new CaveContent[graph.getMaximumCaves()];
        AdminSQLite admin = new AdminSQLite(game_Context, "WumpusDB", null, 7);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor cell = db.rawQuery("SELECT GAME.cave_number, CAVE_CONTENT.content FROM GAME, CAVE_CONTENT " +
                "WHERE GAME.id = " + game_ID + " AND CAVE_CONTENT.id = GAME.content;", null);
        if (cell.moveToFirst()) {
            do{
                caveContents[cell.getInt(0) - 1] = getContentFromString(cell.getString(1));
            }while(cell.moveToNext());
        }
        cell.close();
    }

    /**
     * Gets the content of a cave.
     *
     * @param cave_Number Number of the cave.
     * @return The content of the cave.
     */
    public CaveContent getCaveContent (int cave_Number) {
        return caveContents[cave_Number - 1];
    }

    /**
     * Gets the content of a cave based on a string.
     *
     * @param content String containing the cave content.
     * @return The content of the cave.
     */
    public CaveContent getContentFromString (String content) {
        CaveContent result;
        switch (content){
            case "WUMPUS":
                result = CaveContent.WUMPUS;
                break;
            case "PIT":
                result = CaveContent.PIT;
                break;
            case "BAT":
                result = CaveContent.BAT;
                break;
            default:
                result = CaveContent.EMPTY;
                break;
        }
        return result;
    }
}
