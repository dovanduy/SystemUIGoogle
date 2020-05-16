// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.view.View;
import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.Switch;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

public final class AppControlView extends LinearLayout
{
    public TextView channelName;
    public ImageView iconView;
    public Switch switch;
    
    public AppControlView(final Context context, final AttributeSet set) {
        Intrinsics.checkParameterIsNotNull(context, "c");
        Intrinsics.checkParameterIsNotNull(set, "attrs");
        super(context, set);
    }
    
    public final TextView getChannelName() {
        final TextView channelName = this.channelName;
        if (channelName != null) {
            return channelName;
        }
        Intrinsics.throwUninitializedPropertyAccessException("channelName");
        throw null;
    }
    
    public final ImageView getIconView() {
        final ImageView iconView = this.iconView;
        if (iconView != null) {
            return iconView;
        }
        Intrinsics.throwUninitializedPropertyAccessException("iconView");
        throw null;
    }
    
    public final Switch getSwitch() {
        final Switch switch1 = this.switch;
        if (switch1 != null) {
            return switch1;
        }
        Intrinsics.throwUninitializedPropertyAccessException("switch");
        throw null;
    }
    
    protected void onFinishInflate() {
        final View viewById = this.findViewById(R$id.icon);
        Intrinsics.checkExpressionValueIsNotNull(viewById, "findViewById(R.id.icon)");
        this.iconView = (ImageView)viewById;
        final View viewById2 = this.findViewById(R$id.app_name);
        Intrinsics.checkExpressionValueIsNotNull(viewById2, "findViewById(R.id.app_name)");
        this.channelName = (TextView)viewById2;
        final View viewById3 = this.findViewById(R$id.toggle);
        Intrinsics.checkExpressionValueIsNotNull(viewById3, "findViewById(R.id.toggle)");
        this.switch = (Switch)viewById3;
        this.setOnClickListener((View$OnClickListener)new AppControlView$onFinishInflate.AppControlView$onFinishInflate$1(this));
    }
}
