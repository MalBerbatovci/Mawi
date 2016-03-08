package com.megamal.game.model;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.megamal.framework.util.Painter;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 10/01/16.
 */

//Abstract class to represent an enemy
public abstract class Enemy {

    public final int RECT_LEEWAY_X = 2;
    public final int RECT_LEEWAY_Y = 2;


    //not completely fixed yet
    public boolean isVisible(double cameraOffsetX, double cameraOffsetY, double x, double y, int width, int height) {
        if ((((x + width) - cameraOffsetX > 0) && (((x + width) - cameraOffsetX) <= GameMainActivity.GAME_WIDTH)) ||
                ((x - cameraOffsetX > 0) && (x - cameraOffsetX) <= GameMainActivity.GAME_WIDTH)){

            //Log.d("EnemyVisiblity", "passed X");

            if((((y + height) - cameraOffsetY) > 0 && ((y + height) - cameraOffsetY <= GameMainActivity.GAME_HEIGHT)) ||
                    ((y - cameraOffsetY > 0) && (y - cameraOffsetY <= GameMainActivity.GAME_HEIGHT))) {

                //Log.d("EnemyVisibility", "passed Y");

                //Log.d("RenderingCol", "Rendering!");
                return true;


            } else {
                return false;
            }
        }

        else {
            //Log.d("RenderingEnemy", "Not rendering!");
            return false;
        }

       // Log.d("RenderingEnemy", "rendering by force!");

    }


    protected abstract void updateRects(double cameraOffsetX, double cameraOffsetY);

    public abstract void update(float delta, int[][] map, double cameraOffsetX, double cameraOffsetY, Player mawi);

    protected abstract void updateAnim(float delta);

    public abstract void checkCollisions(Player mawi, double cameraOffsetX, double cameraOffsetY, int[][] map);

    public abstract void render(Painter g, double cameraOffsetX, double cameraOffsetY);

    public abstract void clearAreaAround(Painter g, double cameraOffsetX, double cameraOffsetY);

    public abstract double getX();

    public abstract Rect getRect();

    public abstract boolean isFalling();

    public abstract double getY();

    public abstract void activate();

    public abstract boolean isAlive();

    public abstract boolean safeToRemove();

    public abstract boolean isDying();

    public abstract void death();

    public abstract boolean isActive();

    public abstract boolean isDead();

    public abstract boolean isGrounded();

    public abstract int getVelX();

    public abstract int getVelY();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract Bitmap getImage(int direction);

    public abstract int getMostRecentDirection();

    public abstract void forceDirection(int direction);


}
