// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.usb;

import android.hardware.usb.IUsbManager;
import android.os.RemoteException;
import android.content.ActivityNotFoundException;
import android.hardware.usb.IUsbManager$Stub;
import android.os.ServiceManager;
import com.android.internal.app.chooser.TargetInfo;
import android.content.BroadcastReceiver;
import java.util.Iterator;
import com.android.systemui.R$string;
import android.widget.CheckBox;
import java.util.List;
import android.os.Parcelable;
import android.content.Context;
import android.app.Activity;
import android.os.UserHandle;
import com.android.internal.app.IntentForwarderActivity;
import java.util.Collection;
import java.util.ArrayList;
import android.util.Log;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbAccessory;
import com.android.internal.app.ResolverActivity;

public class UsbResolverActivity extends ResolverActivity
{
    private UsbAccessory mAccessory;
    private UsbDevice mDevice;
    private UsbDisconnectedReceiver mDisconnectedReceiver;
    private ResolveInfo mForwardResolveInfo;
    private Intent mOtherProfileIntent;
    
    protected void onCreate(final Bundle bundle) {
        final Intent intent = this.getIntent();
        final Parcelable parcelableExtra = intent.getParcelableExtra("android.intent.extra.INTENT");
        if (!(parcelableExtra instanceof Intent)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Target is not an intent: ");
            sb.append(parcelableExtra);
            Log.w("UsbResolverActivity", sb.toString());
            this.finish();
            return;
        }
        final Intent intent2 = (Intent)parcelableExtra;
        final ArrayList list = new ArrayList<ResolveInfo>(intent.getParcelableArrayListExtra("rlist"));
        final ArrayList<ResolveInfo> list2 = new ArrayList<ResolveInfo>();
        this.mForwardResolveInfo = null;
        final Iterator<ResolveInfo> iterator = list.iterator();
        while (iterator.hasNext()) {
            final ResolveInfo resolveInfo = iterator.next();
            if (resolveInfo.getComponentInfo().name.equals(IntentForwarderActivity.FORWARD_INTENT_TO_MANAGED_PROFILE)) {
                this.mForwardResolveInfo = resolveInfo;
            }
            else {
                if (UserHandle.getUserId(resolveInfo.activityInfo.applicationInfo.uid) == UserHandle.myUserId()) {
                    continue;
                }
                iterator.remove();
                list2.add(resolveInfo);
            }
        }
        boolean hasAudioCapture;
        if ((this.mDevice = (UsbDevice)intent2.getParcelableExtra("device")) != null) {
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity)this, this.mDevice);
            hasAudioCapture = this.mDevice.getHasAudioCapture();
        }
        else {
            if ((this.mAccessory = (UsbAccessory)intent2.getParcelableExtra("accessory")) == null) {
                Log.e("UsbResolverActivity", "no device or accessory");
                this.finish();
                return;
            }
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity)this, this.mAccessory);
            hasAudioCapture = false;
        }
        if (this.mForwardResolveInfo != null) {
            if (list2.size() > 1) {
                (this.mOtherProfileIntent = new Intent(intent)).putParcelableArrayListExtra("rlist", (ArrayList)list2);
            }
            else {
                (this.mOtherProfileIntent = new Intent((Context)this, (Class)UsbConfirmActivity.class)).putExtra("rinfo", (Parcelable)list2.get(0));
                final UsbDevice mDevice = this.mDevice;
                if (mDevice != null) {
                    this.mOtherProfileIntent.putExtra("device", (Parcelable)mDevice);
                }
                final UsbAccessory mAccessory = this.mAccessory;
                if (mAccessory != null) {
                    this.mOtherProfileIntent.putExtra("accessory", (Parcelable)mAccessory);
                }
            }
        }
        this.getIntent().putExtra("is_audio_capture_device", hasAudioCapture);
        super.onCreate(bundle, intent2, this.getResources().getText(17039812), (Intent[])null, (List)list, true);
        final CheckBox checkBox = (CheckBox)this.findViewById(16908745);
        if (checkBox != null) {
            if (this.mDevice == null) {
                checkBox.setText(R$string.always_use_accessory);
            }
            else {
                checkBox.setText(R$string.always_use_device);
            }
        }
    }
    
    protected void onDestroy() {
        final UsbDisconnectedReceiver mDisconnectedReceiver = this.mDisconnectedReceiver;
        if (mDisconnectedReceiver != null) {
            this.unregisterReceiver((BroadcastReceiver)mDisconnectedReceiver);
        }
        super.onDestroy();
    }
    
    protected boolean onTargetSelected(final TargetInfo targetInfo, final boolean b) {
        final ResolveInfo resolveInfo = targetInfo.getResolveInfo();
        final ResolveInfo mForwardResolveInfo = this.mForwardResolveInfo;
        if (resolveInfo == mForwardResolveInfo) {
            this.startActivityAsUser(this.mOtherProfileIntent, (Bundle)null, UserHandle.of(mForwardResolveInfo.targetUserId));
            return true;
        }
        try {
            final IUsbManager interface1 = IUsbManager$Stub.asInterface(ServiceManager.getService("usb"));
            final int uid = resolveInfo.activityInfo.applicationInfo.uid;
            final int myUserId = UserHandle.myUserId();
            if (this.mDevice != null) {
                interface1.grantDevicePermission(this.mDevice, uid);
                if (b) {
                    interface1.setDevicePackage(this.mDevice, resolveInfo.activityInfo.packageName, myUserId);
                }
                else {
                    interface1.setDevicePackage(this.mDevice, (String)null, myUserId);
                }
            }
            else if (this.mAccessory != null) {
                interface1.grantAccessoryPermission(this.mAccessory, uid);
                if (b) {
                    interface1.setAccessoryPackage(this.mAccessory, resolveInfo.activityInfo.packageName, myUserId);
                }
                else {
                    interface1.setAccessoryPackage(this.mAccessory, (String)null, myUserId);
                }
            }
            try {
                targetInfo.startAsUser((Activity)this, (Bundle)null, UserHandle.of(myUserId));
            }
            catch (ActivityNotFoundException ex) {
                Log.e("UsbResolverActivity", "startActivity failed", (Throwable)ex);
            }
        }
        catch (RemoteException ex2) {
            Log.e("UsbResolverActivity", "onIntentSelected failed", (Throwable)ex2);
        }
        return true;
    }
    
    protected boolean shouldShowTabs() {
        return false;
    }
}
