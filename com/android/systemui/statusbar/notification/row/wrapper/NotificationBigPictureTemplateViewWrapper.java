// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.wrapper;

import com.android.systemui.statusbar.notification.ImageTransformState;
import android.graphics.drawable.Icon;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.view.View;
import android.content.Context;

public class NotificationBigPictureTemplateViewWrapper extends NotificationTemplateViewWrapper
{
    protected NotificationBigPictureTemplateViewWrapper(final Context context, final View view, final ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
    }
    
    private void updateImageTag(final StatusBarNotification statusBarNotification) {
        final Icon icon = (Icon)statusBarNotification.getNotification().extras.getParcelable("android.largeIcon.big");
        if (icon != null) {
            super.mPicture.setTag(ImageTransformState.ICON_TAG, (Object)icon);
        }
    }
    
    @Override
    public void onContentUpdated(final ExpandableNotificationRow expandableNotificationRow) {
        super.onContentUpdated(expandableNotificationRow);
        this.updateImageTag(expandableNotificationRow.getEntry().getSbn());
    }
}
