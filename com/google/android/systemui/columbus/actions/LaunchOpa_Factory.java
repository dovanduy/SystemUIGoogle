// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.android.systemui.tuner.TunerService;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.Set;
import android.content.Context;
import com.android.systemui.assist.AssistManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LaunchOpa_Factory implements Factory<LaunchOpa>
{
    private final Provider<AssistManager> assistManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Set<FeedbackEffect>> feedbackEffectsProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;
    private final Provider<StatusBar> statusBarProvider;
    private final Provider<TunerService> tunerServiceProvider;
    
    public LaunchOpa_Factory(final Provider<Context> contextProvider, final Provider<StatusBar> statusBarProvider, final Provider<Set<FeedbackEffect>> feedbackEffectsProvider, final Provider<AssistManager> assistManagerProvider, final Provider<TunerService> tunerServiceProvider, final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider) {
        this.contextProvider = contextProvider;
        this.statusBarProvider = statusBarProvider;
        this.feedbackEffectsProvider = feedbackEffectsProvider;
        this.assistManagerProvider = assistManagerProvider;
        this.tunerServiceProvider = tunerServiceProvider;
        this.settingsObserverFactoryProvider = settingsObserverFactoryProvider;
    }
    
    public static LaunchOpa_Factory create(final Provider<Context> provider, final Provider<StatusBar> provider2, final Provider<Set<FeedbackEffect>> provider3, final Provider<AssistManager> provider4, final Provider<TunerService> provider5, final Provider<ColumbusContentObserver.Factory> provider6) {
        return new LaunchOpa_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static LaunchOpa provideInstance(final Provider<Context> provider, final Provider<StatusBar> provider2, final Provider<Set<FeedbackEffect>> provider3, final Provider<AssistManager> provider4, final Provider<TunerService> provider5, final Provider<ColumbusContentObserver.Factory> provider6) {
        return new LaunchOpa(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get());
    }
    
    @Override
    public LaunchOpa get() {
        return provideInstance(this.contextProvider, this.statusBarProvider, this.feedbackEffectsProvider, this.assistManagerProvider, this.tunerServiceProvider, this.settingsObserverFactoryProvider);
    }
}
