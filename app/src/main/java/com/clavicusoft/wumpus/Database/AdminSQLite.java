package com.clavicusoft.wumpus.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLite extends SQLiteOpenHelper {

    /**
     * Creates the SQLiteOpenHelper object
     *
     * @param context to use to open or create the database.
     * @param name of the database file, or null for an in-memory database.
     * @param factory to use for creating cursor objects, or null for the default.
     * @param version number of the database (starting at 1);
     *                if the database is older, onUpgrade(SQLiteDatabase, int, int)
     *                will be used to upgrade the database; if the database is newer,
     *                onDowngrade(SQLiteDatabase, int, int) will be used to downgrade
     *                the database.
     */
    public AdminSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory,
                       int version) {
        super(context, name, factory, version);
    }

    /**
     * Creates a new DataBase. This function is only executed
     * when the DataBase is first created on the device.
     *
     * @param db The database.
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
                "VALUES (\"01101000*10010100*10010010*01100001*10000110*01001001*00101001*" +
                "00010110\", 8, \"Cube\", 0);");
        db.execSQL("INSERT INTO GRAPH (relations, number_of_caves, name, custom) " +
                "VALUES (\"011111000000*101010000101*110001000011*100011110000*110100100100*" +
                "101100010010*000110011100*000101101010*000000110111*010010101001*001001011001*" +
                "011000001110\", 12, \"Icosahedron\", 0);");
        db.execSQL("INSERT INTO GRAPH (relations, number_of_caves, name, custom) " +
                "VALUES (\"01100100000000000000*10010010000000000000*10001001000000000000*" +
                "01001000100000000000*00110000010000000000*10000000001100000000*" +
                "01000000001010000000*00100000000101000000*00010000000010100000*" +
                "00001000000001100000*00000110000000010000*00000101000000001000*" +
                "00000010100000000100*00000001010000000010*00000000110000000001*" +
                "00000000001000001100*00000000000100010010*00000000000010010001*" +
                "00000000000001001001*00000000000000100110\", 20, \"Dodecahedron\", 0);");
        db.execSQL("INSERT INTO CAVE_CONTENT (id, content) VALUES (1, EMPTY)");
        db.execSQL("INSERT INTO CAVE_CONTENT (id, content) VALUES (2, BAT)");
        db.execSQL("INSERT INTO CAVE_CONTENT (id, content) VALUES (3, PIT)");
        db.execSQL("INSERT INTO CAVE_CONTENT (id, content) VALUES (4, WUMPUS)");
    }

    /**
     * This code is executed when the database file exists but the stored
     * version number is lower than requested in constructor.
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion  The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS GRAPH;");
        db.execSQL("DROP TABLE IF EXISTS GAME;");
        db.execSQL("DROP TABLE IF EXISTS CAVE_CONTENT;");
        onCreate(db);
    }
}