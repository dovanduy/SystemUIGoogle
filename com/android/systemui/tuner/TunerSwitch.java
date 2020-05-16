// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.content.ContentResolver;
import android.provider.Settings$Secure;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import android.content.res.TypedArray;
import com.android.systemui.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import androidx.preference.SwitchPreference;

public class TunerSwitch extends SwitchPreference implements Tunable
{
    private final int mAction;
    private final boolean mDefault;
    
    public TunerSwitch(final Context context, final AttributeSet set) {
        super(context, set);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.TunerSwitch);
        this.mDefault = obtainStyledAttributes.getBoolean(R$styleable.TunerSwitch_defValue, false);
        this.mAction = obtainStyledAttributes.getInt(R$styleable.TunerSwitch_metricsAction, -1);
    }
    
    @Override
    public void onAttached() {
        super.onAttached();
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, this.getKey().split(","));
    }
    
    @Override
    protected void onClick() {
        super.onClick();
        if (this.mAction != -1) {
            MetricsLogger.action(this.getContext(), this.mAction, this.isChecked());
        }
    }
    
    @Override
    public void onDetached() {
        Dependency.get(TunerService.class).removeTunable((TunerService.Tunable)this);
        super.onDetached();
    }
    
    @Override
    public void onTuningChanged(final String s, final String s2) {
        this.setChecked(TunerService.parseIntegerSwitch(s2, this.mDefault));
    }
    
    @Override
    protected boolean persistBoolean(final boolean b) {
        for (final String s : this.getKey().split(",")) {
            final ContentResolver contentResolver = this.getContext().getContentResolver();
            String s2;
            if (b) {
                s2 = "1";
            }
            else {
                s2 = "0";
            }
            Settings$Secure.putString(contentResolver, s, s2);
        }
        return true;
    }
}
