package com.example.administrator.xiaominote.view.richedittext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.text.TextUtils;

import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.EditText;

import com.example.administrator.xiaominote.utils.ActivityUtils;

/**
 * 这个是从stackOverFlow上面找到的解决方案，主要用途是处理软键盘回删按钮backSpace时回调OnKeyListener
 * 
 * @author xmuSistone
 * 
 */
public class DeletableEditText extends EditText {
	private Paint mPaint;
	private boolean isDrawLine = true;
	private Context mContext;
	OnKeyListener keyListener;
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


	}

//	@Override
//	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
//		return new DeleteInputConnection(super.onCreateInputConnection(outAttrs),
//				true);
//	}

	public void setIsDrawLine(boolean isDrawLine){
		this.isDrawLine = isDrawLine;
		invalidate();
	}
	private void drawCenterLine(Canvas canvas){
		if( getText() != null ){
			String str = getText().toString();
			Rect rect = new Rect();
			if(!TextUtils.isEmpty(str)){
				getPaint().getTextBounds(str,0,str.length(),rect);
				float centerY = getLineHeight();
			//	Log.e("gac","right:"+rect.right +" centerx:"+(rect.bottom+rect.top)/2+" centerY:"+centerY);
				canvas.drawLine(13,centerY,rect.right+10,centerY,mPaint);

			}
		}
	}


	public boolean sendEnterKey(){
		if( getText() != null ){
			String str = getText().toString();
			Rect rect = new Rect();
			if(!TextUtils.isEmpty(str)){
				getPaint().getTextBounds(str,0,str.length(),rect);
				float screenwidth =ActivityUtils.getScreenWidth(getContext());
				Log.e("gac","screenwidth:"+screenwidth+" right:"+rect.right);
				if(rect.right >=  screenwidth){
					return true;
				}

			}
		}
		return false;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		sendEnterKey();
		if(isDrawLine){
			drawCenterLine(canvas);
		}
	}
//
//	private class DeleteInputConnection extends InputConnectionWrapper {
//
//		public DeleteInputConnection(InputConnection target, boolean mutable) {
//			super(target, mutable);
//		}
//
//		@Override
//		public boolean sendKeyEvent(KeyEvent event) {
//			return super.sendKeyEvent(event);
//		}
//
//		@Override
//		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
//			if (beforeLength == 1 && afterLength == 0) {
//				return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
//						KeyEvent.KEYCODE_DEL))
//						&& sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
//								KeyEvent.KEYCODE_DEL));
//			}
//
//			return super.deleteSurroundingText(beforeLength, afterLength);
//		}
//
//	}
}