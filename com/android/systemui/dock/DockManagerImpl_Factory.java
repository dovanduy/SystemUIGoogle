// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dock;

import dagger.internal.Factory;

public final class DockManagerImpl_Factory implements Factory<DockManagerImpl>
{
    private static final DockManagerImpl_Factory INSTANCE;
    
    static {
        INSTANCE = new DockManagerImpl_Factory();
    }
    
    public static DockManagerImpl_Factory create() {
        return DockManagerImpl_Factory.INSTANCE;
    }
    
    public static DockManagerImpl provideInstance() {
        return new DockManagerImpl();
    }
    
    @Override
    public DockManagerImpl get() {
        return provideInstance();
    }
}
