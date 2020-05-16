// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard.dagger;

import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.util.DeviceConfigProxy;
import java.util.concurrent.Executor;
import android.app.trust.TrustManager;
import android.os.PowerManager;
import com.android.systemui.dump.DumpManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.keyguard.KeyguardViewController;
import dagger.Lazy;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.plugins.FalsingManager;
import android.content.Context;

public class KeyguardModule
{
    public static KeyguardViewMediator newKeyguardViewMediator(final Context context, final FalsingManager falsingManager, final LockPatternUtils lockPatternUtils, final BroadcastDispatcher broadcastDispatcher, final NotificationShadeWindowController notificationShadeWindowController, final Lazy<KeyguardViewController> lazy, final DismissCallbackRegistry dismissCallbackRegistry, final KeyguardUpdateMonitor keyguardUpdateMonitor, final DumpManager dumpManager, final PowerManager powerManager, final TrustManager trustManager, final Executor executor, final DeviceConfigProxy deviceConfigProxy, final NavigationModeController navigationModeController) {
        return new KeyguardViewMediator(context, falsingManager, lockPatternUtils, broadcastDispatcher, notificationShadeWindowController, lazy, dismissCallbackRegistry, keyguardUpdateMonitor, dumpManager, executor, powerManager, trustManager, deviceConfigProxy, navigationModeController);
    }
}
