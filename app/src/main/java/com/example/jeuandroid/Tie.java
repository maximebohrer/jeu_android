package com.example.jeuandroid;

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

        float x1, y1;
        x1 = x;
        y1 = y;
        float x2, y2;
        x2 = x1 + w;
        y2 = y;
        float x3, y3;
        x3 = x2;
        y3 = y2 + h;
        float x4, y4;
        x4 = x3 - w;
        y4 = y3;

        //Effectue une rotation des coordonnées
        float[] point1 = new float[]{x1, y1};
        float[] point2 = new float[]{x2, y2};
        float[] point3 = new float[]{x3, y3};
        float[] point4 = new float[]{x4, y4};

        point1 = rotation(point1);
        point2 = rotation(point2);
        point3 = rotation(point3);
        point4 = rotation(point4);

        path.moveTo(point1[0], point1[1]);
        path.lineTo(point2[0], point2[1]);
        path.lineTo(point3[0], point3[1]);
        path.lineTo(point4[0], point4[1]);
        path.close();
        return path;
    }
    public float[] rotation(float[] point){
        float x0 = point[0];
        float y0 = point[1];
        float xCentre = x + w/2;
        x0 -= xCentre;
        float yCentre = y + h/2;
        y0 -= yCentre;
        float temp = x0;
        x0 = (float) (x0*Math.cos(angle + Math.PI / 2) - y0*Math.sin(angle + Math.PI / 2));
        y0 = (float) (temp*Math.sin(angle + Math.PI / 2) + y0*Math.cos(angle + Math.PI / 2));
        x0 += xCentre;
        y0 += yCentre;
        return new float[]{x0, y0};
    }

    public Path getCircleHitBox(){
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
