// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LeakReporter_Factory implements Factory<LeakReporter>
{
    private final Provider<Context> contextProvider;
    private final Provider<LeakDetector> leakDetectorProvider;
    private final Provider<String> leakReportEmailProvider;
    
    public LeakReporter_Factory(final Provider<Context> contextProvider, final Provider<LeakDetector> leakDetectorProvider, final Provider<String> leakReportEmailProvider) {
        this.contextProvider = contextProvider;
        this.leakDetectorProvider = leakDetectorProvider;
        this.leakReportEmailProvider = leakReportEmailProvider;
    }
    
    public static LeakReporter_Factory create(final Provider<Context> provider, final Provider<LeakDetector> provider2, final Provider<String> provider3) {
        return new LeakReporter_Factory(provider, provider2, provider3);
    }
    
    public static LeakReporter provideInstance(final Provider<Context> provider, final Provider<LeakDetector> provider2, final Provider<String> provider3) {
        return new LeakReporter(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public LeakReporter get() {
        return provideInstance(this.contextProvider, this.leakDetectorProvider, this.leakReportEmailProvider);
    }
}
