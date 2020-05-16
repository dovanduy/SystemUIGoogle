// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.R$string;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.annotations.VisibleForTesting;
import android.content.res.Configuration;
import com.android.systemui.Dependency;
import android.view.DisplayCutout;
import android.view.WindowInsets;
import com.android.systemui.statusbar.notification.stack.ExpandableViewState;
import android.util.MathUtils;
import android.view.View$OnAttachStateChangeListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver$OnPreDrawListener;
import com.android.systemui.statusbar.notification.stack.ViewState;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.NotificationUtils;
import android.view.ViewGroup$LayoutParams;
import android.content.res.Resources;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import android.view.View;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.R$id;
import android.os.SystemProperties;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.NotificationIconContainer;
import android.graphics.Rect;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.notification.stack.AmbientState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.view.View$OnLayoutChangeListener;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;

public class NotificationShelf extends ActivatableNotificationView implements View$OnLayoutChangeListener, StateListener
{
    private static final boolean ICON_ANMATIONS_WHILE_SCROLLING;
    private static final int TAG_CONTINUOUS_CLIPPING;
    private static final boolean USE_ANIMATIONS_WHEN_OPENING;
    private AmbientState mAmbientState;
    private boolean mAnimationsEnabled;
    private final KeyguardBypassController mBypassController;
    private Rect mClipRect;
    private NotificationIconContainer mCollapsedIcons;
    private int mCutoutHeight;
    private float mFirstElementRoundness;
    private int mGapHeight;
    private boolean mHasItemsInStableShelf;
    private float mHiddenShelfIconSize;
    private boolean mHideBackground;
    private NotificationStackScrollLayout mHostLayout;
    private int mIconAppearTopPadding;
    private int mIconSize;
    private boolean mInteractive;
    private int mMaxLayoutHeight;
    private boolean mNoAnimationsInThisFrame;
    private int mNotGoneIndex;
    private float mOpenedAmount;
    private int mPaddingBetweenElements;
    private int mRelativeOffset;
    private int mScrollFastThreshold;
    private NotificationIconContainer mShelfIcons;
    private boolean mShowNotificationShelf;
    private int mStatusBarHeight;
    private int mStatusBarState;
    private int[] mTmp;
    
    static {
        USE_ANIMATIONS_WHEN_OPENING = SystemProperties.getBoolean("debug.icon_opening_animations", true);
        ICON_ANMATIONS_WHILE_SCROLLING = SystemProperties.getBoolean("debug.icon_scroll_animations", true);
        TAG_CONTINUOUS_CLIPPING = R$id.continuous_clipping_tag;
    }
    
    public NotificationShelf(final Context context, final AttributeSet set, final KeyguardBypassController mBypassController) {
        super(context, set);
        this.mTmp = new int[2];
        this.mAnimationsEnabled = true;
        this.mClipRect = new Rect();
        this.mBypassController = mBypassController;
    }
    
    private float calculateIconTransformationStart(final ExpandableView expandableView) {
        final View shelfTransformationTarget = expandableView.getShelfTransformationTarget();
        if (shelfTransformationTarget == null) {
            return expandableView.getTranslationY();
        }
        return expandableView.getTranslationY() + expandableView.getRelativeTopPadding(shelfTransformationTarget) - expandableView.getShelfIcon().getTop();
    }
    
    private void clipTransientViews() {
        for (int i = 0; i < this.mHostLayout.getTransientViewCount(); ++i) {
            final View transientView = this.mHostLayout.getTransientView(i);
            if (transientView instanceof ExpandableView) {
                this.updateNotificationClipHeight((ExpandableView)transientView, this.getTranslationY(), -1);
            }
        }
    }
    
    private float getFullyClosedTranslation() {
        return (float)(-(this.getIntrinsicHeight() - this.mStatusBarHeight) / 2);
    }
    
    private NotificationIconContainer.IconState getIconState(final StatusBarIconView statusBarIconView) {
        return this.mShelfIcons.getIconState(statusBarIconView);
    }
    
    private void handleCustomTransformHeight(final ExpandableView expandableView, final boolean b, final NotificationIconContainer.IconState iconState) {
        if (iconState != null && b && this.mAmbientState.getScrollY() == 0 && !this.mAmbientState.isOnKeyguard() && !iconState.isLastExpandIcon) {
            final float n = (float)(this.mAmbientState.getIntrinsicPadding() + this.mHostLayout.getPositionInLinearLayout((View)expandableView));
            final float n2 = (float)(this.mMaxLayoutHeight - this.getIntrinsicHeight());
            if (n < n2 && expandableView.getIntrinsicHeight() + n >= n2 && expandableView.getTranslationY() < n) {
                boolean b2 = true;
                iconState.isLastExpandIcon = true;
                iconState.customTransformHeight = Integer.MIN_VALUE;
                if (this.mMaxLayoutHeight - this.getIntrinsicHeight() - n >= this.getIntrinsicHeight()) {
                    b2 = false;
                }
                if (!b2) {
                    iconState.customTransformHeight = (int)(this.mMaxLayoutHeight - this.getIntrinsicHeight() - n);
                }
            }
        }
    }
    
