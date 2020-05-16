// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib;

import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.PreferenceManager;
import android.util.TypedValue;
import android.content.res.TypedArray;
import androidx.preference.Preference;
import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;
import androidx.preference.SwitchPreference;

public class RestrictedSwitchPreference extends SwitchPreference
{
    RestrictedPreferenceHelper mHelper;
    private int mIconSize;
    CharSequence mRestrictedSwitchSummary;
    boolean mUseAdditionalSummary;
    
    public RestrictedSwitchPreference(final Context context) {
        this(context, null);
    }
    
    public RestrictedSwitchPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R$attr.switchPreferenceStyle, 16843629));
    }
    
    public RestrictedSwitchPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public RestrictedSwitchPreference(final Context context, final AttributeSet set, int resourceId, final int n) {
        super(context, set, resourceId, n);
        this.mUseAdditionalSummary = false;
        this.setWidgetLayoutResource(R$layout.restricted_switch_widget);
        this.mHelper = new RestrictedPreferenceHelper(context, this, set);
        if (set != null) {
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.RestrictedSwitchPreference);
            final TypedValue peekValue = obtainStyledAttributes.peekValue(R$styleable.RestrictedSwitchPreference_useAdditionalSummary);
            if (peekValue != null) {
                this.mUseAdditionalSummary = (peekValue.type == 18 && peekValue.data != 0);
            }
            final TypedValue peekValue2 = obtainStyledAttributes.peekValue(R$styleable.RestrictedSwitchPreference_restrictedSwitchSummary);
            if (peekValue2 != null && peekValue2.type == 3) {
                resourceId = peekValue2.resourceId;
                if (resourceId != 0) {
                    this.mRestrictedSwitchSummary = context.getText(resourceId);
                }
                else {
                    this.mRestrictedSwitchSummary = peekValue2.string;
                }
            }
        }
        if (this.mUseAdditionalSummary) {
            this.setLayoutResource(R$layout.restricted_switch_preference);
            this.useAdminDisabledSummary(false);
        }
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
        CharSequence charSequence;
        if ((charSequence = this.mRestrictedSwitchSummary) == null) {
            final Context context = this.getContext();
            int n;
            if (this.isChecked()) {
                n = R$string.enabled_by_admin;
            }
            else {
                n = R$string.disabled_by_admin;
            }
            charSequence = context.getText(n);
        }
        final View viewById = preferenceViewHolder.findViewById(R$id.restricted_icon);
        final View viewById2 = preferenceViewHolder.findViewById(16908352);
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
        if (viewById2 != null) {
            int visibility2;
            if (this.isDisabledByAdmin()) {
                visibility2 = 8;
            }
            else {
                visibility2 = 0;
            }
            viewById2.setVisibility(visibility2);
        }
        final ImageView imageView = (ImageView)preferenceViewHolder.itemView.findViewById(16908294);
        if (this.mIconSize > 0) {
            final int mIconSize = this.mIconSize;
            imageView.setLayoutParams((ViewGroup$LayoutParams)new LinearLayout$LayoutParams(mIconSize, mIconSize));
        }
        if (this.mUseAdditionalSummary) {
            final TextView textView = (TextView)preferenceViewHolder.findViewById(R$id.additional_summary);
            if (textView != null) {
                if (this.isDisabledByAdmin()) {
                    textView.setText(charSequence);
                    textView.setVisibility(0);
                }
                else {
                    textView.setVisibility(8);
                }
            }
        }
        else {
            final TextView textView2 = (TextView)preferenceViewHolder.findViewById(16908304);
            if (textView2 != null && this.isDisabledByAdmin()) {
                textView2.setText(charSequence);
                textView2.setVisibility(0);
            }
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
    
    public void useAdminDisabledSummary(final boolean b) {
        this.mHelper.useAdminDisabledSummary(b);
    }
}
