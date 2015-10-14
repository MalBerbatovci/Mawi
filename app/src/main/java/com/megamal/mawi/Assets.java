package com.megamal.mawi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by malberbatovci on 22/09/15.
 */

import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;

import com.megamal.framework.util.Tile;

public class Assets {
    private static SoundPool soundPool;
    public static Bitmap welcome, grassImage, mawiStanding;

    //input stream used to read data from device's file system
    public static void load() {
        welcome = loadBitmap("welcome.png", false);
        grassImage = loadBitmap("grass.png", false);
        mawiStanding = loadBitmap("mawiStanding.png", true);
    }

    private static Bitmap loadBitmap(String filename, boolean transparency) {
        InputStream inputStream = null;
        try {
            inputStream = GameMainActivity.assets.open(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //then create an options object that specifies how that image should be stored in memory
        //no transparency = less memory consumption
        //or transparency = higher memory consumption
        Options options = new Options();
        if (transparency) {
            options.inPreferredConfig = Config.ARGB_8888;
        } else {
            options.inPreferredConfig = Config.RGB_565;
        }

        //finally creates a new Bitmap using BitmapFactory
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null,
                options);
        return bitmap;
    }

    private static int loadSound(String filename) {
        int soundID = 0;
        if (soundPool == null) {
            soundPool = new SoundPool(25, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            soundID = soundPool.load(GameMainActivity.assets.openFd(filename),
                    1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return soundID;
    }

    public static void playSound(int soundID) {
        soundPool.play(soundID, 1, 1, 1, 0, 1);
    }
}

