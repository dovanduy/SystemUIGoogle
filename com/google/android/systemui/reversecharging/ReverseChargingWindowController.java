// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.reversecharging;

import com.android.settingslib.utils.ThreadUtils;
import com.android.systemui.R$id;
import androidx.lifecycle.Lifecycle;
import com.android.systemui.R$string;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import androidx.lifecycle.LifecycleRegistry;
import com.google.android.systemui.ambientmusic.AmbientIndicationContainer;
import com.android.systemui.statusbar.policy.BatteryController;
import androidx.lifecycle.LifecycleOwner;

public class ReverseChargingWindowController implements LifecycleOwner, BatteryStateChangeCallback
{
    private AmbientIndicationContainer mAmbientIndicationContainer;
    private final BatteryController mBatteryController;
    private final String mDisplayText;
    private int mLevel;
    private final LifecycleRegistry mLifecycle;
    private boolean mReverse;
    private final Lazy<StatusBar> mStatusBarLazy;
    
    public ReverseChargingWindowController(final Context context, final BatteryController mBatteryController, final Lazy<StatusBar> mStatusBarLazy) {
        this.mLifecycle = new LifecycleRegistry(this);
        this.mBatteryController = mBatteryController;
        this.mStatusBarLazy = mStatusBarLazy;
        this.mDisplayText = context.getResources().getString(R$string.charging_reverse_text);
    }
    
    private void updateWindowVisibility() {
        final AmbientIndicationContainer mAmbientIndicationContainer = this.mAmbientIndicationContainer;
        if (mAmbientIndicationContainer != null) {
            String mDisplayText;
            if (this.mReverse && this.mLevel >= 0) {
                mDisplayText = this.mDisplayText;
            }
            else {
                mDisplayText = "";
            }
            mAmbientIndicationContainer.setReverseChargingMessage(mDisplayText);
        }
    }
    
    @Override
    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }
    
    public void initialize() {
        this.mBatteryController.observe(this.mLifecycle, (BatteryController.BatteryStateChangeCallback)this);
        this.mLifecycle.markState(Lifecycle.State.RESUMED);
        this.mAmbientIndicationContainer = (AmbientIndicationContainer)this.mStatusBarLazy.get().getNotificationShadeWindowView().findViewById(R$id.ambient_indication_container);
    }
    
    @Override
    public void onReverseChanged(final boolean mReverse, final int mLevel, final String s) {
        this.mReverse = mReverse;
        this.mLevel = mLevel;
        ThreadUtils.postOnMainThread(new _$$Lambda$ReverseChargingWindowController$4ft8vPnS3Jw_B_aZQavUxlpiGV0(this));
    }
}
