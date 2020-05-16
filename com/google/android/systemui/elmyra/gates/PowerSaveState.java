// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import android.content.IntentFilter;
import com.android.systemui.Dependency;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.os.PowerManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.internal.annotations.GuardedBy;

public class PowerSaveState extends Gate
{
    @GuardedBy({ "mLock" })
    private boolean mBatterySaverEnabled;
    private BroadcastDispatcher mBroadcastDispatcher;
    @GuardedBy({ "mLock" })
    private boolean mIsDeviceInteractive;
    private final Object mLock;
    private final PowerManager mPowerManager;
    private final BroadcastReceiver mReceiver;
    
    public PowerSaveState(final Context context) {
        super(context);
        this.mLock = new Object();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                PowerSaveState.this.refreshStatus();
                PowerSaveState.this.notifyListener();
            }
        };
        this.mPowerManager = (PowerManager)context.getSystemService("power");
        this.mBroadcastDispatcher = Dependency.get(BroadcastDispatcher.class);
    }
    
    private void refreshStatus() {
        synchronized (this.mLock) {
            this.mBatterySaverEnabled = this.mPowerManager.getPowerSaveState(13).batterySaverEnabled;
            this.mIsDeviceInteractive = this.mPowerManager.isInteractive();
        }
    }
    
    private boolean shouldBlock() {
        synchronized (this.mLock) {
            return this.mBatterySaverEnabled && !this.mIsDeviceInteractive;
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
        this.mBroadcastDispatcher.registerReceiver(this.mReceiver, intentFilter);
        this.refreshStatus();
    }
    
    @Override
    protected void onDeactivate() {
        this.mBroadcastDispatcher.unregisterReceiver(this.mReceiver);
    }
}
