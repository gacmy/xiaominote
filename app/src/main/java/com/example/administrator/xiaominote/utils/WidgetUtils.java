package com.example.administrator.xiaominote.utils;

import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/6/27.
 */
public class WidgetUtils {
    public static void setEditEnable(boolean flag,EditText et){
        et.setEnabled(flag);
        et.setFocusable(flag);
       // et.setClickable(true);
    }
    public static void setTextViewEnd(String str,TextView tv){
        String text = null;
        if(str.length() > 20){
             tv.setText(str.substring(0, 20)+ "...");
        }else{
            tv.setText(str);
        }
    }
}
