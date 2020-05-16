// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.settings;

import android.content.Intent;
import android.content.Context;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.Executor;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.app.ActivityManager;
import java.util.ArrayList;
import java.util.List;
import android.content.BroadcastReceiver;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.broadcast.BroadcastDispatcher;
import java.util.function.Consumer;

public abstract class CurrentUserTracker
{
    private Consumer<Integer> mCallback;
    private final UserReceiver mUserReceiver;
    
    public CurrentUserTracker(final BroadcastDispatcher broadcastDispatcher) {
        this(UserReceiver.getInstance(broadcastDispatcher));
    }
    
    @VisibleForTesting
    CurrentUserTracker(final UserReceiver mUserReceiver) {
        this.mCallback = (Consumer<Integer>)new _$$Lambda$JYv4q5Exc5xk6WCK6WtC6eC0sA8(this);
        this.mUserReceiver = mUserReceiver;
    }
    
    public int getCurrentUserId() {
        return this.mUserReceiver.getCurrentUserId();
    }
    
    public abstract void onUserSwitched(final int p0);
    
    public void startTracking() {
        this.mUserReceiver.addTracker(this.mCallback);
    }
    
    public void stopTracking() {
        this.mUserReceiver.removeTracker(this.mCallback);
    }
    
    @VisibleForTesting
    static class UserReceiver extends BroadcastReceiver
    {
        private static UserReceiver sInstance;
        private final BroadcastDispatcher mBroadcastDispatcher;
        private List<Consumer<Integer>> mCallbacks;
        private int mCurrentUserId;
        private boolean mReceiverRegistered;
        
        @VisibleForTesting
        UserReceiver(final BroadcastDispatcher mBroadcastDispatcher) {
            this.mCallbacks = new ArrayList<Consumer<Integer>>();
            this.mBroadcastDispatcher = mBroadcastDispatcher;
        }
        
        private void addTracker(final Consumer<Integer> consumer) {
            if (!this.mCallbacks.contains(consumer)) {
                this.mCallbacks.add(consumer);
            }
            if (!this.mReceiverRegistered) {
                this.mCurrentUserId = ActivityManager.getCurrentUser();
                this.mBroadcastDispatcher.registerReceiver(this, new IntentFilter("android.intent.action.USER_SWITCHED"), null, UserHandle.ALL);
                this.mReceiverRegistered = true;
            }
        }
        
        static UserReceiver getInstance(final BroadcastDispatcher broadcastDispatcher) {
            if (UserReceiver.sInstance == null) {
                UserReceiver.sInstance = new UserReceiver(broadcastDispatcher);
            }
            return UserReceiver.sInstance;
        }
        
        private void notifyUserSwitched(final int n) {
            if (this.mCurrentUserId != n) {
                this.mCurrentUserId = n;
                for (final Consumer<Integer> consumer : new ArrayList<Consumer<Integer>>(this.mCallbacks)) {
                    if (this.mCallbacks.contains(consumer)) {
                        consumer.accept(n);
                    }
                }
            }
        }
        
        private void removeTracker(final Consumer<Integer> consumer) {
            if (this.mCallbacks.contains(consumer)) {
                this.mCallbacks.remove(consumer);
                if (this.mCallbacks.size() == 0 && this.mReceiverRegistered) {
                    this.mBroadcastDispatcher.unregisterReceiver(this);
                    this.mReceiverRegistered = false;
                }
            }
        }
        
        public int getCurrentUserId() {
            return this.mCurrentUserId;
        }
        
        public void onReceive(final Context context, final Intent intent) {
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                this.notifyUserSwitched(intent.getIntExtra("android.intent.extra.user_handle", 0));
            }
        }
    }
}
