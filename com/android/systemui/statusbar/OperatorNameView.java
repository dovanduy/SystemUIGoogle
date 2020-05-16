// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.view.View;
import android.graphics.Rect;
import android.os.Bundle;
import android.telephony.ServiceState;
import java.util.List;
import android.text.TextUtils;
import android.telephony.SubscriptionInfo;
import com.android.settingslib.WirelessUtils;
import android.net.ConnectivityManager;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.DemoMode;
import android.widget.TextView;

public class OperatorNameView extends TextView implements DemoMode, DarkReceiver, SignalCallback, Tunable
{
    private final KeyguardUpdateMonitorCallback mCallback;
    private boolean mDemoMode;
    private KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    
    public OperatorNameView(final Context context) {
        this(context, null);
    }
    
    public OperatorNameView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public OperatorNameView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onRefreshCarrierInfo() {
                OperatorNameView.this.updateText();
            }
        };
    }
    
    private void update() {
        final TunerService tunerService = Dependency.get(TunerService.class);
        boolean b = true;
        if (tunerService.getValue("show_operator_name", 1) == 0) {
            b = false;
        }
        int visibility;
        if (b) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        this.setVisibility(visibility);
        final boolean networkSupported = ConnectivityManager.from(super.mContext).isNetworkSupported(0);
        final boolean airplaneModeOn = WirelessUtils.isAirplaneModeOn(super.mContext);
        if (networkSupported && !airplaneModeOn) {
            if (!this.mDemoMode) {
                this.updateText();
            }
            return;
        }
        this.setText((CharSequence)null);
        this.setVisibility(8);
    }
    
    private void updateText() {
        final KeyguardUpdateMonitor mKeyguardUpdateMonitor = this.mKeyguardUpdateMonitor;
        int i = 0;
        while (true) {
            for (List<SubscriptionInfo> filteredSubscriptionInfo = mKeyguardUpdateMonitor.getFilteredSubscriptionInfo(false); i < filteredSubscriptionInfo.size(); ++i) {
                final int subscriptionId = filteredSubscriptionInfo.get(i).getSubscriptionId();
                final int simState = this.mKeyguardUpdateMonitor.getSimState(subscriptionId);
                final CharSequence carrierName = filteredSubscriptionInfo.get(i).getCarrierName();
                if (!TextUtils.isEmpty(carrierName) && simState == 5) {
                    final ServiceState serviceState = this.mKeyguardUpdateMonitor.getServiceState(subscriptionId);
                    if (serviceState != null && serviceState.getState() == 0) {
                        this.setText(carrierName);
                        return;
                    }
                }
            }
            final CharSequence carrierName = null;
            continue;
        }
    }
    
    public void dispatchDemoCommand(final String s, final Bundle bundle) {
        if (!this.mDemoMode && s.equals("enter")) {
            this.mDemoMode = true;
        }
        else if (this.mDemoMode && s.equals("exit")) {
            this.mDemoMode = false;
            this.update();
        }
        else if (this.mDemoMode && s.equals("operator")) {
            this.setText((CharSequence)bundle.getString("name"));
        }
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        (this.mKeyguardUpdateMonitor = Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mCallback);
        Dependency.get(DarkIconDispatcher.class).addDarkReceiver((DarkIconDispatcher.DarkReceiver)this);
        Dependency.get(NetworkController.class).addCallback((NetworkController.SignalCallback)this);
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "show_operator_name");
    }
    
    public void onDarkChanged(final Rect rect, final float n, final int n2) {
        this.setTextColor(DarkIconDispatcher.getTint(rect, (View)this, n2));
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mKeyguardUpdateMonitor.removeCallback(this.mCallback);
        Dependency.get(DarkIconDispatcher.class).removeDarkReceiver((DarkIconDispatcher.DarkReceiver)this);
        Dependency.get(NetworkController.class).removeCallback((NetworkController.SignalCallback)this);
        Dependency.get(TunerService.class).removeTunable((TunerService.Tunable)this);
    }
    
    public void onTuningChanged(final String s, final String s2) {
        this.update();
    }
    
    public void setIsAirplaneMode(final IconState iconState) {
        this.update();
    }
}
