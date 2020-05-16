// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import android.os.RemoteException;
import java.io.PrintWriter;
import android.util.Log;
import android.app.IWallpaperManager;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.BiometricUnlockController;

public class DozeWallpaperState implements Part
{
    private static final boolean DEBUG;
    private final BiometricUnlockController mBiometricUnlockController;
    private final DozeParameters mDozeParameters;
    private boolean mIsAmbientMode;
    private final IWallpaperManager mWallpaperManagerService;
    
    static {
        DEBUG = Log.isLoggable("DozeWallpaperState", 3);
    }
    
    public DozeWallpaperState(final IWallpaperManager mWallpaperManagerService, final BiometricUnlockController mBiometricUnlockController, final DozeParameters mDozeParameters) {
        this.mWallpaperManagerService = mWallpaperManagerService;
        this.mBiometricUnlockController = mBiometricUnlockController;
        this.mDozeParameters = mDozeParameters;
    }
    
    @Override
    public void dump(final PrintWriter printWriter) {
        printWriter.println("DozeWallpaperState:");
        final StringBuilder sb = new StringBuilder();
        sb.append(" isAmbientMode: ");
        sb.append(this.mIsAmbientMode);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(" hasWallpaperService: ");
        sb2.append(this.mWallpaperManagerService != null);
        printWriter.println(sb2.toString());
    }
    
    @Override
    public void transitionTo(final State state, final State state2) {
        final int n = DozeWallpaperState$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()];
        boolean shouldControlScreenOff = false;
        boolean mIsAmbientMode = false;
        switch (n) {
            default: {
                mIsAmbientMode = false;
                break;
            }
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8: {
                mIsAmbientMode = true;
                break;
            }
        }
        if (mIsAmbientMode) {
            shouldControlScreenOff = this.mDozeParameters.shouldControlScreenOff();
        }
        else {
            final boolean b = state == State.DOZE_PULSING && state2 == State.FINISH;
            if (((this.mDozeParameters.getDisplayNeedsBlanking() ^ true) && !this.mBiometricUnlockController.unlockedByWakeAndUnlock()) || b) {
                shouldControlScreenOff = true;
            }
        }
        if (mIsAmbientMode != this.mIsAmbientMode) {
            this.mIsAmbientMode = mIsAmbientMode;
            if (this.mWallpaperManagerService != null) {
                long lng;
                if (shouldControlScreenOff) {
                    lng = 500L;
                }
                else {
                    lng = 0L;
                }
                try {
                    if (DozeWallpaperState.DEBUG) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("AOD wallpaper state changed to: ");
                        sb.append(this.mIsAmbientMode);
                        sb.append(", animationDuration: ");
                        sb.append(lng);
                        Log.i("DozeWallpaperState", sb.toString());
                    }
                    this.mWallpaperManagerService.setInAmbientMode(this.mIsAmbientMode, lng);
                }
                catch (RemoteException ex) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Cannot notify state to WallpaperManagerService: ");
                    sb2.append(this.mIsAmbientMode);
                    Log.w("DozeWallpaperState", sb2.toString());
                }
            }
        }
    }
}
