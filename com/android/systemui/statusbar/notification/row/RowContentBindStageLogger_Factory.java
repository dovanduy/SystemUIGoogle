// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.log.LogBuffer;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class RowContentBindStageLogger_Factory implements Factory<RowContentBindStageLogger>
{
    private final Provider<LogBuffer> bufferProvider;
    
    public RowContentBindStageLogger_Factory(final Provider<LogBuffer> bufferProvider) {
        this.bufferProvider = bufferProvider;
    }
    
    public static RowContentBindStageLogger_Factory create(final Provider<LogBuffer> provider) {
        return new RowContentBindStageLogger_Factory(provider);
    }
    
    public static RowContentBindStageLogger provideInstance(final Provider<LogBuffer> provider) {
        return new RowContentBindStageLogger(provider.get());
    }
    
    @Override
    public RowContentBindStageLogger get() {
        return provideInstance(this.bufferProvider);
    }
}
