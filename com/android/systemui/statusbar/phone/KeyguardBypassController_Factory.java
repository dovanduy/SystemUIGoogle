// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.tuner.TunerService;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardBypassController_Factory implements Factory<KeyguardBypassController>
{
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<TunerService> tunerServiceProvider;
    
    public KeyguardBypassController_Factory(final Provider<Context> contextProvider, final Provider<TunerService> tunerServiceProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.contextProvider = contextProvider;
        this.tunerServiceProvider = tunerServiceProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.lockscreenUserManagerProvider = lockscreenUserManagerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static KeyguardBypassController_Factory create(final Provider<Context> provider, final Provider<TunerService> provider2, final Provider<StatusBarStateController> provider3, final Provider<NotificationLockscreenUserManager> provider4, final Provider<KeyguardStateController> provider5, final Provider<DumpManager> provider6) {
        return new KeyguardBypassController_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static KeyguardBypassController provideInstance(final Provider<Context> provider, final Provider<TunerService> provider2, final Provider<StatusBarStateController> provider3, final Provider<NotificationLockscreenUserManager> provider4, final Provider<KeyguardStateController> provider5, final Provider<DumpManager> provider6) {
        return new KeyguardBypassController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get());
    }
    
    @Override
    public KeyguardBypassController get() {
        return provideInstance(this.contextProvider, this.tunerServiceProvider, this.statusBarStateControllerProvider, this.lockscreenUserManagerProvider, this.keyguardStateControllerProvider, this.dumpManagerProvider);
    }
}
