package com.devdroid.sleepassistant.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.jinrishici.sdk.android.model.OriginBean;

public class ShiciActivity extends BaseActivity {
  private OriginBean mOriginBean;
  private TextView mTvShiciTitle;
  private TextView mTvShiciDynasty;
  private TextView mTvShiciAuthor;
  private TextView mTvShiciContent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shici);
  }

  @Override
  protected void onStart() {
    super.onStart();
    initView();
  }

  private void initView() {
    Intent intent = getIntent();
    mOriginBean = (OriginBean)intent.getSerializableExtra("BeanOrigin");
    mTvShiciTitle = findViewById(R.id.tv_shici_title);
    mTvShiciDynasty = findViewById(R.id.tv_shici_dynasty);
    mTvShiciAuthor = findViewById(R.id.tv_shici_author);
    mTvShiciContent = findViewById(R.id.tv_shici_content);
  }

  @Override
  protected void onResume() {
    super.onResume();
    initData();
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
     // sb.append(str.replaceAll("，|,", "，\n").replaceAll("。|\\.", "。\n").replaceAll("；|;", "；\n")).append("\n");
      sb.append(str.replaceAll("(，|,|\\.|。|;|；|\\?|？)", "$1\n")).append("\n");
    }
    mTvShiciContent.setText(sb.toString());
  }
}