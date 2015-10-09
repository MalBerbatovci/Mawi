package com.megamal.game.state;

import android.util.Log;
import android.view.MotionEvent;

import com.megamal.framework.util.Tile;
import com.megamal.framework.util.TileMapFactory;
import com.megamal.framework.util.TileMapRenderer;
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
    private String levelString = "test.txt";

    @Override
    public void init() {
        tileRenderer = new TileMapRenderer();
        tileFactory = new TileMapFactory();

        //tileRenderer.parseFile(levelString);

        try {
            tileFactory.parseFileIntoMap(levelString);
        } catch (IOException e ) {
            System.err.print("Error parsing file");
        }
    }

    /* private int calculateIndex(int x, int y) {
        return (int) (y * MAP_HEIGHT) + x;
    } */

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(Painter g) {
        tileRenderer.renderMap(g, tileFactory.getMap());
    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY) {
        return false;
    }
}
