// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.glwallpaper;

import java.util.function.Consumer;
import android.util.Log;
import android.os.AsyncTask;
import android.graphics.Matrix;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.ColorMatrix;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.os.Message;
import android.os.Handler$Callback;
import android.os.Handler;

class ImageProcessHelper
{
    private static final float[] LUMINOSITY_MATRIX;
    private static final String TAG = "ImageProcessHelper";
    private final Handler mHandler;
    private float mThreshold;
    
    static {
        LUMINOSITY_MATRIX = new float[] { 0.2126f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.7152f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0722f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f };
    }
    
    ImageProcessHelper() {
        this.mHandler = new Handler((Handler$Callback)new Handler$Callback() {
            public boolean handleMessage(final Message message) {
                if (message.what != 1) {
                    return false;
                }
                ImageProcessHelper.this.mThreshold = (float)message.obj;
                return true;
            }
        });
        this.mThreshold = 0.8f;
    }
    
    float getThreshold() {
        return Math.min(this.mThreshold, 0.89f);
    }
    
    void start(final ImageWallpaperRenderer.WallpaperTexture wallpaperTexture) {
        new ThresholdComputeTask(this.mHandler).execute((Object[])new ImageWallpaperRenderer.WallpaperTexture[] { wallpaperTexture });
    }
    
    private static class Otsus implements ThresholdAlgorithm
    {
        @Override
        public float compute(final Bitmap bitmap, final int[] array) {
            final float n = (float)(bitmap.getWidth() * bitmap.getHeight());
            final float[] array2 = new float[2];
            final float[] array3 = new float[2];
            final float[] array4 = new float[2];
            for (int i = 0; i < array.length; ++i) {
                array3[1] += array[i] * i;
            }
            array2[1] = n;
            float n3;
            float n2 = n3 = 0.0f;
            float n7;
            float n8;
            for (int j = 0; j < array.length; ++j, n2 = n7, n3 = n8) {
                final float n4 = (float)array[j];
                final float n5 = (float)j;
                final float n6 = n4 * n5;
                array2[0] += n4;
                array2[1] -= n4;
                n7 = n2;
                n8 = n3;
                if (array2[0] != 0.0f) {
                    if (array2[1] == 0.0f) {
                        n7 = n2;
                        n8 = n3;
                    }
                    else {
                        array3[0] += n6;
                        array3[1] -= n6;
                        array4[0] = array3[0] / array2[0];
                        array4[1] = array3[1] / array2[1];
                        final float n9 = array4[0] - array4[1];
                        final float n10 = array2[0] * array2[1] * n9 * n9;
                        n7 = n2;
                        n8 = n3;
                        if (n10 > n3) {
                            n7 = (n5 + 1.0f) / array.length;
                            n8 = n10;
                        }
                    }
                }
            }
            return n2;
        }
    }
    
    private static class Percentile85 implements ThresholdAlgorithm
    {
        @Override
        public float compute(final Bitmap bitmap, final int[] array) {
            final int width = bitmap.getWidth();
            final int height = bitmap.getHeight();
            final float[] array2 = new float[256];
            float n = 0.8f;
            int n4;
            float n6;
            for (int i = 0; i < 256; i = n4, n = n6) {
                array2[i] = array[i] / (float)(width * height);
                float n2;
                if (i == 0) {
                    n2 = 0.0f;
                }
                else {
                    n2 = array2[i - 1];
                }
                final float n3 = array2[i];
                n4 = i + 1;
                final float n5 = n4 / 255.0f;
                n6 = n;
                if (n2 < 0.85f) {
                    n6 = n;
                    if (n3 + n2 >= 0.85f) {
                        n6 = n5;
                    }
                }
                if (i > 0) {
                    array2[i] += array2[i - 1];
                }
            }
            return n;
        }
    }
    
    private static class Threshold
    {
        private int[] getHistogram(final Bitmap bitmap) {
            final int width = bitmap.getWidth();
            final int height = bitmap.getHeight();
            final int[] array = new int[256];
            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width; ++j) {
                    final int pixel = bitmap.getPixel(j, i);
                    final int n = Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
                    ++array[n];
                }
            }
            return array;
        }
        
        private boolean isSolidColor(final Bitmap bitmap, final int[] array) {
            final int n = bitmap.getWidth() * bitmap.getHeight();
            final int length = array.length;
            final boolean b = false;
            int n2 = 0;
            boolean b2;
            while (true) {
                b2 = b;
                if (n2 >= length) {
                    break;
                }
                final int n3 = array[n2];
                if (n3 != 0 && n3 != n) {
                    b2 = b;
                    break;
                }
                if (n3 == n) {
                    b2 = true;
                    break;
                }
                ++n2;
            }
            return b2;
        }
        
        private Bitmap toGrayscale(final Bitmap bitmap) {
            final Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig(), false, bitmap.getColorSpace());
            final Canvas canvas = new Canvas(bitmap2);
            final ColorMatrix colorMatrix = new ColorMatrix(ImageProcessHelper.LUMINOSITY_MATRIX);
            final Paint paint = new Paint();
            paint.setColorFilter((ColorFilter)new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(bitmap, new Matrix(), paint);
            return bitmap2;
        }
        
        public float compute(final Bitmap bitmap) {
            final Bitmap grayscale = this.toGrayscale(bitmap);
            final int[] histogram = this.getHistogram(grayscale);
            ThresholdAlgorithm thresholdAlgorithm;
            if (this.isSolidColor(grayscale, histogram)) {
                thresholdAlgorithm = new Percentile85();
            }
            else {
                thresholdAlgorithm = new Otsus();
            }
            return thresholdAlgorithm.compute(grayscale, histogram);
        }
    }
    
    private interface ThresholdAlgorithm
    {
        float compute(final Bitmap p0, final int[] p1);
    }
    
    private static class ThresholdComputeTask extends AsyncTask<ImageWallpaperRenderer.WallpaperTexture, Void, Float>
    {
        private Handler mUpdateHandler;
        
        ThresholdComputeTask(final Handler mUpdateHandler) {
            super(mUpdateHandler);
            this.mUpdateHandler = mUpdateHandler;
        }
        
        protected Float doInBackground(final ImageWallpaperRenderer.WallpaperTexture... array) {
            final ImageWallpaperRenderer.WallpaperTexture wallpaperTexture = array[0];
            final float[] array2 = { 0.8f };
            if (wallpaperTexture == null) {
                Log.e(ImageProcessHelper.TAG, "ThresholdComputeTask: WallpaperTexture not initialized");
                return array2[0];
            }
            wallpaperTexture.use(new _$$Lambda$ImageProcessHelper$ThresholdComputeTask$qb7fHBxF91jySQCwrCi0i_Xrqpo(array2));
            return array2[0];
        }
        
        protected void onPostExecute(final Float n) {
            this.mUpdateHandler.sendMessage(this.mUpdateHandler.obtainMessage(1, (Object)n));
        }
    }
}
