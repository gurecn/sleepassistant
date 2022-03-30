package com.devdroid.sleepassistant.base;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.devdroid.sleepassistant.listener.OnScreenShotListener;
import com.devdroid.sleepassistant.observer.MediaContentObserver;

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
	}


	
}
