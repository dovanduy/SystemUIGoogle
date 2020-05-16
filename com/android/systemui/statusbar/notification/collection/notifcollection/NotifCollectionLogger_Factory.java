// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.log.LogBuffer;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotifCollectionLogger_Factory implements Factory<NotifCollectionLogger>
{
    private final Provider<LogBuffer> bufferProvider;
    
    public NotifCollectionLogger_Factory(final Provider<LogBuffer> bufferProvider) {
        this.bufferProvider = bufferProvider;
    }
    
    public static NotifCollectionLogger_Factory create(final Provider<LogBuffer> provider) {
        return new NotifCollectionLogger_Factory(provider);
    }
    
    public static NotifCollectionLogger provideInstance(final Provider<LogBuffer> provider) {
        return new NotifCollectionLogger(provider.get());
    }
    
    @Override
    public NotifCollectionLogger get() {
        return provideInstance(this.bufferProvider);
    }
}
