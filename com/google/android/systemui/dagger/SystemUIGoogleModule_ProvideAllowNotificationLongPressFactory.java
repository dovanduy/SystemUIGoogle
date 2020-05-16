// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dagger;

import dagger.internal.Factory;

public final class SystemUIGoogleModule_ProvideAllowNotificationLongPressFactory implements Factory<Boolean>
{
    private static final SystemUIGoogleModule_ProvideAllowNotificationLongPressFactory INSTANCE;
    
    static {
        INSTANCE = new SystemUIGoogleModule_ProvideAllowNotificationLongPressFactory();
    }
    
    public static SystemUIGoogleModule_ProvideAllowNotificationLongPressFactory create() {
        return SystemUIGoogleModule_ProvideAllowNotificationLongPressFactory.INSTANCE;
    }
    
    public static Boolean provideInstance() {
        proxyProvideAllowNotificationLongPress();
        return Boolean.TRUE;
    }
    
    public static boolean proxyProvideAllowNotificationLongPress() {
        return SystemUIGoogleModule.provideAllowNotificationLongPress();
    }
    
    @Override
    public Boolean get() {
        return provideInstance();
    }
}
