package com.clavicusoft.wumpus.AR;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.beyondar.android.world.GeoObject;
import com.clavicusoft.wumpus.Database.AdminSQLite;
import com.clavicusoft.wumpus.Maze.CaveContent;
import com.clavicusoft.wumpus.Maze.Graph;
import com.clavicusoft.wumpus.R;

public class Game_Data {
    private int game_ID;
    private Graph graph;
    private CaveContent[] caveContents;
    private Context game_Context;

    public Game_Data (Context context, int game_ID) {
        this.game_ID = game_ID;
        this.game_Context = context;
        setInitialData();
    }

    public void setInitialData () {
        int graph_ID = getGameID();
        if (graph_ID != -1) {
            Boolean graph_Created = createGraph(graph_ID);
            if (graph_Created) {
                setCaveContent();
            }
        }
    }

    public int getGameID() {
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

    public void setCaveContent() {
        caveContents = new CaveContent[graph.getMaximumCaves()];
        AdminSQLite admin = new AdminSQLite(game_Context, "WumpusDB", null, 7);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor cell = db.rawQuery("SELECT GAME.cave_number, CAVE_CONTENT.content FROM GAME, CAVE_CONTENT " +
                "WHERE GAME.id = " + game_ID + " AND CAVE_CONTENT.id = GAME.content;", null);
        if (cell.moveToFirst()) {
            caveContents[cell.getInt(0) - 1] = getContentFromString(cell.getString(1));
        }
        cell.close();
    }

    public CaveContent getCaveContent (int cave_Number) {
        return caveContents[cave_Number - 1];
    }

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
