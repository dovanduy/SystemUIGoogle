// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.row.HybridNotificationView;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.util.ArraySet;
import com.android.systemui.statusbar.CrossFadeHelper;
import android.view.View$MeasureSpec;
import android.content.res.Configuration;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.TransformableView;
import android.widget.RemoteViews;
import android.service.notification.StatusBarNotification;
import android.app.Notification$Builder;
import android.content.res.Resources;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import com.android.systemui.statusbar.notification.NotificationUtils;
import java.util.ArrayList;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import android.view.NotificationHeaderView;
import com.android.systemui.statusbar.notification.row.HybridGroupManager;
import com.android.systemui.statusbar.NotificationHeaderUtil;
import android.view.View$OnClickListener;
import android.view.View;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.List;
import com.android.internal.annotations.VisibleForTesting;
import android.view.ViewGroup;

public class NotificationChildrenContainer extends ViewGroup
{
    private static final AnimationProperties ALPHA_FADE_IN;
    @VisibleForTesting
    static final int NUMBER_OF_CHILDREN_WHEN_COLLAPSED = 2;
    @VisibleForTesting
    static final int NUMBER_OF_CHILDREN_WHEN_SYSTEM_EXPANDED = 5;
    private int mActualHeight;
    private int mChildPadding;
    private final List<ExpandableNotificationRow> mChildren;
    private boolean mChildrenExpanded;
    private int mClipBottomAmount;
    private float mCollapsedBottompadding;
    private ExpandableNotificationRow mContainingNotification;
    private ViewGroup mCurrentHeader;
    private int mCurrentHeaderTranslation;
    private float mDividerAlpha;
    private int mDividerHeight;
    private final List<View> mDividers;
    private boolean mEnableShadowOnChildNotifications;
    private ViewState mGroupOverFlowState;
    private View$OnClickListener mHeaderClickListener;
    private int mHeaderHeight;
    private NotificationHeaderUtil mHeaderUtil;
    private ViewState mHeaderViewState;
    private float mHeaderVisibleAmount;
    private boolean mHideDividersDuringExpand;
    private final HybridGroupManager mHybridGroupManager;
    private boolean mIsLowPriority;
    private boolean mNeverAppliedGroupState;
    private NotificationHeaderView mNotificationHeader;
    private NotificationHeaderView mNotificationHeaderLowPriority;
    private int mNotificationHeaderMargin;
    private NotificationViewWrapper mNotificationHeaderWrapper;
    private NotificationViewWrapper mNotificationHeaderWrapperLowPriority;
    private int mNotificatonTopPadding;
    private TextView mOverflowNumber;
    private int mRealHeight;
    private boolean mShowDividersWhenExpanded;
    private int mTranslationForHeader;
    private boolean mUserLocked;
    
    static {
        final AnimationProperties alpha_FADE_IN = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;
            
            {
                final AnimationFilter mAnimationFilter = new AnimationFilter();
                mAnimationFilter.animateAlpha();
                this.mAnimationFilter = mAnimationFilter;
            }
            
            @Override
            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        alpha_FADE_IN.setDuration(200L);
        ALPHA_FADE_IN = alpha_FADE_IN;
    }
    
    public NotificationChildrenContainer(final Context context) {
        this(context, null);
    }
    
