// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.usb;

import android.content.BroadcastReceiver;
import android.view.View;
import com.android.internal.app.AlertController$AlertParams;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.content.PermissionChecker;
import android.app.Activity;
import com.android.systemui.R$string;
import android.os.Bundle;
import android.hardware.usb.IUsbManager;
import android.util.Log;
import android.content.ComponentName;
import android.os.Parcelable;
import android.content.Intent;
import android.os.UserHandle;
import android.hardware.usb.IUsbManager$Stub;
import android.os.ServiceManager;
import android.content.DialogInterface;
import android.widget.CompoundButton;
import android.content.pm.ResolveInfo;
import android.hardware.usb.UsbDevice;
import android.widget.TextView;
import android.widget.CheckBox;
import android.hardware.usb.UsbAccessory;
import android.widget.CompoundButton$OnCheckedChangeListener;
import android.content.DialogInterface$OnClickListener;
import com.android.internal.app.AlertActivity;

public class UsbConfirmActivity extends AlertActivity implements DialogInterface$OnClickListener, CompoundButton$OnCheckedChangeListener
{
    private UsbAccessory mAccessory;
    private CheckBox mAlwaysUse;
    private TextView mClearDefaultHint;
    private UsbDevice mDevice;
    private UsbDisconnectedReceiver mDisconnectedReceiver;
    private ResolveInfo mResolveInfo;
    
    public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
        final TextView mClearDefaultHint = this.mClearDefaultHint;
        if (mClearDefaultHint == null) {
            return;
        }
        if (b) {
            mClearDefaultHint.setVisibility(0);
        }
        else {
            mClearDefaultHint.setVisibility(8);
        }
    }
    
    public void onClick(final DialogInterface dialogInterface, int myUserId) {
        if (myUserId == -1) {
            try {
                final IUsbManager interface1 = IUsbManager$Stub.asInterface(ServiceManager.getService("usb"));
                final int uid = this.mResolveInfo.activityInfo.applicationInfo.uid;
                myUserId = UserHandle.myUserId();
                final boolean b = this.mAlwaysUse != null && this.mAlwaysUse.isChecked();
                final UsbDevice mDevice = this.mDevice;
                Intent intent = null;
                if (mDevice != null) {
                    intent = new Intent("android.hardware.usb.action.USB_DEVICE_ATTACHED");
                    intent.putExtra("device", (Parcelable)this.mDevice);
                    interface1.grantDevicePermission(this.mDevice, uid);
                    if (b) {
                        interface1.setDevicePackage(this.mDevice, this.mResolveInfo.activityInfo.packageName, myUserId);
                    }
                    else {
                        interface1.setDevicePackage(this.mDevice, (String)null, myUserId);
                    }
                }
                else if (this.mAccessory != null) {
                    intent = new Intent("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
                    intent.putExtra("accessory", (Parcelable)this.mAccessory);
                    interface1.grantAccessoryPermission(this.mAccessory, uid);
                    if (b) {
                        interface1.setAccessoryPackage(this.mAccessory, this.mResolveInfo.activityInfo.packageName, myUserId);
                    }
                    else {
                        interface1.setAccessoryPackage(this.mAccessory, (String)null, myUserId);
                    }
                }
                intent.addFlags(268435456);
                intent.setComponent(new ComponentName(this.mResolveInfo.activityInfo.packageName, this.mResolveInfo.activityInfo.name));
                this.startActivityAsUser(intent, new UserHandle(myUserId));
            }
            catch (Exception ex) {
                Log.e("UsbConfirmActivity", "Unable to start activity", (Throwable)ex);
            }
        }
        this.finish();
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final Intent intent = this.getIntent();
        this.mDevice = (UsbDevice)intent.getParcelableExtra("device");
        this.mAccessory = (UsbAccessory)intent.getParcelableExtra("accessory");
        this.mResolveInfo = (ResolveInfo)intent.getParcelableExtra("rinfo");
        final String stringExtra = intent.getStringExtra("android.hardware.usb.extra.PACKAGE");
        final String string = this.mResolveInfo.loadLabel(this.getPackageManager()).toString();
        final AlertController$AlertParams mAlertParams = super.mAlertParams;
        mAlertParams.mTitle = string;
        boolean b;
        if (this.mDevice == null) {
            mAlertParams.mMessage = this.getString(R$string.usb_accessory_confirm_prompt, new Object[] { string, this.mAccessory.getDescription() });
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity)this, this.mAccessory);
            b = false;
        }
        else {
            final boolean b2 = PermissionChecker.checkPermissionForPreflight((Context)this, "android.permission.RECORD_AUDIO", -1, intent.getIntExtra("android.intent.extra.UID", -1), stringExtra) == 0;
            b = (this.mDevice.getHasAudioCapture() && !b2);
            int n;
            if (b) {
                n = R$string.usb_device_confirm_prompt_warn;
            }
            else {
                n = R$string.usb_device_confirm_prompt;
            }
            mAlertParams.mMessage = this.getString(n, new Object[] { string, this.mDevice.getProductName() });
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity)this, this.mDevice);
        }
        mAlertParams.mPositiveButtonText = this.getString(17039370);
        mAlertParams.mNegativeButtonText = this.getString(17039360);
        mAlertParams.mPositiveButtonListener = (DialogInterface$OnClickListener)this;
        mAlertParams.mNegativeButtonListener = (DialogInterface$OnClickListener)this;
        if (!b) {
            final View inflate = ((LayoutInflater)this.getSystemService("layout_inflater")).inflate(17367090, (ViewGroup)null);
            mAlertParams.mView = inflate;
            final CheckBox mAlwaysUse = (CheckBox)inflate.findViewById(16908745);
            this.mAlwaysUse = mAlwaysUse;
            final UsbDevice mDevice = this.mDevice;
            if (mDevice == null) {
                mAlwaysUse.setText((CharSequence)this.getString(R$string.always_use_accessory, new Object[] { string, this.mAccessory.getDescription() }));
            }
            else {
                mAlwaysUse.setText((CharSequence)this.getString(R$string.always_use_device, new Object[] { string, mDevice.getProductName() }));
            }
            this.mAlwaysUse.setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)this);
            (this.mClearDefaultHint = (TextView)mAlertParams.mView.findViewById(16908835)).setVisibility(8);
        }
        this.setupAlert();
    }
    
    protected void onDestroy() {
        final UsbDisconnectedReceiver mDisconnectedReceiver = this.mDisconnectedReceiver;
        if (mDisconnectedReceiver != null) {
            this.unregisterReceiver((BroadcastReceiver)mDisconnectedReceiver);
        }
        super.onDestroy();
    }
}
