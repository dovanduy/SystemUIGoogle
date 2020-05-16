// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.logging;

import com.android.systemui.log.LogBuffer;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class QSLogger_Factory implements Factory<QSLogger>
{
    private final Provider<LogBuffer> bufferProvider;
    
    public QSLogger_Factory(final Provider<LogBuffer> bufferProvider) {
        this.bufferProvider = bufferProvider;
    }
    
    public static QSLogger_Factory create(final Provider<LogBuffer> provider) {
        return new QSLogger_Factory(provider);
    }
    
    public static QSLogger provideInstance(final Provider<LogBuffer> provider) {
        return new QSLogger(provider.get());
    }
    
    @Override
    public QSLogger get() {
        return provideInstance(this.bufferProvider);
    }
}
