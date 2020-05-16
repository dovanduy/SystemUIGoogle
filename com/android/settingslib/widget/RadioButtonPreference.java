// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import android.widget.TextView;
import android.text.TextUtils;
import androidx.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import androidx.preference.CheckBoxPreference;

public class RadioButtonPreference extends CheckBoxPreference
{
    private View mAppendix;
    private int mAppendixVisibility;
    private OnClickListener mListener;
    
    public RadioButtonPreference(final Context context, final AttributeSet set) {
        super(context, set);
        this.mListener = null;
        this.mAppendixVisibility = -1;
        this.init();
    }
    
    private void init() {
        this.setWidgetLayoutResource(R$layout.preference_widget_radiobutton);
        this.setLayoutResource(R$layout.preference_radio);
        this.setIconSpaceReserved(false);
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        final View viewById = preferenceViewHolder.findViewById(R$id.summary_container);
        if (viewById != null) {
            int visibility;
            if (TextUtils.isEmpty(this.getSummary())) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            viewById.setVisibility(visibility);
            final View viewById2 = preferenceViewHolder.findViewById(R$id.appendix);
            this.mAppendix = viewById2;
            if (viewById2 != null) {
                final int mAppendixVisibility = this.mAppendixVisibility;
                if (mAppendixVisibility != -1) {
                    viewById2.setVisibility(mAppendixVisibility);
                }
            }
        }
        final TextView textView = (TextView)preferenceViewHolder.findViewById(16908310);
        if (textView != null) {
            textView.setSingleLine(false);
            textView.setMaxLines(3);
        }
    }
    
    public void onClick() {
        final OnClickListener mListener = this.mListener;
        if (mListener != null) {
            mListener.onRadioButtonClicked(this);
        }
    }
    
    public interface OnClickListener
    {
        void onRadioButtonClicked(final RadioButtonPreference p0);
    }
}
