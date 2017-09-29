package com.example.benja.canvas;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SelectPolyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_poly);
    }

    /*
    * Gets a regular maze from the DB once an image is clicked.
    */
    void imageClicked(View v) {
        Button button = (Button) findViewById(v.getId());
        String buttonID = button.getText().toString();
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        String graphName = "";
        Integer graphID;
        switch (buttonID) {
            case "btnTetrahedron":
                graphName = "Tetrahedron";
                break;
            case "btnOctahedron":
                graphName = "Octahedron";
                break;
            case "btnCube":
                graphName = "Cube";
                break;
            case "btnIcosahedron":
                graphName = "Icosahedron";
                break;
            case "btnDodecahedron":
                graphName = "Dodecahedron";
                break;
        }
        Cursor cell = db.rawQuery("SELECT id FROM GRAPH WHERE GRAPH.name = " + graphName, null);
        if (cell.moveToFirst()){
            graphID = cell.getInt(0);
            cell.close();
            //TODO: Call the next layout and send the id as parameter.
        }
        else {
            Toast.makeText(this, "The Wumpus isn't around this caves. Try another one!", Toast.LENGTH_LONG).show();
            db.close();
        }
        cell.close();
    }
}
