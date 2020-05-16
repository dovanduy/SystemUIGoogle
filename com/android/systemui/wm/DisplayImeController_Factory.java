// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.wm;

import com.android.systemui.TransactionPool;
import android.os.Handler;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DisplayImeController_Factory implements Factory<DisplayImeController>
{
    private final Provider<DisplayController> displayControllerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<SystemWindows> syswinProvider;
    private final Provider<TransactionPool> transactionPoolProvider;
    
    public DisplayImeController_Factory(final Provider<SystemWindows> syswinProvider, final Provider<DisplayController> displayControllerProvider, final Provider<Handler> mainHandlerProvider, final Provider<TransactionPool> transactionPoolProvider) {
        this.syswinProvider = syswinProvider;
        this.displayControllerProvider = displayControllerProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.transactionPoolProvider = transactionPoolProvider;
    }
    
    public static DisplayImeController_Factory create(final Provider<SystemWindows> provider, final Provider<DisplayController> provider2, final Provider<Handler> provider3, final Provider<TransactionPool> provider4) {
        return new DisplayImeController_Factory(provider, provider2, provider3, provider4);
    }
    
    public static DisplayImeController provideInstance(final Provider<SystemWindows> provider, final Provider<DisplayController> provider2, final Provider<Handler> provider3, final Provider<TransactionPool> provider4) {
        return new DisplayImeController(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public DisplayImeController get() {
        return provideInstance(this.syswinProvider, this.displayControllerProvider, this.mainHandlerProvider, this.transactionPoolProvider);
    }
}
