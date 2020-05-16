// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import android.os.RemoteException;
import android.util.Log;
import android.service.vr.IVrManager$Stub;
import android.os.ServiceManager;
import android.service.vr.IVrStateCallbacks$Stub;
import android.content.Context;
import android.service.vr.IVrStateCallbacks;
import android.service.vr.IVrManager;

public class VrMode extends Gate
{
    private boolean mInVrMode;
    private final IVrManager mVrManager;
    private final IVrStateCallbacks mVrStateCallbacks;
    
    public VrMode(final Context context) {
        super(context);
        this.mVrStateCallbacks = (IVrStateCallbacks)new IVrStateCallbacks$Stub() {
            public void onVrStateChanged(final boolean b) {
                if (b != VrMode.this.mInVrMode) {
                    VrMode.this.mInVrMode = b;
                    VrMode.this.notifyListener();
                }
            }
        };
        this.mVrManager = IVrManager$Stub.asInterface(ServiceManager.getService("vrmanager"));
    }
    
    @Override
    protected boolean isBlocked() {
        return this.mInVrMode;
    }
    
    @Override
    protected void onActivate() {
        final IVrManager mVrManager = this.mVrManager;
        if (mVrManager != null) {
            try {
                this.mInVrMode = mVrManager.getVrModeState();
                this.mVrManager.registerListener(this.mVrStateCallbacks);
            }
            catch (RemoteException ex) {
                Log.e("Elmyra/VrMode", "Could not register IVrManager listener", (Throwable)ex);
                this.mInVrMode = false;
            }
        }
    }
    
    @Override
    protected void onDeactivate() {
        final IVrManager mVrManager = this.mVrManager;
        if (mVrManager != null) {
            try {
                mVrManager.unregisterListener(this.mVrStateCallbacks);
            }
            catch (RemoteException ex) {
                Log.e("Elmyra/VrMode", "Could not unregister IVrManager listener", (Throwable)ex);
                this.mInVrMode = false;
            }
        }
    }
}
