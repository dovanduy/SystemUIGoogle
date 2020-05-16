// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.settingslib.fuelgauge.BatterySaverUtils;
import android.content.Intent;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.Bundle;
import android.content.IntentFilter;
import java.util.Iterator;
import com.android.settingslib.utils.PowerUtil;
import com.android.internal.annotations.VisibleForTesting;
import android.util.Log;
import android.os.PowerManager;
import com.android.systemui.power.EnhancedEstimates;
import com.android.settingslib.fuelgauge.Estimate;
import android.content.Context;
import java.util.ArrayList;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Handler;
import android.content.BroadcastReceiver;

public class BatteryControllerImpl extends BroadcastReceiver implements BatteryController
{
    private static final boolean DEBUG;
    private boolean mAodPowerSave;
    private final Handler mBgHandler;
    private final BroadcastDispatcher mBroadcastDispatcher;
    protected final ArrayList<BatteryStateChangeCallback> mChangeCallbacks;
    private boolean mCharged;
    protected boolean mCharging;
    protected final Context mContext;
    private boolean mDemoMode;
    private Estimate mEstimate;
    private final EnhancedEstimates mEstimates;
    private final ArrayList<EstimateFetchCompletion> mFetchCallbacks;
    private boolean mFetchingEstimate;
    private boolean mHasReceivedBattery;
    protected int mLevel;
    private final Handler mMainHandler;
    protected boolean mPluggedIn;
    private final PowerManager mPowerManager;
    protected boolean mPowerSave;
    private boolean mTestmode;
    
    static {
        DEBUG = Log.isLoggable("BatteryController", 3);
    }
    
    @VisibleForTesting
    protected BatteryControllerImpl(final Context mContext, final EnhancedEstimates mEstimates, final PowerManager mPowerManager, final BroadcastDispatcher mBroadcastDispatcher, final Handler mMainHandler, final Handler mBgHandler) {
        this.mChangeCallbacks = new ArrayList<BatteryStateChangeCallback>();
        this.mFetchCallbacks = new ArrayList<EstimateFetchCompletion>();
        this.mTestmode = false;
        this.mHasReceivedBattery = false;
        this.mFetchingEstimate = false;
        this.mContext = mContext;
        this.mMainHandler = mMainHandler;
        this.mBgHandler = mBgHandler;
        this.mPowerManager = mPowerManager;
        this.mEstimates = mEstimates;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.registerReceiver();
        this.updatePowerSave();
        this.updateEstimate();
    }
    
    private void firePowerSaveChanged() {
        synchronized (this.mChangeCallbacks) {
            for (int size = this.mChangeCallbacks.size(), i = 0; i < size; ++i) {
                this.mChangeCallbacks.get(i).onPowerSaveChanged(this.mPowerSave);
            }
        }
    }
    
    private String generateTimeRemainingString() {
        synchronized (this.mFetchCallbacks) {
            if (this.mEstimate == null) {
                return null;
            }
            return PowerUtil.getBatteryRemainingShortStringFormatted(this.mContext, this.mEstimate.getEstimateMillis());
        }
    }
    
