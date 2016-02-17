package com.megamal.game.state;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.megamal.framework.util.Painter;
import com.megamal.framework.util.UIButton;
import com.megamal.mawi.Assets;

/**
 * Created by malberbatovci on 12/02/16.
 */
public class MenuState extends State {


    private UIButton playButton, exitButton, levelEditorButton;
    private boolean renderedFlag = false;



    @Override
    public void init() {
        playButton = new UIButton(340, 275, 527, 350, null, null);
        exitButton = new UIButton(340, 365, 527, 435, null, null);
        levelEditorButton = new UIButton(780, 120, 825, 150, null, null);
    }

    @Override
    public void update(float delta, Painter g) {

    }

    @Override
    public void render(Painter g) {

        if(!renderedFlag) {
            g.drawImage(Assets.startScreen, 0, 0);
            renderedFlag = true;
        }
    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY, int ID, boolean moveAction,
                           View v) {

        //if action is a move on, ignore, only interested in when finger moves up
        //(Improve this if there is time later)
        if(moveAction) {
            return true;
        }

        else {
           int maskedAction = MotionEventCompat.getActionMasked(e);

            if(maskedAction == MotionEvent.ACTION_POINTER_UP ||
                    maskedAction == MotionEvent.ACTION_UP) {

                if(playButton.isContained(scaledX, scaledY)) {
                    setCurrentState(new PlayState());
                }

                else if (exitButton.isContained(scaledX, scaledY)) {
                    setCurrentState(new PlayState());
                }

                else if (levelEditorButton.isContained(scaledX,scaledY)) {
                    setCurrentState(new LevelEditorState());
                }

            }
        }

        return true;
    }
}
