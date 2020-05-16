// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class BubbleOverflowActivity_Factory implements Factory<BubbleOverflowActivity>
{
    private final Provider<BubbleController> controllerProvider;
    
    public BubbleOverflowActivity_Factory(final Provider<BubbleController> controllerProvider) {
        this.controllerProvider = controllerProvider;
    }
    
    public static BubbleOverflowActivity_Factory create(final Provider<BubbleController> provider) {
        return new BubbleOverflowActivity_Factory(provider);
    }
    
    public static BubbleOverflowActivity provideInstance(final Provider<BubbleController> provider) {
        return new BubbleOverflowActivity(provider.get());
    }
    
    @Override
    public BubbleOverflowActivity get() {
        return provideInstance(this.controllerProvider);
    }
}
