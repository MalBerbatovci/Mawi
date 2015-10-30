package com.megamal.game.state;

import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.megamal.framework.util.Tile;
import com.megamal.framework.util.TileMapFactory;
import com.megamal.framework.util.TileMapRenderer;
import com.megamal.framework.util.UIButton;
import com.megamal.game.model.Player;
import com.megamal.mawi.Assets;
import com.megamal.framework.util.Painter;
import com.megamal.mawi.GameMainActivity;

import java.io.IOException;

/**
 * Created by malberbatovci on 22/09/15.
 */
public class MenuState extends State {

    //final variables for passing to mawi.walk/run() method to determine L/R
    private static final int RIGHT = 1;
    private static final int LEFT = -1;

    private TileMapRenderer tileRenderer;
    private TileMapFactory tileFactory;
    private Player mawi;
    private Tile tile;
    private String levelString = "test.txt";

    private UIButton walkR, walkL, runR, runL, jump;

    private boolean walkingRight = false, walkingLeft = false, runningRight = false, runningLeft = false;
    private int[][] map;

    private int maskedAction, pointerActiveIndex;

    @Override
    public void init() {
        tileRenderer = new TileMapRenderer();
        tileFactory = new TileMapFactory();

        tile = new Tile(1);

        try {
            map = tileFactory.parseFileIntoMap(levelString);
        } catch (IOException e) {
            System.err.print("Error parsing file: " + levelString);
        }

        loop:
        for (int i = 0; i < (GameMainActivity.GAME_HEIGHT / GameMainActivity.TILE_HEIGHT); i++) {
            tile.setID(map[i][2]);
            if (tile.isObstacle()) {
                tile.setLocation(i, 2);
                break loop;
            }
        }

        //tile.setID(1);
        //tile.setLocation(4, 4);
        // - 64

        mawi = new Player(tile.getX(), // + GameMainActivity.PLAYER_WIDTH
                tile.getY() - GameMainActivity.PLAYER_HEIGHT,
                GameMainActivity.PLAYER_WIDTH, GameMainActivity.PLAYER_HEIGHT);


        runL = new UIButton(120, 450, 220, 490, Assets.runButtonL, Assets.runButtonPressedL);
        runR = new UIButton(225, 450, 325, 490, Assets.runButtonR, Assets.runButtonPressedR);

        walkL = new UIButton(330, 450, 430, 490, Assets.walkButtonL, Assets.walkButtonPressedL);
        walkR = new UIButton(435, 450, 535, 490, Assets.walkButtonR, Assets.walkButtonPressedR);

        jump = new UIButton(620, 450, 720, 490, Assets.walkButtonL, Assets.walkButtonPressedL);
    }

    /* private int calculateIndex(int x, int y) {
        return (int) (y * MAP_HEIGHT) + x;
    } */

    @Override
    public void update(float delta) {

        if (!mawi.isAlive()) {
            //do something if end of game
        } else {
            mawi.update(delta, map);
        }

    }

    @Override
    public void render(Painter g) {
        g.setColor(Color.rgb(208, 244, 247));
        g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);

        tileRenderer.renderMap(g, map);

        //renderButton methods
        walkR.render(g);
        walkL.render(g);
        runR.render(g);
        runL.render(g);
        jump.render(g);

        renderPlayer(g);
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

        pointerActiveIndex = MotionEventCompat.getActionIndex(e);

