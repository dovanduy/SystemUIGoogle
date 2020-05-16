// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.R$id;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;

public class MediaHeaderView extends ActivatableNotificationView
{
    private View mContentView;
    
    public MediaHeaderView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    @Override
    protected View getContentView() {
        return this.mContentView;
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mContentView = this.findViewById(R$id.keyguard_media_view);
    }
    
    public void setBackgroundColor(final int tintColor) {
        this.setTintColor(tintColor);
    }
}
