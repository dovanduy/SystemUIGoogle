// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import com.android.internal.logging.MetricsLogger;
import android.util.ArraySet;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.Dependency;
import android.provider.Settings$Secure;
import android.app.ActivityManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.content.Context;
import java.util.Set;
import androidx.preference.SwitchPreference;

public class StatusBarSwitch extends SwitchPreference implements Tunable
{
    private Set<String> mBlacklist;
    
    public StatusBarSwitch(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private void setList(final Set<String> set) {
        Settings$Secure.putStringForUser(this.getContext().getContentResolver(), "icon_blacklist", TextUtils.join((CharSequence)",", (Iterable)set), ActivityManager.getCurrentUser());
    }
    
    @Override
    public void onAttached() {
        super.onAttached();
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "icon_blacklist");
    }
    
    @Override
    public void onDetached() {
        Dependency.get(TunerService.class).removeTunable((TunerService.Tunable)this);
        super.onDetached();
    }
    
    @Override
    public void onTuningChanged(final String anObject, final String s) {
        if (!"icon_blacklist".equals(anObject)) {
            return;
        }
        final ArraySet<String> iconBlacklist = StatusBarIconController.getIconBlacklist(this.getContext(), s);
        this.mBlacklist = (Set<String>)iconBlacklist;
        this.setChecked(((Set)iconBlacklist).contains(this.getKey()) ^ true);
    }
    
    @Override
    protected boolean persistBoolean(final boolean b) {
        if (!b) {
            if (!this.mBlacklist.contains(this.getKey())) {
                MetricsLogger.action(this.getContext(), 234, this.getKey());
                this.mBlacklist.add(this.getKey());
                this.setList(this.mBlacklist);
            }
        }
        else if (this.mBlacklist.remove(this.getKey())) {
            MetricsLogger.action(this.getContext(), 233, this.getKey());
            this.setList(this.mBlacklist);
        }
        return true;
    }
}
