// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GlobalScreenshot_Factory implements Factory<GlobalScreenshot>
{
    private final Provider<Context> contextProvider;
    private final Provider<LayoutInflater> layoutInflaterProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<ScreenshotNotificationsController> screenshotNotificationsControllerProvider;
    
    public GlobalScreenshot_Factory(final Provider<Context> contextProvider, final Provider<Resources> resourcesProvider, final Provider<LayoutInflater> layoutInflaterProvider, final Provider<ScreenshotNotificationsController> screenshotNotificationsControllerProvider) {
        this.contextProvider = contextProvider;
        this.resourcesProvider = resourcesProvider;
        this.layoutInflaterProvider = layoutInflaterProvider;
        this.screenshotNotificationsControllerProvider = screenshotNotificationsControllerProvider;
    }
    
    public static GlobalScreenshot_Factory create(final Provider<Context> provider, final Provider<Resources> provider2, final Provider<LayoutInflater> provider3, final Provider<ScreenshotNotificationsController> provider4) {
        return new GlobalScreenshot_Factory(provider, provider2, provider3, provider4);
    }
    
    public static GlobalScreenshot provideInstance(final Provider<Context> provider, final Provider<Resources> provider2, final Provider<LayoutInflater> provider3, final Provider<ScreenshotNotificationsController> provider4) {
        return new GlobalScreenshot(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public GlobalScreenshot get() {
        return provideInstance(this.contextProvider, this.resourcesProvider, this.layoutInflaterProvider, this.screenshotNotificationsControllerProvider);
    }
}
