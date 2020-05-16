// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import java.util.Iterator;
import android.view.View$OnClickListener;
import com.android.systemui.statusbar.notification.row.DungeonRow;
import kotlin.TypeCastException;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import java.util.Comparator;
import kotlin.collections.CollectionsKt;
import android.widget.LinearLayout;
import com.android.systemui.R$id;
import android.service.notification.StatusBarNotification;
import com.android.systemui.util.Assert;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.NotificationRemoveInterceptor;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.functions.Function3;
import java.util.LinkedHashSet;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.view.View;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Set;

public final class ForegroundServiceSectionController
{
    private final Set<NotificationEntry> entries;
    private View entriesView;
    private final NotificationEntryManager entryManager;
    private final ForegroundServiceDismissalFeatureController featureController;
    
    public ForegroundServiceSectionController(final NotificationEntryManager entryManager, final ForegroundServiceDismissalFeatureController featureController) {
        Intrinsics.checkParameterIsNotNull(entryManager, "entryManager");
        Intrinsics.checkParameterIsNotNull(featureController, "featureController");
        this.entryManager = entryManager;
        this.featureController = featureController;
        this.entries = new LinkedHashSet<NotificationEntry>();
        if (this.featureController.isForegroundServiceDismissalEnabled()) {
            this.entryManager.addNotificationRemoveInterceptor(new ForegroundServiceSectionController$sam$com_android_systemui_statusbar_NotificationRemoveInterceptor$0(new Function3<String, NotificationEntry, Integer, Boolean>(this)));
            this.entryManager.addNotificationEntryListener(new NotificationEntryListener() {
                final /* synthetic */ ForegroundServiceSectionController this$0;
                
                @Override
                public void onPostEntryUpdated(final NotificationEntry notificationEntry) {
                    Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
                    if (ForegroundServiceSectionController.access$getEntries$p(this.this$0).contains(notificationEntry)) {
                        ForegroundServiceSectionController.this.removeEntry(notificationEntry);
                        ForegroundServiceSectionController.this.addEntry(notificationEntry);
                        ForegroundServiceSectionController.this.update();
                    }
                }
            });
        }
    }
    
    public static final /* synthetic */ Set access$getEntries$p(final ForegroundServiceSectionController foregroundServiceSectionController) {
        return foregroundServiceSectionController.entries;
    }
    
    private final void addEntry(final NotificationEntry notificationEntry) {
        Assert.isMainThread();
        this.entries.add(notificationEntry);
    }
    
    private final void removeEntry(final NotificationEntry notificationEntry) {
        Assert.isMainThread();
        this.entries.remove(notificationEntry);
    }
    
    private final boolean shouldInterceptRemoval(final String s, final NotificationEntry notificationEntry, int n) {
        Assert.isMainThread();
        final boolean b = n == 3;
        final boolean b2 = n == 2 || n == 1;
        if (n != 8) {}
        if (n == 12) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (notificationEntry == null) {
            return false;
        }
        if (b2) {
            final StatusBarNotification sbn = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn, "entry.sbn");
            if (!sbn.isClearable()) {
                if (!this.hasEntry(notificationEntry)) {
                    this.addEntry(notificationEntry);
                    this.update();
                }
                this.entryManager.updateNotifications("FgsSectionController.onNotificationRemoveRequested");
                return true;
            }
        }
        if (b || n != 0) {
            final StatusBarNotification sbn2 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn2, "entry.sbn");
            if (!sbn2.isClearable()) {
                return true;
            }
        }
        if (this.hasEntry(notificationEntry)) {
            this.removeEntry(notificationEntry);
            this.update();
        }
        return false;
    }
    
    private final void update() {
        Assert.isMainThread();
        final View entriesView = this.entriesView;
        if (entriesView == null) {
            throw new IllegalStateException("ForegroundServiceSectionController is trying to show dismissed fgs notifications without having been initialized!");
        }
        if (entriesView == null) {
            Intrinsics.throwNpe();
            throw null;
        }
        final View viewById = entriesView.findViewById(R$id.entry_list);
        if (viewById != null) {
            final LinearLayout linearLayout = (LinearLayout)viewById;
            linearLayout.removeAllViews();
            for (final NotificationEntry entry : CollectionsKt.sortedWith((Iterable<?>)this.entries, (Comparator<? super Object>)new ForegroundServiceSectionController$$special$$inlined$sortedBy.ForegroundServiceSectionController$$special$$inlined$sortedBy$1())) {
                final View inflate = LayoutInflater.from(linearLayout.getContext()).inflate(R$layout.foreground_service_dungeon_row, (ViewGroup)null);
                if (inflate == null) {
                    throw new TypeCastException("null cannot be cast to non-null type com.android.systemui.statusbar.notification.row.DungeonRow");
                }
                final DungeonRow dungeonRow = (DungeonRow)inflate;
                dungeonRow.setEntry(entry);
                dungeonRow.setOnClickListener((View$OnClickListener)new ForegroundServiceSectionController$update$$inlined$apply$lambda.ForegroundServiceSectionController$update$$inlined$apply$lambda$1(dungeonRow, entry, linearLayout, this));
                linearLayout.addView((View)dungeonRow);
            }
            if (this.entries.isEmpty()) {
                final View entriesView2 = this.entriesView;
                if (entriesView2 != null) {
                    entriesView2.setVisibility(8);
                }
            }
            else {
                final View entriesView3 = this.entriesView;
                if (entriesView3 != null) {
                    entriesView3.setVisibility(0);
                }
            }
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.widget.LinearLayout");
    }
    
    public final View createView(final LayoutInflater layoutInflater) {
        Intrinsics.checkParameterIsNotNull(layoutInflater, "li");
        final View inflate = layoutInflater.inflate(R$layout.foreground_service_dungeon, (ViewGroup)null);
        this.entriesView = inflate;
        if (inflate == null) {
            Intrinsics.throwNpe();
            throw null;
        }
        inflate.setVisibility(8);
        final View entriesView = this.entriesView;
        if (entriesView != null) {
            return entriesView;
        }
        Intrinsics.throwNpe();
        throw null;
    }
    
    public final NotificationEntryManager getEntryManager() {
        return this.entryManager;
    }
    
    public final boolean hasEntry(final NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        Assert.isMainThread();
        return this.entries.contains(notificationEntry);
    }
}
