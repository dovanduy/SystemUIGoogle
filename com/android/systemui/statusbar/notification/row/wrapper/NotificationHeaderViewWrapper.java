// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.wrapper;

import com.android.systemui.statusbar.TransformableView;
import android.util.ArraySet;
import com.android.systemui.statusbar.notification.ImageTransformState;
import com.android.systemui.statusbar.notification.TransformState;
import android.view.ViewGroup;
import java.util.Stack;
import android.view.View$OnClickListener;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.CustomInterpolatorTransformation;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.R$bool;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.content.Context;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import com.android.systemui.statusbar.ViewTransformationHelper;
import android.view.NotificationHeaderView;
import com.android.internal.widget.CachingIconView;
import android.widget.TextView;
import com.android.internal.widget.NotificationExpandButton;
import android.view.View;
import android.view.animation.Interpolator;

public class NotificationHeaderViewWrapper extends NotificationViewWrapper
{
    private static final Interpolator LOW_PRIORITY_HEADER_CLOSE;
    private View mAppOps;
    private View mAudiblyAlertedIcon;
    private View mCameraIcon;
    private NotificationExpandButton mExpandButton;
    private TextView mHeaderText;
    private CachingIconView mIcon;
    private boolean mIsLowPriority;
    private View mMicIcon;
    protected NotificationHeaderView mNotificationHeader;
    private View mOverlayIcon;
    private boolean mShowExpandButtonAtEnd;
    private boolean mTransformLowPriorityTitle;
    protected final ViewTransformationHelper mTransformationHelper;
    private ImageView mWorkProfileImage;
    
    static {
        LOW_PRIORITY_HEADER_CLOSE = (Interpolator)new PathInterpolator(0.4f, 0.0f, 0.7f, 1.0f);
    }
    
    protected NotificationHeaderViewWrapper(final Context context, final View view, final ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
        this.mShowExpandButtonAtEnd = (context.getResources().getBoolean(R$bool.config_showNotificationExpandButtonAtEnd) || NotificationUtils.useNewInterruptionModel(context));
        (this.mTransformationHelper = new ViewTransformationHelper()).setCustomTransformation((ViewTransformationHelper.CustomTransformation)new CustomInterpolatorTransformation(1) {
            @Override
            public Interpolator getCustomInterpolator(final int n, final boolean b) {
                final boolean b2 = NotificationHeaderViewWrapper.this.mView instanceof NotificationHeaderView;
                if (n != 16) {
                    return null;
                }
                if ((b2 && !b) || (!b2 && b)) {
                    return Interpolators.LINEAR_OUT_SLOW_IN;
                }
                return NotificationHeaderViewWrapper.LOW_PRIORITY_HEADER_CLOSE;
            }
            
            @Override
            protected boolean hasCustomTransformation() {
                return NotificationHeaderViewWrapper.this.mIsLowPriority && NotificationHeaderViewWrapper.this.mTransformLowPriorityTitle;
            }
        }, 1);
        this.resolveHeaderViews();
        this.addAppOpsOnClickListener(expandableNotificationRow);
    }
    
    private void addAppOpsOnClickListener(final ExpandableNotificationRow expandableNotificationRow) {
        final View$OnClickListener appOpsOnClickListener = expandableNotificationRow.getAppOpsOnClickListener();
        final NotificationHeaderView mNotificationHeader = this.mNotificationHeader;
        if (mNotificationHeader != null) {
            mNotificationHeader.setAppOpsOnClickListener(appOpsOnClickListener);
        }
        final View mAppOps = this.mAppOps;
        if (mAppOps != null) {
            mAppOps.setOnClickListener(appOpsOnClickListener);
        }
        final View mCameraIcon = this.mCameraIcon;
        if (mCameraIcon != null) {
            mCameraIcon.setOnClickListener(appOpsOnClickListener);
        }
        final View mMicIcon = this.mMicIcon;
        if (mMicIcon != null) {
            mMicIcon.setOnClickListener(appOpsOnClickListener);
        }
        final View mOverlayIcon = this.mOverlayIcon;
        if (mOverlayIcon != null) {
            mOverlayIcon.setOnClickListener(appOpsOnClickListener);
        }
    }
    
