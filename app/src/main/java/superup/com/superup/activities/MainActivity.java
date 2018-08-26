package superup.com.superup.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, CircleView.CircleViewListener {


    private static final String TAG_SCORE = "TAG Score";
    private static final String TAG_MAIN_ACTIVITY = "TAG_MAIN_ACTIVITY";
    private static final int DELAY_TIME_CREATE_CIRCLE = 3000;
    private static final int DURATION_TIME_ANIMATION_CIRCLE = 2000;
    private RelativeLayout parentCircleRelativeLayout;
    private TextView resultTextView;
    private int score, lastScore;
    private boolean gameStarted;
    private float xPosLastTouch, yPosLastTouch, maxRadius;
    private boolean startGameClicked;
    private int colorCircles;
    private MediaPlayer mediaPlayer;

    private Handler handler;
    private Runnable runnableCreateCircles;
    private LottieAnimationView confettiAnimationLottie;

    public static Bitmap loadBitmapFromView(View view) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            view.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public void initGame() {
        score = 0;
        gameStarted = false;
        startGameClicked = false;
        lastScore = readLastScore();
        parentCircleRelativeLayout.removeAllViews();
        addMainCircle();
    }

    public void updateScore() {
        score++;
        resultTextView.setText((getString(R.string.score) + score));

    }

    private void userFail() {
        Utils.vibratePhone(getApplicationContext());
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
            }, 4500);
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

    private int readLastScore() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_MAIN_ACTIVITY, MODE_PRIVATE);
        return sharedPreferences.getInt(TAG_SCORE, 0);
    }


    private void saveLastScore(int score) {
        SharedPreferences.Editor editor = getSharedPreferences(TAG_MAIN_ACTIVITY, MODE_PRIVATE).edit();
        editor.putInt(TAG_SCORE, score);
        editor.apply();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utils.setFullScreen(this);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        colorCircles = Color.RED;
        initViews();
        initGame();
        addMainCircle();
        initListeners();
        initVar();
        if (lastScore == 0) {
            resultTextView.setText(("Click the button to begin\nAnd put your finger in the circle"));
        } else {
            resultTextView.setText(("Your last score is : " + lastScore));
        }
    }

    private void initVar() {
        handler = new Handler();
        runnableCreateCircles = new Runnable() {
            @Override
            public void run() {
                CircleView brokenCircleView = createBrokenCircle();
                brokenCircleView.startAnimation(maxRadius);
                handler.postDelayed(this, DELAY_TIME_CREATE_CIRCLE);
            }
        };
    }

    private void addMainCircle() {
        maxRadius = Utils.convertPixelsToDp(getMaxSizeRadius(), this);
        CircleView fullCircle = CircleView.createFullCircle(getApplicationContext(), colorCircles, maxRadius);
        fullCircle.setOnTouchListener(this);
        parentCircleRelativeLayout.addView(fullCircle);
    }

    private int getMaxSizeRadius() {
        int maxSize;
        if (Utils.isPortraitOrientation(this)) {
            maxSize = Utils.getWidthSizeScreen(this);
        } else {
            maxSize = Utils.getHeightSizeScreen(this);

        }
        return maxSize - 50;
    }

    private void initListeners() {
        parentCircleRelativeLayout.setOnTouchListener(this);
        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameClicked = true;
                startGame();
            }
        });

    }

    private void initViews() {
        resultTextView = findViewById(R.id.result_text_view);
        parentCircleRelativeLayout = findViewById(R.id.parent_circle_relative_layout);
        confettiAnimationLottie = findViewById(R.id.confetti_animation_lottie);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!startGameClicked) {
            return true;
        }
        xPosLastTouch = motionEvent.getX();
        yPosLastTouch = motionEvent.getY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                checkIfFingerInCircle(view);
                break;
            case MotionEvent.ACTION_UP:
                if (gameStarted) {
                    userFail();
                }
                break;
            default:
                if (!gameStarted) {
                    checkIfFingerInCircle(view);
                } else {
                    checkIfFingerOnCircle();
                }
        }
        return true;

    }

    private void checkIfFingerInCircle(View v) {
        if (!gameStarted) {
            try {
                CircleView v1 = (CircleView) v;
                gameStarted = v1.isContainsPoint(xPosLastTouch, yPosLastTouch);
                Log.e("Game start", "" + gameStarted);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkIfFingerOnCircle() {
        if (gameStarted) {
            Bitmap bitmap = loadBitmapFromView(parentCircleRelativeLayout);
            if (bitmap == null) {
                return;
            }
            try {
                int pxl = bitmap.getPixel(((int) xPosLastTouch), ((int) yPosLastTouch));
                int redComponent = Color.red(pxl);
                int greenComponent = Color.green(pxl);
                int blueComponent = Color.blue(pxl);
                if (!isFineColor(redComponent, greenComponent, blueComponent)) {
                    userFail();
                }
            } catch (IllegalArgumentException e) {
                Log.e("On touch event", "Touch in wrong area");

            }
        }

    }


    public void startGame() {
        if (startGameClicked) {
            startGameClicked = false;
            parentCircleRelativeLayout.removeAllViews();
            addMainCircle();
        }
        handler.post(runnableCreateCircles);
        startGameClicked = true;
    }


    private CircleView createBrokenCircle() {
        int randomNumberHole = Utils.getRandomNumber(1, 3);
        CircleView brokenCircleView = CircleView.createBrokenCircle(getApplicationContext(), colorCircles, randomNumberHole);
        brokenCircleView.setListener(MainActivity.this);
        brokenCircleView.setOnTouchListener(this);
        parentCircleRelativeLayout.addView(brokenCircleView);
        return brokenCircleView;
    }

    @Override
    public void overRadius(CircleView circleView) {
        circleView.stopAnimation();
        ViewGroup parent = (ViewGroup) circleView.getParent();
        if (parent != null) {
            parent.removeView(circleView);
        }
        if (gameStarted) {
            updateScore();
        }
    }

    public boolean isFineColor(int redComponent, int greenComponent, int blueComponent) {
        return redComponent != 255 && greenComponent == 0 && blueComponent == 0;
    }

    @Override
    public void updateRadius() {
        checkIfFingerOnCircle();

    }
}
