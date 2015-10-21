package com.megamal.framework.util;

/**
 * Created by malberbatovci on 20/10/15.
 */

import android.graphics.Bitmap;
import android.graphics.Rect;

public class UIButton {
    private Rect buttonRect;
    private boolean buttonDown = false;
    private Bitmap buttonImage, buttonDownImage;

    public UIButton(int left, int top, int right, int bottom, Bitmap buttonImage,
                    Bitmap buttonPressedImage) {
        buttonRect = new Rect(left, top, right, bottom);
        this.buttonImage = buttonImage;
        this.buttonDownImage = buttonPressedImage;
    }

    public void render(Painter g) {
        Bitmap currentButtonImage = buttonDown ? buttonDownImage : buttonImage;
        g.drawImage(currentButtonImage, buttonRect.left, buttonRect.top, buttonRect.width(),
                buttonRect.height());

    }

    //check if the touchEvent is contained in a button, if it is put buttonDown as true
    public void onTouchDown(int touchX, int touchY) {
        if (buttonRect.contains(touchX, touchY))
            buttonDown = true;
        else
            buttonDown = false;
    }

    //cancel the press
    public void cancel() {
        buttonDown = false;
    }

    //check if the button has been pressed
    public boolean isPressed(int touchX, int touchY) {
        return buttonDown && buttonRect.contains(touchX, touchY);
    }

    public boolean isTouched() {
        return buttonDown;
    }
}

