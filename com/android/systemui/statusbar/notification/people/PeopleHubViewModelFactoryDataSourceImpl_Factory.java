// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.people;

import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PeopleHubViewModelFactoryDataSourceImpl_Factory implements Factory<PeopleHubViewModelFactoryDataSourceImpl>
{
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<DataSource<Object>> dataSourceProvider;
    
    public PeopleHubViewModelFactoryDataSourceImpl_Factory(final Provider<ActivityStarter> activityStarterProvider, final Provider<DataSource<Object>> dataSourceProvider) {
        this.activityStarterProvider = activityStarterProvider;
        this.dataSourceProvider = dataSourceProvider;
    }
    
    public static PeopleHubViewModelFactoryDataSourceImpl_Factory create(final Provider<ActivityStarter> provider, final Provider<DataSource<Object>> provider2) {
        return new PeopleHubViewModelFactoryDataSourceImpl_Factory(provider, provider2);
    }
    
    public static PeopleHubViewModelFactoryDataSourceImpl provideInstance(final Provider<ActivityStarter> provider, final Provider<DataSource<Object>> provider2) {
        return new PeopleHubViewModelFactoryDataSourceImpl(provider.get(), provider2.get());
    }
    
    @Override
    public PeopleHubViewModelFactoryDataSourceImpl get() {
        return provideInstance(this.activityStarterProvider, this.dataSourceProvider);
    }
}
