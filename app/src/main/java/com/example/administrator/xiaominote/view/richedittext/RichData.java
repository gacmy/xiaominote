package com.example.administrator.xiaominote.view.richedittext;
import android.view.View;
import android.widget.LinearLayout;

import com.example.administrator.xiaominote.view.checkbox.SmoothCheckBox;
/**
 * Created by gac on 2016/7/5.
 */
public class RichData {
    public DeletableEditText delet;
    public SmoothCheckBox checkBox;
    public boolean isVisibleCheck;
    public boolean isChecked;
    public int viewindex = -1;
    public View view;//保存布局文件的视图 为了得到视图的位置
    public int getViewIndex(LinearLayout layout){
        return  layout.indexOfChild(view);
    }
}
