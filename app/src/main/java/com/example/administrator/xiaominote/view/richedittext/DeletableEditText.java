package com.example.administrator.xiaominote.view.richedittext;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;


/**
 *
 * 
 * @author gac
 * 
 */
public class DeletableEditText extends EditText {
	private Paint mPaint;
	private Context mContext;
	int beforcount;
	private Editable editable;
	private TextSpan span;
	public DeletableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public DeletableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DeletableEditText(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context){
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(4);
		mPaint.setStyle(Paint.Style.STROKE);
		mContext = context;
		addTextChangedListener(watcher);
		span = new TextSpan();
	}

	TextWatcher watcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			beforcount = count;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			editable = s;
			s.setSpan(span,0,s.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	};


	public void setIsDrawLine(boolean isDrawLine){

		span.setIsDrawLine(isDrawLine);
		if(editable != null){
			editable.setSpan(span,0,editable.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (!isDrawLine){
			editable.removeSpan(span);
		}

	}


}