// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib;

import android.view.View;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference;
import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public class RestrictedPreference extends TwoTargetPreference
{
    RestrictedPreferenceHelper mHelper;
    
    public RestrictedPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R$attr.preferenceStyle, 16842894));
    }
    
    public RestrictedPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public RestrictedPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mHelper = new RestrictedPreferenceHelper(context, this, set);
    }
    
    @Override
    protected int getSecondTargetResId() {
        return R$layout.restricted_icon;
    }
    
    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }
    
    @Override
    protected void onAttachedToHierarchy(final PreferenceManager preferenceManager) {
        this.mHelper.onAttachedToHierarchy();
        super.onAttachedToHierarchy(preferenceManager);
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
        final View viewById = preferenceViewHolder.findViewById(R$id.restricted_icon);
        if (viewById != null) {
            int visibility;
            if (this.isDisabledByAdmin()) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
        }
    }
    
    @Override
    public void performClick() {
        if (!this.mHelper.performClick()) {
            super.performClick();
        }
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        if (enabled && this.isDisabledByAdmin()) {
            this.mHelper.setDisabledByAdmin(null);
            return;
        }
        super.setEnabled(enabled);
    }
    
    @Override
    protected boolean shouldHideSecondTarget() {
        return this.isDisabledByAdmin() ^ true;
    }
}
