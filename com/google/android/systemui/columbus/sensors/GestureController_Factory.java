// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

import dagger.internal.Factory;

public final class GestureController_Factory implements Factory<GestureController>
{
    private static final GestureController_Factory INSTANCE;
    
    static {
        INSTANCE = new GestureController_Factory();
    }
    
    public static GestureController_Factory create() {
        return GestureController_Factory.INSTANCE;
    }
    
    public static GestureController provideInstance() {
        return new GestureController();
    }
    
    @Override
    public GestureController get() {
        return provideInstance();
    }
}
