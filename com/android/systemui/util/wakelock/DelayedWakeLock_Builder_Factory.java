// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.wakelock;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DelayedWakeLock_Builder_Factory implements Factory<DelayedWakeLock.Builder>
{
    private final Provider<Context> contextProvider;
    
    public DelayedWakeLock_Builder_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static DelayedWakeLock_Builder_Factory create(final Provider<Context> provider) {
        return new DelayedWakeLock_Builder_Factory(provider);
    }
    
    public static DelayedWakeLock.Builder provideInstance(final Provider<Context> provider) {
        return new DelayedWakeLock.Builder(provider.get());
    }
    
    @Override
    public DelayedWakeLock.Builder get() {
        return provideInstance(this.contextProvider);
    }
}
