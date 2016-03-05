package com.megamal.game.state;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.megamal.framework.util.Camera;
import com.megamal.framework.util.Painter;
import com.megamal.framework.util.Tile;
import com.megamal.framework.util.TileMapFactory;
import com.megamal.framework.util.TileMapRenderer;
import com.megamal.framework.util.UIButton;
import com.megamal.game.model.Collectable;
import com.megamal.game.model.Enemy;
import com.megamal.game.model.Hedgehog;
import com.megamal.game.model.Mover;
import com.megamal.game.model.Player;
import com.megamal.game.model.Projectile;
import com.megamal.mawi.Assets;
import com.megamal.mawi.GameMainActivity;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by malberbatovci on 22/09/15.
 */
public class LevelEditorPlayState extends State {

    //final variables for passing to mawi.walk/run() method to determine L/R
    private static final int RIGHT = 1;
    private static final int LEFT = -1;
    private static final int MAX_PROJECTILES = 10;
    private static final int NO_ENEMIES = 1;

    private TileMapRenderer tileRenderer;
    private Player mawi;
    private Tile tile;

    private ArrayList<Collectable> collectables = new ArrayList<Collectable>();

    private Projectile[] projectileArray = new Projectile[MAX_PROJECTILES];

    private Enemy[] enemyArray = new Enemy[NO_ENEMIES];

    private UIButton runR, runL, jump, shoot, backToLevelEditor;

    private boolean runningRight = false, runningLeft = false;
    private boolean initialRender = true;
    private int[][] map, originalMap;

    private int maskedAction, maxX, maxY;
    private double cameraOffsetX, cameraOffsetY, previousOffsetX, previousOffsetY;
    private Camera camera;


    public LevelEditorPlayState(int[][] levelEditorMap, int currentMaxX, int currentMaxY) {

        //initialise new map
        map = new int[(currentMaxY + 1)][(currentMaxX + 1)];

        for(int i = 0; i <= currentMaxX; i++) {
            for(int j = 0; j <= currentMaxY; j++) {
                map[j][i] = levelEditorMap[j][i];
            }
        }

        //map = levelEditorMap.clone();
        originalMap = levelEditorMap.clone();
        this.maxX = currentMaxX;
        this.maxY = currentMaxY;
    }

    @Override
    public void init() {
        cameraOffsetX = 0;
        cameraOffsetY = 0;

        tile = new Tile(1);

        tileRenderer = new TileMapRenderer(map);

        //loop to find first tile to place mawi on
        loop:
        for (int i = 2; i < (GameMainActivity.GAME_HEIGHT / GameMainActivity.TILE_HEIGHT); i++) {
            tile.setID(map[i][2]);
            if (tile.isObstacle()) {
                tile.setLocation(i, 2, cameraOffsetX, cameraOffsetY);
                break loop;
            }
        }

        mawi = new Player(tile.getX(), // + GameMainActivity.PLAYER_WIDTH
                tile.getY() - GameMainActivity.PLAYER_HEIGHT,
                GameMainActivity.PLAYER_WIDTH, GameMainActivity.PLAYER_HEIGHT);

        //hedge = new Hedgehog(400.0, 0.0, cameraOffsetX, cameraOffsetY);

        //create an array of 10 projectiles which are not active.
        for(int i = 0; i < MAX_PROJECTILES; i++ ) {
            projectileArray[i] = new Projectile(400.0, 200.0, true, 1, cameraOffsetX, cameraOffsetY, RIGHT);
            projectileArray[i].makeNonActive();
        }



        runL = new UIButton(120, 450, 220, 490, Assets.runButtonL, Assets.runButtonPressedL);
        runR = new UIButton(225, 450, 325, 490, Assets.runButtonR, Assets.runButtonPressedR);

        jump = new UIButton(610, 440, 730, 500, Assets.walkButtonL, Assets.walkButtonPressedL);
        shoot = new UIButton(740, 450, 800, 490, Assets.runButtonR, Assets.runButtonPressedR);

        backToLevelEditor = new UIButton(10, 10, 74, 74, Assets.backToLEButton, Assets.backToLEButton);

        camera = new Camera(map);

    }

