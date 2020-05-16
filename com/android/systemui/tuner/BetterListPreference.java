// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.util.AttributeSet;
import android.content.Context;
import androidx.preference.ListPreference;

public class BetterListPreference extends ListPreference
{
    private CharSequence mSummary;
    
    public BetterListPreference(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    @Override
    public CharSequence getSummary() {
        return this.mSummary;
    }
    
    @Override
    public void setSummary(final CharSequence charSequence) {
        super.setSummary(charSequence);
        this.mSummary = charSequence;
    }
}
