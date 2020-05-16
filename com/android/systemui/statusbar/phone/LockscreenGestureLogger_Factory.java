// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import dagger.internal.Factory;

public final class LockscreenGestureLogger_Factory implements Factory<LockscreenGestureLogger>
{
    private static final LockscreenGestureLogger_Factory INSTANCE;
    
    static {
        INSTANCE = new LockscreenGestureLogger_Factory();
    }
    
    public static LockscreenGestureLogger_Factory create() {
        return LockscreenGestureLogger_Factory.INSTANCE;
    }
    
    public static LockscreenGestureLogger provideInstance() {
        return new LockscreenGestureLogger();
    }
    
    @Override
    public LockscreenGestureLogger get() {
        return provideInstance();
    }
}
