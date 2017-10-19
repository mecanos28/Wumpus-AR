package com.clavicusoft.wumpus.AR;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.clavicusoft.wumpus.Database.AdminSQLite;
import com.clavicusoft.wumpus.R;

import java.util.ArrayList;

public class AR_Helper extends Activity {
    public static World world;
    //private Location location;

    /**
     * Creates the AR World
     *
     * @param context App´s context.
     * @param number_of_caves Number of caves in the game.
     * @param game_id DB's id of the current game.
     * @return The created world.
     */
    public World createWorld(Context context, int number_of_caves, int game_id){
        if(world != null){
            return world;
        }
        world = new World(context);
        //ubicacion = new Ubicacion(context);
        createCaves(number_of_caves, game_id);
        return world;
    }

    /**
     * Creates the caves inside the world.
     *
     * @param number_of_caves Number of caves to create
     * @param game_id DB's id of the current game.
     */
    public void createCaves(int number_of_caves, int game_id){

        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 6);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT latitude, longitude, cave_number FROM GAME WHERE id = " +
                        String.valueOf(game_id), null);

        int geo_id = 1;
        if (cursor.moveToFirst()) {
            do{
                GeoObject geo_Object = new GeoObject(geo_id);
                geo_Object.setGeoPosition(Double.parseDouble(cursor.getString(0)), Double.parseDouble(cursor.getString(1)));
                geo_Object.setName("Cueva: " + String.valueOf(cursor.getInt(2)));
                geo_Object.setImageResource(R.drawable.cave);
                world.addBeyondarObject(geo_Object);
                ++geo_id;
            }while(cursor.moveToNext());
        }

        cursor.close();
    }
}
