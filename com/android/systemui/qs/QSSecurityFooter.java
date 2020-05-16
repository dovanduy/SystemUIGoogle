// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.text.style.ClickableSpan;
import android.os.Message;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R$dimen;
import android.content.Intent;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.content.pm.UserInfo;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyEventLogger;
import com.android.systemui.R$string;
import android.text.method.MovementMethod;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import com.android.systemui.R$style;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.Dependency;
import android.os.Looper;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.util.Log;
import android.os.UserManager;
import com.android.systemui.statusbar.policy.SecurityController;
import android.os.Handler;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.app.AlertDialog;
import android.content.Context;
import com.android.systemui.plugins.ActivityStarter;
import android.content.DialogInterface$OnClickListener;
import android.view.View$OnClickListener;

public class QSSecurityFooter implements View$OnClickListener, DialogInterface$OnClickListener
{
    private final ActivityStarter mActivityStarter;
    private final Callback mCallback;
    private final Context mContext;
    private AlertDialog mDialog;
    private final View mDivider;
    private final ImageView mFooterIcon;
    private int mFooterIconId;
    private final TextView mFooterText;
    private CharSequence mFooterTextContent;
    protected H mHandler;
    private QSTileHost mHost;
    private boolean mIsVisible;
    private final Handler mMainHandler;
    private final View mRootView;
    private final SecurityController mSecurityController;
    private final UserManager mUm;
    private final Runnable mUpdateDisplayState;
    private final Runnable mUpdateIcon;
    
    static {
        Log.isLoggable("QSSecurityFooter", 3);
    }
    
    public QSSecurityFooter(final QSPanel qsPanel, final Context mContext) {
        final View view = null;
        this.mCallback = new Callback();
        this.mFooterTextContent = null;
        this.mUpdateIcon = new Runnable() {
            @Override
            public void run() {
                QSSecurityFooter.this.mFooterIcon.setImageResource(QSSecurityFooter.this.mFooterIconId);
            }
        };
        this.mUpdateDisplayState = new Runnable() {
            @Override
            public void run() {
                if (QSSecurityFooter.this.mFooterTextContent != null) {
                    QSSecurityFooter.this.mFooterText.setText(QSSecurityFooter.this.mFooterTextContent);
                }
                final View access$700 = QSSecurityFooter.this.mRootView;
                final boolean access$701 = QSSecurityFooter.this.mIsVisible;
                final int n = 0;
                int visibility;
                if (access$701) {
                    visibility = 0;
                }
                else {
                    visibility = 8;
                }
                access$700.setVisibility(visibility);
                if (QSSecurityFooter.this.mDivider != null) {
                    final View access$702 = QSSecurityFooter.this.mDivider;
                    int visibility2 = n;
                    if (QSSecurityFooter.this.mIsVisible) {
                        visibility2 = 8;
                    }
                    access$702.setVisibility(visibility2);
                }
            }
        };
        (this.mRootView = LayoutInflater.from(mContext).inflate(R$layout.quick_settings_footer, (ViewGroup)qsPanel, false)).setOnClickListener((View$OnClickListener)this);
        this.mFooterText = (TextView)this.mRootView.findViewById(R$id.footer_text);
        this.mFooterIcon = (ImageView)this.mRootView.findViewById(R$id.footer_icon);
        this.mFooterIconId = R$drawable.ic_info_outline;
        this.mContext = mContext;
        this.mMainHandler = new Handler(Looper.myLooper());
        this.mActivityStarter = Dependency.get(ActivityStarter.class);
        this.mSecurityController = Dependency.get(SecurityController.class);
        this.mHandler = new H((Looper)Dependency.get(Dependency.BG_LOOPER));
        View divider;
        if (qsPanel == null) {
            divider = view;
        }
        else {
            divider = qsPanel.getDivider();
        }
        this.mDivider = divider;
        this.mUm = (UserManager)this.mContext.getSystemService("user");
    }
    
