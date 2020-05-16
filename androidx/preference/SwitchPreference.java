// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.widget.CompoundButton;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.CompoundButton$OnCheckedChangeListener;
import android.widget.Switch;
import android.view.View;
import android.content.res.TypedArray;
import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public class SwitchPreference extends TwoStatePreference
{
    private final Listener mListener;
    private CharSequence mSwitchOff;
    private CharSequence mSwitchOn;
    
    public SwitchPreference(final Context context) {
        this(context, null);
    }
    
    public SwitchPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R$attr.switchPreferenceStyle, 16843629));
    }
    
    public SwitchPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public SwitchPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mListener = new Listener();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.SwitchPreference, n, n2);
        this.setSummaryOn(TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.SwitchPreference_summaryOn, R$styleable.SwitchPreference_android_summaryOn));
        this.setSummaryOff(TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.SwitchPreference_summaryOff, R$styleable.SwitchPreference_android_summaryOff));
        this.setSwitchTextOn(TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.SwitchPreference_switchTextOn, R$styleable.SwitchPreference_android_switchTextOn));
        this.setSwitchTextOff(TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.SwitchPreference_switchTextOff, R$styleable.SwitchPreference_android_switchTextOff));
        this.setDisableDependentsState(TypedArrayUtils.getBoolean(obtainStyledAttributes, R$styleable.SwitchPreference_disableDependentsState, R$styleable.SwitchPreference_android_disableDependentsState, false));
        obtainStyledAttributes.recycle();
    }
    
    private void syncSwitchView(final View view) {
        final boolean b = view instanceof Switch;
        if (b) {
            ((Switch)view).setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)null);
        }
        if (view instanceof Checkable) {
            ((Checkable)view).setChecked(super.mChecked);
        }
        if (b) {
            final Switch switch1 = (Switch)view;
            switch1.setTextOn(this.mSwitchOn);
            switch1.setTextOff(this.mSwitchOff);
            switch1.setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)this.mListener);
        }
    }
    
    private void syncViewIfAccessibilityEnabled(final View view) {
        if (!((AccessibilityManager)this.getContext().getSystemService("accessibility")).isEnabled()) {
            return;
        }
        this.syncSwitchView(view.findViewById(16908352));
        this.syncSummaryView(view.findViewById(16908304));
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.syncSwitchView(preferenceViewHolder.findViewById(16908352));
        this.syncSummaryView(preferenceViewHolder);
    }
    
    @Override
    protected void performClick(final View view) {
        super.performClick(view);
        this.syncViewIfAccessibilityEnabled(view);
    }
    
    public void setSwitchTextOff(final CharSequence mSwitchOff) {
        this.mSwitchOff = mSwitchOff;
        this.notifyChanged();
    }
    
    public void setSwitchTextOn(final CharSequence mSwitchOn) {
        this.mSwitchOn = mSwitchOn;
        this.notifyChanged();
    }
    
    private class Listener implements CompoundButton$OnCheckedChangeListener
    {
        Listener() {
        }
        
        public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
            if (!SwitchPreference.this.callChangeListener(b)) {
                compoundButton.setChecked(b ^ true);
                return;
            }
            SwitchPreference.this.setChecked(b);
        }
    }
}