    private void initDimens() {
        final Resources resources = this.getResources();
        this.mIconAppearTopPadding = resources.getDimensionPixelSize(R$dimen.notification_icon_appear_padding);
        this.mStatusBarHeight = resources.getDimensionPixelOffset(R$dimen.status_bar_height);
        resources.getDimensionPixelOffset(R$dimen.status_bar_padding_start);
        this.mPaddingBetweenElements = resources.getDimensionPixelSize(R$dimen.notification_divider_height);
        final ViewGroup$LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.height = resources.getDimensionPixelOffset(R$dimen.notification_shelf_height);
        this.setLayoutParams(layoutParams);
        final int dimensionPixelOffset = resources.getDimensionPixelOffset(R$dimen.shelf_icon_container_padding);
        this.mShelfIcons.setPadding(dimensionPixelOffset, 0, dimensionPixelOffset, 0);
        this.mScrollFastThreshold = resources.getDimensionPixelOffset(R$dimen.scroll_fast_threshold);
        this.mShowNotificationShelf = resources.getBoolean(R$bool.config_showNotificationShelf);
        this.mIconSize = resources.getDimensionPixelSize(17105474);
        this.mHiddenShelfIconSize = (float)resources.getDimensionPixelOffset(R$dimen.hidden_shelf_icon_size);
        this.mGapHeight = resources.getDimensionPixelSize(R$dimen.qs_notification_padding);
        if (!this.mShowNotificationShelf) {
            this.setVisibility(8);
        }
    }
    
    private boolean isTargetClipped(final ExpandableView expandableView) {
        final View shelfTransformationTarget = expandableView.getShelfTransformationTarget();
        boolean b = false;
        if (shelfTransformationTarget == null) {
            return false;
        }
        if (expandableView.getTranslationY() + expandableView.getContentTranslation() + expandableView.getRelativeTopPadding(shelfTransformationTarget) + shelfTransformationTarget.getHeight() >= this.getTranslationY() - this.mPaddingBetweenElements) {
            b = true;
        }
        return b;
    }
    
    private void setFirstElementRoundness(final float mFirstElementRoundness) {
        if (this.mFirstElementRoundness != mFirstElementRoundness) {
            this.setTopRoundness(this.mFirstElementRoundness = mFirstElementRoundness, false);
        }
    }
    
    private void setHasItemsInStableShelf(final boolean mHasItemsInStableShelf) {
        if (this.mHasItemsInStableShelf != mHasItemsInStableShelf) {
            this.mHasItemsInStableShelf = mHasItemsInStableShelf;
            this.updateInteractiveness();
        }
    }
    
    private void setHideBackground(final boolean mHideBackground) {
        if (this.mHideBackground != mHideBackground) {
            this.mHideBackground = mHideBackground;
            this.updateBackground();
            this.updateOutline();
        }
    }
    
