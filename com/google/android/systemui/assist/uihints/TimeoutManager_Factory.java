// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.DoubleCheck;
import com.android.systemui.assist.AssistManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class TimeoutManager_Factory implements Factory<TimeoutManager>
{
    private final Provider<AssistManager> assistManagerProvider;
    
    public TimeoutManager_Factory(final Provider<AssistManager> assistManagerProvider) {
        this.assistManagerProvider = assistManagerProvider;
    }
    
    public static TimeoutManager_Factory create(final Provider<AssistManager> provider) {
        return new TimeoutManager_Factory(provider);
    }
    
    public static TimeoutManager provideInstance(final Provider<AssistManager> provider) {
        return new TimeoutManager(DoubleCheck.lazy(provider));
    }
    
    @Override
    public TimeoutManager get() {
        return provideInstance(this.assistManagerProvider);
    }
}
