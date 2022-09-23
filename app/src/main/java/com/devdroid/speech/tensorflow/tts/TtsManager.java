package com.devdroid.speech.tensorflow.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.devdroid.speech.tensorflow.dispatcher.OnTtsStateListener;
import com.devdroid.speech.tensorflow.dispatcher.TtsStateDispatcher;
import com.devdroid.speech.tensorflow.utils.ThreadPoolManager;
import com.devdroid.sleepassistant.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @author {@link "mailto:xuefeng.ding@outlook.com" "Xuefeng Ding"}
 * Created 2020-07-28 14:25
 */
public class TtsManager {
    private static final String TAG = "TtsManager";

    private static final Object INSTANCE_WRITE_LOCK = new Object();

    private static volatile TtsManager instance;
    private OnTtsStateListener mTtsStateListener;
    /**
     *  当前状态：
     *  0：默认状态
     *  1：就绪状态
     *  2：输出语音状态
     */
    private static int state = 0;


    /**
     *  播放速度
     */
    private static float speed = 1f;

    public static TtsManager getInstance() {
        if (instance == null) {
            synchronized (INSTANCE_WRITE_LOCK) {
                if (instance == null) {
                    instance = new TtsManager();
                }
            }
        }
        return instance;
    }

    private InputWorker mWorker;

    private final static String FASTSPEECH2_MODULE = "tensorflowtts/fastspeech2_quan.tflite";
    private final static String MELGAN_MODULE = "tensorflowtts/mb_melgan_new.tflite";

    public void init(Context context, TextToSpeech.OnInitListener onInitListener) {
        if(state == 1 || state == 2){
            onInitListener.onInit(TextToSpeech.SUCCESS);
            return;
        }
        if(mTtsStateListener == null) {
            mTtsStateListener = new OnTtsStateListener() {
                @Override
                public void onTtsReady() {
                    state = 1;
                    onInitListener.onInit(TextToSpeech.SUCCESS);
                    Logger.d(TAG, "onTtsReady");
                }

                @Override
                public void onTtsStart(String text) {
                    state = 2;
                    Logger.d(TAG, "onTtsStart");
                }

                @Override
                public void onTtsStop() {
                    state = 1;
                    Logger.d(TAG, "onTtsStop");
                }
            };
        }
        TtsStateDispatcher.getInstance().addListener(mTtsStateListener);

        ThreadPoolManager.getInstance().getSingleExecutor("init").execute(() -> {
            try {
                String fastspeech = copyFile(context, FASTSPEECH2_MODULE);
                String vocoder = copyFile(context, MELGAN_MODULE);
                mWorker = new InputWorker(context,fastspeech, vocoder);
            } catch (Exception e) {
                Log.e(TAG, "mWorker init failed", e);
            }

            TtsStateDispatcher.getInstance().onTtsReady();
        });
    }

    private String copyFile(Context context, String strOutFileName) {
        Log.d(TAG, "start copy file " + strOutFileName);
        File file = context.getFilesDir();

        String tmpFile = file.getAbsolutePath() + "/" + strOutFileName;
        File f = new File(tmpFile);
        if (f.exists()) {
            Log.d(TAG, "file exists " + strOutFileName);
            return f.getAbsolutePath();
        } else {
            Objects.requireNonNull(f.getParentFile()).mkdirs();
        }

        try (OutputStream myOutput = new FileOutputStream(f);
             InputStream myInput = context.getAssets().open(strOutFileName)) {
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            myOutput.flush();
            Log.d(TAG, "Copy task successful");
        } catch (Exception e) {
            Log.e(TAG, "copyFile: Failed to copy", e);
        } finally {
            Log.d(TAG, "end copy file " + strOutFileName);
        }
        return f.getAbsolutePath();
    }

    public void stopTts() {
        if(mWorker != null) {
            mWorker.interrupt();
        }
    }

    public void speak(String inputText, float speed, boolean interrupt) {
        if (interrupt) {
            stopTts();
        }

        ThreadPoolManager.getInstance().execute(() ->
                mWorker.processInput(inputText, speed));
    }

    public void speak(String inputText) {
        speak(inputText, speed, true);
    }

    public boolean isSpeaking() {
        return state == 2;
    }

    public static void setSpeed(float speed) {
        TtsManager.speed = speed;
    }
}
