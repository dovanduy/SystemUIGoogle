// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.input;

import dagger.internal.Preconditions;
import com.google.android.systemui.assist.uihints.TranscriptionController;
import com.google.android.systemui.assist.uihints.IconController;
import javax.inject.Provider;
import java.util.Set;
import dagger.internal.Factory;

public final class InputModule_ProvideTouchActionRegionsFactory implements Factory<Set<TouchActionRegion>>
{
    private final Provider<IconController> iconControllerProvider;
    private final Provider<TranscriptionController> transcriptionControllerProvider;
    
    public InputModule_ProvideTouchActionRegionsFactory(final Provider<IconController> iconControllerProvider, final Provider<TranscriptionController> transcriptionControllerProvider) {
        this.iconControllerProvider = iconControllerProvider;
        this.transcriptionControllerProvider = transcriptionControllerProvider;
    }
    
    public static InputModule_ProvideTouchActionRegionsFactory create(final Provider<IconController> provider, final Provider<TranscriptionController> provider2) {
        return new InputModule_ProvideTouchActionRegionsFactory(provider, provider2);
    }
    
    public static Set<TouchActionRegion> provideInstance(final Provider<IconController> provider, final Provider<TranscriptionController> provider2) {
        return proxyProvideTouchActionRegions(provider.get(), provider2.get());
    }
    
    public static Set<TouchActionRegion> proxyProvideTouchActionRegions(final IconController iconController, final TranscriptionController transcriptionController) {
        final Set<TouchActionRegion> provideTouchActionRegions = InputModule.provideTouchActionRegions(iconController, transcriptionController);
        Preconditions.checkNotNull(provideTouchActionRegions, "Cannot return null from a non-@Nullable @Provides method");
        return provideTouchActionRegions;
    }
    
    @Override
    public Set<TouchActionRegion> get() {
        return provideInstance(this.iconControllerProvider, this.transcriptionControllerProvider);
    }
}
