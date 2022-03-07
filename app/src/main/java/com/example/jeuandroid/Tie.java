package com.example.jeuandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Path;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.lang.Math;

public class Tie {
    final int delay = 10;
    ImageView tieImage;
    ImageView explosionImage;
    Handler handlerForMovingTie;
    Runnable movingTie;
    float x, y, angle, V, newV, xMax, yMax, w, h;
    final float frottements = 0.002f;
    Boolean exploded = false;

    public Tie(ImageView tieImage, ImageView explosionImage){
        this.tieImage = tieImage;
        this.explosionImage = explosionImage;
        this.explosionImage.setVisibility(View.INVISIBLE);
    }

    public void startMove(){
        x = tieImage.getX();
        y = tieImage.getY();

        handlerForMovingTie = new Handler();
        movingTie = new Runnable() {
            @Override
            public void run() {
                if(!exploded) update();
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

        tieImage.setRotation((float) Math.toDegrees(angle + Math.PI / 2));
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

    public void explode(){
        explosionImage.setRotation((float) Math.toDegrees(angle + Math.PI / 2));
        explosionImage.setX(x);
        explosionImage.setY(y);
        explosionImage.setVisibility(View.VISIBLE);
        exploded = true;
    }

    public void reset(){
        explosionImage.setVisibility(View.INVISIBLE);
        V = 0f;
        newV = 0f;
        angle = (float) (-Math.PI/2);
        x = xMax / 2f - w / 2f;
        y = yMax / 2f - h / 2f;
        tieImage.setX(xMax / 2f - w / 2f);
        tieImage.setY(yMax / 2f - h / 2f);
        exploded = false;
    }
}
