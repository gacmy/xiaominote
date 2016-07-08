package com.example.administrator.xiaominote.view.richedittext;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.example.administrator.xiaominote.R;
import com.example.administrator.xiaominote.utils.ActivityUtils;
import com.example.administrator.xiaominote.view.checkbox.SmoothCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是一个富文本编辑器，给外部提供insertImage接口，添加的图片跟当前光标所在位置有关
 *
 * @author xmuSistone
 *
 */
@SuppressLint({ "NewApi", "InflateParams" })
public class RichTextEditor extends ScrollView {
	private static final int EDIT_PADDING = 10; // edittext常规padding是10dp
	private static final int EDIT_FIRST_PADDING_TOP = 10; // 第一个EditText的paddingTop值
	private int viewTagIndex = 0; // 新生的view都会打一个tag，对每个view来说，这个tag是唯一的。
	private LinearLayout allLayout; // 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
	private LayoutInflater inflater;
	private OnKeyListener keyListener; // 所有EditText的软键盘监听器
	private OnClickListener btnListener; // 图片右上角红叉按钮监听器
	private OnClickListener checkListener;
	private OnFocusChangeListener focusListener; // 所有EditText的焦点监听listener
	private DeletableEditText lastFocusEdit; // 最近被聚焦的EditText
	private LayoutTransition mTransitioner; // 只在图片View添加或remove时，触发transition动画
	private int editNormalPadding = 0; //
	private int disappearingImageIndex = 0;
	private Context context;
	private List<RichData> list_richData;
	public static String TAG = RichTextEditor.class.getName();
	private boolean mIsVisibleCheckBox;

