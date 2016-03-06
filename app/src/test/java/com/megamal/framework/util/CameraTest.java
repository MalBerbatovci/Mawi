package com.megamal.framework.util;


import android.view.MotionEvent;

import com.megamal.game.model.Player;
import com.megamal.mawi.GameMainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by malberbatovci on 06/03/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Camera.class)
public class CameraTest {


    @Mock
    private Player testMawi;

    private Camera testCamera;
    int[][] map;

    @Before
    public void setUp() throws Exception {

        //map will never be less than 8 in y, and 13 in x

        //Max in X = 128
        map = new int[10][15];
        testCamera = new Camera(map);
    }

    @Test
    public void checkMaximumOffsets() {

        int expectedMaxOffsetX = 128;
        int expectedMaxOffsetY = 128;

        assertEquals(testCamera.getMaxCameraOffsetX(), expectedMaxOffsetX);
        assertEquals(testCamera.getMaxCameraOffsetY(), expectedMaxOffsetY);

        int[][] offsetCheckMap = new int[20][35];
        Camera offsetCamera = new Camera(offsetCheckMap);

        expectedMaxOffsetY = 768;
        expectedMaxOffsetX = 1408;

        assertEquals(offsetCamera.getMaxCameraOffsetX(), expectedMaxOffsetX);
        assertEquals(offsetCamera.getMaxCameraOffsetY(), expectedMaxOffsetY);
    }

    @Test
    public void checkNoOffsetChangeWhenMawiNotRunning() {

        double previousOffsetX = testCamera.getCameraOffsetX();
        double previousOffsetY = testCamera.getCameraOffsetY();

        double xValueToReturn = 540;
        double yValueToReturn = 380;

        when(testMawi.isRunning()).thenReturn(false);
        when(testMawi.isWalking()).thenReturn(false);
        when(testMawi.getX()).thenReturn(xValueToReturn);

        testCamera.updateCameraX(testMawi, previousOffsetX, map);

        assertEquals(previousOffsetX, testCamera.getCameraOffsetX(), 0);


        when(testMawi.getVelY()).thenReturn(0.0);
        when(testMawi.getY()).thenReturn(yValueToReturn);

        testCamera.updateCameraY(testMawi, previousOffsetY, map);
        assertEquals(previousOffsetY, testCamera.getCameraOffsetY(), 0);
    }

    @Test
    public void cameraOffsetXCalculationsWhenOffsetIsInitiallyZeroAndNotCrossingThreshold() {

        double mawiXNotCrossingThreshold = 30.0;
        int cameraOffsetX = 0;

        when(testMawi.isRunning()).thenReturn(true);
        when(testMawi.getX()).thenReturn(mawiXNotCrossingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffsetX, map);
        assertEquals(0.0, testCamera.getCameraOffsetX(), 0);

        mawiXNotCrossingThreshold = 400;


        when(testMawi.getX()).thenReturn(mawiXNotCrossingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffsetX, map);
        assertEquals(0.0, testCamera.getCameraOffsetX(), 0);
    }

    @Test
    public void cameraOffsetXCalculationWhenOffsetIsIntiallyZeroAndCrossingThreshold() {

        //this is boudnary for just crossing, should not update cameraOffset but should pass
        //condition
        double mawiXCrossingThreshold = 448;
        int cameraOffsetX = 0;

        when(testMawi.isRunning()).thenReturn(true);
        when(testMawi.getX()).thenReturn(mawiXCrossingThreshold);

        testCamera.updateCameraX(testMawi, cameraOffsetX, map);
        assertEquals(0.0, testCamera.getCameraOffsetX(), 0);


        mawiXCrossingThreshold = 480;
        double expectedCameraOffsetX = 32.0;

        when(testMawi.getX()).thenReturn(mawiXCrossingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffsetX, map);

        assertEquals(expectedCameraOffsetX, testCamera.getCameraOffsetX(), 0.0);

    }

