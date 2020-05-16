// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.people;

import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.plugins.ActivityStarter;

public final class PeopleHubViewModelFactoryDataSourceImpl implements DataSource<Object>
{
    public PeopleHubViewModelFactoryDataSourceImpl(final ActivityStarter activityStarter, final DataSource<Object> dataSource) {
        Intrinsics.checkParameterIsNotNull(activityStarter, "activityStarter");
        Intrinsics.checkParameterIsNotNull(dataSource, "dataSource");
    }
}
