// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import android.os.Handler;
import com.android.systemui.assist.DeviceConfigHelper;
import dagger.Lazy;
import com.android.systemui.Dumpable;

public final class ColumbusServiceWrapper implements Dumpable
{
    private final Lazy<ColumbusService> columbusService;
    private final boolean debugBuildType;
    private final DeviceConfigHelper deviceConfigHelper;
    private final Handler handler;
    private final DeviceConfig$OnPropertiesChangedListener propertiesChangedListener;
    private boolean started;
    
    public ColumbusServiceWrapper(final boolean debugBuildType, final Lazy<ColumbusService> columbusService, final DeviceConfigHelper deviceConfigHelper, final Handler handler) {
        Intrinsics.checkParameterIsNotNull(columbusService, "columbusService");
        Intrinsics.checkParameterIsNotNull(deviceConfigHelper, "deviceConfigHelper");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        this.debugBuildType = debugBuildType;
        this.columbusService = columbusService;
        this.deviceConfigHelper = deviceConfigHelper;
        this.handler = handler;
        this.propertiesChangedListener = (DeviceConfig$OnPropertiesChangedListener)new ColumbusServiceWrapper$propertiesChangedListener.ColumbusServiceWrapper$propertiesChangedListener$1(this);
        final boolean debugBuildType2 = this.debugBuildType;
        int n = 0;
        if (debugBuildType2) {
            n = n;
            if (this.deviceConfigHelper.getBoolean("systemui_google_columbus_enabled", false)) {
                n = 1;
            }
        }
        if (n != 0) {
            this.startService();
        }
        else if (this.debugBuildType) {
            this.deviceConfigHelper.addOnPropertiesChangedListener(new Executor() {
                final /* synthetic */ ColumbusServiceWrapper this$0;
                
                @Override
                public final void execute(final Runnable runnable) {
                    ColumbusServiceWrapper.access$getHandler$p(this.this$0).post(runnable);
                }
            }, this.propertiesChangedListener);
        }
    }
    
    public static final /* synthetic */ Handler access$getHandler$p(final ColumbusServiceWrapper columbusServiceWrapper) {
        return columbusServiceWrapper.handler;
    }
    
    private final void startService() {
        this.deviceConfigHelper.removeOnPropertiesChangedListener(this.propertiesChangedListener);
        this.started = true;
        this.columbusService.get();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        if (this.started) {
            this.columbusService.get().dump(fileDescriptor, printWriter, array);
        }
    }
}
