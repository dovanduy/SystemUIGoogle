// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.widget.Switch;
import com.android.systemui.R$drawable;
import android.content.IntentFilter;
import com.android.systemui.R$string;
import android.content.Intent;
import android.content.Context;
import com.android.systemui.qs.QSHost;
import android.content.BroadcastReceiver;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.nfc.NfcAdapter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class NfcTile extends QSTileImpl<BooleanState>
{
    private NfcAdapter mAdapter;
    private BroadcastDispatcher mBroadcastDispatcher;
    private boolean mListening;
    private BroadcastReceiver mNfcReceiver;
    
    public NfcTile(final QSHost qsHost, final BroadcastDispatcher mBroadcastDispatcher) {
        super(qsHost);
        this.mNfcReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                NfcTile.this.refreshState();
            }
        };
        this.mBroadcastDispatcher = mBroadcastDispatcher;
    }
    
    private NfcAdapter getAdapter() {
        if (this.mAdapter == null) {
            try {
                this.mAdapter = NfcAdapter.getDefaultAdapter(super.mContext);
            }
            catch (UnsupportedOperationException ex) {
                this.mAdapter = null;
            }
        }
        return this.mAdapter;
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (((BooleanState)super.mState).value) {
            return super.mContext.getString(R$string.quick_settings_nfc_on);
        }
        return super.mContext.getString(R$string.quick_settings_nfc_off);
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.NFC_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 800;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_nfc_label);
    }
    
    @Override
    protected void handleClick() {
        if (this.getAdapter() == null) {
            return;
        }
        if (!this.getAdapter().isEnabled()) {
            this.getAdapter().enable();
        }
        else {
            this.getAdapter().disable();
        }
    }
    
    @Override
    protected void handleSecondaryClick() {
        this.handleClick();
    }
    
    public void handleSetListening(final boolean mListening) {
        super.handleSetListening(mListening);
        this.mListening = mListening;
        if (mListening) {
            this.mBroadcastDispatcher.registerReceiver(this.mNfcReceiver, new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED"));
        }
        else {
            this.mBroadcastDispatcher.unregisterReceiver(this.mNfcReceiver);
        }
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        final NfcAdapter adapter = this.getAdapter();
        int state = 1;
        booleanState.value = (adapter != null && this.getAdapter().isEnabled());
        if (this.getAdapter() == null) {
            state = 0;
        }
        else if (booleanState.value) {
            state = 2;
        }
        booleanState.state = state;
        int n;
        if (booleanState.value) {
            n = R$drawable.ic_qs_nfc_enabled;
        }
        else {
            n = R$drawable.ic_qs_nfc_disabled;
        }
        booleanState.icon = ResourceIcon.get(n);
        booleanState.label = super.mContext.getString(R$string.quick_settings_nfc_label);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.contentDescription = booleanState.label;
    }
    
    @Override
    protected void handleUserSwitch(final int n) {
    }
    
    @Override
    public boolean isAvailable() {
        return super.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc");
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
}
