// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.os.Handler;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class UserSwitcherController_Factory implements Factory<UserSwitcherController>
{
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    
    public UserSwitcherController_Factory(final Provider<Context> contextProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<Handler> handlerProvider, final Provider<ActivityStarter> activityStarterProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.contextProvider = contextProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.handlerProvider = handlerProvider;
        this.activityStarterProvider = activityStarterProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static UserSwitcherController_Factory create(final Provider<Context> provider, final Provider<KeyguardStateController> provider2, final Provider<Handler> provider3, final Provider<ActivityStarter> provider4, final Provider<BroadcastDispatcher> provider5) {
        return new UserSwitcherController_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static UserSwitcherController provideInstance(final Provider<Context> provider, final Provider<KeyguardStateController> provider2, final Provider<Handler> provider3, final Provider<ActivityStarter> provider4, final Provider<BroadcastDispatcher> provider5) {
        return new UserSwitcherController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public UserSwitcherController get() {
        return provideInstance(this.contextProvider, this.keyguardStateControllerProvider, this.handlerProvider, this.activityStarterProvider, this.broadcastDispatcherProvider);
    }
}
