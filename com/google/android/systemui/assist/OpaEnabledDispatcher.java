// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist;

import android.os.UserManager;
import android.content.Context;
import java.util.ArrayList;
import android.view.View;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;

public class OpaEnabledDispatcher implements OpaEnabledListener
{
    private final Lazy<StatusBar> mStatusBarLazy;
    
    public OpaEnabledDispatcher(final Lazy<StatusBar> mStatusBarLazy) {
        this.mStatusBarLazy = mStatusBarLazy;
    }
    
    private void dispatchUnchecked(final boolean opaEnabled) {
        final StatusBar statusBar = this.mStatusBarLazy.get();
        if (statusBar.getNavigationBarView() != null) {
            final ArrayList<View> views = statusBar.getNavigationBarView().getHomeButton().getViews();
            for (int i = 0; i < views.size(); ++i) {
                ((OpaLayout)views.get(i)).setOpaEnabled(opaEnabled);
            }
        }
    }
    
    @Override
    public void onOpaEnabledReceived(final Context context, final boolean b, final boolean b2, final boolean b3) {
        this.dispatchUnchecked((b && b2) || UserManager.isDeviceInDemoMode(context));
    }
}
