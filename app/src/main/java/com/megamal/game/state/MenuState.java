package com.megamal.game.state;

import android.view.MotionEvent;

import com.megamal.mawi.Assets;
import com.megamal.framework.util.Painter;

/**
 * Created by malberbatovci on 22/09/15.
 */
public class MenuState extends State {
    @Override
    public void init() {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(Painter g) {
        g.drawImage(Assets.welcome, 0, 0);
    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY) {
        return false;
    }
}
