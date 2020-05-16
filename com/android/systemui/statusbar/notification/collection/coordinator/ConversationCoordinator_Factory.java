// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import dagger.internal.Factory;

public final class ConversationCoordinator_Factory implements Factory<ConversationCoordinator>
{
    private static final ConversationCoordinator_Factory INSTANCE;
    
    static {
        INSTANCE = new ConversationCoordinator_Factory();
    }
    
    public static ConversationCoordinator_Factory create() {
        return ConversationCoordinator_Factory.INSTANCE;
    }
    
    public static ConversationCoordinator provideInstance() {
        return new ConversationCoordinator();
    }
    
    @Override
    public ConversationCoordinator get() {
        return provideInstance();
    }
}
