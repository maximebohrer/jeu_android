package com.example.jeuandroid;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.graphics.Path;

public class Asteroid {
    ImageView imageViewOfAsteroid;
    Path pattern;
    int frequency = 1000;

    Asteroid(ImageView imageViewOfAsteroid){
        this.imageViewOfAsteroid = imageViewOfAsteroid;
    }

    public void setPattern(Path pattern) {
        this.pattern = pattern;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    Rect getHitBox(){
        int[] position = new int[2];
        imageViewOfAsteroid.getLocationOnScreen(position);
        return new Rect(position[0], position[1], position[0] + imageViewOfAsteroid.getMeasuredWidth(), position[1] + imageViewOfAsteroid.getMeasuredHeight());
    }
    void animate(){
        ObjectAnimator animatorsOfAsteroid = ObjectAnimator.ofFloat(imageViewOfAsteroid, View.X, View.Y, pattern);
        animatorsOfAsteroid.setDuration(frequency);
        animatorsOfAsteroid.setRepeatCount(Animation.INFINITE);
        animatorsOfAsteroid.start();
    }
}
