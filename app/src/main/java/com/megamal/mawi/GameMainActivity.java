package com.megamal.mawi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;


/**
 * Created by malberbatovci on 22/09/15.
 */
public class GameMainActivity extends Activity {
    public static final int GAME_WIDTH = 832;
    public static final int GAME_HEIGHT = 512;
    public static final int TILE_HEIGHT = 64;
    public static final int TILE_WIDTH = 64;
    public static final int PLAYER_HEIGHT = 128;
    public static final int PLAYER_WIDTH = 64;
    public static final int BACKGROUND_COLOUR = Color.rgb(80, 143, 240);

    public static final String preferenceString = "levelProgress";


    public static GameView sGame;
    public static AssetManager assets;

    public static GameMainActivity instance;

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferenceEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        instance = this;

        assets = getAssets();
        sGame = new GameView(this, GAME_WIDTH, GAME_HEIGHT);
        Log.d("GameMainActivity", "sGame created!");

        preferences = this.getSharedPreferences(getString(R.string.shared_pref),
                Context.MODE_PRIVATE);
        preferenceEditor = preferences.edit();

        //if no prefeences is created, then create one with the default value of one.
        if(!preferences.contains(getString(R.string.shared_pref))) {
            preferenceEditor.putInt(getString(R.string.shared_pref), 1);
            preferenceEditor.apply();
            Log.d("Preferences", "Preferences Created!");
        }

        else {
            preferenceEditor.remove(getString(R.string.shared_pref));
            preferenceEditor.apply();
            Log.d("Preferences", "Preferences Cleared");
        }



        setContentView(sGame);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {

        super.onResume();
       // setContentView(sGame);


    }

    public static Context getApplicationConext() {
        return instance;
    }
}

