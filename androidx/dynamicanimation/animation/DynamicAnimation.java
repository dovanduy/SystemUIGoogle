// 
// Decompiled by Procyon v0.5.36
// 

package androidx.dynamicanimation.animation;

import android.util.AndroidRuntimeException;
import android.os.Looper;
import androidx.core.view.ViewCompat;
import android.view.View;
import java.util.ArrayList;

public abstract class DynamicAnimation<T extends DynamicAnimation<T>> implements AnimationFrameCallback
{
    public static final ViewProperty ALPHA;
    public static final ViewProperty ROTATION;
    public static final ViewProperty ROTATION_X;
    public static final ViewProperty ROTATION_Y;
    public static final ViewProperty SCALE_X;
    public static final ViewProperty SCALE_Y;
    public static final ViewProperty SCROLL_X;
    public static final ViewProperty SCROLL_Y;
    public static final ViewProperty TRANSLATION_X;
    public static final ViewProperty TRANSLATION_Y;
    public static final ViewProperty TRANSLATION_Z;
    public static final ViewProperty Y;
    private final ArrayList<OnAnimationEndListener> mEndListeners;
    private long mLastFrameTime;
    float mMaxValue;
    float mMinValue;
    private float mMinVisibleChange;
    final FloatPropertyCompat mProperty;
    boolean mRunning;
    boolean mStartValueIsSet;
    final Object mTarget;
    private final ArrayList<OnAnimationUpdateListener> mUpdateListeners;
    float mValue;
    float mVelocity;
    
    static {
        TRANSLATION_X = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return view.getTranslationX();
            }
            
