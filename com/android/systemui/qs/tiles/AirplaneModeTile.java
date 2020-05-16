// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.widget.Switch;
import android.content.IntentFilter;
import android.sysprop.TelephonyProperties;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$string;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.content.Intent;
import android.content.Context;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.GlobalSetting;
import android.content.BroadcastReceiver;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class AirplaneModeTile extends QSTileImpl<BooleanState>
{
    private final ActivityStarter mActivityStarter;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Icon mIcon;
    private boolean mListening;
    private final BroadcastReceiver mReceiver;
    private final GlobalSetting mSetting;
    
    public AirplaneModeTile(final QSHost qsHost, final ActivityStarter mActivityStarter, final BroadcastDispatcher mBroadcastDispatcher) {
        super(qsHost);
        this.mIcon = ResourceIcon.get(17302793);
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("android.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
                    AirplaneModeTile.this.refreshState();
                }
            }
        };
        this.mActivityStarter = mActivityStarter;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mSetting = new GlobalSetting(super.mContext, super.mHandler, "airplane_mode_on") {
            @Override
            protected void handleValueChanged(final int i) {
                QSTileImpl.this.handleRefreshState(i);
            }
        };
    }
    
    private void setEnabled(final boolean airplaneMode) {
        ((ConnectivityManager)super.mContext.getSystemService("connectivity")).setAirplaneMode(airplaneMode);
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (((BooleanState)super.mState).value) {
            return super.mContext.getString(R$string.accessibility_quick_settings_airplane_changed_on);
        }
        return super.mContext.getString(R$string.accessibility_quick_settings_airplane_changed_off);
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 112;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.airplane_mode);
    }
    
    public void handleClick() {
        final boolean value = ((BooleanState)super.mState).value;
        MetricsLogger.action(super.mContext, this.getMetricsCategory(), value ^ true);
        if (!value && TelephonyProperties.in_ecm_mode().orElse(Boolean.FALSE)) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS"), 0);
            return;
        }
        this.setEnabled(value ^ true);
    }
    
    public void handleSetListening(final boolean b) {
        super.handleSetListening(b);
        if (this.mListening == b) {
            return;
        }
        this.mListening = b;
        if (b) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
            this.mBroadcastDispatcher.registerReceiver(this.mReceiver, intentFilter);
        }
        else {
            this.mBroadcastDispatcher.unregisterReceiver(this.mReceiver);
        }
        this.mSetting.setListening(b);
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        this.checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_airplane_mode");
        int n;
        if (o instanceof Integer) {
            n = (int)o;
        }
        else {
            n = this.mSetting.getValue();
        }
        final int n2 = 1;
        final boolean value = n != 0;
        booleanState.value = value;
        booleanState.label = super.mContext.getString(R$string.airplane_mode);
        booleanState.icon = this.mIcon;
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        booleanState.slash.isSlashed = (value ^ true);
        int state = n2;
        if (value) {
            state = 2;
        }
        booleanState.state = state;
        booleanState.contentDescription = booleanState.label;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
}