    private void createDialog() {
        final boolean deviceManaged = this.mSecurityController.isDeviceManaged();
        final boolean hasWorkProfile = this.mSecurityController.hasWorkProfile();
        final CharSequence deviceOwnerOrganizationName = this.mSecurityController.getDeviceOwnerOrganizationName();
        final boolean hasCACertInCurrentUser = this.mSecurityController.hasCACertInCurrentUser();
        final boolean hasCACertInWorkProfile = this.mSecurityController.hasCACertInWorkProfile();
        final boolean networkLoggingEnabled = this.mSecurityController.isNetworkLoggingEnabled();
        final String primaryVpnName = this.mSecurityController.getPrimaryVpnName();
        final String workProfileVpnName = this.mSecurityController.getWorkProfileVpnName();
        (this.mDialog = new SystemUIDialog(this.mContext)).requestWindowFeature(1);
        final View inflate = LayoutInflater.from((Context)new ContextThemeWrapper(this.mContext, R$style.Theme_SystemUI_Dialog)).inflate(R$layout.quick_settings_footer_dialog, (ViewGroup)null, false);
        this.mDialog.setView(inflate);
        this.mDialog.setButton(-1, (CharSequence)this.getPositiveButton(), (DialogInterface$OnClickListener)this);
        final CharSequence managementMessage = this.getManagementMessage(deviceManaged, deviceOwnerOrganizationName);
        if (managementMessage == null) {
            inflate.findViewById(R$id.device_management_disclosures).setVisibility(8);
        }
        else {
            inflate.findViewById(R$id.device_management_disclosures).setVisibility(0);
            ((TextView)inflate.findViewById(R$id.device_management_warning)).setText(managementMessage);
            this.mDialog.setButton(-2, (CharSequence)this.getSettingsButton(), (DialogInterface$OnClickListener)this);
        }
        final CharSequence caCertsMessage = this.getCaCertsMessage(deviceManaged, hasCACertInCurrentUser, hasCACertInWorkProfile);
        if (caCertsMessage == null) {
            inflate.findViewById(R$id.ca_certs_disclosures).setVisibility(8);
        }
        else {
            inflate.findViewById(R$id.ca_certs_disclosures).setVisibility(0);
            final TextView textView = (TextView)inflate.findViewById(R$id.ca_certs_warning);
            textView.setText(caCertsMessage);
            textView.setMovementMethod((MovementMethod)new LinkMovementMethod());
        }
        final CharSequence networkLoggingMessage = this.getNetworkLoggingMessage(networkLoggingEnabled);
        if (networkLoggingMessage == null) {
            inflate.findViewById(R$id.network_logging_disclosures).setVisibility(8);
        }
        else {
            inflate.findViewById(R$id.network_logging_disclosures).setVisibility(0);
            ((TextView)inflate.findViewById(R$id.network_logging_warning)).setText(networkLoggingMessage);
        }
        final CharSequence vpnMessage = this.getVpnMessage(deviceManaged, hasWorkProfile, primaryVpnName, workProfileVpnName);
        if (vpnMessage == null) {
            inflate.findViewById(R$id.vpn_disclosures).setVisibility(8);
        }
        else {
            inflate.findViewById(R$id.vpn_disclosures).setVisibility(0);
            final TextView textView2 = (TextView)inflate.findViewById(R$id.vpn_warning);
            textView2.setText(vpnMessage);
            textView2.setMovementMethod((MovementMethod)new LinkMovementMethod());
        }
        this.configSubtitleVisibility(managementMessage != null, caCertsMessage != null, networkLoggingMessage != null, vpnMessage != null, inflate);
        this.mDialog.show();
        this.mDialog.getWindow().setLayout(-1, -2);
    }
    
    private String getPositiveButton() {
        return this.mContext.getString(R$string.ok);
    }
    
    private String getSettingsButton() {
        return this.mContext.getString(R$string.monitoring_button_view_policies);
    }
    
