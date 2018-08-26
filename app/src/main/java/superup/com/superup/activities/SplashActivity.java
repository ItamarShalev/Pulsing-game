package superup.com.superup.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

import superup.com.superup.R;
import superup.com.superup.utils.Utils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utils.setFullScreen(this);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LottieAnimationView splashAnimation =  findViewById(R.id.splash_animation);
                splashAnimation.setVisibility(View.INVISIBLE);
                splashAnimation.cancelAnimation();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        },2200);


    }
}
