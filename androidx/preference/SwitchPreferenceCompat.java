// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.widget.CompoundButton;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.CompoundButton$OnCheckedChangeListener;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;
import android.content.res.TypedArray;
import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public class SwitchPreferenceCompat extends TwoStatePreference
{
    private final Listener mListener;
    private CharSequence mSwitchOff;
    private CharSequence mSwitchOn;
    
    public SwitchPreferenceCompat(final Context context, final AttributeSet set) {
        this(context, set, R$attr.switchPreferenceCompatStyle);
    }
    
    public SwitchPreferenceCompat(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public SwitchPreferenceCompat(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mListener = new Listener();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.SwitchPreferenceCompat, n, n2);
        this.setSummaryOn(TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.SwitchPreferenceCompat_summaryOn, R$styleable.SwitchPreferenceCompat_android_summaryOn));
        this.setSummaryOff(TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.SwitchPreferenceCompat_summaryOff, R$styleable.SwitchPreferenceCompat_android_summaryOff));
        this.setSwitchTextOn(TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.SwitchPreferenceCompat_switchTextOn, R$styleable.SwitchPreferenceCompat_android_switchTextOn));
        this.setSwitchTextOff(TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.SwitchPreferenceCompat_switchTextOff, R$styleable.SwitchPreferenceCompat_android_switchTextOff));
        this.setDisableDependentsState(TypedArrayUtils.getBoolean(obtainStyledAttributes, R$styleable.SwitchPreferenceCompat_disableDependentsState, R$styleable.SwitchPreferenceCompat_android_disableDependentsState, false));
        obtainStyledAttributes.recycle();
    }
    
    private void syncSwitchView(final View view) {
        final boolean b = view instanceof SwitchCompat;
        if (b) {
            ((SwitchCompat)view).setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)null);
        }
        if (view instanceof Checkable) {
            ((Checkable)view).setChecked(super.mChecked);
        }
        if (b) {
            final SwitchCompat switchCompat = (SwitchCompat)view;
            switchCompat.setTextOn(this.mSwitchOn);
            switchCompat.setTextOff(this.mSwitchOff);
            switchCompat.setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)this.mListener);
        }
    }
    
    private void syncViewIfAccessibilityEnabled(final View view) {
        if (!((AccessibilityManager)this.getContext().getSystemService("accessibility")).isEnabled()) {
            return;
        }
        this.syncSwitchView(view.findViewById(R$id.switchWidget));
        this.syncSummaryView(view.findViewById(16908304));
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.syncSwitchView(preferenceViewHolder.findViewById(R$id.switchWidget));
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
            if (!SwitchPreferenceCompat.this.callChangeListener(b)) {
                compoundButton.setChecked(b ^ true);
                return;
            }
            SwitchPreferenceCompat.this.setChecked(b);
        }
    }
}
