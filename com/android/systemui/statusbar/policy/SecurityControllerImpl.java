// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.ComponentName;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.internal.net.LegacyVpnInfo;
import android.os.RemoteException;
import android.content.pm.ApplicationInfo;
import android.content.pm.UserInfo;
import android.content.pm.PackageManager$NameNotFoundException;
import com.android.systemui.R$string;
import java.util.Iterator;
import android.app.ActivityManager;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.net.IConnectivityManager$Stub;
import android.os.ServiceManager;
import android.content.Intent;
import android.net.Network;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Handler;
import java.util.Set;
import android.net.NetworkRequest$Builder;
import android.util.Log;
import android.os.UserManager;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager$NetworkCallback;
import android.util.ArrayMap;
import android.app.admin.DevicePolicyManager;
import com.android.internal.net.VpnConfig;
import android.util.SparseArray;
import android.content.Context;
import android.net.IConnectivityManager;
import android.net.ConnectivityManager;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import android.content.BroadcastReceiver;
import java.util.concurrent.Executor;
import android.net.NetworkRequest;
import com.android.systemui.settings.CurrentUserTracker;

public class SecurityControllerImpl extends CurrentUserTracker implements SecurityController
{
    private static final boolean DEBUG;
    private static final NetworkRequest REQUEST;
    private final Executor mBgExecutor;
    private final BroadcastReceiver mBroadcastReceiver;
    @GuardedBy({ "mCallbacks" })
    private final ArrayList<SecurityControllerCallback> mCallbacks;
    private final ConnectivityManager mConnectivityManager;
    private final IConnectivityManager mConnectivityManagerService;
    private final Context mContext;
    private int mCurrentUserId;
    private SparseArray<VpnConfig> mCurrentVpns;
    private final DevicePolicyManager mDevicePolicyManager;
    private ArrayMap<Integer, Boolean> mHasCACerts;
    private final ConnectivityManager$NetworkCallback mNetworkCallback;
    private final PackageManager mPackageManager;
    private final UserManager mUserManager;
    private int mVpnUserId;
    
    static {
        DEBUG = Log.isLoggable("SecurityController", 3);
        REQUEST = new NetworkRequest$Builder().removeCapability(15).removeCapability(13).removeCapability(14).setUids((Set)null).build();
    }
    
