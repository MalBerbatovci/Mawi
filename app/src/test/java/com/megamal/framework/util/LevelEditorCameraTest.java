package com.megamal.framework.util;

import com.megamal.mawi.GameMainActivity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by malberbatovci on 06/03/16.
 */
public class LevelEditorCameraTest {

    private LevelEditorCamera testCamera;
    private int[][] map;

    private final static int LEFT = -1;
    private final static int RIGHT = 1;

    private final static int DOWN = -1;
    private final static int UP = 1;

    @Before
    public void setUp() {

        //this is chosen as this is the LE default size
        map = new int[100][100];
        testCamera = new LevelEditorCamera(map);

    }

    @Test
    public void noChangeInCameraOffsetShouldBeRecordedAsIDNotEqualToControllerID() {

        int ID = 2;
        int controllerID = 1;
        int cameraOffsetX = 0;
        int movementX = 30;
        int movementY = 30;


        testCamera.setControllerID(controllerID);
        testCamera.forceOffsetX(cameraOffsetX);
        testCamera.updateCameraX(movementX, RIGHT, ID);
        assertEquals(cameraOffsetX, testCamera.getX(), 0);


        //try with diff direction
        testCamera.updateCameraX(movementX, LEFT, ID);
        assertEquals(cameraOffsetX, testCamera.getX(), 0);

        //try with different offset
        cameraOffsetX = 30;
        testCamera.forceOffsetX(cameraOffsetX);
        testCamera.updateCameraX(movementX, RIGHT, ID);
        assertEquals(cameraOffsetX, testCamera.getX(), 0);

        testCamera.updateCameraX(movementX, LEFT, ID);
        assertEquals(cameraOffsetX, testCamera.getX(), 0);

        int cameraOffsetY = 0;
        testCamera.forceOffsetY(cameraOffsetY);
        testCamera.updateCameraY(movementY, UP, ID);
        assertEquals(cameraOffsetY, testCamera.getY(), 0);

        testCamera.forceOffsetY(cameraOffsetY);
        testCamera.updateCameraY(movementY, DOWN, ID);
        assertEquals(cameraOffsetY, testCamera.getY(), 0);

        cameraOffsetY = 30;
        testCamera.forceOffsetY(cameraOffsetY);
        testCamera.updateCameraY(movementY, UP, ID);
        assertEquals(cameraOffsetY, testCamera.getY(), 0);

        testCamera.forceOffsetY(cameraOffsetY);
        testCamera.updateCameraY(movementY, DOWN, ID);
        assertEquals(cameraOffsetY, testCamera.getY(), 0);

    }


    @Test
    public void noChangeInCameraOffsetXShouldBeRecordedAsTryingToMoveRightAndAtMaxOffset() {

        int maxOffsetX = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;
        int controllerID = 1;

        //force ID to be controller iD
        testCamera.forceNoID();
        testCamera.setControllerID(controllerID);
        testCamera.forceOffsetX(maxOffsetX);

        int movementX = 40;
        testCamera.updateCameraX(movementX, RIGHT, controllerID);
        assertEquals(maxOffsetX, testCamera.getX(), 0);

        movementX = 2;
        testCamera.forceOffsetX(maxOffsetX);
        testCamera.updateCameraX(movementX, RIGHT, controllerID);
        assertEquals(maxOffsetX, testCamera.getX(), 0);
    }

    @Test
    public void noChangeInCameraOffsetXShouldBeRecordedAsTryingToMoveLeftAndAtMinOffset() {

        int minOffsetX = 0;
        int controllerID = 1;

        //force ID to be controller iD
        testCamera.forceNoID();
        testCamera.setControllerID(controllerID);
        testCamera.forceOffsetX(minOffsetX);

        int movementX = 40;
        testCamera.updateCameraX(movementX, LEFT, controllerID);
        assertEquals(minOffsetX, testCamera.getX(), 0);

        movementX = 2;
        testCamera.forceOffsetX(minOffsetX);
        testCamera.updateCameraX(movementX, LEFT, controllerID);
        assertEquals(minOffsetX, testCamera.getX(), 0);
    }


