// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import java.util.Objects;
import com.android.internal.policy.IKeyguardDismissCallback;
import java.util.concurrent.Executor;
import java.util.ArrayList;

public class DismissCallbackRegistry
{
    private final ArrayList<DismissCallbackWrapper> mDismissCallbacks;
    private final Executor mUiBgExecutor;
    
    public DismissCallbackRegistry(final Executor mUiBgExecutor) {
        this.mDismissCallbacks = new ArrayList<DismissCallbackWrapper>();
        this.mUiBgExecutor = mUiBgExecutor;
    }
    
    public void addCallback(final IKeyguardDismissCallback keyguardDismissCallback) {
        this.mDismissCallbacks.add(new DismissCallbackWrapper(keyguardDismissCallback));
    }
    
    public void notifyDismissCancelled() {
        for (int i = this.mDismissCallbacks.size() - 1; i >= 0; --i) {
            final DismissCallbackWrapper obj = this.mDismissCallbacks.get(i);
            final Executor mUiBgExecutor = this.mUiBgExecutor;
            Objects.requireNonNull(obj);
            mUiBgExecutor.execute(new _$$Lambda$zM6bayhThdtgvBghgFXo519LeO0(obj));
        }
        this.mDismissCallbacks.clear();
    }
    
    public void notifyDismissSucceeded() {
        for (int i = this.mDismissCallbacks.size() - 1; i >= 0; --i) {
            final DismissCallbackWrapper obj = this.mDismissCallbacks.get(i);
            final Executor mUiBgExecutor = this.mUiBgExecutor;
            Objects.requireNonNull(obj);
            mUiBgExecutor.execute(new _$$Lambda$2j_lq_QeR0jp4UUzPHOB_8BlctI(obj));
        }
        this.mDismissCallbacks.clear();
    }
}
