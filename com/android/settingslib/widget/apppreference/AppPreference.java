// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget.apppreference;

import android.widget.ProgressBar;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.widget.R$layout;
import android.util.AttributeSet;
import android.content.Context;
import androidx.preference.Preference;

public class AppPreference extends Preference
{
    private int mProgress;
    private boolean mProgressVisible;
    
    public AppPreference(final Context context, final AttributeSet set) {
        super(context, set);
        this.setLayoutResource(R$layout.preference_app);
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        final ProgressBar progressBar = (ProgressBar)preferenceViewHolder.findViewById(16908301);
        if (this.mProgressVisible) {
            progressBar.setProgress(this.mProgress);
            progressBar.setVisibility(0);
        }
        else {
            progressBar.setVisibility(8);
        }
    }
}
