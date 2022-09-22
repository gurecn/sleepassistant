package com.devdroid.sleepassistant.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
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
import android.widget.Toast;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.speech.SpeechManager;
import com.devdroid.sleepassistant.speech.tensorflow.dispatcher.OnTtsStateListener;
import com.devdroid.sleepassistant.speech.tensorflow.dispatcher.TtsStateDispatcher;
import com.devdroid.sleepassistant.speech.tensorflow.tts.TtsManager;
import com.devdroid.sleepassistant.speech.tensorflow.utils.ThreadPoolManager;
import com.devdroid.sleepassistant.freefont.core.animation.A;
import com.devdroid.sleepassistant.freefont.core.data.DrawData;
import com.devdroid.sleepassistant.freefont.core.view.STextView;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.devdroid.sleepassistant.utils.Logger;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.jinrishici.sdk.android.model.OriginBean;
import com.devdroid.sleepassistant.speech.local.Speech;
import com.devdroid.sleepassistant.speech.local.TextToSpeechCallback;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

public class ShiciActivity extends BaseActivity implements View.OnClickListener {
  public static final String REGEX = "(:|：|，|,|\\.|。|;|；|\\?|？|！|!)";
  private OriginBean mOriginBean;
  private TextView mTvShiciTitle;
  private TextView mTvShiciDynasty;
  private TextView mTvShiciAuthor;
  private STextView mSTvShiciContent;
  private TextView mTvShiciContent;
  private View mContentLayout;
  private View mTitleLayout;
  public static Bitmap mBitmap;
  private ImageView mIvShiciBg;
  private int showType = 0;  //0 诗词；1 注释
  private boolean hasAnimation = true;
  private boolean hasPinyin = false;
  private static int PHOTO_PICKER_REQUEST_CODE = 110;
  private static final String LOG_TAG = ShiciActivity.class.getSimpleName();

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
    mContentLayout = findViewById(R.id.rl_content_layout);
    mTitleLayout = findViewById(R.id.ll_title_layout);
    mTvShiciTitle = findViewById(R.id.tv_shici_title);
    mTvShiciDynasty = findViewById(R.id.tv_shici_dynasty);
    mTvShiciAuthor = findViewById(R.id.tv_shici_author);
    mSTvShiciContent = findViewById(R.id.stv_shici_content);
    mSTvShiciContent.setMovementMethod(new ScrollingMovementMethod());
    mTvShiciContent = findViewById(R.id.tv_shici_content);
    mTvShiciContent.setMovementMethod(new ScrollingMovementMethod());
    mIvShiciBg = findViewById(R.id.in_shici_bg);
    setMagnifier(mTvShiciTitle);
    Random r1 = new Random();
    int[] resources = new int[]{R.drawable.shici_bg_0,R.drawable.shici_bg_1,R.drawable.shici_bg_2};
    mIvShiciBg.setImageResource(resources[r1.nextInt(9)%3]);

//    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
//      View btnShiciBg = findViewById(R.id.btn_shici_background);
//      btnShiciBg.setVisibility(View.GONE);
//    }
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
    List<String> translates = mOriginBean.getTranslate();
    if(translates == null || translates.size() <= 0) {
      findViewById(R.id.btn_shici_translate).setEnabled(false);
    }
    StringBuilder shici = new StringBuilder();
    if(showType == 0) {
      StringBuilder sc = new StringBuilder();
      for (String str : mOriginBean.getContent()) {
        sc.append(str.replaceAll(REGEX, "$1\n"));
      }
      for (String ciju : sc.toString().split("\n")) {
        String pinyin = getAllPinyin(ciju);
        if (hasPinyin) {
          shici.append(pinyin).append("\n");
        }
        shici.append(ciju).append("\n");
      }
    } else {
      if(translates != null && translates.size() > 0) {
        for (String str : mOriginBean.getTranslate()) {
          shici.append(str.replaceAll(REGEX, "$1\n"));
        }
      }
    }
    if(!hasAnimation || shici.length() > 270){
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

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.btn_shici_share:
        mBitmap = getShiciImage(mSTvShiciContent.getVisibility() == View.VISIBLE?mSTvShiciContent:mTvShiciContent);
        Intent intent = new Intent(ShiciActivity.this, ImageActivity.class);
        startActivity(intent);
        break;
      case R.id.btn_shici_content:
        showType = 0;
        v.setBackgroundResource(R.drawable.icon_shici_press);
        findViewById(R.id.btn_shici_translate).setBackgroundResource(R.drawable.icon_shici);
        initData();
        break;
      case R.id.btn_shici_translate:
        showType = 1;
        v.setBackgroundResource(R.drawable.icon_shici_press);
        findViewById(R.id.btn_shici_content).setBackgroundResource(R.drawable.icon_shici);
        findViewById(R.id.btn_shici_pinyin).setBackgroundResource(R.drawable.icon_shici);
        hasPinyin = false;
        initData();
        break;
      case R.id.btn_shici_pinyin:
        if(showType == 0) {
          hasPinyin = !hasPinyin;
          v.setBackgroundResource(hasPinyin ? R.drawable.icon_shici_press : R.drawable.icon_shici);
          initData();
        } else {
          Toast.makeText(this,"注解不支持注音", Toast.LENGTH_SHORT).show();
        }
        break;
      case R.id.btn_shici_animation:
        hasAnimation = !hasAnimation;
        v.setBackgroundResource(hasAnimation?R.drawable.icon_shici_press:R.drawable.icon_shici);
        initData();
        break;
      case R.id.btn_shici_background:
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
          intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE);
        } else {
          intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
          startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE);
        }
        break;
      case R.id.btn_shici_speech:

        if(!SpeechManager.getInstance().isSpeaking()) {
          SpeechManager.getInstance().init(this, 0, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
              if (status == TextToSpeech.SUCCESS) {
                String text = mTvShiciContent.getText().toString().trim();
                if (text.isEmpty()) {
                  text = mSTvShiciContent.getText().toString().trim();
                }
                if (text.isEmpty()) {
                  text = "无内容";
                }
                SpeechManager.getInstance().setSpeechRate(1.0f);
                SpeechManager.getInstance().say(text);
              }
            }
          });
        }
        break;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) {
      return;
    }
    if(requestCode == PHOTO_PICKER_REQUEST_CODE){
      Uri currentUri = data.getData();
      ContentResolver cr = this.getContentResolver();
      try {
        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(currentUri));
        mIvShiciBg.setImageBitmap(bitmap);
      } catch (FileNotFoundException e) {
        Log.e("Exception", e.getMessage(),e);
      }
    }
  }

  private Bitmap getShiciImage(TextView contentView) {
    int[] location = new int[2];
    contentView.getLocationOnScreen(location);
    int x = location[0]; // view距离 屏幕左边的距离（即x轴方向）
    int y = location[1]; // view距离 屏幕顶边的距离（即y轴方向）
    int contentWidth = contentView.getWidth();
    int contentLayoutWidth = mContentLayout.getWidth();
    mTitleLayout.getLocationOnScreen(location);
    int titleX = location[0]; // view距离 屏幕左边的距离（即x轴方向）
    int titleY = location[1]; // view距离 屏幕顶边的距离（即y轴方向）
    TextPaint contentPaint = contentView.getPaint();
    StaticLayout staticLayout = new StaticLayout(contentView.getText(), contentPaint,contentWidth , Layout.Alignment.ALIGN_CENTER, 1.2F, 0, false);
    int heightContent = staticLayout.getHeight();
    Bitmap.Config config = Bitmap.Config.ARGB_8888;
    Bitmap bitmapAll;
    try {
      bitmapAll = Bitmap.createBitmap(contentLayoutWidth, y + heightContent, config);
    } catch (Exception e) {
      e.printStackTrace();
      config = Bitmap.Config.RGB_565;
      bitmapAll = Bitmap.createBitmap(contentLayoutWidth, y + heightContent, config);
    }
    Canvas canvas = new Canvas(bitmapAll);
    canvas.drawColor(Color.WHITE);
    Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setDither(true);
    paint.setFilterBitmap(true);
    canvas.drawBitmap(getImageViewBitmap(mIvShiciBg, mIvShiciBg.getWidth(), y + heightContent), 0, 0, paint);
    Bitmap bitmap = drawableToBitmap(getResources().getDrawable(R.drawable.aishici));
    canvas.drawBitmap(bitmap,(int)((contentLayoutWidth - bitmap.getWidth())/2), titleY- bitmap.getHeight() * 3, paint);

    canvas.translate(titleX, titleY);
    mTitleLayout.draw(canvas);
    canvas.translate(x - titleX, y - titleY);
    staticLayout.draw(canvas);
    canvas.save();
    return bitmapAll;
  }


  static Bitmap drawableToBitmap(Drawable drawable){ // drawable 转换成bitmap
    int width = drawable.getIntrinsicWidth();// 取drawable的长宽
    int height = drawable.getIntrinsicHeight();
    Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;// 取drawable的颜色格式
    Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
    Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
    drawable.setBounds(0, 0, width, height);
    drawable.draw(canvas);// 把drawable内容画到画布中
    return bitmap;
  }

  /**
   * 从View抓获取Bitmap
   * @param view 获取Bitmap的view
   * @param w Bitmap宽度
   * @param h Bitmap高
   */
  private Bitmap getImageViewBitmap(View view, int w, int h) {
    Bitmap originBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(originBitmap);
    view.draw(canvas);
    return resizeImage(originBitmap, w, h);
  }

  /**
   * 缩放图片
   * @param origin 原始图片
   * @param newWidth  最终图片的宽度
   * @param newHeight 最终图片的高度
   */
  public static Bitmap resizeImage(Bitmap origin, int newWidth, int newHeight) {
    if (origin == null) return null;
    int height = origin.getHeight();
    int width = origin.getWidth();
    float scaleWidth = ((float) newWidth) / width;
    float scaleHeight = ((float) newHeight) / height;
    Matrix matrix = new Matrix();
    matrix.postScale(scaleWidth, scaleHeight);
    Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    if (!origin.isRecycled()) {
      origin.recycle();
    }
    return newBM;
  }

  private String getAllPinyin(String hanzi){
    HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
    format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
    format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
    StringBuilder sb = new StringBuilder();
    try {
      for(char cha:hanzi.toCharArray()){
        if(Character.toString(cha).matches("[\\u4E00-\\u9FA5]+")) {
          String[] pys = PinyinHelper.toHanyuPinyinStringArray(cha, format);
          sb.append(pys[0]).append(" ");
        }
      }
      sb.deleteCharAt(sb.length()-1);
    } catch (Exception exception){
      exception.printStackTrace();
    }
    return sb.toString();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Speech.getInstance().shutdown();
  }


}