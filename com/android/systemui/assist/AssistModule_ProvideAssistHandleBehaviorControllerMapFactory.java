// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import dagger.internal.Preconditions;
import javax.inject.Provider;
import java.util.Map;
import dagger.internal.Factory;

public final class AssistModule_ProvideAssistHandleBehaviorControllerMapFactory implements Factory<Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>>
{
    private final Provider<AssistHandleLikeHomeBehavior> likeHomeBehaviorProvider;
    private final Provider<AssistHandleOffBehavior> offBehaviorProvider;
    private final Provider<AssistHandleReminderExpBehavior> reminderExpBehaviorProvider;
    
    public AssistModule_ProvideAssistHandleBehaviorControllerMapFactory(final Provider<AssistHandleOffBehavior> offBehaviorProvider, final Provider<AssistHandleLikeHomeBehavior> likeHomeBehaviorProvider, final Provider<AssistHandleReminderExpBehavior> reminderExpBehaviorProvider) {
        this.offBehaviorProvider = offBehaviorProvider;
        this.likeHomeBehaviorProvider = likeHomeBehaviorProvider;
        this.reminderExpBehaviorProvider = reminderExpBehaviorProvider;
    }
    
    public static AssistModule_ProvideAssistHandleBehaviorControllerMapFactory create(final Provider<AssistHandleOffBehavior> provider, final Provider<AssistHandleLikeHomeBehavior> provider2, final Provider<AssistHandleReminderExpBehavior> provider3) {
        return new AssistModule_ProvideAssistHandleBehaviorControllerMapFactory(provider, provider2, provider3);
    }
    
    public static Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController> provideInstance(final Provider<AssistHandleOffBehavior> provider, final Provider<AssistHandleLikeHomeBehavior> provider2, final Provider<AssistHandleReminderExpBehavior> provider3) {
        return proxyProvideAssistHandleBehaviorControllerMap(provider.get(), provider2.get(), provider3.get());
    }
    
    public static Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController> proxyProvideAssistHandleBehaviorControllerMap(final Object o, final Object o2, final Object o3) {
        final Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController> provideAssistHandleBehaviorControllerMap = AssistModule.provideAssistHandleBehaviorControllerMap((AssistHandleOffBehavior)o, (AssistHandleLikeHomeBehavior)o2, (AssistHandleReminderExpBehavior)o3);
        Preconditions.checkNotNull(provideAssistHandleBehaviorControllerMap, "Cannot return null from a non-@Nullable @Provides method");
        return provideAssistHandleBehaviorControllerMap;
    }
    
    @Override
    public Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController> get() {
        return provideInstance(this.offBehaviorProvider, this.likeHomeBehaviorProvider, this.reminderExpBehaviorProvider);
    }
}
