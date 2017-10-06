package com.example.benja.canvas;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void tipoIndividual(View vista)
    {
        Intent i = new Intent(this,SelectPolyActivity.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_down, R.anim.slide_out_down);
        startActivity(i, options.toBundle());

    }

    public void tipoMultijugador(View vista)
    {
        Intent i = new Intent(this, BluetoothSettings.class);
        startActivity(i);
    }

}
