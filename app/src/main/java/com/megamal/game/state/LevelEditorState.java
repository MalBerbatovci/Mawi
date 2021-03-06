package com.megamal.game.state;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.megamal.framework.util.LevelEditorCamera;
import com.megamal.framework.util.Painter;
import com.megamal.framework.util.RandomNumberGenerator;
import com.megamal.framework.util.Tile;
import com.megamal.framework.util.TileMapRenderer;
import com.megamal.framework.util.UIButton;
import com.megamal.game.model.Enemy;
import com.megamal.game.model.Hedgehog;
import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;
import com.megamal.mawi.GameView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by malberbatovci on 12/02/16.
 */
public class LevelEditorState extends State {

    private final static int LEFT = -1;
    private final static int DOWN = -1;
    private final static int RIGHT = 1;
    private final static int UP = 1;
    private final static int REMINDER_DISTANCE_X = -15;
    private final static int REMINDER_DISTANCE_Y = 10;
    private final static int REMINDER_WITDH = 20;
    private final static int REMINDER_HEIGHT = 20;
    private final static int NUMBER_TILES = 4;
    private final static int MIN_Y_CELLS = 7;
    private final static int MIN_X_CELLS = 12;

    private final static String FILE_NAME = "level2.txt";
    private static final int BOX_ID = 4;
    private static final int GRASS_ID = 1;

    protected int currentMaxX = 12;
    protected int currentMaxY = 7;

    protected int previousMapX = -1;
    protected int previousMapY = -1;

    protected int previousEnemyX = -1;
    protected int previousEnemyY = -1;

    protected boolean leftEnemyTouched = false;
    protected boolean rightEnemyTouched = false;

    protected boolean showExtraToolKit = false;
    protected boolean showIDSwapper = false;
    protected boolean showEnemySwapper = false;

    protected Tile testTile;
    protected Enemy testEnemy;


    private boolean mapChanged = true;
    protected UIButton exitButton, dragButton, pencilButton, wrenchButton, leftButton, rightButton;
    protected UIButton saveButton, playButton;
    protected UIButton enemyButton;

    protected ArrayList<Enemy> enemies = new ArrayList<Enemy>();

    private int[][] map;
    private int maskedAction;

    int previousX, previousY;

    private LevelEditorCamera camera;
    private TileMapRenderer tileMapRenderer;
    private UIButton eraserButton;

    public LevelEditorState(int[][] newMap, int maxX, int maxY) {

        if(newMap.length < 2) {
            map = new int[100][100];

            for(int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) {
                    map[i][j] = 0;
                }
            }


            for(int i = 0; i < newMap.length; i++) {
                for(int j = 0; j < newMap[0].length; j++) {
                    map[i][j] = newMap[i][j];
                }
            }
        }

        else {
            map = newMap.clone();
        }

        if(maxX > currentMaxX) {
            currentMaxX = maxX;
        }

