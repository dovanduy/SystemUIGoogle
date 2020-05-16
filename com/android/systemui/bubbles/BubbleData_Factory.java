// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BubbleData_Factory implements Factory<BubbleData>
{
    private final Provider<Context> contextProvider;
    
    public BubbleData_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static BubbleData_Factory create(final Provider<Context> provider) {
        return new BubbleData_Factory(provider);
    }
    
    public static BubbleData provideInstance(final Provider<Context> provider) {
        return new BubbleData(provider.get());
    }
    
    @Override
    public BubbleData get() {
        return provideInstance(this.contextProvider);
    }
}
