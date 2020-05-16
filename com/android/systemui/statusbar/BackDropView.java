// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public class BackDropView extends FrameLayout
{
    private Runnable mOnVisibilityChangedRunnable;
    
    public BackDropView(final Context context) {
        super(context);
    }
    
    public BackDropView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public BackDropView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public BackDropView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onVisibilityChanged(final View view, final int n) {
        super.onVisibilityChanged(view, n);
        if (view == this) {
            final Runnable mOnVisibilityChangedRunnable = this.mOnVisibilityChangedRunnable;
            if (mOnVisibilityChangedRunnable != null) {
                mOnVisibilityChangedRunnable.run();
            }
        }
    }
}
