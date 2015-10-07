package com.megamal.framework.animation;

import android.graphics.Bitmap;

/**
 * Created by malberbatovci on 24/09/15.
 */
public class Frame {
    private Bitmap image;
    private double duration;

    public Frame(Bitmap image, double duration) {
        this.image = image;
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public Bitmap getImage() {
        return image;
    }
}
