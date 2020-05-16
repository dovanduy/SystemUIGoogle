// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.widget.Switch;
import android.content.Intent;
import com.android.systemui.R$string;
import com.android.systemui.R$drawable;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class WorkModeTile extends QSTileImpl<BooleanState> implements ManagedProfileController.Callback
{
    private final Icon mIcon;
    private final ManagedProfileController mProfileController;
    
    public WorkModeTile(final QSHost qsHost, final ManagedProfileController mProfileController) {
        super(qsHost);
        this.mIcon = ResourceIcon.get(R$drawable.stat_sys_managed_profile_status);
        (this.mProfileController = mProfileController).observe(this.getLifecycle(), (ManagedProfileController.Callback)this);
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (((BooleanState)super.mState).value) {
            return super.mContext.getString(R$string.accessibility_quick_settings_work_mode_changed_on);
        }
        return super.mContext.getString(R$string.accessibility_quick_settings_work_mode_changed_off);
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.MANAGED_PROFILE_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 257;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_work_mode_label);
    }
    
    public void handleClick() {
        this.mProfileController.setWorkModeEnabled(((BooleanState)super.mState).value ^ true);
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        if (!this.isAvailable()) {
            this.onManagedProfileRemoved();
        }
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        if (o instanceof Boolean) {
            booleanState.value = (boolean)o;
        }
        else {
            booleanState.value = this.mProfileController.isWorkModeEnabled();
        }
        booleanState.icon = this.mIcon;
        final boolean value = booleanState.value;
        int state = 1;
        if (value) {
            booleanState.slash.isSlashed = false;
        }
        else {
            booleanState.slash.isSlashed = true;
        }
        final String string = super.mContext.getString(R$string.quick_settings_work_mode_label);
        booleanState.label = string;
        booleanState.contentDescription = string;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        if (booleanState.value) {
            state = 2;
        }
        booleanState.state = state;
    }
    
    @Override
    public boolean isAvailable() {
        return this.mProfileController.hasActiveProfile();
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
    
    @Override
    public void onManagedProfileChanged() {
        this.refreshState(this.mProfileController.isWorkModeEnabled());
    }
    
    @Override
    public void onManagedProfileRemoved() {
        super.mHost.removeTile(this.getTileSpec());
        super.mHost.unmarkTileAsAutoAdded(this.getTileSpec());
    }
}
