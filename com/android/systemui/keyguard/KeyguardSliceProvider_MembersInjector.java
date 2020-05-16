// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.DozeParameters;
import android.content.ContentResolver;
import android.app.AlarmManager;

public final class KeyguardSliceProvider_MembersInjector implements Object<KeyguardSliceProvider>
{
    public static void injectMAlarmManager(final KeyguardSliceProvider keyguardSliceProvider, final AlarmManager mAlarmManager) {
        keyguardSliceProvider.mAlarmManager = mAlarmManager;
    }
    
    public static void injectMContentResolver(final KeyguardSliceProvider keyguardSliceProvider, final ContentResolver mContentResolver) {
        keyguardSliceProvider.mContentResolver = mContentResolver;
    }
    
    public static void injectMDozeParameters(final KeyguardSliceProvider keyguardSliceProvider, final DozeParameters mDozeParameters) {
        keyguardSliceProvider.mDozeParameters = mDozeParameters;
    }
    
    public static void injectMKeyguardBypassController(final KeyguardSliceProvider keyguardSliceProvider, final KeyguardBypassController mKeyguardBypassController) {
        keyguardSliceProvider.mKeyguardBypassController = mKeyguardBypassController;
    }
    
    public static void injectMMediaManager(final KeyguardSliceProvider keyguardSliceProvider, final NotificationMediaManager mMediaManager) {
        keyguardSliceProvider.mMediaManager = mMediaManager;
    }
    
    public static void injectMNextAlarmController(final KeyguardSliceProvider keyguardSliceProvider, final NextAlarmController mNextAlarmController) {
        keyguardSliceProvider.mNextAlarmController = mNextAlarmController;
    }
    
    public static void injectMStatusBarStateController(final KeyguardSliceProvider keyguardSliceProvider, final StatusBarStateController mStatusBarStateController) {
        keyguardSliceProvider.mStatusBarStateController = mStatusBarStateController;
    }
    
    public static void injectMZenModeController(final KeyguardSliceProvider keyguardSliceProvider, final ZenModeController mZenModeController) {
        keyguardSliceProvider.mZenModeController = mZenModeController;
    }
}
