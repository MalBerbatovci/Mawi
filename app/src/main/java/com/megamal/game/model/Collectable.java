package com.megamal.game.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.megamal.framework.util.Painter;
import com.megamal.framework.util.Tile;
import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 23/11/15.
 */
public class Collectable {

    private int ID;
    private double x, y;
    private double rectX, rectY;
    private double velX, velY;
    private int height, width;
    private int scanLineDownXa, scanLineDownXb, scanLineDownY;
    private int scanLineAcrossX, scanLineAcrossYa, scanLineAcrossYb;

    private Tile tileA, tileB;
    private int tileY, tileX;


    private boolean isPowerUp;
    private boolean isGrounded = false;
    private boolean isAlive = true;
    //boolean isVisible = true;
    private Rect rect;
    private Bitmap image;

    private final static int JUMPING_ACCELERATION = -152;
    private final static int ACCEL_GRAVITY = 282;
    private final static int MOVING_VEL = 92;

    //variables to set object's co-ordinates from tile co-ordinate of objs where the object was created
    private final static int COIN_WIDTH = 64;
    private final static int COIN_HEIGHT = 64;

    private final static int RECT_LEEWAY_X = 3;
    private final static int RECT_LEEWAY_Y = 3;

    private final static int SCAN_LEEWAY_Y = 10;
    private final static int SCAN_LEEWAY_X = 10;


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

        setVariables(ID, x, y, cameraOffsetX, cameraOffsetY);
    }

    public void setVariables(int ID, double x, double y, double cameraOffsetX, double cameraOffsetY) {
        this.ID = ID;
        this.velY = JUMPING_ACCELERATION;
        this.velX = MOVING_VEL;
        isAlive = true;

        //true x and y of object
        this.x = x + cameraOffsetX;
        this.y = y + cameraOffsetY;

        switch(ID) {
            //gives true x and y of coin
            case (1): this.image = Assets.coinBigImage;
                      this.x = (x + cameraOffsetX);
                      this.y = (y + cameraOffsetY) - GameMainActivity.TILE_HEIGHT;
                      this.height = COIN_HEIGHT;
                      this.width = COIN_WIDTH;
                      this.isPowerUp = false;
                      updateRect(x, y, cameraOffsetX, cameraOffsetY);
                      Log.d("Enemy", "collectable rendered with co-ords: ( " + (this.x - cameraOffsetX) + "," + (this.y - cameraOffsetY) + ". \n");
                      Log.d("Collectables", "collectable made with tile co-ords: ( " + x + "," + y + ". \n");
                      break;
        }

    }

    private void updateRect(double x, double y, double cameraOffsetX, double cameraOffsetY) {

        //if (visible(x, y);
        //set to be in relation to the screen - IF VISIBLE

        if (isVisible(cameraOffsetX, cameraOffsetY)) {
            rectX = (x + RECT_LEEWAY_X - cameraOffsetX);
            rectY = (y + RECT_LEEWAY_Y - cameraOffsetY);

            rect.set((int) rectX, (int) rectY, (int) rectX + (width + RECT_LEEWAY_X), (int) rectY + (height + RECT_LEEWAY_Y));
        }

        else
            return;

    }

    public void update(float delta, int[][] map, double cameraOffsetX, double cameraOffsetY, Player mawi) {

        if (isAlive) {
            x += velX * delta;

            if (!isGrounded) {
                velY += ACCEL_GRAVITY * delta;
            } else {
                velY = 0;
            }

            y += velY * delta;

            //must be done when invisible too,
            checkXMovement(map);
            checkYMovement(map);

            //if (visible(x, y, cameraOffsetX, cameraOffsetY);
            updateRect(x, y, cameraOffsetX, cameraOffsetY);

            //if (playerInRange()) {
            checkCollisions(mawi);
            // }
        }

        else
            return;

    }

    //c
    /*private boolean playerInRange() {

    }*/

    private void checkCollisions(Player mawi) {
        if ((mawi.getplayerRect()).intersect(rect)) {
            collectableCaught(mawi);
        }
        else
            return;

    }

    private void collectableCaught(Player mawi) {
        mawi.performAction(ID);
        Log.d("Collectables", "player caught collectables");
        isAlive = false;
    }

    private void checkYMovement(int[][] map) {

        //this means that the object is falling, therefore check scanline for underneath,
        //if collision then set grounded to true, and set Y to be just above the suitable tile
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
            if(tileA.isObstacle() || tileB.isObstacle()) {
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

            scanLineDownY = (int) Math.floor((y + height - RECT_LEEWAY_Y)/ GameMainActivity.TILE_HEIGHT);


            if (scanLineDownY < 0 || scanLineDownY >= map.length) {
                Log.d("Collectables", "isAlive false in checkY.velY else");
                isAlive = false;
                return;
            }

            if (velX > 0) {
                scanLineDownXa = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
                if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                    Log.d("Collectables", "isAlive false in checkY.velY else");
                    isAlive = false;
                    return;
                }
            }
            else {
                scanLineDownXa = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
                if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                    Log.d("Collectables", "isAlive false in checkY.velY else");
                    isAlive = false;
                    return;
                }
            }


            Log.d("CollectableBug", "index [" + scanLineDownY + "][" + scanLineDownXa + "] checked.");
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

    //method to check is coin is on screen and render appropriately
    public boolean isVisible(double cameraOffsetX, double cameraOffsetY) {

        if ((((x + width) - cameraOffsetX > 0) && (((x + width) - cameraOffsetX) <= GameMainActivity.GAME_WIDTH)) ||
                ((x - cameraOffsetX > 0) && (x - cameraOffsetX) <= GameMainActivity.GAME_WIDTH)){

            if((((y + height) - cameraOffsetY) > 0 && (y + height <= GameMainActivity.GAME_HEIGHT)) ||
                    ((y - cameraOffsetY > 0) && (y - cameraOffsetY <= GameMainActivity.GAME_HEIGHT))) {

                    Log.d("RenderingCol", "Rendering!");
                    return true;
            }

            else {
                return false;
            }
        }

        else {
            Log.d("RenderingCol", "Not rendering!");
            return false;
        }

    }

    public void render(Painter g, double cameraOffsetX, double cameraOffsetY) {

        if(isVisible(cameraOffsetX,cameraOffsetY) && isAlive) {
            g.drawImage(image, (int) (x - cameraOffsetX), (int) (y - cameraOffsetY), width, height);
        } else
            return;

    }

    public void clearAreaAroundCoin(Painter g, double cameraOffsetX, double cameraOffsetY) {
        if (isVisible(cameraOffsetX, cameraOffsetY) && isAlive) {
            if (velY <= 0) {
                g.setColor(Color.rgb(208, 244, 247));
                g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY), width, height);
            } else {
                g.setColor(Color.rgb(208, 244, 247));
                g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY) - SCAN_LEEWAY_Y, width, height);
            }
        }
    }

    public void removeImage(Painter g, double cameraOffsetX, double cameraOffsetY) {
        g.setColor(Color.rgb(208, 244, 247));
        g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY) - SCAN_LEEWAY_Y, width, height);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isMovingY() {
        return (velY != 0);
    }

    public double getX() {
        Log.d("RenderingCollectable", "x is: " + x + ".\n");
        return x;
    }

    public double getY() {
        Log.d("RenderingCollectable", "y is: " + y + ".\n");
        return y;
    }

    public boolean isFalling() {
        return (velY < 0);
    }
}
