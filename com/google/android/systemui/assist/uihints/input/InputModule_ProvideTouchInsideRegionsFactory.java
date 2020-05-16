// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.input;

import dagger.internal.Preconditions;
import com.google.android.systemui.assist.uihints.TranscriptionController;
import com.google.android.systemui.assist.uihints.ScrimController;
import com.google.android.systemui.assist.uihints.GlowController;
import javax.inject.Provider;
import java.util.Set;
import dagger.internal.Factory;

public final class InputModule_ProvideTouchInsideRegionsFactory implements Factory<Set<TouchInsideRegion>>
{
    private final Provider<GlowController> glowControllerProvider;
    private final Provider<ScrimController> scrimControllerProvider;
    private final Provider<TranscriptionController> transcriptionControllerProvider;
    
    public InputModule_ProvideTouchInsideRegionsFactory(final Provider<GlowController> glowControllerProvider, final Provider<ScrimController> scrimControllerProvider, final Provider<TranscriptionController> transcriptionControllerProvider) {
        this.glowControllerProvider = glowControllerProvider;
        this.scrimControllerProvider = scrimControllerProvider;
        this.transcriptionControllerProvider = transcriptionControllerProvider;
    }
    
    public static InputModule_ProvideTouchInsideRegionsFactory create(final Provider<GlowController> provider, final Provider<ScrimController> provider2, final Provider<TranscriptionController> provider3) {
        return new InputModule_ProvideTouchInsideRegionsFactory(provider, provider2, provider3);
    }
    
    public static Set<TouchInsideRegion> provideInstance(final Provider<GlowController> provider, final Provider<ScrimController> provider2, final Provider<TranscriptionController> provider3) {
        return proxyProvideTouchInsideRegions(provider.get(), provider2.get(), provider3.get());
    }
    
    public static Set<TouchInsideRegion> proxyProvideTouchInsideRegions(final GlowController glowController, final ScrimController scrimController, final TranscriptionController transcriptionController) {
        final Set<TouchInsideRegion> provideTouchInsideRegions = InputModule.provideTouchInsideRegions(glowController, scrimController, transcriptionController);
        Preconditions.checkNotNull(provideTouchInsideRegions, "Cannot return null from a non-@Nullable @Provides method");
        return provideTouchInsideRegions;
    }
    
    @Override
    public Set<TouchInsideRegion> get() {
        return provideInstance(this.glowControllerProvider, this.scrimControllerProvider, this.transcriptionControllerProvider);
    }
}
