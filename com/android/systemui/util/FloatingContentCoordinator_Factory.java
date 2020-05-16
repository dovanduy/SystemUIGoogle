// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import dagger.internal.Factory;

public final class FloatingContentCoordinator_Factory implements Factory<FloatingContentCoordinator>
{
    private static final FloatingContentCoordinator_Factory INSTANCE;
    
    static {
        INSTANCE = new FloatingContentCoordinator_Factory();
    }
    
    public static FloatingContentCoordinator_Factory create() {
        return FloatingContentCoordinator_Factory.INSTANCE;
    }
    
    public static FloatingContentCoordinator provideInstance() {
        return new FloatingContentCoordinator();
    }
    
    @Override
    public FloatingContentCoordinator get() {
        return provideInstance();
    }
}
