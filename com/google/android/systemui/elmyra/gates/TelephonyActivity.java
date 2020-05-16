// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;

public class TelephonyActivity extends Gate
{
    private boolean mIsCallBlocked;
    private final PhoneStateListener mPhoneStateListener;
    private final TelephonyManager mTelephonyManager;
    
    public TelephonyActivity(final Context context) {
        super(context);
        this.mPhoneStateListener = new PhoneStateListener() {
            public void onCallStateChanged(final int n, final String s) {
                final boolean access$000 = TelephonyActivity.this.isCallBlocked(n);
                if (access$000 != TelephonyActivity.this.mIsCallBlocked) {
                    TelephonyActivity.this.mIsCallBlocked = access$000;
                    TelephonyActivity.this.notifyListener();
                }
            }
        };
        this.mTelephonyManager = (TelephonyManager)context.getSystemService("phone");
    }
    
    private boolean isCallBlocked(final int n) {
        return n == 2;
    }
    
    @Override
    protected boolean isBlocked() {
        return this.mIsCallBlocked;
    }
    
    @Override
    protected void onActivate() {
        this.mIsCallBlocked = this.isCallBlocked(this.mTelephonyManager.getCallState());
        this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
    }
    
    @Override
    protected void onDeactivate() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }
}
