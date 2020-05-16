// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import android.os.UserManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class TakeScreenshotService_Factory implements Factory<TakeScreenshotService>
{
    private final Provider<GlobalScreenshotLegacy> globalScreenshotLegacyProvider;
    private final Provider<GlobalScreenshot> globalScreenshotProvider;
    private final Provider<UserManager> userManagerProvider;
    
    public TakeScreenshotService_Factory(final Provider<GlobalScreenshot> globalScreenshotProvider, final Provider<GlobalScreenshotLegacy> globalScreenshotLegacyProvider, final Provider<UserManager> userManagerProvider) {
        this.globalScreenshotProvider = globalScreenshotProvider;
        this.globalScreenshotLegacyProvider = globalScreenshotLegacyProvider;
        this.userManagerProvider = userManagerProvider;
    }
    
    public static TakeScreenshotService_Factory create(final Provider<GlobalScreenshot> provider, final Provider<GlobalScreenshotLegacy> provider2, final Provider<UserManager> provider3) {
        return new TakeScreenshotService_Factory(provider, provider2, provider3);
    }
    
    public static TakeScreenshotService provideInstance(final Provider<GlobalScreenshot> provider, final Provider<GlobalScreenshotLegacy> provider2, final Provider<UserManager> provider3) {
        return new TakeScreenshotService(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public TakeScreenshotService get() {
        return provideInstance(this.globalScreenshotProvider, this.globalScreenshotLegacyProvider, this.userManagerProvider);
    }
}
