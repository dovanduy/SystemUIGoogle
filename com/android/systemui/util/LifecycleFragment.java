// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import android.os.Bundle;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LifecycleOwner;
import android.app.Fragment;

public class LifecycleFragment extends Fragment implements LifecycleOwner
{
    private final LifecycleRegistry mLifecycle;
    
    public LifecycleFragment() {
        this.mLifecycle = new LifecycleRegistry(this);
    }
    
    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }
    
    public void onCreate(final Bundle bundle) {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        super.onCreate(bundle);
    }
    
    public void onDestroy() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        super.onDestroy();
    }
    
    public void onPause() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        super.onPause();
    }
    
    public void onResume() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        super.onResume();
    }
    
    public void onStart() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
        super.onStart();
    }
    
    public void onStop() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        super.onStop();
    }
}
