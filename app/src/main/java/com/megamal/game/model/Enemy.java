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


    public void updateRects(double x, double y, double cameraOffsetX, double cameraOffsetY,
                            double rectX, double rectY, int width, int height, Rect rect) {
        {
            //if (visible(x, y);
            //set to be in relation to the screen - IF VISIBLE

            if (isVisible(cameraOffsetX, cameraOffsetY, x, y)) {
                rectX = (x + RECT_LEEWAY_X - cameraOffsetX);
                rectY = (y + RECT_LEEWAY_Y - cameraOffsetY);

                rect.set((int) rectX, (int) rectY, (int) rectX + (width + RECT_LEEWAY_X), (int) rectY + (height + RECT_LEEWAY_Y));
            }

            else
                return;

        }

    }

    //not completely fixed yet
    public boolean isVisible(double cameraOffsetX, double cameraOffsetY, double x, double y) {
        if ((Math.abs(x - cameraOffsetX) > VISIBLE_THRESHOLD_X) ||
                Math.abs(y - cameraOffsetY) > VISIBLE_THRESHOLD_Y) {
            return false;
        }

        else
            return true;
    }

    public abstract void update(float delta, int[][] map, double cameraOffsetX, double cameraOffsetY);

    public abstract void updateAnim(float delta);

    public abstract void checkCollision(Rect playerRect);

    public abstract void render(Painter g, Bitmap image);

}
