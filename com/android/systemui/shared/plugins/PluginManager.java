// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.plugins;

import android.text.TextUtils;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;

public interface PluginManager
{
     <T extends Plugin> void addPluginListener(final PluginListener<T> p0, final Class<?> p1);
    
     <T extends Plugin> void addPluginListener(final PluginListener<T> p0, final Class<?> p1, final boolean p2);
    
     <T extends Plugin> void addPluginListener(final String p0, final PluginListener<T> p1, final Class<?> p2);
    
     <T> boolean dependsOn(final Plugin p0, final Class<T> p1);
    
    String[] getWhitelistedPlugins();
    
    void removePluginListener(final PluginListener<?> p0);
    
    public static class Helper
    {
        public static <P> String getAction(final Class<P> clazz) {
            final ProvidesInterface providesInterface = clazz.getDeclaredAnnotation(ProvidesInterface.class);
            if (providesInterface == null) {
                final StringBuilder sb = new StringBuilder();
                sb.append(clazz);
                sb.append(" doesn't provide an interface");
                throw new RuntimeException(sb.toString());
            }
            if (!TextUtils.isEmpty((CharSequence)providesInterface.action())) {
                return providesInterface.action();
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(clazz);
            sb2.append(" doesn't provide an action");
            throw new RuntimeException(sb2.toString());
        }
    }
}
