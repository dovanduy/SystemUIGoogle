// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.screenrecord.RecordingController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ScreenRecordTile_Factory implements Factory<ScreenRecordTile>
{
    private final Provider<RecordingController> controllerProvider;
    private final Provider<QSHost> hostProvider;
    
    public ScreenRecordTile_Factory(final Provider<QSHost> hostProvider, final Provider<RecordingController> controllerProvider) {
        this.hostProvider = hostProvider;
        this.controllerProvider = controllerProvider;
    }
    
    public static ScreenRecordTile_Factory create(final Provider<QSHost> provider, final Provider<RecordingController> provider2) {
        return new ScreenRecordTile_Factory(provider, provider2);
    }
    
    public static ScreenRecordTile provideInstance(final Provider<QSHost> provider, final Provider<RecordingController> provider2) {
        return new ScreenRecordTile(provider.get(), provider2.get());
    }
    
    @Override
    public ScreenRecordTile get() {
        return provideInstance(this.hostProvider, this.controllerProvider);
    }
}
