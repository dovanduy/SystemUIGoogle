// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;
import android.bluetooth.BluetoothClass;
import java.util.Iterator;
import android.os.SystemClock;
import android.os.ParcelUuid;
import android.util.EventLog;
import android.bluetooth.BluetoothUuid;
import android.content.SharedPreferences$Editor;
import android.content.SharedPreferences;
import android.util.Log;
import android.os.Message;
import android.os.Looper;
import java.util.concurrent.CopyOnWriteArrayList;
import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import java.util.Collection;

public class CachedBluetoothDevice implements Comparable<CachedBluetoothDevice>
{
    private final Collection<Callback> mCallbacks;
    private long mConnectAttempted;
    private final Context mContext;
    BluetoothDevice mDevice;
    private final Handler mHandler;
    private long mHiSyncId;
    private boolean mIsA2dpProfileConnectedFail;
    private boolean mIsActiveDeviceA2dp;
    private boolean mIsActiveDeviceHeadset;
    private boolean mIsActiveDeviceHearingAid;
    private boolean mIsHeadsetProfileConnectedFail;
    private boolean mIsHearingAidProfileConnectedFail;
    boolean mJustDiscovered;
    private final BluetoothAdapter mLocalAdapter;
    private boolean mLocalNapRoleConnected;
    private final Object mProfileLock;
    private final LocalBluetoothProfileManager mProfileManager;
    private final Collection<LocalBluetoothProfile> mProfiles;
    private final Collection<LocalBluetoothProfile> mRemovedProfiles;
    short mRssi;
    private CachedBluetoothDevice mSubDevice;
    
