// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.inputmethod;

import android.content.Context;
import androidx.preference.SwitchPreference;

public class SwitchWithNoTextPreference extends SwitchPreference
{
    public SwitchWithNoTextPreference(final Context context) {
        super(context);
        this.setSwitchTextOn("");
        this.setSwitchTextOff("");
    }
}
