// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AccessibilityManagerWrapper_Factory implements Factory<AccessibilityManagerWrapper>
{
    private final Provider<Context> contextProvider;
    
    public AccessibilityManagerWrapper_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static AccessibilityManagerWrapper_Factory create(final Provider<Context> provider) {
        return new AccessibilityManagerWrapper_Factory(provider);
    }
    
    public static AccessibilityManagerWrapper provideInstance(final Provider<Context> provider) {
        return new AccessibilityManagerWrapper(provider.get());
    }
    
    @Override
    public AccessibilityManagerWrapper get() {
        return provideInstance(this.contextProvider);
    }
}
