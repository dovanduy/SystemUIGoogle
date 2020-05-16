// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import com.android.systemui.statusbar.phone.StatusBar;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class CameraAction_Builder_Factory implements Factory<CameraAction.Builder>
{
    private final Provider<Context> contextProvider;
    private final Provider<StatusBar> statusBarProvider;
    
    public CameraAction_Builder_Factory(final Provider<Context> contextProvider, final Provider<StatusBar> statusBarProvider) {
        this.contextProvider = contextProvider;
        this.statusBarProvider = statusBarProvider;
    }
    
    public static CameraAction_Builder_Factory create(final Provider<Context> provider, final Provider<StatusBar> provider2) {
        return new CameraAction_Builder_Factory(provider, provider2);
    }
    
    public static CameraAction.Builder provideInstance(final Provider<Context> provider, final Provider<StatusBar> provider2) {
        return new CameraAction.Builder(provider.get(), provider2.get());
    }
    
    @Override
    public CameraAction.Builder get() {
        return provideInstance(this.contextProvider, this.statusBarProvider);
    }
}
