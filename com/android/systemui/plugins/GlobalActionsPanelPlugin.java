// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.view.View;
import android.graphics.drawable.Drawable;
import android.app.PendingIntent$CanceledException;
import android.app.PendingIntent;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.Dependencies;

@Dependencies({ @DependsOn(target = Callbacks.class), @DependsOn(target = PanelViewController.class) })
@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_GLOBAL_ACTIONS_PANEL", version = 0)
public interface GlobalActionsPanelPlugin extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_GLOBAL_ACTIONS_PANEL";
    public static final int VERSION = 0;
    
    PanelViewController onPanelShown(final Callbacks p0, final boolean p1);
    
    @ProvidesInterface(version = 0)
    public interface Callbacks
    {
        public static final int VERSION = 0;
        
        void dismissGlobalActionsMenu();
        
        default void startPendingIntentDismissingKeyguard(final PendingIntent pendingIntent) {
            try {
                pendingIntent.send();
            }
            catch (PendingIntent$CanceledException ex) {}
        }
    }
    
    @ProvidesInterface(version = 0)
    public interface PanelViewController
    {
        public static final int VERSION = 0;
        
        default Drawable getBackgroundDrawable() {
            return null;
        }
        
        View getPanelContent();
        
        void onDeviceLockStateChanged(final boolean p0);
        
        void onDismissed();
    }
}
