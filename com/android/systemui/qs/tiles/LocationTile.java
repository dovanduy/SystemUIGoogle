// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.widget.Switch;
import android.content.Intent;
import com.android.systemui.R$string;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.R$drawable;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class LocationTile extends QSTileImpl<BooleanState>
{
    private final ActivityStarter mActivityStarter;
    private final Callback mCallback;
    private final LocationController mController;
    private final Icon mIcon;
    private final KeyguardStateController mKeyguard;
    
    public LocationTile(final QSHost qsHost, final LocationController mController, final KeyguardStateController mKeyguard, final ActivityStarter mActivityStarter) {
        super(qsHost);
        this.mIcon = ResourceIcon.get(R$drawable.ic_location);
        final Callback mCallback = new Callback();
        this.mCallback = mCallback;
        this.mController = mController;
        this.mKeyguard = mKeyguard;
        this.mActivityStarter = mActivityStarter;
        mController.observe(this, (LocationController.LocationChangeCallback)mCallback);
        this.mKeyguard.observe(this, (KeyguardStateController.Callback)this.mCallback);
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (((BooleanState)super.mState).value) {
            return super.mContext.getString(R$string.accessibility_quick_settings_location_changed_on);
        }
        return super.mContext.getString(R$string.accessibility_quick_settings_location_changed_off);
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 122;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_location_label);
    }
    
    @Override
    protected void handleClick() {
        if (this.mKeyguard.isMethodSecure() && this.mKeyguard.isShowing()) {
            this.mActivityStarter.postQSRunnableDismissingKeyguard(new _$$Lambda$LocationTile$cnlxD4jGztrpcRYGbQTKRSm3Ng0(this));
            return;
        }
        this.mController.setLocationEnabled(((BooleanState)super.mState).value ^ true);
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        booleanState.value = this.mController.isLocationEnabled();
        this.checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_share_location");
        if (!booleanState.disabledByPolicy) {
            this.checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_config_location");
        }
        booleanState.icon = this.mIcon;
        final SlashState slash = booleanState.slash;
        final boolean value = booleanState.value;
        int state = 1;
        slash.isSlashed = (value ^ true);
        final String string = super.mContext.getString(R$string.quick_settings_location_label);
        booleanState.label = string;
        booleanState.contentDescription = string;
        if (booleanState.value) {
            state = 2;
        }
        booleanState.state = state;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
    
    private final class Callback implements LocationChangeCallback, KeyguardStateController.Callback
    {
        @Override
        public void onKeyguardShowingChanged() {
            LocationTile.this.refreshState();
        }
        
        @Override
        public void onLocationSettingsChanged(final boolean b) {
            LocationTile.this.refreshState();
        }
    }
}
