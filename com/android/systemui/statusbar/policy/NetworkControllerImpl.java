// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.os.PersistableBundle;
import android.content.res.Resources;
import com.android.systemui.R$bool;
import android.os.AsyncTask;
import com.android.internal.annotations.GuardedBy;
import java.util.Comparator;
import android.content.Intent;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.Collections;
import android.telephony.CellSignalStrength;
import android.text.TextUtils;
import android.util.MathUtils;
import android.os.Bundle;
import android.content.ContentResolver;
import android.provider.Settings$Global;
import android.content.IntentFilter;
import com.android.systemui.R$string;
import android.telephony.CarrierConfigManager;
import android.telephony.UiccAccessRule;
import android.graphics.Bitmap;
import java.util.concurrent.Executor;
import java.util.Objects;
import android.net.NetworkCapabilities;
import android.net.Network;
import android.net.ConnectivityManager$NetworkCallback;
import java.util.ArrayList;
import android.os.Looper;
import android.net.NetworkScoreManager;
import android.util.Log;
import android.net.wifi.WifiManager;
import com.android.systemui.settings.CurrentUserTracker;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionManager$OnSubscriptionsChangedListener;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.SparseArray;
import java.util.Locale;
import android.telephony.ServiceState;
import com.android.internal.annotations.VisibleForTesting;
import android.telephony.SubscriptionInfo;
import java.util.List;
import android.content.Context;
import android.net.ConnectivityManager;
import java.util.BitSet;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.Dumpable;
import com.android.settingslib.net.DataUsageController;
import com.android.systemui.DemoMode;
import android.content.BroadcastReceiver;

public class NetworkControllerImpl extends BroadcastReceiver implements NetworkController, DemoMode, NetworkNameProvider, Dumpable
{
    static final boolean CHATTY;
    static final boolean DEBUG;
    private final AccessPointControllerImpl mAccessPoints;
    private int mActiveMobileDataSubscription;
    private boolean mAirplaneMode;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final CallbackHandler mCallbackHandler;
    private final Runnable mClearForceValidated;
    private Config mConfig;
    private final BitSet mConnectedTransports;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private List<SubscriptionInfo> mCurrentSubscriptions;
    private int mCurrentUserId;
    private final DataSaverController mDataSaverController;
    private final DataUsageController mDataUsageController;
    private MobileSignalController mDefaultSignalController;
    private boolean mDemoInetCondition;
    private boolean mDemoMode;
    private WifiSignalController.WifiState mDemoWifiState;
    private int mEmergencySource;
    @VisibleForTesting
    final EthernetSignalController mEthernetSignalController;
    private boolean mForceCellularValidated;
    private final boolean mHasMobileDataFeature;
    private boolean mHasNoSubs;
    private boolean mInetCondition;
    private boolean mIsEmergency;
    @VisibleForTesting
    ServiceState mLastServiceState;
    @VisibleForTesting
    boolean mListening;
    private Locale mLocale;
    private final Object mLock;
    @VisibleForTesting
    final SparseArray<MobileSignalController> mMobileSignalControllers;
    private final TelephonyManager mPhone;
    private PhoneStateListener mPhoneStateListener;
    private final Handler mReceiverHandler;
    private final Runnable mRegisterListeners;
    private boolean mSimDetected;
    private final SubscriptionDefaults mSubDefaults;
    private SubscriptionManager$OnSubscriptionsChangedListener mSubscriptionListener;
    private final SubscriptionManager mSubscriptionManager;
    private boolean mUserSetup;
    private final CurrentUserTracker mUserTracker;
    private final BitSet mValidatedTransports;
    private final WifiManager mWifiManager;
    @VisibleForTesting
    final WifiSignalController mWifiSignalController;
    
    static {
        DEBUG = Log.isLoggable("NetworkController", 3);
        CHATTY = Log.isLoggable("NetworkControllerChat", 3);
    }
    
