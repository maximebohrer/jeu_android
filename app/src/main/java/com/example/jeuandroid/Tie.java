package com.example.jeuandroid;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import java.lang.Math;

public class Tie {
    final int delay = 10;
    ImageView tieImage;
    Handler handlerForMovingTie;
    Runnable movingTie;
    float x, y, angle, V, newV, xMax, yMax, w, h;
    final float frottements = 0.002f;

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
}