    @Test
    public void cameraOffsetXCalculationsWhenOffsetMoreThanZeroButLessThanMaxGoingRight() {

        int mawiNotCrossingThresholdX = 420;
        int cameraOffsetX = 50;

        when(testMawi.getX()).thenReturn((double) mawiNotCrossingThresholdX);
        when(testMawi.isRunning()).thenReturn(true);
        when(testMawi.isRight()).thenReturn(true);
        when(testMawi.isLeft()).thenReturn(false);

        testCamera.updateCameraX(testMawi, cameraOffsetX, map);
        assertEquals(cameraOffsetX, testCamera.getCameraOffsetX(), 0);


        //case, when mawi has just crossed threshold, CO should equal 0
        int mawiCrossingThreshold = 448;

        when(testMawi.getX()).thenReturn((double) mawiCrossingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffsetX, map);
        assertEquals(cameraOffsetX, testCamera.getCameraOffsetX(), 0);


        mawiCrossingThreshold = 490;
        int expectedOffset = (cameraOffsetX + 42);

        when(testMawi.getX()).thenReturn((double) mawiCrossingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffsetX, map);
        assertEquals(expectedOffset, testCamera.getCameraOffsetX(), 0);


        //case where new offset will make it more than max - ensure ir remains at max
        mawiCrossingThreshold = 750;
        expectedOffset = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;

        when(testMawi.getX()).thenReturn((double) mawiCrossingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffsetX, map);
        assertEquals(expectedOffset, testCamera.getCameraOffsetX(), 0);
    }

    @Test
    public void cameraOffsetXCalculationsWhenOffsetMoreThanZeroButLessThanMaxGoingLeft() {

        int mawiNotCrossingThresholdX = 420;
        int cameraOffsetX = 50;

        when(testMawi.getX()).thenReturn((double) mawiNotCrossingThresholdX);
        when(testMawi.isRunning()).thenReturn(true);
        when(testMawi.isRight()).thenReturn(false);
        when(testMawi.isLeft()).thenReturn(true);

        testCamera.updateCameraX(testMawi, cameraOffsetX, map);
        assertEquals(cameraOffsetX, testCamera.getCameraOffsetX(), 0);


        //case, when mawi has just crossed threshold, CO should equal 0
        int mawiCrossingThreshold = 320;

        when(testMawi.getX()).thenReturn((double) mawiCrossingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffsetX, map);
        assertEquals(cameraOffsetX, testCamera.getCameraOffsetX(), 0);


        mawiCrossingThreshold = 280;
        int expectedOffset = (cameraOffsetX - 40);

        when(testMawi.getX()).thenReturn((double) mawiCrossingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffsetX, map);
        assertEquals(expectedOffset, testCamera.getCameraOffsetX(), 0);


        //case where new offset will make it more than max - ensure ir remains at max
        mawiCrossingThreshold = 20;
        expectedOffset = 0;

        when(testMawi.getX()).thenReturn((double) mawiCrossingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffsetX, map);
        assertEquals(expectedOffset, testCamera.getCameraOffsetX(), 0);
    }

    @Test
    public void cameraOffsetCalculationsWhenCameraOffsetIsMaxAndGoingLeft() {

        int mawiXNotPassingThreshold = 720;
        int cameraOffset = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;

        when(testMawi.getX()).thenReturn((double) mawiXNotPassingThreshold);
        when(testMawi.isRunning()).thenReturn(true);
        when(testMawi.isRight()).thenReturn(false);
        when(testMawi.isLeft()).thenReturn(true);

        //check to ensure that cameraOffset hasnt changed
        testCamera.updateCameraX(testMawi, cameraOffset, map);
        assertEquals(cameraOffset, testCamera.getCameraOffsetX(), 0);


        //new case, when amwi just passes threshold
        int mawiXPassingThreshold = 320;
        when(testMawi.getX()).thenReturn((double) mawiXPassingThreshold);

        testCamera.updateCameraX(testMawi, cameraOffset, map);
        assertEquals(cameraOffset, testCamera.getCameraOffsetX(), 0);


        //when mawi has well passed threshold
        mawiXPassingThreshold = 280;
        int expectedOffset = (cameraOffset - 40);
        when(testMawi.getX()).thenReturn((double) mawiXPassingThreshold);

        testCamera.updateCameraX(testMawi, cameraOffset, map);
        assertEquals(expectedOffset, testCamera.getCameraOffsetX(), 0);
    }

