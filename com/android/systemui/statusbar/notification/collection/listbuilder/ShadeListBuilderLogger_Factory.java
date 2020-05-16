// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogBuffer;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ShadeListBuilderLogger_Factory implements Factory<ShadeListBuilderLogger>
{
    private final Provider<LogBuffer> bufferProvider;
    
    public ShadeListBuilderLogger_Factory(final Provider<LogBuffer> bufferProvider) {
        this.bufferProvider = bufferProvider;
    }
    
    public static ShadeListBuilderLogger_Factory create(final Provider<LogBuffer> provider) {
        return new ShadeListBuilderLogger_Factory(provider);
    }
    
    public static ShadeListBuilderLogger provideInstance(final Provider<LogBuffer> provider) {
        return new ShadeListBuilderLogger(provider.get());
    }
    
    @Override
    public ShadeListBuilderLogger get() {
        return provideInstance(this.bufferProvider);
    }
}
