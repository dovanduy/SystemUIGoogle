// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.wrapper;

import com.android.systemui.statusbar.ViewTransformationHelper;
import android.view.View$OnClickListener;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.R$dimen;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.content.Context;
import com.android.internal.widget.MessagingLinearLayout;
import android.view.ViewGroup;
import com.android.internal.widget.ConversationLayout;
import com.android.internal.widget.CachingIconView;
import android.view.View;

public final class NotificationConversationTemplateViewWrapper extends NotificationTemplateViewWrapper
{
    private View appName;
    private View conversationBadgeBg;
    private CachingIconView conversationIcon;
    private final ConversationLayout conversationLayout;
    private View conversationTitle;
    private View expandButton;
    private View expandButtonContainer;
    private View facePileBottom;
    private View facePileBottomBg;
    private View facePileTop;
    private ViewGroup imageMessageContainer;
    private View importanceRing;
    private MessagingLinearLayout messagingLinearLayout;
    private final int minHeightWithActions;
    
    public NotificationConversationTemplateViewWrapper(final Context context, final View view, final ExpandableNotificationRow expandableNotificationRow) {
        Intrinsics.checkParameterIsNotNull(context, "ctx");
        Intrinsics.checkParameterIsNotNull(view, "view");
        Intrinsics.checkParameterIsNotNull(expandableNotificationRow, "row");
        super(context, view, expandableNotificationRow);
        this.minHeightWithActions = NotificationUtils.getFontScaledHeight(context, R$dimen.notification_messaging_actions_min_height);
        this.conversationLayout = (ConversationLayout)view;
    }
    
    private final void addTransformedViews(final View... array) {
        for (final View view : array) {
            if (view != null) {
                super.mTransformationHelper.addTransformedView(view);
            }
        }
    }
    
    private final void addViewsTransformingToSimilar(final View... array) {
        for (final View view : array) {
            if (view != null) {
                super.mTransformationHelper.addViewTransformingToSimilar(view);
            }
        }
    }
    
