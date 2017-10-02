package com.example.benja.canvas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SelectPolyActivity extends AppCompatActivity  {

    ViewPager viewPager;
    CustomSwip  customSwip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_poly);
        viewPager=(ViewPager)findViewById(R.id.ImageSlider);
        int[] imageResources = {R.drawable.tetra,R.drawable.octa,R.drawable.cube,R.drawable.icosa,R.drawable.dodeca};
        customSwip = new CustomSwip(this,imageResources);
        viewPager.setAdapter(customSwip);
    }

    /*
    * Gets a regular maze from the DB once an image is clicked.
    */
    public void imageClicked(View v) {
        Button button = (Button) findViewById(v.getId());
        String buttonID = button.getText().toString();
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 2);
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
                graphName = "cube";
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


    public void DrawLabyrinthView(View vista)
    {
        Intent i = new Intent(this,DrawMazeActivity.class);
        startActivity(i);

    }

    public void selectFromLibView(View vista)
    {
        Intent i = new Intent(this,SelectFromLibActivity.class);
        startActivity(i);

    }

}
