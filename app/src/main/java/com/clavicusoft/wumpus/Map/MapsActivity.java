package com.clavicusoft.wumpus.Map;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.clavicusoft.wumpus.AR.Game_World;
import com.clavicusoft.wumpus.Database.AdminSQLite;
import com.clavicusoft.wumpus.Maze.CaveContent;
import com.clavicusoft.wumpus.Maze.Graph;
import com.clavicusoft.wumpus.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Shows the real location in the map and generates the labyrinth from where the user wishes. Storing them in the database with a game id
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;

    private double latitude;
    private double longitude;

    double selectedLatitude;
    double selectedLongitude;
    double meterToCoordinates;

    int graph_ID;
    String info;
    int numberCaves;
    int game_id;
    double distance;

    Button btnContinue;
    Button btnTerrain;
    Button btnHybrid;
    Button btnListo;

    boolean creado;

    Graph graph;
    CaveContent[] caveContents;
    /**
     * Obtain the SupportMapFragment and get notified when the map is ready to be used. Further,
     * gets the number of caves and the relationships according to the id of the graph in the database.
     * @param savedInstanceState State of the instance saved
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle b;
        b = getIntent().getExtras();
        latitude = b.getDouble("Latitud");
        longitude =b.getDouble("Longitud");
        graph_ID=b.getInt("graphID");
        distance=b.getDouble("Distancia");

        btnContinue =(Button) findViewById(R.id.bcontinuar);
        btnHybrid =(Button) findViewById(R.id.bhibrido);
        btnTerrain =(Button) findViewById(R.id.bterreno);
        btnListo=(Button) findViewById(R.id.bListo);

        btnContinue.setOnClickListener(this);
        btnTerrain.setOnClickListener(this);
        btnHybrid.setOnClickListener(this);
        btnListo.setOnClickListener(this);

        selectedLatitude =0.0;
        selectedLongitude =0.0;

        meterToCoordinates = 0.0000095;

        creado=false;


        int status= GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (status== ConnectionResult.SUCCESS)
        {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);}

        else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,(Activity)getApplicationContext(),10);
            dialog.show();
        }

        accessBD(graph_ID);


    }

    /**
     * Button Functions, change the terrain map type to hybrid. On the other hand, you can start creating the
     * labyrinth from the point the user chooses and displays it on the map.
     * @param view  Used view
     */

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.bterreno:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.bhibrido:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.bcontinuar: {


                if (selectedLatitude !=0.0 && selectedLongitude !=0.0)
                {

                    //Create caves

                    putCave(numberCaves, selectedLatitude, selectedLongitude);

                }
                else {//Create caves
                    putCave(numberCaves, latitude, longitude);
                }
                game_id++;
                creado=true;
            }
            break;

            case R.id.bListo:
                if(creado) {
                    Intent i = new Intent(MapsActivity.this, ActualCoordinate.class);
                    i.putExtra("gameID", (game_id - 1));
                    i.putExtra("numCaves", numberCaves);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(MapsActivity.this, R.anim.fade_in, R.anim.fade_out);
                    startActivity(i, options.toBundle());
                }
                    else
                    {
                    Toast.makeText(MapsActivity.this,"Debe crear un mapa de juego",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;


        }


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * Also, with a long click, it displays a new marker.
     *
     * @param googleMap google Map that is shown

     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings=mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Add a marker in the current point and move the camera
        LatLng current = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(current).title("Ubicación Actual").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        float zoomLevel=16;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));



        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLngChosen) {
                mMap.clear();
                LatLng actual = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(actual).title("Ubicación Actual").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                mMap.addMarker(new MarkerOptions().title("Posicion Deseada").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(latLngChosen));
                selectedLatitude =latLngChosen.latitude;
                selectedLongitude =latLngChosen.longitude;


            }
        });
    }

    /**
     *Gets the number of caves and the relationships according to the id of the graph in the database.
     * @param graph_id id of the selected graph.
     *
     */

    public void accessBD(int graph_id)
    {

        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 7);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor cell = db.rawQuery("SELECT GRAPH.relations, GRAPH.number_of_caves FROM GRAPH WHERE GRAPH.id = " + graph_id + ";", null);
        if (cell.moveToFirst()) {
            info = cell.getString(0);
            numberCaves = cell.getInt(1);
            cell.close();
        } else {
            Toast.makeText(this, "Error obteniendo el las relaciones y el ID!", Toast.LENGTH_LONG).show();
            db.close();
        }
        cell.close();
        cell = db.rawQuery("SELECT MAX(id) FROM GAME;", null);
        if (cell.moveToFirst()) {
            game_id = cell.getInt(0) + 1;
            cell.close();
        } else {
            Toast.makeText(this, "Error obteniendo el las ID del juego!", Toast.LENGTH_LONG).show();
            db.close();
        }
        cell.close();
        graph = new Graph(numberCaves);
        //TODO posible cambio
        caveContents = graph.randomEntitiesGen(0);

    }

    /**
     * Generates the location for the Maze's caves.
     *
     * @param cave Number of caves in the Graph.
     * @param latitudeGPS Player's latitude.
     * @param longitudeGPS Player's longitude.
     */
    public void putCave (int cave,double latitudeGPS, double longitudeGPS) {
        mMap.clear();
        switch (numberCaves) {
            case 2:
                /*
                *  1 - 2
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                break;
            case 3:
                /*
                *     3
                *     |
                * 1 - 2
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(3, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                break;
            case 4:
                /*
                *  3 - 4
                *  |   |
                *  1 - 2
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(3, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(4, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                break;
            case 5:
                /*
                *  4  -  2
                *  |  1  |
                *  5  -  3
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                break;
            case 6:
                /*
                *  2 - 5
                *  |   |
                *  1 - 4
                *  |   |
                *  3 - 6
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                break;
            case 7:
                /*
                *      2 - 5
                *      |   |
                *  7 - 1 - 4
                *      |   |
                *      3 - 6
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                break;
            case 8:
                /*
                *  8 - 2 - 5
                *  |   |   |
                *  7 - 1 - 4
                *      |   |
                *      3 - 6
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                break;
            case 9:
                /*
                *  8 - 2 - 5
                *  |   |   |
                *  7 - 1 - 4
                *  |   |   |
                *  9 - 3 - 6
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                break;
            case 10:
                /*
                *  8 - 2 - 5
                *  |   |   |
                *  7 - 1 - 4 - 10
                *  |   |   |
                *  9 - 3 - 6
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                break;
            case 11:
                /*
                *  8 - 2 - 5 - 11
                *  |   |   |   |
                *  7 - 1 - 4 - 10
                *  |   |   |
                *  9 - 3 - 6
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                break;
            case 12:
                /*
                *  8 - 2 - 5 - 11
                *  |   |   |   |
                *  7 - 1 - 4 - 10
                *  |   |   |   |
                *  9 - 3 - 6 - 12
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                break;
            case 13:
                /*
                *  8 - 2 - 5 - 11
                *  |   |   |   |
                *  7 - 1 - 4 - 10
                *  |   |   |   |
                *  9 - 3 - 6 - 12
                *  |
                *  13
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                break;
            case 14:
                /*
                *  8 - 2 - 5 - 11
                *  |   |   |   |
                *  7 - 1 - 4 - 10
                *  |   |   |   |
                *  9 - 3 - 6 - 12
                *  |   |
                *  13- 14
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                break;
            case 15:
                /*
                *  8 - 2 - 5 - 11
                *  |   |   |   |
                *  7 - 1 - 4 - 10
                *  |   |   |   |
                *  9 - 3 - 6 - 12
                *  |   |   |
                *  13- 14- 15
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                break;
            case 16:
                /*
                *  8 - 2 - 5 - 11
                *  |   |   |   |
                *  7 - 1 - 4 - 10
                *  |   |   |   |
                *  9 - 3 - 6 - 12
                *  |   |   |   |
                *  13- 14- 15- 16
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(16, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                break;
            case 17:
                /*
                *  17- 8 - 2 - 5 - 11
                *      |   |   |   |
                *      7 - 1 - 4 - 10
                *      |   |   |   |
                *      9 - 3 - 6 - 12
                *      |   |   |   |
                *      13- 14- 15- 16
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(16, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(17, addMetersToLatitude(latitudeGPS, distance, 2, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                break;
            case 18:
                /*
                *  17- 8 - 2 - 5 - 11
                *  |   |   |   |   |
                *  18- 7 - 1 - 4 - 10
                *      |   |   |   |
                *      9 - 3 - 6 - 12
                *      |   |   |   |
                *      13- 14- 15- 16
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(16, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(17, addMetersToLatitude(latitudeGPS, distance, 2, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(18, addMetersToLatitude(latitudeGPS, distance, 2, false), longitudeGPS);
                break;
            case 19:
                /*
                *  17- 8 - 2 - 5 - 11
                *  |   |   |   |   |
                *  18- 7 - 1 - 4 - 10
                *  |   |   |   |   |
                *  19- 9 - 3 - 6 - 12
                *      |   |   |   |
                *      13- 14- 15- 16
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(16, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(17, addMetersToLatitude(latitudeGPS, distance, 2, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(18, addMetersToLatitude(latitudeGPS, distance, 2, false), longitudeGPS);
                createCave(19, addMetersToLatitude(latitudeGPS, distance, 2, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                break;
            case 20:
                /*
                *  17- 8 - 2 - 5 - 11
                *  |   |   |   |   |
                *  18- 7 - 1 - 4 - 10
                *  |   |   |   |   |
                *  19- 9 - 3 - 6 - 12
                *  |   |   |   |   |
                *  20- 13- 14- 15- 16
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(16, addMetersToLatitude(latitudeGPS, distance, 2, true),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(17, addMetersToLatitude(latitudeGPS, distance, 2, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(18, addMetersToLatitude(latitudeGPS, distance, 2, false), longitudeGPS);
                createCave(19, addMetersToLatitude(latitudeGPS, distance, 2, false),
                        addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(20, addMetersToLatitude(latitudeGPS, distance, 2, false),
                        addMetersToLongitude(longitudeGPS, distance, 2, false));
                break;
        }
        startGame();
    }

    public void startGame()
    {
        Intent i = new Intent(this, Game_World.class);
        i.putExtra("game_ID",game_id);
        i.putExtra("number_of_caves",numberCaves);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in,
                R.anim.fade_out);
        startActivity(i, options.toBundle());
    }

    /**
     * Stores the cave in the DB.
     *
     * @param cave_number The number of the cave inside the graph.
     * @param coordX The latitude of the cave.
     * @param coordY The longitude of the cave.
     */
    public void createCave (int cave_number, double coordX, double coordY) {
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 7);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues data = new ContentValues();
        data.put("id", game_id);
        data.put("graph_id", graph_ID);
        data.put("cave_number", cave_number);
        data.put("latitude", String.valueOf(coordX));
        data.put("longitude", String.valueOf(coordY));
        data.put("content", caveContents[cave_number-1].getValue());
        db.insert("GAME", null, data);

        LatLng newCave = new LatLng(coordX, coordY);

        if (cave_number==1)
        {        mMap.addMarker(new MarkerOptions().position(newCave).title("Cueva "+cave_number)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        else {
            mMap.addMarker(new MarkerOptions().position(newCave).title("Cueva " + cave_number)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

    }


    /**
     * Adds meters to the latitude of a location.
     *
     * @param latitude Actual latitude.
     * @param meters Amount of meters to add.
     * @param times Number of times you want to add those meters.
     * @param sum Checks if you want to add or subtract the meters.
     * @return The new latitude.
     */
    public double addMetersToLatitude (double latitude, double meters, double times, boolean sum) {
        double result;
        if (sum) {
            result = latitude + (times * (180/Math.PI) * (meters/6378137));
        }
        else {
            result = latitude - (times * (180/Math.PI) * (meters/6378137));
        }
        return result;
    }

    /**
     * Adds meters to the longitude of a location.
     *
     * @param longitude Actual longitude.
     * @param meters Amount of meters to add.
     * @param times Number of times you want to add those meters.
     * @param sum Checks if you want to add or subtract the meters.
     * @return The new longitude.
     */
    public double addMetersToLongitude (double longitude, double meters, double times, boolean sum) {
        double result;
        if (sum) {
            //result = longitude + (times * (180/Math.PI) * (meters/6378137) / Math.cos(Math.PI/180.0 * longitude));
            result = longitude + (times * (180/Math.PI) * (meters/6378137));
        }
        else {
            //result = longitude - (times * (180/Math.PI) * (meters/6378137) / Math.cos(Math.PI/180.0 * longitude));
            result = longitude - (times * (180/Math.PI) * (meters/6378137));
        }
        return result;
    }

}
