// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.util.Log;
import com.android.systemui.plugins.ActivityStarter;

public class KeyguardDismissUtil implements KeyguardDismissHandler
{
    private volatile KeyguardDismissHandler mDismissHandler;
    
    @Override
    public void executeWhenUnlocked(final ActivityStarter.OnDismissAction onDismissAction, final boolean b) {
        final KeyguardDismissHandler mDismissHandler = this.mDismissHandler;
        if (mDismissHandler == null) {
            Log.wtf("KeyguardDismissUtil", "KeyguardDismissHandler not set.");
            onDismissAction.onDismiss();
            return;
        }
        mDismissHandler.executeWhenUnlocked(onDismissAction, b);
    }
    
    public void setDismissHandler(final KeyguardDismissHandler mDismissHandler) {
        this.mDismissHandler = mDismissHandler;
    }
}
