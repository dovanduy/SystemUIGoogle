// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.util.DeviceConfigProxy;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationSectionsFeatureManager_Factory implements Factory<NotificationSectionsFeatureManager>
{
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigProxy> proxyProvider;
    
    public NotificationSectionsFeatureManager_Factory(final Provider<DeviceConfigProxy> proxyProvider, final Provider<Context> contextProvider) {
        this.proxyProvider = proxyProvider;
        this.contextProvider = contextProvider;
    }
    
    public static NotificationSectionsFeatureManager_Factory create(final Provider<DeviceConfigProxy> provider, final Provider<Context> provider2) {
        return new NotificationSectionsFeatureManager_Factory(provider, provider2);
    }
    
    public static NotificationSectionsFeatureManager provideInstance(final Provider<DeviceConfigProxy> provider, final Provider<Context> provider2) {
        return new NotificationSectionsFeatureManager(provider.get(), provider2.get());
    }
    
    @Override
    public NotificationSectionsFeatureManager get() {
        return provideInstance(this.proxyProvider, this.contextProvider);
    }
}
