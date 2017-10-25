package com.clavicusoft.wumpus.AR;


import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.World;
import com.clavicusoft.wumpus.Maze.CaveContent;
import com.clavicusoft.wumpus.R;

import java.util.ArrayList;

public class Game_World extends FragmentActivity implements OnClickBeyondarObjectListener {

    private BeyondarFragmentSupport currentBeyondARFragment;
    private AR_Helper worldHelper;
    private World world;
    private Game_Data data;
    private int game_ID;
    private int number_of_caves;

    /**
     * Sets the view once this activity starts.
     *
     * @param savedInstanceState Activity's previous saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_layout);

        //Get the game parameters
        Bundle b = getIntent().getExtras();
        game_ID = b.getInt("game_ID");
        number_of_caves = b.getInt("number_of_caves");

        data = new Game_Data(this, game_ID, 1);

        //Sets the fragment.
        currentBeyondARFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(
                R.id.beyondarFragment);

        worldHelper = new AR_Helper(this);
        worldHelper.updateObjects(this, 0, data);

        //Allows BeyondAR to access user's position
        BeyondarLocationManager.setLocationManager((LocationManager) this.getSystemService(
                Context.LOCATION_SERVICE));

        //Starts the world
        world = worldHelper.getWorld();

        setDistanceParameters();
        currentBeyondARFragment.setWorld(world);

        setLocationParameters();

        //Assign onClick listener
        currentBeyondARFragment.setOnClickBeyondarObjectListener(this);
    }

    /**
     * Enables the GPS once the game resumes.
     */
    @Override
    protected void onResume(){
        // Enable GPS
        super.onResume();
        BeyondarLocationManager.enable();
    }

    /**
     * Disables the GPS once the game pauses.
     */
    @Override
    protected void onPause(){
        // Disable GPS
        super.onPause();
        BeyondarLocationManager.disable();
    }

    /**
     * Displays alert message when the user click a GeoObject.
     * @param arrayList List of the GeoObjects.
     */
    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> arrayList) {
        // The first element in the array belongs to the closest BeyondarObject
        final int cave_Number = getCaveNumberFromName(arrayList.get(0).getName());
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("Has encontrado " + arrayList.get(0).getName());
        newDialog.setMessage("¿Desea entrar a esta cueva?");
        newDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
                updateGame(cave_Number);
            }
        });
        newDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        newDialog.show();
    }

    /**
     * Sets the distance parameters for BeyondAR
     */
    public void setDistanceParameters () {
        //Set the distance (in meters) which the objects will be considered to render.
        currentBeyondARFragment.setMaxDistanceToRender(3000);
        // Set the distance factor for rendering all the objects. As bigger the factor the
        // closer the objects
        currentBeyondARFragment.setDistanceFactor(4);
        /*
         * When a GeoObject is rendered
         * according to its position it could look very big if it is too close. Use
         * this method to render near objects as if there were farther.
         * For instance if there is an object at 1 meters and we want to have
         * everything at to look like if they where at least at 10 meters, we could
         * use this method for that purpose.
         */
        currentBeyondARFragment.setPushAwayDistance(0);
        /*
         * When a GeoObject is rendered
         * according to its position it could look very small if it is far away. Use
         * this method to render far objects as if there were closer.
         * For instance if there are objects farther than 50 meters and we want them
         * to be displayed as they where at 50 meters, we could use this method for
         * that purpose.
         */
        currentBeyondARFragment.setPullCloserDistance(0);
    }

    /**
     * Sets the location parameters for BeyondAR.
     */
    public void setLocationParameters () {
        //Dynamic position for the world
        BeyondarLocationManager.enable();

        //Allow BeyondAR to update the world position.
        BeyondarLocationManager.addWorldLocationUpdate(world);
        BeyondarLocationManager.setLocationManager((LocationManager) getSystemService(
                Context.LOCATION_SERVICE));
    }

    public void updateGame (int cave_Number) {
        checkCaveContent(cave_Number);
        worldHelper.updateObjects(this, cave_Number, data);
    }

    public int getCaveNumberFromName (String name) {
        return Integer.parseInt(name.substring(name.length() - 1));
    }

    public void checkCaveContent (int cave_Number){
        CaveContent content = data.getCaveContent(cave_Number);
        switch (content) {
            case WUMPUS:
                break;
            case BAT:
                break;
            case PIT:
                break;
        }
    }
}
