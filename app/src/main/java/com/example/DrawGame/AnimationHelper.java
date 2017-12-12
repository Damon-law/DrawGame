package com.example.DrawGame;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by lenovo on 2017/12/10.
 */

public class AnimationHelper {
    public static Animation createTranslateAnim(Context context, int fromX, int toX) {
        TranslateAnimation tlAnim = new TranslateAnimation(fromX, toX, 0, 0);
        long duration = (long) (Math.abs(toX - fromX) * 1.0f / BarrageTools.getScreenWidth(context) * 3000);
        tlAnim.setDuration(duration);
        tlAnim.setInterpolator(new DecelerateAccelerateInterpolator());
        tlAnim.setFillAfter(true);
        return tlAnim;
    }

}
