// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.wrapper;

import android.view.ViewGroup;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.content.Context;
import android.view.View;

public class NotificationDecoratedCustomViewWrapper extends NotificationTemplateViewWrapper
{
    private View mWrappedView;
    
    protected NotificationDecoratedCustomViewWrapper(final Context context, final View view, final ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
        this.mWrappedView = null;
    }
    
    @Override
    public void onContentUpdated(final ExpandableNotificationRow expandableNotificationRow) {
        final ViewGroup viewGroup = (ViewGroup)super.mView.findViewById(16909212);
        final Integer n = (Integer)viewGroup.getTag(16909210);
        if (n != null && n != -1) {
            this.mWrappedView = viewGroup.getChildAt((int)n);
        }
        if (this.needsInversion(this.resolveBackgroundColor(), this.mWrappedView)) {
            this.invertViewLuminosity(this.mWrappedView);
        }
        super.onContentUpdated(expandableNotificationRow);
    }
}
