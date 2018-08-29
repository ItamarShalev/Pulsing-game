package superup.com.superup.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

import superup.com.superup.R;
import superup.com.superup.utils.Utils;

/**
 * This Activity show first when you open the application before {@link GameActivity} will open
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * duration time the animation will run until {@link GameActivity} will open
     */
    private static final int DURATION_TIME_ANIMATION = 2200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utils.setFullScreen(this);
        setContentView(R.layout.activity_splash);
        makeOpenAnimation();
    }


    /**
     * Make animation before {@link GameActivity} will open by {@value DURATION_TIME_ANIMATION} time
     */
    private void makeOpenAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LottieAnimationView splashAnimation =  findViewById(R.id.splash_animation);
                splashAnimation.setVisibility(View.INVISIBLE);
                splashAnimation.cancelAnimation();
                startActivity(new Intent(getApplicationContext(),GameActivity.class));
            }
        },DURATION_TIME_ANIMATION);
    }
}
