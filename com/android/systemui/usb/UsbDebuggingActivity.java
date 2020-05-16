// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.usb;

import android.view.WindowManager$LayoutParams;
import com.android.internal.app.AlertController$AlertParams;
import android.content.Intent;
import android.view.Window;
import android.view.View$OnTouchListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
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
import android.widget.CheckBox;
import android.content.DialogInterface$OnClickListener;
import com.android.internal.app.AlertActivity;

public class UsbDebuggingActivity extends AlertActivity implements DialogInterface$OnClickListener
{
    private CheckBox mAlwaysAllow;
    private String mKey;
    
    public void onClick(final DialogInterface dialogInterface, int n) {
        boolean b = true;
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
                interface1.allowDebugging(b, this.mKey);
            }
            else {
                interface1.denyDebugging();
            }
        }
        catch (Exception ex) {
            Log.e("UsbDebuggingActivity", "Unable to notify Usb service", (Throwable)ex);
        }
        this.finish();
    }
    
    public void onCreate(final Bundle bundle) {
        final Window window = this.getWindow();
        window.addSystemFlags(524288);
        window.setType(2008);
        super.onCreate(bundle);
        final Intent intent = this.getIntent();
        final String stringExtra = intent.getStringExtra("fingerprints");
        final String stringExtra2 = intent.getStringExtra("key");
        this.mKey = stringExtra2;
        if (stringExtra != null && stringExtra2 != null) {
            final AlertController$AlertParams mAlertParams = super.mAlertParams;
            mAlertParams.mTitle = this.getString(R$string.usb_debugging_title);
            mAlertParams.mMessage = this.getString(R$string.usb_debugging_message, new Object[] { stringExtra });
            mAlertParams.mPositiveButtonText = this.getString(R$string.usb_debugging_allow);
            mAlertParams.mNegativeButtonText = this.getString(17039360);
            mAlertParams.mPositiveButtonListener = (DialogInterface$OnClickListener)this;
            mAlertParams.mNegativeButtonListener = (DialogInterface$OnClickListener)this;
            final View inflate = LayoutInflater.from(mAlertParams.mContext).inflate(17367090, (ViewGroup)null);
            (this.mAlwaysAllow = (CheckBox)inflate.findViewById(16908745)).setText((CharSequence)this.getString(R$string.usb_debugging_always));
            mAlertParams.mView = inflate;
            window.setCloseOnTouchOutside(false);
            this.setupAlert();
            super.mAlert.getButton(-1).setOnTouchListener((View$OnTouchListener)_$$Lambda$UsbDebuggingActivity$XWt__qGCtWBJlTLnAvCSF7AuSg8.INSTANCE);
            return;
        }
        this.finish();
    }
    
    public void onWindowAttributesChanged(final WindowManager$LayoutParams windowManager$LayoutParams) {
        super.onWindowAttributesChanged(windowManager$LayoutParams);
    }
}
