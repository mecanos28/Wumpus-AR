package com.clavicusoft.wumpus.Select;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.clavicusoft.wumpus.R;


public class MainActivity extends Activity {
    AlertDialog.Builder alert; //Alert

    /**
     * Sets the view once this activity starts.
     *
     * @param savedInstanceState Activity's previous saved state.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        alert = new AlertDialog.Builder(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Permissions not granted.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

    }


    /**
     * Requests the user to accept permissions for camera services if they
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
                alert.setMessage("Para poder continuar con el juego debe permitir a Wumpus acceder a la cámara.");
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
     * Starts the single player activity, and sets the animation for the transition.
     *
     * @param view Current view.
    */
    public void singlePlayer (View view)
    {
        //---This block of code ensures camera permissions are granted before launching anything else
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Permissions not granted.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        //---
        else {
            Intent i = new Intent(this, SelectPolyActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_down,
                    R.anim.slide_out_down);
            startActivity(i, options.toBundle());
        }
    }

    /**
     * Starts the single player activity, and sets the animation for the transition.
     *
     * @param view Current view.
     */
    public void multiPlayer (View view)
    {
        //---This block of code ensures camera permissions are granted before launching anything else
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        //---
        else {
            Intent i = new Intent(this, Multiplayer.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_down,
                    R.anim.slide_out_down);
            startActivity(i, options.toBundle());
        }
    }
}
