// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import kotlin.jvm.internal.Intrinsics;
import android.os.Handler;
import android.content.Context;

public final class ChargingState extends TransientGate
{
    private final long gateDuration;
    private final ChargingState$powerReceiver.ChargingState$powerReceiver$1 powerReceiver;
    
    public ChargingState(final Context context, final Handler handler, final long gateDuration) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        super(context, handler);
        this.gateDuration = gateDuration;
        this.powerReceiver = new ChargingState$powerReceiver.ChargingState$powerReceiver$1(this);
    }
    
    @Override
    protected void onActivate() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        this.getContext().registerReceiver((BroadcastReceiver)this.powerReceiver, intentFilter);
    }
    
    @Override
    protected void onDeactivate() {
        this.getContext().unregisterReceiver((BroadcastReceiver)this.powerReceiver);
    }
}
