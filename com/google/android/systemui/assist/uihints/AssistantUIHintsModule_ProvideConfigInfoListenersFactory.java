// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.Preconditions;
import javax.inject.Provider;
import java.util.Set;
import dagger.internal.Factory;

public final class AssistantUIHintsModule_ProvideConfigInfoListenersFactory implements Factory<Set<NgaMessageHandler.ConfigInfoListener>>
{
    private final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider;
    private final Provider<ColorChangeHandler> colorChangeHandlerProvider;
    private final Provider<ConfigurationHandler> configurationHandlerProvider;
    private final Provider<KeyboardMonitor> keyboardMonitorProvider;
    private final Provider<TaskStackNotifier> taskStackNotifierProvider;
    private final Provider<TouchInsideHandler> touchInsideHandlerProvider;
    private final Provider<TouchOutsideHandler> touchOutsideHandlerProvider;
    
    public AssistantUIHintsModule_ProvideConfigInfoListenersFactory(final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider, final Provider<TouchInsideHandler> touchInsideHandlerProvider, final Provider<TouchOutsideHandler> touchOutsideHandlerProvider, final Provider<TaskStackNotifier> taskStackNotifierProvider, final Provider<KeyboardMonitor> keyboardMonitorProvider, final Provider<ColorChangeHandler> colorChangeHandlerProvider, final Provider<ConfigurationHandler> configurationHandlerProvider) {
        this.assistantPresenceHandlerProvider = assistantPresenceHandlerProvider;
        this.touchInsideHandlerProvider = touchInsideHandlerProvider;
        this.touchOutsideHandlerProvider = touchOutsideHandlerProvider;
        this.taskStackNotifierProvider = taskStackNotifierProvider;
        this.keyboardMonitorProvider = keyboardMonitorProvider;
        this.colorChangeHandlerProvider = colorChangeHandlerProvider;
        this.configurationHandlerProvider = configurationHandlerProvider;
    }
    
    public static AssistantUIHintsModule_ProvideConfigInfoListenersFactory create(final Provider<AssistantPresenceHandler> provider, final Provider<TouchInsideHandler> provider2, final Provider<TouchOutsideHandler> provider3, final Provider<TaskStackNotifier> provider4, final Provider<KeyboardMonitor> provider5, final Provider<ColorChangeHandler> provider6, final Provider<ConfigurationHandler> provider7) {
        return new AssistantUIHintsModule_ProvideConfigInfoListenersFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7);
    }
    
    public static Set<NgaMessageHandler.ConfigInfoListener> provideInstance(final Provider<AssistantPresenceHandler> provider, final Provider<TouchInsideHandler> provider2, final Provider<TouchOutsideHandler> provider3, final Provider<TaskStackNotifier> provider4, final Provider<KeyboardMonitor> provider5, final Provider<ColorChangeHandler> provider6, final Provider<ConfigurationHandler> provider7) {
        return proxyProvideConfigInfoListeners(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get());
    }
    
    public static Set<NgaMessageHandler.ConfigInfoListener> proxyProvideConfigInfoListeners(final AssistantPresenceHandler assistantPresenceHandler, final TouchInsideHandler touchInsideHandler, final Object o, final Object o2, final Object o3, final ColorChangeHandler colorChangeHandler, final ConfigurationHandler configurationHandler) {
        final Set<NgaMessageHandler.ConfigInfoListener> provideConfigInfoListeners = AssistantUIHintsModule.provideConfigInfoListeners(assistantPresenceHandler, touchInsideHandler, (TouchOutsideHandler)o, (TaskStackNotifier)o2, (KeyboardMonitor)o3, colorChangeHandler, configurationHandler);
        Preconditions.checkNotNull(provideConfigInfoListeners, "Cannot return null from a non-@Nullable @Provides method");
        return provideConfigInfoListeners;
    }
    
    @Override
    public Set<NgaMessageHandler.ConfigInfoListener> get() {
        return provideInstance(this.assistantPresenceHandlerProvider, this.touchInsideHandlerProvider, this.touchOutsideHandlerProvider, this.taskStackNotifierProvider, this.keyboardMonitorProvider, this.colorChangeHandlerProvider, this.configurationHandlerProvider);
    }
}
