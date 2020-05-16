// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget.settingsspinner;

import com.android.settingslib.widget.R$drawable;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.Spinner;

public class SettingsSpinner extends Spinner
{
    public SettingsSpinner(final Context context, final AttributeSet set) {
        super(context, set);
        this.setBackgroundResource(R$drawable.settings_spinner_background);
    }
}
