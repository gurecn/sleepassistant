package com.devdroid.sleepassistant.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.freefont.core.animation.A;
import com.devdroid.sleepassistant.freefont.core.data.DrawData;
import com.devdroid.sleepassistant.freefont.core.view.STextView;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.jinrishici.sdk.android.model.OriginBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class ShiciActivity extends BaseActivity {
  private OriginBean mOriginBean;
  private TextView mTvShiciTitle;
  private TextView mTvShiciDynasty;
  private TextView mTvShiciAuthor;
  private STextView mTvShiciContent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ImmersionBar.with(this).transparentBar().init();
    getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
    //不显示状态栏
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    setContentView(R.layout.activity_shici);
    initView();
    initData();
  }

  private void initView() {
    String shiciContext = LauncherModel.getInstance().getSharedPreferencesManager().getString(IPreferencesIds.KEY_SHICI_CONTENT_LAST, "");
    mOriginBean = new Gson().fromJson(shiciContext, OriginBean.class);
    mTvShiciTitle = findViewById(R.id.tv_shici_title);
    mTvShiciDynasty = findViewById(R.id.tv_shici_dynasty);
    mTvShiciAuthor = findViewById(R.id.tv_shici_author);
    mTvShiciContent = findViewById(R.id.tv_shici_content);
    ImageView ivShiciBg = findViewById(R.id.in_shici_bg);
    Random r1 = new Random();
    int[] resources = new int[]{R.drawable.shici_bg_0,R.drawable.shici_bg_1,R.drawable.shici_bg_2};
    ivShiciBg.setImageResource(resources[r1.nextInt(9)%3]);
  }

  private void initData() {
    //从asset 读取字体
    AssetManager mgr = getAssets();
    Typeface tf = Typeface.createFromAsset(mgr, "fonts/STKAITI.TTF");
    mTvShiciTitle.setTypeface(tf);
    mTvShiciDynasty.setTypeface(tf);
    mTvShiciAuthor.setTypeface(tf);
    mTvShiciContent.setTypeface(tf);
    mTvShiciTitle.setText(mOriginBean.getTitle());
    mTvShiciDynasty.setText(mOriginBean.getDynasty());
    mTvShiciAuthor.setText(mOriginBean.getAuthor());
    StringBuilder sb = new StringBuilder();
    for (String str:mOriginBean.getContent()){
      sb.append(str.replaceAll("(:|：|，|,|\\.|。|;|；|\\?|？|！|!)", "$1\n"));
    }
    mTvShiciContent.setText(sb.toString());
    try {
      InputStream is = getAssets().open("gson.txt");
      InputStreamReader isr = new InputStreamReader(is);
      DrawData data = new Gson().fromJson(isr, DrawData.class);
      data.aniType = A.SINGLE_RIGHT_FADE_INF_LEFT_FADE_OUT;
      int coclor = mTvShiciContent.getCurrentTextColor();
      data.layers.get(0).paintParam.color = String.valueOf(coclor);
      mTvShiciContent.setData(data);
    } catch (Exception e){
      e.printStackTrace();
    }
    mTvShiciContent.getTAnimation().start();
  }
}