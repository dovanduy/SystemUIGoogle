// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.List;
import android.text.TextUtils;
import android.telephony.SubscriptionInfo;
import android.provider.Settings$Global;
import android.net.ConnectivityManager;
import android.content.IntentFilter;
import com.android.systemui.Dependency;
import android.content.Intent;
import android.util.AttributeSet;
import android.content.Context;
import android.content.BroadcastReceiver;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.widget.TextView;

public class EmergencyCryptkeeperText extends TextView
{
    private final KeyguardUpdateMonitorCallback mCallback;
    private KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final BroadcastReceiver mReceiver;
    
    public EmergencyCryptkeeperText(final Context context, final AttributeSet set) {
        super(context, set);
        this.mCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onPhoneStateChanged(final int n) {
                EmergencyCryptkeeperText.this.update();
            }
            
            @Override
            public void onRefreshCarrierInfo() {
                EmergencyCryptkeeperText.this.update();
            }
        };
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("android.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
                    EmergencyCryptkeeperText.this.update();
                }
            }
        };
        this.setVisibility(8);
    }
    
    private boolean iccCardExist(final int n) {
        return n == 2 || n == 3 || n == 4 || n == 5 || n == 6 || n == 7 || n == 8 || n == 9 || n == 10;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        (this.mKeyguardUpdateMonitor = Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mCallback);
        this.getContext().registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.AIRPLANE_MODE"));
        this.update();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final KeyguardUpdateMonitor mKeyguardUpdateMonitor = this.mKeyguardUpdateMonitor;
        if (mKeyguardUpdateMonitor != null) {
            mKeyguardUpdateMonitor.removeCallback(this.mCallback);
        }
        this.getContext().unregisterReceiver(this.mReceiver);
    }
    
    public void update() {
        final ConnectivityManager from = ConnectivityManager.from(super.mContext);
        final int n = 0;
        final boolean networkSupported = from.isNetworkSupported(0);
        final int int1 = Settings$Global.getInt(super.mContext.getContentResolver(), "airplane_mode_on", 0);
        int n2 = 1;
        final boolean b = int1 == 1;
        if (networkSupported && !b) {
            final List<SubscriptionInfo> filteredSubscriptionInfo = this.mKeyguardUpdateMonitor.getFilteredSubscriptionInfo(false);
            final int size = filteredSubscriptionInfo.size();
            int i = 0;
            CharSequence text = null;
            while (i < size) {
                final int simState = this.mKeyguardUpdateMonitor.getSimState(filteredSubscriptionInfo.get(i).getSubscriptionId());
                final CharSequence carrierName = filteredSubscriptionInfo.get(i).getCarrierName();
                int n3 = n2;
                CharSequence charSequence = text;
                if (this.iccCardExist(simState)) {
                    n3 = n2;
                    charSequence = text;
                    if (!TextUtils.isEmpty(carrierName)) {
                        n3 = 0;
                        charSequence = carrierName;
                    }
                }
                ++i;
                n2 = n3;
                text = charSequence;
            }
            if (n2 != 0) {
                if (size != 0) {
                    text = filteredSubscriptionInfo.get(0).getCarrierName();
                }
                else {
                    text = this.getContext().getText(17040085);
                    final Intent registerReceiver = this.getContext().registerReceiver((BroadcastReceiver)null, new IntentFilter("android.telephony.action.SERVICE_PROVIDERS_UPDATED"));
                    if (registerReceiver != null) {
                        text = registerReceiver.getStringExtra("android.telephony.extra.PLMN");
                    }
                }
            }
            this.setText(text);
            int visibility = n;
            if (TextUtils.isEmpty(text)) {
                visibility = 8;
            }
            this.setVisibility(visibility);
            return;
        }
        this.setText((CharSequence)null);
        this.setVisibility(8);
    }
}
