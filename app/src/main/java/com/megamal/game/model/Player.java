package com.megamal.game.model;

import android.graphics.Rect;

import com.megamal.framework.util.Painter;
import com.megamal.framework.util.Tile;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 14/10/15.
 */
public class Player {

    //Bounding rect leeways in order to check collisions for area
    //less than the size of Mawi
    private final static int X_RECT_LEEWAY = 15;
    private final static int Y_RECT_LEEWAY = 30;

    //Constant for gravity
    private final static double ACCEL_GRAVITY = 9.8119;

    //Scan Lines co-ordinates -  must be added to mawi.X
    private final static int SCAN_A_DOWN = 15;
    private final static int SCAN_B_DOWN = 49;

    //Scan line co-ordinate - must be 'taken away' from mawi.Y
    private final static int SCAN_A_ACROSS = 32;
    private final static int SCAN_B_ACROSS = 96;

    //Constant to 'minus' from the y co-ordinate of mawi, in order to make her
    //step above the ground
    private static final int ABOVE_FLOOR_CONST = 25;

    //constant for the popping Mawi away from an obstacle if too close
    private static final int CLOSENESS_TO_OBSTACLE = 5;

    private float x, y;
    private int width, height;


    //variables for the position of scanLines
    private double xScanLineADown, xScanLineBDown, yScanLineAAcross, yScanLineBAcross;
    private int scanADown, scanBDown;
    private int xStartAcross, xEndAcross;
    private int yFloor;

    //velY for jumping, velX for walking/running
    private double velY = 0;

    private boolean isGrounded = true;
    private boolean isAlive = true;

    //DuckRect too, when implementing
    private Rect rect;

    //variable for tiles when checking scan lines
    private Tile tileA, tileB;

    public Player(float x, float y, int width, int height) {
        this.x = x;
        this.y = y - ABOVE_FLOOR_CONST;
        this.width = width;
        this.height = height;

        //initialise the scanLines for checking underneath Mawi
        xScanLineADown = x + SCAN_A_DOWN;
        xScanLineBDown = x + SCAN_B_DOWN;

        yScanLineAAcross = y - SCAN_A_ACROSS;
        yScanLineBAcross = y - SCAN_B_ACROSS;

        //initialise two tiles in order to check tile(s) underneath Mawi
        tileA = new Tile(0);
        tileB = new Tile(0);

        //setting rect smaller than the actual character, in order to give leeway during fighting
        //(better to avoid getting hit, then hit unnecessarily
        rect = new Rect((int) x + X_RECT_LEEWAY, (int) y + Y_RECT_LEEWAY,
                        (int) x + (width - X_RECT_LEEWAY), (int) y + (height - Y_RECT_LEEWAY));

    }

    public void update(float delta) {

        //update Scan Lines appropriately
        xScanLineADown = x + SCAN_A_DOWN;
        xScanLineBDown = x + SCAN_B_DOWN;

        yScanLineAAcross = y + SCAN_A_ACROSS;
        yScanLineBAcross = y + SCAN_B_ACROSS;

        //variables for the x co-ordinate of the scanLines to check obstacles
        //one to the left of mawi and one to the right
        xStartAcross = (int) x - CLOSENESS_TO_OBSTACLE;
        xEndAcross = (int) x + CLOSENESS_TO_OBSTACLE + GameMainActivity.TILE_HEIGHT;

        if (!isGrounded) {
            velY = velY + ACCEL_GRAVITY;
            updateRects();
        }
        else {
            velY = 0;
        }

        y += velY * delta;


    }

    private void updateRects() {
        rect.set((int) x + X_RECT_LEEWAY, (int) y + Y_RECT_LEEWAY,
                (int) x + (width - X_RECT_LEEWAY), (int) y + (height - Y_RECT_LEEWAY));
    }



