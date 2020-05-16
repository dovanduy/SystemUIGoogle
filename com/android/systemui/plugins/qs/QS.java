// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins.qs;

import android.view.View$OnClickListener;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.view.View;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.FragmentBase;

@DependsOn(target = HeightListener.class)
@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_QS", version = 7)
public interface QS extends FragmentBase
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_QS";
    public static final String TAG = "QS";
    public static final int VERSION = 7;
    
    void animateHeaderSlidingIn(final long p0);
    
    void animateHeaderSlidingOut();
    
    void closeDetail();
    
    int getDesiredHeight();
    
    View getHeader();
    
    int getQsMinExpansionHeight();
    
    void hideImmediately();
    
    boolean isCustomizing();
    
    boolean isShowingDetail();
    
    void notifyCustomizeChanged();
    
    default boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        return this.isCustomizing();
    }
    
    void setContainer(final ViewGroup p0);
    
    void setExpandClickListener(final View$OnClickListener p0);
    
    void setExpanded(final boolean p0);
    
    default void setHasNotifications(final boolean b) {
    }
    
    void setHeaderClickable(final boolean p0);
    
    void setHeaderListening(final boolean p0);
    
    void setHeightOverride(final int p0);
    
    void setListening(final boolean p0);
    
    void setOverscrolling(final boolean p0);
    
    void setPanelView(final HeightListener p0);
    
    void setQsExpansion(final float p0, final float p1);
    
    default void setShowCollapsedOnKeyguard(final boolean b) {
    }
    
    @ProvidesInterface(version = 1)
    public interface HeightListener
    {
        public static final int VERSION = 1;
        
        void onQsHeightChanged();
    }
}
