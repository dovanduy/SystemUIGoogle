// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import dagger.internal.Factory;

public final class InitController_Factory implements Factory<InitController>
{
    private static final InitController_Factory INSTANCE;
    
    static {
        INSTANCE = new InitController_Factory();
    }
    
    public static InitController_Factory create() {
        return InitController_Factory.INSTANCE;
    }
    
    public static InitController provideInstance() {
        return new InitController();
    }
    
    @Override
    public InitController get() {
        return provideInstance();
    }
}
