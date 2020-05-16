// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenrecord;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class RecordingController_Factory implements Factory<RecordingController>
{
    private final Provider<Context> contextProvider;
    
    public RecordingController_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static RecordingController_Factory create(final Provider<Context> provider) {
        return new RecordingController_Factory(provider);
    }
    
    public static RecordingController provideInstance(final Provider<Context> provider) {
        return new RecordingController(provider.get());
    }
    
    @Override
    public RecordingController get() {
        return provideInstance(this.contextProvider);
    }
}
