// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import dagger.internal.Factory;

public final class UiOffloadThread_Factory implements Factory<UiOffloadThread>
{
    private static final UiOffloadThread_Factory INSTANCE;
    
    static {
        INSTANCE = new UiOffloadThread_Factory();
    }
    
    public static UiOffloadThread_Factory create() {
        return UiOffloadThread_Factory.INSTANCE;
    }
    
    public static UiOffloadThread provideInstance() {
        return new UiOffloadThread();
    }
    
    @Override
    public UiOffloadThread get() {
        return provideInstance();
    }
}
