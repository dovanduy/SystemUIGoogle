// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.ui.utils;

import android.support.annotation.Nullable;
import java.util.Random;

public final class Utils
{
    static {
        new Random();
    }
    
    public static <T> T checkNotNull(@Nullable final T t) {
        if (t != null) {
            return t;
        }
        throw null;
    }
}
