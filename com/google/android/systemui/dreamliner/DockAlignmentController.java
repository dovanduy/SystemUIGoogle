// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dreamliner;

import android.util.Log;

public class DockAlignmentController
{
    private static final boolean DEBUG;
    private int mAlignmentState;
    private final DockObserver mDockObserver;
    private final WirelessCharger mWirelessCharger;
    
    static {
        DEBUG = Log.isLoggable("DockAlignmentController", 3);
    }
    
    public DockAlignmentController(final WirelessCharger mWirelessCharger, final DockObserver mDockObserver) {
        this.mAlignmentState = 0;
        this.mWirelessCharger = mWirelessCharger;
        this.mDockObserver = mDockObserver;
    }
    
    private int getAlignmentState(final DockAlignInfo dockAlignInfo) {
        if (DockAlignmentController.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("onAlignInfo, state: ");
            sb.append(dockAlignInfo.getAlignState());
            sb.append(", alignPct: ");
            sb.append(dockAlignInfo.getAlignPct());
            Log.d("DockAlignmentController", sb.toString());
        }
        int mAlignmentState = this.mAlignmentState;
        final int alignState = dockAlignInfo.getAlignState();
        if (alignState != 0) {
            mAlignmentState = 1;
            if (alignState != 1) {
                if (alignState != 2) {
                    if (alignState != 3) {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("Unexpected state: ");
                        sb2.append(dockAlignInfo.getAlignState());
                        Log.w("DockAlignmentController", sb2.toString());
                    }
                }
                else {
                    final int alignPct = dockAlignInfo.getAlignPct();
                    if (alignPct >= 0) {
                        if (alignPct < 100) {
                            return mAlignmentState;
                        }
                        mAlignmentState = 0;
                        return mAlignmentState;
                    }
                }
                mAlignmentState = -1;
            }
            else {
                mAlignmentState = 2;
            }
        }
        return mAlignmentState;
    }
    
    private void onAlignInfoCallBack(final DockAlignInfo dockAlignInfo) {
        final int mAlignmentState = this.mAlignmentState;
        final int alignmentState = this.getAlignmentState(dockAlignInfo);
        this.mAlignmentState = alignmentState;
        if (mAlignmentState != alignmentState) {
            this.mDockObserver.onAlignStateChanged(alignmentState);
            if (DockAlignmentController.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("onAlignStateChanged, state: ");
                sb.append(this.mAlignmentState);
                Log.d("DockAlignmentController", sb.toString());
            }
        }
    }
    
    void registerAlignInfoListener() {
        final WirelessCharger mWirelessCharger = this.mWirelessCharger;
        if (mWirelessCharger == null) {
            Log.w("DockAlignmentController", "wirelessCharger is null");
            return;
        }
        mWirelessCharger.registerAlignInfo((WirelessCharger.AlignInfoListener)new RegisterAlignInfoListener());
    }
    
    private final class RegisterAlignInfoListener implements AlignInfoListener
    {
        @Override
        public void onAlignInfoChanged(final DockAlignInfo dockAlignInfo) {
            DockAlignmentController.this.onAlignInfoCallBack(dockAlignInfo);
        }
    }
}
