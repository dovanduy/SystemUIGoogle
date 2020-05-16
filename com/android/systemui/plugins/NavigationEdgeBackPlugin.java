// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.view.WindowManager$LayoutParams;
import android.graphics.Point;
import android.view.MotionEvent;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_NAVIGATION_EDGE_BACK_ACTION", version = 1)
public interface NavigationEdgeBackPlugin extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_NAVIGATION_EDGE_BACK_ACTION";
    public static final int VERSION = 1;
    
    void onMotionEvent(final MotionEvent p0);
    
    void setBackCallback(final BackCallback p0);
    
    void setDisplaySize(final Point p0);
    
    void setInsets(final int p0, final int p1);
    
    void setIsLeftPanel(final boolean p0);
    
    void setLayoutParams(final WindowManager$LayoutParams p0);
    
    public interface BackCallback
    {
        void cancelBack();
        
        void triggerBack();
    }
}
