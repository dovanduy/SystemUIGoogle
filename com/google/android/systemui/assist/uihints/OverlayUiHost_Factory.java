// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class OverlayUiHost_Factory implements Factory<OverlayUiHost>
{
    private final Provider<Context> contextProvider;
    private final Provider<TouchOutsideHandler> touchOutsideProvider;
    
    public OverlayUiHost_Factory(final Provider<Context> contextProvider, final Provider<TouchOutsideHandler> touchOutsideProvider) {
        this.contextProvider = contextProvider;
        this.touchOutsideProvider = touchOutsideProvider;
    }
    
    public static OverlayUiHost_Factory create(final Provider<Context> provider, final Provider<TouchOutsideHandler> provider2) {
        return new OverlayUiHost_Factory(provider, provider2);
    }
    
    public static OverlayUiHost provideInstance(final Provider<Context> provider, final Provider<TouchOutsideHandler> provider2) {
        return new OverlayUiHost(provider.get(), provider2.get());
    }
    
    @Override
    public OverlayUiHost get() {
        return provideInstance(this.contextProvider, this.touchOutsideProvider);
    }
}
