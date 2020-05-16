// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NextAlarmControllerImpl_Factory implements Factory<NextAlarmControllerImpl>
{
    private final Provider<Context> contextProvider;
    
    public NextAlarmControllerImpl_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static NextAlarmControllerImpl_Factory create(final Provider<Context> provider) {
        return new NextAlarmControllerImpl_Factory(provider);
    }
    
    public static NextAlarmControllerImpl provideInstance(final Provider<Context> provider) {
        return new NextAlarmControllerImpl(provider.get());
    }
    
    @Override
    public NextAlarmControllerImpl get() {
        return provideInstance(this.contextProvider);
    }
}
