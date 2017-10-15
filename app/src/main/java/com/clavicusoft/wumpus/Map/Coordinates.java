package com.clavicusoft.wumpus.Map;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clavicusoft.wumpus.R;

public class Coordinates extends Activity {

    LocationManager locationManager; //Manager that requests location updates.
    LocationListenerGPS locationListenerGPS; //Listener that gets the location changes for the game start.
    LocationListenerCurrent locationListenerCurrent; //Listener that gets the location changes periodically for multiplayer game.
    ProgressBar loading; //Loading bar while searching for location updates.
    SpinnerActivity sp; //Displays the available distances between caves.
    double latitudeGPS; //Current latitude.
    double longitudeGPS; //Current longitude.
    double distance; //Selected distance between caves.
    TextView tv_dist; //Sets distance on screen.
    Button btnGetLocation; //Requests current distance.
    boolean flag; //Checks if location services are enabled.
    Spinner spn_distances; //Allows to select available distances.
    int graph_id; //ID of the selected maze.
    AlertDialog.Builder alert; //Alert

    /**
     * Initializes services to get the location updates.
     *
     * @param savedInstanceState Activity's previous saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordenadas);

        //Display distance.
        tv_dist = (TextView) findViewById(R.id.tv_dist);

        //Get location.
        btnGetLocation =(Button)findViewById(R.id.buttonMyLocation);

        //Location services.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListenerGPS = new LocationListenerGPS();
        longitudeGPS = 0.0;
        latitudeGPS = 0.0;
        flag = false;

        //Spinner options.
        spn_distances = (Spinner) findViewById(R.id.spn_distancias);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.distances, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_distances.setAdapter(adapter);
        sp = new SpinnerActivity();
        spn_distances.setOnItemSelectedListener(sp);
        spn_distances.setVisibility(View.VISIBLE);

        //Loading bar.
        loading = (ProgressBar) findViewById(R.id.progressBar);
        loading.setVisibility(View.GONE);

        //Gets graph ID from previos activity.
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        String graphID = b.getString("graphID");
        graph_id = Integer.parseInt(graphID);

        alert = new AlertDialog.Builder(this);
    }

    /**
     * Requests the user to accept permissions for location and Internet services if they
     * were not previously accepted on installation.
     *
     * @param requestCode Application specific request code to match with a result
     *                    reported to onRequestPermissionsResult(int, String[], int[])
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
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
    }

    /**
     * Requests single location update to start the game with the player's location.
     *
     * @param v Current view.
     */
    public void getCurrentLocation(View v) {
        //Checks status of location services.
        flag = displayGpsStatus();
        if (flag) {
            //Services are enabled.
            if (sp.selected) {
                //A distance is selected from the spinner list.
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //Permissions not granted.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                else {
                    //Permissions granted.
                    tv_dist.setVisibility(View.GONE);
                    btnGetLocation.setVisibility(View.GONE);
                    spn_distances.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    if (displayNetworkGPSStatus()) {
                        //Location by network provider is on.
                        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListenerGPS, Looper.getMainLooper());
                    } else {
                        //Location by network provider is off.
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListenerGPS, Looper.getMainLooper());
                    }
                }
            } else {
                //No option selected on spinner.
                Toast.makeText(this, "Por favor indique la distancia deseada.", Toast.LENGTH_LONG).show();
            }
        } else {
            //Services are disabled.
            createAlertDialog("Estado del GPS", "El GPS está desactivado.");
        }
    }

    /**
     * Gets periodic location updates for multiplayer game.
     *
     * @param v Current view.
     */
    public void updateLocation(View v) {
        //Checks status of location services.
        flag = displayGpsStatus();
        if (flag) {
            //Services are enabled.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Permissions not granted
                return;
            }
            //Permissions granted
            if(displayNetworkGPSStatus()){
                //Location by network provider is on.
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 3, locationListenerCurrent);
            }
            else{
                //Location by network provider is off.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 3, locationListenerCurrent);
            }
        } else {
            //Services are disabled.
            createAlertDialog("Estado del GPS", "El GPS está desactivado.");
        }
    }

    /**
     * Checks whether network provider or gps provider is on.
     *
     * @return Boolean, if any of the providers are on returns true, if both are off returns false.
     */
    private boolean displayGpsStatus() {
        return  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Checks whether network provider or gps provider is on.
     *
     * @return Boolean, if the network provider is on returns true, if it's off returns false.
     */
    private boolean displayNetworkGPSStatus() {
        return  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
     * Creates an alert dialog if the location services are disabled.
     *
     * @param title Title of the dialog box.
     * @param message Message of the dialog box.
     */
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

    /**
     * Gets the user's last known latitude.
     *
     * @return The player's current latitude.
     */
    public double getLatitudeGPS() {
        return latitudeGPS;
    }

    /**
     * Gets the user's last known longitude.
     *
     * @return The player's current longitude.
     */
    public double getLongitudeGPS() {
        return longitudeGPS;
    }

    /**
     * Location listener for single updates.
     */
    public class LocationListenerGPS implements LocationListener {

        /**
         *Checks if location has changed and starts the map activity with the current location.
         *
         * @param location Current location.
         */
        @Override
        public void onLocationChanged(Location location) {
            //Gets current coordinates.
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            //Starts loading.
            loading.setVisibility(View.VISIBLE);
            if (longitudeGPS != 0 && latitudeGPS != 0) {
                //Succesfully gets the coordinates.
                loading.setVisibility(View.GONE);
                tv_dist.setVisibility(View.VISIBLE);
                btnGetLocation.setVisibility(View.VISIBLE);
                spn_distances.setVisibility(View.VISIBLE);
                Intent i = new Intent(Coordinates.this, MapsActivity.class);
                //Sets atributes for the next activity like coordinates, distance and the maze ID.
                i.putExtra("Latitud", getLatitudeGPS());
                i.putExtra("Longitud", getLongitudeGPS());
                i.putExtra("graphID",graph_id);
                i.putExtra("Distancia",distance);
                //Starts the map activity with the given parameters.
                ActivityOptions options = ActivityOptions.makeCustomAnimation(Coordinates.this, R.anim.fade_in, R.anim.fade_out);
                startActivity(i, options.toBundle());
            }
        }

        /**
         *Excecutes if the provider status has changed.
         *
         * @param s Provider.
         * @param i Status
         * @param bundle Extras.
         */
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        /**
         * Called if provider is enabled.
         *
         * @param s Provider.
         */
        @Override
        public void onProviderEnabled(String s) {
        }

        /**
         * Called if provider is enabled.
         *
         * @param s Provider.
         */
        @Override
        public void onProviderDisabled(String s) {
        }
    };

    public class LocationListenerCurrent implements LocationListener {

        /**
         *Checks if location has changed and starts the map activity with the current location.
         *
         * @param location Current location.
         */
        @Override
        public void onLocationChanged(Location location) {
            //Gets current location.
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
        }

        /**
         *Excecutes if the provider status has changed.
         *
         * @param s Provider.
         * @param i Status
         * @param bundle Extras.
         */
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        /**
         * Called if provider is enabled.
         *
         * @param s Provider.
         */
        @Override
        public void onProviderEnabled(String s) {
        }

        /**
         * Called if provider is enabled.
         *
         * @param s Provider.
         */
        @Override
        public void onProviderDisabled(String s) {
        }
    };

    /**
     * Spinner class
     */
    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        boolean selected; //Option selected status.

        /**
         * Invoked when an item in this view has been selected.
         *
         * @param parent AdapterView where the selection happened
         * @param view View within the AdapterView that was clicked
         * @param pos Position of the view in the adapter
         * @param id Iow id of the item that is selected
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            selected = true;
            switch(pos){
                //Sets the distance between caves depending on ehich item was selected.
                case 0:
                    //Sets distance to 5 meters.
                    distance = 5;
                    break;
                case 1:
                    //Sets distance to 10 meters.
                    distance = 10;
                    break;
                case 2:
                    //Sets distance to 25 meters.
                    distance = 25;
                    break;
                case 3:
                    //Sets distance to 50 meters.
                    distance = 50;
                    break;
                case 4:
                    //Sets distance to 100 meters.
                    distance = 100;
                    break;
                default:
                    break;
            }
        }

        /**
         * Invoked when an item in this view has not been selected.
         *
         * @param parent AdapterView where the selection is missing.
         */
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            selected = false;
        }
    }
}
