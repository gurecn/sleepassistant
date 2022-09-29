package com.devdroid.speech.tensorflow.module;

import android.annotation.SuppressLint;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author {@link "mailto:xuefeng.ding@outlook.com" "Xuefeng Ding"}
 * Created 2020-07-20 17:26
 */
public class FastSpeech2 extends AbstractModule {
    private static final String TAG = "FastSpeech2";
    private Interpreter mModule;

    public FastSpeech2(String modulePath) {
        try {
            mModule = new Interpreter(new File(modulePath), getOption());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TensorBuffer getMelSpectrogram(int[] inputIds, float speed) {
        mModule.resizeInput(0, new int[]{1, inputIds.length});
        mModule.allocateTensors();
        @SuppressLint("UseSparseArrays")
        Map<Integer, Object> outputMap = new HashMap<>();

        FloatBuffer outputBuffer = FloatBuffer.allocate(350000);
        outputMap.put(0, outputBuffer);

        int[][] inputs = new int[1][inputIds.length];
        inputs[0] = inputIds;
        mModule.runForMultipleInputsOutputs(
                new Object[]{inputs, new int[]{0}, new float[]{speed}, new float[]{1F}, new float[]{1F}},
                outputMap);
        int size = mModule.getOutputTensor(0).shape()[2];
        int[] shape = {1, outputBuffer.position() / size, size};
        TensorBuffer spectrogram = TensorBuffer.createFixedSize(shape, DataType.FLOAT32);
        float[] outputArray = new float[outputBuffer.position()];
        outputBuffer.rewind();
        outputBuffer.get(outputArray);
        spectrogram.loadArray(outputArray);
        return spectrogram;
    }
}
