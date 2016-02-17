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

    private final static int Y_THRESHOLD_UP = (GameMainActivity.GAME_HEIGHT / 2) - (int) (GameMainActivity.TILE_HEIGHT * 1.5);
    private final static int Y_THRESHOLD_DOWN = (GameMainActivity.GAME_HEIGHT / 2) + (int) (GameMainActivity.TILE_HEIGHT * 1.5);

    private double playerCentreX, maxCameraOffsetX, overRun, maxCameraOffsetY, playerCentreY, cameraOffsetX, cameraOffsetY;


    public Camera(int[][] map) {

        //calculate the maximum the camera can be offsetted for the map in question
        maxCameraOffsetY = (map.length * GameMainActivity.TILE_HEIGHT) - GameMainActivity.GAME_HEIGHT;
        maxCameraOffsetX = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;

    }

    //return
    public double updateCameraX(Player player, double cameraOffsetX, int[][] map) {

        //calculate the x of the players center
        playerCentreX = player.getX() + (GameMainActivity.TILE_HEIGHT / 2);


        //calculate the maximum the camera can be offsetted for the map in question
        //maxCameraOffsetX = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;

        if (player.isRunning() || player.isWalking()) {
            //OffsetX = 0 is the case when mawi can only move camera to the RIGHT
            if (cameraOffsetX == 0) {
                //control for if a certain area of a level is only 13 tiles in the x direction - no scrolling necessary
                if (map[0].length == 13) {

                    this.cameraOffsetX = cameraOffsetX;
                    return cameraOffsetX;
                }
                    //else, player not yet crossed the threshold
                else if (playerCentreX < X_THRESHOLD_RIGHT) {
                    this.cameraOffsetX = 0;
                    return 0;
                }
                    //else, player has crossed threshold, find overRun appropriately and start scrolling
                else if (playerCentreX >= X_THRESHOLD_RIGHT) {

                    if (playerCentreX > X_THRESHOLD_RIGHT) {
                        overRun = playerCentreX - X_THRESHOLD_RIGHT;
                    }

                    else {
                        overRun = 0;
                    }
                    //Log.d("Camera","OverRun is: " + overRun + ". \n");

                    cameraOffsetX = cameraOffsetX + overRun;
                    player.lockToXThreshold(X_THRESHOLD_RIGHT);
                    this.cameraOffsetX = cameraOffsetX;
                    return cameraOffsetX;
                }
                //in this case, mawi can move the camera either to the RIGHT OR the LEFT, therefore
                //must implement deadZones
            } else if (cameraOffsetX > 0 && cameraOffsetX < maxCameraOffsetX) {
                //Log.d("Camera", "Entered cameraOffset > 0 loop");

                //case moving to the right
                //if(playerCentreX > DEAD_ZONE_RIGHT) {
                if (playerCentreX >= X_THRESHOLD_RIGHT && player.isRight()) {
                    if (playerCentreX > X_THRESHOLD_RIGHT) {
                        overRun = playerCentreX - X_THRESHOLD_RIGHT;
                    }
                    else {
                        overRun = 0;
                    }
                    //Log.d("Camera","OverRun is: " + overRun + ". \n");

                    cameraOffsetX = cameraOffsetX + overRun;
                    if (cameraOffsetX > maxCameraOffsetX) {
                        cameraOffsetX = maxCameraOffsetX;
                    }

                    player.lockToXThreshold(X_THRESHOLD_RIGHT);

                    this.cameraOffsetX = cameraOffsetX;
                    return cameraOffsetX;

                    //case moving to the left
                } //else if (playerCentreX < DEAD_ZONE_LEFT) {
                //CASE WHERE going from right -> left result in huge leaps in co-ordsI also learned about the University database as a source of helpful information, along with a reinforced idea of how to collect information of relevance to a specific task.

                else if (playerCentreX <= X_THRESHOLD_LEFT && player.isLeft()) {
                    if (playerCentreX < X_THRESHOLD_LEFT) {
                        overRun = X_THRESHOLD_LEFT - playerCentreX;
                    }
                    else {
                        overRun = 0;
                    }

                    cameraOffsetX = cameraOffsetX - overRun;


                    if (cameraOffsetX < 0) {
                        cameraOffsetX = 0;
                    }
                    player.lockToXThreshold(X_THRESHOLD_LEFT);

                    this.cameraOffsetX = cameraOffsetX;
                    return cameraOffsetX;

                } else {
                    this.cameraOffsetX = cameraOffsetX;
                    return cameraOffsetX;

                }



                //in this case, mawi can only move to the left
            } else if (cameraOffsetX == maxCameraOffsetX) {

                if (playerCentreX > X_THRESHOLD_LEFT) {
                    this.cameraOffsetX = cameraOffsetX;
                    return cameraOffsetX;
                }
                else if (playerCentreX <= X_THRESHOLD_LEFT && player.isLeft()) {

                    if (playerCentreX < X_THRESHOLD_LEFT) {
                        overRun = X_THRESHOLD_LEFT - playerCentreX;
                    }

                    else {
                        overRun = 0;
                    }

                    cameraOffsetX = cameraOffsetX - overRun;
                    player.lockToXThreshold(X_THRESHOLD_LEFT);

                    this.cameraOffsetX = cameraOffsetX;
                    return cameraOffsetX;
                }
            }
        }

        this.cameraOffsetX = cameraOffsetX;
        return cameraOffsetX;
    }

    public double updateCameraY(Player player, double cameraOffsetY, int[][] map) {

        //calculate the y of the players center
        playerCentreY = player.getY() + (GameMainActivity.TILE_HEIGHT);

        //only way that player will be able to reach the suitable y
        if (player.getVelY() != 0) {

            //in this case, camera can only move the camera DOWN
            if (cameraOffsetY == 0) {

                //if a levels height is 8, then no scrolling will be necessary, return ASAP
                if (map.length == 8) {
                    this.cameraOffsetY = cameraOffsetY;
                    return cameraOffsetY;
                }

                //else case where player has not yet crossed the threshold
                else if (playerCentreY < Y_THRESHOLD_DOWN) {
                    //Log.d("YCamera", "not passed threshold on offsetY = 0");
                    return 0;

                } else if (playerCentreY >= Y_THRESHOLD_DOWN) {

                    if (playerCentreY > Y_THRESHOLD_DOWN) {
                        overRun = playerCentreY - Y_THRESHOLD_DOWN;
                    }

                    else {
                        overRun = 0;
                    }

                   // Log.d("YCamera", "player passed threshold on offsetY = 0 w/ overRun: " + overRun);
                    cameraOffsetY = cameraOffsetY + overRun;
                    player.lockToYThreshold(Y_THRESHOLD_DOWN);
                    this.cameraOffsetY = cameraOffsetY;
                    return cameraOffsetY;
                }

            //else case where player can either make camera move down or up
            } else if (cameraOffsetY > 0 && cameraOffsetY < maxCameraOffsetY) {

                //if case where player has passed threshold to bring camera down
                if (playerCentreY >= Y_THRESHOLD_DOWN && player.getVelY() > 0) {
                    if (playerCentreY > Y_THRESHOLD_DOWN) {
                        overRun = playerCentreY - Y_THRESHOLD_DOWN;
                    }
                    else {
                        overRun = 0;
                    }

                   // Log.d("YCamera", "player passed DOWN threshold on offsetY >0 with overRun: " + overRun);
                    cameraOffsetY = cameraOffsetY + overRun;


                    if (cameraOffsetY > maxCameraOffsetY) {
                        cameraOffsetY = maxCameraOffsetY;
                    }

                    player.lockToYThreshold(Y_THRESHOLD_DOWN);
                   // Log.d("YCameraOffset", "Offset is: " + cameraOffsetY);

                    this.cameraOffsetY = cameraOffsetY;
                    return cameraOffsetY;
                }

                //else case where player has passed threshold to bring camera up
                else if (playerCentreY <= Y_THRESHOLD_UP && player.getVelY() < 0) {
                    if (playerCentreY < Y_THRESHOLD_UP) {
                        overRun = Y_THRESHOLD_UP - playerCentreY;
                    }
                    else {
                        overRun = 0;
                    }

                    //Log.d("YCamera", "player passed UP threshold on offsetY >0 with overRun: " + overRun);
                    cameraOffsetY = cameraOffsetY - overRun;

                    if (cameraOffsetY < 0) {
                        cameraOffsetY = 0;
                    }

                    player.lockToYThreshold(Y_THRESHOLD_UP);
                    this.cameraOffsetY = cameraOffsetY;

                    //Log.d("YCameraOffset", "Offset is: " + cameraOffsetY);
                    return cameraOffsetY;
                }

                //else, do nothing
                else {
                    //Log.d("YCameraOffset", "Offset is: " + cameraOffsetY);
                    return cameraOffsetY;
                }
            }

            //else case where Y_Threshold is maximum (aka bottom of screen)
            else if (cameraOffsetY == maxCameraOffsetY) {

                //not passed threshold, leave as is
                if (playerCentreY > Y_THRESHOLD_UP) {
                    return cameraOffsetY;
                }

                else if (playerCentreY <= Y_THRESHOLD_UP) {
                    if (playerCentreY < Y_THRESHOLD_UP) {
                        overRun = Y_THRESHOLD_UP - playerCentreY;
                    }

                    else {
                        overRun = 0;
                    }

                   // Log.d("YCamera", "player passed UP threshold on offsetY = max with overRun: " + overRun);

                    cameraOffsetY = cameraOffsetY - overRun;
                    player.lockToYThreshold(Y_THRESHOLD_UP);
                    this.cameraOffsetY = cameraOffsetY;
                   // Log.d("YCameraOffset", "Offset is: " + cameraOffsetY);
                    return cameraOffsetY;
                }
            }
        }
        this.cameraOffsetY = cameraOffsetY;
        return cameraOffsetY;
    }

    public double getCameraOffsetX() {
        return cameraOffsetX;
    }

    public double getCameraOffsetY() {
        return cameraOffsetY;
    }
}
