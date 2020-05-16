// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.external;

import android.util.Log;
import android.os.IBinder;
import android.service.quicksettings.IQSTileService;

public class QSTileServiceWrapper
{
    private final IQSTileService mService;
    
    public QSTileServiceWrapper(final IQSTileService mService) {
        this.mService = mService;
    }
    
    public IBinder asBinder() {
        return this.mService.asBinder();
    }
    
    public boolean onClick(final IBinder binder) {
        try {
            this.mService.onClick(binder);
            return true;
        }
        catch (Exception ex) {
            Log.d("IQSTileServiceWrapper", "Caught exception from TileService", (Throwable)ex);
            return false;
        }
    }
    
    public boolean onStartListening() {
        try {
            this.mService.onStartListening();
            return true;
        }
        catch (Exception ex) {
            Log.d("IQSTileServiceWrapper", "Caught exception from TileService", (Throwable)ex);
            return false;
        }
    }
    
    public boolean onStopListening() {
        try {
            this.mService.onStopListening();
            return true;
        }
        catch (Exception ex) {
            Log.d("IQSTileServiceWrapper", "Caught exception from TileService", (Throwable)ex);
            return false;
        }
    }
    
    public boolean onTileAdded() {
        try {
            this.mService.onTileAdded();
            return true;
        }
        catch (Exception ex) {
            Log.d("IQSTileServiceWrapper", "Caught exception from TileService", (Throwable)ex);
            return false;
        }
    }
    
    public boolean onTileRemoved() {
        try {
            this.mService.onTileRemoved();
            return true;
        }
        catch (Exception ex) {
            Log.d("IQSTileServiceWrapper", "Caught exception from TileService", (Throwable)ex);
            return false;
        }
    }
    
    public boolean onUnlockComplete() {
        try {
            this.mService.onUnlockComplete();
            return true;
        }
        catch (Exception ex) {
            Log.d("IQSTileServiceWrapper", "Caught exception from TileService", (Throwable)ex);
            return false;
        }
    }
}
