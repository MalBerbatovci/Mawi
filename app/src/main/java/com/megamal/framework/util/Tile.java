package com.megamal.framework.util;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 07/10/15.
 */
public class Tile {

    private int ID;
    private double x, y;
    private boolean isObstacle;
    private Bitmap image;
    private Rect rect;

    public Tile(int ID) {
        this.ID = ID;
        rect = new Rect();
        setVariables();
    }

    private void setVariables() {
        switch(ID) {
            case(0):this.image = null;
                    this.isObstacle = false;
                    break;
            case(1):this.image = Assets.grassImage;
                    this.isObstacle = true;
                    break;
            case(2):this.image = Assets.earthImage;
                    this.isObstacle = true;
                    break;
        }

    }

    //takes the index of the array in order to work out co-ordinates for the tile
    //and also updates the rect for each tile
    public void setLocation(double y, double x, double cameraOffsetX, double cameraOffsetY) {
            this.y = y * GameMainActivity.TILE_HEIGHT;
            this.x = (x * GameMainActivity.TILE_WIDTH) - cameraOffsetX;
    }

    public void setRect(double y, double x) {
        rect.set((int) this.x, (int) this.y,(int) this.x + GameMainActivity.TILE_WIDTH,
                (int) this.y + GameMainActivity.TILE_HEIGHT);

    }

    public void setID(int ID) {
        this.ID = ID;
        setVariables();
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
