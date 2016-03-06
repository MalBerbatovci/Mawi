package com.megamal.framework.util;

import android.util.Log;

import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 17/02/16.
 */
public class LevelEditorCamera {

    private int cameraOffsetX, cameraOffsetY, maxCameraOffsetX, maxCameraOffsetY;

    private final static int LEFT = -1;
    private final static int RIGHT = 1;

    private final static int DOWN = -1;
    private final static int UP = 1;

    private boolean IDSet = false;
    private int controllerID = -1;

    public LevelEditorCamera(int[][] map) {

        //calculate the maximum the camera can be offsetted for the map in question
        maxCameraOffsetY = (map.length * GameMainActivity.TILE_HEIGHT) - GameMainActivity.GAME_HEIGHT;
        maxCameraOffsetX = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;


        this.cameraOffsetX = 0;
        this.cameraOffsetY = 0;
    }

    public void updateCameraX(int movementX, int direction, int ID) {


        //only carry out calculations if the controller ID is this pointer ID
        if(ID == controllerID) {

            //if swipe movement moved right
            if (direction == RIGHT) {

                //cannot move more to the right, so keep offset at max and return,
                //when rendering - nothing will happen
                if (cameraOffsetX == maxCameraOffsetX) {
                    return;
                }

                cameraOffsetX = cameraOffsetX + movementX;


                //bounds checking
                if(cameraOffsetX > maxCameraOffsetX) {

                    cameraOffsetX = maxCameraOffsetX;
                }


            }

            //else if swipe movement moving left
            else if (direction == LEFT) {

                //cannot move to the left, so keep offset the same
                if (cameraOffsetX == 0) {
                    return;
                }

                cameraOffsetX = cameraOffsetX - movementX;

                //bounds checking
                if(cameraOffsetX < 0) {
                    cameraOffsetX = 0;
                }

            }
        }

    }

    public void updateCameraY(int movementY, int direction, int ID) {

        //make sure that pointer that initialised is controller movement
        if(controllerID == ID) {

            if (direction == UP) {

                //cannot be moved past this, return
                if(cameraOffsetY == 0) {
                    return;
                }

                cameraOffsetY = cameraOffsetY - movementY;

                if(cameraOffsetY < 0) {
                    cameraOffsetY = 0;
                }


            //update location of pointer
            } else if (direction == DOWN) {

                //cannot be moved past this
                if(cameraOffsetY == maxCameraOffsetY) {
                    return;
                }

                cameraOffsetY = cameraOffsetY + movementY;

                //boundary checking
                if(cameraOffsetY > maxCameraOffsetY) {
                    cameraOffsetY = maxCameraOffsetY;
                }

            }
        }

        //else, not controller by this pointer
        else {
            return;
        }

    }


    //called on a touch-up event
    public void lockToNearest(int[][] map, int ID) {

        //if controller ID, then lock appropriately and
        if(ID == controllerID) {

            //do logistics
            controllerID = -1;
            IDSet = false;

            int remainderX = cameraOffsetX % GameMainActivity.TILE_WIDTH;

            if (remainderX == 0) {
                //do nothing
                Log.d("LECamera", "RemainderX == 0");
            }

            //else, if more than half, clip to next closest on the RIGHT
            else if (remainderX >= (GameMainActivity.TILE_WIDTH / 2)) {

                cameraOffsetX = cameraOffsetX + (GameMainActivity.TILE_WIDTH - remainderX);
                Log.d("LECamera", "RemainderX >=");
            }

            //else, less than half way, clip to next closest on the LEFT
            else {
                cameraOffsetX = cameraOffsetX - remainderX;
                Log.d("LECamera", "Else");
            }


            int remainderY = cameraOffsetY % GameMainActivity.TILE_HEIGHT;

            if (remainderY == 0) {
                //do nothing
            }

            //else, if more than half, clip to next closest DOWN
            else if (remainderY >= (GameMainActivity.TILE_HEIGHT / 2)) {
                cameraOffsetY = cameraOffsetY + (GameMainActivity.TILE_HEIGHT - remainderY);
            }

            //else, if less than half, clip to next closest UP
            else {
                cameraOffsetY = cameraOffsetY - remainderY;
            }
        }

        else {
            Log.d("LECamera", "ControllerID != ID");
        }

    }

    public boolean hasIDSet() {
        return IDSet;
    }

    public int getControllerID() {
        return controllerID;
    }

    public void setControllerID(int ID) {
        //if ID not set, set ID so we can track which pointer it is
        if(!IDSet) {
            this.controllerID = ID;
            IDSet = true;

            //just set, so no historical data
            return;
        }
    }

    public double getX() {
        return cameraOffsetX;
    }

    public double getY() {
        return cameraOffsetY;
    }

    protected void forceNoID() {
        IDSet = false;
    }

    protected void forceOffsetX(int offsetX) {
        cameraOffsetX = offsetX;
    }

    protected void forceOffsetY(int offsetY) {
        cameraOffsetY = offsetY;
    }

}
