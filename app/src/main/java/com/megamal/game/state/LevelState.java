package com.megamal.game.state;

import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

import com.megamal.framework.util.Painter;
import com.megamal.framework.util.Tile;
import com.megamal.framework.util.TileMapFactory;
import com.megamal.framework.util.TileMapRenderer;
import com.megamal.framework.util.UIButton;
import com.megamal.game.model.LevelEditorPlayer;
import com.megamal.game.model.Player;
import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

import java.io.IOException;

/**
 * Created by malberbatovci on 29/02/16.
 */
public class LevelState extends State {

    private static final int RIGHT = 1;
    private static final int LEFT = -1;

    private static UIButton walkR, walkL, walkU, walkD, backToMenuState;
    private TileMapFactory tileFactory;
    private TileMapRenderer tileRenderer;

    private String levelString = "levelState.txt";
    private Tile tile;
    private int[][] map;


    private LevelEditorPlayer mawi;
    private boolean walkingUp = false;
    private boolean walkingDown = false;
    private boolean walkingRight = false;
    private boolean walkingLeft = false;

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

        tile.setLocation(2, 1, 0, 0);

        mawi = new LevelEditorPlayer(tile.getX(), tile.getY(),
                GameMainActivity.PLAYER_WIDTH, GameMainActivity.PLAYER_HEIGHT);

        backToMenuState = new UIButton(10, 10, 74, 74, Assets.backToLEButton, Assets.backToLEButton);
        walkL = new UIButton(120, 450, 220, 490, Assets.runButtonL, Assets.runButtonPressedL);
        walkR = new UIButton(225, 450, 325, 490, Assets.runButtonR, Assets.runButtonPressedR);

        walkU = new UIButton(350, 450, 450, 490, Assets.runButtonL, Assets.runButtonPressedL);
        walkD = new UIButton(460, 450, 560, 490, Assets.runButtonR, Assets.runButtonPressedR);

