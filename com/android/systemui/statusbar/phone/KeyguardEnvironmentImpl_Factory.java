// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import dagger.internal.Factory;

public final class KeyguardEnvironmentImpl_Factory implements Factory<KeyguardEnvironmentImpl>
{
    private static final KeyguardEnvironmentImpl_Factory INSTANCE;
    
    static {
        INSTANCE = new KeyguardEnvironmentImpl_Factory();
    }
    
    public static KeyguardEnvironmentImpl_Factory create() {
        return KeyguardEnvironmentImpl_Factory.INSTANCE;
    }
    
    public static KeyguardEnvironmentImpl provideInstance() {
        return new KeyguardEnvironmentImpl();
    }
    
    @Override
    public KeyguardEnvironmentImpl get() {
        return provideInstance();
    }
}
