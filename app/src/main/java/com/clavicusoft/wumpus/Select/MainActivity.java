package com.clavicusoft.wumpus.Select;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.clavicusoft.wumpus.R;


public class MainActivity extends Activity {

    /**
     * Sets the view once this activity starts.
     *
     * @param savedInstanceState Activity's previous saved state.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /**
     * Starts the single player activity, and sets the animation for the transition.
     *
     * @param view Current view.
    */
    public void singlePlayer (View view)
    {
        Intent i = new Intent(this,SelectPolyActivity.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_down,
                R.anim.slide_out_down);
        startActivity(i, options.toBundle());
    }

    /**
     * Starts the single player activity, and sets the animation for the transition.
     *
     * @param view Current view.
     */
    public void multiPlayer (View view)
    {
        Intent i = new Intent(this,Multiplayer.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_down,
                R.anim.slide_out_down);
        startActivity(i, options.toBundle());
    }

}
