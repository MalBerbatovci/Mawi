package com.megamal.game.model;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.megamal.framework.util.Painter;
import com.megamal.framework.util.Tile;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 10/01/16.
 */

//Class to represents a Mover, which is a type of enemy that just moves
public abstract class Mover extends Enemy {

    //Method to be called once updated the x and y with the suitable velocity; checks to see whether
    //enemy has moved off-screen/onto the floor in the Y direction and updates variables accordingly
    protected abstract void checkYMovement(int[][] map);


    //Method to be called once updated the x and y with the suitable velocity; checks to see whether
    //enemy has moved off-screen/into an obstacle in the X direction and updates variables accordingly
    protected abstract void checkXMovement(int[][] map);

    public abstract void clearAreaAround(Painter g, double cameraOffsetX, double cameraOffsetY);



}
