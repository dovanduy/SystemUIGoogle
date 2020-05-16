// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.usb;

import com.android.internal.app.AlertController$AlertParams;
import com.android.systemui.R$string;
import android.os.Bundle;
import android.content.ActivityNotFoundException;
import android.util.Log;
import android.os.UserHandle;
import android.content.Intent;
import android.content.DialogInterface;
import android.net.Uri;
import android.hardware.usb.UsbAccessory;
import android.content.DialogInterface$OnClickListener;
import com.android.internal.app.AlertActivity;

public class UsbAccessoryUriActivity extends AlertActivity implements DialogInterface$OnClickListener
{
    private UsbAccessory mAccessory;
    private Uri mUri;
    
    public void onClick(final DialogInterface dialogInterface, final int n) {
        if (n == -1) {
            final Intent intent = new Intent("android.intent.action.VIEW", this.mUri);
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.addFlags(268435456);
            try {
                this.startActivityAsUser(intent, UserHandle.CURRENT);
            }
            catch (ActivityNotFoundException ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("startActivity failed for ");
                sb.append(this.mUri);
                Log.e("UsbAccessoryUriActivity", sb.toString());
            }
        }
        this.finish();
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final Intent intent = this.getIntent();
        this.mAccessory = (UsbAccessory)intent.getParcelableExtra("accessory");
        final String stringExtra = intent.getStringExtra("uri");
        Uri parse;
        if (stringExtra == null) {
            parse = null;
        }
        else {
            parse = Uri.parse(stringExtra);
        }
        this.mUri = parse;
        if (parse == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("could not parse Uri ");
            sb.append(stringExtra);
            Log.e("UsbAccessoryUriActivity", sb.toString());
            this.finish();
            return;
        }
        final String scheme = parse.getScheme();
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Uri not http or https: ");
            sb2.append(this.mUri);
            Log.e("UsbAccessoryUriActivity", sb2.toString());
            this.finish();
            return;
        }
        final AlertController$AlertParams mAlertParams = super.mAlertParams;
        final String description = this.mAccessory.getDescription();
        if ((mAlertParams.mTitle = description) == null || description.length() == 0) {
            mAlertParams.mTitle = this.getString(R$string.title_usb_accessory);
        }
        mAlertParams.mMessage = this.getString(R$string.usb_accessory_uri_prompt, new Object[] { this.mUri });
        mAlertParams.mPositiveButtonText = this.getString(R$string.label_view);
        mAlertParams.mNegativeButtonText = this.getString(17039360);
        mAlertParams.mPositiveButtonListener = (DialogInterface$OnClickListener)this;
        ((AlertActivity)(mAlertParams.mNegativeButtonListener = (DialogInterface$OnClickListener)this)).setupAlert();
    }
}
