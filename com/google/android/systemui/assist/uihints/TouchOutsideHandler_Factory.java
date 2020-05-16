// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.Factory;

public final class TouchOutsideHandler_Factory implements Factory<TouchOutsideHandler>
{
    private static final TouchOutsideHandler_Factory INSTANCE;
    
    static {
        INSTANCE = new TouchOutsideHandler_Factory();
    }
    
    public static TouchOutsideHandler_Factory create() {
        return TouchOutsideHandler_Factory.INSTANCE;
    }
    
    public static TouchOutsideHandler provideInstance() {
        return new TouchOutsideHandler();
    }
    
    @Override
    public TouchOutsideHandler get() {
        return provideInstance();
    }
}
