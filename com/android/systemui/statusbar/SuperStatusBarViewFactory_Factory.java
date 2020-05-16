// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.row.dagger.NotificationRowComponent;
import com.android.systemui.statusbar.phone.LockscreenLockIconController;
import com.android.systemui.util.InjectionInflationController;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SuperStatusBarViewFactory_Factory implements Factory<SuperStatusBarViewFactory>
{
    private final Provider<Context> contextProvider;
    private final Provider<InjectionInflationController> injectionInflationControllerProvider;
    private final Provider<LockscreenLockIconController> lockIconControllerProvider;
    private final Provider<NotificationRowComponent.Builder> notificationRowComponentBuilderProvider;
    
    public SuperStatusBarViewFactory_Factory(final Provider<Context> contextProvider, final Provider<InjectionInflationController> injectionInflationControllerProvider, final Provider<NotificationRowComponent.Builder> notificationRowComponentBuilderProvider, final Provider<LockscreenLockIconController> lockIconControllerProvider) {
        this.contextProvider = contextProvider;
        this.injectionInflationControllerProvider = injectionInflationControllerProvider;
        this.notificationRowComponentBuilderProvider = notificationRowComponentBuilderProvider;
        this.lockIconControllerProvider = lockIconControllerProvider;
    }
    
    public static SuperStatusBarViewFactory_Factory create(final Provider<Context> provider, final Provider<InjectionInflationController> provider2, final Provider<NotificationRowComponent.Builder> provider3, final Provider<LockscreenLockIconController> provider4) {
        return new SuperStatusBarViewFactory_Factory(provider, provider2, provider3, provider4);
    }
    
    public static SuperStatusBarViewFactory provideInstance(final Provider<Context> provider, final Provider<InjectionInflationController> provider2, final Provider<NotificationRowComponent.Builder> provider3, final Provider<LockscreenLockIconController> provider4) {
        return new SuperStatusBarViewFactory(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public SuperStatusBarViewFactory get() {
        return provideInstance(this.contextProvider, this.injectionInflationControllerProvider, this.notificationRowComponentBuilderProvider, this.lockIconControllerProvider);
    }
}
