// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.Factory;

public final class FlingVelocityWrapper_Factory implements Factory<FlingVelocityWrapper>
{
    private static final FlingVelocityWrapper_Factory INSTANCE;
    
    static {
        INSTANCE = new FlingVelocityWrapper_Factory();
    }
    
    public static FlingVelocityWrapper_Factory create() {
        return FlingVelocityWrapper_Factory.INSTANCE;
    }
    
    public static FlingVelocityWrapper provideInstance() {
        return new FlingVelocityWrapper();
    }
    
    @Override
    public FlingVelocityWrapper get() {
        return provideInstance();
    }
}
