// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.google.android.systemui.assist.AssistManagerGoogle;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AssistInvocationEffect_Factory implements Factory<AssistInvocationEffect>
{
    private final Provider<AssistManagerGoogle> assistManagerGoogleProvider;
    private final Provider<OpaHomeButton> opaHomeButtonProvider;
    private final Provider<OpaLockscreen> opaLockscreenProvider;
    
    public AssistInvocationEffect_Factory(final Provider<AssistManagerGoogle> assistManagerGoogleProvider, final Provider<OpaHomeButton> opaHomeButtonProvider, final Provider<OpaLockscreen> opaLockscreenProvider) {
        this.assistManagerGoogleProvider = assistManagerGoogleProvider;
        this.opaHomeButtonProvider = opaHomeButtonProvider;
        this.opaLockscreenProvider = opaLockscreenProvider;
    }
    
    public static AssistInvocationEffect_Factory create(final Provider<AssistManagerGoogle> provider, final Provider<OpaHomeButton> provider2, final Provider<OpaLockscreen> provider3) {
        return new AssistInvocationEffect_Factory(provider, provider2, provider3);
    }
    
    public static AssistInvocationEffect provideInstance(final Provider<AssistManagerGoogle> provider, final Provider<OpaHomeButton> provider2, final Provider<OpaLockscreen> provider3) {
        return new AssistInvocationEffect(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public AssistInvocationEffect get() {
        return provideInstance(this.assistManagerGoogleProvider, this.opaHomeButtonProvider, this.opaLockscreenProvider);
    }
}
