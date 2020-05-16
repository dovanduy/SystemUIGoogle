// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import dagger.internal.Factory;

public final class ScreenLifecycle_Factory implements Factory<ScreenLifecycle>
{
    private static final ScreenLifecycle_Factory INSTANCE;
    
    static {
        INSTANCE = new ScreenLifecycle_Factory();
    }
    
    public static ScreenLifecycle_Factory create() {
        return ScreenLifecycle_Factory.INSTANCE;
    }
    
    public static ScreenLifecycle provideInstance() {
        return new ScreenLifecycle();
    }
    
    @Override
    public ScreenLifecycle get() {
        return provideInstance();
    }
}
