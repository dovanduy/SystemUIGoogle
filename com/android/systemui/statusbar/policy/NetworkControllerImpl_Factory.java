// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.net.NetworkScoreManager;
import android.content.Context;
import android.net.ConnectivityManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Looper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NetworkControllerImpl_Factory implements Factory<NetworkControllerImpl>
{
    private final Provider<Looper> bgLooperProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<ConnectivityManager> connectivityManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<NetworkScoreManager> networkScoreManagerProvider;
    private final Provider<TelephonyManager> telephonyManagerProvider;
    private final Provider<WifiManager> wifiManagerProvider;
    
    public NetworkControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<Looper> bgLooperProvider, final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<ConnectivityManager> connectivityManagerProvider, final Provider<TelephonyManager> telephonyManagerProvider, final Provider<WifiManager> wifiManagerProvider, final Provider<NetworkScoreManager> networkScoreManagerProvider) {
        this.contextProvider = contextProvider;
        this.bgLooperProvider = bgLooperProvider;
        this.deviceProvisionedControllerProvider = deviceProvisionedControllerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.connectivityManagerProvider = connectivityManagerProvider;
        this.telephonyManagerProvider = telephonyManagerProvider;
        this.wifiManagerProvider = wifiManagerProvider;
        this.networkScoreManagerProvider = networkScoreManagerProvider;
    }
    
    public static NetworkControllerImpl_Factory create(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<DeviceProvisionedController> provider3, final Provider<BroadcastDispatcher> provider4, final Provider<ConnectivityManager> provider5, final Provider<TelephonyManager> provider6, final Provider<WifiManager> provider7, final Provider<NetworkScoreManager> provider8) {
        return new NetworkControllerImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }
    
    public static NetworkControllerImpl provideInstance(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<DeviceProvisionedController> provider3, final Provider<BroadcastDispatcher> provider4, final Provider<ConnectivityManager> provider5, final Provider<TelephonyManager> provider6, final Provider<WifiManager> provider7, final Provider<NetworkScoreManager> provider8) {
        return new NetworkControllerImpl(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get());
    }
    
    @Override
    public NetworkControllerImpl get() {
        return provideInstance(this.contextProvider, this.bgLooperProvider, this.deviceProvisionedControllerProvider, this.broadcastDispatcherProvider, this.connectivityManagerProvider, this.telephonyManagerProvider, this.wifiManagerProvider, this.networkScoreManagerProvider);
    }
}
