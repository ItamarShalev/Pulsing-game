package superup.com.superup.widget;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import superup.com.superup.utils.Utils;

@SuppressLint("ViewConstructor")
public class CircleView extends View {


    private static final int MAX_ANGELS = 180;
    private static final int COUNT_SIDE_ANGEL = 4;
    private static final int SIZE_SIDE_ANGEL = MAX_ANGELS / COUNT_SIDE_ANGEL;
    private static final int MINIMUM_TOUCH = 20;
    private static final int MAXIMUM_TOUCH = 30;
    private float radius;
    private boolean isBrokenCircle;
    private int colorStroke;
    private int countHoleLine;
    private float maxRadius;
    private int sizeStrokeWidth;
    private CircleViewListener listener;
    private ValueAnimator translate;
    private boolean animationWork;
    private List<Pair[]> pairOfPositionLine;
    private Handler handler;
    private List<Integer> integersArea;
    private Canvas canvas;
    private Paint paint;
    private RectF rect;


    public CircleView(Context context, boolean isBrokenCircle, int colorStroke, int countHoleLine, float radius) {
        super(context);
        this.isBrokenCircle = isBrokenCircle;
        this.colorStroke = colorStroke;
        this.countHoleLine = countHoleLine;
        this.radius = radius;
        this.sizeStrokeWidth = 11;
        setBackgroundColor(Color.TRANSPARENT);
        handler = new Handler(Looper.myLooper());
        integersArea = new ArrayList<>();
        for (int i = 1; i <= COUNT_SIDE_ANGEL; i++) {
            integersArea.add(i);
        }
    }

    public static CircleView createBrokenCircle(Context context, int colorStroke, int countHoleLine) {
        return new CircleView(context, true, colorStroke, countHoleLine, 0f);
    }

    public static CircleView createFullCircle(Context context, int colorStroke, float radius) {
        return new CircleView(context, false, colorStroke, 0, radius);
    }

    public void stopAnimation() {
        animationWork = true;
        if (translate != null) {
            translate.end();

            translate.cancel();
            translate = null;
        }
    }


    private Paint getPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(sizeStrokeWidth);
        paint.setAntiAlias(true);
        paint.setColor(colorStroke);
        return paint;
    }


    private void drawBrokenCircle() {
        if (pairOfPositionLine == null) {
            pairOfPositionLine = getPairOfPositionLine(countHoleLine);
        }
        for (Pair[] integerIntegerPair : pairOfPositionLine) {
            Pair<Integer, Integer> locationLine = integerIntegerPair[0];
            Pair<Integer, Integer> locationHole = integerIntegerPair.length == 2 ? integerIntegerPair[1] : null;

            if (locationHole == null) {
                canvas.drawArc(rect, locationLine.first, locationLine.second, false, paint);
            } else {
                Log.e("Number Hole"," " + countHoleLine);
                Log.e("First Line", "from : " + locationLine.first + " To : " +  locationHole.first);
                Log.e("Second Line", "from : " + locationHole.second + " To : " +  locationLine.second);
                canvas.drawArc(rect, locationLine.first, locationHole.first, false, paint);
                canvas.drawArc(rect, locationHole.second, locationLine.second, false, paint);
            }
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        paint = getPaint();
        rect = getRect(getWidth(), getHeight());

        if (!isBrokenCircle) {
            canvas.drawArc(rect, 0, 360, false, paint);
        } else {
            drawBrokenCircle();
        }

    }

    public boolean isContainsPoint(float x, float y){
        return rect.contains(x,y);
    }

    private RectF getRect(int width, int height) {
        float center_x = width / 2;
        float center_y = height / 2;

        float left = center_x - radius;
        float top = center_y - radius;
        float right = center_x + radius;
        float bottom = center_y + radius;

        return new RectF(left, top, right, bottom);
    }

    public void startAnimation(float maxRadius) {
        this.maxRadius = maxRadius;
        handler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                if (animationWork) {
                    handler.removeCallbacks(this);
                    return;
                }
                radius += 3;
                invalidate();
                listener.updateRadius();
                if (radius >= CircleView.this.maxRadius) {
                    listener.overRadius(CircleView.this);
                }
                handler.postDelayed(this, 2);

            }
        });
    }


    private List<Pair[]> getPairOfPositionLine(int countHoleLine) {
        List<Pair[]> list = new ArrayList<>();

        Pair<Integer, Integer> locationLine = null;
        Pair<Integer, Integer> locationHole = null;


        int time = 0;

        while (integersArea.size() > 0) {
            int randomArea = getRandomArea();


            int startLine = randomArea != 1 ? (randomArea - 1) * SIZE_SIDE_ANGEL : 0;
            int stopLine = (randomArea) * SIZE_SIDE_ANGEL;

            locationLine = new Pair<>(startLine, stopLine);

            if (countHoleLine > 0) {
                countHoleLine -= 1;
                int touchSize = Utils.getRandomNumber(MINIMUM_TOUCH, MAXIMUM_TOUCH);
                int startHole = Utils.getRandomNumber(startLine, stopLine - touchSize);
                int stopHole = startHole + touchSize;

                locationHole = new Pair<>(startHole, stopHole);
            }
            list.add(new Pair[]{locationLine, locationHole});
            locationHole = null;
        }
        return list;
    }


    private int getRandomArea() {
        int position = Utils.getRandomNumber(0, integersArea.size() - 1);
        return integersArea.remove(position);
    }

    public float getRadius() {
        return radius;
    }

    public void setListener(CircleViewListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return false;
    }

    public interface CircleViewListener {
        void overRadius(CircleView circleView);

        void updateRadius();
    }

}