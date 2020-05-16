// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Factory;

public final class SystemUIDefaultModule_ProvideLeakReportEmailFactory implements Factory<String>
{
    private static final SystemUIDefaultModule_ProvideLeakReportEmailFactory INSTANCE;
    
    static {
        INSTANCE = new SystemUIDefaultModule_ProvideLeakReportEmailFactory();
    }
    
    public static SystemUIDefaultModule_ProvideLeakReportEmailFactory create() {
        return SystemUIDefaultModule_ProvideLeakReportEmailFactory.INSTANCE;
    }
    
    public static String provideInstance() {
        return proxyProvideLeakReportEmail();
    }
    
    public static String proxyProvideLeakReportEmail() {
        return SystemUIDefaultModule.provideLeakReportEmail();
    }
    
    @Override
    public String get() {
        return provideInstance();
    }
}
