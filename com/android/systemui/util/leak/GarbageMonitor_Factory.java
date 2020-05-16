// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import android.content.Context;
import android.os.Looper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GarbageMonitor_Factory implements Factory<GarbageMonitor>
{
    private final Provider<Looper> bgLooperProvider;
    private final Provider<Context> contextProvider;
    private final Provider<LeakDetector> leakDetectorProvider;
    private final Provider<LeakReporter> leakReporterProvider;
    
    public GarbageMonitor_Factory(final Provider<Context> contextProvider, final Provider<Looper> bgLooperProvider, final Provider<LeakDetector> leakDetectorProvider, final Provider<LeakReporter> leakReporterProvider) {
        this.contextProvider = contextProvider;
        this.bgLooperProvider = bgLooperProvider;
        this.leakDetectorProvider = leakDetectorProvider;
        this.leakReporterProvider = leakReporterProvider;
    }
    
    public static GarbageMonitor_Factory create(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<LeakDetector> provider3, final Provider<LeakReporter> provider4) {
        return new GarbageMonitor_Factory(provider, provider2, provider3, provider4);
    }
    
    public static GarbageMonitor provideInstance(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<LeakDetector> provider3, final Provider<LeakReporter> provider4) {
        return new GarbageMonitor(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public GarbageMonitor get() {
        return provideInstance(this.contextProvider, this.bgLooperProvider, this.leakDetectorProvider, this.leakReporterProvider);
    }
}
