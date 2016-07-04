package com.example.administrator.xiaominote.utils.BitmapUtil;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by gac on 2016/7/4.
 */
public class RecyleBitmapDrawable extends BitmapDrawable {
    private int displayResCount = 0;
    private boolean mHasBeenDisplayed;
    public  RecyleBitmapDrawable(Resources res,Bitmap bitmap){
        super(res,bitmap);
    }

    public void setIsDisplayed(boolean isDisplay){
        synchronized (this){
            if(isDisplay){
                mHasBeenDisplayed = true;
                displayResCount ++;
            }else{
                displayResCount --;
            }
        }
        checkState();
    }
    //check bitmap state if memory is recycled
    private synchronized void checkState(){
        if(displayResCount <= 0 && mHasBeenDisplayed
                && hasValidBitmap()){
            getBitmap().recycle();
        }
    }

    //if bitmap is null and call recyle method
    private synchronized boolean hasValidBitmap(){
        Bitmap bitmap = getBitmap();
        return bitmap != null && !bitmap.isRecycled();
    }

}