        if (maskedAction == MotionEvent.ACTION_DOWN) {
            Log.d("MenuState", "Action Down entered");
            walkR.onTouchDown(scaledX, scaledY);
            walkL.onTouchDown(scaledX, scaledY);
            runR.onTouchDown(scaledX, scaledY);
            runL.onTouchDown(scaledX, scaledY);
            jump.onTouchDown(scaledX, scaledY);

            if (walkR.isTouched()) {
                mawi.walk(RIGHT);
                walkingRight = true;
            } else if (runR.isTouched()) {
                mawi.run(RIGHT);
                runningRight = true;
            } else if (walkL.isTouched()) {
                mawi.walk(LEFT);
                walkingLeft = true;
            } else if (runL.isTouched()) {
                mawi.run(LEFT);
                runningLeft = true;
            } else if (jump.isTouched()) {
                mawi.jump();
            }
        } else if (maskedAction == MotionEvent.ACTION_POINTER_DOWN) {
            Log.d("MenuState", "Action Pointer Down Called!");

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
            }
        }

        //v. naive way of checking.
        if (maskedAction == MotionEvent.ACTION_UP) {
            Log.d("MenuState", "Action_UP checked with: " + scaledX + "," + scaledY + ". \n");
            if (walkR.isPressed(scaledX, scaledY)) {
                mawi.stopWalking();
                walkingRight = false;
                walkR.cancel();
                if (walkingLeft)
                    mawi.walk(LEFT);
                else if (runningRight)
                    mawi.run(RIGHT);
                else if (runningLeft)
                    mawi.run(LEFT);
            } else if (walkL.isPressed(scaledX, scaledY)) {
                mawi.stopWalking();
                walkingLeft = false;
                walkL.cancel();
                if (walkingRight)
                    mawi.walk(RIGHT);
                else if (runningRight)
                    mawi.run(RIGHT);
                else if (runningLeft)
                    mawi.run(LEFT);
            } else if (runR.isPressed(scaledX, scaledY)) {
                mawi.stopRunning();
                runningRight = false;
                runR.cancel();
                if (walkingLeft)
                    mawi.walk(LEFT);
                else if (walkingRight)
                    mawi.run(RIGHT);
                else if (runningLeft)
                    mawi.run(LEFT);
            } else if (runL.isPressed(scaledX, scaledY)) {
                mawi.stopRunning();
                runningLeft = false;
                runL.cancel();
                if (walkingLeft)
                    mawi.walk(LEFT);
                else if (runningRight)
                    mawi.run(RIGHT);
                else if (walkingRight)
                    mawi.run(RIGHT);
            } else if (jump.isPressed(scaledX, scaledY)) {
                jump.cancel();
            }

        } else if (maskedAction == MotionEvent.ACTION_POINTER_UP) {
            Log.d("MenuState", "Action Pointer UP checked with: " + scaledX2 + "," + scaledY2 + ". \n");

            scaledX2 = (int) ((MotionEventCompat.getX(e, pointerActiveIndex) / v.getWidth()) *
                    GameMainActivity.GAME_WIDTH);
            scaledY2 = (int) ((MotionEventCompat.getY(e, pointerActiveIndex) / v.getHeight()) *
                    GameMainActivity.GAME_HEIGHT);

            Log.d("MenuState", "New Scaled X2: " + scaledX2 + ". Scaled Y2 :" + scaledY2 + ". \n");

            if (walkR.isPressed(scaledX2, scaledY2)) {
                mawi.stopWalking();
                walkingRight = false;
                walkR.cancel();
                Log.d("MenuState", "walk right cancelled in pointer Up. walkingLeft = " + walkingLeft
                        + ".\n");
                if (walkingLeft)
                    mawi.walk(LEFT);
                else if (runningRight)
                    mawi.run(RIGHT);
                else if (runningLeft)
                    mawi.run(LEFT);
            } else if (walkL.isPressed(scaledX2, scaledY2)) {
                mawi.stopWalking();
                walkingLeft = false;
                walkL.cancel();
                if (walkingRight)
                    mawi.walk(RIGHT);
                else if (runningRight)
                    mawi.run(RIGHT);
                else if (runningLeft)
                    mawi.run(LEFT);
            } else if (runR.isPressed(scaledX2, scaledY2)) {
                mawi.stopRunning();
                runningRight = false;
                runR.cancel();
                if (walkingLeft)
                    mawi.walk(LEFT);
                else if (walkingRight)
                    mawi.run(RIGHT);
                else if (runningLeft)
                    mawi.run(LEFT);
            } else if (runL.isPressed(scaledX2, scaledY2)) {
                mawi.stopRunning();
                runningLeft = false;
                runL.cancel();
                if (walkingLeft)
                    mawi.walk(LEFT);
                else if (runningRight)
                    mawi.run(RIGHT);
                else if (walkingRight)
                    mawi.run(RIGHT);
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

