// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.widget.Switch;
import android.app.ActivityManager;
import android.content.Intent;
import com.android.systemui.R$string;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class FlashlightTile extends QSTileImpl<BooleanState> implements FlashlightListener
{
    private final FlashlightController mFlashlightController;
    private final Icon mIcon;
    
    public FlashlightTile(final QSHost qsHost, final FlashlightController mFlashlightController) {
        super(qsHost);
        this.mIcon = ResourceIcon.get(17302798);
        (this.mFlashlightController = mFlashlightController).observe(this.getLifecycle(), (FlashlightController.FlashlightListener)this);
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (((BooleanState)super.mState).value) {
            return super.mContext.getString(R$string.accessibility_quick_settings_flashlight_changed_on);
        }
        return super.mContext.getString(R$string.accessibility_quick_settings_flashlight_changed_off);
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.media.action.STILL_IMAGE_CAMERA");
    }
    
    @Override
    public int getMetricsCategory() {
        return 119;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_flashlight_label);
    }
    
    @Override
    protected void handleClick() {
        if (ActivityManager.isUserAMonkey()) {
            return;
        }
        final boolean b = ((BooleanState)super.mState).value ^ true;
        this.refreshState(b);
        this.mFlashlightController.setFlashlight(b);
    }
    
    @Override
    protected void handleDestroy() {
        super.handleDestroy();
    }
    
    @Override
    protected void handleLongClick() {
        this.handleClick();
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        booleanState.label = super.mHost.getContext().getString(R$string.quick_settings_flashlight_label);
        booleanState.secondaryLabel = "";
        booleanState.stateDescription = "";
        final boolean available = this.mFlashlightController.isAvailable();
        int state = 1;
        if (!available) {
            booleanState.icon = this.mIcon;
            booleanState.slash.isSlashed = true;
            final String string = super.mContext.getString(R$string.quick_settings_flashlight_camera_in_use);
            booleanState.secondaryLabel = string;
            booleanState.stateDescription = string;
            booleanState.state = 0;
            return;
        }
        if (o instanceof Boolean) {
            final boolean booleanValue = (boolean)o;
            if (booleanValue == booleanState.value) {
                return;
            }
            booleanState.value = booleanValue;
        }
        else {
            booleanState.value = this.mFlashlightController.isEnabled();
        }
        booleanState.icon = this.mIcon;
        booleanState.slash.isSlashed = (booleanState.value ^ true);
        booleanState.contentDescription = super.mContext.getString(R$string.quick_settings_flashlight_label);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        if (booleanState.value) {
            state = 2;
        }
        booleanState.state = state;
    }
    
    @Override
    protected void handleUserSwitch(final int n) {
    }
    
    @Override
    public boolean isAvailable() {
        return this.mFlashlightController.hasFlashlight();
    }
    
    @Override
    public BooleanState newTileState() {
        final BooleanState booleanState = new QSTile.BooleanState();
        booleanState.handlesLongClick = false;
        return booleanState;
    }
    
    @Override
    public void onFlashlightAvailabilityChanged(final boolean b) {
        this.refreshState();
    }
    
    @Override
    public void onFlashlightChanged(final boolean b) {
        this.refreshState(b);
    }
    
    @Override
    public void onFlashlightError() {
        this.refreshState(Boolean.FALSE);
    }
}