    public NotificationChildrenContainer(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public NotificationChildrenContainer(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public NotificationChildrenContainer(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mDividers = new ArrayList<View>();
        this.mChildren = new ArrayList<ExpandableNotificationRow>();
        this.mCurrentHeaderTranslation = 0;
        this.mHeaderVisibleAmount = 1.0f;
        this.mHybridGroupManager = new HybridGroupManager(this.getContext(), this);
        this.initDimens();
        this.setClipChildren(false);
    }
    
    private ViewGroup calculateDesiredHeader() {
        NotificationHeaderView notificationHeaderView;
        if (this.showingAsLowPriority()) {
            notificationHeaderView = this.mNotificationHeaderLowPriority;
        }
        else {
            notificationHeaderView = this.mNotificationHeader;
        }
        return (ViewGroup)notificationHeaderView;
    }
    
    private int getIntrinsicHeight(float mCollapsedBottompadding) {
        if (this.showingAsLowPriority()) {
            return this.mNotificationHeaderLowPriority.getHeight();
        }
        int n = this.mNotificationHeaderMargin + this.mCurrentHeaderTranslation;
        final int size = this.mChildren.size();
        float groupExpandFraction;
        if (this.mUserLocked) {
            groupExpandFraction = this.getGroupExpandFraction();
        }
        else {
            groupExpandFraction = 0.0f;
        }
        final boolean mChildrenExpanded = this.mChildrenExpanded;
        int n2 = 1;
        int n4;
        for (int n3 = n4 = 0; n3 < size && n4 < mCollapsedBottompadding; ++n4, ++n3) {
            int n5;
            if (n2 == 0) {
                if (this.mUserLocked) {
                    n5 = (int)(n + NotificationUtils.interpolate((float)this.mChildPadding, (float)this.mDividerHeight, groupExpandFraction));
                }
                else {
                    int n6;
                    if (mChildrenExpanded) {
                        n6 = this.mDividerHeight;
                    }
                    else {
                        n6 = this.mChildPadding;
                    }
                    n5 = n + n6;
                }
            }
            else {
                if (this.mUserLocked) {
                    n5 = (int)(n + NotificationUtils.interpolate(0.0f, (float)(this.mNotificatonTopPadding + this.mDividerHeight), groupExpandFraction));
                }
                else {
                    int n7;
                    if (mChildrenExpanded) {
                        n7 = this.mNotificatonTopPadding + this.mDividerHeight;
                    }
                    else {
                        n7 = 0;
                    }
                    n5 = n + n7;
                }
                n2 = 0;
            }
            n = n5 + this.mChildren.get(n3).getIntrinsicHeight();
        }
        float n8;
        if (this.mUserLocked) {
            mCollapsedBottompadding = (float)n;
            final float interpolate = NotificationUtils.interpolate(this.mCollapsedBottompadding, 0.0f, groupExpandFraction);
            n8 = mCollapsedBottompadding;
            mCollapsedBottompadding = interpolate;
        }
        else {
            final int n9 = n;
            if (mChildrenExpanded) {
                return n9;
            }
            n8 = (float)n;
            mCollapsedBottompadding = this.mCollapsedBottompadding;
        }
        return (int)(n8 + mCollapsedBottompadding);
    }
    
    private int getMinHeight(final int n, final boolean b) {
        return this.getMinHeight(n, b, this.mCurrentHeaderTranslation);
    }
    
    private int getMinHeight(final int n, final boolean b, int n2) {
        if (!b && this.showingAsLowPriority()) {
            return this.mNotificationHeaderLowPriority.getHeight();
        }
        n2 += this.mNotificationHeaderMargin;
        final int size = this.mChildren.size();
        int n3 = 1;
        int n5;
        for (int n4 = n5 = 0; n4 < size && n5 < n; ++n5, ++n4) {
            if (n3 == 0) {
                n2 += this.mChildPadding;
            }
            else {
                n3 = 0;
            }
            n2 += this.mChildren.get(n4).getSingleLineView().getHeight();
        }
        return (int)(n2 + this.mCollapsedBottompadding);
    }
    
    private int getVisibleChildrenExpandHeight() {
        int n = this.mNotificationHeaderMargin + this.mCurrentHeaderTranslation + this.mNotificatonTopPadding + this.mDividerHeight;
        for (int size = this.mChildren.size(), maxAllowedVisibleChildren = this.getMaxAllowedVisibleChildren(true), n2 = 0, n3 = 0; n2 < size && n3 < maxAllowedVisibleChildren; ++n3, ++n2) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(n2);
            int n4;
            if (expandableNotificationRow.isExpanded(true)) {
                n4 = expandableNotificationRow.getMaxExpandHeight();
            }
            else {
                n4 = expandableNotificationRow.getShowingLayout().getMinHeight(true);
            }
            n += (int)(float)n4;
        }
        return n;
    }
    
    private NotificationViewWrapper getWrapperForView(final View view) {
        if (view == this.mNotificationHeader) {
            return this.mNotificationHeaderWrapper;
        }
        return this.mNotificationHeaderWrapperLowPriority;
    }
    
    private View inflateDivider() {
        return LayoutInflater.from(super.mContext).inflate(R$layout.notification_children_divider, (ViewGroup)this, false);
    }
    
    private void initDimens() {
        final Resources resources = this.getResources();
        this.mChildPadding = resources.getDimensionPixelSize(R$dimen.notification_children_padding);
        this.mDividerHeight = resources.getDimensionPixelSize(R$dimen.notification_children_container_divider_height);
        this.mDividerAlpha = resources.getFloat(R$dimen.notification_divider_alpha);
        this.mNotificationHeaderMargin = resources.getDimensionPixelSize(R$dimen.notification_children_container_margin_top);
        final int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.notification_children_container_top_padding);
        this.mNotificatonTopPadding = dimensionPixelSize;
        this.mHeaderHeight = this.mNotificationHeaderMargin + dimensionPixelSize;
        this.mCollapsedBottompadding = (float)resources.getDimensionPixelSize(17105342);
        this.mEnableShadowOnChildNotifications = resources.getBoolean(R$bool.config_enableShadowOnChildNotifications);
        this.mShowDividersWhenExpanded = resources.getBoolean(R$bool.config_showDividersWhenGroupNotificationExpanded);
        this.mHideDividersDuringExpand = resources.getBoolean(R$bool.config_hideDividersDuringExpand);
        this.mTranslationForHeader = resources.getDimensionPixelSize(17105342) - this.mNotificationHeaderMargin;
        this.mHybridGroupManager.initDimens();
    }
    
    private void recreateLowPriorityHeader(final Notification$Builder notification$Builder) {
        final StatusBarNotification sbn = this.mContainingNotification.getEntry().getSbn();
        if (this.mIsLowPriority) {
            Notification$Builder recoverBuilder;
            if ((recoverBuilder = notification$Builder) == null) {
                recoverBuilder = Notification$Builder.recoverBuilder(this.getContext(), sbn.getNotification());
            }
            final RemoteViews lowPriorityContentView = recoverBuilder.makeLowPriorityContentView(true);
            if (this.mNotificationHeaderLowPriority == null) {
                final NotificationHeaderView mNotificationHeaderLowPriority = (NotificationHeaderView)lowPriorityContentView.apply(this.getContext(), (ViewGroup)this);
                this.mNotificationHeaderLowPriority = mNotificationHeaderLowPriority;
                mNotificationHeaderLowPriority.findViewById(16908938).setVisibility(0);
                this.mNotificationHeaderLowPriority.setOnClickListener(this.mHeaderClickListener);
                this.mNotificationHeaderWrapperLowPriority = NotificationViewWrapper.wrap(this.getContext(), (View)this.mNotificationHeaderLowPriority, this.mContainingNotification);
                this.addView((View)this.mNotificationHeaderLowPriority, 0);
                this.invalidate();
            }
            else {
                lowPriorityContentView.reapply(this.getContext(), (View)this.mNotificationHeaderLowPriority);
            }
            this.mNotificationHeaderWrapperLowPriority.onContentUpdated(this.mContainingNotification);
            this.resetHeaderVisibilityIfNeeded((View)this.mNotificationHeaderLowPriority, (View)this.calculateDesiredHeader());
        }
        else {
            this.removeView((View)this.mNotificationHeaderLowPriority);
            this.mNotificationHeaderLowPriority = null;
            this.mNotificationHeaderWrapperLowPriority = null;
        }
    }
    
    private void resetHeaderVisibilityIfNeeded(final View view, final View view2) {
        if (view == null) {
            return;
        }
        if (view != this.mCurrentHeader && view != view2) {
            this.getWrapperForView(view).setVisible(false);
            view.setVisibility(4);
        }
        if (view == view2 && view.getVisibility() != 0) {
            this.getWrapperForView(view).setVisible(true);
            view.setVisibility(0);
        }
    }
    
