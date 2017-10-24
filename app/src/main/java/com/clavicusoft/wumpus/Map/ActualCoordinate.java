package com.clavicusoft.wumpus.Map;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clavicusoft.wumpus.Database.AdminSQLite;
import com.clavicusoft.wumpus.Map.Coordinates;
import com.clavicusoft.wumpus.Map.MapsActivity;
import com.clavicusoft.wumpus.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class ActualCoordinate extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //Variables


    //LocationManager locationManager; //Manager that requests location updates.
    //ActualCoordinate.LocationListenerGPS locationListenerGPS; //Listener that gets the location changes for the game start.
    double latitudeGPS; //Current latitude.
    double longitudeGPS; //Current longitude.
    boolean flag; //Checks if location services are enabled.
    //AlertDialog.Builder alert; //Alert
    TextView tv_info, tv_actualCave;
    double contador;
    Location caves[];
    int game_id;
    int numCaves;
    double nearCave;
    boolean notCave;

    //Atributos del Api
    static final String TAG = ActualCoordinate.class.getName();

    static final int RC_LOCATION_PERMISION = 100;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    static final int INTERVAL = 200;
    static final int FAST_INTERVAL = 150;

    boolean simulateSemaphore;
    boolean mRequestLocationUpdates;
    Location mcurrentLocation;

    /**
     * Initializes services to get the location updates.
     *
     * @param savedInstanceState Activity's previous saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualcoordinate);

//Api google
        mRequestLocationUpdates = false;
        contador = 0;
        simulateSemaphore=true;

        //Location services.
        // locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationListenerGPS = new ActualCoordinate.LocationListenerGPS();
        longitudeGPS = 0.0;
        latitudeGPS = 0.0;
        flag = false;

        //alert = new AlertDialog.Builder(this);

        //TextView
        tv_info = (TextView) findViewById(R.id.textViewActualCoordinate);
        tv_actualCave = (TextView) findViewById(R.id.actualCave);


        //Gets game ID from previos activity.
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        game_id = b.getInt("gameID");
        numCaves = b.getInt("numCaves");

        //Inicializa el tamaño del array
        caves = new Location[numCaves];


        //Crea la matriz de coordenadas
        accessBD(game_id);


//Google


        // createLocationRequest();

        //Solicitar permisos si es necesario (Android 6.0+)
        requestPermissionIfNeedIt();

        initGoogleApiClient();

        // getCurrentLocation();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected())
                startLocationUpdates();
            else
                mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void initGoogleApiClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            //Creamos una peticion de ubicacion con el objeto LocationRequest
            createLocationRequest();
        }


    }

    private void createLocationRequest() { //Encapsula las peticiones del Api

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL); //cada cuanto se actualiza
        mLocationRequest.setFastestInterval(FAST_INTERVAL); //Ultima detectada
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //Alta precision


    }


    private void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && !mRequestLocationUpdates) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                mRequestLocationUpdates = true;
            }
        }
    }

    private void stopLocationUpdates() { //Se llama cuando la app ya no esta activa, como en el onDestroy
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && mRequestLocationUpdates) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    private void requestPermissionIfNeedIt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION_PERMISION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION_PERMISION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                requestPermissionIfNeedIt();
            }
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (!mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
        Toast.makeText(this, getString(R.string.app_name), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed" + connectionResult.getErrorMessage());
    }


    @Override
    public void onLocationChanged(Location location) {
        if (simulateSemaphore) //Solo entre 1
        {simulateSemaphore=false;
            mcurrentLocation = location;
        refreshUi();
            simulateSemaphore=true;
        }


    }

    private void refreshUi() {
        if (mcurrentLocation != null) {
            longitudeGPS=mcurrentLocation.getLongitude();
            latitudeGPS=mcurrentLocation.getLatitude();

            //Gets current coordinates.
            notCave = true; //Pregunta si estoy a medio metro de la cueva
            nearCave = Double.MAX_VALUE; //Verifica cueva más cercana

            int i = 0;//Indice de last cuevas
            while (i < numCaves && notCave) {

                if (mcurrentLocation.distanceTo(caves[i]) <= 0.25) {
                    tv_actualCave.setText("Cueva Actual: " +(i+1));
                    notCave = false;
                    nearCave = 0.0;
                } else {
                    if (nearCave > mcurrentLocation.distanceTo(caves[i])) {
                        nearCave = mcurrentLocation.distanceTo(caves[i]);
                    }

                }

                ++i;
            }

            if (notCave) {
                tv_actualCave.setText("No estoy en ninguna cueva.. La más cercana se encuentra a " + (nearCave - 0.25) + "metros ");
            }

            ++contador;

            tv_info.setText("Latitud: " + latitudeGPS + " longitud: " + longitudeGPS + "  \n---Actualización: " + contador + "---");

        }
    }



    public void accessBD(int game_id) {

        double coordLong;
        double coordLat;
        String number;


        Location nuevo;

        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 7);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor cell = db.rawQuery("SELECT GAME.longitude, GAME.latitude, GAME.cave_number FROM GAME WHERE GAME.id = " + game_id + ";", null);

        int indice=0;


        if (cell.moveToFirst()) {
            do {
                coordLong = Double.parseDouble(cell.getString(0));
                coordLat = Double.parseDouble(cell.getString(1));
                number=Integer.toString(cell.getInt(2));

                nuevo = new Location(number);
                nuevo.setLongitude(coordLong);
                nuevo.setLatitude(coordLat);
                caves[indice] = nuevo;
                ++indice;
            }
            while (cell.moveToNext());

            cell.close();
            db.close();
        }

        else {
            Toast.makeText(this, "Error obteniendo las coordenadas!", Toast.LENGTH_LONG).show();
            db.close();
        }
    }

}




/*



    /**
     * Requests the user to accept permissions for location and Internet services if they
     * were not previously accepted on installation.
     *
     * @param requestCode Application specific request code to match with a result
     *                    reported to onRequestPermissionsResult(int, String[], int[])
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission accepted
            } else {
                //Permission denied
                alert.setTitle("Error");
                alert.setMessage("Para poder continuar con el juego debe permitir a Wumpus acceder a su ubicación.");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        }
    }*/

    /**
     * Requests single location update to start the game with the player's location.
     *
     */
