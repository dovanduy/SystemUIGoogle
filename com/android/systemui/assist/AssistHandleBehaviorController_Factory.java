// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.phone.NavigationModeController;
import android.os.Handler;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import java.util.Map;
import com.android.internal.app.AssistUtils;
import android.view.accessibility.AccessibilityManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AssistHandleBehaviorController_Factory implements Factory<AssistHandleBehaviorController>
{
    private final Provider<AccessibilityManager> a11yManagerProvider;
    private final Provider<AssistHandleViewController> assistHandleViewControllerProvider;
    private final Provider<AssistUtils> assistUtilsProvider;
    private final Provider<Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>> behaviorMapProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;
    
    public AssistHandleBehaviorController_Factory(final Provider<Context> contextProvider, final Provider<AssistUtils> assistUtilsProvider, final Provider<Handler> handlerProvider, final Provider<AssistHandleViewController> assistHandleViewControllerProvider, final Provider<DeviceConfigHelper> deviceConfigHelperProvider, final Provider<Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>> behaviorMapProvider, final Provider<NavigationModeController> navigationModeControllerProvider, final Provider<AccessibilityManager> a11yManagerProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.contextProvider = contextProvider;
        this.assistUtilsProvider = assistUtilsProvider;
        this.handlerProvider = handlerProvider;
        this.assistHandleViewControllerProvider = assistHandleViewControllerProvider;
        this.deviceConfigHelperProvider = deviceConfigHelperProvider;
        this.behaviorMapProvider = behaviorMapProvider;
        this.navigationModeControllerProvider = navigationModeControllerProvider;
        this.a11yManagerProvider = a11yManagerProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static AssistHandleBehaviorController_Factory create(final Provider<Context> provider, final Provider<AssistUtils> provider2, final Provider<Handler> provider3, final Provider<AssistHandleViewController> provider4, final Provider<DeviceConfigHelper> provider5, final Provider<Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>> provider6, final Provider<NavigationModeController> provider7, final Provider<AccessibilityManager> provider8, final Provider<DumpManager> provider9) {
        return new AssistHandleBehaviorController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
    }
    
    public static AssistHandleBehaviorController provideInstance(final Provider<Context> provider, final Provider<AssistUtils> provider2, final Provider<Handler> provider3, final Provider<AssistHandleViewController> provider4, final Provider<DeviceConfigHelper> provider5, final Provider<Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>> provider6, final Provider<NavigationModeController> provider7, final Provider<AccessibilityManager> provider8, final Provider<DumpManager> provider9) {
        return new AssistHandleBehaviorController(provider.get(), provider2.get(), provider3.get(), provider4, provider5.get(), provider6.get(), provider7.get(), DoubleCheck.lazy(provider8), provider9.get());
    }
    
    @Override
    public AssistHandleBehaviorController get() {
        return provideInstance(this.contextProvider, this.assistUtilsProvider, this.handlerProvider, this.assistHandleViewControllerProvider, this.deviceConfigHelperProvider, this.behaviorMapProvider, this.navigationModeControllerProvider, this.a11yManagerProvider, this.dumpManagerProvider);
    }
}
