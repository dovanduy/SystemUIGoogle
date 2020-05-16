// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.log.LogBuffer;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PreparationCoordinatorLogger_Factory implements Factory<PreparationCoordinatorLogger>
{
    private final Provider<LogBuffer> bufferProvider;
    
    public PreparationCoordinatorLogger_Factory(final Provider<LogBuffer> bufferProvider) {
        this.bufferProvider = bufferProvider;
    }
    
    public static PreparationCoordinatorLogger_Factory create(final Provider<LogBuffer> provider) {
        return new PreparationCoordinatorLogger_Factory(provider);
    }
    
    public static PreparationCoordinatorLogger provideInstance(final Provider<LogBuffer> provider) {
        return new PreparationCoordinatorLogger(provider.get());
    }
    
    @Override
    public PreparationCoordinatorLogger get() {
        return provideInstance(this.bufferProvider);
    }
}
