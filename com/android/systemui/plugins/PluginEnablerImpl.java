// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.content.ComponentName;
import com.android.internal.annotations.VisibleForTesting;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import com.android.systemui.shared.plugins.PluginEnabler;

public class PluginEnablerImpl implements PluginEnabler
{
    private static final String CRASH_DISABLED_PLUGINS_PREF_FILE = "auto_disabled_plugins_prefs";
    private final SharedPreferences mAutoDisabledPrefs;
    private PackageManager mPm;
    
    public PluginEnablerImpl(final Context context) {
        this(context, context.getPackageManager());
    }
    
    @VisibleForTesting
    public PluginEnablerImpl(final Context context, final PackageManager mPm) {
        this.mAutoDisabledPrefs = context.getSharedPreferences("auto_disabled_plugins_prefs", 0);
        this.mPm = mPm;
    }
    
    @Override
    public int getDisableReason(final ComponentName componentName) {
        if (this.isEnabled(componentName)) {
            return 0;
        }
        return this.mAutoDisabledPrefs.getInt(componentName.flattenToString(), 1);
    }
    
    @Override
    public boolean isEnabled(final ComponentName componentName) {
        return this.mPm.getComponentEnabledSetting(componentName) != 2;
    }
    
    @Override
    public void setDisabled(final ComponentName componentName, final int n) {
        final boolean b = n == 0;
        int n2;
        if (b) {
            n2 = 1;
        }
        else {
            n2 = 2;
        }
        this.mPm.setComponentEnabledSetting(componentName, n2, 1);
        if (b) {
            this.mAutoDisabledPrefs.edit().remove(componentName.flattenToString()).apply();
        }
        else {
            this.mAutoDisabledPrefs.edit().putInt(componentName.flattenToString(), n).apply();
        }
    }
    
    @Override
    public void setEnabled(final ComponentName componentName) {
        this.setDisabled(componentName, 0);
    }
}
