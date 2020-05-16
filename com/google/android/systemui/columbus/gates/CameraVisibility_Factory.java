// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.os.Handler;
import com.google.android.systemui.columbus.actions.Action;
import java.util.List;
import android.content.Context;
import android.app.IActivityManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class CameraVisibility_Factory implements Factory<CameraVisibility>
{
    private final Provider<IActivityManager> activityManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<List<Action>> exceptionsProvider;
    private final Provider<KeyguardVisibility> keyguardGateProvider;
    private final Provider<PowerState> powerStateProvider;
    private final Provider<Handler> updateHandlerProvider;
    
    public CameraVisibility_Factory(final Provider<Context> contextProvider, final Provider<List<Action>> exceptionsProvider, final Provider<KeyguardVisibility> keyguardGateProvider, final Provider<PowerState> powerStateProvider, final Provider<IActivityManager> activityManagerProvider, final Provider<Handler> updateHandlerProvider) {
        this.contextProvider = contextProvider;
        this.exceptionsProvider = exceptionsProvider;
        this.keyguardGateProvider = keyguardGateProvider;
        this.powerStateProvider = powerStateProvider;
        this.activityManagerProvider = activityManagerProvider;
        this.updateHandlerProvider = updateHandlerProvider;
    }
    
    public static CameraVisibility_Factory create(final Provider<Context> provider, final Provider<List<Action>> provider2, final Provider<KeyguardVisibility> provider3, final Provider<PowerState> provider4, final Provider<IActivityManager> provider5, final Provider<Handler> provider6) {
        return new CameraVisibility_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static CameraVisibility provideInstance(final Provider<Context> provider, final Provider<List<Action>> provider2, final Provider<KeyguardVisibility> provider3, final Provider<PowerState> provider4, final Provider<IActivityManager> provider5, final Provider<Handler> provider6) {
        return new CameraVisibility(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get());
    }
    
    @Override
    public CameraVisibility get() {
        return provideInstance(this.contextProvider, this.exceptionsProvider, this.keyguardGateProvider, this.powerStateProvider, this.activityManagerProvider, this.updateHandlerProvider);
    }
}
