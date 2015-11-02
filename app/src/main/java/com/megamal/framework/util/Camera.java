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
    private final static int DEAD_ZONE_RIGHT = 7 * 64;
    private final static int DEAD_ZONE_LEFT = 6 * 64;

    private double playerCentreX, maxCameraOffsetX;


    //return
    public int updateCameraX(Player player, int cameraOffsetX, int[][] map) {

        //calculate the x of the players center
        playerCentreX = player.getX() + (GameMainActivity.TILE_HEIGHT / 2);


        //calculate the maximum the camera can be offsetted for the map in question
        maxCameraOffsetX = map[0].length * GameMainActivity.TILE_WIDTH;

        if (player.isJumping() || player.isRunning() || player.isWalking()) {
            //OffsetX = 0 is the case when mawi can only move camera to the RIGHT
            if (cameraOffsetX == 0) {
                if (map[0].length == 13)
                    return 0;
                else if (playerCentreX < X_THRESHOLD)
                    return 0;
                else if (playerCentreX >= X_THRESHOLD) {
                    cameraOffsetX = cameraOffsetX + CAMERA_MOVEMENT;
                    return cameraOffsetX;
                }
            //in this case, mawi can move the camera either to the RIGHT OR the LEFT, therefore
            //must implement deadZones
            } else if (cameraOffsetX > 0 & cameraOffsetX < maxCameraOffsetX) {

                //case moving to the right
                if(playerCentreX > DEAD_ZONE_RIGHT) {
                    cameraOffsetX = cameraOffsetX + CAMERA_MOVEMENT;
                    Log.d("CameraOffset", "cameraOffSet set to: " + cameraOffsetX + ". \n");
                    return cameraOffsetX;

                //case moving to the left
                } else if (playerCentreX < DEAD_ZONE_LEFT) {
                    cameraOffsetX = cameraOffsetX - CAMERA_MOVEMENT;
                    return cameraOffsetX;
                } else
                    return 0;

            //in this case, mawi can only move to the left
            } else if (cameraOffsetX == maxCameraOffsetX) {
                if (playerCentreX > X_THRESHOLD)
                    return 0;
                else if (playerCentreX <= X_THRESHOLD) {
                    cameraOffsetX = cameraOffsetX - CAMERA_MOVEMENT;
                    return cameraOffsetX;
                }
            }
        }
        return 0;
    }
}
