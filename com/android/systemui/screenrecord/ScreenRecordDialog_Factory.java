// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenrecord;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class ScreenRecordDialog_Factory implements Factory<ScreenRecordDialog>
{
    private final Provider<RecordingController> controllerProvider;
    
    public ScreenRecordDialog_Factory(final Provider<RecordingController> controllerProvider) {
        this.controllerProvider = controllerProvider;
    }
    
    public static ScreenRecordDialog_Factory create(final Provider<RecordingController> provider) {
        return new ScreenRecordDialog_Factory(provider);
    }
    
    public static ScreenRecordDialog provideInstance(final Provider<RecordingController> provider) {
        return new ScreenRecordDialog(provider.get());
    }
    
    @Override
    public ScreenRecordDialog get() {
        return provideInstance(this.controllerProvider);
    }
}
