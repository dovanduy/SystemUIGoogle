// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.content.res.Resources;
import com.android.systemui.R$drawable;
import android.widget.Switch;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.qs.SignalTileView;
import com.android.systemui.plugins.qs.QSIconView;
import android.app.AlertDialog;
import android.app.Dialog;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import com.android.systemui.Prefs;
import android.content.DialogInterface;
import android.telephony.SubscriptionManager;
import android.content.Intent;
import com.android.systemui.R$string;
import android.text.Html;
import android.text.TextUtils;
import android.content.Context;
import com.android.systemui.qs.QSHost;
import com.android.settingslib.net.DataUsageController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class CellularTile extends QSTileImpl<SignalState>
{
    private final ActivityStarter mActivityStarter;
    private final NetworkController mController;
    private final DataUsageController mDataController;
    private final CellularDetailAdapter mDetailAdapter;
    private final CellSignalCallback mSignalCallback;
    
    public CellularTile(final QSHost qsHost, final NetworkController mController, final ActivityStarter mActivityStarter) {
        super(qsHost);
        this.mSignalCallback = new CellSignalCallback();
        this.mController = mController;
        this.mActivityStarter = mActivityStarter;
        this.mDataController = mController.getMobileDataController();
        this.mDetailAdapter = new CellularDetailAdapter();
        this.mController.observe(this.getLifecycle(), (NetworkController.SignalCallback)this.mSignalCallback);
    }
    
    private CharSequence appendMobileDataType(final CharSequence charSequence, final CharSequence charSequence2) {
        if (TextUtils.isEmpty(charSequence2)) {
            return (CharSequence)Html.fromHtml(charSequence.toString(), 0);
        }
        if (TextUtils.isEmpty(charSequence)) {
            return (CharSequence)Html.fromHtml(charSequence2.toString(), 0);
        }
        return (CharSequence)Html.fromHtml(super.mContext.getString(R$string.mobile_carrier_text_format, new Object[] { charSequence, charSequence2 }), 0);
    }
    
    static Intent getCellularSettingIntent() {
        final Intent intent = new Intent("android.settings.NETWORK_OPERATOR_SETTINGS");
        if (SubscriptionManager.getDefaultDataSubscriptionId() != -1) {
            intent.putExtra("android.provider.extra.SUB_ID", SubscriptionManager.getDefaultDataSubscriptionId());
        }
        return intent;
    }
    
    private CharSequence getMobileDataContentName(final CallbackInfo callbackInfo) {
        if (callbackInfo.roaming && !TextUtils.isEmpty(callbackInfo.dataContentDescription)) {
            return super.mContext.getString(R$string.mobile_data_text_format, new Object[] { super.mContext.getString(R$string.data_connection_roaming), callbackInfo.dataContentDescription.toString() });
        }
        if (callbackInfo.roaming) {
            return super.mContext.getString(R$string.data_connection_roaming);
        }
        return callbackInfo.dataContentDescription;
    }
    
    private void maybeShowDisableDialog() {
        if (Prefs.getBoolean(super.mContext, "QsHasTurnedOffMobileData", false)) {
            this.mDataController.setMobileDataEnabled(false);
            return;
        }
        String s;
        if (TextUtils.isEmpty((CharSequence)(s = this.mController.getMobileDataNetworkName()))) {
            s = super.mContext.getString(R$string.mobile_data_disable_message_default_carrier);
        }
        final AlertDialog create = new AlertDialog$Builder(super.mContext).setTitle(R$string.mobile_data_disable_title).setMessage((CharSequence)super.mContext.getString(R$string.mobile_data_disable_message, new Object[] { s })).setNegativeButton(17039360, (DialogInterface$OnClickListener)null).setPositiveButton(17039631, (DialogInterface$OnClickListener)new _$$Lambda$CellularTile$oLJGrvqAwKFs9wNM4MvnfZ_a1QQ(this)).create();
        create.getWindow().setType(2009);
        SystemUIDialog.setShowForAllUsers((Dialog)create, true);
        SystemUIDialog.registerDismissListener((Dialog)create);
        SystemUIDialog.setWindowOnTop((Dialog)create);
        create.show();
    }
    
    @Override
    public QSIconView createTileView(final Context context) {
        return new SignalTileView(context);
    }
    
    @Override
    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }
    
    @Override
    public Intent getLongClickIntent() {
        if (this.getState().state == 0) {
            return new Intent("android.settings.WIRELESS_SETTINGS");
        }
        return getCellularSettingIntent();
    }
    
    @Override
    public int getMetricsCategory() {
        return 115;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_cellular_detail_title);
    }
    
    @Override
    protected void handleClick() {
        if (this.getState().state == 0) {
            return;
        }
        if (this.mDataController.isMobileDataEnabled()) {
            this.maybeShowDisableDialog();
        }
        else {
            this.mDataController.setMobileDataEnabled(true);
        }
    }
    
    @Override
    protected void handleSecondaryClick() {
        if (this.mDataController.isMobileDataSupported()) {
            this.showDetail(true);
        }
        else {
            this.mActivityStarter.postStartActivityDismissingKeyguard(getCellularSettingIntent(), 0);
        }
    }
    
    @Override
    protected void handleUpdateState(final SignalState signalState, final Object o) {
        Object access$200;
        if ((access$200 = o) == null) {
            access$200 = this.mSignalCallback.mInfo;
        }
        final Resources resources = super.mContext.getResources();
        signalState.label = resources.getString(R$string.mobile_data);
        final boolean value = this.mDataController.isMobileDataSupported() && this.mDataController.isMobileDataEnabled();
        signalState.value = value;
        signalState.activityIn = (value && ((CallbackInfo)access$200).activityIn);
        signalState.activityOut = (value && ((CallbackInfo)access$200).activityOut);
        signalState.expandedAccessibilityClassName = Switch.class.getName();
        if (((CallbackInfo)access$200).noSim) {
            signalState.icon = ResourceIcon.get(R$drawable.ic_qs_no_sim);
        }
        else {
            signalState.icon = ResourceIcon.get(R$drawable.ic_swap_vert);
        }
        if (((CallbackInfo)access$200).noSim) {
            signalState.state = 0;
            signalState.secondaryLabel = resources.getString(R$string.keyguard_missing_sim_message_short);
        }
        else if (((CallbackInfo)access$200).airplaneModeEnabled) {
            signalState.state = 0;
            signalState.secondaryLabel = resources.getString(R$string.status_bar_airplane);
        }
        else if (value) {
            signalState.state = 2;
            CharSequence dataSubscriptionName;
            if (((CallbackInfo)access$200).multipleSubs) {
                dataSubscriptionName = ((CallbackInfo)access$200).dataSubscriptionName;
            }
            else {
                dataSubscriptionName = "";
            }
            signalState.secondaryLabel = this.appendMobileDataType(dataSubscriptionName, this.getMobileDataContentName((CallbackInfo)access$200));
        }
        else {
            signalState.state = 1;
            signalState.secondaryLabel = resources.getString(R$string.cell_data_off);
        }
        signalState.contentDescription = signalState.label;
        if (signalState.state == 1) {
            signalState.stateDescription = "";
        }
        else {
            signalState.stateDescription = signalState.secondaryLabel;
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.mController.hasMobileDataFeature();
    }
    
    @Override
    public SignalState newTileState() {
        return new QSTile.SignalState();
    }
    
    private static final class CallbackInfo
    {
        boolean activityIn;
        boolean activityOut;
        boolean airplaneModeEnabled;
        CharSequence dataContentDescription;
        CharSequence dataSubscriptionName;
        boolean multipleSubs;
        boolean noSim;
        boolean roaming;
    }
    
    private final class CellSignalCallback implements SignalCallback
    {
        private final CallbackInfo mInfo;
        
        private CellSignalCallback() {
            this.mInfo = new CallbackInfo();
        }
        
        @Override
        public void setIsAirplaneMode(final IconState iconState) {
            final CallbackInfo mInfo = this.mInfo;
            mInfo.airplaneModeEnabled = iconState.visible;
            QSTileImpl.this.refreshState(mInfo);
        }
        
        @Override
        public void setMobileDataEnabled(final boolean mobileDataEnabled) {
            CellularTile.this.mDetailAdapter.setMobileDataEnabled(mobileDataEnabled);
        }
        
        @Override
        public void setMobileDataIndicators(final IconState iconState, final IconState iconState2, int numberSubscriptions, final int n, final boolean activityIn, final boolean activityOut, final CharSequence charSequence, CharSequence dataContentDescription, final CharSequence charSequence2, final boolean b, final int n2, final boolean roaming) {
            if (iconState2 == null) {
                return;
            }
            this.mInfo.dataSubscriptionName = CellularTile.this.mController.getMobileDataNetworkName();
            final CallbackInfo mInfo = this.mInfo;
            if (charSequence2 == null) {
                dataContentDescription = null;
            }
            mInfo.dataContentDescription = dataContentDescription;
            final CallbackInfo mInfo2 = this.mInfo;
            mInfo2.activityIn = activityIn;
            mInfo2.activityOut = activityOut;
            mInfo2.roaming = roaming;
            numberSubscriptions = CellularTile.this.mController.getNumberSubscriptions();
            boolean multipleSubs = true;
            if (numberSubscriptions <= 1) {
                multipleSubs = false;
            }
            mInfo2.multipleSubs = multipleSubs;
            QSTileImpl.this.refreshState(this.mInfo);
        }
        
        @Override
        public void setNoSims(final boolean noSim, final boolean b) {
            final CallbackInfo mInfo = this.mInfo;
            mInfo.noSim = noSim;
            QSTileImpl.this.refreshState(mInfo);
        }
    }
    
    private final class CellularDetailAdapter implements DetailAdapter
    {
        @Override
        public View createDetailView(final Context context, View view, final ViewGroup viewGroup) {
            int visibility = 0;
            if (view == null) {
                view = LayoutInflater.from(CellularTile.this.mContext).inflate(R$layout.data_usage, viewGroup, false);
            }
            final DataUsageDetailView dataUsageDetailView = (DataUsageDetailView)view;
            final DataUsageController.DataUsageInfo dataUsageInfo = CellularTile.this.mDataController.getDataUsageInfo();
            if (dataUsageInfo == null) {
                return (View)dataUsageDetailView;
            }
            dataUsageDetailView.bind(dataUsageInfo);
            view = dataUsageDetailView.findViewById(R$id.roaming_text);
            if (!CellularTile.this.mSignalCallback.mInfo.roaming) {
                visibility = 4;
            }
            view.setVisibility(visibility);
            return (View)dataUsageDetailView;
        }
        
        @Override
        public int getMetricsCategory() {
            return 117;
        }
        
        @Override
        public Intent getSettingsIntent() {
            return CellularTile.getCellularSettingIntent();
        }
        
        @Override
        public CharSequence getTitle() {
            return CellularTile.this.mContext.getString(R$string.quick_settings_cellular_detail_title);
        }
        
        @Override
        public Boolean getToggleState() {
            Boolean value;
            if (CellularTile.this.mDataController.isMobileDataSupported()) {
                value = CellularTile.this.mDataController.isMobileDataEnabled();
            }
            else {
                value = null;
            }
            return value;
        }
        
        public void setMobileDataEnabled(final boolean b) {
            CellularTile.this.fireToggleStateChanged(b);
        }
        
        @Override
        public void setToggleState(final boolean mobileDataEnabled) {
            MetricsLogger.action(CellularTile.this.mContext, 155, mobileDataEnabled);
            CellularTile.this.mDataController.setMobileDataEnabled(mobileDataEnabled);
        }
    }
}
