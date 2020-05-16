// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.Preconditions;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;
import javax.inject.Provider;
import java.util.Set;
import dagger.internal.Factory;

public final class AssistantUIHintsModule_ProvideAudioInfoListenersFactory implements Factory<Set<NgaMessageHandler.AudioInfoListener>>
{
    private final Provider<EdgeLightsController> edgeLightsControllerProvider;
    private final Provider<GlowController> glowControllerProvider;
    
    public AssistantUIHintsModule_ProvideAudioInfoListenersFactory(final Provider<EdgeLightsController> edgeLightsControllerProvider, final Provider<GlowController> glowControllerProvider) {
        this.edgeLightsControllerProvider = edgeLightsControllerProvider;
        this.glowControllerProvider = glowControllerProvider;
    }
    
    public static AssistantUIHintsModule_ProvideAudioInfoListenersFactory create(final Provider<EdgeLightsController> provider, final Provider<GlowController> provider2) {
        return new AssistantUIHintsModule_ProvideAudioInfoListenersFactory(provider, provider2);
    }
    
    public static Set<NgaMessageHandler.AudioInfoListener> provideInstance(final Provider<EdgeLightsController> provider, final Provider<GlowController> provider2) {
        return proxyProvideAudioInfoListeners(provider.get(), provider2.get());
    }
    
    public static Set<NgaMessageHandler.AudioInfoListener> proxyProvideAudioInfoListeners(final EdgeLightsController edgeLightsController, final GlowController glowController) {
        final Set<NgaMessageHandler.AudioInfoListener> provideAudioInfoListeners = AssistantUIHintsModule.provideAudioInfoListeners(edgeLightsController, glowController);
        Preconditions.checkNotNull(provideAudioInfoListeners, "Cannot return null from a non-@Nullable @Provides method");
        return provideAudioInfoListeners;
    }
    
    @Override
    public Set<NgaMessageHandler.AudioInfoListener> get() {
        return provideInstance(this.edgeLightsControllerProvider, this.glowControllerProvider);
    }
}
