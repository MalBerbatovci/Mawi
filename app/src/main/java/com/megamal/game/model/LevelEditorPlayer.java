package com.megamal.game.model;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.megamal.framework.util.Painter;
import com.megamal.framework.util.Tile;
import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 13/03/16.
 */
public class LevelEditorPlayer {

    //Bounding rect leeways in order to check collisions for area
    //less than the size of Mawi
    private final static int X_RECT_LEEWAY = 15;
    private final static int Y_RECT_LEEWAY = 30;

    private final static double WALKING_SPEED = 100;

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


    private double cameraOffsetX  = 0;
    private double cameraOffsetY = 0;

    //variables for the position of scanLines
    private double xScanLineADown, xScanLineBDown, yScanLineAAcross, yScanLineBAcross;
    private int scanADown, scanBDown;
    private int xStartAcross, xEndAcross;
    private int yFloor;

    //velY for jumping, velX for walking/running
    private double velY = 0, velX = 0;
    private boolean isWalking = false;

    private boolean hasMoved = true;
    private boolean horizontalCollision = false;
    private boolean verticalCollision = false;
    private boolean left, right = false;
    private boolean up, down = false;

    //DuckRect too, when implementing
    private Rect playerRect;

    //variable for tiles when checking scan lines
    private Tile tileA, tileB;
    private boolean isWalkingUp = false;

    public LevelEditorPlayer(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;

        previousX = 0;
        previousY = 0;

        this.width = width;
        this.height = height;


        //initialise tile in order to check tile(s) underneath Mawi
        tileA = new Tile(0);
        tileB = new Tile(0);
        //collectable = new Collectable(1, 0, 0, 0, 0);

        //setting rect smaller than the actual character, in order to give leeway during fighting
        //(better to avoid getting hit, then hit unnecessarily
        playerRect = new Rect((int) x + X_RECT_LEEWAY, (int) y + Y_RECT_LEEWAY,
                (int) x + (width - X_RECT_LEEWAY), (int) y + (height - Y_RECT_LEEWAY));

    }

