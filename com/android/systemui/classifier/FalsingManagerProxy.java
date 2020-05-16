// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import android.net.Uri;
import android.view.MotionEvent;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.classifier.brightline.BrightLineFalsingManager;
import com.android.systemui.classifier.brightline.FalsingDataProvider;
import android.provider.DeviceConfig$Properties;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.FalsingPlugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.shared.plugins.PluginManager;
import android.content.Context;
import java.util.concurrent.Executor;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.dock.DockManager;
import android.util.DisplayMetrics;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.Dumpable;
import com.android.systemui.plugins.FalsingManager;

public class FalsingManagerProxy implements FalsingManager, Dumpable
{
    private boolean mBrightlineEnabled;
    private final DeviceConfigProxy mDeviceConfig;
    private DeviceConfig$OnPropertiesChangedListener mDeviceConfigListener;
    private final DisplayMetrics mDisplayMetrics;
    private final DockManager mDockManager;
    private FalsingManager mInternalFalsingManager;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final ProximitySensor mProximitySensor;
    private final StatusBarStateController mStatusBarStateController;
    private Executor mUiBgExecutor;
    
    FalsingManagerProxy(final Context context, final PluginManager pluginManager, final Executor executor, final DisplayMetrics mDisplayMetrics, final ProximitySensor mProximitySensor, final DeviceConfigProxy mDeviceConfig, final DockManager mDockManager, final KeyguardUpdateMonitor mKeyguardUpdateMonitor, final DumpManager dumpManager, final Executor mUiBgExecutor, final StatusBarStateController mStatusBarStateController) {
        this.mDisplayMetrics = mDisplayMetrics;
        this.mProximitySensor = mProximitySensor;
        this.mDockManager = mDockManager;
        this.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
        this.mUiBgExecutor = mUiBgExecutor;
        this.mStatusBarStateController = mStatusBarStateController;
        mProximitySensor.setTag("FalsingManager");
        this.mProximitySensor.setSensorDelay(1);
        this.mDeviceConfig = mDeviceConfig;
        this.mDeviceConfigListener = (DeviceConfig$OnPropertiesChangedListener)new _$$Lambda$FalsingManagerProxy$15gIs_9mVwyDjJbglxP0IV0T3ag(this, context);
        this.setupFalsingManager(context);
        this.mDeviceConfig.addOnPropertiesChangedListener("systemui", executor, this.mDeviceConfigListener);
        pluginManager.addPluginListener((PluginListener<Plugin>)new PluginListener<FalsingPlugin>() {
            @Override
            public void onPluginConnected(final FalsingPlugin falsingPlugin, final Context context) {
                final FalsingManager falsingManager = falsingPlugin.getFalsingManager(context);
                if (falsingManager != null) {
                    FalsingManagerProxy.this.mInternalFalsingManager.cleanup();
                    FalsingManagerProxy.this.mInternalFalsingManager = falsingManager;
                }
            }
            
            @Override
            public void onPluginDisconnected(final FalsingPlugin falsingPlugin) {
                FalsingManagerProxy.this.mInternalFalsingManager = new FalsingManagerImpl(context, FalsingManagerProxy.this.mUiBgExecutor);
            }
        }, FalsingPlugin.class);
        dumpManager.registerDumpable("FalsingManager", this);
    }
    
    private void onDeviceConfigPropertiesChanged(final Context context, final String anObject) {
        if (!"systemui".equals(anObject)) {
            return;
        }
        this.setupFalsingManager(context);
    }
    
    private void setupFalsingManager(final Context context) {
        final boolean boolean1 = this.mDeviceConfig.getBoolean("systemui", "brightline_falsing_manager_enabled", true);
        if (boolean1 == this.mBrightlineEnabled && this.mInternalFalsingManager != null) {
            return;
        }
        this.mBrightlineEnabled = boolean1;
        final FalsingManager mInternalFalsingManager = this.mInternalFalsingManager;
        if (mInternalFalsingManager != null) {
            mInternalFalsingManager.cleanup();
        }
        if (!boolean1) {
            this.mInternalFalsingManager = new FalsingManagerImpl(context, this.mUiBgExecutor);
        }
        else {
            this.mInternalFalsingManager = new BrightLineFalsingManager(new FalsingDataProvider(this.mDisplayMetrics), this.mKeyguardUpdateMonitor, this.mProximitySensor, this.mDeviceConfig, this.mDockManager, this.mStatusBarStateController);
        }
    }
    
    @Override
    public void cleanup() {
        this.mDeviceConfig.removeOnPropertiesChangedListener(this.mDeviceConfigListener);
        this.mInternalFalsingManager.cleanup();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        this.mInternalFalsingManager.dump(printWriter);
    }
    
    @Override
    public void dump(final PrintWriter printWriter) {
        this.mInternalFalsingManager.dump(printWriter);
    }
    
