// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.widget.Switch;
import com.android.systemui.R$drawable;
import android.content.DialogInterface$OnClickListener;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.Prefs;
import android.content.Intent;
import com.android.systemui.R$string;
import android.content.DialogInterface;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class DataSaverTile extends QSTileImpl<BooleanState> implements Listener
{
    private final DataSaverController mDataSaverController;
    
    public DataSaverTile(final QSHost qsHost, final NetworkController networkController) {
        super(qsHost);
        (this.mDataSaverController = networkController.getDataSaverController()).observe(this.getLifecycle(), (DataSaverController.Listener)this);
    }
    
    private void toggleDataSaver() {
        ((BooleanState)super.mState).value = (this.mDataSaverController.isDataSaverEnabled() ^ true);
        this.mDataSaverController.setDataSaverEnabled(((BooleanState)super.mState).value);
        this.refreshState(((BooleanState)super.mState).value);
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (((BooleanState)super.mState).value) {
            return super.mContext.getString(R$string.accessibility_quick_settings_data_saver_changed_on);
        }
        return super.mContext.getString(R$string.accessibility_quick_settings_data_saver_changed_off);
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.DATA_SAVER_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 284;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.data_saver);
    }
    
    @Override
    protected void handleClick() {
        if (!((BooleanState)super.mState).value && !Prefs.getBoolean(super.mContext, "QsDataSaverDialogShown", false)) {
            final SystemUIDialog systemUIDialog = new SystemUIDialog(super.mContext);
            systemUIDialog.setTitle(17039995);
            systemUIDialog.setMessage(17039993);
            systemUIDialog.setPositiveButton(17039994, (DialogInterface$OnClickListener)new _$$Lambda$DataSaverTile$7vpE4nfIgph7ByTloh1_igU2EhI(this));
            systemUIDialog.setNegativeButton(17039360, null);
            systemUIDialog.setShowForAllUsers(true);
            systemUIDialog.show();
            Prefs.putBoolean(super.mContext, "QsDataSaverDialogShown", true);
            return;
        }
        this.toggleDataSaver();
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        boolean value;
        if (o instanceof Boolean) {
            value = (boolean)o;
        }
        else {
            value = this.mDataSaverController.isDataSaverEnabled();
        }
        booleanState.value = value;
        int state;
        if (value) {
            state = 2;
        }
        else {
            state = 1;
        }
        booleanState.state = state;
        final String string = super.mContext.getString(R$string.data_saver);
        booleanState.label = string;
        booleanState.contentDescription = string;
        int n;
        if (booleanState.value) {
            n = R$drawable.ic_data_saver;
        }
        else {
            n = R$drawable.ic_data_saver_off;
        }
        booleanState.icon = ResourceIcon.get(n);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
    
    @Override
    public void onDataSaverChanged(final boolean b) {
        this.refreshState(b);
    }
}
