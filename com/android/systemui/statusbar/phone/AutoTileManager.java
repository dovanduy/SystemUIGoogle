// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.hardware.display.ColorDisplayManager;
import java.util.Iterator;
import android.hardware.display.NightDisplayListener;
import android.hardware.display.NightDisplayListener$Callback;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.qs.QSTileHost;
import android.os.Handler;
import com.android.systemui.statusbar.policy.DataSaverController;
import android.content.Context;
import com.android.systemui.qs.SecureSetting;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.qs.AutoAddTracker;

public class AutoTileManager
{
    private final AutoAddTracker mAutoTracker;
    @VisibleForTesting
    final CastController.Callback mCastCallback;
    private final CastController mCastController;
    private SecureSetting mColorsSetting;
    private final Context mContext;
    private final DataSaverController mDataSaverController;
    private final DataSaverController.Listener mDataSaverListener;
    private final Handler mHandler;
    private final QSTileHost mHost;
    private final HotspotController.Callback mHotspotCallback;
    private final HotspotController mHotspotController;
    private final ManagedProfileController mManagedProfileController;
    @VisibleForTesting
    final NightDisplayListener$Callback mNightDisplayCallback;
    private final NightDisplayListener mNightDisplayListener;
    private final ManagedProfileController.Callback mProfileCallback;
    
    public AutoTileManager(final Context mContext, final AutoAddTracker mAutoTracker, final QSTileHost mHost, final Handler mHandler, final HotspotController mHotspotController, final DataSaverController mDataSaverController, final ManagedProfileController mManagedProfileController, final NightDisplayListener mNightDisplayListener, final CastController mCastController) {
        this.mProfileCallback = new ManagedProfileController.Callback() {
            @Override
            public void onManagedProfileChanged() {
                if (AutoTileManager.this.mAutoTracker.isAdded("work")) {
                    return;
                }
                if (AutoTileManager.this.mManagedProfileController.hasActiveProfile()) {
                    AutoTileManager.this.mHost.addTile("work");
                    AutoTileManager.this.mAutoTracker.setTileAdded("work");
                }
            }
            
            @Override
            public void onManagedProfileRemoved() {
            }
        };
        this.mDataSaverListener = new DataSaverController.Listener() {
            @Override
            public void onDataSaverChanged(final boolean b) {
                if (AutoTileManager.this.mAutoTracker.isAdded("saver")) {
                    return;
                }
                if (b) {
                    AutoTileManager.this.mHost.addTile("saver");
                    AutoTileManager.this.mAutoTracker.setTileAdded("saver");
                    AutoTileManager.this.mHandler.post((Runnable)new _$$Lambda$AutoTileManager$3$jtlbOv9xqjXTNoW_lFuZ_dYzc1k(this));
                }
            }
        };
        this.mHotspotCallback = new HotspotController.Callback() {
            @Override
            public void onHotspotChanged(final boolean b, final int n) {
                if (AutoTileManager.this.mAutoTracker.isAdded("hotspot")) {
                    return;
                }
                if (b) {
                    AutoTileManager.this.mHost.addTile("hotspot");
                    AutoTileManager.this.mAutoTracker.setTileAdded("hotspot");
                    AutoTileManager.this.mHandler.post((Runnable)new _$$Lambda$AutoTileManager$4$B3sgSxASy9hbK7cekuTaJNclHvY(this));
                }
            }
        };
        this.mNightDisplayCallback = (NightDisplayListener$Callback)new NightDisplayListener$Callback() {
            private void addNightTile() {
                if (AutoTileManager.this.mAutoTracker.isAdded("night")) {
                    return;
                }
                AutoTileManager.this.mHost.addTile("night");
                AutoTileManager.this.mAutoTracker.setTileAdded("night");
                AutoTileManager.this.mHandler.post((Runnable)new _$$Lambda$AutoTileManager$5$RSaNJ4x5t8UQTrBCygb8__uU0S0(this));
            }
            
            public void onActivated(final boolean b) {
                if (b) {
                    this.addNightTile();
                }
            }
            
            public void onAutoModeChanged(final int n) {
                if (n == 1 || n == 2) {
                    this.addNightTile();
                }
            }
        };
        this.mCastCallback = new CastController.Callback() {
            @Override
            public void onCastDevicesChanged() {
                if (AutoTileManager.this.mAutoTracker.isAdded("cast")) {
                    return;
                }
                final int n = 0;
                final Iterator<CastController.CastDevice> iterator = AutoTileManager.this.mCastController.getCastDevices().iterator();
                while (true) {
                    int state;
                    do {
                        final int n2 = n;
                        if (!iterator.hasNext()) {
                            if (n2 != 0) {
                                AutoTileManager.this.mHost.addTile("cast");
                                AutoTileManager.this.mAutoTracker.setTileAdded("cast");
                                AutoTileManager.this.mHandler.post((Runnable)new _$$Lambda$AutoTileManager$6$Es5SN3_RKnhrBR7n3pYQ0OR57uE(this));
                            }
                            return;
                        }
                        state = ((CastDevice)iterator.next()).state;
                    } while (state != 2 && state != 1);
                    final int n2 = 1;
                    continue;
                }
            }
        };
        this.mAutoTracker = mAutoTracker;
        this.mContext = mContext;
        this.mHost = mHost;
        this.mHandler = mHandler;
        this.mHotspotController = mHotspotController;
        this.mDataSaverController = mDataSaverController;
        this.mManagedProfileController = mManagedProfileController;
        this.mNightDisplayListener = mNightDisplayListener;
        this.mCastController = mCastController;
        if (!mAutoTracker.isAdded("hotspot")) {
            mHotspotController.addCallback(this.mHotspotCallback);
        }
        if (!this.mAutoTracker.isAdded("saver")) {
            mDataSaverController.addCallback(this.mDataSaverListener);
        }
        if (!this.mAutoTracker.isAdded("inversion")) {
            (this.mColorsSetting = new SecureSetting(this.mContext, this.mHandler, "accessibility_display_inversion_enabled") {
                @Override
                protected void handleValueChanged(final int n, final boolean b) {
                    if (AutoTileManager.this.mAutoTracker.isAdded("inversion")) {
                        return;
                    }
                    if (n != 0) {
                        AutoTileManager.this.mHost.addTile("inversion");
                        AutoTileManager.this.mAutoTracker.setTileAdded("inversion");
                        AutoTileManager.this.mHandler.post((Runnable)new _$$Lambda$AutoTileManager$1$fkFB83CLnhxsYFtYdorSMjVQp8g(this));
                    }
                }
            }).setListening(true);
        }
        if (!this.mAutoTracker.isAdded("work")) {
            mManagedProfileController.addCallback(this.mProfileCallback);
        }
        if (!this.mAutoTracker.isAdded("night") && ColorDisplayManager.isNightDisplayAvailable(this.mContext)) {
            mNightDisplayListener.setCallback(this.mNightDisplayCallback);
        }
        if (!this.mAutoTracker.isAdded("cast")) {
            mCastController.addCallback(this.mCastCallback);
        }
    }
    
    public void unmarkTileAsAutoAdded(final String tileRemoved) {
        this.mAutoTracker.setTileRemoved(tileRemoved);
    }
}
