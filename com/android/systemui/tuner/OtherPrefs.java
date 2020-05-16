// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import com.android.systemui.R$xml;
import android.os.Bundle;
import androidx.preference.PreferenceFragment;

public class OtherPrefs extends PreferenceFragment
{
    @Override
    public void onCreatePreferences(final Bundle bundle, final String s) {
        this.addPreferencesFromResource(R$xml.other_settings);
    }
}
