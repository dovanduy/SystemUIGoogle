// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.reversecharging;

import java.util.Iterator;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import java.util.Collection;
import vendor.google.wireless_charger.V1_2.RtxStatusInfo;
import android.content.Context;
import vendor.google.wireless_charger.V1_2.IWirelessCharger;
import java.util.ArrayList;
import android.os.IHwBinder$DeathRecipient;
import vendor.google.wireless_charger.V1_2.IWirelessChargerRtxStatusCallback;

public class ReverseWirelessCharger extends Stub implements IHwBinder$DeathRecipient
{
    private final Object mLock;
    private final ArrayList<RtxStatusCallback> mRtxStatusCallbacks;
    private IWirelessCharger mWirelessCharger;
    
    public ReverseWirelessCharger(final Context context) {
        new ArrayList();
        this.mRtxStatusCallbacks = new ArrayList<RtxStatusCallback>();
        this.mLock = new Object();
    }
    
    private void dispatchRtxStatusCallbacks(final RtxStatusInfo rtxStatusInfo) {
        Object o = this.mLock;
        synchronized (o) {
            final ArrayList<RtxStatusCallback> list = new ArrayList<RtxStatusCallback>(this.mRtxStatusCallbacks);
            // monitorexit(o)
            o = list.iterator();
            while (((Iterator)o).hasNext()) {
                ((Iterator<RtxStatusCallback>)o).next().onRtxStatusChanged(rtxStatusInfo);
            }
        }
    }
    
    private void initHALInterface() {
        if (this.mWirelessCharger == null) {
            try {
                (this.mWirelessCharger = IWirelessCharger.getService()).linkToDeath((IHwBinder$DeathRecipient)this, 0L);
                this.mWirelessCharger.registerRtxCallback(this);
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("no wireless charger hal found: ");
                sb.append(ex.getMessage());
                Log.i("ReverseWirelessCharger", sb.toString(), (Throwable)ex);
                this.mWirelessCharger = null;
            }
        }
    }
    
    public void addReverseChargingChangeListener(final ReverseChargingChangeListener reverseChargingChangeListener) {
        this.addRtxStatusCallback((RtxStatusCallback)reverseChargingChangeListener);
    }
    
    public void addRtxStatusCallback(final RtxStatusCallback e) {
        synchronized (this.mLock) {
            this.mRtxStatusCallbacks.add(e);
        }
    }
    
    public boolean isRtxModeOn() {
        this.initHALInterface();
        final IWirelessCharger mWirelessCharger = this.mWirelessCharger;
        if (mWirelessCharger != null) {
            try {
                return mWirelessCharger.isRtxModeOn();
            }
            catch (Exception ex) {
                Log.i("ReverseWirelessCharger", "isRtxModeOn fail: ", (Throwable)ex);
            }
        }
        return false;
    }
    
    public boolean isRtxSupported() {
        this.initHALInterface();
        final IWirelessCharger mWirelessCharger = this.mWirelessCharger;
        if (mWirelessCharger != null) {
            try {
                return mWirelessCharger.isRtxSupported();
            }
            catch (Exception ex) {
                Log.i("ReverseWirelessCharger", "isRtxSupported fail: ", (Throwable)ex);
            }
        }
        return false;
    }
    
    public void rtxStatusInfoChanged(final RtxStatusInfo rtxStatusInfo) throws RemoteException {
        this.dispatchRtxStatusCallbacks(rtxStatusInfo);
    }
    
    public void serviceDied(final long n) {
        Log.i("ReverseWirelessCharger", "serviceDied");
        this.mWirelessCharger = null;
    }
    
    public void setRtxMode(final boolean rtxMode) {
        this.initHALInterface();
        final IWirelessCharger mWirelessCharger = this.mWirelessCharger;
        if (mWirelessCharger != null) {
            try {
                mWirelessCharger.setRtxMode(rtxMode);
            }
            catch (Exception ex) {
                Log.i("ReverseWirelessCharger", "setRtxMode fail: ", (Throwable)ex);
            }
        }
    }
    
    public interface ReverseChargingChangeListener extends RtxStatusCallback
    {
        default Bundle buildReverseStatusBundle(final RtxStatusInfo rtxStatusInfo) {
            final Bundle bundle = new Bundle();
            bundle.putInt("key_rtx_mode", (int)rtxStatusInfo.mode);
            bundle.putInt("key_accessory_type", rtxStatusInfo.acctype);
            bundle.putBoolean("key_rtx_connection", rtxStatusInfo.chg_s);
            bundle.putInt("key_rtx_iout", rtxStatusInfo.iout);
            bundle.putInt("key_rtx_vout", rtxStatusInfo.vout);
            bundle.putInt("key_rtx_level", rtxStatusInfo.level);
            bundle.putInt("key_reason_type", (int)rtxStatusInfo.reason);
            return bundle;
        }
        
        void onReverseStatusChanged(final Bundle p0);
        
        default void onRtxStatusChanged(final RtxStatusInfo rtxStatusInfo) {
            this.onReverseStatusChanged(this.buildReverseStatusBundle(rtxStatusInfo));
        }
    }
    
    public interface RtxStatusCallback
    {
        void onRtxStatusChanged(final RtxStatusInfo p0);
    }
}
