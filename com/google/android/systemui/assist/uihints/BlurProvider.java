// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.renderscript.Allocation$MipmapControl;
import android.renderscript.Element;
import android.renderscript.Allocation;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.RenderScript;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.MathUtils;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

public final class BlurProvider
{
    private final BlurKernel mBlurKernel;
    private Bitmap mBuffer;
    private final SourceDownsampler mImageSource;
    private final Resources mResources;
    
    public BlurProvider(final Context context, final Drawable drawable) {
        this.mResources = context.getResources();
        this.mImageSource = new SourceDownsampler(drawable);
        this.mBlurKernel = new BlurKernel(context);
    }
    
    private BlurResult blur(float constrain, final Bitmap bitmap, final Bitmap bitmap2) {
        constrain = MathUtils.constrain(constrain, 0.0f, 1000.0f);
        if (constrain <= 1.0f) {
            return new BlurResult(this.mImageSource.getDrawable());
        }
        final int numDownsampleStepsForEffectiveRadius = getNumDownsampleStepsForEffectiveRadius(constrain);
        this.mImageSource.rasterize(bitmap, numDownsampleStepsForEffectiveRadius);
        this.mBlurKernel.blur(bitmap, bitmap2, constrain * SourceDownsampler.getDownsampleScaleFactor(numDownsampleStepsForEffectiveRadius));
        return new BlurResult((Drawable)new BitmapDrawable(this.mResources, bitmap2), getBitmapVisibleRegion(bitmap2, this.mImageSource.getDownsampledWidth(numDownsampleStepsForEffectiveRadius), this.mImageSource.getDownsampledHeight(numDownsampleStepsForEffectiveRadius)));
    }
    
    private static RectF getBitmapVisibleRegion(final Bitmap bitmap, int n, final int n2) {
        final int n3 = 0;
        int i = 0;
    Label_0049:
        while (true) {
            while (i < n) {
                for (int j = 0; j < n2; ++j) {
                    if (bitmap.getPixel(i, j) != 0) {
                        break Label_0049;
                    }
                }
                ++i;
                continue;
                int n4 = 0;
                int n5 = 0;
            Label_0101:
                while (true) {
                    n5 = n3;
                    if (n4 >= n2) {
                        break;
                    }
                    for (int k = i; k < n; ++k) {
                        if (bitmap.getPixel(k, n4) != 0) {
                            n5 = n4;
                            break Label_0101;
                        }
                    }
                    ++n4;
                }
                int n6 = n - 1;
                int n7 = 0;
            Label_0153:
                while (true) {
                    n7 = n;
                    if (n6 < i) {
                        break;
                    }
                    for (int l = n2 - 1; l >= n5; --l) {
                        if (bitmap.getPixel(n6, l) != 0) {
                            n7 = n6;
                            break Label_0153;
                        }
                    }
                    --n6;
                }
                n = n2 - 1;
                int n8 = 0;
            Label_0206:
                while (true) {
                    n8 = n2;
                    if (n < n5) {
                        break;
                    }
                    for (int n9 = n7 - 1; n9 >= i; --n9) {
                        if (bitmap.getPixel(n9, n) != 0) {
                            n8 = n;
                            break Label_0206;
                        }
                    }
                    --n;
                }
                return new RectF((float)i, (float)n5, (float)n7, (float)n8);
            }
            i = 0;
            continue Label_0049;
        }
    }
    
    private static int getNumDownsampleStepsForEffectiveRadius(final float n) {
        int n2 = 0;
        for (int n3 = 25; n3 < n; n3 *= 2) {
            ++n2;
        }
        return n2;
    }
    
    public void clearCaches() {
        this.mBuffer = null;
        this.mBlurKernel.clearCaches();
    }
    
    public BlurResult get(final int n) {
        if (this.mBuffer == null) {
            this.mBuffer = this.mImageSource.createBitmapBuffer(0);
        }
        final float n2 = (float)n;
        final Bitmap mBuffer = this.mBuffer;
        return this.blur(n2, mBuffer, mBuffer);
    }
    
    private static class BlurKernel
    {
        private final RenderScript mBlurRenderScript;
        private final ScriptIntrinsicBlur mBlurScript;
        private Allocation mLastInputAllocation;
        private Bitmap mLastInputBitmap;
        private Allocation mLastOutputAllocation;
        private Bitmap mLastOutputBitmap;
        
        public BlurKernel(final Context context) {
            final RenderScript create = RenderScript.create(context);
            this.mBlurRenderScript = create;
            this.mBlurScript = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
        }
        
        private static boolean canShareAllocations(final Bitmap bitmap, final Bitmap bitmap2) {
            boolean b2;
            final boolean b = b2 = false;
            if (bitmap != null) {
                if (bitmap2 == null) {
                    b2 = b;
                }
                else {
                    b2 = b;
                    if (bitmap.getWidth() == bitmap2.getWidth()) {
                        b2 = b;
                        if (bitmap.getHeight() == bitmap2.getHeight()) {
                            b2 = true;
                        }
                    }
                }
            }
            return b2;
        }
        
