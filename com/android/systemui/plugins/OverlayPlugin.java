// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import com.android.systemui.plugins.statusbar.DozeParameters;
import android.view.View;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_OVERLAY", version = 4)
public interface OverlayPlugin extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_OVERLAY";
    public static final int VERSION = 4;
    
    default boolean holdStatusBarOpen() {
        return false;
    }
    
    default void setCollapseDesired(final boolean b) {
    }
    
    void setup(final View p0, final View p1);
    
    default void setup(final View view, final View view2, final Callback callback, final DozeParameters dozeParameters) {
        this.setup(view, view2);
    }
    
    public interface Callback
    {
        void onHoldStatusBarOpenChange();
    }
}
