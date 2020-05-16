// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.widget.Switch;
import android.content.Intent;
import com.android.systemui.R$string;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.R$drawable;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.SecureSetting;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class ColorInversionTile extends QSTileImpl<BooleanState>
{
    private final Icon mIcon;
    private final SecureSetting mSetting;
    
    public ColorInversionTile(final QSHost qsHost) {
        super(qsHost);
        this.mIcon = ResourceIcon.get(R$drawable.ic_invert_colors);
        this.mSetting = new SecureSetting(super.mContext, super.mHandler, "accessibility_display_inversion_enabled") {
            @Override
            protected void handleValueChanged(final int i, final boolean b) {
                QSTileImpl.this.handleRefreshState(i);
            }
        };
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (((BooleanState)super.mState).value) {
            return super.mContext.getString(R$string.accessibility_quick_settings_color_inversion_changed_on);
        }
        return super.mContext.getString(R$string.accessibility_quick_settings_color_inversion_changed_off);
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.ACCESSIBILITY_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 116;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_inversion_label);
    }
    
    @Override
    protected void handleClick() {
        this.mSetting.setValue((((BooleanState)super.mState).value ^ true) ? 1 : 0);
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
        int n;
        if (o instanceof Integer) {
            n = (int)o;
        }
        else {
            n = this.mSetting.getValue();
        }
        final int n2 = 1;
        final boolean value = n != 0;
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        booleanState.value = value;
        booleanState.slash.isSlashed = (value ^ true);
        int state = n2;
        if (value) {
            state = 2;
        }
        booleanState.state = state;
        booleanState.label = super.mContext.getString(R$string.quick_settings_inversion_label);
        booleanState.icon = this.mIcon;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.contentDescription = booleanState.label;
    }
    
    @Override
    protected void handleUserSwitch(final int userId) {
        this.mSetting.setUserId(userId);
        this.handleRefreshState(this.mSetting.getValue());
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
}