        private Allocation createAllocationForBitmap(final Bitmap bitmap) {
            return Allocation.createFromBitmap(this.mBlurRenderScript, bitmap, Allocation$MipmapControl.MIPMAP_NONE, 1);
        }
        
        private void prepareInputAllocation(Bitmap mLastInputBitmap) {
            final boolean canShareAllocations = canShareAllocations(this.mLastInputBitmap, mLastInputBitmap);
            this.mLastInputBitmap = mLastInputBitmap;
            if (canShareAllocations) {
                this.mLastInputAllocation.copyFrom(mLastInputBitmap);
                return;
            }
            final Allocation mLastInputAllocation = this.mLastInputAllocation;
            if (mLastInputAllocation != null) {
                mLastInputAllocation.destroy();
                this.mLastInputAllocation = null;
            }
            mLastInputBitmap = this.mLastInputBitmap;
            if (mLastInputBitmap != null) {
                this.mLastInputAllocation = this.createAllocationForBitmap(mLastInputBitmap);
            }
        }
        
        private void prepareOutputAllocation(final Bitmap mLastOutputBitmap) {
            if (this.mLastOutputAllocation != null && !canShareAllocations(this.mLastOutputBitmap, mLastOutputBitmap)) {
                this.mLastOutputAllocation.destroy();
                this.mLastOutputAllocation = null;
            }
            if ((this.mLastOutputBitmap = mLastOutputBitmap) != null && this.mLastOutputAllocation == null) {
                this.mLastOutputAllocation = this.createAllocationForBitmap(mLastOutputBitmap);
            }
        }
        
        public void blur(final Bitmap bitmap, final Bitmap bitmap2, float constrain) {
            this.prepareInputAllocation(bitmap);
            this.prepareOutputAllocation(bitmap2);
            constrain = MathUtils.constrain(constrain, 0.0f, 25.0f);
            this.mBlurScript.setRadius(constrain);
            this.mBlurScript.setInput(this.mLastInputAllocation);
            this.mBlurScript.forEach(this.mLastOutputAllocation);
            this.mLastOutputAllocation.copyTo(bitmap2);
        }
        
        public void clearCaches() {
            this.prepareInputAllocation(null);
            this.prepareOutputAllocation(null);
        }
    }
    
    public class BlurResult
    {
        public final RectF cropRegion;
        public final Drawable drawable;
        
        BlurResult(final BlurProvider blurProvider, final Drawable drawable) {
            this(blurProvider, drawable, new RectF(0.0f, 0.0f, (float)drawable.getIntrinsicWidth(), (float)drawable.getIntrinsicHeight()));
        }
        
        BlurResult(final BlurProvider blurProvider, final Drawable drawable, final RectF cropRegion) {
            this.drawable = drawable;
            this.cropRegion = cropRegion;
        }
    }
    
    private static class SourceDownsampler
    {
        private final Drawable mDrawable;
        
        public SourceDownsampler(final Drawable mDrawable) {
            this.mDrawable = mDrawable;
        }
        
        public static float getDownsampleScaleFactor(final int n) {
            return 1.0f / (1 << n);
        }
        
        private static int getSideLength(final float n, final int n2) {
            return (int)Math.ceil(n * getDownsampleScaleFactor(n2) + 50.0f);
        }
        
        public Bitmap createBitmapBuffer(final int n) {
            return Bitmap.createBitmap(this.getDownsampledWidth(n), this.getDownsampledHeight(n), Bitmap$Config.ARGB_8888);
        }
        
        public int getDownsampledHeight(final int n) {
            return getSideLength((float)this.getDrawable().getIntrinsicHeight(), n);
        }
        
        public int getDownsampledWidth(final int n) {
            return getSideLength((float)this.getDrawable().getIntrinsicWidth(), n);
        }
        
        public Drawable getDrawable() {
            return this.mDrawable;
        }
        
        public void rasterize(final Bitmap bitmap, final int n) {
            final Canvas canvas = new Canvas(bitmap);
            canvas.clipRect(0, 0, this.getDownsampledWidth(n) + 25, this.getDownsampledHeight(n) + 25);
            canvas.drawColor(0, BlendMode.CLEAR);
            final float n2 = 25;
            canvas.translate(n2, n2);
            final float downsampleScaleFactor = getDownsampleScaleFactor(n);
            canvas.scale(downsampleScaleFactor, downsampleScaleFactor);
            this.getDrawable().setBounds(0, 0, this.getDrawable().getIntrinsicWidth(), this.getDrawable().getIntrinsicHeight());
            this.getDrawable().draw(canvas);
        }
    }
}
