// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shortcut;

import com.android.systemui.recents.Recents;
import com.android.systemui.stackdivider.Divider;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ShortcutKeyDispatcher_Factory implements Factory<ShortcutKeyDispatcher>
{
    private final Provider<Context> contextProvider;
    private final Provider<Divider> dividerProvider;
    private final Provider<Recents> recentsProvider;
    
    public ShortcutKeyDispatcher_Factory(final Provider<Context> contextProvider, final Provider<Divider> dividerProvider, final Provider<Recents> recentsProvider) {
        this.contextProvider = contextProvider;
        this.dividerProvider = dividerProvider;
        this.recentsProvider = recentsProvider;
    }
    
    public static ShortcutKeyDispatcher_Factory create(final Provider<Context> provider, final Provider<Divider> provider2, final Provider<Recents> provider3) {
        return new ShortcutKeyDispatcher_Factory(provider, provider2, provider3);
    }
    
    public static ShortcutKeyDispatcher provideInstance(final Provider<Context> provider, final Provider<Divider> provider2, final Provider<Recents> provider3) {
        return new ShortcutKeyDispatcher(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public ShortcutKeyDispatcher get() {
        return provideInstance(this.contextProvider, this.dividerProvider, this.recentsProvider);
    }
}
