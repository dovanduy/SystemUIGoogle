// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.R$id;
import android.view.View;
import kotlin.jvm.internal.Intrinsics;
import android.util.AttributeSet;
import android.content.Context;

public final class ForegroundServiceDungeonView extends StackScrollerDecorView
{
    public ForegroundServiceDungeonView(final Context context, final AttributeSet set) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(set, "attrs");
        super(context, set);
    }
    
    @Override
    protected View findContentView() {
        return this.findViewById(R$id.foreground_service_dungeon);
    }
    
    @Override
    protected View findSecondaryView() {
        return null;
    }
    
    @Override
    public void setVisible(final boolean b, final boolean b2) {
    }
}
