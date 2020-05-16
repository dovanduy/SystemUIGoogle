// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import com.android.systemui.R$array;
import com.android.systemui.shared.plugins.PluginEnabler;
import android.content.Context;
import com.android.systemui.Dependency;
import android.os.Looper;
import com.android.systemui.shared.plugins.PluginInitializer;

public class PluginInitializerImpl implements PluginInitializer
{
    private static final boolean WTFS_SHOULD_CRASH = false;
    private boolean mWtfsSet;
    
    @Override
    public Looper getBgLooper() {
        return Dependency.get(Dependency.BG_LOOPER);
    }
    
    @Override
    public PluginEnabler getPluginEnabler(final Context context) {
        return new PluginEnablerImpl(context);
    }
    
    @Override
    public String[] getWhitelistedPlugins(final Context context) {
        return context.getResources().getStringArray(R$array.config_pluginWhitelist);
    }
    
    @Override
    public void handleWtfs() {
    }
    
    @Override
    public void onPluginManagerInit() {
        Dependency.get(PluginDependencyProvider.class).allowPluginDependency(ActivityStarter.class);
    }
}
