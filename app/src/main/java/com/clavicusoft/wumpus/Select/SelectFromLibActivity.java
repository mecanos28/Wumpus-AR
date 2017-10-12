package com.clavicusoft.wumpus.Select;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.clavicusoft.wumpus.Database.AdminSQLite;
import com.clavicusoft.wumpus.Map.Coordinates;
import com.clavicusoft.wumpus.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SelectFromLibActivity extends Activity {

    ListView mazeList;
    ArrayList<String> datos;
    ArrayList<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_from_lib);

        populateListView();

        mazeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /*
            * Gets the maze once one is clicked.
            */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] value = names.toArray(new String [names.size()]);
                String name = value[position];
                String graphID = getGraphID(name);
                startGame(graphID);
            }
        });
    }

    public String getGraphID(String graphName) {
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 6);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor cell = db.rawQuery("SELECT GRAPH.id FROM GRAPH WHERE GRAPH.name = \"" + graphName +"\";", null);
        if (cell.moveToFirst()){
            String graphID = cell.getString(0);
            cell.close();
            return graphID;
        }
        else {
            Toast.makeText(this, "The Wumpus isn't around this caves. Try another one!", Toast.LENGTH_LONG).show();
            db.close();
        }
        cell.close();
        return "";
    }

    /*
    * Fills the ListView with the mazes from the DB
    */
    public void populateListView() {
        mazeList = (ListView)findViewById(R.id.listViewMazes);
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 6);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void startGame (String stringGraphID) {
        Intent i = new Intent(this, Coordinates.class);
        i.putExtra("graphID",stringGraphID);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        startActivity(i, options.toBundle());
    }
}