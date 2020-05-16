// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.util.DisplayMetrics;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class FlingAnimationUtils_Builder_Factory implements Factory<FlingAnimationUtils.Builder>
{
    private final Provider<DisplayMetrics> displayMetricsProvider;
    
    public FlingAnimationUtils_Builder_Factory(final Provider<DisplayMetrics> displayMetricsProvider) {
        this.displayMetricsProvider = displayMetricsProvider;
    }
    
    public static FlingAnimationUtils_Builder_Factory create(final Provider<DisplayMetrics> provider) {
        return new FlingAnimationUtils_Builder_Factory(provider);
    }
    
    public static FlingAnimationUtils.Builder provideInstance(final Provider<DisplayMetrics> provider) {
        return new FlingAnimationUtils.Builder(provider.get());
    }
    
    @Override
    public FlingAnimationUtils.Builder get() {
        return provideInstance(this.displayMetricsProvider);
    }
}
