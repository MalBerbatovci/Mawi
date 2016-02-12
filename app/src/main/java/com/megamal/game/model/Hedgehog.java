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
 * Created by malberbatovci on 11/01/16.
 */
public class Hedgehog extends Mover {

    private static final int SCAN_LEEWAY_Y = 10;
    private final static int ACCEL_GRAVITY = 282;
    private final static int DEATH_VELOCITY = -222;

    private Tile tileA;
    private Tile tileB;

    private Bitmap image;
    private Rect rect;

    private double x, y;
    private double rectX, rectY;
    private int width, height;
    private int velX, velY;
    private boolean isAlive = true;
    private boolean isActive = true;
    private boolean isGrounded = false;
    private boolean isDying = false;
    private boolean isDead = false;

    // if(isDying && !isActive) -> safe to remove

    public Hedgehog(double x, double y, double cameraOffsetX, double cameraOffsetY) {

        //will be given in 'true' form, must add on cameraOffsetX and cameraOffsetY to render correctly
        this.x = x;
        this.y = y;

        tileA = new Tile(0);
        tileB = new Tile(0);
        rect = new Rect();

        updateRects(cameraOffsetX, cameraOffsetY);

        this.width = GameMainActivity.TILE_WIDTH;
        this.height = GameMainActivity.TILE_HEIGHT;

        //change to respective image
        this.image = Assets.coinImage;

        velX = 100;
        velY = 100;

    }

    //Called on each iteraiton of the game loop, updates x and y given velX and velY, and also
    //checks the movement that the enemy will do, dealing with suitabl collisons in the suitable
    //methods
    @Override
    public void update(float delta, int[][] map, double cameraOffsetX, double cameraOffsetY, Player mawi) {

        if (!isActive) {
            //do what
        }

        else {
            if (isAlive) {
                x += delta * velX;

                if (!isGrounded) {
                    velY += ACCEL_GRAVITY * delta;
                    y += velY * delta;
                }

                checkXMovement(map);
                checkYMovement(map);

                updateRects(cameraOffsetX, cameraOffsetY);
                checkCollisions(mawi, cameraOffsetX, cameraOffsetY);

            }

            if (isDying) {
                velY += ACCEL_GRAVITY * delta;
                y += velY * delta;

                Log.d("EnemyVisiblity", "Y is: " + y + ". Camera offset is: " + cameraOffsetY);

                //Log.d("EnemyVisibility", "STILL VISIBLE");

                if (!isVisible(cameraOffsetX,cameraOffsetY,x,y,width,height)) {
                    Log.d("EnemyVisibility", "NOT VISIBLE");
                    isDead = true;
                    isDying = false;
                    isActive = false;
                }
            }

        }

    }


    public void updateRects(double cameraOffsetX, double cameraOffsetY) {

        //Check playerX against enemy x
        if (isVisible(cameraOffsetX, cameraOffsetY, x, y, width, height)) {

           // Log.d("Intersection rect", "updated)");
            rectX = (x + RECT_LEEWAY_X - cameraOffsetX);
            rectY = (y + RECT_LEEWAY_Y - cameraOffsetY);

            rect.set((int) rectX, (int) rectY, (int) rectX + (width + RECT_LEEWAY_X),
                    (int) rectY + (height + RECT_LEEWAY_Y));
        }

        else {
            Log.d("Visibility", "Not visible");
            return;
        }

    }

