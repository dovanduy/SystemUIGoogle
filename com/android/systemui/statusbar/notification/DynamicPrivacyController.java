// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import java.util.Iterator;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import android.util.ArraySet;
import com.android.systemui.statusbar.policy.KeyguardStateController;

public class DynamicPrivacyController implements Callback
{
    private boolean mCacheInvalid;
    private final KeyguardStateController mKeyguardStateController;
    private boolean mLastDynamicUnlocked;
    private ArraySet<Listener> mListeners;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final StatusBarStateController mStateController;
    private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    
    DynamicPrivacyController(final NotificationLockscreenUserManager mLockscreenUserManager, final KeyguardStateController mKeyguardStateController, final StatusBarStateController mStateController) {
        this.mListeners = (ArraySet<Listener>)new ArraySet();
        this.mLockscreenUserManager = mLockscreenUserManager;
        this.mStateController = mStateController;
        (this.mKeyguardStateController = mKeyguardStateController).addCallback((KeyguardStateController.Callback)this);
        this.mLastDynamicUnlocked = this.isDynamicallyUnlocked();
    }
    
    private boolean isDynamicPrivacyEnabled() {
        final NotificationLockscreenUserManager mLockscreenUserManager = this.mLockscreenUserManager;
        return mLockscreenUserManager.shouldHideNotifications(mLockscreenUserManager.getCurrentUserId()) ^ true;
    }
    
    public void addListener(final Listener listener) {
        this.mListeners.add((Object)listener);
    }
    
    public boolean isDynamicallyUnlocked() {
        return (this.mKeyguardStateController.canDismissLockScreen() || this.mKeyguardStateController.isKeyguardGoingAway() || this.mKeyguardStateController.isKeyguardFadingAway()) && this.isDynamicPrivacyEnabled();
    }
    
    public boolean isInLockedDownShade() {
        if (this.mStatusBarKeyguardViewManager.isShowing()) {
            if (this.mKeyguardStateController.isMethodSecure()) {
                final int state = this.mStateController.getState();
                if (state != 0 && state != 2) {
                    return false;
                }
                if (this.isDynamicPrivacyEnabled()) {
                    if (!this.isDynamicallyUnlocked()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void onUnlockedChanged() {
        if (this.isDynamicPrivacyEnabled()) {
            final boolean dynamicallyUnlocked = this.isDynamicallyUnlocked();
            if (dynamicallyUnlocked != this.mLastDynamicUnlocked || this.mCacheInvalid) {
                this.mLastDynamicUnlocked = dynamicallyUnlocked;
                final Iterator iterator = this.mListeners.iterator();
                while (iterator.hasNext()) {
                    iterator.next().onDynamicPrivacyChanged();
                }
            }
            this.mCacheInvalid = false;
        }
        else {
            this.mCacheInvalid = true;
        }
    }
    
    public void setStatusBarKeyguardViewManager(final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager) {
        this.mStatusBarKeyguardViewManager = mStatusBarKeyguardViewManager;
    }
    
    public interface Listener
    {
        void onDynamicPrivacyChanged();
    }
}
