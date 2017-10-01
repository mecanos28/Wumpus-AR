package com.example.benja.canvas;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    public boolean tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void tipoIndividual(View vista)
    {
        Intent i = new Intent(this,SelectPolyActivity.class);
        startActivity(i);

    }

    public void tipoMultijugador(View vista)
    {
        setContentView(R.layout.layout_multijugador);
    }

}
