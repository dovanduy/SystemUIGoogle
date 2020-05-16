// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.time;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DateFormatUtil_Factory implements Factory<DateFormatUtil>
{
    private final Provider<Context> contextProvider;
    
    public DateFormatUtil_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static DateFormatUtil_Factory create(final Provider<Context> provider) {
        return new DateFormatUtil_Factory(provider);
    }
    
    public static DateFormatUtil provideInstance(final Provider<Context> provider) {
        return new DateFormatUtil(provider.get());
    }
    
    @Override
    public DateFormatUtil get() {
        return provideInstance(this.contextProvider);
    }
}
