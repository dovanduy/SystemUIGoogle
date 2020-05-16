// 
// Decompiled by Procyon v0.5.36
// 

package androidx.palette.graphics;

import java.util.Arrays;
import androidx.core.graphics.ColorUtils;
import android.graphics.Color;
import java.util.ArrayList;
import android.graphics.Rect;
import java.util.Collections;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import java.util.List;
import androidx.collection.SimpleArrayMap;

public final class Palette
{
    static final Filter DEFAULT_FILTER;
    private final Swatch mDominantSwatch;
    private final SimpleArrayMap<Target, Swatch> mSelectedSwatches;
    private final List<Swatch> mSwatches;
    private final List<Target> mTargets;
    private final SparseBooleanArray mUsedColors;
    
    static {
        DEFAULT_FILTER = (Filter)new Filter() {
            private boolean isBlack(final float[] array) {
                return array[2] <= 0.05f;
            }
            
            private boolean isNearRedILine(final float[] array) {
                boolean b2;
                final boolean b = b2 = false;
                if (array[0] >= 10.0f) {
                    b2 = b;
                    if (array[0] <= 37.0f) {
                        b2 = b;
                        if (array[1] <= 0.82f) {
                            b2 = true;
                        }
                    }
                }
                return b2;
            }
            
            private boolean isWhite(final float[] array) {
                return array[2] >= 0.95f;
            }
            
            @Override
            public boolean isAllowed(final int n, final float[] array) {
                return !this.isWhite(array) && !this.isBlack(array) && !this.isNearRedILine(array);
            }
        };
    }
    
    Palette(final List<Swatch> mSwatches, final List<Target> mTargets) {
        this.mSwatches = mSwatches;
        this.mTargets = mTargets;
        this.mUsedColors = new SparseBooleanArray();
        this.mSelectedSwatches = new SimpleArrayMap<Target, Swatch>();
        this.mDominantSwatch = this.findDominantSwatch();
    }
    
    private Swatch findDominantSwatch() {
        final int size = this.mSwatches.size();
        int n = Integer.MIN_VALUE;
        Swatch swatch = null;
        int population;
        for (int i = 0; i < size; ++i, n = population) {
            final Swatch swatch2 = this.mSwatches.get(i);
            if (swatch2.getPopulation() > (population = n)) {
                population = swatch2.getPopulation();
                swatch = swatch2;
            }
        }
        return swatch;
    }
    
    public static Builder from(final Bitmap bitmap) {
        return new Builder(bitmap);
    }
    
    private float generateScore(final Swatch swatch, final Target target) {
        final float[] hsl = swatch.getHsl();
        final Swatch mDominantSwatch = this.mDominantSwatch;
        int population;
        if (mDominantSwatch != null) {
            population = mDominantSwatch.getPopulation();
        }
        else {
            population = 1;
        }
        final float saturationWeight = target.getSaturationWeight();
        float n = 0.0f;
        float n2;
        if (saturationWeight > 0.0f) {
            n2 = target.getSaturationWeight() * (1.0f - Math.abs(hsl[1] - target.getTargetSaturation()));
        }
        else {
            n2 = 0.0f;
        }
        float n3;
        if (target.getLightnessWeight() > 0.0f) {
            n3 = target.getLightnessWeight() * (1.0f - Math.abs(hsl[2] - target.getTargetLightness()));
        }
        else {
            n3 = 0.0f;
        }
        if (target.getPopulationWeight() > 0.0f) {
            n = target.getPopulationWeight() * (swatch.getPopulation() / (float)population);
        }
        return n2 + n3 + n;
    }
    
    private Swatch generateScoredTarget(final Target target) {
        final Swatch maxScoredSwatchForTarget = this.getMaxScoredSwatchForTarget(target);
        if (maxScoredSwatchForTarget != null && target.isExclusive()) {
            this.mUsedColors.append(maxScoredSwatchForTarget.getRgb(), true);
        }
        return maxScoredSwatchForTarget;
    }
    
