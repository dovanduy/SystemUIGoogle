// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.screenshot.ScreenshotNotificationSmartActionsProvider;
import java.util.concurrent.Executor;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.phone.StatusBar;
import android.os.Handler;
import android.os.Looper;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.phone.KeyguardBouncer;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import android.view.ViewGroup;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.dagger.DependencyProvider;
import com.android.systemui.dagger.DaggerSystemUIRootComponent;
import android.util.Log;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.dagger.SystemUIRootComponent;

public class SystemUIFactory
{
    static SystemUIFactory mFactory;
    private SystemUIRootComponent mRootComponent;
    
    @VisibleForTesting
    static void cleanup() {
        SystemUIFactory.mFactory = null;
    }
    
    public static void createFromConfig(final Context context) {
        if (SystemUIFactory.mFactory != null) {
            return;
        }
        final String string = context.getString(R$string.config_systemUIFactoryComponent);
        if (string != null && string.length() != 0) {
            try {
                (SystemUIFactory.mFactory = (SystemUIFactory)context.getClassLoader().loadClass(string).newInstance()).init(context);
                return;
            }
            finally {
                final StringBuilder sb = new StringBuilder();
                sb.append("Error creating SystemUIFactory component: ");
                sb.append(string);
                final Throwable cause;
                Log.w("SystemUIFactory", sb.toString(), cause);
                throw new RuntimeException(cause);
            }
        }
        throw new RuntimeException("No SystemUIFactory component configured");
    }
    
    public static <T extends SystemUIFactory> T getInstance() {
        return (T)SystemUIFactory.mFactory;
    }
    
    private void init(final Context context) {
        this.mRootComponent = this.buildSystemUIRootComponent(context);
        final Dependency dependency = new Dependency();
        this.mRootComponent.createDependency().createSystemUI(dependency);
        dependency.start();
    }
    
    protected SystemUIRootComponent buildSystemUIRootComponent(final Context context) {
        final DaggerSystemUIRootComponent.Builder builder = DaggerSystemUIRootComponent.builder();
        builder.dependencyProvider(new DependencyProvider());
        builder.contextHolder(new ContextHolder(context));
        return builder.build();
    }
    
    public KeyguardBouncer createKeyguardBouncer(final Context context, final ViewMediatorCallback viewMediatorCallback, final LockPatternUtils lockPatternUtils, final ViewGroup viewGroup, final DismissCallbackRegistry dismissCallbackRegistry, final KeyguardBouncer.BouncerExpansionCallback bouncerExpansionCallback, final KeyguardStateController keyguardStateController, final FalsingManager falsingManager, final KeyguardBypassController keyguardBypassController) {
        return new KeyguardBouncer(context, viewMediatorCallback, lockPatternUtils, viewGroup, dismissCallbackRegistry, falsingManager, bouncerExpansionCallback, keyguardStateController, Dependency.get(KeyguardUpdateMonitor.class), keyguardBypassController, new Handler(Looper.getMainLooper()));
    }
    
    public NotificationIconAreaController createNotificationIconAreaController(final Context context, final StatusBar statusBar, final NotificationWakeUpCoordinator notificationWakeUpCoordinator, final KeyguardBypassController keyguardBypassController, final StatusBarStateController statusBarStateController) {
        return new NotificationIconAreaController(context, statusBar, statusBarStateController, notificationWakeUpCoordinator, keyguardBypassController, Dependency.get(NotificationMediaManager.class), Dependency.get(NotificationListener.class), Dependency.get(DozeParameters.class));
    }
    
    public ScreenshotNotificationSmartActionsProvider createScreenshotNotificationSmartActionsProvider(final Context context, final Executor executor, final Handler handler) {
        return new ScreenshotNotificationSmartActionsProvider();
    }
    
    public SystemUIRootComponent getRootComponent() {
        return this.mRootComponent;
    }
    
    public static class ContextHolder
    {
        private Context mContext;
        
        public ContextHolder(final Context mContext) {
            this.mContext = mContext;
        }
        
        public Context provideContext() {
            return this.mContext;
        }
    }
}
