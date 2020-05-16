// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.view.View;
import java.util.Collection;
import java.util.Set;
import android.view.WindowManager$LayoutParams;
import android.view.Window;
import android.view.WindowInsets$Type;
import android.graphics.drawable.ColorDrawable;
import android.view.View$OnClickListener;
import android.widget.TextView;
import com.android.systemui.R$id;
import android.content.DialogInterface$OnDismissListener;
import com.android.systemui.R$layout;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Comparator;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.SequencesKt;
import kotlin.sequences.Sequence;
import android.content.pm.ParceledListSlice;
import kotlin.collections.CollectionsKt;
import java.util.Iterator;
import android.util.Log;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;
import android.app.INotificationManager;
import java.util.HashMap;
import android.app.NotificationChannel;
import java.util.Map;
import android.app.Dialog;
import android.content.Context;
import android.app.NotificationChannelGroup;
import java.util.List;
import android.graphics.drawable.Drawable;

public final class ChannelEditorDialogController
{
    private Drawable appIcon;
    private String appName;
    private boolean appNotificationsEnabled;
    private Integer appUid;
    private final List<NotificationChannelGroup> channelGroupList;
    private final Context context;
    public Dialog dialog;
    private final Map<NotificationChannel, Integer> edits;
    private final HashMap<String, CharSequence> groupNameLookup;
    private final INotificationManager noMan;
    private OnChannelEditorDialogFinishedListener onFinishListener;
    private NotificationInfo.OnSettingsClickListener onSettingsClickListener;
    private String packageName;
    private final List<NotificationChannel> providedChannels;
    private final int wmFlags;
    
    public ChannelEditorDialogController(Context applicationContext, final INotificationManager noMan) {
        Intrinsics.checkParameterIsNotNull(applicationContext, "c");
        Intrinsics.checkParameterIsNotNull(noMan, "noMan");
        this.noMan = noMan;
        applicationContext = applicationContext.getApplicationContext();
        Intrinsics.checkExpressionValueIsNotNull(applicationContext, "c.applicationContext");
        this.context = applicationContext;
        this.providedChannels = new ArrayList<NotificationChannel>();
        this.edits = new LinkedHashMap<NotificationChannel, Integer>();
        this.appNotificationsEnabled = true;
        this.groupNameLookup = new HashMap<String, CharSequence>();
        this.channelGroupList = new ArrayList<NotificationChannelGroup>();
        this.wmFlags = -2130444032;
    }
    
