// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.os.UserManager;
import com.android.internal.widget.LockPatternUtils;
import android.content.Context;
import android.os.Bundle;

public class Utils
{
    static int getAuthenticators(final Bundle bundle) {
        return bundle.getInt("authenticators_allowed");
    }
    
    static int getCredentialType(final Context context, int keyguardStoredPasswordQuality) {
        keyguardStoredPasswordQuality = new LockPatternUtils(context).getKeyguardStoredPasswordQuality(keyguardStoredPasswordQuality);
        if (keyguardStoredPasswordQuality == 65536) {
            return 2;
        }
        if (keyguardStoredPasswordQuality != 131072 && keyguardStoredPasswordQuality != 196608) {
            return 3;
        }
        return 1;
    }
    
    static boolean isBiometricAllowed(final Bundle bundle) {
        return (getAuthenticators(bundle) & 0xFF) != 0x0;
    }
    
    static boolean isDeviceCredentialAllowed(final Bundle bundle) {
        return (getAuthenticators(bundle) & 0x8000) != 0x0;
    }
    
    static boolean isManagedProfile(final Context context, final int n) {
        return ((UserManager)context.getSystemService((Class)UserManager.class)).isManagedProfile(n);
    }
    
    static void notifyAccessibilityContentChanged(final AccessibilityManager accessibilityManager, final ViewGroup viewGroup) {
        if (!accessibilityManager.isEnabled()) {
            return;
        }
        final AccessibilityEvent obtain = AccessibilityEvent.obtain();
        obtain.setEventType(2048);
        obtain.setContentChangeTypes(1);
        viewGroup.sendAccessibilityEventUnchecked(obtain);
        viewGroup.notifySubtreeAccessibilityStateChanged((View)viewGroup, (View)viewGroup, 1);
    }
}
