// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import android.os.PersistableBundle;
import android.content.Context;
import android.os.Bundle;
import com.android.settingslib.core.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import android.app.Activity;

public class LifecycleActivity extends Activity implements LifecycleOwner
{
    private final Lifecycle lifecycle;
    
    public LifecycleActivity() {
        this.lifecycle = new Lifecycle(this);
    }
    
    public Lifecycle getLifecycle() {
        return this.lifecycle;
    }
    
    protected void onCreate(final Bundle bundle) {
        this.lifecycle.onAttach((Context)this);
        this.lifecycle.onCreate(bundle);
        this.lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_CREATE);
        super.onCreate(bundle);
    }
    
    public void onCreate(final Bundle bundle, final PersistableBundle persistableBundle) {
        this.lifecycle.onAttach((Context)this);
        this.lifecycle.onCreate(bundle);
        this.lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_CREATE);
        super.onCreate(bundle, persistableBundle);
    }
    
    protected void onDestroy() {
        this.lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_DESTROY);
        super.onDestroy();
    }
    
    protected void onPause() {
        this.lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_PAUSE);
        super.onPause();
    }
    
    protected void onResume() {
        this.lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_RESUME);
        super.onResume();
    }
    
    protected void onStart() {
        this.lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_START);
        super.onStart();
    }
    
    protected void onStop() {
        this.lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_STOP);
        super.onStop();
    }
}
