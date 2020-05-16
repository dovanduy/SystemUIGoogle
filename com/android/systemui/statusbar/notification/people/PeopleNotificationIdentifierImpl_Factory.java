// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.people;

import com.android.systemui.statusbar.phone.NotificationGroupManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PeopleNotificationIdentifierImpl_Factory implements Factory<PeopleNotificationIdentifierImpl>
{
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<NotificationPersonExtractor> personExtractorProvider;
    
    public PeopleNotificationIdentifierImpl_Factory(final Provider<NotificationPersonExtractor> personExtractorProvider, final Provider<NotificationGroupManager> groupManagerProvider) {
        this.personExtractorProvider = personExtractorProvider;
        this.groupManagerProvider = groupManagerProvider;
    }
    
    public static PeopleNotificationIdentifierImpl_Factory create(final Provider<NotificationPersonExtractor> provider, final Provider<NotificationGroupManager> provider2) {
        return new PeopleNotificationIdentifierImpl_Factory(provider, provider2);
    }
    
    public static PeopleNotificationIdentifierImpl provideInstance(final Provider<NotificationPersonExtractor> provider, final Provider<NotificationGroupManager> provider2) {
        return new PeopleNotificationIdentifierImpl(provider.get(), provider2.get());
    }
    
    @Override
    public PeopleNotificationIdentifierImpl get() {
        return provideInstance(this.personExtractorProvider, this.groupManagerProvider);
    }
}
