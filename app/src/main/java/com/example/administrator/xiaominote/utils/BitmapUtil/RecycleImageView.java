package com.example.administrator.xiaominote.utils.BitmapUtil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * //low android 2.3
 * Created by Administrator on 2016/7/4.
 *   how use the class
 *   ImageView imageView = new RecycleImageView(context);
 *   imageView.setImageDrawable(new RecycleBitmapDrawable(context.getResource(), bitmap));
 */
public class RecycleImageView extends ImageView {
    public RecycleImageView(Context context) {
        super(context);
    }

    public RecycleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        Drawable previousDrawable = getDrawable();
        super.setImageDrawable(drawable);
        //show new drawable
        notifyDrawable(drawable,true);
        //recycle previous drawable
        notifyDrawable(previousDrawable, false);
    }

    @Override
    protected void onDetachedFromWindow() {
        //view is dectached from window ,clear drawable
        setImageDrawable(null);
        super.onDetachedFromWindow();
    }
    public static void notifyDrawable(Drawable drawable,boolean isDisplayed){
        if(drawable instanceof RecyleBitmapDrawable){
            ((RecyleBitmapDrawable)drawable).setIsDisplayed(isDisplayed);
        }else if(drawable instanceof LayerDrawable){
            LayerDrawable layerDrawable = (LayerDrawable)drawable;
            for(int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++){
                notifyDrawable(layerDrawable.getDrawable(i),isDisplayed);
            }
        }
    }
}


















