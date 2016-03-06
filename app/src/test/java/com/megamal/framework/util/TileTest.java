package com.megamal.framework.util;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by malberbatovci on 04/03/16.
 */


//Mocking is not necessary in this case
public class TileTest {

    @Test
    public void checkLocationWithNoCameraOffset() {

        //get random id - shouldn't matter
        int iD = RandomNumberGenerator.getRandIntBetween(0, 22);
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;

        //should return co-ordinates: (256, 256)
        int xIndex = 4;
        int yIndex = 4;
        int expectedX = 256;
        int expectedY = 256;

        Tile testTile = new Tile(iD);


        testTile.setLocation(yIndex, xIndex, cameraOffsetX, cameraOffsetY);

        assertEquals(expectedX, (int) testTile.getX());
        assertEquals(expectedY, (int) testTile.getY());


        xIndex = 7;
        yIndex = 3;
        expectedX = 448;
        expectedY = 192;

        testTile.setLocation(yIndex, xIndex, cameraOffsetX, cameraOffsetY);

        assertEquals(expectedX, (int) testTile.getX());
        assertEquals(expectedY, (int) testTile.getY());
    }

    @Test
    public void checkLocationWithCameraOffsetX() {

        int iD = RandomNumberGenerator.getRandIntBetween(0, 22);
        int cameraOffsetX = 50;
        int cameraOffsetY = 0;

        int xIndex = 5;
        int yIndex = 1;

        int expectedX = 270;
        int expectedY = 64;

        Tile testTile = new Tile(iD);


        testTile.setLocation(yIndex, xIndex, cameraOffsetX, cameraOffsetY);

        assertEquals(expectedX, (int) testTile.getX());
        assertEquals(expectedY, (int) testTile.getY());

        xIndex = 4;
        yIndex = 2;
        expectedX = 206;
        expectedY = 128;

        testTile.setLocation(yIndex, xIndex, cameraOffsetX, cameraOffsetY);

        assertEquals(expectedX, (int) testTile.getX());
        assertEquals(expectedY, (int) testTile.getY());

    }

    @Test
    public void checkLocationWithCameraOffsetY(){

        int iD = RandomNumberGenerator.getRandIntBetween(0, 22);
        int cameraOffsetX = 0;
        int cameraOffsetY = 50;

        int xIndex = 1;
        int yIndex = 5;

        int expectedX = 64;
        int expectedY = 270;

        Tile testTile = new Tile(iD);

        testTile.setLocation(yIndex, xIndex, cameraOffsetX, cameraOffsetY);

        assertEquals(expectedX, (int) testTile.getX());
        assertEquals(expectedY, (int) testTile.getY());

        yIndex = 4;
        xIndex = 2;
        expectedY = 206;
        expectedX = 128;

        testTile.setLocation(yIndex, xIndex, cameraOffsetX, cameraOffsetY);

        assertEquals(expectedX, (int) testTile.getX());
        assertEquals(expectedY, (int) testTile.getY());

    }


    @Test
    public void checkLocationWithCameraOffsetYAndCameraOffsetX(){

        int iD = RandomNumberGenerator.getRandIntBetween(0, 22);
        int cameraOffsetX = 50;
        int cameraOffsetY = 50;

        int xIndex = 3;
        int yIndex = 6;

        int expectedX = 142;
        int expectedY = 334;

        Tile testTile = new Tile(iD);

        testTile.setLocation(yIndex, xIndex, cameraOffsetX, cameraOffsetY);

        assertEquals(expectedX, (int) testTile.getX());
        assertEquals(expectedY, (int) testTile.getY());


        xIndex = 7;
        yIndex = 10;
        expectedX = 398;
        expectedY = 590;

        testTile.setLocation(yIndex, xIndex, cameraOffsetX, cameraOffsetY);

        assertEquals(expectedX, (int) testTile.getX());
        assertEquals(expectedY, (int) testTile.getY());

    }


    @Test
    public void newIDShouldBeSetWithAllCorrespondingVariables() {

        //first ID is obstacle, not collectable, does not have collectable
        int firstID = 1;

        //second ID is not obstacles, is collectable and does not have collectable
        int secondID = 3;


        Tile testTile = new Tile(firstID);
        testTile.setID(secondID);

        assertFalse("testTile is still an obstacle!", testTile.isObstacle());
        assertTrue("testTile is not a collectable", testTile.isCollectable());
        assertFalse("testTile contains a collectable", testTile.hasCollectable());


        testTile.setID(firstID);
        assertTrue("testTile is still an obstacle!", testTile.isObstacle());
        assertFalse("testTile is not a collectable", testTile.isCollectable());
        assertFalse("testTile contains a collectable", testTile.hasCollectable());
    }

}