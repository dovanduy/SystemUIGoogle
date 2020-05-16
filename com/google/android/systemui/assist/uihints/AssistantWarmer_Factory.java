// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AssistantWarmer_Factory implements Factory<AssistantWarmer>
{
    private final Provider<Context> contextProvider;
    
    public AssistantWarmer_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static AssistantWarmer_Factory create(final Provider<Context> provider) {
        return new AssistantWarmer_Factory(provider);
    }
    
    public static AssistantWarmer provideInstance(final Provider<Context> provider) {
        return new AssistantWarmer(provider.get());
    }
    
    @Override
    public AssistantWarmer get() {
        return provideInstance(this.contextProvider);
    }
}