    @Override
    public void update(float delta, Painter g) {

        if (!mawi.isAlive()) {
            setCurrentState(new LevelEditorState(originalMap, maxX, maxY));
        } else {

            mawi.update(delta, map, cameraOffsetX, cameraOffsetY);

            if (mawi.hitNewBox()) {
                collectables.add(mawi.getMostRecentCollectable());
            }

            if(!collectables.isEmpty()) {
                for (int i = 0; i < collectables.size(); i++) {
                    collectables.get(i).update(delta, map, cameraOffsetX, cameraOffsetY, mawi);
                }
            }

            for(int i = 0; i < enemyArray.length; i++) {
                if(enemyArray[i] != null && enemyArray[i].isActive()) {
                    enemyArray[i].update(delta, map, cameraOffsetX, cameraOffsetY, mawi);
                }
            }

            for(int i = 0; i < projectileArray.length; i++) {
                if(projectileArray[i].isActive()) {
                    projectileArray[i].update(delta, map, cameraOffsetX, cameraOffsetY, mawi, g,
                            enemyArray);
                }
            }

            previousOffsetX = cameraOffsetX;
            previousOffsetY = cameraOffsetY;
            cameraOffsetX = camera.updateCameraX(mawi, cameraOffsetX, map);
            cameraOffsetY = camera.updateCameraY(mawi, cameraOffsetY, map);
        }

    }

    @Override
    public void render(Painter g) {

        //whole screen must be rendered
        if(initialRender) {
            tileRenderer.renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);
            initialRender = false;
        }

        else {
            if(previousOffsetX != cameraOffsetX) {
            }
            tileRenderer.renderMap(g, map, cameraOffsetX, cameraOffsetY, previousOffsetX, previousOffsetY, mawi);
        }


        clearAreas(g);


        renderProjectiles(g);
        renderEnemies(g);
        renderCollectables(g);


        runR.render(g);
        runL.render(g);
        jump.render(g);
        shoot.render(g);
        backToLevelEditor.render(g);

