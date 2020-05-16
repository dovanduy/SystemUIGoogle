// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import java.util.Optional;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyboardMonitor_Factory implements Factory<KeyboardMonitor>
{
    private final Provider<Optional<CommandQueue>> commandQueueOptionalProvider;
    private final Provider<Context> contextProvider;
    
    public KeyboardMonitor_Factory(final Provider<Context> contextProvider, final Provider<Optional<CommandQueue>> commandQueueOptionalProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueOptionalProvider = commandQueueOptionalProvider;
    }
    
    public static KeyboardMonitor_Factory create(final Provider<Context> provider, final Provider<Optional<CommandQueue>> provider2) {
        return new KeyboardMonitor_Factory(provider, provider2);
    }
    
    public static KeyboardMonitor provideInstance(final Provider<Context> provider, final Provider<Optional<CommandQueue>> provider2) {
        return new KeyboardMonitor(provider.get(), provider2.get());
    }
    
    @Override
    public KeyboardMonitor get() {
        return provideInstance(this.contextProvider, this.commandQueueOptionalProvider);
    }
}
