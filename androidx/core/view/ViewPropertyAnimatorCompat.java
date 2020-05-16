// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.os.Build$VERSION;
import android.animation.TimeInterpolator;
import android.view.animation.Interpolator;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import java.lang.ref.WeakReference;

public final class ViewPropertyAnimatorCompat
{
    Runnable mEndAction;
    int mOldLayerType;
    Runnable mStartAction;
    private WeakReference<View> mView;
    
    ViewPropertyAnimatorCompat(final View referent) {
        this.mStartAction = null;
        this.mEndAction = null;
        this.mOldLayerType = -1;
        this.mView = new WeakReference<View>(referent);
    }
    
    private void setListenerInternal(final View view, final ViewPropertyAnimatorListener viewPropertyAnimatorListener) {
        if (viewPropertyAnimatorListener != null) {
            view.animate().setListener((Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
                public void onAnimationCancel(final Animator animator) {
                    viewPropertyAnimatorListener.onAnimationCancel(view);
                }
                
                public void onAnimationEnd(final Animator animator) {
                    viewPropertyAnimatorListener.onAnimationEnd(view);
                }
                
                public void onAnimationStart(final Animator animator) {
                    viewPropertyAnimatorListener.onAnimationStart(view);
                }
            });
        }
        else {
            view.animate().setListener((Animator$AnimatorListener)null);
        }
    }
    
    public ViewPropertyAnimatorCompat alpha(final float n) {
        final View view = this.mView.get();
        if (view != null) {
            view.animate().alpha(n);
        }
        return this;
    }
    
    public void cancel() {
        final View view = this.mView.get();
        if (view != null) {
            view.animate().cancel();
        }
    }
    
    public long getDuration() {
        final View view = this.mView.get();
        if (view != null) {
            return view.animate().getDuration();
        }
        return 0L;
    }
    
    public ViewPropertyAnimatorCompat setDuration(final long duration) {
        final View view = this.mView.get();
        if (view != null) {
            view.animate().setDuration(duration);
        }
        return this;
    }
    
    public ViewPropertyAnimatorCompat setInterpolator(final Interpolator interpolator) {
        final View view = this.mView.get();
        if (view != null) {
            view.animate().setInterpolator((TimeInterpolator)interpolator);
        }
        return this;
    }
    
    public ViewPropertyAnimatorCompat setListener(final ViewPropertyAnimatorListener viewPropertyAnimatorListener) {
        final View view = this.mView.get();
        if (view != null) {
            if (Build$VERSION.SDK_INT >= 16) {
                this.setListenerInternal(view, viewPropertyAnimatorListener);
            }
            else {
                view.setTag(2113929216, (Object)viewPropertyAnimatorListener);
                this.setListenerInternal(view, new ViewPropertyAnimatorListenerApi14(this));
            }
        }
        return this;
    }
    
    public ViewPropertyAnimatorCompat setStartDelay(final long startDelay) {
        final View view = this.mView.get();
        if (view != null) {
            view.animate().setStartDelay(startDelay);
        }
        return this;
    }
    
    public ViewPropertyAnimatorCompat setUpdateListener(final ViewPropertyAnimatorUpdateListener viewPropertyAnimatorUpdateListener) {
        final View view = this.mView.get();
        if (view != null && Build$VERSION.SDK_INT >= 19) {
            Object updateListener = null;
            if (viewPropertyAnimatorUpdateListener != null) {
                updateListener = new ValueAnimator$AnimatorUpdateListener(this) {
                    public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                        viewPropertyAnimatorUpdateListener.onAnimationUpdate(view);
                    }
                };
            }
            view.animate().setUpdateListener((ValueAnimator$AnimatorUpdateListener)updateListener);
        }
        return this;
    }
    
    public void start() {
        final View view = this.mView.get();
        if (view != null) {
            view.animate().start();
        }
    }
    
    public ViewPropertyAnimatorCompat translationY(final float n) {
        final View view = this.mView.get();
        if (view != null) {
            view.animate().translationY(n);
        }
        return this;
    }
    
    static class ViewPropertyAnimatorListenerApi14 implements ViewPropertyAnimatorListener
    {
        boolean mAnimEndCalled;
        ViewPropertyAnimatorCompat mVpa;
        
        ViewPropertyAnimatorListenerApi14(final ViewPropertyAnimatorCompat mVpa) {
            this.mVpa = mVpa;
        }
        
        @Override
        public void onAnimationCancel(final View view) {
            final Object tag = view.getTag(2113929216);
            ViewPropertyAnimatorListener viewPropertyAnimatorListener;
            if (tag instanceof ViewPropertyAnimatorListener) {
                viewPropertyAnimatorListener = (ViewPropertyAnimatorListener)tag;
            }
            else {
                viewPropertyAnimatorListener = null;
            }
            if (viewPropertyAnimatorListener != null) {
                viewPropertyAnimatorListener.onAnimationCancel(view);
            }
        }
        
        @SuppressLint({ "WrongConstant" })
        @Override
        public void onAnimationEnd(final View view) {
            final int mOldLayerType = this.mVpa.mOldLayerType;
            ViewPropertyAnimatorListener viewPropertyAnimatorListener = null;
            if (mOldLayerType > -1) {
                view.setLayerType(mOldLayerType, (Paint)null);
                this.mVpa.mOldLayerType = -1;
            }
            if (Build$VERSION.SDK_INT >= 16 || !this.mAnimEndCalled) {
                final ViewPropertyAnimatorCompat mVpa = this.mVpa;
                final Runnable mEndAction = mVpa.mEndAction;
                if (mEndAction != null) {
                    mVpa.mEndAction = null;
                    mEndAction.run();
                }
                final Object tag = view.getTag(2113929216);
                if (tag instanceof ViewPropertyAnimatorListener) {
                    viewPropertyAnimatorListener = (ViewPropertyAnimatorListener)tag;
                }
                if (viewPropertyAnimatorListener != null) {
                    viewPropertyAnimatorListener.onAnimationEnd(view);
                }
                this.mAnimEndCalled = true;
            }
        }
        
        @Override
        public void onAnimationStart(final View view) {
            this.mAnimEndCalled = false;
            final int mOldLayerType = this.mVpa.mOldLayerType;
            ViewPropertyAnimatorListener viewPropertyAnimatorListener = null;
            if (mOldLayerType > -1) {
                view.setLayerType(2, (Paint)null);
            }
            final ViewPropertyAnimatorCompat mVpa = this.mVpa;
            final Runnable mStartAction = mVpa.mStartAction;
            if (mStartAction != null) {
                mVpa.mStartAction = null;
                mStartAction.run();
            }
            final Object tag = view.getTag(2113929216);
            if (tag instanceof ViewPropertyAnimatorListener) {
                viewPropertyAnimatorListener = (ViewPropertyAnimatorListener)tag;
            }
            if (viewPropertyAnimatorListener != null) {
                viewPropertyAnimatorListener.onAnimationStart(view);
            }
        }
    }
}
