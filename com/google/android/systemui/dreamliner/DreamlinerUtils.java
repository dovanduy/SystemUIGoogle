// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dreamliner;

import android.text.TextUtils;
import com.android.systemui.R$string;
import android.content.Context;

public final class DreamlinerUtils
{
    public static WirelessCharger getInstance(final Context context) {
        if (context == null) {
            return null;
        }
        final String string = context.getString(R$string.config_dockComponent);
        if (TextUtils.isEmpty((CharSequence)string)) {
            return null;
        }
        try {
            return (WirelessCharger)context.getClassLoader().loadClass(string).newInstance();
        }
        finally {
            return null;
        }
    }
}
