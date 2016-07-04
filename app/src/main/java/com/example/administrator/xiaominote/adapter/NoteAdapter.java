package com.example.administrator.xiaominote.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaominote.R;
import com.example.administrator.xiaominote.bean.NoteBean;
import com.example.administrator.xiaominote.utils.ActivityUtils;
import com.example.administrator.xiaominote.utils.WidgetUtils;
import com.example.administrator.xiaominote.view.checkbox.SmoothCheckBox;

import java.util.List;

/**
 * Created by gac on 2016/6/24.
 */
public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static String TAG = NoteAdapter.class.getName();
    private List<NoteBean> mList;
    public static int DIR_VIEW = 1;
    public static int NOTE_VIEW = 2;
    public CountListener countListener;
    private Activity context;
    public interface CountListener{
        public abstract void count(int count);
    }
    public NoteAdapter(List<NoteBean> list,Activity context){
        mList = list;
        this.context = context;
    }

    public void setCountListener(CountListener listener){
        countListener = listener;
    }
    @Override
    public int getItemViewType(int position) {
        if(mList.get(position).isDirectory()){
            return DIR_VIEW;
        }
        return NOTE_VIEW;
    }

    //设置recylerview可以编辑状态
    public void setEditable(boolean flag){
        for(int i = 0; i < mList.size();i++){
            mList.get(i).isEidtable = flag;
        }
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
       if(viewType == NOTE_VIEW){
            View noteView =  LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.note_list_item, parent,
                            false);

            holder = new MyViewHolder(noteView);
        }else{
           View dirnoteView = LayoutInflater.from(
                   parent.getContext()).inflate(R.layout.note_list_dir_item, parent,
                   false);
           holder = new DirViewHolder(dirnoteView);
       }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            MyViewHolder myViewHolder = ((MyViewHolder) holder);
            onBindMyViewHolder(myViewHolder,position);
        }
        if(holder instanceof  DirViewHolder){
            DirViewHolder dirViewHolder = ((DirViewHolder) holder);
            onDirHolder(dirViewHolder, position);
        }

    }

    private void onBindMyViewHolder(MyViewHolder holder,final int position){
        NoteBean bean = mList.get(position);
        WidgetUtils.setTextViewEnd(bean.content, holder.tv_content);
        holder.tv_date.setText(bean.date);
        if(bean.isEidtable){
            holder.scb_note.setVisibility(View.VISIBLE);
            CheckBox cb;
            holder.tv_date.setVisibility(View.GONE);
          //  holder.scb_note.setChecked(bean.isCheck);
        }else{
            holder.scb_note.setVisibility(View.GONE);
            holder.tv_date.setVisibility(View.VISIBLE);
        }
        Log.e(TAG,"isChecked:"+bean.isCheck);
        holder.scb_note.setChecked(bean.isCheck);
        holder.scb_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SmoothCheckBox checkBox = (SmoothCheckBox) v;
                boolean isCheck = checkBox.isChecked();
                if(isCheck){
                    isCheck = false;
                }else{
                    isCheck = true;
                }
                Log.e(TAG,"note click"+" checked:"+checkBox.isChecked());
                mList.get(position).isCheck = isCheck;
                checkBox.setChecked(isCheck);
                countListener.count(checkedCount());
            }

          });
    }
                  //检测选择条目的数量

    private int checkedCount(){
        int count = 0;
        for(int i = 0; i < mList.size();i++){
            if(mList.get(i).isCheck){
                count++;
            }
        }
        return count;
    }
    public void clearCheckCount(){
        for(int i = 0; i < mList.size();i++){
            mList.get(i).isCheck = false;
        }
        countListener.count(0);
    }
    private void onDirHolder(DirViewHolder holder, final int position){
        NoteBean bean = mList.get(position);
        WidgetUtils.setTextViewEnd(bean.dirName, holder.tv_dirname);
        holder.tv_dir_notecount.setText(bean.count);
        if(bean.isEidtable){
            holder.iv_enter.setVisibility(View.GONE);
            holder.scb_dir.setVisibility(View.VISIBLE);

        }else{
            holder.iv_enter.setVisibility(View.VISIBLE);
            holder.scb_dir.setVisibility(View.GONE);
        }
        holder.scb_dir.setChecked(bean.isCheck);
        holder.scb_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmoothCheckBox checkBox = (SmoothCheckBox) v;
                boolean isCheck = checkBox.isChecked();
                if(isCheck){
                    isCheck = false;
                }else{
                    isCheck = true;
                }
                Log.e(TAG,"note click"+" checked:"+checkBox.isChecked());
                mList.get(position).isCheck = isCheck;
                checkBox.setChecked(isCheck);
                countListener.count(checkedCount());
            }
        });


    }
        @Override
        public int getItemCount() {
            return mList.size();
         }



    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_content;
        TextView tv_date;
        SmoothCheckBox scb_note;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_content = (TextView)itemView.findViewById(R.id.content);
            tv_date = (TextView)itemView.findViewById(R.id.date);
            scb_note = (SmoothCheckBox)itemView.findViewById(R.id.scb_note);
        }
    }

    class DirViewHolder extends  RecyclerView.ViewHolder{
        public TextView tv_dirname;
        public TextView tv_dir_notecount;
        public ImageView iv_enter;
        public SmoothCheckBox scb_dir;
        public DirViewHolder(View itemView) {
            super(itemView);
            tv_dirname = (TextView)itemView.findViewById(R.id.tv_dirname);
            tv_dir_notecount = (TextView)itemView.findViewById(R.id.tv_dir_notecount);
            iv_enter = (ImageView)itemView.findViewById(R.id.iv_note_dir_back);
            scb_dir = (SmoothCheckBox)itemView.findViewById(R.id.scb_dir);
        }
    }


}

