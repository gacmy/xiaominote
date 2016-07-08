package com.example.administrator.xiaominote.view.richedittext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.LineBackgroundSpan;
import android.util.Log;

import com.example.administrator.xiaominote.utils.ActivityUtils;

import javax.security.auth.login.LoginException;

/**
 * Created by Administrator on 2016/3/17.
 */
public class TextSpan implements LineBackgroundSpan {
    private  int color ;
    private boolean isDrawline = false;
    public static String TAG = TextSpan.class.getName();
    public TextSpan(int color){
        this.color = color;

    }
    public TextSpan(){
        this.color = Color.RED;

    }

public void setIsDrawLine(boolean isDrawline){
    this.isDrawline = isDrawline;

}
  private void drawCenterLine(Paint p,CharSequence text,int start,int end,int top,int baseline,Canvas c){
      if(isDrawline){
          int oldColor = p.getColor();
          CharSequence curtext = text.subSequence(start,end);
          // Log.e(TAG,"curtext:"+curtext.toString());
          float rightwidth = getTextWidth(p,curtext.toString());
          // Log.e(TAG,"rightwidth:"+rightwidth);
          Rect rect = new Rect();
          p.getTextBounds(text.toString(),0,text.length(),rect);
          p.setColor(color);
          float centerY = (top+baseline)/2;
          c.drawLine(rect.left,centerY,rightwidth,centerY,p);
          p.setColor(oldColor);
      }else{

      }

  }
    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
        drawCenterLine(p,text,start,end,top,baseline,c);
    }
    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
}
