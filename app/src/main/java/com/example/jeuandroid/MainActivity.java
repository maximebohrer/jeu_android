package com.example.jeuandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    float xStart = 0;
    float yStart = 0;

    float xCoeff = 0;
    float yCoeff = 0;

    float xPadOffset = 0;
    float yPadOffset = 0;
    float padOffsetFactor = 100;

    boolean useAccelerometer = false;
    private SensorManager mSensorManager;
    private Sensor accelerometer;

    View mainLayout;
    ImageView padInt;
    ImageView padExt;
    ImageView tieImage;
    ImageView explosionImage;
    ImageView asteroidLineImage;
    ImageView asteroidArcImage;
    ImageView asteroidSerpentImage;
    ImageView asteroidFocusImage;
    Tie tie;
    Asteroid asteroidLine;
    Asteroid asteroidArc;
    Asteroid asteroidSerpent;
    Asteroid asteroidFocus;
    TextView scoreText;

    Handler handlerForCollisionTie;
    Runnable collisionTie;
    int score = 0;
    ArrayList<Asteroid> asteroids = new ArrayList<>();

    enum GameState{
        CREATING,
        STARTING,
        RUNNING,
        STOPPING,
    }
    GameState gameState = GameState.CREATING;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Objets du layout
        mainLayout = findViewById(R.id.mainLayout);
        padInt = findViewById(R.id.padInt);
        padExt = findViewById(R.id.padExt);
        tieImage = findViewById(R.id.tie);
        explosionImage = findViewById(R.id.explosion);
        asteroidLineImage = findViewById(R.id.asteroid);
        asteroidArcImage = findViewById(R.id.asteroid2);
        asteroidSerpentImage = findViewById(R.id.asteroid3);
        asteroidFocusImage = findViewById(R.id.asteroid4);
        scoreText = findViewById(R.id.scoreText);

        tie = new Tie(tieImage, explosionImage);

        asteroidLine = new Asteroid(asteroidLineImage, Asteroid.Pattern.LINE);
        asteroidArc = new Asteroid(asteroidArcImage, Asteroid.Pattern.ARC);
        asteroidSerpent = new Asteroid(asteroidSerpentImage, Asteroid.Pattern.SERPENT);
        asteroidFocus = new Asteroid(asteroidFocusImage, tieImage);
        asteroids.add(asteroidLine);
        asteroids.add(asteroidArc);
        asteroids.add(asteroidSerpent);
        asteroids.add(asteroidFocus);

        //Layout listener pour placer le joystick
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            positionPadExt();
            positionPadInt();
            positionScoreText();
            tie.setMaxScreenXY(mainLayout.getWidth(), mainLayout.getHeight());
            tie.setImageWH();
            for(Asteroid asteroid : asteroids){
                asteroid.setMaxScreenXY(mainLayout.getWidth(), mainLayout.getHeight());
                asteroid.setImageWH();
            }
            if(gameState == GameState.CREATING){
                tie.reset();
            }
        });

        //Touch listener sur le vaisseau pour permettre le changement joystick / accelerometre
        tieImage.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if(gameState == GameState.CREATING) {
                    gameState = GameState.STARTING;
                    start();
                }
                if (useAccelerometer) usePad();
                else useAccelerometer();
                return true;
            }
            return false;
        });

        //Touch listener pour gérer le joystick
        padInt.setOnTouchListener((v, event) -> {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(gameState == GameState.CREATING){
                        gameState = GameState.STARTING;
                        start();
                    }
                    xStart = event.getX();
                    yStart = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float xMoved = event.getX() - xStart;
                    float yMoved = event.getY() - yStart;
                    xCoeff = xMoved / 150f;
                    yCoeff = yMoved / 150f;
                    float squaredLength = xCoeff * xCoeff + yCoeff * yCoeff;
                    if (squaredLength > 1) {
                        xCoeff /= Math.sqrt(squaredLength);
                        yCoeff /= Math.sqrt(squaredLength);
                        Log.d("x", Float.toString(xCoeff));
                        Log.d("y", Float.toString(yCoeff));
                    }
                    xPadOffset = padOffsetFactor * xCoeff;
                    yPadOffset = padOffsetFactor * yCoeff;
                    positionPadInt();
                    tie.setSpeed(xCoeff, yCoeff);
                    break;
                case MotionEvent.ACTION_UP:
                    xCoeff = 0;
                    yCoeff = 0;
                    xPadOffset = 0;
                    yPadOffset = 0;
                    positionPadInt();
                    tie.setSpeed(0,0);
                    break;
                default:
                    return false;
            }
            return true;
        });

        tie.startMove();
        collisionManager();
    }

    void positionPadExt() {
        padExt.setX(mainLayout.getWidth() / 2f - padExt.getWidth() / 2f);
        padExt.setY(mainLayout.getHeight() - padExt.getHeight() - 50);
    }

    void positionPadInt() {
        padInt.setX(padExt.getX() + (padExt.getWidth() - padInt.getWidth()) / 2f + xPadOffset);
        padInt.setY(padExt.getY() + (padExt.getHeight() - padInt.getHeight()) / 2f + yPadOffset);
    }

    void useAccelerometer() {
        padInt.setVisibility(View.INVISIBLE);
        padExt.setVisibility(View.INVISIBLE);
        useAccelerometer = true;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        xCoeff = 0;
        yCoeff = 0;
        tie.setSpeed(0, 0);
    }

    void usePad() {
        padInt.setVisibility(View.VISIBLE);
        padExt.setVisibility(View.VISIBLE);
        useAccelerometer = false;
        mSensorManager.unregisterListener(this, accelerometer);
        xCoeff = 0;
        yCoeff = 0;
        tie.setSpeed(0, 0);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float gammaX = event.values[0], gammaY = event.values[1], gammaZ = event.values[2];
        Log.d("Valeurs accelerometre",gammaX+","+gammaY+","+gammaZ);
        xCoeff = (float)(- event.values[0] * 0.3);
        if (xCoeff > 1) xCoeff = 1;
        else if (xCoeff < -1) xCoeff = -1;
        yCoeff = (float)(event.values[1] * 0.3);
        if (yCoeff > 1) yCoeff = 1;
        else if (yCoeff < -1) yCoeff = -1;
        Log.d("x", Float.toString(xCoeff));
        Log.d("y", Float.toString(yCoeff));
        tie.setSpeed(xCoeff, yCoeff);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    void positionScoreText() {
        scoreText.setX(mainLayout.getWidth() / 2f - scoreText.getWidth() / 2f);
        scoreText.setY(50f);
    }

    //Gère la collision avec le vaisseau
    public void collisionManager(){
        handlerForCollisionTie = new Handler();
        int delay = 10;
        collisionTie = new Runnable() {
            @Override
            public void run() {
                if(isColliding() && gameState!=GameState.STOPPING){
                    gameOver();
                }
                else if(gameState == GameState.RUNNING) {
                    score += 1;
                    scoreText.setText(String.valueOf(score));
                }
                handlerForCollisionTie.postDelayed(this, delay);
            }
        };
        collisionTie.run();
    }

    public boolean isColliding(){
        Path path = new Path();
        Path hitBoxTie = tie.getHitBox();
        for (Asteroid asteroid : asteroids) {
            path.op(hitBoxTie, asteroid.getHitBox(), Path.Op.INTERSECT);
            if (!path.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void gameOver(){
        gameState=GameState.STOPPING;
        stop();
        gameOverMessage();
    }

    public void gameOverMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(tieImage.getContext());
        builder.setTitle("Game over");
        builder.setMessage("Score : " + score);
        builder.setPositiveButton("Recommencer", (dialog, id) -> {
            reset();
            gameState = GameState.CREATING;
        });
        builder.setNegativeButton("Quitter", (dialog, id) -> finish());
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Gère le jeu
    public void reset(){
        score = 0;
        scoreText.setText("0");
        tie.reset();
        for (Asteroid asteroid : asteroids) {
            asteroid.reset();
        }
        if(useAccelerometer) usePad();
    }

    public void start(){
        gameState = GameState.RUNNING;
        for (Asteroid asteroid : asteroids) {
            asteroid.start();
        }
    }

    public void stop(){
        tie.explode();
        for(Asteroid asteroid : asteroids){
            asteroid.stop();
        }
    }
}