    private final void resolveViews() {
        final MessagingLinearLayout messagingLinearLayout = this.conversationLayout.getMessagingLinearLayout();
        Intrinsics.checkExpressionValueIsNotNull(messagingLinearLayout, "conversationLayout.messagingLinearLayout");
        this.messagingLinearLayout = messagingLinearLayout;
        final ViewGroup imageMessageContainer = this.conversationLayout.getImageMessageContainer();
        Intrinsics.checkExpressionValueIsNotNull(imageMessageContainer, "conversationLayout.imageMessageContainer");
        this.imageMessageContainer = imageMessageContainer;
        final ConversationLayout conversationLayout = this.conversationLayout;
        final View requireViewById = conversationLayout.requireViewById(16908879);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "requireViewById(com.andr\u2026l.R.id.conversation_icon)");
        this.conversationIcon = (CachingIconView)requireViewById;
        final View requireViewById2 = conversationLayout.requireViewById(16908881);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById2, "requireViewById(com.andr\u2026nversation_icon_badge_bg)");
        this.conversationBadgeBg = requireViewById2;
        final View requireViewById3 = conversationLayout.requireViewById(16908938);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById3, "requireViewById(com.andr\u2026ernal.R.id.expand_button)");
        this.expandButton = requireViewById3;
        final View requireViewById4 = conversationLayout.requireViewById(16908940);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById4, "requireViewById(com.andr\u2026.expand_button_container)");
        this.expandButtonContainer = requireViewById4;
        final View requireViewById5 = conversationLayout.requireViewById(16908882);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById5, "requireViewById(com.andr\u2026ersation_icon_badge_ring)");
        this.importanceRing = requireViewById5;
        final View requireViewById6 = conversationLayout.requireViewById(16908754);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById6, "requireViewById(com.andr\u2026ernal.R.id.app_name_text)");
        this.appName = requireViewById6;
        final View requireViewById7 = conversationLayout.requireViewById(16908885);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById7, "requireViewById(com.andr\u2026l.R.id.conversation_text)");
        this.conversationTitle = requireViewById7;
        this.facePileTop = conversationLayout.findViewById(16908877);
        this.facePileBottom = conversationLayout.findViewById(16908875);
        this.facePileBottomBg = conversationLayout.findViewById(16908876);
    }
    
    @Override
    public boolean disallowSingleClick(final float n, final float n2) {
        final View expandButtonContainer = this.expandButtonContainer;
        if (expandButtonContainer != null) {
            final int visibility = expandButtonContainer.getVisibility();
            final boolean b = true;
            boolean b2 = false;
            Label_0058: {
                if (visibility == 0) {
                    final View expandButtonContainer2 = this.expandButtonContainer;
                    if (expandButtonContainer2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("expandButtonContainer");
                        throw null;
                    }
                    if (this.isOnView(expandButtonContainer2, n, n2)) {
                        b2 = true;
                        break Label_0058;
                    }
                }
                b2 = false;
            }
            boolean b3 = b;
            if (!b2) {
                b3 = (super.disallowSingleClick(n, n2) && b);
            }
            return b3;
        }
        Intrinsics.throwUninitializedPropertyAccessException("expandButtonContainer");
        throw null;
    }
    
    @Override
    public int getMinLayoutHeight() {
        final View mActionsContainer = super.mActionsContainer;
        if (mActionsContainer != null) {
            Intrinsics.checkExpressionValueIsNotNull(mActionsContainer, "mActionsContainer");
            if (mActionsContainer.getVisibility() != 8) {
                return this.minHeightWithActions;
            }
        }
        return super.getMinLayoutHeight();
    }
    
    @Override
    public View getShelfTransformationTarget() {
        if (!this.conversationLayout.isImportantConversation()) {
            return super.getShelfTransformationTarget();
        }
        final CachingIconView conversationIcon = this.conversationIcon;
        if (conversationIcon == null) {
            Intrinsics.throwUninitializedPropertyAccessException("conversationIcon");
            throw null;
        }
        if (conversationIcon.getVisibility() == 8) {
            return super.getShelfTransformationTarget();
        }
        final CachingIconView conversationIcon2 = this.conversationIcon;
        if (conversationIcon2 != null) {
            return (View)conversationIcon2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("conversationIcon");
        throw null;
    }
    
    @Override
    public void onContentUpdated(final ExpandableNotificationRow expandableNotificationRow) {
        Intrinsics.checkParameterIsNotNull(expandableNotificationRow, "row");
        this.resolveViews();
        super.onContentUpdated(expandableNotificationRow);
    }
    
    @Override
    public void setRemoteInputVisible(final boolean b) {
        this.conversationLayout.showHistoricMessages(b);
    }
    
    @Override
    public void setShelfIconVisible(final boolean b) {
        if (this.conversationLayout.isImportantConversation()) {
            final CachingIconView conversationIcon = this.conversationIcon;
            if (conversationIcon == null) {
                Intrinsics.throwUninitializedPropertyAccessException("conversationIcon");
                throw null;
            }
            if (conversationIcon.getVisibility() != 8) {
                final CachingIconView conversationIcon2 = this.conversationIcon;
                if (conversationIcon2 != null) {
                    conversationIcon2.setForceHidden(b);
                    return;
                }
                Intrinsics.throwUninitializedPropertyAccessException("conversationIcon");
                throw null;
            }
        }
        super.setShelfIconVisible(b);
    }
    
    @Override
    public void updateExpandability(final boolean b, final View$OnClickListener view$OnClickListener) {
        this.conversationLayout.updateExpandability(b, view$OnClickListener);
    }
    
    @Override
    protected void updateTransformedTypes() {
        super.updateTransformedTypes();
        final MessagingLinearLayout messagingLinearLayout = this.messagingLinearLayout;
        if (messagingLinearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("messagingLinearLayout");
            throw null;
        }
        final View appName = this.appName;
        if (appName == null) {
            Intrinsics.throwUninitializedPropertyAccessException("appName");
            throw null;
        }
        final View conversationTitle = this.conversationTitle;
        if (conversationTitle == null) {
            Intrinsics.throwUninitializedPropertyAccessException("conversationTitle");
            throw null;
        }
        this.addTransformedViews((View)messagingLinearLayout, appName, conversationTitle);
        final ViewTransformationHelper mTransformationHelper = super.mTransformationHelper;
        final NotificationConversationTemplateViewWrapper$updateTransformedTypes.NotificationConversationTemplateViewWrapper$updateTransformedTypes$1 notificationConversationTemplateViewWrapper$updateTransformedTypes$1 = new NotificationConversationTemplateViewWrapper$updateTransformedTypes.NotificationConversationTemplateViewWrapper$updateTransformedTypes$1();
        final ViewGroup imageMessageContainer = this.imageMessageContainer;
        if (imageMessageContainer == null) {
            Intrinsics.throwUninitializedPropertyAccessException("imageMessageContainer");
            throw null;
        }
        mTransformationHelper.setCustomTransformation((ViewTransformationHelper.CustomTransformation)notificationConversationTemplateViewWrapper$updateTransformedTypes$1, imageMessageContainer.getId());
        final CachingIconView conversationIcon = this.conversationIcon;
        if (conversationIcon == null) {
            Intrinsics.throwUninitializedPropertyAccessException("conversationIcon");
            throw null;
        }
        final View conversationBadgeBg = this.conversationBadgeBg;
        if (conversationBadgeBg == null) {
            Intrinsics.throwUninitializedPropertyAccessException("conversationBadgeBg");
            throw null;
        }
        final View expandButton = this.expandButton;
        if (expandButton == null) {
            Intrinsics.throwUninitializedPropertyAccessException("expandButton");
            throw null;
        }
        final View importanceRing = this.importanceRing;
        if (importanceRing != null) {
            this.addViewsTransformingToSimilar((View)conversationIcon, conversationBadgeBg, expandButton, importanceRing, this.facePileTop, this.facePileBottom, this.facePileBottomBg);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("importanceRing");
        throw null;
    }
}
