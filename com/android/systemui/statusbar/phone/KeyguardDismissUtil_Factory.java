// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import dagger.internal.Factory;

public final class KeyguardDismissUtil_Factory implements Factory<KeyguardDismissUtil>
{
    private static final KeyguardDismissUtil_Factory INSTANCE;
    
    static {
        INSTANCE = new KeyguardDismissUtil_Factory();
    }
    
    public static KeyguardDismissUtil_Factory create() {
        return KeyguardDismissUtil_Factory.INSTANCE;
    }
    
    public static KeyguardDismissUtil provideInstance() {
        return new KeyguardDismissUtil();
    }
    
    @Override
    public KeyguardDismissUtil get() {
        return provideInstance();
    }
}
