// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import com.android.internal.app.AssistUtils;
import dagger.internal.Factory;

public final class AssistModule_ProvideAssistUtilsFactory implements Factory<AssistUtils>
{
    private final Provider<Context> contextProvider;
    
    public AssistModule_ProvideAssistUtilsFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static AssistModule_ProvideAssistUtilsFactory create(final Provider<Context> provider) {
        return new AssistModule_ProvideAssistUtilsFactory(provider);
    }
    
    public static AssistUtils provideInstance(final Provider<Context> provider) {
        return proxyProvideAssistUtils(provider.get());
    }
    
    public static AssistUtils proxyProvideAssistUtils(final Context context) {
        final AssistUtils provideAssistUtils = AssistModule.provideAssistUtils(context);
        Preconditions.checkNotNull(provideAssistUtils, "Cannot return null from a non-@Nullable @Provides method");
        return provideAssistUtils;
    }
    
    @Override
    public AssistUtils get() {
        return provideInstance(this.contextProvider);
    }
}
