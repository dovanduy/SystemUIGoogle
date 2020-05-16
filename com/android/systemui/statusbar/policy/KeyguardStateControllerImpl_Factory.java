// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardStateControllerImpl_Factory implements Factory<KeyguardStateControllerImpl>
{
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    
    public KeyguardStateControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<LockPatternUtils> lockPatternUtilsProvider) {
        this.contextProvider = contextProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.lockPatternUtilsProvider = lockPatternUtilsProvider;
    }
    
    public static KeyguardStateControllerImpl_Factory create(final Provider<Context> provider, final Provider<KeyguardUpdateMonitor> provider2, final Provider<LockPatternUtils> provider3) {
        return new KeyguardStateControllerImpl_Factory(provider, provider2, provider3);
    }
    
    public static KeyguardStateControllerImpl provideInstance(final Provider<Context> provider, final Provider<KeyguardUpdateMonitor> provider2, final Provider<LockPatternUtils> provider3) {
        return new KeyguardStateControllerImpl(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public KeyguardStateControllerImpl get() {
        return provideInstance(this.contextProvider, this.keyguardUpdateMonitorProvider, this.lockPatternUtilsProvider);
    }
}
