// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import java.util.concurrent.Executor;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.provider.Settings$Secure;
import android.util.Log;
import com.android.internal.widget.ILockSettings$Stub;
import android.os.ServiceManager;
import java.util.ArrayList;
import com.android.internal.widget.ILockSettings;
import java.util.List;
import android.content.Context;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.content.BroadcastReceiver;
import com.android.systemui.broadcast.BroadcastDispatcher;

public class OpaEnabledReceiver
{
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mBroadcastReceiver;
    private final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private boolean mIsAGSAAssistant;
    private boolean mIsOpaEligible;
    private boolean mIsOpaEnabled;
    private final List<OpaEnabledListener> mListeners;
    private final ILockSettings mLockSettings;
    
    public OpaEnabledReceiver(final Context mContext, final BroadcastDispatcher mBroadcastDispatcher) {
        this.mBroadcastReceiver = new OpaEnabledBroadcastReceiver();
        this.mListeners = new ArrayList<OpaEnabledListener>();
        this.mContext = mContext;
        this.mContentResolver = mContext.getContentResolver();
        this.mContentObserver = new AssistantContentObserver(this.mContext);
        this.mLockSettings = ILockSettings$Stub.asInterface(ServiceManager.getService("lock_settings"));
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.updateOpaEnabledState(this.mContext);
        this.registerContentObserver();
        this.registerEnabledReceiver(-2);
    }
    
    private void dispatchOpaEnabledState(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Dispatching OPA eligble = ");
        sb.append(this.mIsOpaEligible);
        sb.append("; AGSA = ");
        sb.append(this.mIsAGSAAssistant);
        sb.append("; OPA enabled = ");
        sb.append(this.mIsOpaEnabled);
        Log.i("OpaEnabledReceiver", sb.toString());
        for (int i = 0; i < this.mListeners.size(); ++i) {
            this.mListeners.get(i).onOpaEnabledReceived(context, this.mIsOpaEligible, this.mIsAGSAAssistant, this.mIsOpaEnabled);
        }
    }
    
    private boolean isOpaEligible(final Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        boolean b = false;
        if (Settings$Secure.getIntForUser(contentResolver, "systemui.google.opa_enabled", 0, -2) != 0) {
            b = true;
        }
        return b;
    }
    
    private boolean isOpaEnabled(final Context context) {
        try {
            return this.mLockSettings.getBoolean("systemui.google.opa_user_enabled", false, -2);
        }
        catch (RemoteException ex) {
            Log.e("OpaEnabledReceiver", "isOpaEnabled RemoteException", (Throwable)ex);
            return false;
        }
    }
    
    private void registerContentObserver() {
        this.mContentResolver.registerContentObserver(Settings$Secure.getUriFor("assistant"), false, this.mContentObserver, -2);
    }
    
    private void registerEnabledReceiver(final int n) {
        this.mBroadcastDispatcher.registerReceiver(this.mBroadcastReceiver, new IntentFilter("com.google.android.systemui.OPA_ENABLED"), null, new UserHandle(n));
        this.mBroadcastDispatcher.registerReceiver(this.mBroadcastReceiver, new IntentFilter("com.google.android.systemui.OPA_USER_ENABLED"), null, new UserHandle(n));
    }
    
    private void updateOpaEnabledState(final Context context) {
        this.mIsOpaEligible = this.isOpaEligible(context);
        this.mIsAGSAAssistant = OpaUtils.isAGSACurrentAssistant(context);
        this.mIsOpaEnabled = this.isOpaEnabled(context);
    }
    
    public void addOpaEnabledListener(final OpaEnabledListener opaEnabledListener) {
        this.mListeners.add(opaEnabledListener);
        opaEnabledListener.onOpaEnabledReceived(this.mContext, this.mIsOpaEligible, this.mIsAGSAAssistant, this.mIsOpaEnabled);
    }
    
    public void dispatchOpaEnabledState() {
        this.dispatchOpaEnabledState(this.mContext);
    }
    
    public void onUserSwitching(final int n) {
        this.updateOpaEnabledState(this.mContext);
        this.dispatchOpaEnabledState(this.mContext);
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
        this.registerContentObserver();
        this.mBroadcastDispatcher.unregisterReceiver(this.mBroadcastReceiver);
        this.registerEnabledReceiver(n);
    }
    
    private class AssistantContentObserver extends ContentObserver
    {
        public AssistantContentObserver(final Context context) {
            super(new Handler(context.getMainLooper()));
        }
        
        public void onChange(final boolean b, final Uri uri) {
            final OpaEnabledReceiver this$0 = OpaEnabledReceiver.this;
            this$0.updateOpaEnabledState(this$0.mContext);
            final OpaEnabledReceiver this$2 = OpaEnabledReceiver.this;
            this$2.dispatchOpaEnabledState(this$2.mContext);
        }
    }
    
    private class OpaEnabledBroadcastReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals("com.google.android.systemui.OPA_ENABLED")) {
                Settings$Secure.putIntForUser(context.getContentResolver(), "systemui.google.opa_enabled", (int)(intent.getBooleanExtra("OPA_ENABLED", false) ? 1 : 0), -2);
            }
            else if (intent.getAction().equals("com.google.android.systemui.OPA_USER_ENABLED")) {
                final boolean booleanExtra = intent.getBooleanExtra("OPA_USER_ENABLED", false);
                try {
                    OpaEnabledReceiver.this.mLockSettings.setBoolean("systemui.google.opa_user_enabled", booleanExtra, -2);
                }
                catch (RemoteException ex) {
                    Log.e("OpaEnabledReceiver", "RemoteException on OPA_USER_ENABLED", (Throwable)ex);
                }
            }
            OpaEnabledReceiver.this.updateOpaEnabledState(context);
            OpaEnabledReceiver.this.dispatchOpaEnabledState(context);
        }
    }
}