    private Swatch getMaxScoredSwatchForTarget(final Target target) {
        final int size = this.mSwatches.size();
        float n = 0.0f;
        Swatch swatch = null;
        float n2;
        Swatch swatch3;
        for (int i = 0; i < size; ++i, n = n2, swatch = swatch3) {
            final Swatch swatch2 = this.mSwatches.get(i);
            n2 = n;
            swatch3 = swatch;
            if (this.shouldBeScoredForTarget(swatch2, target)) {
                final float generateScore = this.generateScore(swatch2, target);
                if (swatch != null) {
                    n2 = n;
                    swatch3 = swatch;
                    if (generateScore <= n) {
                        continue;
                    }
                }
                swatch3 = swatch2;
                n2 = generateScore;
            }
        }
        return swatch;
    }
    
    private boolean shouldBeScoredForTarget(final Swatch swatch, final Target target) {
        final float[] hsl = swatch.getHsl();
        boolean b = true;
        if (hsl[1] < target.getMinimumSaturation() || hsl[1] > target.getMaximumSaturation() || hsl[2] < target.getMinimumLightness() || hsl[2] > target.getMaximumLightness() || this.mUsedColors.get(swatch.getRgb())) {
            b = false;
        }
        return b;
    }
    
    void generate() {
        for (int size = this.mTargets.size(), i = 0; i < size; ++i) {
            final Target target = this.mTargets.get(i);
            target.normalizeWeights();
            this.mSelectedSwatches.put(target, this.generateScoredTarget(target));
        }
        this.mUsedColors.clear();
    }
    
    public Swatch getDarkMutedSwatch() {
        return this.getSwatchForTarget(Target.DARK_MUTED);
    }
    
    public Swatch getDarkVibrantSwatch() {
        return this.getSwatchForTarget(Target.DARK_VIBRANT);
    }
    
    public Swatch getDominantSwatch() {
        return this.mDominantSwatch;
    }
    
    public Swatch getLightMutedSwatch() {
        return this.getSwatchForTarget(Target.LIGHT_MUTED);
    }
    
    public Swatch getLightVibrantSwatch() {
        return this.getSwatchForTarget(Target.LIGHT_VIBRANT);
    }
    
    public Swatch getMutedSwatch() {
        return this.getSwatchForTarget(Target.MUTED);
    }
    
    public Swatch getSwatchForTarget(final Target target) {
        return this.mSelectedSwatches.get(target);
    }
    
    public List<Swatch> getSwatches() {
        return Collections.unmodifiableList((List<? extends Swatch>)this.mSwatches);
    }
    
    public Swatch getVibrantSwatch() {
        return this.getSwatchForTarget(Target.VIBRANT);
    }
    
    public static final class Builder
    {
        private final Bitmap mBitmap;
        private final List<Filter> mFilters;
        private int mMaxColors;
        private Rect mRegion;
        private int mResizeArea;
        private int mResizeMaxDimension;
        private final List<Swatch> mSwatches;
        private final List<Target> mTargets;
        
        public Builder(final Bitmap mBitmap) {
            this.mTargets = new ArrayList<Target>();
            this.mMaxColors = 16;
            this.mResizeArea = 12544;
            this.mResizeMaxDimension = -1;
            this.mFilters = new ArrayList<Filter>();
            if (mBitmap != null && !mBitmap.isRecycled()) {
                this.mFilters.add(Palette.DEFAULT_FILTER);
                this.mBitmap = mBitmap;
                this.mSwatches = null;
                this.mTargets.add(Target.LIGHT_VIBRANT);
                this.mTargets.add(Target.VIBRANT);
                this.mTargets.add(Target.DARK_VIBRANT);
                this.mTargets.add(Target.LIGHT_MUTED);
                this.mTargets.add(Target.MUTED);
                this.mTargets.add(Target.DARK_MUTED);
                return;
            }
            throw new IllegalArgumentException("Bitmap is not valid");
        }
        
        private int[] getPixelsFromBitmap(final Bitmap bitmap) {
            final int width = bitmap.getWidth();
            final int height = bitmap.getHeight();
            final int[] array = new int[width * height];
            bitmap.getPixels(array, 0, width, 0, 0, width, height);
            final Rect mRegion = this.mRegion;
            if (mRegion == null) {
                return array;
            }
            final int width2 = mRegion.width();
            final int height2 = this.mRegion.height();
            final int[] array2 = new int[width2 * height2];
            for (int i = 0; i < height2; ++i) {
                final Rect mRegion2 = this.mRegion;
                System.arraycopy(array, (mRegion2.top + i) * width + mRegion2.left, array2, i * width2, width2);
            }
            return array2;
        }
        
