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
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

    LocationManager locationManager;
    LocationListenerGPS locationListenerGPS;
    ProgressBar loading;
    SpinnerActivity sp;
    double latitudeGPS;
    double longitudeGPS;
    double distance;
    TextView tv_dist;
    Button but_VerUbicacion;
    boolean flag;
    Spinner spn_distances;
    int graph_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordenadas);
        tv_dist = (TextView) findViewById(R.id.tv_dist);
        but_VerUbicacion=(Button)findViewById(R.id.buttonVerMiUbicacion);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListenerGPS = new LocationListenerGPS();
        longitudeGPS = 0.0;
        latitudeGPS = 0.0;

        flag = false;

        //Spinner
        spn_distances = (Spinner) findViewById(R.id.spn_distancias);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.distances, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_distances.setAdapter(adapter);
        sp = new SpinnerActivity();
        spn_distances.setOnItemSelectedListener(sp);
        spn_distances.setVisibility(View.VISIBLE);

        //loading bar
       loading = (ProgressBar) findViewById(R.id.progressBar);
        loading.setVisibility(View.GONE);

        //Recibe el id del grafo
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        String graphID = b.getString("graphID");
        graph_id = Integer.parseInt(graphID);
    }

    public void getCurrentLocation(View v) {
        flag = displayGpsStatus();
        if (flag) {
            if (sp.selected) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                tv_dist.setVisibility(View.GONE);
                but_VerUbicacion.setVisibility(View.GONE);
                spn_distances.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                if(displayNetworkGPSStatus()){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5 * 1000, 3, locationListenerGPS);
                }
                else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000, 3, locationListenerGPS);
                }

            } else {
                Toast.makeText(this, "Por favor indique la distancia deseada.", Toast.LENGTH_LONG).show();
            }
        } else {
            createAlertDialog("Estado del GPS", "El GPS está desactivado.");
        }
    }

   private boolean displayGpsStatus() {
        return  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
   }

    private boolean displayNetworkGPSStatus() {
        return  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    protected void createAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Activar GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(i);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public double getLatitudeGPS() {
        return latitudeGPS;
    }

    public double getLongitudeGPS() {
        return longitudeGPS;
    }

    public class LocationListenerGPS implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            loading.setVisibility(View.VISIBLE);
            if (longitudeGPS != 0 && latitudeGPS != 0) {
                loading.setVisibility(View.GONE);
                tv_dist.setVisibility(View.VISIBLE);
                but_VerUbicacion.setVisibility(View.VISIBLE);
                spn_distances.setVisibility(View.VISIBLE);
                Intent i = new Intent(Coordinates.this, MapsActivity.class);
                i.putExtra("Latitud", getLatitudeGPS());
                i.putExtra("Longitud", getLongitudeGPS());
                i.putExtra("graphID",graph_id);
                i.putExtra("Distancia",distance);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(Coordinates.this, R.anim.fade_in, R.anim.fade_out);
                startActivity(i, options.toBundle());
            }
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        boolean selected;

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            selected = true;
            switch(pos){
                case 0:
                    distance = 6;
                    break;
                case 1:
                    distance = 8;
                    break;
                case 2:
                    distance = 10;
                    break;
                case 3:
                    distance = 12;
                    break;
                case 4:
                    distance = 100;
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            selected = false;
        }
    }
}
