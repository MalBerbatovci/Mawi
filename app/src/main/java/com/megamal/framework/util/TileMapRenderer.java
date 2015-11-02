package com.megamal.framework.util;

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

    int remainderX, RemainderY;
    double startingX, startingY;

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
    public void renderMap(Painter g, int[][] map, int cameraOffsetX, int cameraOffsetY) {

        //must use remainderX!!!!!!
        remainderX = cameraOffsetX % GameMainActivity.TILE_WIDTH;

        if (cameraOffsetX == 0) {
            //switch (remainderX) {
            //case (0):
            //check if camera has changed before rendering again
            //check if offset has changed since previous
            for (int y = 0; y < SCREEN_TILE_SIZE_Y; y++) {
                for (int x = 0; x < SCREEN_TILE_SIZE_X; x++) {
                    currentTile.setID(map[y][x]);
                    currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                    if (currentTile.getImage() != null)
                        g.drawImage(currentTile.getImage(), currentTile.getX(), currentTile.getY());

                }
            }

        //in this case, then the two tiles on the end of the screen need to be drawn at a displacement
        } else {
            //Log.d("Camera", "1: remainderX is: " + remainderX + ". \n");
            Log.d("Camera", "1: OffsetX is: " + cameraOffsetX + ". \n");
            startingX = Math.ceil((double) cameraOffsetX / GameMainActivity.TILE_WIDTH);
            Log.d("Camera", "2: StartingX is: " + startingX + ". Camera OffsetX is: " + cameraOffsetX + ". \n");
            for (int y = 0; y < SCREEN_TILE_SIZE_Y; y++) {
                for (int x = (int) startingX; x < (SCREEN_TILE_SIZE_X + (int) startingX - 1); x++) {
                    currentTile.setID(map[y][x]);
                    currentTile.setLocation(y, x, cameraOffsetX, cameraOffsetY);
                    if (currentTile.getImage() != null)
                        g.drawImage(currentTile.getImage(), currentTile.getX(), currentTile.getY());

                    //this is the tile on the left that needs to be draw offsetted slightly
                    if (x == startingX) {
                        if(currentTile.getImage() != null) {
                            currentTile.setID(map[y][x - 1]);
                            g.drawImage(currentTile.getImage(), currentTile.getX() - GameMainActivity.TILE_WIDTH,
                                    currentTile.getY());
                        }

                    //this is the tile on the right that needs to be draw offsetted slightly
                    } else if (x == (SCREEN_TILE_SIZE_X + (int) startingX - 2)) {
                        if (currentTile.getImage() != null) {
                            currentTile.setID(map[y][x + 1]);
                            g.drawImage(currentTile.getImage(), currentTile.getX() + GameMainActivity.TILE_WIDTH,
                                    currentTile.getY());
                        }
                    }
                }
            }
        }
    }
}
