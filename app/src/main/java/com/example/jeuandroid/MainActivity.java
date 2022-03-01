package com.example.jeuandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

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
    ImageView asteroidImage;
    Tie tie;
    Asteroid asteroid;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Objets du layout
        mainLayout = findViewById(R.id.mainLayout);
        padInt = (ImageView) findViewById(R.id.padInt);
        padExt = (ImageView) findViewById(R.id.padExt);
        tieImage = (ImageView) findViewById(R.id.tie);
        asteroidImage = (ImageView) findViewById(R.id.asteroid);

        tie = new Tie(tieImage);
        asteroid = new Asteroid(asteroidImage);
        asteroid.setPattern(Pattern.getSerpent());
        asteroid.setFrequency(16000);
        asteroid.animate();

        //Layout listener pour placer le joystick
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            positionPadExt();
            positionPadInt();
            tie.setMaxScreenXY(mainLayout.getWidth(), mainLayout.getHeight());
            tie.setImageWH();
        });

        //Touch listener sur le vaisseau pour permettre le changement joystick / accelerometre
        tieImage.setOnTouchListener((v, event) -> {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if (useAccelerometer) usePad();
                    else useAccelerometer();
                    break;
                default:
                    return false;
            }
            return true;
        });

        //Touch listener pour gérer le joystick
        padInt.setOnTouchListener((v, event) -> {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
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
}