    public void update(float delta, int[][] map) {

        checkYMovement(map);
        checkXMovement(map);

        //Log.d("Location", "Previous x: " + x + ". Previous y:" + y + ".\t x = " + x + ". y = " + y + ".\n");


        previousX = x;
        previousY = y;

        //if mawi is not in collision with an Object & is walking/running then update x appropriately
        if (isWalking) {
            if (!horizontalCollision) {
                x += velX * delta;

                if (x < 0) {
                    x = 0;
                } else if ((x + width > GameMainActivity.GAME_WIDTH)) {
                    x = GameMainActivity.GAME_WIDTH - width;
                }
            }

            if (!verticalCollision) {
                y += velY * delta;

                if (y < 0) {
                    y = 0;
                }

                else if ((y + height) > GameMainActivity.GAME_HEIGHT) {
                    y = GameMainActivity.GAME_HEIGHT - height;
                }
            }

        } else {
            velX = 0;
            right = false;
            left = false;

            velY = 0;
            up = false;
            down = false;
        }

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
                if (horizontalCollision) {
                    Assets.walkHitAnimR.update(delta);
                }
                else
                    Assets.walkAnimR.update(delta);
            } else
                Assets.runAnimR.update(delta);
        } else if (left) {
            if (isWalking) {
                if (horizontalCollision) {
                    Assets.walkHitAnimL.update(delta);
                }
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
    private void checkYMovement(int[][] map) {

        //i.e if has moved in Y direction
        if (previousY != y || verticalCollision) {

            verticalCollision = false;

            xScanLineADown = x + SCAN_A_DOWN;
            xScanLineBDown = x + SCAN_B_DOWN;

            //convert into tile co-ordinates
            scanADown = (int) Math.floor(xScanLineADown / GameMainActivity.TILE_HEIGHT);
            scanBDown = (int) Math.floor(xScanLineBDown / GameMainActivity.TILE_HEIGHT);

            //boundary conditions - these will cause an arrayIndexOutOfBounds Exception
            if (scanADown < 0 || scanADown >= map[0].length || scanBDown < 0 || scanBDown >= map[0].length)
                return;

            //this means charatcer is currently Moving up first get map[Y] value then see if obstacles
            //if obstacles then charatcer has hit head
            if (velY < 0) {
                yFloor = (int) Math.floor((y) / GameMainActivity.TILE_HEIGHT);

                //boundary conditions
                if (yFloor < 0 || yFloor >= map.length) {
                    return;
                }

                tileA.setID(map[yFloor][scanADown]);
                tileB.setID(map[yFloor][scanBDown]);

                //check if either is obstacle, if so set y and change velY
                if (tileA.isObstacle()) {

                    tileA.setLocation(yFloor, scanADown, cameraOffsetX, cameraOffsetY);
                    yObstacleCollision(tileA, yFloor, scanADown);

                   /* y = tileA.getY() + GameMainActivity.TILE_HEIGHT;
                    velY = Math.abs(velY) / 5; */

                }

                if (tileB.isObstacle()) {

                    tileB.setLocation(yFloor, scanBDown, cameraOffsetX, cameraOffsetY);
                    yObstacleCollision(tileB, yFloor, scanBDown);

                    /*y = tileB.getY() + GameMainActivity.TILE_HEIGHT;
                    velY = Math.abs(velY) / 5;*/

                }

                return;
            }
            //else, character is not jumping so check tiles beneath
            else {

                //get map[Y] value!
                yFloor = (int) (y + GameMainActivity.PLAYER_HEIGHT + 2);
                yFloor = (int) Math.floor(yFloor / GameMainActivity.TILE_HEIGHT);

                //do something if character has somehow fallen to bottom of map
                if (yFloor < 0) {
                    return;
                }


                //this
                if (yFloor >= map.length) {
                    return;
                }

                //do something with this info about player being outside screen (indexOutOfBounds)
                if (scanBDown >= map[0].length || scanBDown < 0 || scanADown < 0 || scanADown >= map[0].length) {
                    return;
                }

                //check both of the tiles beneath mawi's feet
                tileA.setID(map[yFloor][scanADown]);
                tileB.setID(map[yFloor][scanBDown]);

                //Log.d("Grounded", "map[" + yFloor + "][" + scanADown + "]" + "and map[" + yFloor + "][" + scanADown + "]" +
                //   " checked!");

                //if both are obstacles
                if (tileA.isObstacle()) {

                    tileA.setLocation(yFloor, scanADown, cameraOffsetX, cameraOffsetY);
                    yObstacleCollision(tileA, yFloor, scanADown);
                    return;
                }

                else if (tileB.isObstacle()) {

                    tileB.setLocation(yFloor, scanBDown, cameraOffsetX, cameraOffsetY);
                    yObstacleCollision(tileB, yFloor, scanBDown);
                    return;
                }
            }
        }

        verticalCollision = false;
    }

    private void yObstacleCollision(Tile tile, int yIndex, int xIndex) {
        tile.setLocation(yIndex, xIndex, cameraOffsetX, cameraOffsetY);


        //travelling downwards
        if(velY > 0) {
            y = (tile.getY() - CLOSENESS_TO_OBSTACLE - height);
        }

        //travelling upwards
        else {
            y = (tile.getY() + CLOSENESS_TO_OBSTACLE + height);
        }

        verticalCollision = true;
        return;

    }

    //method to put mawi's y to be perfectly above tile after just being grounded
    //Render tiles below to avoid overlay
    private void allignMawiY(double cameraOffsetX, double cameraOffsetY) {
        if ((int) (y + cameraOffsetY) % GameMainActivity.TILE_HEIGHT != 0) {
            if (tileA.isObstacle()) {
                tileA.setLocation(yFloor, scanADown, cameraOffsetX, cameraOffsetY);
                y = (tileA.getY() - height);
                //Log.d("Grounded", "TileA.isObstacle - y set to: " + y);
            } else {
                tileB.setLocation(yFloor, scanBDown, cameraOffsetX, cameraOffsetY);
                y = tileB.getY() - height;
                //Log.d("Grounded", "TileB.isObstacle - y set to: " + y);
            }
        }
    }


    //method to check mawi's closeness to an object, and pop out if relevant (using the scanLinesAcross)
    private void checkXMovement(int[][] map) {

        if (hasMoved() || horizontalCollision) {

            yScanLineAAcross = y + SCAN_A_ACROSS + cameraOffsetY;
            yScanLineBAcross = y + SCAN_B_ACROSS + cameraOffsetY;

            xStartAcross = (int) ((x - CLOSENESS_TO_OBSTACLE - 1) + cameraOffsetX);
            xEndAcross = (int) ((x + CLOSENESS_TO_OBSTACLE + GameMainActivity.TILE_WIDTH + 1) + cameraOffsetX);

            horizontalCollision = false;

            //boolean values so that if mawi is half off screen, necessary bottom tiles are checked for collisions
            boolean aValid = true;
            boolean bValid = true;
            boolean endValid = true;
            boolean startValid = true;

            int scanAAcrossY = (int) Math.floor(yScanLineAAcross / GameMainActivity.TILE_HEIGHT);
            if (scanAAcrossY < 0 || scanAAcrossY >= map.length) {
                aValid = false;
                //return;
            }

            int scanBAcrossY = (int) Math.floor(yScanLineBAcross / GameMainActivity.TILE_HEIGHT);
            if (scanBAcrossY < 0 || scanBAcrossY >= map.length) {
                bValid = false;
                //return;
            }

            //variables for tile co-ordinates in relevance to scanLine variables
            int scanEndAcrossX = (int) Math.floor(xEndAcross / GameMainActivity.TILE_HEIGHT);
            if (scanEndAcrossX < 0 || scanEndAcrossX >= map[0].length) {
                endValid = false;
            }


            int scanStartAcrossX = (int) Math.floor(xStartAcross / GameMainActivity.TILE_HEIGHT);
            if (scanStartAcrossX < 0 || scanStartAcrossX >= map[0].length) {
                startValid = false;
                //return;
            }

            //i.e if moving right, then check scan lines to the right
            if (right) {

                //set Tile ID appropriately from scanlines

                //if all valid, check both A and B
                if (endValid && aValid && bValid) {
                    tileA.setID(map[scanAAcrossY][scanEndAcrossX]);
                    tileB.setID(map[scanBAcrossY][scanEndAcrossX]);


                    //check the first scan line and the tile to the right, if obstacle; set location of tile,
                    // and set new x from this location
                    if (tileA.isObstacle()) {
                        xObstacleCollision(tileA, scanAAcrossY, scanEndAcrossX, cameraOffsetX, cameraOffsetY, RIGHT);
                        //Log.d("CollisionsX", "Case 1!");
                        return;

                        //else, check the second scan line and the tile to the right, if obstacle set new x
                    } else if (tileB.isObstacle()) {
                        xObstacleCollision(tileB, scanBAcrossY, scanEndAcrossX, cameraOffsetX, cameraOffsetY, RIGHT);
                        //Log.d("CollisionsX", "Case 2!");
                        return;

                    }
                }

                //else if only endValid and aValid, check one tile
                else if (endValid && aValid) {
                    tileA.setID(map[scanAAcrossY][scanEndAcrossX]);

                    if (tileA.isObstacle()) {
                        xObstacleCollision(tileA, scanAAcrossY, scanEndAcrossX, cameraOffsetX, cameraOffsetY, RIGHT);
                        return;
                    }
                }

                //else if only endValid and bValid, only check one tile
                else if (endValid && bValid) {
                    tileA.setID(map[scanBAcrossY][scanEndAcrossX]);

                    if (tileA.isObstacle()) {
                        xObstacleCollision(tileA, scanBAcrossY, scanEndAcrossX, cameraOffsetX, cameraOffsetY, RIGHT);
                        return;
                    }
                }
            }

            //now check if there are any obstacles to the left, starting with scanLineA,
            //if on the left, then must set x to previous X instead of tile's x.
            else if (left) {

                //if all valid, then check all
                if (aValid && startValid && bValid) {
                    tileA.setID(map[scanAAcrossY][scanStartAcrossX]);
                    tileB.setID(map[scanBAcrossY][scanStartAcrossX]);

                    //Log.d("CollisionsX", "3: map[" + scanAAcrossY + "][" + scanStartAcrossX + "] checked");

                    if (tileA.isObstacle()) {
                        xObstacleCollision(tileA, scanAAcrossY, scanStartAcrossX, cameraOffsetX, cameraOffsetY, LEFT);
                        return;

                    } else if (tileB.isObstacle()) {
                        //Log.d("CollisionsX", "4: map[" + scanBAcrossY + "][" + scanStartAcrossX + "] checked");
                        xObstacleCollision(tileB, scanBAcrossY, scanStartAcrossX, cameraOffsetX, cameraOffsetY, LEFT);
                        return;


                        //not Mutually exclusive, so must check both
                    }
                }

                //if only aValid and startValid, check only apprpiate tile
                else if (aValid && startValid) {
                    tileA.setID(map[scanAAcrossY][scanStartAcrossX]);

                    if (tileA.isObstacle()) {
                        xObstacleCollision(tileA, scanAAcrossY, scanStartAcrossX, cameraOffsetX, cameraOffsetY, LEFT);
                        return;

                    }

                }


                //if only bValid and startValid, check only the appropriate tile
                else if (bValid && startValid) {
                    tileA.setID(map[scanBAcrossY][scanStartAcrossX]);

                    if (tileA.isObstacle()) {
                        xObstacleCollision(tileA, scanBAcrossY, scanStartAcrossX, cameraOffsetX, cameraOffsetY, LEFT);
                        return;

                    }

                }


                //else, case when moving but not left or right i.e justGrounded
            }
        }

        horizontalCollision = false;
    }

    private boolean hasMoved() {

        if(previousX != x) {
            return true;
        }

        else if(previousY != y) {
            return true;
        }

        return false;
    }

    private void xObstacleCollision(Tile tile, int yIndex, int xIndex, double cameraOffsetX, double cameraOffsetY, int direction) {
        tile.setLocation(yIndex, xIndex, cameraOffsetX, cameraOffsetY);


        if (direction == LEFT) {
            x = (tile.getX() + CLOSENESS_TO_OBSTACLE + GameMainActivity.TILE_WIDTH);
            Log.d("EdgeBug", "MAWI MOVED LEFT");
        } else if (direction == RIGHT) {
            x = (tile.getX() - CLOSENESS_TO_OBSTACLE - GameMainActivity.TILE_WIDTH);
            Log.d("EdgeBug", "MAWI MOVED RIGHT");
        }

        horizontalCollision = true;
        return;

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

    public void walkUp() {
        velY = -WALKING_SPEED;
        up = true;
        down = false;
        isWalkingUp = true;
    }

    public void walkDown() {
        velY = WALKING_SPEED;
        up = true;
        down = false;
        isWalkingUp = true;
    }

    public void stopWalking() {
        velX = 0;
        isWalking = false;
    }

    public void stopWalkingVert() {
        velY = 0;
        isWalkingUp = false;
    }



    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void clearAreaAround(Painter g) {
        g.setColor(Color.rgb(80, 143, 240));
        g.fillRect((int) x, (int) y, width, height);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public double getVelY() {
        return velY;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public boolean isCollided() {
        return horizontalCollision;
    }

    //if not right, then left, not sure if necessary
    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }


}



