package com.megamal.game.model;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.megamal.framework.util.Painter;
import com.megamal.framework.util.Tile;
import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 23/11/15.
 */
public class Collectable {

    int ID;
    double x, y;
    double rectX, rectY;
    double velX, velY;
    int scanLineDownXa, scanLineDownXb, scanLineDownY;
    int scanLineAcrossX, scanLineAcrossYa, scanLineAcrossYb;

    Tile tileA, tileB;
    int tileY, tileX;


    boolean isPowerUp;
    boolean isGrounded = false;
    Rect rect;
    Bitmap image;

    private final static int JUMPING_ACCELERATION = -300;
    private final static int ACCEL_GRAVITY = 1222;
    private final static int MOVING_VEL = 150;

    //variables to set object's co-ordinates from tile co-ordinate of objs where the object was created
    private final static int COIN_X = 0;
    private final static int COIN_Y = 0;
    private final static int COIN_WIDTH = 0;
    private final static int COIN_HEIGHT = 0;

    private final static int RECT_LEEWAY_X = 3;
    private final static int RECT_LEEWAY_Y = 3;


    //check ID before, if -1 then do not call this
    public Collectable(int ID, double x, double y, double cameraOffsetX, double cameraOffsetY) {
        this.ID = ID;
        this.velY = JUMPING_ACCELERATION;
        this.velX = MOVING_VEL;
        this.rect = new Rect();

        //true x & y of the object w/out relation to screen viewing, transform to cameraOffset when rendering
        this.x = x + cameraOffsetX;
        this.y = y + cameraOffsetY;

        tileA = new Tile(0);
        tileB = new Tile(0);

        setVariables(ID, cameraOffsetX, cameraOffsetY);
    }

    private void setVariables(int ID, double cameraOffsetX, double cameraOffsetY) {
        switch(ID) {
            case (1): this.image = Assets.coinImage;
                      this.x = x + COIN_X;
                      this.y = y + COIN_Y;
                      this.isPowerUp = false;
                      updateRect(x, y, cameraOffsetX, cameraOffsetY);
                      break;
        }

    }

    private void updateRect(double x, double y, double cameraOffsetX, double cameraOffsetY) {

        //if (visible(x, y);
        //set to be in relation to the screen - IF VISIBLE
        rectX = (x + RECT_LEEWAY_X - cameraOffsetX);
        rectY = (y + RECT_LEEWAY_Y - cameraOffsetY);

        rect.set((int) rectX, (int) rectY, (int) rectX + COIN_WIDTH, (int) rectY + COIN_HEIGHT);

    }

    public void update(float delta, int[][] map, double cameraOffsetX, double cameraOffsetY) {

        x += velX * delta;

        if (!isGrounded) {
            velY += ACCEL_GRAVITY * delta;
            y += velY * delta;
        } else {
            velY = 0;
        }

        //must be done when invisible too,
        checkXMovement(map);
        checkYMovement(map);

        //if (visible(x, y, cameraOffsetX, cameraOffsetY);
        updateRect(x, y, cameraOffsetX, cameraOffsetY);

    }