    private void addRemainingTransformTypes() {
        this.mTransformationHelper.addRemainingTransformTypes(super.mView);
    }
    
    private void updateCropToPaddingForImageViews() {
        final Stack<View> stack = new Stack<View>();
        stack.push(super.mView);
        while (!stack.isEmpty()) {
            final View view = stack.pop();
            if (view instanceof ImageView) {
                ((ImageView)view).setCropToPadding(true);
            }
            else {
                if (!(view instanceof ViewGroup)) {
                    continue;
                }
                final ViewGroup viewGroup = (ViewGroup)view;
                for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                    stack.push(viewGroup.getChildAt(i));
                }
            }
        }
    }
    
    @Override
    public TransformState getCurrentState(final int n) {
        return this.mTransformationHelper.getCurrentState(n);
    }
    
    @Override
    public NotificationHeaderView getNotificationHeader() {
        return this.mNotificationHeader;
    }
    
    @Override
    public int getOriginalIconColor() {
        return this.mIcon.getOriginalIconColor();
    }
    
    @Override
    public View getShelfTransformationTarget() {
        return (View)this.mIcon;
    }
    
    @Override
    public void onContentUpdated(final ExpandableNotificationRow expandableNotificationRow) {
        super.onContentUpdated(expandableNotificationRow);
        this.mIsLowPriority = expandableNotificationRow.isLowPriority();
        final boolean childInGroup = expandableNotificationRow.isChildInGroup();
        int i = 0;
        this.mTransformLowPriorityTitle = (!childInGroup && !expandableNotificationRow.isSummaryWithChildren());
        final ArraySet<View> allTransformingViews = this.mTransformationHelper.getAllTransformingViews();
        this.resolveHeaderViews();
        this.updateTransformedTypes();
        this.addRemainingTransformTypes();
        this.updateCropToPaddingForImageViews();
        this.mIcon.setTag(ImageTransformState.ICON_TAG, (Object)expandableNotificationRow.getEntry().getSbn().getNotification().getSmallIcon());
        final ArraySet<View> allTransformingViews2 = this.mTransformationHelper.getAllTransformingViews();
        while (i < allTransformingViews.size()) {
            final View view = (View)allTransformingViews.valueAt(i);
            if (!allTransformingViews2.contains((Object)view)) {
                this.mTransformationHelper.resetTransformedView(view);
            }
            ++i;
        }
    }
    
    protected void resolveHeaderViews() {
        this.mIcon = (CachingIconView)super.mView.findViewById(16908294);
        this.mHeaderText = (TextView)super.mView.findViewById(16909021);
        this.mExpandButton = (NotificationExpandButton)super.mView.findViewById(16908938);
        this.mWorkProfileImage = (ImageView)super.mView.findViewById(16909304);
        this.mNotificationHeader = (NotificationHeaderView)super.mView.findViewById(16909211);
        this.mCameraIcon = super.mView.findViewById(16908814);
        this.mMicIcon = super.mView.findViewById(16909158);
        this.mOverlayIcon = super.mView.findViewById(16909254);
        this.mAppOps = super.mView.findViewById(16908755);
        this.mAudiblyAlertedIcon = super.mView.findViewById(16908734);
        final NotificationHeaderView mNotificationHeader = this.mNotificationHeader;
        if (mNotificationHeader != null) {
            mNotificationHeader.setShowExpandButtonAtEnd(this.mShowExpandButtonAtEnd);
            this.mNotificationHeader.getOriginalIconColor();
        }
    }
    
    @Override
    public void setIsChildInGroup(final boolean isChildInGroup) {
        super.setIsChildInGroup(isChildInGroup);
        this.mTransformLowPriorityTitle = (isChildInGroup ^ true);
    }
    
    @Override
    public void setRecentlyAudiblyAlerted(final boolean b) {
        final View mAudiblyAlertedIcon = this.mAudiblyAlertedIcon;
        if (mAudiblyAlertedIcon != null) {
            int visibility;
            if (b) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            mAudiblyAlertedIcon.setVisibility(visibility);
        }
    }
    
    @Override
    public void setShelfIconVisible(final boolean b) {
        super.setShelfIconVisible(b);
        this.mIcon.setForceHidden(b);
    }
    
    @Override
    public void setVisible(final boolean b) {
        super.setVisible(b);
        this.mTransformationHelper.setVisible(b);
    }
    
    @Override
    public void showAppOpsIcons(final ArraySet<Integer> set) {
        if (set == null) {
            return;
        }
        final View mOverlayIcon = this.mOverlayIcon;
        final int n = 0;
        if (mOverlayIcon != null) {
            int visibility;
            if (set.contains((Object)24)) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            mOverlayIcon.setVisibility(visibility);
        }
        final View mCameraIcon = this.mCameraIcon;
        if (mCameraIcon != null) {
            int visibility2;
            if (set.contains((Object)26)) {
                visibility2 = 0;
            }
            else {
                visibility2 = 8;
            }
            mCameraIcon.setVisibility(visibility2);
        }
        final View mMicIcon = this.mMicIcon;
        if (mMicIcon != null) {
            int visibility3;
            if (set.contains((Object)27)) {
                visibility3 = n;
            }
            else {
                visibility3 = 8;
            }
            mMicIcon.setVisibility(visibility3);
        }
    }
    
    @Override
    public void transformFrom(final TransformableView transformableView) {
        this.mTransformationHelper.transformFrom(transformableView);
    }
    
    @Override
    public void transformFrom(final TransformableView transformableView, final float n) {
        this.mTransformationHelper.transformFrom(transformableView, n);
    }
    
    @Override
    public void transformTo(final TransformableView transformableView, final float n) {
        this.mTransformationHelper.transformTo(transformableView, n);
    }
    
    @Override
    public void transformTo(final TransformableView transformableView, final Runnable runnable) {
        this.mTransformationHelper.transformTo(transformableView, runnable);
    }
    
    @Override
    public void updateExpandability(final boolean b, View$OnClickListener onClickListener) {
        final NotificationExpandButton mExpandButton = this.mExpandButton;
        int visibility;
        if (b) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mExpandButton.setVisibility(visibility);
        final NotificationHeaderView mNotificationHeader = this.mNotificationHeader;
        if (mNotificationHeader != null) {
            if (!b) {
                onClickListener = null;
            }
            mNotificationHeader.setOnClickListener(onClickListener);
        }
    }
    
    protected void updateTransformedTypes() {
        this.mTransformationHelper.reset();
        this.mTransformationHelper.addTransformedView(0, (View)this.mIcon);
        this.mTransformationHelper.addViewTransformingToSimilar((View)this.mWorkProfileImage);
        if (this.mIsLowPriority) {
            final TextView mHeaderText = this.mHeaderText;
            if (mHeaderText != null) {
                this.mTransformationHelper.addTransformedView(1, (View)mHeaderText);
            }
        }
        final View mCameraIcon = this.mCameraIcon;
        if (mCameraIcon != null) {
            this.mTransformationHelper.addViewTransformingToSimilar(mCameraIcon);
        }
        final View mMicIcon = this.mMicIcon;
        if (mMicIcon != null) {
            this.mTransformationHelper.addViewTransformingToSimilar(mMicIcon);
        }
        final View mOverlayIcon = this.mOverlayIcon;
        if (mOverlayIcon != null) {
            this.mTransformationHelper.addViewTransformingToSimilar(mOverlayIcon);
        }
        final View mAudiblyAlertedIcon = this.mAudiblyAlertedIcon;
        if (mAudiblyAlertedIcon != null) {
            this.mTransformationHelper.addViewTransformingToSimilar(mAudiblyAlertedIcon);
        }
    }
}
