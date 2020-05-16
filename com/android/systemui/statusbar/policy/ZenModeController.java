// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.net.Uri;
import android.service.notification.ZenModeConfig$ZenRule;
import android.app.NotificationManager$Policy;
import android.service.notification.ZenModeConfig;

public interface ZenModeController extends CallbackController<Callback>
{
    boolean areNotificationsHiddenInShade();
    
    ZenModeConfig getConfig();
    
    NotificationManager$Policy getConsolidatedPolicy();
    
    ZenModeConfig$ZenRule getManualRule();
    
    long getNextAlarm();
    
    int getZen();
    
    boolean isVolumeRestricted();
    
    void setZen(final int p0, final Uri p1, final String p2);
    
    public interface Callback
    {
        default void onConfigChanged(final ZenModeConfig zenModeConfig) {
        }
        
        default void onConsolidatedPolicyChanged(final NotificationManager$Policy notificationManager$Policy) {
        }
        
        default void onEffectsSupressorChanged() {
        }
        
        default void onManualRuleChanged(final ZenModeConfig$ZenRule zenModeConfig$ZenRule) {
        }
        
        default void onNextAlarmChanged() {
        }
        
        default void onZenAvailableChanged(final boolean b) {
        }
        
        default void onZenChanged(final int n) {
        }
    }
}