    private void handleClick() {
        this.showDeviceMonitoringDialog();
        DevicePolicyEventLogger.createEvent(57).write();
    }
    
    private void handleRefreshState() {
        final boolean deviceManaged = this.mSecurityController.isDeviceManaged();
        final UserInfo userInfo = this.mUm.getUserInfo(ActivityManager.getCurrentUser());
        final boolean deviceInDemoMode = UserManager.isDeviceInDemoMode(this.mContext);
        final boolean b = true;
        final boolean b2 = deviceInDemoMode && userInfo != null && userInfo.isDemo();
        final boolean hasWorkProfile = this.mSecurityController.hasWorkProfile();
        final boolean hasCACertInCurrentUser = this.mSecurityController.hasCACertInCurrentUser();
        final boolean hasCACertInWorkProfile = this.mSecurityController.hasCACertInWorkProfile();
        final boolean networkLoggingEnabled = this.mSecurityController.isNetworkLoggingEnabled();
        final String primaryVpnName = this.mSecurityController.getPrimaryVpnName();
        final String workProfileVpnName = this.mSecurityController.getWorkProfileVpnName();
        final CharSequence deviceOwnerOrganizationName = this.mSecurityController.getDeviceOwnerOrganizationName();
        final CharSequence workProfileOrganizationName = this.mSecurityController.getWorkProfileOrganizationName();
        boolean mIsVisible = false;
        Label_0192: {
            if (deviceManaged) {
                mIsVisible = b;
                if (!b2) {
                    break Label_0192;
                }
            }
            mIsVisible = b;
            if (!hasCACertInCurrentUser) {
                mIsVisible = b;
                if (!hasCACertInWorkProfile) {
                    mIsVisible = b;
                    if (primaryVpnName == null) {
                        mIsVisible = (workProfileVpnName != null && b);
                    }
                }
            }
        }
        this.mIsVisible = mIsVisible;
        this.mFooterTextContent = this.getFooterText(deviceManaged, hasWorkProfile, hasCACertInCurrentUser, hasCACertInWorkProfile, networkLoggingEnabled, primaryVpnName, workProfileVpnName, deviceOwnerOrganizationName, workProfileOrganizationName);
        int mFooterIconId = R$drawable.ic_info_outline;
        if (primaryVpnName != null || workProfileVpnName != null) {
            if (this.mSecurityController.isVpnBranded()) {
                mFooterIconId = R$drawable.stat_sys_branded_vpn;
            }
            else {
                mFooterIconId = R$drawable.stat_sys_vpn_ic;
            }
        }
        if (this.mFooterIconId != mFooterIconId) {
            this.mFooterIconId = mFooterIconId;
            this.mMainHandler.post(this.mUpdateIcon);
        }
        this.mMainHandler.post(this.mUpdateDisplayState);
    }
    
    protected void configSubtitleVisibility(final boolean b, final boolean b2, final boolean b3, final boolean b4, final View view) {
        if (b) {
            return;
        }
        int n = 0;
        if (b2) {
            n = 1;
        }
        int n2 = n;
        if (b3) {
            n2 = n + 1;
        }
        int n3 = n2;
        if (b4) {
            n3 = n2 + 1;
        }
        if (n3 != 1) {
            return;
        }
        if (b2) {
            view.findViewById(R$id.ca_certs_subtitle).setVisibility(8);
        }
        if (b3) {
            view.findViewById(R$id.network_logging_subtitle).setVisibility(8);
        }
        if (b4) {
            view.findViewById(R$id.vpn_subtitle).setVisibility(8);
        }
    }
    
    protected CharSequence getCaCertsMessage(final boolean b, final boolean b2, final boolean b3) {
        if (!b2 && !b3) {
            return null;
        }
        if (b) {
            return this.mContext.getString(R$string.monitoring_description_management_ca_certificate);
        }
        if (b3) {
            return this.mContext.getString(R$string.monitoring_description_managed_profile_ca_certificate);
        }
        return this.mContext.getString(R$string.monitoring_description_ca_certificate);
    }
    
