package com.clavicusoft.wumpus.Select;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.clavicusoft.wumpus.Bluetooth.BluetoothChat;
import com.clavicusoft.wumpus.Bluetooth.SelectLabToShare;
import com.clavicusoft.wumpus.R;


public class Multiplayer extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_sr_layout);
    }

    /**
     * Starts the send activity, and sets the animation for the transition.
     *
     * @param view current view.
     */
    public void send(View view){
        Intent i = new Intent(this, SelectLabToShare.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in,
                R.anim.fade_out);
        startActivity(i, options.toBundle());
    }

    /**
     * Starts the receive activity, sets the parameter for it, and sets the animation for
     * the transition.
     *
     * @param view current view.
     */
    public void receive(View view){
        Intent i = new Intent(this, BluetoothChat.class);
        i.putExtra("funcion","recibir");
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in,
                R.anim.fade_out);
        startActivity(i, options.toBundle());
    }

    /**
     * Sets the animation for the onBackPressed function.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

}
