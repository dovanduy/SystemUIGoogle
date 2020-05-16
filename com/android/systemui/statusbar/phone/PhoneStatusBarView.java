// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.accessibility.AccessibilityRecord;
import android.view.accessibility.AccessibilityEvent;
import android.view.MotionEvent;
import com.android.systemui.R$id;
import android.content.res.Configuration;
import android.view.WindowInsets;
import android.view.ViewGroup$LayoutParams;
import com.android.systemui.R$dimen;
import java.util.Objects;
import com.android.systemui.util.leak.RotationUtils;
import com.android.systemui.ScreenDecorations;
import android.graphics.Rect;
import android.widget.LinearLayout$LayoutParams;
import android.util.Pair;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import android.view.DisplayCutout;
import com.android.systemui.statusbar.CommandQueue;
import android.view.View;
import com.android.systemui.plugins.DarkIconDispatcher;

public class PhoneStatusBarView extends PanelBar
{
    StatusBar mBar;
    private DarkIconDispatcher.DarkReceiver mBattery;
    private View mCenterIconSpace;
    private final CommandQueue mCommandQueue;
    private int mCutoutSideNudge;
    private View mCutoutSpace;
    private DisplayCutout mDisplayCutout;
    private boolean mHeadsUpVisible;
    private Runnable mHideExpandedRunnable;
    boolean mIsFullyOpenedPanel;
    private float mMinFraction;
    private int mRotationOrientation;
    private int mRoundedCornerPadding;
    private ScrimController mScrimController;
    private int mStatusBarHeight;
    
