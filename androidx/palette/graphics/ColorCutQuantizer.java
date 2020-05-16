// 
// Decompiled by Procyon v0.5.36
// 

package androidx.palette.graphics;

import java.util.Arrays;
import androidx.core.graphics.ColorUtils;
import java.util.PriorityQueue;
import java.util.Iterator;
import java.util.Collection;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

final class ColorCutQuantizer
{
    private static final Comparator<Vbox> VBOX_COMPARATOR_VOLUME;
    final int[] mColors;
    final Palette.Filter[] mFilters;
    final int[] mHistogram;
    final List<Palette.Swatch> mQuantizedColors;
    private final float[] mTempHsl;
    
    static {
        VBOX_COMPARATOR_VOLUME = new Comparator<Vbox>() {
            @Override
            public int compare(final Vbox vbox, final Vbox vbox2) {
                return vbox2.getVolume() - vbox.getVolume();
            }
        };
    }
    
    ColorCutQuantizer(int[] mColors, int i, final Palette.Filter[] mFilters) {
        this.mTempHsl = new float[3];
        this.mFilters = mFilters;
        final int[] mHistogram = new int[32768];
        this.mHistogram = mHistogram;
        final int n = 0;
        for (int j = 0; j < mColors.length; ++j) {
            final int quantizeFromRgb888 = quantizeFromRgb888(mColors[j]);
            mHistogram[mColors[j] = quantizeFromRgb888] = mHistogram[quantizeFromRgb888] + 1;
        }
        int n2;
        int n3;
        for (int k = n2 = 0; k < 32768; ++k, n2 = n3) {
            if (mHistogram[k] > 0 && this.shouldIgnoreColor(k)) {
                mHistogram[k] = 0;
            }
            n3 = n2;
            if (mHistogram[k] > 0) {
                n3 = n2 + 1;
            }
        }
        mColors = new int[n2];
        this.mColors = mColors;
        int n4;
        int n5;
        for (int l = n4 = 0; l < 32768; ++l, n4 = n5) {
            n5 = n4;
            if (mHistogram[l] > 0) {
                mColors[n4] = l;
                n5 = n4 + 1;
            }
        }
        if (n2 <= i) {
            this.mQuantizedColors = new ArrayList<Palette.Swatch>();
            int n6;
            for (i = n; i < n2; ++i) {
                n6 = mColors[i];
                this.mQuantizedColors.add(new Palette.Swatch(approximateToRgb888(n6), mHistogram[n6]));
            }
        }
        else {
            this.mQuantizedColors = this.quantizePixels(i);
        }
    }
    
    private static int approximateToRgb888(final int n) {
        return approximateToRgb888(quantizedRed(n), quantizedGreen(n), quantizedBlue(n));
    }
    
    static int approximateToRgb888(final int n, final int n2, final int n3) {
        return Color.rgb(modifyWordWidth(n, 5, 8), modifyWordWidth(n2, 5, 8), modifyWordWidth(n3, 5, 8));
    }
    
    private List<Palette.Swatch> generateAverageColors(final Collection<Vbox> collection) {
        final ArrayList<Palette.Swatch> list = new ArrayList<Palette.Swatch>(collection.size());
        final Iterator<Vbox> iterator = collection.iterator();
        while (iterator.hasNext()) {
            final Palette.Swatch averageColor = iterator.next().getAverageColor();
            if (!this.shouldIgnoreColor(averageColor)) {
                list.add(averageColor);
            }
        }
        return list;
    }
    
    static void modifySignificantOctet(final int[] array, int quantizedGreen, int i, final int n) {
        int j = i;
        if (quantizedGreen != -2) {
            if (quantizedGreen == -1) {
                while (i <= n) {
                    final int n2 = array[i];
                    final int quantizedBlue = quantizedBlue(n2);
                    quantizedGreen = quantizedGreen(n2);
                    array[i] = (quantizedRed(n2) | (quantizedBlue << 10 | quantizedGreen << 5));
                    ++i;
                }
            }
        }
        else {
            while (j <= n) {
                quantizedGreen = array[j];
                final int quantizedGreen2 = quantizedGreen(quantizedGreen);
                i = quantizedRed(quantizedGreen);
                array[j] = (quantizedBlue(quantizedGreen) | (quantizedGreen2 << 10 | i << 5));
                ++j;
            }
        }
    }
    
