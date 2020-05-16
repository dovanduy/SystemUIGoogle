// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log.dagger;

import dagger.internal.Preconditions;
import android.os.Looper;
import android.content.ContentResolver;
import javax.inject.Provider;
import com.android.systemui.log.LogcatEchoTracker;
import dagger.internal.Factory;

public final class LogModule_ProvideLogcatEchoTrackerFactory implements Factory<LogcatEchoTracker>
{
    private final Provider<ContentResolver> contentResolverProvider;
    private final Provider<Looper> looperProvider;
    
    public LogModule_ProvideLogcatEchoTrackerFactory(final Provider<ContentResolver> contentResolverProvider, final Provider<Looper> looperProvider) {
        this.contentResolverProvider = contentResolverProvider;
        this.looperProvider = looperProvider;
    }
    
    public static LogModule_ProvideLogcatEchoTrackerFactory create(final Provider<ContentResolver> provider, final Provider<Looper> provider2) {
        return new LogModule_ProvideLogcatEchoTrackerFactory(provider, provider2);
    }
    
    public static LogcatEchoTracker provideInstance(final Provider<ContentResolver> provider, final Provider<Looper> provider2) {
        return proxyProvideLogcatEchoTracker(provider.get(), provider2.get());
    }
    
    public static LogcatEchoTracker proxyProvideLogcatEchoTracker(final ContentResolver contentResolver, final Looper looper) {
        final LogcatEchoTracker provideLogcatEchoTracker = LogModule.provideLogcatEchoTracker(contentResolver, looper);
        Preconditions.checkNotNull(provideLogcatEchoTracker, "Cannot return null from a non-@Nullable @Provides method");
        return provideLogcatEchoTracker;
    }
    
    @Override
    public LogcatEchoTracker get() {
        return provideInstance(this.contentResolverProvider, this.looperProvider);
    }
}
