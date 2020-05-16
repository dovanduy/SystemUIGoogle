// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import android.text.TextUtils$TruncateAt;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;

public class AutoMarqueeTextView extends TextView
{
    private boolean mAggregatedVisible;
    
    public AutoMarqueeTextView(final Context context) {
        super(context);
        this.mAggregatedVisible = false;
    }
    
    public AutoMarqueeTextView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mAggregatedVisible = false;
    }
    
    public AutoMarqueeTextView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mAggregatedVisible = false;
    }
    
    public AutoMarqueeTextView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mAggregatedVisible = false;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setSelected(true);
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.setSelected(false);
    }
    
    protected void onFinishInflate() {
        this.onVisibilityAggregated(this.isVisibleToUser());
    }
    
    public void onVisibilityAggregated(final boolean mAggregatedVisible) {
        super.onVisibilityAggregated(mAggregatedVisible);
        if (mAggregatedVisible == this.mAggregatedVisible) {
            return;
        }
        this.mAggregatedVisible = mAggregatedVisible;
        if (mAggregatedVisible) {
            this.setEllipsize(TextUtils$TruncateAt.MARQUEE);
        }
        else {
            this.setEllipsize(TextUtils$TruncateAt.END);
        }
    }
}
