// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib;

import android.view.View;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.content.Context;
import androidx.preference.Preference;

public class TwoTargetPreference extends Preference
{
    private int mIconSize;
    private int mMediumIconSize;
    private int mSmallIconSize;
    
    public TwoTargetPreference(final Context context, final AttributeSet set) {
        super(context, set);
        this.init(context);
    }
    
    public TwoTargetPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.init(context);
    }
    
    private void init(final Context context) {
        this.setLayoutResource(R$layout.preference_two_target);
        this.mSmallIconSize = context.getResources().getDimensionPixelSize(R$dimen.two_target_pref_small_icon_size);
        this.mMediumIconSize = context.getResources().getDimensionPixelSize(R$dimen.two_target_pref_medium_icon_size);
        final int secondTargetResId = this.getSecondTargetResId();
        if (secondTargetResId != 0) {
            this.setWidgetLayoutResource(secondTargetResId);
        }
    }
    
    protected int getSecondTargetResId() {
        return 0;
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        final ImageView imageView = (ImageView)preferenceViewHolder.itemView.findViewById(16908294);
        final int mIconSize = this.mIconSize;
        if (mIconSize != 1) {
            if (mIconSize == 2) {
                final int mSmallIconSize = this.mSmallIconSize;
                imageView.setLayoutParams((ViewGroup$LayoutParams)new LinearLayout$LayoutParams(mSmallIconSize, mSmallIconSize));
            }
        }
        else {
            final int mMediumIconSize = this.mMediumIconSize;
            imageView.setLayoutParams((ViewGroup$LayoutParams)new LinearLayout$LayoutParams(mMediumIconSize, mMediumIconSize));
        }
        final View viewById = preferenceViewHolder.findViewById(R$id.two_target_divider);
        final View viewById2 = preferenceViewHolder.findViewById(16908312);
        final boolean shouldHideSecondTarget = this.shouldHideSecondTarget();
        final int n = 8;
        if (viewById != null) {
            int visibility;
            if (shouldHideSecondTarget) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            viewById.setVisibility(visibility);
        }
        if (viewById2 != null) {
            int visibility2;
            if (shouldHideSecondTarget) {
                visibility2 = n;
            }
            else {
                visibility2 = 0;
            }
            viewById2.setVisibility(visibility2);
        }
    }
    
    protected boolean shouldHideSecondTarget() {
        return this.getSecondTargetResId() == 0;
    }
}
