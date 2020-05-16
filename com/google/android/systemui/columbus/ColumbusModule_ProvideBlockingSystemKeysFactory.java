// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.Preconditions;
import java.util.Set;
import dagger.internal.Factory;

public final class ColumbusModule_ProvideBlockingSystemKeysFactory implements Factory<Set<Integer>>
{
    private static final ColumbusModule_ProvideBlockingSystemKeysFactory INSTANCE;
    
    static {
        INSTANCE = new ColumbusModule_ProvideBlockingSystemKeysFactory();
    }
    
    public static ColumbusModule_ProvideBlockingSystemKeysFactory create() {
        return ColumbusModule_ProvideBlockingSystemKeysFactory.INSTANCE;
    }
    
    public static Set<Integer> provideInstance() {
        return proxyProvideBlockingSystemKeys();
    }
    
    public static Set<Integer> proxyProvideBlockingSystemKeys() {
        final Set<Integer> provideBlockingSystemKeys = ColumbusModule.provideBlockingSystemKeys();
        Preconditions.checkNotNull(provideBlockingSystemKeys, "Cannot return null from a non-@Nullable @Provides method");
        return provideBlockingSystemKeys;
    }
    
    @Override
    public Set<Integer> get() {
        return provideInstance();
    }
}
