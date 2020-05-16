// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import android.view.WindowManager;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ScreenshotNotificationsController_Factory implements Factory<ScreenshotNotificationsController>
{
    private final Provider<Context> contextProvider;
    private final Provider<WindowManager> windowManagerProvider;
    
    public ScreenshotNotificationsController_Factory(final Provider<Context> contextProvider, final Provider<WindowManager> windowManagerProvider) {
        this.contextProvider = contextProvider;
        this.windowManagerProvider = windowManagerProvider;
    }
    
    public static ScreenshotNotificationsController_Factory create(final Provider<Context> provider, final Provider<WindowManager> provider2) {
        return new ScreenshotNotificationsController_Factory(provider, provider2);
    }
    
    public static ScreenshotNotificationsController provideInstance(final Provider<Context> provider, final Provider<WindowManager> provider2) {
        return new ScreenshotNotificationsController(provider.get(), provider2.get());
    }
    
    @Override
    public ScreenshotNotificationsController get() {
        return provideInstance(this.contextProvider, this.windowManagerProvider);
    }
}
