package com.megamal.framework.util;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 07/10/15.
 */
public class Tile {

    private static final int COIN_RECT_CONST = 5;

    private int ID;
    private double x, y;
    private boolean isObstacle, isCollectable;
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
                    this.isCollectable = false;
                    break;
            case(1):this.image = Assets.grassImage;
                    this.isObstacle = true;
                    this.isCollectable = false;
                    break;
            case(2):this.image = Assets.earthImage;
                    this.isObstacle = true;
                    this.isCollectable = false;
                    break;
            case(3):this.image = Assets.coinImage;
                    this.isObstacle = false;
                    this.isCollectable = true;
                    break;
        }

    }

    //takes the index of the array in order to work out co-ordinates for the tile
    //and also updates the rect for each tile
    public void setLocation(double yIndex, double xIndex, double cameraOffsetX, double cameraOffsetY) {
            this.y = (yIndex * GameMainActivity.TILE_HEIGHT) - cameraOffsetY;
            this.x = (xIndex * GameMainActivity.TILE_WIDTH) - cameraOffsetX;
    }

    public void setRect(double yIndex, double xIndex) {
        rect.set((int) xIndex, (int) yIndex,(int) this.x + GameMainActivity.TILE_WIDTH,
                (int) this.y + GameMainActivity.TILE_HEIGHT);

    }

    public void setRectCoin(double yIndex, double xIndex, double cameraOffsetX, double cameraOffsetY) {
        /*rect.set((int) (((xIndex * GameMainActivity.TILE_WIDTH)) - cameraOffsetX),
                 (int) (((yIndex * GameMainActivity.TILE_HEIGHT)) - cameraOffsetY),
                 (int) (((xIndex * GameMainActivity.TILE_WIDTH) + GameMainActivity.TILE_WIDTH)  - cameraOffsetX),
                 (int) (((yIndex * GameMainActivity.TILE_HEIGHT) + GameMainActivity.TILE_HEIGHT) - cameraOffsetY)); */

        rect.set((int) (((xIndex * GameMainActivity.TILE_WIDTH)) - cameraOffsetX),
                (int) (((yIndex * GameMainActivity.TILE_HEIGHT) + COIN_RECT_CONST) - cameraOffsetY),
                (int) (((xIndex * GameMainActivity.TILE_WIDTH) + GameMainActivity.TILE_WIDTH)  - cameraOffsetX),
                (int) (((yIndex * GameMainActivity.TILE_HEIGHT) + GameMainActivity.TILE_HEIGHT - COIN_RECT_CONST) - cameraOffsetY));


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

    public boolean isCollectable() { return isCollectable; }

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
