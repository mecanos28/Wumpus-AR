package com.example.benja.canvas;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.benja.canvas.Bluetooth.BluetoothChat;
import com.example.benja.canvas.Bluetooth.SelectLabToShare;


public class Multiplayer extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_sr_layout);
    }

    public void send(View vista){
        Intent i = new Intent(this, SelectLabToShare.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        startActivity(i, options.toBundle());
    }

    public void receive(View vista){
        Intent i = new Intent(this, BluetoothChat.class);
        i.putExtra("funcion","recibir");
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        startActivity(i, options.toBundle());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

}
