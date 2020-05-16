// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.phone.StatusBar;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class OverlappedElementController_Factory implements Factory<OverlappedElementController>
{
    private final Provider<StatusBar> statusBarLazyProvider;
    
    public OverlappedElementController_Factory(final Provider<StatusBar> statusBarLazyProvider) {
        this.statusBarLazyProvider = statusBarLazyProvider;
    }
    
    public static OverlappedElementController_Factory create(final Provider<StatusBar> provider) {
        return new OverlappedElementController_Factory(provider);
    }
    
    public static OverlappedElementController provideInstance(final Provider<StatusBar> provider) {
        return new OverlappedElementController(DoubleCheck.lazy(provider));
    }
    
    @Override
    public OverlappedElementController get() {
        return provideInstance(this.statusBarLazyProvider);
    }
}
