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

    private final static int SCAN_LEEWAY_Y = 10;


    //Method to be called once updated the x and y with the suitable velocity; checks to see whether
    //enemy has moved off-screen/onto the floor in the Y direction and updates variables accordingly
    public void checkYMovement(int[][] map, Tile tileA, Tile tileB, double x, double y, int width,
                               int height, int velY, boolean isAlive, boolean isGrounded) {

        int tileY;
        int scanLineDownY;
        int scanLineDownXa;
        int scanLineDownXb;

        if (velY > 0) {
            Log.d("Collectables", "Case 1a");

            scanLineDownY = (int) Math.floor((y + height) / GameMainActivity.TILE_HEIGHT);

            if (scanLineDownY < 0 || scanLineDownY >= map.length) {
                Log.d("Collectables", "isAlive false in checkY.velY > 0");
                isAlive = false;
                return;
            }

            scanLineDownXa = (int) Math.floor(((x + width) - RECT_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
            scanLineDownXb = (int) Math.floor((x + RECT_LEEWAY_X) / GameMainActivity.TILE_WIDTH);

            if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                Log.d("Collectables", "isAlive false in checkY.velY > 0");
                isAlive = false;
                return;
            }

            if (scanLineDownXb < 0 || scanLineDownXb >= map[0].length) {
                Log.d("Collectables", "isAlive false in checkY.velY < 0");
                isAlive = false;
                return;
            }

            Log.d("CollectableBug1", "index [" + scanLineDownY + "][" + scanLineDownXa + "] checked.");
            tileA.setID(map[scanLineDownY][scanLineDownXa]);
            tileB.setID(map[scanLineDownY][scanLineDownXb]);

            //if obstacle then deal with appropriately
            if (tileA.isObstacle() || tileB.isObstacle()) {
                isGrounded = true;
                velY = 0;

                //same scanLineDownY used so doesnt matter if tileA/tileB
                tileY = tileA.yLocationNoOffset(scanLineDownY);

                //set Y to be just above tile
                y = tileY - height;
            }
            return;


            //this means that it is 'jumping', check scanLine above, if collision then decrease velY and set Y to suitable
        } else if (velY < 0) {
            Log.d("Collectables", "Case 2a");
            scanLineDownY = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);


            if (scanLineDownY < 0 || scanLineDownY >= map.length) {
                Log.d("Collectables", "isAlive false in checkY.velY < 0");
                isAlive = false;
                return;
            }

            scanLineDownXa = (int) Math.floor(((x + width) - RECT_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
            scanLineDownXb = (int) Math.floor((x + RECT_LEEWAY_X) / GameMainActivity.TILE_WIDTH);

            if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                Log.d("Collectables", "isAlive false in checkY.velY < 0");
                isAlive = false;
                return;
            }

            if (scanLineDownXb < 0 || scanLineDownXb >= map[0].length) {
                Log.d("Collectables", "isAlive false in checkY.velY < 0");
                isAlive = false;
                return;
            }

            Log.d("CollectableBug", "index [" + scanLineDownY + "][" + scanLineDownXa + "] checked.");
            tileA.setID(map[scanLineDownY][scanLineDownXa]);
            tileB.setID(map[scanLineDownY][scanLineDownXb]);

            //decrease velocity and set y to be just below tile
            if (tileA.isObstacle()) {
                velY = Math.abs(velY) / 5;
                tileY = tileA.yLocationNoOffset(scanLineDownY);
                y = tileY + GameMainActivity.TILE_HEIGHT;
            }
            return;

            //else, this is the case where the object is moving on the ground, check beneath to see if
            //still grounded
        } else {
            Log.d("Collectables", "Case 3a");

            scanLineDownY = (int) Math.floor((y + height - RECT_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);


        }
    }



    //Method to be called once updated the x and y with the suitable velocity; checks to see whether
    //enemy has moved off-screen/into an obstacle in the X direction and updates variables accordingly
    private void checkXMovement(int[][] map, Tile tileA, Tile tileB, double x, double y, int width,
                                int height, int velX, boolean isAlive) {

        int scanLineAcrossX;
        int scanLineAcrossYa;
        int scanLineAcrossYb;
        int tileX;

        if (velX > 0) {
            Log.d("Collectables", "Case 1b");
            scanLineAcrossX = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
            if (scanLineAcrossX < 0 || scanLineAcrossX >= map[0].length) {
                Log.d("Collectables", "isAlive false in checkX.velX > 0");
                isAlive = false;
                return;
            }
        }

        else {
            Log.d("Collectables", "Case 2b");
            scanLineAcrossX = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
            if (scanLineAcrossX < 0 || scanLineAcrossX >= map[0].length) {
                Log.d("Collectables", "isAlive false in checkX. else velX >0");
                isAlive = false;
                return;
            }
        }

        scanLineAcrossYa = (int) Math.floor((y + height - SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);
        scanLineAcrossYb = (int) Math.floor((y + SCAN_LEEWAY_Y)/ GameMainActivity.TILE_HEIGHT);

        if (scanLineAcrossYa < 0 || scanLineAcrossYa >= map.length) {
            Log.d("Collectables", "isAlive false in checkX.velY != 0");
            isAlive = false;
            return;
        }
        if (scanLineAcrossYb < 0 || scanLineAcrossYb >= map.length) {
            Log.d("Collectables", "isAlive false in checkX.velY != 0");
            isAlive = false;
            return;
        }

        Log.d("CollectableBug", "index [" + scanLineAcrossYa + "][" + scanLineAcrossX + "] checked.");

        tileA.setID(map[scanLineAcrossYa][scanLineAcrossX]);
        tileB.setID(map[scanLineAcrossYb][scanLineAcrossX]);

        if (tileA.isObstacle() || tileB.isObstacle()) {
            tileX = tileA.xLocationNoOffset(scanLineAcrossX);

            if (velX > 0) {
                x = tileX - width;
            } else {
                x = tileX + GameMainActivity.TILE_HEIGHT;
            }

            velX = -(velX);
        }
        return;
    }

    public abstract void updateAnim(float delta);

    public abstract void update(float delta, int[][] map, double cameraOffsetX, double cameraOffsetY);

    public abstract void checkCollisions(Rect playerRect);

    public void render(Painter g, Bitmap image) {

    }

}
