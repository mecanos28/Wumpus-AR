package com.example.benja.canvas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DrawMazeActivity extends AppCompatActivity {

    Graph customMaze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_poly);
        customMaze = new Graph();
    }

    /*
    * Saves the actual maze.
    */
    public void saveMaze() {
        if (customMaze.valid()) {
            //TODO: Take a screenshot of the draw and store it on the phone, then put the image name on the corresponding variable.
            String image = "";
            Integer graphID;
            String relations = customMaze.arrayToString();
            AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("relations", relations);
            data.put("number_of_caves", customMaze.getMaximumCaves());
            data.put("image", image);
            db.insert("GRAPH", null, data);
            Cursor cell = db.rawQuery("SELECT id FROM GRAPH WHERE GRAPH.relations = " + relations, null);
            if (cell.moveToFirst()){
                graphID = cell.getInt(0);
                cell.close();
                db.close();
                //TODO: Ask if they want to keep drawing or if they want to play with the new maze.
                //TODO: If they want to play, send the graphID to the next Layout.
            }
            else {
                Toast.makeText(this, "The Wumpus doesn't seem to like this caves. Try drawing another one!", Toast.LENGTH_LONG).show();
                db.close();
            }
            cell.close();
            db.close();
        }
        else {
            Toast.makeText(this, "The Wumpus doesn't seem to like this caves. Try drawing another one!", Toast.LENGTH_LONG).show();
        }
    }

}