    //Uses two scanLines located at x + 15 & x + 49 to check what tile(s) are under mawi;
    //this is then used to establish whether mawi is grounded or not
    public void checkGrounded(int[][] map) {

        //get map[Y] value!
        yFloor = (int) y + GameMainActivity.PLAYER_HEIGHT;
        yFloor = (int) Math.ceil(yFloor / GameMainActivity.TILE_HEIGHT);

        //convert into tile co-ordinates
        scanADown = (int) Math.floor(xScanLineADown / GameMainActivity.TILE_WIDTH);

        tileA.setID(map[yFloor][scanADown]);
        if(tileA.isObstacle()) {
            isGrounded = true;
            return;
        } else {
            scanBDown = (int) Math.floor(xScanLineBDown / GameMainActivity.TILE_HEIGHT);
            tileB.setID(map[yFloor][scanBDown]);
            if (!tileB.isObstacle()) {
                isGrounded = false;
            } else
                isGrounded = true;
        }
    }

    //can be improved once walking starts - get which way the character is facing,
    //and then only check those necessary tiles i.e to the right or the left
    public void checkCloseness(int[][] map) {

        if (isGrounded)
        {
            //check if moved also in order to not unnecessarily carry out calculations - EFFICIENCY!

            //variables for tile co-ordinates in relevance to scanLine variables
            int scanEndAcrossX = (int) Math.floor(xEndAcross / GameMainActivity.TILE_HEIGHT);
            int scanAAcrossY = (int) Math.ceil(yScanLineAAcross / GameMainActivity.TILE_WIDTH);

            //set Tile ID appropriately from scanlines
            tileA.setID(map[scanAAcrossY][scanEndAcrossX]);

            //check the first scan line and the tile to the right, if obstacle; set location of tile,
            // and set new x from this location
            if (tileA.isObstacle()) {
                tileA.setLocation(scanAAcrossY, scanEndAcrossX);
                x = tileA.getX() - CLOSENESS_TO_OBSTACLE - GameMainActivity.TILE_HEIGHT;
                System.out.println("Case 1!");

                //else, check the second scan line and the tile to the right, if obstacle set new x
            } else {
                int scanBAcrossY = (int) Math.floor(yScanLineBAcross / GameMainActivity.TILE_HEIGHT);
                tileA.setID(map[scanBAcrossY][scanEndAcrossX]);

                if (tileA.isObstacle()) {
                    tileA.setLocation(scanBAcrossY,scanEndAcrossX);
                    x = tileA.getX() - CLOSENESS_TO_OBSTACLE - GameMainActivity.TILE_HEIGHT;
                    System.out.println("Case 2!");


                    //now check if there are any obstacles to the left, starting with scanLineA
                } else {
                    int scanStartAcrossX = (int) Math.ceil(xStartAcross / GameMainActivity.TILE_HEIGHT);
                    tileA.setID(map[scanAAcrossY][scanStartAcrossX]);

                    if (tileA.isObstacle()) {
                        tileA.setLocation(scanAAcrossY, scanStartAcrossX);
                        x = tileA.getX() + CLOSENESS_TO_OBSTACLE;
                        System.out.println("Case 3!");

                        //check obstacles to the left on scanLineB
                    } else {
                        tileA.setID(map[scanBAcrossY][scanStartAcrossX]);

                        if (tileA.isObstacle()) {
                            tileA.setLocation(scanBAcrossY, scanStartAcrossX);
                            x = tileA.getX() + CLOSENESS_TO_OBSTACLE;
                            System.out.println("Case 4!");
                        }

                    }

                }


            }
        }


    }
    /*
    //method to get the top left X and Y of mawi when you have the tile you want her to be placed on
    public void setLocationFromTile(Tile tile) {
        float x = tile.getX();
        float y = tile.getY();

        this.x = x + GameMainActivity.PLAYER_WIDTH;
        this.y = y - GameMainActivity.PLAYER_HEIGHT;


    }      */

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

    public boolean isAlive() {
        return isAlive;
    }

}
