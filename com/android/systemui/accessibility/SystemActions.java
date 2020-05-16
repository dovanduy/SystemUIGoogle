// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.accessibility;

import android.content.Intent;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.app.RemoteAction;
import android.graphics.drawable.Icon;
import android.content.BroadcastReceiver;
import android.view.InputEvent;
import android.hardware.input.InputManager;
import android.view.KeyEvent;
import java.util.function.Consumer;
import android.os.Handler;
import android.os.Looper;
import com.android.internal.util.ScreenshotHelper;
import android.view.IWindowManager;
import android.os.RemoteException;
import android.util.Log;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.PowerManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.Dependency;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.recents.Recents;
import com.android.systemui.SystemUI;

public class SystemActions extends SystemUI
{
    private SystemActionsBroadcastReceiver mReceiver;
    private Recents mRecents;
    private StatusBar mStatusBar;
    
    public SystemActions(final Context context) {
        super(context);
        this.mRecents = Dependency.get(Recents.class);
        this.mStatusBar = Dependency.get(StatusBar.class);
        this.mReceiver = new SystemActionsBroadcastReceiver();
    }
    
    private void handleAccessibilityMenu() {
        AccessibilityManager.getInstance(super.mContext).notifyAccessibilityButtonClicked(0);
    }
    
    private void handleBack() {
        this.sendDownAndUpKeyEvents(4);
    }
    
    private void handleHome() {
        this.sendDownAndUpKeyEvents(3);
    }
    
    private void handleLockScreen() {
        final IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
        ((PowerManager)super.mContext.getSystemService((Class)PowerManager.class)).goToSleep(SystemClock.uptimeMillis(), 7, 0);
        try {
            windowManagerService.lockNow((Bundle)null);
        }
        catch (RemoteException ex) {
            Log.e("SystemActions", "failed to lock screen.");
        }
    }
    
    private void handleNotifications() {
        this.mStatusBar.animateExpandNotificationsPanel();
    }
    
    private void handlePowerDialog() {
        final IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
        try {
            windowManagerService.showGlobalActions();
        }
        catch (RemoteException ex) {
            Log.e("SystemActions", "failed to display power dialog.");
        }
    }
    
    private void handleQuickSettings() {
        this.mStatusBar.animateExpandSettingsPanel(null);
    }
    
    private void handleRecents() {
        this.mRecents.toggleRecentApps();
    }
    
    private void handleTakeScreenshot() {
        new ScreenshotHelper(super.mContext).takeScreenshot(1, true, true, new Handler(Looper.getMainLooper()), (Consumer)null);
    }
    
    private void handleToggleSplitScreen() {
        this.mStatusBar.toggleSplitScreen();
    }
    
    private void sendDownAndUpKeyEvents(final int n) {
        final long uptimeMillis = SystemClock.uptimeMillis();
        this.sendKeyEventIdentityCleared(n, 0, uptimeMillis, uptimeMillis);
        this.sendKeyEventIdentityCleared(n, 1, uptimeMillis, SystemClock.uptimeMillis());
    }
    
    private void sendKeyEventIdentityCleared(final int n, final int n2, final long n3, final long n4) {
        final KeyEvent obtain = KeyEvent.obtain(n3, n4, n2, n, 0, 0, -1, 0, 8, 257, (String)null);
        InputManager.getInstance().injectInputEvent((InputEvent)obtain, 0);
        obtain.recycle();
    }
    
