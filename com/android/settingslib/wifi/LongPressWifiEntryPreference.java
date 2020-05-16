// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.wifi;

import android.view.View$OnCreateContextMenuListener;
import androidx.preference.PreferenceViewHolder;
import androidx.fragment.app.Fragment;

public class LongPressWifiEntryPreference extends WifiEntryPreference
{
    private final Fragment mFragment;
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        final Fragment mFragment = this.mFragment;
        if (mFragment != null) {
            preferenceViewHolder.itemView.setOnCreateContextMenuListener((View$OnCreateContextMenuListener)mFragment);
            preferenceViewHolder.itemView.setTag((Object)this);
            preferenceViewHolder.itemView.setLongClickable(true);
        }
    }
}