    @VisibleForTesting
    NetworkControllerImpl(final Context mContext, final ConnectivityManager mConnectivityManager, final TelephonyManager mPhone, final WifiManager mWifiManager, final NetworkScoreManager networkScoreManager, final SubscriptionManager mSubscriptionManager, final Config mConfig, final Looper looper, final CallbackHandler mCallbackHandler, final AccessPointControllerImpl mAccessPoints, final DataUsageController mDataUsageController, final SubscriptionDefaults mSubDefaults, final DeviceProvisionedController deviceProvisionedController, final BroadcastDispatcher mBroadcastDispatcher) {
        this.mLock = new Object();
        this.mActiveMobileDataSubscription = -1;
        this.mMobileSignalControllers = (SparseArray<MobileSignalController>)new SparseArray();
        this.mConnectedTransports = new BitSet();
        this.mValidatedTransports = new BitSet();
        this.mAirplaneMode = false;
        this.mLocale = null;
        this.mCurrentSubscriptions = new ArrayList<SubscriptionInfo>();
        this.mClearForceValidated = new _$$Lambda$NetworkControllerImpl$oNWIIIg3gBRqx9jT8qywGtEkW2E(this);
        this.mRegisterListeners = new Runnable() {
            @Override
            public void run() {
                NetworkControllerImpl.this.registerListeners();
            }
        };
        this.mContext = mContext;
        this.mConfig = mConfig;
        this.mReceiverHandler = new Handler(looper);
        this.mCallbackHandler = mCallbackHandler;
        this.mDataSaverController = new DataSaverControllerImpl(mContext);
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mSubscriptionManager = mSubscriptionManager;
        this.mSubDefaults = mSubDefaults;
        this.mConnectivityManager = mConnectivityManager;
        this.mHasMobileDataFeature = mConnectivityManager.isNetworkSupported(0);
        this.mPhone = mPhone;
        this.mWifiManager = mWifiManager;
        this.mLocale = this.mContext.getResources().getConfiguration().locale;
        this.mAccessPoints = mAccessPoints;
        (this.mDataUsageController = mDataUsageController).setNetworkController((DataUsageController.NetworkNameProvider)this);
        this.mDataUsageController.setCallback((DataUsageController.Callback)new Callback() {
            @Override
            public void onMobileDataEnabled(final boolean mobileDataEnabled) {
                NetworkControllerImpl.this.mCallbackHandler.setMobileDataEnabled(mobileDataEnabled);
                NetworkControllerImpl.this.notifyControllersMobileDataChanged();
            }
        });
        this.mWifiSignalController = new WifiSignalController(this.mContext, this.mHasMobileDataFeature, this.mCallbackHandler, this, this.mWifiManager, this.mConnectivityManager, networkScoreManager);
        this.mEthernetSignalController = new EthernetSignalController(this.mContext, this.mCallbackHandler, this);
        this.updateAirplaneMode(true);
        (this.mUserTracker = new CurrentUserTracker(mBroadcastDispatcher) {
            @Override
            public void onUserSwitched(final int n) {
                NetworkControllerImpl.this.onUserSwitched(n);
            }
        }).startTracking();
        deviceProvisionedController.addCallback((DeviceProvisionedController.DeviceProvisionedListener)new DeviceProvisionedController.DeviceProvisionedListener() {
            @Override
            public void onUserSetupChanged() {
                final NetworkControllerImpl this$0 = NetworkControllerImpl.this;
                final DeviceProvisionedController val$deviceProvisionedController = deviceProvisionedController;
                this$0.setUserSetupComplete(val$deviceProvisionedController.isUserSetup(val$deviceProvisionedController.getCurrentUser()));
            }
        });
        this.mConnectivityManager.registerDefaultNetworkCallback((ConnectivityManager$NetworkCallback)new ConnectivityManager$NetworkCallback() {
            private Network mLastNetwork;
            private NetworkCapabilities mLastNetworkCapabilities;
            
            public void onCapabilitiesChanged(final Network mLastNetwork, final NetworkCapabilities mLastNetworkCapabilities) {
                final NetworkCapabilities mLastNetworkCapabilities2 = this.mLastNetworkCapabilities;
                final boolean b = mLastNetworkCapabilities2 != null && mLastNetworkCapabilities2.hasCapability(16);
                final boolean hasCapability = mLastNetworkCapabilities.hasCapability(16);
                if (mLastNetwork.equals((Object)this.mLastNetwork) && mLastNetworkCapabilities.equalsTransportTypes(this.mLastNetworkCapabilities) && hasCapability == b) {
                    return;
                }
                this.mLastNetwork = mLastNetwork;
                this.mLastNetworkCapabilities = mLastNetworkCapabilities;
                NetworkControllerImpl.this.updateConnectivity();
            }
        }, this.mReceiverHandler);
        final Handler mReceiverHandler = this.mReceiverHandler;
        Objects.requireNonNull(mReceiverHandler);
        this.mPhoneStateListener = new PhoneStateListener(new _$$Lambda$LfzJt661qZfn2w_6SYHFbD3aMy0(mReceiverHandler)) {
            public void onActiveDataSubscriptionIdChanged(final int n) {
                final NetworkControllerImpl this$0 = NetworkControllerImpl.this;
                if (this$0.keepCellularValidationBitInSwitch(this$0.mActiveMobileDataSubscription, n)) {
                    if (NetworkControllerImpl.DEBUG) {
                        Log.d("NetworkController", ": mForceCellularValidated to true.");
                    }
                    NetworkControllerImpl.this.mForceCellularValidated = true;
                    NetworkControllerImpl.this.mReceiverHandler.removeCallbacks(NetworkControllerImpl.this.mClearForceValidated);
                    NetworkControllerImpl.this.mReceiverHandler.postDelayed(NetworkControllerImpl.this.mClearForceValidated, 2000L);
                }
                NetworkControllerImpl.this.mActiveMobileDataSubscription = n;
                NetworkControllerImpl.this.doUpdateMobileControllers();
            }
        };
    }
    
    public NetworkControllerImpl(final Context context, final Looper looper, final DeviceProvisionedController deviceProvisionedController, final BroadcastDispatcher broadcastDispatcher, final ConnectivityManager connectivityManager, final TelephonyManager telephonyManager, final WifiManager wifiManager, final NetworkScoreManager networkScoreManager) {
        this(context, connectivityManager, telephonyManager, wifiManager, networkScoreManager, SubscriptionManager.from(context), Config.readConfig(context), looper, new CallbackHandler(), new AccessPointControllerImpl(context), new DataUsageController(context), new SubscriptionDefaults(), deviceProvisionedController, broadcastDispatcher);
        this.mReceiverHandler.post(this.mRegisterListeners);
    }
    
    private SubscriptionInfo addSignalController(final int n, final int n2) {
        final SubscriptionInfo subscriptionInfo = new SubscriptionInfo(n, "", n2, (CharSequence)"", (CharSequence)"", 0, 0, "", 0, (Bitmap)null, (String)null, (String)null, "", false, (UiccAccessRule[])null, (String)null);
        final MobileSignalController mobileSignalController = new MobileSignalController(this.mContext, this.mConfig, this.mHasMobileDataFeature, this.mPhone.createForSubscriptionId(subscriptionInfo.getSubscriptionId()), this.mCallbackHandler, this, subscriptionInfo, this.mSubDefaults, this.mReceiverHandler.getLooper());
        this.mMobileSignalControllers.put(n, (Object)mobileSignalController);
        ((SignalController<MobileSignalController.MobileState, I>)mobileSignalController).getState().userSetup = true;
        return subscriptionInfo;
    }
    
