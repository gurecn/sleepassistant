package com.devdroid.sleepassistant.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;

public class BaseActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
}
