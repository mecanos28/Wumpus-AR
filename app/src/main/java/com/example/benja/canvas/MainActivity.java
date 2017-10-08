package com.example.benja.canvas;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.benja.canvas.Bluetooth.BluetoothChat;
import com.example.benja.canvas.Bluetooth.SelectLabToShare;


public class MainActivity extends Activity {

    //boolean multiplayerSubMenu = false;

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
        Intent i = new Intent(this,Multiplayer.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_down, R.anim.slide_out_down);
        startActivity(i, options.toBundle());
    }


    /*@Override
    public void onBackPressed() {
        if(multiplayerSubMenu){
            setContentView(R.layout.activity_main);
            multiplayerSubMenu = false;
        }else{
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }*/

}
