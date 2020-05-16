// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$drawable;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import java.util.Iterator;
import android.service.notification.ZenModeConfig$ZenRule;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Slog;
import android.view.View$OnAttachStateChangeListener;
import android.app.ActivityManager;
import android.provider.Settings$Secure;
import android.widget.Switch;
import android.text.TextUtils;
import com.android.systemui.SysUIToast;
import android.net.Uri;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.R$string;
import com.android.systemui.Prefs;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.settingslib.notification.EnableZenModeDialog;
import android.app.Dialog;
import android.content.IntentFilter;
import android.content.Context;
import android.service.notification.ZenModeConfig;
import com.android.systemui.qs.QSHost;
import com.android.systemui.volume.ZenModePanel;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences$OnSharedPreferenceChangeListener;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.ActivityStarter;
import android.content.Intent;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class DndTile extends QSTileImpl<BooleanState>
{
    private static final Intent ZEN_PRIORITY_SETTINGS;
    private static final Intent ZEN_SETTINGS;
    private final ActivityStarter mActivityStarter;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final ZenModeController mController;
    private final DndDetailAdapter mDetailAdapter;
    private boolean mListening;
    private final SharedPreferences$OnSharedPreferenceChangeListener mPrefListener;
    private final BroadcastReceiver mReceiver;
    private boolean mReceiverRegistered;
    private final SharedPreferences mSharedPreferences;
    private boolean mShowingDetail;
    private final ZenModeController.Callback mZenCallback;
    private final ZenModePanel.Callback mZenModePanelCallback;
    
    static {
        ZEN_SETTINGS = new Intent("android.settings.ZEN_MODE_SETTINGS");
        ZEN_PRIORITY_SETTINGS = new Intent("android.settings.ZEN_MODE_PRIORITY_SETTINGS");
    }
    
    public DndTile(final QSHost qsHost, final ZenModeController mController, final ActivityStarter mActivityStarter, final BroadcastDispatcher mBroadcastDispatcher, final SharedPreferences mSharedPreferences) {
        super(qsHost);
        this.mPrefListener = (SharedPreferences$OnSharedPreferenceChangeListener)new SharedPreferences$OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String s) {
                if ("DndTileCombinedIcon".equals(s) || "DndTileVisible".equals(s)) {
                    DndTile.this.refreshState();
                }
            }
        };
        this.mZenCallback = new ZenModeController.Callback() {
            @Override
            public void onConfigChanged(final ZenModeConfig zenModeConfig) {
                if (QSTileImpl.this.isShowingDetail()) {
                    DndTile.this.mDetailAdapter.updatePanel();
                }
            }
            
            @Override
            public void onZenChanged(final int i) {
                QSTileImpl.this.refreshState(i);
                if (QSTileImpl.this.isShowingDetail()) {
                    DndTile.this.mDetailAdapter.updatePanel();
                }
            }
        };
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                DndTile.setVisible(DndTile.this.mContext, intent.getBooleanExtra("visible", false));
                DndTile.this.refreshState();
            }
        };
        this.mZenModePanelCallback = new ZenModePanel.Callback() {
            @Override
            public void onExpanded(final boolean b) {
            }
            
            @Override
            public void onInteraction() {
            }
            
            @Override
            public void onPrioritySettings() {
                DndTile.this.mActivityStarter.postStartActivityDismissingKeyguard(DndTile.ZEN_PRIORITY_SETTINGS, 0);
            }
        };
        this.mController = mController;
        this.mActivityStarter = mActivityStarter;
        this.mSharedPreferences = mSharedPreferences;
        this.mDetailAdapter = new DndDetailAdapter();
        (this.mBroadcastDispatcher = mBroadcastDispatcher).registerReceiver(this.mReceiver, new IntentFilter("com.android.systemui.dndtile.SET_VISIBLE"));
        this.mReceiverRegistered = true;
        this.mController.observe(this.getLifecycle(), this.mZenCallback);
    }
    
    public static boolean isCombinedIcon(final SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("DndTileCombinedIcon", false);
    }
    
    public static boolean isVisible(final SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("DndTileVisible", false);
    }
    
    public static void setCombinedIcon(final Context context, final boolean b) {
        Prefs.putBoolean(context, "DndTileCombinedIcon", b);
    }
    
    public static void setVisible(final Context context, final boolean b) {
        Prefs.putBoolean(context, "DndTileVisible", b);
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (((BooleanState)super.mState).value) {
            return super.mContext.getString(R$string.accessibility_quick_settings_dnd_changed_on);
        }
        return super.mContext.getString(R$string.accessibility_quick_settings_dnd_changed_off);
    }
    
    @Override
    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }
    
    @Override
    public Intent getLongClickIntent() {
        return DndTile.ZEN_SETTINGS;
    }
    
    @Override
    public int getMetricsCategory() {
        return 118;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_dnd_label);
    }
    
    @Override
    protected void handleClick() {
        if (((BooleanState)super.mState).value) {
            this.mController.setZen(0, null, super.TAG);
        }
        else {
            this.showDetail(true);
        }
    }
    
    @Override
    protected void handleDestroy() {
        super.handleDestroy();
        if (this.mReceiverRegistered) {
            this.mBroadcastDispatcher.unregisterReceiver(this.mReceiver);
            this.mReceiverRegistered = false;
        }
    }
    
    @Override
    protected void handleSecondaryClick() {
        if (this.mController.isVolumeRestricted()) {
            super.mHost.collapsePanels();
            final Context mContext = super.mContext;
            SysUIToast.makeText(mContext, mContext.getString(17040089), 1).show();
            return;
        }
        if (!((BooleanState)super.mState).value) {
            this.mController.addCallback((ZenModeController.Callback)new ZenModeController.Callback() {
                @Override
                public void onZenChanged(final int n) {
                    DndTile.this.mController.removeCallback((ZenModeController.Callback)this);
                    DndTile.this.showDetail(true);
                }
            });
            this.mController.setZen(1, null, super.TAG);
        }
        else {
            this.showDetail(true);
        }
    }
    
    public void handleSetListening(final boolean mListening) {
        super.handleSetListening(mListening);
        if (this.mListening == mListening) {
            return;
        }
        this.mListening = mListening;
        if (mListening) {
            Prefs.registerListener(super.mContext, this.mPrefListener);
        }
        else {
            Prefs.unregisterListener(super.mContext, this.mPrefListener);
        }
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, Object o) {
        final ZenModeController mController = this.mController;
        if (mController == null) {
            return;
        }
        int n;
        if (o instanceof Integer) {
            n = (int)o;
        }
        else {
            n = mController.getZen();
        }
        final boolean value = n != 0;
        final boolean b = booleanState.value != value;
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        booleanState.dualTarget = true;
        booleanState.value = value;
        int state;
        if (value) {
            state = 2;
        }
        else {
            state = 1;
        }
        booleanState.state = state;
        booleanState.slash.isSlashed = (booleanState.value ^ true);
        booleanState.label = this.getTileLabel();
        booleanState.secondaryLabel = TextUtils.emptyIfNull(ZenModeConfig.getDescription(super.mContext, n != 0, this.mController.getConfig(), false));
        booleanState.icon = ResourceIcon.get(17302797);
        this.checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_adjust_volume");
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    booleanState.contentDescription = super.mContext.getString(R$string.accessibility_quick_settings_dnd);
                }
                else {
                    o = new StringBuilder();
                    ((StringBuilder)o).append(super.mContext.getString(R$string.accessibility_quick_settings_dnd));
                    ((StringBuilder)o).append(", ");
                    ((StringBuilder)o).append(super.mContext.getString(R$string.accessibility_quick_settings_dnd_alarms_on));
                    ((StringBuilder)o).append(", ");
                    ((StringBuilder)o).append((Object)booleanState.secondaryLabel);
                    booleanState.contentDescription = ((StringBuilder)o).toString();
                }
            }
            else {
                o = new StringBuilder();
                ((StringBuilder)o).append(super.mContext.getString(R$string.accessibility_quick_settings_dnd));
                ((StringBuilder)o).append(", ");
                ((StringBuilder)o).append(super.mContext.getString(R$string.accessibility_quick_settings_dnd_none_on));
                ((StringBuilder)o).append(", ");
                ((StringBuilder)o).append((Object)booleanState.secondaryLabel);
                booleanState.contentDescription = ((StringBuilder)o).toString();
            }
        }
        else {
            o = new StringBuilder();
            ((StringBuilder)o).append(super.mContext.getString(R$string.accessibility_quick_settings_dnd));
            ((StringBuilder)o).append(", ");
            ((StringBuilder)o).append((Object)booleanState.secondaryLabel);
            booleanState.contentDescription = ((StringBuilder)o).toString();
        }
        if (b) {
            this.fireToggleStateChanged(booleanState.value);
        }
        booleanState.dualLabelContentDescription = super.mContext.getResources().getString(R$string.accessibility_quick_settings_open_settings, new Object[] { this.getTileLabel() });
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }
    
    @Override
    public boolean isAvailable() {
        return isVisible(this.mSharedPreferences);
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
    
    @Override
    public void showDetail(final boolean b) {
        final int int1 = Settings$Secure.getInt(super.mContext.getContentResolver(), "zen_duration", 0);
        if (Settings$Secure.getInt(super.mContext.getContentResolver(), "show_zen_upgrade_notification", 0) != 0 && Settings$Secure.getInt(super.mContext.getContentResolver(), "zen_settings_updated", 0) != 1) {
            Settings$Secure.putInt(super.mContext.getContentResolver(), "show_zen_upgrade_notification", 0);
            this.mController.setZen(1, null, super.TAG);
            final Intent intent = new Intent("android.settings.ZEN_MODE_ONBOARDING");
            intent.addFlags(268468224);
            this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }
        else if (int1 != -1) {
            if (int1 != 0) {
                this.mController.setZen(1, ZenModeConfig.toTimeCondition(super.mContext, int1, ActivityManager.getCurrentUser(), true).id, super.TAG);
            }
            else {
                this.mController.setZen(1, null, super.TAG);
            }
        }
        else {
            super.mUiHandler.post((Runnable)new _$$Lambda$DndTile$fMf3Tdb9veQ9DG26bABcK78yOSM(this));
        }
    }
    
    private final class DndDetailAdapter implements DetailAdapter, View$OnAttachStateChangeListener
    {
        private ZenModePanel mZenPanel;
        
        private String getOwnerCaption(String s) {
            final PackageManager packageManager = DndTile.this.mContext.getPackageManager();
            try {
                final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(s, 0);
                if (applicationInfo != null) {
                    final CharSequence loadLabel = applicationInfo.loadLabel(packageManager);
                    if (loadLabel != null) {
                        s = loadLabel.toString().trim();
                        s = DndTile.this.mContext.getString(R$string.qs_dnd_prompt_app, new Object[] { s });
                        return s;
                    }
                }
            }
            finally {
                final Throwable t;
                Slog.w(DndTile.this.TAG, "Error loading owner caption", t);
            }
            return "";
        }
        
        private void updatePanel() {
            if (this.mZenPanel == null) {
                return;
            }
            if (DndTile.this.mController.getZen() == 0) {
                this.mZenPanel.setState(2);
            }
            else {
                final ZenModeConfig config = DndTile.this.mController.getConfig();
                final ZenModeConfig$ZenRule manualRule = config.manualRule;
                String autoText = null;
                Label_0077: {
                    if (manualRule != null) {
                        final String enabler = manualRule.enabler;
                        if (enabler != null) {
                            autoText = this.getOwnerCaption(enabler);
                            break Label_0077;
                        }
                    }
                    autoText = "";
                }
                for (final ZenModeConfig$ZenRule zenModeConfig$ZenRule : config.automaticRules.values()) {
                    if (zenModeConfig$ZenRule.isAutomaticActive()) {
                        if (autoText.isEmpty()) {
                            autoText = DndTile.this.mContext.getString(R$string.qs_dnd_prompt_auto_rule, new Object[] { zenModeConfig$ZenRule.name });
                        }
                        else {
                            autoText = DndTile.this.mContext.getString(R$string.qs_dnd_prompt_auto_rule_app);
                        }
                    }
                }
                if (autoText.isEmpty()) {
                    this.mZenPanel.setState(0);
                }
                else {
                    this.mZenPanel.setState(1);
                    this.mZenPanel.setAutoText(autoText);
                }
            }
        }
        
        @Override
        public View createDetailView(final Context context, final View view, final ViewGroup viewGroup) {
            ZenModePanel mZenPanel;
            if (view != null) {
                mZenPanel = (ZenModePanel)view;
            }
            else {
                mZenPanel = (ZenModePanel)LayoutInflater.from(context).inflate(R$layout.zen_mode_panel, viewGroup, false);
            }
            this.mZenPanel = mZenPanel;
            if (view == null) {
                mZenPanel.init(DndTile.this.mController);
                this.mZenPanel.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)this);
                this.mZenPanel.setCallback(DndTile.this.mZenModePanelCallback);
                this.mZenPanel.setEmptyState(R$drawable.ic_qs_dnd_detail_empty, R$string.dnd_is_off);
            }
            this.updatePanel();
            return (View)this.mZenPanel;
        }
        
        @Override
        public int getMetricsCategory() {
            return 149;
        }
        
        @Override
        public Intent getSettingsIntent() {
            return DndTile.ZEN_SETTINGS;
        }
        
        @Override
        public CharSequence getTitle() {
            return DndTile.this.mContext.getString(R$string.quick_settings_dnd_label);
        }
        
        @Override
        public Boolean getToggleState() {
            return ((BooleanState)DndTile.this.mState).value;
        }
        
        public void onViewAttachedToWindow(final View view) {
            DndTile.this.mShowingDetail = true;
        }
        
        public void onViewDetachedFromWindow(final View view) {
            DndTile.this.mShowingDetail = false;
            this.mZenPanel = null;
        }
        
        @Override
        public void setToggleState(final boolean b) {
            MetricsLogger.action(DndTile.this.mContext, 166, b);
            if (!b) {
                DndTile.this.mController.setZen(0, null, DndTile.this.TAG);
            }
            else {
                DndTile.this.mController.setZen(1, null, DndTile.this.TAG);
            }
        }
    }
}
