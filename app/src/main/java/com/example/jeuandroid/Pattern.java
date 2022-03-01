package com.example.jeuandroid;

import android.graphics.Path;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import java.util.Random;

public class Pattern {

    static Path getSerpent(){
        float n= 400f;
        Random random = new Random();
        Path path = new Path();

        random.nextFloat();

        Path path2 = new Path();
        path2.arcTo(0f, -n, 800f, 0*n, -90f, -180f, true);
        path2.arcTo(0f, 0*n, 800f, n, -90f, 180f, true);

        path.addPath(path2);
        path.addPath(path2, 0,2*n);
        path.addPath(path2, 0,4*n);
        path.addPath(path2, 0,6*n);

        return path;
    }

    static Path getElipse(){
        Path path = new Path();
        path.arcTo(0f, 0, 800f, 800f, -90f, 359f, true);
        return path;
    }

}
