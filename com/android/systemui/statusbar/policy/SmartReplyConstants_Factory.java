// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.os.Handler;
import com.android.systemui.util.DeviceConfigProxy;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SmartReplyConstants_Factory implements Factory<SmartReplyConstants>
{
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigProxy> deviceConfigProvider;
    private final Provider<Handler> handlerProvider;
    
    public SmartReplyConstants_Factory(final Provider<Handler> handlerProvider, final Provider<Context> contextProvider, final Provider<DeviceConfigProxy> deviceConfigProvider) {
        this.handlerProvider = handlerProvider;
        this.contextProvider = contextProvider;
        this.deviceConfigProvider = deviceConfigProvider;
    }
    
    public static SmartReplyConstants_Factory create(final Provider<Handler> provider, final Provider<Context> provider2, final Provider<DeviceConfigProxy> provider3) {
        return new SmartReplyConstants_Factory(provider, provider2, provider3);
    }
    
    public static SmartReplyConstants provideInstance(final Provider<Handler> provider, final Provider<Context> provider2, final Provider<DeviceConfigProxy> provider3) {
        return new SmartReplyConstants(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public SmartReplyConstants get() {
        return provideInstance(this.handlerProvider, this.contextProvider, this.deviceConfigProvider);
    }
}