	private boolean isInsertEdittext;//是否在插入图片后插入文本 插入图片有延时操作
	private Handler  mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			addEditTextAt(getChildCount(),"");
			isInsertEdittext = false;
		}
	};
	public RichTextEditor(Context context) {
		this(context, null);
	}

	public RichTextEditor(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RichTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflater = LayoutInflater.from(context);
		this.context = context;
		list_richData = new ArrayList<>();
		// 1. 初始化allLayout
		allLayout = new LinearLayout(context);
		allLayout.setOrientation(LinearLayout.VERTICAL);
		allLayout.setBackgroundColor(Color.WHITE);
		setupLayoutTransitions();
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		addView(allLayout, layoutParams);

		// 2. 初始化键盘退格监听
		// 主要用来处理点击回删按钮时，view的一些列合并操作
		keyListener = new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//删除按钮的处理情况
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
					DeletableEditText edit = (DeletableEditText) v;
					RichData richData = findRichDataByEditText(edit);
					onBackspacePress(richData);
					return false;
				}
				//回车处理的情况
				if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
					DeletableEditText editText = (DeletableEditText) v;
					RichData richData = findRichDataByEditText(editText);
					int viewindex = -1;
					if(richData != null){
						viewindex =allLayout.indexOfChild(richData.view);
					}
					if(viewindex != -1){
						enterAddEditText(editText,viewindex);
					}else{
						Log.e(TAG,"error:not found edittext");
					}
					return true;
				}
				return false;
			}
		};

		// 3. 图片叉掉处理
		btnListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				RelativeLayout parentView = (RelativeLayout) v.getParent();
				onImageCloseClick(parentView);
			}
		};

		focusListener = new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					lastFocusEdit = (DeletableEditText) v;
				}
			}
		};

		//checkbox点击事件
		checkListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				SmoothCheckBox checkBox = (SmoothCheckBox)v;

				boolean isCheck = checkBox.isChecked();
				if(isCheck){
					isCheck = false;
				}else{
					isCheck = true;
				}
				checkBox.setChecked(isCheck);
				//int index = checkBox.getTag();
				RichData richData = findRichDataByCheckbox(checkBox);
				if(richData != null){
					richData.isChecked = isCheck;
					richData.delet.setIsDrawLine(isCheck);
				}

			}
		};
		initRichEditText(context);
	}

	//找到控件在集合中的位置

	private int findIndexByEditText(DeletableEditText et){
		int res = -1;
		for(int i = 0; i < list_richData.size();i++){
			if(list_richData.get(i).delet.equals(et)){
				res = i;
				break;
			}
		}
		return res;
	}

	//根据布局view 查找在集合的下标
	private int findIndexByView(View view){
		int res = -1;
		for(int i = 0; i < list_richData.size();i++){
			if(list_richData.get(i).view.equals(view)){
				res = i;
				break;
			}
		}
		return res;
	}

	//根据控件找到RichData
	private RichData findRichDataByCheckbox(SmoothCheckBox checkBox){
		RichData data = null;
		for(int i = 0; i < list_richData.size();i++){
			if(list_richData.get(i).checkBox.equals(checkBox)){
				data = list_richData.get(i);
				break;
			}
		}
		return data;
	}

	//根据控件找到RichData
	private RichData findRichDataByEditText(DeletableEditText et){
		RichData data = null;
		for(int i = 0; i < list_richData.size();i++){
			if(list_richData.get(i).delet.equals(et)){
				data = list_richData.get(i);
				break;
			}
		}
		return data;
	}


	//按下回车键添加新的一行文本的处理方法
	private void enterAddEditText(DeletableEditText editText,int viewindex){
		int index = viewindex;
		//光标的位置
		int cursorindex = editText.getSelectionStart();
		//Log.e(TAG,"cursorindex:"+cursorindex);
		String curtext = "";
		String nexttext = "";
		if(editText.getText() != null){
			String text = editText.getText().toString();
			if(!TextUtils.isEmpty(text)){
				 curtext = text.substring(0,cursorindex);
				 nexttext = text.substring(cursorindex,text.length());
			}
		}
		editText.setText(curtext);
		addEditTextAt(index,nexttext);
		//每次按下回车键的时候都会检查checkbox是否显示
		//checkboxIsVisible(list_richData.get(index+1).checkBox);
	}


	//当外部界面点击 checkBox是否显示则调用此函数
	public void setCheckBoxIsVisible(boolean isVisible){
		mIsVisibleCheckBox = isVisible;
		int index = findIndexByEditText(lastFocusEdit);
		RichData richData = list_richData.get(index);
		if(richData == null ){
			return;
		}
		if(richData.checkBox == null){
			return;
		}
		checkboxIsVisible(richData.checkBox);
	}



	//设置checkbox是否处于可见状态
	private void checkboxIsVisible(SmoothCheckBox checkBox){
		checkBox.setVisibility(mIsVisibleCheckBox?View.VISIBLE:View.GONE);
	}


	private void setEnableEditText(DeletableEditText et){
		et.setEnabled(true);
		et.clearFocus();
		et.requestFocus();
		et.setSelection(et.getText().toString().length());//设置光标位置
		lastFocusEdit = et;
	}


	private void initRichEditText(Context context){
		createLineEditText("input content here",true);
	}


	//初始化的时候调用 创建Edittext
	private DeletableEditText createLineEditText(String hint,boolean isVisibleCheck) {
		View view = inflater.inflate(R.layout.edit_item1,
				null);
		DeletableEditText editText = (DeletableEditText) view.findViewById(R.id.del_et);
		editText.setOnKeyListener(keyListener);
		editText.setTag(viewTagIndex);
		view.setTag(viewTagIndex);
		editText.setPadding(editNormalPadding, 0, editNormalPadding, 0);
		editText.setHint(hint);
		editText.setOnFocusChangeListener(focusListener);
		setEnableEditText(editText);
		SmoothCheckBox checkBox = (SmoothCheckBox)view.findViewById(R.id.smoothcheckbox);
		checkBox.setOnClickListener(checkListener);
		checkBox.setTag(viewTagIndex);
		addRichData(checkBox,editText,mIsVisibleCheckBox,viewTagIndex,view);
		LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		allLayout.addView(view, firstEditParam);
		viewTagIndex++;
		return editText;
	}

	//初始化的时候 添加新的一行 所对应的数据信息
	private void addRichData(SmoothCheckBox checkBox,DeletableEditText editor,boolean isVisble,int viewindex,View view){
		RichData richData = new RichData();
		checkboxIsVisible(checkBox);
		richData.checkBox= checkBox;
		richData.delet = editor;
		richData.isVisibleCheck = isVisble;
		richData.isChecked = checkBox.isChecked();
		richData.viewindex = viewindex;
		richData.view = view;
		list_richData.add(richData);
	}


	//在当前文本的下一行插入数据
	private DeletableEditText addEditTextAt(int index,String nexttext){
		//index 当前光标的view的位置
		View view = inflater.inflate(R.layout.edit_item1,
				null);
		DeletableEditText editText = (DeletableEditText) view.findViewById(R.id.del_et);
		editText.setOnKeyListener(keyListener);
		editText.setTag(index+1);
		editText.setText(nexttext);
		view.setTag(index+1);
		editText.setPadding(editNormalPadding, 0, editNormalPadding, 0);
		//editText.setHint(hint);
		editText.setOnFocusChangeListener(focusListener);
		setEnableEditText(editText);
		SmoothCheckBox checkBox = (SmoothCheckBox)view.findViewById(R.id.smoothcheckbox);
		checkBox.setOnClickListener(checkListener);
		checkBox.setTag(index+1);
		addRichData(checkBox,editText,mIsVisibleCheckBox,0,view);
		LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		allLayout.addView(view,index+1, firstEditParam);
		//viewTagIndex++;
		return editText;
	}


	//清空所有内容
	public void clearAllText(){
		if(allLayout != null){
			allLayout.removeAllViews();
			list_richData.clear();
			viewTagIndex = 0;
			createLineEditText("",true);
		}
	}
	private List<EditData> list_editData;
	//根据内容填充richEditText
	public void setRichEditText(List<EditData> list){
		this.list_editData = list;
		if(list == null || list.size() == 0){
			return;
		}
		if(allLayout != null){
			allLayout.removeAllViews();
		}
		for(int i = 0; i < list.size(); i++){
			//Log.e("gac","**************************");
			String text = list.get(i).inputStr;
			String imagePath = list.get(i).imagePath;
			if(!TextUtils.isEmpty(text)){
				//Log.e("gac","insert text");
				addEdiTextAtIndexFoucus(allLayout.getChildCount(), text);
				//Log.e("gac", "inserttext index:" + allLayout.getChildCount());

			}
			if(!TextUtils.isEmpty(imagePath)){
				//Log.e("gac","insert img");
				inserImagesetContent(imagePath);
			}
			//Log.e("gac","**************************");
		}
		addEdiTextAtIndexFoucus(allLayout.getChildCount(), "");
	}
	/**
	 * 处理软键盘backSpace回退事件
	 *
	 * @param
	 *            //光标所在的文本输入框
	 */
	private void onBackspacePress(RichData richData) {
		DeletableEditText editTxt = richData.delet;
		//Log.e(TAG,"onBackPress");
		int startSelection = editTxt.getSelectionStart();
		// 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
		if (startSelection == 0) {
			int editIndex = allLayout.indexOfChild(richData.view);
			Log.e(TAG,"backpress index:"+editIndex);
			//如果为第一个Eidttext 删除checkbox
			if(editIndex == 0){
				richData.isChecked = false;
				richData.checkBox.setVisibility(View.GONE);
			}
			View preView = allLayout.getChildAt(editIndex - 1); // 如果editIndex-1<0,
			// 则返回的是null
			if (null != preView) {
				if (preView instanceof RelativeLayout) {
					// 光标EditText的上一个view对应的是图片
					//Log.e(TAG,"RelativeLayout");
					onImageCloseClick(preView);
				} else if (preView instanceof FrameLayout) {
					backPressEditText(preView,richData.view);
				}
			}
		}
	}

	//找到Edittext 前一个Edittext的布局视图
	private View findPreEdittextView(int curindex){
		View view = null;
		if(curindex < 1){
			return view;
		}
		for(int i = curindex-1;i >= 0;i--){
			view = allLayout.getChildAt(i);
			if(view instanceof FrameLayout){
				break;
			}
		}
		return view;
	}

	//按下返回键 上一个view 为Eidttext的处理方式
	private void backPressEditText(View view,View curView){
		Log.e(TAG,"FrameLayout");
		int i = findIndexByView(curView);//当前光标位置的view 集合下标
		//Log.e(TAG,"curindex:"+curindex);
		//找到view index 对应集合中控件的位置
		Log.e(TAG,"list i:"+i);
		// 光标EditText的上一个view对应的还是文本框EditText

			int curviewindex = allLayout.indexOfChild(curView);
			View preView = findPreEdittextView(curviewindex);
			if(preView == null){
				return;
			}
			int preIndex = findIndexByView(preView);
			Log.e(TAG,"curindex:"+curviewindex+" preindex:"+preIndex);
			//隐藏checkbox
			list_richData.get(i).isChecked = false;
			list_richData.get(i).checkBox.setVisibility(View.GONE);
			//凭凑上下 edittext的文本
			DeletableEditText preeditext = list_richData.get(preIndex).delet;
			DeletableEditText nexteidtext = list_richData.get(i).delet;
			String strpre = preeditext.getText().toString();
			String strnext = nexteidtext.getText().toString();
			String text = strpre+strnext;
			nexteidtext.setText("");
			preeditext.setText(text);
			preeditext.setSelection(0);//设置光标位置在最前面为0
			//int prei= findIByIndex(curindex -1);
			setEnableEditText(list_richData.get(preIndex).delet);

	}



	/**
	 * 处理图片叉掉的点击事件
	 *
	 * @param view
	 *            整个image对应的relativeLayout view
	 * @type 删除类型 0代表backspace删除 1代表按红叉按钮删除
	 */
	private void onImageCloseClick(View view) {
		if (!mTransitioner.isRunning()) {
			disappearingImageIndex = allLayout.indexOfChild(view);
			allLayout.removeView(view);
		}
	}

	/**
	 * 生成文本输入框
	 */
	private View createEditText(String hint, int paddingTop) {
		LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		editNormalPadding = dip2px(EDIT_PADDING);
		View view = inflater.inflate(R.layout.edit_item1,
				null);
		EditText editText = (EditText) view.findViewById(R.id.del_et);
		editText.setOnKeyListener(keyListener);
		editText.setTag(viewTagIndex++);
		editText.setPadding(editNormalPadding, paddingTop, editNormalPadding, 0);
		editText.setHint(hint);
		editText.setOnFocusChangeListener(focusListener);
		allLayout.addView(view, firstEditParam);
		return view;
	}

	/**
	 * 生成图片View
	 */
	private RelativeLayout createImageLayout() {
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.edit_imageview, null);
		layout.setTag(viewTagIndex++);
		View closeView = layout.findViewById(R.id.image_close);
		closeView.setTag(layout.getTag());
		closeView.setOnClickListener(btnListener);
		return layout;
	}

	/**
	 * 根据绝对路径添加view
	 *
	 * @param imagePath
	 */
	public void insertImage(String imagePath) {
		Log.e("gac", "iamgePath:" + imagePath);
		Bitmap bmp = getScaledBitmap(imagePath, getWidth());
		if(bmp == null){

			Log.e("gac","bmp is null");
			return;
		}else{
			Log.e("gac","bmp is not null");
		}
		insertImage(bmp, imagePath);
	}

	//重新填充List<EditData>内容 的时候 采用这个方法
	private void inserImagesetContent(String imagePath){
		Log.e("gac","iamgePath:"+imagePath);
		int screenWidth = ActivityUtils.getScreenWidth(context);
		Log.e("gac","getWidth:"+ screenWidth);
		Bitmap bmp = getScaledBitmap(imagePath, screenWidth);
		if(bmp == null){
			Log.e("gac","bmp is null");
			return;
		}else{
			Log.e("gac","bmp is not null");
		}

		Log.e("gac","insertImage index:"+allLayout.getChildCount());
		addImageViewAtIndexInstant(allLayout.getChildCount(), bmp, imagePath);
	}
	/**
	 * 插入一张图片
	 */
	private void insertImage(Bitmap bitmap, String imagePath) {
		String lastEditStr = lastFocusEdit.getText().toString();
		int cursorIndex = lastFocusEdit.getSelectionStart();//光标的位置
		RichData richData = findRichDataByEditText(lastFocusEdit);
		int curviewindex = allLayout.indexOfChild(richData.view);
		int foucseditindex = findIndexByEditText(lastFocusEdit);
		//光标位置在最前面的时候 则在Eidttext上面插入图片
		if(cursorIndex == 0){
			addImageViewAtIndex(curviewindex, bitmap, imagePath, foucseditindex);
			return;
		}

		//当光标位于文字中间的时候 则 将光标右边的文字 添加到图片下面 并且重新创建一个Edittext控件
		String leftstr = lastEditStr.substring(0, cursorIndex).trim();
		String rightstr = lastEditStr.substring(cursorIndex,lastEditStr.length()).trim();
		if( !TextUtils.isEmpty(leftstr) &&  !TextUtils.isEmpty(rightstr) ){
			lastFocusEdit.setText(leftstr);
			addImageViewAtIndexInstant(curviewindex+1, bitmap, imagePath);
			addEditTextAt(curviewindex+1,rightstr);
			return;
		}
		//当光标位于最右边的时候 editext 不是最后一个view 则直接在Edittext下面插入 如果是则插入后 继续添加个edittext
		if(curviewindex == allLayout.getChildCount()-1){
			//添加imageview
			//Log.e(TAG,"getchildCount::"+allLayout.getChildCount());
			isInsertEdittext = true;
			addImageViewAtIndexInstant(curviewindex+1, bitmap, imagePath);
		//	Log.e(TAG,"getchildCount1::"+allLayout.getChildCount());
			addEditTextAt(allLayout.getChildCount()-1,"");
		}else{
			addImageViewAtIndex(curviewindex+1, bitmap, imagePath, foucseditindex);
		}
		hideKeyBoard();
	}

	/**
	 * 隐藏小键盘
	 */
	public void hideKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(), 0);
	}

	/**
	 * 在特定位置插入EditText
	 *
	 * @param index
	 *            位置
	 * @param editStr
	 *            EditText显示的文字
	 */
	private void addEditTextAtIndex(final int index, String editStr) {
		View view = createEditText("", getResources()
				.getDimensionPixelSize(R.dimen.edit_padding_top));
		EditText editText2 = (DeletableEditText)view.findViewById(R.id.del_et);
		editText2.setText(editStr);

		// 请注意此处，EditText添加、或删除不触动Transition动画
		allLayout.setLayoutTransition(null);
		allLayout.addView(editText2, index);
		allLayout.setLayoutTransition(mTransitioner); // remove之后恢复transition动画
	}

	private void addEdiTextAtIndexFoucus(final int index,String  editStr){
		View view = createEditText("", getResources()
				.getDimensionPixelSize(R.dimen.edit_padding_top));
		EditText editText2 =(EditText) view.findViewById(R.id.del_et);
		editText2.setText(editStr);
		//lastFocusEdit = editText2;
		// 请注意此处，EditText添加、或删除不触动Transition动画
		allLayout.setLayoutTransition(null);
		allLayout.addView(editText2, index);
		allLayout.setLayoutTransition(mTransitioner); // remove之后恢复transition动画
	}

	//立即插入图片 不进行延时处理
	private View addImageViewAtIndexInstant(final int index, Bitmap bmp,
											String imagePath) {
		final RelativeLayout imageLayout = createImageLayout();
		DataImageView imageView = (DataImageView) imageLayout
				.findViewById(R.id.edit_imageView);
		imageView.setImageBitmap(bmp);
		imageView.setBitmap(bmp);
		imageView.setAbsolutePath(imagePath);

		// 调整imageView的高度
		int imageHeight = getWidth() * bmp.getHeight() / bmp.getWidth();
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, imageHeight);
		imageView.setLayoutParams(lp);
	//setEnableEditText(list_richData.get(foucsindex).delet);
		allLayout.addView(imageLayout, index);
		return imageLayout;
	}
	/**
	 * 在特定位置添加ImageView
	 * index 图片插入位置
	 * foucsindex 获得焦点的那行Edittext索引  list index not view index
	 */
	private void addImageViewAtIndex(final int index, Bitmap bmp,
									 String imagePath,int foucsindex) {
		final RelativeLayout imageLayout = createImageLayout();
		DataImageView imageView = (DataImageView) imageLayout
				.findViewById(R.id.edit_imageView);
		imageView.setImageBitmap(bmp);
		imageView.setBitmap(bmp);
		imageView.setAbsolutePath(imagePath);

		// 调整imageView的高度
		int imageHeight = getWidth() * bmp.getHeight() / bmp.getWidth();
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, imageHeight);
		imageView.setLayoutParams(lp);
		setEnableEditText(list_richData.get(foucsindex).delet);
		// onActivityResult无法触发动画，此处post处理
		allLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				allLayout.addView(imageLayout, index);

