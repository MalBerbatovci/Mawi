package com.megamal.game.model;

import android.graphics.Rect;
import android.util.Log;

import com.megamal.framework.util.Tile;
import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 14/10/15.
 */
public class Player {

    //Bounding rect leeways in order to check collisions for area
    //less than the size of Mawi
    private final static int X_RECT_LEEWAY = 15;
    private final static int Y_RECT_LEEWAY = 30;

    //Constant for physics
    private final static double ACCEL_GRAVITY = 2722;
    private final static double WALKING_SPEED = 100;
    private final static double RUNNING_SPEED = 300;
    private final static double JUMPING_ACCELERATION = -1222;

    //Scan Lines co-ordinates for obstacles underneath -  must be added to mawi.X
    private final static int SCAN_A_DOWN = 15;
    private final static int SCAN_B_DOWN = 49;

    //Scan line co-ordinate for obstacles to left/right - must be 'taken away' from mawi.Y
    private final static int SCAN_A_ACROSS = 32;
    private final static int SCAN_B_ACROSS = 96;

    //Constant to 'minus' from the y co-ordinate of mawi, in order to make her
    //step above the ground
    private static final int ABOVE_FLOOR_CONST = 0;

    //constant for the popping Mawi away from an obstacle if too close
    private static final int CLOSENESS_TO_OBSTACLE = 2;

    private float x, y, previousX, previousY;
    private int width, height;

    //variables for the position of scanLines
    private double xScanLineADown, xScanLineBDown, yScanLineAAcross, yScanLineBAcross;
    private int scanADown, scanBDown;
    private int xStartAcross, xEndAcross;
    private int yFloor;

    //velY for jumping, velX for walking/running
    private double velY = 0, velX = 0;

    private boolean isGrounded = true;
    private boolean isAlive = true;
    private boolean isWalking = false;
    private boolean isRunning = false;
    private boolean isJumping = false;
    private boolean collisionWithObj = false;
    private boolean left, right = false;

    //DuckRect too, when implementing
    private Rect rect;

    //variable for tiles when checking scan lines
    private Tile tileA, tileB;

    public Player(float x, float y, int width, int height) {
        this.x = x;
        this.y = y - ABOVE_FLOOR_CONST;

        previousX = 0;
        previousY = 0;

        this.width = width;
        this.height = height;


        //initialise tile in order to check tile(s) underneath Mawi
        tileA = new Tile(0);
        tileB = new Tile(0);

        //setting rect smaller than the actual character, in order to give leeway during fighting
        //(better to avoid getting hit, then hit unnecessarily
        rect = new Rect((int) x + X_RECT_LEEWAY, (int) y + Y_RECT_LEEWAY,
                        (int) x + (width - X_RECT_LEEWAY), (int) y + (height - Y_RECT_LEEWAY));

    }

    public void update(float delta, int[][] map) {

        //update Scan Lines appropriately
        xScanLineADown = x + SCAN_A_DOWN;
        xScanLineBDown = x + SCAN_B_DOWN;

        yScanLineAAcross = y + SCAN_A_ACROSS;
        yScanLineBAcross = y + SCAN_B_ACROSS;

        //variables for the x co-ordinate of the scanLines to check obstacles
        //one to the left of mawi and one to the right
        xStartAcross = (int) x - CLOSENESS_TO_OBSTACLE - 1;
        xEndAcross = (int) x + CLOSENESS_TO_OBSTACLE + GameMainActivity.TILE_HEIGHT + 1;

        checkGrounded(map);
        checkCloseness(map);

        if(hasMoved())
            System.out.println("Previous x: " + x + ". \t Previous y:" + y);

        previousX = x;
        previousY = y;


        if (!isGrounded)
            velY += ACCEL_GRAVITY * delta;
        else
            velY = 0;

        if (isWalking || isRunning) {
            if(!collisionWithObj) {
                x += velX * delta;
            }
        }
        else {
            velX = 0;
            right = false;
            left = false;
        }


        if (velY != 0) {
            y += velY * delta;
        }

        updateRects();
        updateAnim(delta);

    }

