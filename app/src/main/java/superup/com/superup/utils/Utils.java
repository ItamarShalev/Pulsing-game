package superup.com.superup.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.util.Random;

public class Utils {

    private static final Random random;

    static {
        random = new Random(System.currentTimeMillis());
    }



    /**
     * @return true if is in portrait orientation, false if is in landscape orientation
     */
    public static boolean isPortraitOrientation(Activity activity) {
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * this object good for sizes of screen and other
     */
    private static DisplayMetrics getDisplayMetrics(Activity activity) {
        return activity.getResources().getDisplayMetrics();
    }
    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }



    /**
     * @return Height int of screen phone in pixels
     */
    public static int getHeightSizeScreen(Activity activity) {
        return getDisplayMetrics(activity).heightPixels;
    }

    /**
     * @return Width int of screen phone in pixels
     */
    public static int getWidthSizeScreen(Activity activity) {
        return getDisplayMetrics(activity).widthPixels;
    }

    /**
     * @param activity Where you want clear the status bar and action bar
     */
    public static void setFullScreen(AppCompatActivity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar supportActionBar = activity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        } else {
            android.app.ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    public static int getWidthSizeScreen(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getRandomNumber(int min, int max){
        return random.nextInt((max - min) + 1) + min;
    }

    public static void vibratePhone(Context context){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(500);
        }
    }



    public static class Calculations{
        public static boolean isInsideCircle(float radiusCircle, float xPosCircle, float xPosPoint, float yPosPoint){
        return true;
        }
    }
}
