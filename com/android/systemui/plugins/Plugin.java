// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.content.Context;

public interface Plugin
{
    default int getVersion() {
        return -1;
    }
    
    default void onCreate(final Context context, final Context context2) {
    }
    
    default void onDestroy() {
    }
}
