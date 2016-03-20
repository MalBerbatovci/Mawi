package com.megamal.game.model;

import android.graphics.Rect;

import com.megamal.framework.util.Tile;
import com.megamal.mawi.GameMainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by malberbatovci on 16/03/16.
 */


@RunWith(PowerMockRunner.class)
@PrepareForTest(Player.class)
public class PlayerTest {

    private static final int RIGHT = 1;
    private static final int LEFT = -1;

    @Mock
    private Tile mockTileA, mockTileB;

    @Mock
    private Rect mockPlayerRect, mockCoinRect;

    @Mock
    private Player testPlayer;

    private int[][] testMap = new int[20][30];

    @Before
    public void setUp() throws Exception {

        PowerMockito.whenNew(Rect.class).withAnyArguments().thenReturn(mockPlayerRect);
        PowerMockito.whenNew(Rect.class).withAnyArguments().thenReturn(mockCoinRect);
        PowerMockito.whenNew(Tile.class).withAnyArguments().thenReturn(mockTileA);
        PowerMockito.whenNew(Tile.class).withAnyArguments().thenReturn(mockTileB);

        testPlayer = Mockito.spy(new Player(10, 10, GameMainActivity.PLAYER_WIDTH, GameMainActivity.PLAYER_HEIGHT));

    }

    @Test
    public void noXCollisionDueToOOBLeft()  {

        int OOBValueX = -130;

        double expectedVelY;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testPlayer.run(1);

        testPlayer.forceX(OOBValueX);

        expectedVelY = testPlayer.getVelY();

        testPlayer.checkXMovement(testMap, 0, 0);

        assertEquals(expectedVelY, testPlayer.getVelY(), 0);

    }

    @Test
    public void noYCollisionDueToOOBRight()  {

        //in bounds
        double expectedVelY;
        double expectedVelX;
        int cameraOffsetX = 20;
        int cameraOffsetY = 20;

        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(false);

        testPlayer.run(-1);
        testPlayer.justHit();

        testPlayer.jump();

        expectedVelX = testPlayer.getVelX();
        expectedVelY = testPlayer.getVelY();

        testPlayer.checkXMovement(testMap, cameraOffsetX, cameraOffsetY);
        testPlayer.checkYMovement(testMap, cameraOffsetX, cameraOffsetY);

        assertEquals(expectedVelY, testPlayer.getVelY(), 0);
        assertEquals(expectedVelX, testPlayer.getVelX(), 0);

    }

    @Test
    public void noYCollisionDueToOOB()  {

        double expectedVelY;
        double cameraOffsetX, cameraOffsetY;

        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(true);


        testPlayer.run(1);
        testPlayer.run(-1);

        testPlayer.jump();

        expectedVelY = testPlayer.getVelY();

        testPlayer.checkYMovement(testMap, 0, 0);

        assertNotEquals(expectedVelY, testPlayer.getVelY(), 0);

    }


    @Test
    public void CollisionOnXShouldStopMovementRight()  {

        //in bounds
        double expectedVelY;
        double expectedVelX;
        int cameraOffsetX = 20;
        int cameraOffsetY = 20;

        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(false);

        testPlayer.run(-1);
        testPlayer.justHit();

        testPlayer.jump();

        expectedVelX = testPlayer.getVelX();
        expectedVelY = testPlayer.getVelY();

        testPlayer.checkXMovement(testMap, cameraOffsetX, cameraOffsetY);
        testPlayer.checkYMovement(testMap, cameraOffsetX, cameraOffsetY);

        assertEquals(expectedVelY, testPlayer.getVelY(), 0);
        assertEquals(expectedVelX, testPlayer.getVelX(), 0);

    }

