package com.clavicusoft.wumpus.Map;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.clavicusoft.wumpus.R;

public class PassActivity extends AppCompatActivity {

    double selectedLatitude;
    double selectedLongitude;


    TextView tv_infoElegidas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);
        tv_infoElegidas = (TextView) findViewById(R.id.infoElegidas);

        //Recibe las coordenadas
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        selectedLatitude = b.getDouble("Latitud");
        selectedLongitude =b.getDouble("Longitud");

        tv_infoElegidas.setText("---Coordenadas Elegidas-- Latitud: "+ selectedLatitude +"Longitud: "+ selectedLongitude);


    }
}