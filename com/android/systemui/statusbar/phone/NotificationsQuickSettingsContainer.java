// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.ViewGroup;
import com.android.systemui.plugins.qs.QS;
import android.app.Fragment;
import android.view.ViewStub;
import com.android.systemui.R$id;
import com.android.systemui.R$dimen;
import android.content.res.Configuration;
import android.view.WindowInsets;
import android.graphics.Canvas;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import java.util.function.ToIntFunction;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import java.util.Comparator;
import android.view.View;
import java.util.ArrayList;
import com.android.systemui.statusbar.notification.AboveShelfObserver;
import com.android.systemui.fragments.FragmentHostManager;
import android.view.ViewStub$OnInflateListener;
import android.widget.FrameLayout;

public class NotificationsQuickSettingsContainer extends FrameLayout implements ViewStub$OnInflateListener, FragmentListener, HasViewAboveShelfChangedListener
{
    private int mBottomPadding;
    private boolean mCustomizerAnimating;
    private ArrayList<View> mDrawingOrderedChildren;
    private boolean mHasViewsAboveShelf;
    private final Comparator<View> mIndexComparator;
    private boolean mInflated;
    private View mKeyguardStatusBar;
    private ArrayList<View> mLayoutDrawingOrder;
    private boolean mQsExpanded;
    private FrameLayout mQsFrame;
    private NotificationStackScrollLayout mStackScroller;
    private int mStackScrollerMargin;
    private View mUserSwitcher;
    
    public NotificationsQuickSettingsContainer(final Context context, final AttributeSet set) {
        super(context, set);
        this.mDrawingOrderedChildren = new ArrayList<View>();
        this.mLayoutDrawingOrder = new ArrayList<View>();
        this.mIndexComparator = Comparator.comparingInt((ToIntFunction<? super View>)new _$$Lambda$rYOLYKY9UUHboooVhy4ZToEslhI(this));
    }
    
    private void reloadWidth(final View view, final int n) {
        final FrameLayout$LayoutParams layoutParams = (FrameLayout$LayoutParams)view.getLayoutParams();
        layoutParams.width = this.getResources().getDimensionPixelSize(n);
        view.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
    }
    
    private void setBottomMargin(final View view, final int bottomMargin) {
        final FrameLayout$LayoutParams layoutParams = (FrameLayout$LayoutParams)view.getLayoutParams();
        layoutParams.bottomMargin = bottomMargin;
        view.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
    }
    
    protected void dispatchDraw(final Canvas canvas) {
        this.mDrawingOrderedChildren.clear();
        this.mLayoutDrawingOrder.clear();
        if (this.mInflated && this.mUserSwitcher.getVisibility() == 0) {
            this.mDrawingOrderedChildren.add(this.mUserSwitcher);
            this.mLayoutDrawingOrder.add(this.mUserSwitcher);
        }
        if (this.mKeyguardStatusBar.getVisibility() == 0) {
            this.mDrawingOrderedChildren.add(this.mKeyguardStatusBar);
            this.mLayoutDrawingOrder.add(this.mKeyguardStatusBar);
        }
        if (this.mStackScroller.getVisibility() == 0) {
            this.mDrawingOrderedChildren.add((View)this.mStackScroller);
            this.mLayoutDrawingOrder.add((View)this.mStackScroller);
        }
        if (this.mQsFrame.getVisibility() == 0) {
            this.mDrawingOrderedChildren.add((View)this.mQsFrame);
            this.mLayoutDrawingOrder.add((View)this.mQsFrame);
        }
        if (this.mHasViewsAboveShelf) {
            this.mDrawingOrderedChildren.remove(this.mStackScroller);
            this.mDrawingOrderedChildren.add((View)this.mStackScroller);
        }
        this.mLayoutDrawingOrder.sort(this.mIndexComparator);
        super.dispatchDraw(canvas);
    }
    
    protected boolean drawChild(final Canvas canvas, final View o, final long n) {
        final int index = this.mLayoutDrawingOrder.indexOf(o);
        if (index >= 0) {
            return super.drawChild(canvas, (View)this.mDrawingOrderedChildren.get(index), n);
        }
        return super.drawChild(canvas, o, n);
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        this.setPadding(0, 0, 0, this.mBottomPadding = windowInsets.getStableInsetBottom());
        return windowInsets;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FragmentHostManager.get((View)this).addTagListener("QS", (FragmentHostManager.FragmentListener)this);
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.reloadWidth((View)this.mQsFrame, R$dimen.qs_panel_width);
        this.reloadWidth((View)this.mStackScroller, R$dimen.notification_panel_width);
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FragmentHostManager.get((View)this).removeTagListener("QS", (FragmentHostManager.FragmentListener)this);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mQsFrame = (FrameLayout)this.findViewById(R$id.qs_frame);
        final NotificationStackScrollLayout mStackScroller = (NotificationStackScrollLayout)this.findViewById(R$id.notification_stack_scroller);
        this.mStackScroller = mStackScroller;
        this.mStackScrollerMargin = ((FrameLayout$LayoutParams)mStackScroller.getLayoutParams()).bottomMargin;
        this.mKeyguardStatusBar = this.findViewById(R$id.keyguard_header);
        final ViewStub mUserSwitcher = (ViewStub)this.findViewById(R$id.keyguard_user_switcher);
        mUserSwitcher.setOnInflateListener((ViewStub$OnInflateListener)this);
        this.mUserSwitcher = (View)mUserSwitcher;
    }
    
    public void onFragmentViewCreated(final String s, final Fragment fragment) {
        ((QS)fragment).setContainer((ViewGroup)this);
    }
    
    public void onHasViewsAboveShelfChanged(final boolean mHasViewsAboveShelf) {
        this.mHasViewsAboveShelf = mHasViewsAboveShelf;
        this.invalidate();
    }
    
    public void onInflate(final ViewStub viewStub, final View mUserSwitcher) {
        if (viewStub == this.mUserSwitcher) {
            this.mUserSwitcher = mUserSwitcher;
            this.mInflated = true;
        }
    }
    
    public void setCustomizerAnimating(final boolean mCustomizerAnimating) {
        if (this.mCustomizerAnimating != mCustomizerAnimating) {
            this.mCustomizerAnimating = mCustomizerAnimating;
            this.invalidate();
        }
    }
    
    public void setCustomizerShowing(final boolean qsCustomizerShowing) {
        if (qsCustomizerShowing) {
            this.setPadding(0, 0, 0, 0);
            this.setBottomMargin((View)this.mStackScroller, 0);
        }
        else {
            this.setPadding(0, 0, 0, this.mBottomPadding);
            this.setBottomMargin((View)this.mStackScroller, this.mStackScrollerMargin);
        }
        this.mStackScroller.setQsCustomizerShowing(qsCustomizerShowing);
    }
    
    public void setQsExpanded(final boolean mQsExpanded) {
        if (this.mQsExpanded != mQsExpanded) {
            this.mQsExpanded = mQsExpanded;
            this.invalidate();
        }
    }
}
