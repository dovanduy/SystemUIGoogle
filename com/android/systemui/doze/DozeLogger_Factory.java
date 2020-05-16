// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import com.android.systemui.log.LogBuffer;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DozeLogger_Factory implements Factory<DozeLogger>
{
    private final Provider<LogBuffer> bufferProvider;
    
    public DozeLogger_Factory(final Provider<LogBuffer> bufferProvider) {
        this.bufferProvider = bufferProvider;
    }
    
    public static DozeLogger_Factory create(final Provider<LogBuffer> provider) {
        return new DozeLogger_Factory(provider);
    }
    
    public static DozeLogger provideInstance(final Provider<LogBuffer> provider) {
        return new DozeLogger(provider.get());
    }
    
    @Override
    public DozeLogger get() {
        return provideInstance(this.bufferProvider);
    }
}
