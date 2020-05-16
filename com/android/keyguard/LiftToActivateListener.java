// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import android.view.View$OnHoverListener;

class LiftToActivateListener implements View$OnHoverListener
{
    private final AccessibilityManager mAccessibilityManager;
    private boolean mCachedClickableState;
    
    public LiftToActivateListener(final Context context) {
        this.mAccessibilityManager = (AccessibilityManager)context.getSystemService("accessibility");
    }
    
    public boolean onHover(final View view, final MotionEvent motionEvent) {
        if (this.mAccessibilityManager.isEnabled() && this.mAccessibilityManager.isTouchExplorationEnabled()) {
            final int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 9) {
                if (actionMasked == 10) {
                    final int n = (int)motionEvent.getX();
                    final int n2 = (int)motionEvent.getY();
                    if (n > view.getPaddingLeft() && n2 > view.getPaddingTop() && n < view.getWidth() - view.getPaddingRight() && n2 < view.getHeight() - view.getPaddingBottom()) {
                        view.performClick();
                    }
                    view.setClickable(this.mCachedClickableState);
                }
            }
            else {
                this.mCachedClickableState = view.isClickable();
                view.setClickable(false);
            }
        }
        view.onHoverEvent(motionEvent);
        return true;
    }
}
