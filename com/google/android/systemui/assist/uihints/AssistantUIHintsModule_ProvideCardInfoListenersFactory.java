// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.Preconditions;
import javax.inject.Provider;
import java.util.Set;
import dagger.internal.Factory;

public final class AssistantUIHintsModule_ProvideCardInfoListenersFactory implements Factory<Set<NgaMessageHandler.CardInfoListener>>
{
    private final Provider<GlowController> glowControllerProvider;
    private final Provider<LightnessProvider> lightnessProvider;
    private final Provider<ScrimController> scrimControllerProvider;
    private final Provider<TranscriptionController> transcriptionControllerProvider;
    
    public AssistantUIHintsModule_ProvideCardInfoListenersFactory(final Provider<GlowController> glowControllerProvider, final Provider<ScrimController> scrimControllerProvider, final Provider<TranscriptionController> transcriptionControllerProvider, final Provider<LightnessProvider> lightnessProvider) {
        this.glowControllerProvider = glowControllerProvider;
        this.scrimControllerProvider = scrimControllerProvider;
        this.transcriptionControllerProvider = transcriptionControllerProvider;
        this.lightnessProvider = lightnessProvider;
    }
    
    public static AssistantUIHintsModule_ProvideCardInfoListenersFactory create(final Provider<GlowController> provider, final Provider<ScrimController> provider2, final Provider<TranscriptionController> provider3, final Provider<LightnessProvider> provider4) {
        return new AssistantUIHintsModule_ProvideCardInfoListenersFactory(provider, provider2, provider3, provider4);
    }
    
    public static Set<NgaMessageHandler.CardInfoListener> provideInstance(final Provider<GlowController> provider, final Provider<ScrimController> provider2, final Provider<TranscriptionController> provider3, final Provider<LightnessProvider> provider4) {
        return proxyProvideCardInfoListeners(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    public static Set<NgaMessageHandler.CardInfoListener> proxyProvideCardInfoListeners(final GlowController glowController, final ScrimController scrimController, final TranscriptionController transcriptionController, final Object o) {
        final Set<NgaMessageHandler.CardInfoListener> provideCardInfoListeners = AssistantUIHintsModule.provideCardInfoListeners(glowController, scrimController, transcriptionController, (LightnessProvider)o);
        Preconditions.checkNotNull(provideCardInfoListeners, "Cannot return null from a non-@Nullable @Provides method");
        return provideCardInfoListeners;
    }
    
    @Override
    public Set<NgaMessageHandler.CardInfoListener> get() {
        return provideInstance(this.glowControllerProvider, this.scrimControllerProvider, this.transcriptionControllerProvider, this.lightnessProvider);
    }
}
