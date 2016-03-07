package com.megamal.game.state;

import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

import com.megamal.framework.util.Painter;
import com.megamal.framework.util.Tile;
import com.megamal.framework.util.TileMapFactory;
import com.megamal.framework.util.TileMapRenderer;
import com.megamal.framework.util.UIButton;
import com.megamal.game.model.Player;
import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

import java.io.IOException;

/**
 * Created by malberbatovci on 29/02/16.
 */
public class LevelState extends State {

    //need walking right button
    //need walk left button
    //need walk up button
    //need walk down button

    private static UIButton walkR, walkL, walkUp, walkDown, backToMenuState;
    private TileMapFactory tileFactory;
    private TileMapRenderer tileRenderer;

    private String levelString = "levelState.txt";
    private Tile tile;
    private int[][] map;

    private Player mawi;

    @Override
    //needs to create map, get info from persistent storage about what levels are unlocked
    //
    public void init() {

        tileFactory = new TileMapFactory();
        tile = new Tile(1);

        try {
            map = tileFactory.parseFileIntoMap(levelString);
        } catch (IOException e) {
            System.err.print("Error parsing file: " + levelString);
        }

        tileRenderer = new TileMapRenderer(map);

        tile.setLocation(5, 1, 0, 0);

        mawi = new Player(tile.getX(), tile.getY()  - GameMainActivity.PLAYER_HEIGHT,
                GameMainActivity.PLAYER_WIDTH, GameMainActivity.PLAYER_HEIGHT);

        backToMenuState = new UIButton(10, 10, 74, 74, Assets.backToLEButton, Assets.backToLEButton);

        //create new Mawi, place on start
        //create new tilemap (8x13)
        //use persistent storage to establish what levels are unlocked
    }


    @Override
    //update mawi here
    public void update(float delta, Painter g) {

        //update Mawi,
    }

    @Override
    //render mawi and map if mawi has moved (can be determined when button is touched)
    public void render(Painter g) {

        tileRenderer.renderWholeMap(g, map, 0, 0);
        renderPlayer(g);

        backToMenuState.render(g);
        //if mawi has moved, render who map, render map
        //if mawi hasnt move, no need to render anything

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
    //here we need to make Mawi move appropriately when a button is pressed,
    //collison detection should be handeled appropriately, need diff buttons
    //gonna need walking Up and Down
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY, int ID, boolean moveAction, View v) {

        int maskedAction;

        if(moveAction) {
            if(backToMenuState.buttonMovedOn(scaledX, scaledY, ID)) {
                return true;
            }

            else if(backToMenuState.buttonMovedOut(scaledX, scaledY, ID)) {
                return true;
            }

            else {
                return true;
            }
        }


        else {

            maskedAction = MotionEventCompat.getActionMasked(e);

            switch(maskedAction) {
                case (MotionEvent.ACTION_DOWN): {
                    if(backToMenuState.onTouchDown(scaledX, scaledY, ID)) {
                        return true;
                    }

                    //else, not of interest, event handled - return true
                    else {
                        return true;
                    }
                }

                case (MotionEvent.ACTION_POINTER_DOWN): {
                    if(backToMenuState.onTouchDown(scaledX, scaledY, ID)) {
                        return true;
                    }

                    //else, not of interest, event handled - return true
                    else {
                        return true;
                    }
                }

                case (MotionEvent.ACTION_UP): {

                    if (backToMenuState.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new MenuState());
                    }

                    else {
                        return true;
                    }

                    break;
                }

                case (MotionEvent.ACTION_POINTER_UP): {

                    if (backToMenuState.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new MenuState());
                    }

                    else {
                        return true;
                    }


                    break;
                }

            }
        }
        return true;
    }
}
