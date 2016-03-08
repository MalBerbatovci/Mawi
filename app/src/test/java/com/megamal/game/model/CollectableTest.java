package com.megamal.game.model;

import com.megamal.framework.util.Tile;
import com.megamal.mawi.GameMainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by malberbatovci on 06/03/16.
 */

@PrepareForTest(Collectable.class)
@RunWith(PowerMockRunner.class)
public class CollectableTest {

    private Collectable testCollectable;
    int[][] testMap = new int[30][20];

    @Mock
    private Tile mockTileA, mockTileB;

    @Mock
    private Player testPlayer;
    private int ID = 1;

    @Before
    public void setUp() throws Exception {
        double x = 50;
        double y = 50;
        double cameraOffsetX = 0;
        double cameraOffsetY = 0;

        PowerMockito.whenNew(Tile.class).withAnyArguments().thenReturn(mockTileA);
        PowerMockito.whenNew(Tile.class).withAnyArguments().thenReturn(mockTileB);

        testCollectable = Mockito.spy(new Collectable(ID, x, y, cameraOffsetX, cameraOffsetY));
    }

    @Test
    public void collectableShouldNotBeAliveAsHasBeenCaught() {

        testCollectable.collectableCaught(testPlayer);
        assertEquals(false, testCollectable.isAlive());

    }

