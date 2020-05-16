// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.Rect;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.internal.widget.ViewClippingUtil;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.CrossFadeHelper;
import android.view.DisplayCutout;
import android.view.WindowInsets;
import android.view.Display;
import com.android.systemui.Dependency;
import com.android.systemui.R$id;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import android.view.View$OnLayoutChangeListener;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import android.graphics.Point;
import com.android.internal.widget.ViewClippingUtil$ClippingParameters;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.HeadsUpStatusBarView;
import com.android.systemui.statusbar.CommandQueue;
import android.view.View;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;

public class HeadsUpAppearanceController implements OnHeadsUpChangedListener, DarkReceiver, WakeUpListener
{
    private boolean mAnimationsEnabled;
    @VisibleForTesting
    float mAppearFraction;
    private final KeyguardBypassController mBypassController;
    private final View mCenteredIconView;
    private final View mClockView;
    private final CommandQueue mCommandQueue;
    private final DarkIconDispatcher mDarkIconDispatcher;
    @VisibleForTesting
    float mExpandedHeight;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private final HeadsUpStatusBarView mHeadsUpStatusBarView;
    @VisibleForTesting
    boolean mIsExpanded;
    private KeyguardStateController mKeyguardStateController;
    private final NotificationIconAreaController mNotificationIconAreaController;
    private final NotificationPanelViewController mNotificationPanelViewController;
    private final View mOperatorNameView;
    private final ViewClippingUtil$ClippingParameters mParentClippingParams;
    Point mPoint;
    private final BiConsumer<Float, Float> mSetExpandedHeight;
    private final Consumer<ExpandableNotificationRow> mSetTrackingHeadsUp;
    private boolean mShown;
    private final View$OnLayoutChangeListener mStackScrollLayoutChangeListener;
    private final NotificationStackScrollLayout mStackScroller;
    private final StatusBarStateController mStatusBarStateController;
    private ExpandableNotificationRow mTrackedChild;
    private final Runnable mUpdatePanelTranslation;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;
    
    public HeadsUpAppearanceController(final NotificationIconAreaController notificationIconAreaController, final HeadsUpManagerPhone headsUpManagerPhone, final View view, final SysuiStatusBarStateController sysuiStatusBarStateController, final KeyguardBypassController keyguardBypassController, final KeyguardStateController keyguardStateController, final NotificationWakeUpCoordinator notificationWakeUpCoordinator, final CommandQueue commandQueue, final NotificationPanelViewController notificationPanelViewController, final View view2) {
        this(notificationIconAreaController, headsUpManagerPhone, sysuiStatusBarStateController, keyguardBypassController, notificationWakeUpCoordinator, keyguardStateController, commandQueue, (HeadsUpStatusBarView)view2.findViewById(R$id.heads_up_status_bar_view), (NotificationStackScrollLayout)view.findViewById(R$id.notification_stack_scroller), notificationPanelViewController, view2.findViewById(R$id.clock), view2.findViewById(R$id.operator_name_frame), view2.findViewById(R$id.centered_icon_area));
    }
    
