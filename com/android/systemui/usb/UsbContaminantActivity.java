// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.usb;

import android.view.WindowManager$LayoutParams;
import android.view.Window;
import com.android.systemui.R$id;
import android.hardware.usb.UsbManager;
import android.hardware.usb.ParcelableUsbPort;
import com.android.systemui.R$layout;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;
import com.android.systemui.R$string;
import android.view.View;
import android.hardware.usb.UsbPort;
import android.widget.TextView;
import android.view.View$OnClickListener;
import android.app.Activity;

public class UsbContaminantActivity extends Activity implements View$OnClickListener
{
    private TextView mEnableUsb;
    private TextView mGotIt;
    private TextView mLearnMore;
    private TextView mMessage;
    private TextView mTitle;
    private UsbPort mUsbPort;
    
    public void onClick(final View view) {
        if (view == this.mEnableUsb) {
            try {
                this.mUsbPort.enableContaminantDetection(false);
                Toast.makeText((Context)this, R$string.usb_port_enabled, 0).show();
            }
            catch (Exception ex) {
                Log.e("UsbContaminantActivity", "Unable to notify Usb service", (Throwable)ex);
            }
        }
        else if (view == this.mLearnMore) {
            final Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.HelpTrampoline");
            intent.putExtra("android.intent.extra.TEXT", "help_url_usb_contaminant_detected");
            this.startActivity(intent);
        }
        this.finish();
    }
    
    public void onCreate(final Bundle bundle) {
        final Window window = this.getWindow();
        window.addSystemFlags(524288);
        window.setType(2008);
        this.requestWindowFeature(1);
        super.onCreate(bundle);
        this.setContentView(R$layout.contaminant_dialog);
        this.mUsbPort = ((ParcelableUsbPort)this.getIntent().getParcelableExtra("port")).getUsbPort((UsbManager)this.getSystemService((Class)UsbManager.class));
        this.mLearnMore = (TextView)this.findViewById(R$id.learnMore);
        this.mEnableUsb = (TextView)this.findViewById(R$id.enableUsb);
        this.mGotIt = (TextView)this.findViewById(R$id.gotIt);
        this.mTitle = (TextView)this.findViewById(R$id.title);
        this.mMessage = (TextView)this.findViewById(R$id.message);
        this.mTitle.setText((CharSequence)this.getString(R$string.usb_contaminant_title));
        this.mMessage.setText((CharSequence)this.getString(R$string.usb_contaminant_message));
        this.mEnableUsb.setText((CharSequence)this.getString(R$string.usb_disable_contaminant_detection));
        this.mGotIt.setText((CharSequence)this.getString(R$string.got_it));
        this.mLearnMore.setText((CharSequence)this.getString(R$string.learn_more));
        this.mEnableUsb.setOnClickListener((View$OnClickListener)this);
        this.mGotIt.setOnClickListener((View$OnClickListener)this);
        this.mLearnMore.setOnClickListener((View$OnClickListener)this);
    }
    
    public void onWindowAttributesChanged(final WindowManager$LayoutParams windowManager$LayoutParams) {
        super.onWindowAttributesChanged(windowManager$LayoutParams);
    }
}
