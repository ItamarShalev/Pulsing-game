package superup.com.superup.widget;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import superup.com.superup.data.CircleData;
import superup.com.superup.utils.AnimatorListenerEnding;
import superup.com.superup.utils.Utils;

/**
 * This view give 2 option for drawing,
 * 1 - drawing a whole circle, with size by radius and color stroke personal preference {@link #createFullCircle(Context, int, float)}
 * 2 - drawing a broken circle(You could call that too circle with hole - missed line),
 * color stroke and count broken line personal preference {@link #createCircleWithHole(Context, int, int)}
 * And built-in animation scale animation  {@link #startScaleAnimation(int, float, CircleAnimationListener)} {@link #stopScaleAnimation()}
 */
@SuppressLint("ViewConstructor")
public class CircleView extends ViewGroup {

    private static final int MAX_ANGELS = 360;

    //private static final int COUNT_SIDE_ANGEL = 4;

    //private static final int SIZE_SIDE_ANGEL = MAX_ANGELS / COUNT_SIDE_ANGEL;

    private static final int MINIMUM_HOLE = 25;

    private static final int MAXIMUM_HOLE = 35;

    private static final int MINIMUM_PIECE = 50;

    private boolean isBrokenCircle, isCancelAnimation, isPrinted;
    private int colorStroke, countHoleLine, sizeStrokeWidth;
    private float radius;

    private CircleAnimationListener listener;
    private Canvas canvas;
    private Paint paint;
    private RectF rect;
    private ValueAnimator animator;
    private List<CircleData> circleDataList;


    /**
     * @param isBrokenCircle if you want circle with hole the input need to be true, else if you want a whole circle so false
     * @param colorStroke    the color of the stroke on the circle
     * @param countHoleLine  if you want circle with hole so some holes you want (min value : 0, max value 3)
     * @param radius         the radius of circle you want
     * @see #CircleView(Context, boolean, int, int, float)
     */
    public CircleView(Context context, boolean isBrokenCircle, int colorStroke, int countHoleLine, float radius) {
        super(context);
        this.isBrokenCircle = isBrokenCircle;
        this.colorStroke = colorStroke;
        this.countHoleLine = countHoleLine;
        this.radius = radius;
        this.sizeStrokeWidth = 11;
        if (countHoleLine > 0) {
            this.circleDataList = getPositionLine(countHoleLine);
        }
        setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * @param colorStroke   the color of the stroke on the circle
     * @param countHoleLine if you want circle with hole so some holes you want (min value : 0, max value 3)
     * @return CircleData with Hole (Missed line)
     * @see #CircleView(Context, boolean, int, int, float)
     */
    public static CircleView createCircleWithHole(Context context, int colorStroke, int countHoleLine) {
        return new CircleView(context, true, colorStroke, countHoleLine, 0f);
    }

    /**
     * @param colorStroke the color of the stroke on the circle
     * @param radius      the radius of circle you want
     * @return A whole CircleView
     */
    public static CircleView createFullCircle(Context context, int colorStroke, float radius) {
        return new CircleView(context, false, colorStroke, 0, radius);
    }

    /**
     * @param countHoleLine count of holes draw on the circle
     * @return List of pair array , all pair array can be hold 2,
     * index 0 (NonNull) is where need to draw line first is where need to start drawing, second is where need to stop drawing the line
     * index 1 (Nullable) for where do not draw the line,  first is where the hole is start, second is where the hole stop
     */
    private List<CircleData> getPositionLine(int countHoleLine) {
        countHoleLine = 2;
        List<CircleData> circleDataList = new ArrayList<>();
        int holeLength;
        int sweepAngle = 0;
        int startAngel;
        if (countHoleLine == 1) {
            startAngel  = Utils.getRandomNumber(0, MAX_ANGELS);
        }else{
            startAngel = 0;
        }

        int[] holes = new int[countHoleLine];
        int totalSizeHoles = 0;

        for (int i = 0; i < holes.length; i++) {
            holes[i] = Utils.getRandomNumber(MINIMUM_HOLE, MAXIMUM_HOLE);
            totalSizeHoles += holes[i];
        }

        int[] lines = Utils.divideUnevenly(MINIMUM_PIECE, countHoleLine, MAX_ANGELS - totalSizeHoles);
        int firstStartAngel = startAngel;

        for (int i = 0; i < countHoleLine; i++) {
            sweepAngle = lines[i];
/*
            removeLeftHole = totalSizeHoles + (countHoleLine - i) * MINIMUM_PIECE;
            max = maxCircle - removeLeftHole;
            sweepAngle = Utils.getRandomNumber(startAngel, max);*/
            circleDataList.add(new CircleData(startAngel, sweepAngle));

            holeLength = holes[i];
            startAngel = (sweepAngle + holeLength);
        }

        //circleDataList.add(new CircleData(startAngel + sweepAngle,360 - firstStartAngel));

        return circleDataList;
    }


    /**
     * Drawing circle with hole (missing row)
     */
    private void drawBrokenCircle() {
      /*  if (circleDataList == null) {
            circleDataList = getPositionLine(countHoleLine);
        }*/
        for (CircleData circleData : circleDataList) {
            canvas.drawArc(rect, circleData.getStartAngle(), circleData.getSweepAngle(), false, paint);

        }

    }

    /**
     * @return paint for Draw circle
     */
    private Paint createPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(sizeStrokeWidth);
        paint.setAntiAlias(true);
        paint.setColor(colorStroke);
        return paint;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        paint = createPaint();
        rect = createRectF(getWidth(), getHeight());
        if (!isBrokenCircle) {
            drawFullCircle();
        } else {
            synchronized (this) {
                drawBrokenCircle();
            }
        }
    }

