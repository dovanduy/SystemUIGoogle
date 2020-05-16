// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.View;
import android.view.ViewGroup$MarginLayoutParams;
import androidx.leanback.R$id;
import android.view.View$MeasureSpec;
import android.view.KeyEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.RelativeLayout;

class GuidedActionsRelativeLayout extends RelativeLayout
{
    private boolean mInOverride;
    private InterceptKeyEventListener mInterceptKeyEventListener;
    private float mKeyLinePercent;
    
    public GuidedActionsRelativeLayout(final Context context) {
        this(context, null);
    }
    
    public GuidedActionsRelativeLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public GuidedActionsRelativeLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mInOverride = false;
        this.mKeyLinePercent = GuidanceStylingRelativeLayout.getKeyLinePercent(context);
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        final InterceptKeyEventListener mInterceptKeyEventListener = this.mInterceptKeyEventListener;
        return (mInterceptKeyEventListener != null && mInterceptKeyEventListener.onInterceptKeyEvent(keyEvent)) || super.dispatchKeyEvent(keyEvent);
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.mInOverride = false;
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int size = View$MeasureSpec.getSize(n2);
        if (size > 0) {
            final View viewById = this.findViewById(R$id.guidedactions_sub_list);
            if (viewById != null) {
                final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)viewById.getLayoutParams();
                if (viewGroup$MarginLayoutParams.topMargin < 0 && !this.mInOverride) {
                    this.mInOverride = true;
                }
                if (this.mInOverride) {
                    viewGroup$MarginLayoutParams.topMargin = (int)(this.mKeyLinePercent * size / 100.0f);
                }
            }
        }
        super.onMeasure(n, n2);
    }
    
    interface InterceptKeyEventListener
    {
        boolean onInterceptKeyEvent(final KeyEvent p0);
    }
}
