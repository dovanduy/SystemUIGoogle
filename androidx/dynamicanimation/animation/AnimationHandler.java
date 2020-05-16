// 
// Decompiled by Procyon v0.5.36
// 

package androidx.dynamicanimation.animation;

import android.view.Choreographer$FrameCallback;
import android.view.Choreographer;
import android.os.Looper;
import android.os.Handler;
import android.os.Build$VERSION;
import android.os.SystemClock;
import androidx.collection.SimpleArrayMap;
import java.util.ArrayList;

class AnimationHandler
{
    public static final ThreadLocal<AnimationHandler> sAnimatorHandler;
    final ArrayList<AnimationFrameCallback> mAnimationCallbacks;
    private final AnimationCallbackDispatcher mCallbackDispatcher;
    long mCurrentFrameTime;
    private final SimpleArrayMap<AnimationFrameCallback, Long> mDelayedCallbackStartTime;
    private boolean mListDirty;
    private AnimationFrameCallbackProvider mProvider;
    
    static {
        sAnimatorHandler = new ThreadLocal<AnimationHandler>();
    }
    
    AnimationHandler() {
        this.mDelayedCallbackStartTime = new SimpleArrayMap<AnimationFrameCallback, Long>();
        this.mAnimationCallbacks = new ArrayList<AnimationFrameCallback>();
        this.mCallbackDispatcher = new AnimationCallbackDispatcher();
        this.mCurrentFrameTime = 0L;
        this.mListDirty = false;
    }
    
    private void cleanUpList() {
        if (this.mListDirty) {
            for (int i = this.mAnimationCallbacks.size() - 1; i >= 0; --i) {
                if (this.mAnimationCallbacks.get(i) == null) {
                    this.mAnimationCallbacks.remove(i);
                }
            }
            this.mListDirty = false;
        }
    }
    
    public static AnimationHandler getInstance() {
        if (AnimationHandler.sAnimatorHandler.get() == null) {
            AnimationHandler.sAnimatorHandler.set(new AnimationHandler());
        }
        return AnimationHandler.sAnimatorHandler.get();
    }
    
    private boolean isCallbackDue(final AnimationFrameCallback animationFrameCallback, final long n) {
        final Long n2 = this.mDelayedCallbackStartTime.get(animationFrameCallback);
        if (n2 == null) {
            return true;
        }
        if (n2 < n) {
            this.mDelayedCallbackStartTime.remove(animationFrameCallback);
            return true;
        }
        return false;
    }
    
    public void addAnimationFrameCallback(final AnimationFrameCallback animationFrameCallback, final long n) {
        if (this.mAnimationCallbacks.size() == 0) {
            this.getProvider().postFrameCallback();
        }
        if (!this.mAnimationCallbacks.contains(animationFrameCallback)) {
            this.mAnimationCallbacks.add(animationFrameCallback);
        }
        if (n > 0L) {
            this.mDelayedCallbackStartTime.put(animationFrameCallback, SystemClock.uptimeMillis() + n);
        }
    }
    
    void doAnimationFrame(final long n) {
        final long uptimeMillis = SystemClock.uptimeMillis();
        for (int i = 0; i < this.mAnimationCallbacks.size(); ++i) {
            final AnimationFrameCallback animationFrameCallback = this.mAnimationCallbacks.get(i);
            if (animationFrameCallback != null) {
                if (this.isCallbackDue(animationFrameCallback, uptimeMillis)) {
                    animationFrameCallback.doAnimationFrame(n);
                }
            }
        }
        this.cleanUpList();
    }
    
    AnimationFrameCallbackProvider getProvider() {
        if (this.mProvider == null) {
            if (Build$VERSION.SDK_INT >= 16) {
                this.mProvider = (AnimationFrameCallbackProvider)new FrameCallbackProvider16(this.mCallbackDispatcher);
            }
            else {
                this.mProvider = (AnimationFrameCallbackProvider)new FrameCallbackProvider14(this.mCallbackDispatcher);
            }
        }
        return this.mProvider;
    }
    
    public void removeCallback(final AnimationFrameCallback o) {
        this.mDelayedCallbackStartTime.remove(o);
        final int index = this.mAnimationCallbacks.indexOf(o);
        if (index >= 0) {
            this.mAnimationCallbacks.set(index, null);
            this.mListDirty = true;
        }
    }
    
    class AnimationCallbackDispatcher
    {
        void dispatchAnimationFrame() {
            AnimationHandler.this.mCurrentFrameTime = SystemClock.uptimeMillis();
            final AnimationHandler this$0 = AnimationHandler.this;
            this$0.doAnimationFrame(this$0.mCurrentFrameTime);
            if (AnimationHandler.this.mAnimationCallbacks.size() > 0) {
                AnimationHandler.this.getProvider().postFrameCallback();
            }
        }
    }
    
    interface AnimationFrameCallback
    {
        boolean doAnimationFrame(final long p0);
    }
    
    abstract static class AnimationFrameCallbackProvider
    {
        final AnimationCallbackDispatcher mDispatcher;
        
        AnimationFrameCallbackProvider(final AnimationCallbackDispatcher mDispatcher) {
            this.mDispatcher = mDispatcher;
        }
        
        abstract void postFrameCallback();
    }
    
    private static class FrameCallbackProvider14 extends AnimationFrameCallbackProvider
    {
        private final Handler mHandler;
        long mLastFrameTime;
        private final Runnable mRunnable;
        
        FrameCallbackProvider14(final AnimationCallbackDispatcher animationCallbackDispatcher) {
            super(animationCallbackDispatcher);
            this.mLastFrameTime = -1L;
            this.mRunnable = new Runnable() {
                @Override
                public void run() {
                    FrameCallbackProvider14.this.mLastFrameTime = SystemClock.uptimeMillis();
                    FrameCallbackProvider14.this.mDispatcher.dispatchAnimationFrame();
                }
            };
            this.mHandler = new Handler(Looper.myLooper());
        }
        
        @Override
        void postFrameCallback() {
            this.mHandler.postDelayed(this.mRunnable, Math.max(10L - (SystemClock.uptimeMillis() - this.mLastFrameTime), 0L));
        }
    }
    
    private static class FrameCallbackProvider16 extends AnimationFrameCallbackProvider
    {
        private final Choreographer mChoreographer;
        private final Choreographer$FrameCallback mChoreographerCallback;
        
        FrameCallbackProvider16(final AnimationCallbackDispatcher animationCallbackDispatcher) {
            super(animationCallbackDispatcher);
            this.mChoreographer = Choreographer.getInstance();
            this.mChoreographerCallback = (Choreographer$FrameCallback)new Choreographer$FrameCallback() {
                public void doFrame(final long n) {
                    FrameCallbackProvider16.this.mDispatcher.dispatchAnimationFrame();
                }
            };
        }
        
        @Override
        void postFrameCallback() {
            this.mChoreographer.postFrameCallback(this.mChoreographerCallback);
        }
    }
}
