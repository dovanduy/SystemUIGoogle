// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib;

import android.text.TextUtils;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import android.util.TypedValue;
import android.content.res.TypedArray;
import android.os.UserHandle;
import android.util.AttributeSet;
import androidx.preference.Preference;
import android.content.Context;

public class RestrictedPreferenceHelper
{
    private String mAttrUserRestriction;
    private final Context mContext;
    private boolean mDisabledByAdmin;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private final Preference mPreference;
    private boolean mUseAdminDisabledSummary;
    
    public RestrictedPreferenceHelper(final Context mContext, final Preference mPreference, final AttributeSet set) {
        this.mAttrUserRestriction = null;
        final boolean b = false;
        this.mUseAdminDisabledSummary = false;
        this.mContext = mContext;
        this.mPreference = mPreference;
        if (set != null) {
            final TypedArray obtainStyledAttributes = mContext.obtainStyledAttributes(set, R$styleable.RestrictedPreference);
            final TypedValue peekValue = obtainStyledAttributes.peekValue(R$styleable.RestrictedPreference_userRestriction);
            CharSequence charSequence;
            if (peekValue != null && peekValue.type == 3) {
                final int resourceId = peekValue.resourceId;
                if (resourceId != 0) {
                    charSequence = mContext.getText(resourceId);
                }
                else {
                    charSequence = peekValue.string;
                }
            }
            else {
                charSequence = null;
            }
            String string;
            if (charSequence == null) {
                string = null;
            }
            else {
                string = charSequence.toString();
            }
            this.mAttrUserRestriction = string;
            if (RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, string, UserHandle.myUserId())) {
                this.mAttrUserRestriction = null;
                return;
            }
            final TypedValue peekValue2 = obtainStyledAttributes.peekValue(R$styleable.RestrictedPreference_useAdminDisabledSummary);
            if (peekValue2 != null) {
                boolean mUseAdminDisabledSummary = b;
                if (peekValue2.type == 18) {
                    mUseAdminDisabledSummary = b;
                    if (peekValue2.data != 0) {
                        mUseAdminDisabledSummary = true;
                    }
                }
                this.mUseAdminDisabledSummary = mUseAdminDisabledSummary;
            }
        }
    }
    
    public void checkRestrictionAndSetDisabled(final String s, final int n) {
        this.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, s, n));
    }
    
    public boolean isDisabledByAdmin() {
        return this.mDisabledByAdmin;
    }
    
    public void onAttachedToHierarchy() {
        final String mAttrUserRestriction = this.mAttrUserRestriction;
        if (mAttrUserRestriction != null) {
            this.checkRestrictionAndSetDisabled(mAttrUserRestriction, UserHandle.myUserId());
        }
    }
    
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        if (this.mDisabledByAdmin) {
            preferenceViewHolder.itemView.setEnabled(true);
        }
        if (this.mUseAdminDisabledSummary) {
            final TextView textView = (TextView)preferenceViewHolder.findViewById(16908304);
            if (textView != null) {
                final CharSequence text = textView.getContext().getText(R$string.disabled_by_admin_summary_text);
                if (this.mDisabledByAdmin) {
                    textView.setText(text);
                }
                else if (TextUtils.equals(text, textView.getText())) {
                    textView.setText((CharSequence)null);
                }
            }
        }
    }
    
    public boolean performClick() {
        if (this.mDisabledByAdmin) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, this.mEnforcedAdmin);
            return true;
        }
        return false;
    }
    
    public boolean setDisabledByAdmin(final RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin) {
        boolean b = false;
        final boolean mDisabledByAdmin = mEnforcedAdmin != null;
        this.mEnforcedAdmin = mEnforcedAdmin;
        if (this.mDisabledByAdmin != mDisabledByAdmin) {
            this.mDisabledByAdmin = mDisabledByAdmin;
            b = true;
        }
        this.mPreference.setEnabled(mDisabledByAdmin ^ true);
        return b;
    }
    
    public void useAdminDisabledSummary(final boolean mUseAdminDisabledSummary) {
        this.mUseAdminDisabledSummary = mUseAdminDisabledSummary;
    }
}
