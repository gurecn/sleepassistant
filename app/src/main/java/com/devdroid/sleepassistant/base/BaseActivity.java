package com.devdroid.sleepassistant.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.io.FileInputStream;
import java.io.IOException;

public class BaseActivity extends AppCompatActivity implements OnScreenShotListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		captureWindow(imagePath);
	}

	private void captureWindow(String imagePath){
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//自定义布局
		View view = inflater.inflate(R.layout.ll_screenshot_dialog, null);
		ImageView imageView = view.findViewById(R.id.img_screenshot_image);
		ImageView imageClose = view.findViewById(R.id.img_cbm_screenshot_close);
		TextView feedback = view.findViewById(R.id.tv_feed_back);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		int width = 0;
		int hight = 0;
		FileInputStream is = null;
		try {
			is = new FileInputStream(imagePath);
			Bitmap bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, options);
			imageView.setImageBitmap(bmp);
			width = bmp.getWidth();
			hight = bmp.getHeight();
			if(hight > width * 1.5){
				hight = (int)(width * 1.5);
			}else if(hight < width){
				hight =width;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setWidth(width);
		popupWindow.setHeight(hight);
		imageClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(popupWindow != null && popupWindow.isShowing()){
					popupWindow.dismiss();
				}
			}
		});
		feedback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(popupWindow != null && popupWindow.isShowing()){
					popupWindow.dismiss();
				}
				AppUtils.feedbackDialog(BaseActivity.this);
			}
		});
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(getWindow() != null && getWindow().getDecorView() != null) {
					popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.RIGHT, 20, 0);
				}
			}
		});
	}

}
