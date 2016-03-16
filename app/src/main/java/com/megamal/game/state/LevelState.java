package com.megamal.game.state;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
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
import com.megamal.mawi.R;

import java.io.IOException;

/**
 * Created by malberbatovci on 29/02/16.
 */
public class LevelState extends State {

    private static final int RIGHT = 1;
    private static final int LEFT = -1;

    private static final int LEVEL_CONSTANT = 20;

    private static UIButton walkR, walkL, walkU, walkD, playButton, backToMenuState;
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

    private boolean playLevel = false;
    private int levelToPlay;
    private SharedPreferences preferences;

    private int currentLevel;

    @Override
    //needs to create map, get info from persistent storage about what levels are unlocked
    //
    public void init() {

        //set up shared preferences and get the level that we are currently on
        preferences = GameMainActivity.getApplicationConext().getSharedPreferences(GameMainActivity.preferenceString, Context.MODE_PRIVATE);
        currentLevel = preferences.getInt(GameMainActivity.preferenceString, 1);


        tileFactory = new TileMapFactory();
        tile = new Tile(1);


        try {
            map = tileFactory.parseFileIntoMap(levelString);
        } catch (IOException e) {
            System.err.print("Error parsing file: " + levelString);
        }

        tileRenderer = new TileMapRenderer(map);
        createAndPlacePlayer();

        backToMenuState = new UIButton(10, 10, 74, 74, Assets.backToLEButton, Assets.backToLEButton);

        walkL = new UIButton(10, 395, 90, 435, Assets.runButtonL, Assets.runButtonPressedL);
        walkR = new UIButton(170, 395, 250, 435, Assets.runButtonR, Assets.runButtonPressedR);

        walkU = new UIButton(95, 330, 165, 395, Assets.walkButtonU, Assets.walkButtonUPressed);
        walkD = new UIButton(95, 435, 165, 495, Assets.walkButtonD, Assets.walkButtonDPressed);

        playButton = new UIButton(702, 364, 766, 428, Assets.playButton, Assets.playButton);

    }

    //method to place Mawi, respective of what level they is on
    private void createAndPlacePlayer() {

        Log.d("CurrentLevel", "Current level is: " + currentLevel);

        if(currentLevel == 1) {

            tile.setLocation(2, 1, 0, 0);
            mawi = new LevelEditorPlayer(tile.getX(), tile.getY() + GameMainActivity.TILE_HEIGHT,
                    GameMainActivity.PLAYER_WIDTH, GameMainActivity.PLAYER_HEIGHT);
        }


        else {
            for(int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {


                    //found tile should be on before, i.e level 1 if just on level 2 now
                    if((map[i][j] - LEVEL_CONSTANT) == (currentLevel - 1)) {
                        tile.setLocation(i, j, 0, 0);
                        mawi = new LevelEditorPlayer(tile.getX(), tile.getY() - GameMainActivity.TILE_HEIGHT,
                                GameMainActivity.PLAYER_WIDTH, GameMainActivity.PLAYER_HEIGHT);
                        Log.d("CurrentLevel", "Entered if condition");
                        return;
                    }
                }
            }
        }
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

        if(playerInBound(mawi)) {
            playButton.render(g);
        }
    }

    private boolean playerInBound(LevelEditorPlayer player) {

        double x = player.getX();
        double y = player.getY();

        double midX = (x + (player.getWidth() / 2));
        double midY = (y + (player.getHeight() / 2));

        int tileX = (int) Math.floor(midX / GameMainActivity.TILE_WIDTH);
        int tileY = (int) Math.floor(midY / GameMainActivity.TILE_HEIGHT);

        int ID = map[tileY][tileX];



        //Level tiles are 20 and above, therefore check this before proceeding to ensure we are on
        //a level tile
        if(ID > LEVEL_CONSTANT) {

            //next, subtract ID from level Constant, this will give the level that we are stnding on
            //If this is less than, or equal to the currentMaxLevel we are on, then we can play this level
            //as it is either the current level to play, or a previous one.
            if((ID - LEVEL_CONSTANT) <= currentLevel) {
                playLevel = true;
                levelToPlay = ID - LEVEL_CONSTANT;
                return true;
            }

            else {
                playLevel = false;
                return false;
            }
        }

        else {
            playLevel = false;
            return false;
        }

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

                    else if (playLevel && playButton.onTouchDown(scaledX, scaledY, ID)) {
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

                    else if (playLevel && playButton.onTouchDown(scaledX, scaledY, ID)) {
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

                        else {
                            mawi.stopWalking();
                        }

                        return true;

                    } else if (walkL.onTouchUp(scaledX, scaledY, ID)) {
                        walkingLeft = false;

                        if (walkingRight) {
                            mawi.walk(RIGHT);
                        }

                        else {
                            mawi.stopWalking();
                        }

                        return true;

                    }

                    else if (walkU.onTouchUp(scaledX, scaledY, ID)) {
                        walkingUp = false;

                        if(walkingDown) {
                            mawi.walkDown();
                        }

                        else {
                            mawi.stopWalkingVert();
                        }

                        return true;
                    }


                    else if (walkD.onTouchUp(scaledX, scaledY, ID)) {
                        walkingDown = false;

                        if(walkingUp) {
                            mawi.walkUp();
                        }

                        else {
                            mawi.stopWalkingVert();
                        }

                        return true;

                    }

                    else if (playLevel && playButton.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new PlayState(levelToPlay));

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

                        else {
                            mawi.stopWalkingVert();
                        }

                        return true;
                    }


                    else if (walkD.onTouchUp(scaledX, scaledY, ID)) {
                        walkingDown = false;

                        if(walkingUp) {
                            mawi.walkUp();
                        }

                        else {
                            mawi.stopWalkingVert();
                        }

                        return true;

                    }

                    else if (playLevel && playButton.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new PlayState(levelToPlay));

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
