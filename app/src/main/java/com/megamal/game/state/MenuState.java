package com.megamal.game.state;

import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.megamal.framework.util.Camera;
import com.megamal.framework.util.Tile;
import com.megamal.framework.util.TileMapFactory;
import com.megamal.framework.util.TileMapRenderer;
import com.megamal.framework.util.UIButton;
import com.megamal.game.model.Collectable;
import com.megamal.game.model.Enemy;
import com.megamal.game.model.Hedgehog;
import com.megamal.game.model.Mover;
import com.megamal.game.model.Player;
import com.megamal.game.model.Projectile;
import com.megamal.mawi.Assets;
import com.megamal.framework.util.Painter;
import com.megamal.mawi.GameMainActivity;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by malberbatovci on 22/09/15.
 */
public class MenuState extends State {

    //final variables for passing to mawi.walk/run() method to determine L/R
    private static final int RIGHT = 1;
    private static final int LEFT = -1;
    private static final int TOUCH_THRESHOLD = 5;

    private TileMapRenderer tileRenderer;
    private TileMapFactory tileFactory;
    private Player mawi;
    private Mover hedge;
    private Projectile testProjectile;
    private Tile tile;
    private String levelString = "test.txt";

    private ArrayList<Collectable> collectables = new ArrayList<Collectable>();

    private UIButton walkR, walkL, runR, runL, jump;

    private boolean walkingRight = false, walkingLeft = false, runningRight = false, runningLeft = false;
    private boolean initialRender = true;
    private int[][] map;

    private int maskedAction, pointerActiveIndex;
    private double cameraOffsetX, cameraOffsetY, previousOffsetX, previousOffsetY;
    private Camera camera;

    @Override
    public void init() {
        tileFactory = new TileMapFactory();
        cameraOffsetX = 0;
        cameraOffsetY = 0;

        tile = new Tile(1);

        try {
            map = tileFactory.parseFileIntoMap(levelString);
        } catch (IOException e) {
            System.err.print("Error parsing file: " + levelString);
        }

        tileRenderer = new TileMapRenderer(map);

        //loop to find first tile to place mawi on
        loop:
        for (int i = 2; i < (GameMainActivity.GAME_HEIGHT / GameMainActivity.TILE_HEIGHT); i++) {
            tile.setID(map[i][2]);
            if (tile.isObstacle()) {
                tile.setLocation(i, 2, cameraOffsetX, cameraOffsetY);
                break loop;
            }
        }

        mawi = new Player(tile.getX(), // + GameMainActivity.PLAYER_WIDTH
                tile.getY() - GameMainActivity.PLAYER_HEIGHT,
                GameMainActivity.PLAYER_WIDTH, GameMainActivity.PLAYER_HEIGHT);

       // hedge = new Hedgehog(400.0, 0.0, cameraOffsetX, cameraOffsetY);

        testProjectile = new Projectile(400.0, 200.0, true, 1, cameraOffsetX, cameraOffsetY);

        runL = new UIButton(120, 450, 220, 490, Assets.runButtonL, Assets.runButtonPressedL);
        runR = new UIButton(225, 450, 325, 490, Assets.runButtonR, Assets.runButtonPressedR);

        /*walkL = new UIButton(330, 450, 430, 490, Assets.walkButtonL, Assets.walkButtonPressedL);
        walkR = new UIButton(435, 450, 535, 490, Assets.walkButtonR, Assets.walkButtonPressedR);*/

        jump = new UIButton(610, 440, 730, 500, Assets.walkButtonL, Assets.walkButtonPressedL);

        camera = new Camera(map);

    }

    /* private int calculateIndex(int x, int y) {
        return (int) (y * MAP_HEIGHT) + x;
    } */

    @Override
    public void update(float delta, Painter g) {

        if (!mawi.isAlive()) {
            //do something if end of game
        } else {

            mawi.update(delta, map, cameraOffsetX, cameraOffsetY);

            if (mawi.hitNewBox()) {
                collectables.add(mawi.getMostRecentCollectable());
            }

            if(!collectables.isEmpty()) {
                //Log.d("Collectables", "Updating collectables");
                for (int i = 0; i < collectables.size(); i++) {
                    collectables.get(i).update(delta, map, cameraOffsetX, cameraOffsetY, mawi);
                }
            }

            if(hedge != null) {
                hedge.update(delta, map, cameraOffsetX, cameraOffsetY, mawi);
            }

            testProjectile.update(delta, map, cameraOffsetX, cameraOffsetY, mawi, g);

            previousOffsetX = cameraOffsetX;
            previousOffsetY = cameraOffsetY;
            cameraOffsetX = camera.updateCameraX(mawi, cameraOffsetX, map);
            cameraOffsetY = camera.updateCameraY(mawi, cameraOffsetY, map);
        }

    }

    @Override
    public void render(Painter g) {

        //whole screen must be rendered
        if(initialRender) {
            tileRenderer.renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);
            initialRender = false;
        }

