// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import dagger.internal.DoubleCheck;
import android.view.WindowManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.assist.AssistManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ShadeControllerImpl_Factory implements Factory<ShadeControllerImpl>
{
    private final Provider<AssistManager> assistManagerLazyProvider;
    private final Provider<BubbleController> bubbleControllerLazyProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider;
    private final Provider<StatusBar> statusBarLazyProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<WindowManager> windowManagerProvider;
    
    public ShadeControllerImpl_Factory(final Provider<CommandQueue> commandQueueProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider, final Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider, final Provider<WindowManager> windowManagerProvider, final Provider<StatusBar> statusBarLazyProvider, final Provider<AssistManager> assistManagerLazyProvider, final Provider<BubbleController> bubbleControllerLazyProvider) {
        this.commandQueueProvider = commandQueueProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.notificationShadeWindowControllerProvider = notificationShadeWindowControllerProvider;
        this.statusBarKeyguardViewManagerProvider = statusBarKeyguardViewManagerProvider;
        this.windowManagerProvider = windowManagerProvider;
        this.statusBarLazyProvider = statusBarLazyProvider;
        this.assistManagerLazyProvider = assistManagerLazyProvider;
        this.bubbleControllerLazyProvider = bubbleControllerLazyProvider;
    }
    
    public static ShadeControllerImpl_Factory create(final Provider<CommandQueue> provider, final Provider<StatusBarStateController> provider2, final Provider<NotificationShadeWindowController> provider3, final Provider<StatusBarKeyguardViewManager> provider4, final Provider<WindowManager> provider5, final Provider<StatusBar> provider6, final Provider<AssistManager> provider7, final Provider<BubbleController> provider8) {
        return new ShadeControllerImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }
    
    public static ShadeControllerImpl provideInstance(final Provider<CommandQueue> provider, final Provider<StatusBarStateController> provider2, final Provider<NotificationShadeWindowController> provider3, final Provider<StatusBarKeyguardViewManager> provider4, final Provider<WindowManager> provider5, final Provider<StatusBar> provider6, final Provider<AssistManager> provider7, final Provider<BubbleController> provider8) {
        return new ShadeControllerImpl(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), DoubleCheck.lazy(provider6), DoubleCheck.lazy(provider7), DoubleCheck.lazy(provider8));
    }
    
    @Override
    public ShadeControllerImpl get() {
        return provideInstance(this.commandQueueProvider, this.statusBarStateControllerProvider, this.notificationShadeWindowControllerProvider, this.statusBarKeyguardViewManagerProvider, this.windowManagerProvider, this.statusBarLazyProvider, this.assistManagerLazyProvider, this.bubbleControllerLazyProvider);
    }
}
