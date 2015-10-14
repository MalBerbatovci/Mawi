package com.megamal.game.model;

import android.graphics.Rect;

import com.megamal.framework.util.Painter;
import com.megamal.framework.util.Tile;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 14/10/15.
 */
public class Player {
    private float x, y;

    //velY for jumping, velX for walking/running
    private int width, height;

    //DuckRect too, when implementing
    private Rect rect;

    public Player(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        rect = new Rect((int) x, (int) y, (int) x + width, (int) y + height);
    }

    public void update(float delta) {
        //Empty atm, nothing to do.
    }

    //method to get the top left X and Y of mawi when you have the tile you want her to be placed on
    public void setLocationFromTile(Tile tile) {
        float x = tile.getX();
        float y = tile.getY();

        this.x = x + GameMainActivity.PLAYER_WIDTH;
        this.y = y - GameMainActivity.PLAYER_HEIGHT;


    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Rect getRect() {
        return rect;
    }


}