    protected CharSequence getFooterText(final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, String s, final String s2, final CharSequence charSequence, final CharSequence charSequence2) {
        if (b) {
            if (!b3 && !b4 && !b5) {
                if (s != null && s2 != null) {
                    if (charSequence == null) {
                        return this.mContext.getString(R$string.quick_settings_disclosure_management_vpns);
                    }
                    return this.mContext.getString(R$string.quick_settings_disclosure_named_management_vpns, new Object[] { charSequence });
                }
                else if (s == null && s2 == null) {
                    if (charSequence == null) {
                        return this.mContext.getString(R$string.quick_settings_disclosure_management);
                    }
                    return this.mContext.getString(R$string.quick_settings_disclosure_named_management, new Object[] { charSequence });
                }
                else {
                    if (charSequence == null) {
                        final Context mContext = this.mContext;
                        final int quick_settings_disclosure_management_named_vpn = R$string.quick_settings_disclosure_management_named_vpn;
                        if (s == null) {
                            s = s2;
                        }
                        return mContext.getString(quick_settings_disclosure_management_named_vpn, new Object[] { s });
                    }
                    final Context mContext2 = this.mContext;
                    final int quick_settings_disclosure_named_management_named_vpn = R$string.quick_settings_disclosure_named_management_named_vpn;
                    if (s == null) {
                        s = s2;
                    }
                    return mContext2.getString(quick_settings_disclosure_named_management_named_vpn, new Object[] { charSequence, s });
                }
            }
            else {
                if (charSequence == null) {
                    return this.mContext.getString(R$string.quick_settings_disclosure_management_monitoring);
                }
                return this.mContext.getString(R$string.quick_settings_disclosure_named_management_monitoring, new Object[] { charSequence });
            }
        }
        else if (b4) {
            if (charSequence2 == null) {
                return this.mContext.getString(R$string.quick_settings_disclosure_managed_profile_monitoring);
            }
            return this.mContext.getString(R$string.quick_settings_disclosure_named_managed_profile_monitoring, new Object[] { charSequence2 });
        }
        else {
            if (b3) {
                return this.mContext.getString(R$string.quick_settings_disclosure_monitoring);
            }
            if (s != null && s2 != null) {
                return this.mContext.getString(R$string.quick_settings_disclosure_vpns);
            }
            if (s2 != null) {
                return this.mContext.getString(R$string.quick_settings_disclosure_managed_profile_named_vpn, new Object[] { s2 });
            }
            if (s == null) {
                return null;
            }
            if (b2) {
                return this.mContext.getString(R$string.quick_settings_disclosure_personal_profile_named_vpn, new Object[] { s });
            }
            return this.mContext.getString(R$string.quick_settings_disclosure_named_vpn, new Object[] { s });
        }
    }
    
    protected CharSequence getManagementMessage(final boolean b, final CharSequence charSequence) {
        if (!b) {
            return null;
        }
        if (charSequence != null) {
            return this.mContext.getString(R$string.monitoring_description_named_management, new Object[] { charSequence });
        }
        return this.mContext.getString(R$string.monitoring_description_management);
    }
    
    protected CharSequence getNetworkLoggingMessage(final boolean b) {
        if (!b) {
            return null;
        }
        return this.mContext.getString(R$string.monitoring_description_management_network_logging);
    }
    
    public View getView() {
        return this.mRootView;
    }
    
