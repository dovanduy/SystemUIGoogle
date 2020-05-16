// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import androidx.slice.Clock;
import android.os.HandlerThread;
import android.os.Handler;
import com.android.internal.app.AssistUtils;
import android.content.Context;
import com.android.systemui.statusbar.NavigationBarController;
import java.util.EnumMap;
import java.util.Map;

public abstract class AssistModule
{
    static Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController> provideAssistHandleBehaviorControllerMap(final AssistHandleOffBehavior assistHandleOffBehavior, final AssistHandleLikeHomeBehavior assistHandleLikeHomeBehavior, final AssistHandleReminderExpBehavior assistHandleReminderExpBehavior) {
        final EnumMap<AssistHandleBehavior, AssistHandleReminderExpBehavior> enumMap = (EnumMap<AssistHandleBehavior, AssistHandleReminderExpBehavior>)new EnumMap<AssistHandleBehavior, AssistHandleLikeHomeBehavior>(AssistHandleBehavior.class);
        enumMap.put(AssistHandleBehavior.OFF, (AssistHandleLikeHomeBehavior)assistHandleOffBehavior);
        enumMap.put(AssistHandleBehavior.LIKE_HOME, assistHandleLikeHomeBehavior);
        enumMap.put(AssistHandleBehavior.REMINDER_EXP, assistHandleReminderExpBehavior);
        return (Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>)enumMap;
    }
    
    static AssistHandleViewController provideAssistHandleViewController(final NavigationBarController navigationBarController) {
        return navigationBarController.getAssistHandlerViewController();
    }
    
    static AssistUtils provideAssistUtils(final Context context) {
        return new AssistUtils(context);
    }
    
    static Handler provideBackgroundHandler() {
        final HandlerThread handlerThread = new HandlerThread("AssistHandleThread");
        handlerThread.start();
        return handlerThread.getThreadHandler();
    }
    
    static Clock provideSystemClock() {
        return (Clock)_$$Lambda$WyKlJnsW9STKD48w13qf39m_FKI.INSTANCE;
    }
}