//				if(isInsertEdittext){
//					mHandler.sendEmptyMessage(0x00);
//				}
			}
		}, 200);
	}

	/**
	 * 根据view的宽度，动态缩放bitmap尺寸
	 *
	 * @param width
	 *            view的宽度
	 */
	private Bitmap getScaledBitmap(String filePath, int width) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		int sampleSize = options.outWidth > width ? options.outWidth / width
				+ 1 : 1;
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 初始化transition动画
	 */
	private void setupLayoutTransitions() {
		mTransitioner = new LayoutTransition();
		allLayout.setLayoutTransition(mTransitioner);
		mTransitioner.addTransitionListener(new TransitionListener() {

			@Override
			public void startTransition(LayoutTransition transition,
										ViewGroup container, View view, int transitionType) {

			}

			@Override
			public void endTransition(LayoutTransition transition,
									  ViewGroup container, View view, int transitionType) {
				if (!transition.isRunning()
						&& transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
					// transition动画结束，合并EditText
					// mergeEditText();
				}
			}
		});
		mTransitioner.setDuration(300);
	}



	/**
	 * dp和pixel转换
	 *
	 * @param dipValue
	 *            dp值
	 * @return 像素值
	 */
	public int dip2px(float dipValue) {
		float m = getContext().getResources().getDisplayMetrics().density;
		return (int) (dipValue * m + 0.5f);
	}

	/**
	 * 对外提供的接口, 生成编辑数据上传
	 */
	public List<EditData> buildEditData() {
		List<EditData> dataList = new ArrayList<EditData>();
		int num = allLayout.getChildCount();
		for (int index = 0; index < num; index++) {
			View itemView = allLayout.getChildAt(index);
			EditData itemData = new EditData();
			if (itemView instanceof EditText) {
				EditText item = (EditText) itemView;
				itemData.inputStr = item.getText().toString();
			} else if (itemView instanceof RelativeLayout) {
				DataImageView item = (DataImageView) itemView
						.findViewById(R.id.edit_imageView);
				itemData.imagePath = item.getAbsolutePath();
				//itemData.bitmap = item.getBitmap();
			}
			dataList.add(itemData);
		}

		return dataList;
	}


	public class EditData {
		public String inputStr;
		public String imagePath;
		//public Bitmap bitmap;
		public EditData(){

		}
		public EditData(String text,String path){
			this.inputStr = text;
			this.imagePath = path;
		}
	}
}
