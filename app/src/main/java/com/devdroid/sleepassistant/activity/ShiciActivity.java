package com.devdroid.sleepassistant.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Magnifier;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class ShiciActivity extends BaseActivity {
  public static final String REGEX = "(:|：|，|,|\\.|。|;|；|\\?|？|！|!)";
  private OriginBean mOriginBean;
  private TextView mTvShiciTitle;
  private TextView mTvShiciDynasty;
  private TextView mTvShiciAuthor;
  private STextView mSTvShiciContent;
  private TextView mTvShiciContent;

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
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//      setActionMode();
//    }
  }


  private void initView() {
    Intent intent = getIntent();
    String shici = intent.getStringExtra("shici");
    if(!TextUtils.isEmpty(shici)){
      LauncherModel.getInstance().getSharedPreferencesManager().commitString(IPreferencesIds.KEY_SHICI_CONTENT_LAST, shici);
    } else {
      shici = LauncherModel.getInstance().getSharedPreferencesManager().getString(IPreferencesIds.KEY_SHICI_CONTENT_LAST, "");
    }
    mOriginBean = new Gson().fromJson(shici, OriginBean.class);
    View layout = findViewById(R.id.rl_content_layout);
    mTvShiciTitle = findViewById(R.id.tv_shici_title);
    mTvShiciDynasty = findViewById(R.id.tv_shici_dynasty);
    mTvShiciAuthor = findViewById(R.id.tv_shici_author);
    mSTvShiciContent = findViewById(R.id.stv_shici_content);
    mTvShiciContent = findViewById(R.id.tv_shici_content);
    ImageView ivShiciBg = findViewById(R.id.in_shici_bg);
    setMagnifier(mSTvShiciContent);
    setMagnifier(mTvShiciContent);
    setMagnifier(layout);
    Random r1 = new Random();
    int[] resources = new int[]{R.drawable.shici_bg_0,R.drawable.shici_bg_1,R.drawable.shici_bg_2};
    ivShiciBg.setImageResource(resources[r1.nextInt(9)%3]);
  }

  @SuppressLint("ClickableViewAccessibility")
  private void setMagnifier(View view) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Magnifier.Builder builder = new Magnifier.Builder(view);
        builder.setOverlay(ContextCompat.getDrawable(this, R.drawable.magnifier));//设置覆盖物
        builder.setInitialZoom(2);//设置初始放大倍数，与Magnifier的setZoom功能相同
        builder.setCornerRadius(100);  //设置圆角：0-100
        builder.setClippingEnabled(false);//false时放大镜可以滑到手机屏幕外；true时放大镜只能再屏幕内滑动。
        builder.setSize(180, 180);//设置放大镜宽高
        Magnifier magnifier = builder.build();
        magnifier.setZoom(2);  //设置放大倍数：
        view.setOnTouchListener((v, event) -> {
          switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
              // Fall through.
            case MotionEvent.ACTION_MOVE: {
              final int[] viewPosition = new int[2];
              v.getLocationOnScreen(viewPosition);
              magnifier.show(event.getRawX() - viewPosition[0], event.getRawY() - viewPosition[1]);
              break;
            }
            case MotionEvent.ACTION_CANCEL:
              // Fall through.
            case MotionEvent.ACTION_UP: {
              magnifier.dismiss();
            }
          }
          return true;
        });
      }
    }
  }

  private void initData() {
    //从asset 读取字体
    AssetManager mgr = getAssets();
    Typeface tf = Typeface.createFromAsset(mgr, "fonts/STKAITI.TTF");
    mTvShiciTitle.setTypeface(tf);
    mTvShiciDynasty.setTypeface(tf);
    mTvShiciAuthor.setTypeface(tf);
    mSTvShiciContent.setTypeface(tf);
    mTvShiciContent.setTypeface(tf);
    mTvShiciTitle.setText(mOriginBean.getTitle());
    mTvShiciDynasty.setText(mOriginBean.getDynasty());
    mTvShiciAuthor.setText(mOriginBean.getAuthor());
    StringBuilder shici = new StringBuilder();
    for (String str:mOriginBean.getContent()){
      shici.append(str.replaceAll(REGEX, "$1\n"));
    }
    if(shici.length() > 270){
      mSTvShiciContent.setVisibility(View.GONE);
      mTvShiciContent.setVisibility(View.VISIBLE);
      mTvShiciContent.setText(shici.toString());
    } else {
      mSTvShiciContent.setVisibility(View.VISIBLE);
      mSTvShiciContent.setText(shici.toString());
      mTvShiciContent.setVisibility(View.GONE);
      try {
        InputStream is = getAssets().open("gson.txt");
        InputStreamReader isr = new InputStreamReader(is);
        DrawData data = new Gson().fromJson(isr, DrawData.class);
        data.aniType = A.SINGLE_RIGHT_FADE_INF_LEFT_FADE_OUT;
        int coclor = mTvShiciContent.getCurrentTextColor();
        data.layers.get(0).paintParam.color = String.valueOf(coclor);
        mSTvShiciContent.setData(data);
      } catch (Exception e){
        e.printStackTrace();
      }
      mSTvShiciContent.getTAnimation().start();
    }
//    StringBuilder shici = new StringBuilder();
//    for (String ciju:sb.toString().split("\n")) {
//      String pinyin = PinyinUtils.getPinyin(ciju.replaceAll(REGEX, ""));
//      shici.append(pinyin).append("\n").append(ciju).append("\n");
//    }
    Log.i("11111111111", "initData OK");
  }

  /**
   * 设置长按菜单
   */
  @RequiresApi(api = Build.VERSION_CODES.M)
  private void setActionMode() {
    mTvShiciContent.setCustomSelectionActionModeCallback(new ActionMode.Callback2() {
      @Override
      public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
      }

      @Override
      public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
      }

      @Override
      public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
      }

      @Override
      public void onDestroyActionMode(ActionMode mode) {

      }
    });
  }
}