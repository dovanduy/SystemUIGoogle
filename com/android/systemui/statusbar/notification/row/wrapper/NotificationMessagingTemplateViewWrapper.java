// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.wrapper;

import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.R$dimen;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.view.View;
import android.content.Context;
import com.android.internal.widget.MessagingLinearLayout;
import com.android.internal.widget.MessagingLayout;

public class NotificationMessagingTemplateViewWrapper extends NotificationTemplateViewWrapper
{
    private MessagingLayout mMessagingLayout;
    private MessagingLinearLayout mMessagingLinearLayout;
    private final int mMinHeightWithActions;
    
    protected NotificationMessagingTemplateViewWrapper(final Context context, final View view, final ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
        this.mMessagingLayout = (MessagingLayout)view;
        this.mMinHeightWithActions = NotificationUtils.getFontScaledHeight(context, R$dimen.notification_messaging_actions_min_height);
    }
    
    private void resolveViews() {
        this.mMessagingLinearLayout = this.mMessagingLayout.getMessagingLinearLayout();
    }
    
    @Override
    public int getMinLayoutHeight() {
        final View mActionsContainer = super.mActionsContainer;
        if (mActionsContainer != null && mActionsContainer.getVisibility() != 8) {
            return this.mMinHeightWithActions;
        }
        return super.getMinLayoutHeight();
    }
    
    @Override
    public void onContentUpdated(final ExpandableNotificationRow expandableNotificationRow) {
        this.resolveViews();
        super.onContentUpdated(expandableNotificationRow);
    }
    
    @Override
    public void setRemoteInputVisible(final boolean b) {
        this.mMessagingLayout.showHistoricMessages(b);
    }
    
    @Override
    protected void updateTransformedTypes() {
        super.updateTransformedTypes();
        final MessagingLinearLayout mMessagingLinearLayout = this.mMessagingLinearLayout;
        if (mMessagingLinearLayout != null) {
            super.mTransformationHelper.addTransformedView(mMessagingLinearLayout.getId(), (View)this.mMessagingLinearLayout);
        }
    }
}