    @Test
    public void CollisionOnYShouldStopMovementRight()  {

        //in bounds
        double expectedVelY;
        double expectedVelX;
        int cameraOffsetX = 20;
        int cameraOffsetY = 20;

        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(false);

        testPlayer.run(RIGHT);
        testPlayer.justHit();

        testPlayer.jump();

        expectedVelX = testPlayer.getVelX();
        expectedVelY = testPlayer.getVelY();

        testPlayer.checkXMovement(testMap, cameraOffsetX, cameraOffsetY);
        testPlayer.checkYMovement(testMap, cameraOffsetX, cameraOffsetY);

        assertEquals(expectedVelY, testPlayer.getVelY(), 0);
        assertEquals(expectedVelX, testPlayer.getVelX(), 0);

    }

    @Test
    public void CollisionOnXShouldStopMovementLeft()  {

        //in bounds
        double expectedVelY;
        double expectedVelX;
        int cameraOffsetX = 20;
        int cameraOffsetY = 20;

        testPlayer.run(LEFT);
        testPlayer.jump();

        expectedVelX = testPlayer.getVelX();
        expectedVelY = testPlayer.getVelY();

        testPlayer.checkXMovement(testMap, cameraOffsetX, cameraOffsetY);
        testPlayer.checkYMovement(testMap, cameraOffsetX, cameraOffsetY);
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);


