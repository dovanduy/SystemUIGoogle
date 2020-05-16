// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.actions.Action;
import java.util.List;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardDeferredSetup_Factory implements Factory<KeyguardDeferredSetup>
{
    private final Provider<Context> contextProvider;
    private final Provider<List<Action>> exceptionsProvider;
    private final Provider<KeyguardVisibility> keyguardGateProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;
    
    public KeyguardDeferredSetup_Factory(final Provider<Context> contextProvider, final Provider<List<Action>> exceptionsProvider, final Provider<KeyguardVisibility> keyguardGateProvider, final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider) {
        this.contextProvider = contextProvider;
        this.exceptionsProvider = exceptionsProvider;
        this.keyguardGateProvider = keyguardGateProvider;
        this.settingsObserverFactoryProvider = settingsObserverFactoryProvider;
    }
    
    public static KeyguardDeferredSetup_Factory create(final Provider<Context> provider, final Provider<List<Action>> provider2, final Provider<KeyguardVisibility> provider3, final Provider<ColumbusContentObserver.Factory> provider4) {
        return new KeyguardDeferredSetup_Factory(provider, provider2, provider3, provider4);
    }
    
    public static KeyguardDeferredSetup provideInstance(final Provider<Context> provider, final Provider<List<Action>> provider2, final Provider<KeyguardVisibility> provider3, final Provider<ColumbusContentObserver.Factory> provider4) {
        return new KeyguardDeferredSetup(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public KeyguardDeferredSetup get() {
        return provideInstance(this.contextProvider, this.exceptionsProvider, this.keyguardGateProvider, this.settingsObserverFactoryProvider);
    }
}
