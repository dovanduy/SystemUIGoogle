// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogBuffer;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationEntryManagerLogger_Factory implements Factory<NotificationEntryManagerLogger>
{
    private final Provider<LogBuffer> bufferProvider;
    
    public NotificationEntryManagerLogger_Factory(final Provider<LogBuffer> bufferProvider) {
        this.bufferProvider = bufferProvider;
    }
    
    public static NotificationEntryManagerLogger_Factory create(final Provider<LogBuffer> provider) {
        return new NotificationEntryManagerLogger_Factory(provider);
    }
    
    public static NotificationEntryManagerLogger provideInstance(final Provider<LogBuffer> provider) {
        return new NotificationEntryManagerLogger(provider.get());
    }
    
    @Override
    public NotificationEntryManagerLogger get() {
        return provideInstance(this.bufferProvider);
    }
}