        if(maxY > currentMaxY) {
            currentMaxY = maxY;
        }
    }

    @Override
    public void init() {

        //Placements for buttons
        exitButton = new UIButton(10, 10, 74, 54, Assets.exitButton, Assets.exitButtonPressed);
        wrenchButton = new UIButton(32, 450, 96, 504, Assets.wrenchTool, Assets.wrenchToolInUse);


        //set of 'extra tool kit'
        pencilButton = new UIButton(32, 364, 96, 428, Assets.pencilTool, Assets.pencilToolInUse);
        eraserButton = new UIButton(96, 364, 160, 428, Assets.eraserTool, Assets.eraserToolInUse);
        dragButton = new UIButton(160, 364, 224, 428, Assets.movingTool, Assets.movingToolUsed);
        enemyButton = new UIButton(234, 364, 298, 428, Assets.enemyButton, Assets.enemyButtonUsed);
        saveButton = new UIButton(768, 364, 832, 428, Assets.saveButton, Assets.saveButton);
        playButton = new UIButton(702, 364, 766, 428, Assets.playButton, Assets.playButton);


        //set of buttons to switch placement ID
        leftButton = new UIButton(340, 450, 380, 504, Assets.leftID, Assets.leftIDUsed);
        rightButton = new UIButton(452, 450, 492, 504, Assets.rightID, Assets.rightIDUsed);


        //stub value for tile, just initialisig
        testTile = new Tile(0);

        //stub value
//        testEnemy = new Hedgehog(0, 0, camera.getX(), camera.getY());

        tileMapRenderer = new TileMapRenderer(map);

        //create new camera
        camera = new LevelEditorCamera(map);


    }

    @Override
    public void update(float delta, Painter g) {

        if(wrenchButton.isTouched() && pencilButton.isTouched()) {
            showIDSwapper = true;
            mapChanged = true;
        }

        if(wrenchButton.isTouched() && enemyButton.isTouched()) {
            showEnemySwapper = true;
            mapChanged = true;
        }
    }

    @Override
    public void render(Painter g) {

        if(mapChanged) {
            /*g.setColor(Color.rgb(80, 143, 240));
            g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);*/

            tileMapRenderer.renderWholeMap(g, map, camera.getX(), camera.getY());
            mapChanged = false;


        }

        exitButton.render(g);
        wrenchButton.render(g);

        if(showExtraToolKit) {
            dragButton.render(g);
            pencilButton.render(g);
            eraserButton.render(g);
            enemyButton.render(g);
            saveButton.render(g);
            playButton.render(g);
        }

        if(showIDSwapper) {
            leftButton.render(g);
            rightButton.render(g);

            g.drawImage(testTile.getImage(), 384, 450);
        }

       /* else if (showEnemySwapper) {
            leftButton.render(g);
            rightButton.render(g);

            if(leftEnemyTouched) {
                g.drawImage(testEnemy.getImage(LEFT), 384, 450);
                leftEnemyTouched = false;
            }

            else if (rightEnemyTouched) {
                g.drawImage(testEnemy.getImage(RIGHT), 384, 450);
                rightEnemyTouched = false;
            }

        } */

        if(dragButton.isTouched()) {
            drawReminder(g, dragButton);
        }

        else if (pencilButton.isTouched()) {
            drawReminder(g, pencilButton);
        }

        else if (eraserButton.isTouched()) {
            drawReminder(g, eraserButton);
        }

        else if (enemyButton.isTouched()) {
            drawReminder(g, enemyButton);
        }



    }

    private void drawReminder(Painter g, UIButton button) {
        g.drawImage(button.getButtonImage(), wrenchButton.getX() + REMINDER_DISTANCE_X,
                wrenchButton.getY() + REMINDER_DISTANCE_Y, REMINDER_WITDH, REMINDER_HEIGHT);

        mapChanged = true;
    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY, int ID, boolean moveAction,
                           View v) {

        //check if moved onto exit button, or changing cameraOffsetButton
        //also will need to check placement of tiles.
        if(moveAction) {

            //then it is safe to set ID to this
            if(!camera.hasIDSet()  && dragButton.isTouched()) {
                //calculate distance in X and Y

                previousX = scaledX;
                previousY = scaledY;

                camera.setControllerID(ID);
                return true;

            }

            //then it is safe to calculate distance
            else if (camera.hasIDSet() && dragButton.isTouched() && ID == camera.getControllerID()) {

                //flags to check whether whole map needs to be rendered or not
                boolean xChanged = true;
                boolean yChanged = true;

                int distanceX = previousX - scaledX;
                int distanceY = previousY - scaledY;


                if(Math.abs(distanceX) > 3) {
                    //moving LEFT
                    if (distanceX < 0) {
                        distanceX = Math.abs(distanceX);
                        camera.updateCameraX(distanceX, LEFT, ID);
                    }

                    //moving RIGHT
                    else if (distanceX > 0) {
                        camera.updateCameraX(distanceX, RIGHT, ID);

                    }

                    //distance X is 0
                    else {
                        xChanged = false;
                        //no updating necessary
                    }
                }

                if(Math.abs(distanceY) > 3) {

                    //MOVING UP
                    if(distanceY < 0) {
                        distanceY = Math.abs(distanceY);
                        camera.updateCameraY(distanceY, UP, ID);
                    }

                    //MOVING DOWN
                    else if (distanceY > 0) {
                        camera.updateCameraY(distanceY, DOWN, ID);

                    }

                    else {
                        yChanged = false;
                        //no updating necessary, distanceY == 0
                    }
                }

                previousX = scaledX;
                previousY = scaledY;

                /*Log.d("LevelEditor", "CameraX: " + camera.getX());
                Log.d("LevelEditor", "CameraY: " + camera.getY()); */

                //if xNotChanged and yNotChanged
                if(!xChanged && !yChanged) {
                    mapChanged = false;
                }

                else {
                    mapChanged = true;
                }

                if(showExtraToolKit && !wrenchButton.isContained(scaledX, scaledY) &&
                        !dragButton.isContained(scaledX, scaledY)) {

                    showExtraToolKit = false;
                    wrenchButton.forceTouchOff();
                }

                return true;


            }

            /*else if(enemyButton.isTouched()) {

                //if extra tool kit is shown, but enemyButton not contained and wrench
                //not contained
                if(showExtraToolKit && !enemyButton.isContained(scaledX, scaledY) &&
                        !wrenchButton.isContained(scaledX, scaledY)) {


                    //this means they want to draw out of shwoing enemy swapper
                    if(showEnemySwapper && !rightButton.isContained(scaledX, scaledY) &&
                            !leftButton.isContained(scaledX, scaledY)) {

                        drawEnemy(scaledX, scaledY);
                        showEnemySwapper = false;
                        showExtraToolKit = false;
                        mapChanged = true;
                        wrenchButton.forceTouchOff();

                    }

                    else if (!showEnemySwapper) {
                        drawEnemy(scaledX, scaledY);
                        mapChanged = true;
                        wrenchButton.forceTouchOff();
                    }


                }

                else if (!showExtraToolKit && !wrenchButton.isContained(scaledX, scaledY)) {
                    drawEnemy(scaledX, scaledY);
                }
            } */

            else if(pencilButton.isTouched()) {
                if(showExtraToolKit && !pencilButton.isContained(scaledX, scaledY) &&
                        !wrenchButton.isContained(scaledX, scaledY)) {

                    if(showIDSwapper && !rightButton.isContained(scaledX, scaledY) &&
                        !leftButton.isContained(scaledX, scaledY)){

                        drawTile(scaledX, scaledY, testTile.getID());

                        showExtraToolKit = false;
                        showIDSwapper = false;
                        mapChanged = true;

                        wrenchButton.forceTouchOff();
                    }

                    else if (!showIDSwapper) {
                        drawTile(scaledX, scaledY, testTile.getID());
                        mapChanged = true;
                        wrenchButton.forceTouchOff();
                    }
                }

                else if (!showExtraToolKit && !wrenchButton.isContained(scaledX, scaledY)) {
                    drawTile(scaledX, scaledY, testTile.getID());
                }

            }

            else if(eraserButton.isTouched()) {

                if(showExtraToolKit && !eraserButton.isContained(scaledX, scaledY) &&
                        !wrenchButton.isContained(scaledX, scaledY)) {
                    drawTile(scaledX, scaledY, 0);
                    showExtraToolKit = false;
                    wrenchButton.forceTouchOff();
                }

                else if (!showExtraToolKit && !wrenchButton.isContained(scaledX, scaledY)) {
                    drawTile(scaledX, scaledY, 0);
                }

            }

            else if(exitButton.buttonMovedOn(scaledX, scaledY, ID)) {
                return true;
            }

            else if (exitButton.buttonMovedOut(scaledX, scaledY, ID)) {
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
                    if(exitButton.onTouchDown(scaledX, scaledY, ID)) {
                        return true;
                    }


                    else if(wrenchButton.isTouched() &&
                            wrenchButton.isContained(scaledX, scaledY)) {

                        if(wrenchButton.onTouchDownOff(scaledX, scaledY)) {
                            showExtraToolKit = false;
                            showIDSwapper = false;
                            mapChanged = true;
                            return true;
                        }
                    }

                    //touch down, so show toolkit
                    else if (wrenchButton.onTouchDown(scaledX, scaledY, ID)) {
                        showExtraToolKit = true;
                        mapChanged = true;
                        return true;
                    }

                    else if (wrenchButton.isTouched()) {

                        if (dragButton.isTouched() &&
                                dragButton.isContained(scaledX, scaledY)) {

                            Log.d("dragButton", "isTouched && ID is the same");
                            if (dragButton.onTouchDownOff(scaledX, scaledY)) {
                                return true;
                            }

                        } else if (dragButton.onTouchDown(scaledX, scaledY, ID)) {

                            if (pencilButton.isTouched()) {
                                pencilButton.forceTouchOff();
                                showIDSwapper = false;
                                mapChanged = true;
                            }

                            else if(eraserButton.isTouched()) {
                                eraserButton.forceTouchOff();
                                mapChanged = true;
                            }

                            return true;

                        } else if (pencilButton.isTouched() &&
                                pencilButton.isContained(scaledX, scaledY)) {

                            if (pencilButton.onTouchDownOff(scaledX, scaledY)) {

                                //in order to remove
                                showIDSwapper = false;
                                mapChanged = true;
                                return true;
                            }
                        } else if (pencilButton.onTouchDown(scaledX, scaledY, ID)) {

                            if (dragButton.isTouched()) {
                                dragButton.forceTouchOff();
                                mapChanged = true;
                            }

                            else if (eraserButton.isTouched()) {
                                eraserButton.forceTouchOff();
                                mapChanged = true;
                            }

                            showIDSwapper = true;
                            testTile.setID(1);

                            return true;
                        }

                        else if(eraserButton.isTouched() &&
                                eraserButton.isContained(scaledX, scaledY)) {

                            if(eraserButton.onTouchDownOff(scaledX,scaledY)) {
                                return true;
                            }
                        }

                        else if (eraserButton.onTouchDown(scaledX, scaledY, ID)) {

                            if(dragButton.isTouched()) {
                                dragButton.forceTouchOff();
                                showIDSwapper = false;
                                mapChanged = true;
                            }

                            else if(pencilButton.isTouched()) {
                                pencilButton.forceTouchOff();
                                showIDSwapper = false;
                                mapChanged= true;
                            }

                            return true;
                        }

                        else if (showExtraToolKit && saveButton.onTouchDown(scaledX, scaledY, ID)) {
                            if(dragButton.isTouched()) {
                                dragButton.forceTouchOff();
                                showIDSwapper = false;
                                mapChanged = true;
                            }

                            else if(pencilButton.isTouched()) {
                                pencilButton.forceTouchOff();
                                showIDSwapper = false;
                                mapChanged= true;
                            }

                            else if (eraserButton.isTouched()) {
                                eraserButton.forceTouchOff();
                                mapChanged = true;

                            }

                            return true;
                        }

                        else if (playButton.onTouchDown(scaledX, scaledY, ID)) {
                            setCurrentState(new LevelEditorPlayState(map, currentMaxX,
                                    currentMaxY));
                        }


                        //register left/right movement for ID Switching in this case
                        else if(showIDSwapper) {

                            if(rightButton.onTouchDown(scaledX, scaledY, ID)) {
                                return true;
                            }

                            else if(leftButton.onTouchDown(scaledX, scaledY, ID)) {
                                return true;
                            }
                        }
                    }

                    else if(pencilButton.isTouched()) {

                        if(showExtraToolKit && !pencilButton.isContained(scaledX, scaledY) &&
                                !wrenchButton.isContained(scaledX, scaledY)) {

                            if(showIDSwapper && !rightButton.isContained(scaledX, scaledY) &&
                                    !leftButton.isContained(scaledX, scaledY)) {

                                showIDSwapper = false;
                                drawTile(scaledX, scaledY, testTile.getID());
                            }

                            else if (!showIDSwapper) {
                                drawTile(scaledX, scaledY, testTile.getID());
                            }
                        }

                        else if (!showExtraToolKit && !wrenchButton.isContained(scaledX, scaledY)) {
                            drawTile(scaledX, scaledY, testTile.getID());
                        }

                    }

                    else if(eraserButton.isTouched()) {

                        if(showExtraToolKit && !eraserButton.isContained(scaledX, scaledY) &&
                                !wrenchButton.isContained(scaledX, scaledY)) {
                            drawTile(scaledX, scaledY, 0);
                        }

                        else if (!showExtraToolKit && !wrenchButton.isContained(scaledX, scaledY)) {
                            drawTile(scaledX, scaledY, 0);
                        }

                    }
                    else {
                        return true;
                    }
                }

                case (MotionEvent.ACTION_POINTER_DOWN): {
                    if(exitButton.onTouchDown(scaledX, scaledY, ID)) {
                        return true;
                    }


                    else if(wrenchButton.isTouched() &&
                            wrenchButton.isContained(scaledX, scaledY)) {

                        if(wrenchButton.onTouchDownOff(scaledX, scaledY)) {
                            showExtraToolKit = false;
                            mapChanged = true;
                            return true;
                        }
                    }

                    //touch down, so show toolkit
                    else if (wrenchButton.onTouchDown(scaledX, scaledY, ID)) {
                        showExtraToolKit = true;
                        mapChanged = true;
                        return true;
                    }

                    else if (wrenchButton.isTouched()) {

                        if (dragButton.isTouched() &&
                                dragButton.isContained(scaledX, scaledY)) {

                            Log.d("dragButton", "isTouched && ID is the same");
                            if (dragButton.onTouchDownOff(scaledX, scaledY)) {
                                return true;
                            }

                        } else if (dragButton.onTouchDown(scaledX, scaledY, ID)) {

                            if (pencilButton.isTouched()) {
                                pencilButton.forceTouchOff();
                                showIDSwapper = false;
                                mapChanged = true;
                            }

                            else if(eraserButton.isTouched()) {
                                eraserButton.forceTouchOff();
                                mapChanged = true;
                            }

                            return true;

                        } else if (pencilButton.isTouched() &&
                                pencilButton.isContained(scaledX, scaledY)) {

                            if (pencilButton.onTouchDownOff(scaledX, scaledY)) {

                                //in order to remove
                                showIDSwapper = false;
                                mapChanged = true;
                                return true;
                            }
                        } else if (pencilButton.onTouchDown(scaledX, scaledY, ID)) {

                            if (dragButton.isTouched()) {
                                dragButton.forceTouchOff();
                                mapChanged = true;
                            }

                            else if (eraserButton.isTouched()) {
                                eraserButton.forceTouchOff();
                                mapChanged = true;
                            }

                            showIDSwapper = true;
                            testTile.setID(1);

                            return true;
                        }

                        else if(eraserButton.isTouched() &&
                                eraserButton.isContained(scaledX, scaledY)) {

                            if(eraserButton.onTouchDownOff(scaledX,scaledY)) {
                                return true;
                            }
                        }

                        else if (eraserButton.onTouchDown(scaledX, scaledY, ID)) {

                            if(dragButton.isTouched()) {
                                dragButton.forceTouchOff();
                                showIDSwapper = false;
                                mapChanged = true;
                            }

                            else if(pencilButton.isTouched()) {
                                pencilButton.forceTouchOff();
                                showIDSwapper = false;
                                mapChanged= true;
                            }

                            return true;
                        }

                        else if (showExtraToolKit && saveButton.onTouchDown(scaledX, scaledY, ID)) {
                            if(dragButton.isTouched()) {
                                dragButton.forceTouchOff();
                                showIDSwapper = false;
                                mapChanged = true;
                            }

                            else if(pencilButton.isTouched()) {
                                pencilButton.forceTouchOff();
                                showIDSwapper = false;
                                mapChanged= true;
                            }

                            else if (eraserButton.isTouched()) {
                                eraserButton.forceTouchOff();
                                mapChanged = true;

                            }

                            return true;
                        }

                        //register left/right movement for ID Switching in this case
                        else if(showIDSwapper) {

                            if(rightButton.onTouchDown(scaledX, scaledY, ID)) {
                                return true;
                            }

                            else if(leftButton.onTouchDown(scaledX, scaledY, ID)) {
                                return true;
                            }
                        }
                    }

                    else if(pencilButton.isTouched()) {

                        if(showExtraToolKit && !pencilButton.isContained(scaledX, scaledY) &&
                                !wrenchButton.isContained(scaledX, scaledY)) {

                            if(showIDSwapper && !rightButton.isContained(scaledX, scaledY) &&
                                    !leftButton.isContained(scaledX, scaledY)) {

                                showIDSwapper = false;
                                drawTile(scaledX, scaledY, testTile.getID());
                            }

                            else if (!showIDSwapper) {
                                drawTile(scaledX, scaledY, testTile.getID());
                            }
                        }

                        else if (!showExtraToolKit && !wrenchButton.isContained(scaledX, scaledY)) {
                            drawTile(scaledX, scaledY, testTile.getID());
                        }

                    }

                    else if(eraserButton.isTouched()) {

                        if(showExtraToolKit && !eraserButton.isContained(scaledX, scaledY) &&
                                !wrenchButton.isContained(scaledX, scaledY)) {
                            drawTile(scaledX, scaledY, 0);
                        }

                        else if (!showExtraToolKit && !wrenchButton.isContained(scaledX, scaledY)) {
                            drawTile(scaledX, scaledY, 0);
                        }

                    }
                    else {
                        return true;
                    }
                }


                //IF ACTION_UP is same ID as current camera controller, then ensure previousX = 0 etc
                case (MotionEvent.ACTION_UP): {

                    if(dragButton.isTouched()) {
                        camera.lockToNearest(map, ID);

                        mapChanged = true;

                        Log.d("LevelEditor", "Locked to closest");
                        Log.d("LevelEditor", "CameraX: " + camera.getX());
                        Log.d("LevelEditor", "CameraY: " + camera.getY());
                    }


                    else if(exitButton.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new MenuState());
                        return true;
                    }

                    else if(showExtraToolKit && saveButton.onTouchUp(scaledX, scaledY, ID)) {
                        parseMapAndsaveFile();
                    }

                    else if(showIDSwapper) {

                        if (rightButton.onTouchUp(scaledX, scaledY, ID)) {

                            if (testTile.getID() < NUMBER_TILES) {
                                testTile.setID(testTile.getID() + 1);
                            }

                            //else testTile.getID >=
                            else {
                                testTile.setID(1);
                            }

                            Log.d("IDSwapper", "Right registered");
                            return true;
                        } else if (leftButton.onTouchUp(scaledX, scaledY, ID)) {

                            if (testTile.getID() > 1) {
                                testTile.setID(testTile.getID() - 1);
                            }

                            //else, getID == 1
                            else {
                                testTile.setID(NUMBER_TILES);
                            }
                            Log.d("IDSwapper", "Left registered");
                        }
                    }


                    else {
                        return true;
                    }


                }

                case (MotionEvent.ACTION_POINTER_UP): {
                    if (exitButton.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new MenuState());
                        return true;
                    }

                    else if(dragButton.isTouched()) {
                        camera.lockToNearest(map, ID);

                        Log.d("LevelEditor", "Locked to closest");
                        Log.d("LevelEditor", "CameraX: " + camera.getX());
                        Log.d("LevelEditor", "CameraY: " + camera.getY());

                        return true;
                    }

                    else if(rightButton.onTouchUp(scaledX, scaledY, ID)) {
                        Log.d("IDSwapper", "Right registered");
                        return true;
                    }

                    else if(leftButton.onTouchUp(scaledX, scaledY, ID)) {
                        Log.d("IDSwapper", "Left registered");
                    }

                    else if(showExtraToolKit && saveButton.onTouchUp(scaledX, scaledY, ID)) {
                        parseMapAndsaveFile();
                    }

                    else {
                        return true;
                    }
                }
            }
        }

        return true;
    }

    private void drawEnemy(int scaledX, int scaledY) {

        int mapEntryX = (int) Math.floor((scaledX + camera.getX())
                / GameMainActivity.TILE_WIDTH);

        int mapEntryY = (int) Math.floor((scaledY + camera.getY())
                / GameMainActivity.TILE_HEIGHT);

        if(mapEntryX < 0 || mapEntryX >= map[0].length) {
            return;
        }

        if(mapEntryY < 0 || mapEntryY >= map.length) {
            return;
        }

        else {

            //i.e if map is not currently an enemy
            if((map[mapEntryY][mapEntryX]) != -1) {

                map[mapEntryY][mapEntryX] = -1;
                testEnemy = new Hedgehog((mapEntryX * GameMainActivity.TILE_WIDTH),
                        (mapEntryY * GameMainActivity.TILE_HEIGHT), camera.getX(), camera.getY());
                enemies.add(testEnemy);


            }

          /*  if(checkRestrictions(ID, mapEntryX, mapEntryY)) {
                if (!(map[mapEntryY][mapEntryX] == ID)) {
                    map[mapEntryY][mapEntryX] = ID;

                    updateMapBoundaries(mapEntryX, mapEntryY, ID);
                }


                previousMapY = mapEntryY;
                previousMapX = mapEntryX;

                mapChanged = true;
            } */
        }

    }

    private void drawTile(int scaledX, int scaledY, int ID) {

        int mapEntryX = (int) Math.floor((scaledX + camera.getX())
                / GameMainActivity.TILE_WIDTH);
        int mapEntryY = (int) Math.floor((scaledY + camera.getY())
                    / GameMainActivity.TILE_HEIGHT);

        if(mapEntryX < 0 || mapEntryX >= map[0].length) {
            return;
        }

        if(mapEntryY < 0 || mapEntryY >= map.length) {
            return;
        }

        //
        if(previousMapX == mapEntryX &&
                previousMapY == mapEntryY) {

            return;
        }


        //need to keep track of currentMax
        else {

            if(checkRestrictions(ID, mapEntryX, mapEntryY)) {
                if (!(map[mapEntryY][mapEntryX] == ID)) {
                    map[mapEntryY][mapEntryX] = ID;

                    updateMapBoundaries(mapEntryX, mapEntryY, ID);
                }


                previousMapY = mapEntryY;
                previousMapX = mapEntryX;

                mapChanged = true;
            }
        }


    }

    private boolean checkRestrictions(int id, int mapEntryX, int mapEntryY) {
        
        switch (id) {
            default: {

                if((mapEntryY - 1) >= 0 && (mapEntryY - 1) < map.length) {
                    if((mapEntryX >= 0) && mapEntryX < map[0].length) {
                        if (map[mapEntryY - 1][mapEntryX] == BOX_ID) {
                            return false;
                        }
                    }
                }
                //if tile underneath is box, this one cant be

                if((mapEntryY + 1) >= 0 && (mapEntryY + 1) < map.length) {
                    if((mapEntryX >= 0) && mapEntryX < map[0].length) {
                        if (map[mapEntryY + 1][mapEntryX] == BOX_ID) {
                            return false;
                        }
                    }
                }

                break;
            }
        }

        return true;
    }


    //updates maxX and maxY where a non 0 ID tile is placed,
    //this means these are the boundaries of the level
    private void updateMapBoundaries(int mapEntryX, int mapEntryY, int ID) {

        //I.E if a tile is being drawn on, not erased
        if(ID != 0) {
            if (mapEntryX > currentMaxX) {

                if (mapEntryX < MIN_X_CELLS) {
                    currentMaxX = MIN_X_CELLS;
                } else {
                    currentMaxX = mapEntryX;
                }
            }


            if (mapEntryY > currentMaxY) {

                if(mapEntryY < MIN_Y_CELLS) {
                    currentMaxY = MIN_Y_CELLS;
                } else {
                    currentMaxY = mapEntryY;
                }

            }
        }

        //else, in this case is being erased
        else {

            boolean maxFoundX = false;
            boolean maxFoundY = false;


            if(mapEntryX == currentMaxX && mapEntryY == currentMaxY) {

                //code here
                int previousCurrentMaxX = currentMaxX;

                for(int i = currentMaxX; i >= 0 && !maxFoundX; i--) {
                    for(int j = currentMaxY; j >= 0 && !maxFoundX; j--) {

                        //once a non 0 ID is found, then this is current max
                        if(map[j][i] != 0 ) {
                            currentMaxX = i;
                            maxFoundX = true;
                        }
                    }
                }

                if(!maxFoundX) {
                    currentMaxX = MIN_X_CELLS;
                }

                if(currentMaxX < MIN_X_CELLS) {
                    currentMaxX = MIN_X_CELLS;
                }



                for(int j = currentMaxY; j >= 0 && !maxFoundY; j--) {
                    for(int i = currentMaxX; i >= 0 && !maxFoundY; i--) {

                        if(map[j][i] != 0) {
                            currentMaxY = j;
                            maxFoundY = true;
                        }
                    }
                }

                if(!maxFoundY) {
                    Log.d("Boundaries", "Max not found");
                    currentMaxY  = MIN_Y_CELLS;
                }

                if(currentMaxY < MIN_Y_CELLS) {
                    currentMaxY = MIN_Y_CELLS;
                }

            }

            //if currentMax has been erased
            else if(mapEntryX == currentMaxX) {

                //we know currentMax is going to be much smaller than currentMaxX.
                //and we know currentY is going to be max for Y searching.
                for(int i = currentMaxX; i >= 0 && !maxFoundX; i--) {
                    for(int j = currentMaxY; j >= 0 && !maxFoundX; j--) {

                        //once a non 0 ID is found, then this is current max
                        if(map[j][i] != 0 ) {
                            currentMaxX = i;
                            maxFoundX = true;
                        }
                    }
                }

                if(!maxFoundX) {
                    currentMaxX = MIN_X_CELLS;
                }

                if(currentMaxX < MIN_X_CELLS) {
                    currentMaxX = MIN_X_CELLS;
                }
            }

            else if(mapEntryY == currentMaxY) {

                //only need to search from currentMaxY and currentMaxX (must be smaller than these)


                for(int j = currentMaxY; j >= 0 && !maxFoundY; j--) {
                    for(int i = currentMaxX; i >= 0 && !maxFoundY; i--) {

                        if(map[j][i] != 0) {
                            currentMaxY = j;
                            maxFoundY = true;
                        }
                    }
                }

                if(!maxFoundY) {
                    currentMaxY  = MIN_Y_CELLS;
                }

                if(currentMaxY < MIN_Y_CELLS) {
                    currentMaxY = MIN_Y_CELLS;
                }
            }
        }

        Log.d("Boundaries", "Max X: " + currentMaxX + ", Max Y: " + currentMaxY);
    }


    //method to save file into internal storage.
    //need to read from saved file in order to see if saved correctly
    private void parseMapAndsaveFile() {

        String separator = System.getProperty("line.separator");


        try {

            //createFile
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(GameMainActivity.sGame.getContext().openFileOutput(FILE_NAME,
                            Context.MODE_PRIVATE));

            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            //write the dimensions of map first
            bufferedWriter.write(String.valueOf(currentMaxY + 1));
            bufferedWriter.write(",");
            bufferedWriter.write(String.valueOf(currentMaxX + 1));


            //separate lines
            bufferedWriter.write(separator);


            //now write the dimensions of the map, into parser friendly format
            for(int j = 0; j <= currentMaxY; j++) {
                for(int i = 0; i <= currentMaxX; i++) {
                    bufferedWriter.write(String.valueOf(map[j][i]));

                    //do not do a comma on the last line
                    if(i != currentMaxX) {
                        bufferedWriter.write(",");
                    }
                }

                if(j != currentMaxY) {
                    bufferedWriter.write(separator);
                }
            }

            bufferedWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }


        readAndPrintFile(FILE_NAME);
    }


    //to make sure everythings working
    protected void readAndPrintFile(String fileName) {
        String currentLine;

        try {
            //create buffered reading and input streams
            InputStream inputStream = GameMainActivity.sGame.getContext().openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bReader = new BufferedReader(inputStreamReader);

            while ((currentLine = bReader.readLine()) != null) {
                Log.d("currentline", currentLine);
            }

            bReader.close();

        } catch (IOException e) {
            Log.d("Exception", "File read failed: " + e.toString());
        }
    }

}
