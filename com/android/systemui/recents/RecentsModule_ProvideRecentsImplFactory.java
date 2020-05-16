// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import dagger.internal.Preconditions;
import android.content.Context;
import com.android.systemui.dagger.ContextComponentHelper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class RecentsModule_ProvideRecentsImplFactory implements Factory<RecentsImplementation>
{
    private final Provider<ContextComponentHelper> componentHelperProvider;
    private final Provider<Context> contextProvider;
    
    public RecentsModule_ProvideRecentsImplFactory(final Provider<Context> contextProvider, final Provider<ContextComponentHelper> componentHelperProvider) {
        this.contextProvider = contextProvider;
        this.componentHelperProvider = componentHelperProvider;
    }
    
    public static RecentsModule_ProvideRecentsImplFactory create(final Provider<Context> provider, final Provider<ContextComponentHelper> provider2) {
        return new RecentsModule_ProvideRecentsImplFactory(provider, provider2);
    }
    
    public static RecentsImplementation provideInstance(final Provider<Context> provider, final Provider<ContextComponentHelper> provider2) {
        return proxyProvideRecentsImpl(provider.get(), provider2.get());
    }
    
    public static RecentsImplementation proxyProvideRecentsImpl(final Context context, final ContextComponentHelper contextComponentHelper) {
        final RecentsImplementation provideRecentsImpl = RecentsModule.provideRecentsImpl(context, contextComponentHelper);
        Preconditions.checkNotNull(provideRecentsImpl, "Cannot return null from a non-@Nullable @Provides method");
        return provideRecentsImpl;
    }
    
    @Override
    public RecentsImplementation get() {
        return provideInstance(this.contextProvider, this.componentHelperProvider);
    }
}
