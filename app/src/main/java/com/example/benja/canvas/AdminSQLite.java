package com.example.benja.canvas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
*   This class starts the DB.
*/

public class AdminSQLite extends SQLiteOpenHelper {

    public AdminSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE GRAPH (" +
                "id INTEGER AUTO INCREMENT PRIMARY KEY," +
                "relations TEXT" +
                ");");
        db.execSQL("CREATE TABLE CAVE (" +
                "id INTEGER AUTO INCREMENT PRIMARY KEY," +
                "graph_id INTEGER," +
                "cave_number INTEGER," +
                "FOREIGN KEY (graph_id) REFERENCES GRAPH(id)" +
                ");");
        db.execSQL("CREATE TABLE CAVE_CONTENT (" +
                "id INTEGER AUTO INCREMENT PRIMARY KEY," +
                "content TEXT" +
                ");");
        db.execSQL("CREATE TABLE GAME (" +
                "id INTEGER AUTO INCREMENT PRIMARY KEY," +
                "graph_id INTEGER," +
                "cave_id INTEGER," +
                "latitude TEXT," +
                "longitude TEXT" +
                "cave_number INTEGER," +
                "content INTEGER," +
                "FOREIGN KEY (graph_id) REFERENCES GRAPH(id)," +
                "FOREIGN KEY (cave_id) REFERENCES CAVE(id)," +
                "FOREIGN KEY (content) REFERENCES CAVE_CONTENT(id)" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS GRAPH;");
        db.execSQL("DROP TABLE IF EXISTS CAVE;");
        db.execSQL("DROP TABLE IF EXISTS GAME;");
        onCreate(db);
    }
}