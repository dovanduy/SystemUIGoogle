// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.graphics.Region;
import android.view.View;
import android.graphics.Region$Op;
import android.view.ViewTreeObserver$InternalInsetsInfo;
import android.util.AttributeSet;
import android.content.Context;
import android.view.ViewTreeObserver$OnComputeInternalInsetsListener;
import android.widget.FrameLayout;

public class RegionInterceptingFrameLayout extends FrameLayout
{
    private final ViewTreeObserver$OnComputeInternalInsetsListener mInsetsListener;
    
    public RegionInterceptingFrameLayout(final Context context) {
        super(context);
        this.mInsetsListener = (ViewTreeObserver$OnComputeInternalInsetsListener)new _$$Lambda$RegionInterceptingFrameLayout$JlFdsR9I_9ubvsna7k1PTnmr7OI(this);
    }
    
    public RegionInterceptingFrameLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.mInsetsListener = (ViewTreeObserver$OnComputeInternalInsetsListener)new _$$Lambda$RegionInterceptingFrameLayout$JlFdsR9I_9ubvsna7k1PTnmr7OI(this);
    }
    
    public RegionInterceptingFrameLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mInsetsListener = (ViewTreeObserver$OnComputeInternalInsetsListener)new _$$Lambda$RegionInterceptingFrameLayout$JlFdsR9I_9ubvsna7k1PTnmr7OI(this);
    }
    
    public RegionInterceptingFrameLayout(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mInsetsListener = (ViewTreeObserver$OnComputeInternalInsetsListener)new _$$Lambda$RegionInterceptingFrameLayout$JlFdsR9I_9ubvsna7k1PTnmr7OI(this);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.getViewTreeObserver().addOnComputeInternalInsetsListener(this.mInsetsListener);
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mInsetsListener);
    }
    
    public interface RegionInterceptableView
    {
        Region getInterceptRegion();
        
        default boolean shouldInterceptTouch() {
            return false;
        }
    }
}
