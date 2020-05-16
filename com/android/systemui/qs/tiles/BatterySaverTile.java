// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.widget.Switch;
import com.android.systemui.R$string;
import android.content.Intent;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.SecureSetting;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class BatterySaverTile extends QSTileImpl<BooleanState> implements BatteryStateChangeCallback
{
    private final BatteryController mBatteryController;
    private Icon mIcon;
    private boolean mPluggedIn;
    private boolean mPowerSave;
    private final SecureSetting mSetting;
    
    public BatterySaverTile(final QSHost qsHost, final BatteryController mBatteryController) {
        super(qsHost);
        this.mIcon = ResourceIcon.get(17302795);
        (this.mBatteryController = mBatteryController).observe(this.getLifecycle(), (BatteryController.BatteryStateChangeCallback)this);
        this.mSetting = new SecureSetting(super.mContext, super.mHandler, "low_power_warning_acknowledged") {
            @Override
            protected void handleValueChanged(final int n, final boolean b) {
                QSTileImpl.this.handleRefreshState(null);
            }
        };
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.intent.action.POWER_USAGE_SUMMARY");
    }
    
    @Override
    public int getMetricsCategory() {
        return 261;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.battery_detail_switch_title);
    }
    
    @Override
    protected void handleClick() {
        if (this.getState().state == 0) {
            return;
        }
        this.mBatteryController.setPowerSaveMode(this.mPowerSave ^ true);
    }
    
    @Override
    protected void handleDestroy() {
        super.handleDestroy();
        this.mSetting.setListening(false);
    }
    
    public void handleSetListening(final boolean listening) {
        super.handleSetListening(listening);
        this.mSetting.setListening(listening);
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        final boolean mPluggedIn = this.mPluggedIn;
        boolean showRippleEffect = true;
        int state;
        if (mPluggedIn) {
            state = 0;
        }
        else if (this.mPowerSave) {
            state = 2;
        }
        else {
            state = 1;
        }
        booleanState.state = state;
        booleanState.icon = this.mIcon;
        final String string = super.mContext.getString(R$string.battery_detail_switch_title);
        booleanState.label = string;
        booleanState.contentDescription = string;
        booleanState.value = this.mPowerSave;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        if (this.mSetting.getValue() != 0) {
            showRippleEffect = false;
        }
        booleanState.showRippleEffect = showRippleEffect;
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
    
    @Override
    public void onBatteryLevelChanged(final int i, final boolean mPluggedIn, final boolean b) {
        this.mPluggedIn = mPluggedIn;
        this.refreshState(i);
    }
    
    @Override
    public void onPowerSaveChanged(final boolean mPowerSave) {
        this.mPowerSave = mPowerSave;
        this.refreshState(null);
    }
}
