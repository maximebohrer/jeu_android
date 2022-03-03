package com.example.jeuandroid;

import android.animation.ObjectAnimator;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.graphics.Path;

public class Asteroid {
    ImageView imageViewOfAsteroid;
    Path pattern;
    int frequency = 1000;

    Asteroid(ImageView imageViewOfAsteroid, Path pattern){
        this.imageViewOfAsteroid = imageViewOfAsteroid;
        this.pattern = pattern;
        animate();
    }
    Asteroid(ImageView imageViewOfAsteroid, Path pattern, int frequency){
        this.imageViewOfAsteroid = imageViewOfAsteroid;
        this.pattern = pattern;
        this.frequency = frequency;
        animate();
    }

    public void setPattern(Path pattern) {
        this.pattern = pattern;
        animate();
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    
    Path getHitBox(){
        Path path = new Path();
        float x = imageViewOfAsteroid.getX();
        float y = imageViewOfAsteroid.getY();
        float w = imageViewOfAsteroid.getMeasuredWidth();
        float h = imageViewOfAsteroid.getMeasuredHeight();
        path.arcTo(x,y,x+w,y+h,0f, 359f, true);
        return path;
    }

    Path getHitBox2(){
        Path path = new Path();
        float x = imageViewOfAsteroid.getX();
        float y = imageViewOfAsteroid.getY();
        float w = imageViewOfAsteroid.getMeasuredWidth();
        float h = imageViewOfAsteroid.getMeasuredHeight();
        RectF rectF = new RectF(x, y, x + w, y+h);
        path.addRect(rectF, Path.Direction.CW);
        return path;
    }

    void animate(){
        ObjectAnimator animatorsOfAsteroid = ObjectAnimator.ofFloat(imageViewOfAsteroid, View.X, View.Y, pattern);
        animatorsOfAsteroid.setDuration(frequency);
        animatorsOfAsteroid.setRepeatCount(Animation.INFINITE);
        animatorsOfAsteroid.start();
    }
}
