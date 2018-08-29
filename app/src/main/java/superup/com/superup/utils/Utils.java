package superup.com.superup.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Random;

/**
 * Class with static Method for help in global stuff
 */
public class Utils {

    private static final Random random;

    static {
        random = new Random(System.currentTimeMillis());
    }


    /**
     * @param activity the activity you want check their screen orientation
     * @return true if is in portrait orientation, false if is in landscape orientation
     */
    public static boolean isPortraitOrientation(Activity activity) {
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }


    /**
     * this object good for sizes of screen and other
     */
    private static DisplayMetrics getDisplayMetrics(Activity activity) {
        return activity.getResources().getDisplayMetrics();
    }


    /**
     * @param px      the size in px you want convert to dp
     * @param context for get the resources
     * @return float size in dp from the px you insert
     */
    public static float convertPixelsToDp(float px, Context context) {
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
     * @param activity Where you want hide the status bar and action bar
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


    /**
     * @param min minimum number you want to get
     * @param max maximum number you want to get
     * @return number between {@param min} and {@param max} including
     * for example if you insert min - 0 , max - 3, you can get 0 or 1 or 2 or 3
     */
    public static int getRandomNumber(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * @see #vibratePhone(Context, long)
     * with def time 30 millisecond
     */
    public static void shortVibratePhone(Context context) {
        vibratePhone(context, 30);
    }

    /**
     * @see #vibratePhone(Context, long)
     * with def time 1500 millisecond
     */
    public static void longVibratePhone(Context context) {
        vibratePhone(context, 1500);
    }


    /**
     * Make device vibrate by
     *
     * @param duration in millisecond
     */
    private static void vibratePhone(Context context, long duration) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(duration);
            }
        }
    }

    /**
     * @param view the view you will want create from the bitmap
     * @return an bitmap created from the view
     * if there was any exception return null
     */
    private static Bitmap createBitmapFromView(@NonNull View view) {
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


    /**
     * @param view must be nonNull the view they take the pixel from
     * @param xPos in the view
     * @param yPos in the view
     * @return null if there was a IllegalStateException or other Exception
     * else return the integer pixel by xPos and yPos
     */
    public static Integer getPixelInScreen(@NonNull View view, int xPos, int yPos) {
        Bitmap bitmap = Utils.createBitmapFromView(view);
        if (bitmap == null) {
            return null;
        }
        try {
            return bitmap.getPixel(xPos, yPos);
        } catch (IllegalStateException e) {
            return null;
        }
    }


    /**
     * @param pixel the pixel they take the colors from
     * @return null if the pixel is null, else return int array of 4 index,
     * 0 - alpha, 1 - red, 2 - green, 3 - blue
     * @see #getPixelInScreen(View, int, int)
     */
    private static int[] getColorsFromPixel(Integer pixel) {
        if (pixel == null) {
            return null;
        }
        return new int[]{Color.alpha(pixel), Color.red(pixel), Color.green(pixel), Color.blue(pixel)};

    }


    /**
     * @param colors int array of 4 index, of argb colors
     * @return if array is not valid return null, else return string in hex
     * for example "#00ff00ff"
     * @see #getColorsFromPixel(Integer)
     */
    private static String getColorFromColorsComponent(int[] colors) {
        if (colors == null || colors.length != 4) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder("#");
        for (int color : colors) {
            stringBuilder.append(Integer.toHexString(color));
        }
        return stringBuilder.toString();
    }


    /**
     * A quick way to take color by view, xPos, and yPos
     *
     * @return like {@link #getColorFromColorsComponent(int[])}
     * @see #getPixelInScreen(View, int, int)
     * @see #getColorsFromPixel(Integer)
     * @see #getColorFromColorsComponent(int[])
     */
    public static String getColorFromScreen(View view, int xPos, int yPos) {
        Integer pixelInScreen = getPixelInScreen(view, xPos, yPos);
        int[] colorsFromPixel = getColorsFromPixel(pixelInScreen);
        return getColorFromColorsComponent(colorsFromPixel);
    }

    /**
     * @param pixel the pixel you need to parse
     * @return hex string from the pixel
     * @see #getColorsFromPixel(Integer)
     * @see #getColorFromColorsComponent(int[])
     */
    public static String getColorFromPixel(Integer pixel) {
        int[] colorsFromPixel = Utils.getColorsFromPixel(pixel);
        return Utils.getColorFromColorsComponent(colorsFromPixel);
    }


    /**
     * @param colorOne some color
     * @param colorTwo some color
     * @return true if equals, false if not
     */
    public static boolean isSameColor(int colorOne, int colorTwo) {
        return colorOne == colorTwo;
    }

}
