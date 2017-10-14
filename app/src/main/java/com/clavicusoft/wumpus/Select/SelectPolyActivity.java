package com.clavicusoft.wumpus.Select;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.clavicusoft.wumpus.Database.AdminSQLite;
import com.clavicusoft.wumpus.Draw.DrawMazeActivity;
import com.clavicusoft.wumpus.Map.Coordinates;
import com.clavicusoft.wumpus.R;

public class SelectPolyActivity extends Activity {

    ViewPager viewPager;
    CustomSwip  customSwip;
    int currentPage;

    /**
     * Sets the view once this activity starts. Fills the slider with the images.
     *
     * @param savedInstanceState Activity's previous saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_poly);
        currentPage = 1;
        viewPager=(ViewPager)findViewById(R.id.ImageSlider);
        int[] imageResources = {R.drawable.tetra_light, R.drawable.octa_light,
                R.drawable.cube_light, R.drawable.icosa_light, R.drawable.dodeca_light};
        customSwip = new CustomSwip(this,imageResources);
        viewPager.setAdapter(customSwip);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position + 1;
            }
        });
    }

    /**
     * Retrieves the DB information about the selected graph and sends it to the Coordinates
     * activity.
     *
     * @param graph The graph's position inside the slider.
     */
    public void imageClicked(int graph) {
        //Starts the DB
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 6);
        SQLiteDatabase db = admin.getWritableDatabase();

        //Gets the name of the selected maze
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

        //Gets the DB information the selected maze.
        Cursor cell = db.rawQuery("SELECT GRAPH.id FROM GRAPH WHERE GRAPH.name = \"" + graphName +
                "\";", null);
        if (cell.moveToFirst()){
            int graphID = cell.getInt(0);
            cell.close();
            String stringGraphID = Integer.toString(graphID);

            //Sends the information about the maze to the Coordinates activity.
            Intent i = new Intent(this, Coordinates.class);
            i.putExtra("graphID",stringGraphID);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in,
                    R.anim.fade_out);
            startActivity(i, options.toBundle());
        }
        else {
            Toast.makeText(this, "El Wumpus no se encuentra en estas cuevas, intenta otra.",
                    Toast.LENGTH_LONG).show();
            db.close();
        }
        cell.close();
    }

    /**
     * Starts the draw maze activity, and sets the animation for the transition.
     *
     * @param view Current view.
     */
    public void drawLabyrinthView(View view)
    {
        Intent i = new Intent(this,DrawMazeActivity.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in,
                R.anim.fade_out);
        startActivity(i, options.toBundle());
    }

    /**
     * Starts the select from library activity, and sets the animation for the transition.
     *
     * @param view Current view.
     */
    public void selectFromLibView(View view)
    {
        Intent i = new Intent(this,SelectFromLibActivity.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in,
                R.anim.fade_out);
        startActivity(i, options.toBundle());
    }

    /**
     * Sets the animation for the onBackPressed function.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    /**
     * Gets the position of the slider and sends it to the imageClicked function.
     *
     * @param view Current view.
     */
    public void startGame(View view) {
        this.imageClicked(currentPage);
    }

}
