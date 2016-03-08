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
    private static final int COLLISION_LEEWAY = 7;
    private static final int Y_MOVEMENT_EXTRA = 20;
    private final static int MAX_OUT_OF_BOUNDS = 5;
    private static final int OBSTACLE_MAX_COUNT = 5;
    private final static int LEFT = -1;
    private final static int RIGHT = 1;


    private Tile tileA;
    private Tile tileB;

    private Bitmap image;
    protected Rect rect;

    private int tileOnX, tileOnY;
    private int mostRecentDirecton = RIGHT;
    private double x, y;
    private double rectX, rectY;
    private int width, height;
    private int velX, velY;
    private boolean isAlive = true;
    private boolean isActive = false;
    private boolean isGrounded = false;
    private boolean isDying = false;
    private boolean isDead = false;
    private boolean safeToRemove = false;

    private int outOfBoundsCount = 0;
    private int isInObstacleCount = 0;


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
        this.image = Assets.hedgeStandard;

        velX = 100;
        velY = 100;

    }

    //Called on each iteraiton of the game loop, updates x and y given velX and velY, and also
    //checks the movement that the enemy will do, dealing with suitabl collisons in the suitable
    //methods
    @Override
    public void update(float delta, int[][] map, double cameraOffsetX, double cameraOffsetY, Player mawi) {

        if (!isActive && !isDead()) {
            if (isVisible(cameraOffsetX, cameraOffsetY, x, y, width, height)) {
                isActive = true;
            }
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
                updateAnim(delta);
                checkCollisions(mawi, cameraOffsetX, cameraOffsetY, map);

            }

            if (isDying) {
                velY += ACCEL_GRAVITY * delta;
                y += velY * delta;

                Log.d("EnemyVisiblity", "Y is: " + y + ". Camera offset is: " + cameraOffsetY);

                //Log.d("EnemyVisibility", "STILL VISIBLE");

                if (!isVisible(cameraOffsetX, cameraOffsetY, x, y, width, height)) {
                    Log.d("EnemyVisibility", "NOT VISIBLE");
                    isDead = true;
                    isDying = false;
                    isActive = false;
                    safeToRemove = true;
                }

                return;
            }

            if (outOfBoundsCount > MAX_OUT_OF_BOUNDS) {
                isDead = true;
                isDying = false;
                isActive = false;
                safeToRemove = true;
                Log.d("EnemyStuff", "Removed - too many out of bounds");
            }


            tileOnX = (int) Math.floor((x + (width / 2)) / GameMainActivity.TILE_WIDTH);

            if (tileOnX < 0 || tileOnX >= map[0].length) {
                outOfBoundsCount++;
                return;
            }

            tileOnY = (int) Math.floor((y + (height / 2)) / GameMainActivity.TILE_HEIGHT);

            if (tileOnY < 0 || tileOnY >= map.length) {
                outOfBoundsCount++;
                return;
            }

            tileA.setID(map[tileOnY][tileOnX]);
            if (tileA.isObstacle()) {
                isInObstacleCount++;

                if (isInObstacleCount > OBSTACLE_MAX_COUNT) {
                    isDead = true;
                    isDying = false;
                    isActive = false;
                    safeToRemove = true;
                    Log.d("EnemyStuff", "Removed - too many out of obstacle");
                }
            }

            //not obstacle, reset count
            else {
                isInObstacleCount = 0;
            }
        }

    }


    public void updateRects(double cameraOffsetX, double cameraOffsetY) {

        //Check playerX against enemy x
        if (isVisible(cameraOffsetX, cameraOffsetY, x, y, width, height)) {



            //set so rect is smaller than enemy, in order to ensure
            //fairness
            rectX = (x - RECT_LEEWAY_X - cameraOffsetX);
            rectY = (y - RECT_LEEWAY_Y - cameraOffsetY);

            rect.set((int) rectX, (int) rectY, (int) rectX + (width - (RECT_LEEWAY_X * 2)),
                    (int) rectY + (height - (RECT_LEEWAY_Y * 2)));
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

        //this means that the object is falling, therefore check scanline for underneath,
        //if collision then set grounded to true, and set Y to be just above the suitable tile
        if (velY > 0) {

            scanLineDownY = (int) Math.floor((y + height) / GameMainActivity.TILE_HEIGHT);

            if (scanLineDownY < 0 || scanLineDownY >= map.length) {
                outOfBoundsCount++;
                return;
            }

            if(velX > 0) {
                scanLineDownXa = (int) Math.floor(((x + width - Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH));
            }

            else {
                scanLineDownXa = (int) Math.floor((x + Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH);

            }

            if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                outOfBoundsCount++;
                return;
            }

            tileA.setID(map[scanLineDownY][scanLineDownXa]);
            //if obstacle then deal with appropriately
            if(tileA.isObstacle()) {
                isGrounded = true;
                velY = 0;
                tileY = tileA.yLocationNoOffset(scanLineDownY);
                y = tileY - height;
            }

            outOfBoundsCount = 0;
            return;


            //this means that it is 'jumping', check scanLine above, if collision then decrease velY and set Y to suitable
        } else if (velY < 0) {
            scanLineDownY = (int) Math.floor(y / GameMainActivity.TILE_HEIGHT);


            if (scanLineDownY < 0 || scanLineDownY >= map.length) {
                outOfBoundsCount++;
                return;
            }

            if(velX > 0) {
                scanLineDownXa = (int) Math.floor(((x + width) - Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH);
            }

            else {
                scanLineDownXa = (int) Math.floor((x + Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH);
            }

            if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                outOfBoundsCount++;
                return;
            }

            tileA.setID(map[scanLineDownY][scanLineDownXa]);

            //decrease velocity and set y to be just below tile
            if (tileA.isObstacle()) {
                velY = Math.abs(velY) / 5;
                tileY = tileA.yLocationNoOffset(scanLineDownY);
                y = tileY + GameMainActivity.TILE_HEIGHT;
            }

            outOfBoundsCount = 0;
            return;

            //else, this is the case where the object is moving on the ground, check beneath to see if
            //still grounded
        } else {
            scanLineDownY = (int) Math.floor((y + height + RECT_LEEWAY_Y)/ GameMainActivity.TILE_HEIGHT);


            if (scanLineDownY < 0 || scanLineDownY >= map.length) {
                outOfBoundsCount++;
                return;
            }

            if (velX > 0) {
                scanLineDownXa = (int) Math.floor(((x + width) - Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH);
                if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                    outOfBoundsCount++;
                    return;
                }
            }
            else {
                scanLineDownXa = (int) Math.floor((x + Y_MOVEMENT_EXTRA) / GameMainActivity.TILE_WIDTH);
                if (scanLineDownXa < 0 || scanLineDownXa >= map[0].length) {
                    outOfBoundsCount++;
                    return;
                }
            }


            tileA.setID(map[scanLineDownY][scanLineDownXa]);

            if (!(tileA.isObstacle())) {
                isGrounded = false;
            }

        }

        outOfBoundsCount = 0;
        return;
    }

    protected void checkXMovement(int[][] map) {

        int scanLineAcrossX;
        int scanLineAcrossYa;
        int tileX;

        if (velX > 0) {
            Log.d("Collectables", "Case 1b");
            scanLineAcrossX = (int) Math.floor(((x + width) + (RECT_LEEWAY_X * 2)) / GameMainActivity.TILE_WIDTH);

            if (scanLineAcrossX < 0 || scanLineAcrossX >= map[0].length) {
                outOfBoundsCount++;
                return;
            }
        }

        else {
            scanLineAcrossX = (int) Math.floor((x - (RECT_LEEWAY_X * 2)) / GameMainActivity.TILE_WIDTH);
            if (scanLineAcrossX < 0 || scanLineAcrossX >= map[0].length) {
                outOfBoundsCount++;
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
            outOfBoundsCount++;
            return;
        }




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
        outOfBoundsCount = 0;
        return;
    }

    @Override
    public void updateAnim(float delta) {
        if (isAlive) {
            if (isGrounded) {
                if(velX > 0) {
                    Assets.hedgeAnimR.update(delta);
                }

                else {
                    Assets.hedgeAnimL.update(delta);
                }
            }
            else {

            }

        }

    }

    @Override
    public void render(Painter g, double cameraOffsetX, double cameraOffsetY) {

        if (isVisible(cameraOffsetX, cameraOffsetY, x, y, width, height) && isActive()) {
                g.drawImage(image, (int) (x - cameraOffsetX), (int) (y - cameraOffsetY), width, height);
            } else
                return;

    }

    public void clearAreaAround(Painter g, double cameraOffsetX, double cameraOffsetY) {
        if (isVisible(cameraOffsetX, cameraOffsetY, x, y, width, height) && isAlive && isActive()) {
            if (velY <= 0) {
                g.setColor(Color.rgb(80, 143, 240));
                g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY), width, height);
            } else {
                g.setColor(Color.rgb(80, 143, 240));
                g.fillRect((int) (x - cameraOffsetX), (int) (y - cameraOffsetY) - SCAN_LEEWAY_Y, width, height);
            }
        }
    }

    @Override
    public void checkCollisions(Player mawi, double cameraOffsetX, double cameraOffsetY, int[][] map) {


        //Should only be done when within visible range!
        if (isVisible(cameraOffsetX, cameraOffsetY, x, y, width, height)) {
            //Just a check, should be fine though
            if (isAlive && !mawi.isDying()) {

                //if mawi rect has intersected with enemy rect
                if ((mawi.getplayerRect()).intersect(rect)) {


                    //if mawi is grounded, this means that mawi will definitely be harmed
                    if (mawi.isGrounded()) {
                        Log.d("EnemyCollision", "Mawi hit");

                        if(mawi.isInvincible()) {
                            Log.d("Invisible", "INVISIBLE");
                            return;
                        }

                        else {
                            mawi.hitByEnemy(map);
                        }


                    //else, mawi is not grounded - need to check bottom of rect to determine
                    //who is effected
                    } else {

                        if(mawi.getplayerRect().bottom < (rect.top + (COLLISION_LEEWAY * 2))) {
                            mawi.hitEnemy();
                            death();
                        }

                        else {
                            if(mawi.isInvincible()) {
                                Log.d("Invisible", "INVISIBLE");
                                return;
                            }

                            else {
                                mawi.hitByEnemy(map);
                            }
                        }
                    }

                } else
                    return;
            }
        }

    }

    public void death() {
        image = Assets.hedgeStandard;

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
        if(!isActive() && isDead && safeToRemove) {
            return true;
        }

        else
            return false;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public int getVelX() {
        return velX;
    }

    public int getVelY() {
        return velY;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Bitmap getImage(int direction) {

        mostRecentDirecton = direction;

        if(direction == LEFT) {
            return(Assets.hedgeWalkL1);
        }

        else if (direction == RIGHT) {
            return(Assets.hedgeWalkR1);
        }

        else {
            return(Assets.hedgeWalkL1);
        }
    }

    @Override
    public int getMostRecentDirection() {

        if(mostRecentDirecton != LEFT && mostRecentDirecton != RIGHT) {
            mostRecentDirecton = LEFT;
            return mostRecentDirecton;
        }

        else {
            return mostRecentDirecton;
        }
    }

    public int getWidth() {
        return width;
    }

    public void forceDirection(int direction) {

        if(direction == LEFT) {
            velX = -100;
        }

        else {
            velX = 100;
        }

    }


}
