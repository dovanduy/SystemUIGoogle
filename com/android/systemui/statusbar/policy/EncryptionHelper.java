// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.sysprop.VoldProperties;

public class EncryptionHelper
{
    public static final boolean IS_DATA_ENCRYPTED;
    
    static {
        IS_DATA_ENCRYPTED = isDataEncrypted();
    }
    
    private static boolean isDataEncrypted() {
        final String s = VoldProperties.decrypt().orElse("");
        return "1".equals(s) || "trigger_restart_min_framework".equals(s);
    }
}
