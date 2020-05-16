// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib;

import java.util.Objects;
import android.os.UserHandle;
import android.os.UserManager;
import android.content.ComponentName;
import android.os.Parcelable;
import android.content.Intent;
import android.content.Context;

public class RestrictedLockUtils
{
    public static Intent getShowAdminSupportDetailsIntent(final Context context, final EnforcedAdmin enforcedAdmin) {
        final Intent intent = new Intent("android.settings.SHOW_ADMIN_SUPPORT_DETAILS");
        if (enforcedAdmin != null) {
            final ComponentName component = enforcedAdmin.component;
            if (component != null) {
                intent.putExtra("android.app.extra.DEVICE_ADMIN", (Parcelable)component);
            }
            intent.putExtra("android.intent.extra.USER", (Parcelable)enforcedAdmin.user);
        }
        return intent;
    }
    
    public static boolean isCurrentUserOrProfile(final Context context, final int n) {
        return ((UserManager)context.getSystemService((Class)UserManager.class)).getUserProfiles().contains(UserHandle.of(n));
    }
    
    public static void sendShowAdminSupportDetailsIntent(final Context context, final EnforcedAdmin enforcedAdmin) {
        final Intent showAdminSupportDetailsIntent = getShowAdminSupportDetailsIntent(context, enforcedAdmin);
        int n2;
        final int n = n2 = UserHandle.myUserId();
        if (enforcedAdmin != null) {
            final UserHandle user = enforcedAdmin.user;
            n2 = n;
            if (user != null) {
                n2 = n;
                if (isCurrentUserOrProfile(context, user.getIdentifier())) {
                    n2 = enforcedAdmin.user.getIdentifier();
                }
            }
            showAdminSupportDetailsIntent.putExtra("android.app.extra.RESTRICTION", enforcedAdmin.enforcedRestriction);
        }
        context.startActivityAsUser(showAdminSupportDetailsIntent, UserHandle.of(n2));
    }
    
    public static class EnforcedAdmin
    {
        public ComponentName component;
        public String enforcedRestriction;
        public UserHandle user;
        
        public EnforcedAdmin() {
            this.component = null;
            this.enforcedRestriction = null;
            this.user = null;
        }
        
        public EnforcedAdmin(final ComponentName component, final String enforcedRestriction, final UserHandle user) {
            this.component = null;
            this.enforcedRestriction = null;
            this.user = null;
            this.component = component;
            this.enforcedRestriction = enforcedRestriction;
            this.user = user;
        }
        
        public static EnforcedAdmin createDefaultEnforcedAdminWithRestriction(final String enforcedRestriction) {
            final EnforcedAdmin enforcedAdmin = new EnforcedAdmin();
            enforcedAdmin.enforcedRestriction = enforcedRestriction;
            return enforcedAdmin;
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b = true;
            if (this == o) {
                return true;
            }
            if (o != null && EnforcedAdmin.class == o.getClass()) {
                final EnforcedAdmin enforcedAdmin = (EnforcedAdmin)o;
                if (!Objects.equals(this.user, enforcedAdmin.user) || !Objects.equals(this.component, enforcedAdmin.component) || !Objects.equals(this.enforcedRestriction, enforcedAdmin.enforcedRestriction)) {
                    b = false;
                }
                return b;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.component, this.enforcedRestriction, this.user);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("EnforcedAdmin{component=");
            sb.append(this.component);
            sb.append(", enforcedRestriction='");
            sb.append(this.enforcedRestriction);
            sb.append(", user=");
            sb.append(this.user);
            sb.append('}');
            return sb.toString();
        }
    }
}
