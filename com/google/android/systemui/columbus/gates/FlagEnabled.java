// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import android.os.Handler;
import java.util.concurrent.Executor;
import com.android.systemui.assist.DeviceConfigHelper;

public final class FlagEnabled extends Gate
{
    private boolean columbusEnabled;
    private final boolean debugBuildType;
    private final DeviceConfigHelper deviceConfigHelper;
    private final Executor executor;
    private final Handler handler;
    private final DeviceConfig$OnPropertiesChangedListener propertiesChangedListener;
    
    public FlagEnabled(final boolean debugBuildType, final Context context, final Handler handler, final DeviceConfigHelper deviceConfigHelper) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        Intrinsics.checkParameterIsNotNull(deviceConfigHelper, "deviceConfigHelper");
        super(context);
        this.debugBuildType = debugBuildType;
        this.handler = handler;
        this.deviceConfigHelper = deviceConfigHelper;
        this.propertiesChangedListener = (DeviceConfig$OnPropertiesChangedListener)new FlagEnabled$propertiesChangedListener.FlagEnabled$propertiesChangedListener$1(this);
        this.executor = (Executor)new FlagEnabled$executor.FlagEnabled$executor$1(this);
    }
    
    @Override
    protected boolean isBlocked() {
        return this.columbusEnabled ^ true;
    }
    
    @Override
    protected void onActivate() {
        final boolean debugBuildType = this.debugBuildType;
        boolean columbusEnabled = false;
        if (debugBuildType) {
            columbusEnabled = columbusEnabled;
            if (this.deviceConfigHelper.getBoolean("systemui_google_columbus_enabled", false)) {
                columbusEnabled = true;
            }
        }
        this.columbusEnabled = columbusEnabled;
        if (this.debugBuildType) {
            this.deviceConfigHelper.addOnPropertiesChangedListener(this.executor, this.propertiesChangedListener);
        }
        this.notifyListener();
    }
    
    @Override
    protected void onDeactivate() {
        this.deviceConfigHelper.removeOnPropertiesChangedListener(this.propertiesChangedListener);
    }
}
