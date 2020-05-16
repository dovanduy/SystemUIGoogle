// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.os.Handler;
import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager$AccessibilityServicesStateChangeListener;

public class AccessibilityManagerWrapper implements CallbackController<AccessibilityManager$AccessibilityServicesStateChangeListener>
{
    private final AccessibilityManager mAccessibilityManager;
    
    public AccessibilityManagerWrapper(final Context context) {
        this.mAccessibilityManager = (AccessibilityManager)context.getSystemService((Class)AccessibilityManager.class);
    }
    
    @Override
    public void addCallback(final AccessibilityManager$AccessibilityServicesStateChangeListener accessibilityManager$AccessibilityServicesStateChangeListener) {
        this.mAccessibilityManager.addAccessibilityServicesStateChangeListener(accessibilityManager$AccessibilityServicesStateChangeListener, (Handler)null);
    }
    
    public int getRecommendedTimeoutMillis(final int n, final int n2) {
        return this.mAccessibilityManager.getRecommendedTimeoutMillis(n, n2);
    }
    
    @Override
    public void removeCallback(final AccessibilityManager$AccessibilityServicesStateChangeListener accessibilityManager$AccessibilityServicesStateChangeListener) {
        this.mAccessibilityManager.removeAccessibilityServicesStateChangeListener(accessibilityManager$AccessibilityServicesStateChangeListener);
    }
}
