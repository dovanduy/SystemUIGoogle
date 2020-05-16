// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifPromoter;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;

public final class ConversationCoordinator implements Coordinator
{
    private final ConversationCoordinator$notificationPromoter.ConversationCoordinator$notificationPromoter$1 notificationPromoter;
    
    public ConversationCoordinator() {
        this.notificationPromoter = new ConversationCoordinator$notificationPromoter.ConversationCoordinator$notificationPromoter$1("ConversationCoordinator");
    }
    
    @Override
    public void attach(final NotifPipeline notifPipeline) {
        Intrinsics.checkParameterIsNotNull(notifPipeline, "pipeline");
        notifPipeline.addPromoter((NotifPromoter)this.notificationPromoter);
    }
}
