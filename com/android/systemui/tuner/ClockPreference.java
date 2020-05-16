// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.text.TextUtils;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import android.util.ArraySet;
import androidx.preference.DropDownPreference;

public class ClockPreference extends DropDownPreference implements Tunable
{
    private ArraySet<String> mBlacklist;
    private final String mClock;
    private boolean mClockEnabled;
    private boolean mHasSeconds;
    private boolean mHasSetValue;
    private boolean mReceivedClock;
    private boolean mReceivedSeconds;
    
    public ClockPreference(final Context context, final AttributeSet set) {
        super(context, set);
        this.mClock = context.getString(17041286);
        this.setEntryValues(new CharSequence[] { "seconds", "default", "disabled" });
    }
    
    @Override
    public void onAttached() {
        super.onAttached();
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "icon_blacklist", "clock_seconds");
    }
    
    @Override
    public void onDetached() {
        Dependency.get(TunerService.class).removeTunable((TunerService.Tunable)this);
        super.onDetached();
    }
    
    @Override
    public void onTuningChanged(final String s, final String s2) {
        if ("icon_blacklist".equals(s)) {
            this.mReceivedClock = true;
            final ArraySet<String> iconBlacklist = StatusBarIconController.getIconBlacklist(this.getContext(), s2);
            this.mBlacklist = iconBlacklist;
            this.mClockEnabled = (iconBlacklist.contains((Object)this.mClock) ^ true);
        }
        else if ("clock_seconds".equals(s)) {
            this.mReceivedSeconds = true;
            this.mHasSeconds = (s2 != null && Integer.parseInt(s2) != 0);
        }
        if (!this.mHasSetValue && this.mReceivedClock && this.mReceivedSeconds) {
            this.mHasSetValue = true;
            if (this.mClockEnabled && this.mHasSeconds) {
                this.setValue("seconds");
            }
            else if (this.mClockEnabled) {
                this.setValue("default");
            }
            else {
                this.setValue("disabled");
            }
        }
    }
    
    @Override
    protected boolean persistString(final String s) {
        Dependency.get(TunerService.class).setValue("clock_seconds", "seconds".equals(s) ? 1 : 0);
        if ("disabled".equals(s)) {
            this.mBlacklist.add((Object)this.mClock);
        }
        else {
            this.mBlacklist.remove((Object)this.mClock);
        }
        Dependency.get(TunerService.class).setValue("icon_blacklist", TextUtils.join((CharSequence)",", (Iterable)this.mBlacklist));
        return true;
    }
}
