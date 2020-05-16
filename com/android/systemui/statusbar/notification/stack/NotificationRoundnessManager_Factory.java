// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationRoundnessManager_Factory implements Factory<NotificationRoundnessManager>
{
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<NotificationSectionsFeatureManager> sectionsFeatureManagerProvider;
    
    public NotificationRoundnessManager_Factory(final Provider<KeyguardBypassController> keyguardBypassControllerProvider, final Provider<NotificationSectionsFeatureManager> sectionsFeatureManagerProvider) {
        this.keyguardBypassControllerProvider = keyguardBypassControllerProvider;
        this.sectionsFeatureManagerProvider = sectionsFeatureManagerProvider;
    }
    
    public static NotificationRoundnessManager_Factory create(final Provider<KeyguardBypassController> provider, final Provider<NotificationSectionsFeatureManager> provider2) {
        return new NotificationRoundnessManager_Factory(provider, provider2);
    }
    
    public static NotificationRoundnessManager provideInstance(final Provider<KeyguardBypassController> provider, final Provider<NotificationSectionsFeatureManager> provider2) {
        return new NotificationRoundnessManager(provider.get(), provider2.get());
    }
    
    @Override
    public NotificationRoundnessManager get() {
        return provideInstance(this.keyguardBypassControllerProvider, this.sectionsFeatureManagerProvider);
    }
}