    @Test
    public void collectableShouldNotBeVisibleCameraOffsetZero() {
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;

        //X out of bounds, Y in bounds
        int collectableX = -65;
        int collectableY = 40;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test Boundary value when moving right
        collectableX = -1;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);
        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test boundary for left
        collectableX = -65;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //test Boundary value when moving right
        collectableX = -1;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);
        testCollectable.forceRight();
        testCollectable.forceUp();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //X in range, Y isn't - check boundayr at Y when moving down
        collectableX = 40;
        collectableY = -65;
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //boundary of y when moving up
        collectableY = -65;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));



        //Both not in range
        collectableX = 940;
        collectableY = 890;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));
    }

    @Test
    public void collectableShouldBeVisibleCameraOffsetZero() {
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;

        //X out of bounds, Y in bounds
        int collectableX = -63;
        int collectableY = 240;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test Boundary value when moving right
        collectableX = 1;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);
        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test boundary for left
        collectableX = -63;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //test Boundary value when moving right
        collectableX = 1;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);
        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //Y in range, X isn't - check boundayr at Y when moving down
        collectableX = 40;
        collectableY = 511;
        testCollectable.forceY(collectableY);
        testCollectable.forceX(collectableX);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //boundary of y when moving up
        collectableY = -63;
        testCollectable.forceY(collectableY);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));



        //Both in range
        collectableX = 420;
        collectableY = 320;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

    }

    @Test
    public void collectableShouldNotBeVisibleCameraOffsetNotZero() {

        //set initial variables - X is out of range even with CO
        int collectableX = -115;
        int collectableY = 40;
        int cameraOffsetX = 50;
        int cameraOffsetY = 50;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);

        //X out of bounds, Y in bounds
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test Boundary value when moving right
        collectableX = -51;
        testCollectable.forceX(collectableX);
        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test boundary for left
        collectableX = -115;
        testCollectable.forceX(collectableX);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //test Boundary value when moving right
        collectableX = -51;
        testCollectable.forceX(collectableX);
        testCollectable.forceRight();
        testCollectable.forceUp();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //X in range, Y isn't - check boundayr at Y when moving down
        collectableX = 40;
        collectableY = -51;
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //boundary of y when moving up
        collectableY = -115;
        testCollectable.forceY(collectableY);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));



        //Both not in range
        collectableX = 940;
        collectableY = 890;
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //set initial variables - X is out of range even with CO
        collectableX = -90;
        collectableY = 40;
        cameraOffsetX = 25;
        cameraOffsetY = 100;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);

        //X out of bounds, Y in bounds
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test Boundary value when moving right
        collectableX = -39;
        testCollectable.forceX(collectableX);
        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test boundary for left
        collectableX = -90;
        testCollectable.forceX(collectableX);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //test Boundary value when moving right
        collectableX = -39;
        testCollectable.forceX(collectableX);
        testCollectable.forceRight();
        testCollectable.forceUp();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //X in range, Y isn't - check boundayr at Y when moving down
        collectableX = 40;
        collectableY = -101;
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //boundary of y when moving up
        collectableY = -155;
        testCollectable.forceY(collectableY);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));



        //Both not in range
        collectableX = 940;
        collectableY = 890;
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is visible", false, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

    }

    @Test
    public void collectableSHouldBeVisibleCameraOffsetZero() {

        //set initial variables - X is out of range even with CO
        int collectableX = 15;
        int collectableY = 40;
        int cameraOffsetX = 50;
        int cameraOffsetY = 50;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);

        //X out of bounds, Y in bounds
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test Boundary value when moving right
        collectableX = 51;
        testCollectable.forceX(collectableX);
        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test boundary for left
        collectableX = -13;
        testCollectable.forceX(collectableX);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //test Boundary value when moving right
        collectableX = 51;
        testCollectable.forceX(collectableX);
        testCollectable.forceRight();
        testCollectable.forceUp();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //X in range, Y isn't - check boundayr at Y when moving down
        collectableX = 60;
        collectableY = 50;
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //boundary of y when moving up
        collectableY = -13;
        testCollectable.forceY(collectableY);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));



        //Both in range
        collectableX = 340;
        collectableY = 230;
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //set initial variables - X is out of range even with CO
        collectableX = -38;
        collectableY = 40;
        cameraOffsetX = 25;
        cameraOffsetY = 100;
        testCollectable.setVariables(ID, collectableX, collectableY, cameraOffsetX, cameraOffsetY);

        //X out of bounds, Y in bounds
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test Boundary value when moving right
        collectableX = 26;
        testCollectable.forceX(collectableX);
        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        //test boundary for left
        collectableX = -38;
        testCollectable.forceX(collectableX);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //test Boundary value when moving right
        collectableX = 26;
        testCollectable.forceX(collectableX);
        testCollectable.forceRight();
        testCollectable.forceUp();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //X in range, Y isn't - check boundayr at Y when moving down
        collectableX = 40;
        collectableY = 101;
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


        //boundary of y when moving up
        collectableY = 37;
        testCollectable.forceY(collectableY);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));



        //Both not in range
        collectableX = 320;
        collectableY = 120;
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        testCollectable.forceLeft();
        testCollectable.forceDown();

        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));

        testCollectable.forceRight();
        testCollectable.isVisible(cameraOffsetX, cameraOffsetY);
        assertEquals("Collectable is not visible", true, testCollectable.isVisible(cameraOffsetX, cameraOffsetY));


    }


    @Test
    public void collectableShouldNotHaveRegisteredACollisionInYMovementWhenMovingUpAsOOB() {
        //First case, OOB Y
        int collectableY = -65;
        int collectableX = 50;

        double expectedVelY;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testCollectable.forceDown();
        testCollectable.forceRight();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);

        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelY, testCollectable.getVelY(), 0);


        //first result on boundary
        collectableY = (testMap.length + GameMainActivity.TILE_HEIGHT + 2) * GameMainActivity.TILE_HEIGHT;
        testCollectable.forceY(collectableY);
        testCollectable.forceDown();
        testCollectable.forceRight();

        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelY, testCollectable.getVelY(), 0);

        //this time y is acceptable -  (- 6 / -70)
        collectableY = 40;
        collectableX = -6;
        testCollectable.forceDown();
        testCollectable.forceRight();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);

        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertNotEquals(expectedVelY, testCollectable.getVelY(), 0);

        collectableX = -70;
        testCollectable.forceRight();
        testCollectable.forceX(collectableX);
        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelY, testCollectable.getVelY(), 0);

        //Case when rising - NEW OOB checks
        collectableY = -1;
        collectableX = 50;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testCollectable.forceUp();
        testCollectable.forceRight();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);

        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelY, testCollectable.getVelY(), 0);


        //first result on boundary
        collectableY = (testMap.length + 1) * GameMainActivity.TILE_HEIGHT;
        testCollectable.forceY(collectableY);
        testCollectable.forceUp();
        testCollectable.forceRight();

        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelY, testCollectable.getVelY(), 0);

        //this time y is acceptable -  (- 6 / -70)
        collectableY = 40;
        collectableX = -6;
        testCollectable.forceUp();
        testCollectable.forceRight();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);

        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertNotEquals(expectedVelY, testCollectable.getVelY(), 0);

        collectableX = -70;
        testCollectable.forceUp();
        testCollectable.forceX(collectableX);
        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelY, testCollectable.getVelY(), 0);

    }

    @Test
    public void collectableShouldNotHaveRegisteredACollisionInYMovementWhenMovingUpAsNoCollisionDespiteInRange() {

        //in Range X and Y
        int collectableX = 50;
        int collectableY = 60;
        double expectedVelY;


        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(false);
        testCollectable.forceUp();
        testCollectable.forceRight();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        expectedVelY = testCollectable.getVelY();

        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelY, testCollectable.getVelY(), 0);


    }

    @Test
    public void collectableShouldHaveRegisteredACollisionInYMovementWhenMovingUpAsInBoundsAndTileIsObstacle() {

        //Needs to have both in range, and obstacle true - try one tile, then two tiles
        int collectableX = 80;
        int collectableY = 60;
        double expectedVelY;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testCollectable.forceRight();
        testCollectable.forceUp();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);

        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertNotEquals(expectedVelY, testCollectable.getVelY());

    }

    @Test
    public void collectableShouldNotHaveRegisteredACollisionInYMovementWhenMovingDownAsOOB() {
//Case when rising - NEW OOB checks
        int collectableY = -1;
        int collectableX = 50;
        double expectedVelY;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testCollectable.forceUp();
        testCollectable.forceRight();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);

        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelY, testCollectable.getVelY(), 0);


        //first result on boundary
        collectableY = (testMap.length + 1) * GameMainActivity.TILE_HEIGHT;
        testCollectable.forceY(collectableY);
        testCollectable.forceUp();
        testCollectable.forceRight();

        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelY, testCollectable.getVelY(), 0);

        //this time y is acceptable -  (- 6 / -70)
        collectableY = 40;
        collectableX = -6;
        testCollectable.forceUp();
        testCollectable.forceRight();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);

        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertNotEquals(expectedVelY, testCollectable.getVelY(), 0);

        collectableX = -70;
        testCollectable.forceUp();
        testCollectable.forceX(collectableX);
        expectedVelY = testCollectable.getVelY();
        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelY, testCollectable.getVelY(), 0);
    }

    @Test
    public void collectableShouldNotHaveRegisteredACollisionInYMovementWhenMovingDownAsNotCloseEnoughToObstacleTile() {

        int collectableX = 50;
        int collectableY = 60;
        double expectedVelY;


        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(false);
        testCollectable.forceDown();
        testCollectable.forceRight();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        expectedVelY = testCollectable.getVelY();

        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelY, testCollectable.getVelY(), 0);
    }

    @Test
    public void collectableShouldHaveRegisteredACollisionInYMovementWhenMovingDownAsInBoundsAndTileIsObstacle() {
        int collectableX = 50;
        int collectableY = 60;
        double expectedVelY;


        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testCollectable.forceDown();
        testCollectable.forceRight();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        expectedVelY = testCollectable.getVelY();

        testCollectable.checkYMovement(testMap);
        assertNotEquals(expectedVelY, testCollectable.getVelY(), 0);
    }

    @Test
    public void collectableShouldNotHaveRegisteredACollisionInXMovementWhenMovingRightAsOOB() {

    }

    @Test
    public void collectableShouldNotHaveRegisteredACollisionInXMovementWhenMovingRightAsNotCloseEnoughToObstacleTile() {
        int collectableX = 50;
        int collectableY = 60;
        double expectedVelX;


        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(false);
        testCollectable.forceUp();
        testCollectable.forceRight();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        expectedVelX = testCollectable.getVelX();

        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelX, testCollectable.getVelX(), 0);
    }

    @Test
    public void collectableShouldHaveRegisteredACollisionInXMovementWhenMovingRightAsInBoundsAndTileIsObstacle() {

    }


    @Test
    public void collectableShouldNotHaveRegisteredACollisionInXMovementWhenMovingLeftAsOOB() {

    }

    @Test
    public void collectableShouldNotHaveRegisteredACollisionInXMovementWhenMovingLeftAsNotCloseEnoughToObstacleTile() {
        int collectableX = 50;
        int collectableY = 60;
        double expectedVelX;


        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(false);
        testCollectable.forceUp();
        testCollectable.forceLeft();
        testCollectable.forceX(collectableX);
        testCollectable.forceY(collectableY);
        expectedVelX = testCollectable.getVelX();

        testCollectable.checkYMovement(testMap);
        assertEquals(expectedVelX, testCollectable.getVelX(), 0);
    }

    @Test
    public void collectableShouldHaveRegisteredACollisionInXMovementWhenMovingLeftAsInBoundsAndTileIsObstacle() {

    }


}