// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.log.LogBuffer;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotifBindPipelineLogger_Factory implements Factory<NotifBindPipelineLogger>
{
    private final Provider<LogBuffer> bufferProvider;
    
    public NotifBindPipelineLogger_Factory(final Provider<LogBuffer> bufferProvider) {
        this.bufferProvider = bufferProvider;
    }
    
    public static NotifBindPipelineLogger_Factory create(final Provider<LogBuffer> provider) {
        return new NotifBindPipelineLogger_Factory(provider);
    }
    
    public static NotifBindPipelineLogger provideInstance(final Provider<LogBuffer> provider) {
        return new NotifBindPipelineLogger(provider.get());
    }
    
    @Override
    public NotifBindPipelineLogger get() {
        return provideInstance(this.bufferProvider);
    }
}
