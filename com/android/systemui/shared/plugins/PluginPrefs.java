// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.plugins;

import java.util.Collection;
import android.util.ArraySet;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

public class PluginPrefs
{
    private final Set<String> mPluginActions;
    private final SharedPreferences mSharedPrefs;
    
    public PluginPrefs(final Context context) {
        this.mSharedPrefs = context.getSharedPreferences("plugin_prefs", 0);
        this.mPluginActions = (Set<String>)new ArraySet((Collection)this.mSharedPrefs.getStringSet("actions", (Set)null));
    }
    
    public static boolean hasPlugins(final Context context) {
        return context.getSharedPreferences("plugin_prefs", 0).getBoolean("plugins", false);
    }
    
    public static void setHasPlugins(final Context context) {
        context.getSharedPreferences("plugin_prefs", 0).edit().putBoolean("plugins", true).apply();
    }
    
    public void addAction(final String s) {
        synchronized (this) {
            if (this.mPluginActions.add(s)) {
                this.mSharedPrefs.edit().putStringSet("actions", (Set)this.mPluginActions).apply();
            }
        }
    }
    
    public Set<String> getPluginList() {
        return (Set<String>)new ArraySet((Collection)this.mPluginActions);
    }
}
