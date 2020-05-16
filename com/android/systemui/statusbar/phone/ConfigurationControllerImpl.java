// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.Iterator;
import java.util.Collection;
import android.content.res.Resources;
import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;
import android.os.LocaleList;
import java.util.List;
import android.content.res.Configuration;
import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;

public final class ConfigurationControllerImpl implements ConfigurationController
{
    private final Context context;
    private int density;
    private float fontScale;
    private final boolean inCarMode;
    private final Configuration lastConfig;
    private final List<ConfigurationListener> listeners;
    private LocaleList localeList;
    private int uiMode;
    
    public ConfigurationControllerImpl(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.listeners = new ArrayList<ConfigurationListener>();
        this.lastConfig = new Configuration();
        final Resources resources = context.getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources, "context.resources");
        final Configuration configuration = resources.getConfiguration();
        this.context = context;
        this.fontScale = configuration.fontScale;
        this.density = configuration.densityDpi;
        this.inCarMode = ((configuration.uiMode & 0xF) == 0x3);
        this.uiMode = (configuration.uiMode & 0x30);
        Intrinsics.checkExpressionValueIsNotNull(configuration, "currentConfig");
        this.localeList = configuration.getLocales();
    }
    
    @Override
    public void addCallback(final ConfigurationListener configurationListener) {
        Intrinsics.checkParameterIsNotNull(configurationListener, "listener");
        this.listeners.add(configurationListener);
        configurationListener.onDensityOrFontScaleChanged();
    }
    
    @Override
    public void notifyThemeChanged() {
        for (final ConfigurationListener configurationListener : new ArrayList<ConfigurationListener>(this.listeners)) {
            if (this.listeners.contains(configurationListener)) {
                configurationListener.onThemeChanged();
            }
        }
    }
    
    @Override
    public void onConfigurationChanged(final Configuration configuration) {
        Intrinsics.checkParameterIsNotNull(configuration, "newConfig");
        final ArrayList<ConfigurationListener> list = new ArrayList<ConfigurationListener>(this.listeners);
        for (final ConfigurationListener configurationListener : list) {
            if (this.listeners.contains(configurationListener)) {
                configurationListener.onConfigChanged(configuration);
            }
        }
        final float fontScale = configuration.fontScale;
        final int densityDpi = configuration.densityDpi;
        final int uiMode = configuration.uiMode & 0x30;
        final boolean b = uiMode != this.uiMode;
        if (densityDpi != this.density || fontScale != this.fontScale || (this.inCarMode && b)) {
            for (final ConfigurationListener configurationListener2 : list) {
                if (this.listeners.contains(configurationListener2)) {
                    configurationListener2.onDensityOrFontScaleChanged();
                }
            }
            this.density = densityDpi;
            this.fontScale = fontScale;
        }
        final LocaleList locales = configuration.getLocales();
        if (Intrinsics.areEqual(locales, this.localeList) ^ true) {
            this.localeList = locales;
            for (final ConfigurationListener configurationListener3 : list) {
                if (this.listeners.contains(configurationListener3)) {
                    configurationListener3.onLocaleListChanged();
                }
            }
        }
        if (b) {
            this.context.getTheme().applyStyle(this.context.getThemeResId(), true);
            this.uiMode = uiMode;
            for (final ConfigurationListener configurationListener4 : list) {
                if (this.listeners.contains(configurationListener4)) {
                    configurationListener4.onUiModeChanged();
                }
            }
        }
        if ((this.lastConfig.updateFrom(configuration) & Integer.MIN_VALUE) != 0x0) {
            for (final ConfigurationListener configurationListener5 : list) {
                if (this.listeners.contains(configurationListener5)) {
                    configurationListener5.onOverlayChanged();
                }
            }
        }
    }
    
    @Override
    public void removeCallback(final ConfigurationListener configurationListener) {
        Intrinsics.checkParameterIsNotNull(configurationListener, "listener");
        this.listeners.remove(configurationListener);
    }
}