    private void notifyEstimateFetchCallbacks() {
        synchronized (this.mFetchCallbacks) {
            final String generateTimeRemainingString = this.generateTimeRemainingString();
            final Iterator<EstimateFetchCompletion> iterator = this.mFetchCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onBatteryRemainingEstimateRetrieved(generateTimeRemainingString);
            }
            this.mFetchCallbacks.clear();
        }
    }
    
    private void registerReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction("com.android.systemui.BATTERY_LEVEL_TEST");
        this.mBroadcastDispatcher.registerReceiver(this, intentFilter);
    }
    
    private void setPowerSave(final boolean mPowerSave) {
        if (mPowerSave == this.mPowerSave) {
            return;
        }
        this.mPowerSave = mPowerSave;
        this.mAodPowerSave = this.mPowerManager.getPowerSaveState(14).batterySaverEnabled;
        if (BatteryControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Power save is ");
            String str;
            if (this.mPowerSave) {
                str = "on";
            }
            else {
                str = "off";
            }
            sb.append(str);
            Log.d("BatteryController", sb.toString());
        }
        this.firePowerSaveChanged();
    }
    
    private void updateEstimate() {
        final Estimate cachedEstimateIfAvailable = Estimate.getCachedEstimateIfAvailable(this.mContext);
        this.mEstimate = cachedEstimateIfAvailable;
        if (cachedEstimateIfAvailable == null) {
            final Estimate estimate = this.mEstimates.getEstimate();
            if ((this.mEstimate = estimate) != null) {
                Estimate.storeCachedEstimate(this.mContext, estimate);
            }
        }
    }
    
    private void updateEstimateInBackground() {
        if (this.mFetchingEstimate) {
            return;
        }
        this.mFetchingEstimate = true;
        this.mBgHandler.post((Runnable)new _$$Lambda$BatteryControllerImpl$Q2m5_jQFbUIrN5_x5MkihyCoos8(this));
    }
    
    private void updatePowerSave() {
        this.setPowerSave(this.mPowerManager.isPowerSaveMode());
    }
    
    public void addCallback(final BatteryStateChangeCallback e) {
        synchronized (this.mChangeCallbacks) {
            this.mChangeCallbacks.add(e);
            // monitorexit(this.mChangeCallbacks)
            if (!this.mHasReceivedBattery) {
                return;
            }
            e.onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
            e.onPowerSaveChanged(this.mPowerSave);
        }
    }
    
    public void dispatchDemoCommand(String string, final Bundle bundle) {
        if (!this.mDemoMode && string.equals("enter")) {
            this.mDemoMode = true;
            this.mBroadcastDispatcher.unregisterReceiver(this);
        }
        else if (this.mDemoMode && string.equals("exit")) {
            this.mDemoMode = false;
            this.registerReceiver();
            this.updatePowerSave();
        }
        else if (this.mDemoMode && string.equals("battery")) {
            string = bundle.getString("level");
            final String string2 = bundle.getString("plugged");
            final String string3 = bundle.getString("powersave");
            if (string != null) {
                this.mLevel = Math.min(Math.max(Integer.parseInt(string), 0), 100);
            }
            if (string2 != null) {
                this.mPluggedIn = Boolean.parseBoolean(string2);
            }
            if (string3 != null) {
                this.mPowerSave = string3.equals("true");
                this.firePowerSaveChanged();
            }
            this.fireBatteryLevelChanged();
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("BatteryController state:");
        printWriter.print("  mLevel=");
        printWriter.println(this.mLevel);
        printWriter.print("  mPluggedIn=");
        printWriter.println(this.mPluggedIn);
        printWriter.print("  mCharging=");
        printWriter.println(this.mCharging);
        printWriter.print("  mCharged=");
        printWriter.println(this.mCharged);
        printWriter.print("  mPowerSave=");
        printWriter.println(this.mPowerSave);
    }
    
    protected void fireBatteryLevelChanged() {
        synchronized (this.mChangeCallbacks) {
            for (int size = this.mChangeCallbacks.size(), i = 0; i < size; ++i) {
                this.mChangeCallbacks.get(i).onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
            }
        }
    }
    
    public void getEstimatedTimeRemainingString(final EstimateFetchCompletion e) {
        synchronized (this.mFetchCallbacks) {
            this.mFetchCallbacks.add(e);
            // monitorexit(this.mFetchCallbacks)
            this.updateEstimateInBackground();
        }
    }
    
    public boolean isAodPowerSave() {
        return this.mAodPowerSave;
    }
    
    public boolean isPowerSave() {
        return this.mPowerSave;
    }
    
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        final boolean equals = action.equals("android.intent.action.BATTERY_CHANGED");
        final boolean b = true;
        if (equals) {
            if (this.mTestmode && !intent.getBooleanExtra("testmode", false)) {
                return;
            }
            this.mHasReceivedBattery = true;
            this.mLevel = (int)(intent.getIntExtra("level", 0) * 100.0f / intent.getIntExtra("scale", 100));
            this.mPluggedIn = (intent.getIntExtra("plugged", 0) != 0);
            final int intExtra = intent.getIntExtra("status", 1);
            final boolean mCharged = intExtra == 5;
            this.mCharged = mCharged;
            boolean mCharging = b;
            if (!mCharged) {
                mCharging = (intExtra == 2 && b);
            }
            this.mCharging = mCharging;
            this.fireBatteryLevelChanged();
        }
        else if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGED")) {
            this.updatePowerSave();
        }
        else if (action.equals("com.android.systemui.BATTERY_LEVEL_TEST")) {
            this.mTestmode = true;
            this.mMainHandler.post((Runnable)new Runnable() {
                int curLevel = 0;
                Intent dummy;
                int incr = 1;
                int saveLevel;
                boolean savePlugged;
                
                {
                    BatteryControllerImpl.this = BatteryControllerImpl.this;
                    this.saveLevel = BatteryControllerImpl.this.mLevel;
                    this.savePlugged = BatteryControllerImpl.this.mPluggedIn;
                    this.dummy = new Intent("android.intent.action.BATTERY_CHANGED");
                }
                
                @Override
                public void run() {
                    final int curLevel = this.curLevel;
                    int n = 0;
                    if (curLevel < 0) {
                        BatteryControllerImpl.this.mTestmode = false;
                        this.dummy.putExtra("level", this.saveLevel);
                        this.dummy.putExtra("plugged", this.savePlugged);
                        this.dummy.putExtra("testmode", false);
                    }
                    else {
                        this.dummy.putExtra("level", curLevel);
                        final Intent dummy = this.dummy;
                        if (this.incr > 0) {
                            n = 1;
                        }
                        dummy.putExtra("plugged", n);
                        this.dummy.putExtra("testmode", true);
                    }
                    context.sendBroadcast(this.dummy);
                    if (!BatteryControllerImpl.this.mTestmode) {
                        return;
                    }
                    final int curLevel2 = this.curLevel;
                    final int incr = this.incr;
                    if ((this.curLevel = curLevel2 + incr) == 100) {
                        this.incr = incr * -1;
                    }
                    BatteryControllerImpl.this.mMainHandler.postDelayed((Runnable)this, 200L);
                }
            });
        }
    }
    
    public void removeCallback(final BatteryStateChangeCallback o) {
        synchronized (this.mChangeCallbacks) {
            this.mChangeCallbacks.remove(o);
        }
    }
    
    public void setPowerSaveMode(final boolean b) {
        BatterySaverUtils.setPowerSaveMode(this.mContext, b, true);
    }
}
