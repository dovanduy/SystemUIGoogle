// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.wifi;

import android.net.wifi.WifiInfo;
import android.net.NetworkInfo;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import com.android.internal.app.AlertController$AlertParams;
import com.android.systemui.R$string;
import android.app.Activity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.content.DialogInterface$OnClickListener;
import com.android.internal.app.AlertActivity;

public class WifiDebuggingSecondaryUserActivity extends AlertActivity implements DialogInterface$OnClickListener
{
    private WifiChangeReceiver mWifiChangeReceiver;
    private WifiManager mWifiManager;
    
    public void onClick(final DialogInterface dialogInterface, final int n) {
        this.finish();
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.mWifiManager = (WifiManager)this.getSystemService("wifi");
        this.mWifiChangeReceiver = new WifiChangeReceiver((Activity)this);
        final AlertController$AlertParams mAlertParams = super.mAlertParams;
        mAlertParams.mTitle = this.getString(R$string.wifi_debugging_secondary_user_title);
        mAlertParams.mMessage = this.getString(R$string.wifi_debugging_secondary_user_message);
        mAlertParams.mPositiveButtonText = this.getString(17039370);
        ((AlertActivity)(mAlertParams.mPositiveButtonListener = (DialogInterface$OnClickListener)this)).setupAlert();
    }
    
    public void onStart() {
        super.onStart();
        final IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.registerReceiver((BroadcastReceiver)this.mWifiChangeReceiver, intentFilter);
        this.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }
    
    protected void onStop() {
        final WifiChangeReceiver mWifiChangeReceiver = this.mWifiChangeReceiver;
        if (mWifiChangeReceiver != null) {
            this.unregisterReceiver((BroadcastReceiver)mWifiChangeReceiver);
        }
        super.onStop();
    }
    
    private class WifiChangeReceiver extends BroadcastReceiver
    {
        private final Activity mActivity;
        
        WifiChangeReceiver(final Activity mActivity) {
            this.mActivity = mActivity;
        }
        
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                if (intent.getIntExtra("wifi_state", 1) == 1) {
                    this.mActivity.finish();
                }
            }
            else if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                final NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra("networkInfo");
                if (networkInfo.getType() == 1) {
                    if (!networkInfo.isConnected()) {
                        this.mActivity.finish();
                        return;
                    }
                    final WifiInfo connectionInfo = WifiDebuggingSecondaryUserActivity.this.mWifiManager.getConnectionInfo();
                    if (connectionInfo == null || connectionInfo.getNetworkId() == -1) {
                        this.mActivity.finish();
                    }
                }
            }
        }
    }
}
