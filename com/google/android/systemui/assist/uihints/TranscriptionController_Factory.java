// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.view.ViewGroup;
import com.android.systemui.statusbar.policy.ConfigurationController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class TranscriptionController_Factory implements Factory<TranscriptionController>
{
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<TouchInsideHandler> defaultOnTapProvider;
    private final Provider<FlingVelocityWrapper> flingVelocityProvider;
    private final Provider<ViewGroup> parentProvider;
    
    public TranscriptionController_Factory(final Provider<ViewGroup> parentProvider, final Provider<TouchInsideHandler> defaultOnTapProvider, final Provider<FlingVelocityWrapper> flingVelocityProvider, final Provider<ConfigurationController> configurationControllerProvider) {
        this.parentProvider = parentProvider;
        this.defaultOnTapProvider = defaultOnTapProvider;
        this.flingVelocityProvider = flingVelocityProvider;
        this.configurationControllerProvider = configurationControllerProvider;
    }
    
    public static TranscriptionController_Factory create(final Provider<ViewGroup> provider, final Provider<TouchInsideHandler> provider2, final Provider<FlingVelocityWrapper> provider3, final Provider<ConfigurationController> provider4) {
        return new TranscriptionController_Factory(provider, provider2, provider3, provider4);
    }
    
    public static TranscriptionController provideInstance(final Provider<ViewGroup> provider, final Provider<TouchInsideHandler> provider2, final Provider<FlingVelocityWrapper> provider3, final Provider<ConfigurationController> provider4) {
        return new TranscriptionController(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public TranscriptionController get() {
        return provideInstance(this.parentProvider, this.defaultOnTapProvider, this.flingVelocityProvider, this.configurationControllerProvider);
    }
}