    private static int modifyWordWidth(int n, final int n2, final int n3) {
        if (n3 > n2) {
            n <<= n3 - n2;
        }
        else {
            n >>= n2 - n3;
        }
        return n & (1 << n3) - 1;
    }
    
    private static int quantizeFromRgb888(final int n) {
        return modifyWordWidth(Color.blue(n), 8, 5) | (modifyWordWidth(Color.red(n), 8, 5) << 10 | modifyWordWidth(Color.green(n), 8, 5) << 5);
    }
    
    private List<Palette.Swatch> quantizePixels(final int initialCapacity) {
        final PriorityQueue<Vbox> priorityQueue = new PriorityQueue<Vbox>(initialCapacity, ColorCutQuantizer.VBOX_COMPARATOR_VOLUME);
        priorityQueue.offer(new Vbox(0, this.mColors.length - 1));
        this.splitBoxes(priorityQueue, initialCapacity);
        return this.generateAverageColors(priorityQueue);
    }
    
    static int quantizedBlue(final int n) {
        return n & 0x1F;
    }
    
    static int quantizedGreen(final int n) {
        return n >> 5 & 0x1F;
    }
    
    static int quantizedRed(final int n) {
        return n >> 10 & 0x1F;
    }
    
    private boolean shouldIgnoreColor(int approximateToRgb888) {
        approximateToRgb888 = approximateToRgb888(approximateToRgb888);
        ColorUtils.colorToHSL(approximateToRgb888, this.mTempHsl);
        return this.shouldIgnoreColor(approximateToRgb888, this.mTempHsl);
    }
    
