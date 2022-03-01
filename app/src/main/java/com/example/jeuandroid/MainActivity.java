package com.example.jeuandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    float xStart = 0;
    float yStart = 0;

    float xCoeff = 0;
    float yCoeff = 0;

    float xPadOffset = 0;
    float yPadOffset = 0;
    float padOffsetFactor = 100;

    View mainLayout;
    ImageView padInt;
    ImageView padExt;
    ImageView tieImage;
    Tie tie;

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

        tie = new Tie(tieImage);

        //Layout listener pour placer le joystick
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            positionPadExt();
            positionPadInt();
            tie.setMaxScreenXY(mainLayout.getWidth(), mainLayout.getHeight());
            tie.setImageWH();
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


}