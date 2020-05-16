// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import dagger.internal.Factory;

public final class WakefulnessLifecycle_Factory implements Factory<WakefulnessLifecycle>
{
    private static final WakefulnessLifecycle_Factory INSTANCE;
    
    static {
        INSTANCE = new WakefulnessLifecycle_Factory();
    }
    
    public static WakefulnessLifecycle_Factory create() {
        return WakefulnessLifecycle_Factory.INSTANCE;
    }
    
    public static WakefulnessLifecycle provideInstance() {
        return new WakefulnessLifecycle();
    }
    
    @Override
    public WakefulnessLifecycle get() {
        return provideInstance();
    }
}
