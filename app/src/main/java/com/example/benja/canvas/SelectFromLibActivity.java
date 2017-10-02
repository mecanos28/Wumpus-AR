package com.example.benja.canvas;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

//https://www.youtube.com/watch?v=HRPpQw0dzko

public class SelectFromLibActivity extends Activity {

    ListView mazeList;
    String[] data;
    int[] toListIds;

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
        Cursor cursor = db.rawQuery("SELECT id as _id, name, number_of_caves FROM GRAPH", null);
        data = new String[]{"_id", "name", "number_of_caves"};
        toListIds = new int[]{R.id.maze_id, R.id.maze_name, R.id.maze_caves};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_items, cursor, data, toListIds, 0);
        mazeList.setAdapter(cursorAdapter);
        cursor.close();
    }

}
