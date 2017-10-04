package com.example.benja.canvas;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MazeCreator extends AppCompatActivity {

    /*private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private double latitudeGPS;
    private double longitudeGPS;
    private TextView tv_info = (TextView) findViewById(R.id.tv_information);
    private boolean flag = false;
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*setContentView(R.layout.activity_create_maze);
       tv_info.setText("Informacion de coordenadas");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);*/
    }
/*
    public void getCurrentLocation(View v) {
        flag = displayGpsStatus();
        if (flag) {
            locationListener = new MazeCreator.MyLocationListener();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            String coordinates = "Longitude: " + getLongitudeGPS() + "\nLatitude: " + getLatitudeGPS();
            tv_info.setText(coordinates);
        } else {
            createAlertDialog("GPS Status!", "Your GPS is: OFF");
        }
    }

    private boolean displayGpsStatus() {
        boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;
        } else {
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
                                Intent myIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                                startActivity(myIntent);
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

    protected class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            latitudeGPS = loc.getLatitude();
            longitudeGPS = loc.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }*/
}
