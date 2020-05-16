// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import kotlin.jvm.internal.Intrinsics;
import android.os.Handler;
import android.content.Context;

public final class UsbState extends TransientGate
{
    private final long gateDuration;
    private boolean usbConnected;
    private final UsbState$usbReceiver.UsbState$usbReceiver$1 usbReceiver;
    
    public UsbState(final Context context, final Handler handler, final long gateDuration) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        super(context, handler);
        this.gateDuration = gateDuration;
        this.usbReceiver = new UsbState$usbReceiver.UsbState$usbReceiver$1(this);
    }
    
    @Override
    protected void onActivate() {
        final IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_STATE");
        final Intent registerReceiver = this.getContext().registerReceiver((BroadcastReceiver)null, intentFilter);
        if (registerReceiver != null) {
            this.usbConnected = registerReceiver.getBooleanExtra("connected", false);
        }
        this.getContext().registerReceiver((BroadcastReceiver)this.usbReceiver, intentFilter);
    }
    
    @Override
    protected void onDeactivate() {
        this.getContext().unregisterReceiver((BroadcastReceiver)this.usbReceiver);
    }
}
