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

        PowerMockito.whenNew(Rect.class).withAnyArguments().thenReturn(buttonRect);
        testButton = new UIButton(left, top, right, bottom, image, imageDown);
    }

    @Test
    public void touchDownShouldHaveNoEfectAsPointerIdNotSameAndButtonDown() {

    }

    @Test
    public void touchDownShouldNotHaveEffectAsButtonRectNotContained() {

    }


    @Test
    public void touchDownShouldHaveAnEffectAsButtonRectIsContainedAndAllOtherConditions() {

    }

}