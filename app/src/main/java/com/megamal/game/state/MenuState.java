package com.megamal.game.state;

import android.util.Log;
import android.view.MotionEvent;

import com.megamal.framework.util.Tile;
import com.megamal.mawi.Assets;
import com.megamal.framework.util.Painter;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 22/09/15.
 */
public class MenuState extends State {
    private int[][] map;
    private Tile[] tile;
    private int index;
    public boolean notYetRendered = true;

    private static final float MAP_WIDTH = (GameMainActivity.GAME_WIDTH / Tile.TILE_WIDTH);
    private static final float MAP_HEIGHT = (GameMainActivity.GAME_HEIGHT / Tile.TILE_HEIGHT);
    private static final int ARRAY_LENGTH = (int) MAP_WIDTH * (int) MAP_HEIGHT;

    @Override
    public void init() {

        //VERY TEMPORARY!
        map = new int[(int)MAP_WIDTH][(int)MAP_HEIGHT];
        tile = new Tile[ARRAY_LENGTH];

        //setting tilemap (would be reading in a txt file which will be
        //created in the level editor).
        for (int y = 0; y < MAP_WIDTH; y++) {
            for(int x = 0; x < MAP_HEIGHT; x++) {
                map[y][x] = 1;
            }
        }

        for (int y = 0; y < MAP_WIDTH; y++) {
            for(int x = 0; x < MAP_HEIGHT; x++) {
                index = calculateIndex(x,y);
                tile[index] = new Tile(map[y][x], true);
                tile[index].setLocation(x, y);

                System.out.println("Index " + index + " has location " + tile[index].getX()
                                    + "," + tile[index].getY());
            }
        }
    }

    private int calculateIndex(int x, int y) {
        return (int) (y * MAP_HEIGHT) + x;
    }

    @Override
    public void update(float delta) {



    }

    @Override
    public void render(Painter g) {

        if(notYetRendered) {
            for (int i = 0; i < tile.length; i++) {
                    g.drawImage(tile[i].getImage(), (int) tile[i].getX(),
                            (int) tile[i].getY());
            }
        }

        notYetRendered = false;



    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY) {
        return false;
    }
}
