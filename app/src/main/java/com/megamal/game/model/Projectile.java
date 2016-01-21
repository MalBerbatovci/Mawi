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
 * Created by malberbatovci on 19/01/16.
 */
public class Projectile {

    private final static int WIDTH = 32;
    private final static int HEIGHT = 32;
    private final static int SCAN_LEEWAY_Y = 2;
    private final static int SCAN_LEEWAY_X = 2;
    private final static int RECT_LEEWAY_X = 20;
    private final static int RECT_LEEWAY_Y = 20;
    private final static int RECT_LEEWAY_X_UPDATE = 2;
    private final static int RECT_LEEWAY_Y_UPDATE = 2;
    private final static int VISIBILITY_THRESHOLD = 500;

    private final static int VEL_X = 120;
    private final static int VEL_Y = 120;
    private final static int ACCEL_GRAVITY = 350;
    private final static int BOUNCING_VEL = -350;

    private Rect rect;
    private Bitmap image;
    private Tile tileA, tileB;

    private int width, height;
    private int scanLineXa, scanLineXb;
    private int scanLineYa, scanLineYb;
    private int velY, velX;
    private double x, y;
    private double closenessToTile;

    private double rectX, rectY;

    private boolean isVisible = false;
    private boolean isActive = false;
    private boolean isPlayers;
    private boolean safeToRemove = false;

    public Projectile(double x, double y, boolean isPlayers, int ID, double cameraOffsetX, double cameraOffsetY) {
        this.x = x;
        this.y = y;
        this.isPlayers = isPlayers;

        tileA = new Tile(0);
        tileB = new Tile(0);

        rect = new Rect();
        updateRects(cameraOffsetX, cameraOffsetY);

        width = WIDTH;
        height = HEIGHT;
        velX = VEL_X;
        velY = VEL_Y;

        switch(ID) {
            case(1): this.image = Assets.coinImage;
                     break;
        }

        isVisible = true;
        isActive = true;
    }

    public void reset(double x, double y, double cameraOffsetX, double cameraOffsetY, boolean isPlayers, int ID) {
        this.x = x;
        this.y = y;
        this.isPlayers = isPlayers;

        updateRects(cameraOffsetX, cameraOffsetY);

        switch(ID) {
            case(1): this.image = Assets.coinImage;
                break;
        }

        isVisible = true;
        isActive = true;
    }

    private void updateRects(double cameraOffsetX, double cameraOffsetY) {
        rectX = (x + RECT_LEEWAY_X_UPDATE - cameraOffsetX);
        rectY = (y + RECT_LEEWAY_Y_UPDATE - cameraOffsetY);

        rect.set((int) rectX, (int) rectY, (int) rectX + (width + RECT_LEEWAY_X_UPDATE),
                (int) rectY + (height + RECT_LEEWAY_Y_UPDATE));

    }


    public void update(float delta, int[][] map, double cameraOffsetX, double cameraOffsetY, Player mawi, Painter g) {

        if(!isActive) {

        }

        else {

            x += velX * delta;
            checkXMovement(map, g, cameraOffsetX, cameraOffsetY);


            velY += ACCEL_GRAVITY * delta;
            y += velY * delta;
            checkYMovement(map, g, cameraOffsetX, cameraOffsetY);

            updateRects(cameraOffsetX, cameraOffsetY);

            if (isPlayers) {
                checkCollisionsEnemies();
            }
            else {
                checkCollisionsPlayer(mawi);
            }


        }

    }

    public void render(Painter g, double cameraOffsetX, double cameraOffsetY) {
        if (!isVisible) {
            return;
        }

        else {
            g.drawImage(image, (int) (x - cameraOffsetX), (int) (y - cameraOffsetY), width, height);
        }


    }


