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
    int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_poly);
        currentPage = 1;
        viewPager=(ViewPager)findViewById(R.id.ImageSlider);
        int[] imageResources = {R.drawable.tetra,R.drawable.octa,R.drawable.cube,R.drawable.icosa,R.drawable.dodeca};
        customSwip = new CustomSwip(this,imageResources);
        viewPager.setAdapter(customSwip);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position + 1;
            }
        });
    }

    /*
    * Gets a regular maze from the DB once an image is clicked.
    */
    public void imageClicked(int graph) {
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 2);
        SQLiteDatabase db = admin.getWritableDatabase();
        String graphName = "";
        switch (graph) {
            case 1:
                graphName = "Tetrahedron";
                break;
            case 2:
                graphName = "Octahedron";
                break;
            case 3:
                graphName = "Cube";
                break;
            case 4:
                graphName = "Icosahedron";
                break;
            case 5:
                graphName = "Dodecahedron";
                break;
        }
        Cursor cell = db.rawQuery("SELECT id FROM GRAPH WHERE GRAPH.name = " + graphName, null);
        if (cell.moveToFirst()){
            int graphID = cell.getInt(0);
            cell.close();
            //Intent i = new Intent(this, EmplazarActivity.class);
            //i.putExtra("graphID",graphID);
            //startActivity(i);
        }
        else {
            Toast.makeText(this, "The Wumpus isn't around this caves. Try another one!", Toast.LENGTH_LONG).show();
            db.close();
        }
        cell.close();
    }


    public void DrawLabyrinthView(View view)
    {
        Intent i = new Intent(this,DrawMazeActivity.class);
        startActivity(i);

    }

    public void selectFromLibView(View view)
    {
        Intent i = new Intent(this,SelectFromLibActivity.class);
        startActivity(i);

    }

    public void startGame(View view) {
        Toast.makeText(this, "Posición: " + currentPage  , Toast.LENGTH_LONG).show();
        // this.imageClicked(currentPage);
    }

}
