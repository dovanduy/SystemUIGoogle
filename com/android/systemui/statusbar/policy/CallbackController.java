// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public interface CallbackController<T>
{
    void addCallback(final T p0);
    
    default T observe(final Lifecycle lifecycle, final T t) {
        lifecycle.addObserver(new _$$Lambda$CallbackController$TlIH8GpCbmJQdNzMgf9ko_xLlUk(this, t));
        return t;
    }
    
    default T observe(final LifecycleOwner lifecycleOwner, final T t) {
        return this.observe(lifecycleOwner.getLifecycle(), t);
    }
    
    void removeCallback(final T p0);
}
