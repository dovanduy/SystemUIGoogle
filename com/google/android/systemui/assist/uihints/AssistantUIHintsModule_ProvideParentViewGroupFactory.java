// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.Preconditions;
import javax.inject.Provider;
import android.view.ViewGroup;
import dagger.internal.Factory;

public final class AssistantUIHintsModule_ProvideParentViewGroupFactory implements Factory<ViewGroup>
{
    private final Provider<OverlayUiHost> overlayUiHostProvider;
    
    public AssistantUIHintsModule_ProvideParentViewGroupFactory(final Provider<OverlayUiHost> overlayUiHostProvider) {
        this.overlayUiHostProvider = overlayUiHostProvider;
    }
    
    public static AssistantUIHintsModule_ProvideParentViewGroupFactory create(final Provider<OverlayUiHost> provider) {
        return new AssistantUIHintsModule_ProvideParentViewGroupFactory(provider);
    }
    
    public static ViewGroup provideInstance(final Provider<OverlayUiHost> provider) {
        return proxyProvideParentViewGroup(provider.get());
    }
    
    public static ViewGroup proxyProvideParentViewGroup(final Object o) {
        final ViewGroup provideParentViewGroup = AssistantUIHintsModule.provideParentViewGroup((OverlayUiHost)o);
        Preconditions.checkNotNull(provideParentViewGroup, "Cannot return null from a non-@Nullable @Provides method");
        return provideParentViewGroup;
    }
    
    @Override
    public ViewGroup get() {
        return provideInstance(this.overlayUiHostProvider);
    }
}
