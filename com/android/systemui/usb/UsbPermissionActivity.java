// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.usb;

import android.hardware.usb.IUsbManager;
import android.content.BroadcastReceiver;
import android.app.PendingIntent$CanceledException;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.Parcelable;
import android.hardware.usb.IUsbManager$Stub;
import android.os.ServiceManager;
import android.view.View;
import com.android.internal.app.AlertController$AlertParams;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.content.PermissionChecker;
import android.app.Activity;
import com.android.systemui.R$string;
import android.os.Bundle;
import android.content.DialogInterface;
import android.widget.CompoundButton;
import android.app.PendingIntent;
import android.hardware.usb.UsbDevice;
import android.widget.TextView;
import android.widget.CheckBox;
import android.hardware.usb.UsbAccessory;
import android.widget.CompoundButton$OnCheckedChangeListener;
import android.content.DialogInterface$OnClickListener;
import com.android.internal.app.AlertActivity;

public class UsbPermissionActivity extends AlertActivity implements DialogInterface$OnClickListener, CompoundButton$OnCheckedChangeListener
{
    private UsbAccessory mAccessory;
    private CheckBox mAlwaysUse;
    private TextView mClearDefaultHint;
    private UsbDevice mDevice;
    private UsbDisconnectedReceiver mDisconnectedReceiver;
    private String mPackageName;
    private PendingIntent mPendingIntent;
    private boolean mPermissionGranted;
    private int mUid;
    
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
    
    public void onClick(final DialogInterface dialogInterface, final int n) {
        if (n == -1) {
            this.mPermissionGranted = true;
        }
        this.finish();
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final Intent intent = this.getIntent();
        this.mDevice = (UsbDevice)intent.getParcelableExtra("device");
        this.mAccessory = (UsbAccessory)intent.getParcelableExtra("accessory");
        this.mPendingIntent = (PendingIntent)intent.getParcelableExtra("android.intent.extra.INTENT");
        this.mUid = intent.getIntExtra("android.intent.extra.UID", -1);
        this.mPackageName = intent.getStringExtra("android.hardware.usb.extra.PACKAGE");
        final boolean booleanExtra = intent.getBooleanExtra("android.hardware.usb.extra.CAN_BE_DEFAULT", false);
        final PackageManager packageManager = this.getPackageManager();
        try {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.mPackageName, 0);
            final String string = applicationInfo.loadLabel(packageManager).toString();
            final AlertController$AlertParams mAlertParams = super.mAlertParams;
            mAlertParams.mTitle = string;
            boolean b;
            if (this.mDevice == null) {
                mAlertParams.mMessage = this.getString(R$string.usb_accessory_permission_prompt, new Object[] { string, this.mAccessory.getDescription() });
                this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity)this, this.mAccessory);
                b = false;
            }
            else {
                final boolean b2 = PermissionChecker.checkPermissionForPreflight((Context)this, "android.permission.RECORD_AUDIO", -1, applicationInfo.uid, this.mPackageName) == 0;
                b = (this.mDevice.getHasAudioCapture() && !b2);
                int n;
                if (b) {
                    n = R$string.usb_device_permission_prompt_warn;
                }
                else {
                    n = R$string.usb_device_permission_prompt;
                }
                mAlertParams.mMessage = this.getString(n, new Object[] { string, this.mDevice.getProductName() });
                this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity)this, this.mDevice);
            }
            mAlertParams.mPositiveButtonText = this.getString(17039370);
            mAlertParams.mNegativeButtonText = this.getString(17039360);
            mAlertParams.mPositiveButtonListener = (DialogInterface$OnClickListener)this;
            mAlertParams.mNegativeButtonListener = (DialogInterface$OnClickListener)this;
            if (!b && booleanExtra && (this.mDevice != null || this.mAccessory != null)) {
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
        catch (PackageManager$NameNotFoundException ex) {
            Log.e("UsbPermissionActivity", "unable to look up package name", (Throwable)ex);
            this.finish();
        }
    }
    
    public void onDestroy() {
        final IUsbManager interface1 = IUsbManager$Stub.asInterface(ServiceManager.getService("usb"));
        final Intent intent = new Intent();
        try {
            if (this.mDevice != null) {
                intent.putExtra("device", (Parcelable)this.mDevice);
                if (this.mPermissionGranted) {
                    interface1.grantDevicePermission(this.mDevice, this.mUid);
                    if (this.mAlwaysUse != null && this.mAlwaysUse.isChecked()) {
                        interface1.setDevicePackage(this.mDevice, this.mPackageName, UserHandle.getUserId(this.mUid));
                    }
                }
            }
            if (this.mAccessory != null) {
                intent.putExtra("accessory", (Parcelable)this.mAccessory);
                if (this.mPermissionGranted) {
                    interface1.grantAccessoryPermission(this.mAccessory, this.mUid);
                    if (this.mAlwaysUse != null && this.mAlwaysUse.isChecked()) {
                        interface1.setAccessoryPackage(this.mAccessory, this.mPackageName, UserHandle.getUserId(this.mUid));
                    }
                }
            }
            intent.putExtra("permission", this.mPermissionGranted);
            this.mPendingIntent.send((Context)this, 0, intent);
        }
        catch (RemoteException ex) {
            Log.e("UsbPermissionActivity", "IUsbService connection failed", (Throwable)ex);
        }
        catch (PendingIntent$CanceledException ex2) {
            Log.w("UsbPermissionActivity", "PendingIntent was cancelled");
        }
        final UsbDisconnectedReceiver mDisconnectedReceiver = this.mDisconnectedReceiver;
        if (mDisconnectedReceiver != null) {
            this.unregisterReceiver((BroadcastReceiver)mDisconnectedReceiver);
        }
        super.onDestroy();
    }
}
