package com.megamal.game.state;

import android.view.MotionEvent;

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
    public void update(float delta) {
        setCurrentState(new MenuState());

    }

    @Override
    public void render(Painter g) {

    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY) {
        return false;
    }
}
