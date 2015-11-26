package com.megamal.framework.util;

import android.graphics.Color;
import android.util.Log;

import com.megamal.game.model.Player;
import com.megamal.mawi.GameMainActivity;

import java.io.IOException;

/**
 * Created by malberbatovci on 08/10/15.
 */


//class responsible for modelling TileMap on screen
public class TileMapRenderer {
    Tile currentTile;

    //private int remainderX, RemainderY;
    private int xStart, yStart;
    private double startingX, startingY;

    private int maxCameraOffsetX;


    //variables to represent number of tiles that can fit on the screen in each axis
    //this is used in order to only render tiles that can currently be seen on the screen
    private static final int SCREEN_TILE_SIZE_Y = GameMainActivity.GAME_HEIGHT /
            GameMainActivity.TILE_HEIGHT;
    private static final int SCREEN_TILE_SIZE_X = GameMainActivity.GAME_WIDTH /
            GameMainActivity.TILE_WIDTH;

    public TileMapRenderer(int[][] map) {
        currentTile = new Tile(0);
        maxCameraOffsetX = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;
    }



    //method to render map
    public void renderMap(Painter g, int[][] map, double cameraOffsetX, double cameraOffsetY,
                          double previousX, double previousY, Player mawi) {

        //maxCameraOffsetX = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;

        //in this case, only an area of 1 tile around mawi needs to be rendered
        if (cameraOffsetX == previousX && cameraOffsetY == previousY && mawi.hasMoved(cameraOffsetX, cameraOffsetY)) {
            Log.d("Render", "Rendering case when cameraOffset stayed same but movement detected");


            //get the starting map index for X
            xStart = (int) Math.ceil((mawi.getX() + cameraOffsetX) / GameMainActivity.TILE_WIDTH);
            xStart = xStart - 2;

            int xStartScreen = (int) Math.ceil(mawi.getX() / GameMainActivity.TILE_WIDTH) - 2;

            //get the starting map index for Y
            yStart = (int) Math.ceil((mawi.getY() + cameraOffsetY) / GameMainActivity.TILE_HEIGHT);
            yStart = yStart - 2;

            int yStartScreen = (int) Math.ceil(mawi.getY() / GameMainActivity.TILE_HEIGHT) - 2;

            //now refresh suitable area of screen
            g.setColor(Color.rgb(208, 244, 247));
            g.fillRect((xStartScreen * GameMainActivity.TILE_WIDTH), (yStartScreen * GameMainActivity.TILE_HEIGHT),
                    GameMainActivity.TILE_WIDTH * 4, GameMainActivity.TILE_HEIGHT * 5);

            Log.d("YRendering", "yStart: " + yStart + ". xStart: " + xStart);

            //now render suitable tiles in place
            for (int y = (yStart - 1); y < yStart + 6; y++) {
                for (int x = (xStart - 1); x < xStart + 5; x++) {

                    if (y >= 0 && x >= 0 && x < map[0].length && y < map.length) {
                        currentTile.setID(map[y][x]);
                        if (currentTile.getImage() != null) {
                            currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                            g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());
                        }
                    }
                }
            }
            return;

            //case when offset has shifted, whole map must be rendered!
        } else if (cameraOffsetX != previousX || cameraOffsetY != previousY) {
            renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);

