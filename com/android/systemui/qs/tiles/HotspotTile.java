// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.util.Log;
import android.widget.Switch;
import android.content.Intent;
import com.android.systemui.R$plurals;
import com.android.systemui.R$string;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.R$drawable;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class HotspotTile extends QSTileImpl<BooleanState>
{
    private final HotspotAndDataSaverCallbacks mCallbacks;
    private final DataSaverController mDataSaverController;
    private final Icon mEnabledStatic;
    private final HotspotController mHotspotController;
    private boolean mListening;
    
    public HotspotTile(final QSHost qsHost, final HotspotController mHotspotController, final DataSaverController mDataSaverController) {
        super(qsHost);
        this.mEnabledStatic = ResourceIcon.get(R$drawable.ic_hotspot);
        final HotspotAndDataSaverCallbacks mCallbacks = new HotspotAndDataSaverCallbacks();
        this.mCallbacks = mCallbacks;
        this.mHotspotController = mHotspotController;
        this.mDataSaverController = mDataSaverController;
        mHotspotController.observe(this, (HotspotController.Callback)mCallbacks);
        this.mDataSaverController.observe(this, (DataSaverController.Listener)this.mCallbacks);
    }
    
    private String getSecondaryLabel(final boolean b, final boolean b2, final boolean b3, final int i) {
        if (b2) {
            return super.mContext.getString(R$string.quick_settings_hotspot_secondary_label_transient);
        }
        if (b3) {
            return super.mContext.getString(R$string.quick_settings_hotspot_secondary_label_data_saver_enabled);
        }
        if (i > 0 && b) {
            return super.mContext.getResources().getQuantityString(R$plurals.quick_settings_hotspot_secondary_label_num_devices, i, new Object[] { i });
        }
        return null;
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (((BooleanState)super.mState).value) {
            return super.mContext.getString(R$string.accessibility_quick_settings_hotspot_changed_on);
        }
        return super.mContext.getString(R$string.accessibility_quick_settings_hotspot_changed_off);
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.TETHER_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 120;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_hotspot_label);
    }
    
    @Override
    protected void handleClick() {
        final boolean value = ((BooleanState)super.mState).value;
        if (!value && this.mDataSaverController.isDataSaverEnabled()) {
            return;
        }
        Object arg_SHOW_TRANSIENT_ENABLING;
        if (value) {
            arg_SHOW_TRANSIENT_ENABLING = null;
        }
        else {
            arg_SHOW_TRANSIENT_ENABLING = QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        }
        this.refreshState(arg_SHOW_TRANSIENT_ENABLING);
        this.mHotspotController.setHotspotEnabled(value ^ true);
    }
    
    @Override
    protected void handleDestroy() {
        super.handleDestroy();
    }
    
    public void handleSetListening(final boolean mListening) {
        super.handleSetListening(mListening);
        if (this.mListening == mListening) {
            return;
        }
        this.mListening = mListening;
        if (mListening) {
            this.refreshState();
        }
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        final Object arg_SHOW_TRANSIENT_ENABLING = QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        int state = 1;
        final boolean b = o == arg_SHOW_TRANSIENT_ENABLING;
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        final boolean isTransient = b || this.mHotspotController.isHotspotTransient();
        this.checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_config_tethering");
        int n;
        boolean b2;
        if (o instanceof CallbackInfo) {
            final CallbackInfo callbackInfo = (CallbackInfo)o;
            booleanState.value = (b || callbackInfo.isHotspotEnabled);
            n = callbackInfo.numConnectedDevices;
            b2 = callbackInfo.isDataSaverEnabled;
        }
        else {
            booleanState.value = (b || this.mHotspotController.isHotspotEnabled());
            n = this.mHotspotController.getNumConnectedDevices();
            b2 = this.mDataSaverController.isDataSaverEnabled();
        }
        booleanState.icon = this.mEnabledStatic;
        booleanState.label = super.mContext.getString(R$string.quick_settings_hotspot_label);
        booleanState.isTransient = isTransient;
        booleanState.slash.isSlashed = (!booleanState.value && !isTransient);
        if (booleanState.isTransient) {
            booleanState.icon = ResourceIcon.get(17302439);
        }
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.contentDescription = booleanState.label;
        final boolean b3 = booleanState.value || booleanState.isTransient;
        if (b2) {
            booleanState.state = 0;
        }
        else {
            if (b3) {
                state = 2;
            }
            booleanState.state = state;
        }
        final String secondaryLabel = this.getSecondaryLabel(b3, isTransient, b2, n);
        booleanState.secondaryLabel = secondaryLabel;
        booleanState.stateDescription = secondaryLabel;
    }
    
    @Override
    public boolean isAvailable() {
        return this.mHotspotController.isHotspotSupported();
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
    
    protected static final class CallbackInfo
    {
        boolean isDataSaverEnabled;
        boolean isHotspotEnabled;
        int numConnectedDevices;
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CallbackInfo[");
            sb.append("isHotspotEnabled=");
            sb.append(this.isHotspotEnabled);
            sb.append(",numConnectedDevices=");
            sb.append(this.numConnectedDevices);
            sb.append(",isDataSaverEnabled=");
            sb.append(this.isDataSaverEnabled);
            sb.append(']');
            return sb.toString();
        }
    }
    
    private final class HotspotAndDataSaverCallbacks implements HotspotController.Callback, Listener
    {
        CallbackInfo mCallbackInfo;
        
        private HotspotAndDataSaverCallbacks() {
            this.mCallbackInfo = new CallbackInfo();
        }
        
        @Override
        public void onDataSaverChanged(final boolean isDataSaverEnabled) {
            final CallbackInfo mCallbackInfo = this.mCallbackInfo;
            mCallbackInfo.isDataSaverEnabled = isDataSaverEnabled;
            QSTileImpl.this.refreshState(mCallbackInfo);
        }
        
        @Override
        public void onHotspotAvailabilityChanged(final boolean b) {
            if (!b) {
                Log.d(HotspotTile.this.TAG, "Tile removed. Hotspot no longer available");
                HotspotTile.this.mHost.removeTile(HotspotTile.this.getTileSpec());
            }
        }
        
        @Override
        public void onHotspotChanged(final boolean isHotspotEnabled, final int numConnectedDevices) {
            final CallbackInfo mCallbackInfo = this.mCallbackInfo;
            mCallbackInfo.isHotspotEnabled = isHotspotEnabled;
            mCallbackInfo.numConnectedDevices = numConnectedDevices;
            QSTileImpl.this.refreshState(mCallbackInfo);
        }
    }
}
