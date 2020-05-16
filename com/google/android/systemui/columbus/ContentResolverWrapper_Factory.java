// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ContentResolverWrapper_Factory implements Factory<ContentResolverWrapper>
{
    private final Provider<Context> contextProvider;
    
    public ContentResolverWrapper_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static ContentResolverWrapper_Factory create(final Provider<Context> provider) {
        return new ContentResolverWrapper_Factory(provider);
    }
    
    public static ContentResolverWrapper provideInstance(final Provider<Context> provider) {
        return new ContentResolverWrapper(provider.get());
    }
    
    @Override
    public ContentResolverWrapper get() {
        return provideInstance(this.contextProvider);
    }
}
