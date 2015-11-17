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
    TileMapFactory tileFactory = new TileMapFactory();

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

    public TileMapRenderer() {
        currentTile = new Tile(0);
    }

    //WHEN IMPLEMENTING COLLISIONS: use the scan along line technique to create smaller Tile array
    //which will hold the necessary rects for collision checking. (renderMap method continuously
    //overwrites itself so rect is no stored)
    //NB: make sure said array is of size: character_height_in_tiles * screen_tile_width.
    //create array outside of loops, set rects inside loop to check collisions


    //WHEN IMPLEMENTING CAMERA - CALCULATE WHERE TO START THE FOR LOOP FOR X AND Y
    //AND THEN CHECK Y < (SCREEN_TILE_SIZE_Y + initialY) - SAME FOR X.
    //method to render map
    public void renderMap(Painter g, int[][] map, double cameraOffsetX, double cameraOffsetY,
                          double previousX, double previousY, Player mawi) {

        maxCameraOffsetX = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;

        //in this case, only an area of 1 tile around mawi needs to be rendered
        if (cameraOffsetX == 0 && mawi.hasMoved(cameraOffsetX, cameraOffsetY) && (int) previousX == 0) {

            Log.d("Render", "Rendering case when cameraOffset stayed as 0 and movement detected");


            //get the starting map index for X
            xStart = (int) Math.ceil(mawi.getX() / GameMainActivity.TILE_WIDTH);
            xStart = xStart - 2;

            //get the starting map index for Y
            yStart = (int) Math.ceil(mawi.getY() / GameMainActivity.TILE_HEIGHT);
            yStart = yStart - 2;

            //now refresh suitable area of screen
            g.setColor(Color.rgb(208, 244, 247));
            g.fillRect((xStart * GameMainActivity.TILE_WIDTH), (yStart * GameMainActivity.TILE_HEIGHT),
                    GameMainActivity.TILE_WIDTH * 4, GameMainActivity.TILE_HEIGHT * 5);

            //now render suitable tiles in place
            for (int y = yStart; y < yStart + 5; y++) {
                for (int x = xStart; x < xStart + 4; x++) {

                    if (y >= 0 && x >= 0) {
                        currentTile.setID(map[y][x]);
                        if (currentTile.getImage() != null) {
                            currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                            g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());
                        }
                    }
                }
            }
            return;

            //else player has just entered cameraOffsetX, needs whole screen rendered
        } else if (cameraOffsetX == 0 && mawi.hasMoved(cameraOffsetX, cameraOffsetY) && (int) previousX != 0) {
            renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);
            Log.d("Render", "Rendering case when cameraOffset has just become 0 and movement detected");
            return;

            //in this case, nothing new needs to be rendered
        } else if (cameraOffsetX == 0 && !mawi.hasMoved(cameraOffsetX, cameraOffsetY)) {
            if (mawi.justGrounded()) {
                renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);
                Log.d("Render", "Rendering case when cameraOffset = 0 and no movement but mawi just grounded");
            }
            Log.d("Render", "NO Rendering case when cameraOffset = 0 and no movement detected");
            return;

            //in this case, then the two tiles on the end of the screen need to be drawn at a displacement and
            //whole screen needs to be rendered.
        } else if (cameraOffsetX < maxCameraOffsetX && cameraOffsetX != 0 && mawi.hasMoved(cameraOffsetX, cameraOffsetY)) {

            if (previousX != cameraOffsetX) {
                renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);
                Log.d("Render", "Rendering case when moving through offset > 0 and offset Moving (Whole screen)");
            } else {

                Log.d("Render", "Rendering case when cameraOffset > 0 but movement  within deadZone");


                //get the starting map index for X
                xStart = (int) Math.ceil((mawi.getX() + cameraOffsetX) / GameMainActivity.TILE_WIDTH);
                xStart = xStart - 2;

                int xStartScreen = (int) Math.ceil(mawi.getX()/ GameMainActivity.TILE_WIDTH) - 2;

                //get the starting map index for Y
                //MUST PLUS YSTART TO OFFSETY
                yStart = (int) Math.ceil((mawi.getY() / GameMainActivity.TILE_HEIGHT));
                yStart = yStart - 2;

                //now refresh suitable area of screen
                g.setColor(Color.rgb(208, 244, 247));
                g.fillRect(xStartScreen * GameMainActivity.TILE_WIDTH, yStart * GameMainActivity.TILE_HEIGHT,
                        GameMainActivity.TILE_WIDTH * 4, GameMainActivity.TILE_HEIGHT * 5);

                //now render suitable tiles in place
                for (int y = yStart; y < yStart + 5; y++) {
                    for (int x = (xStart - 1); x < xStart + 5; x++) {

                        if (y >= 0 && x < map[0].length) {
                            currentTile.setID(map[y][x]);
                            if (currentTile.getImage() != null) {
                                currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                                g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());
                            }
                        }
                    }
                }
                return;
            }

            //in this case cameraOffset is not zero & mawi has not moved, so render whole map only if mawi just grounded
        } else if (cameraOffsetX < maxCameraOffsetX && cameraOffsetX != 0 && !mawi.hasMoved(cameraOffsetX, cameraOffsetY)) {
            if (mawi.justGrounded()) {
                renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);
                Log.d("Render", "Rendering case when cameraOffset > 0 and mawi just grounded");
            }
            Log.d("Render", "NO Rendering case when cameraOffset > 0 and no movement detected");
            return;
        }

        //in this case, mawi is at end screen, and has moved within maxOffset so only 1 area needs to be
        //rendered
        else if (cameraOffsetX == maxCameraOffsetX && mawi.hasMoved(cameraOffsetX, cameraOffsetY)
                && (int) previousX == maxCameraOffsetX) {

            Log.d("Render", "Rendering case when cameraOffset stayed at Max and movement detected");


            //get the starting map index for X
            xStart = (int) Math.ceil((mawi.getX() + maxCameraOffsetX) / GameMainActivity.TILE_WIDTH);
            xStart = xStart - 2;

            int xStartScreen = ((int) Math.ceil((mawi.getX() / GameMainActivity.TILE_WIDTH))) - 2;

            Log.d("Render", "xStart is: " + xStart);
            //get the starting map index for Y
            //MUST PLUS YSTART TO OFFSETY
            yStart = (int) Math.ceil((mawi.getY() / GameMainActivity.TILE_HEIGHT));
            yStart = yStart - 2;

            //now refresh suitable area of screen
            g.setColor(Color.rgb(208, 244, 247));
            g.fillRect(xStartScreen * GameMainActivity.TILE_WIDTH, yStart * GameMainActivity.TILE_HEIGHT,
                        GameMainActivity.TILE_WIDTH * 4, GameMainActivity.TILE_HEIGHT * 5);

            //now render suitable tiles in place
            for (int y = yStart; y < yStart + 5; y++) {
                for (int x = xStart; x < xStart + 4; x++) {

                    if (y >= 0 && x < map[0].length) {
                        currentTile.setID(map[y][x]);
                        if (currentTile.getImage() != null) {
                            currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                            g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());
                        }
                    }
                }
            }
            return;
        }

        //this means mawi has entered max offset after being in none offset
        else if (cameraOffsetX == maxCameraOffsetX && mawi.hasMoved(cameraOffsetX, cameraOffsetY) &&
                (int) previousX != maxCameraOffsetX) {
            Log.d("Render", "Rendering case when cameraOffset just become Max and movement detected");
            renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);

        } else if (cameraOffsetX == maxCameraOffsetX && !mawi.hasMoved(cameraOffsetX, cameraOffsetY)) {
            if (mawi.justGrounded()) {
                Log.d("Render", "NO Rendering case when cameraOffset stayed at Max and mawi not moving");
                renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);
            }
            return;
        }
    }

    //only tile just underneath mawi needs to be rendered, as this is causing an overlay effect
    private void renderMawiArea(Painter g, int[][] map, double cameraOffsetX, double cameraOffsetY, Player mawi) {

        Log.d("Render", "RenderMawiArea entered, justGrounded true and hasMoved false");

        xStart = (int) Math.floor((mawi.getX() + (GameMainActivity.TILE_WIDTH / 2)) / GameMainActivity.TILE_WIDTH);
        yStart = (int) Math.floor((mawi.getY() + mawi.getHeight() + GameMainActivity.TILE_HEIGHT / 2) / GameMainActivity.TILE_HEIGHT);
        currentTile.setID(map[yStart][xStart]);

        Log.d("Render", "tile examined at: (" + xStart + "," + yStart + "). \n");

        if (currentTile.getImage() != null) {

                /*g.setColor(Color.rgb(208, 244, 246));
                g.fillRect((int) mawi.getX(), (int) mawi.getY() + mawi.getHeight(), GameMainActivity.TILE_WIDTH,
                        GameMainActivity.TILE_HEIGHT); */

            currentTile.setLocation(yStart, xStart, cameraOffsetX, cameraOffsetY);
            g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());

            Log.d("Render", "tile drawn at: (" + currentTile.getX() + "," + currentTile.getY() + "). \n");
        }

    }

    public void renderWholeMap(Painter g, int[][] map, double cameraOffsetX, double cameraOffsetY) {
        g.setColor(Color.rgb(208, 244, 247));
        g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);

        if (cameraOffsetX == 0) {
            for (int y = 0; y < SCREEN_TILE_SIZE_Y; y++) {
                for (int x = 0; x < SCREEN_TILE_SIZE_X; x++) {
                    currentTile.setID(map[y][x]);
                    currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                    if (currentTile.getImage() != null)
                        g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());

                }
            }
        } else if (cameraOffsetX == maxCameraOffsetX) {
            startingX = Math.ceil(cameraOffsetX / GameMainActivity.TILE_WIDTH);
            for (int y = 0; y < SCREEN_TILE_SIZE_Y; y++) {
                for (int x = (int) startingX; x < (SCREEN_TILE_SIZE_X + startingX); x++) {
                    currentTile.setID(map[y][x]);
                    currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                    if (currentTile.getImage() != null)
                        g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());

                }
            }
        } else {

            startingX = Math.ceil(cameraOffsetX / GameMainActivity.TILE_WIDTH);

            //Log.d("Camera", "1: remainderX is: " + remainderX + ". \n");
            Log.d("Camera", "1: OffsetX is: " + cameraOffsetX + ". \n");
            Log.d("Camera", "2: StartingX is: " + startingX + ". Camera OffsetX is: " + cameraOffsetX + ". \n");

            for (int y = 0; y < SCREEN_TILE_SIZE_Y; y++) {
                for (int x = (int) (startingX - 1); x < (SCREEN_TILE_SIZE_X + (int) startingX); x++) {
                    currentTile.setID(map[y][x]);
                    currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                    if (currentTile.getImage() != null)
                        g.drawImage(currentTile.getImage(), (int) currentTile.getX(), (int) currentTile.getY());
                }
            }

        }
    }
}
