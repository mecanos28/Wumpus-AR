package com.example.benja.canvas;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class PassActivity extends AppCompatActivity {

    double latitudElegida;
    double longitudElegida;


    TextView tv_infoElegidas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);
        tv_infoElegidas = (TextView) findViewById(R.id.infoElegidas);

        //Recibe las coordenadas
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        latitudElegida = b.getDouble("Latitud");
        longitudElegida =b.getDouble("Longitud");

        tv_infoElegidas.setText("---Coordenadas Elegidas-- Latitud: "+latitudElegida+"Longitud: "+longitudElegida);


    }
}