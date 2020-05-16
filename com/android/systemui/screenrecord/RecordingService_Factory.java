// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenrecord;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class RecordingService_Factory implements Factory<RecordingService>
{
    private final Provider<RecordingController> controllerProvider;
    
    public RecordingService_Factory(final Provider<RecordingController> controllerProvider) {
        this.controllerProvider = controllerProvider;
    }
    
    public static RecordingService_Factory create(final Provider<RecordingController> provider) {
        return new RecordingService_Factory(provider);
    }
    
    public static RecordingService provideInstance(final Provider<RecordingController> provider) {
        return new RecordingService(provider.get());
    }
    
    @Override
    public RecordingService get() {
        return provideInstance(this.controllerProvider);
    }
}
