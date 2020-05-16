// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.Preconditions;
import com.google.android.systemui.columbus.actions.TakeScreenshot;
import com.google.android.systemui.columbus.actions.ManageMedia;
import com.google.android.systemui.columbus.actions.LaunchOverview;
import com.google.android.systemui.columbus.actions.LaunchOpa;
import com.google.android.systemui.columbus.actions.LaunchCamera;
import javax.inject.Provider;
import com.google.android.systemui.columbus.actions.Action;
import java.util.Map;
import dagger.internal.Factory;

public final class ColumbusModule_ProvideUserSelectedActionsFactory implements Factory<Map<String, Action>>
{
    private final Provider<LaunchCamera> launchCameraProvider;
    private final Provider<LaunchOpa> launchOpaProvider;
    private final Provider<LaunchOverview> launchOverviewProvider;
    private final Provider<ManageMedia> manageMediaProvider;
    private final Provider<TakeScreenshot> takeScreenshotProvider;
    
    public ColumbusModule_ProvideUserSelectedActionsFactory(final Provider<LaunchOpa> launchOpaProvider, final Provider<LaunchCamera> launchCameraProvider, final Provider<ManageMedia> manageMediaProvider, final Provider<TakeScreenshot> takeScreenshotProvider, final Provider<LaunchOverview> launchOverviewProvider) {
        this.launchOpaProvider = launchOpaProvider;
        this.launchCameraProvider = launchCameraProvider;
        this.manageMediaProvider = manageMediaProvider;
        this.takeScreenshotProvider = takeScreenshotProvider;
        this.launchOverviewProvider = launchOverviewProvider;
    }
    
    public static ColumbusModule_ProvideUserSelectedActionsFactory create(final Provider<LaunchOpa> provider, final Provider<LaunchCamera> provider2, final Provider<ManageMedia> provider3, final Provider<TakeScreenshot> provider4, final Provider<LaunchOverview> provider5) {
        return new ColumbusModule_ProvideUserSelectedActionsFactory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static Map<String, Action> provideInstance(final Provider<LaunchOpa> provider, final Provider<LaunchCamera> provider2, final Provider<ManageMedia> provider3, final Provider<TakeScreenshot> provider4, final Provider<LaunchOverview> provider5) {
        return proxyProvideUserSelectedActions(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    public static Map<String, Action> proxyProvideUserSelectedActions(final LaunchOpa launchOpa, final LaunchCamera launchCamera, final ManageMedia manageMedia, final TakeScreenshot takeScreenshot, final LaunchOverview launchOverview) {
        final Map<String, Action> provideUserSelectedActions = ColumbusModule.provideUserSelectedActions(launchOpa, launchCamera, manageMedia, takeScreenshot, launchOverview);
        Preconditions.checkNotNull(provideUserSelectedActions, "Cannot return null from a non-@Nullable @Provides method");
        return provideUserSelectedActions;
    }
    
    @Override
    public Map<String, Action> get() {
        return provideInstance(this.launchOpaProvider, this.launchCameraProvider, this.manageMediaProvider, this.takeScreenshotProvider, this.launchOverviewProvider);
    }
}
