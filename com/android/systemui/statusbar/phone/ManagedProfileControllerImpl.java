// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.app.StatusBarManager;
import java.util.concurrent.Executor;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.app.ActivityManager;
import java.util.Iterator;
import android.content.Intent;
import java.util.ArrayList;
import android.os.UserManager;
import android.content.BroadcastReceiver;
import android.content.pm.UserInfo;
import java.util.LinkedList;
import android.content.Context;
import java.util.List;
import com.android.systemui.broadcast.BroadcastDispatcher;

public class ManagedProfileControllerImpl implements ManagedProfileController
{
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final List<Callback> mCallbacks;
    private final Context mContext;
    private int mCurrentUser;
    private boolean mListening;
    private final LinkedList<UserInfo> mProfiles;
    private final BroadcastReceiver mReceiver;
    private final UserManager mUserManager;
    
    public ManagedProfileControllerImpl(final Context mContext, final BroadcastDispatcher mBroadcastDispatcher) {
        this.mCallbacks = new ArrayList<Callback>();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                ManagedProfileControllerImpl.this.reloadManagedProfiles();
                final Iterator<Callback> iterator = ManagedProfileControllerImpl.this.mCallbacks.iterator();
                while (iterator.hasNext()) {
                    iterator.next().onManagedProfileChanged();
                }
            }
        };
        this.mContext = mContext;
        this.mUserManager = UserManager.get(mContext);
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mProfiles = new LinkedList<UserInfo>();
    }
    
    private void reloadManagedProfiles() {
        synchronized (this.mProfiles) {
            final boolean b = this.mProfiles.size() > 0;
            final int currentUser = ActivityManager.getCurrentUser();
            this.mProfiles.clear();
            for (final UserInfo e : this.mUserManager.getEnabledProfiles(currentUser)) {
                if (e.isManagedProfile()) {
                    this.mProfiles.add(e);
                }
            }
            if (this.mProfiles.size() == 0 && b && currentUser == this.mCurrentUser) {
                final Iterator<Callback> iterator2 = this.mCallbacks.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().onManagedProfileRemoved();
                }
            }
            this.mCurrentUser = currentUser;
        }
    }
    
    private void setListening(final boolean mListening) {
        this.mListening = mListening;
        if (mListening) {
            this.reloadManagedProfiles();
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
            this.mBroadcastDispatcher.registerReceiver(this.mReceiver, intentFilter, null, UserHandle.ALL);
        }
        else {
            this.mBroadcastDispatcher.unregisterReceiver(this.mReceiver);
        }
    }
    
    @Override
    public void addCallback(final Callback callback) {
        this.mCallbacks.add(callback);
        if (this.mCallbacks.size() == 1) {
            this.setListening(true);
        }
        callback.onManagedProfileChanged();
    }
    
    @Override
    public boolean hasActiveProfile() {
        if (!this.mListening) {
            this.reloadManagedProfiles();
        }
        synchronized (this.mProfiles) {
            return this.mProfiles.size() > 0;
        }
    }
    
    @Override
    public boolean isWorkModeEnabled() {
        if (!this.mListening) {
            this.reloadManagedProfiles();
        }
        synchronized (this.mProfiles) {
            final Iterator<UserInfo> iterator = this.mProfiles.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().isQuietModeEnabled()) {
                    return false;
                }
            }
            return true;
        }
    }
    
    @Override
    public void removeCallback(final Callback callback) {
        if (this.mCallbacks.remove(callback) && this.mCallbacks.size() == 0) {
            this.setListening(false);
        }
    }
    
    @Override
    public void setWorkModeEnabled(final boolean b) {
        synchronized (this.mProfiles) {
            final Iterator<UserInfo> iterator = this.mProfiles.iterator();
            while (iterator.hasNext()) {
                if (!this.mUserManager.requestQuietModeEnabled(!b, UserHandle.of(iterator.next().id))) {
                    ((StatusBarManager)this.mContext.getSystemService("statusbar")).collapsePanels();
                }
            }
        }
    }
}
