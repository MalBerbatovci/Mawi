package com.megamal.framework.util;

import android.util.Log;

import com.megamal.game.model.Player;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 02/11/15.
 */
public class Camera {

    private final static int X_THRESHOLD_RIGHT = (GameMainActivity.GAME_WIDTH / 2) + GameMainActivity.TILE_WIDTH;
    private final static int X_THRESHOLD_LEFT = (GameMainActivity.GAME_WIDTH / 2) - GameMainActivity.TILE_WIDTH;
    private final static int RIGHT = 1;
    private final static int LEFT = -1;

    private double playerCentreX, maxCameraOffsetX,overRun;



    //return
    public double updateCameraX(Player player, double cameraOffsetX, int[][] map) {

        //calculate the x of the players center
        playerCentreX = player.getX() + (GameMainActivity.TILE_HEIGHT / 2);


        //calculate the maximum the camera can be offsetted for the map in question
        maxCameraOffsetX = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;

        if (player.isJumping() || player.isRunning() || player.isWalking()) {
            //OffsetX = 0 is the case when mawi can only move camera to the RIGHT
            if (cameraOffsetX == 0) {
                //control for if a certain area of a level is only 13 tiles in the x direction - no scrolling necessary
                if (map[0].length == 13)
                    return cameraOffsetX;
                //else, player not yet crossed the threshold
                else if (playerCentreX < X_THRESHOLD_RIGHT)
                    return 0;
                //else, player has crossed threshold, find overRun appropriately and start scrolling
                else if (playerCentreX >= X_THRESHOLD_RIGHT) {
                    if(playerCentreX > X_THRESHOLD_RIGHT)
                        overRun = playerCentreX - X_THRESHOLD_RIGHT;
                    else
                        overRun = 0;
                    //Log.d("Camera","OverRun is: " + overRun + ". \n");
                    cameraOffsetX = cameraOffsetX + overRun;
                    player.lockToXThreshold(X_THRESHOLD_RIGHT);
                    return cameraOffsetX;
                }
            //in this case, mawi can move the camera either to the RIGHT OR the LEFT, therefore
            //must implement deadZones
            } else if (cameraOffsetX > 0 && cameraOffsetX < maxCameraOffsetX) {
                //Log.d("Camera", "Entered cameraOffset > 0 loop");

                //case moving to the right
                //if(playerCentreX > DEAD_ZONE_RIGHT) {
                if (playerCentreX >= X_THRESHOLD_RIGHT && player.isRight()) {
                    if(playerCentreX > X_THRESHOLD_RIGHT)
                        overRun = playerCentreX - X_THRESHOLD_RIGHT;
                    else
                        overRun = 0;
                    //Log.d("Camera","OverRun is: " + overRun + ". \n");
                    cameraOffsetX = cameraOffsetX + overRun;
                    if (cameraOffsetX > maxCameraOffsetX)
                        cameraOffsetX = maxCameraOffsetX;
                    player.lockToXThreshold(X_THRESHOLD_RIGHT);
                    return cameraOffsetX;

                //case moving to the left
                } //else if (playerCentreX < DEAD_ZONE_LEFT) {
                //CASE WHERE going from right -> left result in huge leaps in co-ordsI also learned about the University database as a source of helpful information, along with a reinforced idea of how to collect information of relevance to a specific task.

                else if (playerCentreX <= X_THRESHOLD_LEFT && player.isLeft()) {
                    if(playerCentreX < X_THRESHOLD_LEFT)
                        overRun = X_THRESHOLD_LEFT - playerCentreX;
                    else
                        overRun = 0;
                    cameraOffsetX = cameraOffsetX - overRun;
                    if (cameraOffsetX < 0)
                        cameraOffsetX = 0;
                    player.lockToXThreshold(X_THRESHOLD_LEFT);
                    return cameraOffsetX;
                } else {
                    return cameraOffsetX;

                }
            //in this case, mawi can only move to the left
            } else if (cameraOffsetX == maxCameraOffsetX) {
                if (playerCentreX > X_THRESHOLD_LEFT)
                    return cameraOffsetX;
                else if (playerCentreX <= X_THRESHOLD_LEFT && player.isLeft()) {
                    if(playerCentreX < X_THRESHOLD_LEFT)
                        overRun = X_THRESHOLD_LEFT - playerCentreX;
                    else
                        overRun = 0;
                    cameraOffsetX = cameraOffsetX - overRun;
                    player.lockToXThreshold(X_THRESHOLD_LEFT);
                    return cameraOffsetX;
                }
            }
        }
        return cameraOffsetX;
    }
}
