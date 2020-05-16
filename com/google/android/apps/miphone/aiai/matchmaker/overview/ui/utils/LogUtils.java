// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.ui.utils;

import android.support.annotation.Nullable;
import android.util.Log;

public final class LogUtils
{
    private static final LoggingState loggingState;
    
    static {
        loggingState = new LoggingState();
    }
    
    public static void d(final String s) {
        if (!LogUtils.loggingState.loggingEnabled) {
            return;
        }
        Log.d("AiAiSuggestUi", s);
    }
    
    public static void e(final String s, @Nullable final Throwable t) {
        Log.e("AiAiSuggestUi", s, t);
    }
    
    private static class LoggingState
    {
        boolean loggingEnabled;
        
        private LoggingState() {
            this.loggingEnabled = false;
        }
    }
}
