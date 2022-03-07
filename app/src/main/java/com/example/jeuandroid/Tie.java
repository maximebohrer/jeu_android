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
        y1 = y + h/3;
        float x2, y2;
        x2 = x + w/5;
        y2 = y;
        float x3, y3;
        x3 = x + w/2;
        y3 = y + h/3;
        float x4, y4;
        x4 = x + w*4/5;
        y4 = y;
        float x5, y5;
        x5 = x + w;
        y5 = y + h/3;
        float x6, y6;
        x6 = x + w;
        y6 = y + h*2/3;
        float x7, y7;
        x7 = x + w*4/5;
        y7 = y + h;
        float x8, y8;
        x8 = x + w/2;
        y8 = y + h*2/3;
        float x9, y9;
        x9 = x + w/5;
        y9 = y + h;
        float x10, y10;
        x10 = x;
        y10 = y + h*2/3;

        //Effectue une rotation des coordonnées
        float[] point1 = new float[]{x1, y1};
        float[] point2 = new float[]{x2, y2};
        float[] point3 = new float[]{x3, y3};
        float[] point4 = new float[]{x4, y4};
        float[] point5 = new float[]{x5, y5};
        float[] point6 = new float[]{x6, y6};
        float[] point7 = new float[]{x7, y7};
        float[] point8 = new float[]{x8, y8};
        float[] point9 = new float[]{x9, y9};
        float[] point10 = new float[]{x10, y10};
        float[][] points = new float[][]{point1, point2, point3, point4, point5, point6, point7, point8, point9, point10};

        float[] point;
        float xp, yp;
        for(int i=0; i<points.length; i++){
            point = rotation(points[i]);
            xp = point[0];
            yp = point[1];
            if(i==0){
                path.moveTo(xp,yp);
            }
            else {
                path.lineTo(xp,yp);
            }
        }
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
