// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.net;

import android.telephony.SubscriptionManager;
import android.content.Context;

public class SignalStrengthUtil
{
    public static boolean shouldInflateSignalStrength(final Context context, final int n) {
        return SubscriptionManager.getResourcesForSubId(context, n).getBoolean(17891474);
    }
}
