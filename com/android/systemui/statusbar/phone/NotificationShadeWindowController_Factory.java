// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.WindowManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import android.app.IActivityManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationShadeWindowController_Factory implements Factory<NotificationShadeWindowController>
{
    private final Provider<IActivityManager> activityManagerProvider;
    private final Provider<SysuiColorExtractor> colorExtractorProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<WindowManager> windowManagerProvider;
    
    public NotificationShadeWindowController_Factory(final Provider<Context> contextProvider, final Provider<WindowManager> windowManagerProvider, final Provider<IActivityManager> activityManagerProvider, final Provider<DozeParameters> dozeParametersProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<ConfigurationController> configurationControllerProvider, final Provider<KeyguardBypassController> keyguardBypassControllerProvider, final Provider<SysuiColorExtractor> colorExtractorProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.contextProvider = contextProvider;
        this.windowManagerProvider = windowManagerProvider;
        this.activityManagerProvider = activityManagerProvider;
        this.dozeParametersProvider = dozeParametersProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.keyguardBypassControllerProvider = keyguardBypassControllerProvider;
        this.colorExtractorProvider = colorExtractorProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static NotificationShadeWindowController_Factory create(final Provider<Context> provider, final Provider<WindowManager> provider2, final Provider<IActivityManager> provider3, final Provider<DozeParameters> provider4, final Provider<StatusBarStateController> provider5, final Provider<ConfigurationController> provider6, final Provider<KeyguardBypassController> provider7, final Provider<SysuiColorExtractor> provider8, final Provider<DumpManager> provider9) {
        return new NotificationShadeWindowController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
    }
    
    public static NotificationShadeWindowController provideInstance(final Provider<Context> provider, final Provider<WindowManager> provider2, final Provider<IActivityManager> provider3, final Provider<DozeParameters> provider4, final Provider<StatusBarStateController> provider5, final Provider<ConfigurationController> provider6, final Provider<KeyguardBypassController> provider7, final Provider<SysuiColorExtractor> provider8, final Provider<DumpManager> provider9) {
        return new NotificationShadeWindowController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get());
    }
    
    @Override
    public NotificationShadeWindowController get() {
        return provideInstance(this.contextProvider, this.windowManagerProvider, this.activityManagerProvider, this.dozeParametersProvider, this.statusBarStateControllerProvider, this.configurationControllerProvider, this.keyguardBypassControllerProvider, this.colorExtractorProvider, this.dumpManagerProvider);
    }
}
