// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.recents.RecentsImplementation;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import com.android.systemui.recents.Recents;
import dagger.internal.Factory;

public final class SystemUIDefaultModule_ProvideRecentsFactory implements Factory<Recents>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<RecentsImplementation> recentsImplementationProvider;
    
    public SystemUIDefaultModule_ProvideRecentsFactory(final Provider<Context> contextProvider, final Provider<RecentsImplementation> recentsImplementationProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.contextProvider = contextProvider;
        this.recentsImplementationProvider = recentsImplementationProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static SystemUIDefaultModule_ProvideRecentsFactory create(final Provider<Context> provider, final Provider<RecentsImplementation> provider2, final Provider<CommandQueue> provider3) {
        return new SystemUIDefaultModule_ProvideRecentsFactory(provider, provider2, provider3);
    }
    
    public static Recents provideInstance(final Provider<Context> provider, final Provider<RecentsImplementation> provider2, final Provider<CommandQueue> provider3) {
        return proxyProvideRecents(provider.get(), provider2.get(), provider3.get());
    }
    
    public static Recents proxyProvideRecents(final Context context, final RecentsImplementation recentsImplementation, final CommandQueue commandQueue) {
        final Recents provideRecents = SystemUIDefaultModule.provideRecents(context, recentsImplementation, commandQueue);
        Preconditions.checkNotNull(provideRecents, "Cannot return null from a non-@Nullable @Provides method");
        return provideRecents;
    }
    
    @Override
    public Recents get() {
        return provideInstance(this.contextProvider, this.recentsImplementationProvider, this.commandQueueProvider);
    }
}
