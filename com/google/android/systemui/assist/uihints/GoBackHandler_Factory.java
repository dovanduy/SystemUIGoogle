// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.Factory;

public final class GoBackHandler_Factory implements Factory<GoBackHandler>
{
    private static final GoBackHandler_Factory INSTANCE;
    
    static {
        INSTANCE = new GoBackHandler_Factory();
    }
    
    public static GoBackHandler_Factory create() {
        return GoBackHandler_Factory.INSTANCE;
    }
    
    public static GoBackHandler provideInstance() {
        return new GoBackHandler();
    }
    
    @Override
    public GoBackHandler get() {
        return provideInstance();
    }
}
