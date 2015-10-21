package com.megamal.game.state;

import android.graphics.Color;
import android.view.MotionEvent;

import com.megamal.framework.util.Tile;
import com.megamal.framework.util.TileMapFactory;
import com.megamal.framework.util.TileMapRenderer;
import com.megamal.framework.util.UIButton;
import com.megamal.game.model.Player;
import com.megamal.mawi.Assets;
import com.megamal.framework.util.Painter;
import com.megamal.mawi.GameMainActivity;

import java.io.IOException;

/**
 * Created by malberbatovci on 22/09/15.
 */
public class MenuState extends State {

    private TileMapRenderer tileRenderer;
    private TileMapFactory tileFactory;
    private Player mawi;
    private Tile tile;
    private String levelString = "test.txt";

    private UIButton walkRight;

    private int[][] map;

    @Override
    public void init() {
        tileRenderer = new TileMapRenderer();
        tileFactory = new TileMapFactory();

        tile = new Tile(1);

        try {
            map = tileFactory.parseFileIntoMap(levelString);
        } catch (IOException e ) {
            System.err.print("Error parsing file: " + levelString);
        }

        loop:
        for(int i = 0; i < (GameMainActivity.GAME_HEIGHT / GameMainActivity.TILE_HEIGHT); i++) {
            tile.setID(map[i][1]);
            if (tile.isObstacle()) {
                tile.setLocation(i, 1);
                break loop;
            }
        }

        //tile.setID(1);
        //tile.setLocation(4, 4);
        // - 64

        mawi = new Player((tile.getX() + GameMainActivity.PLAYER_WIDTH),
                          tile.getY() - GameMainActivity.PLAYER_HEIGHT,
                          GameMainActivity.PLAYER_WIDTH, GameMainActivity.PLAYER_HEIGHT);

        walkRight = new UIButton(120, 450, 220, 490, Assets.walkRightButton, Assets.walkRightButtonPressed);
    }

    /* private int calculateIndex(int x, int y) {
        return (int) (y * MAP_HEIGHT) + x;
    } */

    @Override
    public void update(float delta) {

        if(!mawi.isAlive()) {
            //do something if end of game
        }

        else {
            mawi.checkGrounded(map);
            mawi.checkCloseness(map);
            mawi.update(delta);
        }

    }

    @Override
    public void render(Painter g) {
        g.setColor(Color.rgb(208, 244, 247));
        g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);

        tileRenderer.renderMap(g, map);
        walkRight.render(g);
        renderPlayer(g);
    }

    private void renderPlayer(Painter g) {
        if (mawi.isWalking()) {
            Assets.walkAnim.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
        }
        else
            g.drawImage(Assets.mawiStandingFront, (int) mawi.getX(), (int) mawi.getY());
    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY) {
        //check if walk button is pressed, this changes walkRight.isPressed to true if contained
        //in the buttons rect
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            walkRight.onTouchDown(scaledX, scaledY);

            if(walkRight.isTouched()) {
                mawi.walkRight();
            }
        }

        //v. naive way of checking.
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if(walkRight.isTouched()) {
                mawi.stopWalking();
            }
        }

        return true;
    }
}