    @VisibleForTesting
    FalsingManager getInternalFalsingManager() {
        return this.mInternalFalsingManager;
    }
    
    @Override
    public boolean isClassifierEnabled() {
        return this.mInternalFalsingManager.isClassifierEnabled();
    }
    
    @Override
    public boolean isFalseTouch() {
        return this.mInternalFalsingManager.isFalseTouch();
    }
    
    @Override
    public boolean isReportingEnabled() {
        return this.mInternalFalsingManager.isReportingEnabled();
    }
    
    @Override
    public boolean isUnlockingDisabled() {
        return this.mInternalFalsingManager.isUnlockingDisabled();
    }
    
    @Override
    public void onAffordanceSwipingAborted() {
        this.mInternalFalsingManager.onAffordanceSwipingAborted();
    }
    
    @Override
    public void onAffordanceSwipingStarted(final boolean b) {
        this.mInternalFalsingManager.onAffordanceSwipingStarted(b);
    }
    
    @Override
    public void onBouncerHidden() {
        this.mInternalFalsingManager.onBouncerHidden();
    }
    
    @Override
    public void onBouncerShown() {
        this.mInternalFalsingManager.onBouncerShown();
    }
    
    @Override
    public void onCameraHintStarted() {
        this.mInternalFalsingManager.onCameraHintStarted();
    }
    
    @Override
    public void onCameraOn() {
        this.mInternalFalsingManager.onCameraOn();
    }
    
    @Override
    public void onExpansionFromPulseStopped() {
        this.mInternalFalsingManager.onExpansionFromPulseStopped();
    }
    
    @Override
    public void onLeftAffordanceHintStarted() {
        this.mInternalFalsingManager.onLeftAffordanceHintStarted();
    }
    
    @Override
    public void onLeftAffordanceOn() {
        this.mInternalFalsingManager.onLeftAffordanceOn();
    }
    
    @Override
    public void onNotificationActive() {
        this.mInternalFalsingManager.onNotificationActive();
    }
    
    @Override
    public void onNotificationDismissed() {
        this.mInternalFalsingManager.onNotificationDismissed();
    }
    
    @Override
    public void onNotificationDoubleTap(final boolean b, final float n, final float n2) {
        this.mInternalFalsingManager.onNotificationDoubleTap(b, n, n2);
    }
    
    @Override
    public void onNotificatonStartDismissing() {
        this.mInternalFalsingManager.onNotificatonStartDismissing();
    }
    
    @Override
    public void onNotificatonStartDraggingDown() {
        this.mInternalFalsingManager.onNotificatonStartDraggingDown();
    }
    
    @Override
    public void onNotificatonStopDismissing() {
        this.mInternalFalsingManager.onNotificatonStopDismissing();
    }
    
    @Override
    public void onNotificatonStopDraggingDown() {
        this.mInternalFalsingManager.onNotificatonStartDraggingDown();
    }
    
    @Override
    public void onQsDown() {
        this.mInternalFalsingManager.onQsDown();
    }
    
    @Override
    public void onScreenOff() {
        this.mInternalFalsingManager.onScreenOff();
    }
    
    @Override
    public void onScreenOnFromTouch() {
        this.mInternalFalsingManager.onScreenOnFromTouch();
    }
    
    @Override
    public void onScreenTurningOn() {
        this.mInternalFalsingManager.onScreenTurningOn();
    }
    
    @Override
    public void onStartExpandingFromPulse() {
        this.mInternalFalsingManager.onStartExpandingFromPulse();
    }
    
    @Override
    public void onSuccessfulUnlock() {
        this.mInternalFalsingManager.onSuccessfulUnlock();
    }
    
    @Override
    public void onTouchEvent(final MotionEvent motionEvent, final int n, final int n2) {
        this.mInternalFalsingManager.onTouchEvent(motionEvent, n, n2);
    }
    
    @Override
    public void onTrackingStarted(final boolean b) {
        this.mInternalFalsingManager.onTrackingStarted(b);
    }
    
    @Override
    public void onTrackingStopped() {
        this.mInternalFalsingManager.onTrackingStopped();
    }
    
    @Override
    public void onUnlockHintStarted() {
        this.mInternalFalsingManager.onUnlockHintStarted();
    }
    
    @Override
    public Uri reportRejectedTouch() {
        return this.mInternalFalsingManager.reportRejectedTouch();
    }
    
    @Override
    public void setNotificationExpanded() {
        this.mInternalFalsingManager.setNotificationExpanded();
    }
    
    @Override
    public void setQsExpanded(final boolean qsExpanded) {
        this.mInternalFalsingManager.setQsExpanded(qsExpanded);
    }
    
    @Override
    public void setShowingAod(final boolean showingAod) {
        this.mInternalFalsingManager.setShowingAod(showingAod);
    }
    
    @Override
    public boolean shouldEnforceBouncer() {
        return this.mInternalFalsingManager.shouldEnforceBouncer();
    }
}
