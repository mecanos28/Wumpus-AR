package com.example.benja.canvas;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class Coordenadas extends AppCompatActivity  {

    LocationManager locationManager;
    LocationListener locationListener;
    double latitudeGPS;
    double longitudeGPS;
    TextView tv_info;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordenadas);
        tv_info = (TextView) findViewById(R.id.tv_information);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        longitudeGPS = 0.0;
        latitudeGPS = 0.0;

        //Recibe el id del grafo

        Bundle b = new Bundle();
        b = getIntent().getExtras();
        String graphID = b.getString("graphID");

//Acceso a la BD

        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 5);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor cell = db.rawQuery("SELECT GRAPH.relations, GRAPH.number_of_caves FROM GRAPH WHERE GRAPH.id = \"" + graphID +"\";", null);
        if (cell.moveToFirst()){
            String info=cell.getString(0);
            String caves=cell.getString(1);
            cell.close();
            Toast.makeText(this, "ID: " + graphID + "\nrelaciones: " + info + "\ncaves: " + caves,  Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Error obteniendo el las relaciones y el ID!", Toast.LENGTH_LONG).show();
            db.close();
        }
        cell.close();
    }

    public void getCurrentLocation(View v) {
        flag = displayGpsStatus();
        if (flag) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);
            String coordinates = "Longitude: " + getLongitudeGPS() + "\nLatitude: " + getLatitudeGPS();
            tv_info.setText(coordinates);
        } else {
            createAlertDialog("Estado del GPS", "El GPS está desactivado.");
        }
    }

    private boolean displayGpsStatus() {
        boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gpsStatus) {
            return true;
        }
        else {
            return false;
        }
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

    public double getLatitudeGPS() { return latitudeGPS;  }

    public double getLongitudeGPS() {
        return longitudeGPS;
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String coordinates = "Longitude: " + getLongitudeGPS() + "\nLatitude: " + getLatitudeGPS();
                    tv_info.setText(coordinates);
                }
            });
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

}
