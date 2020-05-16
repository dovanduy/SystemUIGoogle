// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import android.text.TextUtils;
import android.view.View$OnClickListener;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import androidx.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.content.Context;
import androidx.preference.Preference;

public class ActionButtonsPreference extends Preference
{
    private final ButtonInfo mButton1Info;
    private final ButtonInfo mButton2Info;
    private final ButtonInfo mButton3Info;
    private final ButtonInfo mButton4Info;
    
    public ActionButtonsPreference(final Context context, final AttributeSet set) {
        super(context, set);
        this.mButton1Info = new ButtonInfo();
        this.mButton2Info = new ButtonInfo();
        this.mButton3Info = new ButtonInfo();
        this.mButton4Info = new ButtonInfo();
        this.init();
    }
    
    private void init() {
        this.setLayoutResource(R$layout.settings_action_buttons);
        this.setSelectable(false);
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(true);
        preferenceViewHolder.setDividerAllowedBelow(true);
        this.mButton1Info.mButton = (Button)preferenceViewHolder.findViewById(R$id.button1);
        this.mButton2Info.mButton = (Button)preferenceViewHolder.findViewById(R$id.button2);
        this.mButton3Info.mButton = (Button)preferenceViewHolder.findViewById(R$id.button3);
        this.mButton4Info.mButton = (Button)preferenceViewHolder.findViewById(R$id.button4);
        this.mButton1Info.setUpButton();
        this.mButton2Info.setUpButton();
        this.mButton3Info.setUpButton();
        this.mButton4Info.setUpButton();
    }
    
    static class ButtonInfo
    {
        private Button mButton;
        private Drawable mIcon;
        private boolean mIsEnabled;
        private boolean mIsVisible;
        private View$OnClickListener mListener;
        private CharSequence mText;
        
        ButtonInfo() {
            this.mIsEnabled = true;
            this.mIsVisible = true;
        }
        
        private boolean shouldBeVisible() {
            return this.mIsVisible && (!TextUtils.isEmpty(this.mText) || this.mIcon != null);
        }
        
        void setUpButton() {
            this.mButton.setText(this.mText);
            this.mButton.setOnClickListener(this.mListener);
            this.mButton.setEnabled(this.mIsEnabled);
            this.mButton.setCompoundDrawablesWithIntrinsicBounds((Drawable)null, this.mIcon, (Drawable)null, (Drawable)null);
            if (this.shouldBeVisible()) {
                this.mButton.setVisibility(0);
            }
            else {
                this.mButton.setVisibility(8);
            }
        }
    }
}
