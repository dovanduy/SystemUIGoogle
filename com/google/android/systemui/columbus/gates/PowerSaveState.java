// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.os.PowerManager;

public final class PowerSaveState extends Gate
{
    private boolean batterySaverEnabled;
    private boolean isDeviceInteractive;
    private final Object lock;
    private final PowerManager powerManager;
    private final PowerSaveState$receiver.PowerSaveState$receiver$1 receiver;
    
    public PowerSaveState(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context);
        this.lock = new Object();
        this.powerManager = (PowerManager)context.getSystemService("power");
        this.receiver = new PowerSaveState$receiver.PowerSaveState$receiver$1(this);
    }
    
    private final void refreshStatus() {
        synchronized (this.lock) {
            final PowerManager powerManager = this.powerManager;
            final boolean b = false;
            boolean batterySaverEnabled = false;
            Label_0046: {
                if (powerManager != null) {
                    final android.os.PowerSaveState powerSaveState = powerManager.getPowerSaveState(13);
                    if (powerSaveState != null && powerSaveState.batterySaverEnabled) {
                        batterySaverEnabled = true;
                        break Label_0046;
                    }
                }
                batterySaverEnabled = false;
            }
            this.batterySaverEnabled = batterySaverEnabled;
            final PowerManager powerManager2 = this.powerManager;
            boolean isDeviceInteractive = b;
            if (powerManager2 != null) {
                isDeviceInteractive = b;
                if (powerManager2.isInteractive()) {
                    isDeviceInteractive = true;
                }
            }
            this.isDeviceInteractive = isDeviceInteractive;
            final Unit instance = Unit.INSTANCE;
        }
    }
    
    private final boolean shouldBlock() {
        synchronized (this.lock) {
            return this.batterySaverEnabled && !this.isDeviceInteractive;
        }
    }
    
    @Override
    protected boolean isBlocked() {
        return this.shouldBlock();
    }
    
    @Override
    protected void onActivate() {
        final IntentFilter intentFilter = new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        this.getContext().registerReceiver((BroadcastReceiver)this.receiver, intentFilter);
        this.refreshStatus();
    }
    
    @Override
    protected void onDeactivate() {
        this.getContext().unregisterReceiver((BroadcastReceiver)this.receiver);
    }
}
