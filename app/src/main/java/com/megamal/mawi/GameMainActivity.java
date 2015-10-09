package com.megamal.mawi;

import android.app.Activity;
import android.content.res.AssetManager;
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
    public static GameView sGame;
    public static AssetManager assets;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        assets = getAssets();
        sGame = new GameView(this, GAME_WIDTH, GAME_HEIGHT);
        Log.d("GameMainActivity", "sGame created!");
        setContentView(sGame);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}

