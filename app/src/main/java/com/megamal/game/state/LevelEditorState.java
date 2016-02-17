package com.megamal.game.state;

import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.megamal.framework.util.LevelEditorCamera;
import com.megamal.framework.util.Painter;
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


    private boolean mapChanged = true;
    private UIButton exitButton, dragButton;
    private int[][] map;
    private int maskedAction;

    int previousX, previousY;

    private LevelEditorCamera camera;

    @Override
    public void init() {

        //832, 512
        exitButton = new UIButton(750, 440, 830, 510, Assets.exitButton, Assets.exitButtonPressed);

        //stub
        dragButton = new UIButton(0, 0, 0, 0, Assets.earthImage, Assets.exitButtonPressed);

        //stub value to create as big as possible,
        map = new int[100][100];

        //create new camera
        camera = new LevelEditorCamera(map);


    }

    @Override
    public void update(float delta, Painter g) {

    }

    @Override
    public void render(Painter g) {

        if(mapChanged) {
            g.setColor(Color.rgb(80, 143, 240));
            g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);
            mapChanged = false;
        }
        exitButton.render(g);

    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY, int ID, boolean moveAction,
                           View v) {

        //check if moved onto exit button, or changing cameraOffsetButton
        //also will need to check placement of tiles.
        if(moveAction) {


            //then it is safe to set ID to this
            if(!camera.hasIDSet()  /*&& dragButton.isTouched()*/) {
                //calculate distance in X and Y

                previousX = scaledX;
                previousY = scaledY;

                camera.setControllerID(ID);

            }

            //then it is safe to calculate distance
            else if (camera.hasIDSet() /*&& dragButton.isTouched()*/ && ID == camera.getControllerID()) {

                //calculate distance in X and Y
                //dont return just yet, so that exit button is checked

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
                        //no updating necessary, distanceY == 0
                    }
                }

                Log.d("LevelEditor", "Current Y: " + scaledY);
                Log.d("LevelEditor", "Previous Y: " + previousY);

                Log.d("LevelEditor", "****************");


                previousX = scaledX;
                previousY = scaledY;


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

                    else {
                        return true;
                    }
                }

                case (MotionEvent.ACTION_POINTER_DOWN): {
                    if(exitButton.onTouchDown(scaledX, scaledY, ID)) {
                        return true;
                    }

                    else {
                        return true;
                    }
                }


                //IF ACTION_UP is same ID as current camera controller, then ensure previousX = 0 etc
                case (MotionEvent.ACTION_UP): {

                    if(camera.hasIDSet() && ID == camera.getControllerID()) {
                        camera.lockToNearest(map, ID);
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
                    if(exitButton.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new MenuState());
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
}
