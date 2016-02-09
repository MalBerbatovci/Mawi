package com.megamal.mawi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.megamal.framework.util.InputHandler;
import com.megamal.framework.util.Painter;
import com.megamal.game.state.LoadState;
import com.megamal.game.state.State;

/**
 * Created by malberbatovci on 22/09/15.
 */


public class GameView extends SurfaceView implements Runnable{

    private Bitmap gameImage;
    private Rect gameImageSrc;
    private Rect gameImageDst;
    private Canvas gameCanvas;
    private Painter graphics;

    private Thread gameThread;
    private volatile boolean running = false;
    private volatile State currentState;

    private InputHandler inputHandler;

    public GameView(Context context, int gameWidth, int gameHeight) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        //covers the whole screen and will not need to be transparent
        gameImage = Bitmap.createBitmap(gameWidth, gameHeight, Bitmap.Config.RGB_565);

        //Used to specify which region of the gameImage should be drawn on screen
        gameImageSrc = new Rect(0, 0, gameImage.getWidth(), gameImage.getHeight());

        //Used to specify how the gameImage should be scaled when drawn
        gameImageDst = new Rect();

        gameCanvas = new Canvas(gameImage);
        graphics = new Painter(gameCanvas);

        //surface holer allows us to choose to be informed when
        //the surface has been created, and when it has been destroyed
        SurfaceHolder holder = getHolder();
        holder.addCallback(new Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //Log.d("GameView", "Surface Created");
                initInput();
                if (currentState == null) {
                    setCurrentState(new LoadState());
                }
                initGame();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //Log.d("GameView", "Surface Destroyed");
                pauseGame();

            }
        });
    }

    public GameView(Context context) {
        super(context);
    }

    public void setCurrentState(State newState) {
        System.gc();
        newState.init();
        currentState = newState;
        inputHandler.setCurrentState(currentState);
    }

    private void initInput() {
        if (inputHandler == null) {
            inputHandler = new InputHandler();
        }

        setOnTouchListener(inputHandler);
    }

    @Override
    public void run() {
        long updateDurationMillis = 0; //measuring update and rendering
        long sleepDurationMillis = 0; //measuring sleep

        while (running) {
            long beforeUpdateRender = System.nanoTime();

            //calculate duration of each iteration of the game loop
            long deltaMillis = sleepDurationMillis + updateDurationMillis;


            updateAndRender(deltaMillis);

            //calculate the duration of the steps above, and divide to produce milliseconds
            updateDurationMillis = (System.nanoTime() - beforeUpdateRender) / 1000000L;

            //calculate sleeping time and choose max in order to enforce a minimum
            //of 2 seconds of sleep, without going into negative numbers
            sleepDurationMillis = Math.max(2, 17 - updateDurationMillis);

            try {
                Thread.sleep(sleepDurationMillis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void updateAndRender(long delta) {

        //by passing delta to update, each update method will have access
        //to the time taken since previous iteration of update
        currentState.update(delta / 1000f, graphics); //divide by 1000 to give seconds
        currentState.render(graphics);
        renderGameImage();
    }

    private void renderGameImage() {
        //locks canvas for drawing, allows only 1 thread to draw at a time
        Canvas screen = getHolder().lockCanvas();

        //All drawings happen below
        if (screen != null) {

            //check the boundaries of the screen by passing in DST - this informs the Rect object
            //how big the screen is (gameImageDst.left/top/right/bottom values are updated to match screen
            screen.getClipBounds(gameImageDst);

            //with this info, we draw the gameImage to screen using gameImageSrc to retrieve the entire game Image
            //and using gameImageDST to scale it to fit the screen (look @ painter to see how handled)
            screen.drawBitmap(gameImage, gameImageSrc, gameImageDst, null);
            getHolder().unlockCanvasAndPost(screen);
        }
    }


    private void initGame() {
        running = true;
        gameThread = new Thread(this, "Game Thread");
        gameThread.start();
    }

    private void pauseGame() {
        running = false;

        //thread.join used to tell gameThread to stop executing when
        //the application is paused
        while (gameThread.isAlive()) {
            try {
                gameThread.join();
                break;
            } catch (InterruptedException e) {
            }
        }
    }

    public Painter getGraphics() {
        return graphics;
    }
}
