package com.example.benja.canvas;

import android.app.Activity;
import android.app.ActivityOptions;
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

public class SelectPolyActivity extends Activity {

    ViewPager viewPager;
    CustomSwip  customSwip;
    int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_poly);
        currentPage = 1;
        viewPager=(ViewPager)findViewById(R.id.ImageSlider);
        int[] imageResources = {R.drawable.tetra_light, R.drawable.octa_light, R.drawable.cube_light, R.drawable.icosa_light, R.drawable.dodeca_light};
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
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 6);
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
        Cursor cell = db.rawQuery("SELECT GRAPH.id FROM GRAPH WHERE GRAPH.name = \"" + graphName +"\";", null);
        if (cell.moveToFirst()){
            int graphID = cell.getInt(0);
            cell.close();
            String stringGraphID = Integer.toString(graphID);
            Intent i = new Intent(this, Coordenadas.class);
            i.putExtra("graphID",stringGraphID);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
            startActivity(i, options.toBundle());
        }
        else {
            Toast.makeText(this, "The Wumpus isn't around this caves. Try another one!", Toast.LENGTH_LONG).show();
            db.close();
        }
        cell.close();
    }

    public void drawLabyrinthView(View view)
    {
        Intent i = new Intent(this,DrawMazeActivity.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        startActivity(i, options.toBundle());
    }

    public void selectFromLibView(View view)
    {
        Intent i = new Intent(this,SelectFromLibActivity.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        startActivity(i, options.toBundle());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    public void startGame(View view) {
        this.imageClicked(currentPage);
    }

}
