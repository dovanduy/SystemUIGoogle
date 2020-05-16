// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import com.android.systemui.keyguard.KeyguardViewMediator;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class VolumeDialogComponent_Factory implements Factory<VolumeDialogComponent>
{
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardViewMediator> keyguardViewMediatorProvider;
    private final Provider<VolumeDialogControllerImpl> volumeDialogControllerProvider;
    
    public VolumeDialogComponent_Factory(final Provider<Context> contextProvider, final Provider<KeyguardViewMediator> keyguardViewMediatorProvider, final Provider<VolumeDialogControllerImpl> volumeDialogControllerProvider) {
        this.contextProvider = contextProvider;
        this.keyguardViewMediatorProvider = keyguardViewMediatorProvider;
        this.volumeDialogControllerProvider = volumeDialogControllerProvider;
    }
    
    public static VolumeDialogComponent_Factory create(final Provider<Context> provider, final Provider<KeyguardViewMediator> provider2, final Provider<VolumeDialogControllerImpl> provider3) {
        return new VolumeDialogComponent_Factory(provider, provider2, provider3);
    }
    
    public static VolumeDialogComponent provideInstance(final Provider<Context> provider, final Provider<KeyguardViewMediator> provider2, final Provider<VolumeDialogControllerImpl> provider3) {
        return new VolumeDialogComponent(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public VolumeDialogComponent get() {
        return provideInstance(this.contextProvider, this.keyguardViewMediatorProvider, this.volumeDialogControllerProvider);
    }
}
