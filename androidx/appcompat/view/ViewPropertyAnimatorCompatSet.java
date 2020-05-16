// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view;

import java.util.Iterator;
import android.view.View;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.core.view.ViewPropertyAnimatorListener;
import android.view.animation.Interpolator;
import androidx.core.view.ViewPropertyAnimatorCompat;
import java.util.ArrayList;

public class ViewPropertyAnimatorCompatSet
{
    final ArrayList<ViewPropertyAnimatorCompat> mAnimators;
    private long mDuration;
    private Interpolator mInterpolator;
    private boolean mIsStarted;
    ViewPropertyAnimatorListener mListener;
    private final ViewPropertyAnimatorListenerAdapter mProxyListener;
    
    public ViewPropertyAnimatorCompatSet() {
        this.mDuration = -1L;
        this.mProxyListener = new ViewPropertyAnimatorListenerAdapter() {
            private int mProxyEndCount = 0;
            private boolean mProxyStarted = false;
            
            @Override
            public void onAnimationEnd(final View view) {
                final int mProxyEndCount = this.mProxyEndCount + 1;
                this.mProxyEndCount = mProxyEndCount;
                if (mProxyEndCount == ViewPropertyAnimatorCompatSet.this.mAnimators.size()) {
                    final ViewPropertyAnimatorListener mListener = ViewPropertyAnimatorCompatSet.this.mListener;
                    if (mListener != null) {
                        mListener.onAnimationEnd(null);
                    }
                    this.onEnd();
                }
            }
            
            @Override
            public void onAnimationStart(final View view) {
                if (this.mProxyStarted) {
                    return;
                }
                this.mProxyStarted = true;
                final ViewPropertyAnimatorListener mListener = ViewPropertyAnimatorCompatSet.this.mListener;
                if (mListener != null) {
                    mListener.onAnimationStart(null);
                }
            }
            
            void onEnd() {
                this.mProxyEndCount = 0;
                this.mProxyStarted = false;
                ViewPropertyAnimatorCompatSet.this.onAnimationsEnded();
            }
        };
        this.mAnimators = new ArrayList<ViewPropertyAnimatorCompat>();
    }
    
    public void cancel() {
        if (!this.mIsStarted) {
            return;
        }
        final Iterator<ViewPropertyAnimatorCompat> iterator = this.mAnimators.iterator();
        while (iterator.hasNext()) {
            iterator.next().cancel();
        }
        this.mIsStarted = false;
    }
    
    void onAnimationsEnded() {
        this.mIsStarted = false;
    }
    
    public ViewPropertyAnimatorCompatSet play(final ViewPropertyAnimatorCompat e) {
        if (!this.mIsStarted) {
            this.mAnimators.add(e);
        }
        return this;
    }
    
    public ViewPropertyAnimatorCompatSet playSequentially(final ViewPropertyAnimatorCompat e, final ViewPropertyAnimatorCompat e2) {
        this.mAnimators.add(e);
        e2.setStartDelay(e.getDuration());
        this.mAnimators.add(e2);
        return this;
    }
    
    public ViewPropertyAnimatorCompatSet setDuration(final long mDuration) {
        if (!this.mIsStarted) {
            this.mDuration = mDuration;
        }
        return this;
    }
    
    public ViewPropertyAnimatorCompatSet setInterpolator(final Interpolator mInterpolator) {
        if (!this.mIsStarted) {
            this.mInterpolator = mInterpolator;
        }
        return this;
    }
    
    public ViewPropertyAnimatorCompatSet setListener(final ViewPropertyAnimatorListener mListener) {
        if (!this.mIsStarted) {
            this.mListener = mListener;
        }
        return this;
    }
    
    public void start() {
        if (this.mIsStarted) {
            return;
        }
        for (final ViewPropertyAnimatorCompat viewPropertyAnimatorCompat : this.mAnimators) {
            final long mDuration = this.mDuration;
            if (mDuration >= 0L) {
                viewPropertyAnimatorCompat.setDuration(mDuration);
            }
            final Interpolator mInterpolator = this.mInterpolator;
            if (mInterpolator != null) {
                viewPropertyAnimatorCompat.setInterpolator(mInterpolator);
            }
            if (this.mListener != null) {
                viewPropertyAnimatorCompat.setListener(this.mProxyListener);
            }
            viewPropertyAnimatorCompat.start();
        }
        this.mIsStarted = true;
    }
}
