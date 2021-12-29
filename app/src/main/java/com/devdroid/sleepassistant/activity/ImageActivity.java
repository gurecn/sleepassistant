package com.devdroid.sleepassistant.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
    Log.d("11111111111", "onCreate");
    Intent intent = getIntent();
    Bitmap bitmap = intent.getParcelableExtra("img");
    ImageView imageView = findViewById(R.id.iv_shici_img);
    imageView.setImageBitmap(ShiciActivity.mBitmap);
    save(ShiciActivity.mBitmap);
    Log.d("11111111111", "onCreate finish");
  }
  /**
   * 保存图片
   */
  public void save(Bitmap bitmap) {
    String path = BASE_PATH + System.currentTimeMillis() + ".jpg";
    Log.d(TAG, "path：" + path);
    File directory = new File(BASE_PATH);
    if(!directory.isDirectory()){
      boolean mkSuccess = directory.mkdirs();
      Log.d(TAG, "mkdirs：" + mkSuccess);
      if(!mkSuccess){
        directory.mkdirs();
      }
    }
    File file = new File(path);
    Log.d(TAG, "exists：" + file.exists());
    if(!file.exists()){
      try {
        boolean createNewFile = file.createNewFile();
        Log.d(TAG, "createNewFile：" + createNewFile);
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
   * @return 质量压缩图片
   */
  public static Bitmap compressImage(Bitmap image) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    int options = 80;
    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
    while (baos.toByteArray().length / 1024 > 100) {
      //重置baos即清空baos
      baos.reset();
      //这里压缩options%，把压缩后的数据存放到baos中
      image.compress(Bitmap.CompressFormat.JPEG, options, baos);
      //每次都减少10
      options -= 10;
    }
    //把压缩后的数据baos存放到ByteArrayInputStream中
    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
    //把ByteArrayInputStream数据生成图片
    return BitmapFactory.decodeStream(isBm, null, null);
  }

}