    private final void applyAppNotificationsOn(final boolean b) {
        try {
            final INotificationManager noMan = this.noMan;
            final String packageName = this.packageName;
            if (packageName == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            final Integer appUid = this.appUid;
            if (appUid == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            noMan.setNotificationsEnabledForPackage(packageName, (int)appUid, b);
        }
        catch (Exception ex) {
            Log.e("ChannelDialogController", "Error calling NoMan", (Throwable)ex);
        }
    }
    
    private final void buildGroupNameLookup() {
        for (final NotificationChannelGroup notificationChannelGroup : this.channelGroupList) {
            if (notificationChannelGroup.getId() != null) {
                final HashMap<String, CharSequence> groupNameLookup = this.groupNameLookup;
                final String id = notificationChannelGroup.getId();
                Intrinsics.checkExpressionValueIsNotNull(id, "group.id");
                final CharSequence name = notificationChannelGroup.getName();
                Intrinsics.checkExpressionValueIsNotNull(name, "group.name");
                groupNameLookup.put(id, name);
            }
        }
    }
    
    private final boolean checkAreAppNotificationsOn() {
        boolean notificationsEnabledForPackage;
        try {
            final INotificationManager noMan = this.noMan;
            final String packageName = this.packageName;
            if (packageName == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            final Integer appUid = this.appUid;
            if (appUid == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            notificationsEnabledForPackage = noMan.areNotificationsEnabledForPackage(packageName, (int)appUid);
        }
        catch (Exception ex) {
            Log.e("ChannelDialogController", "Error calling NoMan", (Throwable)ex);
            notificationsEnabledForPackage = false;
        }
        return notificationsEnabledForPackage;
    }
    
    private final void done() {
        this.resetState();
        final Dialog dialog = this.dialog;
        if (dialog != null) {
            dialog.dismiss();
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("dialog");
        throw null;
    }
    
    private final List<NotificationChannelGroup> fetchNotificationChannelGroups() {
        List<NotificationChannelGroup> list;
        try {
            final INotificationManager noMan = this.noMan;
            final String packageName = this.packageName;
            list = null;
            if (packageName == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            final Integer appUid = this.appUid;
            if (appUid == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            final ParceledListSlice notificationChannelGroupsForPackage = noMan.getNotificationChannelGroupsForPackage(packageName, (int)appUid, false);
            Intrinsics.checkExpressionValueIsNotNull(notificationChannelGroupsForPackage, "noMan.getNotificationCha\u2026eName!!, appUid!!, false)");
            final List list2 = notificationChannelGroupsForPackage.getList();
            if (list2 instanceof List) {
                list = (List<NotificationChannelGroup>)list2;
            }
            if (list == null) {
                list = CollectionsKt.emptyList();
            }
        }
        catch (Exception ex) {
            Log.e("ChannelDialogController", "Error fetching channel groups", (Throwable)ex);
            list = CollectionsKt.emptyList();
        }
        return list;
    }
    
    private final Sequence<NotificationChannel> getDisplayableChannels(final Sequence<NotificationChannelGroup> sequence) {
        return SequencesKt.sortedWith(SequencesKt.flatMap((Sequence<?>)sequence, (Function1<? super Object, ? extends Sequence<? extends NotificationChannel>>)ChannelEditorDialogController$getDisplayableChannels$channels.ChannelEditorDialogController$getDisplayableChannels$channels$1.INSTANCE), (Comparator<? super NotificationChannel>)new ChannelEditorDialogController$getDisplayableChannels$$inlined$compareBy.ChannelEditorDialogController$getDisplayableChannels$$inlined$compareBy$1());
    }
    
    private final void initDialog() {
        final Dialog dialog = new Dialog(this.context);
        this.dialog = dialog;
        final Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(1);
        }
        final Dialog dialog2 = this.dialog;
        if (dialog2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
            throw null;
        }
        dialog2.setTitle((CharSequence)" ");
        final Dialog dialog3 = this.dialog;
        if (dialog3 != null) {
            dialog3.setContentView(R$layout.notif_half_shelf);
            dialog3.setCanceledOnTouchOutside(true);
            dialog3.setOnDismissListener((DialogInterface$OnDismissListener)new ChannelEditorDialogController$initDialog$$inlined$apply$lambda.ChannelEditorDialogController$initDialog$$inlined$apply$lambda$1(this));
            final ChannelEditorListView channelEditorListView = (ChannelEditorListView)dialog3.findViewById(R$id.half_shelf_container);
            channelEditorListView.setController(this);
            channelEditorListView.setAppIcon(this.appIcon);
            channelEditorListView.setAppName(this.appName);
            channelEditorListView.setChannels(this.providedChannels);
            final TextView textView = (TextView)dialog3.findViewById(R$id.done_button);
            if (textView != null) {
                textView.setOnClickListener((View$OnClickListener)new ChannelEditorDialogController$initDialog$$inlined$apply$lambda.ChannelEditorDialogController$initDialog$$inlined$apply$lambda$2(this));
            }
            final TextView textView2 = (TextView)dialog3.findViewById(R$id.see_more_button);
            if (textView2 != null) {
                textView2.setOnClickListener((View$OnClickListener)new ChannelEditorDialogController$initDialog$$inlined$apply$lambda.ChannelEditorDialogController$initDialog$$inlined$apply$lambda$3(this));
            }
            final Window window2 = dialog3.getWindow();
            if (window2 != null) {
                window2.setBackgroundDrawable((Drawable)new ColorDrawable(0));
                window2.addFlags(this.wmFlags);
                window2.setType(2017);
                window2.setWindowAnimations(16973910);
                final WindowManager$LayoutParams attributes = window2.getAttributes();
                attributes.format = -3;
                attributes.setTitle((CharSequence)ChannelEditorDialogController.class.getSimpleName());
                attributes.gravity = 81;
                final WindowManager$LayoutParams attributes2 = window2.getAttributes();
                Intrinsics.checkExpressionValueIsNotNull(attributes2, "attributes");
                attributes.setFitInsetsTypes(attributes2.getFitInsetsTypes() & WindowInsets$Type.statusBars());
                attributes.width = -1;
                attributes.height = -2;
                window2.setAttributes(attributes);
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("dialog");
        throw null;
    }
    
    private final void padToFourChannels(final Set<NotificationChannel> set) {
        this.providedChannels.clear();
        CollectionsKt.addAll((Collection<? super Object>)this.providedChannels, SequencesKt.take(CollectionsKt.asSequence((Iterable<?>)set), 4));
        CollectionsKt.addAll((Collection<? super Object>)this.providedChannels, SequencesKt.take(SequencesKt.distinct(SequencesKt.filterNot((Sequence<?>)this.getDisplayableChannels(CollectionsKt.asSequence((Iterable<? extends NotificationChannelGroup>)this.channelGroupList)), (Function1<?, Boolean>)new ChannelEditorDialogController$padToFourChannels.ChannelEditorDialogController$padToFourChannels$1(this))), 4 - this.providedChannels.size()));
        if (this.providedChannels.size() == 1 && Intrinsics.areEqual("miscellaneous", this.providedChannels.get(0).getId())) {
            this.providedChannels.clear();
        }
    }
    
    private final void resetState() {
        this.appIcon = null;
        this.appUid = null;
        this.packageName = null;
        this.appName = null;
        this.edits.clear();
        this.providedChannels.clear();
        this.groupNameLookup.clear();
    }
    
    private final void setChannelImportance(final NotificationChannel notificationChannel, final int importance) {
        try {
            notificationChannel.setImportance(importance);
            final INotificationManager noMan = this.noMan;
            final String packageName = this.packageName;
            if (packageName == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            final Integer appUid = this.appUid;
            if (appUid == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            noMan.updateNotificationChannelForPackage(packageName, (int)appUid, notificationChannel);
        }
        catch (Exception ex) {
            Log.e("ChannelDialogController", "Unable to update notification importance", (Throwable)ex);
        }
    }
    
    @VisibleForTesting
    public final void apply() {
        for (final Map.Entry<NotificationChannel, Integer> entry : this.edits.entrySet()) {
            final NotificationChannel notificationChannel = entry.getKey();
            final int intValue = entry.getValue().intValue();
            if (notificationChannel.getImportance() != intValue) {
                this.setChannelImportance(notificationChannel, intValue);
            }
        }
        if (this.appNotificationsEnabled != this.checkAreAppNotificationsOn()) {
            this.applyAppNotificationsOn(this.appNotificationsEnabled);
        }
    }
    
    public final void close() {
        this.done();
    }
    
    public final boolean getAppNotificationsEnabled() {
        return this.appNotificationsEnabled;
    }
    
    public final OnChannelEditorDialogFinishedListener getOnFinishListener() {
        return this.onFinishListener;
    }
    
    public final List<NotificationChannel> getProvidedChannels$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
        return this.providedChannels;
    }
    
    public final CharSequence groupNameForId(String s) {
        s = (String)this.groupNameLookup.get(s);
        if (s == null) {
            s = "";
        }
        return s;
    }
    
    @VisibleForTesting
    public final void launchSettings(final View view) {
        Intrinsics.checkParameterIsNotNull(view, "sender");
        final NotificationInfo.OnSettingsClickListener onSettingsClickListener = this.onSettingsClickListener;
        if (onSettingsClickListener != null) {
            final Integer appUid = this.appUid;
            if (appUid == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            onSettingsClickListener.onClick(view, null, appUid);
        }
    }
    
    public final void prepareDialogForApp(final String appName, final String packageName, final int i, final Set<NotificationChannel> set, final Drawable appIcon, final NotificationInfo.OnSettingsClickListener onSettingsClickListener) {
        Intrinsics.checkParameterIsNotNull(appName, "appName");
        Intrinsics.checkParameterIsNotNull(packageName, "packageName");
        Intrinsics.checkParameterIsNotNull(set, "channels");
        Intrinsics.checkParameterIsNotNull(appIcon, "appIcon");
        this.appName = appName;
        this.packageName = packageName;
        this.appUid = i;
        this.appIcon = appIcon;
        this.appNotificationsEnabled = this.checkAreAppNotificationsOn();
        this.onSettingsClickListener = onSettingsClickListener;
        this.channelGroupList.clear();
        this.channelGroupList.addAll(this.fetchNotificationChannelGroups());
        this.buildGroupNameLookup();
        this.padToFourChannels(set);
    }
    
    public final void proposeEditForChannel(final NotificationChannel notificationChannel, final int i) {
        Intrinsics.checkParameterIsNotNull(notificationChannel, "channel");
        if (notificationChannel.getImportance() == i) {
            this.edits.remove(notificationChannel);
        }
        else {
            this.edits.put(notificationChannel, i);
        }
    }
    
    public final void setAppNotificationsEnabled(final boolean appNotificationsEnabled) {
        this.appNotificationsEnabled = appNotificationsEnabled;
    }
    
    public final void setOnFinishListener(final OnChannelEditorDialogFinishedListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }
    
    public final void show() {
        this.initDialog();
        final Dialog dialog = this.dialog;
        if (dialog != null) {
            dialog.show();
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("dialog");
        throw null;
    }
}
