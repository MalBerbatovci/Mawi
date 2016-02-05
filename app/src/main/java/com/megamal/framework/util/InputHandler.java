package com.megamal.framework.util;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.megamal.mawi.GameMainActivity;
import com.megamal.game.state.State;

/**
 * Created by malberbatovci on 22/09/15.
 */
public class InputHandler implements OnTouchListener {

    private State currentState;
    private int scaledX1 = 0;
    private int scaledY1 = 0;
    private int scaledX2, scaledY2, scaledX3, scaledY3;
    private int previousX1, previousX2, previousY1, previousY2, previousX3, previousY3;

    public void setCurrentState(State currentState){
        this.currentState = currentState;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        scaledX1 = (int) ((event.getX() / v.getWidth()) *
                GameMainActivity.GAME_WIDTH);
        scaledY1 = (int) ((event.getY() / v.getHeight()) *
                GameMainActivity.GAME_HEIGHT);

        //Log.d("MenuState", "Scaled X1: " + scaledX1 + ". Scaled Y1:" + scaledY1 + ". \n");

        if (event.getPointerCount() == 1) {
            scaledX2 = -1;
            scaledY2 = -1;
            scaledX3 = -1;
            scaledY3 = -1;
            //Log.d("InputHandler", "Pointer count 1");

           // Log.d("MultiTouch", "ScaledX1 is " + scaledX1);
            return currentState.onTouch(event, scaledX1, scaledY1, scaledX2, scaledY2, v);
        } else if (event.getPointerCount() == 2) {

            //Log.d("InputHandler", "Pointer count 2");

            //in the case of 1 becoming 0, this does not work
            scaledX2 = (int) ((event.getX(1) / v.getWidth()) *
                    GameMainActivity.GAME_WIDTH);
            scaledY2 = (int) ((event.getY(1) / v.getHeight()) *
                    GameMainActivity.GAME_HEIGHT);

            scaledX3 = -1;
            scaledY3 = -1;

            //Log.d("MenuState","Scaled X2: " + scaledX2 + ". Scaled Y2 :" + scaledY2 + ". \n");
            return currentState.onTouch(event, scaledX1, scaledY1, scaledX2, scaledY2, v);

        } else {
            scaledX2 = (int) ((event.getX(1) / v.getWidth()) *
                    GameMainActivity.GAME_WIDTH);
            scaledY2 = (int) ((event.getY(1) / v.getHeight()) *
                    GameMainActivity.GAME_HEIGHT);

            scaledX3 = (int) ((event.getX(2) / v.getWidth()) *
                    GameMainActivity.GAME_WIDTH);
            scaledY3 = (int) ((event.getY(2) / v.getHeight()) *
                    GameMainActivity.GAME_HEIGHT);

            return currentState.onTouch(event, scaledX1, scaledY1, scaledX2, scaledY2, v);

        }
    }
}