    private void setIconTransformationAmount(final ExpandableView expandableView, final float n, float interpolate, final boolean b, final boolean b2) {
        if (!(expandableView instanceof ExpandableNotificationRow)) {
            return;
        }
        final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)expandableView;
        final StatusBarIconView shelfIcon = expandableNotificationRow.getShelfIcon();
        final NotificationIconContainer.IconState iconState = this.getIconState(shelfIcon);
        final View shelfTransformationTarget = expandableNotificationRow.getShelfTransformationTarget();
        int n2;
        int relativeStartPadding;
        if (shelfTransformationTarget != null) {
            n2 = expandableNotificationRow.getRelativeTopPadding(shelfTransformationTarget);
            relativeStartPadding = expandableNotificationRow.getRelativeStartPadding(shelfTransformationTarget);
            interpolate = (float)shelfTransformationTarget.getHeight();
        }
        else {
            n2 = this.mIconAppearTopPadding;
            interpolate = 0.0f;
            relativeStartPadding = 0;
        }
        float mHiddenShelfIconSize;
        if (this.mAmbientState.isFullyHidden()) {
            mHiddenShelfIconSize = this.mHiddenShelfIconSize;
        }
        else {
            mHiddenShelfIconSize = (float)this.mIconSize;
        }
        final float n3 = mHiddenShelfIconSize * shelfIcon.getIconScale();
        final float n4 = expandableNotificationRow.getTranslationY() + expandableNotificationRow.getContentTranslation();
        final boolean b3 = expandableNotificationRow.isInShelf() && !expandableNotificationRow.isTransformingIntoShelf();
        float interpolate2;
        if (b && !b3) {
            interpolate2 = NotificationUtils.interpolate(Math.min(this.mIconAppearTopPadding + n4 - this.getTranslationY(), 0.0f), 0.0f, n);
        }
        else {
            interpolate2 = 0.0f;
        }
        final float interpolate3 = NotificationUtils.interpolate(n4 + n2 - (this.getTranslationY() + shelfIcon.getTop() + (shelfIcon.getHeight() - n3) / 2.0f), interpolate2, n);
        float xTranslation = NotificationUtils.interpolate(relativeStartPadding - (shelfIcon.getLeft() + (1.0f - shelfIcon.getIconScale()) * shelfIcon.getWidth() / 2.0f), this.mShelfIcons.getActualPaddingStart(), n);
        final boolean b4 = expandableNotificationRow.isShowingIcon() ^ true;
        float alpha;
        if (b4) {
            interpolate = n3 / 2.0f;
            xTranslation = this.mShelfIcons.getActualPaddingStart();
            alpha = n;
        }
        else {
            alpha = 1.0f;
        }
        interpolate = NotificationUtils.interpolate(interpolate, n3, n);
        if (iconState != null) {
            interpolate /= n3;
            iconState.scaleX = interpolate;
            iconState.scaleY = interpolate;
            iconState.hidden = (n == 0.0f && !iconState.isAnimating((View)shelfIcon));
            if (expandableNotificationRow.isDrawingAppearAnimation() && !expandableNotificationRow.isInShelf()) {
                iconState.hidden = true;
                iconState.iconAppearAmount = 0.0f;
            }
            iconState.alpha = alpha;
            iconState.yTranslation = interpolate3;
            iconState.xTranslation = xTranslation;
            if (b3) {
                iconState.iconAppearAmount = 1.0f;
                iconState.alpha = 1.0f;
                iconState.scaleX = 1.0f;
                iconState.scaleY = 1.0f;
                iconState.hidden = false;
            }
            if (expandableNotificationRow.isAboveShelf() || expandableNotificationRow.showingPulsing() || (!expandableNotificationRow.isInShelf() && ((b2 && expandableNotificationRow.areGutsExposed()) || expandableNotificationRow.getTranslationZ() > this.mAmbientState.getBaseZHeight()))) {
                iconState.hidden = true;
            }
            int iconColor;
            final int n5 = iconColor = shelfIcon.getContrastedStaticDrawableColor(this.getBackgroundColorWithoutTint());
            if (!b4 && (iconColor = n5) != 0) {
                iconColor = NotificationUtils.interpolateColors(expandableNotificationRow.getOriginalIconColor(), n5, iconState.iconAppearAmount);
            }
            iconState.iconColor = iconColor;
        }
    }
    
    private void setOpenedAmount(float n) {
        this.mNoAnimationsInThisFrame = (n == 1.0f && this.mOpenedAmount == 0.0f);
        this.mOpenedAmount = n;
        if (!this.mAmbientState.isPanelFullWidth() || this.mAmbientState.isDozing()) {
            n = 1.0f;
        }
        int mRelativeOffset;
        final int n2 = mRelativeOffset = this.mRelativeOffset;
        if (this.isLayoutRtl()) {
            mRelativeOffset = this.getWidth() - n2 - this.mCollapsedIcons.getWidth();
        }
        this.mShelfIcons.setActualLayoutWidth((int)NotificationUtils.interpolate((float)(this.mCollapsedIcons.getFinalTranslationX() + mRelativeOffset), (float)this.mShelfIcons.getWidth(), Interpolators.FAST_OUT_SLOW_IN_REVERSE.getInterpolation(n)));
        final boolean hasOverflow = this.mCollapsedIcons.hasOverflow();
        final int paddingEnd = this.mCollapsedIcons.getPaddingEnd();
        int n3;
        if (!hasOverflow) {
            n3 = this.mCollapsedIcons.getNoOverflowExtraPadding();
        }
        else {
            n3 = this.mCollapsedIcons.getPartialOverflowExtraPadding();
        }
        this.mShelfIcons.setActualPaddingEnd(NotificationUtils.interpolate((float)(paddingEnd - n3), (float)this.mShelfIcons.getPaddingEnd(), n));
        this.mShelfIcons.setActualPaddingStart(NotificationUtils.interpolate((float)mRelativeOffset, (float)this.mShelfIcons.getPaddingStart(), n));
        this.mShelfIcons.setOpenedAmount(n);
    }
    
    private void updateContinuousClipping(final ExpandableNotificationRow expandableNotificationRow) {
        final StatusBarIconView shelfIcon = expandableNotificationRow.getEntry().getIcons().getShelfIcon();
        final boolean animatingY = ViewState.isAnimatingY((View)shelfIcon);
        boolean b = true;
        final boolean b2 = animatingY && !this.mAmbientState.isDozing();
        if (shelfIcon.getTag(NotificationShelf.TAG_CONTINUOUS_CLIPPING) == null) {
            b = false;
        }
        if (b2 && !b) {
            final ViewTreeObserver viewTreeObserver = shelfIcon.getViewTreeObserver();
            final ViewTreeObserver$OnPreDrawListener viewTreeObserver$OnPreDrawListener = (ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (!ViewState.isAnimatingY((View)shelfIcon)) {
                        if (viewTreeObserver.isAlive()) {
                            viewTreeObserver.removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
                        }
                        shelfIcon.setTag(NotificationShelf.TAG_CONTINUOUS_CLIPPING, (Object)null);
                        return true;
                    }
                    NotificationShelf.this.updateIconClipAmount(expandableNotificationRow);
                    return true;
                }
            };
            viewTreeObserver.addOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)viewTreeObserver$OnPreDrawListener);
            shelfIcon.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener(this) {
                public void onViewAttachedToWindow(final View view) {
                }
                
                public void onViewDetachedFromWindow(final View view) {
                    if (view == shelfIcon) {
                        if (viewTreeObserver.isAlive()) {
                            viewTreeObserver.removeOnPreDrawListener(viewTreeObserver$OnPreDrawListener);
                        }
                        shelfIcon.setTag(NotificationShelf.TAG_CONTINUOUS_CLIPPING, (Object)null);
                    }
                }
            });
            shelfIcon.setTag(NotificationShelf.TAG_CONTINUOUS_CLIPPING, (Object)viewTreeObserver$OnPreDrawListener);
        }
    }
    
    private void updateIconClipAmount(final ExpandableNotificationRow expandableNotificationRow) {
        float n;
        final float a = n = expandableNotificationRow.getTranslationY();
        if (this.getClipTopAmount() != 0) {
            n = Math.max(a, this.getTranslationY() + this.getClipTopAmount());
        }
        final StatusBarIconView shelfIcon = expandableNotificationRow.getEntry().getIcons().getShelfIcon();
        final float n2 = this.getTranslationY() + shelfIcon.getTop() + shelfIcon.getTranslationY();
        if (n2 < n && !this.mAmbientState.isFullyHidden()) {
            final int a2 = (int)(n - n2);
            shelfIcon.setClipBounds(new Rect(0, a2, shelfIcon.getWidth(), Math.max(a2, shelfIcon.getHeight())));
        }
        else {
            shelfIcon.setClipBounds((Rect)null);
        }
    }
    
    private void updateIconPositioning(final ExpandableView expandableView, float n, float iconAppearAmount, final float n2, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        final StatusBarIconView shelfIcon = expandableView.getShelfIcon();
        final NotificationIconContainer.IconState iconState = this.getIconState(shelfIcon);
        if (iconState == null) {
            return;
        }
        final boolean isLastExpandIcon = iconState.isLastExpandIcon;
        final boolean b5 = false;
        final boolean b6 = isLastExpandIcon && !iconState.hasCustomTransformHeight();
        final boolean b7 = n > 0.5f || this.isTargetClipped(expandableView);
        final float n3 = 1.0f;
        float clampedAppearAmount;
        if (b7) {
            clampedAppearAmount = 1.0f;
        }
        else {
            clampedAppearAmount = 0.0f;
        }
        if (n == clampedAppearAmount) {
            final boolean noAnimations = (b2 || b3) && !b6;
            iconState.noAnimations = noAnimations;
            iconState.useFullTransitionAmount = (noAnimations || (!NotificationShelf.ICON_ANMATIONS_WHILE_SCROLLING && n == 0.0f && b));
            iconState.useLinearTransitionAmount = (!NotificationShelf.ICON_ANMATIONS_WHILE_SCROLLING && n == 0.0f && !this.mAmbientState.isExpansionChanging());
            iconState.translateContent = (this.mMaxLayoutHeight - this.getTranslationY() - this.getIntrinsicHeight() > 0.0f);
        }
        if (!b6 && (b2 || (b3 && iconState.useFullTransitionAmount && !ViewState.isAnimatingY((View)shelfIcon)))) {
            iconState.cancelAnimations((View)shelfIcon);
            iconState.useFullTransitionAmount = true;
            iconState.noAnimations = true;
        }
        if (iconState.hasCustomTransformHeight()) {
            iconState.useFullTransitionAmount = true;
        }
        if (iconState.isLastExpandIcon) {
            iconState.translateContent = false;
        }
        if (this.mAmbientState.isHiddenAtAll() && !expandableView.isInShelf()) {
            if (this.mAmbientState.isFullyHidden()) {
                n = n3;
            }
            else {
                n = 0.0f;
            }
        }
        else if (!b4 && NotificationShelf.USE_ANIMATIONS_WHEN_OPENING && !iconState.useFullTransitionAmount) {
            if (!iconState.useLinearTransitionAmount) {
                iconState.needsCannedAnimation = (iconState.clampedAppearAmount != clampedAppearAmount && !this.mNoAnimationsInThisFrame);
                n = clampedAppearAmount;
            }
        }
        if (NotificationShelf.USE_ANIMATIONS_WHEN_OPENING) {
            if (!iconState.useFullTransitionAmount) {
                iconAppearAmount = n;
            }
        }
        iconState.iconAppearAmount = iconAppearAmount;
        iconState.clampedAppearAmount = clampedAppearAmount;
        boolean b8 = b5;
        if (clampedAppearAmount != n) {
            b8 = true;
        }
        this.setIconTransformationAmount(expandableView, n, n2, b8, b4);
    }
    
    private void updateInteractiveness() {
        final int mStatusBarState = this.mStatusBarState;
        int importantForAccessibility = 1;
        this.setClickable(this.mInteractive = (mStatusBarState == 1 && this.mHasItemsInStableShelf));
        this.setFocusable(this.mInteractive);
        if (!this.mInteractive) {
            importantForAccessibility = 4;
        }
        this.setImportantForAccessibility(importantForAccessibility);
    }
    
    private int updateNotificationClipHeight(final ExpandableView expandableView, final float n, int min) {
        final float n2 = expandableView.getTranslationY() + expandableView.getActualHeight();
        final boolean pinned = expandableView.isPinned();
        boolean showingPulsing = true;
        final boolean b = (pinned || expandableView.isHeadsUpAnimatingAway()) && !this.mAmbientState.isDozingAndNotPulsing(expandableView);
        if (this.mAmbientState.isPulseExpanding()) {
            if (min != 0) {
                showingPulsing = false;
            }
        }
        else {
            showingPulsing = expandableView.showingPulsing();
        }
        if (n2 > n && !showingPulsing && (this.mAmbientState.isShadeExpanded() || !b)) {
            final int b2 = min = (int)(n2 - n);
            if (b) {
                min = Math.min(expandableView.getIntrinsicHeight() - expandableView.getCollapsedHeight(), b2);
            }
            expandableView.setClipBottomAmount(min);
        }
        else {
            expandableView.setClipBottomAmount(0);
        }
        if (showingPulsing) {
            return (int)(n2 - this.getTranslationY());
        }
        return 0;
    }
    
    private void updateRelativeOffset() {
        this.mCollapsedIcons.getLocationOnScreen(this.mTmp);
        final int[] mTmp = this.mTmp;
        this.mRelativeOffset = mTmp[0];
        this.getLocationOnScreen(mTmp);
        this.mRelativeOffset -= this.mTmp[0];
    }
    
    private float updateShelfTransformation(final ExpandableView expandableView, float n, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        final NotificationIconContainer.IconState iconState = this.getIconState(expandableView.getShelfIcon());
        final float translationY = expandableView.getTranslationY();
        final int a = expandableView.getActualHeight() + this.mPaddingBetweenElements;
        final float calculateIconTransformationStart = this.calculateIconTransformationStart(expandableView);
        final float n2 = (float)this.getIntrinsicHeight();
        final float interpolate = NotificationUtils.interpolate(1.0f, 1.5f, n);
        final float b5 = (float)a;
        final float min = Math.min(b5 + translationY - calculateIconTransformationStart, Math.min(n2 * 1.5f * interpolate, b5));
        int min2 = a;
        float min3 = min;
        if (b4) {
            min2 = Math.min(a, expandableView.getMinHeight() - this.getIntrinsicHeight());
            min3 = Math.min(min, (float)(expandableView.getMinHeight() - this.getIntrinsicHeight()));
        }
        final float n3 = (float)min2;
        this.handleCustomTransformHeight(expandableView, b3, iconState);
        final float translationY2 = this.getTranslationY();
        final int n4 = 1;
        final int n5 = 1;
        final float n6 = 0.0f;
        int n10;
        float n14;
        float n15;
        float n16;
        if (n3 + translationY >= translationY2 && (!this.mAmbientState.isUnlockHintRunning() || expandableView.isInShelf()) && (this.mAmbientState.isShadeExpanded() || (!expandableView.isPinned() && !expandableView.isHeadsUpAnimatingAway()))) {
            float n7;
            float n9;
            if (translationY < translationY2) {
                int customTransformHeight = min2;
                n7 = min3;
                if (iconState != null) {
                    customTransformHeight = min2;
                    n7 = min3;
                    if (iconState.hasCustomTransformHeight()) {
                        customTransformHeight = iconState.customTransformHeight;
                        n7 = (float)customTransformHeight;
                    }
                }
                final float n8 = translationY2 - translationY;
                final float min4 = Math.min(1.0f, n8 / customTransformHeight);
                n9 = 1.0f - NotificationUtils.interpolate(Interpolators.ACCELERATE_DECELERATE.getInterpolation(min4), min4, n);
                if (b4) {
                    n = n8 / n7;
                }
                else {
                    n = (translationY2 - calculateIconTransformationStart) / n7;
                }
                n = 1.0f - MathUtils.constrain(n, 0.0f, 1.0f);
                n10 = 0;
            }
            else {
                final float n11 = n = 1.0f;
                n10 = n5;
                n7 = min3;
                n9 = n11;
            }
            final float n12 = 1.0f - Math.min(1.0f, (translationY2 - translationY) / n7);
            final float n13 = n9;
            n14 = n;
            n15 = n7;
            n16 = n13;
            n = n12;
        }
        else {
            n15 = min3;
            final float n17 = 0.0f;
            final float n18 = n = n17;
            n10 = n4;
            n16 = n18;
            n14 = n17;
        }
        if (iconState != null && n10 != 0 && !b3 && iconState.isLastExpandIcon) {
            iconState.isLastExpandIcon = false;
            iconState.customTransformHeight = Integer.MIN_VALUE;
        }
        float n19 = n6;
        if (!expandableView.isAboveShelf()) {
            n19 = n6;
            if (!expandableView.showingPulsing()) {
                if (!b4 && iconState != null && !iconState.translateContent) {
                    n19 = n6;
                }
                else {
                    n19 = n;
                }
            }
        }
        expandableView.setContentTransformationAmount(n19, b4);
        this.updateIconPositioning(expandableView, n14, n16, n15, b, b2, b3, b4);
        return n16;
    }
    
    public void bind(final AmbientState mAmbientState, final NotificationStackScrollLayout mHostLayout) {
        this.mAmbientState = mAmbientState;
        this.mHostLayout = mHostLayout;
    }
    
    public ExpandableViewState createExpandableViewState() {
        return new ShelfState();
    }
    
    @Override
    protected View getContentView() {
        return (View)this.mShelfIcons;
    }
    
    public int getNotGoneIndex() {
        return this.mNotGoneIndex;
    }
    
    public NotificationIconContainer getShelfIcons() {
        return this.mShelfIcons;
    }
    
    public boolean hasNoContentHeight() {
        return true;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    @Override
    protected boolean isInteractive() {
        return this.mInteractive;
    }
    
    public boolean needsClippingToShelf() {
        return false;
    }
    
    protected boolean needsOutline() {
        return !this.mHideBackground && super.needsOutline();
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        final WindowInsets onApplyWindowInsets = super.onApplyWindowInsets(windowInsets);
        final DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        int safeInsetTop;
        if (displayCutout != null && displayCutout.getSafeInsetTop() >= 0) {
            safeInsetTop = displayCutout.getSafeInsetTop();
        }
        else {
            safeInsetTop = 0;
        }
        this.mCutoutHeight = safeInsetTop;
        return onApplyWindowInsets;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Dependency.get((Class<SysuiStatusBarStateController>)StatusBarStateController.class).addCallback(this, 3);
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.initDimens();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(StatusBarStateController.class).removeCallback((StatusBarStateController.StateListener)this);
    }
    
    @VisibleForTesting
    public void onFinishInflate() {
        super.onFinishInflate();
        (this.mShelfIcons = (NotificationIconContainer)this.findViewById(R$id.content)).setClipChildren(false);
        this.mShelfIcons.setClipToPadding(false);
        this.setClipToActualHeight(false);
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.mShelfIcons.setIsStaticLayout(false);
        this.setBottomRoundness(1.0f, false);
        this.setFirstInSection(true);
        this.initDimens();
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.mInteractive) {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_EXPAND);
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(16, (CharSequence)this.getContext().getString(R$string.accessibility_overflow_action)));
        }
    }
    
    @Override
    protected void onLayout(final boolean b, int heightPixels, final int n, final int n2, final int n3) {
        super.onLayout(b, heightPixels, n, n2, n3);
        this.updateRelativeOffset();
        heightPixels = this.getResources().getDisplayMetrics().heightPixels;
        this.mClipRect.set(0, -heightPixels, this.getWidth(), heightPixels);
        this.mShelfIcons.setClipBounds(this.mClipRect);
    }
    
    public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        this.updateRelativeOffset();
    }
    
    public void onStateChanged(final int mStatusBarState) {
        this.mStatusBarState = mStatusBarState;
        this.updateInteractiveness();
    }
    
    public void onUiModeChanged() {
        this.updateBackgroundColors();
    }
    
    public void setAnimationsEnabled(final boolean mAnimationsEnabled) {
        if (!(this.mAnimationsEnabled = mAnimationsEnabled)) {
            this.mShelfIcons.setAnimationsEnabled(false);
        }
    }
    
    public void setCollapsedIcons(final NotificationIconContainer mCollapsedIcons) {
        (this.mCollapsedIcons = mCollapsedIcons).addOnLayoutChangeListener((View$OnLayoutChangeListener)this);
    }
    
    @Override
    public void setFakeShadowIntensity(float n, final float n2, final int n3, final int n4) {
        if (!this.mHasItemsInStableShelf) {
            n = 0.0f;
        }
        super.setFakeShadowIntensity(n, n2, n3, n4);
    }
    
    public void setMaxLayoutHeight(final int mMaxLayoutHeight) {
        this.mMaxLayoutHeight = mMaxLayoutHeight;
    }
    
    public void setMaxShelfEnd(final float n) {
    }
    
    @Override
    protected boolean shouldHideBackground() {
        return super.shouldHideBackground() || this.mHideBackground;
    }
    
    public void updateAppearance() {
        if (!this.mShowNotificationShelf) {
            return;
        }
        this.mShelfIcons.resetViewStates();
        final float translationY = this.getTranslationY();
        final ActivatableNotificationView lastVisibleBackgroundChild = this.mAmbientState.getLastVisibleBackgroundChild();
        this.mNotGoneIndex = -1;
        final float n = (float)(this.mMaxLayoutHeight - this.getIntrinsicHeight() * 2);
        final float n2 = 0.0f;
        float min;
        if (translationY >= n) {
            min = Math.min(1.0f, (translationY - n) / this.getIntrinsicHeight());
        }
        else {
            min = 0.0f;
        }
        final boolean b = this.mHideBackground && !((ShelfState)this.getViewState()).hasItemsInStableShelf;
        final float currentScrollVelocity = this.mAmbientState.getCurrentScrollVelocity();
        final boolean b2 = currentScrollVelocity > this.mScrollFastThreshold || (this.mAmbientState.isExpansionChanging() && Math.abs(this.mAmbientState.getExpandingVelocity()) > this.mScrollFastThreshold);
        final boolean b3 = currentScrollVelocity > 0.0f;
        final boolean b4 = this.mAmbientState.isExpansionChanging() && !this.mAmbientState.isPanelTracking();
        final int baseZHeight = this.mAmbientState.getBaseZHeight();
        ActivatableNotificationView activatableNotificationView = null;
        float n3;
        float firstElementRoundness = n3 = 0.0f;
        int n4 = 0;
        int n5 = 0;
        int backgroundTop = 0;
        int i = 0;
        int n6 = 0;
        int tintColor = 0;
        int n7 = 0;
        final float n8 = min;
        float n9 = n2;
        while (i < this.mHostLayout.getChildCount()) {
            final ExpandableView expandableView = (ExpandableView)this.mHostLayout.getChildAt(i);
            float currentTopRoundness = 0.0f;
            float n14 = 0.0f;
            Label_0848: {
                if (expandableView.needsClippingToShelf()) {
                    final float n10 = firstElementRoundness;
                    if (expandableView.getVisibility() != 8) {
                        final boolean b5 = ViewState.getFinalTranslationZ((View)expandableView) > baseZHeight || expandableView.isPinned();
                        final boolean b6 = expandableView == lastVisibleBackgroundChild;
                        final float translationY2 = expandableView.getTranslationY();
                        float n11;
                        if ((!b6 || expandableView.isInShelf()) && !b5 && !b) {
                            n11 = translationY - this.mPaddingBetweenElements;
                        }
                        else {
                            n11 = this.getIntrinsicHeight() + translationY;
                        }
                        final int max = Math.max(this.updateNotificationClipHeight(expandableView, n11, n5), n4);
                        final float updateShelfTransformation = this.updateShelfTransformation(expandableView, n8, b3, b2, b4, b6);
                        float n12;
                        if (expandableView instanceof ExpandableNotificationRow) {
                            final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)expandableView;
                            n3 += updateShelfTransformation;
                            final int backgroundColorWithoutTint = expandableNotificationRow.getBackgroundColorWithoutTint();
                            Label_0530: {
                                if (translationY2 >= translationY && this.mNotGoneIndex == -1) {
                                    this.mNotGoneIndex = n5;
                                    this.setTintColor(tintColor);
                                    this.setOverrideTintColor(n6, n9);
                                }
                                else if (this.mNotGoneIndex == -1) {
                                    n12 = updateShelfTransformation;
                                    break Label_0530;
                                }
                                tintColor = n6;
                                n12 = n9;
                            }
                            final boolean b7 = this.mAmbientState.isShadeExpanded() && (!this.mAmbientState.isOnKeyguard() || !this.mBypassController.getBypassEnabled());
                            if (b6 && b7) {
                                if (n7 == 0) {
                                    n7 = backgroundColorWithoutTint;
                                }
                                expandableNotificationRow.setOverrideTintColor(n7, updateShelfTransformation);
                            }
                            else {
                                expandableNotificationRow.setOverrideTintColor(0, 0.0f);
                                n7 = backgroundColorWithoutTint;
                            }
                            if (n5 != 0 || !b5) {
                                expandableNotificationRow.setAboveShelf(false);
                            }
                            Label_0692: {
                                if (n5 == 0) {
                                    final NotificationIconContainer.IconState iconState = this.getIconState(expandableNotificationRow.getEntry().getIcons().getShelfIcon());
                                    if (iconState != null && iconState.clampedAppearAmount == 1.0f) {
                                        backgroundTop = (int)(expandableView.getTranslationY() - this.getTranslationY());
                                        currentTopRoundness = expandableNotificationRow.getCurrentTopRoundness();
                                        break Label_0692;
                                    }
                                }
                                currentTopRoundness = n10;
                            }
                            n6 = tintColor;
                            tintColor = backgroundColorWithoutTint;
                            ++n5;
                        }
                        else {
                            n12 = n9;
                            currentTopRoundness = n10;
                        }
                        if (expandableView instanceof ActivatableNotificationView) {
                            final ExpandableNotificationRow expandableNotificationRow2 = (ExpandableNotificationRow)expandableView;
                            if (expandableNotificationRow2.isFirstInSection() && activatableNotificationView != null && activatableNotificationView.isLastInSection()) {
                                final float translationY3 = expandableView.getTranslationY();
                                final float translationY4 = this.getTranslationY();
                                final float n13 = this.getTranslationY() - (activatableNotificationView.getTranslationY() + activatableNotificationView.getActualHeight());
                                if (n13 > 0.0f) {
                                    currentTopRoundness = (float)Math.min(1.0, n13 / this.mGapHeight);
                                    activatableNotificationView.setBottomRoundness(currentTopRoundness, false);
                                    backgroundTop = (int)(translationY3 - translationY4);
                                }
                            }
                            activatableNotificationView = expandableNotificationRow2;
                        }
                        n4 = max;
                        n14 = n12;
                        break Label_0848;
                    }
                }
                n14 = n9;
                currentTopRoundness = firstElementRoundness;
            }
            ++i;
            firstElementRoundness = currentTopRoundness;
            n9 = n14;
        }
        this.clipTransientViews();
        this.setClipTopAmount(n4);
        final boolean b8 = this.getViewState().hidden || n4 >= this.getIntrinsicHeight();
        if (this.mShowNotificationShelf) {
            int visibility;
            if (b8) {
                visibility = 4;
            }
            else {
                visibility = 0;
            }
            this.setVisibility(visibility);
        }
        this.setBackgroundTop(backgroundTop);
        this.setFirstElementRoundness(firstElementRoundness);
        this.mShelfIcons.setSpeedBumpIndex(this.mAmbientState.getSpeedBumpIndex());
        this.mShelfIcons.calculateIconTranslations();
        this.mShelfIcons.applyIconStates();
        for (int j = 0; j < this.mHostLayout.getChildCount(); ++j) {
            final View child = this.mHostLayout.getChildAt(j);
            if (child instanceof ExpandableNotificationRow) {
                if (child.getVisibility() != 8) {
                    final ExpandableNotificationRow expandableNotificationRow3 = (ExpandableNotificationRow)child;
                    this.updateIconClipAmount(expandableNotificationRow3);
                    this.updateContinuousClipping(expandableNotificationRow3);
                }
            }
        }
        this.setHideBackground(n3 < 1.0f || b);
        if (this.mNotGoneIndex == -1) {
            this.mNotGoneIndex = n5;
        }
    }
    
    public void updateState(final AmbientState ambientState) {
        final ActivatableNotificationView lastVisibleBackgroundChild = ambientState.getLastVisibleBackgroundChild();
        final ShelfState shelfState = (ShelfState)this.getViewState();
        final boolean mShowNotificationShelf = this.mShowNotificationShelf;
        final boolean b = true;
        if (mShowNotificationShelf && lastVisibleBackgroundChild != null) {
            final float b2 = ambientState.getInnerHeight() + ambientState.getTopPadding() + ambientState.getStackTranslation();
            final ExpandableViewState viewState = lastVisibleBackgroundChild.getViewState();
            final float yTranslation = viewState.yTranslation;
            final float n = (float)viewState.height;
            shelfState.copyFrom(viewState);
            shelfState.height = this.getIntrinsicHeight();
            shelfState.yTranslation = Math.max(Math.min(yTranslation + n, b2) - shelfState.height, this.getFullyClosedTranslation());
            shelfState.zTranslation = (float)ambientState.getBaseZHeight();
            shelfState.openedAmount = Math.min(1.0f, (shelfState.yTranslation - this.getFullyClosedTranslation()) / (this.getIntrinsicHeight() * 2 + this.mCutoutHeight));
            shelfState.clipTopAmount = 0;
            shelfState.alpha = 1.0f;
            shelfState.belowSpeedBump = (this.mAmbientState.getSpeedBumpIndex() == 0);
            shelfState.hideSensitive = false;
            shelfState.xTranslation = this.getTranslationX();
            final int mNotGoneIndex = this.mNotGoneIndex;
            if (mNotGoneIndex != -1) {
                shelfState.notGoneIndex = Math.min(shelfState.notGoneIndex, mNotGoneIndex);
            }
            shelfState.hasItemsInStableShelf = viewState.inShelf;
            boolean hidden = b;
            if (this.mAmbientState.isShadeExpanded()) {
                hidden = (this.mAmbientState.isQsCustomizerShowing() && b);
            }
            shelfState.hidden = hidden;
            shelfState.maxShelfEnd = b2;
        }
        else {
            shelfState.hidden = true;
            shelfState.location = 64;
            shelfState.hasItemsInStableShelf = false;
        }
    }
    
    private class ShelfState extends ExpandableViewState
    {
        private boolean hasItemsInStableShelf;
        private float maxShelfEnd;
        private float openedAmount;
        
        @Override
        public void animateTo(final View view, final AnimationProperties animationProperties) {
            if (!NotificationShelf.this.mShowNotificationShelf) {
                return;
            }
            super.animateTo(view, animationProperties);
            NotificationShelf.this.setMaxShelfEnd(this.maxShelfEnd);
            NotificationShelf.this.setOpenedAmount(this.openedAmount);
            NotificationShelf.this.updateAppearance();
            NotificationShelf.this.setHasItemsInStableShelf(this.hasItemsInStableShelf);
            NotificationShelf.this.mShelfIcons.setAnimationsEnabled(NotificationShelf.this.mAnimationsEnabled);
        }
        
        @Override
        public void applyToView(final View view) {
            if (!NotificationShelf.this.mShowNotificationShelf) {
                return;
            }
            super.applyToView(view);
            NotificationShelf.this.setMaxShelfEnd(this.maxShelfEnd);
            NotificationShelf.this.setOpenedAmount(this.openedAmount);
            NotificationShelf.this.updateAppearance();
            NotificationShelf.this.setHasItemsInStableShelf(this.hasItemsInStableShelf);
            NotificationShelf.this.mShelfIcons.setAnimationsEnabled(NotificationShelf.this.mAnimationsEnabled);
        }
    }
}
