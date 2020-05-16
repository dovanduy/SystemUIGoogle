// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import com.android.internal.logging.MetricsLogger;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import com.google.android.systemui.columbus.gates.Gate;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.Set;
import com.google.android.systemui.columbus.actions.Action;
import java.util.List;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ColumbusService_Factory implements Factory<ColumbusService>
{
    private final Provider<List<Action>> actionsProvider;
    private final Provider<Set<FeedbackEffect>> effectsProvider;
    private final Provider<Set<Gate>> gatesProvider;
    private final Provider<GestureSensor> gestureSensorProvider;
    private final Provider<MetricsLogger> loggerProvider;
    private final Provider<PowerManagerWrapper> powerManagerProvider;
    
    public ColumbusService_Factory(final Provider<List<Action>> actionsProvider, final Provider<Set<FeedbackEffect>> effectsProvider, final Provider<Set<Gate>> gatesProvider, final Provider<GestureSensor> gestureSensorProvider, final Provider<PowerManagerWrapper> powerManagerProvider, final Provider<MetricsLogger> loggerProvider) {
        this.actionsProvider = actionsProvider;
        this.effectsProvider = effectsProvider;
        this.gatesProvider = gatesProvider;
        this.gestureSensorProvider = gestureSensorProvider;
        this.powerManagerProvider = powerManagerProvider;
        this.loggerProvider = loggerProvider;
    }
    
    public static ColumbusService_Factory create(final Provider<List<Action>> provider, final Provider<Set<FeedbackEffect>> provider2, final Provider<Set<Gate>> provider3, final Provider<GestureSensor> provider4, final Provider<PowerManagerWrapper> provider5, final Provider<MetricsLogger> provider6) {
        return new ColumbusService_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static ColumbusService provideInstance(final Provider<List<Action>> provider, final Provider<Set<FeedbackEffect>> provider2, final Provider<Set<Gate>> provider3, final Provider<GestureSensor> provider4, final Provider<PowerManagerWrapper> provider5, final Provider<MetricsLogger> provider6) {
        return new ColumbusService(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get());
    }
    
    @Override
    public ColumbusService get() {
        return provideInstance(this.actionsProvider, this.effectsProvider, this.gatesProvider, this.gestureSensorProvider, this.powerManagerProvider, this.loggerProvider);
    }
}
