// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.wrapper;

import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.view.View;
import android.content.Context;
import com.android.internal.widget.ImageFloatingTextView;

public class NotificationBigTextTemplateViewWrapper extends NotificationTemplateViewWrapper
{
    private ImageFloatingTextView mBigtext;
    
    protected NotificationBigTextTemplateViewWrapper(final Context context, final View view, final ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
    }
    
    private void resolveViews(final StatusBarNotification statusBarNotification) {
        this.mBigtext = (ImageFloatingTextView)super.mView.findViewById(16908784);
    }
    
    @Override
    public void onContentUpdated(final ExpandableNotificationRow expandableNotificationRow) {
        this.resolveViews(expandableNotificationRow.getEntry().getSbn());
        super.onContentUpdated(expandableNotificationRow);
    }
    
    @Override
    protected void updateTransformedTypes() {
        super.updateTransformedTypes();
        final ImageFloatingTextView mBigtext = this.mBigtext;
        if (mBigtext != null) {
            super.mTransformationHelper.addTransformedView(2, (View)mBigtext);
        }
    }
}