    @VisibleForTesting
    public HeadsUpAppearanceController(final NotificationIconAreaController mNotificationIconAreaController, final HeadsUpManagerPhone mHeadsUpManager, final StatusBarStateController mStatusBarStateController, final KeyguardBypassController mBypassController, final NotificationWakeUpCoordinator mWakeUpCoordinator, final KeyguardStateController mKeyguardStateController, final CommandQueue mCommandQueue, final HeadsUpStatusBarView mHeadsUpStatusBarView, final NotificationStackScrollLayout mStackScroller, final NotificationPanelViewController mNotificationPanelViewController, final View mClockView, final View mOperatorNameView, final View mCenteredIconView) {
        this.mSetTrackingHeadsUp = (Consumer<ExpandableNotificationRow>)new _$$Lambda$u27UVgFXO2Fq_gY8QI0m_qAQyl8(this);
        this.mUpdatePanelTranslation = new _$$Lambda$22QZFjoGlQJQoKOrFe_bHbZltB4(this);
        this.mSetExpandedHeight = (BiConsumer<Float, Float>)new _$$Lambda$bcWIlLYHGuPtIh99P0bExeXSsMQ(this);
        this.mStackScrollLayoutChangeListener = (View$OnLayoutChangeListener)new _$$Lambda$HeadsUpAppearanceController$hwNOwOgXItDjQM7QwL00pigpnrk(this);
        this.mParentClippingParams = (ViewClippingUtil$ClippingParameters)new ViewClippingUtil$ClippingParameters() {
            public boolean shouldFinish(final View view) {
                return view.getId() == R$id.status_bar;
            }
        };
        this.mAnimationsEnabled = true;
        this.mNotificationIconAreaController = mNotificationIconAreaController;
        (this.mHeadsUpManager = mHeadsUpManager).addListener(this);
        this.mHeadsUpStatusBarView = mHeadsUpStatusBarView;
        this.mCenteredIconView = mCenteredIconView;
        mHeadsUpStatusBarView.setOnDrawingRectChangedListener(new _$$Lambda$HeadsUpAppearanceController$1d3l5klDiH8maZOdHwrJBKgigPE(this));
        this.mStackScroller = mStackScroller;
        (this.mNotificationPanelViewController = mNotificationPanelViewController).addTrackingHeadsUpListener(this.mSetTrackingHeadsUp);
        mNotificationPanelViewController.addVerticalTranslationListener(this.mUpdatePanelTranslation);
        mNotificationPanelViewController.setHeadsUpAppearanceController(this);
        this.mStackScroller.addOnExpandedHeightChangedListener(this.mSetExpandedHeight);
        this.mStackScroller.addOnLayoutChangeListener(this.mStackScrollLayoutChangeListener);
        this.mStackScroller.setHeadsUpAppearanceController(this);
        this.mClockView = mClockView;
        this.mOperatorNameView = mOperatorNameView;
        (this.mDarkIconDispatcher = Dependency.get(DarkIconDispatcher.class)).addDarkReceiver((DarkIconDispatcher.DarkReceiver)this);
        this.mHeadsUpStatusBarView.addOnLayoutChangeListener((View$OnLayoutChangeListener)new View$OnLayoutChangeListener() {
            public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
                if (HeadsUpAppearanceController.this.shouldBeVisible()) {
                    HeadsUpAppearanceController.this.updateTopEntry();
                    HeadsUpAppearanceController.this.mStackScroller.requestLayout();
                }
                HeadsUpAppearanceController.this.mHeadsUpStatusBarView.removeOnLayoutChangeListener((View$OnLayoutChangeListener)this);
            }
        });
        this.mBypassController = mBypassController;
        this.mStatusBarStateController = mStatusBarStateController;
        (this.mWakeUpCoordinator = mWakeUpCoordinator).addListener((NotificationWakeUpCoordinator.WakeUpListener)this);
        this.mCommandQueue = mCommandQueue;
        this.mKeyguardStateController = mKeyguardStateController;
    }
    
    private int getRtlTranslation() {
        if (this.mPoint == null) {
            this.mPoint = new Point();
        }
        final Display display = this.mStackScroller.getDisplay();
        int safeInsetRight = 0;
        int x;
        if (display != null) {
            this.mStackScroller.getDisplay().getRealSize(this.mPoint);
            x = this.mPoint.x;
        }
        else {
            x = 0;
        }
        final WindowInsets rootWindowInsets = this.mStackScroller.getRootWindowInsets();
        DisplayCutout displayCutout;
        if (rootWindowInsets != null) {
            displayCutout = rootWindowInsets.getDisplayCutout();
        }
        else {
            displayCutout = null;
        }
        int stableInsetLeft;
        if (rootWindowInsets != null) {
            stableInsetLeft = rootWindowInsets.getStableInsetLeft();
        }
        else {
            stableInsetLeft = 0;
        }
        int stableInsetRight;
        if (rootWindowInsets != null) {
            stableInsetRight = rootWindowInsets.getStableInsetRight();
        }
        else {
            stableInsetRight = 0;
        }
        int safeInsetLeft;
        if (displayCutout != null) {
            safeInsetLeft = displayCutout.getSafeInsetLeft();
        }
        else {
            safeInsetLeft = 0;
        }
        if (displayCutout != null) {
            safeInsetRight = displayCutout.getSafeInsetRight();
        }
        return Math.max(stableInsetLeft, safeInsetLeft) + this.mStackScroller.getRight() + Math.max(stableInsetRight, safeInsetRight) - x;
    }
    
    private void hide(final View view, final int n) {
        this.hide(view, n, null);
    }
    
    private void hide(final View view, final int visibility, final Runnable runnable) {
        if (this.mAnimationsEnabled) {
            CrossFadeHelper.fadeOut(view, 110L, 0, new _$$Lambda$HeadsUpAppearanceController$6jWM7O8t5p3KhJ2lcC8glbZxW9w(view, visibility, runnable));
        }
        else {
            view.setVisibility(visibility);
            if (runnable != null) {
                runnable.run();
            }
        }
    }
    
    private void setShown(final boolean mShown) {
        if (this.mShown != mShown) {
            this.mShown = mShown;
            if (mShown) {
                this.updateParentClipping(false);
                this.mHeadsUpStatusBarView.setVisibility(0);
                this.show((View)this.mHeadsUpStatusBarView);
                this.hide(this.mClockView, 4);
                if (this.mCenteredIconView.getVisibility() != 8) {
                    this.hide(this.mCenteredIconView, 4);
                }
                final View mOperatorNameView = this.mOperatorNameView;
                if (mOperatorNameView != null) {
                    this.hide(mOperatorNameView, 4);
                }
            }
            else {
                this.show(this.mClockView);
                if (this.mCenteredIconView.getVisibility() != 8) {
                    this.show(this.mCenteredIconView);
                }
                final View mOperatorNameView2 = this.mOperatorNameView;
                if (mOperatorNameView2 != null) {
                    this.show(mOperatorNameView2);
                }
                this.hide((View)this.mHeadsUpStatusBarView, 8, new _$$Lambda$HeadsUpAppearanceController$iMPD_c_MpkAUOLIdQAujzNCdyYQ(this));
            }
            if (this.mStatusBarStateController.getState() != 0) {
                this.mCommandQueue.recomputeDisableFlags(this.mHeadsUpStatusBarView.getContext().getDisplayId(), false);
            }
        }
    }
    
    private void show(final View view) {
        if (this.mAnimationsEnabled) {
            CrossFadeHelper.fadeIn(view, 110L, 100);
        }
        else {
            view.setVisibility(0);
        }
    }
    
    private void updateHeadsUpHeaders() {
        this.mHeadsUpManager.getAllEntries().forEach(new _$$Lambda$HeadsUpAppearanceController$r_oAtsVltL_EqS4w4SiU08R_o1A(this));
    }
    
    private void updateIsolatedIconLocation(final boolean b) {
        this.mNotificationIconAreaController.setIsolatedIconLocation(this.mHeadsUpStatusBarView.getIconDrawingRect(), b);
    }
    
    private void updateParentClipping(final boolean b) {
        ViewClippingUtil.setClippingDeactivated((View)this.mHeadsUpStatusBarView, b ^ true, this.mParentClippingParams);
    }
    
    private void updateTopEntry() {
        final boolean shouldBeVisible = this.shouldBeVisible();
        final StatusBarIconView statusBarIconView = null;
        NotificationEntry topEntry;
        if (shouldBeVisible) {
            topEntry = this.mHeadsUpManager.getTopEntry();
        }
        else {
            topEntry = null;
        }
        final NotificationEntry showingEntry = this.mHeadsUpStatusBarView.getShowingEntry();
        this.mHeadsUpStatusBarView.setEntry(topEntry);
        if (topEntry != showingEntry) {
            boolean b2 = false;
            Label_0088: {
                boolean b;
                if (topEntry == null) {
                    this.setShown(false);
                    b = this.mIsExpanded;
                }
                else {
                    if (showingEntry != null) {
                        b2 = false;
                        break Label_0088;
                    }
                    this.setShown(true);
                    b = this.mIsExpanded;
                }
                b2 = (b ^ true);
            }
            this.updateIsolatedIconLocation(false);
            final NotificationIconAreaController mNotificationIconAreaController = this.mNotificationIconAreaController;
            StatusBarIconView statusBarIcon;
            if (topEntry == null) {
                statusBarIcon = statusBarIconView;
            }
            else {
                statusBarIcon = topEntry.getIcons().getStatusBarIcon();
            }
            mNotificationIconAreaController.showIconIsolated(statusBarIcon, b2);
        }
    }
    
    public void destroy() {
        this.mHeadsUpManager.removeListener(this);
        this.mHeadsUpStatusBarView.setOnDrawingRectChangedListener(null);
        this.mWakeUpCoordinator.removeListener((NotificationWakeUpCoordinator.WakeUpListener)this);
        this.mNotificationPanelViewController.removeTrackingHeadsUpListener(this.mSetTrackingHeadsUp);
        this.mNotificationPanelViewController.removeVerticalTranslationListener(this.mUpdatePanelTranslation);
        this.mNotificationPanelViewController.setHeadsUpAppearanceController(null);
        this.mStackScroller.removeOnExpandedHeightChangedListener(this.mSetExpandedHeight);
        this.mStackScroller.removeOnLayoutChangeListener(this.mStackScrollLayoutChangeListener);
        this.mDarkIconDispatcher.removeDarkReceiver((DarkIconDispatcher.DarkReceiver)this);
    }
    
    @VisibleForTesting
    public boolean isShown() {
        return this.mShown;
    }
    
    @Override
    public void onDarkChanged(final Rect rect, final float n, final int n2) {
        this.mHeadsUpStatusBarView.onDarkChanged(rect, n, n2);
    }
    
    @Override
    public void onFullyHiddenChanged(final boolean b) {
        this.updateTopEntry();
    }
    
    @Override
    public void onHeadsUpPinned(final NotificationEntry notificationEntry) {
        this.updateTopEntry();
        this.updateHeader(notificationEntry);
    }
    
    @Override
    public void onHeadsUpUnPinned(final NotificationEntry notificationEntry) {
        this.updateTopEntry();
        this.updateHeader(notificationEntry);
    }
    
    public void onStateChanged() {
        this.updateTopEntry();
    }
    
    void readFrom(final HeadsUpAppearanceController headsUpAppearanceController) {
        if (headsUpAppearanceController != null) {
            this.mTrackedChild = headsUpAppearanceController.mTrackedChild;
            this.mExpandedHeight = headsUpAppearanceController.mExpandedHeight;
            this.mIsExpanded = headsUpAppearanceController.mIsExpanded;
            this.mAppearFraction = headsUpAppearanceController.mAppearFraction;
        }
    }
    
    @VisibleForTesting
    void setAnimationsEnabled(final boolean mAnimationsEnabled) {
        this.mAnimationsEnabled = mAnimationsEnabled;
    }
    
    public void setAppearFraction(final float mExpandedHeight, final float mAppearFraction) {
        final float mExpandedHeight2 = this.mExpandedHeight;
        boolean mIsExpanded = true;
        final boolean b = mExpandedHeight != mExpandedHeight2;
        this.mExpandedHeight = mExpandedHeight;
        this.mAppearFraction = mAppearFraction;
        if (mExpandedHeight <= 0.0f) {
            mIsExpanded = false;
        }
        if (b) {
            this.updateHeadsUpHeaders();
        }
        if (mIsExpanded != this.mIsExpanded) {
            this.mIsExpanded = mIsExpanded;
            this.updateTopEntry();
        }
    }
    
    public void setTrackingHeadsUp(final ExpandableNotificationRow mTrackedChild) {
        final ExpandableNotificationRow mTrackedChild2 = this.mTrackedChild;
        this.mTrackedChild = mTrackedChild;
        if (mTrackedChild2 != null) {
            this.updateHeader(mTrackedChild2.getEntry());
        }
    }
    
    public boolean shouldBeVisible() {
        final boolean notificationsFullyHidden = this.mWakeUpCoordinator.getNotificationsFullyHidden();
        boolean b = true;
        final boolean b2 = notificationsFullyHidden ^ true;
        boolean b4;
        final boolean b3 = b4 = (!this.mIsExpanded && b2);
        Label_0088: {
            if (this.mBypassController.getBypassEnabled()) {
                if (this.mStatusBarStateController.getState() != 1) {
                    b4 = b3;
                    if (!this.mKeyguardStateController.isKeyguardGoingAway()) {
                        break Label_0088;
                    }
                }
                b4 = b3;
                if (b2) {
                    b4 = true;
                }
            }
        }
        if (!b4 || !this.mHeadsUpManager.hasPinnedHeadsUp()) {
            b = false;
        }
        return b;
    }
    
    public void updateHeader(final NotificationEntry notificationEntry) {
        final ExpandableNotificationRow row = notificationEntry.getRow();
        float mAppearFraction;
        if (!row.isPinned() && !row.isHeadsUpAnimatingAway() && row != this.mTrackedChild && !row.showingPulsing()) {
            mAppearFraction = 1.0f;
        }
        else {
            mAppearFraction = this.mAppearFraction;
        }
        row.setHeaderVisibleAmount(mAppearFraction);
    }
    
    public void updatePanelTranslation() {
        int n;
        if (this.mStackScroller.isLayoutRtl()) {
            n = this.getRtlTranslation();
        }
        else {
            n = this.mStackScroller.getLeft();
        }
        this.mHeadsUpStatusBarView.setPanelTranslation(n + this.mStackScroller.getTranslationX());
    }
}
