// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.feedback;

import com.android.systemui.assist.AssistManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AssistInvocationEffect_Factory implements Factory<AssistInvocationEffect>
{
    private final Provider<AssistManager> assistManagerProvider;
    
    public AssistInvocationEffect_Factory(final Provider<AssistManager> assistManagerProvider) {
        this.assistManagerProvider = assistManagerProvider;
    }
    
    public static AssistInvocationEffect_Factory create(final Provider<AssistManager> provider) {
        return new AssistInvocationEffect_Factory(provider);
    }
    
    public static AssistInvocationEffect provideInstance(final Provider<AssistManager> provider) {
        return new AssistInvocationEffect(provider.get());
    }
    
    @Override
    public AssistInvocationEffect get() {
        return provideInstance(this.assistManagerProvider);
    }
}
