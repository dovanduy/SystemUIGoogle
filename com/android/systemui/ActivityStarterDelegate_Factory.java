// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Optional;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ActivityStarterDelegate_Factory implements Factory<ActivityStarterDelegate>
{
    private final Provider<Optional<Lazy<StatusBar>>> statusBarProvider;
    
    public ActivityStarterDelegate_Factory(final Provider<Optional<Lazy<StatusBar>>> statusBarProvider) {
        this.statusBarProvider = statusBarProvider;
    }
    
    public static ActivityStarterDelegate_Factory create(final Provider<Optional<Lazy<StatusBar>>> provider) {
        return new ActivityStarterDelegate_Factory(provider);
    }
    
    public static ActivityStarterDelegate provideInstance(final Provider<Optional<Lazy<StatusBar>>> provider) {
        return new ActivityStarterDelegate(provider.get());
    }
    
    @Override
    public ActivityStarterDelegate get() {
        return provideInstance(this.statusBarProvider);
    }
}
