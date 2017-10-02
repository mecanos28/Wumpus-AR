package com.example.benja.canvas;


import android.app.Activity;
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

import java.util.ArrayList;

public class SelectFromLibActivity extends Activity {

    ListView mazeList;
    String[] data;

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
                String value = data[position];
                Toast.makeText(SelectFromLibActivity.this, value, Toast.LENGTH_SHORT).show();
                //TODO: Send the data to the next layout.
            }
        });
    }

    /*
    * Fills the ListView with the mazes from the DB
    */
    public void populateListView() {
        mazeList = (ListView)findViewById(R.id.listViewMazes);
        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 5);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, number_of_caves FROM GRAPH", null);
        ArrayList<String> datos = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do{
                String dato = "Name: " + cursor.getString(0) + "\nNumber of caves: " + cursor.getString(1);
                datos.add(dato);
            }while(cursor.moveToNext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datos);
        mazeList.setAdapter(adapter);
        cursor.close();
    }

}