    @Override
    public void start() {
        final Context mContext = super.mContext;
        final SystemActionsBroadcastReceiver mReceiver = this.mReceiver;
        mContext.registerReceiverForAllUsers((BroadcastReceiver)mReceiver, mReceiver.createIntentFilter(), (String)null, (Handler)null);
        final RemoteAction remoteAction = new RemoteAction(Icon.createWithResource(super.mContext, 17301684), (CharSequence)super.mContext.getString(17039583), (CharSequence)super.mContext.getString(17039583), this.mReceiver.createPendingIntent(super.mContext, "SYSTEM_ACTION_BACK"));
        final RemoteAction remoteAction2 = new RemoteAction(Icon.createWithResource(super.mContext, 17301684), (CharSequence)super.mContext.getString(17039584), (CharSequence)super.mContext.getString(17039584), this.mReceiver.createPendingIntent(super.mContext, "SYSTEM_ACTION_HOME"));
        final RemoteAction remoteAction3 = new RemoteAction(Icon.createWithResource(super.mContext, 17301684), (CharSequence)super.mContext.getString(17039589), (CharSequence)super.mContext.getString(17039589), this.mReceiver.createPendingIntent(super.mContext, "SYSTEM_ACTION_RECENTS"));
        final RemoteAction remoteAction4 = new RemoteAction(Icon.createWithResource(super.mContext, 17301684), (CharSequence)super.mContext.getString(17039586), (CharSequence)super.mContext.getString(17039586), this.mReceiver.createPendingIntent(super.mContext, "SYSTEM_ACTION_NOTIFICATIONS"));
        final RemoteAction remoteAction5 = new RemoteAction(Icon.createWithResource(super.mContext, 17301684), (CharSequence)super.mContext.getString(17039588), (CharSequence)super.mContext.getString(17039588), this.mReceiver.createPendingIntent(super.mContext, "SYSTEM_ACTION_QUICK_SETTINGS"));
        final RemoteAction remoteAction6 = new RemoteAction(Icon.createWithResource(super.mContext, 17301684), (CharSequence)super.mContext.getString(17039587), (CharSequence)super.mContext.getString(17039587), this.mReceiver.createPendingIntent(super.mContext, "SYSTEM_ACTION_POWER_DIALOG"));
        final RemoteAction remoteAction7 = new RemoteAction(Icon.createWithResource(super.mContext, 17301684), (CharSequence)super.mContext.getString(17039591), (CharSequence)super.mContext.getString(17039591), this.mReceiver.createPendingIntent(super.mContext, "SYSTEM_ACTION_TOGGLE_SPLIT_SCREEN"));
        final RemoteAction remoteAction8 = new RemoteAction(Icon.createWithResource(super.mContext, 17301684), (CharSequence)super.mContext.getString(17039585), (CharSequence)super.mContext.getString(17039585), this.mReceiver.createPendingIntent(super.mContext, "SYSTEM_ACTION_LOCK_SCREEN"));
        final RemoteAction remoteAction9 = new RemoteAction(Icon.createWithResource(super.mContext, 17301684), (CharSequence)super.mContext.getString(17039590), (CharSequence)super.mContext.getString(17039590), this.mReceiver.createPendingIntent(super.mContext, "SYSTEM_ACTION_TAKE_SCREENSHOT"));
        final RemoteAction remoteAction10 = new RemoteAction(Icon.createWithResource(super.mContext, 17301684), (CharSequence)super.mContext.getString(17039582), (CharSequence)super.mContext.getString(17039582), this.mReceiver.createPendingIntent(super.mContext, "SYSTEM_ACTION_ACCESSIBILITY_MENU"));
        final AccessibilityManager accessibilityManager = (AccessibilityManager)super.mContext.getSystemService("accessibility");
        accessibilityManager.registerSystemAction(remoteAction, 1);
        accessibilityManager.registerSystemAction(remoteAction2, 2);
        accessibilityManager.registerSystemAction(remoteAction3, 3);
        accessibilityManager.registerSystemAction(remoteAction4, 4);
        accessibilityManager.registerSystemAction(remoteAction5, 5);
        accessibilityManager.registerSystemAction(remoteAction6, 6);
        accessibilityManager.registerSystemAction(remoteAction7, 7);
        accessibilityManager.registerSystemAction(remoteAction8, 8);
        accessibilityManager.registerSystemAction(remoteAction9, 9);
        accessibilityManager.registerSystemAction(remoteAction10, 10);
    }
    
    private class SystemActionsBroadcastReceiver extends BroadcastReceiver
    {
        private IntentFilter createIntentFilter() {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("SYSTEM_ACTION_BACK");
            intentFilter.addAction("SYSTEM_ACTION_HOME");
            intentFilter.addAction("SYSTEM_ACTION_RECENTS");
            intentFilter.addAction("SYSTEM_ACTION_NOTIFICATIONS");
            intentFilter.addAction("SYSTEM_ACTION_QUICK_SETTINGS");
            intentFilter.addAction("SYSTEM_ACTION_POWER_DIALOG");
            intentFilter.addAction("SYSTEM_ACTION_TOGGLE_SPLIT_SCREEN");
            intentFilter.addAction("SYSTEM_ACTION_LOCK_SCREEN");
            intentFilter.addAction("SYSTEM_ACTION_TAKE_SCREENSHOT");
            intentFilter.addAction("SYSTEM_ACTION_ACCESSIBILITY_MENU");
            return intentFilter;
        }
        
