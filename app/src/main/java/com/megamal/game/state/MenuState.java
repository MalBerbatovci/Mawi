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
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY, int scaledX2, int scaledY2, View v) {
        //check if walk button is pressed, this changes walkR.isPressed to true if contained
        //in the buttons rect

        maskedAction = MotionEventCompat.getActionMasked(e);


        int pointerIndex = (maskedAction & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                    MotionEvent.ACTION_POINTER_INDEX_SHIFT;


        int pointerID = e.getPointerId(pointerIndex);

        if (maskedAction == MotionEvent.ACTION_DOWN) {

            runR.onTouchDown(scaledX, scaledY);
            runL.onTouchDown(scaledX, scaledY);
            jump.onTouchDown(scaledX, scaledY);

            if (runR.isTouched()) {

                if(runningLeft) {
                    runningLeft = false;
                    runL.cancel();
                }

                mawi.run(RIGHT);
                runningRight = true;

            }else if (runL.isTouched()) {

                if(runningRight) {
                    runningRight = false;
                    runR.cancel();
                }
                mawi.run(LEFT);
                runningLeft = true;
            } else if (jump.isTouched()) {
                mawi.jump();
            }

        } else if (maskedAction == MotionEvent.ACTION_MOVE) {

            if(e.getHistorySize() != 0)
            {

                try {

                    for (int i = 0; i < e.getPointerCount(); i++) {

                        pointerID = e.getPointerId(i);

                        int previousX = (int) ((e.getHistoricalX(pointerID, 0) / v.getWidth()) * GameMainActivity.GAME_WIDTH);
                        int previousY = (int) ((e.getHistoricalY(pointerID, 0) / v.getHeight()) * GameMainActivity.GAME_HEIGHT);
                        int xToUse = (int) ((e.getX(i) / v.getWidth()) * GameMainActivity.GAME_WIDTH);
                        int yToUse = (int) ((e.getY(i) / v.getHeight()) * GameMainActivity.GAME_HEIGHT);


                        if (e.getPointerCount() > 1) {
                            Log.d("MultiTouch", "PointerID: " + pointerID);
                            Log.d("MultiTouch", "PointerIndex: " + i);
                            Log.d("MultiTouch", "Pointer Co's: " + xToUse + "," + yToUse);
                        }



                        if (runR.isContained(previousX, previousY) && !runR.isContained(xToUse, yToUse)) {
                            runR.cancel();
                            runningRight = false;
                            mawi.stopRunning();

                            //Log.d("MultiTouch", "RUNR was contained");

                       /* if (runningLeft) {
                            mawi.run(LEFT);
                        } */
                        } else if (runL.isContained(previousX, previousY) && !runL.isContained(xToUse, yToUse)) {
                            runL.cancel();
                            runningLeft = false;
                            mawi.stopRunning();

                            //Log.d("MultiTouch", "RUNL was contained");
                       /* if (runningRight) {
                            mawi.run(RIGHT);
                        }*/
                        } else if (jump.isContained(previousX, previousY) && !jump.isContained(xToUse, yToUse)) {
                            jump.cancel();

                        } else {

                            // Log.d("MultiTouch", "Nothing was contained");
                        }

                        runR.onTouchDown(xToUse, yToUse);
                        runL.onTouchDown(xToUse, yToUse);
                        jump.onTouchDown(xToUse, yToUse);

                        if (runR.isTouched() && !runningRight) {

                            mawi.run(RIGHT);
                            runningRight = true;

                        } else if (runL.isTouched() && !runningLeft) {

                            mawi.run(LEFT);
                            runningLeft = true;

                        } else if (jump.isTouched() && ((Math.abs(previousX - xToUse) > TOUCH_THRESHOLD) ||
                                (Math.abs(previousY - yToUse) > TOUCH_THRESHOLD))) {

                            mawi.jump();
                        }

                    }
                }catch (Exception E) {
                    Log.d("MultiTouch", "EXCEPTION CAUGHT");
                    E.printStackTrace();
                }

            }

        } else if (maskedAction == MotionEvent.ACTION_POINTER_DOWN) {
            if(e.getPointerCount() >= 2) {


                for(int i = 1; i < e.getPointerCount(); i++) {
                    Log.d("MultiTouch", "i: " + i + " == pointerID: " + e.getPointerId(i));
                }
                Log.d("MultiTouch", "Pointer 2");
                runR.onTouchDownPointer(scaledX2, scaledY2);
                runL.onTouchDownPointer(scaledX2, scaledY2);
                jump.onTouchDownPointer(scaledX2, scaledY2);

                //if run is touched with second pointer, and runRight is not currently pressed
                if(runR.isTouched() && !runningRight) {
                    runningRight = true;

                    mawi.run(RIGHT);
                }

                //deal with up in pointer turn
                else if (runL.isTouched() && !runningLeft) {
                    runningLeft = true;

                    mawi.run(LEFT);
                }

                else if (jump.isTouched()) {
                    mawi.jump();
                }

            }

            else {

            }

        }

      /*  } else if (maskedAction == MotionEvent.ACTION_POINTER_DOWN) {
            //Log.d("MenuState", "Action Pointer Down Called!");

            walkR.onTouchDownPointer(scaledX2, scaledY2);
            walkL.onTouchDownPointer(scaledX2, scaledY2);
            runR.onTouchDownPointer(scaledX2, scaledY2);
            runL.onTouchDownPointer(scaledX2, scaledY2);
            jump.onTouchDownPointer(scaledX2, scaledY2);

            if (walkR.isPressed(scaledX2, scaledY2)) {
                mawi.walk(RIGHT);
                walkingRight = true;
            } else if (runR.isPressed(scaledX2, scaledY2)) {
                mawi.run(RIGHT);
                runningRight = true;
            } else if (walkL.isPressed(scaledX2, scaledY2)) {
                mawi.walk(LEFT);
                walkingLeft = true;
            } else if (runL.isPressed(scaledX2, scaledY2)) {
                mawi.run(LEFT);
                runningLeft = true;
            } else if (jump.isPressed(scaledX2, scaledY2)) {
                mawi.jump();
            } */

        //v. naive way of checking.
        else if (maskedAction == MotionEvent.ACTION_UP) {
           if (runningRight) {
                mawi.stopRunning();
                runningRight = false;
                runR.cancel();
                if (runningLeft) {
                    mawi.run(LEFT);
                }
            } //else if (runL.isPressed(scaledX, scaledY)) {
                else if (runningLeft) {
                mawi.stopRunning();
                runningLeft = false;
                runL.cancel();

                if (runningRight) {
                    mawi.run(RIGHT);
                }
            } //else if (jump.isPressed(scaledX, scaledY)) {
                else if (mawi.isJumping()) {
                jump.cancel();
            }

        } else if (maskedAction == MotionEvent.ACTION_POINTER_UP) {
            //Log.d("MenuState", "Action Pointer UP checked with: " + scaledX2 + "," + scaledY2 + ". \n");

            scaledX2 = (int) ((MotionEventCompat.getX(e, pointerActiveIndex) / v.getWidth()) *
                    GameMainActivity.GAME_WIDTH);
            scaledY2 = (int) ((MotionEventCompat.getY(e, pointerActiveIndex) / v.getHeight()) *
                    GameMainActivity.GAME_HEIGHT);

             if (runR.isPressed(scaledX2, scaledY2)) {
                mawi.stopRunning();
                runningRight = false;
                runR.cancel();

                if (runningLeft) {
                    mawi.run(LEFT);
                }

            } else if (runL.isPressed(scaledX2, scaledY2)) {
                mawi.stopRunning();
                runningLeft = false;
                runL.cancel();
                if (runningRight) {
                    mawi.run(RIGHT);
                }

            } else if (jump.isPressed(scaledX2, scaledY2)) {
                jump.cancel();
            }
        }

        return true;
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

