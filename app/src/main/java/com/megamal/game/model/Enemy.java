package com.megamal.game.model;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.megamal.framework.util.Painter;

/**
 * Created by malberbatovci on 10/01/16.
 */

//Abstract class to represent an enemy
public abstract class Enemy {

    public final int VISIBLE_THRESHOLD_X = 500;
    public final int VISIBLE_THRESHOLD_Y = 500;
    public final int RECT_LEEWAY_X = 4;
    public final int RECT_LEEWAY_Y = 4;

    //not completely fixed yet
    public boolean isVisible(double cameraOffsetX, double cameraOffsetY, double x, double y) {
        if ((Math.abs(x - cameraOffsetX) > VISIBLE_THRESHOLD_X) ||
                Math.abs(y - cameraOffsetY) > VISIBLE_THRESHOLD_Y) {
            return false;
        }

        else
            return true;
    }


    protected abstract void updateRects(double cameraOffsetX, double cameraOffsetY);

    public abstract void update(float delta, int[][] map, double cameraOffsetX, double cameraOffsetY, Player mawi);

    protected abstract void updateAnim(float delta);

    public abstract void checkCollisions(Player mawi);

    public abstract void render(Painter g, Bitmap image);

    public abstract void activate();

}
