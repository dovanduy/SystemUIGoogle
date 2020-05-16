// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.smartspace;

import android.content.IntentFilter;

public class GSAIntents
{
    public static IntentFilter getGsaPackageFilter(final String... array) {
        return getPackageFilter("com.google.android.googlequicksearchbox", array);
    }
    
    public static IntentFilter getPackageFilter(final String s, final String... array) {
        final IntentFilter intentFilter = new IntentFilter();
        for (int length = array.length, i = 0; i < length; ++i) {
            intentFilter.addAction(array[i]);
        }
        intentFilter.addDataScheme("package");
        intentFilter.addDataSchemeSpecificPart(s, 0);
        return intentFilter;
    }
}
