package com.megamal.framework.util;

/**
 * Created by malberbatovci on 20/10/15.
 */

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.megamal.mawi.Assets;

public class UIButton {
    private Rect buttonRect;
    private boolean buttonDown = false;
    private Bitmap buttonImage, buttonDownImage;
    private int pointerID;

    public UIButton(int left, int top, int right, int bottom, Bitmap buttonImage,
                    Bitmap buttonPressedImage) {
        buttonRect = new Rect(left, top, right, bottom);

        if(buttonImage != null) {
            this.buttonImage = buttonImage;
        }
        this.pointerID = -1;

        if(buttonImage != null) {
            this.buttonDownImage = buttonPressedImage;
        }
    }

    public void render(Painter g) {

        if(buttonDownImage != null && buttonImage != null) {
            Bitmap currentButtonImage = buttonDown ? buttonDownImage : buttonImage;
            g.drawImage(currentButtonImage, buttonRect.left, buttonRect.top, buttonRect.width(),
                    buttonRect.height());

        } else {
            Log.d("UIButton", "One of the images is Null - NOT RENDERING");
        }

    }

    //check if the touchEvent is contained in a button, if it is put buttonDown as true
    public boolean onTouchDown(int touchX, int touchY, int pointerID) {

        //this means button is available
        if((!buttonDown) && this.pointerID == -1) {

            //button is contained, and is now set to down
            if(buttonRect.contains(touchX, touchY)) {
                buttonDown = true;
                this.pointerID = pointerID;
                return true;
            }

            //button available, but not touched down
            else {
                return false;
            }
        }

        //this means button is currently being used, this down needs not be registered
        else {
            return false;
        }
    }

    public boolean onTouchDownOff(int touchX, int touchY) {

        if(buttonRect.contains(touchX, touchY)) {
            buttonDown = false;
            this.pointerID = -1;
            return true;
        }

        else {
            return false;
        }
    }

    public boolean onTouchUp(int touchX, int touchY, int pointerID) {

        if (this.pointerID != pointerID) {
            //Log.d("MultiTouch", "Pointer not associated with this button");
            return false;
        }

        if (buttonRect.contains(touchX, touchY)) {
            buttonDown = false;
            this.pointerID = -1;
            return true;

        } else {
            return false;
        }

    }

    //only called when button ID same as button, and not contained
    public boolean buttonMovedOut(int touchX, int touchY, int pointerID) {

        //this pointer was previous associated with this button
        if(this.pointerID == pointerID) {

            //is now not in the vicinity of the button, so has moved from button
            if(!isContained(touchX, touchY)) {
                buttonDown = false;
                this.pointerID = -1;
                return true;
            }

            else {
                return false;
            }
        }

        //pointer not associated with pointer, so movement irrelevant on this button
        else {
            return false;
        }
    }

    public boolean buttonMovedOn(int touchX, int touchY, int pointerID) {

        //button already occupied
        if(this.pointerID != -1 && buttonDown) {

            return false;
        }

        else {
            //if button is on and this ID is not associated with itself
            if(this.pointerID != pointerID && isContained(touchX, touchY)) {
                this.pointerID = pointerID;
                this.buttonDown = true;
                return true;

            }
            else {
                return false;
            }
        }
    }

    protected void forcePointerID(int iD) {
        this.pointerID = iD;
    }

    protected void forceTouchDown(int iD) {
        this.buttonDown = true;
        this.pointerID = iD;
    }

    //needed in order to ensure that only one button remains on at one time duirng level editor
    public void forceTouchOff() {
        buttonDown = false;
        this.pointerID = -1;
    }

    //cancel the press
    public void cancel() {
        buttonDown = false;
    }

    public boolean isContained(int touchX, int touchY) {
        return buttonRect.contains(touchX, touchY);
    }

    public boolean isTouched() {
        return buttonDown;
    }

    public int getID() {
        return pointerID;
    }

    public Bitmap getButtonImage() {
        return buttonImage;
    }

    public int getX() {
        return buttonRect.right;
    }

    public int getY() {
        return buttonRect.top;
    }
}


