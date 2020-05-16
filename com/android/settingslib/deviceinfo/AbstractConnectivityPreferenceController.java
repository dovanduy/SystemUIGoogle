// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.deviceinfo;

import android.os.Handler;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.AbstractPreferenceController;

public abstract class AbstractConnectivityPreferenceController extends AbstractPreferenceController implements LifecycleObserver, OnStart, OnStop
{
    private final BroadcastReceiver mConnectivityReceiver;
    
    protected abstract String[] getConnectivityIntents();
    
    @Override
    public void onStart() {
        final IntentFilter intentFilter = new IntentFilter();
        final String[] connectivityIntents = this.getConnectivityIntents();
        for (int length = connectivityIntents.length, i = 0; i < length; ++i) {
            intentFilter.addAction(connectivityIntents[i]);
        }
        super.mContext.registerReceiver(this.mConnectivityReceiver, intentFilter, "android.permission.CHANGE_NETWORK_STATE", (Handler)null);
    }
    
    @Override
    public void onStop() {
        super.mContext.unregisterReceiver(this.mConnectivityReceiver);
    }
}
