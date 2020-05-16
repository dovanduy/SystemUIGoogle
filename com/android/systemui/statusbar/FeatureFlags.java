// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import java.util.Iterator;
import android.provider.DeviceConfig$Properties;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import android.provider.DeviceConfig;
import android.util.ArrayMap;
import java.util.concurrent.Executor;
import java.util.Map;

public class FeatureFlags
{
    private final Map<String, Boolean> mCachedDeviceConfigFlags;
    
    public FeatureFlags(final Executor executor) {
        this.mCachedDeviceConfigFlags = (Map<String, Boolean>)new ArrayMap();
        DeviceConfig.addOnPropertiesChangedListener("systemui", executor, (DeviceConfig$OnPropertiesChangedListener)new _$$Lambda$FeatureFlags$quCg0PddUt747ILcyW36A9ZoNcM(this));
    }
    
    private boolean getDeviceConfigFlag(final String s, final boolean b) {
        synchronized (this.mCachedDeviceConfigFlags) {
            Boolean value;
            if ((value = this.mCachedDeviceConfigFlags.get(s)) == null) {
                value = DeviceConfig.getBoolean("systemui", s, b);
                this.mCachedDeviceConfigFlags.put(s, value);
            }
            return value;
        }
    }
    
    private void onPropertiesChanged(final DeviceConfig$Properties deviceConfig$Properties) {
        synchronized (this.mCachedDeviceConfigFlags) {
            final Iterator<String> iterator = deviceConfig$Properties.getKeyset().iterator();
            while (iterator.hasNext()) {
                this.mCachedDeviceConfigFlags.remove(iterator.next());
            }
        }
    }
    
    public boolean isNewNotifPipelineEnabled() {
        return this.getDeviceConfigFlag("notification.newpipeline.enabled", true);
    }
    
    public boolean isNewNotifPipelineRenderingEnabled() {
        final boolean newNotifPipelineEnabled = this.isNewNotifPipelineEnabled();
        boolean b = false;
        if (newNotifPipelineEnabled) {
            b = b;
            if (this.getDeviceConfigFlag("notification.newpipeline.rendering", false)) {
                b = true;
            }
        }
        return b;
    }
}
