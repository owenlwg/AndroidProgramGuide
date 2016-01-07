package com.owen.tests;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mDisplayInfo;
    private StringBuilder mSb = new StringBuilder();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        displaySreenInfo(metrics);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getRealMetrics(metrics);
        displaySreenInfo(metrics);

        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;


        mDisplayInfo = (TextView) findViewById(R.id.display_info_textview);

        float widthInches = metrics.widthPixels / metrics.xdpi;
        float heightInches = metrics.heightPixels / metrics.ydpi;
        double diagonalInches = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));

        mDisplayInfo.setText(mSb + "statusBarHeight:" + statusBarHeight + "\n"
                                     + "titleBarHeight:" + titleBarHeight + "\n"
                                     + "diagonalInches:" + diagonalInches);

        Configuration config = getResources().getConfiguration();
        mDisplayInfo.setText(mSb + "statusBarHeight:" + statusBarHeight + "\n"
                                     + "titleBarHeight:" + titleBarHeight + "\n"
                                     + "diagonalInches:" + diagonalInches + "\n"
                                     + "smallestScreenWidthDp:" + config.smallestScreenWidthDp);
    }

    private void displaySreenInfo(DisplayMetrics metrics) {
//        mSb = new StringBuilder();
        mSb.append("metrics.densityDpi: " + metrics.densityDpi + "\n");
        mSb.append("metrics.density: " + metrics.density + "\n");
        mSb.append("metrics.widthPixels: " + metrics.widthPixels + "\n");
        mSb.append("metrics.heightPixels: " + metrics.heightPixels + "\n");
        mSb.append("metrics.scaledDensity: " + metrics.scaledDensity + "\n");
        double screenSize = Math.sqrt(Math.pow(metrics.widthPixels, 2) + Math.pow(metrics.heightPixels, 2))
                                    / metrics.densityDpi;
        mSb.append("屏幕尺寸: " + screenSize + "\n");
    }
}