    @Test
    public void appropriateChangeInCameraOffsetXMovingRight() {

        int offsetX = 0;
        int controllerID = 1;

        //force ID to be controller iD
        testCamera.forceNoID();
        testCamera.setControllerID(controllerID);
        testCamera.forceOffsetX(offsetX);

        int movementX = 40;
        int expectedOffset = (offsetX + movementX);
        testCamera.updateCameraX(movementX, RIGHT, controllerID);
        assertEquals(expectedOffset, testCamera.getX(), 0);

        offsetX = 50;
        movementX = 20;
        expectedOffset = (offsetX + movementX);
        testCamera.forceOffsetX(offsetX);
        testCamera.updateCameraX(movementX, RIGHT, controllerID);
        assertEquals(expectedOffset, testCamera.getX(), 0);

        offsetX = ((map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH) - 50;
        movementX = 100;
        expectedOffset = (map[0].length * GameMainActivity.TILE_WIDTH) - GameMainActivity.GAME_WIDTH;
        testCamera.forceOffsetX(offsetX);
        testCamera.updateCameraX(movementX, RIGHT, controllerID);
        assertEquals(expectedOffset, testCamera.getX(), 0);


    }

    @Test
    public void appropriateChangeInCameraOffsetXMovingLeft() {

        int offsetX = 920;
        int controllerID = 1;

        //force ID to be controller iD
        testCamera.forceNoID();
        testCamera.setControllerID(controllerID);
        testCamera.forceOffsetX(offsetX);

        int movementX = 40;
        int expectedOffset = (offsetX - movementX);
        testCamera.updateCameraX(movementX, LEFT, controllerID);
        assertEquals(expectedOffset, testCamera.getX(), 0);

        offsetX = 50;
        movementX = 20;
        expectedOffset = (offsetX - movementX);
        testCamera.forceOffsetX(offsetX);
        testCamera.updateCameraX(movementX, LEFT, controllerID);
        assertEquals(expectedOffset, testCamera.getX(), 0);

        offsetX = 20;
        movementX = 100;
        expectedOffset = 0;
        testCamera.forceOffsetX(offsetX);
        testCamera.updateCameraX(movementX, LEFT, controllerID);
        assertEquals(expectedOffset, testCamera.getX(), 0);
    }

    @Test
    public void noChangeInCameraOffsetYShouldBeRecordedAsTryingToMoveDownAndAtMaxOffset() {

        int maxOffsetY = (map.length * GameMainActivity.TILE_HEIGHT) - GameMainActivity.GAME_HEIGHT;
        int controllerID = 1;

        //force ID to be controller iD
        testCamera.forceNoID();
        testCamera.setControllerID(controllerID);
        testCamera.forceOffsetY(maxOffsetY);

        int movementY = 40;
        testCamera.updateCameraY(movementY, DOWN, controllerID);
        assertEquals(maxOffsetY, testCamera.getY(), 0);

        movementY = 2;
        testCamera.forceOffsetY(maxOffsetY);
        testCamera.updateCameraY(movementY, DOWN, controllerID);
        assertEquals(maxOffsetY, testCamera.getY(), 0);
    }

    @Test
    public void noChangeInCameraOffsetYShouldBeRecordedAsTryingToMoveUpAndAtMinOffset() {

        int minOffsetY = 0;
        int controllerID = 1;

        //force ID to be controller iD
        testCamera.forceNoID();
        testCamera.setControllerID(controllerID);
        testCamera.forceOffsetY(minOffsetY);

        int movementY = 40;
        testCamera.updateCameraY(movementY, UP, controllerID);
        assertEquals(minOffsetY, testCamera.getY(), 0);

        movementY = 2;
        testCamera.forceOffsetY(minOffsetY);
        testCamera.updateCameraY(movementY, UP, controllerID);
        assertEquals(minOffsetY, testCamera.getY(), 0);
    }


    @Test
    public void appropriateChangeInCameraOffsetYMovingDown() {

        int offsetY = 20;
        int controllerID = 1;

        //force ID to be controller iD
        testCamera.forceNoID();
        testCamera.setControllerID(controllerID);
        testCamera.forceOffsetY(offsetY);

        int movementY = 40;
        int expectedOffsetY = (offsetY + movementY);
        testCamera.updateCameraY(movementY, DOWN, controllerID);
        assertEquals(expectedOffsetY, testCamera.getY(), 0);

        offsetY = 0;
        movementY = 2;
        expectedOffsetY = (offsetY + movementY);
        testCamera.forceOffsetY(offsetY);
        testCamera.updateCameraY(movementY, DOWN, controllerID);
        assertEquals(expectedOffsetY, testCamera.getY(), 0);

        offsetY = ((map.length * GameMainActivity.TILE_HEIGHT) - GameMainActivity.GAME_HEIGHT) - 50;
        movementY = 100;
        expectedOffsetY = (map.length * GameMainActivity.TILE_HEIGHT) - GameMainActivity.GAME_HEIGHT;
        testCamera.forceOffsetY(offsetY);
        testCamera.updateCameraY(movementY, DOWN, controllerID);
        assertEquals(expectedOffsetY, testCamera.getY(), 0);

    }

    @Test
    public void appropriateChangeInCameraOffsetYMovingUp() {

        int offsetY = 920;
        int controllerID = 1;

        //force ID to be controller iD
        testCamera.forceNoID();
        testCamera.setControllerID(controllerID);
        testCamera.forceOffsetY(offsetY);

        int movementY = 40;
        int expectedOffsetY = (offsetY - movementY);
        testCamera.updateCameraY(movementY, UP, controllerID);
        assertEquals(expectedOffsetY, testCamera.getY(), 0);

        offsetY = 10;
        movementY = 2;
        expectedOffsetY = (offsetY - movementY);
        testCamera.forceOffsetY(offsetY);
        testCamera.updateCameraY(movementY, UP, controllerID);
        assertEquals(expectedOffsetY, testCamera.getY(), 0);

        offsetY = 20;
        movementY = 100;
        expectedOffsetY = 0;
        testCamera.forceOffsetY(offsetY);
        testCamera.updateCameraY(movementY, UP, controllerID);
        assertEquals(expectedOffsetY, testCamera.getY(), 0);
    }

}