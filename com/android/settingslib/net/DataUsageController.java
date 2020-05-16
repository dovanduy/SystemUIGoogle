// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.net;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import java.util.Iterator;
import java.time.ZonedDateTime;
import android.util.Range;
import android.app.usage.NetworkStats$Bucket;
import android.os.RemoteException;
import android.text.format.DateUtils;
import android.net.NetworkPolicy;
import android.net.NetworkTemplate;
import android.net.INetworkStatsService$Stub;
import android.os.ServiceManager;
import java.util.Locale;
import android.util.Log;
import android.net.NetworkPolicyManager;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import java.util.Formatter;

public class DataUsageController
{
    private static final StringBuilder PERIOD_BUILDER;
    private static final Formatter PERIOD_FORMATTER;
    private Callback mCallback;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private NetworkNameProvider mNetworkController;
    private final NetworkStatsManager mNetworkStatsManager;
    private final NetworkPolicyManager mPolicyManager;
    private int mSubscriptionId;
    
    static {
        Log.isLoggable("DataUsageController", 3);
        PERIOD_BUILDER = new StringBuilder(50);
        PERIOD_FORMATTER = new Formatter(DataUsageController.PERIOD_BUILDER, Locale.getDefault());
    }
    
    public DataUsageController(final Context mContext) {
        this.mContext = mContext;
        this.mConnectivityManager = ConnectivityManager.from(mContext);
        INetworkStatsService$Stub.asInterface(ServiceManager.getService("netstats"));
        this.mPolicyManager = NetworkPolicyManager.from(this.mContext);
        this.mNetworkStatsManager = (NetworkStatsManager)mContext.getSystemService((Class)NetworkStatsManager.class);
        this.mSubscriptionId = -1;
    }
    
    private NetworkPolicy findNetworkPolicy(final NetworkTemplate networkTemplate) {
        final NetworkPolicyManager mPolicyManager = this.mPolicyManager;
        if (mPolicyManager != null) {
            if (networkTemplate != null) {
                final NetworkPolicy[] networkPolicies = mPolicyManager.getNetworkPolicies();
                if (networkPolicies == null) {
                    return null;
                }
                for (final NetworkPolicy networkPolicy : networkPolicies) {
                    if (networkPolicy != null && networkTemplate.equals((Object)networkPolicy.template)) {
                        return networkPolicy;
                    }
                }
            }
        }
        return null;
    }
    
    private String formatDateRange(final long n, final long n2) {
        synchronized (DataUsageController.PERIOD_BUILDER) {
            DataUsageController.PERIOD_BUILDER.setLength(0);
            return DateUtils.formatDateRange(this.mContext, DataUsageController.PERIOD_FORMATTER, n, n2, 65552, (String)null).toString();
        }
    }
    
    private long getUsageLevel(final NetworkTemplate networkTemplate, final long n, final long n2) {
        try {
            final NetworkStats$Bucket querySummaryForDevice = this.mNetworkStatsManager.querySummaryForDevice(networkTemplate, n, n2);
            if (querySummaryForDevice != null) {
                return querySummaryForDevice.getRxBytes() + querySummaryForDevice.getTxBytes();
            }
            Log.w("DataUsageController", "Failed to get data usage, no entry data");
        }
        catch (RemoteException ex) {
            Log.w("DataUsageController", "Failed to get data usage, remote call failed");
        }
        return -1L;
    }
    
