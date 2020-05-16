// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import dagger.internal.Preconditions;
import com.android.systemui.TransactionPool;
import com.android.systemui.wm.SystemWindows;
import com.android.systemui.recents.Recents;
import dagger.Lazy;
import java.util.Optional;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.wm.DisplayImeController;
import android.os.Handler;
import com.android.systemui.wm.DisplayController;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DividerModule_ProvideDividerFactory implements Factory<Divider>
{
    private final Provider<Context> contextProvider;
    private final Provider<DisplayController> displayControllerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<DisplayImeController> imeControllerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<Optional<Lazy<Recents>>> recentsOptionalLazyProvider;
    private final Provider<SystemWindows> systemWindowsProvider;
    private final Provider<TransactionPool> transactionPoolProvider;
    
    public DividerModule_ProvideDividerFactory(final Provider<Context> contextProvider, final Provider<Optional<Lazy<Recents>>> recentsOptionalLazyProvider, final Provider<DisplayController> displayControllerProvider, final Provider<SystemWindows> systemWindowsProvider, final Provider<DisplayImeController> imeControllerProvider, final Provider<Handler> handlerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<TransactionPool> transactionPoolProvider) {
        this.contextProvider = contextProvider;
        this.recentsOptionalLazyProvider = recentsOptionalLazyProvider;
        this.displayControllerProvider = displayControllerProvider;
        this.systemWindowsProvider = systemWindowsProvider;
        this.imeControllerProvider = imeControllerProvider;
        this.handlerProvider = handlerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.transactionPoolProvider = transactionPoolProvider;
    }
    
    public static DividerModule_ProvideDividerFactory create(final Provider<Context> provider, final Provider<Optional<Lazy<Recents>>> provider2, final Provider<DisplayController> provider3, final Provider<SystemWindows> provider4, final Provider<DisplayImeController> provider5, final Provider<Handler> provider6, final Provider<KeyguardStateController> provider7, final Provider<TransactionPool> provider8) {
        return new DividerModule_ProvideDividerFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }
    
    public static Divider provideInstance(final Provider<Context> provider, final Provider<Optional<Lazy<Recents>>> provider2, final Provider<DisplayController> provider3, final Provider<SystemWindows> provider4, final Provider<DisplayImeController> provider5, final Provider<Handler> provider6, final Provider<KeyguardStateController> provider7, final Provider<TransactionPool> provider8) {
        return proxyProvideDivider(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get());
    }
    
    public static Divider proxyProvideDivider(final Context context, final Optional<Lazy<Recents>> optional, final DisplayController displayController, final SystemWindows systemWindows, final DisplayImeController displayImeController, final Handler handler, final KeyguardStateController keyguardStateController, final TransactionPool transactionPool) {
        final Divider provideDivider = DividerModule.provideDivider(context, optional, displayController, systemWindows, displayImeController, handler, keyguardStateController, transactionPool);
        Preconditions.checkNotNull(provideDivider, "Cannot return null from a non-@Nullable @Provides method");
        return provideDivider;
    }
    
    @Override
    public Divider get() {
        return provideInstance(this.contextProvider, this.recentsOptionalLazyProvider, this.displayControllerProvider, this.systemWindowsProvider, this.imeControllerProvider, this.handlerProvider, this.keyguardStateControllerProvider, this.transactionPoolProvider);
    }
}