        private Bitmap scaleBitmapDown(final Bitmap bitmap) {
            final int mResizeArea = this.mResizeArea;
            final double n = -1.0;
            double sqrt;
            if (mResizeArea > 0) {
                final int n2 = bitmap.getWidth() * bitmap.getHeight();
                final int mResizeArea2 = this.mResizeArea;
                sqrt = n;
                if (n2 > mResizeArea2) {
                    sqrt = Math.sqrt(mResizeArea2 / (double)n2);
                }
            }
            else {
                sqrt = n;
                if (this.mResizeMaxDimension > 0) {
                    final int max = Math.max(bitmap.getWidth(), bitmap.getHeight());
                    final int mResizeMaxDimension = this.mResizeMaxDimension;
                    sqrt = n;
                    if (max > mResizeMaxDimension) {
                        sqrt = mResizeMaxDimension / (double)max;
                    }
                }
            }
            if (sqrt <= 0.0) {
                return bitmap;
            }
            return Bitmap.createScaledBitmap(bitmap, (int)Math.ceil(bitmap.getWidth() * sqrt), (int)Math.ceil(bitmap.getHeight() * sqrt), false);
        }
        
        public Builder addFilter(final Filter filter) {
            if (filter != null) {
                this.mFilters.add(filter);
            }
            return this;
        }
        
        public Builder clearFilters() {
            this.mFilters.clear();
            return this;
        }
        
        public Palette generate() {
            final Bitmap mBitmap = this.mBitmap;
            List<Swatch> list;
            if (mBitmap != null) {
                final Bitmap scaleBitmapDown = this.scaleBitmapDown(mBitmap);
                final Rect mRegion = this.mRegion;
                if (scaleBitmapDown != this.mBitmap && mRegion != null) {
                    final double n = scaleBitmapDown.getWidth() / (double)this.mBitmap.getWidth();
                    mRegion.left = (int)Math.floor(mRegion.left * n);
                    mRegion.top = (int)Math.floor(mRegion.top * n);
                    mRegion.right = Math.min((int)Math.ceil(mRegion.right * n), scaleBitmapDown.getWidth());
                    mRegion.bottom = Math.min((int)Math.ceil(mRegion.bottom * n), scaleBitmapDown.getHeight());
                }
                final int[] pixelsFromBitmap = this.getPixelsFromBitmap(scaleBitmapDown);
                final int mMaxColors = this.mMaxColors;
                Filter[] array;
                if (this.mFilters.isEmpty()) {
                    array = null;
                }
                else {
                    final List<Filter> mFilters = this.mFilters;
                    array = mFilters.toArray(new Filter[mFilters.size()]);
                }
                final ColorCutQuantizer colorCutQuantizer = new ColorCutQuantizer(pixelsFromBitmap, mMaxColors, array);
                if (scaleBitmapDown != this.mBitmap) {
                    scaleBitmapDown.recycle();
                }
                list = colorCutQuantizer.getQuantizedColors();
            }
            else {
                list = this.mSwatches;
                if (list == null) {
                    throw new AssertionError();
                }
            }
            final Palette palette = new Palette(list, this.mTargets);
            palette.generate();
            return palette;
        }
        
        public Builder maximumColorCount(final int mMaxColors) {
            this.mMaxColors = mMaxColors;
            return this;
        }
        
        public Builder resizeBitmapArea(final int mResizeArea) {
            this.mResizeArea = mResizeArea;
            this.mResizeMaxDimension = -1;
            return this;
        }
        
        public Builder setRegion(final int n, final int n2, final int n3, final int n4) {
            if (this.mBitmap != null) {
                if (this.mRegion == null) {
                    this.mRegion = new Rect();
                }
                this.mRegion.set(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
                if (!this.mRegion.intersect(n, n2, n3, n4)) {
                    throw new IllegalArgumentException("The given region must intersect with the Bitmap's dimensions.");
                }
            }
            return this;
        }
    }
    
    public interface Filter
    {
        boolean isAllowed(final int p0, final float[] p1);
    }
    
    public static final class Swatch
    {
        private final int mBlue;
        private int mBodyTextColor;
        private boolean mGeneratedTextColors;
        private final int mGreen;
        private float[] mHsl;
        private final int mPopulation;
        private final int mRed;
        private final int mRgb;
        private int mTitleTextColor;
        
