// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import android.os.Handler;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ManageMedia_Factory implements Factory<ManageMedia>
{
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;
    
    public ManageMedia_Factory(final Provider<Context> contextProvider, final Provider<Handler> handlerProvider) {
        this.contextProvider = contextProvider;
        this.handlerProvider = handlerProvider;
    }
    
    public static ManageMedia_Factory create(final Provider<Context> provider, final Provider<Handler> provider2) {
        return new ManageMedia_Factory(provider, provider2);
    }
    
    public static ManageMedia provideInstance(final Provider<Context> provider, final Provider<Handler> provider2) {
        return new ManageMedia(provider.get(), provider2.get());
    }
    
    @Override
    public ManageMedia get() {
        return provideInstance(this.contextProvider, this.handlerProvider);
    }
}
