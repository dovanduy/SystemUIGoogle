// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.Factory;

public final class LightnessProvider_Factory implements Factory<LightnessProvider>
{
    private static final LightnessProvider_Factory INSTANCE;
    
    static {
        INSTANCE = new LightnessProvider_Factory();
    }
    
    public static LightnessProvider_Factory create() {
        return LightnessProvider_Factory.INSTANCE;
    }
    
    public static LightnessProvider provideInstance() {
        return new LightnessProvider();
    }
    
    @Override
    public LightnessProvider get() {
        return provideInstance();
    }
}
