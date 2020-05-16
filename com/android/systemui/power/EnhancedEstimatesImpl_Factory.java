// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.power;

import dagger.internal.Factory;

public final class EnhancedEstimatesImpl_Factory implements Factory<EnhancedEstimatesImpl>
{
    private static final EnhancedEstimatesImpl_Factory INSTANCE;
    
    static {
        INSTANCE = new EnhancedEstimatesImpl_Factory();
    }
    
    public static EnhancedEstimatesImpl_Factory create() {
        return EnhancedEstimatesImpl_Factory.INSTANCE;
    }
    
    public static EnhancedEstimatesImpl provideInstance() {
        return new EnhancedEstimatesImpl();
    }
    
    @Override
    public EnhancedEstimatesImpl get() {
        return provideInstance();
    }
}
