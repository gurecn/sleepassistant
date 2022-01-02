package com.devdroid.sleepassistant.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.devdroid.sleepassistant.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
    Intent intent = getIntent();
    Bitmap bitmap = intent.getParcelableExtra("img");
    ImageView imageView = findViewById(R.id.iv_shici_img);
    imageView.setImageBitmap(ShiciActivity.mBitmap);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
      save(ShiciActivity.mBitmap);
    } else {
      insertImage(ShiciActivity.mBitmap);
    }
  }
  /**
   * 保存图片 SDK <=28
   */
  public void save(Bitmap bitmap) {
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
        boolean createNewFile = file.createNewFile();
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
    MediaStore.
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
}