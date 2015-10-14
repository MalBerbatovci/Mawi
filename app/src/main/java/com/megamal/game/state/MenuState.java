package com.megamal.game.state;

import android.util.Log;
import android.view.MotionEvent;

import com.megamal.framework.util.Tile;
import com.megamal.framework.util.TileMapFactory;
import com.megamal.framework.util.TileMapRenderer;
import com.megamal.game.model.Player;
import com.megamal.mawi.Assets;
import com.megamal.framework.util.Painter;
import com.megamal.mawi.GameMainActivity;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by malberbatovci on 22/09/15.
 */
public class MenuState extends State {

    private TileMapRenderer tileRenderer;
    private TileMapFactory tileFactory;
    private Player mawi;
    private Tile tile;
    private String levelString = "test.txt";
    private int[][] map;

    @Override
    public void init() {
        tileRenderer = new TileMapRenderer();
        tileFactory = new TileMapFactory();

        try {
            map = tileFactory.parseFileIntoMap(levelString);
        } catch (IOException e ) {
            System.err.print("Error parsing file: " + levelString);
        }

        tile = new Tile(1);
        tile.setLocation(6, 2);

        mawi = new Player(tile.getX() + GameMainActivity.PLAYER_WIDTH,
                          tile.getY() - GameMainActivity.PLAYER_HEIGHT,
                          GameMainActivity.PLAYER_HEIGHT, GameMainActivity.PLAYER_WIDTH);


    }

    /* private int calculateIndex(int x, int y) {
        return (int) (y * MAP_HEIGHT) + x;
    } */

    @Override
    public void update(float delta) {
        mawi.update(delta);

    }

    @Override
    public void render(Painter g) {
        tileRenderer.renderMap(g, map);
        renderPlayer(g);
    }

    private void renderPlayer(Painter g) {
        g.drawImage(Assets.mawiStanding, (int) mawi.getX(), (int) mawi.getY());
    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY) {
        return false;
    }
}
