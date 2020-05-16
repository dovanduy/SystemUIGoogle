// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.res.Configuration;

public interface ConfigurationController extends CallbackController<ConfigurationListener>
{
    void notifyThemeChanged();
    
    void onConfigurationChanged(final Configuration p0);
    
    public interface ConfigurationListener
    {
        default void onConfigChanged(final Configuration configuration) {
        }
        
        default void onDensityOrFontScaleChanged() {
        }
        
        default void onLocaleListChanged() {
        }
        
        default void onOverlayChanged() {
        }
        
        default void onThemeChanged() {
        }
        
        default void onUiModeChanged() {
        }
    }
}
