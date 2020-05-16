// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import dagger.internal.Factory;

public final class TunerActivity_Factory implements Factory<TunerActivity>
{
    private static final TunerActivity_Factory INSTANCE;
    
    static {
        INSTANCE = new TunerActivity_Factory();
    }
    
    public static TunerActivity_Factory create() {
        return TunerActivity_Factory.INSTANCE;
    }
    
    public static TunerActivity provideInstance() {
        return new TunerActivity();
    }
    
    @Override
    public TunerActivity get() {
        return provideInstance();
    }
}
