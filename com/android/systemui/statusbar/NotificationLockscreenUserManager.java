// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.content.pm.UserInfo;
import android.util.SparseArray;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotificationLockscreenUserManager
{
    void addUserChangedListener(final UserChangedListener p0);
    
    int getCurrentUserId();
    
    boolean isAnyProfilePublicMode();
    
    boolean isCurrentProfile(final int p0);
    
    boolean isLockscreenPublicMode(final int p0);
    
    boolean needsRedaction(final NotificationEntry p0);
    
    default boolean needsSeparateWorkChallenge(final int n) {
        return false;
    }
    
    void setUpWithPresenter(final NotificationPresenter p0);
    
    boolean shouldAllowLockscreenRemoteInput();
    
    boolean shouldHideNotifications(final int p0);
    
    boolean shouldHideNotifications(final String p0);
    
    boolean shouldShowLockscreenNotifications();
    
    boolean shouldShowOnKeyguard(final NotificationEntry p0);
    
    void updatePublicMode();
    
    boolean userAllowsNotificationsInPublic(final int p0);
    
    boolean userAllowsPrivateNotificationsInPublic(final int p0);
    
    public interface UserChangedListener
    {
        default void onCurrentProfilesChanged(final SparseArray<UserInfo> sparseArray) {
        }
        
        default void onUserChanged(final int n) {
        }
    }
}
