// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import dagger.internal.Preconditions;
import android.os.Handler;
import dagger.internal.Factory;

public final class AssistModule_ProvideBackgroundHandlerFactory implements Factory<Handler>
{
    private static final AssistModule_ProvideBackgroundHandlerFactory INSTANCE;
    
    static {
        INSTANCE = new AssistModule_ProvideBackgroundHandlerFactory();
    }
    
    public static AssistModule_ProvideBackgroundHandlerFactory create() {
        return AssistModule_ProvideBackgroundHandlerFactory.INSTANCE;
    }
    
    public static Handler provideInstance() {
        return proxyProvideBackgroundHandler();
    }
    
    public static Handler proxyProvideBackgroundHandler() {
        final Handler provideBackgroundHandler = AssistModule.provideBackgroundHandler();
        Preconditions.checkNotNull(provideBackgroundHandler, "Cannot return null from a non-@Nullable @Provides method");
        return provideBackgroundHandler;
    }
    
    @Override
    public Handler get() {
        return provideInstance();
    }
}
