// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.Preconditions;
import com.google.android.systemui.assist.uihints.input.NgaInputHandler;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;
import javax.inject.Provider;
import java.util.Set;
import dagger.internal.Factory;

public final class AssistantUIHintsModule_BindEdgeLightsInfoListenersFactory implements Factory<Set<NgaMessageHandler.EdgeLightsInfoListener>>
{
    private final Provider<EdgeLightsController> edgeLightsControllerProvider;
    private final Provider<NgaInputHandler> ngaInputHandlerProvider;
    
    public AssistantUIHintsModule_BindEdgeLightsInfoListenersFactory(final Provider<EdgeLightsController> edgeLightsControllerProvider, final Provider<NgaInputHandler> ngaInputHandlerProvider) {
        this.edgeLightsControllerProvider = edgeLightsControllerProvider;
        this.ngaInputHandlerProvider = ngaInputHandlerProvider;
    }
    
    public static AssistantUIHintsModule_BindEdgeLightsInfoListenersFactory create(final Provider<EdgeLightsController> provider, final Provider<NgaInputHandler> provider2) {
        return new AssistantUIHintsModule_BindEdgeLightsInfoListenersFactory(provider, provider2);
    }
    
    public static Set<NgaMessageHandler.EdgeLightsInfoListener> provideInstance(final Provider<EdgeLightsController> provider, final Provider<NgaInputHandler> provider2) {
        return proxyBindEdgeLightsInfoListeners(provider.get(), provider2.get());
    }
    
    public static Set<NgaMessageHandler.EdgeLightsInfoListener> proxyBindEdgeLightsInfoListeners(final EdgeLightsController edgeLightsController, final NgaInputHandler ngaInputHandler) {
        final Set<NgaMessageHandler.EdgeLightsInfoListener> bindEdgeLightsInfoListeners = AssistantUIHintsModule.bindEdgeLightsInfoListeners(edgeLightsController, ngaInputHandler);
        Preconditions.checkNotNull(bindEdgeLightsInfoListeners, "Cannot return null from a non-@Nullable @Provides method");
        return bindEdgeLightsInfoListeners;
    }
    
    @Override
    public Set<NgaMessageHandler.EdgeLightsInfoListener> get() {
        return provideInstance(this.edgeLightsControllerProvider, this.ngaInputHandlerProvider);
    }
}