        assertEquals(expectedVelY, testPlayer.getVelY(), 0);
        assertEquals(expectedVelX, testPlayer.getVelX(), 0);

    }

    @Test
    public void CollisionOnYShouldStopMovementLeft()  {

        //in bounds
        double expectedVelY;
        double expectedVelX;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;

        testPlayer.run(LEFT);
        testPlayer.jump();
        testPlayer.justHit();

        expectedVelX = testPlayer.getVelX();
        expectedVelY = testPlayer.getVelY();

        testPlayer.checkXMovement(testMap, cameraOffsetX, cameraOffsetY);
        testPlayer.checkYMovement(testMap, cameraOffsetX, cameraOffsetY);
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);


        assertEquals(expectedVelY, testPlayer.getVelY(), 0);
        assertEquals(expectedVelX, testPlayer.getVelX(), 0);

    }

    @Test
    public void noYCollisionDueToOOBLeft()  {
        int OOBValueY = 1000;

        double expectedVelY;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testPlayer.run(1);

        testPlayer.forceX(OOBValueY);

        expectedVelY = testPlayer.getVelY();

        testPlayer.checkXMovement(testMap, 0, 0);

        assertEquals(expectedVelY, testPlayer.getVelY(), 0);

    }

    @Test
    public void noXCollisionDueToOOBRight()  {

        int OOBValueX = 1000;

        double expectedVelY;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testPlayer.run(RIGHT);

        testPlayer.forceX(OOBValueX);

        expectedVelY = testPlayer.getVelY();

        testPlayer.checkXMovement(testMap, 0, 0);

        assertEquals(expectedVelY, testPlayer.getVelY(), 0);

    }

    @Test
    public void noXCollisionDueToOOB()  {
        int OOBValueX = -1000;

        double expectedVelY;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testPlayer.run(LEFT);

        testPlayer.forceX(OOBValueX);

        expectedVelY = testPlayer.getVelY();

        testPlayer.checkXMovement(testMap, 0, 0);

        assertEquals(expectedVelY, testPlayer.getVelY(), 0);




        OOBValueX = (testMap.length * GameMainActivity.TILE_HEIGHT * GameMainActivity.TILE_WIDTH);

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testPlayer.run(LEFT);

        testPlayer.forceX(OOBValueX);

        expectedVelY = testPlayer.getVelY();

        testPlayer.checkXMovement(testMap, 0, 0);

        assertEquals("Value was not recorded as OOB, meaning that could throw error",
                expectedVelY, testPlayer.getVelY(), 0);

    }


    @Test
    public void collisionShouldKeepPlayerAliveAsEnemyIsAlreadyDying() throws Exception {


    }

    @Test
    public void PlayerShouldHaveRegisteredACollisionInXMovementWhenMovingRightAsInBoundsAndTileIsObstacle() {

        //in bounds
        double expectedVelY;
        double expectedVelX;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);



        testPlayer.run(RIGHT);
        testPlayer.justHit();
        testPlayer.jump();

        expectedVelX = testPlayer.getVelX();
        expectedVelY = testPlayer.getVelY();
        testPlayer.checkXMovement(testMap, cameraOffsetX, cameraOffsetY);
        testPlayer.checkYMovement(testMap, cameraOffsetX, cameraOffsetY);

        assertEquals("Collision Was actually detected in Y", expectedVelY, testPlayer.getVelY(), 0);
        assertEquals("Collision Was actually detected in X", expectedVelX, testPlayer.getVelX(), 0);

    }


    @Test
    public void PlayerShouldNotHaveRegisteredACollisionInXMovementWhenMovingLeftAsOOB() {

        //in bounds
        double expectedVelY;
        double expectedVelX;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);

        testPlayer.run(RIGHT);
        testPlayer.justHit();
        testPlayer.jump();

        expectedVelX = testPlayer.getVelX();
        expectedVelY = testPlayer.getVelY();
        testPlayer.checkXMovement(testMap, cameraOffsetX, cameraOffsetY);
        testPlayer.checkYMovement(testMap, cameraOffsetX, cameraOffsetY);

        assertEquals("Collision Was actually detected in Y", expectedVelY, testPlayer.getVelY(), 0);
        assertEquals("Collision Was actually detected in X", expectedVelX, testPlayer.getVelX(), 0);




        cameraOffsetX = 200;
        cameraOffsetY = 200;
        testPlayer.run(RIGHT);
        testPlayer.jump();

        expectedVelX = testPlayer.getVelX();
        expectedVelY = testPlayer.getVelY();
        testPlayer.checkXMovement(testMap, cameraOffsetX, cameraOffsetY);
        testPlayer.checkYMovement(testMap, cameraOffsetX, cameraOffsetY);

        assertNotEquals("Collision Was actually detected in Y", expectedVelY, testPlayer.getVelY(), 0);
        assertEquals("Collision Was actually detected in X", expectedVelX, testPlayer.getVelX(), 0);

    }


    @Test
    public void PlayerShouldHaveMovedCameraOffsetZero() {

        //in bounds
        double expectedVelY;
        double expectedVelX;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);

        testPlayer.run(RIGHT);
        testPlayer.justHit();
        testPlayer.jump();

        expectedVelX = testPlayer.getVelX();
        expectedVelY = testPlayer.getVelY();
        testPlayer.checkXMovement(testMap, cameraOffsetX, cameraOffsetY);
        testPlayer.checkYMovement(testMap, cameraOffsetX, cameraOffsetY);

        assertEquals("Collision Was actually detected in Y", expectedVelY, testPlayer.getVelY(), 0);
        assertEquals("Collision Was actually detected in X", expectedVelX, testPlayer.getVelX(), 0);


        cameraOffsetX = 200;
        cameraOffsetY = 200;
        testPlayer.run(RIGHT);
        testPlayer.jump();

        expectedVelX = testPlayer.getVelX();
        expectedVelY = testPlayer.getVelY();
        testPlayer.checkXMovement(testMap, cameraOffsetX, cameraOffsetY);
        testPlayer.checkYMovement(testMap, cameraOffsetX, cameraOffsetY);

        assertNotEquals("Collision Was actually detected in Y", expectedVelY, testPlayer.getVelY(), 0);
        assertEquals("Collision Was actually detected in X", expectedVelX, testPlayer.getVelX(), 0);

    }

    @Test
    public void PlayerShouldHaveNotMovedCameraOffsetZero() {
        double expectedVelY;
        double expectedVelX;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);

        testPlayer.run(RIGHT);
        testPlayer.justHit();
        testPlayer.jump();
        expectedVelX = testPlayer.getVelX();
        expectedVelY = testPlayer.getVelY();


        testPlayer.checkXMovement(testMap, cameraOffsetX, cameraOffsetY);
        testPlayer.checkYMovement(testMap, cameraOffsetX, cameraOffsetY);

        assertEquals("Collision Was actually detected in Y", expectedVelY, testPlayer.getVelY(), 0);
        assertEquals("Collision Was actually detected in X", expectedVelX, testPlayer.getVelX(), 0);


    }




}