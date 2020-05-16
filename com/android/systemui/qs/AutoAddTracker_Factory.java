// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AutoAddTracker_Factory implements Factory<AutoAddTracker>
{
    private final Provider<Context> contextProvider;
    
    public AutoAddTracker_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static AutoAddTracker_Factory create(final Provider<Context> provider) {
        return new AutoAddTracker_Factory(provider);
    }
    
    public static AutoAddTracker provideInstance(final Provider<Context> provider) {
        return new AutoAddTracker(provider.get());
    }
    
    @Override
    public AutoAddTracker get() {
        return provideInstance(this.contextProvider);
    }
}
