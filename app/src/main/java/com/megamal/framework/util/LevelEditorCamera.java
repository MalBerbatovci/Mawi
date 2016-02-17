package com.megamal.framework.util;

import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 17/02/16.
 */
public class LevelEditorCamera {

    private double cameraOffsetX, cameraOffsetY, maxCameraOffsetX, maxCameraOffsetY;

    private final static int LEFT = -1;
    private final static int RIGHT = -1;

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

    public void updateCameraX(int[][] map, double movementX, int direction, int ID) {


        //only carry out calculations if the controller ID is this pointer ID
        if(ID == controllerID) {

            double tempCameraOffsetX = this.cameraOffsetX;

            //if swipe movement moved right
            if (direction == RIGHT) {

                //cannot move more to the right, so keep offset at max and return,
                //when rendering - nothing will happen
                if (tempCameraOffsetX == maxCameraOffsetX) {
                    return;
                }


            }

            //else if swipe movement moving left
            else if (direction == LEFT) {

                //cannot move to the left, so keep offset the same
                if (tempCameraOffsetX == 0) {
                    return;
                }

            }
        }

    }

    public void updateCameraY(int[][] map, double movementY, int direction, int ID) {

        //make sure that pointer that initialised is controller movement
        if(controllerID == ID) {

            if (direction == UP) {

            } else if (direction == DOWN) {

            }
        }

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

        }

        double remainderX = cameraOffsetX % GameMainActivity.TILE_WIDTH;

        if(remainderX == 0) {
            //do nothing
        }

        //else, if more than half, clip to next closest on the RIGHT
        else if(remainderX > (GameMainActivity.TILE_WIDTH / 2)) {

            cameraOffsetX = cameraOffsetX + (GameMainActivity.TILE_WIDTH - remainderX);
        }

        //else, less than half way, clip to next closest on the LEFT
        else {
            cameraOffsetX = cameraOffsetX - remainderX;
        }


        double remainderY = cameraOffsetY % GameMainActivity.TILE_HEIGHT;

        if(remainderY == 0) {
            //do nothing
        }

        //else, if more than half, clip to next closest DOWN
        else if (remainderY > (GameMainActivity.TILE_HEIGHT / 2)) {
            cameraOffsetY = cameraOffsetY + (GameMainActivity.TILE_HEIGHT - remainderX);
        }

        //else, if less than half, clip to next closest UP
        else {
            cameraOffsetY = cameraOffsetY - remainderY;
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

}
