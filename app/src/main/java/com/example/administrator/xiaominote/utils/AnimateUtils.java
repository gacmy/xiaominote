package com.example.administrator.xiaominote.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by gac on 2016/6/27.
 */
public class AnimateUtils {
    public static void translationY(final View view,float start,float end){
        //start 当前控件开始处的偏移量  向上负数 向下是正数
        //end 当前控件 动作结束后停留的位置 也是该控件最开始处的偏移量
            ObjectAnimator obj = ObjectAnimator//
                    .ofFloat(view, "translationY", start, end)//
                    .setDuration(500);//

                    obj.start();
    }
    public static void rotationX(View view,float startDegree,float endDegree){
       ObjectAnimator objectAnimator =  ObjectAnimator//
                .ofFloat(view, "rotationX", startDegree, endDegree)//
                .setDuration(500);//

                objectAnimator.start();
        ObjectAnimator o;
    }
}
