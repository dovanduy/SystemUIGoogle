// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.view.WindowManager;
import android.content.Context;
import javax.inject.Provider;
import android.util.DisplayMetrics;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideDisplayMetricsFactory implements Factory<DisplayMetrics>
{
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;
    private final Provider<WindowManager> windowManagerProvider;
    
    public DependencyProvider_ProvideDisplayMetricsFactory(final DependencyProvider module, final Provider<Context> contextProvider, final Provider<WindowManager> windowManagerProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
        this.windowManagerProvider = windowManagerProvider;
    }
    
    public static DependencyProvider_ProvideDisplayMetricsFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider, final Provider<WindowManager> provider2) {
        return new DependencyProvider_ProvideDisplayMetricsFactory(dependencyProvider, provider, provider2);
    }
    
    public static DisplayMetrics provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider, final Provider<WindowManager> provider2) {
        return proxyProvideDisplayMetrics(dependencyProvider, provider.get(), provider2.get());
    }
    
    public static DisplayMetrics proxyProvideDisplayMetrics(final DependencyProvider dependencyProvider, final Context context, final WindowManager windowManager) {
        final DisplayMetrics provideDisplayMetrics = dependencyProvider.provideDisplayMetrics(context, windowManager);
        Preconditions.checkNotNull(provideDisplayMetrics, "Cannot return null from a non-@Nullable @Provides method");
        return provideDisplayMetrics;
    }
    
    @Override
    public DisplayMetrics get() {
        return provideInstance(this.module, this.contextProvider, this.windowManagerProvider);
    }
}
