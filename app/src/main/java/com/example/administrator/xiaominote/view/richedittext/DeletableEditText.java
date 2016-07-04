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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

/**
 * 这个是从stackOverFlow上面找到的解决方案，主要用途是处理软键盘回删按钮backSpace时回调OnKeyListener
 * 
 * @author xmuSistone
 * 
 */
public class DeletableEditText extends EditText {
	private Paint mPaint;
	private boolean isDrawLine = true;
	public DeletableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DeletableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DeletableEditText(Context context) {
		super(context);
		init();
	}

	private void init(){
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(4);
		mPaint.setStyle(Paint.Style.STROKE);
	}
	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		return new DeleteInputConnection(super.onCreateInputConnection(outAttrs),
				true);
	}

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
				Log.e("gac","right:"+rect.right +" centerx:"+(rect.bottom+rect.top)/2+" centerY:"+centerY);
				canvas.drawLine(13,centerY,rect.right+10,centerY,mPaint);
			}
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(isDrawLine){
			drawCenterLine(canvas);
		}
	}

	private class DeleteInputConnection extends InputConnectionWrapper {

		public DeleteInputConnection(InputConnection target, boolean mutable) {
			super(target, mutable);
		}

		@Override
		public boolean sendKeyEvent(KeyEvent event) {
			return super.sendKeyEvent(event);
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			if (beforeLength == 1 && afterLength == 0) {
				return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DEL))
						&& sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
								KeyEvent.KEYCODE_DEL));
			}

			return super.deleteSurroundingText(beforeLength, afterLength);
		}

	}
}