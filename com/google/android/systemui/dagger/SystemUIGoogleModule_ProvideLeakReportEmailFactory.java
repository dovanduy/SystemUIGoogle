// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dagger;

import dagger.internal.Preconditions;
import dagger.internal.Factory;

public final class SystemUIGoogleModule_ProvideLeakReportEmailFactory implements Factory<String>
{
    private static final SystemUIGoogleModule_ProvideLeakReportEmailFactory INSTANCE;
    
    static {
        INSTANCE = new SystemUIGoogleModule_ProvideLeakReportEmailFactory();
    }
    
    public static SystemUIGoogleModule_ProvideLeakReportEmailFactory create() {
        return SystemUIGoogleModule_ProvideLeakReportEmailFactory.INSTANCE;
    }
    
    public static String provideInstance() {
        return proxyProvideLeakReportEmail();
    }
    
    public static String proxyProvideLeakReportEmail() {
        final String provideLeakReportEmail = SystemUIGoogleModule.provideLeakReportEmail();
        Preconditions.checkNotNull(provideLeakReportEmail, "Cannot return null from a non-@Nullable @Provides method");
        return provideLeakReportEmail;
    }
    
    @Override
    public String get() {
        return provideInstance();
    }
}
