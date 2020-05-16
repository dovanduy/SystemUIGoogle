// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.net;

import com.android.internal.util.ArrayUtils;
import android.util.Log;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.net.NetworkTemplate;
import android.content.Context;

public class DataUsageUtils
{
    public static NetworkTemplate getMobileTemplate(final Context context, final int i) {
        final TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService((Class)TelephonyManager.class);
        final SubscriptionManager subscriptionManager = (SubscriptionManager)context.getSystemService((Class)SubscriptionManager.class);
        final NetworkTemplate buildTemplateMobileAll = NetworkTemplate.buildTemplateMobileAll(telephonyManager.getSubscriberId());
        if (!subscriptionManager.isActiveSubscriptionId(i)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Subscription is not active: ");
            sb.append(i);
            Log.i("DataUsageUtils", sb.toString());
            return buildTemplateMobileAll;
        }
        final String[] mergedImsisFromGroup = telephonyManager.createForSubscriptionId(i).getMergedImsisFromGroup();
        if (ArrayUtils.isEmpty((Object[])mergedImsisFromGroup)) {
            Log.i("DataUsageUtils", "mergedSubscriberIds is null.");
            return buildTemplateMobileAll;
        }
        return NetworkTemplate.normalize(buildTemplateMobileAll, mergedImsisFromGroup);
    }
}