        //create new Mawi, place on start
        //create new tilemap (8x13)
        //use persistent storage to establish what levels are unlocked
    }


    @Override
    //update mawi here
    public void update(float delta, Painter g) {

        mawi.update(delta, map);
    }

    @Override
    //render mawi and map if mawi has moved (can be determined when button is touched)
    public void render(Painter g) {

        tileRenderer.renderWholeMap(g, map, 0, 0);
        renderPlayer(g);

        backToMenuState.render(g);

        walkR.render(g);
        walkL.render(g);
        walkU.render(g);
        walkD.render(g);
        //if mawi has moved, render who map, render map
        //if mawi hasnt move, no need to render anything

    }

    private void renderPlayer(Painter g) {
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
        }

        else
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

            else if (walkR.buttonMovedOn(scaledX, scaledY, ID)) {
                walkingRight = true;
                mawi.walk(RIGHT);
                return true;
            }

            else if (walkR.buttonMovedOut(scaledX, scaledY, ID)) {
                walkingRight = false;

                if (walkingLeft) {
                    mawi.walk(LEFT);
                }

                else if (walkingUp) {
                    mawi.walkUp();
                }

                else if (walkingDown) {
                    mawi.walkDown();
                }

                else {
                    mawi.stopWalking();
                    mawi.stopWalkingVert();
                }

                return true;
            }

            else if (walkL.buttonMovedOn(scaledX, scaledY, ID)) {
                walkingLeft = true;
                mawi.walk(LEFT);
                return true;

            }

            else if (walkL.buttonMovedOut(scaledX, scaledY, ID)) {
                walkingLeft = false;

                if (walkingRight) {
                    mawi.walk(RIGHT);
                }

                else if (walkingUp) {
                    mawi.walkUp();
                }

                else if (walkingDown) {
                    mawi.walkDown();
                }

                else {
                    mawi.stopWalking();
                    mawi.stopWalkingVert();
                }

                return true;

            }

            else if (walkU.buttonMovedOn(scaledX, scaledY, ID)) {
                walkingUp = true;
                mawi.walkUp();
                return true;
            }

            else if (walkU.buttonMovedOut(scaledX, scaledY, ID)) {
                walkingUp = false;

                if(walkingDown) {
                    mawi.walkDown();
                }

                else if (walkingRight) {
                    mawi.walk(RIGHT);
                }

                else if (walkingLeft) {
                    mawi.walk(LEFT);
                }

                else {
                    mawi.stopWalking();
                    mawi.stopWalkingVert();
                }

                return true;
            }

            else if (walkD.buttonMovedOn(scaledX, scaledY, ID)) {
                walkingDown = true;
                mawi.walkDown();
                return true;
            }

            else if (walkD.buttonMovedOut(scaledX, scaledY, ID)) {
                walkingDown = false;

                if(walkingUp) {
                    mawi.walkUp();
                }

                else if (walkingRight) {
                    mawi.walk(RIGHT);
                }

                else if (walkingLeft) {
                    mawi.walk(LEFT);
                }

                else {
                    mawi.stopWalking();
                    mawi.stopWalkingVert();
                }

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

                    else if (walkR.onTouchDown(scaledX, scaledY, ID)) {
                        walkingRight = true;
                        mawi.walk(RIGHT);
                        return true;
                    }

                    else if (walkL.onTouchDown(scaledX, scaledY, ID)) {
                        walkingLeft = true;
                        mawi.walk(LEFT);
                        return true;
                    }

                    else if (walkU.onTouchDown(scaledX, scaledY, ID)) {
                        walkingUp = true;
                        mawi.walkUp();
                        return true;
                    }

                    else if (walkD.onTouchDown(scaledX, scaledY, ID)) {
                        walkingDown = true;
                        mawi.walkDown();
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

                    else if (walkR.onTouchDown(scaledX, scaledY, ID)) {
                        walkingRight = true;
                        mawi.walk(RIGHT);
                        return true;
                    }

                    else if (walkL.onTouchDown(scaledX, scaledY, ID)) {
                        walkingLeft = true;
                        mawi.walk(LEFT);
                        return true;
                    }

                    else if (walkU.onTouchDown(scaledX, scaledY, ID)) {
                        walkingUp = true;
                        mawi.walkUp();
                        return true;
                    }

                    else if (walkD.onTouchDown(scaledX, scaledY, ID)) {
                        walkingDown = true;
                        mawi.walkDown();
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

                    else if (walkR.onTouchUp(scaledX, scaledY, ID)) {
                        walkingRight = false;

                        if (walkingLeft) {
                            mawi.walk(LEFT);
                        }

                        else if (walkingUp) {
                            mawi.walkUp();
                        }

                        else if (walkingDown) {
                            mawi.walkDown();
                        }

                        else {
                            mawi.stopWalking();
                            mawi.stopWalkingVert();
                        }

                        return true;

                    } else if (walkL.onTouchUp(scaledX, scaledY, ID)) {
                        walkingLeft = false;

                        if (walkingRight) {
                            mawi.walk(RIGHT);
                        }

                        else if (walkingUp) {
                            mawi.walkUp();
                        } else if (walkingDown) {
                            mawi.walkDown();
                        }

                        else {
                            mawi.stopWalking();
                            mawi.stopWalkingVert();
                        }

                        return true;

                    }

                    else if (walkU.onTouchUp(scaledX, scaledY, ID)) {
                        walkingUp = false;

                        if(walkingDown) {
                            mawi.walkDown();
                        }

                        else if (walkingRight) {
                            mawi.walk(RIGHT);
                        }

                        else if (walkingLeft) {
                            mawi.walk(LEFT);
                        }

                        else {
                            mawi.stopWalking();
                            mawi.stopWalkingVert();
                        }

                        return true;
                    }


                    else if (walkD.onTouchUp(scaledX, scaledY, ID)) {
                        walkingDown = false;

                        if(walkingUp) {
                            mawi.walkUp();
                        }

                        else if (walkingRight) {
                            mawi.walk(RIGHT);
                        }

                        else if (walkingLeft) {
                            mawi.walk(LEFT);
                        }

                        else {
                            mawi.stopWalking();
                            mawi.stopWalkingVert();
                        }

                        return true;

                    }

                    else {
                        return true;
                    }

                    break;
                }

                case (MotionEvent.ACTION_POINTER_UP): {

                    if (backToMenuState.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new MenuState());
                    } else if (walkR.onTouchUp(scaledX, scaledY, ID)) {
                        walkingRight = false;

                        if (walkingLeft) {
                            mawi.walk(LEFT);
                        } else {
                            mawi.stopWalking();
                        }
                        return true;

                    } else if (walkL.onTouchUp(scaledX, scaledY, ID)) {
                        walkingLeft = false;

                        if (walkingRight) {
                            mawi.walk(RIGHT);
                        } else {
                            mawi.stopWalking();
                        }
                        return true;
                    }

                    else if (walkU.onTouchUp(scaledX, scaledY, ID)) {
                        walkingUp = false;

                        if(walkingDown) {
                            mawi.walkDown();
                        }

                        else if (walkingRight) {
                            mawi.walk(RIGHT);
                        }

                        else if (walkingLeft) {
                            mawi.walk(LEFT);
                        }

                        else {
                            mawi.stopWalking();
                            mawi.stopWalkingVert();
                        }

                        return true;
                    }


                    else if (walkD.onTouchUp(scaledX, scaledY, ID)) {
                        walkingDown = false;

                        if(walkingUp) {
                            mawi.walkUp();
                        }

                        else if (walkingRight) {
                            mawi.walk(RIGHT);
                        }

                        else if (walkingLeft) {
                            mawi.walk(LEFT);
                        }

                        else {
                            mawi.stopWalking();
                            mawi.stopWalkingVert();
                        }

                        return true;

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
