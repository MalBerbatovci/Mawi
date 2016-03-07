package com.megamal.framework.util;

import android.graphics.Bitmap;
import android.graphics.Rect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by malberbatovci on 06/03/16.
 */

@PrepareForTest(UIButton.class)
@RunWith(PowerMockRunner.class)
public class UIButtonTest {

    @Mock
    private Rect buttonRect;

    @Mock
    private Bitmap image, imageDown;

    private UIButton testButton;

    @Before
    public void setUp() throws Exception {
        int left = 40;
        int top = 40;

        int right = 80;
        int bottom = 80;

        PowerMockito.whenNew(Rect.class).withArguments(left, top, right, bottom).thenReturn(buttonRect);
        testButton = new UIButton(left, top, right, bottom, image, imageDown);
    }

    @Test
    public void touchDownShouldHaveNoEfectAsPointerIdNotSameAndButtonDown() {

        int controllerID = 1;
        int ourID = 2;

        int touchX = 50;
        int touchY = 50;

        testButton.forceTouchOff();
        testButton.forcePointerID(controllerID);
        when(buttonRect.contains(touchX, touchY)).thenReturn(true);
        testButton.onTouchDown(touchX, touchY, ourID);
        assertEquals(false, testButton.isTouched());

        testButton.forceTouchOff();
        testButton.forcePointerID(controllerID);
        testButton.onTouchDown(touchX, touchY, ourID);
        assertEquals(false, testButton.isTouched());

    }



    @Test
    public void touchDownShouldNotHaveEffectAsButtonRectNotContained() {

        int controllerID = 1;
        int expectedPointerID = -1;

        int touchX = 50;
        int touchY = 50;

        testButton.forceTouchOff();
        when(buttonRect.contains(touchX, touchY)).thenReturn(false);
        testButton.onTouchDown(touchX, touchY, controllerID);

        assertEquals(false, testButton.isTouched());
        assertEquals(expectedPointerID, testButton.getID());
    }


    @Test
    public void touchDownShouldHaveAnEffectAsButtonRectIsContainedAndAllOtherConditions() {

        int controllerID = 1;
        int touchX = 50;
        int touchY = 50;

        testButton.forceTouchOff();
        when(buttonRect.contains(touchX, touchY)).thenReturn(true);
        testButton.onTouchDown(touchX, touchY, controllerID);

        assertEquals(true, testButton.isTouched());
        assertEquals(controllerID, testButton.getID());

    }

    @Test
    public void touchUpShouldNotHaveAnEffectAsNotTheCorrectPointerID() {

        int controllerID = 1;
        int ourID = 2;

        int touchX = 50;
        int touchY = 50;

        testButton.forceTouchOff();
        testButton.forceTouchDown(controllerID);
        when(buttonRect.contains(touchX, touchY)).thenReturn(true);
        testButton.onTouchUp(touchX, touchY, ourID);
        assertEquals(true, testButton.isTouched());
        assertEquals(controllerID, testButton.getID());

    }

    @Test
    public void touchUpShouldNotHaveAnEffectAsButtonRectNotContained() {

        int controllerID = 1;

        int touchX = 50;
        int touchY = 50;


        testButton.forceTouchOff();
        testButton.forceTouchDown(controllerID);
        when(buttonRect.contains(touchX, touchY)).thenReturn(false);
        testButton.onTouchUp(touchX, touchY, controllerID);
        assertEquals(true, testButton.isTouched());
        assertEquals(controllerID, testButton.getID());

    }

    @Test
    public void touchUpShouldHaveAnEffectAsSameIDAndContained() {

        int controllerID = 1;
        int expectedID = -1;

        int touchX = 50;
        int touchY = 50;


        testButton.forceTouchOff();
        testButton.forceTouchDown(controllerID);
        when(buttonRect.contains(touchX, touchY)).thenReturn(true);
        testButton.onTouchUp(touchX, touchY, controllerID);
        assertEquals(false, testButton.isTouched());
        assertEquals(expectedID, testButton.getID());
    }

    @Test
    public void movedOnShouldNotHaveAnEffectAsPointerIDNotCorrect() {

        int controllerID = 1;
        int ourID = 2;

        int touchX = 50;
        int touchY = 50;

        testButton.forceTouchOff();
        testButton.forceTouchDown(controllerID);
        when(buttonRect.contains(touchX, touchY)).thenReturn(true);
        testButton.buttonMovedOn(touchX, touchY, ourID);
        assertEquals(true, testButton.isTouched());
        assertEquals(controllerID, testButton.getID());


    }

    @Test
    public void movedOnShouldNotHaveAnEffectAsButtonRectNotContained() {

        int controllerID = 1;
        int ourID = 2;

        int touchX = 50;
        int touchY = 50;

        testButton.forceTouchOff();
        testButton.forceTouchDown(controllerID);
        when(buttonRect.contains(touchX, touchY)).thenReturn(false);
        testButton.buttonMovedOn(touchX, touchY, ourID);
        assertEquals(true, testButton.isTouched());
        assertEquals(controllerID, testButton.getID());

    }

    @Test
    public void movedOnShouldHaveAnEffectAsSameIDAndContained() {

        int ourID = 2;

        int touchX = 50;
        int touchY = 50;

        testButton.forceTouchOff();
        when(buttonRect.contains(touchX, touchY)).thenReturn(true);
        testButton.buttonMovedOn(touchX, touchY, ourID);
        assertEquals(true, testButton.isTouched());
        assertEquals(ourID, testButton.getID());

    }

    @Test
    public void movedOffShouldNotHaveAnEffectAsPointerIDNotCorrect() {

        int controllerID = 1;
        int ourID = 2;

        int touchX = 50;
        int touchY = 50;

        testButton.forceTouchOff();
        testButton.forceTouchDown(controllerID);
        when(buttonRect.contains(touchX, touchY)).thenReturn(true);
        testButton.buttonMovedOut(touchX, touchY, ourID);
        assertEquals(true, testButton.isTouched());
        assertEquals(controllerID, testButton.getID());
    }

    @Test
    public void movedOffShouldNotHaveAnEffectAsButtonRectNotContained() {

        int controllerID = 1;
        int ourID = 2;

        int touchX = 50;
        int touchY = 50;

        testButton.forceTouchOff();
        testButton.forceTouchDown(controllerID);
        when(buttonRect.contains(touchX, touchY)).thenReturn(false);
        testButton.buttonMovedOut(touchX, touchY, ourID);
        assertEquals(true, testButton.isTouched());
        assertEquals(controllerID, testButton.getID());
    }

    @Test
    public void movedOffShouldHaveAnEffectAsSameIDAndContained() {

        int ourID = 2;
        int expectedID = -1;

        int touchX = 50;
        int touchY = 50;

        testButton.forceTouchOff();
        when(buttonRect.contains(touchX, touchY)).thenReturn(true);
        testButton.buttonMovedOut(touchX, touchY, ourID);
        assertEquals(false, testButton.isTouched());
        assertEquals(expectedID, testButton.getID());

    }

}