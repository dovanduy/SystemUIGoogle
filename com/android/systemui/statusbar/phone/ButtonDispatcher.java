// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.drawable.Drawable$Callback;
import android.animation.Animator$AnimatorListener;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.graphics.drawable.Drawable;
import com.android.systemui.Dependency;
import android.animation.Animator;
import java.util.ArrayList;
import android.view.View$OnTouchListener;
import android.view.View$OnHoverListener;
import android.view.View$OnLongClickListener;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.View$OnClickListener;
import com.android.systemui.assist.AssistManager;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.view.View$AccessibilityDelegate;

public class ButtonDispatcher
{
    private View$AccessibilityDelegate mAccessibilityDelegate;
    private Float mAlpha;
    private final ValueAnimator$AnimatorUpdateListener mAlphaListener;
    private final AssistManager mAssistManager;
    private View$OnClickListener mClickListener;
    private View mCurrentView;
    private Float mDarkIntensity;
    private Boolean mDelayTouchFeedback;
    private ValueAnimator mFadeAnimator;
    private final AnimatorListenerAdapter mFadeListener;
    private final int mId;
    private KeyButtonDrawable mImageDrawable;
    private View$OnLongClickListener mLongClickListener;
    private Boolean mLongClickable;
    private View$OnHoverListener mOnHoverListener;
    private View$OnTouchListener mTouchListener;
    private boolean mVertical;
    private final ArrayList<View> mViews;
    private Integer mVisibility;
    
