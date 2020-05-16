// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import androidx.preference.ListPreferenceDialogFragment;
import android.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;

public abstract class TunerPreferenceFragment extends PreferenceFragment
{
    @Override
    public void onDisplayPreferenceDialog(final Preference preference) {
        ListPreferenceDialogFragment instance;
        if (preference instanceof CustomListPreference) {
            instance = CustomListPreference.CustomListPreferenceDialogFragment.newInstance(preference.getKey());
        }
        else {
            super.onDisplayPreferenceDialog(preference);
            instance = null;
        }
        instance.setTargetFragment((Fragment)this, 0);
        instance.show(this.getFragmentManager(), "dialog_preference");
    }
}