        private PendingIntent createPendingIntent(final Context context, final String s) {
            int n = 0;
            Label_0245: {
                switch (s.hashCode()) {
                    case 1962121443: {
                        if (s.equals("SYSTEM_ACTION_TOGGLE_SPLIT_SCREEN")) {
                            n = 6;
                            break Label_0245;
                        }
                        break;
                    }
                    case 1668921710: {
                        if (s.equals("SYSTEM_ACTION_QUICK_SETTINGS")) {
                            n = 4;
                            break Label_0245;
                        }
                        break;
                    }
                    case 1579999269: {
                        if (s.equals("SYSTEM_ACTION_TAKE_SCREENSHOT")) {
                            n = 8;
                            break Label_0245;
                        }
                        break;
                    }
                    case 42571871: {
                        if (s.equals("SYSTEM_ACTION_RECENTS")) {
                            n = 2;
                            break Label_0245;
                        }
                        break;
                    }
                    case -153384569: {
                        if (s.equals("SYSTEM_ACTION_LOCK_SCREEN")) {
                            n = 7;
                            break Label_0245;
                        }
                        break;
                    }
                    case -535129457: {
                        if (s.equals("SYSTEM_ACTION_NOTIFICATIONS")) {
                            n = 3;
                            break Label_0245;
                        }
                        break;
                    }
                    case -720484549: {
                        if (s.equals("SYSTEM_ACTION_POWER_DIALOG")) {
                            n = 5;
                            break Label_0245;
                        }
                        break;
                    }
                    case -1103619272: {
                        if (s.equals("SYSTEM_ACTION_HOME")) {
                            n = 1;
                            break Label_0245;
                        }
                        break;
                    }
                    case -1103811776: {
                        if (s.equals("SYSTEM_ACTION_BACK")) {
                            n = 0;
                            break Label_0245;
                        }
                        break;
                    }
                    case -1173809047: {
                        if (s.equals("SYSTEM_ACTION_ACCESSIBILITY_MENU")) {
                            n = 9;
                            break Label_0245;
                        }
                        break;
                    }
                }
                n = -1;
            }
            switch (n) {
                default: {
                    return null;
                }
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9: {
                    return PendingIntent.getBroadcast(context, 0, new Intent(s), 0);
                }
            }
        }
        
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            int n = 0;
            Label_0249: {
                switch (action.hashCode()) {
                    case 1962121443: {
                        if (action.equals("SYSTEM_ACTION_TOGGLE_SPLIT_SCREEN")) {
                            n = 6;
                            break Label_0249;
                        }
                        break;
                    }
                    case 1668921710: {
                        if (action.equals("SYSTEM_ACTION_QUICK_SETTINGS")) {
                            n = 4;
                            break Label_0249;
                        }
                        break;
                    }
                    case 1579999269: {
                        if (action.equals("SYSTEM_ACTION_TAKE_SCREENSHOT")) {
                            n = 8;
                            break Label_0249;
                        }
                        break;
                    }
                    case 42571871: {
                        if (action.equals("SYSTEM_ACTION_RECENTS")) {
                            n = 2;
                            break Label_0249;
                        }
                        break;
                    }
                    case -153384569: {
                        if (action.equals("SYSTEM_ACTION_LOCK_SCREEN")) {
                            n = 7;
                            break Label_0249;
                        }
                        break;
                    }
                    case -535129457: {
                        if (action.equals("SYSTEM_ACTION_NOTIFICATIONS")) {
                            n = 3;
                            break Label_0249;
                        }
                        break;
                    }
                    case -720484549: {
                        if (action.equals("SYSTEM_ACTION_POWER_DIALOG")) {
                            n = 5;
                            break Label_0249;
                        }
                        break;
                    }
                    case -1103619272: {
                        if (action.equals("SYSTEM_ACTION_HOME")) {
                            n = 1;
                            break Label_0249;
                        }
                        break;
                    }
                    case -1103811776: {
                        if (action.equals("SYSTEM_ACTION_BACK")) {
                            n = 0;
                            break Label_0249;
                        }
                        break;
                    }
                    case -1173809047: {
                        if (action.equals("SYSTEM_ACTION_ACCESSIBILITY_MENU")) {
                            n = 9;
                            break Label_0249;
                        }
                        break;
                    }
                }
                n = -1;
            }
            switch (n) {
                case 9: {
                    SystemActions.this.handleAccessibilityMenu();
                    break;
                }
                case 8: {
                    SystemActions.this.handleTakeScreenshot();
                    break;
                }
                case 7: {
                    SystemActions.this.handleLockScreen();
                    break;
                }
                case 6: {
                    SystemActions.this.handleToggleSplitScreen();
                    break;
                }
                case 5: {
                    SystemActions.this.handlePowerDialog();
                    break;
                }
                case 4: {
                    SystemActions.this.handleQuickSettings();
                    break;
                }
                case 3: {
                    SystemActions.this.handleNotifications();
                    break;
                }
                case 2: {
                    SystemActions.this.handleRecents();
                    break;
                }
                case 1: {
                    SystemActions.this.handleHome();
                    break;
                }
                case 0: {
                    SystemActions.this.handleBack();
                    break;
                }
            }
        }
    }
}
