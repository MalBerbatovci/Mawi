package com.megamal.game.state;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.megamal.framework.util.LevelEditorCamera;
import com.megamal.framework.util.Painter;
import com.megamal.framework.util.RandomNumberGenerator;
import com.megamal.framework.util.TileMapRenderer;
import com.megamal.framework.util.UIButton;
import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 12/02/16.
 */
public class LevelEditorState extends State {

    private final static int LEFT = -1;
    private final static int DOWN = -1;
    private final static int RIGHT = 1;
    private final static int UP = 1;

    protected int previousMapX = -1;
    protected int previousMapY = -1;


    private boolean mapChanged = true;
    private UIButton exitButton, dragButton, wrenchButton;
    private int[][] map;
    private int maskedAction;

    int previousX, previousY;

    private LevelEditorCamera camera;
    private TileMapRenderer tileMapRenderer;

    @Override
    public void init() {

        //832, 512
        exitButton = new UIButton(750, 440, 830, 510, Assets.exitButton, Assets.exitButtonPressed);


        dragButton = new UIButton(670, 440, 734, 504, Assets.movingTool, Assets.movingToolUsed);

        wrenchButton = new UIButton(20, 440, 84, 504, Assets.wrenchTool, Assets.wrenchToolInUse);



        //stub value to create as big as possible,
        map = new int[100][100];

        for(int i = 0; i < 100; i++) {
            for(int j = 0; j < 100; j++) {
                if(RandomNumberGenerator.getRandInt(50) < 25) {
                    map[i][j] = 1;
                }
                else {
                    map[i][j] = 0;
                }
            }
        }

        tileMapRenderer = new TileMapRenderer(map);

        //create new camera
        camera = new LevelEditorCamera(map);


    }

    @Override
    public void update(float delta, Painter g) {

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
        dragButton.render(g);
        wrenchButton.render(g);

    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY, int ID, boolean moveAction,
                           View v) {

        //check if moved onto exit button, or changing cameraOffsetButton
        //also will need to check placement of tiles.
        if(moveAction) {


           /* if (!dragButton.isTouched() && dragButton.buttonMovedOut(scaledX, scaledY, ID)) {
                return true;
            }

            else if (!dragButton.isTouched() && dragButton.buttonMovedOn(scaledX, scaledY, ID)) {
                Log.d("dragButton", "Button movedOn");
                return true;
            } */

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
                        camera.updateCameraX(map, distanceX, LEFT, ID);
                    }

                    //moving RIGHT
                    else if (distanceX > 0) {
                        camera.updateCameraX(map, distanceX, RIGHT, ID);

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
                        camera.updateCameraY(map, distanceY, UP, ID);
                    }

                    //MOVING DOWN
                    else if (distanceY > 0) {
                        camera.updateCameraY(map, distanceY, DOWN, ID);

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

                return true;


            }

            if(exitButton.buttonMovedOn(scaledX, scaledY, ID)) {
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

                    else if (dragButton.isTouched() &&
                            dragButton.isContained(scaledX, scaledY)) {

                        Log.d("dragButton", "isTouched && ID is the same");
                        if(dragButton.onTouchDownOff(scaledX, scaledY)) {
                            return true;
                        }
                    }

                    else if (dragButton.onTouchDown(scaledX, scaledY, ID)) {

                        if(wrenchButton.isTouched()) {
                            wrenchButton.forceTouchOff();
                        }
                        return true;
                    }

                    else if (wrenchButton.isTouched() &&
                            wrenchButton.isContained(scaledX, scaledY)) {

                        if(wrenchButton.onTouchDownOff(scaledX, scaledY)) {

                            //in order to remove
                            mapChanged = true;
                            return true;
                        }
                    }

                    else if (wrenchButton.onTouchDown(scaledX, scaledY, ID)) {

                        if(dragButton.isTouched()) {
                            dragButton.forceTouchOff();
                            mapChanged = true;
                        }

                        return true;
                    }

                    else if(wrenchButton.isTouched() &&
                            !wrenchButton.isContained(scaledX, scaledY)) {

                        //in this case, when this means we have just placed finger onto blank screen
                        //ready to draw!

                        drawTile(scaledX, scaledY);
                    }

                    else {
                        return true;
                    }
                }

                case (MotionEvent.ACTION_POINTER_DOWN): {
                    if(exitButton.onTouchDown(scaledX, scaledY, ID)) {
                        return true;
                    }

                    else if (dragButton.isTouched() &&
                            dragButton.isContained(scaledX, scaledY)) {

                        Log.d("dragButton", "isTouched && ID is the same");

                        if(dragButton.onTouchDownOff(scaledX, scaledY)) {
                            return true;
                        }
                    }

                    else if (dragButton.onTouchDown(scaledX, scaledY, ID)) {

                        //cannot have both drag and place tiles at the same time
                        if(wrenchButton.isTouched()) {
                            wrenchButton.forceTouchOff();
                        }

                        return true;
                    }

                    else if (wrenchButton.isTouched() &&
                            wrenchButton.isContained(scaledX, scaledY)) {

                        if(wrenchButton.onTouchDownOff(scaledX, scaledY)) {
                            return true;
                        }
                    }

                    else if (wrenchButton.onTouchDown(scaledX, scaledY, ID)) {

                        if(dragButton.isTouched()) {
                            dragButton.forceTouchOff();
                        }
                        return true;
                    }
                    
                    else if(wrenchButton.isTouched() &&
                            !wrenchButton.isContained(scaledX, scaledY)) {
                        
                        //in this case, when this means we have just placed finger onto blank screen
                        //ready to draw!
                        
                        drawTile(scaledX, scaledY);
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


                    if(exitButton.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new MenuState());
                        return true;
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

                    else {
                        return true;
                    }
                }
            }
        }

        return true;
    }

    private void drawTile(int scaledX, int scaledY) {

        int mapEntryX = (int) Math.floor((scaledX + camera.getX())
                / GameMainActivity.TILE_WIDTH);
        int mapEntryY = (int) Math.floor((scaledY + camera.getY())
                    / GameMainActivity.TILE_HEIGHT);

        //
        if(previousMapX == mapEntryX &&
                previousMapY == mapEntryY) {

            return;
        }

        else {

            if(map[mapEntryY][mapEntryX] == 0) {
                map[mapEntryY][mapEntryX] = 1;
            }

            previousMapY = mapEntryY;
            previousMapX = mapEntryX;

            mapChanged = true;
        }


    }
}
