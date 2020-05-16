// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.view.View;
import android.content.Context;

public class PluginUtils
{
    public static void setId(final Context context, final View view, final String s) {
        view.setId(context.getResources().getIdentifier(s, "id", context.getPackageName()));
    }
}