    private DataUsageInfo warn(final String str) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Failed to get data usage, ");
        sb.append(str);
        Log.w("DataUsageController", sb.toString());
        return null;
    }
    
    public DataUsageInfo getDataUsageInfo() {
        return this.getDataUsageInfo(DataUsageUtils.getMobileTemplate(this.mContext, this.mSubscriptionId));
    }
    
    public DataUsageInfo getDataUsageInfo(final NetworkTemplate networkTemplate) {
        final NetworkPolicy networkPolicy = this.findNetworkPolicy(networkTemplate);
        long n = System.currentTimeMillis();
        Iterator<Range> cycleIterator;
        if (networkPolicy != null) {
            cycleIterator = (Iterator<Range>)networkPolicy.cycleIterator();
        }
        else {
            cycleIterator = null;
        }
        long epochMilli;
        if (cycleIterator != null && cycleIterator.hasNext()) {
            final Range range = cycleIterator.next();
            epochMilli = ((ZonedDateTime)range.getLower()).toInstant().toEpochMilli();
            n = ((ZonedDateTime)range.getUpper()).toInstant().toEpochMilli();
        }
        else {
            epochMilli = n - 2419200000L;
        }
        final long usageLevel = this.getUsageLevel(networkTemplate, epochMilli, n);
        final long n2 = 0L;
        if (usageLevel < 0L) {
            return this.warn("no entry data");
        }
        final DataUsageInfo dataUsageInfo = new DataUsageInfo();
        dataUsageInfo.usageLevel = usageLevel;
        dataUsageInfo.period = this.formatDateRange(epochMilli, n);
        if (networkPolicy != null) {
            long limitBytes = networkPolicy.limitBytes;
            if (limitBytes <= 0L) {
                limitBytes = 0L;
            }
            dataUsageInfo.limitLevel = limitBytes;
            final long warningBytes = networkPolicy.warningBytes;
            long warningLevel = n2;
            if (warningBytes > 0L) {
                warningLevel = warningBytes;
            }
            dataUsageInfo.warningLevel = warningLevel;
        }
        else {
            dataUsageInfo.warningLevel = this.getDefaultWarningLevel();
        }
        final NetworkNameProvider mNetworkController = this.mNetworkController;
        if (mNetworkController != null) {
            dataUsageInfo.carrier = mNetworkController.getMobileDataNetworkName();
        }
        return dataUsageInfo;
    }
    
    public long getDefaultWarningLevel() {
        return this.mContext.getResources().getInteger(17694929) * 1048576L;
    }
    
    @VisibleForTesting
    public TelephonyManager getTelephonyManager() {
        int n;
        if (!SubscriptionManager.isValidSubscriptionId(n = this.mSubscriptionId)) {
            n = SubscriptionManager.getDefaultDataSubscriptionId();
        }
        int n2 = n;
        if (!SubscriptionManager.isValidSubscriptionId(n)) {
            final int[] activeSubscriptionIdList = SubscriptionManager.from(this.mContext).getActiveSubscriptionIdList();
            n2 = n;
            if (!ArrayUtils.isEmpty(activeSubscriptionIdList)) {
                n2 = activeSubscriptionIdList[0];
            }
        }
        return ((TelephonyManager)this.mContext.getSystemService((Class)TelephonyManager.class)).createForSubscriptionId(n2);
    }
    
    public boolean isMobileDataEnabled() {
        return this.getTelephonyManager().isDataEnabled();
    }
    
    public boolean isMobileDataSupported() {
        final ConnectivityManager mConnectivityManager = this.mConnectivityManager;
        boolean b = false;
        if (mConnectivityManager.isNetworkSupported(0)) {
            b = b;
            if (this.getTelephonyManager().getSimState() == 5) {
                b = true;
            }
        }
        return b;
    }
    
    public void setCallback(final Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    public void setMobileDataEnabled(final boolean b) {
        final StringBuilder sb = new StringBuilder();
        sb.append("setMobileDataEnabled: enabled=");
        sb.append(b);
        Log.d("DataUsageController", sb.toString());
        this.getTelephonyManager().setDataEnabled(b);
        final Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onMobileDataEnabled(b);
        }
    }
    
    public void setNetworkController(final NetworkNameProvider mNetworkController) {
        this.mNetworkController = mNetworkController;
    }
    
    public interface Callback
    {
        void onMobileDataEnabled(final boolean p0);
    }
    
    public static class DataUsageInfo
    {
        public String carrier;
        public long limitLevel;
        public String period;
        public long usageLevel;
        public long warningLevel;
    }
    
    public interface NetworkNameProvider
    {
        String getMobileDataNetworkName();
    }
}