    CachedBluetoothDevice(final Context mContext, final LocalBluetoothProfileManager mProfileManager, final BluetoothDevice mDevice) {
        this.mProfileLock = new Object();
        this.mProfiles = new CopyOnWriteArrayList<LocalBluetoothProfile>();
        this.mRemovedProfiles = new CopyOnWriteArrayList<LocalBluetoothProfile>();
        this.mCallbacks = new CopyOnWriteArrayList<Callback>();
        this.mIsActiveDeviceA2dp = false;
        this.mIsActiveDeviceHeadset = false;
        this.mIsActiveDeviceHearingAid = false;
        this.mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(final Message message) {
                final int what = message.what;
                if (what != 1) {
                    if (what != 2) {
                        if (what != 21) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("handleMessage(): unknown message : ");
                            sb.append(message.what);
                            Log.w("CachedBluetoothDevice", sb.toString());
                        }
                        else {
                            CachedBluetoothDevice.this.mIsHearingAidProfileConnectedFail = true;
                        }
                    }
                    else {
                        CachedBluetoothDevice.this.mIsA2dpProfileConnectedFail = true;
                    }
                }
                else {
                    CachedBluetoothDevice.this.mIsHeadsetProfileConnectedFail = true;
                }
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Connect to profile : ");
                sb2.append(message.what);
                sb2.append(" timeout, show error message !");
                Log.w("CachedBluetoothDevice", sb2.toString());
                CachedBluetoothDevice.this.refresh();
            }
        };
        this.mContext = mContext;
        this.mLocalAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mProfileManager = mProfileManager;
        this.mDevice = mDevice;
        this.fillData();
        this.mHiSyncId = 0L;
    }
    
    private void connectAllEnabledProfiles() {
        synchronized (this.mProfileLock) {
            if (this.mProfiles.isEmpty()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("No profiles. Maybe we will connect later for device ");
                sb.append(this.mDevice);
                Log.d("CachedBluetoothDevice", sb.toString());
                return;
            }
            this.mLocalAdapter.connectAllEnabledProfiles(this.mDevice);
        }
    }
    
    private String describe(final LocalBluetoothProfile obj) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Address:");
        sb.append(this.mDevice);
        if (obj != null) {
            sb.append(" Profile:");
            sb.append(obj);
        }
        return sb.toString();
    }
    
    private boolean ensurePaired() {
        if (this.getBondState() == 10) {
            this.startPairing();
            return false;
        }
        return true;
    }
    
    private void fetchActiveDevices() {
        final A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile != null) {
            this.mIsActiveDeviceA2dp = this.mDevice.equals((Object)a2dpProfile.getActiveDevice());
        }
        final HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile != null) {
            this.mIsActiveDeviceHeadset = this.mDevice.equals((Object)headsetProfile.getActiveDevice());
        }
        final HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile != null) {
            this.mIsActiveDeviceHearingAid = hearingAidProfile.getActiveDevices().contains(this.mDevice);
        }
    }
    
    private void fillData() {
        this.updateProfiles();
        this.fetchActiveDevices();
        this.migratePhonebookPermissionChoice();
        this.migrateMessagePermissionChoice();
        this.dispatchAttributesChanged();
    }
    
    private void migrateMessagePermissionChoice() {
        final SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("bluetooth_message_permission", 0);
        if (!sharedPreferences.contains(this.mDevice.getAddress())) {
            return;
        }
        if (this.mDevice.getMessageAccessPermission() == 0) {
            final int int1 = sharedPreferences.getInt(this.mDevice.getAddress(), 0);
            if (int1 == 1) {
                this.mDevice.setMessageAccessPermission(1);
            }
            else if (int1 == 2) {
                this.mDevice.setMessageAccessPermission(2);
            }
        }
        final SharedPreferences$Editor edit = sharedPreferences.edit();
        edit.remove(this.mDevice.getAddress());
        edit.commit();
    }
    
    private void migratePhonebookPermissionChoice() {
        final SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("bluetooth_phonebook_permission", 0);
        if (!sharedPreferences.contains(this.mDevice.getAddress())) {
            return;
        }
        if (this.mDevice.getPhonebookAccessPermission() == 0) {
            final int int1 = sharedPreferences.getInt(this.mDevice.getAddress(), 0);
            if (int1 == 1) {
                this.mDevice.setPhonebookAccessPermission(1);
            }
            else if (int1 == 2) {
                this.mDevice.setPhonebookAccessPermission(2);
            }
        }
        final SharedPreferences$Editor edit = sharedPreferences.edit();
        edit.remove(this.mDevice.getAddress());
        edit.commit();
    }
    
    private void processPhonebookAccess() {
        if (this.mDevice.getBondState() != 12) {
            return;
        }
        if (BluetoothUuid.containsAnyUuid(this.mDevice.getUuids(), PbapServerProfile.PBAB_CLIENT_UUIDS) && this.mDevice.getPhonebookAccessPermission() == 0) {
            if (this.mDevice.getBluetoothClass().getDeviceClass() == 1032 || this.mDevice.getBluetoothClass().getDeviceClass() == 1028) {
                EventLog.writeEvent(1397638484, new Object[] { "138529441", -1, "" });
            }
            this.mDevice.setPhonebookAccessPermission(2);
        }
    }
    
    private boolean updateProfiles() {
        final ParcelUuid[] uuids = this.mDevice.getUuids();
        int i = 0;
        if (uuids == null) {
            return false;
        }
        final ParcelUuid[] uuids2 = this.mLocalAdapter.getUuids();
        if (uuids2 == null) {
            return false;
        }
        this.processPhonebookAccess();
        Object o = this.mProfileLock;
        synchronized (o) {
            this.mProfileManager.updateProfiles(uuids, uuids2, this.mProfiles, this.mRemovedProfiles, this.mLocalNapRoleConnected, this.mDevice);
            // monitorexit(o)
            o = new StringBuilder();
            ((StringBuilder)o).append("updating profiles for ");
            ((StringBuilder)o).append(this.mDevice.getAlias());
            ((StringBuilder)o).append(", ");
            ((StringBuilder)o).append(this.mDevice);
            Log.e("CachedBluetoothDevice", ((StringBuilder)o).toString());
            o = this.mDevice.getBluetoothClass();
            if (o != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Class: ");
                sb.append(((BluetoothClass)o).toString());
                Log.v("CachedBluetoothDevice", sb.toString());
            }
            Log.v("CachedBluetoothDevice", "UUID:");
            while (i < uuids.length) {
                final ParcelUuid obj = uuids[i];
                o = new StringBuilder();
                ((StringBuilder)o).append("  ");
                ((StringBuilder)o).append(obj);
                Log.v("CachedBluetoothDevice", ((StringBuilder)o).toString());
                ++i;
            }
            return true;
        }
    }
    
    @Override
    public int compareTo(final CachedBluetoothDevice cachedBluetoothDevice) {
        final boolean b = cachedBluetoothDevice.isConnected() - this.isConnected();
        if (b) {
            return b ? 1 : 0;
        }
        final int bondState = cachedBluetoothDevice.getBondState();
        int n = 1;
        int n2;
        if (bondState == 12) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (this.getBondState() != 12) {
            n = 0;
        }
        final int n3 = n2 - n;
        if (n3 != 0) {
            return n3;
        }
        final boolean b2 = cachedBluetoothDevice.mJustDiscovered - this.mJustDiscovered;
        if (b2) {
            return b2 ? 1 : 0;
        }
        final int n4 = cachedBluetoothDevice.mRssi - this.mRssi;
        if (n4 != 0) {
            return n4;
        }
        return this.getName().compareTo(cachedBluetoothDevice.getName());
    }
    
    public void connect() {
        if (!this.ensurePaired()) {
            return;
        }
        this.mConnectAttempted = SystemClock.elapsedRealtime();
        this.connectAllEnabledProfiles();
    }
    
    @Deprecated
    public void connect(final boolean b) {
        this.connect();
    }
    
    public void disconnect() {
        synchronized (this.mProfileLock) {
            this.mLocalAdapter.disconnectAllEnabledProfiles(this.mDevice);
            // monitorexit(this.mProfileLock)
            final PbapServerProfile pbapProfile = this.mProfileManager.getPbapProfile();
            if (pbapProfile != null && this.isConnectedProfile(pbapProfile)) {
                pbapProfile.setEnabled(this.mDevice, false);
            }
        }
    }
    
    void dispatchAttributesChanged() {
        final Iterator<Callback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onDeviceAttributesChanged();
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof CachedBluetoothDevice && this.mDevice.equals((Object)((CachedBluetoothDevice)o).mDevice);
    }
    
    public String getAddress() {
        return this.mDevice.getAddress();
    }
    
    public int getBatteryLevel() {
        return this.mDevice.getBatteryLevel();
    }
    
    public int getBondState() {
        return this.mDevice.getBondState();
    }
    
    public BluetoothClass getBtClass() {
        return this.mDevice.getBluetoothClass();
    }
    
    public BluetoothDevice getDevice() {
        return this.mDevice;
    }
    
    public long getHiSyncId() {
        return this.mHiSyncId;
    }
    
    public int getMaxConnectionState() {
        synchronized (this.mProfileLock) {
            final Iterator<LocalBluetoothProfile> iterator = this.getProfiles().iterator();
            int n = 0;
            while (iterator.hasNext()) {
                final int profileConnectionState = this.getProfileConnectionState(iterator.next());
                if (profileConnectionState > n) {
                    n = profileConnectionState;
                }
            }
            return n;
        }
    }
    
    public String getName() {
        String s;
        if (TextUtils.isEmpty((CharSequence)(s = this.mDevice.getAlias()))) {
            s = this.getAddress();
        }
        return s;
    }
    
    public int getProfileConnectionState(final LocalBluetoothProfile localBluetoothProfile) {
        int connectionStatus;
        if (localBluetoothProfile != null) {
            connectionStatus = localBluetoothProfile.getConnectionStatus(this.mDevice);
        }
        else {
            connectionStatus = 0;
        }
        return connectionStatus;
    }
    
    public List<LocalBluetoothProfile> getProfiles() {
        return new ArrayList<LocalBluetoothProfile>(this.mProfiles);
    }
    
    public CachedBluetoothDevice getSubDevice() {
        return this.mSubDevice;
    }
    
    @Override
    public int hashCode() {
        return this.mDevice.getAddress().hashCode();
    }
    
    public boolean isActiveDevice(final int i) {
        if (i == 1) {
            return this.mIsActiveDeviceHeadset;
        }
        if (i == 2) {
            return this.mIsActiveDeviceA2dp;
        }
        if (i != 21) {
            final StringBuilder sb = new StringBuilder();
            sb.append("getActiveDevice: unknown profile ");
            sb.append(i);
            Log.w("CachedBluetoothDevice", sb.toString());
            return false;
        }
        return this.mIsActiveDeviceHearingAid;
    }
    
    public boolean isBusy() {
        synchronized (this.mProfileLock) {
            final Iterator<LocalBluetoothProfile> iterator = this.mProfiles.iterator();
            int profileConnectionState;
            do {
                final boolean hasNext = iterator.hasNext();
                boolean b = true;
                if (!hasNext) {
                    if (this.getBondState() != 11) {
                        b = false;
                    }
                    return b;
                }
                profileConnectionState = this.getProfileConnectionState(iterator.next());
            } while (profileConnectionState != 1 && profileConnectionState != 3);
            return true;
        }
    }
    
    public boolean isConnected() {
        synchronized (this.mProfileLock) {
            final Iterator<LocalBluetoothProfile> iterator = this.mProfiles.iterator();
            while (iterator.hasNext()) {
                if (this.getProfileConnectionState(iterator.next()) == 2) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public boolean isConnectedProfile(final LocalBluetoothProfile localBluetoothProfile) {
        return this.getProfileConnectionState(localBluetoothProfile) == 2;
    }
    
    public boolean isHearingAidDevice() {
        return this.mHiSyncId != 0L;
    }
    
    public void onActiveDeviceChanged(final boolean b, int i) {
        final int n = 1;
        final int n2 = 1;
        final int n3 = 1;
        final int n4 = 0;
        if (i != 1) {
            if (i != 2) {
                if (i != 21) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onActiveDeviceChanged: unknown profile ");
                    sb.append(i);
                    sb.append(" isActive ");
                    sb.append(b);
                    Log.w("CachedBluetoothDevice", sb.toString());
                    i = n4;
                }
                else {
                    if (this.mIsActiveDeviceHearingAid != b) {
                        i = n3;
                    }
                    else {
                        i = 0;
                    }
                    this.mIsActiveDeviceHearingAid = b;
                }
            }
            else {
                if (this.mIsActiveDeviceA2dp != b) {
                    i = n;
                }
                else {
                    i = 0;
                }
                this.mIsActiveDeviceA2dp = b;
            }
        }
        else {
            if (this.mIsActiveDeviceHeadset != b) {
                i = n2;
            }
            else {
                i = 0;
            }
            this.mIsActiveDeviceHeadset = b;
        }
        if (i != 0) {
            this.dispatchAttributesChanged();
        }
    }
    
    void onAudioModeChanged() {
        this.dispatchAttributesChanged();
    }
    
    void onBondingStateChanged(final int n) {
        if (n == 10) {
            synchronized (this.mProfileLock) {
                this.mProfiles.clear();
                // monitorexit(this.mProfileLock)
                this.mDevice.setPhonebookAccessPermission(0);
                this.mDevice.setMessageAccessPermission(0);
                this.mDevice.setSimAccessPermission(0);
            }
        }
        this.refresh();
        if (n == 12 && this.mDevice.isBondingInitiatedLocally()) {
            this.connect();
        }
    }
    
    void onProfileStateChanged(final LocalBluetoothProfile obj, final int n) {
        final StringBuilder sb = new StringBuilder();
        sb.append("onProfileStateChanged: profile ");
        sb.append(obj);
        sb.append(", device=");
        sb.append(this.mDevice);
        sb.append(", newProfileState ");
        sb.append(n);
        Log.d("CachedBluetoothDevice", sb.toString());
        if (this.mLocalAdapter.getState() == 13) {
            Log.d("CachedBluetoothDevice", " BT Turninig Off...Profile conn state change ignored...");
            return;
        }
        synchronized (this.mProfileLock) {
            if (obj instanceof A2dpProfile || obj instanceof HeadsetProfile || obj instanceof HearingAidProfile) {
                this.setProfileConnectedStatus(obj.getProfileId(), false);
                if (n != 0) {
                    if (n != 1) {
                        if (n != 2) {
                            if (n != 3) {
                                final StringBuilder sb2 = new StringBuilder();
                                sb2.append("onProfileStateChanged(): unknown profile state : ");
                                sb2.append(n);
                                Log.w("CachedBluetoothDevice", sb2.toString());
                            }
                            else if (this.mHandler.hasMessages(obj.getProfileId())) {
                                this.mHandler.removeMessages(obj.getProfileId());
                            }
                        }
                        else {
                            this.mHandler.removeMessages(obj.getProfileId());
                        }
                    }
                    else {
                        this.mHandler.sendEmptyMessageDelayed(obj.getProfileId(), 60000L);
                    }
                }
                else if (this.mHandler.hasMessages(obj.getProfileId())) {
                    this.mHandler.removeMessages(obj.getProfileId());
                    this.setProfileConnectedStatus(obj.getProfileId(), true);
                }
            }
            if (n == 2) {
                if (obj instanceof MapProfile) {
                    obj.setEnabled(this.mDevice, true);
                }
                if (!this.mProfiles.contains(obj)) {
                    this.mRemovedProfiles.remove(obj);
                    this.mProfiles.add(obj);
                    if (obj instanceof PanProfile && ((PanProfile)obj).isLocalRoleNap(this.mDevice)) {
                        this.mLocalNapRoleConnected = true;
                    }
                }
            }
            else if (obj instanceof MapProfile && n == 0) {
                obj.setEnabled(this.mDevice, false);
            }
            else if (this.mLocalNapRoleConnected && obj instanceof PanProfile && ((PanProfile)obj).isLocalRoleNap(this.mDevice) && n == 0) {
                Log.d("CachedBluetoothDevice", "Removing PanProfile from device after NAP disconnect");
                this.mProfiles.remove(obj);
                this.mRemovedProfiles.add(obj);
                this.mLocalNapRoleConnected = false;
            }
            // monitorexit(this.mProfileLock)
            this.fetchActiveDevices();
        }
    }
    
    void onUuidChanged() {
        this.updateProfiles();
        final ParcelUuid[] uuids = this.mDevice.getUuids();
        long n;
        if (ArrayUtils.contains((Object[])uuids, (Object)BluetoothUuid.HOGP)) {
            n = 30000L;
        }
        else if (ArrayUtils.contains((Object[])uuids, (Object)BluetoothUuid.HEARING_AID)) {
            n = 15000L;
        }
        else {
            n = 5000L;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("onUuidChanged: Time since last connect=");
        sb.append(SystemClock.elapsedRealtime() - this.mConnectAttempted);
        Log.d("CachedBluetoothDevice", sb.toString());
        if (!this.mProfiles.isEmpty() && this.mConnectAttempted + n > SystemClock.elapsedRealtime()) {
            this.connectAllEnabledProfiles();
        }
        this.dispatchAttributesChanged();
    }
    
    void refresh() {
        this.dispatchAttributesChanged();
    }
    
    void refreshName() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Device name: ");
        sb.append(this.getName());
        Log.d("CachedBluetoothDevice", sb.toString());
        this.dispatchAttributesChanged();
    }
    
    public void registerCallback(final Callback callback) {
        this.mCallbacks.add(callback);
    }
    
    public void setHiSyncId(final long n) {
        final StringBuilder sb = new StringBuilder();
        sb.append("setHiSyncId: mDevice ");
        sb.append(this.mDevice);
        sb.append(", id ");
        sb.append(n);
        Log.d("CachedBluetoothDevice", sb.toString());
        this.mHiSyncId = n;
    }
    
    public void setJustDiscovered(final boolean mJustDiscovered) {
        if (this.mJustDiscovered != mJustDiscovered) {
            this.mJustDiscovered = mJustDiscovered;
            this.dispatchAttributesChanged();
        }
    }
    
    void setProfileConnectedStatus(final int i, final boolean b) {
        if (i != 1 && i != 2 && i != 21) {
            final StringBuilder sb = new StringBuilder();
            sb.append("setProfileConnectedStatus(): unknown profile id : ");
            sb.append(i);
            Log.w("CachedBluetoothDevice", sb.toString());
        }
    }
    
    void setRssi(final short n) {
        if (this.mRssi != n) {
            this.mRssi = n;
            this.dispatchAttributesChanged();
        }
    }
    
    public void setSubDevice(final CachedBluetoothDevice mSubDevice) {
        this.mSubDevice = mSubDevice;
    }
    
    public boolean startPairing() {
        if (this.mLocalAdapter.isDiscovering()) {
            this.mLocalAdapter.cancelDiscovery();
        }
        return this.mDevice.createBond();
    }
    
    public void switchSubDeviceContent() {
        final BluetoothDevice mDevice = this.mDevice;
        final short mRssi = this.mRssi;
        final boolean mJustDiscovered = this.mJustDiscovered;
        final CachedBluetoothDevice mSubDevice = this.mSubDevice;
        this.mDevice = mSubDevice.mDevice;
        this.mRssi = mSubDevice.mRssi;
        this.mJustDiscovered = mSubDevice.mJustDiscovered;
        mSubDevice.mDevice = mDevice;
        mSubDevice.mRssi = mRssi;
        mSubDevice.mJustDiscovered = mJustDiscovered;
        this.fetchActiveDevices();
    }
    
    @Override
    public String toString() {
        return this.mDevice.toString();
    }
    
    public void unpair() {
        final int bondState = this.getBondState();
        if (bondState == 11) {
            this.mDevice.cancelBondProcess();
        }
        if (bondState != 10) {
            final BluetoothDevice mDevice = this.mDevice;
            if (mDevice != null && mDevice.removeBond()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Command sent successfully:REMOVE_BOND ");
                sb.append(this.describe(null));
                Log.d("CachedBluetoothDevice", sb.toString());
            }
        }
    }
    
    public void unregisterCallback(final Callback callback) {
        this.mCallbacks.remove(callback);
    }
    
    public interface Callback
    {
        void onDeviceAttributesChanged();
    }
}
