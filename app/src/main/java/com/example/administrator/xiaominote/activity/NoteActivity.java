package com.example.administrator.xiaominote.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.xiaominote.R;
import com.example.administrator.xiaominote.utils.BitmapUtils;
import com.example.administrator.xiaominote.view.richedittext.DeletableEditText;
import com.example.administrator.xiaominote.view.richedittext.RichTextEditor;
import com.example.administrator.xiaominote.view.roundedimageview.RoundedImageView;

import cn.trinea.android.common.service.impl.ImageCache;
import cn.trinea.android.common.util.CacheManager;

/**
 * Created by Administrator on 2016/6/30.
 */
public class NoteActivity extends BaseActivity {
    private Toolbar toolbar;
    private ImageView iv_menu;
    private RoundedImageView riv;
    private RichTextEditor richeditor;
    private ImageView iv_back;

    public static final ImageCache IMAGE_CACHE = CacheManager.getImageCache();
   // private DeletableEditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithToolbar(R.layout.activity_note);
    }

    @Override
    public void initData() {

    }

    @Override
    public Toolbar initToolBar() {
        toolbar = mGetView(R.id.toolbar_note);
        return toolbar;
    }

    @Override
    protected void initView() {
        iv_menu = mGetViewSetOnClick(R.id.iv_menu);
        iv_back = mGetViewSetOnClick(R.id.iv_back);
        riv = mGetView(R.id.iv_test);
        richeditor = mGetView(R.id.richet);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_menu:
                //BitmapUtils.gallery(this);
                richeditor.setCheckBoxIsVisible(true);
                break;
            case R.id.iv_back:
                richeditor.setCheckBoxIsVisible(false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = BitmapUtils.getGalleryUri(data, requestCode);
       //Bitmap bitmap = BitmapUtils.compressImg(uri, this);
      // if(bitmap!= null){
           richeditor.insertImage(uri.getPath());
        //}

    }
}
