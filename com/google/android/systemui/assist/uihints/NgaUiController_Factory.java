// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.DoubleCheck;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NavigationBarController;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;
import android.content.Context;
import com.android.systemui.assist.AssistManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NgaUiController_Factory implements Factory<NgaUiController>
{
    private final Provider<AssistManager> assistManagerProvider;
    private final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider;
    private final Provider<AssistantWarmer> assistantWarmerProvider;
    private final Provider<ColorChangeHandler> colorChangeHandlerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<EdgeLightsController> edgeLightsControllerProvider;
    private final Provider<FlingVelocityWrapper> flingVelocityProvider;
    private final Provider<GlowController> glowControllerProvider;
    private final Provider<IconController> iconControllerProvider;
    private final Provider<LightnessProvider> lightnessProvider;
    private final Provider<NavigationBarController> navigationBarControllerProvider;
    private final Provider<ScrimController> scrimControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<TimeoutManager> timeoutManagerProvider;
    private final Provider<TouchInsideHandler> touchInsideHandlerProvider;
    private final Provider<TranscriptionController> transcriptionControllerProvider;
    private final Provider<OverlayUiHost> uiHostProvider;
    
    public NgaUiController_Factory(final Provider<Context> contextProvider, final Provider<TimeoutManager> timeoutManagerProvider, final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider, final Provider<TouchInsideHandler> touchInsideHandlerProvider, final Provider<ColorChangeHandler> colorChangeHandlerProvider, final Provider<OverlayUiHost> uiHostProvider, final Provider<EdgeLightsController> edgeLightsControllerProvider, final Provider<GlowController> glowControllerProvider, final Provider<ScrimController> scrimControllerProvider, final Provider<TranscriptionController> transcriptionControllerProvider, final Provider<IconController> iconControllerProvider, final Provider<LightnessProvider> lightnessProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<AssistManager> assistManagerProvider, final Provider<NavigationBarController> navigationBarControllerProvider, final Provider<FlingVelocityWrapper> flingVelocityProvider, final Provider<AssistantWarmer> assistantWarmerProvider) {
        this.contextProvider = contextProvider;
        this.timeoutManagerProvider = timeoutManagerProvider;
        this.assistantPresenceHandlerProvider = assistantPresenceHandlerProvider;
        this.touchInsideHandlerProvider = touchInsideHandlerProvider;
        this.colorChangeHandlerProvider = colorChangeHandlerProvider;
        this.uiHostProvider = uiHostProvider;
        this.edgeLightsControllerProvider = edgeLightsControllerProvider;
        this.glowControllerProvider = glowControllerProvider;
        this.scrimControllerProvider = scrimControllerProvider;
        this.transcriptionControllerProvider = transcriptionControllerProvider;
        this.iconControllerProvider = iconControllerProvider;
        this.lightnessProvider = lightnessProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.assistManagerProvider = assistManagerProvider;
        this.navigationBarControllerProvider = navigationBarControllerProvider;
        this.flingVelocityProvider = flingVelocityProvider;
        this.assistantWarmerProvider = assistantWarmerProvider;
    }
    
    public static NgaUiController_Factory create(final Provider<Context> provider, final Provider<TimeoutManager> provider2, final Provider<AssistantPresenceHandler> provider3, final Provider<TouchInsideHandler> provider4, final Provider<ColorChangeHandler> provider5, final Provider<OverlayUiHost> provider6, final Provider<EdgeLightsController> provider7, final Provider<GlowController> provider8, final Provider<ScrimController> provider9, final Provider<TranscriptionController> provider10, final Provider<IconController> provider11, final Provider<LightnessProvider> provider12, final Provider<StatusBarStateController> provider13, final Provider<AssistManager> provider14, final Provider<NavigationBarController> provider15, final Provider<FlingVelocityWrapper> provider16, final Provider<AssistantWarmer> provider17) {
        return new NgaUiController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17);
    }
    
    public static NgaUiController provideInstance(final Provider<Context> provider, final Provider<TimeoutManager> provider2, final Provider<AssistantPresenceHandler> provider3, final Provider<TouchInsideHandler> provider4, final Provider<ColorChangeHandler> provider5, final Provider<OverlayUiHost> provider6, final Provider<EdgeLightsController> provider7, final Provider<GlowController> provider8, final Provider<ScrimController> provider9, final Provider<TranscriptionController> provider10, final Provider<IconController> provider11, final Provider<LightnessProvider> provider12, final Provider<StatusBarStateController> provider13, final Provider<AssistManager> provider14, final Provider<NavigationBarController> provider15, final Provider<FlingVelocityWrapper> provider16, final Provider<AssistantWarmer> provider17) {
        return new NgaUiController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), DoubleCheck.lazy(provider14), DoubleCheck.lazy(provider15), provider16.get(), provider17.get());
    }
    
    @Override
    public NgaUiController get() {
        return provideInstance(this.contextProvider, this.timeoutManagerProvider, this.assistantPresenceHandlerProvider, this.touchInsideHandlerProvider, this.colorChangeHandlerProvider, this.uiHostProvider, this.edgeLightsControllerProvider, this.glowControllerProvider, this.scrimControllerProvider, this.transcriptionControllerProvider, this.iconControllerProvider, this.lightnessProvider, this.statusBarStateControllerProvider, this.assistManagerProvider, this.navigationBarControllerProvider, this.flingVelocityProvider, this.assistantWarmerProvider);
    }
}
