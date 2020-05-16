// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.qs.tiles;

import android.widget.Switch;
import com.android.systemui.R$string;
import android.content.Intent;
import com.android.systemui.R$drawable;
import com.android.systemui.qs.QSHost;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class ReverseChargingTile extends QSTileImpl<BooleanState> implements BatteryStateChangeCallback
{
    @VisibleForTesting
    private BatteryController mBatteryController;
    private Icon mIcon;
    private boolean mReverseChargingOn;
    
    public ReverseChargingTile(final QSHost qsHost, final BatteryController mBatteryController) {
        super(qsHost);
        this.mReverseChargingOn = false;
        this.mIcon = ResourceIcon.get(R$drawable.ic_qs_reverse_charging);
        (this.mBatteryController = mBatteryController).observe(this.getLifecycle(), (BatteryController.BatteryStateChangeCallback)this);
    }
    
    @Override
    public Intent getLongClickIntent() {
        final Intent intent = new Intent("android.settings.REVERSE_CHARGING_SETTINGS");
        intent.setPackage("com.android.settings");
        return intent;
    }
    
    @Override
    public int getMetricsCategory() {
        return 0;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.reverse_charging_title);
    }
    
    @Override
    protected void handleClick() {
        final boolean b = this.mReverseChargingOn ^ true;
        this.mReverseChargingOn = b;
        this.mBatteryController.setReverseState(b);
    }
    
    @Override
    protected void handleDestroy() {
        super.handleDestroy();
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        final boolean mReverseChargingOn = this.mReverseChargingOn;
        booleanState.value = mReverseChargingOn;
        int state;
        if (mReverseChargingOn) {
            state = 2;
        }
        else {
            state = 1;
        }
        booleanState.state = state;
        booleanState.icon = this.mIcon;
        final CharSequence tileLabel = this.getTileLabel();
        booleanState.label = tileLabel;
        booleanState.contentDescription = tileLabel;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }
    
    @Override
    public boolean isAvailable() {
        return this.mBatteryController.isReverseSupported();
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
    
    @Override
    public void onBatteryLevelChanged(final int n, final boolean b, final boolean b2) {
    }
    
    @Override
    public void onPowerSaveChanged(final boolean b) {
    }
    
    @Override
    public void onReverseChanged(final boolean mReverseChargingOn, final int n, final String s) {
        this.mReverseChargingOn = mReverseChargingOn;
        this.refreshState(null);
    }
}