    @Test
    public void cameraOffsetCalculationsWhenCameraOffsetIsMaxAndGoingRight() {

        //cameraOffset should never change in this case
        int mawiXPassingThreshold = 720;
        int cameraOffset = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;

        when(testMawi.getX()).thenReturn((double) mawiXPassingThreshold);
        when(testMawi.isRunning()).thenReturn(true);
        when(testMawi.isRight()).thenReturn(true);
        when(testMawi.isLeft()).thenReturn(false);

        //check to ensure that cameraOffset hasnt changed
        testCamera.updateCameraX(testMawi, cameraOffset, map);
        assertEquals(cameraOffset, testCamera.getCameraOffsetX(), 0);

        mawiXPassingThreshold = 800;
        when(testMawi.getX()).thenReturn((double) mawiXPassingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffset, map);
        assertEquals(cameraOffset, testCamera.getCameraOffsetX(), 0);

        mawiXPassingThreshold = 600;
        when(testMawi.getX()).thenReturn((double) mawiXPassingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffset, map);
        assertEquals(cameraOffset, testCamera.getCameraOffsetX(), 0);

        mawiXPassingThreshold = 820;
        when(testMawi.getX()).thenReturn((double) mawiXPassingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffset, map);
        assertEquals(cameraOffset, testCamera.getCameraOffsetX(), 0);

        mawiXPassingThreshold = 890;
        when(testMawi.getX()).thenReturn((double) mawiXPassingThreshold);
        testCamera.updateCameraX(testMawi, cameraOffset, map);
        assertEquals(cameraOffset, testCamera.getCameraOffsetX(), 0);

    }