    private static final String emergencyToString(final int n) {
        if (n > 300) {
            final StringBuilder sb = new StringBuilder();
            sb.append("ASSUMED_VOICE_CONTROLLER(");
            sb.append(n - 200);
            sb.append(")");
            return sb.toString();
        }
        if (n > 300) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("NO_SUB(");
            sb2.append(n - 300);
            sb2.append(")");
            return sb2.toString();
        }
        if (n > 200) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("VOICE_CONTROLLER(");
            sb3.append(n - 200);
            sb3.append(")");
            return sb3.toString();
        }
        if (n > 100) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("FIRST_CONTROLLER(");
            sb4.append(n - 100);
            sb4.append(")");
            return sb4.toString();
        }
        if (n == 0) {
            return "NO_CONTROLLERS";
        }
        return "UNKNOWN_SOURCE";
    }
    
    private void filterMobileSubscriptionInSameGroup(final List<SubscriptionInfo> list) {
        if (list.size() == 2) {
            SubscriptionInfo subscriptionInfo = list.get(0);
            final SubscriptionInfo subscriptionInfo2 = list.get(1);
            if (subscriptionInfo.getGroupUuid() != null && subscriptionInfo.getGroupUuid().equals((Object)subscriptionInfo2.getGroupUuid())) {
                if (!subscriptionInfo.isOpportunistic() && !subscriptionInfo2.isOpportunistic()) {
                    return;
                }
                if (CarrierConfigManager.getDefaultConfig().getBoolean("always_show_primary_signal_bar_in_opportunistic_network_boolean")) {
                    if (!subscriptionInfo.isOpportunistic()) {
                        subscriptionInfo = subscriptionInfo2;
                    }
                    list.remove(subscriptionInfo);
                }
                else {
                    SubscriptionInfo subscriptionInfo3 = subscriptionInfo;
                    if (subscriptionInfo.getSubscriptionId() == this.mActiveMobileDataSubscription) {
                        subscriptionInfo3 = subscriptionInfo2;
                    }
                    list.remove(subscriptionInfo3);
                }
            }
        }
    }
    
    private MobileSignalController getDataController() {
        final int activeDataSubId = this.mSubDefaults.getActiveDataSubId();
        if (!SubscriptionManager.isValidSubscriptionId(activeDataSubId)) {
            if (NetworkControllerImpl.DEBUG) {
                Log.e("NetworkController", "No data sim selected");
            }
            return this.mDefaultSignalController;
        }
        if (this.mMobileSignalControllers.indexOfKey(activeDataSubId) >= 0) {
            return (MobileSignalController)this.mMobileSignalControllers.get(activeDataSubId);
        }
        if (NetworkControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot find controller for data sub: ");
            sb.append(activeDataSubId);
            Log.e("NetworkController", sb.toString());
        }
        return this.mDefaultSignalController;
    }
    
    private void handleSetUserSetupComplete(final boolean mUserSetup) {
        this.mUserSetup = mUserSetup;
        for (int i = 0; i < this.mMobileSignalControllers.size(); ++i) {
            ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).setUserSetupComplete(this.mUserSetup);
        }
    }
    
    private boolean hasAnySim() {
        for (int activeModemCount = this.mPhone.getActiveModemCount(), i = 0; i < activeModemCount; ++i) {
            final int simState = this.mPhone.getSimState(i);
            if (simState != 1 && simState != 0) {
                return true;
            }
        }
        return false;
    }
    
    private void notifyAllListeners() {
        this.notifyListeners();
        for (int i = 0; i < this.mMobileSignalControllers.size(); ++i) {
            ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).notifyListeners();
        }
        this.mWifiSignalController.notifyListeners();
        this.mEthernetSignalController.notifyListeners();
    }
    
    private void notifyControllersMobileDataChanged() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); ++i) {
            ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).onMobileDataChanged();
        }
    }
    
    private void notifyListeners() {
        this.mCallbackHandler.setIsAirplaneMode(new IconState(this.mAirplaneMode, TelephonyIcons.FLIGHT_MODE_ICON, R$string.accessibility_airplane_mode, this.mContext));
        this.mCallbackHandler.setNoSims(this.mHasNoSubs, this.mSimDetected);
    }
    
    private void onUserSwitched(final int mCurrentUserId) {
        this.mCurrentUserId = mCurrentUserId;
        this.mAccessPoints.onUserSwitched(mCurrentUserId);
        this.updateConnectivity();
    }
    
    private void pushConnectivityToSignals() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); ++i) {
            ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
        }
        this.mWifiSignalController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
        this.mEthernetSignalController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
    }
    
    private void refreshLocale() {
        final Locale locale = this.mContext.getResources().getConfiguration().locale;
        if (!locale.equals(this.mLocale)) {
            this.mLocale = locale;
            this.mWifiSignalController.refreshLocale();
            this.notifyAllListeners();
        }
    }
    
    private void registerListeners() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); ++i) {
            ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).registerListener();
        }
        if (this.mSubscriptionListener == null) {
            this.mSubscriptionListener = new SubListener();
        }
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionListener);
        this.mPhone.listen(this.mPhoneStateListener, 4194304);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.intent.action.SERVICE_STATE");
        intentFilter.addAction("android.telephony.action.SERVICE_PROVIDERS_UPDATED");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.conn.INET_CONDITION_ACTION");
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, this.mReceiverHandler);
        this.mListening = true;
        this.updateMobileControllers();
    }
    
    private void setUserSetupComplete(final boolean b) {
        this.mReceiverHandler.post((Runnable)new _$$Lambda$NetworkControllerImpl$8S0AEfzpddaLuo_a_D4xFAJ8V58(this, b));
    }
    
    private void unregisterListeners() {
        int i = 0;
        this.mListening = false;
        while (i < this.mMobileSignalControllers.size()) {
            ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).unregisterListener();
            ++i;
        }
        this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mSubscriptionListener);
        this.mContext.unregisterReceiver((BroadcastReceiver)this);
    }
    
    private void updateAirplaneMode(final boolean b) {
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        int i = 0;
        final int int1 = Settings$Global.getInt(contentResolver, "airplane_mode_on", 0);
        boolean mAirplaneMode = true;
        if (int1 != 1) {
            mAirplaneMode = false;
        }
        if (mAirplaneMode != this.mAirplaneMode || b) {
            this.mAirplaneMode = mAirplaneMode;
            while (i < this.mMobileSignalControllers.size()) {
                ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).setAirplaneMode(this.mAirplaneMode);
                ++i;
            }
            this.notifyListeners();
        }
    }
    
    private void updateConnectivity() {
        this.mConnectedTransports.clear();
        this.mValidatedTransports.clear();
        for (final NetworkCapabilities networkCapabilities : this.mConnectivityManager.getDefaultNetworkCapabilitiesForUser(this.mCurrentUserId)) {
            for (final int n : networkCapabilities.getTransportTypes()) {
                this.mConnectedTransports.set(n);
                if (networkCapabilities.hasCapability(16)) {
                    this.mValidatedTransports.set(n);
                }
            }
        }
        if (this.mForceCellularValidated) {
            this.mValidatedTransports.set(0);
        }
        if (NetworkControllerImpl.CHATTY) {
            final StringBuilder sb = new StringBuilder();
            sb.append("updateConnectivity: mConnectedTransports=");
            sb.append(this.mConnectedTransports);
            Log.d("NetworkController", sb.toString());
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("updateConnectivity: mValidatedTransports=");
            sb2.append(this.mValidatedTransports);
            Log.d("NetworkController", sb2.toString());
        }
        this.mInetCondition = (this.mValidatedTransports.isEmpty() ^ true);
        this.pushConnectivityToSignals();
    }
    
    private void updateMobileControllers() {
        if (!this.mListening) {
            return;
        }
        this.doUpdateMobileControllers();
    }
    
    public void addCallback(final SignalCallback signalCallback) {
        signalCallback.setSubs(this.mCurrentSubscriptions);
        signalCallback.setIsAirplaneMode(new IconState(this.mAirplaneMode, TelephonyIcons.FLIGHT_MODE_ICON, R$string.accessibility_airplane_mode, this.mContext));
        signalCallback.setNoSims(this.mHasNoSubs, this.mSimDetected);
        this.mWifiSignalController.notifyListeners(signalCallback);
        this.mEthernetSignalController.notifyListeners(signalCallback);
        for (int i = 0; i < this.mMobileSignalControllers.size(); ++i) {
            ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).notifyListeners(signalCallback);
        }
        this.mCallbackHandler.setListening(signalCallback, true);
    }
    
    public void dispatchDemoCommand(String s, final Bundle bundle) {
        if (!this.mDemoMode && s.equals("enter")) {
            if (NetworkControllerImpl.DEBUG) {
                Log.d("NetworkController", "Entering demo mode");
            }
            this.unregisterListeners();
            this.mDemoMode = true;
            this.mDemoInetCondition = this.mInetCondition;
            final WifiSignalController.WifiState mDemoWifiState = ((SignalController<WifiSignalController.WifiState, I>)this.mWifiSignalController).getState();
            this.mDemoWifiState = mDemoWifiState;
            mDemoWifiState.ssid = "DemoMode";
        }
        else {
            final boolean mDemoMode = this.mDemoMode;
            int i = 0;
            if (mDemoMode && s.equals("exit")) {
                if (NetworkControllerImpl.DEBUG) {
                    Log.d("NetworkController", "Exiting demo mode");
                }
                this.mDemoMode = false;
                this.updateMobileControllers();
                while (i < this.mMobileSignalControllers.size()) {
                    ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).resetLastState();
                    ++i;
                }
                this.mWifiSignalController.resetLastState();
                this.mReceiverHandler.post(this.mRegisterListeners);
                this.notifyAllListeners();
            }
            else if (this.mDemoMode && s.equals("network")) {
                s = bundle.getString("airplane");
                if (s != null) {
                    this.mCallbackHandler.setIsAirplaneMode(new IconState(s.equals("show"), TelephonyIcons.FLIGHT_MODE_ICON, R$string.accessibility_airplane_mode, this.mContext));
                }
                s = bundle.getString("fully");
                if (s != null) {
                    this.mDemoInetCondition = Boolean.parseBoolean(s);
                    final BitSet set = new BitSet();
                    if (this.mDemoInetCondition) {
                        set.set(this.mWifiSignalController.mTransportType);
                    }
                    this.mWifiSignalController.updateConnectivity(set, set);
                    for (int j = 0; j < this.mMobileSignalControllers.size(); ++j) {
                        final MobileSignalController mobileSignalController = (MobileSignalController)this.mMobileSignalControllers.valueAt(j);
                        if (this.mDemoInetCondition) {
                            set.set(mobileSignalController.mTransportType);
                        }
                        mobileSignalController.updateConnectivity(set, set);
                    }
                }
                s = bundle.getString("wifi");
                if (s != null) {
                    final boolean equals = s.equals("show");
                    s = bundle.getString("level");
                    if (s != null) {
                        final WifiSignalController.WifiState mDemoWifiState2 = this.mDemoWifiState;
                        int min;
                        if (s.equals("null")) {
                            min = -1;
                        }
                        else {
                            min = Math.min(Integer.parseInt(s), WifiIcons.WIFI_LEVEL_COUNT - 1);
                        }
                        mDemoWifiState2.level = min;
                        final WifiSignalController.WifiState mDemoWifiState3 = this.mDemoWifiState;
                        mDemoWifiState3.connected = (mDemoWifiState3.level >= 0);
                    }
                    s = bundle.getString("activity");
                    if (s != null) {
                        final int hashCode = s.hashCode();
                        int n = 0;
                        Label_0551: {
                            if (hashCode != 3365) {
                                if (hashCode != 110414) {
                                    if (hashCode == 100357129) {
                                        if (s.equals("inout")) {
                                            n = 0;
                                            break Label_0551;
                                        }
                                    }
                                }
                                else if (s.equals("out")) {
                                    n = 2;
                                    break Label_0551;
                                }
                            }
                            else if (s.equals("in")) {
                                n = 1;
                                break Label_0551;
                            }
                            n = -1;
                        }
                        if (n != 0) {
                            if (n != 1) {
                                if (n != 2) {
                                    this.mWifiSignalController.setActivity(0);
                                }
                                else {
                                    this.mWifiSignalController.setActivity(2);
                                }
                            }
                            else {
                                this.mWifiSignalController.setActivity(1);
                            }
                        }
                        else {
                            this.mWifiSignalController.setActivity(3);
                        }
                    }
                    else {
                        this.mWifiSignalController.setActivity(0);
                    }
                    s = bundle.getString("ssid");
                    if (s != null) {
                        this.mDemoWifiState.ssid = s;
                    }
                    this.mDemoWifiState.enabled = equals;
                    this.mWifiSignalController.notifyListeners();
                }
                s = bundle.getString("sims");
                if (s != null) {
                    final int constrain = MathUtils.constrain(Integer.parseInt(s), 1, 8);
                    final ArrayList<SubscriptionInfo> subs = new ArrayList<SubscriptionInfo>();
                    if (constrain != this.mMobileSignalControllers.size()) {
                        this.mMobileSignalControllers.clear();
                        int k = 0;
                        while (k < (k = this.mSubscriptionManager.getActiveSubscriptionInfoCountMax()) + constrain) {
                            subs.add(this.addSignalController(k, k));
                            ++k;
                        }
                        this.mCallbackHandler.setSubs(subs);
                        for (int l = 0; l < this.mMobileSignalControllers.size(); ++l) {
                            ((MobileSignalController)this.mMobileSignalControllers.get(this.mMobileSignalControllers.keyAt(l))).notifyListeners();
                        }
                    }
                }
                s = bundle.getString("nosim");
                if (s != null) {
                    final boolean equals2 = s.equals("show");
                    this.mHasNoSubs = equals2;
                    this.mCallbackHandler.setNoSims(equals2, this.mSimDetected);
                }
                s = bundle.getString("mobile");
                if (s != null) {
                    final boolean equals3 = s.equals("show");
                    s = bundle.getString("datatype");
                    final String string = bundle.getString("slot");
                    int int1;
                    if (TextUtils.isEmpty((CharSequence)string)) {
                        int1 = 0;
                    }
                    else {
                        int1 = Integer.parseInt(string);
                    }
                    final int constrain2 = MathUtils.constrain(int1, 0, 8);
                    final ArrayList<SubscriptionInfo> subs2 = new ArrayList<SubscriptionInfo>();
                    while (this.mMobileSignalControllers.size() <= constrain2) {
                        final int size = this.mMobileSignalControllers.size();
                        subs2.add(this.addSignalController(size, size));
                    }
                    if (!subs2.isEmpty()) {
                        this.mCallbackHandler.setSubs(subs2);
                    }
                    final MobileSignalController mobileSignalController2 = (MobileSignalController)this.mMobileSignalControllers.valueAt(constrain2);
                    ((SignalController<MobileSignalController.MobileState, I>)mobileSignalController2).getState().dataSim = (s != null);
                    ((SignalController<MobileSignalController.MobileState, I>)mobileSignalController2).getState().isDefault = (s != null);
                    ((SignalController<MobileSignalController.MobileState, I>)mobileSignalController2).getState().dataConnected = (s != null);
                    if (s != null) {
                        final MobileSignalController.MobileState mobileState = ((SignalController<MobileSignalController.MobileState, I>)mobileSignalController2).getState();
                        MobileSignalController.MobileIconGroup iconGroup;
                        if (s.equals("1x")) {
                            iconGroup = TelephonyIcons.ONE_X;
                        }
                        else if (s.equals("3g")) {
                            iconGroup = TelephonyIcons.THREE_G;
                        }
                        else if (s.equals("4g")) {
                            iconGroup = TelephonyIcons.FOUR_G;
                        }
                        else if (s.equals("4g+")) {
                            iconGroup = TelephonyIcons.FOUR_G_PLUS;
                        }
                        else if (s.equals("5g")) {
                            iconGroup = TelephonyIcons.NR_5G;
                        }
                        else if (s.equals("5ge")) {
                            iconGroup = TelephonyIcons.LTE_CA_5G_E;
                        }
                        else if (s.equals("5g+")) {
                            iconGroup = TelephonyIcons.NR_5G_PLUS;
                        }
                        else if (s.equals("e")) {
                            iconGroup = TelephonyIcons.E;
                        }
                        else if (s.equals("g")) {
                            iconGroup = TelephonyIcons.G;
                        }
                        else if (s.equals("h")) {
                            iconGroup = TelephonyIcons.H;
                        }
                        else if (s.equals("h+")) {
                            iconGroup = TelephonyIcons.H_PLUS;
                        }
                        else if (s.equals("lte")) {
                            iconGroup = TelephonyIcons.LTE;
                        }
                        else if (s.equals("lte+")) {
                            iconGroup = TelephonyIcons.LTE_PLUS;
                        }
                        else if (s.equals("dis")) {
                            iconGroup = TelephonyIcons.DATA_DISABLED;
                        }
                        else if (s.equals("not")) {
                            iconGroup = TelephonyIcons.NOT_DEFAULT_DATA;
                        }
                        else {
                            iconGroup = TelephonyIcons.UNKNOWN;
                        }
                        mobileState.iconGroup = iconGroup;
                    }
                    if (bundle.containsKey("roam")) {
                        ((SignalController<MobileSignalController.MobileState, I>)mobileSignalController2).getState().roaming = "show".equals(bundle.getString("roam"));
                    }
                    s = bundle.getString("level");
                    if (s != null) {
                        final MobileSignalController.MobileState mobileState2 = ((SignalController<MobileSignalController.MobileState, I>)mobileSignalController2).getState();
                        int min2;
                        if (s.equals("null")) {
                            min2 = -1;
                        }
                        else {
                            min2 = Math.min(Integer.parseInt(s), CellSignalStrength.getNumSignalStrengthLevels());
                        }
                        mobileState2.level = min2;
                        ((SignalController<MobileSignalController.MobileState, I>)mobileSignalController2).getState().connected = (((SignalController<MobileSignalController.MobileState, I>)mobileSignalController2).getState().level >= 0);
                    }
                    if (bundle.containsKey("inflate")) {
                        for (int n2 = 0; n2 < this.mMobileSignalControllers.size(); ++n2) {
                            ((MobileSignalController)this.mMobileSignalControllers.valueAt(n2)).mInflateSignalStrengths = "true".equals(bundle.getString("inflate"));
                        }
                    }
                    s = bundle.getString("activity");
                    if (s != null) {
                        ((SignalController<MobileSignalController.MobileState, I>)mobileSignalController2).getState().dataConnected = true;
                        final int hashCode2 = s.hashCode();
                        int n3 = 0;
                        Label_1647: {
                            if (hashCode2 != 3365) {
                                if (hashCode2 != 110414) {
                                    if (hashCode2 == 100357129) {
                                        if (s.equals("inout")) {
                                            n3 = 0;
                                            break Label_1647;
                                        }
                                    }
                                }
                                else if (s.equals("out")) {
                                    n3 = 2;
                                    break Label_1647;
                                }
                            }
                            else if (s.equals("in")) {
                                n3 = 1;
                                break Label_1647;
                            }
                            n3 = -1;
                        }
                        if (n3 != 0) {
                            if (n3 != 1) {
                                if (n3 != 2) {
                                    mobileSignalController2.setActivity(0);
                                }
                                else {
                                    mobileSignalController2.setActivity(2);
                                }
                            }
                            else {
                                mobileSignalController2.setActivity(1);
                            }
                        }
                        else {
                            mobileSignalController2.setActivity(3);
                        }
                    }
                    else {
                        mobileSignalController2.setActivity(0);
                    }
                    ((SignalController<MobileSignalController.MobileState, I>)mobileSignalController2).getState().enabled = equals3;
                    mobileSignalController2.notifyListeners();
                }
                int n4 = 0;
                s = bundle.getString("carriernetworkchange");
                if (s != null) {
                    final boolean equals4 = s.equals("show");
                    while (n4 < this.mMobileSignalControllers.size()) {
                        ((MobileSignalController)this.mMobileSignalControllers.valueAt(n4)).setCarrierNetworkChangeMode(equals4);
                        ++n4;
                    }
                }
            }
        }
    }
    
    @VisibleForTesting
    void doUpdateMobileControllers() {
        List<SubscriptionInfo> currentSubscriptionsLocked;
        if ((currentSubscriptionsLocked = (List<SubscriptionInfo>)this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList()) == null) {
            currentSubscriptionsLocked = Collections.emptyList();
        }
        this.filterMobileSubscriptionInSameGroup(currentSubscriptionsLocked);
        if (this.hasCorrectMobileControllers(currentSubscriptionsLocked)) {
            this.updateNoSims();
            return;
        }
        synchronized (this.mLock) {
            this.setCurrentSubscriptionsLocked(currentSubscriptionsLocked);
            // monitorexit(this.mLock)
            this.updateNoSims();
            this.recalculateEmergency();
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("NetworkController state:");
        printWriter.println("  - telephony ------");
        printWriter.print("  hasVoiceCallingFeature()=");
        printWriter.println(this.hasVoiceCallingFeature());
        printWriter.println("  - connectivity ------");
        printWriter.print("  mConnectedTransports=");
        printWriter.println(this.mConnectedTransports);
        printWriter.print("  mValidatedTransports=");
        printWriter.println(this.mValidatedTransports);
        printWriter.print("  mInetCondition=");
        printWriter.println(this.mInetCondition);
        printWriter.print("  mAirplaneMode=");
        printWriter.println(this.mAirplaneMode);
        printWriter.print("  mLocale=");
        printWriter.println(this.mLocale);
        printWriter.print("  mLastServiceState=");
        printWriter.println(this.mLastServiceState);
        printWriter.print("  mIsEmergency=");
        printWriter.println(this.mIsEmergency);
        printWriter.print("  mEmergencySource=");
        printWriter.println(emergencyToString(this.mEmergencySource));
        printWriter.println("  - config ------");
        for (int i = 0; i < this.mMobileSignalControllers.size(); ++i) {
            ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).dump(printWriter);
        }
        this.mWifiSignalController.dump(printWriter);
        this.mEthernetSignalController.dump(printWriter);
        this.mAccessPoints.dump(printWriter);
    }
    
    public AccessPointController getAccessPointController() {
        return this.mAccessPoints;
    }
    
    public DataSaverController getDataSaverController() {
        return this.mDataSaverController;
    }
    
    public DataUsageController getMobileDataController() {
        return this.mDataUsageController;
    }
    
    public String getMobileDataNetworkName() {
        final MobileSignalController dataController = this.getDataController();
        String networkNameData;
        if (dataController != null) {
            networkNameData = ((SignalController<MobileSignalController.MobileState, I>)dataController).getState().networkNameData;
        }
        else {
            networkNameData = "";
        }
        return networkNameData;
    }
    
    public int getNumberSubscriptions() {
        return this.mMobileSignalControllers.size();
    }
    
    @VisibleForTesting
    void handleConfigurationChanged() {
        this.updateMobileControllers();
        for (int i = 0; i < this.mMobileSignalControllers.size(); ++i) {
            ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).setConfiguration(this.mConfig);
        }
        this.refreshLocale();
    }
    
    @VisibleForTesting
    boolean hasCorrectMobileControllers(final List<SubscriptionInfo> list) {
        if (list.size() != this.mMobileSignalControllers.size()) {
            return false;
        }
        final Iterator<SubscriptionInfo> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (this.mMobileSignalControllers.indexOfKey(iterator.next().getSubscriptionId()) < 0) {
                return false;
            }
        }
        return true;
    }
    
    public boolean hasEmergencyCryptKeeperText() {
        return EncryptionHelper.IS_DATA_ENCRYPTED;
    }
    
    public boolean hasMobileDataFeature() {
        return this.mHasMobileDataFeature;
    }
    
    public boolean hasVoiceCallingFeature() {
        return this.mPhone.getPhoneType() != 0;
    }
    
    boolean isDataControllerDisabled() {
        final MobileSignalController dataController = this.getDataController();
        return dataController != null && dataController.isDataDisabled();
    }
    
    public boolean isEmergencyOnly() {
        final int size = this.mMobileSignalControllers.size();
        boolean b = true;
        if (size == 0) {
            this.mEmergencySource = 0;
            final ServiceState mLastServiceState = this.mLastServiceState;
            if (mLastServiceState == null || !mLastServiceState.isEmergencyOnly()) {
                b = false;
            }
            return b;
        }
        final int defaultVoiceSubId = this.mSubDefaults.getDefaultVoiceSubId();
        if (!SubscriptionManager.isValidSubscriptionId(defaultVoiceSubId)) {
            for (int i = 0; i < this.mMobileSignalControllers.size(); ++i) {
                final MobileSignalController mobileSignalController = (MobileSignalController)this.mMobileSignalControllers.valueAt(i);
                if (!((SignalController<MobileSignalController.MobileState, I>)mobileSignalController).getState().isEmergency) {
                    this.mEmergencySource = mobileSignalController.mSubscriptionInfo.getSubscriptionId() + 100;
                    if (NetworkControllerImpl.DEBUG) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Found emergency ");
                        sb.append(mobileSignalController.mTag);
                        Log.d("NetworkController", sb.toString());
                    }
                    return false;
                }
            }
        }
        if (this.mMobileSignalControllers.indexOfKey(defaultVoiceSubId) >= 0) {
            this.mEmergencySource = defaultVoiceSubId + 200;
            if (NetworkControllerImpl.DEBUG) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Getting emergency from ");
                sb2.append(defaultVoiceSubId);
                Log.d("NetworkController", sb2.toString());
            }
            return ((MobileSignalController.MobileState)((MobileSignalController)this.mMobileSignalControllers.get(defaultVoiceSubId)).getState()).isEmergency;
        }
        if (this.mMobileSignalControllers.size() == 1) {
            this.mEmergencySource = this.mMobileSignalControllers.keyAt(0) + 400;
            if (NetworkControllerImpl.DEBUG) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("Getting assumed emergency from ");
                sb3.append(this.mMobileSignalControllers.keyAt(0));
                Log.d("NetworkController", sb3.toString());
            }
            return ((SignalController<MobileSignalController.MobileState, I>)this.mMobileSignalControllers.valueAt(0)).getState().isEmergency;
        }
        if (NetworkControllerImpl.DEBUG) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("Cannot find controller for voice sub: ");
            sb4.append(defaultVoiceSubId);
            Log.e("NetworkController", sb4.toString());
        }
        this.mEmergencySource = defaultVoiceSubId + 300;
        return true;
    }
    
    boolean isInGroupDataSwitch(final int n, final int n2) {
        final SubscriptionInfo activeSubscriptionInfo = this.mSubscriptionManager.getActiveSubscriptionInfo(n);
        final SubscriptionInfo activeSubscriptionInfo2 = this.mSubscriptionManager.getActiveSubscriptionInfo(n2);
        return activeSubscriptionInfo != null && activeSubscriptionInfo2 != null && activeSubscriptionInfo.getGroupUuid() != null && activeSubscriptionInfo.getGroupUuid().equals((Object)activeSubscriptionInfo2.getGroupUuid());
    }
    
    public boolean isRadioOn() {
        return this.mAirplaneMode ^ true;
    }
    
    boolean keepCellularValidationBitInSwitch(final int n, final int n2) {
        final BitSet mValidatedTransports = this.mValidatedTransports;
        boolean b = false;
        if (mValidatedTransports.get(0)) {
            b = b;
            if (this.isInGroupDataSwitch(n, n2)) {
                b = true;
            }
        }
        return b;
    }
    
    public void onReceive(final Context context, final Intent obj) {
        if (NetworkControllerImpl.CHATTY) {
            final StringBuilder sb = new StringBuilder();
            sb.append("onReceive: intent=");
            sb.append(obj);
            Log.d("NetworkController", sb.toString());
        }
        final String action = obj.getAction();
        final int hashCode = action.hashCode();
        int i = 0;
        int n = 0;
        Label_0255: {
            switch (hashCode) {
                case 623179603: {
                    if (action.equals("android.net.conn.INET_CONDITION_ACTION")) {
                        n = 1;
                        break Label_0255;
                    }
                    break;
                }
                case -25388475: {
                    if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
                        n = 4;
                        break Label_0255;
                    }
                    break;
                }
                case -229777127: {
                    if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                        n = 5;
                        break Label_0255;
                    }
                    break;
                }
                case -1076576821: {
                    if (action.equals("android.intent.action.AIRPLANE_MODE")) {
                        n = 2;
                        break Label_0255;
                    }
                    break;
                }
                case -1138588223: {
                    if (action.equals("android.telephony.action.CARRIER_CONFIG_CHANGED")) {
                        n = 7;
                        break Label_0255;
                    }
                    break;
                }
                case -1172645946: {
                    if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                        n = 0;
                        break Label_0255;
                    }
                    break;
                }
                case -1465084191: {
                    if (action.equals("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED")) {
                        n = 3;
                        break Label_0255;
                    }
                    break;
                }
                case -2104353374: {
                    if (action.equals("android.intent.action.SERVICE_STATE")) {
                        n = 6;
                        break Label_0255;
                    }
                    break;
                }
            }
            n = -1;
        }
        switch (n) {
            default: {
                final int intExtra = obj.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1);
                if (!SubscriptionManager.isValidSubscriptionId(intExtra)) {
                    this.mWifiSignalController.handleBroadcast(obj);
                    break;
                }
                if (this.mMobileSignalControllers.indexOfKey(intExtra) >= 0) {
                    ((MobileSignalController)this.mMobileSignalControllers.get(intExtra)).handleBroadcast(obj);
                    break;
                }
                this.updateMobileControllers();
                break;
            }
            case 7: {
                this.mConfig = Config.readConfig(this.mContext);
                this.mReceiverHandler.post((Runnable)new _$$Lambda$ybM43k5QVX_SxWbQACu1XwL3Knk(this));
                break;
            }
            case 6: {
                this.mLastServiceState = ServiceState.newFromBundle(obj.getExtras());
                if (this.mMobileSignalControllers.size() == 0) {
                    this.recalculateEmergency();
                    break;
                }
                break;
            }
            case 5: {
                if (obj.getBooleanExtra("rebroadcastOnUnlock", false)) {
                    break;
                }
                this.updateMobileControllers();
                break;
            }
            case 4: {
                while (i < this.mMobileSignalControllers.size()) {
                    ((MobileSignalController)this.mMobileSignalControllers.valueAt(i)).handleBroadcast(obj);
                    ++i;
                }
                this.mConfig = Config.readConfig(this.mContext);
                this.mReceiverHandler.post((Runnable)new _$$Lambda$ybM43k5QVX_SxWbQACu1XwL3Knk(this));
                break;
            }
            case 3: {
                this.recalculateEmergency();
                break;
            }
            case 2: {
                this.refreshLocale();
                this.updateAirplaneMode(false);
                break;
            }
            case 0:
            case 1: {
                this.updateConnectivity();
                break;
            }
        }
    }
    
    void recalculateEmergency() {
        final boolean emergencyOnly = this.isEmergencyOnly();
        this.mIsEmergency = emergencyOnly;
        this.mCallbackHandler.setEmergencyCallsOnly(emergencyOnly);
    }
    
    public void removeCallback(final SignalCallback signalCallback) {
        this.mCallbackHandler.setListening(signalCallback, false);
    }
    
    @GuardedBy({ "mLock" })
    @VisibleForTesting
    public void setCurrentSubscriptionsLocked(final List<SubscriptionInfo> subs) {
        Collections.sort((List<Object>)subs, (Comparator<? super Object>)new Comparator<SubscriptionInfo>(this) {
            @Override
            public int compare(final SubscriptionInfo subscriptionInfo, final SubscriptionInfo subscriptionInfo2) {
                int n;
                int n2;
                if (subscriptionInfo.getSimSlotIndex() == subscriptionInfo2.getSimSlotIndex()) {
                    n = subscriptionInfo.getSubscriptionId();
                    n2 = subscriptionInfo2.getSubscriptionId();
                }
                else {
                    n = subscriptionInfo.getSimSlotIndex();
                    n2 = subscriptionInfo2.getSimSlotIndex();
                }
                return n - n2;
            }
        });
        this.mCurrentSubscriptions = subs;
        final SparseArray sparseArray = new SparseArray();
        for (int i = 0; i < this.mMobileSignalControllers.size(); ++i) {
            sparseArray.put(this.mMobileSignalControllers.keyAt(i), (Object)this.mMobileSignalControllers.valueAt(i));
        }
        this.mMobileSignalControllers.clear();
        for (int size = subs.size(), j = 0; j < size; ++j) {
            final int subscriptionId = subs.get(j).getSubscriptionId();
            if (sparseArray.indexOfKey(subscriptionId) >= 0) {
                this.mMobileSignalControllers.put(subscriptionId, (Object)sparseArray.get(subscriptionId));
                sparseArray.remove(subscriptionId);
            }
            else {
                final MobileSignalController mDefaultSignalController = new MobileSignalController(this.mContext, this.mConfig, this.mHasMobileDataFeature, this.mPhone.createForSubscriptionId(subscriptionId), this.mCallbackHandler, this, subs.get(j), this.mSubDefaults, this.mReceiverHandler.getLooper());
                mDefaultSignalController.setUserSetupComplete(this.mUserSetup);
                this.mMobileSignalControllers.put(subscriptionId, (Object)mDefaultSignalController);
                if (subs.get(j).getSimSlotIndex() == 0) {
                    this.mDefaultSignalController = mDefaultSignalController;
                }
                if (this.mListening) {
                    mDefaultSignalController.registerListener();
                }
            }
        }
        if (this.mListening) {
            for (int k = 0; k < sparseArray.size(); ++k) {
                final int key = sparseArray.keyAt(k);
                if (sparseArray.get(key) == this.mDefaultSignalController) {
                    this.mDefaultSignalController = null;
                }
                ((MobileSignalController)sparseArray.get(key)).unregisterListener();
            }
        }
        this.mCallbackHandler.setSubs(subs);
        this.notifyAllListeners();
        this.pushConnectivityToSignals();
        this.updateAirplaneMode(true);
    }
    
    public void setWifiEnabled(final boolean b) {
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(final Void... array) {
                NetworkControllerImpl.this.mWifiManager.setWifiEnabled(b);
                return null;
            }
        }.execute((Object[])new Void[0]);
    }
    
    @VisibleForTesting
    protected void updateNoSims() {
        final boolean mHasNoSubs = this.mHasMobileDataFeature && this.mMobileSignalControllers.size() == 0;
        final boolean hasAnySim = this.hasAnySim();
        if (mHasNoSubs != this.mHasNoSubs || hasAnySim != this.mSimDetected) {
            this.mHasNoSubs = mHasNoSubs;
            this.mSimDetected = hasAnySim;
            this.mCallbackHandler.setNoSims(mHasNoSubs, hasAnySim);
        }
    }
    
    @VisibleForTesting
    static class Config
    {
        boolean alwaysShowCdmaRssi;
        boolean alwaysShowDataRatIcon;
        boolean hideLtePlus;
        boolean hspaDataDistinguishable;
        boolean show4gFor3g;
        boolean show4gForLte;
        boolean showAtLeast3G;
        
        Config() {
            this.showAtLeast3G = false;
            this.show4gFor3g = false;
            this.alwaysShowCdmaRssi = false;
            this.show4gForLte = false;
            this.hideLtePlus = false;
            this.alwaysShowDataRatIcon = false;
        }
        
        static Config readConfig(final Context context) {
            final Config config = new Config();
            final Resources resources = context.getResources();
            config.showAtLeast3G = resources.getBoolean(R$bool.config_showMin3G);
            config.alwaysShowCdmaRssi = resources.getBoolean(17891359);
            config.hspaDataDistinguishable = resources.getBoolean(R$bool.config_hspa_data_distinguishable);
            resources.getBoolean(17891474);
            final CarrierConfigManager carrierConfigManager = (CarrierConfigManager)context.getSystemService("carrier_config");
            SubscriptionManager.from(context);
            final PersistableBundle configForSubId = carrierConfigManager.getConfigForSubId(SubscriptionManager.getDefaultDataSubscriptionId());
            if (configForSubId != null) {
                config.alwaysShowDataRatIcon = configForSubId.getBoolean("always_show_data_rat_icon_bool");
                config.show4gForLte = configForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
                config.show4gFor3g = configForSubId.getBoolean("show_4g_for_3g_data_icon_bool");
                config.hideLtePlus = configForSubId.getBoolean("hide_lte_plus_data_icon_bool");
            }
            return config;
        }
    }
    
    private class SubListener extends SubscriptionManager$OnSubscriptionsChangedListener
    {
        public void onSubscriptionsChanged() {
            NetworkControllerImpl.this.updateMobileControllers();
        }
    }
    
    public static class SubscriptionDefaults
    {
        public int getActiveDataSubId() {
            return SubscriptionManager.getActiveDataSubscriptionId();
        }
        
        public int getDefaultDataSubId() {
            return SubscriptionManager.getDefaultDataSubscriptionId();
        }
        
        public int getDefaultVoiceSubId() {
            return SubscriptionManager.getDefaultVoiceSubscriptionId();
        }
    }
}