    private boolean shouldIgnoreColor(final int n, final float[] array) {
        final Palette.Filter[] mFilters = this.mFilters;
        if (mFilters != null && mFilters.length > 0) {
            for (int length = mFilters.length, i = 0; i < length; ++i) {
                if (!this.mFilters[i].isAllowed(n, array)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean shouldIgnoreColor(final Palette.Swatch swatch) {
        return this.shouldIgnoreColor(swatch.getRgb(), swatch.getHsl());
    }
    
    private void splitBoxes(final PriorityQueue<Vbox> priorityQueue, final int n) {
        while (priorityQueue.size() < n) {
            final Vbox e = priorityQueue.poll();
            if (e == null || !e.canSplit()) {
                break;
            }
            priorityQueue.offer(e.splitBox());
            priorityQueue.offer(e);
        }
    }
    
    List<Palette.Swatch> getQuantizedColors() {
        return this.mQuantizedColors;
    }
    
    private class Vbox
    {
        private int mLowerIndex;
        private int mMaxBlue;
        private int mMaxGreen;
        private int mMaxRed;
        private int mMinBlue;
        private int mMinGreen;
        private int mMinRed;
        private int mPopulation;
        private int mUpperIndex;
        
        Vbox(final int mLowerIndex, final int mUpperIndex) {
            this.mLowerIndex = mLowerIndex;
            this.mUpperIndex = mUpperIndex;
            this.fitBox();
        }
        
        final boolean canSplit() {
            final int colorCount = this.getColorCount();
            boolean b = true;
            if (colorCount <= 1) {
                b = false;
            }
            return b;
        }
        
        final int findSplitPoint() {
            final int longestColorDimension = this.getLongestColorDimension();
            final ColorCutQuantizer this$0 = ColorCutQuantizer.this;
            final int[] mColors = this$0.mColors;
            final int[] mHistogram = this$0.mHistogram;
            ColorCutQuantizer.modifySignificantOctet(mColors, longestColorDimension, this.mLowerIndex, this.mUpperIndex);
            Arrays.sort(mColors, this.mLowerIndex, this.mUpperIndex + 1);
            ColorCutQuantizer.modifySignificantOctet(mColors, longestColorDimension, this.mLowerIndex, this.mUpperIndex);
            final int n = this.mPopulation / 2;
            int mLowerIndex = this.mLowerIndex;
            int n2 = 0;
            while (true) {
                final int mUpperIndex = this.mUpperIndex;
                if (mLowerIndex > mUpperIndex) {
                    return this.mLowerIndex;
                }
                n2 += mHistogram[mColors[mLowerIndex]];
                if (n2 >= n) {
                    return Math.min(mUpperIndex - 1, mLowerIndex);
                }
                ++mLowerIndex;
            }
        }
        
        final void fitBox() {
            final ColorCutQuantizer this$0 = ColorCutQuantizer.this;
            final int[] mColors = this$0.mColors;
            final int[] mHistogram = this$0.mHistogram;
            final int mLowerIndex = this.mLowerIndex;
            int mMinRed = Integer.MAX_VALUE;
            int mMaxRed = Integer.MIN_VALUE;
            int mMaxBlue;
            final int n = mMaxBlue = mMaxRed;
            int mPopulation = 0;
            final int n2;
            int mMinGreen = n2 = Integer.MAX_VALUE;
            int mMaxGreen = n;
            int mMinBlue = n2;
            int n4;
            int n5;
            int n6;
            int n7;
            int n8;
            int n9;
            int n10;
            for (int i = mLowerIndex; i <= this.mUpperIndex; ++i, mMinRed = n6, mMinGreen = n8, mMinBlue = n10, mMaxRed = n5, mMaxGreen = n7, mMaxBlue = n9, mPopulation = n4) {
                final int n3 = mColors[i];
                n4 = mPopulation + mHistogram[n3];
                final int quantizedRed = ColorCutQuantizer.quantizedRed(n3);
                final int quantizedGreen = ColorCutQuantizer.quantizedGreen(n3);
                final int quantizedBlue = ColorCutQuantizer.quantizedBlue(n3);
                if (quantizedRed > (n5 = mMaxRed)) {
                    n5 = quantizedRed;
                }
                if (quantizedRed < (n6 = mMinRed)) {
                    n6 = quantizedRed;
                }
                if (quantizedGreen > (n7 = mMaxGreen)) {
                    n7 = quantizedGreen;
                }
                if (quantizedGreen < (n8 = mMinGreen)) {
                    n8 = quantizedGreen;
                }
                if (quantizedBlue > (n9 = mMaxBlue)) {
                    n9 = quantizedBlue;
                }
                if (quantizedBlue < (n10 = mMinBlue)) {
                    n10 = quantizedBlue;
                }
            }
            this.mMinRed = mMinRed;
            this.mMaxRed = mMaxRed;
            this.mMinGreen = mMinGreen;
            this.mMaxGreen = mMaxGreen;
            this.mMinBlue = mMinBlue;
            this.mMaxBlue = mMaxBlue;
            this.mPopulation = mPopulation;
        }
        
        final Palette.Swatch getAverageColor() {
            final ColorCutQuantizer this$0 = ColorCutQuantizer.this;
            final int[] mColors = this$0.mColors;
            final int[] mHistogram = this$0.mHistogram;
            int i = this.mLowerIndex;
            int n = 0;
            int n2 = 0;
            int n4;
            int n3 = n4 = n2;
            while (i <= this.mUpperIndex) {
                final int n5 = mColors[i];
                final int n6 = mHistogram[n5];
                n2 += n6;
                n += ColorCutQuantizer.quantizedRed(n5) * n6;
                n3 += ColorCutQuantizer.quantizedGreen(n5) * n6;
                n4 += n6 * ColorCutQuantizer.quantizedBlue(n5);
                ++i;
            }
            final float n7 = (float)n;
            final float n8 = (float)n2;
            return new Palette.Swatch(ColorCutQuantizer.approximateToRgb888(Math.round(n7 / n8), Math.round(n3 / n8), Math.round(n4 / n8)), n2);
        }
        
        final int getColorCount() {
            return this.mUpperIndex + 1 - this.mLowerIndex;
        }
        
        final int getLongestColorDimension() {
            final int n = this.mMaxRed - this.mMinRed;
            final int n2 = this.mMaxGreen - this.mMinGreen;
            final int n3 = this.mMaxBlue - this.mMinBlue;
            if (n >= n2 && n >= n3) {
                return -3;
            }
            if (n2 >= n && n2 >= n3) {
                return -2;
            }
            return -1;
        }
        
        final int getVolume() {
            return (this.mMaxRed - this.mMinRed + 1) * (this.mMaxGreen - this.mMinGreen + 1) * (this.mMaxBlue - this.mMinBlue + 1);
        }
        
        final Vbox splitBox() {
            if (this.canSplit()) {
                final int splitPoint = this.findSplitPoint();
                final Vbox vbox = new Vbox(splitPoint + 1, this.mUpperIndex);
                this.mUpperIndex = splitPoint;
                this.fitBox();
                return vbox;
            }
            throw new IllegalStateException("Can not split a box with only 1 color");
        }
    }
}
