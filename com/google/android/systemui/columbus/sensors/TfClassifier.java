// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import android.content.res.AssetManager;
import org.tensorflow.lite.Interpreter;

public class TfClassifier
{
    Interpreter interpreter;
    
    public TfClassifier(final AssetManager assetManager, final String s) {
        this.interpreter = null;
        try {
            final AssetFileDescriptor openFd = assetManager.openFd(s);
            this.interpreter = new Interpreter(new FileInputStream(openFd.getFileDescriptor()).getChannel().map(FileChannel.MapMode.READ_ONLY, openFd.getStartOffset(), openFd.getDeclaredLength()));
            final StringBuilder sb = new StringBuilder();
            sb.append("tflite file loaded: ");
            sb.append(s);
            Log.d("Columbus", sb.toString());
        }
        catch (Exception ex) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("load tflite file error: ");
            sb2.append(s);
            Log.d("Columbus", sb2.toString());
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("tflite file:");
            sb3.append(ex.toString());
            Log.e("Columbus", sb3.toString());
        }
    }
    
    public ArrayList<ArrayList<Float>> predict(final ArrayList<Float> list, final int n) {
        if (this.interpreter == null) {
            return new ArrayList<ArrayList<Float>>();
        }
        final float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            array[i] = list.get(i);
        }
        final HashMap<Object, float[][]> hashMap = new HashMap<Object, float[][]>();
        hashMap.put(0, new float[1][n]);
        this.interpreter.runForMultipleInputsOutputs(new Object[] { array }, (Map<Integer, Object>)hashMap);
        final float[][] array2 = hashMap.get(0);
        final ArrayList<ArrayList<Float>> list2 = new ArrayList<ArrayList<Float>>();
        final ArrayList<Float> e = new ArrayList<Float>();
        for (int j = 0; j < n; ++j) {
            e.add(array2[0][j]);
        }
        list2.add(e);
        return list2;
    }
}