    private void startChildAlphaAnimations(final boolean b) {
        float alpha;
        if (b) {
            alpha = 1.0f;
        }
        else {
            alpha = 0.0f;
        }
        for (int size = this.mChildren.size(), n = 0; n < size && n < 5; ++n) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(n);
            expandableNotificationRow.setAlpha(1.0f - alpha);
            final ViewState viewState = new ViewState();
            viewState.initFrom((View)expandableNotificationRow);
            viewState.alpha = alpha;
            NotificationChildrenContainer.ALPHA_FADE_IN.setDelay(n * 50);
            viewState.animateTo((View)expandableNotificationRow, NotificationChildrenContainer.ALPHA_FADE_IN);
        }
    }
    
    private void updateChildrenClipping() {
        if (this.mContainingNotification.hasExpandingChild()) {
            return;
        }
        final int size = this.mChildren.size();
        final int actualHeight = this.mContainingNotification.getActualHeight();
        final int mClipBottomAmount = this.mClipBottomAmount;
        for (int i = 0; i < size; ++i) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(i);
            if (expandableNotificationRow.getVisibility() != 8) {
                final float translationY = expandableNotificationRow.getTranslationY();
                final float n = expandableNotificationRow.getActualHeight() + translationY;
                final float n2 = (float)(actualHeight - mClipBottomAmount);
                boolean b = true;
                boolean b2;
                int clipBottomAmount;
                if (translationY > n2) {
                    clipBottomAmount = ((b2 = false) ? 1 : 0);
                }
                else {
                    if (n > n2) {
                        clipBottomAmount = (int)(n - n2);
                    }
                    else {
                        clipBottomAmount = 0;
                    }
                    b2 = true;
                }
                if (expandableNotificationRow.getVisibility() != 0) {
                    b = false;
                }
                if (b2 != b) {
                    int visibility;
                    if (b2) {
                        visibility = 0;
                    }
                    else {
                        visibility = 4;
                    }
                    expandableNotificationRow.setVisibility(visibility);
                }
                expandableNotificationRow.setClipBottomAmount(clipBottomAmount);
            }
        }
    }
    
    private void updateExpansionStates() {
        if (!this.mChildrenExpanded) {
            if (!this.mUserLocked) {
                for (int size = this.mChildren.size(), i = 0; i < size; ++i) {
                    final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(i);
                    boolean systemChildExpanded = true;
                    if (i != 0 || size != 1) {
                        systemChildExpanded = false;
                    }
                    expandableNotificationRow.setSystemChildExpanded(systemChildExpanded);
                }
            }
        }
    }
    
    private void updateHeaderTouchability() {
        final NotificationHeaderView mNotificationHeader = this.mNotificationHeader;
        if (mNotificationHeader != null) {
            mNotificationHeader.setAcceptAllTouches(this.mChildrenExpanded || this.mUserLocked);
        }
    }
    
    private void updateHeaderTransformation() {
        if (this.mUserLocked && this.showingAsLowPriority()) {
            final float groupExpandFraction = this.getGroupExpandFraction();
            this.mNotificationHeaderWrapper.transformFrom(this.mNotificationHeaderWrapperLowPriority, groupExpandFraction);
            this.mNotificationHeader.setVisibility(0);
            this.mNotificationHeaderWrapperLowPriority.transformTo(this.mNotificationHeaderWrapper, groupExpandFraction);
        }
    }
    
    private void updateHeaderVisibility(final boolean b) {
        final ViewGroup mCurrentHeader = this.mCurrentHeader;
        final ViewGroup calculateDesiredHeader = this.calculateDesiredHeader();
        if (mCurrentHeader == calculateDesiredHeader) {
            return;
        }
        boolean b2 = b;
        if (b) {
            if (calculateDesiredHeader != null && mCurrentHeader != null) {
                mCurrentHeader.setVisibility(0);
                calculateDesiredHeader.setVisibility(0);
                final NotificationViewWrapper wrapperForView = this.getWrapperForView((View)calculateDesiredHeader);
                final NotificationViewWrapper wrapperForView2 = this.getWrapperForView((View)mCurrentHeader);
                wrapperForView.transformFrom(wrapperForView2);
                wrapperForView2.transformTo(wrapperForView, new _$$Lambda$NotificationChildrenContainer$yaCA55rJjaS5fwWl4gZlw69MJ2w(this));
                this.startChildAlphaAnimations(calculateDesiredHeader == this.mNotificationHeader);
                b2 = b;
            }
            else {
                b2 = false;
            }
        }
        if (!b2) {
            if (calculateDesiredHeader != null) {
                this.getWrapperForView((View)calculateDesiredHeader).setVisible(true);
                calculateDesiredHeader.setVisibility(0);
            }
            if (mCurrentHeader != null) {
                final NotificationViewWrapper wrapperForView3 = this.getWrapperForView((View)mCurrentHeader);
                if (wrapperForView3 != null) {
                    wrapperForView3.setVisible(false);
                }
                mCurrentHeader.setVisibility(4);
            }
        }
        this.resetHeaderVisibilityIfNeeded((View)this.mNotificationHeader, (View)calculateDesiredHeader);
        this.resetHeaderVisibilityIfNeeded((View)this.mNotificationHeaderLowPriority, (View)calculateDesiredHeader);
        this.mCurrentHeader = calculateDesiredHeader;
    }
    
    public void addNotification(final ExpandableNotificationRow expandableNotificationRow, final int n) {
        int size = n;
        if (n < 0) {
            size = this.mChildren.size();
        }
        this.mChildren.add(size, expandableNotificationRow);
        this.addView((View)expandableNotificationRow);
        expandableNotificationRow.setUserLocked(this.mUserLocked);
        final View inflateDivider = this.inflateDivider();
        this.addView(inflateDivider);
        this.mDividers.add(size, inflateDivider);
        this.updateGroupOverflow();
        expandableNotificationRow.setContentTransformationAmount(0.0f, false);
        final ExpandableViewState viewState = expandableNotificationRow.getViewState();
        if (viewState != null) {
            viewState.cancelAnimations((View)expandableNotificationRow);
            expandableNotificationRow.cancelAppearDrawing();
        }
    }
    
    public boolean applyChildOrder(final List<? extends NotificationListItem> list, final VisualStabilityManager visualStabilityManager, final VisualStabilityManager.Callback callback) {
        int n = 0;
        if (list == null) {
            return false;
        }
        boolean b = false;
        while (n < this.mChildren.size() && n < list.size()) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(n);
            final ExpandableNotificationRow expandableNotificationRow2 = (ExpandableNotificationRow)list.get(n);
            boolean b2 = b;
            if (expandableNotificationRow != expandableNotificationRow2) {
                if (visualStabilityManager.canReorderNotification(expandableNotificationRow2)) {
                    this.mChildren.remove(expandableNotificationRow2);
                    this.mChildren.add(n, expandableNotificationRow2);
                    b2 = true;
                }
                else {
                    visualStabilityManager.addReorderingAllowedCallback(callback);
                    b2 = b;
                }
            }
            ++n;
            b = b2;
        }
        this.updateExpansionStates();
        return b;
    }
    
    public void applyState() {
        final int size = this.mChildren.size();
        final ViewState viewState = new ViewState();
        float groupExpandFraction;
        if (this.mUserLocked) {
            groupExpandFraction = this.getGroupExpandFraction();
        }
        else {
            groupExpandFraction = 0.0f;
        }
        final boolean b = (this.mUserLocked && !this.showingAsLowPriority()) || (this.mChildrenExpanded && this.mShowDividersWhenExpanded) || (this.mContainingNotification.isGroupExpansionChanging() && !this.mHideDividersDuringExpand);
        for (int i = 0; i < size; ++i) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(i);
            final ExpandableViewState viewState2 = expandableNotificationRow.getViewState();
            viewState2.applyToView((View)expandableNotificationRow);
            final View view = this.mDividers.get(i);
            viewState.initFrom(view);
            viewState.yTranslation = viewState2.yTranslation - this.mDividerHeight;
            float mDividerAlpha;
            if (this.mChildrenExpanded && viewState2.alpha != 0.0f) {
                mDividerAlpha = this.mDividerAlpha;
            }
            else {
                mDividerAlpha = 0.0f;
            }
            float interpolate = mDividerAlpha;
            if (this.mUserLocked) {
                interpolate = mDividerAlpha;
                if (!this.showingAsLowPriority()) {
                    final float alpha = viewState2.alpha;
                    interpolate = mDividerAlpha;
                    if (alpha != 0.0f) {
                        interpolate = NotificationUtils.interpolate(0.0f, 0.5f, Math.min(alpha, groupExpandFraction));
                    }
                }
            }
            viewState.hidden = (b ^ true);
            viewState.alpha = interpolate;
            viewState.applyToView(view);
            expandableNotificationRow.setFakeShadowIntensity(0.0f, 0.0f, 0, 0);
        }
        final ViewState mGroupOverFlowState = this.mGroupOverFlowState;
        if (mGroupOverFlowState != null) {
            mGroupOverFlowState.applyToView((View)this.mOverflowNumber);
            this.mNeverAppliedGroupState = false;
        }
        final ViewState mHeaderViewState = this.mHeaderViewState;
        if (mHeaderViewState != null) {
            mHeaderViewState.applyToView((View)this.mNotificationHeader);
        }
        this.updateChildrenClipping();
    }
    
    public int getCollapsedHeight() {
        return this.getMinHeight(this.getMaxAllowedVisibleChildren(true), false);
    }
    
    public int getCollapsedHeightWithoutHeader() {
        return this.getMinHeight(this.getMaxAllowedVisibleChildren(true), false, 0);
    }
    
    @VisibleForTesting
    public ViewGroup getCurrentHeaderView() {
        return this.mCurrentHeader;
    }
    
    public float getGroupExpandFraction() {
        int n;
        if (this.showingAsLowPriority()) {
            n = this.getMaxContentHeight();
        }
        else {
            n = this.getVisibleChildrenExpandHeight();
        }
        final int collapsedHeight = this.getCollapsedHeight();
        return Math.max(0.0f, Math.min(1.0f, (this.mActualHeight - collapsedHeight) / (float)(n - collapsedHeight)));
    }
    
    public NotificationHeaderView getHeaderView() {
        return this.mNotificationHeader;
    }
    
    public float getIncreasedPaddingAmount() {
        if (this.showingAsLowPriority()) {
            return 0.0f;
        }
        return this.getGroupExpandFraction();
    }
    
    public int getIntrinsicHeight() {
        return this.getIntrinsicHeight((float)this.getMaxAllowedVisibleChildren());
    }
    
    public NotificationHeaderView getLowPriorityHeaderView() {
        return this.mNotificationHeaderLowPriority;
    }
    
    @VisibleForTesting
    int getMaxAllowedVisibleChildren() {
        return this.getMaxAllowedVisibleChildren(false);
    }
    
    @VisibleForTesting
    int getMaxAllowedVisibleChildren(final boolean b) {
        if (!b && (this.mChildrenExpanded || this.mContainingNotification.isUserLocked()) && !this.showingAsLowPriority()) {
            return 8;
        }
        if (!this.mIsLowPriority && (this.mContainingNotification.isOnKeyguard() || !this.mContainingNotification.isExpanded()) && (!this.mContainingNotification.isHeadsUpState() || !this.mContainingNotification.canShowHeadsUp())) {
            return 2;
        }
        return 5;
    }
    
    public int getMaxContentHeight() {
        if (this.showingAsLowPriority()) {
            return this.getMinHeight(5, true);
        }
        int n = this.mNotificationHeaderMargin + this.mCurrentHeaderTranslation + this.mNotificatonTopPadding;
        int size;
        int n2;
        int n3;
        for (size = this.mChildren.size(), n2 = 0, n3 = 0; n2 < size && n3 < 8; ++n3, ++n2) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(n2);
            int n4;
            if (expandableNotificationRow.isExpanded(true)) {
                n4 = expandableNotificationRow.getMaxExpandHeight();
            }
            else {
                n4 = expandableNotificationRow.getShowingLayout().getMinHeight(true);
            }
            n += (int)(float)n4;
        }
        int n5 = n;
        if (n3 > 0) {
            n5 = n + n3 * this.mDividerHeight;
        }
        return n5;
    }
    
    public int getMinHeight() {
        return this.getMinHeight(2, false);
    }
    
    public int getNotificationChildCount() {
        return this.mChildren.size();
    }
    
    public List<ExpandableNotificationRow> getNotificationChildren() {
        return this.mChildren;
    }
    
    public int getPositionInLinearLayout(final View view) {
        int n = this.mNotificationHeaderMargin + this.mCurrentHeaderTranslation + this.mNotificatonTopPadding;
        for (int i = 0; i < this.mChildren.size(); ++i) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(i);
            final boolean b = expandableNotificationRow.getVisibility() != 8;
            int n2 = n;
            if (b) {
                n2 = n + this.mDividerHeight;
            }
            if (expandableNotificationRow == view) {
                return n2;
            }
            n = n2;
            if (b) {
                n = n2 + expandableNotificationRow.getIntrinsicHeight();
            }
        }
        return 0;
    }
    
    public ExpandableNotificationRow getViewAtPosition(final float n) {
        for (int size = this.mChildren.size(), i = 0; i < size; ++i) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(i);
            final float translationY = expandableNotificationRow.getTranslationY();
            final float n2 = (float)expandableNotificationRow.getClipTopAmount();
            final float n3 = (float)expandableNotificationRow.getActualHeight();
            if (n >= n2 + translationY && n <= translationY + n3) {
                return expandableNotificationRow;
            }
        }
        return null;
    }
    
    public NotificationHeaderView getVisibleHeader() {
        NotificationHeaderView notificationHeaderView = this.mNotificationHeader;
        if (this.showingAsLowPriority()) {
            notificationHeaderView = this.mNotificationHeaderLowPriority;
        }
        return notificationHeaderView;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    @VisibleForTesting
    public boolean isUserLocked() {
        return this.mUserLocked;
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.updateGroupOverflow();
    }
    
    public void onExpansionChanged() {
        if (this.mIsLowPriority) {
            final boolean mUserLocked = this.mUserLocked;
            if (mUserLocked) {
                this.setUserLocked(mUserLocked);
            }
            this.updateHeaderVisibility(true);
        }
    }
    
    protected void onLayout(final boolean b, int i, int n, final int n2, final int n3) {
        View view;
        for (n = Math.min(this.mChildren.size(), 8), i = 0; i < n; ++i) {
            view = (View)this.mChildren.get(i);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            this.mDividers.get(i).layout(0, 0, this.getWidth(), this.mDividerHeight);
        }
        if (this.mOverflowNumber != null) {
            n = this.getLayoutDirection();
            i = 1;
            if (n != 1) {
                i = 0;
            }
            if (i != 0) {
                i = 0;
            }
            else {
                i = this.getWidth() - this.mOverflowNumber.getMeasuredWidth();
            }
            n = this.mOverflowNumber.getMeasuredWidth();
            final TextView mOverflowNumber = this.mOverflowNumber;
            mOverflowNumber.layout(i, 0, n + i, mOverflowNumber.getMeasuredHeight());
        }
        final NotificationHeaderView mNotificationHeader = this.mNotificationHeader;
        if (mNotificationHeader != null) {
            mNotificationHeader.layout(0, 0, mNotificationHeader.getMeasuredWidth(), this.mNotificationHeader.getMeasuredHeight());
        }
        final NotificationHeaderView mNotificationHeaderLowPriority = this.mNotificationHeaderLowPriority;
        if (mNotificationHeaderLowPriority != null) {
            mNotificationHeaderLowPriority.layout(0, 0, mNotificationHeaderLowPriority.getMeasuredWidth(), this.mNotificationHeaderLowPriority.getMeasuredHeight());
        }
    }
    
    protected void onMeasure(final int n, int n2) {
        final int mode = View$MeasureSpec.getMode(n2);
        final boolean b = mode == 1073741824;
        final boolean b2 = mode == Integer.MIN_VALUE;
        final int size = View$MeasureSpec.getSize(n2);
        int measureSpec;
        if (!b && !b2) {
            measureSpec = n2;
        }
        else {
            measureSpec = View$MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
        }
        final int size2 = View$MeasureSpec.getSize(n);
        final TextView mOverflowNumber = this.mOverflowNumber;
        if (mOverflowNumber != null) {
            mOverflowNumber.measure(View$MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE), measureSpec);
        }
        final int measureSpec2 = View$MeasureSpec.makeMeasureSpec(this.mDividerHeight, 1073741824);
        n2 = this.mNotificationHeaderMargin + this.mNotificatonTopPadding;
        final int min = Math.min(this.mChildren.size(), 8);
        int maxAllowedVisibleChildren = this.getMaxAllowedVisibleChildren(true);
        if (min > maxAllowedVisibleChildren) {
            --maxAllowedVisibleChildren;
        }
        else {
            maxAllowedVisibleChildren = -1;
        }
        int n3;
        for (int i = 0; i < min; ++i, n2 = n3) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(i);
            int measuredWidth = 0;
            Label_0236: {
                if (i == maxAllowedVisibleChildren) {
                    final TextView mOverflowNumber2 = this.mOverflowNumber;
                    if (mOverflowNumber2 != null) {
                        measuredWidth = mOverflowNumber2.getMeasuredWidth();
                        break Label_0236;
                    }
                }
                measuredWidth = 0;
            }
            expandableNotificationRow.setSingleLineWidthIndention(measuredWidth);
            expandableNotificationRow.measure(n, measureSpec);
            this.mDividers.get(i).measure(n, measureSpec2);
            n3 = n2;
            if (expandableNotificationRow.getVisibility() != 8) {
                n3 = n2 + (expandableNotificationRow.getMeasuredHeight() + this.mDividerHeight);
            }
        }
        this.mRealHeight = n2;
        int min2 = n2;
        if (mode != 0) {
            min2 = Math.min(n2, size);
        }
        n2 = View$MeasureSpec.makeMeasureSpec(this.mHeaderHeight, 1073741824);
        final NotificationHeaderView mNotificationHeader = this.mNotificationHeader;
        if (mNotificationHeader != null) {
            mNotificationHeader.measure(n, n2);
        }
        if (this.mNotificationHeaderLowPriority != null) {
            n2 = View$MeasureSpec.makeMeasureSpec(this.mHeaderHeight, 1073741824);
            this.mNotificationHeaderLowPriority.measure(n, n2);
        }
        this.setMeasuredDimension(size2, min2);
    }
    
    public void onNotificationUpdated() {
        this.mHybridGroupManager.setOverflowNumberColor(this.mOverflowNumber, this.mContainingNotification.getNotificationColor());
    }
    
    public boolean pointInView(final float n, final float n2, final float n3) {
        final float n4 = -n3;
        return n >= n4 && n2 >= n4 && n < super.mRight - super.mLeft + n3 && n2 < this.mRealHeight + n3;
    }
    
    public void prepareExpansionChanged() {
    }
    
    public void reInflateViews(final View$OnClickListener view$OnClickListener, final StatusBarNotification statusBarNotification) {
        final NotificationHeaderView mNotificationHeader = this.mNotificationHeader;
        if (mNotificationHeader != null) {
            this.removeView((View)mNotificationHeader);
            this.mNotificationHeader = null;
        }
        final NotificationHeaderView mNotificationHeaderLowPriority = this.mNotificationHeaderLowPriority;
        if (mNotificationHeaderLowPriority != null) {
            this.removeView((View)mNotificationHeaderLowPriority);
            this.mNotificationHeaderLowPriority = null;
        }
        this.recreateNotificationHeader(view$OnClickListener);
        this.initDimens();
        for (int i = 0; i < this.mDividers.size(); ++i) {
            final View view = this.mDividers.get(i);
            final int indexOfChild = this.indexOfChild(view);
            this.removeView(view);
            final View inflateDivider = this.inflateDivider();
            this.addView(inflateDivider, indexOfChild);
            this.mDividers.set(i, inflateDivider);
        }
        this.removeView((View)this.mOverflowNumber);
        this.mOverflowNumber = null;
        this.mGroupOverFlowState = null;
        this.updateGroupOverflow();
    }
    
    public void recreateNotificationHeader(final View$OnClickListener mHeaderClickListener) {
        this.mHeaderClickListener = mHeaderClickListener;
        final Notification$Builder recoverBuilder = Notification$Builder.recoverBuilder(this.getContext(), this.mContainingNotification.getEntry().getSbn().getNotification());
        final RemoteViews notificationHeader = recoverBuilder.makeNotificationHeader();
        if (this.mNotificationHeader == null) {
            final NotificationHeaderView mNotificationHeader = (NotificationHeaderView)notificationHeader.apply(this.getContext(), (ViewGroup)this);
            this.mNotificationHeader = mNotificationHeader;
            mNotificationHeader.findViewById(16908938).setVisibility(0);
            this.mNotificationHeader.setOnClickListener(this.mHeaderClickListener);
            this.mNotificationHeaderWrapper = NotificationViewWrapper.wrap(this.getContext(), (View)this.mNotificationHeader, this.mContainingNotification);
            this.addView((View)this.mNotificationHeader, 0);
            this.invalidate();
        }
        else {
            notificationHeader.reapply(this.getContext(), (View)this.mNotificationHeader);
        }
        this.mNotificationHeaderWrapper.onContentUpdated(this.mContainingNotification);
        this.recreateLowPriorityHeader(recoverBuilder);
        this.updateHeaderVisibility(false);
        this.updateChildrenHeaderAppearance();
    }
    
    public void removeNotification(final ExpandableNotificationRow expandableNotificationRow) {
        final int index = this.mChildren.indexOf(expandableNotificationRow);
        this.mChildren.remove(expandableNotificationRow);
        this.removeView((View)expandableNotificationRow);
        final View view = this.mDividers.remove(index);
        this.removeView(view);
        this.getOverlay().add(view);
        CrossFadeHelper.fadeOut(view, new Runnable() {
            @Override
            public void run() {
                NotificationChildrenContainer.this.getOverlay().remove(view);
            }
        });
        expandableNotificationRow.setSystemChildExpanded(false);
        expandableNotificationRow.setUserLocked(false);
        this.updateGroupOverflow();
        if (!expandableNotificationRow.isRemoved()) {
            this.mHeaderUtil.restoreNotificationHeader(expandableNotificationRow);
        }
    }
    
    public void setActualHeight(int mActualHeight) {
        if (!this.mUserLocked) {
            return;
        }
        this.mActualHeight = mActualHeight;
        final float groupExpandFraction = this.getGroupExpandFraction();
        final boolean showingAsLowPriority = this.showingAsLowPriority();
        this.updateHeaderTransformation();
        final int maxAllowedVisibleChildren = this.getMaxAllowedVisibleChildren(true);
        for (int size = this.mChildren.size(), i = 0; i < size; ++i) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(i);
            if (showingAsLowPriority) {
                mActualHeight = expandableNotificationRow.getShowingLayout().getMinHeight(false);
            }
            else if (expandableNotificationRow.isExpanded(true)) {
                mActualHeight = expandableNotificationRow.getMaxExpandHeight();
            }
            else {
                mActualHeight = expandableNotificationRow.getShowingLayout().getMinHeight(true);
            }
            final float n = (float)mActualHeight;
            if (i < maxAllowedVisibleChildren) {
                expandableNotificationRow.setActualHeight((int)NotificationUtils.interpolate((float)expandableNotificationRow.getShowingLayout().getMinHeight(false), n, groupExpandFraction), false);
            }
            else {
                expandableNotificationRow.setActualHeight((int)n, false);
            }
        }
    }
    
    public void setChildrenExpanded(final boolean b) {
        this.mChildrenExpanded = b;
        this.updateExpansionStates();
        final NotificationHeaderView mNotificationHeader = this.mNotificationHeader;
        if (mNotificationHeader != null) {
            mNotificationHeader.setExpanded(b);
        }
        for (int size = this.mChildren.size(), i = 0; i < size; ++i) {
            this.mChildren.get(i).setChildrenExpanded(b, false);
        }
        this.updateHeaderTouchability();
    }
    
    public void setClipBottomAmount(final int mClipBottomAmount) {
        this.mClipBottomAmount = mClipBottomAmount;
        this.updateChildrenClipping();
    }
    
    public void setContainingNotification(final ExpandableNotificationRow mContainingNotification) {
        this.mContainingNotification = mContainingNotification;
        this.mHeaderUtil = new NotificationHeaderUtil(this.mContainingNotification);
    }
    
    public void setCurrentBottomRoundness(final float n) {
        int i = this.mChildren.size();
        int n2 = 1;
        --i;
        while (i >= 0) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(i);
            if (expandableNotificationRow.getVisibility() != 8) {
                float n3;
                if (n2 != 0) {
                    n3 = n;
                }
                else {
                    n3 = 0.0f;
                }
                expandableNotificationRow.setBottomRoundness(n3, this.isShown());
                n2 = 0;
            }
            --i;
        }
    }
    
    public void setHeaderVisibleAmount(final float mHeaderVisibleAmount) {
        this.mHeaderVisibleAmount = mHeaderVisibleAmount;
        this.mCurrentHeaderTranslation = (int)((1.0f - mHeaderVisibleAmount) * this.mTranslationForHeader);
    }
    
    public void setIsLowPriority(final boolean mIsLowPriority) {
        this.mIsLowPriority = mIsLowPriority;
        if (this.mContainingNotification != null) {
            this.recreateLowPriorityHeader(null);
            this.updateHeaderVisibility(false);
        }
        final boolean mUserLocked = this.mUserLocked;
        if (mUserLocked) {
            this.setUserLocked(mUserLocked);
        }
    }
    
    public void setRecentlyAudiblyAlerted(final boolean b) {
        final NotificationViewWrapper mNotificationHeaderWrapper = this.mNotificationHeaderWrapper;
        if (mNotificationHeaderWrapper != null) {
            mNotificationHeaderWrapper.setRecentlyAudiblyAlerted(b);
        }
        final NotificationViewWrapper mNotificationHeaderWrapperLowPriority = this.mNotificationHeaderWrapperLowPriority;
        if (mNotificationHeaderWrapperLowPriority != null) {
            mNotificationHeaderWrapperLowPriority.setRecentlyAudiblyAlerted(b);
        }
    }
    
    public void setShelfIconVisible(final boolean b) {
        final NotificationViewWrapper mNotificationHeaderWrapper = this.mNotificationHeaderWrapper;
        if (mNotificationHeaderWrapper != null) {
            final NotificationHeaderView notificationHeader = mNotificationHeaderWrapper.getNotificationHeader();
            if (notificationHeader != null) {
                notificationHeader.getIcon().setForceHidden(b);
            }
        }
        final NotificationViewWrapper mNotificationHeaderWrapperLowPriority = this.mNotificationHeaderWrapperLowPriority;
        if (mNotificationHeaderWrapperLowPriority != null) {
            final NotificationHeaderView notificationHeader2 = mNotificationHeaderWrapperLowPriority.getNotificationHeader();
            if (notificationHeader2 != null) {
                notificationHeader2.getIcon().setForceHidden(b);
            }
        }
    }
    
    public void setUserLocked(final boolean mUserLocked) {
        if (!(this.mUserLocked = mUserLocked)) {
            this.updateHeaderVisibility(false);
        }
        for (int size = this.mChildren.size(), i = 0; i < size; ++i) {
            this.mChildren.get(i).setUserLocked(mUserLocked && !this.showingAsLowPriority());
        }
        this.updateHeaderTouchability();
    }
    
    public void showAppOpsIcons(final ArraySet<Integer> set) {
        final NotificationViewWrapper mNotificationHeaderWrapper = this.mNotificationHeaderWrapper;
        if (mNotificationHeaderWrapper != null) {
            mNotificationHeaderWrapper.showAppOpsIcons(set);
        }
        final NotificationViewWrapper mNotificationHeaderWrapperLowPriority = this.mNotificationHeaderWrapperLowPriority;
        if (mNotificationHeaderWrapperLowPriority != null) {
            mNotificationHeaderWrapperLowPriority.showAppOpsIcons(set);
        }
    }
    
    public boolean showingAsLowPriority() {
        return this.mIsLowPriority && !this.mContainingNotification.isExpanded();
    }
    
    public void startAnimationToState(final AnimationProperties animationProperties) {
        int i = this.mChildren.size();
        final ViewState viewState = new ViewState();
        final float groupExpandFraction = this.getGroupExpandFraction();
        final boolean b = (this.mUserLocked && !this.showingAsLowPriority()) || (this.mChildrenExpanded && this.mShowDividersWhenExpanded) || (this.mContainingNotification.isGroupExpansionChanging() && !this.mHideDividersDuringExpand);
        --i;
        while (i >= 0) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(i);
            final ExpandableViewState viewState2 = expandableNotificationRow.getViewState();
            viewState2.animateTo((View)expandableNotificationRow, animationProperties);
            final View view = this.mDividers.get(i);
            viewState.initFrom(view);
            viewState.yTranslation = viewState2.yTranslation - this.mDividerHeight;
            float n;
            if (this.mChildrenExpanded && viewState2.alpha != 0.0f) {
                n = 0.5f;
            }
            else {
                n = 0.0f;
            }
            float interpolate = n;
            if (this.mUserLocked) {
                interpolate = n;
                if (!this.showingAsLowPriority()) {
                    final float alpha = viewState2.alpha;
                    interpolate = n;
                    if (alpha != 0.0f) {
                        interpolate = NotificationUtils.interpolate(0.0f, 0.5f, Math.min(alpha, groupExpandFraction));
                    }
                }
            }
            viewState.hidden = (b ^ true);
            viewState.alpha = interpolate;
            viewState.animateTo(view, animationProperties);
            expandableNotificationRow.setFakeShadowIntensity(0.0f, 0.0f, 0, 0);
            --i;
        }
        final TextView mOverflowNumber = this.mOverflowNumber;
        if (mOverflowNumber != null) {
            if (this.mNeverAppliedGroupState) {
                final ViewState mGroupOverFlowState = this.mGroupOverFlowState;
                final float alpha2 = mGroupOverFlowState.alpha;
                mGroupOverFlowState.alpha = 0.0f;
                mGroupOverFlowState.applyToView((View)mOverflowNumber);
                this.mGroupOverFlowState.alpha = alpha2;
                this.mNeverAppliedGroupState = false;
            }
            this.mGroupOverFlowState.animateTo((View)this.mOverflowNumber, animationProperties);
        }
        final NotificationHeaderView mNotificationHeader = this.mNotificationHeader;
        if (mNotificationHeader != null) {
            this.mHeaderViewState.applyToView((View)mNotificationHeader);
        }
        this.updateChildrenClipping();
    }
    
    public void updateChildrenHeaderAppearance() {
        this.mHeaderUtil.updateChildrenHeaderAppearance();
    }
    
    public void updateGroupOverflow() {
        final int size = this.mChildren.size();
        final int maxAllowedVisibleChildren = this.getMaxAllowedVisibleChildren(true);
        if (size > maxAllowedVisibleChildren) {
            this.mOverflowNumber = this.mHybridGroupManager.bindOverflowNumber(this.mOverflowNumber, size - maxAllowedVisibleChildren);
            if (this.mGroupOverFlowState == null) {
                this.mGroupOverFlowState = new ViewState();
                this.mNeverAppliedGroupState = true;
            }
        }
        else {
            final TextView mOverflowNumber = this.mOverflowNumber;
            if (mOverflowNumber != null) {
                this.removeView((View)mOverflowNumber);
                if (this.isShown() && this.isAttachedToWindow()) {
                    final TextView mOverflowNumber2 = this.mOverflowNumber;
                    this.addTransientView((View)mOverflowNumber2, this.getTransientViewCount());
                    CrossFadeHelper.fadeOut((View)mOverflowNumber2, new Runnable() {
                        @Override
                        public void run() {
                            NotificationChildrenContainer.this.removeTransientView(mOverflowNumber2);
                        }
                    });
                }
                this.mOverflowNumber = null;
                this.mGroupOverFlowState = null;
            }
        }
    }
    
    public void updateHeaderForExpansion(final boolean b) {
        final NotificationHeaderView mNotificationHeader = this.mNotificationHeader;
        if (mNotificationHeader != null) {
            if (b) {
                final ColorDrawable headerBackgroundDrawable = new ColorDrawable();
                headerBackgroundDrawable.setColor(this.mContainingNotification.calculateBgColor());
                this.mNotificationHeader.setHeaderBackgroundDrawable((Drawable)headerBackgroundDrawable);
            }
            else {
                mNotificationHeader.setHeaderBackgroundDrawable((Drawable)null);
            }
        }
    }
    
    public void updateState(final ExpandableViewState expandableViewState, final AmbientState ambientState) {
        final int size = this.mChildren.size();
        int n = this.mNotificationHeaderMargin + this.mCurrentHeaderTranslation;
        final int n2 = this.getMaxAllowedVisibleChildren() - 1;
        int maxAllowedVisibleChildren = n2 + 1;
        final boolean b = this.mUserLocked && !this.showingAsLowPriority();
        float groupExpandFraction;
        if (this.mUserLocked) {
            groupExpandFraction = this.getGroupExpandFraction();
            maxAllowedVisibleChildren = this.getMaxAllowedVisibleChildren(true);
        }
        else {
            groupExpandFraction = 0.0f;
        }
        final boolean b2 = this.mChildrenExpanded && !this.mContainingNotification.isGroupExpansionChanging();
        int n3 = 1;
        int i = 0;
        int n4 = 0;
        while (i < size) {
            final ExpandableNotificationRow expandableNotificationRow = this.mChildren.get(i);
            int n5;
            if (n3 == 0) {
                if (b) {
                    n5 = (int)(n + NotificationUtils.interpolate((float)this.mChildPadding, (float)this.mDividerHeight, groupExpandFraction));
                }
                else {
                    int n6;
                    if (this.mChildrenExpanded) {
                        n6 = this.mDividerHeight;
                    }
                    else {
                        n6 = this.mChildPadding;
                    }
                    n5 = n + n6;
                }
            }
            else {
                if (b) {
                    n5 = (int)(n + NotificationUtils.interpolate(0.0f, (float)(this.mNotificatonTopPadding + this.mDividerHeight), groupExpandFraction));
                }
                else {
                    int n7;
                    if (this.mChildrenExpanded) {
                        n7 = this.mNotificatonTopPadding + this.mDividerHeight;
                    }
                    else {
                        n7 = 0;
                    }
                    n5 = n + n7;
                }
                n3 = 0;
            }
            final ExpandableViewState viewState = expandableNotificationRow.getViewState();
            final int intrinsicHeight = expandableNotificationRow.getIntrinsicHeight();
            viewState.height = intrinsicHeight;
            viewState.yTranslation = (float)(n5 + n4);
            viewState.hidden = false;
            float zTranslation;
            if (b2 && this.mEnableShadowOnChildNotifications) {
                zTranslation = expandableViewState.zTranslation;
            }
            else {
                zTranslation = 0.0f;
            }
            viewState.zTranslation = zTranslation;
            viewState.dimmed = expandableViewState.dimmed;
            viewState.hideSensitive = expandableViewState.hideSensitive;
            viewState.belowSpeedBump = expandableViewState.belowSpeedBump;
            viewState.clipTopAmount = 0;
            viewState.alpha = 0.0f;
            float alpha = 1.0f;
            if (i < maxAllowedVisibleChildren) {
                if (this.showingAsLowPriority()) {
                    alpha = groupExpandFraction;
                }
                viewState.alpha = alpha;
            }
            else if (groupExpandFraction == 1.0f && i <= n2) {
                final float n8 = (this.mActualHeight - viewState.yTranslation) / viewState.height;
                viewState.alpha = n8;
                viewState.alpha = Math.max(0.0f, Math.min(1.0f, n8));
            }
            viewState.location = expandableViewState.location;
            viewState.inShelf = expandableViewState.inShelf;
            n = n5 + intrinsicHeight;
            if (expandableNotificationRow.isExpandAnimationRunning()) {
                n4 = -ambientState.getExpandAnimationTopChange();
            }
            ++i;
        }
        if (this.mOverflowNumber != null) {
            final ExpandableNotificationRow expandableNotificationRow2 = this.mChildren.get(Math.min(this.getMaxAllowedVisibleChildren(true), size) - 1);
            this.mGroupOverFlowState.copyFrom(expandableNotificationRow2.getViewState());
            if (!this.mChildrenExpanded) {
                final HybridNotificationView singleLineView = expandableNotificationRow2.getSingleLineView();
                if (singleLineView != null) {
                    Object o;
                    if (((View)(o = singleLineView.getTextView())).getVisibility() == 8) {
                        o = singleLineView.getTitleView();
                    }
                    if (((View)o).getVisibility() == 8) {
                        o = singleLineView;
                    }
                    this.mGroupOverFlowState.alpha = ((View)o).getAlpha();
                    final ViewState mGroupOverFlowState = this.mGroupOverFlowState;
                    mGroupOverFlowState.yTranslation += NotificationUtils.getRelativeYOffset((View)o, (View)expandableNotificationRow2);
                }
            }
            else {
                final ViewState mGroupOverFlowState2 = this.mGroupOverFlowState;
                mGroupOverFlowState2.yTranslation += this.mNotificationHeaderMargin;
                mGroupOverFlowState2.alpha = 0.0f;
            }
        }
        if (this.mNotificationHeader != null) {
            if (this.mHeaderViewState == null) {
                this.mHeaderViewState = new ViewState();
            }
            this.mHeaderViewState.initFrom((View)this.mNotificationHeader);
            final ViewState mHeaderViewState = this.mHeaderViewState;
            float zTranslation2;
            if (b2) {
                zTranslation2 = expandableViewState.zTranslation;
            }
            else {
                zTranslation2 = 0.0f;
            }
            mHeaderViewState.zTranslation = zTranslation2;
            final ViewState mHeaderViewState2 = this.mHeaderViewState;
            mHeaderViewState2.yTranslation = (float)this.mCurrentHeaderTranslation;
            mHeaderViewState2.alpha = this.mHeaderVisibleAmount;
            mHeaderViewState2.hidden = false;
        }
    }
}
