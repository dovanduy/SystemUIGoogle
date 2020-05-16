// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.ConversationNotificationProcessor;
import java.util.concurrent.Executor;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationContentInflater_Factory implements Factory<NotificationContentInflater>
{
    private final Provider<Executor> bgExecutorProvider;
    private final Provider<ConversationNotificationProcessor> conversationProcessorProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    private final Provider<NotifRemoteViewCache> remoteViewCacheProvider;
    private final Provider<SmartReplyConstants> smartReplyConstantsProvider;
    private final Provider<SmartReplyController> smartReplyControllerProvider;
    
    public NotificationContentInflater_Factory(final Provider<NotifRemoteViewCache> remoteViewCacheProvider, final Provider<NotificationRemoteInputManager> remoteInputManagerProvider, final Provider<SmartReplyConstants> smartReplyConstantsProvider, final Provider<SmartReplyController> smartReplyControllerProvider, final Provider<ConversationNotificationProcessor> conversationProcessorProvider, final Provider<Executor> bgExecutorProvider) {
        this.remoteViewCacheProvider = remoteViewCacheProvider;
        this.remoteInputManagerProvider = remoteInputManagerProvider;
        this.smartReplyConstantsProvider = smartReplyConstantsProvider;
        this.smartReplyControllerProvider = smartReplyControllerProvider;
        this.conversationProcessorProvider = conversationProcessorProvider;
        this.bgExecutorProvider = bgExecutorProvider;
    }
    
    public static NotificationContentInflater_Factory create(final Provider<NotifRemoteViewCache> provider, final Provider<NotificationRemoteInputManager> provider2, final Provider<SmartReplyConstants> provider3, final Provider<SmartReplyController> provider4, final Provider<ConversationNotificationProcessor> provider5, final Provider<Executor> provider6) {
        return new NotificationContentInflater_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static NotificationContentInflater provideInstance(final Provider<NotifRemoteViewCache> provider, final Provider<NotificationRemoteInputManager> provider2, final Provider<SmartReplyConstants> provider3, final Provider<SmartReplyController> provider4, final Provider<ConversationNotificationProcessor> provider5, final Provider<Executor> provider6) {
        return new NotificationContentInflater(provider.get(), provider2.get(), DoubleCheck.lazy(provider3), DoubleCheck.lazy(provider4), provider5.get(), provider6.get());
    }
    
    @Override
    public NotificationContentInflater get() {
        return provideInstance(this.remoteViewCacheProvider, this.remoteInputManagerProvider, this.smartReplyConstantsProvider, this.smartReplyControllerProvider, this.conversationProcessorProvider, this.bgExecutorProvider);
    }
}
