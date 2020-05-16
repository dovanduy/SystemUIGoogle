// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Factory;

public final class SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory implements Factory<Boolean>
{
    private static final SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory INSTANCE;
    
    static {
        INSTANCE = new SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory();
    }
    
    public static SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory create() {
        return SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory.INSTANCE;
    }
    
    public static Boolean provideInstance() {
        proxyProvideAllowNotificationLongPress();
        return Boolean.TRUE;
    }
    
    public static boolean proxyProvideAllowNotificationLongPress() {
        return SystemUIDefaultModule.provideAllowNotificationLongPress();
    }
    
    @Override
    public Boolean get() {
        return provideInstance();
    }
}
