package com.megamal.game.model;

import android.graphics.Rect;

import com.megamal.framework.util.Painter;
import com.megamal.framework.util.Tile;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by malberbatovci on 24/02/16.
 */


@RunWith(PowerMockRunner.class)
@PrepareForTest(Projectile.class)
public class ProjectileTest {

    //projectile to be tested upon
    private Projectile testProjectile;

    @Mock
    private Hedgehog hedge;

    @Mock
    private Rect mockRect;

    //classic map size, in order to allow MAX_COx and MAX_COy
    private int[][] map = new int[8][13];

    @Mock
    private Tile mockTileA, mockTileB;


    private Enemy[] enemyArray;


    @Before
    public void setUp() throws Exception {


        PowerMockito.whenNew(Rect.class).withAnyArguments().thenReturn(mockRect);
        PowerMockito.whenNew(Tile.class).withArguments(0).thenReturn(mockTileA);
        PowerMockito.whenNew(Tile.class).withArguments(-1).thenReturn(mockTileB);
        enemyArray = new Enemy[1];
        enemyArray[0] = hedge;
        testProjectile = Mockito.spy(new Projectile(10, 10, true, 1, 0, 0, 1));

    }

    @Test
    public void collisionShouldKeepProjectileAliveAsEnemyIsAlreadyDying() throws Exception {


        when(hedge.isActive()).thenReturn(true);
       when(mockRect.intersect(hedge.rect)).thenReturn(true);
        when(hedge.isDying()).thenReturn(true);
        testProjectile.checkCollisionsEnemies(enemyArray);

        assertFalse("Enemy was not dying when collision checked", testProjectile.isDying());
    }

   @Test
    public void collisionShouldKeepProjectileAliveAsEnemyArrayDoesNotExsist() throws Exception {

        when(hedge.isActive()).thenReturn(true);
        when(hedge.isDying()).thenReturn(false);
        when(mockRect.intersect(hedge.rect)).thenReturn(true);
        Enemy[] enemyArrayStub = new Enemy[1];


        testProjectile.checkCollisionsEnemies(enemyArrayStub);
        assertFalse("EnemyArray was not empty", testProjectile.isDying());

    }

    @Test
    public void collisionShouldKeepProjectileAliveAsXNotCloseEnoughButYIs() throws Exception {

        when(hedge.isActive()).thenReturn(true);
        when(hedge.isDying()).thenReturn(false);
        when(mockRect.intersect(hedge.rect)).thenReturn(true);
        when(hedge.getX()).thenReturn(61.0);
        when(hedge.getY()).thenReturn(10.0);

        testProjectile.checkCollisionsEnemies(enemyArray);
        assertFalse("isDying is false", testProjectile.isDying());
    }

    @Test
    public void collisionShouldKeepProjectileAliveAsYNotCloseEnoughButXIs() throws Exception {

        //Projectile x and y: 10, 10
        when(hedge.isActive()).thenReturn(true);
        when(hedge.isDying()).thenReturn(false);
        when(mockRect.intersect(hedge.rect)).thenReturn(true);
        when(hedge.getX()).thenReturn(10.0);
        when(hedge.getY()).thenReturn(61.0);



        testProjectile.checkCollisionsEnemies(enemyArray);
        assertFalse("Y is not close to projectile", testProjectile.isDying());
    }


    @Test
    public void collisionShouldKeepProjectileAliveAsXAndYAreNotCloseEnough() throws Exception {

        when(hedge.isActive()).thenReturn(true);
        when(hedge.isDying()).thenReturn(false);
        when(mockRect.intersect(hedge.rect)).thenReturn(true);
        when(hedge.getX()).thenReturn(60.0);
        when(hedge.getY()).thenReturn(60.0);


        testProjectile.checkCollisionsEnemies(enemyArray);
        assertFalse("X and Y are close enough to projectile", testProjectile.isDying());
    }

    @Test
    public void collisionShouldKeepKillProjectileDueToIntersection() throws Exception {

        double inBoundsX = 12.0;
        double inBoundsY = 12.0;

        when(hedge.isActive()).thenReturn(true);
        when(hedge.isDying()).thenReturn(false);
        when(mockRect.intersect(hedge.rect)).thenReturn(true);
        when(hedge.getX()).thenReturn(inBoundsX);
        when(hedge.getY()).thenReturn(inBoundsY);

        testProjectile.checkCollisionsEnemies(enemyArray);
        assertTrue("isDying is false", testProjectile.isDying());
    }

