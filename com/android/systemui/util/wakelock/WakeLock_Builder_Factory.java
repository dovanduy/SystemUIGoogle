// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.wakelock;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class WakeLock_Builder_Factory implements Factory<WakeLock.Builder>
{
    private final Provider<Context> contextProvider;
    
    public WakeLock_Builder_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static WakeLock_Builder_Factory create(final Provider<Context> provider) {
        return new WakeLock_Builder_Factory(provider);
    }
    
    public static WakeLock.Builder provideInstance(final Provider<Context> provider) {
        return new WakeLock.Builder(provider.get());
    }
    
    @Override
    public WakeLock.Builder get() {
        return provideInstance(this.contextProvider);
    }
}
