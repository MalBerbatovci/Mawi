package com.megamal.framework.util;

import android.util.Log;

import com.megamal.game.model.Player;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 02/11/15.
 */
public class Camera {
    private final static int X_THRESHOLD = (GameMainActivity.GAME_WIDTH / 2);
    private final static int CAMERA_MOVEMENT = (GameMainActivity.TILE_WIDTH / 4);

    //ERROR IS HERE; 7 AND 6 DO NOT REMAIN STATIC
    private final static int DEAD_ZONE_RIGHT = X_THRESHOLD + (GameMainActivity.TILE_WIDTH / 2);
    private final static int DEAD_ZONE_LEFT = X_THRESHOLD - (GameMainActivity.TILE_WIDTH / 2);

    private final static int RIGHT = 1;
    private final static int LEFT = -1;

    private double playerCentreX, maxCameraOffsetX,overRun;



    //return
    public int updateCameraX(Player player, int cameraOffsetX, int[][] map) {

        //calculate the x of the players center
        playerCentreX = player.getX() + (GameMainActivity.TILE_HEIGHT / 2);


        //calculate the maximum the camera can be offsetted for the map in question
        maxCameraOffsetX = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;

        if (player.isJumping() || player.isRunning() || player.isWalking()) {
            //OffsetX = 0 is the case when mawi can only move camera to the RIGHT
            if (cameraOffsetX == 0) {
                if (map[0].length == 13)
                    return cameraOffsetX;
                else if (playerCentreX < X_THRESHOLD)
                    return cameraOffsetX;
                else if (playerCentreX >= X_THRESHOLD) {
                    if(playerCentreX > X_THRESHOLD)
                        overRun = playerCentreX - X_THRESHOLD;
                    else
                        overRun = 0;
                    Log.d("Camera","OverRun is: " + overRun + ". \n");
                    cameraOffsetX = cameraOffsetX + CAMERA_MOVEMENT;
                    player.setCenterX(cameraOffsetX, RIGHT, overRun);
                    return cameraOffsetX;
                }
            //in this case, mawi can move the camera either to the RIGHT OR the LEFT, therefore
            //must implement deadZones
            } else if (cameraOffsetX > 0 & cameraOffsetX < maxCameraOffsetX) {
                Log.d("Camera", "Entered cameraOffset > 0 loop");

                //case moving to the right
                //if(playerCentreX > DEAD_ZONE_RIGHT) {
                if (playerCentreX >= X_THRESHOLD && player.isRight()) {
                    if(playerCentreX > X_THRESHOLD)
                        overRun = playerCentreX - X_THRESHOLD;
                    else
                        overRun = 0;
                    Log.d("Camera","OverRun is: " + overRun + ". \n");
                    cameraOffsetX = cameraOffsetX + CAMERA_MOVEMENT;
                    player.setCenterX(cameraOffsetX, RIGHT, overRun);
                    Log.d("CameraOffset", "cameraOffSet set to: " + cameraOffsetX + ". \n");
                    return cameraOffsetX;

                //case moving to the left
                } //else if (playerCentreX < DEAD_ZONE_LEFT) {
                else if (playerCentreX <= X_THRESHOLD && player.isLeft()) {
                    if(playerCentreX < X_THRESHOLD)
                        overRun = X_THRESHOLD - playerCentreX;
                    else
                        overRun = 0;
                    cameraOffsetX = cameraOffsetX - CAMERA_MOVEMENT;
                    player.setCenterX(cameraOffsetX, LEFT, overRun);
                    return cameraOffsetX;
                } else {
                    Log.d("Camera","Left just returning cameraOffsetX");
                    return cameraOffsetX;

                }
            //in this case, mawi can only move to the left
            } else if (cameraOffsetX == maxCameraOffsetX) {
                if (playerCentreX > X_THRESHOLD)
                    return cameraOffsetX;
                else if (playerCentreX <= X_THRESHOLD && player.isLeft()) {
                    if(playerCentreX < X_THRESHOLD)
                        overRun = X_THRESHOLD - playerCentreX;
                    else
                        overRun = 0;
                    cameraOffsetX = cameraOffsetX - CAMERA_MOVEMENT;
                    player.setCenterX(cameraOffsetX, LEFT, overRun);
                    return cameraOffsetX;
                }
            }
        }
        return cameraOffsetX;
    }
}
