package com.megamal.framework.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 07/10/15.
 */
public class Tile {

    private static final int COIN_RECT_CONST = 5;
    private static final int BOX_JUST_HIT = 10;

    private int ID;
    private double x, y;
    private boolean isObstacle, isCollectable, hasCollectable, boxJustHit, isEndOfLevel;
    private int collectableID;
    private Bitmap image;
    private Rect rect;

    public Tile(int ID) {
        this.ID = ID;
        rect = new Rect();
        this.boxJustHit = false;
        setVariables();
    }

    private void setVariables() {
        this.collectableID = 0;
        this.isEndOfLevel = false;

        switch(ID) {
            case(0): {
                this.image = null;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }

            case(1): {
                this.image = Assets.grassImage;
                this.isObstacle = true;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }
            
            case(2): {
                this.image = Assets.earthImage;
                this.isObstacle = true;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }

            case(3): {
                this.image = Assets.coinImage;
                this.isObstacle = false;
                this.isCollectable = true;
                this.hasCollectable = false;
                break;
            }

            case(4): {
                this.image = Assets.boxImage;
                this.isObstacle = true;
                this.isCollectable = false;
                this.hasCollectable = true;
                this.collectableID = 1;
                break;
            }

            case(5): {
                this.image = Assets.boxImage;
                this.isObstacle = true;
                this.isCollectable = false;
                this.hasCollectable = true;
                this.collectableID = 2;
                break;
            }

            case(6): {
                this.image = Assets.boxImage;
                this.isObstacle = true;
                this.isCollectable = false;
                this.hasCollectable = true;
                this.collectableID = 3;
                break;
            }


            case(7): {
                this.image = Assets.boxUsedImage;
                this.isObstacle = true;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }

            case(8): {
                this.image = Assets.portalTopL;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                this.isEndOfLevel = true;
                break;
            }

            case(9): {
                this.image = Assets.portalBottomL;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                this.isEndOfLevel = true;
                break;
            }

            case(10): {
                this.image = Assets.portalTopR;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                this.isEndOfLevel = true;
                break;
            }

            case(11): {
                this.image = Assets.portalBottomR;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                this.isEndOfLevel = true;
                break;
            }

            case(15): {
                this.image = Assets.treeTileB;
                this.isObstacle = true;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }

            case(16): {
                this.image = Assets.treeTileC;
                this.isObstacle = true;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }

            case(17): {
                this.image = Assets.pathTileC;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }

            case(18): {
                this.image = Assets.pathTileB;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }

            case(19): {
                this.image = Assets.treeTile;
                this.isObstacle = true;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }

            case(20): {
                this.image = Assets.pathTile;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }

            case (21): {
                this.image = Assets.level1Tile;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }
            case (22): {
                this.image = Assets.level2Tile;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }

            case (23): {
                this.image = Assets.level3Tile;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;
            }

            default:
                this.image = null;
                this.isObstacle = false;
                this.isCollectable = false;
                this.hasCollectable = false;
                break;

        }

    }

    //takes the index of the array in order to work out co-ordinates for the tile
    //in order to render onScreen & use tile info onScreen
    public void setLocation(int yIndex, int xIndex, double cameraOffsetX, double cameraOffsetY) {

        if (this.boxJustHit) {
            this.y = (yIndex * GameMainActivity.TILE_HEIGHT - BOX_JUST_HIT) - cameraOffsetY;
            this.x = (xIndex * GameMainActivity.TILE_WIDTH) - cameraOffsetX;
            boxJustHit = false;
        }
        else {
            this.y = (yIndex * GameMainActivity.TILE_HEIGHT) - cameraOffsetY;
            this.x = (xIndex * GameMainActivity.TILE_WIDTH) - cameraOffsetX;
        }
    }

    public int xLocationNoOffset(int xIndex) {
        return (xIndex * GameMainActivity.TILE_WIDTH);
    }

    public int yLocationNoOffset(int yIndex) {
        return (yIndex * GameMainActivity.TILE_WIDTH);
    }

    public void setRect(double yIndex, double xIndex) {
        rect.set((int) xIndex, (int) yIndex,(int) this.x + GameMainActivity.TILE_WIDTH,
                (int) this.y + GameMainActivity.TILE_HEIGHT);

    }

    public void setRectCoin(double yIndex, double xIndex, double cameraOffsetX, double cameraOffsetY) {
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

    public int getCollectableID() { return collectableID; }

    public Bitmap getImage() {
        return image;
    }

    public void boxJustHit(boolean justHit) {
        this.boxJustHit = justHit;
    }

    public boolean isObstacle() {
        return isObstacle;
    }

    public boolean isCollectable() { return isCollectable; }

    public boolean isEndOfLevel() { return isEndOfLevel; }

    public boolean hasCollectable() { return hasCollectable; }

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
