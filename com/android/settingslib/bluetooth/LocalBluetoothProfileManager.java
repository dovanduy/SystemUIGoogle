// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import android.content.Intent;
import com.android.internal.util.ArrayUtils;
import android.bluetooth.BluetoothUuid;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import java.util.List;
import com.android.internal.util.CollectionUtils;
import android.bluetooth.BluetoothAdapter;
import java.util.Iterator;
import java.util.ArrayList;
import android.util.Log;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;
import android.content.Context;

public class LocalBluetoothProfileManager
{
    private A2dpProfile mA2dpProfile;
    private A2dpSinkProfile mA2dpSinkProfile;
    private final Context mContext;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private final BluetoothEventManager mEventManager;
    private HeadsetProfile mHeadsetProfile;
    private HearingAidProfile mHearingAidProfile;
    private HfpClientProfile mHfpClientProfile;
    private HidDeviceProfile mHidDeviceProfile;
    private HidProfile mHidProfile;
    private MapClientProfile mMapClientProfile;
    private MapProfile mMapProfile;
    private OppProfile mOppProfile;
    private PanProfile mPanProfile;
    private PbapClientProfile mPbapClientProfile;
    private PbapServerProfile mPbapProfile;
    private final Map<String, LocalBluetoothProfile> mProfileNameMap;
    private SapProfile mSapProfile;
    private final Collection<ServiceListener> mServiceListeners;
    
    LocalBluetoothProfileManager(final Context mContext, final LocalBluetoothAdapter localBluetoothAdapter, final CachedBluetoothDeviceManager mDeviceManager, final BluetoothEventManager mEventManager) {
        this.mProfileNameMap = new HashMap<String, LocalBluetoothProfile>();
        this.mServiceListeners = new CopyOnWriteArrayList<ServiceListener>();
        this.mContext = mContext;
        this.mDeviceManager = mDeviceManager;
        this.mEventManager = mEventManager;
        localBluetoothAdapter.setProfileManager(this);
        Log.d("LocalBluetoothProfileManager", "LocalBluetoothProfileManager construction complete");
    }
    
    private void addHeadsetProfile(final LocalBluetoothProfile localBluetoothProfile, final String s, final String s2, final String s3, final int n) {
        final HeadsetStateChangeHandler headsetStateChangeHandler = new HeadsetStateChangeHandler(localBluetoothProfile, s3, n);
        this.mEventManager.addProfileHandler(s2, (BluetoothEventManager.Handler)headsetStateChangeHandler);
        this.mEventManager.addProfileHandler(s3, (BluetoothEventManager.Handler)headsetStateChangeHandler);
        this.mProfileNameMap.put(s, localBluetoothProfile);
    }
    
    private void addPanProfile(final LocalBluetoothProfile localBluetoothProfile, final String s, final String s2) {
        this.mEventManager.addProfileHandler(s2, (BluetoothEventManager.Handler)new PanStateChangedHandler(localBluetoothProfile));
        this.mProfileNameMap.put(s, localBluetoothProfile);
    }
    
    private void addProfile(final LocalBluetoothProfile localBluetoothProfile, final String s, final String s2) {
        this.mEventManager.addProfileHandler(s2, (BluetoothEventManager.Handler)new StateChangedHandler(localBluetoothProfile));
        this.mProfileNameMap.put(s, localBluetoothProfile);
    }
    
    public void addServiceListener(final ServiceListener serviceListener) {
        this.mServiceListeners.add(serviceListener);
    }
    
    void callServiceConnectedListeners() {
        final Iterator<ServiceListener> iterator = new ArrayList<ServiceListener>(this.mServiceListeners).iterator();
        while (iterator.hasNext()) {
            iterator.next().onServiceConnected();
        }
    }
    
    void callServiceDisconnectedListeners() {
        final Iterator<ServiceListener> iterator = new ArrayList<ServiceListener>(this.mServiceListeners).iterator();
        while (iterator.hasNext()) {
            iterator.next().onServiceDisconnected();
        }
    }
    
    public A2dpProfile getA2dpProfile() {
        return this.mA2dpProfile;
    }
    
    public HeadsetProfile getHeadsetProfile() {
        return this.mHeadsetProfile;
    }
    
    public HearingAidProfile getHearingAidProfile() {
        return this.mHearingAidProfile;
    }
    
    HidDeviceProfile getHidDeviceProfile() {
        return this.mHidDeviceProfile;
    }
    
    HidProfile getHidProfile() {
        return this.mHidProfile;
    }
    
    public PbapServerProfile getPbapProfile() {
        return this.mPbapProfile;
    }
    