    //method to update respective animation if necessary
    public void updateAnim(float delta) {

        if (velX == 0)
            return;

        //only
       /* if (!isGrounded)
            Assets.fallingAnim.update(delta); */

        if (right) {
            if (isWalking) {
                if (collisionWithObj)
                    Assets.walkHitAnimR.update(delta);
                else
                    Assets.walkAnimR.update(delta);
            }
            else
                Assets.runAnimR.update(delta);
        }
        else if (left) {
            if (isWalking) {
                if(collisionWithObj)
                     Assets.walkHitAnimL.update(delta);
                else {
                    Assets.walkAnimL.update(delta);
                }
            }
            else
                Assets.runAnimL.update(delta);
        }
    }

    private void updateRects() {
        rect.set((int) x + X_RECT_LEEWAY, (int) y + Y_RECT_LEEWAY,
                (int) x + (width - X_RECT_LEEWAY), (int) y + (height - Y_RECT_LEEWAY));
    }



    //Uses two scanLines located at x + 15 & x + 49 to check what tile(s) are under mawi;
    //this is then used to establish whether mawi is grounded or not
    private void checkGrounded(int[][] map) {

        //TEMPOARY TO STOP MAWI CRASHING
        if (y < 0 || x < 0 || x > 832) {
            isGrounded = false;
            return;
        }


        if (hasMoved()) {

            //get map[Y] value!
            yFloor = (int) y + GameMainActivity.PLAYER_HEIGHT;
            yFloor = (int) Math.floor(yFloor / GameMainActivity.TILE_HEIGHT);

            //convert into tile co-ordinates
            scanADown = (int) Math.floor(xScanLineADown / GameMainActivity.TILE_HEIGHT);
            scanBDown = (int) Math.floor(xScanLineBDown / GameMainActivity.TILE_HEIGHT);

            //check both of the tiles beneath mawi's feet
            tileA.setID(map[yFloor][scanADown]);
            tileB.setID(map[yFloor][scanBDown]);

            //if both are obstacles
            if (tileA.isObstacle() & tileB.isObstacle()) {
                //if mawi is not grounded at this point, she must have just contacted the
                //ground, therefore set the initial Y of the floor to avoid 'sinking'
                if (!isGrounded) {
                    System.out.println("yFloor is: " + yFloor);
                    y = (yFloor * GameMainActivity.TILE_HEIGHT) - height; //- height;
                    isGrounded = true;
                    if (isJumping)
                        isJumping = false;
                }
                //else, just walking/running along ground
                else {
                    isGrounded = true;
                    if (isJumping)
                        isJumping = false;
                }

            //else, if both are not obstacles, mawi is not grounded
            } else if (!tileA.isObstacle() & !tileB.isObstacle()) {
                isGrounded = false;

            //else, only one scanLine has been trigged, can do something w/ this info.
            } else {
                isGrounded = true;
                if (isJumping)
                    isJumping = false;
            }
        }
    }


