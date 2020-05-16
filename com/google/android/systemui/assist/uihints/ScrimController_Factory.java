// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.view.ViewGroup;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ScrimController_Factory implements Factory<ScrimController>
{
    private final Provider<LightnessProvider> lightnessProvider;
    private final Provider<OverlappedElementController> overlappedElementControllerProvider;
    private final Provider<ViewGroup> parentProvider;
    private final Provider<TouchInsideHandler> touchInsideHandlerProvider;
    
    public ScrimController_Factory(final Provider<ViewGroup> parentProvider, final Provider<OverlappedElementController> overlappedElementControllerProvider, final Provider<LightnessProvider> lightnessProvider, final Provider<TouchInsideHandler> touchInsideHandlerProvider) {
        this.parentProvider = parentProvider;
        this.overlappedElementControllerProvider = overlappedElementControllerProvider;
        this.lightnessProvider = lightnessProvider;
        this.touchInsideHandlerProvider = touchInsideHandlerProvider;
    }
    
    public static ScrimController_Factory create(final Provider<ViewGroup> provider, final Provider<OverlappedElementController> provider2, final Provider<LightnessProvider> provider3, final Provider<TouchInsideHandler> provider4) {
        return new ScrimController_Factory(provider, provider2, provider3, provider4);
    }
    
    public static ScrimController provideInstance(final Provider<ViewGroup> provider, final Provider<OverlappedElementController> provider2, final Provider<LightnessProvider> provider3, final Provider<TouchInsideHandler> provider4) {
        return new ScrimController(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public ScrimController get() {
        return provideInstance(this.parentProvider, this.overlappedElementControllerProvider, this.lightnessProvider, this.touchInsideHandlerProvider);
    }
}
