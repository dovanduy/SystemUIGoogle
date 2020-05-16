// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import java.time.LocalTime;
import android.content.res.Resources;
import android.widget.Switch;
import android.text.TextUtils;
import java.time.temporal.TemporalAccessor;
import android.text.format.DateFormat;
import com.android.systemui.R$string;
import android.content.Intent;
import com.android.systemui.qs.QSHost;
import android.app.UiModeManager;
import java.time.format.DateTimeFormatter;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class UiModeNightTile extends QSTileImpl<BooleanState> implements ConfigurationListener, BatteryStateChangeCallback
{
    public static DateTimeFormatter formatter;
    private final BatteryController mBatteryController;
    private final Icon mIcon;
    private final UiModeManager mUiModeManager;
    
    static {
        UiModeNightTile.formatter = DateTimeFormatter.ofPattern("hh:mm a");
    }
    
    public UiModeNightTile(final QSHost qsHost, final ConfigurationController configurationController, final BatteryController mBatteryController) {
        super(qsHost);
        this.mIcon = ResourceIcon.get(17302800);
        this.mBatteryController = mBatteryController;
        this.mUiModeManager = (UiModeManager)super.mContext.getSystemService((Class)UiModeManager.class);
        configurationController.observe(this.getLifecycle(), (ConfigurationController.ConfigurationListener)this);
        mBatteryController.observe(this.getLifecycle(), (BatteryController.BatteryStateChangeCallback)this);
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.DARK_THEME_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 1706;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return this.getState().label;
    }
    
    @Override
    protected void handleClick() {
        if (this.getState().state == 0) {
            return;
        }
        final boolean b = ((BooleanState)super.mState).value ^ true;
        this.mUiModeManager.setNightModeActivated(b);
        this.refreshState(b);
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        final int nightMode = this.mUiModeManager.getNightMode();
        final boolean powerSave = this.mBatteryController.isPowerSave();
        final int uiMode = super.mContext.getResources().getConfiguration().uiMode;
        final int n = 1;
        final boolean value = (uiMode & 0x30) == 0x20;
        if (powerSave) {
            booleanState.secondaryLabel = super.mContext.getResources().getString(R$string.quick_settings_dark_mode_secondary_label_battery_saver);
        }
        else if (nightMode == 0) {
            final Resources resources = super.mContext.getResources();
            int n2;
            if (value) {
                n2 = R$string.quick_settings_dark_mode_secondary_label_until_sunrise;
            }
            else {
                n2 = R$string.quick_settings_dark_mode_secondary_label_on_at_sunset;
            }
            booleanState.secondaryLabel = resources.getString(n2);
        }
        else if (nightMode == 3) {
            final boolean is24HourFormat = DateFormat.is24HourFormat(super.mContext);
            LocalTime temporal;
            if (value) {
                temporal = this.mUiModeManager.getCustomNightModeEnd();
            }
            else {
                temporal = this.mUiModeManager.getCustomNightModeStart();
            }
            final Resources resources2 = super.mContext.getResources();
            int n3;
            if (value) {
                n3 = R$string.quick_settings_dark_mode_secondary_label_until;
            }
            else {
                n3 = R$string.quick_settings_dark_mode_secondary_label_on_at;
            }
            String s;
            if (is24HourFormat) {
                s = temporal.toString();
            }
            else {
                s = UiModeNightTile.formatter.format(temporal);
            }
            booleanState.secondaryLabel = resources2.getString(n3, new Object[] { s });
        }
        else {
            booleanState.secondaryLabel = null;
        }
        booleanState.value = value;
        booleanState.label = super.mContext.getString(R$string.quick_settings_ui_mode_night_label);
        booleanState.icon = this.mIcon;
        CharSequence contentDescription;
        if (TextUtils.isEmpty(booleanState.secondaryLabel)) {
            contentDescription = booleanState.label;
        }
        else {
            contentDescription = TextUtils.concat(new CharSequence[] { booleanState.label, ", ", booleanState.secondaryLabel });
        }
        booleanState.contentDescription = contentDescription;
        if (powerSave) {
            booleanState.state = 0;
        }
        else {
            int state = n;
            if (booleanState.value) {
                state = 2;
            }
            booleanState.state = state;
        }
        booleanState.showRippleEffect = false;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
    
    @Override
    public void onPowerSaveChanged(final boolean b) {
        this.refreshState();
    }
    
    @Override
    public void onUiModeChanged() {
        this.refreshState();
    }
}
