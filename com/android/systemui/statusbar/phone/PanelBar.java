// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.ViewGroup;
import android.util.Log;
import android.view.MotionEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public abstract class PanelBar extends FrameLayout
{
    public static final String TAG;
    private boolean mBouncerShowing;
    private boolean mExpanded;
    PanelViewController mPanel;
    protected float mPanelFraction;
    private int mState;
    private boolean mTracking;
    
    static {
        TAG = PanelBar.class.getSimpleName();
    }
    
    public PanelBar(final Context context, final AttributeSet set) {
        super(context, set);
        this.mState = 0;
    }
    
    public void collapsePanel(final boolean b, final boolean b2, final float n) {
        final PanelViewController mPanel = this.mPanel;
        boolean b3;
        if (b && !mPanel.isFullyCollapsed()) {
            mPanel.collapse(b2, n);
            b3 = true;
        }
        else {
            mPanel.resetViews(false);
            mPanel.setExpandedFraction(0.0f);
            mPanel.cancelPeek();
            b3 = false;
        }
        if (!b3 && this.mState != 0) {
            this.go(0);
            this.onPanelCollapsed();
        }
    }
    
    public float getExpansionFraction() {
        return this.mPanelFraction;
    }
    
    public void go(final int mState) {
        this.mState = mState;
    }
    
    public boolean isClosed() {
        return this.mState == 0;
    }
    
    public boolean isExpanded() {
        return this.mExpanded;
    }
    
    public void onClosingFinished() {
    }
    
    public void onExpandingFinished() {
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
    
    public void onPanelCollapsed() {
    }
    
    public void onPanelFullyOpened() {
    }
    
    public void onPanelPeeked() {
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable != null && parcelable instanceof Bundle) {
            final Bundle bundle = (Bundle)parcelable;
            super.onRestoreInstanceState(bundle.getParcelable("panel_bar_super_parcelable"));
            if (bundle.containsKey("state")) {
                this.go(bundle.getInt("state", 0));
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("panel_bar_super_parcelable", super.onSaveInstanceState());
        bundle.putInt("state", this.mState);
        return (Parcelable)bundle;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final String tag = PanelBar.TAG;
        final boolean panelEnabled = this.panelEnabled();
        boolean b = false;
        if (!panelEnabled) {
            if (motionEvent.getAction() == 0) {
                Log.v(tag, String.format("onTouch: all panels disabled, ignoring touch at (%d,%d)", (int)motionEvent.getX(), (int)motionEvent.getY()));
            }
            return false;
        }
        if (motionEvent.getAction() == 0) {
            final PanelViewController mPanel = this.mPanel;
            if (mPanel == null) {
                Log.v(tag, String.format("onTouch: no panel for touch at (%d,%d)", (int)motionEvent.getX(), (int)motionEvent.getY()));
                return true;
            }
            if (!mPanel.isEnabled()) {
                Log.v(tag, String.format("onTouch: panel (%s) is disabled, ignoring touch at (%d,%d)", mPanel, (int)motionEvent.getX(), (int)motionEvent.getY()));
                return true;
            }
        }
        final PanelViewController mPanel2 = this.mPanel;
        if (mPanel2 == null || mPanel2.getView().dispatchTouchEvent(motionEvent)) {
            b = true;
        }
        return b;
    }
    
    public void onTrackingStarted() {
        this.mTracking = true;
    }
    
    public void onTrackingStopped(final boolean b) {
        this.mTracking = false;
    }
    
    public boolean panelEnabled() {
        return true;
    }
    
    public void panelExpansionChanged(final float n, final boolean mExpanded) {
        if (!Float.isNaN(n)) {
            final PanelViewController mPanel = this.mPanel;
            this.mExpanded = mExpanded;
            this.mPanelFraction = n;
            this.updateVisibility();
            boolean b = true;
            boolean b2;
            int n3;
            if (mExpanded) {
                if (this.mState == 0) {
                    this.go(1);
                    this.onPanelPeeked();
                }
                if (mPanel.getExpandedFraction() < 1.0f) {
                    b = false;
                }
                final int n2 = 0;
                b2 = b;
                n3 = n2;
            }
            else {
                n3 = 1;
                b2 = false;
            }
            if (b2 && !this.mTracking) {
                this.go(2);
                this.onPanelFullyOpened();
            }
            else if (n3 != 0 && !this.mTracking && this.mState != 0) {
                this.go(0);
                this.onPanelCollapsed();
            }
            return;
        }
        throw new IllegalArgumentException("frac cannot be NaN");
    }
    
    public abstract void panelScrimMinFractionChanged(final float p0);
    
    public void setBouncerShowing(final boolean mBouncerShowing) {
        this.mBouncerShowing = mBouncerShowing;
        int n;
        if (mBouncerShowing) {
            n = 4;
        }
        else {
            n = 0;
        }
        this.setImportantForAccessibility(n);
        this.updateVisibility();
        final PanelViewController mPanel = this.mPanel;
        if (mPanel != null) {
            mPanel.getView().setImportantForAccessibility(n);
        }
    }
    
    public void setPanel(final PanelViewController mPanel) {
        (this.mPanel = mPanel).setBar(this);
    }
    
    protected boolean shouldPanelBeVisible() {
        return this.mExpanded || this.mBouncerShowing;
    }
    
    protected void updateVisibility() {
        final ViewGroup view = this.mPanel.getView();
        int visibility;
        if (this.shouldPanelBeVisible()) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        view.setVisibility(visibility);
    }
}