    //method to check mawi's closeness to an object, and pop out if relevant (using the scanLinesAcross)
    //can be improved once walking starts - get which way the character is facing,
    //and then only check those necessary tiles i.e to the right or the left
    private void checkCloseness(int[][] map) {

        //TEMPORARY TO STOP MAWI CRASHING
        if (y < 0 || x < 0 || x > 832)
            return;

        collisionWithObj = false;

        if (isWalking || isRunning) {

            //System.out.println("has moved is true");
            //Log.d("Player", "Sneakily entered check Closeness");

            int scanAAcrossY = (int) Math.floor(yScanLineAAcross / GameMainActivity.TILE_WIDTH);
            int scanBAcrossY = (int) Math.floor(yScanLineBAcross / GameMainActivity.TILE_HEIGHT);

            //i.e if moving right, then check scan lines to the right
            if (right) {

                //variables for tile co-ordinates in relevance to scanLine variables
                int scanEndAcrossX = (int) Math.floor(xEndAcross / GameMainActivity.TILE_HEIGHT);

                //set Tile ID appropriately from scanlines
                tileA.setID(map[scanAAcrossY][scanEndAcrossX]);
                System.out.println("1: map[" + scanAAcrossY + "][" + scanEndAcrossX + "] checked");

                //check the first scan line and the tile to the right, if obstacle; set location of tile,
                // and set new x from this location
                if (tileA.isObstacle()) {
                    tileA.setLocation(scanAAcrossY, scanEndAcrossX);
                    x = (tileA.getX() - CLOSENESS_TO_OBSTACLE - GameMainActivity.TILE_WIDTH);
                    System.out.println("Case 1!");
                    collisionWithObj = true;
                    return;

                    //else, check the second scan line and the tile to the right, if obstacle set new x
                } else {
                    tileA.setID(map[scanBAcrossY][scanEndAcrossX]);
                    System.out.println("2: map[" + scanBAcrossY + "][" + scanEndAcrossX + "] checked");

                    if (tileA.isObstacle()) {
                        tileA.setLocation(scanBAcrossY, scanEndAcrossX);
                        x = (tileA.getX() - CLOSENESS_TO_OBSTACLE - GameMainActivity.TILE_HEIGHT);
                        System.out.println("Case 2!");
                        collisionWithObj = true;
                        return;
                    }
                }
            }

            //now check if there are any obstacles to the left, starting with scanLineA,
            //if on the left, then must set x to previous X instead of tile's x.
            else if (left) {
                int scanStartAcrossX = (int) Math.floor(xStartAcross / GameMainActivity.TILE_HEIGHT);
                tileA.setID(map[scanAAcrossY][scanStartAcrossX]);

                System.out.println("3: map[" + scanAAcrossY + "][" + scanStartAcrossX + "] checked");

                if (tileA.isObstacle()) {
                    tileA.setLocation(scanAAcrossY, scanStartAcrossX);
                    x = (tileA.getX() + CLOSENESS_TO_OBSTACLE + GameMainActivity.TILE_WIDTH);
                    System.out.println("Case 3! With co-ordinate: (" + previousX + "," + previousY + ")");

                    collisionWithObj = true;
                    return;

                    //X = *--®   o     ® is needed, * is given.
                    //    [  ]   T
                    //check obstacles to the left on scanLineB
                } else {
                    tileA.setID(map[scanBAcrossY][scanStartAcrossX]);
                    System.out.println("4: map[" + scanBAcrossY + "][" + scanStartAcrossX + "] checked");

                    if (tileA.isObstacle()) {
                        tileA.setLocation(scanBAcrossY, scanStartAcrossX);
                        x = (tileA.getX() + CLOSENESS_TO_OBSTACLE + GameMainActivity.TILE_WIDTH);
                        System.out.println("Case 4!");
                        collisionWithObj = true;
                        return;
                    }

                }

            }
        }
    }

    //Direction = parameter to tell whether walking left or right.
    //right = positive, left = negative (1 and -1 respectively)
    public void walk(int direction) {
        if (direction > 0) {
            velX = WALKING_SPEED;
            right = true;
        }
        else {
            velX = -WALKING_SPEED;
            left = true;
        }

        isWalking = true;
    }

    public void run(int direction) {
        if (direction > 0) {
            velX = RUNNING_SPEED;
            right = true;
        }
        else {
            velX = -RUNNING_SPEED;
            left = true;
        }

        isRunning = true;
    }

    public void stopWalking() {
        velX = 0;
        isWalking = false;
    }

    public void stopRunning() {
        velX = 0;
        isRunning = false;
    }

    public void jump() {
        if (isGrounded) {
            //initial jump
            y -= 10;
            //set velY as jumping_acceleration
            velY = JUMPING_ACCELERATION;
            updateRects();
            isJumping = true;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Rect getRect() {
        return rect;
    }

    public boolean hasMoved() {
        if(previousX == x && previousY == y)
            return false;
        else
            return true;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public boolean isCollided() {
        return collisionWithObj;
    }

    public boolean isRunning() { return isRunning; }

    public boolean isJumping() { return isJumping; }

    //if not right, then left, not sure if necessary
    public boolean isLeft() { return left; }

    public boolean isRight() { return right;}
}
