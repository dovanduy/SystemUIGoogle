// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.tv.dagger;

import com.android.systemui.pip.tv.PipControlsView;
import com.android.systemui.pip.tv.PipControlsViewController;

public interface TvPipComponent
{
    PipControlsViewController getPipControlsViewController();
    
    public interface Builder
    {
        TvPipComponent build();
        
        Builder pipControlsView(final PipControlsView p0);
    }
}
