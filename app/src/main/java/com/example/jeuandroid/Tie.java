package com.example.jeuandroid;

import android.graphics.Path;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import java.lang.Math;

public class Tie {
    final int delay = 10;
    ImageView tieImage;
    Handler handlerForMovingTie;
    Handler handlerForCollisionTie;
    Runnable movingTie;
    Runnable collisionTie;
    float x, y, angle, V, newV, xMax, yMax, w, h;
    final float frottements = 0.002f;
    Asteroid[] asteroids = new Asteroid[0];

    public Tie(ImageView tieImage){
        this.tieImage = tieImage;
        this.w = tieImage.getWidth();
        this.h = tieImage.getHeight();
    }

    public void startMove(){
        x = tieImage.getX();
        y = tieImage.getY();

        handlerForMovingTie = new Handler();
        movingTie = new Runnable() {
            @Override
            public void run() {
                update();
                handlerForMovingTie.postDelayed(this, delay);
            }
        };
        movingTie.run();
    }

    private void update() {
        if (newV >= V) V = newV;
        else V -= frottements * delay;
        if (V < 0) V = 0;


        //V = newV;
        x += V * delay * Math.cos(angle);
        y += V * delay * Math.sin(angle);

        if (x > xMax - w / 2) x = xMax - w / 2;
        if (x < - w / 2) x = - w / 2;
        if (y > yMax - w / 2) y = yMax - w / 2;
        if (y < - w / 2) y = - w / 2;

        tieImage.setRotation((float)Math.toDegrees(angle + Math.PI / 2));
        tieImage.setX(x);
        tieImage.setY(y);
    }

    public void setSpeed(float xCoeff, float yCoeff) {
        newV = (float) Math.sqrt(xCoeff * xCoeff + yCoeff * yCoeff) * 1.5f;
        if (!(xCoeff == 0) && !(yCoeff == 0)) {
            if (xCoeff == 0) angle = (float)(yCoeff > 0 ? Math.PI / 2 : - Math.PI / 2);
            else if (yCoeff == 0) angle = (float)(xCoeff > 0 ? 0 : Math.PI);
            else angle = (float)(xCoeff > 0 ? Math.atan(yCoeff / xCoeff) : Math.atan(yCoeff / xCoeff) + Math.PI);
        }
    }

    public void setMaxScreenXY(float xMax, float yMax) {
        this.xMax = xMax;
        this.yMax = yMax;
    }

    public void setImageWH() {
        w = tieImage.getWidth();
        h = tieImage.getHeight();
    }

    public Path getHitBox(){
        Path path = new Path();
        float x = tieImage.getX();
        float y = tieImage.getY();
        float w = tieImage.getMeasuredWidth();
        float h = tieImage.getMeasuredHeight();
        path.arcTo(x,y,x+w,y+h,0f, 359f, true);
        return path;
    }

    public void startCollision(){
        handlerForCollisionTie = new Handler();
        collisionTie = new Runnable() {
            @Override
            public void run() {
                if(isColliding()){
                    explode();
                };
                handlerForMovingTie.postDelayed(this, delay);
            }
        };
        collisionTie.run();
    }

    public boolean isColliding(){
        Path path = new Path();
        Path hitBoxTie = getHitBox();
        Asteroid asteroid;
        for(int i = 0; i<asteroids.length; i++){
            asteroid = asteroids[i];
            path.op(hitBoxTie, asteroid.getHitBox(), Path.Op.INTERSECT);
            if(!path.isEmpty()){
                return true;
            }
        }
        return false;
    }

    public void explode(){
        Log.d("Explode", "Le vaisseau est entré en collision");
    }

    public void setAsteroids(Asteroid[] asteroids){
        this.asteroids = asteroids;
    }
}
