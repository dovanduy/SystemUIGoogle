// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib;

import android.content.ComponentName;
import android.content.pm.UserInfo;
import java.util.List;
import android.os.UserManager$EnforcingUser;
import android.os.UserHandle;
import android.os.UserManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;

public class RestrictedLockUtilsInternal extends RestrictedLockUtils
{
    static Proxy sProxy;
    
    static {
        RestrictedLockUtilsInternal.sProxy = new Proxy();
    }
    
    public static EnforcedAdmin checkIfRestrictionEnforced(final Context context, final String s, final int n) {
        if (context.getSystemService("device_policy") == null) {
            return null;
        }
        final UserManager value = UserManager.get(context);
        final List userRestrictionSources = value.getUserRestrictionSources(s, UserHandle.of(n));
        if (userRestrictionSources.isEmpty()) {
            return null;
        }
        if (userRestrictionSources.size() > 1) {
            return EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(s);
        }
        final int userRestrictionSource = userRestrictionSources.get(0).getUserRestrictionSource();
        final int identifier = userRestrictionSources.get(0).getUserHandle().getIdentifier();
        if (userRestrictionSource == 4) {
            if (identifier == n) {
                return getProfileOwner(context, s, identifier);
            }
            final UserInfo profileParent = value.getProfileParent(identifier);
            Object o;
            if (profileParent != null && profileParent.id == n) {
                o = getProfileOwner(context, s, identifier);
            }
            else {
                o = EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(s);
            }
            return (EnforcedAdmin)o;
        }
        else {
            if (userRestrictionSource == 2) {
                Object o2;
                if (identifier == n) {
                    o2 = getDeviceOwner(context, s);
                }
                else {
                    o2 = EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(s);
                }
                return (EnforcedAdmin)o2;
            }
            return null;
        }
    }
    
    private static EnforcedAdmin getDeviceOwner(final Context context, final String s) {
        final DevicePolicyManager devicePolicyManager = (DevicePolicyManager)context.getSystemService("device_policy");
        Object o = null;
        if (devicePolicyManager == null) {
            return null;
        }
        final ComponentName deviceOwnerComponentOnAnyUser = devicePolicyManager.getDeviceOwnerComponentOnAnyUser();
        if (deviceOwnerComponentOnAnyUser != null) {
            o = new EnforcedAdmin(deviceOwnerComponentOnAnyUser, s, devicePolicyManager.getDeviceOwnerUser());
        }
        return (EnforcedAdmin)o;
    }
    
    private static EnforcedAdmin getProfileOwner(final Context context, final String s, final int n) {
        final EnforcedAdmin enforcedAdmin = null;
        if (n == -10000) {
            return null;
        }
        final DevicePolicyManager devicePolicyManager = (DevicePolicyManager)context.getSystemService("device_policy");
        if (devicePolicyManager == null) {
            return null;
        }
        final ComponentName profileOwnerAsUser = devicePolicyManager.getProfileOwnerAsUser(n);
        Object o = enforcedAdmin;
        if (profileOwnerAsUser != null) {
            o = new EnforcedAdmin(profileOwnerAsUser, s, getUserHandleOf(n));
        }
        return (EnforcedAdmin)o;
    }
    
    private static UserHandle getUserHandleOf(final int n) {
        if (n == -10000) {
            return null;
        }
        return UserHandle.of(n);
    }
    
    public static boolean hasBaseUserRestriction(final Context context, final String s, final int n) {
        return ((UserManager)context.getSystemService("user")).hasBaseUserRestriction(s, UserHandle.of(n));
    }
    
    static class Proxy
    {
    }
}
