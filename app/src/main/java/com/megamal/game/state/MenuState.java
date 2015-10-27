package com.megamal.game.state;

import android.graphics.Color;
import android.view.KeyEvent;
import android.view.MotionEvent;

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

    private int[][] map;

    @Override
    public void init() {
        tileRenderer = new TileMapRenderer();
        tileFactory = new TileMapFactory();

        tile = new Tile(1);

        try {
            map = tileFactory.parseFileIntoMap(levelString);
        } catch (IOException e ) {
            System.err.print("Error parsing file: " + levelString);
        }

        loop:
        for(int i = 0; i < (GameMainActivity.GAME_HEIGHT / GameMainActivity.TILE_HEIGHT); i++) {
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

        walkR = new UIButton(120, 450, 220, 490, Assets.walkButtonR, Assets.walkButtonPressedR);
        runR = new UIButton(225, 450, 325, 490, Assets.runButtonR, Assets.runButtonPressedR);

        walkL = new UIButton(330, 450, 430, 490, Assets.walkButtonL, Assets.walkButtonPressedL);
        runL = new UIButton(435, 450, 535, 490, Assets.runButtonL, Assets.runButtonPressedL);

        jump = new UIButton(620, 450, 720, 490, Assets.walkButtonL, Assets.walkButtonPressedL);
    }

    /* private int calculateIndex(int x, int y) {
        return (int) (y * MAP_HEIGHT) + x;
    } */

    @Override
    public void update(float delta) {

        if(!mawi.isAlive()) {
            //do something if end of game
        }

        else {
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
            if(mawi.isRight()) {
                g.drawImage(Assets.mawiJumpingR, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                return;
            } else {
                g.drawImage(Assets.mawiJumpingL, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                return;
            }
        }

        if (mawi.isWalking()) {
           if(mawi.isRight()) {
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
            if(mawi.isRight())
                Assets.runAnimR.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
            else
                Assets.runAnimL.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
        }
        else
            g.drawImage(Assets.mawiStandingFront, (int) mawi.getX(), (int) mawi.getY());
    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY) {
        //check if walk button is pressed, this changes walkR.isPressed to true if contained
        //in the buttons rect
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            walkR.onTouchDown(scaledX, scaledY);
            walkL.onTouchDown(scaledX, scaledY);
            runR.onTouchDown(scaledX, scaledY);
            runL.onTouchDown(scaledX, scaledY);
            jump.onTouchDown(scaledX, scaledY);

            if(walkR.isTouched()) {
                mawi.walk(RIGHT);
            } else if (runR.isTouched()) {
                mawi.run(RIGHT);
            } else if (walkL.isTouched()) {
                mawi.walk(LEFT);
            } else if (runL.isTouched()) {
                mawi.run(LEFT);
            } else if (jump.isTouched()) {
                mawi.jump();
            }
        }

        //v. naive way of checking.
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if(walkR.isTouched()) {
                mawi.stopWalking();
                walkR.cancel();
            } else if (walkL.isTouched()) {
                mawi.stopWalking();
                walkL.cancel();
            } else if (runR.isTouched()) {
                mawi.stopRunning();
                runR.cancel();
            } else if (runL.isTouched()) {
                mawi.stopRunning();
                runL.cancel();
            } else if (jump.isTouched()) {
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