    @Test
    public void cameraOffsetYCalculationsWhenOffsetIsInitiallyZeroAndNotCrossingThresholdMovingDown() {

        double mawiYNotCrossingThreshold = 30.0;
        int cameraOffsetY = 0;
        int velYMovement = 40;

        when(testMawi.getY()).thenReturn(mawiYNotCrossingThreshold);
        when(testMawi.getVelY()).thenReturn((double) velYMovement);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(0.0, testCamera.getCameraOffsetY(), 0);


        //boundary of crossing threshold - shouldnt change offset
        mawiYNotCrossingThreshold = 288.0;


        when(testMawi.getY()).thenReturn(mawiYNotCrossingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(0.0, testCamera.getCameraOffsetY(), 0);
    }

    @Test
    public void cameraOffsetXCalculationWhenOffsetIsIntiallyZeroAndCrossingThresholdGoingDown() {


        double mawiYCrossingThreshold = 350.0;
        int cameraOffsetY = 0;
        int expectedOffset = (cameraOffsetY + 62);
        int velYMovement = 40;

        when(testMawi.getY()).thenReturn(mawiYCrossingThreshold);
        when(testMawi.getVelY()).thenReturn((double) velYMovement);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(expectedOffset, testCamera.getCameraOffsetY(), 0);


        //overRuns on y, but should keep at max
        mawiYCrossingThreshold = 450.0;
        expectedOffset = (map.length * GameMainActivity.TILE_HEIGHT) - GameMainActivity.GAME_HEIGHT;

        when(testMawi.getY()).thenReturn(mawiYCrossingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(expectedOffset, testCamera.getCameraOffsetY(), 0);

    }

    @Test
    public void cameraOffsetYCalculationsWhenOffsetMoreThanZeroButLessThanMaxGoingDown() {

        int mawiYNotPassingThreshold = 200;
        int cameraOffsetY = 40;
        int velYGoingDown = 15;

        when(testMawi.getY()).thenReturn((double) mawiYNotPassingThreshold);
        when(testMawi.getVelY()).thenReturn((double) velYGoingDown);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);

        assertEquals(cameraOffsetY, testCamera.getCameraOffsetY(), 0);

        int mawiYPassingThreshold = 288;
        when(testMawi.getY()).thenReturn((double) mawiYPassingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(cameraOffsetY, testCamera.getCameraOffsetY(), 0);


        mawiYPassingThreshold = 300;
        int expectedOffsetY = (cameraOffsetY + 12);
        when(testMawi.getY()).thenReturn((double) mawiYPassingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(expectedOffsetY, testCamera.getCameraOffsetY(), 0);

        //goes to more than max, so should remain at max
        mawiYPassingThreshold = 480;
        expectedOffsetY = (map.length * GameMainActivity.TILE_HEIGHT) - GameMainActivity.GAME_HEIGHT;
        when(testMawi.getY()).thenReturn((double) mawiYPassingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(expectedOffsetY, testCamera.getCameraOffsetY(), 0);

    }

    @Test
    public void cameraOffsetYCalculationsWhenOffsetMoreThanZeroButLessThanMaxGoingUp() {
        //VelX < 0
        //case 1: not passed (x = 40)
        //case 2: just passed(x =288, co stays the same)
        //case 3: passed (x = 300, co + 12)
        //case 4: overruns (y = 480, co = (map.length * GameMainActivity.TILE_HEIGHT) - GameMainActivity.GAME_HEIGHT;

        int mawiYNotPassingThreshold = 200;
        int cameraOffsetY = 40;
        int velYGoingUp = -15;

        when(testMawi.getY()).thenReturn((double) mawiYNotPassingThreshold);
        when(testMawi.getVelY()).thenReturn((double) velYGoingUp);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);

        assertEquals(cameraOffsetY, testCamera.getCameraOffsetY(), 0);

        int mawiYPassingThreshold = 96;
        when(testMawi.getY()).thenReturn((double) mawiYPassingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(cameraOffsetY, testCamera.getCameraOffsetY(), 0);


        mawiYPassingThreshold = 84;
        int expectedOffsetY = (cameraOffsetY - 12);
        when(testMawi.getY()).thenReturn((double) mawiYPassingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(expectedOffsetY, testCamera.getCameraOffsetY(), 0);

        //goes to more than max, so should remain at max
        mawiYPassingThreshold = 10;
        expectedOffsetY = 0;
        when(testMawi.getY()).thenReturn((double) mawiYPassingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(expectedOffsetY, testCamera.getCameraOffsetY(), 0);
    }

    @Test
    public void cameraOffsetCalculationsWhenCameraOffsetIsMaxAndGoingDown() {
        //in this case camera offset should not change at all

        int mawiYPosition = 50;
        int velYDown = 15;
        //maxOffset
        int cameraOffsetY = (map.length * GameMainActivity.TILE_HEIGHT) - GameMainActivity.GAME_HEIGHT;

        when(testMawi.getY()).thenReturn((double) mawiYPosition);
        when(testMawi.getVelY()).thenReturn((double) velYDown);

        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(cameraOffsetY, testCamera.getCameraOffsetY(), 0);

        mawiYPosition = 200;
        when(testMawi.getY()).thenReturn((double) mawiYPosition);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(cameraOffsetY, testCamera.getCameraOffsetY(), 0);

        mawiYPosition = 150;
        when(testMawi.getY()).thenReturn((double) mawiYPosition);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(cameraOffsetY, testCamera.getCameraOffsetY(), 0);


        mawiYPosition = 300;
        when(testMawi.getY()).thenReturn((double) mawiYPosition);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(cameraOffsetY, testCamera.getCameraOffsetY(), 0);


        mawiYPosition = 500;
        when(testMawi.getY()).thenReturn((double) mawiYPosition);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(cameraOffsetY, testCamera.getCameraOffsetY(), 0);


    }

    @Test
    public void cameraOffsetCalculationsWhenCameraOffsetIsMaxAndGoingUp() {

        int mawiYNotCrossingThreshold = 440;
        int velYGoingUp = -15;
        int cameraOffsetY = (map.length * GameMainActivity.TILE_HEIGHT) - GameMainActivity.GAME_HEIGHT;

        when(testMawi.getY()).thenReturn((double) mawiYNotCrossingThreshold);
        when(testMawi.getVelY()).thenReturn((double) velYGoingUp);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(cameraOffsetY, testCamera.getCameraOffsetY(), 0);


        //just crossing threshold
        int mawiYCrossingThreshold = 96;

        when(testMawi.getY()).thenReturn((double) mawiYCrossingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(cameraOffsetY, testCamera.getCameraOffsetY(), 0);


        mawiYCrossingThreshold = 86;
        int expectedOffset = (cameraOffsetY - 10);
        when(testMawi.getY()).thenReturn((double) mawiYCrossingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(expectedOffset, testCamera.getCameraOffsetY(), 0);


        mawiYCrossingThreshold = 10;
        expectedOffset = (cameraOffsetY - 86);
        when(testMawi.getY()).thenReturn((double) mawiYCrossingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(expectedOffset, testCamera.getCameraOffsetY(), 0);

        mawiYCrossingThreshold = -100;
        expectedOffset = 0;
        when(testMawi.getY()).thenReturn((double) mawiYCrossingThreshold);
        testCamera.updateCameraY(testMawi, cameraOffsetY, map);
        assertEquals(expectedOffset, testCamera.getCameraOffsetY(), 0);



    }
}