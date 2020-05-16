// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.R$id;
import java.util.Iterator;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.Transition$TransitionListener;
import android.transition.AutoTransition;
import android.widget.TextView;
import android.widget.CompoundButton$OnCheckedChangeListener;
import com.android.systemui.R$string;
import android.view.View;
import kotlin.TypeCastException;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;
import android.util.AttributeSet;
import android.content.Context;
import android.app.NotificationChannel;
import java.util.List;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;

public final class ChannelEditorListView extends LinearLayout
{
    private AppControlView appControlRow;
    private Drawable appIcon;
    private String appName;
    private List<NotificationChannel> channels;
    public ChannelEditorDialogController controller;
    
    public ChannelEditorListView(final Context context, final AttributeSet set) {
        Intrinsics.checkParameterIsNotNull(context, "c");
        Intrinsics.checkParameterIsNotNull(set, "attrs");
        super(context, set);
        this.channels = new ArrayList<NotificationChannel>();
    }
    
    private final void addChannelRow(final NotificationChannel channel, final LayoutInflater layoutInflater) {
        final View inflate = layoutInflater.inflate(R$layout.notif_half_shelf_row, (ViewGroup)null);
        if (inflate == null) {
            throw new TypeCastException("null cannot be cast to non-null type com.android.systemui.statusbar.notification.row.ChannelRow");
        }
        final ChannelRow channelRow = (ChannelRow)inflate;
        final ChannelEditorDialogController controller = this.controller;
        if (controller != null) {
            channelRow.setController(controller);
            channelRow.setChannel(channel);
            this.addView((View)channelRow);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("controller");
        throw null;
    }
    
    private final void updateAppControlRow(final boolean checked) {
        final AppControlView appControlRow = this.appControlRow;
        if (appControlRow == null) {
            Intrinsics.throwUninitializedPropertyAccessException("appControlRow");
            throw null;
        }
        appControlRow.getIconView().setImageDrawable(this.appIcon);
        final AppControlView appControlRow2 = this.appControlRow;
        if (appControlRow2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("appControlRow");
            throw null;
        }
        final TextView channelName = appControlRow2.getChannelName();
        final Context context = this.getContext();
        Intrinsics.checkExpressionValueIsNotNull(context, "context");
        channelName.setText((CharSequence)context.getResources().getString(R$string.notification_channel_dialog_title, new Object[] { this.appName }));
        final AppControlView appControlRow3 = this.appControlRow;
        if (appControlRow3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("appControlRow");
            throw null;
        }
        appControlRow3.getSwitch().setChecked(checked);
        final AppControlView appControlRow4 = this.appControlRow;
        if (appControlRow4 != null) {
            appControlRow4.getSwitch().setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)new ChannelEditorListView$updateAppControlRow.ChannelEditorListView$updateAppControlRow$1(this));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("appControlRow");
        throw null;
    }
    
    private final void updateRows() {
        final ChannelEditorDialogController controller = this.controller;
        if (controller != null) {
            final boolean appNotificationsEnabled = controller.getAppNotificationsEnabled();
            final AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(200L);
            autoTransition.addListener((Transition$TransitionListener)new ChannelEditorListView$updateRows.ChannelEditorListView$updateRows$1(this));
            TransitionManager.beginDelayedTransition((ViewGroup)this, (Transition)autoTransition);
            for (int i = this.getChildCount(); i >= 0; --i) {
                final View child = this.getChildAt(i);
                if (child instanceof ChannelRow) {
                    this.removeView(child);
                }
            }
            this.updateAppControlRow(appNotificationsEnabled);
            if (appNotificationsEnabled) {
                final LayoutInflater from = LayoutInflater.from(this.getContext());
                for (final NotificationChannel notificationChannel : this.channels) {
                    Intrinsics.checkExpressionValueIsNotNull(from, "inflater");
                    this.addChannelRow(notificationChannel, from);
                }
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("controller");
        throw null;
    }
    
    public final ChannelEditorDialogController getController() {
        final ChannelEditorDialogController controller = this.controller;
        if (controller != null) {
            return controller;
        }
        Intrinsics.throwUninitializedPropertyAccessException("controller");
        throw null;
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        final View viewById = this.findViewById(R$id.app_control);
        Intrinsics.checkExpressionValueIsNotNull(viewById, "findViewById(R.id.app_control)");
        this.appControlRow = (AppControlView)viewById;
    }
    
    public final void setAppIcon(final Drawable appIcon) {
        this.appIcon = appIcon;
    }
    
    public final void setAppName(final String appName) {
        this.appName = appName;
    }
    
    public final void setChannels(final List<NotificationChannel> channels) {
        Intrinsics.checkParameterIsNotNull(channels, "newValue");
        this.channels = channels;
        this.updateRows();
    }
    
    public final void setController(final ChannelEditorDialogController controller) {
        Intrinsics.checkParameterIsNotNull(controller, "<set-?>");
        this.controller = controller;
    }
}
