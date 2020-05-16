// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import android.view.View;
import android.view.ViewGroup;

public class NestedScrollingParentHelper
{
    private int mNestedScrollAxesNonTouch;
    private int mNestedScrollAxesTouch;
    
    public NestedScrollingParentHelper(final ViewGroup viewGroup) {
    }
    
    public int getNestedScrollAxes() {
        return this.mNestedScrollAxesNonTouch | this.mNestedScrollAxesTouch;
    }
    
    public void onNestedScrollAccepted(final View view, final View view2, final int n) {
        this.onNestedScrollAccepted(view, view2, n, 0);
    }
    
    public void onNestedScrollAccepted(final View view, final View view2, final int n, final int n2) {
        if (n2 == 1) {
            this.mNestedScrollAxesNonTouch = n;
        }
        else {
            this.mNestedScrollAxesTouch = n;
        }
    }
    
    public void onStopNestedScroll(final View view) {
        this.onStopNestedScroll(view, 0);
    }
    
    public void onStopNestedScroll(final View view, final int n) {
        if (n == 1) {
            this.mNestedScrollAxesNonTouch = 0;
        }
        else {
            this.mNestedScrollAxesTouch = 0;
        }
    }
}
