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
    public static Bitmap grassImage, earthImage, coinImage, coinBigImage, mawiStandingFront, mawiWalkR1, mawiWalkR2, mawiWalkR3, mawiStandingSide,
            mawiRunR1, mawiRunR2, mawiRunR3, walkButtonR, walkButtonPressedR, runButtonR, runButtonPressedR, walkButtonL,
            walkButtonPressedL, runButtonL, runButtonPressedL, mawiWalkL1, mawiWalkL2, mawiWalkL3, mawiRunL1, mawiRunL2,
            mawiRunL3, mawiWalkHitR1, mawiWalkHitR2, mawiWalkHitR3, mawiWalkHitL1, mawiWalkHitL2, mawiWalkHitL3,
            mawiJumpingR, mawiJumpingL, boxImage, boxUsedImage, startScreen, exitButton, exitButtonPressed,
            movingTool, movingToolUsed, wrenchTool, wrenchToolInUse, eraserTool, eraserToolInUse,
            pencilTool, pencilToolInUse, leftID, leftIDUsed, rightID, rightIDUsed, saveButton,
            playButton, backToLEButton, projectileImage;

    public static Bitmap walkButtonU, walkButtonUPressed, walkButtonD, walkButtonDPressed;

    public static Bitmap hedgeWalkR1, hedgeWalkR2, hedgeWalkR3, hedgeWalkR4, hedgeWalkR5, hedgeWalkR6,
            hedgeWalkL1, hedgeWalkL2, hedgeWalkL3, hedgeWalkL4, hedgeWalkL5, hedgeWalkL6, hedgeStandard;

    public static Bitmap enemyButton, enemyButtonUsed, jumpButton, jumpButtonPressed, projectileButton,
        projectileButtonPressed;


    public static Bitmap level1Tile, level2Tile, pathTile, pathTileB, pathTileC, treeTile, treeTileB,
        treeTileC, portalBottomR, portalTopR, portalBottomL, portalTopL, level3Tile, level4Tile;

    public static Animation walkAnimR, walkHitAnimR, walkAnimL, walkHitAnimL, runAnimR, runAnimL,
                hedgeAnimR, hedgeAnimL;

    //input stream used to read data from device's file system
    public static void load() {
        grassImage = loadBitmap("grass.png", false);
        earthImage = loadBitmap("earth.png", false);
        coinImage = loadBitmap("coin.png", false);
        projectileImage = loadBitmap("projectile.png", true);
        coinBigImage = loadBitmap("coinBig.png", false);
        boxImage = loadBitmap("box.png", false);
        boxUsedImage = loadBitmap("boxUsed.png", false);
        mawiStandingFront = loadBitmap("mawiStandingFront.png", true);
        mawiStandingSide = loadBitmap("mawiStanding.png", true);
        mawiJumpingR = loadBitmap("mawiJumpingR.png", true);
        mawiJumpingL = loadBitmap("mawiJumpingL.png", true);

        mawiWalkR1 = loadBitmap("mawiWalkingAnimR1.png", true);
        mawiWalkR2 = loadBitmap("mawiWalkingAnimR2.png", true);
        mawiWalkR3 = loadBitmap("mawiWalkingAnimR3.png", true);
        mawiRunR1 = loadBitmap("mawiRunningAnimR1.png", true);
        mawiRunR2 = loadBitmap("mawiRunningAnimR2.png", true);
        mawiRunR3 = loadBitmap("mawiRunningAnimR3.png", true);


        mawiWalkL1 = loadBitmap("mawiWalkingAnimL1.png", true);
        mawiWalkL2 = loadBitmap("mawiWalkingAnimL2.png", true);
        mawiWalkL3 = loadBitmap("mawiWalkingAnimL3.png", true);
        mawiRunL1 = loadBitmap("mawiRunningAnimL1.png", true);
        mawiRunL2 = loadBitmap("mawiRunningAnimL2.png", true);
        mawiRunL3 = loadBitmap("mawiRunningAnimL3.png", true);

        mawiWalkHitL1 = loadBitmap("mawiWalkHitAnimL1.png", true);
        mawiWalkHitL2 = loadBitmap("mawiWalkHitAnimL2.png", true);
        mawiWalkHitL3 = loadBitmap("mawiWalkHitAnimL3.png", true);
        mawiWalkHitR1 = loadBitmap("mawiWalkHitAnimR1.png", true);
        mawiWalkHitR2 = loadBitmap("mawiWalkHitAnimR2.png", true);
        mawiWalkHitR3 = loadBitmap("mawiWalkHitAnimR3.png", true);

        walkButtonR = loadBitmap("walkButtonR.png", false);
        walkButtonL = loadBitmap("walkButtonL.png", false);
        walkButtonPressedR = loadBitmap("walkButtonRPressed.png", false);
        walkButtonPressedL = loadBitmap("walkButtonLPressed.png", false);
        walkButtonU = loadBitmap("walkButtonU.png", false);
        walkButtonUPressed = loadBitmap("walkButtonUPressed.png", false);
        walkButtonD = loadBitmap("walkButtonD.png", false);
        walkButtonDPressed = loadBitmap("walkButtonDPressed.png", false);


        runButtonR = loadBitmap("runButtonR.png", false);
        runButtonL = loadBitmap("runButtonL.png", false);
        runButtonPressedR = loadBitmap("runButtonRPressed.png", false);
        runButtonPressedL = loadBitmap("runButtonLPressed.png", false);

        jumpButton = loadBitmap("jumpButton.png", false);
        jumpButtonPressed = loadBitmap("jumpButtonPressed.png", false);

        projectileButton = loadBitmap("projectileButton.png", false);
        projectileButtonPressed = loadBitmap("projectileButtonPressed.png", false);

        startScreen = loadBitmap("startScreen.png", true);
        exitButton = loadBitmap("ExitButton.png", true);
        exitButtonPressed = loadBitmap("ExitButtonPushed.png", true);

        movingTool = loadBitmap("movingTool.png", true);
        movingToolUsed = loadBitmap("movingToolPressed.png", true);

        wrenchTool = loadBitmap("wrench.png", true);
        wrenchToolInUse = loadBitmap("wrenchInUse.png", true);

        pencilTool = loadBitmap("pencil.png", true);
        pencilToolInUse = loadBitmap("pencilUsed.png", true);

        eraserTool = loadBitmap("eraser.png", true);
        eraserToolInUse = loadBitmap("erasedUsed.png", true);

        enemyButton = loadBitmap("enemyButton.png", true);
        enemyButtonUsed = loadBitmap("enemyButtonUsed.png", true);

        leftID = loadBitmap("leftIDButton.png", true);
        leftIDUsed = loadBitmap("leftIDButtonUsed.png", true);

        rightID = loadBitmap("rightIDButton.png", true);
        rightIDUsed = loadBitmap("rightIDUsed.png", true);

        saveButton = loadBitmap("saveButton.png", true);

        playButton = loadBitmap("playButton.png", true);

        backToLEButton = loadBitmap("goBack.png", true);


        pathTile = loadBitmap("path.png", true);
        pathTileB = loadBitmap("path2.png", true);
        pathTileC = loadBitmap("path3.png", true);

        treeTile = loadBitmap("treeTile.png", true);
        treeTileB = loadBitmap("treeTileB.png", true);
        treeTileC = loadBitmap("treeTileC.png", true);

        level1Tile = loadBitmap("level1.png", true);
        level2Tile = loadBitmap("level2.png", true);
        level3Tile = loadBitmap("level3.png", true);
        level4Tile = loadBitmap("level4.png", true);


        portalBottomR = loadBitmap("portalBottomR.png", true);
        portalTopR = loadBitmap("portalTopR.png", true);
        portalBottomL = loadBitmap("portalBottomL.png", true);
        portalTopL = loadBitmap("portalTopL.png", true);


        hedgeStandard = loadBitmap("enemyRF2.png", false);

        hedgeWalkR1 = loadBitmap("enemyRF1.png", false);
        hedgeWalkR2 = loadBitmap("enemyRF2.png", false);
        hedgeWalkR3 = loadBitmap("enemyRF3.png", false);
        hedgeWalkR4 = loadBitmap("enemyRF4.png", false);
        hedgeWalkR5 = loadBitmap("enemyRF5.png", false);
        hedgeWalkR6 = loadBitmap("enemyRF6.png", false);

        hedgeWalkL1 = loadBitmap("enemyLF1.png", false);
        hedgeWalkL2 = loadBitmap("enemyLF2.png", false);
        hedgeWalkL3 = loadBitmap("enemyLF3.png", false);
        hedgeWalkL4 = loadBitmap("enemyLF4.png", false);
        hedgeWalkL5 = loadBitmap("enemyLF5.png", false);
        hedgeWalkL6 = loadBitmap("enemyLF6.png", false);

        Frame eR1 = new Frame(hedgeWalkR1, 0.2f);
        Frame eR2 = new Frame(hedgeWalkR2, 0.2f);
        Frame eR3 = new Frame(hedgeWalkR3, 0.2f);
        Frame eR4 = new Frame(hedgeWalkR4, 0.2f);
        Frame eR5 = new Frame(hedgeWalkR5, 0.2f);
        Frame eR6 = new Frame(hedgeWalkR6, 0.2f);
        hedgeAnimR = new Animation(eR1, eR2, eR3, eR4, eR5, eR6);

        Frame eL1 = new Frame(hedgeWalkL1, 0.2f);
        Frame eL2 = new Frame(hedgeWalkL2, 0.2f);
        Frame eL3 = new Frame(hedgeWalkL3, 0.2f);
        Frame eL4 = new Frame(hedgeWalkL4, 0.2f);
        Frame eL5 = new Frame(hedgeWalkL5, 0.2f);
        Frame eL6 = new Frame(hedgeWalkL6, 0.2f);
        hedgeAnimL = new Animation(eL1, eL2, eL3, eL4, eL5, eL6);


        //create walking animation
        Frame wR1 = new Frame(mawiWalkR1, 0.2f);
        Frame wR2 = new Frame(mawiWalkR2, 0.2f);
        Frame wR3 = new Frame(mawiWalkR3, 0.2f);
        walkAnimR = new Animation(wR1, wR2, wR3, wR2);

        Frame wHR1 = new Frame(mawiWalkHitR1, 0.2f);
        Frame wHR2 = new Frame(mawiWalkHitR2, 0.2f);
        Frame wHR3 = new Frame(mawiWalkHitR3, 0.2f);
        walkHitAnimR = new Animation(wHR1, wHR2, wHR3);

        Frame wL1 = new Frame(mawiWalkL1, 0.2f);
        Frame wL2 = new Frame(mawiWalkL2, 0.2f);
        Frame wL3 = new Frame(mawiWalkL3, 0.2f);
        walkAnimL = new Animation(wL1, wL2, wL3, wL2);

        Frame wHL1 = new Frame(mawiWalkHitL1, 0.2f);
        Frame wHL2 = new Frame(mawiWalkHitL2, 0.2f);
        Frame wHL3 = new Frame(mawiWalkHitL3, 0.2f);
        walkHitAnimL = new Animation(wHL1, wHL2, wHL3);

        //create running animation
        Frame rR1 = new Frame(mawiRunR1, 0.2f);
        Frame rR2 = new Frame(mawiRunR2, 0.2f);
        Frame rR3 = new Frame(mawiRunR3, 0.2f);
        runAnimR = new Animation(rR1, rR2, rR3, rR2);

        Frame rL1 = new Frame(mawiRunL1, 0.2f);
        Frame rL2 = new Frame(mawiWalkL2, 0.2f);
        Frame rL3 = new Frame(mawiWalkL3, 0.2f);
        runAnimL = new Animation(rL1, rL2, rL3, rL2);


        
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

