// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.os.RemoteException;
import android.util.Log;
import android.service.vr.IVrStateCallbacks;
import android.service.vr.IVrManager$Stub;
import android.os.ServiceManager;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.service.vr.IVrManager;

public final class VrMode extends Gate
{
    private boolean inVrMode;
    private final IVrManager vrManager;
    private final VrMode$vrStateCallbacks.VrMode$vrStateCallbacks$1 vrStateCallbacks;
    
    public VrMode(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context);
        this.vrManager = IVrManager$Stub.asInterface(ServiceManager.getService("vrmanager"));
        this.vrStateCallbacks = new VrMode$vrStateCallbacks.VrMode$vrStateCallbacks$1(this);
    }
    
    @Override
    protected boolean isBlocked() {
        return this.inVrMode;
    }
    
    @Override
    protected void onActivate() {
        final IVrManager vrManager = this.vrManager;
        if (vrManager != null) {
            try {
                final boolean vrModeState = vrManager.getVrModeState();
                boolean inVrMode = true;
                if (!vrModeState) {
                    inVrMode = false;
                }
                this.inVrMode = inVrMode;
                vrManager.registerListener((IVrStateCallbacks)this.vrStateCallbacks);
            }
            catch (RemoteException ex) {
                Log.e("Columbus/VrMode", "Could not register IVrManager listener", (Throwable)ex);
                this.inVrMode = false;
            }
        }
    }
    
    @Override
    protected void onDeactivate() {
        try {
            final IVrManager vrManager = this.vrManager;
            if (vrManager != null) {
                vrManager.unregisterListener((IVrStateCallbacks)this.vrStateCallbacks);
            }
        }
        catch (RemoteException ex) {
            Log.e("Columbus/VrMode", "Could not unregister IVrManager listener", (Throwable)ex);
            this.inVrMode = false;
        }
    }
}
