// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.View;
import android.util.Property;
import java.util.ArrayList;
import java.util.List;
import android.util.MathUtils;
import android.view.animation.Interpolator;
import android.util.FloatProperty;

public class TouchAnimator
{
    private static final FloatProperty<TouchAnimator> POSITION;
    private final float mEndDelay;
    private final Interpolator mInterpolator;
    private final KeyframeSet[] mKeyframeSets;
    private float mLastT;
    private final Listener mListener;
    private final float mSpan;
    private final float mStartDelay;
    private final Object[] mTargets;
    
    static {
        POSITION = new FloatProperty<TouchAnimator>() {
            public Float get(final TouchAnimator touchAnimator) {
                return touchAnimator.mLastT;
            }
            
            public void setValue(final TouchAnimator touchAnimator, final float position) {
                touchAnimator.setPosition(position);
            }
        };
    }
    
    private TouchAnimator(final Object[] mTargets, final KeyframeSet[] mKeyframeSets, final float mStartDelay, final float mEndDelay, final Interpolator mInterpolator, final Listener mListener) {
        this.mLastT = -1.0f;
        this.mTargets = mTargets;
        this.mKeyframeSets = mKeyframeSets;
        this.mStartDelay = mStartDelay;
        this.mEndDelay = mEndDelay;
        this.mSpan = 1.0f - mEndDelay - mStartDelay;
        this.mInterpolator = mInterpolator;
        this.mListener = mListener;
    }
    
    public void setPosition(float interpolation) {
        final float constrain = MathUtils.constrain((interpolation - this.mStartDelay) / this.mSpan, 0.0f, 1.0f);
        final Interpolator mInterpolator = this.mInterpolator;
        interpolation = constrain;
        if (mInterpolator != null) {
            interpolation = mInterpolator.getInterpolation(constrain);
        }
        final float mLastT = this.mLastT;
        if (interpolation == mLastT) {
            return;
        }
        final Listener mListener = this.mListener;
        if (mListener != null) {
            if (interpolation == 1.0f) {
                mListener.onAnimationAtEnd();
            }
            else if (interpolation == 0.0f) {
                mListener.onAnimationAtStart();
            }
            else if (mLastT <= 0.0f || mLastT == 1.0f) {
                this.mListener.onAnimationStarted();
            }
            this.mLastT = interpolation;
        }
        int n = 0;
        while (true) {
            final Object[] mTargets = this.mTargets;
            if (n >= mTargets.length) {
                break;
            }
            this.mKeyframeSets[n].setValue(interpolation, mTargets[n]);
            ++n;
        }
    }
    
    public static class Builder
    {
        private float mEndDelay;
        private Interpolator mInterpolator;
        private Listener mListener;
        private float mStartDelay;
        private List<Object> mTargets;
        private List<KeyframeSet> mValues;
        
        public Builder() {
            this.mTargets = new ArrayList<Object>();
            this.mValues = new ArrayList<KeyframeSet>();
        }
        
        private void add(final Object o, final KeyframeSet set) {
            this.mTargets.add(o);
            this.mValues.add(set);
        }
        
        private static Property getProperty(final Object o, final String anObject, final Class<?> clazz) {
            if (o instanceof View) {
                switch (anObject) {
                    case "scaleY": {
                        return View.SCALE_Y;
                    }
                    case "scaleX": {
                        return View.SCALE_X;
                    }
                    case "y": {
                        return View.Y;
                    }
                    case "x": {
                        return View.X;
                    }
                    case "rotation": {
                        return View.ROTATION;
                    }
                    case "alpha": {
                        return View.ALPHA;
                    }
                    case "translationZ": {
                        return View.TRANSLATION_Z;
                    }
                    case "translationY": {
                        return View.TRANSLATION_Y;
                    }
                    case "translationX": {
                        return View.TRANSLATION_X;
                    }
                }
            }
            if (o instanceof TouchAnimator && "position".equals(anObject)) {
                return (Property)TouchAnimator.POSITION;
            }
            return Property.of((Class)o.getClass(), (Class)clazz, anObject);
        }
        
        public Builder addFloat(final Object o, final String s, final float... array) {
            this.add(o, KeyframeSet.ofFloat(getProperty(o, s, Float.TYPE), array));
            return this;
        }
        
        public TouchAnimator build() {
            final List<Object> mTargets = this.mTargets;
            final Object[] array = mTargets.toArray(new Object[mTargets.size()]);
            final List<KeyframeSet> mValues = this.mValues;
            return new TouchAnimator(array, (KeyframeSet[])mValues.toArray(new KeyframeSet[mValues.size()]), this.mStartDelay, this.mEndDelay, this.mInterpolator, this.mListener, null);
        }
        
        public Builder setEndDelay(final float mEndDelay) {
            this.mEndDelay = mEndDelay;
            return this;
        }
        
        public Builder setInterpolator(final Interpolator mInterpolator) {
            this.mInterpolator = mInterpolator;
            return this;
        }
        
        public Builder setListener(final Listener mListener) {
            this.mListener = mListener;
            return this;
        }
        
        public Builder setStartDelay(final float mStartDelay) {
            this.mStartDelay = mStartDelay;
            return this;
        }
    }
    
    private static class FloatKeyframeSet<T> extends KeyframeSet
    {
        private final Property<T, Float> mProperty;
        private final float[] mValues;
        
        public FloatKeyframeSet(final Property<T, Float> mProperty, final float[] mValues) {
            super(mValues.length);
            this.mProperty = mProperty;
            this.mValues = mValues;
        }
        
        @Override
        protected void interpolate(final int n, final float n2, final Object o) {
            final float[] mValues = this.mValues;
            final float n3 = mValues[n - 1];
            this.mProperty.set(o, (Object)(n3 + (mValues[n] - n3) * n2));
        }
    }
    
    private abstract static class KeyframeSet
    {
        private final float mFrameWidth;
        private final int mSize;
        
        public KeyframeSet(final int mSize) {
            this.mSize = mSize;
            this.mFrameWidth = 1.0f / (mSize - 1);
        }
        
        public static KeyframeSet ofFloat(final Property property, final float... array) {
            return (KeyframeSet)new FloatKeyframeSet((android.util.Property<Object, Float>)property, array);
        }
        
        protected abstract void interpolate(final int p0, final float p1, final Object p2);
        
        void setValue(final float n, final Object o) {
            final int constrain = MathUtils.constrain((int)Math.ceil(n / this.mFrameWidth), 1, this.mSize - 1);
            final float mFrameWidth = this.mFrameWidth;
            this.interpolate(constrain, (n - (constrain - 1) * mFrameWidth) / mFrameWidth, o);
        }
    }
    
    public interface Listener
    {
        void onAnimationAtEnd();
        
        void onAnimationAtStart();
        
        void onAnimationStarted();
    }
    
    public static class ListenerAdapter implements Listener
    {
        @Override
        public void onAnimationAtStart() {
        }
    }
}
