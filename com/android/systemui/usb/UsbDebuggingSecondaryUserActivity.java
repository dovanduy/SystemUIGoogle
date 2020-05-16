// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.usb;

import com.android.internal.app.AlertController$AlertParams;
import com.android.systemui.R$string;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import com.android.internal.app.AlertActivity;

public class UsbDebuggingSecondaryUserActivity extends AlertActivity implements DialogInterface$OnClickListener
{
    public void onClick(final DialogInterface dialogInterface, final int n) {
        this.finish();
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final AlertController$AlertParams mAlertParams = super.mAlertParams;
        mAlertParams.mTitle = this.getString(R$string.usb_debugging_secondary_user_title);
        mAlertParams.mMessage = this.getString(R$string.usb_debugging_secondary_user_message);
        mAlertParams.mPositiveButtonText = this.getString(17039370);
        ((AlertActivity)(mAlertParams.mPositiveButtonListener = (DialogInterface$OnClickListener)this)).setupAlert();
    }
}
