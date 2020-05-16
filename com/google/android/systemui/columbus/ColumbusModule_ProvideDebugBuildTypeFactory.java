// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.Factory;

public final class ColumbusModule_ProvideDebugBuildTypeFactory implements Factory<Boolean>
{
    private static final ColumbusModule_ProvideDebugBuildTypeFactory INSTANCE;
    
    static {
        INSTANCE = new ColumbusModule_ProvideDebugBuildTypeFactory();
    }
    
    public static ColumbusModule_ProvideDebugBuildTypeFactory create() {
        return ColumbusModule_ProvideDebugBuildTypeFactory.INSTANCE;
    }
    
    public static Boolean provideInstance() {
        return proxyProvideDebugBuildType();
    }
    
    public static boolean proxyProvideDebugBuildType() {
        return ColumbusModule.provideDebugBuildType();
    }
    
    @Override
    public Boolean get() {
        return provideInstance();
    }
}
