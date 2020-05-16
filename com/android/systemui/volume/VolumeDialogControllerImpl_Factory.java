// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Optional;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class VolumeDialogControllerImpl_Factory implements Factory<VolumeDialogControllerImpl>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Optional<Lazy<StatusBar>>> statusBarOptionalLazyProvider;
    
    public VolumeDialogControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<Optional<Lazy<StatusBar>>> statusBarOptionalLazyProvider) {
        this.contextProvider = contextProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.statusBarOptionalLazyProvider = statusBarOptionalLazyProvider;
    }
    
    public static VolumeDialogControllerImpl_Factory create(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<Optional<Lazy<StatusBar>>> provider3) {
        return new VolumeDialogControllerImpl_Factory(provider, provider2, provider3);
    }
    
    public static VolumeDialogControllerImpl provideInstance(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<Optional<Lazy<StatusBar>>> provider3) {
        return new VolumeDialogControllerImpl(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public VolumeDialogControllerImpl get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider, this.statusBarOptionalLazyProvider);
    }
}
