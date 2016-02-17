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
    private int scaledX;
    private int scaledY;
    private static final int MAX_POINTERS = 3;
    private boolean moveAction = false;

    public void setCurrentState(State currentState){
        this.currentState = currentState;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        //atm, currently when moving and pointers are down,
        //index is only corresponding to the first pointer, therefore
        //need to loop through and get necessary

        int pointerCount = event.getPointerCount();
        int index = getIndex(event);

        //ID DOESNT CHANGE THROUGHOUT DURATION, THEREFORE CAN BE USED IN BUTTON LOGISTICS
        int ID = event.getPointerId(index);

        scaledX = (int) ((event.getX(index) / v.getWidth()) *
                GameMainActivity.GAME_WIDTH);
        scaledY = (int) ((event.getY(index) / v.getHeight()) *
                GameMainActivity.GAME_HEIGHT);


        if(MotionEventCompat.getActionMasked(event) != MotionEvent.ACTION_MOVE) {

            moveAction = false;
            return currentState.onTouch(event, scaledX, scaledY, ID, moveAction, v);
        }

        else {

            boolean moveFlagCheck = false;
            moveAction = true;

           for(index = 0; index < pointerCount && index <= MAX_POINTERS; index++) {
               ID = event.getPointerId(index);

               //Log.d("MultiTouch", "Pointer ID: " + ID);

               scaledX = (int) ((event.getX(index) / v.getWidth()) *
                       GameMainActivity.GAME_WIDTH);
               scaledY = (int) ((event.getY(index) / v.getHeight()) *
                       GameMainActivity.GAME_HEIGHT);

               //only needs to do move really, add move flag into method
               moveFlagCheck = currentState.onTouch(event, scaledX, scaledY, ID, moveAction, v);

               if(!moveFlagCheck) {
                   Log.d("MultiTouch", "NOT HANDLED!");
                   return false;
               }

           }

        }

        return true;

    }

    private int getIndex(MotionEvent event) {
        int index = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

        return index;
    }

    private int scaleX(int x, int viewWidth) {
        return ((x / viewWidth) * GameMainActivity.GAME_WIDTH);
    }

    private int scaleY(int y, int viewHeight) {
        return ((y / viewHeight) * GameMainActivity.GAME_HEIGHT);
    }

}
