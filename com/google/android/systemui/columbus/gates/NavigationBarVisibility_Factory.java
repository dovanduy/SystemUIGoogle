// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import com.google.android.systemui.columbus.actions.Action;
import java.util.List;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.assist.AssistManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NavigationBarVisibility_Factory implements Factory<NavigationBarVisibility>
{
    private final Provider<AssistManager> assistManagerProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<List<Action>> exceptionsProvider;
    private final Provider<KeyguardVisibility> keyguardGateProvider;
    private final Provider<NonGesturalNavigation> navigationModeGateProvider;
    
    public NavigationBarVisibility_Factory(final Provider<Context> contextProvider, final Provider<List<Action>> exceptionsProvider, final Provider<AssistManager> assistManagerProvider, final Provider<KeyguardVisibility> keyguardGateProvider, final Provider<NonGesturalNavigation> navigationModeGateProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.contextProvider = contextProvider;
        this.exceptionsProvider = exceptionsProvider;
        this.assistManagerProvider = assistManagerProvider;
        this.keyguardGateProvider = keyguardGateProvider;
        this.navigationModeGateProvider = navigationModeGateProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static NavigationBarVisibility_Factory create(final Provider<Context> provider, final Provider<List<Action>> provider2, final Provider<AssistManager> provider3, final Provider<KeyguardVisibility> provider4, final Provider<NonGesturalNavigation> provider5, final Provider<CommandQueue> provider6) {
        return new NavigationBarVisibility_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static NavigationBarVisibility provideInstance(final Provider<Context> provider, final Provider<List<Action>> provider2, final Provider<AssistManager> provider3, final Provider<KeyguardVisibility> provider4, final Provider<NonGesturalNavigation> provider5, final Provider<CommandQueue> provider6) {
        return new NavigationBarVisibility(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get());
    }
    
    @Override
    public NavigationBarVisibility get() {
        return provideInstance(this.contextProvider, this.exceptionsProvider, this.assistManagerProvider, this.keyguardGateProvider, this.navigationModeGateProvider, this.commandQueueProvider);
    }
}
