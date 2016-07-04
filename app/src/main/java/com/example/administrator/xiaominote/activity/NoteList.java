package com.example.administrator.xiaominote.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.administrator.xiaominote.R;
import com.example.administrator.xiaominote.adapter.NoteAdapter;
import com.example.administrator.xiaominote.bean.NoteBean;
import com.example.administrator.xiaominote.listener.HidingScrollListener;
import com.example.administrator.xiaominote.listener.ItemClickSupport;
import com.example.administrator.xiaominote.utils.AnimateUtils;
import com.example.administrator.xiaominote.view.DividerItemDecoration;
import com.example.administrator.xiaominote.view.flatbutton.FButton;
import com.example.administrator.xiaominote.view.scrollablelayout.ScrollableLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gac on 2016/6/24.
 */
public class NoteList extends BaseActivity implements ItemClickSupport.OnItemLongClickListener{
    private RecyclerView rv_note;
    private List<NoteBean> list_note;
    private NoteAdapter mAdapter;
    public static String TAG = NoteList.class.getName();
    private RelativeLayout rl_search;
    private RelativeLayout rl_head;
    private FrameLayout framelayout_head;
    private RelativeLayout rl_headchange;
    private ScrollableLayout scrollableLayout;
    private EditText et_query;
    private ImageView iv_searchback;
    private TextView tv_showsearch;
    private ImageView iv_addnote;
    private RelativeLayout rl_bottommenu;
    private ImageView iv_delete;
    private RelativeLayout rl_bottom;
    private FButton fb_ok,fb_cance;
    private LinearLayoutManager linearLayoutManager;
    private TextView tv_checkCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewNoTitle(R.layout.note_list);
    }

    @Override
    public void initData() {
        list_note = new ArrayList<>();
        NoteBean bean1 = new NoteBean("1111");
        bean1.setCount("共1条");
        bean1.setIsDirectory(false);
        bean1.setDate("09:33");
        bean1.setIsCheck(false);
        NoteBean bean2 = new NoteBean("2222");
        bean2.setCount("共1条");
        bean2.setIsDirectory(true);
        bean2.setDate("09:33");
        bean2.setDirName("dirname");
        bean2.setIsCheck(false);
        NoteBean bean3 = new NoteBean("3333");
        bean3.setCount("共1条");
        bean3.setIsDirectory(false);
        bean3.setDate("09:33");
        bean3.setIsCheck(false);
        NoteBean bean4 = new NoteBean("4444");
        bean4.setCount("共1条");
        bean4.setIsDirectory(true);
        bean4.setDirName("11111111111111111111111111111199999111");
        bean4.setDate("09:33");
        bean4.setIsCheck(false);
        NoteBean bean5 = new NoteBean("5555");
        bean5.setCount("共1条");
        bean5.setIsDirectory(false);
        bean5.setDate("09:33");
        bean5.setIsCheck(false);
        NoteBean bean6 = new NoteBean("6666");
        bean6.setCount("共1条");
        bean6.setIsDirectory(true);
        bean6.setDate("09:33");
        bean6.setDirName("2222222222222222222");
        bean6.setIsCheck(false);
        list_note.add(bean1);
        list_note.add(bean2);
        list_note.add(bean3);
        list_note.add(bean4);
        list_note.add(bean5);
        list_note.add(bean6);
        mAdapter = new NoteAdapter(list_note,this);
    }

    @Override
    public Toolbar initToolBar() {
      //  toolbar = mGetView(R.id.toolbar);
        //return toolbar;
        return null;
    }

    @Override
    protected void initView() {
        rl_search = mGetView(R.id.rl_search);
        tv_showsearch = mGetViewSetOnClick(R.id.tv_showsearch);
        iv_addnote = mGetViewSetOnClick(R.id.iv_addnote);
        rl_head = mGetView(R.id.rl_head);
        rl_headchange = mGetView(R.id.rl_head_change);
        et_query = mGetView(R.id.query);
        scrollableLayout = mGetView(R.id.scrolllayout);
        iv_searchback = mGetViewSetOnClick(R.id.back);
        rl_bottommenu = mGetView(R.id.rl_bottom_menu);
        iv_delete = mGetViewSetOnClick(R.id.iv_delete);
        rl_bottom = mGetView(R.id.rl_bottom);
        fb_ok = mGetViewSetOnClick(R.id.fb_ok);
        fb_cance = mGetViewSetOnClick(R.id.fb_cancel);
        framelayout_head = mGetView(R.id.framelayout_head);
        tv_checkCount = mGetView(R.id.tv_checkcount);
        initRecylerView();

        fb_ok.setButtonColor(getResources().getColor(R.color.fbutton_color_pomegranate));
        fb_ok.setShadowEnabled(true);
        fb_ok.setShadowHeight(5);
        fb_ok.setCornerRadius(5);

        fb_cance.setButtonColor(getResources().getColor(R.color.fbutton_color_pomegranate));
        fb_cance.setShadowEnabled(true);
        fb_cance.setShadowHeight(5);
        fb_cance.setCornerRadius(5);
        final ItemClickSupport itemClick = ItemClickSupport.addTo(rv_note);
        itemClick.setOnItemLongClickListener(this);
//        itemClick.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(RecyclerView parent, View child, int position, long id) {
//                mToast.setText("Item clicked: " + position);
//                mToast.show();
//            }
//        });


    }

    private void initRecylerView(){
        rv_note = mGetView(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //设置布局管理器
        rv_note.setLayoutManager(linearLayoutManager);
        //设置adapter
        rv_note.setAdapter(mAdapter);
        mAdapter.setCountListener(new NoteAdapter.CountListener() {
            @Override
            public void count(int count) {
                tv_checkCount.setText("已选择"+count+"条");
            }
        });
        //设置Item增加、移除动画
        rv_note.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        rv_note.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL_LIST));
        rv_note.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {


            }

            @Override
            public void onShow() {

            }
        });

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_showsearch:
                Log.e(TAG,"bootomMenu Y:"+rl_bottom.getTop()+" bottom:"+rl_bottom.getBottom());
                enableSearchView();
                break;
            case R.id.back:
                disableSearchView();
                break;
            case R.id.iv_addnote:
                startActivity(new Intent(this,NoteActivity.class));
                break;
            case R.id.iv_delete:
                hideBottomMenu();
                hideHeadMenu();
                break;
            case R.id.fb_cancel:
                hideMenu();
                break;
        }
    }

    //显示顶部菜单
    private void showHeadMenu(){
        rl_head.setVisibility(View.GONE);
        rl_headchange.setVisibility(View.VISIBLE);
        ObjectAnimator obj = ObjectAnimator//
                .ofFloat(rl_headchange, "translationY", -rl_head.getHeight(),0)//
                .setDuration(500);//
        obj.start();
    }
    //隐藏顶部菜单
    private void hideHeadMenu(){
        ObjectAnimator obj = ObjectAnimator//
                .ofFloat(rl_headchange, "translationY", 0,-rl_head.getHeight())//
                .setDuration(500);//
        obj.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                rl_headchange.setVisibility(View.GONE);
                rl_head.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        obj.start();
    }

    private void showBottomMenu(){
        Log.e(TAG, "showBottomMenu");
        rl_bottom.setVisibility(View.GONE);
        rl_bottommenu.setVisibility(View.VISIBLE);
        ObjectAnimator obj = ObjectAnimator//
                .ofFloat(rl_bottommenu, "translationY", rl_bottom.getHeight(),0)//
                .setDuration(500);//
        obj.start();
    }

    private void hideBottomMenu(){
        rl_bottom.setVisibility(View.VISIBLE);
        //rl_bottom.setVisibility(View.GONE);
        ObjectAnimator obj = ObjectAnimator//
                .ofFloat(rl_bottommenu, "translationY", 0,rl_bottom.getHeight())//
                .setDuration(500);//
        obj.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rl_bottom.setClickable(false);
                //防止连续点击两次
                rl_bottommenu.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                rl_bottommenu.setVisibility(View.GONE);
                rl_bottom.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        obj.start();
    }


    private void disableSearchView(){
        iv_searchback.setVisibility(View.GONE);
        tv_showsearch.setVisibility(View.VISIBLE);
        et_query.setVisibility(View.GONE);
        scrollableLayout.setScrollable(true);
        rl_search.setBackgroundResource(R.color.white);
        AnimateUtils.translationY(framelayout_head, -framelayout_head.getHeight(), 0);
        AnimateUtils.translationY(scrollableLayout, -framelayout_head.getHeight(), 0);
    }


    private void enableSearchView(){
      //  Log.e(TAG, "search click");
       // WidgetUtils.setEditEnable(true,et_query);
        iv_searchback.setVisibility(View.VISIBLE);
        tv_showsearch.setVisibility(View.GONE);
        et_query.setVisibility(View.VISIBLE);
        rl_search.setBackgroundResource(R.color.main_bg);
        //设置Bu可以滚动
        scrollableLayout.setScrollable(false);
        ObjectAnimator obj = ObjectAnimator//
                .ofFloat(framelayout_head, "translationY", 0,-framelayout_head.getHeight())//
                .setDuration(500);//
        obj.start();
        AnimateUtils.translationY(scrollableLayout, 0, -framelayout_head.getHeight());
    }


    private void showMenu(){
        showHeadMenu();
        showBottomMenu();
        mAdapter.setEditable(true);
    }

    private void hideMenu(){
        hideHeadMenu();
        hideBottomMenu();
        mAdapter.clearCheckCount();
        mAdapter.setEditable(false);
    }
    @Override
    public boolean onItemLongClick(RecyclerView parent, View view, int position, long id) {
        toast("position:"+position);
        showMenu();
        return false;
    }


}
