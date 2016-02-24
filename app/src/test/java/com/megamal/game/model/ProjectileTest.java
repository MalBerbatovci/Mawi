package com.megamal.game.model;

import com.megamal.framework.util.Camera;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by malberbatovci on 24/02/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProjectileTest {


    @Mock
    private Camera camera;

    @Mock
    private int[][] map;


    private Enemy[] enemyArray;

    @Mock
    private Hedgehog hedge;

    //(double x, double y, boolean isPlayers, int ID, double cameraOffsetX,
    //double cameraOffsetY, int direction)

    @Before
    public void setUp() throws Exception {
        //nothing to really do?
    }

    @Test
    public void shouldSwapDirection() throws Exception {
        when(camera.getCameraOffsetX()).thenReturn(100.0);
    }
}