    public ButtonDispatcher(final int mId) {
        this.mViews = new ArrayList<View>();
        this.mVisibility = 0;
        this.mAlphaListener = (ValueAnimator$AnimatorUpdateListener)new _$$Lambda$ButtonDispatcher$YQ5xchhAskLzgLUT3UrgvCxrRAQ(this);
        this.mFadeListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                ButtonDispatcher.this.mFadeAnimator = null;
                final ButtonDispatcher this$0 = ButtonDispatcher.this;
                int visibility;
                if (this$0.getAlpha() == 1.0f) {
                    visibility = 0;
                }
                else {
                    visibility = 4;
                }
                this$0.setVisibility(visibility);
            }
        };
        this.mId = mId;
        this.mAssistManager = Dependency.get(AssistManager.class);
    }
    
    public void abortCurrentGesture() {
        for (int size = this.mViews.size(), i = 0; i < size; ++i) {
            if (this.mViews.get(i) instanceof ButtonInterface) {
                ((ButtonInterface)this.mViews.get(i)).abortCurrentGesture();
            }
        }
    }
    
    void addView(final View e) {
        this.mViews.add(e);
        e.setOnClickListener(this.mClickListener);
        e.setOnTouchListener(this.mTouchListener);
        e.setOnLongClickListener(this.mLongClickListener);
        e.setOnHoverListener(this.mOnHoverListener);
        final Boolean mLongClickable = this.mLongClickable;
        if (mLongClickable != null) {
            e.setLongClickable((boolean)mLongClickable);
        }
        final Float mAlpha = this.mAlpha;
        if (mAlpha != null) {
            e.setAlpha((float)mAlpha);
        }
        final Integer mVisibility = this.mVisibility;
        if (mVisibility != null) {
            e.setVisibility((int)mVisibility);
        }
        final View$AccessibilityDelegate mAccessibilityDelegate = this.mAccessibilityDelegate;
        if (mAccessibilityDelegate != null) {
            e.setAccessibilityDelegate(mAccessibilityDelegate);
        }
        if (e instanceof ButtonInterface) {
            final ButtonInterface buttonInterface = (ButtonInterface)e;
            final Float mDarkIntensity = this.mDarkIntensity;
            if (mDarkIntensity != null) {
                buttonInterface.setDarkIntensity(mDarkIntensity);
            }
            final KeyButtonDrawable mImageDrawable = this.mImageDrawable;
            if (mImageDrawable != null) {
                buttonInterface.setImageDrawable(mImageDrawable);
            }
            final Boolean mDelayTouchFeedback = this.mDelayTouchFeedback;
            if (mDelayTouchFeedback != null) {
                buttonInterface.setDelayTouchFeedback(mDelayTouchFeedback);
            }
            buttonInterface.setVertical(this.mVertical);
        }
    }
    
    void clear() {
        this.mViews.clear();
    }
    
    public float getAlpha() {
        final Float mAlpha = this.mAlpha;
        float floatValue;
        if (mAlpha != null) {
            floatValue = mAlpha;
        }
        else {
            floatValue = 1.0f;
        }
        return floatValue;
    }
    
    public View getCurrentView() {
        return this.mCurrentView;
    }
    
    public int getId() {
        return this.mId;
    }
    
    public KeyButtonDrawable getImageDrawable() {
        return this.mImageDrawable;
    }
    
    public ArrayList<View> getViews() {
        return this.mViews;
    }
    
    public int getVisibility() {
        final Integer mVisibility = this.mVisibility;
        int intValue;
        if (mVisibility != null) {
            intValue = mVisibility;
        }
        else {
            intValue = 0;
        }
        return intValue;
    }
    
    public boolean isVisible() {
        return this.getVisibility() == 0;
    }
    
    protected void onDestroy() {
    }
    
    public void setAccessibilityDelegate(final View$AccessibilityDelegate view$AccessibilityDelegate) {
        this.mAccessibilityDelegate = view$AccessibilityDelegate;
        for (int size = this.mViews.size(), i = 0; i < size; ++i) {
            this.mViews.get(i).setAccessibilityDelegate(view$AccessibilityDelegate);
        }
    }
    
    public void setAlpha(final float n) {
        this.setAlpha(n, false);
    }
    
    public void setAlpha(final float n, final boolean b) {
        this.setAlpha(n, b, true);
    }
    
    public void setAlpha(final float n, final boolean b, final long duration, final boolean b2) {
        if (this.mFadeAnimator != null && (b2 || b)) {
            this.mFadeAnimator.cancel();
        }
        int i = 0;
        if (b) {
            this.setVisibility(0);
            (this.mFadeAnimator = ValueAnimator.ofFloat(new float[] { this.getAlpha(), n })).setStartDelay(this.mAssistManager.getAssistHandleShowAndGoRemainingDurationMs());
            this.mFadeAnimator.setDuration(duration);
            this.mFadeAnimator.setInterpolator((TimeInterpolator)Interpolators.LINEAR);
            this.mFadeAnimator.addListener((Animator$AnimatorListener)this.mFadeListener);
            this.mFadeAnimator.addUpdateListener(this.mAlphaListener);
            this.mFadeAnimator.start();
        }
        else {
            final int n2 = (int)(this.getAlpha() * 255.0f);
            final int n3 = (int)(n * 255.0f);
            if (n2 != n3) {
                this.mAlpha = n3 / 255.0f;
                while (i < this.mViews.size()) {
                    this.mViews.get(i).setAlpha((float)this.mAlpha);
                    ++i;
                }
            }
        }
    }
    
    public void setAlpha(final float n, final boolean b, final boolean b2) {
        long n2;
        if (this.getAlpha() < n) {
            n2 = 150L;
        }
        else {
            n2 = 250L;
        }
        this.setAlpha(n, b, n2, b2);
    }
    
    public void setCurrentView(View view) {
        view = view.findViewById(this.mId);
        this.mCurrentView = view;
        final KeyButtonDrawable mImageDrawable = this.mImageDrawable;
        if (mImageDrawable != null) {
            mImageDrawable.setCallback((Drawable$Callback)view);
        }
        view = this.mCurrentView;
        if (view != null) {
            view.setTranslationX(0.0f);
            this.mCurrentView.setTranslationY(0.0f);
            this.mCurrentView.setTranslationZ(0.0f);
        }
    }
    
    public void setDarkIntensity(final float n) {
        this.mDarkIntensity = n;
        for (int size = this.mViews.size(), i = 0; i < size; ++i) {
            if (this.mViews.get(i) instanceof ButtonInterface) {
                ((ButtonInterface)this.mViews.get(i)).setDarkIntensity(n);
            }
        }
    }
    
    public void setImageDrawable(KeyButtonDrawable mImageDrawable) {
        this.mImageDrawable = mImageDrawable;
        for (int size = this.mViews.size(), i = 0; i < size; ++i) {
            if (this.mViews.get(i) instanceof ButtonInterface) {
                ((ButtonInterface)this.mViews.get(i)).setImageDrawable(this.mImageDrawable);
            }
        }
        mImageDrawable = this.mImageDrawable;
        if (mImageDrawable != null) {
            mImageDrawable.setCallback((Drawable$Callback)this.mCurrentView);
        }
    }
    
    public void setLongClickable(final boolean b) {
        this.mLongClickable = b;
        for (int size = this.mViews.size(), i = 0; i < size; ++i) {
            this.mViews.get(i).setLongClickable((boolean)this.mLongClickable);
        }
    }
    
    public void setOnClickListener(final View$OnClickListener mClickListener) {
        this.mClickListener = mClickListener;
        for (int size = this.mViews.size(), i = 0; i < size; ++i) {
            this.mViews.get(i).setOnClickListener(this.mClickListener);
        }
    }
    
    public void setOnHoverListener(final View$OnHoverListener mOnHoverListener) {
        this.mOnHoverListener = mOnHoverListener;
        for (int size = this.mViews.size(), i = 0; i < size; ++i) {
            this.mViews.get(i).setOnHoverListener(this.mOnHoverListener);
        }
    }
    
    public void setOnLongClickListener(final View$OnLongClickListener mLongClickListener) {
        this.mLongClickListener = mLongClickListener;
        for (int size = this.mViews.size(), i = 0; i < size; ++i) {
            this.mViews.get(i).setOnLongClickListener(this.mLongClickListener);
        }
    }
    
    public void setOnTouchListener(final View$OnTouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
        for (int size = this.mViews.size(), i = 0; i < size; ++i) {
            this.mViews.get(i).setOnTouchListener(this.mTouchListener);
        }
    }
    
    public void setVertical(final boolean b) {
        this.mVertical = b;
        for (int size = this.mViews.size(), i = 0; i < size; ++i) {
            final View view = this.mViews.get(i);
            if (view instanceof ButtonInterface) {
                ((ButtonInterface)view).setVertical(b);
            }
        }
    }
    
    public void setVisibility(int i) {
        if (this.mVisibility == i) {
            return;
        }
        final ValueAnimator mFadeAnimator = this.mFadeAnimator;
        if (mFadeAnimator != null) {
            mFadeAnimator.cancel();
        }
        this.mVisibility = i;
        int size;
        for (size = this.mViews.size(), i = 0; i < size; ++i) {
            this.mViews.get(i).setVisibility((int)this.mVisibility);
        }
    }
}
