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

    private int initialiseTime;

    private Tile tileA, tileB;
    private int tileY, tileX;


    private boolean isPowerUp;
    private boolean isGrounded = false;
    private boolean isAlive = true;
    //boolean isVisible = true;
    private Rect rect;
    private Bitmap image;

    private long activationTime;

    private final static int JUMPING_ACCELERATION = -152;
    private final static int ACCEL_GRAVITY = 282;
    private final static int MOVING_VEL = 92;

    //variables to set object's co-ordinates from tile co-ordinate of objs where the object was created
    private final static int COIN_WIDTH = 64;
    private final static int COIN_HEIGHT = 64;

    private final static int SMALL_COIN_WIDTH = 64;
    private final static int SMALL_COINT_HEIGHT = 64;

    private final static int RECT_LEEWAY_X = 3;
    private final static int RECT_LEEWAY_Y = 3;
    private final static int Y_MOVEMENT_EXTRA = 20;

    private final static int SCAN_LEEWAY_Y = 10;
    private final static int SCAN_LEEWAY_X = 10;

    private final static int MAX_TIME = 20;


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

        this.initialiseTime = (int) System.currentTimeMillis();

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
                      break;

            case (2): {
                this.image = Assets.coinImage;
                this.x = (x + cameraOffsetX);
                this.y = (y + cameraOffsetY);
                this.height = SMALL_COINT_HEIGHT;
                this.width = SMALL_COIN_WIDTH;
                this.isPowerUp = false;
                updateRect(x, y, cameraOffsetX, cameraOffsetY);
                velX = 0;
                activationTime = System.currentTimeMillis();
                break;
            }

            default: this.image = Assets.coinBigImage;
                    this.x = (x + cameraOffsetX);
                    this.y = (y + cameraOffsetY) - GameMainActivity.TILE_HEIGHT;
                    this.height = COIN_HEIGHT;
                    this.width = COIN_WIDTH;
                    this.isPowerUp = false;
                    updateRect(x, y, cameraOffsetX, cameraOffsetY);
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

       // Log.d("Timing", "currentTime - initialiseTime is: " + ((int)System.currentTimeMillis() - initialiseTime));

        if(ID == 1) {
            if (((int) System.currentTimeMillis() - initialiseTime) / 1000 > MAX_TIME) {

                //if been alive for longer than set time, then remove by making isAlive false
                this.isAlive = false;

            }
        }

        else if(ID == 2) {
            //longer than 10 seconds
            if((System.currentTimeMillis() - activationTime) / 1000 > 10) {
                Log.d("CoinCount", "Made not alive!");
                this.isAlive = false;
            }
        }


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

    protected void checkCollisions(Player mawi) {
        if ((mawi.getplayerRect()).intersect(rect)) {
            collectableCaught(mawi);
        }
        else {
            return;
        }

    }

    protected void collectableCaught(Player mawi) {
        mawi.performAction(ID);
        Log.d("Collectables", "player caught collectables");
        isAlive = false;
    }

    protected void checkYMovement(int[][] map) {

        //this means that the object is falling, therefore check scanline for underneath,
        //if collision then set grounded to true, and set Y to be just above the suitable tile
        if (velY > 0) {
            Log.d("Collectables", "Case 1a");

            scanLineDownY = (int) Math.floor((y + height) / GameMainActivity.TILE_HEIGHT);

            if (scanLineDownY < 0 || scanLineDownY >= map.length) {
                Log.d("Collectables", "isAlive false in checkY.velY > 0");
                //isAlive = false;
                return;
            }

            if(velX > 0) {
                scanLineDownXa = (int) Math.floor(((x + width - Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH));
            }

            else {
                scanLineDownXa = (int) Math.floor((x + Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH);

            }

            if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                Log.d("Collectables", "isAlive false in checkY.velY > 0");
                //isAlive = false;
                return;
            }

            /*if (scanLineDownXb < 0 || scanLineDownXb >= map[0].length) {
                Log.d("Collectables", "isAlive false in checkY.velY < 0");
                //isAlive = false;
                return;
            } */

            Log.d("CollectableBug1", "index [" + scanLineDownY + "][" + scanLineDownXa + "] checked.");
            tileA.setID(map[scanLineDownY][scanLineDownXa]);
            //tileB.setID(map[scanLineDownY][scanLineDownXb]);

            //if obstacle then deal with appropriately
            if(tileA.isObstacle()) {
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
                //isAlive = false;
                return;
            }

            if(velX > 0) {
                scanLineDownXa = (int) Math.floor(((x + width) - Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH);
            }

            else {
                scanLineDownXa = (int) Math.floor((x + Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH);
            }

            if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                Log.d("Collectables", "isAlive false in checkY.velY < 0");
               // isAlive = false;
                return;
            }

            Log.d("CollectableBug", "index [" + scanLineDownY + "][" + scanLineDownXa + "] checked.");
            tileA.setID(map[scanLineDownY][scanLineDownXa]);
            //tileB.setID(map[scanLineDownY][scanLineDownXb]);

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

            scanLineDownY = (int) Math.floor((y + height + RECT_LEEWAY_Y)/ GameMainActivity.TILE_HEIGHT);


            if (scanLineDownY < 0 || scanLineDownY >= map.length) {
                Log.d("Collectables", "isAlive false in checkY.velY else");
                //isAlive = false;
                return;
            }

            if (velX > 0) {
                scanLineDownXa = (int) Math.floor(((x + width) - Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH);
                if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                    Log.d("Collectables", "isAlive false in checkY.velY else");
                    //isAlive = false;
                    return;
                }
            }
            else {
                scanLineDownXa = (int) Math.floor((x + Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH);
                if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                    Log.d("Collectables", "isAlive false in checkY.velY else");
                    //isAlive = false;
                    return;
                }
            }


            tileA.setID(map[scanLineDownY][scanLineDownXa]);

            if (!(tileA.isObstacle())) {
                isGrounded = false;
            }

        }
        return;

    }

    //method to check X movement against tiles and perform necessary actions
    protected void checkXMovement(int[][] map) {

        if (velX > 0) {
            Log.d("Collectables", "Case 1b");
            scanLineAcrossX = (int) Math.floor(((x + width) + (RECT_LEEWAY_X * 2)) / GameMainActivity.TILE_WIDTH);
            if (scanLineAcrossX < 0 || scanLineAcrossX >= map[0].length) {
                Log.d("Collectables", "isAlive false in checkX.velX > 0");
                //isAlive = false;
                return;
            }
        }

        else {
            Log.d("Collectables", "Case 2b");
            scanLineAcrossX = (int) Math.floor((x - (RECT_LEEWAY_X * 2)) / GameMainActivity.TILE_WIDTH);
            if (scanLineAcrossX < 0 || scanLineAcrossX >= map[0].length) {
                Log.d("Collectables", "isAlive false in checkX. else velX >0");
                //isAlive = false;
                return;
            }
        }

        //FALLING
        if(velY > 0) {
            scanLineAcrossYa = (int) Math.floor((y + height - SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);
        }

        //RISING
        else if (velY < 0) {
            scanLineAcrossYa = (int) Math.floor((y + SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);
        }

        else {
            scanLineAcrossYa = (int) Math.floor((y + (height / 2))/ GameMainActivity.TILE_HEIGHT);

        }

        if (scanLineAcrossYa < 0 || scanLineAcrossYa >= map.length) {
            Log.d("Collectables", "isAlive false in checkX.velY != 0");
            //isAlive = false;
            return;
        }


        Log.d("CollectableBug", "index [" + scanLineAcrossYa + "][" + scanLineAcrossX + "] checked.");

        tileA.setID(map[scanLineAcrossYa][scanLineAcrossX]);
        //tileB.setID(map[scanLineAcrossYb][scanLineAcrossX]);

        if (tileA.isObstacle()) {
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

        boolean isVisible = false;

        //RIGHT
        if ((velX > 0) && (x  - cameraOffsetX > 0) &&
                ((x - cameraOffsetX) <= GameMainActivity.GAME_WIDTH)) {

            //MOVING UP
            if (velY < 0 && ((y + height) - cameraOffsetY > 0) &&
                    (((y + height) - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT)) {

                isVisible = true;
            }

            //MOVING DOWN
            else if (velY > 0 && (y - cameraOffsetY) > 0 &&
                    (y - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT){

                isVisible = true;
            }

            else if (velY == 0 && ((y - cameraOffsetY) > 0 &&
                    (y - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT) ||
                    ((y + height) - cameraOffsetY > 0) &&
                    (((y + height) - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT)) {

                isVisible = true;
            }

            else {
                isVisible = false;
            }

        }


        //MOVING LEFT
        else if ((velX < 0) && ((x + width) - cameraOffsetX) > 0
                && ((x + width) - cameraOffsetX) <= GameMainActivity.GAME_WIDTH) {

            //MOVING UP
            if (velY < 0 && ((y + height) - cameraOffsetY > 0) &&
                    (((y + height) - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT)) {

                isVisible = true;
            }

            //MOVING DOWN
            else if (velY > 0 && (y - cameraOffsetY) > 0 && (y - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT){
                isVisible = true;
            }


            else if (velY == 0 && ((y - cameraOffsetY) > 0 &&
                    (y - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT) ||
                    ((y + height) - cameraOffsetY > 0) &&
                            (((y + height) - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT)) {

                isVisible = true;
            }

            else {
                isVisible = false;
            }

        }

        else if (velX == 0) {
            //MOVING UP
            if (velY < 0 && ((y + height) - cameraOffsetY > 0) &&
                    (((y + height) - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT)) {

                isVisible = true;
            }

            //MOVING DOWN
            else if (velY > 0 && (y - cameraOffsetY) > 0 && (y - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT){
                isVisible = true;
            }


            else if (velY == 0 && ((y - cameraOffsetY) > 0 &&
                    (y - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT) ||
                    ((y + height) - cameraOffsetY > 0) &&
                            (((y + height) - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT)) {

                isVisible = true;
            }

            else {
                isVisible = false;
            }

        }

        else {
            isVisible = false;
        }

        return isVisible;

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
                g.setColor(Color.rgb(80, 143, 240));
                g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY), width, height);
            } else {
                g.setColor(Color.rgb(80, 143, 240));
                g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY) - SCAN_LEEWAY_Y,
                        width, height + SCAN_LEEWAY_Y);
            }
        }
    }

    public void removeImage(Painter g, double cameraOffsetX, double cameraOffsetY) {
        g.setColor(Color.rgb(80, 143, 240));
        g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY) - SCAN_LEEWAY_Y, width,
                (height + SCAN_LEEWAY_Y));
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isMovingY() {
        return (velY != 0);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isFalling() {
        return (velY > 0);
    }

    protected void forceLeft() {
        velX = -(MOVING_VEL);
    }

    protected void forceRight() {
        velX = MOVING_VEL;
    }

    protected void forceUp() {
        velY = JUMPING_ACCELERATION;
    }

    protected void forceDown() {
        velY = -(JUMPING_ACCELERATION);
    }

    protected void forceY(double y) {
        this.y = y;
    }

    protected void forceX(double x) {
        this.x = x;
    }

    protected double getVelY() {
        return velY;
    }

    protected double getVelX() {
        return velX;
    }
}