    @Test
    public void collisionShouldKeepAliveProjectileDueToNoIntersection() throws Exception {

        double inBoundsX = 12.0;
        double inBoundsY = 12.0;

        when(hedge.isActive()).thenReturn(true);
        when(hedge.isDying()).thenReturn(false);

        //no intersection
        when(mockRect.intersect(hedge.rect)).thenReturn(false);


        when(hedge.getX()).thenReturn(inBoundsX);
        when(hedge.getY()).thenReturn(inBoundsY);

        testProjectile.checkCollisionsEnemies(enemyArray);
        assertFalse("isDying is true", testProjectile.isDying());
    }

    @Test
    public void noXCollisionDueToOutOfBoundsXValueLeft() throws Exception {

        //initial checks
        int outOfBoundsX = -1;
        int outOfBoundsY = 15;
        int left = -1;
        int id = 1;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;


        //if the test happens to pass initial check, then return true for is Obstacle
        //in order to force change of direction
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);


        //set new testProjectile with appropriate values
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                left);

        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);


        assertEquals(left, testProjectile.getCurrentDirection());

    }

    @Test
    public void noXCollisionDueToOutOfBoundsValue() throws Exception {

        //initial checks
        int outOfBoundsX = 15;
        int outOfBoundsY = -1;
        int left = -1;
        int right = 1;
        int id = 1;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;


        //if the test happens to pass initial check, then return true for is Obstacle
        //in order to force change of direction
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);


        //set new testProjectile with appropriate values
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                left);

        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //check that no collision has been made
        assertEquals(left, testProjectile.getCurrentDirection());


        //check same with movement in right instead
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                right);

        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //check that no collision made
        assertEquals(right, testProjectile.getCurrentDirection());


        //check case when Y is in Bound but X isn't
        outOfBoundsY = 15;
        outOfBoundsX = -1;

        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                left);

        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);


        assertEquals(left, testProjectile.getCurrentDirection());

        //check projectile going right instead of left
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                right);

        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);


        assertEquals(right, testProjectile.getCurrentDirection());


        //check case when both X and Y are out of bounds
        outOfBoundsY = -1;
        outOfBoundsX = -1;

        //set with left
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                left);

        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);


        //ensure no collisions
        assertEquals(left, testProjectile.getCurrentDirection());


        //check with right
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                right);

        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);


        //ensure no collisions
        assertEquals(right, testProjectile.getCurrentDirection());

    }

    @Test
    public void noYCollisionDueToOutOfBoundsValues() throws Exception {

        //initial checks
        int outOfBoundsX = -1;
        int outOfBoundsY = -1;
        int left = -1;
        int right = 1;
        int id = 1;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;
        int velY;


        //if the test happens to pass initial check, then return true for is Obstacle
        //in order to force change of direction
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);



        //set new testProjectile with appropriate values, moving in left
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                left);

        //get velocity in Y direction before collision
        velY = testProjectile.getVelY();

        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //ensure no collisions
        assertEquals(velY, testProjectile.getVelY());



        //set new testProjectile with appropriate values, moving in right
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                right);

        //get velcoity in Y direction before collision
        velY = testProjectile.getVelY();

        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //ensure no collisions
        assertEquals(velY, testProjectile.getVelY());


        //check case when X is in bound, but Y isn't
        outOfBoundsX = 15;
        outOfBoundsY = -1;

        //set new testProjectile with appropriate values, moving in left
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                left);

        //get velocity in Y direction before collision
        velY = testProjectile.getVelY();

        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //ensure no collisions
        assertEquals(velY, testProjectile.getVelY());



        //set new testProjectile with appropriate values, moving in right
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                right);

        //get velcoity in Y direction before collision
        velY = testProjectile.getVelY();

        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //ensure no collisions
        assertEquals(velY, testProjectile.getVelY());


        //check case when Y is in bound, but X isn't
        outOfBoundsX = -1;
        outOfBoundsY = 15;


        //set new testProjectile with appropriate values, moving in left
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                left);

        //get velocity in Y direction before collision
        velY = testProjectile.getVelY();

        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //ensure no collisions
        assertEquals(velY, testProjectile.getVelY());



        //set new testProjectile with appropriate values, moving in right
        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                right);

        //get velcoity in Y direction before collision
        velY = testProjectile.getVelY();

        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //ensure no collisions
        assertEquals(velY, testProjectile.getVelY());

    }

    @Test
    public void noCollisionInXDueToProjectileNotBeingCloseEnough() {

        //N.B = if y is not close enough, then two tiles isn't set, but a collision will
        //still be detected

        //initial values, Y is close enough, but X isn't
        int projectileY = 45;
        int projectileX = 30;
        int left = -1;
        int right = 1;
        int id = 1;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;


        //for right: Y in bounds = 56 -> 63 / 64 -> 68 (if moving UP)
        //for right: Y in bounds = 44 -> 48 / 51 -> 55 (if moving Down)
        //for right: X in bounds = 42 -> 47
        //for left: X in bounds = 64 -> 70 == 0 -> 6


        //ensure that if passed initial constraint, isObstacle return true
        //so that test can fail
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);

        //check moving left first;
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);
        assertEquals("X is close enough", right, testProjectile.getCurrentDirection());



        //not close enough - mockTileB should never be set or called for collision
        projectileY = 30;
        //close enough
        projectileX = 45;

        //mock stuff and initialise
        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(true);
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //in this case, two tiles should not be acticated, however should collide
        assertEquals("Y is close enough and has activated twoTiles", right,
                testProjectile.getCurrentDirection());



        //x is not close enough for Left query
        projectileX = 20;

        //y is close enough (moving down)
        projectileY = 45;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);

        //check moving left
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);
        assertEquals("X is close enough", left, testProjectile.getCurrentDirection());


        //close enough for Left Query;
        projectileX = 4;

        //not close enough
        projectileY = 30;

        when(mockTileA.isObstacle()).thenReturn(false);

        //as y is not close enough, two tiles should ever be initialised and
        //mock tile B will never be queried - so should return false
        when(mockTileB.isObstacle()).thenReturn(true);

        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);
        assertEquals("Two Tiles activated", left, testProjectile.getCurrentDirection());


    }


    @Test
    public void collisionShouldHappenInXAsValuesAreCloseEnough() {

        //X close enough, Y not close enough
        int projectileY = 30;
        int projectileX = 45;
        int left = -1;
        int right = 1;
        int id = 1;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(false);
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //should have collided
        assertEquals("X is not close enough", left, testProjectile.getCurrentDirection());


        //X close enough, Y close enough
        projectileY = 45;
        projectileX = 45;

        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(true);

        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //should have collided
        assertEquals("X is not close enough", left, testProjectile.getCurrentDirection());


        //X close enough for left movement, Y not
        projectileX = 65;
        projectileY = 30;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(false);

        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //should have collided
        assertEquals("X is not close enough", right, testProjectile.getCurrentDirection());

        //X close enough for left movement, Y close enough
        projectileX = 65;
        projectileY = 54;

        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(true);

        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //should have collided
        assertEquals("TwoTiles not activated", right, testProjectile.getCurrentDirection());

    }


    @Test
    public void noCollisionYDueToProjectileNotbeingCloseEnough() {

        //in this case, Y is not close enough, and neither is X
        int projectileY = 30;
        int projectileX = 30;
        int left = -1;
        int right = 1;
        int id = 1;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;

        int velY;

        //mock tileA and b to return true, to make test failure visible
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        velY = testProjectile.getVelY();

        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);
        //should not have collided
        assertEquals("X or Y are close enough", velY, testProjectile.getVelY());


        //check same with left
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        velY = testProjectile.getVelY();

        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);


        //should not collided
        assertEquals("X or Y are close enough", velY, testProjectile.getVelY());


        //Left in bounds: 56 -> 63 // 0 -> 2
        //Right in bounds: 46 -> 48 // 51 -> 56
        //y in bounds, x is not
        projectileX = 30;
        projectileY = 45;

        //should never initialise mocktileB as X is not close enough - meaning
        //should fail
        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(true);

        //check with left
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        velY = testProjectile.getVelY();

        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //Should not have collided
        assertEquals("X or Y are close enough", velY, testProjectile.getVelY());



        //check with right
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        velY = testProjectile.getVelY();

        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);


        //should not have collided
        assertEquals("X or Y are close enough", velY, testProjectile.getVelY());


        //check when Y is not in bounds, but X is
        projectileX = 45;
        projectileY = 30;

        //check with right first
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        velY = testProjectile.getVelY();

        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);
        //should not have collided
        assertEquals("X or Y are close enough", velY, testProjectile.getVelY());


        //check with Left as well as right
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        velY = testProjectile.getVelY();

        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);
        //should not have collided
        assertEquals("X or Y are close enough", velY, testProjectile.getVelY());
    }


    @Test
    public void shouldCollideInYAsProjectileIsCloseEnough() {

        //Left in bounds: 56 -> 63 // 0 -> 2
        //Right in bounds: 46 -> 48 // 51 -> 56
        //Down in bounds: 42 -> 47

        //initial set up - Y close enough, X isn't
        int projectileY = 43;
        int projectileX = 30;
        int left = -1;
        int right = 1;
        int id = 1;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;
        int velY;

        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(false);

        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        velY = testProjectile.getVelY();
        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //should have collided
        assertNotEquals("X is not close enough", velY, testProjectile.getVelY());


        //test on Left too
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        velY = testProjectile.getVelY();
        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //should have collided
        assertNotEquals("X is not close enough", velY, testProjectile.getVelY());

        //Left in bounds: 56 -> 63 // 0 -> 2
        //Right in bounds: 46 -> 48 // 51 -> 56
        //Down in bounds: 42 -> 47
        //Up in bounds: 64 -> 70

        //both x (for right) and Y in bounds
        projectileX = 48;
        projectileY = 45;

        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(true);

        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        velY = testProjectile.getVelY();
        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //should have collided
        assertNotEquals("X is not close enough", velY, testProjectile.getVelY());

        //test by changing which tile returns true and false - shouldnt matter
        //as both are in bounds
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(false);

        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        velY = testProjectile.getVelY();
        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //should have collided
        assertNotEquals("X is not close enough", velY, testProjectile.getVelY());

        //X in bounds for left, and Y in bounds
        projectileX = 65;
        projectileY = 43;

        when(mockTileA.isObstacle()).thenReturn(false);
        when(mockTileB.isObstacle()).thenReturn(true);

        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        velY = testProjectile.getVelY();
        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //should have collided
        assertNotEquals("X is not close enough", velY, testProjectile.getVelY());



        //test by changing which tile returns true and false - shouldnt matter
        //as both are in bounds
        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(false);

        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);

        velY = testProjectile.getVelY();
        testProjectile.checkYMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);

        //should have collided
        assertNotEquals("X is not close enough", velY, testProjectile.getVelY());

    }

    @Test
    public void shouldBeVisibleWithNoCameraOffset() {

        int cameraOffsetX = 0;
        int cameraOffsetY = 0;
        int id = 1;
        int left = -1;
        int right = 1;

        //Left = <- == (x + width)
        //Right = -> == x
        //this value is the boundary for when moving right
        int projectileX = 832;
        int projectileY = 30;

        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);

        assertEquals(true, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));


        //out of bounds value for moving left, should return false
        projectileX = -15;
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        assertEquals(true, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));



        //Moving down = y == 513 boundary

        projectileX = 30;
        projectileY = 512;

        //velY is naturally going downwards, so test this first
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        assertEquals(true, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));

        //try with right, to ensure still same
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        assertEquals(true, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));


        projectileY = 1;
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        assertEquals(true, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));

        //try with left, to ensure still same
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        assertEquals(true, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));


        //now test with velY < 0 (i.e rising)
        //Moving Up = y + height == -17 // 497;

        projectileY = -15;
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        testProjectile.forceVelY(-30);
        assertEquals(true, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));

        //try with left, to ensure still same
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        testProjectile.forceVelY(-30);
        assertEquals(true, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));


        projectileY = 494;
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        testProjectile.forceVelY(-30);
        assertEquals(true, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));

        //try with left, to ensure still same
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        testProjectile.forceVelY(-30);
        assertEquals(true, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));

    }

    @Test
    public void shouldNotBeVisibleWithNoCameraOffset() {

        int cameraOffsetX = 0;
        int cameraOffsetY = 0;
        int id = 1;
        int left = -1;
        int right = 1;

        //Left = <- == (x + width)
        //Right = -> == x
        //this value is the boundary for when moving right
        int projectileX = 833;
        int projectileY = 30;

        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);

        assertEquals(false, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));


        //out of bounds value for moving left, should return false
        projectileX = -17;
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        assertEquals(false, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));



        //Moving down = y == 513 boundary

        projectileX = 30;
        projectileY = 513;

        //velY is naturally going downwards, so test this first
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        assertEquals(false, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));

        //try with right, to ensure still same
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        assertEquals(false, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));


        projectileY = -1;
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        assertEquals(false, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));

        //try with left, to ensure still same
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        assertEquals(false, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));


        //now test with velY < 0 (i.e rising)
        //Moving Up = y + height == -17 // 497;

        projectileY = -17;
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        testProjectile.forceVelY(-30);
        assertEquals(false, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));

        //try with left, to ensure still same
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        testProjectile.forceVelY(-30);
        assertEquals(false, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));


        projectileY = 497;
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                left);
        testProjectile.forceVelY(-30);
        assertEquals(false, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));

        //try with left, to ensure still same
        testProjectile.reset(projectileX, projectileY, true, id, cameraOffsetX, cameraOffsetY,
                right);
        testProjectile.forceVelY(-30);
        assertEquals(false, testProjectile.isVisible(cameraOffsetX, cameraOffsetY));

    }
}