    private void checkYMovement(int[][] map) {

        //this means that the object is falling, therefore check scanline for underneath,
        //if collision then set grounded to true, and set Y to be just above the suitable tile
        if (velY > 0) {

            scanLineDownY = (int) Math.floor((y + COIN_HEIGHT) / GameMainActivity.TILE_HEIGHT);

            if (scanLineDownY < 0 || scanLineDownY > map.length)
                return;

            scanLineDownXa = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
            scanLineDownXb = (int) Math.floor((x + COIN_WIDTH) / GameMainActivity.TILE_WIDTH);

            if (scanLineDownXa < 0 || scanLineDownXa > map[0].length)
                return;
            if (scanLineDownXb < 0 || scanLineDownXb > map[0].length)
                return;

            tileA.setID(map[scanLineDownY][scanLineDownXa]);
            tileB.setID(map[scanLineDownY][scanLineDownXb]);

            //if obstacle then deal with appropriately
            if(tileA.isObstacle() || tileB.isObstacle()) {
                isGrounded = true;
                velY = 0;

                //same scanLineDownY used so doesnt matter if tileA/tileB
                tileY = tileA.yLocationNoOffset(scanLineDownY);

                //set Y to be just above tile
                y = tileY - COIN_HEIGHT;
            }
            return;


        //this means that it is 'jumping', check scanLine above, if collision then decrease velY and set Y to suitable
        } else if (velY < 0) {
            scanLineDownY = (int) Math.ceil(y / GameMainActivity.TILE_HEIGHT);

            if (scanLineDownY < 0 || scanLineDownY > map.length)
                return;

            scanLineDownXa = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
            scanLineDownXb = (int) Math.floor((x + COIN_WIDTH) / GameMainActivity.TILE_WIDTH);

            if (scanLineDownXa < 0 || scanLineDownXa > map[0].length)
                return;
            if (scanLineDownXb < 0 || scanLineDownXb > map[0].length)
                return;

            tileA.setID(map[scanLineDownY][scanLineDownXa]);
            tileB.setID(map[scanLineDownY][scanLineDownXb]);

            //decrease velocity and set y to be just below tile
            if (tileA.isObstacle() || tileB.isObstacle()) {
                velY = Math.abs(velY) / 5;
                tileY = tileA.yLocationNoOffset(scanLineDownY);
                y = tileY + GameMainActivity.TILE_HEIGHT;
            }
            return;

        //else, this is the case where the object is moving on the ground, check beneath to see if
        //still grounded
        } else {

            scanLineDownY = (int) Math.ceil((y + (COIN_HEIGHT / 2) / GameMainActivity.TILE_HEIGHT));

            if (velX > 0) {
                scanLineDownXa = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
                if (scanLineDownXa < 0 || scanLineDownXa > map[0].length)
                    return;
            }
            else {
                scanLineDownXa = (int) Math.floor((x + COIN_WIDTH) / GameMainActivity.TILE_WIDTH);
                if (scanLineDownXa < 0 || scanLineDownXa > map[0].length)
                    return;
            }


            tileA.setID(map[scanLineDownY][scanLineDownXa]);
            if (!(tileA.isObstacle())) {
                isGrounded = false;
            }

        }
        return;

    }

    //method to check X movement against tiles and perform necessary actions
    private void checkXMovement(int[][] map) {

        if (velX > 0) {
            scanLineAcrossX = (int) Math.floor((x + COIN_WIDTH) / GameMainActivity.TILE_WIDTH);
            if (scanLineAcrossX < 0 || scanLineAcrossX > map[0].length)
                return;
        }

        else {
            scanLineAcrossX = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
            if (scanLineAcrossX < 0 || scanLineAcrossX > map[0].length)
                return;
        }


        if (velY != 0) {
            scanLineAcrossYa = (int) Math.ceil((y + COIN_HEIGHT) / GameMainActivity.TILE_HEIGHT);
            scanLineAcrossYb = (int) Math.ceil(y / GameMainActivity.TILE_HEIGHT);

            if (scanLineAcrossYa < 0 || scanLineAcrossYa > map.length)
                return;
            if (scanLineAcrossYb < 0 || scanLineAcrossYb > map.length)
                return;

            tileA.setID(map[scanLineAcrossX][scanLineAcrossYa]);
            tileB.setID(map[scanLineAcrossX][scanLineAcrossYb]);

            if (tileA.isObstacle() || tileB.isObstacle()) {
                tileA.xLocationNoOffset(scanLineAcrossX);

                if (velX > 0) {
                    x = tileA.getX() - COIN_WIDTH;
                } else {
                    x = tileA.getX() - GameMainActivity.TILE_HEIGHT;
                }

                velX = -(velX);
            }
            return;

        //else stationary on Y, so only check one y scanLine
        } else {

            scanLineAcrossYa = (int) Math.ceil(y / GameMainActivity.TILE_HEIGHT);
            if (scanLineAcrossYa < 0 || scanLineAcrossYa > map.length)
                return;

            tileA.setID(map[scanLineAcrossX][scanLineAcrossYa]);

            if (tileA.isObstacle()) {
                tileA.xLocationNoOffset(scanLineAcrossX);
                if (velX > 0) {
                    x = tileA.getX() - COIN_WIDTH;
                } else {
                    x = tileA.getX() - GameMainActivity.TILE_HEIGHT;
                }

                velX = -(velX);
            }
            return;
        }
    }

    public boolean isVisible(double cameraOffsetX, double cameraOffsetY) {

        //check if x with cameraOffset is within limits, if so player is visible, else collectable is not
        if (((x - cameraOffsetX > 0) && (x - cameraOffsetX < GameMainActivity.GAME_WIDTH)) ||
                ((y - cameraOffsetY > 0) && (y - cameraOffsetY < GameMainActivity.GAME_WIDTH))) {

            return true;
        }
        else
            return false;
    }


    public void render(Painter g, double cameraOffsetX, double cameraOffsetY) {

    }
}
