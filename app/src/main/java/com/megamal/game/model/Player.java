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

    //Scan Lines which must be added to mawi.X.
    private final static int SCAN_A = 15;
    private final static int SCAN_B = 49;

    //Constant to 'minus' from the y co-ordinate of mawi, in order to make her
    //step above the ground
    private static final int ABOVE_FLOOR_CONST = 20;

    private float x, y;
    private double xScanLineA, xScanLineB;
    private int scanA, scanB;

    private double velY = 0;

    //velY for jumping, velX for walking/running
    private int width, height;
    private boolean isGrounded = true;
    private boolean isAlive = true;

    private int yFloor;

    //DuckRect too, when implementing
    private Rect rect;
    private Tile tileA, tileB;

    public Player(float x, float y, int width, int height) {
        this.x = x;
        this.y = y - ABOVE_FLOOR_CONST;
        this.width = width;
        this.height = height;

        //initialise the scanLines for checking underneath Mawi
        xScanLineA = x + SCAN_A;
        xScanLineB = x + SCAN_B;

        //initialise two tiles in order to check tile(s) underneath Mawi
        tileA = new Tile(0);
        tileB = new Tile(0);

        //setting rect smaller than the actual character, in order to give leeway during fighting
        //(better to avoid getting hit, then hit unnecessarily
        rect = new Rect((int) x + X_RECT_LEEWAY, (int) y + Y_RECT_LEEWAY,
                        (int) x + (width - X_RECT_LEEWAY), (int) y + (height - Y_RECT_LEEWAY));

    }

    public void update(float delta) {
        if (!isGrounded) {
            velY = velY + ACCEL_GRAVITY;
            updateRect();
        }
        else {
            velY = 0;
        }

        y += velY * delta;


    }

    private void updateRect() {
        rect.set((int) x + X_RECT_LEEWAY, (int) y + Y_RECT_LEEWAY,
                (int) x + (width - X_RECT_LEEWAY), (int) y + (height - Y_RECT_LEEWAY));
    }


    //check whether the tiles underNeath mawi are obstacles;
    //if both are NOT, then mawi is not grounded and should consequently fall

    //CHANGE TO HAVE TWO SCANLINES at mawi.getX() + , & mawi.getX() + 55
    //use these to check what is underneath mawi & then decide if she is grounded or not

    //Uses two scanLines located at x + 15 & x + 49 to check what tile(s) are under mawi;
    //this is then used to establish whether mawi is grounded or not
    public void checkGrounded(int[][] map) {

        //get map[Y] value! (FLOOR???)
        yFloor = (int) y + GameMainActivity.PLAYER_HEIGHT;
        yFloor = (int) Math.floor(yFloor / GameMainActivity.TILE_HEIGHT);

        scanA = (int) Math.floor(xScanLineA / GameMainActivity.TILE_WIDTH);

        tileA.setID(map[yFloor][scanA]);
        if(tileA.isObstacle()) {
            isGrounded = true;
            return;
        } else {
            scanB = (int) Math.floor(xScanLineB / GameMainActivity.TILE_HEIGHT);
            tileB.setID(map[yFloor][scanB]);
            if (!tileB.isObstacle()) {
                isGrounded = false;
            } else
                isGrounded = true;
        }
    }

    //method to get the top left X and Y of mawi when you have the tile you want her to be placed on
    //Useful???
    public void setLocationFromTile(Tile tile) {
        float x = tile.getX();
        float y = tile.getY();

        this.x = x + GameMainActivity.PLAYER_WIDTH;
        this.y = y - GameMainActivity.PLAYER_HEIGHT;


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

    public boolean isAlive() {
        return isAlive;
    }
}
