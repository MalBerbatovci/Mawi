package com.megamal.framework.animation;

import com.megamal.framework.util.Painter;

/**
 * Created by malberbatovci on 24/09/15.
 */
public class Animation {
    private Frame[] frames;
    private double[] frameEndTimes;
    private int currentFrameIndex = 0;

    private double totalDuration = 0;
    private double currentTime = 0;

    public Animation(Frame... frames) {
        this.frames = frames;
        frameEndTimes = new double[frames.length];

        //works out totalDuration while also updating frameEndTimes
        for (int i = 0; i < frames.length; i++) {
            Frame f = frames[i];
            totalDuration += f.getDuration();
            frameEndTimes[i] = totalDuration;
        }
    }

    //keeps track of currentTime and handles irregularities, and also determines
    //currentFrameIndex by comparing to frameEndTime. Increment == delta Value
    public synchronized void update(float increment) {
        currentTime += increment;

        //animation has finished, so repeat animation
        if (currentTime > totalDuration)
            wrapAnimation();

        while (currentTime > frameEndTimes[currentFrameIndex])
            currentFrameIndex++;
    }

    private synchronized void wrapAnimation() {
        currentFrameIndex = 0;

        //using modulus, we are able to work out 'overflow' and play next animation
        //correctly
        currentTime %= totalDuration;
    }

    public synchronized  void render(Painter g, int x, int y) {
        g.drawImage(frames[currentFrameIndex].getImage(), x, y);
    }

    public synchronized void render(Painter g, int x, int y, int width, int height) {
        g.drawImage(frames[currentFrameIndex].getImage(), x, y, width, height);
    }
}
