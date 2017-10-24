package com.clavicusoft.wumpus.Bluetooth;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.clavicusoft.wumpus.Database.AdminSQLite;
import com.clavicusoft.wumpus.R;

import java.util.ArrayList;

public class SelectLabToShare extends Activity {

    ListView mazeList;
    ArrayList<String> datos;
    ArrayList<String> names;
    private static final int REQUEST_CONNECT_DEVICE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.labs_to_share);
        populateListView();
        mazeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /*
            * Gets the maze once one is clicked.
            */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] value = names.toArray(new String [names.size()]);
                String name = value[position];
                int graphID = getGraphID(name);
                Toast.makeText(SelectLabToShare.this, "ID: " + graphID + "\nName: " + name, Toast.LENGTH_SHORT).show();
                String laberinto = getLaberinto(graphID, name);
                listDevices(view, laberinto, name);
            }
        });
    }

    /**
     * Prepare the message to send using the lab selected.
     * @param id labs id
     * @param name labs name
     * @return msg ready to send
     */
    public String getLaberinto(int id, String name){
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 7);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor cell = db.rawQuery("SELECT * FROM GRAPH WHERE GRAPH.name = \"" + name +"\";", null);
        String laberintoObtenido = "";
        String laberintoObtenido2 = "";
        String laberintoObtenido3 = "";
        if (cell.moveToFirst()){
            laberintoObtenido = cell.getString(1);
            laberintoObtenido2 = cell.getString(2);
            laberintoObtenido3 = cell.getString(3);
            cell.close();
        }
        else {
            Toast.makeText(this, "The Wumpus isn't around this caves. Try another one!", Toast.LENGTH_LONG).show();
            db.close();
        }
        return laberintoObtenido + "%" + laberintoObtenido2 + "%" + laberintoObtenido3;
    }


    /**
     * Gets the ID of a graph based on it's name.
     *
     * @param graphName The name of the graph.
     * @return The DB ID of the graph
     */
    public int getGraphID(String graphName) {
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 7);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor cell = db.rawQuery("SELECT GRAPH.id FROM GRAPH WHERE GRAPH.name = \"" + graphName +"\";", null);
        if (cell.moveToFirst()){
            int graphID = cell.getInt(0);
            cell.close();
            return graphID;
        }
        else {
            Toast.makeText(this, "The Wumpus isn't around this caves. Try another one!", Toast.LENGTH_LONG).show();
            db.close();
        }
        cell.close();
        return 0;
    }

    /*
    * Fills the ListView with the mazes from the DB
    */
    public void populateListView() {
        mazeList = (ListView)findViewById(R.id.listViewMazes);
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 7);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, number_of_caves FROM GRAPH WHERE custom = 1", null);
        datos = new ArrayList<String>();
        names = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do{
                String dato = "Nombre: " + cursor.getString(0) + "\nNúmero de cuevas: " + cursor.getString(1);
                datos.add(dato);
                names.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.layout_list_view_item ,datos);
        mazeList.setAdapter(adapter);
        cursor.close();
    }

    /**
     * Sets the animation for the onBackPressed function.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * List all devices
     * @param vista View to be shown.
     * @param laberinto lab selected
     * @param nombre lab's name
     */
    public void listDevices(View vista, String laberinto, String nombre){
        Intent i = new Intent(this, BluetoothChat.class);
        i.putExtra("laberinto",laberinto);
        i.putExtra("nombreLaberinto", nombre);
        i.putExtra("funcion","enviar");
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        startActivity(i, options.toBundle());
    }

}




