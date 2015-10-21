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

import com.megamal.framework.animation.Animation;
import com.megamal.framework.animation.Frame;

public class Assets {
    private static SoundPool soundPool;
    public static Bitmap welcome, grassImage, mawiStandingFront, mawiWalk1, mawiWalk2, mawiWalk3, mawiStandingSide,
                         mawiRun1, mawiRun2, mawiRun3, walkRightButton, walkRightButtonPressed;

    public static Animation walkAnim, runAnim;

    //input stream used to read data from device's file system
    public static void load() {
        welcome = loadBitmap("welcome.png", false);
        grassImage = loadBitmap("grass.png", false);
        mawiStandingFront = loadBitmap("mawiStandingFront.png", true);
        mawiStandingSide = loadBitmap("mawiStanding.png", true);
        mawiWalk1 = loadBitmap("mawiWalkingAnim1.png", true);
        mawiWalk2 = loadBitmap("mawiWalkingAnim2.png", true);
        mawiWalk3 = loadBitmap("mawiWalkingAnim3.png", true);
        mawiRun1 = loadBitmap("mawiRunningRightAnim1.png", true);
        mawiRun2 = loadBitmap("mawiRunningRightAnim2.png", true);
        mawiRun2 = loadBitmap("mawiRunningRightAnim3.png", true);
        walkRightButton = loadBitmap("walkButtonR.png", false);
        walkRightButtonPressed = loadBitmap("walkButtonRPressed.png", false);


        //create walking animation
        Frame w1 = new Frame(mawiWalk1, 0.2f);
        Frame w2 = new Frame(mawiWalk2, 0.2f);
        Frame w3 = new Frame(mawiWalk3, 0.2f);
        walkAnim = new Animation(w1, w2, w3, w2);

        //create running animation
        Frame r1 = new Frame(mawiRun1, 0.2f);
        Frame r2 = new Frame(mawiRun2, 0.2f);
        Frame r3 = new Frame(mawiRun3, 0.2f);
        runAnim = new Animation(r1, r2, r3, r2);
        
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

