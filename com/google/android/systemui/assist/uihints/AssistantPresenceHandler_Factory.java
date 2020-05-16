// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import com.android.internal.app.AssistUtils;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AssistantPresenceHandler_Factory implements Factory<AssistantPresenceHandler>
{
    private final Provider<AssistUtils> assistUtilsProvider;
    
    public AssistantPresenceHandler_Factory(final Provider<AssistUtils> assistUtilsProvider) {
        this.assistUtilsProvider = assistUtilsProvider;
    }
    
    public static AssistantPresenceHandler_Factory create(final Provider<AssistUtils> provider) {
        return new AssistantPresenceHandler_Factory(provider);
    }
    
    public static AssistantPresenceHandler provideInstance(final Provider<AssistUtils> provider) {
        return new AssistantPresenceHandler(provider.get());
    }
    
    @Override
    public AssistantPresenceHandler get() {
        return provideInstance(this.assistUtilsProvider);
    }
}