/*
    public void getCurrentLocation() {
        //Checks status of location services.
        flag = displayGpsStatus();
        if (flag) {
            //Services are enabled.

            //A distance is selected from the spinner list.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Permissions not granted.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else {
                //Permissions granted.

                if (displayNetworkGPSStatus()) {
                    //Location by network provider is on.
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,0, locationListenerGPS);



                } else {
                    //Location by network provider is off.
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListenerGPS );
                }
            }

        } else {
            //Services are disabled.
            createAlertDialog("Estado del GPS", "El GPS está desactivado.");
        }
    }*/

    /**
     * Checks whether network provider or gps provider is on.
     *
     * @return Boolean, if any of the providers are on returns true, if both are off returns false.
     *//*
    private boolean displayGpsStatus() {
        return  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Checks whether network provider or gps provider is on.
     *
     * @return Boolean, if the network provider is on returns true, if it's off returns false.
     *//*
    private boolean displayNetworkGPSStatus() {
        return  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    /**
     * Creates an alert dialog if the location services are disabled.
     *
     * @param title Title of the dialog box.
     * @param message Message of the dialog box.
     */
    /*
    protected void createAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                //Button that redirects to the location settings.
                .setPositiveButton("Activar GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(i);
                                dialog.cancel();
                            }
                        })
                //Button that closes the dialog.
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
*/
    /**
     * Gets the user's last known latitude.
     *
     * @return The player's current latitude.
     */
    /*
    public double getLatitudeGPS() {
        return latitudeGPS;
    }

    /**
     * Gets the user's last known longitude.
     *
     * @return The player's current longitude.
     *//*
    public double getLongitudeGPS() {
        return longitudeGPS;
    }


    /*
        @Override
        public void onLocationChanged(Location location) {
            //Gets current coordinates.
            notCave=true; //Pregunta si estoy en 1 metro de la cueva
            nearCave=Double.MAX_VALUE;
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();

            int i=0;//Indice de last cuevas
            while (i<numCaves&&notCave) {

                if (location.distanceTo(caves[i])<=0.5)
    {
        tv_actualCave.setText("Cueva Actual: "+caves[i].getProvider());
        notCave=false;
        nearCave=0.0;
    }

    else
    {
        if (nearCave>location.distanceTo(caves[i]))
        {nearCave=location.distanceTo(caves[i]);}

    }

++i;
        }

        if (notCave)
        {        tv_actualCave.setText("No estoy en ninguna cueva.. La más cercana se encuentra a " + (nearCave -0.5)+ "metros ");
        }




            ++contador;

               tv_info.setText("Latitud: " + latitudeGPS + " longitud: "+ longitudeGPS+"  ---contador: "+ contador+"---");

        }


        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        /**
         * Called if provider is enabled.
         *
         * @param s Provider.

        @Override
        public void onProviderEnabled(String s) {
        }

        /**
         * Called if provider is enabled.
         *
         * @param s Provider.

        @Override
        public void onProviderDisabled(String s) {
        }
    };
    */

