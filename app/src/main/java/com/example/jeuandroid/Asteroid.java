package com.example.jeuandroid;

import android.animation.ObjectAnimator;
import android.graphics.RectF;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.graphics.Path;

import java.util.Random;

public class Asteroid {
    ImageView asteroidImage;
    int frequency = 1000;
    Handler handlerForRandomAnimaton;
    Runnable randomAnimation;
    Random random = new Random();
    float xMax, yMax, h, w;
    Pattern pattern = Pattern.CUSTOM;
    Path path = new Path();
    boolean started = false;
    boolean firstStart = true;
    ObjectAnimator animatorsOfAsteroid = new ObjectAnimator();
    ImageView targetImage;

    //Les différents types de pattern des astéroides, le Pattern.CUSTOM permet d'utiliser ses propres Path
    enum Pattern{
        LINE,
        ARC,
        SERPENT,
        FOCUS,
        CUSTOM
    }

    Asteroid(ImageView asteroidImage, Path customPath){
        this.asteroidImage = asteroidImage;
        this.path = customPath;
    }
    Asteroid(ImageView asteroidImage, Path customPath, int frequency){
        this(asteroidImage, customPath);
        this.frequency = frequency;
    }
    Asteroid(ImageView asteroidImage, Pattern pattern){
        this.asteroidImage = asteroidImage;
        this.pattern = pattern;
    }
    Asteroid(ImageView asteroidFocusImage, ImageView targetImage){
        this(asteroidFocusImage, Pattern.FOCUS);
        this.targetImage = targetImage;
    }

    public void setCustomPath(Path customPath) {
        this.path = customPath;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setMaxScreenXY(float xMax, float yMax) {
        this.xMax = xMax;
        this.yMax = yMax;
    }

    public void setImageWH() {
        w = asteroidImage.getWidth();
        h = asteroidImage.getHeight();
    }

    public void start(){
        started = true;
        if(firstStart){
            firstStart = false;
            asteroidImage.setVisibility(View.VISIBLE);
            switch (pattern){
                case CUSTOM:
                    animate(true);
                    break;
                case FOCUS:
                    if(targetImage!=null) randomAnimation();
                    break;
                default:
                    randomAnimation();
                    break;
            }
        }
    }

    public void stop(){
        started = false;
        animatorsOfAsteroid.cancel();
    }

    public void reset(){
        animatorsOfAsteroid.end();
    }

    //Renvoie une hitBox circulaire
    Path getHitBox(){
        Path path = new Path();
        float x = asteroidImage.getX();
        float y = asteroidImage.getY();
        path.arcTo(x,y,x + w,y + h,0f, 359f, true);
        return path;
    }

    Path getSquareHitBox(){
        Path path = new Path();
        float x = asteroidImage.getX();
        float y = asteroidImage.getY();
        RectF rectF = new RectF(x, y, x + w, y + h);
        path.addRect(rectF, Path.Direction.CW);
        return path;
    }

    void animate(boolean infinite){
        animatorsOfAsteroid = ObjectAnimator.ofFloat(asteroidImage, View.X, View.Y, path);
        animatorsOfAsteroid.setDuration(frequency);
        if(infinite) animatorsOfAsteroid.setRepeatCount(Animation.INFINITE);
        animatorsOfAsteroid.start();
    }

    //Anime les astéroides avec une fréquence et un chemin aléatoire, dès que l'animation se finit, une nouvelle se lance
    void randomAnimation(){
        handlerForRandomAnimaton = new Handler();
        randomAnimation = new Runnable() {
            @Override
            public void run(){
                if (started && !animatorsOfAsteroid.isRunning()){
                    setRandomPath();
                    frequency = random.nextInt(5000) + 8000;
                    animate(false);
                }
                handlerForRandomAnimaton.postDelayed(this,10);
            }
        };
        randomAnimation.run();
    }

    //Donne à l'astéroïde un nouveau chemin aléatoire en fonction de son type de pattern
    public void setRandomPath(){
        switch (pattern){
            case LINE:
                setRandomLine();
                break;
            case ARC:
                setRandomArc();
                break;
            case SERPENT:
                setRandomSerpent();
                break;
            case FOCUS:
                setFocus();
                break;
            default:
        }
    }

    //Donne à l'astéroïde un chemin aléatoire qui dessine une droite
    public void setRandomLine(){
        path.reset();

        float x1 = random.nextFloat()*xMax;
        float y1 = 0f;
        float x2 = random.nextFloat()*xMax;
        float y2 = yMax;

        //Ajustement pour centrer l'astéroide en abscisse
        y1 -= h;
        x1 -= w/2;
        x2 -= w/2;

        if(random.nextBoolean()){
            path.moveTo(x1, y1);
            path.lineTo(x2, y2);
        }
        else {
            path.moveTo(x2, y2);
            path.lineTo(x1, y1);
        }
    }

    //Donne à l'astéroïde un chemin aléatoire qui dessine un arc depuis le bas de l'écran
    public void setRandomArc(){
        path.reset();

        float hauteur = random.nextFloat()*yMax*2f;
        float leftBorder = random.nextFloat()*xMax/2f;
        float rightBorder = xMax/2f + random.nextFloat()*xMax/2f;

        //Ajustement pour centrer l'astéroide en abscisse
        hauteur += h;
        leftBorder -= w/2;
        rightBorder -= w/2;


        float startAngle;
        float sweepAngle;
        if(random.nextBoolean()){
            startAngle = 180f;
            sweepAngle = 180f;
        }
        else {
            startAngle = 0f;
            sweepAngle = -180f;
        }

        path.arcTo(leftBorder, yMax - hauteur/2f, rightBorder, yMax + hauteur/2f, startAngle, sweepAngle, true);
    }

    //Donne à l'astéroïde un chemin aléatoire sinusoïdal
    public void setRandomSerpent(){
        path.reset();

        float hauteur = (random.nextFloat()*100f + yMax/5f)*2f;
        float leftBorder = random.nextFloat()*xMax/2;
        float rightBorder = xMax/2 + random.nextFloat()*xMax/2;

        //Ajustement pour centrer l'astéroide en abscisse
        leftBorder -= w/2;
        rightBorder -= w/2;

        Path path2 = new Path();
        path2.arcTo(leftBorder, -hauteur/2f, rightBorder, 0f, -90f, -180f, true);
        path2.arcTo(leftBorder, 0f, rightBorder, hauteur/2f, -90f, 180f, true);

        path.addPath(path2);
        path.addPath(path2, 0,hauteur);
        path.addPath(path2, 0,2*hauteur);
    }

    //Donne à l'astéroïde un chemin qui débute aléatoirement en haut de l'écran puis va vers le centre du vaisseau dessinant ainsi une droite
    public void setFocus(){
        path.reset();

        float x1 = random.nextFloat()*xMax;
        float y1 = 0f - asteroidImage.getMeasuredHeight();

        float xTarget = targetImage.getX() + targetImage.getMeasuredWidth()/2f;
        float yTarget = targetImage.getY() + targetImage.getMeasuredHeight()/2f;

        float tauxAcroissement = (xTarget - x1)/(yTarget - y1);
        float y2 = yMax + asteroidImage.getMeasuredHeight();
        float x2 = tauxAcroissement*y2 + x1;

        //Ajustement pour centrer l'astéroide
        x1 -= w/2;
        y1 -= h/2;
        xTarget -= w/2;
        yTarget -= h/2;
        x2 -= w/2;
        y2 -= h/2;

        path.moveTo(x1, y1);
        path.lineTo(xTarget, yTarget);
        path.lineTo(x2, y2);
    }
}
