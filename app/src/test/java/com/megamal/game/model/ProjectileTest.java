package com.megamal.game.model;

import android.graphics.Rect;
import com.megamal.framework.util.Tile;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by malberbatovci on 24/02/16.
 */


@RunWith(PowerMockRunner.class)
@PrepareForTest(Projectile.class)
public class ProjectileTest {

    private Projectile testProjectile;

    @Mock
    private Hedgehog hedge;

    @Mock
    private Rect mockRect;

    private int[][] map;

    @Mock
    private Tile mockTileA, mockTileB;


    private Enemy[] enemyArray;


    @Before
    public void setUp() throws Exception {


        PowerMockito.whenNew(Rect.class).withAnyArguments().thenReturn(mockRect);
//        PowerMockito.whenNew(int[][].class).withAnyArguments().thenReturn(map);
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
        assertFalse("EnemyArray was empty", testProjectile.isDying());

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


        when(hedge.isActive()).thenReturn(true);
        when(hedge.isDying()).thenReturn(false);
        when(mockRect.intersect(hedge.rect)).thenReturn(true);
        when(hedge.getX()).thenReturn(12.0);
        when(hedge.getY()).thenReturn(12.0);

        testProjectile.checkCollisionsEnemies(enemyArray);
        assertTrue("isDying is false", testProjectile.isDying());
    }

    @Test
    public void collisionShouldKeepAliveProjectileDueToNoIntersection() throws Exception {

        double inBoundsX = 12.0;
        double inBoundsY = 12.0;

        when(hedge.isActive()).thenReturn(true);
        when(hedge.isDying()).thenReturn(false);
        when(mockRect.intersect(hedge.rect)).thenReturn(false);
        when(hedge.getX()).thenReturn(inBoundsX);
        when(hedge.getY()).thenReturn(inBoundsY);

        testProjectile.checkCollisionsEnemies(enemyArray);
        assertFalse("isDying is true", testProjectile.isDying());
    }

    /*@Test
    public void noXCollisionDueToOutOfBoundsValueLeft() throws Exception {

        int outOfBoundsX = -10;
        int outOfBoundsY = -10;
        int left = -1;
        int id = 1;
        int cameraOffsetX = 0;
        int cameraOffsetY = 0;

        //(double x, double y, boolean isPlayers, int ID, double cameraOffsetX,
        //double cameraOffsetY, int direction)


        when(mockTileA.isObstacle()).thenReturn(true);
        when(mockTileB.isObstacle()).thenReturn(true);

        testProjectile.reset(outOfBoundsX, outOfBoundsY, true, id, cameraOffsetX, cameraOffsetY,
                left);

        testProjectile.checkXMovement(map, mock(Painter.class), cameraOffsetX, cameraOffsetY);


        assertEquals(left, testProjectile.getCurrentDirection());

    }*/
}