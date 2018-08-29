package superup.com.superup.widget;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import superup.com.superup.utils.AnimatorListenerEnding;
import superup.com.superup.utils.Utils;

/**
 * This view give 2 option for drawing,
 * 1 - drawing a whole circle, with size by radius and color stroke personal preference {@link #createFullCircle(Context, int, float)}
 * 2 - drawing a broken circle(You could call that too circle with hole - missed line),
 * color stroke and count broken line personal preference {@link #createCircleWithHole(Context, int, int)}
 * <p>
 * And built-in animation scale animation  {@link #startScaleAnimation(int, float, CircleAnimationListener)} {@link #stopScaleAnimation()}
 */
@SuppressLint("ViewConstructor")
public class CircleView extends View {

    private static final int MAX_ANGELS = 180;
    private static final int COUNT_SIDE_ANGEL = 6;

    private static final int SIZE_SIDE_ANGEL = MAX_ANGELS / COUNT_SIDE_ANGEL;

    private static final int MINIMUM_TOUCH = SIZE_SIDE_ANGEL;

    private static final int MAXIMUM_TOUCH = SIZE_SIDE_ANGEL;

    private boolean isBrokenCircle, isCancelAnimation, isPrinted;
    private int colorStroke, countHoleLine, sizeStrokeWidth;
    private float radius;

    private CircleAnimationListener listener;
    private List<Pair[]> pairOfPositionLine;
    private List<Integer> integersArea;
    private Canvas canvas;
    private Paint paint;
    private RectF rect;
    private ValueAnimator animator;


    /**
     * @param isBrokenCircle if you want circle with hole the input need to be true, else if you want a whole circle so false
     * @param colorStroke    the color of the stroke on the circle
     * @param countHoleLine  if you want circle with hole so some holes you want (min value : 0, max value {@value COUNT_SIDE_ANGEL})
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
        setBackgroundColor(Color.TRANSPARENT);
        integersArea = new ArrayList<>();
        for (int i = 1; i <= COUNT_SIDE_ANGEL; i++) {
            integersArea.add(i);
        }
        Collections.shuffle(integersArea);
    }

    /**
     * @param colorStroke   the color of the stroke on the circle
     * @param countHoleLine if you want circle with hole so some holes you want (min value : 0, max value {@value COUNT_SIDE_ANGEL})
     * @return Circle with Hole (Missed line)
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
    private List<Pair[]> getPositionLine(int countHoleLine) {

        Pair<Integer, Integer> locationLine;
        Pair<Integer, Integer> locationHole = null;

        Pair[] o = null;
        List<Pair[]> list = new ArrayList<>(Collections.nCopies(COUNT_SIDE_ANGEL, o));

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
            list.set(randomArea - 1, new Pair[]{locationLine, locationHole});
            locationHole = null;
        }
        return list;
    }


    /**
     * @return random value of area from a circle by {@value COUNT_SIDE_ANGEL}
     */
    private int getRandomArea() {
        int position = Utils.getRandomNumber(0, integersArea.size() - 1);
        return integersArea.remove(position);
    }


    /**
     * Drawing circle with hole (missing row)
     */
    private void drawBrokenCircle() {
        if (pairOfPositionLine == null) {
            pairOfPositionLine = getPositionLine(countHoleLine);
        }

        for (Pair[] integerIntegerPair : pairOfPositionLine) {
            Pair<Integer, Integer> locationLine = integerIntegerPair[0];
            Pair<Integer, Integer> locationHole = integerIntegerPair.length == 2 ? integerIntegerPair[1] : null;

            if (locationHole == null) {
                canvas.drawArc(rect, locationLine.first, locationLine.second, false, paint);
            } else {
                if (!isPrinted) {
                    Log.e("Number Hole", " " + countHoleLine);
                    Log.e("First Line", "from : " + locationLine.first + " To : " + locationHole.first);
                    Log.e("Second Line", "from : " + locationHole.second + " To : " + locationLine.second);
                }

                if (locationHole.first.equals(locationLine.first)) {
                    canvas.drawArc(rect, locationHole.second, locationLine.second, false, paint);
                } else if (locationHole.second.equals(locationLine.second)) {
                    canvas.drawArc(rect, locationLine.first, locationHole.first, false, paint);
                } else {
                    canvas.drawArc(rect, locationLine.first, locationHole.first, false, paint);
                    canvas.drawArc(rect, locationHole.second, locationLine.second, false, paint);
                }

            }
        }
        isPrinted = true;
    }

    /**
     * @return paint for Draw circle
     */
    private Paint getPaint() {
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
        paint = getPaint();
        rect = getRect(getWidth(), getHeight());
        if (!isBrokenCircle) {
            drawFullCircle();
        } else {
            drawBrokenCircle();
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
    private RectF getRect(int width, int height) {
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