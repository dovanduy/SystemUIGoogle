// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.R$id;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.view.View$OnLayoutChangeListener;
import android.view.DisplayCutout;
import android.view.WindowInsets;
import com.android.systemui.ScreenDecorations;
import android.graphics.Rect;
import android.content.res.Resources;
import android.util.Log;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import android.view.ViewTreeObserver$InternalInsetsInfo;
import com.android.systemui.statusbar.policy.ConfigurationController;
import android.graphics.Region;
import android.view.ViewTreeObserver$OnComputeInternalInsetsListener;
import android.view.View;
import android.content.Context;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.Dumpable;

public final class StatusBarTouchableRegionManager implements Dumpable
{
    private final BubbleController mBubbleController;
    private final Context mContext;
    private int mDisplayCutoutTouchableRegionSize;
    private boolean mForceCollapsedUntilLayout;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private boolean mIsStatusBarExpanded;
    private View mNotificationPanelView;
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    private View mNotificationShadeWindowView;
    private final ViewTreeObserver$OnComputeInternalInsetsListener mOnComputeInternalInsetsListener;
    private boolean mShouldAdjustInsets;
    private StatusBar mStatusBar;
    private int mStatusBarHeight;
    private Region mTouchableRegion;
    
    public StatusBarTouchableRegionManager(final Context mContext, final NotificationShadeWindowController mNotificationShadeWindowController, final ConfigurationController configurationController, final HeadsUpManagerPhone mHeadsUpManager, final BubbleController mBubbleController) {
        this.mIsStatusBarExpanded = false;
        this.mShouldAdjustInsets = false;
        this.mForceCollapsedUntilLayout = false;
        this.mTouchableRegion = new Region();
        this.mOnComputeInternalInsetsListener = (ViewTreeObserver$OnComputeInternalInsetsListener)new ViewTreeObserver$OnComputeInternalInsetsListener() {
            public void onComputeInternalInsets(final ViewTreeObserver$InternalInsetsInfo viewTreeObserver$InternalInsetsInfo) {
                if (!StatusBarTouchableRegionManager.this.mIsStatusBarExpanded) {
                    if (!StatusBarTouchableRegionManager.this.mStatusBar.isBouncerShowing()) {
                        viewTreeObserver$InternalInsetsInfo.setTouchableInsets(3);
                        viewTreeObserver$InternalInsetsInfo.touchableRegion.set(StatusBarTouchableRegionManager.this.calculateTouchableRegion());
                    }
                }
            }
        };
        this.mContext = mContext;
        this.initResources();
        configurationController.addCallback((ConfigurationController.ConfigurationListener)new ConfigurationController.ConfigurationListener() {
            @Override
            public void onDensityOrFontScaleChanged() {
                StatusBarTouchableRegionManager.this.initResources();
            }
            
            @Override
            public void onOverlayChanged() {
                StatusBarTouchableRegionManager.this.initResources();
            }
        });
        (this.mHeadsUpManager = mHeadsUpManager).addListener(new OnHeadsUpChangedListener() {
            @Override
            public void onHeadsUpPinnedModeChanged(final boolean b) {
                if (Log.isLoggable("TouchableRegionManager", 5)) {
                    Log.w("TouchableRegionManager", "onHeadsUpPinnedModeChanged");
                }
                StatusBarTouchableRegionManager.this.updateTouchableRegion();
            }
        });
        this.mHeadsUpManager.addHeadsUpPhoneListener((HeadsUpManagerPhone.OnHeadsUpPhoneListenerChange)new HeadsUpManagerPhone.OnHeadsUpPhoneListenerChange() {
            @Override
            public void onHeadsUpGoingAwayStateChanged(final boolean b) {
                if (!b) {
                    StatusBarTouchableRegionManager.this.updateTouchableRegionAfterLayout();
                }
                else {
                    StatusBarTouchableRegionManager.this.updateTouchableRegion();
                }
            }
        });
        (this.mNotificationShadeWindowController = mNotificationShadeWindowController).setForcePluginOpenListener((NotificationShadeWindowController.ForcePluginOpenListener)new _$$Lambda$StatusBarTouchableRegionManager$zqDZ6_Pei5QdrwLKWlTK2XAXySs(this));
        (this.mBubbleController = mBubbleController).setBubbleStateChangeListener((BubbleController.BubbleStateChangeListener)new _$$Lambda$StatusBarTouchableRegionManager$Y8JuQSpnLW6lgZJvlN6rmv4xaLI(this));
    }
    
