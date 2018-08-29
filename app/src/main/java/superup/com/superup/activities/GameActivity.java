package superup.com.superup.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import superup.com.superup.R;
import superup.com.superup.utils.Utils;
import superup.com.superup.widget.CircleView;

/**
 * This activity is the main of the application and open after {@link SplashActivity} open,
 * inside this activity the game start, the game call "Pulsing game"
 * <p>
 * In-depth explanation :
 * 2d game of pulsing circles Each second a circle is generated from the center of the
 * screens with holes and it is growing user must move is finger to holes in order to pass
 * each circle that passed out from the screen granting one point
 */
@SuppressLint("ClickableViewAccessibility")
public class GameActivity extends AppCompatActivity implements View.OnTouchListener, CircleView.CircleAnimationListener {


    private static final String TAG_SCORE = "TAG Score";
    private static final String TAG_MAIN_ACTIVITY = "TAG_MAIN_ACTIVITY";
    private static final int DELAY_TIME_CREATE_CIRCLE = 1000;
    private static final int DURATION_TIME_ANIMATION_CIRCLE = 2000;
    private static final int COLOR_CIRCLES = Color.RED;
    private static final int TIME_ANIMATION_SUCCESS_SCORE = 4500;
    private static final int MINIMUM_HOLE_IN_CIRCLE = 1;
    private static final int MAXIMUM_HOLE_IN_CIRCLE = 3;

    private int score, lastScore;
    private float xPosLastTouch, yPosLastTouch, maxRadius;
    private boolean isClickedToStart, isGameStarted, isAnimationStarted;

    private RelativeLayout parentCircleRelativeLayout;
    private TextView resultTextView;
    private CircleView fullCircle;

    private MediaPlayer mediaPlayer;

    private Handler handler;
    private Runnable runnableCreateCircles;
    //private CheckFingerThread checkFingerThread;
    private LottieAnimationView confettiAnimationLottie;
    private int updateTime;


    /**
     * Make the activity full screen without action bar or status bar, and override animation enter and exit,
     * Initializing the activity , initializing the view, game, add the main circle, listeners and variables (In the order)
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utils.setFullScreen(this);
        setContentView(R.layout.activity_game);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        initViews();
        initGame();
        addMainCircle();
        initListeners();
        initVar();

    }

    /**
     * Initializing the variables, handler and runnable,
     * the runnable make broken circle (circle with hole) all {@value DELAY_TIME_CREATE_CIRCLE} time (in millisecond)
     * and start animation scale on the circle created
     */
    private void initVar() {
        handler = new Handler();
        runnableCreateCircles = new Runnable() {
            @Override
            public void run() {
                CircleView brokenCircleView = createBrokenCircle();
                brokenCircleView.startScaleAnimation(DURATION_TIME_ANIMATION_CIRCLE, maxRadius, GameActivity.this);
                handler.postDelayed(this, DELAY_TIME_CREATE_CIRCLE);
            }
        };
    }

    /**
     * Adding the main circle with the max radius, listener to touch and add to {@link #parentCircleRelativeLayout}
     */
    private void addMainCircle() {
        maxRadius = Utils.convertPixelsToDp(getMaxSizeRadius(), this);
        fullCircle = CircleView.createFullCircle(getApplicationContext(), COLOR_CIRCLES, maxRadius);
        fullCircle.setOnTouchListener(this);
        parentCircleRelativeLayout.addView(fullCircle);
    }

    /**
     * @return the max radius can be in the device by the height or width depending on orientation less 50px
     */
    private int getMaxSizeRadius() {
        int maxSize;
        if (Utils.isPortraitOrientation(this)) {
            maxSize = Utils.getWidthSizeScreen(this);
        } else {
            maxSize = Utils.getHeightSizeScreen(this);

        }
        return maxSize - 50;
    }

