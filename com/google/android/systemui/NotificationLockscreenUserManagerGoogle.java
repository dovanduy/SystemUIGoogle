// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui;

import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.os.Handler;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.app.KeyguardManager;
import com.android.internal.statusbar.IStatusBarService;
import android.os.UserManager;
import android.app.admin.DevicePolicyManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.content.Context;
import com.google.android.systemui.smartspace.SmartSpaceController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import dagger.Lazy;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl;

public class NotificationLockscreenUserManagerGoogle extends NotificationLockscreenUserManagerImpl
{
    private final Lazy<KeyguardBypassController> mKeyguardBypassControllerLazy;
    private final SmartSpaceController mSmartSpaceController;
    
    public NotificationLockscreenUserManagerGoogle(final Context context, final BroadcastDispatcher broadcastDispatcher, final DevicePolicyManager devicePolicyManager, final UserManager userManager, final IStatusBarService statusBarService, final KeyguardManager keyguardManager, final StatusBarStateController statusBarStateController, final Handler handler, final DeviceProvisionedController deviceProvisionedController, final KeyguardStateController keyguardStateController, final Lazy<KeyguardBypassController> mKeyguardBypassControllerLazy, final SmartSpaceController mSmartSpaceController) {
        super(context, broadcastDispatcher, devicePolicyManager, userManager, statusBarService, keyguardManager, statusBarStateController, handler, deviceProvisionedController, keyguardStateController);
        this.mKeyguardBypassControllerLazy = mKeyguardBypassControllerLazy;
        this.mSmartSpaceController = mSmartSpaceController;
    }
    
    @Override
    protected void updateLockscreenNotificationSetting() {
        super.updateLockscreenNotificationSetting();
        this.updateSmartSpaceVisibilitySettings();
    }
    
    @Override
    public void updatePublicMode() {
        super.updatePublicMode();
        this.updateLockscreenNotificationSetting();
    }
    
    public void updateSmartSpaceVisibilitySettings() {
        final boolean userAllowsPrivateNotificationsInPublic = this.userAllowsPrivateNotificationsInPublic(super.mCurrentUserId);
        final boolean b = false;
        final boolean b2 = !userAllowsPrivateNotificationsInPublic && this.isAnyProfilePublicMode();
        final boolean b3 = this.allowsManagedPrivateNotificationsInPublic() ^ true;
        boolean b4;
        if (this.mKeyguardBypassControllerLazy.get().getBypassEnabled()) {
            b4 = b3;
        }
        else {
            b4 = b;
            if (b3) {
                b4 = b;
                if (this.isAnyManagedProfilePublicMode()) {
                    b4 = true;
                }
            }
        }
        this.mSmartSpaceController.setHideSensitiveData(b2, b4);
    }
}