    public void clearAreaAround(Painter g, double cameraOffsetX, double cameraOffsetY) {
        if (velY <= 0) {
            g.setColor(Color.rgb(208, 244, 247));
            g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY), width + RECT_LEEWAY_X, height + RECT_LEEWAY_Y);
        } else {
            g.setColor(Color.rgb(208, 244, 247));
            g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY) - RECT_LEEWAY_Y, width + RECT_LEEWAY_X, height + RECT_LEEWAY_Y);
        }


    }

    private void checkCollisionsEnemies() {
    }

    private void checkCollisionsPlayer(Player mawi) {

    }

    private void checkYMovement(int[][] map, Painter g, double cameraOffsetX, double cameraOffsetY) {

        //in this case falling, so check suitable entries in map. If hit floor make bounce
        if(velY > 0) {
            closenessToTile = (y + height) % GameMainActivity.TILE_HEIGHT;

            if(closenessToTile > 55) {

                scanLineYb = (int) Math.floor((y + height + SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);

                if (scanLineYb < 0 || scanLineYb >= map.length) {
                    return;
                }


                if (velX < 0) {
                    scanLineXa = (int) Math.floor((x - SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
                }

                else {
                    scanLineXa = (int) Math.floor((x + width + SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
                }

                if (scanLineXa < 0 || scanLineXa >= map[0].length) {
                    return;
                }

                /*scanLineXb = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);

                if (scanLineXb < 0 || scanLineXb >= map[0].length) {
                    return;
                }*/


                tileA.setID(map[scanLineYb][scanLineXa]);
                tileA.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYb, scanLineXa);

                //tileB.setID(map[scanLineYb][scanLineXb]);

                if(tileA.isObstacle()) {
                    Log.d("Projectiles", "Is falling, Y movement");

                    Log.d("ProjectilesCollision", "FALLING: Co-ordinates: " + (int) x + ", " + (int) y + ".");
                    Log.d("ProjectilesCollision", "Checked map[" + scanLineYb + "][" + scanLineXa + ".");
                    //velY = -(velY);
                    velY = BOUNCING_VEL;
                    y -= 5;
                }
            }

        }

        //in this case is rising
        else if (velY < 0) {
            closenessToTile = y % GameMainActivity.TILE_HEIGHT;

            if(closenessToTile < 10 ) {
                scanLineYa = (int) Math.ceil((y - SCAN_LEEWAY_Y)/ GameMainActivity.TILE_HEIGHT);

                if (scanLineYa < 0 || scanLineYa >= map.length) {
                    return;
                }

                if (velX < 0) {
                    scanLineXa = (int) Math.floor((x - SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
                }

                else {
                    scanLineXa = (int) Math.floor((x + width + SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
                }

                if (scanLineXa < 0 || scanLineXa >= map[0].length) {
                    return;
                }

               /* scanLineXb = (int) Math.floor((x + height) / GameMainActivity.TILE_WIDTH);

                if (scanLineXb < 0 || scanLineXb >= map[0].length) {
                    return;
                } */

                tileA.setID(map[scanLineYa][scanLineXa]);
                //tileB.setID(map[scanLineYa][scanLineXa]);
                tileA.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYa, scanLineXa);



                if (tileA.isObstacle()/* || tileB.isObstacle()*/) {
                    Log.d("Projectiles", "Is rising, Y movement");
                    Log.d("ProjectilesCollision", "RISING: Co-ordinates: " + (int) x + ", " + (int) y + ".");
                    Log.d("ProjectilesCollision", "Checked map[" + scanLineYa + "][" + scanLineXa + ".");
                    //velY = -(velY);
                    velY = -BOUNCING_VEL;
                    y += 5;

                }
            }
        }

    }

    private void checkXMovement(int[][] map, Painter g, double cameraOffsetX, double cameraOffsetY) {

        //moving right, so check suitable entries into map
        if (velX > 0) {
            closenessToTile = (x + width) % GameMainActivity.TILE_WIDTH;


            //close enough to being at a tile location to check
            if (closenessToTile > 55) {
                //Log.d("Projectiles", "Close enough");

                scanLineXb = (int) Math.floor((x + width + SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);

                if (scanLineXb < 0 || scanLineXb >= map[0].length) {
                    Log.d("Projectiles", "Exited swift, scanLine = " + scanLineXb);
                    return;
                }

                //travelling upwards
                if (velY < 0) {
                    scanLineYa = (int) Math.floor((y - SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);
                }

                else {
                    scanLineYa = (int) Math.floor((y + height + SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);
                }

                if (scanLineYa < 0 || scanLineYa >= map.length) {
                    Log.d("Projectiles", "Exited swifter, scanLine = " + scanLineYa);
                    return;
                }

               /* scanLineYb = (int) Math.floor(y + height / GameMainActivity.TILE_HEIGHT);

                if (scanLineYb < 0 || scanLineYb >= map.length) {
                    return;
                }*/


                tileA.setID(map[scanLineYa][scanLineXb]);
                Log.d("Projectiles", "Map[" + scanLineYa + "][" + scanLineXb + "]");
                tileA.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYa, scanLineXb);
                //tileB.setID(map[scanLineYb][scanLineXb]);




                if(tileA.isObstacle() /*|| tileB.isObstacle()*/) {
                    Log.d("Projectiles", "Is Right, X movement");
                    Log.d("ProjectilesCollision", "RIGHT: Co-ordinates: " + (int) x + ", " + (int) y + ".");
                    Log.d("ProjectilesCollision", "Checked map[" + scanLineYa + "][" + scanLineXb + "].");
                    velX = -(velX);
                    x -= 5;
                }

            }
        }

        else {

            closenessToTile = (x % GameMainActivity.GAME_WIDTH);

            if (closenessToTile < 10) {

                scanLineXa = (int) Math.floor((x - SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);

                if (scanLineXa < 0 || scanLineXa >= map[0].length) {
                    return;
                }

                if (velY < 0) {
                    scanLineYa = (int) Math.floor((y - SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);
                }

                else {
                    scanLineYa = (int) Math.floor((y + height + SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);
                }

                if (scanLineYa < 0 || scanLineYa >= map.length) {
                    return;
                }

               /* scanLineYb = (int) Math.floor(y + height / GameMainActivity.TILE_HEIGHT);

                if (scanLineYb < 0 || scanLineYb >= map.length) {
                    return;
                } */

                tileA.setID(map[scanLineYa][scanLineXa]);

                Log.d("Projectiles", "Map[" + scanLineYa + "][" + scanLineXa + "]");
                tileA.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYa, scanLineXa);


                //tileB.setID(map[scanLineYb][scanLineXb]);

                if(tileA.isObstacle() /* || tileB.isObstacle()*/) {
                    Log.d("ProjectilesCollision", "LEFT: Co-ordinates: " + (int) x + ", " + (int) y + ".");
                    Log.d("ProjectilesCollision", "Checked map[" + scanLineYa + "][" + scanLineXa + ".");
                    Log.d("Projectiles", "Is Left, X movement");
                    velX = -(velX);
                    x += 5;
                }


            }
        }
    }

    public void isVisible(double cameraOffsetX, double cameraOffsetY) {
        if ((velX > 0) && ((x + width) - cameraOffsetX > 0) && (((x + width) - cameraOffsetX) <= GameMainActivity.GAME_WIDTH)) {

            if (velY > 0 && (y + height - cameraOffsetY > 0) && (((y + height) - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT)) {

                if ((x - cameraOffsetX - GameMainActivity.GAME_WIDTH) > VISIBILITY_THRESHOLD) {
                    safeToRemove = true;
                }

                isVisible = true;
            }

            else if (velY < 0 && (y - cameraOffsetY) > 0 && (y - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT){

                if ((x - cameraOffsetX - GameMainActivity.GAME_WIDTH) > VISIBILITY_THRESHOLD) {
                    safeToRemove = true;
                }

                isVisible = true;
            }

            else {
                isVisible = false;
            }

        }


        else if ((velX < 0) && (x - cameraOffsetX) > 0 && (x - cameraOffsetX) <= GameMainActivity.GAME_WIDTH) {
            if (velY > 0 && (y + height - cameraOffsetY > 0) && (((y + height) - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT)) {

                if ((x - cameraOffsetX - GameMainActivity.GAME_WIDTH) > VISIBILITY_THRESHOLD) {
                    safeToRemove = true;
                }
                isVisible = true;
            }

            else if (velY < 0 && (y - cameraOffsetY) > 0 && (y - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT){

                if ((x - cameraOffsetX - GameMainActivity.GAME_WIDTH) > VISIBILITY_THRESHOLD) {
                    safeToRemove = true;
                }

                isVisible = true;
            }

            else {
                isVisible = false;
            }

        }

        isVisible =  false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isFalling() {
        if (velY < 0) {
            return true;
        }

        else
            return false;
    }


}