    /**
     * Listen to touch on {@link #parentCircleRelativeLayout}
     */
    private void initListeners() {
        parentCircleRelativeLayout.setOnTouchListener(this);
        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClickedToStart = true;
                startGame();
            }
        });

    }

    /**
     * Initializing the view in the activity
     */
    private void initViews() {
        resultTextView = findViewById(R.id.result_text_view);
        parentCircleRelativeLayout = findViewById(R.id.parent_circle_relative_layout);
        confettiAnimationLottie = findViewById(R.id.confetti_animation_lottie);
    }


    /**
     * Initializing the game from the start, and stop the animation if still running
     */
    public void initGame() {
        score = 0;
        isGameStarted = false;
        isClickedToStart = false;
        lastScore = readLastScore();
        if (isAnimationStarted) {
            stopAnimation();
        }
        if (lastScore == 0) {
            resultTextView.setText(("Click the button to begin\nAnd put your finger in the circle"));
        } else {
            resultTextView.setText(("Your last score is : " + lastScore));
        }
    }

    /**
     * Upgrade in one the {@link #score} and update the UI
     */
    public void upgradeScore() {
        score++;
        resultTextView.setText((getString(R.string.score) + score));

    }


    /**
     * User fail, make vibrate, init the game, update the UI
     * if the score is bigger from the last so save and show positive animation and sound
     * else make sound negative
     */
    private void userFail() {
        Utils.longVibratePhone(getApplicationContext());
        handler.removeCallbacks(runnableCreateCircles);
        if (score > lastScore) {
            confettiAnimationLottie.setVisibility(View.VISIBLE);
            confettiAnimationLottie.playAnimation();
            mediaPlayer = MediaPlayer.create(this, R.raw.victory_sound);
            mediaPlayer.start();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.stop();
                    confettiAnimationLottie.setVisibility(View.INVISIBLE);
                    confettiAnimationLottie.cancelAnimation();
                }
            }, TIME_ANIMATION_SUCCESS_SCORE);
            resultTextView.setText(("Well done, you broke the score\n" +
                    "Previous score: " + lastScore + "\n" +
                    "Current score :" + score));
            saveLastScore(score);

        } else {
            mediaPlayer = MediaPlayer.create(this, R.raw.fail_sound);
            mediaPlayer.start();
            resultTextView.setText(("You lose" + "\n" + "Total score : " + score));
        }
        initGame();

    }

    /**
     * @return the last score we have, if no have, so return zero
     */
    private int readLastScore() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_MAIN_ACTIVITY, MODE_PRIVATE);
        return sharedPreferences.getInt(TAG_SCORE, 0);
    }


    /**
     * @param score the score will be save
     *              save the score by tag {@value TAG_SCORE}
     */
    private void saveLastScore(int score) {
        SharedPreferences.Editor editor = getSharedPreferences(TAG_MAIN_ACTIVITY, MODE_PRIVATE).edit();
        editor.putInt(TAG_SCORE, score);
        editor.apply();
    }


    /**
     * @param view        touched
     * @param motionEvent all data about the touch
     * @return false, no need other listeners
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!isClickedToStart) {
            return false;
        }
/*        if (motionEvent.getPointerCount() > 1) {
            resultTextView.setText(R.string.message_error_more_then_one_finger);
            stopAnimation();
            return false;
        }*/
        xPosLastTouch = motionEvent.getRawX();
        yPosLastTouch = motionEvent.getRawY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                checkIfFingerInCircle(view);
                break;
            case MotionEvent.ACTION_UP:
                if (isGameStarted) {
                    userFail();
                }
                break;
            default:
                if (!isGameStarted) {
                    checkIfFingerInCircle(view);
                } else {
                    checkIfFingerOnCircle();
                }
        }
        return false;

    }


    /**
     * Check if finger inside the circle (the main circle) if is inside is mean the user put the finger in the circle
     * and the game can be start change {@link #isGameStarted} to the result
     */
    private void checkIfFingerInCircle(View view) {
        if (!isGameStarted && view instanceof CircleView) {
            CircleView mainCircleView = ((CircleView) view);
            isGameStarted = mainCircleView.isContainsPoint(xPosLastTouch, yPosLastTouch);
            if (isGameStarted) {
                fullCircle.setOnTouchListener(null);
            }
            Log.e("Game start", "" + isGameStarted);
        }

    }


    /**
     * Check if finger on the circle by the color from the pixel in the screen by {@link #xPosLastTouch} and {@link #yPosLastTouch}
     * and check if the color touched is same color on the circle
     * if is same color is mean user fail and call to {@link #userFail()}
     */
    private void checkIfFingerOnCircle() {
        if (isGameStarted) {
            Integer pxl = Utils.getPixelInScreen(parentCircleRelativeLayout, (int) xPosLastTouch, (int) yPosLastTouch);
            if (pxl == null) {
                return;
            }
            boolean isRed = Color.red(pxl) == 255 && Color.green(pxl) == 0 && Color.blue(pxl) == 0;
            if (isRed) {
                userFail();
            }
        }

    }


    /**
     * Check if animation running with {@link #isAnimationStarted} so stop the animation and
     * start the game start created broken circles all {@value DELAY_TIME_CREATE_CIRCLE} time
     */
    public void startGame() {
        if (isAnimationStarted) {
            stopAnimation();
        }
        handler.post(runnableCreateCircles);
        isAnimationStarted = true;
    }

    /**
     * Stop the animation and destroy all circles on the screen, make new main circle {@link #addMainCircle()}
     * and change {@link #isAnimationStarted} to false
     */
    private void stopAnimation() {
        parentCircleRelativeLayout.removeAllViews();
        handler.removeCallbacks(runnableCreateCircles);
        addMainCircle();
        isAnimationStarted = false;
    }


    /**
     * @return Broken Circle create random count hole listen to touch and add to {@link #parentCircleRelativeLayout}
     */
    private CircleView createBrokenCircle() {
        int randomNumberHole = Utils.getRandomNumber(MINIMUM_HOLE_IN_CIRCLE, MAXIMUM_HOLE_IN_CIRCLE);
        CircleView brokenCircleView = CircleView.createCircleWithHole(getApplicationContext(), COLOR_CIRCLES, randomNumberHole);
        brokenCircleView.setOnTouchListener(this);
        parentCircleRelativeLayout.addView(brokenCircleView);
        return brokenCircleView;
    }

    /**
     * implement the interface {@link superup.com.superup.widget.CircleView.CircleAnimationListener} from class {@link CircleView}
     *
     * @param circleView the radius get to {@link #maxRadius} Stop the scale animation and remove the circle from the screen,
     *                   after this call to {@link #upgradeScore()}if the game started
     * @see superup.com.superup.widget.CircleView.CircleAnimationListener#onOverRadius(CircleView)
     * Call when the circle reached the maximum radius in scale animation
     */
    @Override
    public void onOverRadius(CircleView circleView) {
        Utils.shortVibratePhone(getApplicationContext());
        circleView.stopScaleAnimation();
        ViewGroup parent = (ViewGroup) circleView.getParent();
        if (parent != null) {
            parent.removeView(circleView);
        }
        if (isGameStarted) {
            upgradeScore();
        }
    }


    /**
     * implement the interface {@link superup.com.superup.widget.CircleView.CircleAnimationListener} from class {@link CircleView}
     *
     * @see CircleView.CircleAnimationListener#onUpdateRadius()
     * Call when circle change radius
     * Check if the finger on circle {@link #checkIfFingerOnCircle()}
     */
    @Override
    public void onUpdateRadius() {
        updateTime++;
        Log.e("Itamar", "update time : " + updateTime);
        checkIfFingerOnCircle();
    }


    /**
     * @see #stopAnimation()
     * Finish and destroy all circle with animation witout the main circle
     */
    @Override
    protected void onStop() {
        super.onStop();
        stopAnimation();

    }

}