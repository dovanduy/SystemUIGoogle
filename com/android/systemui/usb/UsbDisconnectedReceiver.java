// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.usb;

import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.app.Activity;
import android.hardware.usb.UsbAccessory;
import android.content.BroadcastReceiver;

class UsbDisconnectedReceiver extends BroadcastReceiver
{
    private UsbAccessory mAccessory;
    private final Activity mActivity;
    private UsbDevice mDevice;
    
    public UsbDisconnectedReceiver(final Activity mActivity, final UsbAccessory mAccessory) {
        this.mActivity = mActivity;
        this.mAccessory = mAccessory;
        mActivity.registerReceiver((BroadcastReceiver)this, new IntentFilter("android.hardware.usb.action.USB_ACCESSORY_DETACHED"));
    }
    
    public UsbDisconnectedReceiver(final Activity mActivity, final UsbDevice mDevice) {
        this.mActivity = mActivity;
        this.mDevice = mDevice;
        mActivity.registerReceiver((BroadcastReceiver)this, new IntentFilter("android.hardware.usb.action.USB_DEVICE_DETACHED"));
    }
    
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action)) {
            final UsbDevice usbDevice = (UsbDevice)intent.getParcelableExtra("device");
            if (usbDevice != null && usbDevice.equals((Object)this.mDevice)) {
                this.mActivity.finish();
            }
        }
        else if ("android.hardware.usb.action.USB_ACCESSORY_DETACHED".equals(action)) {
            final UsbAccessory usbAccessory = (UsbAccessory)intent.getParcelableExtra("accessory");
            if (usbAccessory != null && usbAccessory.equals((Object)this.mAccessory)) {
                this.mActivity.finish();
            }
        }
    }
}
