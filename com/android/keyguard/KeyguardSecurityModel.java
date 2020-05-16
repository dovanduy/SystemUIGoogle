// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import android.telephony.SubscriptionManager;
import com.android.systemui.Dependency;
import com.android.internal.widget.LockPatternUtils;
import android.content.Context;

public class KeyguardSecurityModel
{
    private final Context mContext;
    private final boolean mIsPukScreenAvailable;
    private LockPatternUtils mLockPatternUtils;
    
    KeyguardSecurityModel(final Context mContext) {
        this.mContext = mContext;
        this.mLockPatternUtils = new LockPatternUtils(mContext);
        this.mIsPukScreenAvailable = this.mContext.getResources().getBoolean(17891457);
    }
    
    public SecurityMode getSecurityMode(int intValue) {
        final KeyguardUpdateMonitor keyguardUpdateMonitor = Dependency.get(KeyguardUpdateMonitor.class);
        if (this.mIsPukScreenAvailable && SubscriptionManager.isValidSubscriptionId(keyguardUpdateMonitor.getNextSubIdForState(3))) {
            return SecurityMode.SimPuk;
        }
        if (SubscriptionManager.isValidSubscriptionId(keyguardUpdateMonitor.getNextSubIdForState(2))) {
            return SecurityMode.SimPin;
        }
        intValue = DejankUtils.whitelistIpcs((Supplier<Integer>)new _$$Lambda$KeyguardSecurityModel$wVA2_YbUv0Q_IYsNKsPxtYAIjp0(this, intValue));
        if (intValue == 0) {
            return SecurityMode.None;
        }
        if (intValue == 65536) {
            return SecurityMode.Pattern;
        }
        if (intValue == 131072 || intValue == 196608) {
            return SecurityMode.PIN;
        }
        if (intValue != 262144 && intValue != 327680 && intValue != 393216 && intValue != 524288) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unknown security quality:");
            sb.append(intValue);
            throw new IllegalStateException(sb.toString());
        }
        return SecurityMode.Password;
    }
    
    void setLockPatternUtils(final LockPatternUtils mLockPatternUtils) {
        this.mLockPatternUtils = mLockPatternUtils;
    }
    
    public enum SecurityMode
    {
        Invalid, 
        None, 
        PIN, 
        Password, 
        Pattern, 
        SimPin, 
        SimPuk;
    }
}
