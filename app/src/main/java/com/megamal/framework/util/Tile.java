package com.megamal.framework.util;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 07/10/15.
 */
public class Tile {
    public static final int TILE_HEIGHT = 32;
    public static final int TILE_WIDTH = 32;


    private int ID;
    private float x, y;
    private boolean isObstacle;
    private Bitmap image;
    private Rect rect;

    public Tile(int ID, boolean isObstacle) {
        this.ID = ID;
        this.isObstacle = isObstacle;
        rect = new Rect();

        switch(ID) {
            case(1):image = Assets.grassImage;
                    break;
        }
    }

    public void setLocation(float y, float x) {
        this.y = y * TILE_HEIGHT;
        this.x = x * TILE_WIDTH;
        rect.set((int)this.x, (int)this.y, (int) this.x + TILE_WIDTH, (int) this.y + TILE_HEIGHT);
    }

    public int getID() {
        return ID;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean isObstacle() {
        return isObstacle;
    }

    public Rect getRect() {
        return rect;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}
