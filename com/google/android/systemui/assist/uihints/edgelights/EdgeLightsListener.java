// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.edgelights;

import com.android.systemui.assist.ui.EdgeLight;

public interface EdgeLightsListener
{
    default void onAssistLightsUpdated(final EdgeLightsView.Mode mode, final EdgeLight[] array) {
    }
    
    default void onModeStarted(final EdgeLightsView.Mode mode) {
    }
}