        public Swatch(final int mRgb, final int mPopulation) {
            this.mRed = Color.red(mRgb);
            this.mGreen = Color.green(mRgb);
            this.mBlue = Color.blue(mRgb);
            this.mRgb = mRgb;
            this.mPopulation = mPopulation;
        }
        
        private void ensureTextColorsGenerated() {
            if (!this.mGeneratedTextColors) {
                final int calculateMinimumAlpha = ColorUtils.calculateMinimumAlpha(-1, this.mRgb, 4.5f);
                final int calculateMinimumAlpha2 = ColorUtils.calculateMinimumAlpha(-1, this.mRgb, 3.0f);
                if (calculateMinimumAlpha != -1 && calculateMinimumAlpha2 != -1) {
                    this.mBodyTextColor = ColorUtils.setAlphaComponent(-1, calculateMinimumAlpha);
                    this.mTitleTextColor = ColorUtils.setAlphaComponent(-1, calculateMinimumAlpha2);
                    this.mGeneratedTextColors = true;
                    return;
                }
                final int calculateMinimumAlpha3 = ColorUtils.calculateMinimumAlpha(-16777216, this.mRgb, 4.5f);
                final int calculateMinimumAlpha4 = ColorUtils.calculateMinimumAlpha(-16777216, this.mRgb, 3.0f);
                if (calculateMinimumAlpha3 != -1 && calculateMinimumAlpha4 != -1) {
                    this.mBodyTextColor = ColorUtils.setAlphaComponent(-16777216, calculateMinimumAlpha3);
                    this.mTitleTextColor = ColorUtils.setAlphaComponent(-16777216, calculateMinimumAlpha4);
                    this.mGeneratedTextColors = true;
                    return;
                }
                int mBodyTextColor;
                if (calculateMinimumAlpha != -1) {
                    mBodyTextColor = ColorUtils.setAlphaComponent(-1, calculateMinimumAlpha);
                }
                else {
                    mBodyTextColor = ColorUtils.setAlphaComponent(-16777216, calculateMinimumAlpha3);
                }
                this.mBodyTextColor = mBodyTextColor;
                int mTitleTextColor;
                if (calculateMinimumAlpha2 != -1) {
                    mTitleTextColor = ColorUtils.setAlphaComponent(-1, calculateMinimumAlpha2);
                }
                else {
                    mTitleTextColor = ColorUtils.setAlphaComponent(-16777216, calculateMinimumAlpha4);
                }
                this.mTitleTextColor = mTitleTextColor;
                this.mGeneratedTextColors = true;
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b = true;
            if (this == o) {
                return true;
            }
            if (o != null && Swatch.class == o.getClass()) {
                final Swatch swatch = (Swatch)o;
                if (this.mPopulation != swatch.mPopulation || this.mRgb != swatch.mRgb) {
                    b = false;
                }
                return b;
            }
            return false;
        }
        
        public int getBodyTextColor() {
            this.ensureTextColorsGenerated();
            return this.mBodyTextColor;
        }
        
        public float[] getHsl() {
            if (this.mHsl == null) {
                this.mHsl = new float[3];
            }
            ColorUtils.RGBToHSL(this.mRed, this.mGreen, this.mBlue, this.mHsl);
            return this.mHsl;
        }
        
        public int getPopulation() {
            return this.mPopulation;
        }
        
        public int getRgb() {
            return this.mRgb;
        }
        
        public int getTitleTextColor() {
            this.ensureTextColorsGenerated();
            return this.mTitleTextColor;
        }
        
        @Override
        public int hashCode() {
            return this.mRgb * 31 + this.mPopulation;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(Swatch.class.getSimpleName());
            sb.append(" [RGB: #");
            sb.append(Integer.toHexString(this.getRgb()));
            sb.append(']');
            sb.append(" [HSL: ");
            sb.append(Arrays.toString(this.getHsl()));
            sb.append(']');
            sb.append(" [Population: ");
            sb.append(this.mPopulation);
            sb.append(']');
            sb.append(" [Title Text: #");
            sb.append(Integer.toHexString(this.getTitleTextColor()));
            sb.append(']');
            sb.append(" [Body Text: #");
            sb.append(Integer.toHexString(this.getBodyTextColor()));
            sb.append(']');
            return sb.toString();
        }
    }
}
