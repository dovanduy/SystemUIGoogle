// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.view.View;
import android.view.View$OnClickListener;
import android.widget.CompoundButton$OnCheckedChangeListener;
import com.android.systemui.R$id;
import android.text.TextUtils;
import kotlin.jvm.internal.Intrinsics;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.Switch;
import android.widget.TextView;
import android.app.NotificationChannel;
import android.widget.LinearLayout;

public final class ChannelRow extends LinearLayout
{
    private NotificationChannel channel;
    private TextView channelDescription;
    private TextView channelName;
    public ChannelEditorDialogController controller;
    private Switch switch;
    
    public ChannelRow(final Context context, final AttributeSet set) {
        Intrinsics.checkParameterIsNotNull(context, "c");
        Intrinsics.checkParameterIsNotNull(set, "attrs");
        super(context, set);
    }
    
    private final void updateImportance() {
        final NotificationChannel channel = this.channel;
        int importance;
        if (channel != null) {
            importance = channel.getImportance();
        }
        else {
            importance = 0;
        }
        if (importance != -1000) {}
    }
    
    private final void updateViews() {
        final NotificationChannel channel = this.channel;
        if (channel == null) {
            return;
        }
        final TextView channelName = this.channelName;
        if (channelName == null) {
            Intrinsics.throwUninitializedPropertyAccessException("channelName");
            throw null;
        }
        CharSequence name = channel.getName();
        if (name == null) {
            name = "";
        }
        channelName.setText(name);
        final String group = channel.getGroup();
        if (group != null) {
            final TextView channelDescription = this.channelDescription;
            if (channelDescription == null) {
                Intrinsics.throwUninitializedPropertyAccessException("channelDescription");
                throw null;
            }
            final ChannelEditorDialogController controller = this.controller;
            if (controller == null) {
                Intrinsics.throwUninitializedPropertyAccessException("controller");
                throw null;
            }
            channelDescription.setText(controller.groupNameForId(group));
        }
        final String group2 = channel.getGroup();
        boolean checked = false;
        Label_0174: {
            if (group2 != null) {
                final TextView channelDescription2 = this.channelDescription;
                if (channelDescription2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("channelDescription");
                    throw null;
                }
                if (!TextUtils.isEmpty(channelDescription2.getText())) {
                    final TextView channelDescription3 = this.channelDescription;
                    if (channelDescription3 != null) {
                        channelDescription3.setVisibility(0);
                        break Label_0174;
                    }
                    Intrinsics.throwUninitializedPropertyAccessException("channelDescription");
                    throw null;
                }
            }
            final TextView channelDescription4 = this.channelDescription;
            if (channelDescription4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("channelDescription");
                throw null;
            }
            channelDescription4.setVisibility(8);
        }
        final Switch switch1 = this.switch;
        if (switch1 != null) {
            if (channel.getImportance() != 0) {
                checked = true;
            }
            switch1.setChecked(checked);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("switch");
        throw null;
    }
    
    public final NotificationChannel getChannel() {
        return this.channel;
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
        final View viewById = this.findViewById(R$id.channel_name);
        Intrinsics.checkExpressionValueIsNotNull(viewById, "findViewById(R.id.channel_name)");
        this.channelName = (TextView)viewById;
        final View viewById2 = this.findViewById(R$id.channel_description);
        Intrinsics.checkExpressionValueIsNotNull(viewById2, "findViewById(R.id.channel_description)");
        this.channelDescription = (TextView)viewById2;
        final View viewById3 = this.findViewById(R$id.toggle);
        Intrinsics.checkExpressionValueIsNotNull(viewById3, "findViewById(R.id.toggle)");
        final Switch switch1 = (Switch)viewById3;
        this.switch = switch1;
        if (switch1 != null) {
            switch1.setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)new ChannelRow$onFinishInflate.ChannelRow$onFinishInflate$1(this));
            this.setOnClickListener((View$OnClickListener)new ChannelRow$onFinishInflate.ChannelRow$onFinishInflate$2(this));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("switch");
        throw null;
    }
    
    public final void setChannel(final NotificationChannel channel) {
        this.channel = channel;
        this.updateImportance();
        this.updateViews();
    }
    
    public final void setController(final ChannelEditorDialogController controller) {
        Intrinsics.checkParameterIsNotNull(controller, "<set-?>");
        this.controller = controller;
    }
}
