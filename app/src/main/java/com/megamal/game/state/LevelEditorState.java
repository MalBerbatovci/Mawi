package com.megamal.game.state;

import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

import com.megamal.framework.util.Painter;
import com.megamal.framework.util.UIButton;
import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

/**
 * Created by malberbatovci on 12/02/16.
 */
public class LevelEditorState extends State {

    private boolean mapChanged = true;
    private UIButton exitButton;
    private int[][] map;
    private int maskedAction;

    @Override
    public void init() {


        //832, 512
        exitButton = new UIButton(750, 440, 830, 510, Assets.exitButton, Assets.exitButtonPressed);

        //stub value to create as big as possible,
        map = new int[100][100];

    }

    @Override
    public void update(float delta, Painter g) {

    }

    @Override
    public void render(Painter g) {

        if(mapChanged) {
            g.setColor(Color.rgb(80, 143, 240));
            g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);
            mapChanged = false;
        }
        exitButton.render(g);

    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY, int ID, boolean moveAction) {

        //check if moved onto exit button, or changing cameraOffsetButton
        //also will need to check placement of tiles.
        if(moveAction) {
            if(exitButton.buttonMovedOn(scaledX, scaledY, ID)) {
                return true;
            }

            else if (exitButton.buttonMovedOut(scaledX, scaledY, ID)) {
                return true;
            }

            else {
                return true;
            }
        }

        else {
            maskedAction = MotionEventCompat.getActionMasked(e);

            switch(maskedAction) {
                case (MotionEvent.ACTION_DOWN): {
                    if(exitButton.onTouchDown(scaledX, scaledY, ID)) {
                        return true;
                    }

                    else {
                        return true;
                    }
                }

                case (MotionEvent.ACTION_POINTER_DOWN): {
                    if(exitButton.onTouchDown(scaledX, scaledY, ID)) {
                        return true;
                    }

                    else {
                        return true;
                    }
                }

                case (MotionEvent.ACTION_UP): {
                    if(exitButton.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new MenuState());
                        return true;
                    }

                    else {
                        return true;
                    }

                }

                case (MotionEvent.ACTION_POINTER_UP): {
                    if(exitButton.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new MenuState());
                        return true;
                    }

                    else {
                        return true;
                    }
                }
            }
        }

        return true;
    }
}
