// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.logging;

import java.util.concurrent.Executor;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationLogger_ExpansionStateLogger_Factory implements Factory<NotificationLogger.ExpansionStateLogger>
{
    private final Provider<Executor> uiBgExecutorProvider;
    
    public NotificationLogger_ExpansionStateLogger_Factory(final Provider<Executor> uiBgExecutorProvider) {
        this.uiBgExecutorProvider = uiBgExecutorProvider;
    }
    
    public static NotificationLogger_ExpansionStateLogger_Factory create(final Provider<Executor> provider) {
        return new NotificationLogger_ExpansionStateLogger_Factory(provider);
    }
    
    public static NotificationLogger.ExpansionStateLogger provideInstance(final Provider<Executor> provider) {
        return new NotificationLogger.ExpansionStateLogger(provider.get());
    }
    
    @Override
    public NotificationLogger.ExpansionStateLogger get() {
        return provideInstance(this.uiBgExecutorProvider);
    }
}
