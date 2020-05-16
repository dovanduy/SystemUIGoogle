// 
// Decompiled by Procyon v0.5.36
// 

package androidx.activity.result;

import android.util.Log;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import java.util.Collection;
import java.util.ArrayList;
import androidx.activity.result.contract.ActivityResultContract;
import android.os.Parcelable;
import android.content.Intent;
import java.util.HashMap;
import android.os.Bundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

public abstract class ActivityResultRegistry
{
    private final transient Map<String, CallbackAndContract<?>> mKeyToCallback;
    private final Map<String, Integer> mKeyToRc;
    private final AtomicInteger mNextRc;
    private final Bundle mPendingResults;
    private final Map<Integer, String> mRcToKey;
    
    public ActivityResultRegistry() {
        this.mNextRc = new AtomicInteger(0);
        this.mRcToKey = new HashMap<Integer, String>();
        this.mKeyToRc = new HashMap<String, Integer>();
        this.mKeyToCallback = new HashMap<String, CallbackAndContract<?>>();
        this.mPendingResults = new Bundle();
    }
    
    private void bindRcKey(final int n, final String s) {
        this.mRcToKey.put(n, s);
        this.mKeyToRc.put(s, n);
    }
    
    private <O> void doDispatch(final String s, final int n, final Intent intent, final CallbackAndContract<O> callbackAndContract) {
        final ActivityResultCallback<O> mCallback = callbackAndContract.mCallback;
        final ActivityResultContract<?, O> mContract = callbackAndContract.mContract;
        if (mCallback != null) {
            mCallback.onActivityResult(mContract.parseResult(n, intent));
        }
        else {
            this.mPendingResults.putParcelable(s, (Parcelable)new ActivityResult(n, intent));
        }
    }
    
    private int registerKey(final String s) {
        final Integer n = this.mKeyToRc.get(s);
        if (n != null) {
            return n;
        }
        final int andIncrement = this.mNextRc.getAndIncrement();
        this.bindRcKey(andIncrement, s);
        return andIncrement;
    }
    
    public boolean dispatchResult(final int i, final int n, final Intent intent) {
        final String s = this.mRcToKey.get(i);
        if (s == null) {
            return false;
        }
        this.doDispatch(s, n, intent, this.mKeyToCallback.get(s));
        return true;
    }
    
    public void onRestoreInstanceState(final Bundle bundle) {
        if (bundle == null) {
            return;
        }
        final ArrayList integerArrayList = bundle.getIntegerArrayList("KEY_COMPONENT_ACTIVITY_REGISTERED_RCS");
        final ArrayList stringArrayList = bundle.getStringArrayList("KEY_COMPONENT_ACTIVITY_REGISTERED_KEYS");
        if (stringArrayList != null) {
            if (integerArrayList != null) {
                final int size = stringArrayList.size();
                for (int i = 0; i < size; ++i) {
                    this.bindRcKey(integerArrayList.get(i), stringArrayList.get(i));
                }
                this.mNextRc.set(size);
                this.mPendingResults.putAll(bundle.getBundle("KEY_COMPONENT_ACTIVITY_PENDING_RESULT"));
            }
        }
    }
    
    public void onSaveInstanceState(final Bundle bundle) {
        bundle.putIntegerArrayList("KEY_COMPONENT_ACTIVITY_REGISTERED_RCS", new ArrayList((Collection<? extends E>)this.mRcToKey.keySet()));
        bundle.putStringArrayList("KEY_COMPONENT_ACTIVITY_REGISTERED_KEYS", new ArrayList((Collection<? extends E>)this.mRcToKey.values()));
        bundle.putBundle("KEY_COMPONENT_ACTIVITY_PENDING_RESULT", this.mPendingResults);
    }
    
    public <I, O> ActivityResultLauncher<I> registerActivityResultCallback(final String s, final LifecycleOwner lifecycleOwner, final ActivityResultContract<I, O> activityResultContract, final ActivityResultCallback<O> activityResultCallback) {
        final int registerKey = this.registerKey(s);
        this.mKeyToCallback.put(s, new CallbackAndContract<Object>((ActivityResultCallback<Object>)activityResultCallback, (ActivityResultContract<?, Object>)activityResultContract));
        final Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        final ActivityResult activityResult = (ActivityResult)this.mPendingResults.getParcelable(s);
        if (activityResult != null) {
            this.mPendingResults.remove(s);
            if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                activityResultCallback.onActivityResult(activityResultContract.parseResult(activityResult.getResultCode(), activityResult.getData()));
            }
            else {
                lifecycle.addObserver(new LifecycleEventObserver(this) {
                    @Override
                    public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event other) {
                        if (Lifecycle.Event.ON_CREATE.equals(other)) {
                            activityResultCallback.onActivityResult(activityResultContract.parseResult(activityResult.getResultCode(), activityResult.getData()));
                        }
                    }
                });
            }
        }
        lifecycle.addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event other) {
                if (Lifecycle.Event.ON_DESTROY.equals(other)) {
                    ActivityResultRegistry.this.unregisterActivityResultCallback(s);
                }
            }
        });
        return new ActivityResultLauncher<I>(this, registerKey, activityResultContract, s) {};
    }
    
    public void unregisterActivityResultCallback(final String str) {
        final Integer n = this.mKeyToRc.remove(str);
        if (n != null) {
            this.mRcToKey.remove(n);
        }
        this.mKeyToCallback.remove(str);
        if (this.mPendingResults.containsKey(str)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Dropping pending result for request ");
            sb.append(str);
            sb.append(": ");
            sb.append(this.mPendingResults.getParcelable(str));
            Log.w("ActivityResultRegistry", sb.toString());
        }
    }
    
    private static class CallbackAndContract<O>
    {
        final ActivityResultCallback<O> mCallback;
        final ActivityResultContract<?, O> mContract;
        
        CallbackAndContract(final ActivityResultCallback<O> mCallback, final ActivityResultContract<?, O> mContract) {
            this.mCallback = mCallback;
            this.mContract = mContract;
        }
    }
}
