// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.text.TextUtils;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import android.content.ContentResolver;
import com.android.systemui.Dependency;
import android.provider.Settings$System;
import android.util.AttributeSet;
import android.content.Context;
import android.util.ArraySet;
import androidx.preference.DropDownPreference;

public class BatteryPreference extends DropDownPreference implements Tunable
{
    private final String mBattery;
    private boolean mBatteryEnabled;
    private ArraySet<String> mBlacklist;
    private boolean mHasPercentage;
    private boolean mHasSetValue;
    
    public BatteryPreference(final Context context, final AttributeSet set) {
        super(context, set);
        this.mBattery = context.getString(17041281);
        this.setEntryValues(new CharSequence[] { "percent", "default", "disabled" });
    }
    
    @Override
    public void onAttached() {
        super.onAttached();
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean mHasPercentage = false;
        if (Settings$System.getInt(contentResolver, "status_bar_show_battery_percent", 0) != 0) {
            mHasPercentage = true;
        }
        this.mHasPercentage = mHasPercentage;
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "icon_blacklist");
    }
    
    @Override
    public void onDetached() {
        Dependency.get(TunerService.class).removeTunable((TunerService.Tunable)this);
        super.onDetached();
    }
    
    @Override
    public void onTuningChanged(final String anObject, final String s) {
        if ("icon_blacklist".equals(anObject)) {
            final ArraySet<String> iconBlacklist = StatusBarIconController.getIconBlacklist(this.getContext(), s);
            this.mBlacklist = iconBlacklist;
            this.mBatteryEnabled = (iconBlacklist.contains((Object)this.mBattery) ^ true);
        }
        if (!this.mHasSetValue) {
            this.mHasSetValue = true;
            if (this.mBatteryEnabled && this.mHasPercentage) {
                this.setValue("percent");
            }
            else if (this.mBatteryEnabled) {
                this.setValue("default");
            }
            else {
                this.setValue("disabled");
            }
        }
    }
    
    @Override
    protected boolean persistString(final String s) {
        final int equals = "percent".equals(s) ? 1 : 0;
        MetricsLogger.action(this.getContext(), 237, (boolean)(equals != 0));
        Settings$System.putInt(this.getContext().getContentResolver(), "status_bar_show_battery_percent", equals);
        if ("disabled".equals(s)) {
            this.mBlacklist.add((Object)this.mBattery);
        }
        else {
            this.mBlacklist.remove((Object)this.mBattery);
        }
        Dependency.get(TunerService.class).setValue("icon_blacklist", TextUtils.join((CharSequence)",", (Iterable)this.mBlacklist));
        return true;
    }
}
