// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.assist.AssistManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class TouchInsideHandler_Factory implements Factory<TouchInsideHandler>
{
    private final Provider<AssistManager> assistManagerProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;
    
    public TouchInsideHandler_Factory(final Provider<AssistManager> assistManagerProvider, final Provider<NavigationModeController> navigationModeControllerProvider) {
        this.assistManagerProvider = assistManagerProvider;
        this.navigationModeControllerProvider = navigationModeControllerProvider;
    }
    
    public static TouchInsideHandler_Factory create(final Provider<AssistManager> provider, final Provider<NavigationModeController> provider2) {
        return new TouchInsideHandler_Factory(provider, provider2);
    }
    
    public static TouchInsideHandler provideInstance(final Provider<AssistManager> provider, final Provider<NavigationModeController> provider2) {
        return new TouchInsideHandler(DoubleCheck.lazy(provider), provider2.get());
    }
    
    @Override
    public TouchInsideHandler get() {
        return provideInstance(this.assistManagerProvider, this.navigationModeControllerProvider);
    }
}
