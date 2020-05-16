// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import android.view.animation.Interpolator;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import java.util.Iterator;
import android.graphics.Canvas;
import android.util.AttributeSet;
import java.util.ArrayList;
import android.content.Context;
import java.util.List;
import android.widget.ListView;

final class OverlayListView extends ListView
{
    private final List<OverlayObject> mOverlayObjects;
    
    public OverlayListView(final Context context) {
        super(context);
        this.mOverlayObjects = new ArrayList<OverlayObject>();
    }
    
    public OverlayListView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mOverlayObjects = new ArrayList<OverlayObject>();
    }
    
    public OverlayListView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mOverlayObjects = new ArrayList<OverlayObject>();
    }
    
    public void addOverlayObject(final OverlayObject overlayObject) {
        this.mOverlayObjects.add(overlayObject);
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (this.mOverlayObjects.size() > 0) {
            final Iterator<OverlayObject> iterator = this.mOverlayObjects.iterator();
            while (iterator.hasNext()) {
                final OverlayObject overlayObject = iterator.next();
                final BitmapDrawable bitmapDrawable = overlayObject.getBitmapDrawable();
                if (bitmapDrawable != null) {
                    bitmapDrawable.draw(canvas);
                }
                if (!overlayObject.update(this.getDrawingTime())) {
                    iterator.remove();
                }
            }
        }
    }
    
    public void startAnimationAll() {
        for (final OverlayObject overlayObject : this.mOverlayObjects) {
            if (!overlayObject.isAnimationStarted()) {
                overlayObject.startAnimation(this.getDrawingTime());
            }
        }
    }
    
    public void stopAnimationAll() {
        final Iterator<OverlayObject> iterator = this.mOverlayObjects.iterator();
        while (iterator.hasNext()) {
            iterator.next().stopAnimation();
        }
    }
    
    public static class OverlayObject
    {
        private BitmapDrawable mBitmap;
        private float mCurrentAlpha;
        private Rect mCurrentBounds;
        private int mDeltaY;
        private long mDuration;
        private float mEndAlpha;
        private Interpolator mInterpolator;
        private boolean mIsAnimationEnded;
        private boolean mIsAnimationStarted;
        private OnAnimationEndListener mListener;
        private float mStartAlpha;
        private Rect mStartRect;
        private long mStartTime;
        
        public OverlayObject(BitmapDrawable mBitmap, Rect rect) {
            this.mCurrentAlpha = 1.0f;
            this.mStartAlpha = 1.0f;
            this.mEndAlpha = 1.0f;
            this.mBitmap = mBitmap;
            this.mStartRect = rect;
            rect = new Rect(rect);
            this.mCurrentBounds = rect;
            mBitmap = this.mBitmap;
            if (mBitmap != null && rect != null) {
                mBitmap.setAlpha((int)(this.mCurrentAlpha * 255.0f));
                this.mBitmap.setBounds(this.mCurrentBounds);
            }
        }
        
        public BitmapDrawable getBitmapDrawable() {
            return this.mBitmap;
        }
        
        public boolean isAnimationStarted() {
            return this.mIsAnimationStarted;
        }
        
        public OverlayObject setAlphaAnimation(final float mStartAlpha, final float mEndAlpha) {
            this.mStartAlpha = mStartAlpha;
            this.mEndAlpha = mEndAlpha;
            return this;
        }
        
        public OverlayObject setAnimationEndListener(final OnAnimationEndListener mListener) {
            this.mListener = mListener;
            return this;
        }
        
        public OverlayObject setDuration(final long mDuration) {
            this.mDuration = mDuration;
            return this;
        }
        
        public OverlayObject setInterpolator(final Interpolator mInterpolator) {
            this.mInterpolator = mInterpolator;
            return this;
        }
        
        public OverlayObject setTranslateYAnimation(final int mDeltaY) {
            this.mDeltaY = mDeltaY;
            return this;
        }
        
        public void startAnimation(final long mStartTime) {
            this.mStartTime = mStartTime;
            this.mIsAnimationStarted = true;
        }
        
        public void stopAnimation() {
            this.mIsAnimationStarted = true;
            this.mIsAnimationEnded = true;
            final OnAnimationEndListener mListener = this.mListener;
            if (mListener != null) {
                mListener.onAnimationEnd();
            }
        }
        
        public boolean update(final long n) {
            if (this.mIsAnimationEnded) {
                return false;
            }
            final float min = Math.min(1.0f, (n - this.mStartTime) / (float)this.mDuration);
            float n2 = 0.0f;
            final float max = Math.max(0.0f, min);
            if (this.mIsAnimationStarted) {
                n2 = max;
            }
            final Interpolator mInterpolator = this.mInterpolator;
            float interpolation;
            if (mInterpolator == null) {
                interpolation = n2;
            }
            else {
                interpolation = mInterpolator.getInterpolation(n2);
            }
            final int n3 = (int)(this.mDeltaY * interpolation);
            final Rect mCurrentBounds = this.mCurrentBounds;
            final Rect mStartRect = this.mStartRect;
            mCurrentBounds.top = mStartRect.top + n3;
            mCurrentBounds.bottom = mStartRect.bottom + n3;
            final float mStartAlpha = this.mStartAlpha;
            final float mCurrentAlpha = mStartAlpha + (this.mEndAlpha - mStartAlpha) * interpolation;
            this.mCurrentAlpha = mCurrentAlpha;
            final BitmapDrawable mBitmap = this.mBitmap;
            if (mBitmap != null && mCurrentBounds != null) {
                mBitmap.setAlpha((int)(mCurrentAlpha * 255.0f));
                this.mBitmap.setBounds(this.mCurrentBounds);
            }
            if (this.mIsAnimationStarted && n2 >= 1.0f) {
                this.mIsAnimationEnded = true;
                final OnAnimationEndListener mListener = this.mListener;
                if (mListener != null) {
                    mListener.onAnimationEnd();
                }
            }
            return this.mIsAnimationEnded ^ true;
        }
        
        public interface OnAnimationEndListener
        {
            void onAnimationEnd();
        }
    }
}
