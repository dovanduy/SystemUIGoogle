// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.systemui.bubbles.BubbleController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BubbleCoordinator_Factory implements Factory<BubbleCoordinator>
{
    private final Provider<BubbleController> bubbleControllerProvider;
    private final Provider<NotifCollection> notifCollectionProvider;
    
    public BubbleCoordinator_Factory(final Provider<BubbleController> bubbleControllerProvider, final Provider<NotifCollection> notifCollectionProvider) {
        this.bubbleControllerProvider = bubbleControllerProvider;
        this.notifCollectionProvider = notifCollectionProvider;
    }
    
    public static BubbleCoordinator_Factory create(final Provider<BubbleController> provider, final Provider<NotifCollection> provider2) {
        return new BubbleCoordinator_Factory(provider, provider2);
    }
    
    public static BubbleCoordinator provideInstance(final Provider<BubbleController> provider, final Provider<NotifCollection> provider2) {
        return new BubbleCoordinator(provider.get(), provider2.get());
    }
    
    @Override
    public BubbleCoordinator get() {
        return provideInstance(this.bubbleControllerProvider, this.notifCollectionProvider);
    }
}
