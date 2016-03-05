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

    protected final static int WIDTH = 16;
    private final static int HEIGHT = 16;
    private final static int SCAN_LEEWAY_Y = 7;
    private final static int SCAN_LEEWAY_X = 7;
    private final static int RECT_LEEWAY_X = 20;
    private final static int RECT_LEEWAY_Y = 20;
    private final static int RECT_LEEWAY_X_UPDATE = 2;
    private final static int RECT_LEEWAY_Y_UPDATE = 2;
    private final static int VISIBILITY_THRESHOLD = 500;
    private final static int COLLISION_THRESHOLD_X = 50;
    private final static int COLLISION_THRESHOLD_Y = 50;
    private final static int DYING_VELOCITY = 6;

    private final static int VEL_X = 240;
    private final static int VEL_Y = 140;
    private final static int MAX_VEL_DOWN = 200;
    private final static int MAX_VEL_UP = 200;
    private final static int ACCEL_GRAVITY = 250;
    private final static int BOUNCING_VEL = -150;

    //Max time is twenty seconds
    private final static int MAX_TIME = 20;

    private final static int LEFT = -1;
    private final static int RIGHT = 1;

    protected Rect rect;
    private Bitmap image;
    private Tile tileA, tileB;

    private int width, height;
    private int scanLineXa, scanLineXb;
    private int scanLineYa, scanLineYb;
    private int velY, velX;
    private double x, y;
    private double closenessToTile;

    private double rectX, rectY;
    private int activationTime;

    private boolean isVisible = false;
    private boolean isActive = false;
    private boolean isPlayers;
    private boolean safeToRemove = false;
    private boolean recentlyRemoved = false;
    private boolean isDying = false;

    //STUB VALUES
    private double previousX = -50;
    private double previousY = -50;

    public Projectile(double x, double y, boolean isPlayers, int ID, double cameraOffsetX,
                      double cameraOffsetY, int direction) {

        this.x = x;
        this.y = y;

        this.isPlayers = isPlayers;

        tileA = new Tile(0);
        tileB = new Tile(-1);

        rect = new Rect();
        updateRects(cameraOffsetX, cameraOffsetY);

        width = WIDTH;
        height = HEIGHT;

        if(direction == LEFT) {
            velX = -(VEL_X);
        }

        else if (direction == RIGHT) {
            velX = VEL_X;
        }

        else {
            velX = VEL_X;
        }


        velY = VEL_Y;

        switch(ID) {
            case(1): {
                this.image = Assets.coinImage;
                break;
            }
            default: {
                this.image = Assets.coinImage;
                break;
            }
        }


        if(isVisible(cameraOffsetX, cameraOffsetY)) {
            isVisible = true;
        }

        isActive = true;

        activationTime = (int) System.currentTimeMillis();
    }

    public void reset(double x, double y, boolean isPlayers, int ID, double cameraOffsetX,
                      double cameraOffsetY, int direction) {
        this.x = x;
        this.y = y;
        this.isPlayers = isPlayers;

        updateRects(cameraOffsetX, cameraOffsetY);

        if(direction == LEFT) {
            velX = -(VEL_X);
        }

        else if (direction == RIGHT) {
            velX = VEL_X;
        }

        else {
            velX = VEL_X;
        }


        velY = VEL_Y;


        switch(ID) {
            case(1): this.image = Assets.coinImage;
                break;
        }


        isVisible = true;
        isActive = true;
        isDying = false;

        activationTime = (int) (System.currentTimeMillis());
    }

    protected void updateRects(double cameraOffsetX, double cameraOffsetY) {
        rectX = (x + RECT_LEEWAY_X_UPDATE - cameraOffsetX);
        rectY = (y + RECT_LEEWAY_Y_UPDATE - cameraOffsetY);

        rect.set((int) rectX, (int) rectY, (int) rectX + (width + RECT_LEEWAY_X_UPDATE),
                (int) rectY + (height + RECT_LEEWAY_Y_UPDATE));

    }


    public void update(float delta, int[][] map, double cameraOffsetX, double cameraOffsetY,
                       Player mawi, Painter g, Enemy[] enemyArray) {

        if(!isActive) {
            return;
        }

        else if(isDying) {
            y += DYING_VELOCITY;

            Log.d("Projectile Intersection", "visible, do not remove");

            if(!isVisible(cameraOffsetX, cameraOffsetY)) {
                Log.d("Projectile Intersection", "Not visible, remove");
                clearAreaAround(g, cameraOffsetX, cameraOffsetY);
                isActive = false;
            }
        }


        else if(((int) System.currentTimeMillis() - activationTime) / 1000 > MAX_TIME) {

            //Render map? - ensure norendeirng take aways
            clearAreaAround(g, cameraOffsetX, cameraOffsetY);
            isActive = false;
            return;
        }

        else {

            if(recentlyRemoved) {
                clearAreaAround(g, cameraOffsetX, cameraOffsetY, previousX, previousY);
                recentlyRemoved = false;
            }

            x += velX * delta;

            checkXMovement(map, g, cameraOffsetX, cameraOffsetY);


            if (velY > 0) {
                if (velY < MAX_VEL_DOWN) {
                    velY += ACCEL_GRAVITY * delta;
                }
            }
            else {
                velY += ACCEL_GRAVITY * delta;
            }

            //velY += ACCEL_GRAVITY * delta;
            y += velY * delta;
            checkYMovement(map, g, cameraOffsetX, cameraOffsetY);

            updateRects(cameraOffsetX, cameraOffsetY);

            if (isPlayers) {
                checkCollisionsEnemies(enemyArray);
            }
            else {
                checkCollisionsPlayer(mawi);
            }


        }

    }

    public void render(Painter g, double cameraOffsetX, double cameraOffsetY) {

        if(isActive) {

            if (!isVisible(cameraOffsetX, cameraOffsetY)) {
                return;
            } else {
                g.drawImage(image, (int) (x - cameraOffsetX), (int) (y - cameraOffsetY), width, height);
            }

        } else {
            return;
        }
    }

    public void clearAreaAround(Painter g, double cameraOffsetX, double cameraOffsetY,
                                double x, double y) {

        g.setColor(Color.rgb(80, 143, 240));

        g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY),
                width, height);

    }

    public void clearAreaAround(Painter g, double cameraOffsetX, double cameraOffsetY) {

        if(isActive) {
            if (isVisible) {
                g.setColor(Color.rgb(80, 143, 240));

                //rising
                if (velY < 0) {

                    //moving right
                    if (velX > 0) {
                        g.fillRect((int) (x - cameraOffsetX) - RECT_LEEWAY_X, (int) (y - cameraOffsetY),
                                width + RECT_LEEWAY_X, height + RECT_LEEWAY_Y);
                    }

                    //moving left
                    else {
                        g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY),
                                width + RECT_LEEWAY_X, height + RECT_LEEWAY_Y);
                    }
                }

                //falling
                else if (velY > 0) {

                    //moving right
                    if (velX > 0) {
                        g.fillRect((int) (x - cameraOffsetX) - RECT_LEEWAY_X, (int) (y - cameraOffsetY - RECT_LEEWAY_Y),
                                width + RECT_LEEWAY_X, height + RECT_LEEWAY_Y);
                    }

                    //moving left
                    else {
                        g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY - RECT_LEEWAY_Y),
                                width + RECT_LEEWAY_X, height + RECT_LEEWAY_Y);
                    }
                }
            }
        }
    }

    protected void checkCollisionsEnemies(Enemy[] enemyArray) {
        if(isPlayers) {
            for(int i = 0; i < enemyArray.length; i++) {

                if (enemyArray[i] != null && !enemyArray[i].isDying()) {
                    //if enemy is not active, move to next
                    if (enemyArray[i].isActive()) {

                        //close enough to check
                        if (Math.abs(enemyArray[i].getX() - x) < COLLISION_THRESHOLD_X &&
                                Math.abs(enemyArray[i].getY() - y) < COLLISION_THRESHOLD_Y) {

                            if (rect.intersect(enemyArray[i].getRect())) {
                                enemyArray[i].death();
                                death();
                                //Log.d("Intersection", "Rect CONTAINED
                                // close enough");
                            } else {
                                //Log.d("Intersection", "Rect not contained, but close enough");
                            }
                        } else {
                            Log.d("Intersection", "Not close enough!");
                        }
                    }

                    //else, enemy is dying / == null, return
                } else {
                    return;
                }
            }
        }

        else {
            return;
        }
    }

    protected void checkCollisionsPlayer(Player mawi) {
        if(isPlayers) {
            return;
        }

        else {
            //handle collisions
        }
    }

    protected void checkYMovement(int[][] map, Painter g, double cameraOffsetX, double cameraOffsetY) {

        boolean twoTiles = false;

        //in this case falling, so check suitable entries in map. If hit floor make bounce
        if(velY > 0) {
            closenessToTile = (y + height) % GameMainActivity.TILE_HEIGHT;

            if(closenessToTile >= 58) {

                scanLineYb = (int) Math.floor((y + height + SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);

                if (scanLineYb < 0 || scanLineYb >= map.length) {
                    return;
                }


                //moving left
                if (velX < 0) {

                    //in this case, then the projectile is inbetween two tiles, and two tiles
                    //will be needed
                    if((x % GameMainActivity.TILE_WIDTH) >= 56) {
                        scanLineXa = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
                        scanLineXb = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
                        twoTiles = true;
                    }

                    //else, very close and acceptable to view two tiles (must make them different
                    //that is why leeway is subtracted)
                    else if(x % GameMainActivity.TILE_WIDTH <= 2) {
                        scanLineXa = (int) Math.floor((x - SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
                        scanLineXb = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
                        twoTiles = true;
                    }

                    //else, projectile is more than halfway into one tile, so only one tile is
                    //necessary
                    else {
                        scanLineXa = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
                    }
                }

                //else, moving right
                else {

                    if((x + width) % GameMainActivity.TILE_WIDTH <= 8) {
                        scanLineXa = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
                        scanLineXb = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
                        twoTiles = true;
                    }

                    //else, very close and acceptable to view two tiles (must make them different
                    //that is why leeway is added)
                    else if((x + width) % GameMainActivity.TILE_WIDTH >= 62) {
                        scanLineXa = (int) Math.floor((x + width + SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
                        scanLineXb = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
                        twoTiles = true;
                    }

                    else {
                        scanLineXa = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
                    }
                }

                if (scanLineXa < 0 || scanLineXa >= map[0].length) {
                    Log.d("ProjectilesOOB", "Out of bounds (VelY > 0), scanLineXa");
                    return;
                }

                if (twoTiles && (scanLineXb < 0 || scanLineXb >= map[0].length)) {
                    Log.d("ProjectilesOOB", "Out of bounds (VelY > 0), scanLineXb");
                    return;
                }

                /*scanLineXb = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);

                if (scanLineXb < 0 || scanLineXb >= map[0].length) {
                    return;
                }*/


                tileA.setID(map[scanLineYb][scanLineXa]);

                if(twoTiles) {
                    tileB.setID(map[scanLineYb][scanLineXb]);
                    // tileB.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYb, scanLineXb);
                }

                // tileA.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYb, scanLineXa);

                //tileB.setID(map[scanLineYb][scanLineXb]);

                if(tileA.isObstacle() || (twoTiles && tileB.isObstacle())) {
                    // Log.d("Projectiles", "Is falling, Y movement");

                    //Log.d("ProjectilesCollision", "FALLING: Co-ordinates: " + (int) x + ", " + (int) y + ".");
                    //Log.d("ProjectilesCollision", "Checked map[" + scanLineYb + "][" + scanLineXa + ".");
                    //velY = -(velY);
                    velY = BOUNCING_VEL;
                    //y -= 2;
                }
            }

        }

        //in this case is rising
        else if (velY < 0) {
            closenessToTile = y % GameMainActivity.TILE_HEIGHT;

            if(closenessToTile <= 6) {

                //scan_leeway_y must be bigger than 4
                scanLineYa = (int) Math.floor((y - SCAN_LEEWAY_Y)/ GameMainActivity.TILE_HEIGHT);

                if (scanLineYa < 0 || scanLineYa >= map.length) {
                    return;
                }


                //moving left
                if (velX < 0) {


                    if(x % GameMainActivity.TILE_WIDTH >= 56) {
                        scanLineXa = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
                        scanLineXb = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
                        twoTiles = true;

                    }

                    //else, very close and acceptable to view two tiles (must make them different
                    //that is why leeway is subtracted)
                    else if (x % GameMainActivity.TILE_WIDTH <= 2) {
                        scanLineXa = (int) Math.floor((x - SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
                        scanLineXb = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
                        twoTiles = true;
                    }

                    else {
                        scanLineXa = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
                    }
                }

                //moving right
                else {

                    //inbetween two tiles (by at most 8 pixel)
                    if((x + width) % GameMainActivity.TILE_WIDTH <= 8) {
                        scanLineXa = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
                        scanLineXb = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
                        twoTiles = true;
                    }

                    //else, very close and acceptable to view two tiles (must make them different
                    //that is why leeway is added)
                    else if ((x + width) % GameMainActivity.TILE_WIDTH >= 62) {
                        scanLineXa = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
                        scanLineXb = (int) Math.floor((x + width + SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
                        twoTiles = true;
                    }

                    else {
                        scanLineXa = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
                    }
                }

                if (scanLineXa < 0 || scanLineXa >= map[0].length) {
                    Log.d("ProjectilesOOB", "Out of bounds (VelY < 0), scanLineXa");
                    return;
                }

                if (twoTiles && (scanLineXb < 0 || scanLineXb >= map[0].length)) {
                    Log.d("ProjectilesOOB", "Out of bounds (VelY < 0), scanLineXb");
                    return;
                }

               /* scanLineXb = (int) Math.floor((x + height) / GameMainActivity.TILE_WIDTH);

                if (scanLineXb < 0 || scanLineXb >= map[0].length) {
                    return;
                } */

                if(twoTiles) {
                    tileB.setID(map[scanLineYa][scanLineXb]);
                    //tileB.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYa, scanLineXb);
                }

                tileA.setID(map[scanLineYa][scanLineXa]);
                //tileB.setID(map[scanLineYa][scanLineXa]);
                //tileA.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYa, scanLineXa);

                // Log.d("RisingProj", "RISING - checking col!");

                if (tileA.isObstacle() || (twoTiles && tileB.isObstacle())) {

                   /* Log.d("RisingProj", "RISING! - is col");
                    Log.d("Projectiles", "Is rising, Y movement");
                    Log.d("ProjectilesCollision", "RISING: Co-ordinates: " + (int) x + ", " + (int) y + ".");
                    Log.d("ProjectilesCollision", "Checked map[" + scanLineYa + "][" + scanLineXa + "."); */
                    //velY = -(velY);
                    velY = -BOUNCING_VEL;
                    //y += 2;

                }
                //Log.d("RisingProj", "RISING! - is not col");
            }
        }

    }

    //method to check the movement of the projectile in the X dimension, if any collisions,
    //they are dealt with and the projectiles velocity is appropriately changed
    protected void checkXMovement(int[][] map, Painter g, double cameraOffsetX, double cameraOffsetY) {

        boolean twoTiles = false;

        //moving right, so check suitable entries into map
        if (velX > 0) {
            closenessToTile = (x + width) % GameMainActivity.TILE_WIDTH;


            //close enough to being at a tile location to check (4 or less pixels away)
            if (closenessToTile >= 58) {
                //Log.d("Projectiles", "Close enough");

                scanLineXb = (int) Math.floor((x + width + SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);

                if (scanLineXb < 0 || scanLineXb >= map[0].length) {
                    Log.d("Projectiles", "Exited swift, scanLine = " + scanLineXb);
                    return;
                }

                //travelling upwards
                if (velY < 0) {


                    if(y % GameMainActivity.TILE_HEIGHT >= 56) {
                        scanLineYa = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);
                        scanLineYb = (int) Math.floor((y + height) / GameMainActivity.TILE_HEIGHT);

                        twoTiles = true;
                    }

                    else if (y % GameMainActivity.TILE_HEIGHT <= 4) {
                        scanLineYa = (int) Math.floor((y - SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);
                        scanLineYb = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);
                        twoTiles = true;
                    }

                    else {
                        scanLineYa = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);
                    }
                }

                //travelling down
                else {

                    if((y + height) % GameMainActivity.TILE_HEIGHT <= 8) {
                        scanLineYa = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);
                        scanLineYb = (int) Math.floor((y + height) / GameMainActivity.TILE_HEIGHT);

                        twoTiles = true;

                    }

                    else if((y + height) % GameMainActivity.TILE_HEIGHT >= 60) {
                        scanLineYa = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);
                        scanLineYb = (int) Math.floor((y + height + SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);

                        twoTiles = true;

                    }

                    else {
                        scanLineYa = (int) Math.floor((y + height) / GameMainActivity.TILE_HEIGHT);
                    }
                }

                if (scanLineYa < 0 || scanLineYa >= map.length) {
                    Log.d("ProjectilesOOB", "Out of bounds (VelX > 0), scanLineYa");
                    return;
                }

                if (twoTiles && (scanLineYb < 0 || scanLineYb >= map.length)) {
                    Log.d("ProjectilesOOB", "Out of bounds (VelX > 0), scanLineYb");
                    return;
                }

               /* scanLineYb = (int) Math.floor(y + height / GameMainActivity.TILE_HEIGHT);

                if (scanLineYb < 0 || scanLineYb >= map.length) {
                    return;
                }*/


                tileA.setID(map[scanLineYa][scanLineXb]);
                //Log.d("Projectiles", "Map[" + scanLineYa + "][" + scanLineXb + "]");
                //tileA.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYa, scanLineXb);
                //tileB.setID(map[scanLineYb][scanLineXb]);

                if(twoTiles) {
                    tileB.setID(map[scanLineYb][scanLineXb]);
                    //tileB.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYb, scanLineXb);
                }



                if(tileA.isObstacle() || (twoTiles && tileB.isObstacle())) {
                    /*Log.d("Projectiles", "Is Right, X movement");
                    Log.d("ProjectilesCollision", "RIGHT: Co-ordinates: " + (int) x + ", " + (int) y + ".");
                    Log.d("ProjectilesCollision", "Checked map[" + scanLineYa + "][" + scanLineXb + "].");*/
                    velX = -(velX);
                    //x -= 2;
                }

            }
        }

        //moving left
        else {


            closenessToTile = (x % GameMainActivity.TILE_WIDTH);

            if (closenessToTile <= 6) {

                scanLineXa = (int) Math.floor((x - SCAN_LEEWAY_X) / GameMainActivity.TILE_WIDTH);

                if (scanLineXa < 0 || scanLineXa >= map[0].length) {
                    return;
                }

                //if falling
                if (velY < 0) {

                    if(y % GameMainActivity.TILE_HEIGHT >= 50) {
                        scanLineYa = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);
                        scanLineYb = (int) Math.floor((y + height) / GameMainActivity.TILE_HEIGHT);

                        twoTiles = true;
                    }

                    //CHANGED -
                   /* else if ((y + height) % GameMainActivity.TILE_HEIGHT >= 60) {
                        scanLineYa = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);
                        scanLineYb = (int) Math.floor((y + height + SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);

                        twoTiles = true;
                    } */

                    else {
                        scanLineYa = (int) Math.floor((y + height) / GameMainActivity.TILE_HEIGHT);
                    }
                }

                //else if rising
                else {

                    //true
                    if((y + height) % GameMainActivity.TILE_HEIGHT <= 14) {
                        scanLineYa = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);
                        scanLineYb = (int) Math.floor((y + height) / GameMainActivity.TILE_HEIGHT);

                        twoTiles = true;
                    }


                    else if (y % GameMainActivity.TILE_HEIGHT <= 4) {
                        scanLineYa = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);
                        scanLineYb = (int) Math.floor((y - SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);

                        twoTiles = true;
                    }

                    else {
                        scanLineYa = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);
                    }
                }

                if (scanLineYa < 0 || scanLineYa >= map.length) {
                    return;
                }

                if (twoTiles && (scanLineYb < 0 || scanLineYb >= map.length)) {
                    Log.d("ProjectilesOOB", "Out of bounds (VelX < 0), scanLineYb");
                    return;
                }

               /* scanLineYb = (int) Math.floor(y + height / GameMainActivity.TILE_HEIGHT);

                if (scanLineYb < 0 || scanLineYb >= map.length) {
                    return;
                } */

                tileA.setID(map[scanLineYa][scanLineXa]);

                //Log.d("Projectiles", "Map[" + scanLineYa + "][" + scanLineXa + "]");
                // tileA.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYa, scanLineXa);


                if(twoTiles) {
                    tileB.setID(map[scanLineYb][scanLineXa]);
                    //tileB.fillTile(g, cameraOffsetX, cameraOffsetY, scanLineYb, scanLineXa);
                }


                //tileB.setID(map[scanLineYb][scanLineXb]);

                if(tileA.isObstacle() || (twoTiles && tileB.isObstacle())) {
                    /*Log.d("ProjectilesCollision", "LEFT: Co-ordinates: " + (int) x + ", " + (int) y + ".");
                    Log.d("ProjectilesCollision", "Checked map[" + scanLineYa + "][" + scanLineXa + ".");
                    Log.d("Projectiles", "Is Left, X movement"); */
                    velX = -(velX);
                    //x += 2;
                }


            }
        }
    }


    //Method to determine whether the projectile is still in visibility range or not
    public boolean isVisible(double cameraOffsetX, double cameraOffsetY) {

        //MOVING RIGHT, check if x is still in bounds
        if ((velX > 0) && (x  - cameraOffsetX > 0) &&
                ((x - cameraOffsetX) <= GameMainActivity.GAME_WIDTH)) {

            //MOVING UP
            if (velY < 0 && (y + height - cameraOffsetY > 0) &&
                    (((y + height) - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT)) {

                isVisible = true;
            }

            //MOVING DOWN
            else if (velY > 0 && (y - cameraOffsetY) > 0 && (y - cameraOffsetY) <= GameMainActivity.GAME_HEIGHT){


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

            else {
                isVisible = false;
            }

        } else {
            isVisible = false;
        }


        if(velX > 0) {
            if ((x - cameraOffsetX - GameMainActivity.GAME_WIDTH) > VISIBILITY_THRESHOLD) {
                safeToRemove = true;
            }
        }


        return isVisible;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getActivationTime() {
        return activationTime;
    }

    private void death() {
        y -= 10;
        isDying = true;
    }


    public void makeNonActive() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isSafeToRemove() {
        return safeToRemove;
    }

    public boolean isFalling() {
        if (velY < 0) {
            return true;
        }

        else
            return false;
    }

    public void setPreviousX(double x) {
        previousX = x;
    }

    public void setPreviousY(double y) {
        previousY = y;
    }

    public void setRecentlyRemoved() {
        recentlyRemoved = true;
    }

    public boolean isDying() {
        return isDying;
    }

    public int getCurrentDirection() {
        if(velX < 0) {
            return LEFT;
        }

        else {
            return RIGHT;
        }
    }


    public Rect getRect() {
        return rect;
    }

    public int getVelX() {
        return velX;
    }

    public int getVelY() {
        return velY;
    }

    //method to force velY in order to sufficiently test
    protected void forceVelY(int velY) {
        this.velY = velY;
    }
}