        else {
            if(previousOffsetX != cameraOffsetX) {
                //Log.d("PreviousOff", "previousX & Y: " + previousOffsetX + " & " + previousOffsetY + ". " +
                //        "CurrentX & Y: " + cameraOffsetX + " & " + cameraOffsetY);
            }
            tileRenderer.renderMap(g, map, cameraOffsetX, cameraOffsetY, previousOffsetX, previousOffsetY, mawi);
        }


        clearAreas(g);

        renderEnemies(g);
        renderProjectiles(g);
        renderCollectables(g);


        //renderButton methods
/*        walkR.render(g);
        walkL.render(g); */
        runR.render(g);
        runL.render(g);
        jump.render(g);

        renderPlayer(g);


    }

    private void clearAreas(Painter g) {

        //clear collectables area first
        if (!collectables.isEmpty()) {
            for (int i = 0; i < collectables.size(); i++) {
                if (collectables.get(i).isAlive() && collectables.get(i).isVisible(cameraOffsetX, cameraOffsetY)) {
                    collectables.get(i).clearAreaAroundCoin(g, cameraOffsetX, cameraOffsetY);
                }
            }
        }

        if (hedge != null) {
            if (!hedge.isDying() && hedge.isActive()) {
                hedge.clearAreaAround(g, cameraOffsetX, cameraOffsetY);
            }
        }

        if (!mawi.hasMoved(cameraOffsetX, cameraOffsetY)) {
            mawi.clearAreaAround(g, cameraOffsetX, cameraOffsetY);
        }

        if(testProjectile.isVisible(cameraOffsetX,cameraOffsetY)) {
            testProjectile.clearAreaAround(g, cameraOffsetX, cameraOffsetY);
        }


    }

    private void renderProjectiles(Painter g) {

        if(testProjectile.isVisible(cameraOffsetX,cameraOffsetY))
        {
            testProjectile.render(g, cameraOffsetX, cameraOffsetY);
            tileRenderer.renderMapCollectable(g, map, cameraOffsetX, cameraOffsetY, testProjectile.getX(),
                    testProjectile.getY(), false, testProjectile.isFalling());
           // Log.d("ProjVisibility", "IS VISIBLE");
        }

        else {
            //Log.d("ProjVisibility", "NOT VISIBLE");
        }

    }

    private void renderCollectables(Painter g) {
        if(!collectables.isEmpty()) {
            for (int i = 0; i < collectables.size(); i++) {

                if (collectables.get(i).isAlive() && collectables.get(i).isVisible(cameraOffsetX, cameraOffsetY)) {

                    collectables.get(i).render(g, cameraOffsetX, cameraOffsetY);
                    tileRenderer.renderMapCollectable(g, map, cameraOffsetX, cameraOffsetY, collectables.get(i).getX(),
                            collectables.get(i).getY(), false, collectables.get(i).isFalling());
                }

                if (!(collectables.get(i).isAlive())) {
                   // Log.d("Collectables", "isAlive = false!");
                    if(collectables.get(i).isVisible(cameraOffsetX, cameraOffsetY)) {
                        tileRenderer.renderMapCollectable(g, map, cameraOffsetX, cameraOffsetY, collectables.get(i).getX(),
                                collectables.get(i).getY(), true, collectables.get(i).isFalling());
                    }

                    collectables.get(i).removeImage(g, cameraOffsetX, cameraOffsetY);
                    collectables.remove(i);
                }
            }
        }

    }

    private void renderEnemies(Painter g) {

        if (hedge != null) {

            //if (hedge.isAlive()) {
            if (!hedge.isDying() && hedge.isActive()) {
                hedge.render(g, cameraOffsetX, cameraOffsetY);
                //Log.d("Enemy", "Rendered");

                //if(hedge.isDying())
                tileRenderer.renderMapCollectable(g, map, cameraOffsetX, cameraOffsetY, hedge.getX(),
                        hedge.getY(), false, hedge.isFalling());


            } else if (hedge.isDying() && hedge.isActive() && !hedge.isDead()) {
                tileRenderer.renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);
                hedge.render(g, cameraOffsetX, cameraOffsetY);
            }

            if (hedge.safeToRemove()) {
                hedge = null;
            }
        }

       // }
    }

    private void renderPlayer(Painter g) {
        if (mawi.isJumping()) {
            if (mawi.isRight()) {
                g.drawImage(Assets.mawiJumpingR, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                return;
            } else if (mawi.isLeft()) {
                g.drawImage(Assets.mawiJumpingL, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                return;
            }
        }

        if (mawi.isWalking()) {
            if (mawi.isRight()) {
                if (mawi.isCollided())
                    Assets.walkHitAnimR.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                else {
                    //System.out.println("mawi.isCollided is false!");
                    Assets.walkAnimR.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                }
            } else {
                if (mawi.isCollided())
                    Assets.walkHitAnimL.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                else
                    Assets.walkAnimL.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
            }

        } else if (mawi.isRunning()) {
            if (mawi.isRight())
                Assets.runAnimR.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
            else
                Assets.runAnimL.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
        } else
            g.drawImage(Assets.mawiStandingFront, (int) mawi.getX(), (int) mawi.getY());
    }


    @Override
    public boolean onTouch(MotionEvent e,int scaledX, int scaledY, int ID, boolean moveAction) {
        //check if walk button is pressed, this changes walkR.isPressed to true if contained
        //in the buttons rect

        if(moveAction) {
                if (runR.buttonMovedOn(scaledX, scaledY, ID)) {
                    Log.d("MultiTouch", "Button moved on!");
                    runningRight = true;
                    mawi.run(RIGHT);
                    return true;
                } else if (runR.buttonMovedOut(scaledX, scaledY, ID)) {
                    runningRight = false;

                    if (runningLeft) {
                        mawi.run(LEFT);
                    } else {
                        mawi.stopRunning();
                    }

                    return true;
                } else if (runL.buttonMovedOn(scaledX, scaledY, ID)) {
                    runningLeft = true;
                    mawi.run(LEFT);
                    return true;

                } else if (runL.buttonMovedOut(scaledX, scaledY, ID)) {
                    runningLeft = false;

                    if (runningRight) {
                        mawi.run(RIGHT);
                    } else {
                        mawi.stopRunning();
                    }

                    return true;
                } else if (jump.buttonMovedOn(scaledX, scaledY, ID)) {
                    mawi.jump();
                    return true;
                } else if (jump.buttonMovedOut(scaledX, scaledY, ID)) {
                    return true;
                } else {
                    return true;
                }
        }

        else {

            //the particular index
            maskedAction = MotionEventCompat.getActionMasked(e);

            switch (maskedAction) {

                case MotionEvent.ACTION_DOWN: {

                    //if any buttons are pressed, then update boolean and make player perform action
                    //do not check if opposite action (i.e running left when pressing running right)
                    //is checked, as this will be suitable checked in the action_up / action_moved
                    //case to then make the player resume any previous movement
                    if (runR.onTouchDown(scaledX, scaledY, ID)) {
                        runningRight = true;
                        mawi.run(RIGHT);
                        return true;
                    } else if (runL.onTouchDown(scaledX, scaledY, ID)) {
                        runningLeft = true;
                        mawi.run(LEFT);
                        return true;
                    } else if (jump.onTouchDown(scaledX, scaledY, ID)) {
                        mawi.jump();
                        return true;
                    }

                    //else, not of interest, event handled - return true
                    else {
                        return true;
                    }
                }

                case MotionEvent.ACTION_POINTER_DOWN: {
                    if (runR.onTouchDown(scaledX, scaledY, ID)) {
                        runningRight = true;
                        mawi.run(RIGHT);
                        return true;
                    } else if (runL.onTouchDown(scaledX, scaledY, ID)) {
                        runningLeft = true;
                        mawi.run(LEFT);
                        return true;
                    } else if (jump.onTouchDown(scaledX, scaledY, ID)) {
                        mawi.jump();
                        return true;
                    }

                    //else, not of interest, event handled - return true
                    else {
                        return true;
                    }
                }

                case MotionEvent.ACTION_UP: {

                    //check if action up was performed on runR (i.e was previous pressed on this),
                    //if so cancel action and see if opposite was being performed and resume that
                    if (runR.onTouchUp(scaledX, scaledY, ID)) {
                        runningRight = false;

                        if (runningLeft) {
                            mawi.run(LEFT);
                        } else {
                            mawi.stopRunning();
                        }

                        return true;
                    } else if (runL.onTouchUp(scaledX, scaledY, ID)) {
                        runningLeft = false;

                        if (runningRight) {
                            mawi.run(RIGHT);
                        } else {
                            mawi.stopRunning();
                        }

                        return true;
                    } else if (jump.onTouchUp(scaledX, scaledY, ID)) {
                        return true;
                    }

                    else {
                        return true;
                    }
                }

                case MotionEvent.ACTION_POINTER_UP: {
                    //check if action up was performed on runR (i.e was previous pressed on this),
                    //if so cancel action and see if opposite was being performed and resume that
                    if (runR.onTouchUp(scaledX, scaledY, ID)) {
                        runningRight = false;

                        if (runningLeft) {
                            mawi.run(LEFT);
                        } else {
                            mawi.stopRunning();
                        }

                        return true;
                    } else if (runL.onTouchUp(scaledX, scaledY, ID)) {
                        runningLeft = false;

                        if (runningRight) {
                            mawi.run(RIGHT);
                        } else {
                            mawi.stopRunning();
                        }

                        return true;
                    } else if (jump.onTouchUp(scaledX, scaledY, ID)) {
                        return true;
                    }

                    else {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /*@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        System.out.println("Key: " + keyCode + " pressed!");

        if (keyCode == event.KEYCODE_J) {
            mawi.jump();
            System.out.println("J pressed!");
        }

        return true;
    }*/
}

