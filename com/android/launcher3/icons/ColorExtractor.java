// 
// Decompiled by Procyon v0.5.36
// 

package com.android.launcher3.icons;

import android.graphics.Color;
import java.util.Arrays;
import android.graphics.Bitmap;
import android.util.SparseArray;

public class ColorExtractor
{
    private final float[] mTmpHsv;
    private final float[] mTmpHueScoreHistogram;
    private final int[] mTmpPixels;
    private final SparseArray<Float> mTmpRgbScores;
    
    public ColorExtractor() {
        this.mTmpHsv = new float[3];
        this.mTmpHueScoreHistogram = new float[360];
        this.mTmpPixels = new int[20];
        this.mTmpRgbScores = (SparseArray<Float>)new SparseArray();
    }
    
    public int findDominantColorByHue(final Bitmap bitmap) {
        return this.findDominantColorByHue(bitmap, 20);
    }
    
    public int findDominantColorByHue(final Bitmap bitmap, int i) {
        final int height = bitmap.getHeight();
        final int width = bitmap.getWidth();
        int n;
        if ((n = (int)Math.sqrt(height * width / i)) < 1) {
            n = 1;
        }
        final float[] mTmpHsv = this.mTmpHsv;
        Arrays.fill(mTmpHsv, 0.0f);
        final float[] mTmpHueScoreHistogram = this.mTmpHueScoreHistogram;
        Arrays.fill(mTmpHueScoreHistogram, 0.0f);
        int n2 = -1;
        final int[] mTmpPixels = this.mTmpPixels;
        Arrays.fill(mTmpPixels, 0);
        int n4;
        int n3 = n4 = 0;
        float n5 = -1.0f;
        int n6;
        while (true) {
            n6 = -16777216;
            if (n3 >= height) {
                break;
            }
            int j = 0;
            int n7 = n2;
            while (j < width) {
                final int pixel = bitmap.getPixel(j, n3);
                int n8;
                int n9;
                float n10;
                if ((pixel >> 24 & 0xFF) < 128) {
                    n8 = n7;
                    n9 = n4;
                    n10 = n5;
                }
                else {
                    final int n11 = pixel | 0xFF000000;
                    Color.colorToHSV(n11, mTmpHsv);
                    final int n12 = (int)mTmpHsv[0];
                    n8 = n7;
                    n9 = n4;
                    n10 = n5;
                    if (n12 >= 0) {
                        if (n12 >= mTmpHueScoreHistogram.length) {
                            n8 = n7;
                            n9 = n4;
                            n10 = n5;
                        }
                        else {
                            int n13;
                            if ((n13 = n4) < i) {
                                mTmpPixels[n4] = n11;
                                n13 = n4 + 1;
                            }
                            mTmpHueScoreHistogram[n12] += mTmpHsv[1] * mTmpHsv[2];
                            n8 = n7;
                            n9 = n13;
                            n10 = n5;
                            if (mTmpHueScoreHistogram[n12] > n5) {
                                n10 = mTmpHueScoreHistogram[n12];
                                n8 = n12;
                                n9 = n13;
                            }
                        }
                    }
                }
                j += n;
                n7 = n8;
                n4 = n9;
                n5 = n10;
            }
            n3 += n;
            n2 = n7;
        }
        final SparseArray<Float> mTmpRgbScores = this.mTmpRgbScores;
        mTmpRgbScores.clear();
        i = 0;
        float n14 = -1.0f;
        int n15 = n6;
        while (i < n4) {
            final int n16 = mTmpPixels[i];
            Color.colorToHSV(n16, mTmpHsv);
            float n21;
            if ((int)mTmpHsv[0] == n2) {
                final float n17 = mTmpHsv[1];
                final float n18 = mTmpHsv[2];
                final int n19 = (int)(100.0f * n17) + (int)(10000.0f * n18);
                float f = n17 * n18;
                final Float n20 = (Float)mTmpRgbScores.get(n19);
                if (n20 != null) {
                    f += n20;
                }
                mTmpRgbScores.put(n19, (Object)f);
                n21 = n14;
                if (f > n14) {
                    n15 = n16;
                    n21 = f;
                }
            }
            else {
                n21 = n14;
            }
            ++i;
            n14 = n21;
        }
        return n15;
    }
}