            //else player has just entered cameraOffsetX, needs whole screen rendered
        } else if (cameraOffsetX == previousX && cameraOffsetY == previousY && !mawi.hasMoved(cameraOffsetX, cameraOffsetY)) {
            if (mawi.justGrounded()) {
                renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);
            }
            return;

        }
    }

    public void renderWholeMap(Painter g, int[][] map, double cameraOffsetX, double cameraOffsetY) {
        g.setColor(Color.rgb(208, 244, 247));
        g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);

        if (cameraOffsetX == 0 && cameraOffsetY == 0) {
            for (int y = 0; y < SCREEN_TILE_SIZE_Y; y++) {
                for (int x = 0; x < SCREEN_TILE_SIZE_X; x++) {

                    if (y >= 0 && x >= 0) {
                        currentTile.setID(map[y][x]);
                        currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                        if (currentTile.getImage() != null)
                            g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());
                    }

                }
            }
        } else if (cameraOffsetX == maxCameraOffsetX && cameraOffsetY == maxCameraOffsetX) {
            startingX = Math.ceil(cameraOffsetX / GameMainActivity.TILE_WIDTH);
            startingY = Math.ceil(cameraOffsetY / GameMainActivity.TILE_HEIGHT);
            for (int y = (int) startingY; y < (SCREEN_TILE_SIZE_Y + startingY); y++) {
                for (int x = (int) startingX; x < (SCREEN_TILE_SIZE_X + startingX); x++) {

                    if(y <= map.length && x <= map[0].length) {
                        currentTile.setID(map[y][x]);
                        currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                        if (currentTile.getImage() != null)
                            g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());
                    }

                }
            }
        } else {

            startingX = Math.ceil(cameraOffsetX / GameMainActivity.TILE_WIDTH);
            startingY = Math.ceil(cameraOffsetY / GameMainActivity.TILE_HEIGHT);

            //Log.d("Camera", "1: remainderX is: " + remainderX + ". \n");
            Log.d("Camera", "2: StartingY is: " + startingY + ". Camera OffsetY is: " + cameraOffsetY + ". \n");

            for (int y = (int) (startingY - 1); y < (SCREEN_TILE_SIZE_Y + (int) startingY); y++) {
                for (int x = (int) (startingX - 1); x < (SCREEN_TILE_SIZE_X + (int) startingX); x++) {

                    if (y >= 0 && x >= 0 && y <= map.length && x <= map[0].length) {
                        currentTile.setID(map[y][x]);
                        currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                        if (currentTile.getImage() != null)
                            g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());
                    }
                }
            }

        }
    }

    public void renderMapCollectable(Painter g, int[][] map, double cameraOffsetX, double cameraOffsetY, double positionX,
                              double positionY, boolean backGroundFill) {

        //ONLY NEEDS TO BE CALLED WHEN COLLECTABLE FALLING really

        /*g.setColor(Color.rgb(208, 244, 247));
        g.fillRect((int) (positionX - cameraOffsetX), (int) (positionY - cameraOffsetY),
                GameMainActivity.TILE_WIDTH, GameMainActivity.TILE_HEIGHT);*/

        xStart = (int) Math.ceil(positionX / GameMainActivity.TILE_WIDTH) - 1;
        Log.d("RenderingCollectable", "xStart is: " + xStart + ".\n");

        yStart = (int) Math.ceil(positionY / GameMainActivity.TILE_WIDTH) - 1;
        Log.d("RenderingCollectable", "yStart is: " + yStart + ".\n");

        for (int y = yStart; y < (yStart + 2); y++) {
            for (int x = xStart; x < (xStart + 2); x++) {
                if (y >= 0 && x >= 0 && y < map.length && x < map[0].length) {

                    currentTile.setID(map[y][x]);
                    if (currentTile.getImage() == null) {
                        if(backGroundFill) {
                        g.setColor(Color.rgb(208, 244, 247));
                        g.fillRect(((int)((x * GameMainActivity.TILE_WIDTH) - cameraOffsetX)), (int) ((y * GameMainActivity.TILE_HEIGHT) - cameraOffsetY),
                                GameMainActivity.TILE_WIDTH, GameMainActivity.TILE_HEIGHT);
                        }
                    } else {
                        currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                        g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());
                    }
                }

            }
        }
        /*if (cameraOffsetX == previousX && cameraOffsetY == previousY && mawi.hasMoved(cameraOffsetX, cameraOffsetY)) {

            //now refresh suitable area of screen
            g.setColor(Color.rgb(208, 244, 247));
            g.fillRect((xStartScreen * GameMainActivity.TILE_WIDTH), (yStartScreen * GameMainActivity.TILE_HEIGHT),
                    GameMainActivity.TILE_WIDTH * 4, GameMainActivity.TILE_HEIGHT * 5);

            Log.d("YRendering", "yStart: " + yStart + ". xStart: " + xStart);

            //now render suitable tiles in place
            for (int y = (yStart - 1); y < yStart + 6; y++) {
                for (int x = (xStart - 1); x < xStart + 5; x++) {

                    if (y >= 0 && x >= 0 && x < map[0].length && y < map.length) {
                        currentTile.setID(map[y][x]);
                        if (currentTile.getImage() != null) {
                            currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                            g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());
                        }
                    }
                }
            }
            return;*/

    }
}
