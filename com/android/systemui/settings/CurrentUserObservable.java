// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.settings;

import androidx.lifecycle.LiveData;
import com.android.systemui.broadcast.BroadcastDispatcher;
import androidx.lifecycle.MutableLiveData;

public class CurrentUserObservable
{
    private final MutableLiveData<Integer> mCurrentUser;
    private final CurrentUserTracker mTracker;
    
    public CurrentUserObservable(final BroadcastDispatcher broadcastDispatcher) {
        this.mCurrentUser = new MutableLiveData<Integer>() {
            @Override
            protected void onActive() {
                super.onActive();
                CurrentUserObservable.this.mTracker.startTracking();
            }
            
            @Override
            protected void onInactive() {
                super.onInactive();
                CurrentUserObservable.this.mTracker.stopTracking();
            }
        };
        this.mTracker = new CurrentUserTracker(broadcastDispatcher) {
            @Override
            public void onUserSwitched(final int i) {
                CurrentUserObservable.this.mCurrentUser.setValue(i);
            }
        };
    }
    
    public LiveData<Integer> getCurrentUser() {
        if (this.mCurrentUser.getValue() == null) {
            this.mCurrentUser.setValue(this.mTracker.getCurrentUserId());
        }
        return this.mCurrentUser;
    }
}
