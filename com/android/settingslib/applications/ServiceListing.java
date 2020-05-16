// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.applications;

import android.content.IntentFilter;
import java.util.Iterator;
import android.util.Slog;
import android.content.pm.ResolveInfo;
import android.app.ActivityManager;
import android.provider.Settings$Secure;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import java.util.ArrayList;
import android.database.ContentObserver;
import android.content.pm.ServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import java.util.HashSet;
import android.content.Context;
import android.content.ContentResolver;
import java.util.List;

public class ServiceListing
{
    private final List<Callback> mCallbacks;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final HashSet<ComponentName> mEnabledServices;
    private final String mIntentAction;
    private boolean mListening;
    private final String mNoun;
    private final BroadcastReceiver mPackageReceiver;
    private final String mPermission;
    private final List<ServiceInfo> mServices;
    private final String mSetting;
    private final ContentObserver mSettingsObserver;
    private final String mTag;
    
    private ServiceListing(final Context mContext, final String mTag, final String mSetting, final String mIntentAction, final String mPermission, final String mNoun) {
        this.mEnabledServices = new HashSet<ComponentName>();
        this.mServices = new ArrayList<ServiceInfo>();
        this.mCallbacks = new ArrayList<Callback>();
        this.mSettingsObserver = new ContentObserver(new Handler()) {
            public void onChange(final boolean b, final Uri uri) {
                ServiceListing.this.reload();
            }
        };
        this.mPackageReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                ServiceListing.this.reload();
            }
        };
        this.mContentResolver = mContext.getContentResolver();
        this.mContext = mContext;
        this.mTag = mTag;
        this.mSetting = mSetting;
        this.mIntentAction = mIntentAction;
        this.mPermission = mPermission;
        this.mNoun = mNoun;
    }
    
    private void loadEnabledServices() {
        this.mEnabledServices.clear();
        final String string = Settings$Secure.getString(this.mContentResolver, this.mSetting);
        if (string != null && !"".equals(string)) {
            final String[] split = string.split(":");
            for (int length = split.length, i = 0; i < length; ++i) {
                final ComponentName unflattenFromString = ComponentName.unflattenFromString(split[i]);
                if (unflattenFromString != null) {
                    this.mEnabledServices.add(unflattenFromString);
                }
            }
        }
    }
    
    public void addCallback(final Callback callback) {
        this.mCallbacks.add(callback);
    }
    
    public void reload() {
        this.loadEnabledServices();
        this.mServices.clear();
        final Iterator<ResolveInfo> iterator = (Iterator<ResolveInfo>)this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent(this.mIntentAction), 132, ActivityManager.getCurrentUser()).iterator();
        while (iterator.hasNext()) {
            final ServiceInfo serviceInfo = iterator.next().serviceInfo;
            if (!this.mPermission.equals(serviceInfo.permission)) {
                final String mTag = this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("Skipping ");
                sb.append(this.mNoun);
                sb.append(" service ");
                sb.append(serviceInfo.packageName);
                sb.append("/");
                sb.append(serviceInfo.name);
                sb.append(": it does not require the permission ");
                sb.append(this.mPermission);
                Slog.w(mTag, sb.toString());
            }
            else {
                this.mServices.add(serviceInfo);
            }
        }
        final Iterator<Callback> iterator2 = this.mCallbacks.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().onServicesReloaded(this.mServices);
        }
    }
    
    public void setListening(final boolean mListening) {
        if (this.mListening == mListening) {
            return;
        }
        this.mListening = mListening;
        if (mListening) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
            intentFilter.addDataScheme("package");
            this.mContext.registerReceiver(this.mPackageReceiver, intentFilter);
            this.mContentResolver.registerContentObserver(Settings$Secure.getUriFor(this.mSetting), false, this.mSettingsObserver);
        }
        else {
            this.mContext.unregisterReceiver(this.mPackageReceiver);
            this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
        }
    }
    
    public static class Builder
    {
        private final Context mContext;
        private String mIntentAction;
        private String mNoun;
        private String mPermission;
        private String mSetting;
        private String mTag;
        
        public Builder(final Context mContext) {
            this.mContext = mContext;
        }
        
        public ServiceListing build() {
            return new ServiceListing(this.mContext, this.mTag, this.mSetting, this.mIntentAction, this.mPermission, this.mNoun, null);
        }
        
        public Builder setIntentAction(final String mIntentAction) {
            this.mIntentAction = mIntentAction;
            return this;
        }
        
        public Builder setNoun(final String mNoun) {
            this.mNoun = mNoun;
            return this;
        }
        
        public Builder setPermission(final String mPermission) {
            this.mPermission = mPermission;
            return this;
        }
        
        public Builder setSetting(final String mSetting) {
            this.mSetting = mSetting;
            return this;
        }
        
        public Builder setTag(final String mTag) {
            this.mTag = mTag;
            return this;
        }
    }
    
    public interface Callback
    {
        void onServicesReloaded(final List<ServiceInfo> p0);
    }
}