    protected CharSequence getVpnMessage(final boolean b, final boolean b2, String s, final String s2) {
        if (s == null && s2 == null) {
            return null;
        }
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (b) {
            if (s != null && s2 != null) {
                spannableStringBuilder.append((CharSequence)this.mContext.getString(R$string.monitoring_description_two_named_vpns, new Object[] { s, s2 }));
            }
            else {
                final Context mContext = this.mContext;
                final int monitoring_description_named_vpn = R$string.monitoring_description_named_vpn;
                if (s == null) {
                    s = s2;
                }
                spannableStringBuilder.append((CharSequence)mContext.getString(monitoring_description_named_vpn, new Object[] { s }));
            }
        }
        else if (s != null && s2 != null) {
            spannableStringBuilder.append((CharSequence)this.mContext.getString(R$string.monitoring_description_two_named_vpns, new Object[] { s, s2 }));
        }
        else if (s2 != null) {
            spannableStringBuilder.append((CharSequence)this.mContext.getString(R$string.monitoring_description_managed_profile_named_vpn, new Object[] { s2 }));
        }
        else if (b2) {
            spannableStringBuilder.append((CharSequence)this.mContext.getString(R$string.monitoring_description_personal_profile_named_vpn, new Object[] { s }));
        }
        else {
            spannableStringBuilder.append((CharSequence)this.mContext.getString(R$string.monitoring_description_named_vpn, new Object[] { s }));
        }
        spannableStringBuilder.append((CharSequence)this.mContext.getString(R$string.monitoring_description_vpn_settings_separator));
        spannableStringBuilder.append((CharSequence)this.mContext.getString(R$string.monitoring_description_vpn_settings), (Object)new VpnSpan(), 0);
        return (CharSequence)spannableStringBuilder;
    }
    
    public boolean hasFooter() {
        return this.mRootView.getVisibility() != 8;
    }
    
    public void onClick(final DialogInterface dialogInterface, final int n) {
        if (n == -2) {
            final Intent intent = new Intent("android.settings.ENTERPRISE_PRIVACY_SETTINGS");
            this.mDialog.dismiss();
            this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }
    }
    
    public void onClick(final View view) {
        if (!this.hasFooter()) {
            return;
        }
        this.mHandler.sendEmptyMessage(0);
    }
    
    public void onConfigurationChanged() {
        FontSizeUtils.updateFontSize(this.mFooterText, R$dimen.qs_tile_text_size);
    }
    
    public void refreshState() {
        this.mHandler.sendEmptyMessage(1);
    }
    
    public void setHostEnvironment(final QSTileHost mHost) {
        this.mHost = mHost;
    }
    
    public void setListening(final boolean b) {
        if (b) {
            this.mSecurityController.addCallback((SecurityController.SecurityControllerCallback)this.mCallback);
            this.refreshState();
        }
        else {
            this.mSecurityController.removeCallback((SecurityController.SecurityControllerCallback)this.mCallback);
        }
    }
    
    public void showDeviceMonitoringDialog() {
        this.mHost.collapsePanels();
        this.createDialog();
    }
    
    private class Callback implements SecurityControllerCallback
    {
        @Override
        public void onStateChanged() {
            QSSecurityFooter.this.refreshState();
        }
    }
    
    private class H extends Handler
    {
        private H(final Looper looper) {
            super(looper);
        }
        
        public void handleMessage(final Message message) {
            String str = null;
            try {
                if (message.what == 1) {
                    str = "handleRefreshState";
                    QSSecurityFooter.this.handleRefreshState();
                }
                else {
                    str = str;
                    if (message.what == 0) {
                        str = "handleClick";
                        QSSecurityFooter.this.handleClick();
                    }
                }
            }
            finally {
                final StringBuilder sb = new StringBuilder();
                sb.append("Error in ");
                sb.append(str);
                final String string = sb.toString();
                final Throwable t;
                Log.w("QSSecurityFooter", string, t);
                QSSecurityFooter.this.mHost.warn(string, t);
            }
        }
    }
    
    protected class VpnSpan extends ClickableSpan
    {
        public boolean equals(final Object o) {
            return o instanceof VpnSpan;
        }
        
        public int hashCode() {
            return 314159257;
        }
        
        public void onClick(final View view) {
            final Intent intent = new Intent("android.settings.VPN_SETTINGS");
            QSSecurityFooter.this.mDialog.dismiss();
            QSSecurityFooter.this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }
    }
}
