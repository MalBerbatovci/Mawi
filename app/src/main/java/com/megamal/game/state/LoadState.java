package com.megamal.game.state;

import android.view.MotionEvent;
import android.view.View;

import com.megamal.mawi.Assets;
import com.megamal.framework.util.Painter;

/**
 * Created by malberbatovci on 22/09/15.
 */
public class LoadState extends State {
    @Override
    public void init() {
        Assets.load();
    }

    @Override
    public void update(float delta, Painter g) {
        setCurrentState(new MenuState());

    }

    @Override
    public void render(Painter g) {

    }

    @Override
    public boolean onTouch(MotionEvent e,int scaledX, int scaledY, int ID, boolean moveAction,
                           View v) {
        return false;
    }
}
