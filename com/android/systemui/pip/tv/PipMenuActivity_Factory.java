// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.tv;

import com.android.systemui.pip.tv.dagger.TvPipComponent;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PipMenuActivity_Factory implements Factory<PipMenuActivity>
{
    private final Provider<TvPipComponent.Builder> pipComponentBuilderProvider;
    private final Provider<PipManager> pipManagerProvider;
    
    public PipMenuActivity_Factory(final Provider<TvPipComponent.Builder> pipComponentBuilderProvider, final Provider<PipManager> pipManagerProvider) {
        this.pipComponentBuilderProvider = pipComponentBuilderProvider;
        this.pipManagerProvider = pipManagerProvider;
    }
    
    public static PipMenuActivity_Factory create(final Provider<TvPipComponent.Builder> provider, final Provider<PipManager> provider2) {
        return new PipMenuActivity_Factory(provider, provider2);
    }
    
    public static PipMenuActivity provideInstance(final Provider<TvPipComponent.Builder> provider, final Provider<PipManager> provider2) {
        return new PipMenuActivity(provider.get(), provider2.get());
    }
    
    @Override
    public PipMenuActivity get() {
        return provideInstance(this.pipComponentBuilderProvider, this.pipManagerProvider);
    }
}
