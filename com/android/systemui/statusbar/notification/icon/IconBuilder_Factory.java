// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.icon;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class IconBuilder_Factory implements Factory<IconBuilder>
{
    private final Provider<Context> contextProvider;
    
    public IconBuilder_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static IconBuilder_Factory create(final Provider<Context> provider) {
        return new IconBuilder_Factory(provider);
    }
    
    public static IconBuilder provideInstance(final Provider<Context> provider) {
        return new IconBuilder(provider.get());
    }
    
    @Override
    public IconBuilder get() {
        return provideInstance(this.contextProvider);
    }
}