    void setBluetoothStateOn() {
        this.updateLocalProfiles();
        this.mEventManager.readPairedDevices();
    }
    
    void updateLocalProfiles() {
        final List supportedProfiles = BluetoothAdapter.getDefaultAdapter().getSupportedProfiles();
        if (CollectionUtils.isEmpty((Collection)supportedProfiles)) {
            Log.d("LocalBluetoothProfileManager", "supportedList is null");
            return;
        }
        if (this.mA2dpProfile == null && supportedProfiles.contains(2)) {
            Log.d("LocalBluetoothProfileManager", "Adding local A2DP profile");
            this.addProfile(this.mA2dpProfile = new A2dpProfile(this.mContext, this.mDeviceManager, this), "A2DP", "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mA2dpSinkProfile == null && supportedProfiles.contains(11)) {
            Log.d("LocalBluetoothProfileManager", "Adding local A2DP SINK profile");
            this.addProfile(this.mA2dpSinkProfile = new A2dpSinkProfile(this.mContext, this.mDeviceManager, this), "A2DPSink", "android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mHeadsetProfile == null && supportedProfiles.contains(1)) {
            Log.d("LocalBluetoothProfileManager", "Adding local HEADSET profile");
            this.addHeadsetProfile(this.mHeadsetProfile = new HeadsetProfile(this.mContext, this.mDeviceManager, this), "HEADSET", "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED", "android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED", 10);
        }
        if (this.mHfpClientProfile == null && supportedProfiles.contains(16)) {
            Log.d("LocalBluetoothProfileManager", "Adding local HfpClient profile");
            this.addHeadsetProfile(this.mHfpClientProfile = new HfpClientProfile(this.mContext, this.mDeviceManager, this), "HEADSET_CLIENT", "android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED", "android.bluetooth.headsetclient.profile.action.AUDIO_STATE_CHANGED", 0);
        }
        if (this.mMapClientProfile == null && supportedProfiles.contains(18)) {
            Log.d("LocalBluetoothProfileManager", "Adding local MAP CLIENT profile");
            this.addProfile(this.mMapClientProfile = new MapClientProfile(this.mContext, this.mDeviceManager, this), "MAP Client", "android.bluetooth.mapmce.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mMapProfile == null && supportedProfiles.contains(9)) {
            Log.d("LocalBluetoothProfileManager", "Adding local MAP profile");
            this.addProfile(this.mMapProfile = new MapProfile(this.mContext, this.mDeviceManager, this), "MAP", "android.bluetooth.map.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mOppProfile == null && supportedProfiles.contains(20)) {
            Log.d("LocalBluetoothProfileManager", "Adding local OPP profile");
            final OppProfile mOppProfile = new OppProfile();
            this.mOppProfile = mOppProfile;
            this.mProfileNameMap.put("OPP", mOppProfile);
        }
        if (this.mHearingAidProfile == null && supportedProfiles.contains(21)) {
            Log.d("LocalBluetoothProfileManager", "Adding local Hearing Aid profile");
            this.addProfile(this.mHearingAidProfile = new HearingAidProfile(this.mContext, this.mDeviceManager, this), "HearingAid", "android.bluetooth.hearingaid.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mHidProfile == null && supportedProfiles.contains(4)) {
            Log.d("LocalBluetoothProfileManager", "Adding local HID_HOST profile");
            this.addProfile(this.mHidProfile = new HidProfile(this.mContext, this.mDeviceManager, this), "HID", "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mHidDeviceProfile == null && supportedProfiles.contains(19)) {
            Log.d("LocalBluetoothProfileManager", "Adding local HID_DEVICE profile");
            this.addProfile(this.mHidDeviceProfile = new HidDeviceProfile(this.mContext, this.mDeviceManager, this), "HID DEVICE", "android.bluetooth.hiddevice.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mPanProfile == null && supportedProfiles.contains(5)) {
            Log.d("LocalBluetoothProfileManager", "Adding local PAN profile");
            this.addPanProfile(this.mPanProfile = new PanProfile(this.mContext), "PAN", "android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mPbapProfile == null && supportedProfiles.contains(6)) {
            Log.d("LocalBluetoothProfileManager", "Adding local PBAP profile");
            this.addProfile(this.mPbapProfile = new PbapServerProfile(this.mContext), "PBAP Server", "android.bluetooth.pbap.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mPbapClientProfile == null && supportedProfiles.contains(17)) {
            Log.d("LocalBluetoothProfileManager", "Adding local PBAP Client profile");
            this.addProfile(this.mPbapClientProfile = new PbapClientProfile(this.mContext, this.mDeviceManager, this), "PbapClient", "android.bluetooth.pbapclient.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mSapProfile == null && supportedProfiles.contains(10)) {
            Log.d("LocalBluetoothProfileManager", "Adding local SAP profile");
            this.addProfile(this.mSapProfile = new SapProfile(this.mContext, this.mDeviceManager, this), "SAP", "android.bluetooth.sap.profile.action.CONNECTION_STATE_CHANGED");
        }
        this.mEventManager.registerProfileIntentReceiver();
    }
    
    void updateProfiles(final ParcelUuid[] array, final ParcelUuid[] array2, final Collection<LocalBluetoothProfile> collection, final Collection<LocalBluetoothProfile> collection2, final boolean b, final BluetoothDevice bluetoothDevice) {
        synchronized (this) {
            collection2.clear();
            collection2.addAll(collection);
            final StringBuilder sb = new StringBuilder();
            sb.append("Current Profiles");
            sb.append(collection.toString());
            Log.d("LocalBluetoothProfileManager", sb.toString());
            collection.clear();
            if (array == null) {
                return;
            }
            if (this.mHeadsetProfile != null && ((ArrayUtils.contains((Object[])array2, (Object)BluetoothUuid.HSP_AG) && ArrayUtils.contains((Object[])array, (Object)BluetoothUuid.HSP)) || (ArrayUtils.contains((Object[])array2, (Object)BluetoothUuid.HFP_AG) && ArrayUtils.contains((Object[])array, (Object)BluetoothUuid.HFP)))) {
                collection.add(this.mHeadsetProfile);
                collection2.remove(this.mHeadsetProfile);
            }
            if (this.mHfpClientProfile != null && ArrayUtils.contains((Object[])array, (Object)BluetoothUuid.HFP_AG) && ArrayUtils.contains((Object[])array2, (Object)BluetoothUuid.HFP)) {
                collection.add(this.mHfpClientProfile);
                collection2.remove(this.mHfpClientProfile);
            }
            if (BluetoothUuid.containsAnyUuid(array, A2dpProfile.SINK_UUIDS) && this.mA2dpProfile != null) {
                collection.add(this.mA2dpProfile);
                collection2.remove(this.mA2dpProfile);
            }
            if (BluetoothUuid.containsAnyUuid(array, A2dpSinkProfile.SRC_UUIDS) && this.mA2dpSinkProfile != null) {
                collection.add(this.mA2dpSinkProfile);
                collection2.remove(this.mA2dpSinkProfile);
            }
            if (ArrayUtils.contains((Object[])array, (Object)BluetoothUuid.OBEX_OBJECT_PUSH) && this.mOppProfile != null) {
                collection.add(this.mOppProfile);
                collection2.remove(this.mOppProfile);
            }
            if ((ArrayUtils.contains((Object[])array, (Object)BluetoothUuid.HID) || ArrayUtils.contains((Object[])array, (Object)BluetoothUuid.HOGP)) && this.mHidProfile != null) {
                collection.add(this.mHidProfile);
                collection2.remove(this.mHidProfile);
            }
            if (this.mHidDeviceProfile != null && this.mHidDeviceProfile.getConnectionStatus(bluetoothDevice) != 0) {
                collection.add(this.mHidDeviceProfile);
                collection2.remove(this.mHidDeviceProfile);
            }
            if (b) {
                Log.d("LocalBluetoothProfileManager", "Valid PAN-NAP connection exists.");
            }
            if ((ArrayUtils.contains((Object[])array, (Object)BluetoothUuid.NAP) && this.mPanProfile != null) || b) {
                collection.add(this.mPanProfile);
                collection2.remove(this.mPanProfile);
            }
            if (this.mMapProfile != null && this.mMapProfile.getConnectionStatus(bluetoothDevice) == 2) {
                collection.add(this.mMapProfile);
                collection2.remove(this.mMapProfile);
                this.mMapProfile.setEnabled(bluetoothDevice, true);
            }
            if (this.mPbapProfile != null && this.mPbapProfile.getConnectionStatus(bluetoothDevice) == 2) {
                collection.add(this.mPbapProfile);
                collection2.remove(this.mPbapProfile);
                this.mPbapProfile.setEnabled(bluetoothDevice, true);
            }
            if (this.mMapClientProfile != null) {
                collection.add(this.mMapClientProfile);
                collection2.remove(this.mMapClientProfile);
            }
            if (this.mPbapClientProfile != null && ArrayUtils.contains((Object[])array2, (Object)BluetoothUuid.PBAP_PCE) && BluetoothUuid.containsAnyUuid(array, PbapClientProfile.SRC_UUIDS)) {
                collection.add(this.mPbapClientProfile);
                collection2.remove(this.mPbapClientProfile);
            }
            if (ArrayUtils.contains((Object[])array, (Object)BluetoothUuid.HEARING_AID) && this.mHearingAidProfile != null) {
                collection.add(this.mHearingAidProfile);
                collection2.remove(this.mHearingAidProfile);
            }
            if (this.mSapProfile != null && ArrayUtils.contains((Object[])array, (Object)BluetoothUuid.SAP)) {
                collection.add(this.mSapProfile);
                collection2.remove(this.mSapProfile);
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("New Profiles");
            sb2.append(collection.toString());
            Log.d("LocalBluetoothProfileManager", sb2.toString());
        }
    }
    
    private class HeadsetStateChangeHandler extends StateChangedHandler
    {
        private final String mAudioChangeAction;
        private final int mAudioDisconnectedState;
        
        HeadsetStateChangeHandler(final LocalBluetoothProfileManager localBluetoothProfileManager, final LocalBluetoothProfile localBluetoothProfile, final String mAudioChangeAction, final int mAudioDisconnectedState) {
            localBluetoothProfileManager.super(localBluetoothProfile);
            this.mAudioChangeAction = mAudioChangeAction;
            this.mAudioDisconnectedState = mAudioDisconnectedState;
        }
        
        public void onReceiveInternal(final Intent intent, final CachedBluetoothDevice cachedBluetoothDevice) {
            if (this.mAudioChangeAction.equals(intent.getAction())) {
                if (intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) != this.mAudioDisconnectedState) {
                    cachedBluetoothDevice.onProfileStateChanged(super.mProfile, 2);
                }
                cachedBluetoothDevice.refresh();
            }
            else {
                super.onReceiveInternal(intent, cachedBluetoothDevice);
            }
        }
    }
    
    private class PanStateChangedHandler extends StateChangedHandler
    {
        PanStateChangedHandler(final LocalBluetoothProfileManager localBluetoothProfileManager, final LocalBluetoothProfile localBluetoothProfile) {
            localBluetoothProfileManager.super(localBluetoothProfile);
        }
        
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            ((PanProfile)super.mProfile).setLocalRole(bluetoothDevice, intent.getIntExtra("android.bluetooth.pan.extra.LOCAL_ROLE", 0));
            super.onReceive(context, intent, bluetoothDevice);
        }
    }
    
    public interface ServiceListener
    {
        void onServiceConnected();
        
        void onServiceDisconnected();
    }
    
    private class StateChangedHandler implements Handler
    {
        final LocalBluetoothProfile mProfile;
        
        StateChangedHandler(final LocalBluetoothProfile mProfile) {
            this.mProfile = mProfile;
        }
        
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice obj) {
            CachedBluetoothDevice cachedBluetoothDevice;
            if ((cachedBluetoothDevice = LocalBluetoothProfileManager.this.mDeviceManager.findDevice(obj)) == null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("StateChangedHandler found new device: ");
                sb.append(obj);
                Log.w("LocalBluetoothProfileManager", sb.toString());
                cachedBluetoothDevice = LocalBluetoothProfileManager.this.mDeviceManager.addDevice(obj);
            }
            this.onReceiveInternal(intent, cachedBluetoothDevice);
        }
        
        protected void onReceiveInternal(final Intent intent, final CachedBluetoothDevice cachedBluetoothDevice) {
            final int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
            final int intExtra2 = intent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 0);
            if (intExtra == 0 && intExtra2 == 1) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Failed to connect ");
                sb.append(this.mProfile);
                sb.append(" device");
                Log.i("LocalBluetoothProfileManager", sb.toString());
            }
            if (LocalBluetoothProfileManager.this.getHearingAidProfile() != null && this.mProfile instanceof HearingAidProfile && intExtra == 2 && cachedBluetoothDevice.getHiSyncId() == 0L) {
                final long hiSyncId = LocalBluetoothProfileManager.this.getHearingAidProfile().getHiSyncId(cachedBluetoothDevice.getDevice());
                if (hiSyncId != 0L) {
                    cachedBluetoothDevice.setHiSyncId(hiSyncId);
                }
            }
            cachedBluetoothDevice.onProfileStateChanged(this.mProfile, intExtra);
            if (cachedBluetoothDevice.getHiSyncId() == 0L || !LocalBluetoothProfileManager.this.mDeviceManager.onProfileConnectionStateChangedIfProcessed(cachedBluetoothDevice, intExtra)) {
                cachedBluetoothDevice.refresh();
                LocalBluetoothProfileManager.this.mEventManager.dispatchProfileConnectionStateChanged(cachedBluetoothDevice, intExtra, this.mProfile.getProfileId());
            }
        }
    }
}
