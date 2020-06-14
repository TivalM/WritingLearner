package com.example.writinglearner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.github.gcacace.signaturepad.utils.Bezier;
import com.github.gcacace.signaturepad.utils.ControlTimedPoints;
import com.github.gcacace.signaturepad.utils.TimedPoint;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.util.ArrayList;
import java.util.List;

public class MyWritingPad extends SignaturePad{
    private List<List<PointF>> paths = new ArrayList<>();
    private List<PointF> path = new ArrayList<>();
    private int history_total_num = 0;

    public MyWritingPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMaxWidth(50);
        setMinWidth(1);
        setVelocityFilterWeight(0.99f);
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        PointF start_Point = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path = new ArrayList<>();
                path.add(start_Point);
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d("wrt2", "history num: " + Integer.toString(event.getHistorySize()));
                int historySize = event.getHistorySize();
                Log.d("wrt2", "ADD" + Integer.toString(historySize));
                for (int i = 0; i < historySize; i++, history_total_num++) {
                    if (history_total_num % 3 == 0 && history_total_num != 0) {
                        PointF point = new PointF(event.getHistoricalX(i),
                                event.getHistoricalY(i));
                        path.add(point);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("wrt2", "UP");
                history_total_num = 0;
                PointF end_Point = new PointF(event.getX(), event.getY());
                if (!start_Point.equals(end_Point)) {
                    path.add(end_Point);
                }
                paths.add(path);

                Log.d("wrt2", "path num: " + Integer.toString(path.size()));
                Log.d("wrt2", "path: " + path.toString());
                Log.d("wrt2", "paths: " + paths.toString());
                break;
            default:
                return false;
        }

        return true;
    }

    public void clean_paths() {
        paths = new ArrayList<>();
        path = new ArrayList<>();
        history_total_num = 0;
    }

    public List<List<PointF>> getPaths() {
        return paths;
    }
}