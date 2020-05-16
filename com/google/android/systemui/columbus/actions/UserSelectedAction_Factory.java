// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import java.util.Map;
import android.os.Handler;
import com.android.systemui.assist.DeviceConfigHelper;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class UserSelectedAction_Factory implements Factory<UserSelectedAction>
{
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<LaunchOpa> launchOpaProvider;
    private final Provider<Map<String, Action>> userSelectedActionsProvider;
    
    public UserSelectedAction_Factory(final Provider<Context> contextProvider, final Provider<DeviceConfigHelper> deviceConfigHelperProvider, final Provider<Map<String, Action>> userSelectedActionsProvider, final Provider<LaunchOpa> launchOpaProvider, final Provider<Handler> handlerProvider) {
        this.contextProvider = contextProvider;
        this.deviceConfigHelperProvider = deviceConfigHelperProvider;
        this.userSelectedActionsProvider = userSelectedActionsProvider;
        this.launchOpaProvider = launchOpaProvider;
        this.handlerProvider = handlerProvider;
    }
    
    public static UserSelectedAction_Factory create(final Provider<Context> provider, final Provider<DeviceConfigHelper> provider2, final Provider<Map<String, Action>> provider3, final Provider<LaunchOpa> provider4, final Provider<Handler> provider5) {
        return new UserSelectedAction_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static UserSelectedAction provideInstance(final Provider<Context> provider, final Provider<DeviceConfigHelper> provider2, final Provider<Map<String, Action>> provider3, final Provider<LaunchOpa> provider4, final Provider<Handler> provider5) {
        return new UserSelectedAction(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public UserSelectedAction get() {
        return provideInstance(this.contextProvider, this.deviceConfigHelperProvider, this.userSelectedActionsProvider, this.launchOpaProvider, this.handlerProvider);
    }
}
