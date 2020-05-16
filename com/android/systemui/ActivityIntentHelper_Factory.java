// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ActivityIntentHelper_Factory implements Factory<ActivityIntentHelper>
{
    private final Provider<Context> contextProvider;
    
    public ActivityIntentHelper_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static ActivityIntentHelper_Factory create(final Provider<Context> provider) {
        return new ActivityIntentHelper_Factory(provider);
    }
    
    public static ActivityIntentHelper provideInstance(final Provider<Context> provider) {
        return new ActivityIntentHelper(provider.get());
    }
    
    @Override
    public ActivityIntentHelper get() {
        return provideInstance(this.contextProvider);
    }
}
