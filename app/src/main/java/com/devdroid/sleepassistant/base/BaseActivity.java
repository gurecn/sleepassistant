package com.devdroid.sleepassistant.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.listener.OnScreenShotListener;
import com.devdroid.sleepassistant.observer.MediaContentObserver;
import com.devdroid.sleepassistant.utils.AppUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.io.FileInputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements OnScreenShotListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImmersionBar.with(this).statusBarColor(R.color.colorPrimary).init();
		Application application = getApplication();
		if (application instanceof IApplication) {
			((IApplication) application).addActivityToStack(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Application application = getApplication();
		if (application instanceof IApplication) {
			((IApplication) application).removeActivityFromStack(this);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Application application = getApplication();
		if (application instanceof IApplication) {
			((IApplication) application).addActivityStart(this);
		}
		MediaContentObserver.getInstance().stareObserve(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Application application = getApplication();
		if (application instanceof IApplication) {
			((IApplication) application).addActivityPause(this);
		}
	}

	/**
	 * 添加引导图片
	 */
	public void addGuideImage(int xmlId, int guideResourceId) {
		if (xmlId == 0) {
			return;
		}
		View view = ((ViewGroup) findViewById(xmlId)).getChildAt(0);
		if (view == null) {
			return;
		}
		ViewParent viewParent = view.getParent();
		if (viewParent instanceof ViewGroup) {
			final ViewGroup frameLayout = (ViewGroup) viewParent;
			if (guideResourceId != 0) {//设置了引导图片
				final ImageView guideImage = new ImageView(this);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				guideImage.setLayoutParams(params);
				guideImage.setScaleType(ImageView.ScaleType.FIT_XY);
				guideImage.setImageResource(guideResourceId);
				guideImage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						frameLayout.removeView(guideImage);
					}
				});
				frameLayout.addView(guideImage);//添加引导图片
			}
		}
	}

	@Override
	public void onShot(String imagePath) {
		Log.d("BaseActivity", "onShot:" + imagePath);
		Bitmap screenshot = decodeBitmap(imagePath);
		popupWindow(screenshot);
	}

	/**
	 * 图片文件解析成Bitmap
	 * @param imagePath 截图图片路径
	 * @return Bitmap对象
	 */
	private Bitmap decodeBitmap(String imagePath){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		FileInputStream is = null;
		try {
			is = new FileInputStream(imagePath);
			Bitmap bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, options);
			return bmp;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据图片尺寸计算窗口高度
	 * @return 窗口高度
	 */
	private int matchHight(int width, int hight) {
		if(hight > width * 1.5){
			hight = (int)(width * 1.5);
		}else if(hight < width){
			hight = width;
		}
		return hight;
	}

	/**
	 * 弹窗显示
	 * @param screenshoth
	 */
	private void popupWindow(Bitmap screenshoth){
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.ll_screenshot_dialog, null);
		ImageView imageView = view.findViewById(R.id.iv_screenshot_image);
		ImageView imageClose = view.findViewById(R.id.iv_screenshot_close);
		TextView feedback = view.findViewById(R.id.tv_feed_back);
		imageView.setImageBitmap(screenshoth);
		int width = screenshoth.getWidth();
		int hight = screenshoth.getHeight();
		hight = matchHight(width, hight);
		PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setWidth(width);
		popupWindow.setHeight(hight);
		imageClose.setOnClickListener(v -> {
			if(popupWindow.isShowing()){
				popupWindow.dismiss();
			}
		});
		feedback.setOnClickListener(v -> {
			if(popupWindow.isShowing()){
				popupWindow.dismiss();
			}
			AppUtils.feedbackDialog(BaseActivity.this);
		});
		runOnUiThread(() -> {
			if(getWindow() != null && getWindow().getDecorView() != null) {
				popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.RIGHT, 20, 0);
			}
		});
	}
}
