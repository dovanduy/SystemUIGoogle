// 
// Decompiled by Procyon v0.5.36
// 

package androidx.lifecycle;

import android.app.Application$ActivityLifecycleCallbacks;
import android.os.Build$VERSION;
import android.os.Bundle;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class ProcessLifecycleOwner implements LifecycleOwner
{
    static final long TIMEOUT_MS = 700L;
    private static final ProcessLifecycleOwner sInstance;
    private Runnable mDelayedPauseRunnable;
    private Handler mHandler;
    ReportFragment.ActivityInitializationListener mInitializationListener;
    private boolean mPauseSent;
    private final LifecycleRegistry mRegistry;
    private int mResumedCounter;
    private int mStartedCounter;
    private boolean mStopSent;
    
    static {
        sInstance = new ProcessLifecycleOwner();
    }
    
    private ProcessLifecycleOwner() {
        this.mStartedCounter = 0;
        this.mResumedCounter = 0;
        this.mPauseSent = true;
        this.mStopSent = true;
        this.mRegistry = new LifecycleRegistry(this);
        this.mDelayedPauseRunnable = new Runnable() {
            @Override
            public void run() {
                ProcessLifecycleOwner.this.dispatchPauseIfNeeded();
                ProcessLifecycleOwner.this.dispatchStopIfNeeded();
            }
        };
        this.mInitializationListener = new ReportFragment.ActivityInitializationListener() {
            @Override
            public void onCreate() {
            }
            
            @Override
            public void onResume() {
                ProcessLifecycleOwner.this.activityResumed();
            }
            
            @Override
            public void onStart() {
                ProcessLifecycleOwner.this.activityStarted();
            }
        };
    }
    
    static void init(final Context context) {
        ProcessLifecycleOwner.sInstance.attach(context);
    }
    
    void activityPaused() {
        final int mResumedCounter = this.mResumedCounter - 1;
        this.mResumedCounter = mResumedCounter;
        if (mResumedCounter == 0) {
            this.mHandler.postDelayed(this.mDelayedPauseRunnable, 700L);
        }
    }
    
    void activityResumed() {
        final int mResumedCounter = this.mResumedCounter + 1;
        this.mResumedCounter = mResumedCounter;
        if (mResumedCounter == 1) {
            if (this.mPauseSent) {
                this.mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
                this.mPauseSent = false;
            }
            else {
                this.mHandler.removeCallbacks(this.mDelayedPauseRunnable);
            }
        }
    }
    
    void activityStarted() {
        final int mStartedCounter = this.mStartedCounter + 1;
        this.mStartedCounter = mStartedCounter;
        if (mStartedCounter == 1 && this.mStopSent) {
            this.mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
            this.mStopSent = false;
        }
    }
    
    void activityStopped() {
        --this.mStartedCounter;
        this.dispatchStopIfNeeded();
    }
    
    void attach(final Context context) {
        this.mHandler = new Handler();
        this.mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        ((Application)context.getApplicationContext()).registerActivityLifecycleCallbacks((Application$ActivityLifecycleCallbacks)new EmptyActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(final Activity activity, final Bundle bundle) {
                if (Build$VERSION.SDK_INT < 29) {
                    ReportFragment.get(activity).setProcessListener(ProcessLifecycleOwner.this.mInitializationListener);
                }
            }
            
            @Override
            public void onActivityPaused(final Activity activity) {
                ProcessLifecycleOwner.this.activityPaused();
            }
            
            public void onActivityPreCreated(final Activity activity, final Bundle bundle) {
                activity.registerActivityLifecycleCallbacks((Application$ActivityLifecycleCallbacks)new EmptyActivityLifecycleCallbacks() {
                    public void onActivityPostResumed(final Activity activity) {
                        ProcessLifecycleOwner.this.activityResumed();
                    }
                    
                    public void onActivityPostStarted(final Activity activity) {
                        ProcessLifecycleOwner.this.activityStarted();
                    }
                });
            }
            
            @Override
            public void onActivityStopped(final Activity activity) {
                ProcessLifecycleOwner.this.activityStopped();
            }
        });
    }
    
    void dispatchPauseIfNeeded() {
        if (this.mResumedCounter == 0) {
            this.mPauseSent = true;
            this.mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        }
    }
    
    void dispatchStopIfNeeded() {
        if (this.mStartedCounter == 0 && this.mPauseSent) {
            this.mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
            this.mStopSent = true;
        }
    }
    
    @Override
    public Lifecycle getLifecycle() {
        return this.mRegistry;
    }
}
