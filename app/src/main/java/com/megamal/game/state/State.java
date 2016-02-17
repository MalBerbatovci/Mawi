package com.megamal.game.state;

import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.megamal.mawi.GameMainActivity;
import com.megamal.framework.util.Painter;

/**
 * Created by malberbatovci on 22/09/15.
 */
public abstract class State {

    public void setCurrentState(State newState) {
        GameMainActivity.sGame.setCurrentState(newState);
    }

    public abstract void init();

    public abstract void update(float delta, Painter g);

    public abstract void render(Painter g);

    public abstract boolean onTouch(MotionEvent e,int scaledX, int scaledY, int ID,
                                    boolean moveAction, View v);
}
