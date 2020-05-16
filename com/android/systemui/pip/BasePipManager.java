// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip;

import com.android.systemui.shared.recents.IPinnedStackAnimationListener;
import android.content.res.Configuration;
import java.io.PrintWriter;

public interface BasePipManager
{
    default void dump(final PrintWriter printWriter) {
    }
    
    void onConfigurationChanged(final Configuration p0);
    
    default void setPinnedStackAnimationListener(final IPinnedStackAnimationListener pinnedStackAnimationListener) {
    }
    
    default void setPinnedStackAnimationType(final int n) {
    }
    
    default void setShelfHeight(final boolean b, final int n) {
    }
    
    void showPictureInPictureMenu();
}
