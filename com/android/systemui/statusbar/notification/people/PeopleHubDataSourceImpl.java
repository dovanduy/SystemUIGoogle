// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.people;

import com.android.systemui.R$dimen;
import android.util.IconDrawableFactory;
import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.LauncherApps;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.os.UserManager;
import android.util.SparseArray;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.settingslib.notification.ConversationIconFactory;
import java.util.List;
import java.util.concurrent.Executor;

public final class PeopleHubDataSourceImpl implements DataSource<Object>
{
    private final Executor bgExecutor;
    private final List<Object<Object>> dataListeners;
    private final NotificationPersonExtractor extractor;
    private final ConversationIconFactory iconFactory;
    private final Executor mainExecutor;
    private final NotificationLockscreenUserManager notifLockscreenUserMgr;
    private final NotificationListener notificationListener;
    private final SparseArray<Object> peopleHubManagerForUser;
    private final PeopleNotificationIdentifier peopleNotificationIdentifier;
    private final UserManager userManager;
    
    public PeopleHubDataSourceImpl(final NotificationEntryManager notificationEntryManager, final NotificationPersonExtractor extractor, final UserManager userManager, final LauncherApps launcherApps, final PackageManager packageManager, final Context context, final NotificationListener notificationListener, final Executor bgExecutor, final Executor mainExecutor, final NotificationLockscreenUserManager notifLockscreenUserMgr, final PeopleNotificationIdentifier peopleNotificationIdentifier) {
        Intrinsics.checkParameterIsNotNull(notificationEntryManager, "notificationEntryManager");
        Intrinsics.checkParameterIsNotNull(extractor, "extractor");
        Intrinsics.checkParameterIsNotNull(userManager, "userManager");
        Intrinsics.checkParameterIsNotNull(launcherApps, "launcherApps");
        Intrinsics.checkParameterIsNotNull(packageManager, "packageManager");
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(notificationListener, "notificationListener");
        Intrinsics.checkParameterIsNotNull(bgExecutor, "bgExecutor");
        Intrinsics.checkParameterIsNotNull(mainExecutor, "mainExecutor");
        Intrinsics.checkParameterIsNotNull(notifLockscreenUserMgr, "notifLockscreenUserMgr");
        Intrinsics.checkParameterIsNotNull(peopleNotificationIdentifier, "peopleNotificationIdentifier");
        this.extractor = extractor;
        this.userManager = userManager;
        this.notificationListener = notificationListener;
        this.bgExecutor = bgExecutor;
        this.mainExecutor = mainExecutor;
        this.notifLockscreenUserMgr = notifLockscreenUserMgr;
        this.peopleNotificationIdentifier = peopleNotificationIdentifier;
        this.dataListeners = new ArrayList<Object<Object>>();
        this.peopleHubManagerForUser = (SparseArray<Object>)new SparseArray();
        final Context applicationContext = context.getApplicationContext();
        final IconDrawableFactory instance = IconDrawableFactory.newInstance(applicationContext);
        Intrinsics.checkExpressionValueIsNotNull(applicationContext, "appContext");
        this.iconFactory = new ConversationIconFactory(applicationContext, launcherApps, packageManager, instance, applicationContext.getResources().getDimensionPixelSize(R$dimen.notification_guts_conversation_icon_size));
    }
}
