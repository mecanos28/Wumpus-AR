package com.example.benja.canvas;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileInputStream;
import java.io.IOException;

/*
*   This class starts the DB.
*/

public class AdminSQLite extends SQLiteOpenHelper {

    public AdminSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /*
    * This code is executed when the DB doesn't exists.
    */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create all the tables.
        db.execSQL("CREATE TABLE GRAPH (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "relations TEXT," +
                "number_of_caves INTEGER," +
                "name TEXT," +
                "custom INTEGER DEFAULT 1" +
                ");");
        db.execSQL("CREATE TABLE CAVE_CONTENT (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "content TEXT" +
                ");");
        db.execSQL("CREATE TABLE GAME (" +
                "id INTEGER NOT NULL," +
                "graph_id INTEGER," +
                "cave_number INTEGER NOT NULL," +
                "latitude TEXT," +
                "longitude TEXT," +
                "content INTEGER," +
                "PRIMARY KEY (id, cave_number)," +
                "FOREIGN KEY (graph_id) REFERENCES GRAPH(id)," +
                "FOREIGN KEY (content) REFERENCES CAVE_CONTENT(id)" +
                ");");

        //Insert the default graphs
        db.execSQL("INSERT INTO GRAPH (relations, number_of_caves, name, custom) " +
                "VALUES (\"0111*1011*1101*1110\", 4, \"Tetrahedron\", 0);");
        db.execSQL("INSERT INTO GRAPH (relations, number_of_caves, name, custom) " +
                "VALUES (\"011110*101101*110011*110011*101101*011110\", 6, \"Octahedron\", 0);");
        db.execSQL("INSERT INTO GRAPH (relations, number_of_caves, name, custom) " +
                "VALUES (\"01101000*10010100*10010010*01100001*10000110*01001001*00101001*00010110\", 8, \"Cube\", 0);");
        db.execSQL("INSERT INTO GRAPH (relations, number_of_caves, name, custom) " +
                "VALUES (\"011111000000*101010000101*110001000011*100011110000*110100100100*101100010010*000110011100*000101101010*000000110111*010010101001*001001011001*011000001110\", 12, \"Icosahedron\", 0);");
        db.execSQL("INSERT INTO GRAPH (relations, number_of_caves, name, custom) " +
                "VALUES (\"01100100000000000000*10010010000000000000*10001001000000000000*01001000100000000000*00110000010000000000*10000000001100000000*01000000001010000000*00100000000101000000*00010000000010100000*00001000000001100000*00000110000000010000*00000101000000001000*00000010100000000100*00000001010000000010*00000000110000000001*00000000001000001100*00000000000100010010*00000000000010010001*00000000000001001001*00000000000000100110\", 20, \"Dodecahedron\", 0);");
    }

    /*
    * This code is executed when the database file exists but the stored
    * version number is lower than requested in constructor.
    */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS GRAPH;");
        db.execSQL("DROP TABLE IF EXISTS GAME;");
        db.execSQL("DROP TABLE IF EXISTS CAVE_CONTENT;");
        onCreate(db);
    }
}