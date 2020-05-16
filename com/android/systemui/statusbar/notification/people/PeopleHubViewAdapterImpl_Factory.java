// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.people;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class PeopleHubViewAdapterImpl_Factory implements Factory<PeopleHubViewAdapterImpl>
{
    private final Provider<DataSource<Object>> dataSourceProvider;
    
    public PeopleHubViewAdapterImpl_Factory(final Provider<DataSource<Object>> dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }
    
    public static PeopleHubViewAdapterImpl_Factory create(final Provider<DataSource<Object>> provider) {
        return new PeopleHubViewAdapterImpl_Factory(provider);
    }
    
    public static PeopleHubViewAdapterImpl provideInstance(final Provider<DataSource<Object>> provider) {
        return new PeopleHubViewAdapterImpl(provider.get());
    }
    
    @Override
    public PeopleHubViewAdapterImpl get() {
        return provideInstance(this.dataSourceProvider);
    }
}
