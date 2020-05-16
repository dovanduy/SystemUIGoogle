// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.content.Context;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;

@DependsOn(target = FalsingManager.class)
@ProvidesInterface(action = "com.android.systemui.action.FALSING_PLUGIN", version = 2)
public interface FalsingPlugin extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.FALSING_PLUGIN";
    public static final int VERSION = 2;
    
    default void dataCollected(final boolean b, final byte[] array) {
    }
    
    default FalsingManager getFalsingManager(final Context context) {
        return null;
    }
}
