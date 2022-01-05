package com.devdroid.sleepassistant.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.devdroid.sleepassistant.R;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageActivity extends AppCompatActivity {

  final static String TAG =  ImageActivity.class.getSimpleName();
  public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "devdroid" + File.separator;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if(getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    ImageView imageView = findViewById(R.id.iv_shici_img);
    imageView.setImageBitmap(ShiciActivity.mBitmap);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case android.R.id.home:
        finish();
        break;
      case R.id.item_feedback_send:
        LinearLayout shareLayout = findViewById(R.id.ll_share_layout);
        if(shareLayout.getVisibility() == View.VISIBLE){
          shareLayout.setVisibility(View.GONE);
        } else {
          shareLayout.setVisibility(View.VISIBLE);
        }
        break;
    }
    return true;
  }

  @SuppressLint("NonConstantResourceId")
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.share_save_image:
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
          saveImage(ShiciActivity.mBitmap);
        } else {
          insertImage(ShiciActivity.mBitmap);
        }
        break;
      case R.id.share_wechat:
        shareWechat(SendMessageToWX.Req.WXSceneSession);
        break;
      case R.id.share_moment:
        shareWechat(SendMessageToWX.Req.WXSceneTimeline);
        break;
      case R.id.share_dingtalk:
        break;
    }
    LinearLayout shareLayout = findViewById(R.id.ll_share_layout);
    if(shareLayout.getVisibility() == View.VISIBLE){
      shareLayout.setVisibility(View.GONE);
    }
  }
  /**
   * 保存图片 SDK <=28
   */
  public void saveImage(Bitmap bitmap) {
    String path = BASE_PATH + System.currentTimeMillis() + ".jpg";
    File directory = new File(BASE_PATH);
    if(!directory.isDirectory()){
      boolean mkSuccess = directory.mkdirs();
      if(!mkSuccess){
        directory.mkdirs();
      }
    }
    File file = new File(path);
    if(!file.exists()){
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
      bos.flush();
      bos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  /**
   * 保存图片,SDK > 28
   */
  private void insertImage(Bitmap bitmap) {
    // 拿到 MediaStore.Images 表的uri
    Uri tableUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    // 创建图片索引
    ContentValues  value = new ContentValues();
    value.put(MediaStore.Images.Media.DISPLAY_NAME,System.currentTimeMillis() + ".jpg");
    value.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
    value.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/devdroid");
    value.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
    // 将该索引信息插入数据表，获得图片的Uri
    Uri imageUri = getContentResolver().insert(tableUri,value);
    try {
      // 通过图片uri获得输出流
      OutputStream os = getContentResolver().openOutputStream(imageUri);
      // 图片压缩保存
      bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void shareWechat(int scene) {
    IWXAPI api = WXAPIFactory.createWXAPI(this, "wx87a479e549343d9f",false);
    WXImageObject imgObj = new WXImageObject(ShiciActivity.mBitmap);
    WXMediaMessage msg = new WXMediaMessage();
    msg.mediaObject = imgObj;
    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = "shareimg";
    req.message = msg;
    req.scene = scene;
    boolean isSend =  api.sendReq(req);
    Log.d(TAG, "isSend:" + isSend);
  }
}