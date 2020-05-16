// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.statusbar.notification.icon.IconPack;
import com.android.internal.statusbar.StatusBarIcon;
import android.view.View;
import com.android.systemui.statusbar.StatusBarIconView;
import android.widget.TextView;
import kotlin.TypeCastException;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.widget.LinearLayout;

public final class DungeonRow extends LinearLayout
{
    private NotificationEntry entry;
    
    public DungeonRow(final Context context, final AttributeSet set) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(set, "attrs");
        super(context, set);
    }
    
    private final void update() {
        final View viewById = this.findViewById(R$id.app_name);
        if (viewById == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.widget.TextView");
        }
        final TextView textView = (TextView)viewById;
        final NotificationEntry entry = this.entry;
        final StatusBarIcon statusBarIcon = null;
        String appName = null;
        Label_0047: {
            if (entry != null) {
                final ExpandableNotificationRow row = entry.getRow();
                if (row != null) {
                    appName = row.getAppName();
                    break Label_0047;
                }
            }
            appName = null;
        }
        textView.setText((CharSequence)appName);
        final View viewById2 = this.findViewById(R$id.icon);
        if (viewById2 != null) {
            final StatusBarIconView statusBarIconView = (StatusBarIconView)viewById2;
            final NotificationEntry entry2 = this.entry;
            StatusBarIcon statusBarIcon2 = statusBarIcon;
            if (entry2 != null) {
                final IconPack icons = entry2.getIcons();
                statusBarIcon2 = statusBarIcon;
                if (icons != null) {
                    final StatusBarIconView statusBarIcon3 = icons.getStatusBarIcon();
                    statusBarIcon2 = statusBarIcon;
                    if (statusBarIcon3 != null) {
                        statusBarIcon2 = statusBarIcon3.getStatusBarIcon();
                    }
                }
            }
            statusBarIconView.set(statusBarIcon2);
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type com.android.systemui.statusbar.StatusBarIconView");
    }
    
    public final NotificationEntry getEntry() {
        return this.entry;
    }
    
    public final void setEntry(final NotificationEntry entry) {
        this.entry = entry;
        this.update();
    }
}
