// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.internal.logging.MetricsLogger;
import java.util.List;
import android.view.ViewGroup;
import android.view.View;
import com.android.systemui.R$drawable;
import com.android.settingslib.wifi.AccessPoint;
import com.android.systemui.qs.QSDetailItems;
import android.content.res.Resources;
import android.widget.Switch;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.qs.AlphaControlledSignalTileView;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.R$string;
import android.content.Context;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.plugins.ActivityStarter;
import android.content.Intent;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class WifiTile extends QSTileImpl<SignalState>
{
    private static final Intent WIFI_PANEL;
    private static final Intent WIFI_SETTINGS;
    private final ActivityStarter mActivityStarter;
    protected final NetworkController mController;
    private final WifiDetailAdapter mDetailAdapter;
    private boolean mExpectDisabled;
    protected final WifiSignalCallback mSignalCallback;
    private final SignalState mStateBeforeClick;
    private final NetworkController.AccessPointController mWifiController;
    
    static {
        WIFI_SETTINGS = new Intent("android.settings.WIFI_SETTINGS");
        WIFI_PANEL = new Intent("android.settings.panel.action.WIFI");
    }
    
    public WifiTile(final QSHost qsHost, final NetworkController mController, final ActivityStarter mActivityStarter) {
        super(qsHost);
        this.mStateBeforeClick = this.newTileState();
        this.mSignalCallback = new WifiSignalCallback();
        this.mController = mController;
        this.mWifiController = mController.getAccessPointController();
        this.mDetailAdapter = (WifiDetailAdapter)this.createDetailAdapter();
        this.mActivityStarter = mActivityStarter;
        this.mController.observe(this.getLifecycle(), (NetworkController.SignalCallback)this.mSignalCallback);
    }
    
    private CharSequence getSecondaryLabel(final boolean b, String string) {
        if (b) {
            string = super.mContext.getString(R$string.quick_settings_wifi_secondary_label_transient);
        }
        return string;
    }
    
    private static String removeDoubleQuotes(final String s) {
        if (s == null) {
            return null;
        }
        int length = s.length();
        String substring = s;
        if (length > 1) {
            substring = s;
            if (s.charAt(0) == '\"') {
                --length;
                substring = s;
                if (s.charAt(length) == '\"') {
                    substring = s.substring(1, length);
                }
            }
        }
        return substring;
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (((SignalState)super.mState).value) {
            return super.mContext.getString(R$string.accessibility_quick_settings_wifi_changed_on);
        }
        return super.mContext.getString(R$string.accessibility_quick_settings_wifi_changed_off);
    }
    
    protected DetailAdapter createDetailAdapter() {
        return new WifiDetailAdapter();
    }
    
    @Override
    public QSIconView createTileView(final Context context) {
        return new AlphaControlledSignalTileView(context);
    }
    
    @Override
    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }
    
    @Override
    public Intent getLongClickIntent() {
        if (super.mQSSettingsPanelOption == QSSettingsPanel.OPEN_LONG_PRESS) {
            return WifiTile.WIFI_PANEL;
        }
        return WifiTile.WIFI_SETTINGS;
    }
    
    @Override
    public int getMetricsCategory() {
        return 126;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_wifi_label);
    }
    
    @Override
    protected void handleClick() {
        if (super.mQSSettingsPanelOption == QSSettingsPanel.OPEN_CLICK) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(WifiTile.WIFI_PANEL, 0);
            return;
        }
        ((SignalState)super.mState).copyTo(this.mStateBeforeClick);
        final boolean value = ((SignalState)super.mState).value;
        Object arg_SHOW_TRANSIENT_ENABLING;
        if (value) {
            arg_SHOW_TRANSIENT_ENABLING = null;
        }
        else {
            arg_SHOW_TRANSIENT_ENABLING = QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        }
        this.refreshState(arg_SHOW_TRANSIENT_ENABLING);
        this.mController.setWifiEnabled(value ^ true);
        this.mExpectDisabled = value;
        if (value) {
            super.mHandler.postDelayed((Runnable)new _$$Lambda$WifiTile$FBMX_zj483F7uFPAUwutmnquiRU(this), 350L);
        }
    }
    
    @Override
    protected void handleSecondaryClick() {
        if (!this.mWifiController.canConfigWifi()) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.WIFI_SETTINGS"), 0);
            return;
        }
        this.showDetail(true);
        if (!((SignalState)super.mState).value) {
            this.mController.setWifiEnabled(true);
        }
    }
    
    @Override
    protected void handleUpdateState(final SignalState signalState, Object o) {
        if (QSTileImpl.DEBUG) {
            final String tag = super.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("handleUpdateState arg=");
            sb.append(o);
            Log.d(tag, sb.toString());
        }
        final CallbackInfo mInfo = this.mSignalCallback.mInfo;
        if (this.mExpectDisabled) {
            if (mInfo.enabled) {
                return;
            }
            this.mExpectDisabled = false;
        }
        final boolean b = o == QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        final boolean b2 = mInfo.enabled && mInfo.wifiSignalIconId > 0 && mInfo.ssid != null;
        final boolean b3 = mInfo.wifiSignalIconId > 0 && mInfo.ssid == null;
        if (signalState.value != mInfo.enabled) {
            this.mDetailAdapter.setItemsVisible(mInfo.enabled);
            this.fireToggleStateChanged(mInfo.enabled);
        }
        if (signalState.slash == null) {
            o = new QSTile.SlashState();
            signalState.slash = (SlashState)o;
            ((SlashState)o).rotation = 6.0f;
        }
        signalState.slash.isSlashed = false;
        final boolean b4 = b || mInfo.isTransient;
        signalState.secondaryLabel = this.getSecondaryLabel(b4, mInfo.statusLabel);
        signalState.state = 2;
        signalState.dualTarget = true;
        signalState.value = (b || mInfo.enabled);
        signalState.activityIn = (mInfo.enabled && mInfo.activityIn);
        signalState.activityOut = (mInfo.enabled && mInfo.activityOut);
        final StringBuffer sb2 = new StringBuffer();
        final StringBuffer sb3 = new StringBuffer();
        final Resources resources = super.mContext.getResources();
        if (b4) {
            signalState.icon = ResourceIcon.get(17302831);
            signalState.label = resources.getString(R$string.quick_settings_wifi_label);
        }
        else if (!signalState.value) {
            signalState.slash.isSlashed = true;
            signalState.state = 1;
            signalState.icon = ResourceIcon.get(17302863);
            signalState.label = resources.getString(R$string.quick_settings_wifi_label);
        }
        else if (b2) {
            signalState.icon = ResourceIcon.get(mInfo.wifiSignalIconId);
            signalState.label = removeDoubleQuotes(mInfo.ssid);
        }
        else if (b3) {
            signalState.icon = ResourceIcon.get(17302863);
            signalState.label = resources.getString(R$string.quick_settings_wifi_label);
        }
        else {
            signalState.icon = ResourceIcon.get(17302863);
            signalState.label = resources.getString(R$string.quick_settings_wifi_label);
        }
        sb2.append(super.mContext.getString(R$string.quick_settings_wifi_label));
        sb2.append(",");
        if (signalState.value && b2) {
            sb3.append(mInfo.wifiSignalContentDescription);
            sb2.append(removeDoubleQuotes(mInfo.ssid));
            if (!TextUtils.isEmpty(signalState.secondaryLabel)) {
                sb2.append(",");
                sb2.append(signalState.secondaryLabel);
            }
        }
        signalState.stateDescription = sb3.toString();
        signalState.contentDescription = sb2.toString();
        signalState.dualLabelContentDescription = resources.getString(R$string.accessibility_quick_settings_open_settings, new Object[] { this.getTileLabel() });
        signalState.expandedAccessibilityClassName = Switch.class.getName();
    }
    
    @Override
    public boolean isAvailable() {
        return super.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi");
    }
    
    @Override
    public SignalState newTileState() {
        return new QSTile.SignalState();
    }
    
    @Override
    public void setDetailListening(final boolean b) {
        if (b) {
            this.mWifiController.addAccessPointCallback((NetworkController.AccessPointController.AccessPointCallback)this.mDetailAdapter);
        }
        else {
            this.mWifiController.removeAccessPointCallback((NetworkController.AccessPointController.AccessPointCallback)this.mDetailAdapter);
        }
    }
    
    @Override
    protected boolean shouldAnnouncementBeDelayed() {
        return this.mStateBeforeClick.value == ((SignalState)super.mState).value;
    }
    
    @Override
    public boolean supportsDetailView() {
        return this.getDetailAdapter() != null && super.mQSSettingsPanelOption == QSSettingsPanel.OPEN_CLICK;
    }
    
    protected static final class CallbackInfo
    {
        boolean activityIn;
        boolean activityOut;
        boolean connected;
        boolean enabled;
        boolean isTransient;
        String ssid;
        public String statusLabel;
        String wifiSignalContentDescription;
        int wifiSignalIconId;
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CallbackInfo[");
            sb.append("enabled=");
            sb.append(this.enabled);
            sb.append(",connected=");
            sb.append(this.connected);
            sb.append(",wifiSignalIconId=");
            sb.append(this.wifiSignalIconId);
            sb.append(",ssid=");
            sb.append(this.ssid);
            sb.append(",activityIn=");
            sb.append(this.activityIn);
            sb.append(",activityOut=");
            sb.append(this.activityOut);
            sb.append(",wifiSignalContentDescription=");
            sb.append(this.wifiSignalContentDescription);
            sb.append(",isTransient=");
            sb.append(this.isTransient);
            sb.append(']');
            return sb.toString();
        }
    }
    
    protected class WifiDetailAdapter implements DetailAdapter, AccessPointCallback, QSDetailItems.Callback
    {
        private AccessPoint[] mAccessPoints;
        private QSDetailItems mItems;
        
        private void filterUnreachableAPs() {
            final AccessPoint[] mAccessPoints = this.mAccessPoints;
            final int length = mAccessPoints.length;
            final int n = 0;
            int n2;
            int n3;
            for (int i = n2 = 0; i < length; ++i, n2 = n3) {
                n3 = n2;
                if (mAccessPoints[i].isReachable()) {
                    n3 = n2 + 1;
                }
            }
            final AccessPoint[] mAccessPoints2 = this.mAccessPoints;
            if (n2 != mAccessPoints2.length) {
                this.mAccessPoints = new AccessPoint[n2];
                final int length2 = mAccessPoints2.length;
                int n4 = 0;
                int n5;
                for (int j = n; j < length2; ++j, n4 = n5) {
                    final AccessPoint accessPoint = mAccessPoints2[j];
                    n5 = n4;
                    if (accessPoint.isReachable()) {
                        this.mAccessPoints[n4] = accessPoint;
                        n5 = n4 + 1;
                    }
                }
            }
        }
        
        private void updateItems() {
            if (this.mItems == null) {
                return;
            }
            final AccessPoint[] mAccessPoints = this.mAccessPoints;
            int n = 0;
            Label_0058: {
                if (mAccessPoints == null || mAccessPoints.length <= 0) {
                    final WifiTile this$0 = WifiTile.this;
                    if (this$0.mSignalCallback.mInfo.enabled) {
                        this$0.fireScanStateChanged(true);
                        break Label_0058;
                    }
                }
                WifiTile.this.fireScanStateChanged(false);
            }
            final boolean enabled = WifiTile.this.mSignalCallback.mInfo.enabled;
            QSDetailItems.Item[] items = null;
            if (!enabled) {
                this.mItems.setEmptyState(17302863, R$string.wifi_is_off);
                this.mItems.setItems(null);
                return;
            }
            this.mItems.setEmptyState(17302863, R$string.quick_settings_wifi_detail_empty_text);
            final AccessPoint[] mAccessPoints2 = this.mAccessPoints;
            if (mAccessPoints2 != null) {
                final Item[] array = new Item[mAccessPoints2.length];
                while (true) {
                    final AccessPoint[] mAccessPoints3 = this.mAccessPoints;
                    if (n >= mAccessPoints3.length) {
                        break;
                    }
                    final AccessPoint tag = mAccessPoints3[n];
                    final Item item = new QSDetailItems.Item();
                    item.tag = tag;
                    item.iconResId = WifiTile.this.mWifiController.getIcon(tag);
                    item.line1 = tag.getSsid();
                    String summary;
                    if (tag.isActive()) {
                        summary = tag.getSummary();
                    }
                    else {
                        summary = null;
                    }
                    item.line2 = summary;
                    int qs_ic_wifi_lock;
                    if (tag.getSecurity() != 0) {
                        qs_ic_wifi_lock = R$drawable.qs_ic_wifi_lock;
                    }
                    else {
                        qs_ic_wifi_lock = -1;
                    }
                    item.icon2 = qs_ic_wifi_lock;
                    array[n] = item;
                    ++n;
                }
                items = array;
            }
            this.mItems.setItems(items);
        }
        
        @Override
        public View createDetailView(final Context context, final View view, final ViewGroup viewGroup) {
            if (QSTileImpl.DEBUG) {
                final String access$1200 = WifiTile.this.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("createDetailView convertView=");
                sb.append(view != null);
                Log.d(access$1200, sb.toString());
            }
            this.mAccessPoints = null;
            (this.mItems = QSDetailItems.convertOrInflate(context, view, viewGroup)).setTagSuffix("Wifi");
            this.mItems.setCallback((QSDetailItems.Callback)this);
            WifiTile.this.mWifiController.scanForAccessPoints();
            this.setItemsVisible(((SignalState)WifiTile.this.mState).value);
            return (View)this.mItems;
        }
        
        @Override
        public int getMetricsCategory() {
            return 152;
        }
        
        @Override
        public Intent getSettingsIntent() {
            return WifiTile.WIFI_SETTINGS;
        }
        
        @Override
        public CharSequence getTitle() {
            return WifiTile.this.mContext.getString(R$string.quick_settings_wifi_label);
        }
        
        @Override
        public Boolean getToggleState() {
            return ((SignalState)WifiTile.this.mState).value;
        }
        
        @Override
        public void onAccessPointsChanged(final List<AccessPoint> list) {
            this.mAccessPoints = list.toArray(new AccessPoint[list.size()]);
            this.filterUnreachableAPs();
            this.updateItems();
        }
        
        @Override
        public void onDetailItemClick(final Item item) {
            if (item != null) {
                final Object tag = item.tag;
                if (tag != null) {
                    final AccessPoint accessPoint = (AccessPoint)tag;
                    if (!accessPoint.isActive() && WifiTile.this.mWifiController.connect(accessPoint)) {
                        WifiTile.this.mHost.collapsePanels();
                    }
                    WifiTile.this.showDetail(false);
                }
            }
        }
        
        @Override
        public void onDetailItemDisconnect(final Item item) {
        }
        
        @Override
        public void onSettingsActivityTriggered(final Intent intent) {
            WifiTile.this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }
        
        public void setItemsVisible(final boolean itemsVisible) {
            final QSDetailItems mItems = this.mItems;
            if (mItems == null) {
                return;
            }
            mItems.setItemsVisible(itemsVisible);
        }
        
        @Override
        public void setToggleState(final boolean b) {
            if (QSTileImpl.DEBUG) {
                final String access$900 = WifiTile.this.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("setToggleState ");
                sb.append(b);
                Log.d(access$900, sb.toString());
            }
            MetricsLogger.action(WifiTile.this.mContext, 153, b);
            WifiTile.this.mController.setWifiEnabled(b);
        }
    }
    
    protected final class WifiSignalCallback implements SignalCallback
    {
        final CallbackInfo mInfo;
        
        protected WifiSignalCallback() {
            this.mInfo = new CallbackInfo();
        }
        
        @Override
        public void setWifiIndicators(final boolean b, final IconState iconState, final IconState iconState2, final boolean activityIn, final boolean activityOut, final String ssid, final boolean isTransient, final String statusLabel) {
            if (QSTileImpl.DEBUG) {
                final String access$100 = WifiTile.this.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("onWifiSignalChanged enabled=");
                sb.append(b);
                Log.d(access$100, sb.toString());
            }
            final CallbackInfo mInfo = this.mInfo;
            mInfo.enabled = b;
            mInfo.connected = iconState2.visible;
            mInfo.wifiSignalIconId = iconState2.icon;
            mInfo.ssid = ssid;
            mInfo.activityIn = activityIn;
            mInfo.activityOut = activityOut;
            mInfo.wifiSignalContentDescription = iconState2.contentDescription;
            mInfo.isTransient = isTransient;
            mInfo.statusLabel = statusLabel;
            if (QSTileImpl.this.isShowingDetail()) {
                WifiTile.this.mDetailAdapter.updateItems();
            }
            WifiTile.this.refreshState();
        }
    }
}
