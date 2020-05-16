// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.view.ViewGroup;
import android.service.controls.Control;
import java.util.List;
import android.content.ComponentName;

public interface ControlsUiController
{
    default static {
        final Companion $$INSTANCE = Companion.$$INSTANCE;
    }
    
    boolean getAvailable();
    
    void hide();
    
    void onActionResponse(final ComponentName p0, final String p1, final int p2);
    
    void onRefreshState(final ComponentName p0, final List<Control> p1);
    
    void show(final ViewGroup p0);
    
    public static final class Companion
    {
        static final /* synthetic */ Companion $$INSTANCE;
        
        static {
            $$INSTANCE = new Companion();
        }
        
        private Companion() {
        }
    }
}
