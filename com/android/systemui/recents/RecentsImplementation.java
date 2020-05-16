// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import android.graphics.Rect;
import android.content.Context;
import android.content.res.Configuration;
import java.io.PrintWriter;

public interface RecentsImplementation
{
    default void cancelPreloadRecentApps() {
    }
    
    default void dump(final PrintWriter printWriter) {
    }
    
    default void growRecents() {
    }
    
    default void hideRecentApps(final boolean b, final boolean b2) {
    }
    
    default void onAppTransitionFinished() {
    }
    
    default void onBootCompleted() {
    }
    
    default void onConfigurationChanged(final Configuration configuration) {
    }
    
    default void onStart(final Context context) {
    }
    
    default void preloadRecentApps() {
    }
    
    default void showRecentApps(final boolean b) {
    }
    
    default boolean splitPrimaryTask(final int n, final Rect rect, final int n2) {
        return false;
    }
    
    default void toggleRecentApps() {
    }
}