    @Override
    protected void checkYMovement(int[][] map) {

        int tileY;
        int scanLineDownY;
        int scanLineDownXa;
        int scanLineDownXb;

        //means object is falling
        if (velY > 0) {
            Log.d("Enemy", "Case 1a");

            scanLineDownY = (int) Math.floor((y + height) / GameMainActivity.TILE_HEIGHT);

            if (scanLineDownY < 0 || scanLineDownY >= map.length) {
                Log.d("Enemy", "ScanLineDownY out of range, aka isAlive false");
                isAlive = false;
                return;
            }

            scanLineDownXa = (int) Math.floor(((x + width) - RECT_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
            scanLineDownXb = (int) Math.floor((x + RECT_LEEWAY_X) / GameMainActivity.TILE_WIDTH);

            if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                Log.d("Enemy", "scanLineXa out range aka isAlive false");
                isAlive = false;
                return;
            }

            if (scanLineDownXb < 0 || scanLineDownXb >= map[0].length) {
                Log.d("Enemy", "scanLineXb out range aka isAlive false");
                isAlive = false;
                return;
            }

            Log.d("Enemy", "index [" + scanLineDownY + "][" + scanLineDownXa + "] checked.");
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
            Log.d("Enemy", "Case 2a");
            scanLineDownY = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);


            if (scanLineDownY < 0 || scanLineDownY >= map.length) {
                Log.d("Enemy", "scanLineDownY out of range, isAlive = false");
                isAlive = false;
                return;
            }

            scanLineDownXa = (int) Math.floor(((x + width) - RECT_LEEWAY_X) / GameMainActivity.TILE_WIDTH);
            scanLineDownXb = (int) Math.floor((x + RECT_LEEWAY_X) / GameMainActivity.TILE_WIDTH);

            if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                Log.d("Enemy", "scanLineDownXa is out of range, isAlive = false");
                isAlive = false;
                return;
            }

            if (scanLineDownXb < 0 || scanLineDownXb >= map[0].length) {
                Log.d("Enemy", "scanLineDownXb is out of range, isAlive = false");
                isAlive = false;
                return;
            }

            Log.d("Enemy", "index [" + scanLineDownY + "][" + scanLineDownXa + "] checked.");
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
            Log.d("Enemy", "Case 3a");
            scanLineDownY = (int) Math.floor((y + height) / GameMainActivity.TILE_HEIGHT);

            if (scanLineDownY < 0 || scanLineDownY >= map.length) {
                Log.d("Enemy", "scanLineDownY out of range, isAlive = false");
                isAlive = false;
                return;
            }

            if (velX > 0) {
                scanLineDownXa = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
                if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                    Log.d("Enemy", "scanLineDownXa out of range, isAlive = false (MOVING RIGHT)");
                    isAlive = false;
                    return;
                }
            }
            else {
                scanLineDownXa = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
                if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                    Log.d("Enemy", "scanLineDownXa out of range, isAlive = false (MOVING LEFT)");
                    isAlive = false;
                    return;
                }
            }


            Log.d("Enemy", "index [" + scanLineDownY + "][" + scanLineDownXa + "] checked.");
            tileA.setID(map[scanLineDownY][scanLineDownXa]);
            if (!(tileA.isObstacle())) {
                Log.d("EnemyGrounded", "isGrounded made false, tile at : " + scanLineDownY + ", " + scanLineDownXa);
                isGrounded = false;
            }

            return;

        }
    }

    protected void checkXMovement(int[][] map) {

        int scanLineAcrossX;
        int scanLineAcrossYa;
        int scanLineAcrossYb;
        int tileX;

        if (velX > 0) {
            Log.d("Enemy", "Case 1b");
            scanLineAcrossX = (int) Math.floor((x + width) / GameMainActivity.TILE_WIDTH);
            if (scanLineAcrossX < 0 || scanLineAcrossX >= map[0].length) {
                Log.d("Enemy", "isAlive false in checkX.velX > 0");
                isAlive = false;
                return;
            }
        }

        else {
            Log.d("Enemy", "Case 2b");
            scanLineAcrossX = (int) Math.floor(x / GameMainActivity.TILE_WIDTH);
            if (scanLineAcrossX < 0 || scanLineAcrossX >= map[0].length) {
                Log.d("Enemy", "isAlive false in checkX. else velX >0");
                isAlive = false;
                return;
            }
        }

        scanLineAcrossYa = (int) Math.floor((y + height - SCAN_LEEWAY_Y) / GameMainActivity.TILE_HEIGHT);
        scanLineAcrossYb = (int) Math.floor((y + SCAN_LEEWAY_Y)/ GameMainActivity.TILE_HEIGHT);

        if (scanLineAcrossYa < 0 || scanLineAcrossYa >= map.length) {
            Log.d("Enemy", "isAlive false in checkX.velY != 0");
            isAlive = false;
            return;
        }
        if (scanLineAcrossYb < 0 || scanLineAcrossYb >= map.length) {
            Log.d("Enemy", "isAlive false in checkX.velY != 0");
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

    @Override
    public void updateAnim(float delta) {
        if (isAlive) {
            if (isGrounded) {
                //ASSETS FOR WALKING
            }
            else {
                //dont update anything
            }

        }

    }

    @Override
    public void render(Painter g, double cameraOffsetX, double cameraOffsetY) {

        //if (isVisible(cameraOffsetX, cameraOffsetY, x, y, width, height) && isActive() && isAlive) {
                g.drawImage(image, (int) (x - cameraOffsetX), (int) (y - cameraOffsetY), width, height);
                Log.d("EnemyRendering", "image drawn at : " + (x - cameraOffsetX) + ", " + (y - cameraOffsetY));
        //    } else
                return;

    }

    public void clearAreaAround(Painter g, double cameraOffsetX, double cameraOffsetY) {
        if (isVisible(cameraOffsetX, cameraOffsetY, x, y, width, height) && isAlive && isActive()) {
            if (velY <= 0) {
                g.setColor(Color.rgb(208, 244, 247));
                g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY), width, height);
            } else {
                g.setColor(Color.rgb(208, 244, 247));
                g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY) - SCAN_LEEWAY_Y, width, height);
            }
        }
    }

    @Override
    public void checkCollisions(Player mawi, double cameraOffsetX, double cameraOffsetY) {
        //Should only be done when within visible range!
        if (isVisible(cameraOffsetX, cameraOffsetY, x, y, width, height)) {
            //Just a check, should be fine though
            if (isAlive) {

                if ((mawi.getplayerRect()).intersect(rect)) {
                    if (mawi.isGrounded()) {
                        Log.d("EnemyCollision", "Mawi hit");
                        //This means mawi is effected
                        //mawi.enemyHit();
                    } else {
                        Log.d("EnemyCollision", "Enemy hit");
                        mawi.hitEnemy();
                        death();
                        //this means that the enemy was killed, flip image maybe and let fall off map - once off, will need to be defeated
                        //death();
                    }
                } else
                    return;
            }
        }

    }

    public void death() {
        image = Assets.coinImage;

        //set IsGrounded to false, as isAlive being false means that usual tile checking won't happen therefore
        //can fall through the ground
        isGrounded = false;
        isAlive = false;

        isDying = true;

        velY = DEATH_VELOCITY;
        velX = 0;

    }

    public boolean isFalling() {
        return !isGrounded;
    }

    public boolean isDying() {
        return isDying;
    }

    public void activate() {
        isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isAlive() { return isAlive; }

    public double getX() { return x; }

    public double getY() { return y; }

    public Rect getRect() {
        return rect;
    }

    public boolean safeToRemove() {
        if(!isActive() && isDead) {
            return true;
        }

        else
            return false;
    }

    public boolean isDead() {
        return isDead;
    }


}
