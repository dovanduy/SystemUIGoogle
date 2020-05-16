// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.model.SysUiState;
import dagger.internal.Factory;

public final class SystemUIModule_ProvideSysUiStateFactory implements Factory<SysUiState>
{
    private static final SystemUIModule_ProvideSysUiStateFactory INSTANCE;
    
    static {
        INSTANCE = new SystemUIModule_ProvideSysUiStateFactory();
    }
    
    public static SystemUIModule_ProvideSysUiStateFactory create() {
        return SystemUIModule_ProvideSysUiStateFactory.INSTANCE;
    }
    
    public static SysUiState provideInstance() {
        return proxyProvideSysUiState();
    }
    
    public static SysUiState proxyProvideSysUiState() {
        final SysUiState provideSysUiState = SystemUIModule.provideSysUiState();
        Preconditions.checkNotNull(provideSysUiState, "Cannot return null from a non-@Nullable @Provides method");
        return provideSysUiState;
    }
    
    @Override
    public SysUiState get() {
        return provideInstance();
    }
}