        renderPlayer(g);


    }

    private void clearAreas(Painter g) {

        boolean isDyingFlag = false;

        //clear collectables area first
        if (!collectables.isEmpty()) {
            for (int i = 0; i < collectables.size(); i++) {
                if (collectables.get(i).isAlive() && collectables.get(i).isVisible(cameraOffsetX, cameraOffsetY)) {
                    collectables.get(i).clearAreaAroundCoin(g, cameraOffsetX, cameraOffsetY);
                }
            }
        }

        for(int i = 0; i < enemyArray.length; i++) {
            if(enemyArray[i] != null && enemyArray[i].isActive() && !enemyArray[i].isDying()) {
                enemyArray[i].clearAreaAround(g, cameraOffsetX, cameraOffsetY);
            }

            if(enemyArray[i] != null && !isDyingFlag && enemyArray[i].isDying()) {
                isDyingFlag = true;
            }
        }


        if (!mawi.hasMoved(cameraOffsetX, cameraOffsetY)) {
            mawi.clearAreaAround(g, cameraOffsetX, cameraOffsetY);
        }


        for(int i = 0; i < projectileArray.length; i++) {
            if(projectileArray[i].isActive()) {
                projectileArray[i].clearAreaAround(g, cameraOffsetX, cameraOffsetY);

                if(!isDyingFlag && projectileArray[i].isDying()) {
                    isDyingFlag = true;
                }
            }
        }

        if(isDyingFlag) {
            tileRenderer.renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);
        }



    }

    private void renderProjectiles(Painter g) {

        for(int i = 0; i < projectileArray.length; i++) {

            //then necessary to render
            if(projectileArray[i].isActive() &&
                    projectileArray[i].isVisible(cameraOffsetX, cameraOffsetY) &&
                    !projectileArray[i].isDying()) {


                projectileArray[i].render(g, cameraOffsetX, cameraOffsetY);
                tileRenderer.renderMapCollectable(g, map, cameraOffsetX, cameraOffsetY,
                        projectileArray[i].getX(), projectileArray[i].getY(), false,
                        projectileArray[i].isFalling());

            } else {
                if(projectileArray[i].isDying() && projectileArray[i].isVisible(cameraOffsetX, cameraOffsetY)) {
                    tileRenderer.renderWholeMap(g, map, cameraOffsetX, cameraOffsetY);
                    projectileArray[i].render(g, cameraOffsetX, cameraOffsetY);
                }
            }
        }
    }

    private void renderCollectables(Painter g) {
        if(!collectables.isEmpty()) {
            for (int i = 0; i < collectables.size(); i++) {

                if (collectables.get(i).isAlive() && collectables.get(i).isVisible(cameraOffsetX, cameraOffsetY)) {

                    collectables.get(i).render(g, cameraOffsetX, cameraOffsetY);
                    tileRenderer.renderMapCollectable(g, map, cameraOffsetX, cameraOffsetY, collectables.get(i).getX(),
                            collectables.get(i).getY(), false, collectables.get(i).isFalling());
                }

                if (!(collectables.get(i).isAlive())) {
                    // Log.d("Collectables", "isAlive = false!");
                    if(collectables.get(i).isVisible(cameraOffsetX, cameraOffsetY)) {
                        tileRenderer.renderMapCollectable(g, map, cameraOffsetX, cameraOffsetY, collectables.get(i).getX(),
                                collectables.get(i).getY(), true, collectables.get(i).isFalling());
                    }

                    collectables.get(i).removeImage(g, cameraOffsetX, cameraOffsetY);
                    collectables.remove(i);
                }
            }
        }

    }

    private void renderEnemies(Painter g) {

        for(int i = 0; i < enemyArray.length; i++) {
            if(enemyArray[i] != null && enemyArray[i].isActive()) {
                if(!enemyArray[i].isDying()) {
                    enemyArray[i].render(g, cameraOffsetX, cameraOffsetY);
                    tileRenderer.renderMapCollectable(g, map, cameraOffsetX, cameraOffsetY, enemyArray[i].getX(),
                            enemyArray[i].getY(), false, enemyArray[i].isFalling());
                }

                else if(enemyArray[i].isDying() && !enemyArray[i].isDead()) {
                    tileRenderer.renderMapCollectable(g, map, cameraOffsetX, cameraOffsetY, enemyArray[i].getX(),
                            enemyArray[i].getY(), false, enemyArray[i].isFalling());
                    enemyArray[i].render(g, cameraOffsetX, cameraOffsetY);

                }

                else if(enemyArray[i].safeToRemove()) {
                    enemyArray[i] = null;
                }
            }
        }
    }

    private void renderPlayer(Painter g) {

        if(!mawi.isDying()) {
            if (mawi.isJumping()) {
                if (mawi.isRight()) {
                    g.drawImage(Assets.mawiJumpingR, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                    return;
                } else if (mawi.isLeft()) {
                    g.drawImage(Assets.mawiJumpingL, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                    return;
                }
            }

            if (mawi.isWalking()) {
                if (mawi.isRight()) {
                    if (mawi.isCollided())
                        Assets.walkHitAnimR.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                    else {
                        //System.out.println("mawi.isCollided is false!");
                        Assets.walkAnimR.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                    }
                } else {
                    if (mawi.isCollided())
                        Assets.walkHitAnimL.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                    else
                        Assets.walkAnimL.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                }

                //else, running so draw necessary
            } else if (mawi.isRunning()) {
                if (mawi.isRight())
                    Assets.runAnimR.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());
                else
                    Assets.runAnimL.render(g, (int) mawi.getX(), (int) mawi.getY(), mawi.getWidth(), mawi.getHeight());


                //else, not doing anything
            } else {
                g.drawImage(Assets.mawiStandingFront, (int) mawi.getX(), (int) mawi.getY());
            }

        }


        //in this case, dying so draw necessary image and play necessary sound
        else {
            g.drawImage(Assets.mawiStandingFront, (int) mawi.getX(), (int) mawi.getY());
        }
    }


    @Override
    public boolean onTouch(MotionEvent e,int scaledX, int scaledY, int ID,
                           boolean moveAction, View v) {
        //check if walk button is pressed, this changes walkR.isPressed to true if contained
        //in the buttons rect

        if(moveAction) {
            if (runR.buttonMovedOn(scaledX, scaledY, ID)) {
                Log.d("MultiTouch", "Button moved on!");
                runningRight = true;
                mawi.run(RIGHT);
                return true;
            } else if (runR.buttonMovedOut(scaledX, scaledY, ID)) {
                runningRight = false;

                if (runningLeft) {
                    mawi.run(LEFT);
                } else {
                    mawi.stopRunning();
                }

                return true;
            } else if (runL.buttonMovedOn(scaledX, scaledY, ID)) {
                runningLeft = true;
                mawi.run(LEFT);
                return true;

            } else if (runL.buttonMovedOut(scaledX, scaledY, ID)) {
                runningLeft = false;

                if (runningRight) {
                    mawi.run(RIGHT);
                } else {
                    mawi.stopRunning();
                }
                return true;

            } else if (jump.buttonMovedOn(scaledX, scaledY, ID)) {
                mawi.jump();
                return true;

            } else if (jump.buttonMovedOut(scaledX, scaledY, ID)) {
                return true;

            } else if (shoot.buttonMovedOn(scaledX, scaledY, ID)) {
                mawi.shoot(projectileArray, cameraOffsetX, cameraOffsetY, map);
                return true;

            }
            else if (shoot.buttonMovedOut(scaledX, scaledY, ID)) {
                return true;

            }

            else if(backToLevelEditor.buttonMovedOn(scaledX, scaledY, ID)) {
                return true;
            }

            else if(backToLevelEditor.buttonMovedOut(scaledX, scaledY, ID)) {
                return true;
            }

            else {
                return true;
            }
        }

        else {

            //the particular index
            maskedAction = MotionEventCompat.getActionMasked(e);

            switch (maskedAction) {

                case MotionEvent.ACTION_DOWN: {

                    //if any buttons are pressed, then update boolean and make player perform action
                    //do not check if opposite action (i.e running left when pressing running right)
                    //is checked, as this will be suitable checked in the action_up / action_moved
                    //case to then make the player resume any previous movement
                    if (runR.onTouchDown(scaledX, scaledY, ID)) {
                        runningRight = true;
                        mawi.run(RIGHT);
                        return true;
                    } else if (runL.onTouchDown(scaledX, scaledY, ID)) {
                        runningLeft = true;
                        mawi.run(LEFT);
                        return true;
                    } else if (jump.onTouchDown(scaledX, scaledY, ID)) {
                        mawi.jump();
                        return true;
                    } else if (shoot.onTouchDown(scaledX, scaledY, ID)) {
                        mawi.shoot(projectileArray, cameraOffsetX, cameraOffsetY, map);
                        return true;
                    }

                    else if(backToLevelEditor.onTouchDown(scaledX, scaledY, ID)) {
                        return true;
                    }

                    //else, not of interest, event handled - return true
                    else {
                        return true;
                    }
                }

                case MotionEvent.ACTION_POINTER_DOWN: {
                    if (runR.onTouchDown(scaledX, scaledY, ID)) {
                        runningRight = true;
                        mawi.run(RIGHT);
                        return true;
                    } else if (runL.onTouchDown(scaledX, scaledY, ID)) {
                        runningLeft = true;
                        mawi.run(LEFT);
                        return true;
                    } else if (jump.onTouchDown(scaledX, scaledY, ID)) {
                        mawi.jump();
                        return true;
                    } else if (shoot.onTouchDown(scaledX, scaledY, ID)) {
                        mawi.shoot(projectileArray, cameraOffsetX, cameraOffsetY, map);
                        return true;
                    }

                    else if(backToLevelEditor.onTouchDown(scaledX, scaledY, ID)) {
                        return true;
                    }

                    //else, not of interest, event handled - return true
                    else {
                        return true;
                    }
                }

                case MotionEvent.ACTION_UP: {

                    //check if action up was performed on runR (i.e was previous pressed on this),
                    //if so cancel action and see if opposite was being performed and resume that
                    if (runR.onTouchUp(scaledX, scaledY, ID)) {
                        runningRight = false;

                        if (runningLeft) {
                            mawi.run(LEFT);
                        } else {
                            mawi.stopRunning();
                        }
                        return true;

                    } else if (runL.onTouchUp(scaledX, scaledY, ID)) {
                        runningLeft = false;

                        if (runningRight) {
                            mawi.run(RIGHT);
                        } else {
                            mawi.stopRunning();
                        }
                        return true;

                    } else if (jump.onTouchUp(scaledX, scaledY, ID)) {
                        return true;

                    } else if (shoot.onTouchUp(scaledX, scaledY, ID)) {
                        return true;
                    }

                    else if (backToLevelEditor.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new LevelEditorState(originalMap, maxX, maxY));
                    }

                    else {
                        return true;
                    }
                }

                case MotionEvent.ACTION_POINTER_UP: {
                    //check if action up was performed on runR (i.e was previous pressed on this),
                    //if so cancel action and see if opposite was being performed and resume that
                    if (runR.onTouchUp(scaledX, scaledY, ID)) {
                        runningRight = false;

                        if (runningLeft) {
                            mawi.run(LEFT);
                        } else {
                            mawi.stopRunning();
                        }

                        return true;
                    } else if (runL.onTouchUp(scaledX, scaledY, ID)) {
                        runningLeft = false;

                        if (runningRight) {
                            mawi.run(RIGHT);
                        } else {
                            mawi.stopRunning();
                        }

                        return true;
                    } else if (jump.onTouchUp(scaledX, scaledY, ID)) {
                        return true;
                    } else if (shoot.onTouchUp(scaledX, scaledY, ID)) {
                        return true;
                    }

                    else if (backToLevelEditor.onTouchUp(scaledX, scaledY, ID)) {
                        setCurrentState(new LevelEditorState(originalMap, maxX, maxY));
                    }

                    else {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}

