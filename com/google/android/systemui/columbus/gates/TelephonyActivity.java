// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.telephony.PhoneStateListener;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.telephony.TelephonyManager;

public final class TelephonyActivity extends Gate
{
    private boolean isCallBlocked;
    private final TelephonyActivity$phoneStateListener.TelephonyActivity$phoneStateListener$1 phoneStateListener;
    private final TelephonyManager telephonyManager;
    
    public TelephonyActivity(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context);
        this.telephonyManager = (TelephonyManager)context.getSystemService("phone");
        this.phoneStateListener = new TelephonyActivity$phoneStateListener.TelephonyActivity$phoneStateListener$1(this);
    }
    
    private final boolean isCallBlocked(final Integer n) {
        if (n != null) {
            if (n == 2) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected boolean isBlocked() {
        return this.isCallBlocked;
    }
    
    @Override
    protected void onActivate() {
        final TelephonyManager telephonyManager = this.telephonyManager;
        Integer value;
        if (telephonyManager != null) {
            value = telephonyManager.getCallState();
        }
        else {
            value = null;
        }
        this.isCallBlocked = this.isCallBlocked(value);
        final TelephonyManager telephonyManager2 = this.telephonyManager;
        if (telephonyManager2 != null) {
            telephonyManager2.listen((PhoneStateListener)this.phoneStateListener, 32);
        }
    }
    
    @Override
    protected void onDeactivate() {
        final TelephonyManager telephonyManager = this.telephonyManager;
        if (telephonyManager != null) {
            telephonyManager.listen((PhoneStateListener)this.phoneStateListener, 0);
        }
    }
}
