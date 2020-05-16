// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.provider;

import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class HighPriorityProvider_Factory implements Factory<HighPriorityProvider>
{
    private final Provider<PeopleNotificationIdentifier> peopleNotificationIdentifierProvider;
    
    public HighPriorityProvider_Factory(final Provider<PeopleNotificationIdentifier> peopleNotificationIdentifierProvider) {
        this.peopleNotificationIdentifierProvider = peopleNotificationIdentifierProvider;
    }
    
    public static HighPriorityProvider_Factory create(final Provider<PeopleNotificationIdentifier> provider) {
        return new HighPriorityProvider_Factory(provider);
    }
    
    public static HighPriorityProvider provideInstance(final Provider<PeopleNotificationIdentifier> provider) {
        return new HighPriorityProvider(provider.get());
    }
    
    @Override
    public HighPriorityProvider get() {
        return provideInstance(this.peopleNotificationIdentifierProvider);
    }
}
