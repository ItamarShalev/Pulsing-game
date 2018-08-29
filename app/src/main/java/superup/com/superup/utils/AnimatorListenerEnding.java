package superup.com.superup.utils;

import android.animation.Animator;

/**
 * This class is intended only to save the realization of functions not needed to have a shorter and more understandable code
 */
public abstract class AnimatorListenerEnding implements Animator.AnimatorListener {


    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public abstract void onAnimationEnd(Animator animation);

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
