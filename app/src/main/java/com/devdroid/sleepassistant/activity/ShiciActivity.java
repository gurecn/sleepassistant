package com.devdroid.sleepassistant.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.jinrishici.sdk.android.model.OriginBean;
import java.util.Random;

public class ShiciActivity extends BaseActivity {
  private OriginBean mOriginBean;
  private TextView mTvShiciTitle;
  private TextView mTvShiciDynasty;
  private TextView mTvShiciAuthor;
  private TextView mTvShiciContent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
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
    boolean isNightMode = LauncherModel.getInstance().getSharedPreferencesManager().getBoolean(IPreferencesIds.KEY_THEME_NIGHT_MODE, false);
    ImageView ivShiciBg = findViewById(R.id.in_shici_bg);
    Random r1 = new Random();
    int[] resources = new int[]{R.drawable.shici_bg_0,R.drawable.shici_bg_1,R.drawable.shici_bg_2};
    ivShiciBg.setImageResource(resources[r1.nextInt(9)%3]);
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
      sb.append(str.replaceAll("(:|：|，|,|\\.|。|;|；|\\?|？|！|!)", "$1\n"));
    }
    mTvShiciContent.setText(sb.toString());
  }
}