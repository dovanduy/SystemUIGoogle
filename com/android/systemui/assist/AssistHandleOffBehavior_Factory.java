// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import dagger.internal.Factory;

public final class AssistHandleOffBehavior_Factory implements Factory<AssistHandleOffBehavior>
{
    private static final AssistHandleOffBehavior_Factory INSTANCE;
    
    static {
        INSTANCE = new AssistHandleOffBehavior_Factory();
    }
    
    public static AssistHandleOffBehavior_Factory create() {
        return AssistHandleOffBehavior_Factory.INSTANCE;
    }
    
    public static AssistHandleOffBehavior provideInstance() {
        return new AssistHandleOffBehavior();
    }
    
    @Override
    public AssistHandleOffBehavior get() {
        return provideInstance();
    }
}
