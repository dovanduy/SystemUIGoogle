// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LaunchCamera_Factory implements Factory<LaunchCamera>
{
    private final Provider<Context> contextProvider;
    
    public LaunchCamera_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static LaunchCamera_Factory create(final Provider<Context> provider) {
        return new LaunchCamera_Factory(provider);
    }
    
    public static LaunchCamera provideInstance(final Provider<Context> provider) {
        return new LaunchCamera(provider.get());
    }
    
    @Override
    public LaunchCamera get() {
        return provideInstance(this.contextProvider);
    }
}