    public PhoneStatusBarView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mIsFullyOpenedPanel = false;
        this.mHideExpandedRunnable = new Runnable() {
            @Override
            public void run() {
                final PhoneStatusBarView this$0 = PhoneStatusBarView.this;
                if (this$0.mPanelFraction == 0.0f) {
                    this$0.mBar.makeExpandedInvisible();
                }
            }
        };
        this.mRotationOrientation = -1;
        this.mCutoutSideNudge = 0;
        this.mRoundedCornerPadding = 0;
        this.mCommandQueue = Dependency.get(CommandQueue.class);
    }
    
    private void updateCutoutLocation(final Pair<Integer, Integer> pair) {
        if (this.mCutoutSpace == null) {
            return;
        }
        final DisplayCutout mDisplayCutout = this.mDisplayCutout;
        if (mDisplayCutout != null && !mDisplayCutout.isEmpty() && pair == null) {
            this.mCenterIconSpace.setVisibility(8);
            this.mCutoutSpace.setVisibility(0);
            final LinearLayout$LayoutParams linearLayout$LayoutParams = (LinearLayout$LayoutParams)this.mCutoutSpace.getLayoutParams();
            final Rect rect = new Rect();
            ScreenDecorations.DisplayCutoutView.boundsFromDirection(this.mDisplayCutout, 48, rect);
            final int left = rect.left;
            final int mCutoutSideNudge = this.mCutoutSideNudge;
            rect.left = left + mCutoutSideNudge;
            rect.right -= mCutoutSideNudge;
            linearLayout$LayoutParams.width = rect.width();
            linearLayout$LayoutParams.height = rect.height();
            return;
        }
        this.mCenterIconSpace.setVisibility(0);
        this.mCutoutSpace.setVisibility(8);
    }
    
    private void updateLayoutForCutout() {
        this.updateStatusBarHeight();
        this.updateCutoutLocation(StatusBarWindowView.cornerCutoutMargins(this.mDisplayCutout, this.getDisplay()));
        this.updateSafeInsets(StatusBarWindowView.statusBarCornerCutoutMargins(this.mDisplayCutout, this.getDisplay(), this.mRotationOrientation, this.mStatusBarHeight));
    }
    
    private boolean updateOrientationAndCutout() {
        final int exactRotation = RotationUtils.getExactRotation(super.mContext);
        final int mRotationOrientation = this.mRotationOrientation;
        final boolean b = true;
        boolean b2;
        if (exactRotation != mRotationOrientation) {
            this.mRotationOrientation = exactRotation;
            b2 = true;
        }
        else {
            b2 = false;
        }
        if (!Objects.equals(this.getRootWindowInsets().getDisplayCutout(), this.mDisplayCutout)) {
            this.mDisplayCutout = this.getRootWindowInsets().getDisplayCutout();
            b2 = b;
        }
        return b2;
    }
    
    private void updateSafeInsets(final Pair<Integer, Integer> pair) {
        final Pair<Integer, Integer> paddingNeededForCutoutAndRoundedCorner = StatusBarWindowView.paddingNeededForCutoutAndRoundedCorner(this.mDisplayCutout, pair, this.mRoundedCornerPadding);
        this.setPadding((int)paddingNeededForCutoutAndRoundedCorner.first, this.getPaddingTop(), (int)paddingNeededForCutoutAndRoundedCorner.second, this.getPaddingBottom());
    }
    
    private void updateScrimFraction() {
        final float mPanelFraction = super.mPanelFraction;
        final float mMinFraction = this.mMinFraction;
        float max = mPanelFraction;
        if (mMinFraction < 1.0f) {
            max = Math.max((mPanelFraction - mMinFraction) / (1.0f - mMinFraction), 0.0f);
        }
        this.mScrimController.setPanelExpansion(max);
    }
    
    private void updateStatusBarHeight() {
        final DisplayCutout mDisplayCutout = this.mDisplayCutout;
        int top;
        if (mDisplayCutout == null) {
            top = 0;
        }
        else {
            top = mDisplayCutout.getWaterfallInsets().top;
        }
        final ViewGroup$LayoutParams layoutParams = this.getLayoutParams();
        final int dimensionPixelSize = this.getResources().getDimensionPixelSize(R$dimen.status_bar_height);
        this.mStatusBarHeight = dimensionPixelSize;
        layoutParams.height = dimensionPixelSize - top;
        this.setLayoutParams(layoutParams);
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        if (this.updateOrientationAndCutout()) {
            this.updateLayoutForCutout();
            this.requestLayout();
        }
        return super.onApplyWindowInsets(windowInsets);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Dependency.get(DarkIconDispatcher.class).addDarkReceiver(this.mBattery);
        if (this.updateOrientationAndCutout()) {
            this.updateLayoutForCutout();
        }
    }
    
    @Override
    public void onClosingFinished() {
        super.onClosingFinished();
        this.mBar.onClosingFinished();
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.updateOrientationAndCutout()) {
            this.updateLayoutForCutout();
            this.requestLayout();
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(DarkIconDispatcher.class).removeDarkReceiver(this.mBattery);
        this.mDisplayCutout = null;
    }
    
    @Override
    public void onExpandingFinished() {
        super.onExpandingFinished();
        this.mScrimController.onExpandingFinished();
    }
    
    public void onFinishInflate() {
        this.mBattery = (DarkIconDispatcher.DarkReceiver)this.findViewById(R$id.battery);
        this.mCutoutSpace = this.findViewById(R$id.cutout_space_view);
        this.mCenterIconSpace = this.findViewById(R$id.centered_icon_area);
        this.updateResources();
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        return this.mBar.interceptTouchEvent(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }
    
    @Override
    public void onPanelCollapsed() {
        super.onPanelCollapsed();
        this.post(this.mHideExpandedRunnable);
        this.mIsFullyOpenedPanel = false;
    }
    
    @Override
    public void onPanelFullyOpened() {
        super.onPanelFullyOpened();
        if (!this.mIsFullyOpenedPanel) {
            super.mPanel.getView().sendAccessibilityEvent(32);
        }
        this.mIsFullyOpenedPanel = true;
    }
    
    @Override
    public void onPanelPeeked() {
        super.onPanelPeeked();
        this.mBar.makeExpandedVisible(false);
    }
    
    public boolean onRequestSendAccessibilityEventInternal(final View view, final AccessibilityEvent accessibilityEvent) {
        if (super.onRequestSendAccessibilityEventInternal(view, accessibilityEvent)) {
            final AccessibilityEvent obtain = AccessibilityEvent.obtain();
            this.onInitializeAccessibilityEvent(obtain);
            this.dispatchPopulateAccessibilityEvent(obtain);
            accessibilityEvent.appendRecord((AccessibilityRecord)obtain);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return this.mBar.interceptTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }
    
    @Override
    public void onTrackingStarted() {
        super.onTrackingStarted();
        this.mBar.onTrackingStarted();
        this.mScrimController.onTrackingStarted();
        this.removePendingHideExpandedRunnables();
    }
    
    @Override
    public void onTrackingStopped(final boolean b) {
        super.onTrackingStopped(b);
        this.mBar.onTrackingStopped(b);
    }
    
    @Override
    public boolean panelEnabled() {
        return this.mCommandQueue.panelsEnabled();
    }
    
    @Override
    public void panelExpansionChanged(final float n, final boolean b) {
        super.panelExpansionChanged(n, b);
        this.updateScrimFraction();
        if ((n == 0.0f || n == 1.0f) && this.mBar.getNavigationBarView() != null) {
            this.mBar.getNavigationBarView().onStatusBarPanelStateChanged();
        }
    }
    
    @Override
    public void panelScrimMinFractionChanged(final float n) {
        if (!Float.isNaN(n)) {
            if (this.mMinFraction != n) {
                this.mMinFraction = n;
                this.updateScrimFraction();
            }
            return;
        }
        throw new IllegalArgumentException("minFraction cannot be NaN");
    }
    
    public void removePendingHideExpandedRunnables() {
        this.removeCallbacks(this.mHideExpandedRunnable);
    }
    
    public void setBar(final StatusBar mBar) {
        this.mBar = mBar;
    }
    
    public void setHeadsUpVisible(final boolean mHeadsUpVisible) {
        this.mHeadsUpVisible = mHeadsUpVisible;
        this.updateVisibility();
    }
    
    public void setScrimController(final ScrimController mScrimController) {
        this.mScrimController = mScrimController;
    }
    
    @Override
    protected boolean shouldPanelBeVisible() {
        return this.mHeadsUpVisible || super.shouldPanelBeVisible();
    }
    
    public void updateResources() {
        this.mCutoutSideNudge = this.getResources().getDimensionPixelSize(R$dimen.display_cutout_margin_consumption);
        this.mRoundedCornerPadding = this.getResources().getDimensionPixelSize(R$dimen.rounded_corner_content_padding);
        this.updateStatusBarHeight();
    }
}