    /**
     * Drawing a whole circle
     */
    private void drawFullCircle() {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
    }

    /**
     * @param x position of xPos on Screen
     * @param y position of yPos on Screen
     * @return true if xPos and yPos is inside a circle, false if it is outside the circle
     */
    public boolean isContainsPoint(float x, float y) {
        return rect.contains(x, y);
    }


    /**
     * @param width  of view
     * @param height of view
     * @return RectF for draw a circle
     */
    private RectF createRectF(int width, int height) {
        float center_x = width / 2;
        float center_y = height / 2;

        float left = center_x - radius;
        float top = center_y - radius;
        float right = center_x + radius;
        float bottom = center_y + radius;

        return new RectF(left, top, right, bottom);
    }


    /**
     * Make scale animation on this circle and call to listener.onOverRadius with this circle when it reached the maximum radius
     *
     * @param duration                how long the animation will run from {@link #radius} to maxRadius which you get as a parameter
     * @param maxRadius               max radius this circle can be
     * @param circleAnimationListener see the listener interface  {@link CircleAnimationListener}
     */
    public void startScaleAnimation(int duration, float maxRadius, CircleAnimationListener circleAnimationListener) {
        listener = circleAnimationListener;
        animator = ValueAnimator.ofFloat(radius, maxRadius);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (float) animation.getAnimatedValue();
                invalidate();
                if (listener != null) {
                    listener.onUpdateRadius();
                }
            }
        });
        animator.addListener(new AnimatorListenerEnding() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null && !isCancelAnimation) {
                    listener.onOverRadius(CircleView.this);
                }
            }

        });
        animator.start();
    }


    /**
     * Stop the scale animation
     */
    public void stopScaleAnimation() {
        isCancelAnimation = true;
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    /**
     * Listener when you use scale animation and want to ger result
     */
    public interface CircleAnimationListener {

        /**
         * Call when the circle reached the maximum radius in scale animation
         */
        void onOverRadius(CircleView circleView);

        /**
         * Call when circle change radius
         */
        void onUpdateRadius();
    }
}