// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import dagger.internal.Factory;

public final class ForegroundServicesDialog_Factory implements Factory<ForegroundServicesDialog>
{
    private static final ForegroundServicesDialog_Factory INSTANCE;
    
    static {
        INSTANCE = new ForegroundServicesDialog_Factory();
    }
    
    public static ForegroundServicesDialog_Factory create() {
        return ForegroundServicesDialog_Factory.INSTANCE;
    }
    
    public static ForegroundServicesDialog provideInstance() {
        return new ForegroundServicesDialog();
    }
    
    @Override
    public ForegroundServicesDialog get() {
        return provideInstance();
    }
}
