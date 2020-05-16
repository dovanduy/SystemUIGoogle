// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui;

import com.google.android.systemui.screenshot.ScreenshotNotificationSmartActionsProviderGoogle;
import com.android.systemui.screenshot.ScreenshotNotificationSmartActionsProvider;
import android.os.Handler;
import java.util.concurrent.Executor;
import com.android.systemui.dagger.DependencyProvider;
import com.google.android.systemui.dagger.DaggerSystemUIGoogleRootComponent;
import com.android.systemui.dagger.SystemUIRootComponent;
import android.content.Context;
import com.android.systemui.SystemUIFactory;

public class SystemUIGoogleFactory extends SystemUIFactory
{
    @Override
    protected SystemUIRootComponent buildSystemUIRootComponent(final Context context) {
        final DaggerSystemUIGoogleRootComponent.Builder builder = DaggerSystemUIGoogleRootComponent.builder();
        builder.dependencyProvider(new DependencyProvider());
        builder.contextHolder(new ContextHolder(context));
        return builder.build();
    }
    
    @Override
    public ScreenshotNotificationSmartActionsProvider createScreenshotNotificationSmartActionsProvider(final Context context, final Executor executor, final Handler handler) {
        return new ScreenshotNotificationSmartActionsProviderGoogle(context, executor, handler);
    }
}