    public SecurityControllerImpl(final Context mContext, final Handler handler, final BroadcastDispatcher broadcastDispatcher, final Executor mBgExecutor) {
        super(broadcastDispatcher);
        this.mCallbacks = new ArrayList<SecurityControllerCallback>();
        this.mCurrentVpns = (SparseArray<VpnConfig>)new SparseArray();
        this.mHasCACerts = (ArrayMap<Integer, Boolean>)new ArrayMap();
        this.mNetworkCallback = new ConnectivityManager$NetworkCallback() {
            public void onAvailable(final Network network) {
                if (SecurityControllerImpl.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onAvailable ");
                    sb.append(network.netId);
                    Log.d("SecurityController", sb.toString());
                }
                SecurityControllerImpl.this.updateState();
                SecurityControllerImpl.this.fireCallbacks();
            }
            
            public void onLost(final Network network) {
                if (SecurityControllerImpl.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onLost ");
                    sb.append(network.netId);
                    Log.d("SecurityController", sb.toString());
                }
                SecurityControllerImpl.this.updateState();
                SecurityControllerImpl.this.fireCallbacks();
            }
        };
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("android.security.action.TRUST_STORE_CHANGED".equals(intent.getAction())) {
                    SecurityControllerImpl.this.refreshCACerts(this.getSendingUserId());
                }
                else if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
                    final int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
                    if (intExtra != -10000) {
                        SecurityControllerImpl.this.refreshCACerts(intExtra);
                    }
                }
            }
        };
        this.mContext = mContext;
        this.mDevicePolicyManager = (DevicePolicyManager)mContext.getSystemService("device_policy");
        this.mConnectivityManager = (ConnectivityManager)mContext.getSystemService("connectivity");
        this.mConnectivityManagerService = IConnectivityManager$Stub.asInterface(ServiceManager.getService("connectivity"));
        this.mPackageManager = mContext.getPackageManager();
        this.mUserManager = (UserManager)mContext.getSystemService("user");
        this.mBgExecutor = mBgExecutor;
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.security.action.TRUST_STORE_CHANGED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        broadcastDispatcher.registerReceiverWithHandler(this.mBroadcastReceiver, intentFilter, handler, UserHandle.ALL);
        this.mConnectivityManager.registerNetworkCallback(SecurityControllerImpl.REQUEST, this.mNetworkCallback);
        this.onUserSwitched(ActivityManager.getCurrentUser());
        this.startTracking();
    }
    
    private void fireCallbacks() {
        synchronized (this.mCallbacks) {
            final Iterator<SecurityControllerCallback> iterator = this.mCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onStateChanged();
            }
        }
    }
    
    private String getNameForVpnConfig(VpnConfig user, final UserHandle userHandle) {
        if (user.legacy) {
            return this.mContext.getString(R$string.legacy_vpn_name);
        }
        user = (VpnConfig)user.user;
        try {
            return VpnConfig.getVpnLabel(this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, userHandle), (String)user).toString();
        }
        catch (PackageManager$NameNotFoundException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Package ");
            sb.append((String)user);
            sb.append(" is not present");
            Log.e("SecurityController", sb.toString(), (Throwable)ex);
            return null;
        }
    }
    
    private String getPackageNameForVpnConfig(final VpnConfig vpnConfig) {
        if (vpnConfig.legacy) {
            return null;
        }
        return vpnConfig.user;
    }
    
    private int getWorkProfileUserId(final int n) {
        for (final UserInfo userInfo : this.mUserManager.getProfiles(n)) {
            if (userInfo.isManagedProfile()) {
                return userInfo.id;
            }
        }
        return -10000;
    }
    
    private boolean isVpnPackageBranded(final String s) {
        try {
            final ApplicationInfo applicationInfo = this.mPackageManager.getApplicationInfo(s, 128);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                if (applicationInfo.isSystemApp()) {
                    return applicationInfo.metaData.getBoolean("com.android.systemui.IS_BRANDED", false);
                }
            }
            return false;
        }
        catch (PackageManager$NameNotFoundException ex) {
            return false;
        }
    }
    
    private void refreshCACerts(final int n) {
        this.mBgExecutor.execute(new _$$Lambda$SecurityControllerImpl$gIe4Ly5u4oeRcLYZFLgXwmhKZ40(this, n));
    }
    
    private void updateState() {
        final SparseArray mCurrentVpns = new SparseArray();
        try {
            for (final UserInfo userInfo : this.mUserManager.getUsers()) {
                final VpnConfig vpnConfig = this.mConnectivityManagerService.getVpnConfig(userInfo.id);
                if (vpnConfig == null) {
                    continue;
                }
                if (vpnConfig.legacy) {
                    final LegacyVpnInfo legacyVpnInfo = this.mConnectivityManagerService.getLegacyVpnInfo(userInfo.id);
                    if (legacyVpnInfo == null) {
                        continue;
                    }
                    if (legacyVpnInfo.state != 3) {
                        continue;
                    }
                }
                mCurrentVpns.put(userInfo.id, (Object)vpnConfig);
            }
            this.mCurrentVpns = (SparseArray<VpnConfig>)mCurrentVpns;
        }
        catch (RemoteException ex) {
            Log.e("SecurityController", "Unable to list active VPNs", (Throwable)ex);
        }
    }
    
    @Override
    public void addCallback(final SecurityControllerCallback e) {
        final ArrayList<SecurityControllerCallback> mCallbacks = this.mCallbacks;
        // monitorenter(mCallbacks)
        if (e == null) {
            return;
        }
        try {
            if (this.mCallbacks.contains(e)) {
                return;
            }
            if (SecurityControllerImpl.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("addCallback ");
                sb.append(e);
                Log.d("SecurityController", sb.toString());
            }
            this.mCallbacks.add(e);
        }
        finally {
        }
        // monitorexit(mCallbacks)
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("SecurityController state:");
        printWriter.print("  mCurrentVpns={");
        for (int i = 0; i < this.mCurrentVpns.size(); ++i) {
            if (i > 0) {
                printWriter.print(", ");
            }
            printWriter.print(this.mCurrentVpns.keyAt(i));
            printWriter.print('=');
            printWriter.print(((VpnConfig)this.mCurrentVpns.valueAt(i)).user);
        }
        printWriter.println("}");
    }
    
    @Override
    public CharSequence getDeviceOwnerOrganizationName() {
        return this.mDevicePolicyManager.getDeviceOwnerOrganizationName();
    }
    
    @Override
    public String getPrimaryVpnName() {
        final VpnConfig vpnConfig = (VpnConfig)this.mCurrentVpns.get(this.mVpnUserId);
        if (vpnConfig != null) {
            return this.getNameForVpnConfig(vpnConfig, new UserHandle(this.mVpnUserId));
        }
        return null;
    }
    
    @Override
    public CharSequence getWorkProfileOrganizationName() {
        final int workProfileUserId = this.getWorkProfileUserId(this.mCurrentUserId);
        if (workProfileUserId == -10000) {
            return null;
        }
        return this.mDevicePolicyManager.getOrganizationNameForUser(workProfileUserId);
    }
    
    @Override
    public String getWorkProfileVpnName() {
        final int workProfileUserId = this.getWorkProfileUserId(this.mVpnUserId);
        if (workProfileUserId == -10000) {
            return null;
        }
        final VpnConfig vpnConfig = (VpnConfig)this.mCurrentVpns.get(workProfileUserId);
        if (vpnConfig != null) {
            return this.getNameForVpnConfig(vpnConfig, UserHandle.of(workProfileUserId));
        }
        return null;
    }
    
    @Override
    public boolean hasCACertInCurrentUser() {
        final Boolean b = (Boolean)this.mHasCACerts.get((Object)this.mCurrentUserId);
        return b != null && b;
    }
    
    @Override
    public boolean hasCACertInWorkProfile() {
        final int workProfileUserId = this.getWorkProfileUserId(this.mCurrentUserId);
        final boolean b = false;
        if (workProfileUserId == -10000) {
            return false;
        }
        final Boolean b2 = (Boolean)this.mHasCACerts.get((Object)workProfileUserId);
        boolean b3 = b;
        if (b2 != null) {
            b3 = b;
            if (b2) {
                b3 = true;
            }
        }
        return b3;
    }
    
    @Override
    public boolean hasWorkProfile() {
        return this.getWorkProfileUserId(this.mCurrentUserId) != -10000;
    }
    
    @Override
    public boolean isDeviceManaged() {
        return this.mDevicePolicyManager.isDeviceManaged();
    }
    
    @Override
    public boolean isNetworkLoggingEnabled() {
        return this.mDevicePolicyManager.isNetworkLoggingEnabled((ComponentName)null);
    }
    
    @Override
    public boolean isVpnBranded() {
        final VpnConfig vpnConfig = (VpnConfig)this.mCurrentVpns.get(this.mVpnUserId);
        if (vpnConfig == null) {
            return false;
        }
        final String packageNameForVpnConfig = this.getPackageNameForVpnConfig(vpnConfig);
        return packageNameForVpnConfig != null && this.isVpnPackageBranded(packageNameForVpnConfig);
    }
    
    @Override
    public boolean isVpnEnabled() {
        final int[] profileIdsWithDisabled = this.mUserManager.getProfileIdsWithDisabled(this.mVpnUserId);
        for (int length = profileIdsWithDisabled.length, i = 0; i < length; ++i) {
            if (this.mCurrentVpns.get(profileIdsWithDisabled[i]) != null) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void onUserSwitched(final int mCurrentUserId) {
        this.mCurrentUserId = mCurrentUserId;
        final UserInfo userInfo = this.mUserManager.getUserInfo(mCurrentUserId);
        if (userInfo.isRestricted()) {
            this.mVpnUserId = userInfo.restrictedProfileParentId;
        }
        else {
            this.mVpnUserId = this.mCurrentUserId;
        }
        this.fireCallbacks();
    }
    
    @Override
    public void removeCallback(final SecurityControllerCallback securityControllerCallback) {
        final ArrayList<SecurityControllerCallback> mCallbacks = this.mCallbacks;
        // monitorenter(mCallbacks)
        Label_0014: {
            if (securityControllerCallback != null) {
                break Label_0014;
            }
            try {
                // monitorexit(mCallbacks)
                return;
                while (true) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("removeCallback ");
                    sb.append(securityControllerCallback);
                    Log.d("SecurityController", sb.toString());
                    Label_0052: {
                        this.mCallbacks.remove(securityControllerCallback);
                    }
                    return;
                    continue;
                }
            }
            // iftrue(Label_0052:, !SecurityControllerImpl.DEBUG)
            finally {
            }
            // monitorexit(mCallbacks)
        }
    }
}
