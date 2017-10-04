package com.example.benja.canvas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class Coordenadas extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordenadas);

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





}
