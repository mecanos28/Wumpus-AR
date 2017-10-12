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

import com.clavicusoft.wumpus.Database.AdminSQLite;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//Recibe las coordenadas
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        latitude = b.getDouble("Latitud");
        longitude =b.getDouble("Longitud");
        graph_ID=b.getInt("graphID");
        distance=b.getDouble("Distancia");

        //Configurar botones
        btnContinue =(Button) findViewById(R.id.bcontinuar);
        btnHybrid =(Button) findViewById(R.id.bhibrido);
        btnTerrain =(Button) findViewById(R.id.bterreno);

        btnContinue.setOnClickListener(this);
        btnTerrain.setOnClickListener(this);
        btnHybrid.setOnClickListener(this);

        //Para preguntar si se selecciona el original
        selectedLatitude =0.0;
        selectedLongitude =0.0;

        //Inicializar para la formula
        meterToCoordinates = 0.0000095;


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

        //Acceso a la BD
        accesoBD(graph_ID);


    }

    @Override
    public void onClick(View view) { //Funcion de los botones
        switch (view.getId())
        {
            case R.id.bterreno:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.bhibrido:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.bcontinuar: {
                Intent i = new Intent(MapsActivity.this, PassActivity.class);
                if (selectedLatitude !=0.0 && selectedLongitude !=0.0)
                {

                    //Crear las cuevas

                    putCave(numberCaves, selectedLatitude, selectedLongitude);

                    //Se las pasa al otro
                    i.putExtra("Latitud", selectedLatitude);
                    i.putExtra("Longitud", selectedLongitude);

                }
                else {//Crear las cuevas
                    putCave(numberCaves, latitude, longitude);


                    //Se las pasa al otro
                    i.putExtra("Latitud", latitude);
                    i.putExtra("Longitud", longitude);

                }
                ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
              //  startActivity(i, options.toBundle());
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
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings=mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng actual = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(actual).title("Ubicación Actual").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        float zoomLevel=16;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actual, zoomLevel));



        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLngElegida) {
                mMap.clear();
                LatLng actual = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(actual).title("Ubicación Actual").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                mMap.addMarker(new MarkerOptions().title("Posicion Deseada").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(latLngElegida));
                selectedLatitude =latLngElegida.latitude;
                selectedLongitude =latLngElegida.longitude;


            }
        });
    }


    public void accesoBD (int graph_id)
    {

        //Acceso a la BD
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 6);
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

    }


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
                createCave(3, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                break;
            case 4:
                /*
                *  3 - 4
                *  |   |
                *  1 - 2
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, addMetersToLatitude(latitudeGPS, distance, 1, true), longitudeGPS);
                createCave(3, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(4, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 1, true));
                break;
            case 5:
                /*
                *  4  -  2
                *  |  1  |
                *  5  -  3
                */
                createCave(1, latitudeGPS, longitudeGPS);
                createCave(2, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(3, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(4, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 2, false));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 2, false));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 2, false));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(16, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 2, false));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(16, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(17, addMetersToLatitude(latitudeGPS, distance, 2, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(16, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(17, addMetersToLatitude(latitudeGPS, distance, 2, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(16, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(17, addMetersToLatitude(latitudeGPS, distance, 2, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(18, addMetersToLatitude(latitudeGPS, distance, 2, false), longitudeGPS);
                createCave(19, addMetersToLatitude(latitudeGPS, distance, 2, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
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
                createCave(5, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(6, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(7, addMetersToLatitude(latitudeGPS, distance, 1, false), longitudeGPS);
                createCave(8, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(9, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(10, addMetersToLatitude(latitudeGPS, distance, 2, true), longitudeGPS);
                createCave(11, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(12, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(13, addMetersToLatitude(latitudeGPS, distance, 1, false), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(14, latitudeGPS, addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(15, addMetersToLatitude(latitudeGPS, distance, 1, true), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(16, addMetersToLatitude(latitudeGPS, distance, 2, true), addMetersToLongitude(longitudeGPS, distance, 2, false));
                createCave(17, addMetersToLatitude(latitudeGPS, distance, 2, false), addMetersToLongitude(longitudeGPS, distance, 1, true));
                createCave(18, addMetersToLatitude(latitudeGPS, distance, 2, false), longitudeGPS);
                createCave(19, addMetersToLatitude(latitudeGPS, distance, 2, false), addMetersToLongitude(longitudeGPS, distance, 1, false));
                createCave(20, addMetersToLatitude(latitudeGPS, distance, 2, false), addMetersToLongitude(longitudeGPS, distance, 2, false));
                break;
        }
    }

    public void createCave (int cave_number, double coordX, double coordY) {
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 6);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues data = new ContentValues();
        data.put("id", game_id);
        data.put("graph_id", graph_ID);
        data.put("cave_number", cave_number);
        data.put("latitude", String.valueOf(coordX));
        data.put("longitude", String.valueOf(coordY));
        db.insert("GAME", null, data);


        LatLng cuevaNueva = new LatLng(coordX, coordY);

        if (cave_number==1)
        {        mMap.addMarker(new MarkerOptions().position(cuevaNueva).title("Cueva "+cave_number).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        else {
            mMap.addMarker(new MarkerOptions().position(cuevaNueva).title("Cueva " + cave_number).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }


    }


    /*
    * Adds a distance in meters to the latitude a number of times.
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

    /*
    * Adds a distance in meters to the longitude a number of times.
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
