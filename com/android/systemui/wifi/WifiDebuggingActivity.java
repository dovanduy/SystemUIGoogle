// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.wifi;

import android.net.wifi.WifiInfo;
import android.net.NetworkInfo;
import android.content.Context;
import android.view.WindowManager$LayoutParams;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import com.android.internal.app.AlertController$AlertParams;
import android.content.Intent;
import android.view.Window;
import android.view.View$OnTouchListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.app.Activity;
import android.os.Bundle;
import android.debug.IAdbManager;
import android.util.Log;
import android.debug.IAdbManager$Stub;
import android.os.ServiceManager;
import android.content.DialogInterface;
import android.widget.Toast;
import com.android.systemui.R$string;
import android.util.EventLog;
import android.view.MotionEvent;
import android.view.View;
import android.net.wifi.WifiManager;
import android.widget.CheckBox;
import android.content.DialogInterface$OnClickListener;
import com.android.internal.app.AlertActivity;

public class WifiDebuggingActivity extends AlertActivity implements DialogInterface$OnClickListener
{
    private CheckBox mAlwaysAllow;
    private String mBssid;
    private boolean mClicked;
    private WifiChangeReceiver mWifiChangeReceiver;
    private WifiManager mWifiManager;
    
    public WifiDebuggingActivity() {
        this.mClicked = false;
    }
    
    public void onClick(final DialogInterface dialogInterface, int n) {
        boolean b = true;
        this.mClicked = true;
        if (n == -1) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0 || !this.mAlwaysAllow.isChecked()) {
            b = false;
        }
        try {
            final IAdbManager interface1 = IAdbManager$Stub.asInterface(ServiceManager.getService("adb"));
            if (n != 0) {
                interface1.allowWirelessDebugging(b, this.mBssid);
            }
            else {
                interface1.denyWirelessDebugging();
            }
        }
        catch (Exception ex) {
            Log.e("WifiDebuggingActivity", "Unable to notify Adb service", (Throwable)ex);
        }
        this.finish();
    }
    
    public void onCreate(final Bundle bundle) {
        final Window window = this.getWindow();
        window.addSystemFlags(524288);
        window.setType(2008);
        super.onCreate(bundle);
        this.mWifiManager = (WifiManager)this.getSystemService("wifi");
        this.mWifiChangeReceiver = new WifiChangeReceiver((Activity)this);
        final Intent intent = this.getIntent();
        final String stringExtra = intent.getStringExtra("ssid");
        final String stringExtra2 = intent.getStringExtra("bssid");
        this.mBssid = stringExtra2;
        if (stringExtra != null && stringExtra2 != null) {
            final AlertController$AlertParams mAlertParams = super.mAlertParams;
            mAlertParams.mTitle = this.getString(R$string.wifi_debugging_title);
            mAlertParams.mMessage = this.getString(R$string.wifi_debugging_message, new Object[] { stringExtra, this.mBssid });
            mAlertParams.mPositiveButtonText = this.getString(R$string.wifi_debugging_allow);
            mAlertParams.mNegativeButtonText = this.getString(17039360);
            mAlertParams.mPositiveButtonListener = (DialogInterface$OnClickListener)this;
            mAlertParams.mNegativeButtonListener = (DialogInterface$OnClickListener)this;
            final View inflate = LayoutInflater.from(mAlertParams.mContext).inflate(17367090, (ViewGroup)null);
            (this.mAlwaysAllow = (CheckBox)inflate.findViewById(16908745)).setText((CharSequence)this.getString(R$string.wifi_debugging_always));
            mAlertParams.mView = inflate;
            window.setCloseOnTouchOutside(false);
            this.setupAlert();
            super.mAlert.getButton(-1).setOnTouchListener((View$OnTouchListener)_$$Lambda$WifiDebuggingActivity$l4yv2jJ1InA__zRAoj9L6yVjZ_M.INSTANCE);
            return;
        }
        this.finish();
    }
    
    protected void onDestroy() {
        super.onDestroy();
        if (!this.mClicked) {
            try {
                IAdbManager$Stub.asInterface(ServiceManager.getService("adb")).denyWirelessDebugging();
            }
            catch (Exception ex) {
                Log.e("WifiDebuggingActivity", "Unable to notify Adb service", (Throwable)ex);
            }
        }
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
    
    public void onWindowAttributesChanged(final WindowManager$LayoutParams windowManager$LayoutParams) {
        super.onWindowAttributesChanged(windowManager$LayoutParams);
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
                    final WifiInfo connectionInfo = WifiDebuggingActivity.this.mWifiManager.getConnectionInfo();
                    if (connectionInfo != null && connectionInfo.getNetworkId() != -1) {
                        final String bssid = connectionInfo.getBSSID();
                        if (bssid == null || bssid.isEmpty()) {
                            this.mActivity.finish();
                            return;
                        }
                        if (!bssid.equals(WifiDebuggingActivity.this.mBssid)) {
                            this.mActivity.finish();
                        }
                    }
                    else {
                        this.mActivity.finish();
                    }
                }
            }
        }
    }
}