    private void initResources() {
        final Resources resources = this.mContext.getResources();
        this.mDisplayCutoutTouchableRegionSize = resources.getDimensionPixelSize(17105173);
        this.mStatusBarHeight = resources.getDimensionPixelSize(17105471);
    }
    
    private void updateRegionForNotch(final Region region) {
        final WindowInsets rootWindowInsets = this.mNotificationShadeWindowView.getRootWindowInsets();
        if (rootWindowInsets == null) {
            Log.w("TouchableRegionManager", "StatusBarWindowView is not attached.");
            return;
        }
        final DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
        if (displayCutout == null) {
            return;
        }
        final Rect rect = new Rect();
        ScreenDecorations.DisplayCutoutView.boundsFromDirection(displayCutout, 48, rect);
        rect.offset(0, this.mDisplayCutoutTouchableRegionSize);
        region.union(rect);
    }
    
    private void updateTouchableRegion() {
        final View mNotificationShadeWindowView = this.mNotificationShadeWindowView;
        final boolean b = true;
        final boolean b2 = mNotificationShadeWindowView != null && mNotificationShadeWindowView.getRootWindowInsets() != null && this.mNotificationShadeWindowView.getRootWindowInsets().getDisplayCutout() != null;
        boolean mShouldAdjustInsets = b;
        if (!this.mHeadsUpManager.hasPinnedHeadsUp()) {
            mShouldAdjustInsets = b;
            if (!this.mHeadsUpManager.isHeadsUpGoingAway()) {
                mShouldAdjustInsets = b;
                if (!this.mBubbleController.hasBubbles()) {
                    mShouldAdjustInsets = b;
                    if (!this.mForceCollapsedUntilLayout) {
                        mShouldAdjustInsets = b;
                        if (!b2) {
                            mShouldAdjustInsets = (this.mNotificationShadeWindowController.getForcePluginOpen() && b);
                        }
                    }
                }
            }
        }
        if (mShouldAdjustInsets == this.mShouldAdjustInsets) {
            return;
        }
        if (mShouldAdjustInsets) {
            this.mNotificationShadeWindowView.getViewTreeObserver().addOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
            this.mNotificationShadeWindowView.requestLayout();
        }
        else {
            this.mNotificationShadeWindowView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
        }
        this.mShouldAdjustInsets = mShouldAdjustInsets;
    }
    
    private void updateTouchableRegionAfterLayout() {
        final View mNotificationPanelView = this.mNotificationPanelView;
        if (mNotificationPanelView != null) {
            this.mForceCollapsedUntilLayout = true;
            mNotificationPanelView.addOnLayoutChangeListener((View$OnLayoutChangeListener)new View$OnLayoutChangeListener() {
                public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
                    if (!StatusBarTouchableRegionManager.this.mNotificationPanelView.isVisibleToUser()) {
                        StatusBarTouchableRegionManager.this.mNotificationPanelView.removeOnLayoutChangeListener((View$OnLayoutChangeListener)this);
                        StatusBarTouchableRegionManager.this.mForceCollapsedUntilLayout = false;
                        StatusBarTouchableRegionManager.this.updateTouchableRegion();
                    }
                }
            });
        }
    }
    
    Region calculateTouchableRegion() {
        final Region touchableRegion = this.mHeadsUpManager.getTouchableRegion();
        if (touchableRegion != null) {
            this.mTouchableRegion.set(touchableRegion);
        }
        else {
            this.mTouchableRegion.set(0, 0, this.mNotificationShadeWindowView.getWidth(), this.mStatusBarHeight);
            this.updateRegionForNotch(this.mTouchableRegion);
        }
        final Rect touchableRegion2 = this.mBubbleController.getTouchableRegion();
        if (touchableRegion2 != null) {
            this.mTouchableRegion.union(touchableRegion2);
        }
        return this.mTouchableRegion;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("StatusBarTouchableRegionManager state:");
        printWriter.print("  mTouchableRegion=");
        printWriter.println(this.mTouchableRegion);
    }
    
    void setPanelExpanded(final boolean mIsStatusBarExpanded) {
        if (mIsStatusBarExpanded != this.mIsStatusBarExpanded) {
            this.mIsStatusBarExpanded = mIsStatusBarExpanded;
            if (mIsStatusBarExpanded) {
                this.mForceCollapsedUntilLayout = false;
            }
            this.updateTouchableRegion();
        }
    }
    
    protected void setup(final StatusBar mStatusBar, final View mNotificationShadeWindowView) {
        this.mStatusBar = mStatusBar;
        this.mNotificationShadeWindowView = mNotificationShadeWindowView;
        this.mNotificationPanelView = mNotificationShadeWindowView.findViewById(R$id.notification_panel);
    }
}