            @Override
            public void setValue(final View view, final float translationX) {
                view.setTranslationX(translationX);
            }
        };
        TRANSLATION_Y = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return view.getTranslationY();
            }
            
            @Override
            public void setValue(final View view, final float translationY) {
                view.setTranslationY(translationY);
            }
        };
        TRANSLATION_Z = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return ViewCompat.getTranslationZ(view);
            }
            
            @Override
            public void setValue(final View view, final float n) {
                ViewCompat.setTranslationZ(view, n);
            }
        };
        SCALE_X = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return view.getScaleX();
            }
            
            @Override
            public void setValue(final View view, final float scaleX) {
                view.setScaleX(scaleX);
            }
        };
        SCALE_Y = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return view.getScaleY();
            }
            
            @Override
            public void setValue(final View view, final float scaleY) {
                view.setScaleY(scaleY);
            }
        };
        ROTATION = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return view.getRotation();
            }
            
            @Override
            public void setValue(final View view, final float rotation) {
                view.setRotation(rotation);
            }
        };
        ROTATION_X = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return view.getRotationX();
            }
            
            @Override
            public void setValue(final View view, final float rotationX) {
                view.setRotationX(rotationX);
            }
        };
        ROTATION_Y = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return view.getRotationY();
            }
            
            @Override
            public void setValue(final View view, final float rotationY) {
                view.setRotationY(rotationY);
            }
        };
        Y = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return view.getY();
            }
            
            @Override
            public void setValue(final View view, final float y) {
                view.setY(y);
            }
        };
        ALPHA = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return view.getAlpha();
            }
            
            @Override
            public void setValue(final View view, final float alpha) {
                view.setAlpha(alpha);
            }
        };
        SCROLL_X = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return (float)view.getScrollX();
            }
            
            @Override
            public void setValue(final View view, final float n) {
                view.setScrollX((int)n);
            }
        };
        SCROLL_Y = (ViewProperty)new ViewProperty() {
            @Override
            public float getValue(final View view) {
                return (float)view.getScrollY();
            }
            
            @Override
            public void setValue(final View view, final float n) {
                view.setScrollY((int)n);
            }
        };
    }
    
     <K> DynamicAnimation(final K mTarget, final FloatPropertyCompat<K> mProperty) {
        this.mVelocity = 0.0f;
        this.mValue = Float.MAX_VALUE;
        this.mStartValueIsSet = false;
        this.mRunning = false;
        this.mMaxValue = Float.MAX_VALUE;
        this.mMinValue = -Float.MAX_VALUE;
        this.mLastFrameTime = 0L;
        this.mEndListeners = new ArrayList<OnAnimationEndListener>();
        this.mUpdateListeners = new ArrayList<OnAnimationUpdateListener>();
        this.mTarget = mTarget;
        this.mProperty = mProperty;
        if (mProperty != DynamicAnimation.ROTATION && mProperty != DynamicAnimation.ROTATION_X && mProperty != DynamicAnimation.ROTATION_Y) {
            if (mProperty == DynamicAnimation.ALPHA) {
                this.mMinVisibleChange = 0.00390625f;
            }
            else if (mProperty != DynamicAnimation.SCALE_X && mProperty != DynamicAnimation.SCALE_Y) {
                this.mMinVisibleChange = 1.0f;
            }
            else {
                this.mMinVisibleChange = 0.00390625f;
            }
        }
        else {
            this.mMinVisibleChange = 0.1f;
        }
    }
    
    private void endAnimationInternal(final boolean b) {
        int i = 0;
        this.mRunning = false;
        AnimationHandler.getInstance().removeCallback((AnimationHandler.AnimationFrameCallback)this);
        this.mLastFrameTime = 0L;
        this.mStartValueIsSet = false;
        while (i < this.mEndListeners.size()) {
            if (this.mEndListeners.get(i) != null) {
                this.mEndListeners.get(i).onAnimationEnd(this, b, this.mValue, this.mVelocity);
            }
            ++i;
        }
        removeNullEntries(this.mEndListeners);
    }
    
    private float getPropertyValue() {
        return this.mProperty.getValue(this.mTarget);
    }
    
    private static <T> void removeEntry(final ArrayList<T> list, final T o) {
        final int index = list.indexOf(o);
        if (index >= 0) {
            list.set(index, null);
        }
    }
    
    private static <T> void removeNullEntries(final ArrayList<T> list) {
        for (int i = list.size() - 1; i >= 0; --i) {
            if (list.get(i) == null) {
                list.remove(i);
            }
        }
    }
    
    private void startAnimationInternal() {
        if (!this.mRunning) {
            this.mRunning = true;
            if (!this.mStartValueIsSet) {
                this.mValue = this.getPropertyValue();
            }
            final float mValue = this.mValue;
            if (mValue > this.mMaxValue || mValue < this.mMinValue) {
                throw new IllegalArgumentException("Starting value need to be in between min value and max value");
            }
            AnimationHandler.getInstance().addAnimationFrameCallback((AnimationHandler.AnimationFrameCallback)this, 0L);
        }
    }
    
    public T addEndListener(final OnAnimationEndListener onAnimationEndListener) {
        if (!this.mEndListeners.contains(onAnimationEndListener)) {
            this.mEndListeners.add(onAnimationEndListener);
        }
        return (T)this;
    }
    
    public T addUpdateListener(final OnAnimationUpdateListener onAnimationUpdateListener) {
        if (!this.isRunning()) {
            if (!this.mUpdateListeners.contains(onAnimationUpdateListener)) {
                this.mUpdateListeners.add(onAnimationUpdateListener);
            }
            return (T)this;
        }
        throw new UnsupportedOperationException("Error: Update listeners must be added beforethe animation.");
    }
    
    public void cancel() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (this.mRunning) {
                this.endAnimationInternal(true);
            }
            return;
        }
        throw new AndroidRuntimeException("Animations may only be canceled on the main thread");
    }
    
    @Override
    public boolean doAnimationFrame(final long n) {
        final long mLastFrameTime = this.mLastFrameTime;
        if (mLastFrameTime == 0L) {
            this.mLastFrameTime = n;
            this.setPropertyValue(this.mValue);
            return false;
        }
        this.mLastFrameTime = n;
        final boolean updateValueAndVelocity = this.updateValueAndVelocity(n - mLastFrameTime);
        final float min = Math.min(this.mValue, this.mMaxValue);
        this.mValue = min;
        this.setPropertyValue(this.mValue = Math.max(min, this.mMinValue));
        if (updateValueAndVelocity) {
            this.endAnimationInternal(false);
        }
        return updateValueAndVelocity;
    }
    
    float getValueThreshold() {
        return this.mMinVisibleChange * 0.75f;
    }
    
    public boolean isRunning() {
        return this.mRunning;
    }
    
    public void removeEndListener(final OnAnimationEndListener onAnimationEndListener) {
        removeEntry(this.mEndListeners, onAnimationEndListener);
    }
    
    public T setMaxValue(final float mMaxValue) {
        this.mMaxValue = mMaxValue;
        return (T)this;
    }
    
    public T setMinValue(final float mMinValue) {
        this.mMinValue = mMinValue;
        return (T)this;
    }
    
    void setPropertyValue(final float n) {
        this.mProperty.setValue(this.mTarget, n);
        for (int i = 0; i < this.mUpdateListeners.size(); ++i) {
            if (this.mUpdateListeners.get(i) != null) {
                this.mUpdateListeners.get(i).onAnimationUpdate(this, this.mValue, this.mVelocity);
            }
        }
        removeNullEntries(this.mUpdateListeners);
    }
    
    public T setStartValue(final float mValue) {
        this.mValue = mValue;
        this.mStartValueIsSet = true;
        return (T)this;
    }
    
    public T setStartVelocity(final float mVelocity) {
        this.mVelocity = mVelocity;
        return (T)this;
    }
    
    public void start() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (!this.mRunning) {
                this.startAnimationInternal();
            }
            return;
        }
        throw new AndroidRuntimeException("Animations may only be started on the main thread");
    }
    
    abstract boolean updateValueAndVelocity(final long p0);
    
    static class MassState
    {
        float mValue;
        float mVelocity;
    }
    
    public interface OnAnimationEndListener
    {
        void onAnimationEnd(final DynamicAnimation p0, final boolean p1, final float p2, final float p3);
    }
    
    public interface OnAnimationUpdateListener
    {
        void onAnimationUpdate(final DynamicAnimation p0, final float p1, final float p2);
    }
    
    public abstract static class ViewProperty extends FloatPropertyCompat<View>
    {
        private ViewProperty(final String s) {
            super(s);
        }
    }
}
