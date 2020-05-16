// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AccessibilityController_Factory implements Factory<AccessibilityController>
{
    private final Provider<Context> contextProvider;
    
    public AccessibilityController_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static AccessibilityController_Factory create(final Provider<Context> provider) {
        return new AccessibilityController_Factory(provider);
    }
    
    public static AccessibilityController provideInstance(final Provider<Context> provider) {
        return new AccessibilityController(provider.get());
    }
    
    @Override
    public AccessibilityController get() {
        return provideInstance(this.contextProvider);
    }
}
