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
    private final static double ACCEL_GRAVITY = 1722;
    private final static double WALKING_SPEED = 100;
    private final static double RUNNING_SPEED = 242;
    private final static double JUMPING_ACCELERATION = -852;

    //Scan Lines co-ordinates for obstacles underneath -  must be added to mawi.X
    private final static int SCAN_A_DOWN = 15;
    private final static int SCAN_B_DOWN = 49;

    //Scan line co-ordinate for obstacles to left/right - must be 'taken away' from mawi.Y
    private final static int SCAN_A_ACROSS = 32;
    private final static int SCAN_B_ACROSS = 96;

    //constant for the popping Mawi away from an obstacle if too close
    private static final int CLOSENESS_TO_OBSTACLE = 2;

    private static final int LEFT = -1;
    private static final int RIGHT = 1;

    private double x, y, previousX, previousY;
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
    private boolean locked = false;
    private boolean hasMoved = true;
    private boolean hitNewBox = false;
    private boolean collisionWithObj = false;
    private boolean left, right = false;
    private boolean justGrounded = false;

    private Collectable collectable;

    //DuckRect too, when implementing
    private Rect playerRect;
    private Rect coinRect;

    //variable for tiles when checking scan lines
    private Tile tileA, tileB;

    public Player(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;

        previousX = 0;
        previousY = 0;

        this.width = width;
        this.height = height;


        //initialise tile in order to check tile(s) underneath Mawi
        tileA = new Tile(0);
        tileB = new Tile(0);
        collectable = new Collectable(1, 0, 0, 0, 0);

        //setting rect smaller than the actual character, in order to give leeway during fighting
        //(better to avoid getting hit, then hit unnecessarily
        playerRect = new Rect((int) x + X_RECT_LEEWAY, (int) y + Y_RECT_LEEWAY,
                (int) x + (width - X_RECT_LEEWAY), (int) y + (height - Y_RECT_LEEWAY));

        //initialise coinRect
        coinRect = new Rect();

    }

    public void update(float delta, int[][] map, double cameraOffsetX, double cameraOffsetY) {

        checkYMovement(map, cameraOffsetX, cameraOffsetY);
        checkXMovement(map, cameraOffsetX, cameraOffsetY);

        //Log.d("Location", "Previous x: " + x + ". Previous y:" + y + ".\t x = " + x + ". y = " + y + ".\n");

        if (!isGrounded) {
            velY += ACCEL_GRAVITY * delta;
        } else {
            velY = 0;
        }

        if (cameraOffsetX == 0 && cameraOffsetY == 0) {
            previousX = x;
            previousY = y;
        }

        //if mawi is not in collision with an Object & is walking/running then update x appropriately
        if (isWalking || isRunning) {
            if (!collisionWithObj) {
                x += velX * delta;

                if (x < 0) {
                    x = 0;
                } else if ((x + width > GameMainActivity.GAME_WIDTH)) {
                    x = GameMainActivity.GAME_WIDTH - width;
                }
            }
        } else {
            velX = 0;
            right = false;
            left = false;
        }


        if (velY != 0) {
            y += velY * delta;
            Log.d("Jumping", "velY: " + velY + ". Y: " + y + ". \n");
        }

        if (locked)
            locked = false;

        if (hasMoved(cameraOffsetX, cameraOffsetY))
            Log.d("Location", "Previous x: " + previousX + ". Previous y:" + previousY + ".\t x = " + x + ". y = " + y + ".\n");

        updateRects();
        updateAnim(delta);

        Log.d("Jumping", "Is jumping: " + isJumping + ".");

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
            } else
                Assets.runAnimR.update(delta);
        } else if (left) {
            if (isWalking) {
                if (collisionWithObj)
                    Assets.walkHitAnimL.update(delta);
                else {
                    Assets.walkAnimL.update(delta);
                }
            } else
                Assets.runAnimL.update(delta);
        }
    }

    private void updateRects() {
        playerRect.set((int) x + X_RECT_LEEWAY, (int) y + Y_RECT_LEEWAY,
                (int) x + (width - X_RECT_LEEWAY), (int) y + (height - Y_RECT_LEEWAY));
    }


    //Uses two scanLines located at x + 15 & x + 49 to check what tile(s) are under mawi;
    //this is then used to establish whether mawi is grounded or not
    private void checkYMovement(int[][] map, double cameraOffsetX, double cameraOffsetY) {

        if(justGrounded)
            justGrounded = false;
        if (hitNewBox)
            hitNewBox = false;


        if (hasMoved(cameraOffsetX, cameraOffsetY) || !isGrounded) {

            xScanLineADown = x + SCAN_A_DOWN + cameraOffsetX;
            xScanLineBDown = x + SCAN_B_DOWN + cameraOffsetX;

            //convert into tile co-ordinates
            scanADown = (int) Math.floor(xScanLineADown / GameMainActivity.TILE_HEIGHT);
            scanBDown = (int) Math.floor(xScanLineBDown / GameMainActivity.TILE_HEIGHT);

            //boundary conditions - these will cause an arrayIndexOutOfBounds Exception
            if (scanADown < 0 || scanADown >= map[0].length || scanBDown < 0 || scanBDown >= map[0].length)
                return;

            //this means charatcer is currently jumping, first get map[Y] value then see if obstacles
            //if obstacles then charatcer has hit head
            if (velY < 0) {
                yFloor = (int) Math.floor((y + cameraOffsetY) / GameMainActivity.TILE_HEIGHT);

                //boundary conditions
                if (yFloor < 0 || yFloor >= map.length) {
                    isGrounded = false;
                    return;
                }

                tileA.setID(map[yFloor][scanADown]);
                tileB.setID(map[yFloor][scanBDown]);

                //check if either is obstacle, if so set y and change velY
                if (tileA.isObstacle()) {

                    //yJumpingObstacle(tileA, )
                    tileA.setLocation(yFloor, scanADown, cameraOffsetX, cameraOffsetY);
                    y = tileA.getY() + GameMainActivity.TILE_HEIGHT;
                    velY = Math.abs(velY) / 5;
                    collectable.setVariables(1, tileA.getX(), tileA.getY(), cameraOffsetX, cameraOffsetY);
                    hitNewBox = true;
                    Log.d("Collectables", "collectable made!");
                    return;
                } else if (tileB.isObstacle()) {
                    tileB.setLocation(yFloor, scanBDown, cameraOffsetX, cameraOffsetY);
                    y = tileB.getY() + GameMainActivity.TILE_HEIGHT;
                    velY = Math.abs(velY) / 5;
                    collectable.setVariables(1, tileB.getX(), tileB.getY(), cameraOffsetX, cameraOffsetY);
                    hitNewBox = true;
                    return;

                //else case where tile is a collectable, check if rects intersect, if so then change entry and add score suitably
                } else if (tileA.isCollectable()) {
                    yCoinCollision(tileA, yFloor, scanADown, cameraOffsetX, cameraOffsetY, map, false);

                } else if (tileB.isCollectable()) {
                    yCoinCollision(tileB, yFloor, scanBDown, cameraOffsetX, cameraOffsetY, map, false);
                }

                //NEED TO ADD CODE HERE TO CREATE COLLECTABLE HERE, SET HIT NEW BOX TO TRUE, AND THEN RETURN COLLECTABLE


                return;
            }
            //else, character is not jumping so check tiles beneath
            else {

                //get map[Y] value!
                yFloor = (int) (y + GameMainActivity.PLAYER_HEIGHT + cameraOffsetY);
                yFloor = (int) Math.floor(yFloor / GameMainActivity.TILE_HEIGHT);

                //do something if character has somehow fallen to bottom of map
                if (yFloor < 0 || yFloor >= map.length)
                    return;

                //do something with this info about player being outside screen (indexOutOfBounds)
                if (scanBDown >= map[0].length || scanBDown < 0 || scanADown < 0 || scanADown >= map[0].length)
                    return;

                //check both of the tiles beneath mawi's feet
                tileA.setID(map[yFloor][scanADown]);
                tileB.setID(map[yFloor][scanBDown]);

                Log.d("Grounded", "map[" + yFloor + "][" + scanADown + "]" + "and map[" + yFloor + "][" + scanADown + "]" +
                        " checked!");

                //if both are obstacles
                if (tileA.isObstacle() && tileB.isObstacle()) {
                    Log.d("Grounded", "both tiles beneath are obstacles");
                    //if mawi is not grounded at this point, she must have just contacted the
                    //ground, therefore set the initial Y of the floor to avoid 'sinking'
                    if (!isGrounded) {
                        Log.d("Grounded", "found floor after being ungrounded");
                        isGrounded = true;
                        justGrounded = true;
                        if (isJumping)
                            isJumping = false;

                        allignMawiY(cameraOffsetX, cameraOffsetY);

                        return;
                    }
                    //else, just walking/running along ground
                    else {
                        if (isJumping)
                            isJumping = false;

                       /* if ((int) y % GameMainActivity.TILE_HEIGHT != 0) {
                            tileA.setLocation(yFloor, scanADown, cameraOffsetX, cameraOffsetY);
                            y = tileA.getY() - height;
                        } */

                        return;
                    }

                    //else, if both are not obstacles, mawi is not grounded
                } else if ((!tileA.isObstacle() & !tileB.isObstacle()) && (!tileA.isCollectable() || !tileB.isCollectable())) {
                    Log.d("Grounded", "both tiles beneath are not obstacles");
                    isGrounded = false;
                    return;

                    //else, only one scanLine has been trigged, can do something w/ this info.
                } else if (tileA.isObstacle() || tileB.isObstacle()){
                    //y = (yFloor * GameMainActivity.TILE_HEIGHT) - height;
                    Log.d("Grounded", "only one tiles beneath is an obstacle");

                    if (!isGrounded) {
                        justGrounded = true;
                        isGrounded = true;
                    }

                    if (isJumping)
                        isJumping = false;

                    //if mawi is not perfectly alligned with tile after falling onto it, force this
                    if (justGrounded) {
                        allignMawiY(cameraOffsetX, cameraOffsetY);
                    }
                    return;

                //case where one tile underneath is an collectable
                } else if (tileA.isCollectable() || tileB.isCollectable()) {
                    Log.d("Collectables", "tileA/B is collectable!");

                    if (tileA.isCollectable()) {
                        yCoinCollision(tileA, yFloor, scanADown, cameraOffsetX, cameraOffsetY, map, true);
                    }
                    else {
                        yCoinCollision(tileB, yFloor, scanBDown, cameraOffsetX, cameraOffsetY, map, true);
                    }

                    isGrounded = false;

                    return;
                }
            }
        }
    }

    private void yCoinCollision(Tile tile, int yIndex, int xIndex, double cameraOffsetX, double cameraOffsetY,
                                int[][]map, boolean grounded) {

        tile.setRectCoin(yIndex, xIndex, cameraOffsetX, cameraOffsetY);
        coinRect = tile.getRect();
        if (playerRect.intersect(coinRect)) {
            map[yIndex][xIndex] = 0;
            if (grounded) {
                isGrounded = false;
            }
        }

    }

    //method to put mawi's y to be perfectly above tile after just being grounded
    private void allignMawiY(double cameraOffsetX, double cameraOffsetY) {
        if ((int) (y + cameraOffsetY) % GameMainActivity.TILE_HEIGHT != 0) {
            if (tileA.isObstacle()) {
                tileA.setLocation(yFloor, scanADown, cameraOffsetX, cameraOffsetY);
                y = (tileA.getY() - height);
                Log.d("Grounded", "TileA.isObstacle - y set to: " + y);
            } else {
                tileB.setLocation(yFloor, scanBDown, cameraOffsetX, cameraOffsetY);
                y = tileB.getY() - height;
                Log.d("Grounded", "TileB.isObstacle - y set to: " + y);
            }
        }
    }


    //method to check mawi's closeness to an object, and pop out if relevant (using the scanLinesAcross)
    //can be improved once walking starts - get which way the character is facing,
    //and then only check those necessary tiles i.e to the right or the left
    private void checkXMovement(int[][] map, double cameraOffsetX, double cameraOffsetY) {

        if (hasMoved(cameraOffsetX, cameraOffsetY) || collisionWithObj || justGrounded()) {

            yScanLineAAcross = y + SCAN_A_ACROSS + cameraOffsetY;
            yScanLineBAcross = y + SCAN_B_ACROSS + cameraOffsetY;

            xStartAcross = (int) ((x - CLOSENESS_TO_OBSTACLE - 1) + cameraOffsetX);
            xEndAcross = (int) ((x + CLOSENESS_TO_OBSTACLE + GameMainActivity.TILE_WIDTH + 1) + cameraOffsetX);

            collisionWithObj = false;

            int scanAAcrossY = (int) Math.floor(yScanLineAAcross / GameMainActivity.TILE_HEIGHT);
            if (scanAAcrossY < 0 || scanAAcrossY >= map.length)
                return;

            int scanBAcrossY = (int) Math.floor(yScanLineBAcross / GameMainActivity.TILE_HEIGHT);
            if (scanBAcrossY < 0 || scanBAcrossY >= map.length)
                return;

            //variables for tile co-ordinates in relevance to scanLine variables
            int scanEndAcrossX = (int) Math.floor(xEndAcross / GameMainActivity.TILE_HEIGHT);
            if (scanEndAcrossX < 0 || scanEndAcrossX >= map[0].length)
                return;


            int scanStartAcrossX = (int) Math.floor(xStartAcross / GameMainActivity.TILE_HEIGHT);
            if (scanStartAcrossX < 0 || scanStartAcrossX >= map[0].length)
                return;

            //i.e if moving right, then check scan lines to the right
            if (right) {

                    //set Tile ID appropriately from scanlines
                    tileA.setID(map[scanAAcrossY][scanEndAcrossX]);
                    tileB.setID(map[scanBAcrossY][scanEndAcrossX]);
                    Log.d("CollisionsX", "1: map[" + scanAAcrossY + "][" + scanEndAcrossX + "] checked");

                    //check the first scan line and the tile to the right, if obstacle; set location of tile,
                    // and set new x from this location
                    if (tileA.isObstacle()) {
                        xObstacleCollision(tileA, scanAAcrossY, scanEndAcrossX, cameraOffsetX, cameraOffsetY, RIGHT);
                        Log.d("CollisionsX", "Case 1!");
                        return;

                        //else, check the second scan line and the tile to the right, if obstacle set new x
                    } else if (tileB.isObstacle()){
                        xObstacleCollision(tileB, scanBAcrossY, scanEndAcrossX, cameraOffsetX, cameraOffsetY, RIGHT);
                        Log.d("CollisionsX", "Case 2!");
                        return;

                    } else if (tileA.isCollectable() || tileB.isCollectable()) {

                        xCoinCollision(tileA, scanAAcrossY, scanEndAcrossX, tileB, scanBAcrossY, scanEndAcrossX,
                                cameraOffsetX, cameraOffsetY, map);

                        return;
                    }
                }

            //now check if there are any obstacles to the left, starting with scanLineA,
            //if on the left, then must set x to previous X instead of tile's x.
            else if (left) {

                tileA.setID(map[scanAAcrossY][scanStartAcrossX]);
                tileB.setID(map[scanBAcrossY][scanStartAcrossX]);

                Log.d("CollisionsX", "3: map[" + scanAAcrossY + "][" + scanStartAcrossX + "] checked");

                if (tileA.isObstacle()) {
                    xObstacleCollision(tileA, scanAAcrossY, scanStartAcrossX, cameraOffsetX, cameraOffsetY, LEFT);
                    return;

                } else if (tileB.isObstacle()) {
                    Log.d("CollisionsX", "4: map[" + scanBAcrossY + "][" + scanStartAcrossX + "] checked");
                    xObstacleCollision(tileB, scanBAcrossY, scanStartAcrossX, cameraOffsetX, cameraOffsetY, LEFT);
                    return;


                    //not Mutually exclusive, so must check both
                } else if (tileA.isCollectable() || tileB.isCollectable()) {

                    xCoinCollision(tileA, scanAAcrossY, scanStartAcrossX, tileB, scanBAcrossY, scanStartAcrossX,
                            cameraOffsetX, cameraOffsetY, map);
                    return;
                }

            //else, case when moving but not left or right i.e justGrounded
            } else if (justGrounded()) {
                tileA.setID(map[scanAAcrossY][scanEndAcrossX]);
                tileB.setID(map[scanBAcrossY][scanEndAcrossX]);

                if (tileA.isObstacle()) {
                    xObstacleCollision(tileA, scanAAcrossY, scanEndAcrossX, cameraOffsetX, cameraOffsetY, RIGHT);
                    return;

                } else if (tileB.isObstacle()) {
                    xObstacleCollision(tileB, scanBAcrossY, scanEndAcrossX, cameraOffsetX, cameraOffsetY, RIGHT);
                    return;

                } else if (tileA.isCollectable() || tileB.isCollectable()) {
                    xCoinCollision(tileA, scanAAcrossY, scanEndAcrossX, tileB, scanBAcrossY, scanEndAcrossX,
                                cameraOffsetX, cameraOffsetY, map);

                } else {

                    tileA.setID(map[scanAAcrossY][scanStartAcrossX]);
                    tileB.setID(map[scanBAcrossY][scanStartAcrossX]);

                    if (tileA.isObstacle()) {
                        xObstacleCollision(tileA, scanAAcrossY, scanStartAcrossX, cameraOffsetX, cameraOffsetY, LEFT);
                        return;

                    } else if (tileB.isObstacle()) {
                        xObstacleCollision(tileB, scanBAcrossY, scanStartAcrossX, cameraOffsetX, cameraOffsetY, LEFT);
                        return;

                    } else if (tileA.isCollectable() || tileB.isCollectable()) {

                        xCoinCollision(tileA, scanAAcrossY, scanStartAcrossX, tileB, scanBAcrossY,
                                        scanStartAcrossX, cameraOffsetX, cameraOffsetY, map);
                    }
                }
            }
            return;
        }

        collisionWithObj = false;
    }

    private void xObstacleCollision(Tile tile, int yIndex, int xIndex, double cameraOffsetX, double cameraOffsetY, int direction) {
        tile.setLocation(yIndex, xIndex, cameraOffsetX, cameraOffsetY);

        if(direction == LEFT)
         x = (tile.getX() + CLOSENESS_TO_OBSTACLE + GameMainActivity.TILE_WIDTH);

        else if (direction == RIGHT)
         x = (tile.getX() - CLOSENESS_TO_OBSTACLE - GameMainActivity.TILE_WIDTH);

        collisionWithObj = true;
        return;

    }

    private void xCoinCollision(Tile tileA, int yIndexA, int xIndexA, Tile tileB, int yIndexB, int xIndexB,
                                double cameraOffsetX, double cameraOffsetY, int[][] map) {

        if(tileA.isCollectable()) {
            tileA.setRectCoin(yIndexA, xIndexA, cameraOffsetX, cameraOffsetY);
            coinRect = tileA.getRect();
            if (playerRect.intersect(coinRect)) {
                map[yIndexA][xIndexA] = 0;
            }
        }

        if (tileB.isCollectable()) {
            tileB.setRectCoin(yIndexB, xIndexB, cameraOffsetX, cameraOffsetY);
            coinRect = tileB.getRect();
            if (playerRect.intersect(coinRect)) {
                map[yIndexB][xIndexB] = 0;
            }
        }

    }

    //Direction = parameter to tell whether walking left or right.
    //right = positive, left = negative (1 and -1 respectively)
    public void walk(int direction) {
        if (direction > 0) {
            velX = WALKING_SPEED;
            right = true;
            left = false;
        } else {
            velX = -WALKING_SPEED;
            left = true;
            right = false;
        }

        isWalking = true;
    }

    public void run(int direction) {
        if (direction > 0) {
            velX = RUNNING_SPEED;
            right = true;
            left = false;
        } else {
            velX = -RUNNING_SPEED;
            left = true;
            right = false;
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
            isGrounded = false;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void lockToXThreshold(int xThreshold) {
        x = xThreshold - (width / 2);
        locked = true;
    }

    public void lockToYThreshold(int yThreshold) {
        y = yThreshold - (height / 2);
        Log.d("YCamera", "Locked at: " + y);
        locked = true;
    }


    public boolean hasMoved(double cameraOffsetX, double cameraOffsetY) {

        if ((cameraOffsetX == 0 && cameraOffsetY == 0)) {
            if (previousX != x || previousY != y) {
                hasMoved = true;
                Log.d("hasMoved", "is true");
                return hasMoved;
            } else if (previousX == x && previousY == y) {
                hasMoved = false;
                Log.d("hasMoved", "is false as previous = this");
                return hasMoved;
            }
        } else {
            if (locked) {
                hasMoved = true;
                Log.d("hasMoved", "is true because locked");
                return hasMoved;
            }
            if (!locked)
                if (isWalking() || isRunning() || velY != 0) {
                    hasMoved = true;
                    Log.d("hasMoved", "is true as unlocked but running/jumping");
                    return hasMoved;
                } else {
                    hasMoved = false;
                    Log.d("hasMoved", "is false as unlocked and not running/jumping");
                    return hasMoved;
                }
        }

        hasMoved = false;
        return hasMoved;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Rect getplayerRect() {
        return playerRect;
    }

    public double getVelY() {
        return velY;
    }

    public Collectable getMostRecentCollectable() { return collectable; }

    public boolean hitNewBox() { return hitNewBox; }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public boolean isCollided() {
        return collisionWithObj;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public boolean justGrounded() { return justGrounded; }

    //if not right, then left, not sure if necessary
    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public void performAction(int ID) {
        switch(ID) {
            case(1): Log.d("Collectable","coin registered as caught");
                     break;
        }
    }
}
