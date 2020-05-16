// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.Preconditions;
import com.google.android.systemui.columbus.sensors.config.Adjustment;
import java.util.Set;
import dagger.internal.Factory;

public final class ColumbusModule_ProvideGestureAdjustmentsFactory implements Factory<Set<Adjustment>>
{
    private static final ColumbusModule_ProvideGestureAdjustmentsFactory INSTANCE;
    
    static {
        INSTANCE = new ColumbusModule_ProvideGestureAdjustmentsFactory();
    }
    
    public static ColumbusModule_ProvideGestureAdjustmentsFactory create() {
        return ColumbusModule_ProvideGestureAdjustmentsFactory.INSTANCE;
    }
    
    public static Set<Adjustment> provideInstance() {
        return proxyProvideGestureAdjustments();
    }
    
    public static Set<Adjustment> proxyProvideGestureAdjustments() {
        final Set<Adjustment> provideGestureAdjustments = ColumbusModule.provideGestureAdjustments();
        Preconditions.checkNotNull(provideGestureAdjustments, "Cannot return null from a non-@Nullable @Provides method");
        return provideGestureAdjustments;
    }
    
    @Override
    public Set<Adjustment> get() {
        return provideInstance();
    }
}
