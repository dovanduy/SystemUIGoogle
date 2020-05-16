// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import android.os.IInterface;
import android.os.Looper;
import com.android.systemui.shared.system.QuickStepContract;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.content.IntentFilter;
import com.android.internal.policy.ScreenDecorationsUtils;
import android.os.IBinder;
import com.android.systemui.shared.recents.IPinnedStackAnimationListener;
import android.os.UserHandle;
import android.view.accessibility.AccessibilityManager;
import android.view.InputMonitor;
import android.os.Parcelable;
import android.hardware.input.InputManager;
import android.graphics.Insets;
import android.graphics.Bitmap;
import java.util.function.Function;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Log;
import android.app.ActivityTaskManager;
import java.util.function.Consumer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.graphics.Rect;
import java.util.ArrayList;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.model.SysUiState;
import com.android.systemui.shared.recents.ISystemUiProxy;
import com.android.systemui.statusbar.phone.StatusBarWindowCallback;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import com.android.internal.util.ScreenshotHelper;
import android.content.ComponentName;
import android.content.Intent;
import com.android.systemui.pip.PipUI;
import android.os.IBinder$DeathRecipient;
import android.content.ServiceConnection;
import com.android.systemui.shared.recents.IOverviewProxy;
import com.android.systemui.statusbar.NavigationBarController;
import android.content.BroadcastReceiver;
import android.os.Handler;
import com.android.systemui.stackdivider.Divider;
import java.util.Optional;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.content.Context;
import java.util.List;
import android.graphics.Region;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.policy.CallbackController;

public class OverviewProxyService implements CallbackController<OverviewProxyListener>, ModeChangedListener, Dumpable
{
    private Region mActiveNavBarRegion;
    private boolean mBound;
    private int mConnectionBackoffAttempts;
    private final List<OverviewProxyListener> mConnectionCallbacks;
    private final Runnable mConnectionRunnable;
    private final Context mContext;
    private int mCurrentBoundedUserId;
    private final Runnable mDeferredConnectionCallback;
    private final DeviceProvisionedController.DeviceProvisionedListener mDeviceProvisionedCallback;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private final Optional<Divider> mDividerOptional;
    private final Handler mHandler;
    private long mInputFocusTransferStartMillis;
    private float mInputFocusTransferStartY;
    private boolean mInputFocusTransferStarted;
    private boolean mIsEnabled;
    private final BroadcastReceiver mLauncherStateChangedReceiver;
    private float mNavBarButtonAlpha;
    private final NavigationBarController mNavBarController;
    private int mNavBarMode;
    private IOverviewProxy mOverviewProxy;
    private final ServiceConnection mOverviewServiceConnection;
    private final IBinder$DeathRecipient mOverviewServiceDeathRcpt;
    private final PipUI mPipUI;
    private final Intent mQuickStepIntent;
    private final ComponentName mRecentsComponentName;
    private final ScreenshotHelper mScreenshotHelper;
    private final Optional<Lazy<StatusBar>> mStatusBarOptionalLazy;
    private final NotificationShadeWindowController mStatusBarWinController;
    private final StatusBarWindowCallback mStatusBarWindowCallback;
    private boolean mSupportsRoundedCornersOnWindows;
    private ISystemUiProxy mSysUiProxy;
    private SysUiState mSysUiState;
    private float mWindowCornerRadius;
    
    public OverviewProxyService(final Context mContext, final CommandQueue commandQueue, final DeviceProvisionedController mDeviceProvisionedController, final NavigationBarController mNavBarController, final NavigationModeController navigationModeController, final NotificationShadeWindowController mStatusBarWinController, final SysUiState mSysUiState, final PipUI mPipUI, final Optional<Divider> mDividerOptional, final Optional<Lazy<StatusBar>> mStatusBarOptionalLazy) {
        this.mConnectionRunnable = new _$$Lambda$OverviewProxyService$2FrwSEVJnaHX9GGsAnD2I96htxU(this);
        this.mConnectionCallbacks = new ArrayList<OverviewProxyListener>();
        this.mCurrentBoundedUserId = -1;
        this.mNavBarMode = 0;
        this.mSysUiProxy = new ISystemUiProxy.Stub() {
            private boolean verifyCaller(final String str) {
                final int identifier = Binder.getCallingUserHandle().getIdentifier();
                if (identifier != OverviewProxyService.this.mCurrentBoundedUserId) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Launcher called sysui with invalid user: ");
                    sb.append(identifier);
                    sb.append(", reason: ");
                    sb.append(str);
                    Log.w("OverviewProxyService", sb.toString());
                    return false;
                }
                return true;
            }
            
            public Rect getNonMinimizedSplitScreenSecondaryBounds() {
                if (!this.verifyCaller("getNonMinimizedSplitScreenSecondaryBounds")) {
                    return null;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return OverviewProxyService.this.mDividerOptional.map((Function<? super Object, ? extends Rect>)_$$Lambda$OverviewProxyService$1$jWyXSUssf3YIGp2Ozuegdbo3RQM.INSTANCE).orElse(null);
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void handleImageAsScreenshot(final Bitmap bitmap, final Rect rect, final Insets insets, final int n) {
                OverviewProxyService.this.mScreenshotHelper.provideScreenshot(bitmap, rect, insets, n, OverviewProxyService.this.mHandler, (Consumer)null);
            }
            
            public Bundle monitorGestureInput(final String s, final int n) {
                if (!this.verifyCaller("monitorGestureInput")) {
                    return null;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    final InputMonitor monitorGestureInput = InputManager.getInstance().monitorGestureInput(s, n);
                    final Bundle bundle = new Bundle();
                    bundle.putParcelable("extra_input_monitor", (Parcelable)monitorGestureInput);
                    return bundle;
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void notifyAccessibilityButtonClicked(final int n) {
                if (!this.verifyCaller("notifyAccessibilityButtonClicked")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    AccessibilityManager.getInstance(OverviewProxyService.this.mContext).notifyAccessibilityButtonClicked(n);
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void notifyAccessibilityButtonLongClicked() {
                if (!this.verifyCaller("notifyAccessibilityButtonLongClicked")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    final Intent intent = new Intent("com.android.internal.intent.action.CHOOSE_ACCESSIBILITY_BUTTON");
                    intent.addFlags(268468224);
                    intent.putExtra("com.android.internal.intent.extra.SHORTCUT_TYPE", 0);
                    OverviewProxyService.this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void notifySwipeToHomeFinished() {
                if (!this.verifyCaller("notifySwipeToHomeFinished")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mPipUI.setPinnedStackAnimationType(1);
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void onAssistantGestureCompletion(final float n) {
                if (!this.verifyCaller("onAssistantGestureCompletion")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post((Runnable)new _$$Lambda$OverviewProxyService$1$9Su7WjOgAjqw2JOgB_qgqoRaUIY(this, n));
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void onAssistantProgress(final float n) {
                if (!this.verifyCaller("onAssistantProgress")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post((Runnable)new _$$Lambda$OverviewProxyService$1$x61OGopTSUwfaMsTcGtlGuJLnog(this, n));
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void onOverviewShown(final boolean b) {
                if (!this.verifyCaller("onOverviewShown")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post((Runnable)new _$$Lambda$OverviewProxyService$1$4FVtgzFdKl6xTtmcCi3ZqBrQngY(this, b));
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void onQuickSwitchToNewTask(final int n) {
                if (!this.verifyCaller("onQuickSwitchToNewTask")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post((Runnable)new _$$Lambda$OverviewProxyService$1$tuK3db_PKF7Z0p3Tp2hFfoHgKRU(this, n));
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void onSplitScreenInvoked() {
                if (!this.verifyCaller("onSplitScreenInvoked")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mDividerOptional.ifPresent((Consumer)_$$Lambda$xuXEcdh0HmTmuN4e7qU9mBkM36M.INSTANCE);
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void onStatusBarMotionEvent(final MotionEvent motionEvent) {
                if (!this.verifyCaller("onStatusBarMotionEvent")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mStatusBarOptionalLazy.ifPresent(new _$$Lambda$OverviewProxyService$1$_sWJd6osdRGJYQOQo8d7uwS1fPg(this, motionEvent));
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void setBackButtonAlpha(final float n, final boolean b) {
                this.setNavBarButtonAlpha(n, b);
            }
            
            public void setNavBarButtonAlpha(final float n, final boolean b) {
                if (!this.verifyCaller("setNavBarButtonAlpha")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mNavBarButtonAlpha = n;
                    OverviewProxyService.this.mHandler.post((Runnable)new _$$Lambda$OverviewProxyService$1$JeSA_8M36F8qXtmvuNJjrSs1G_E(this, n, b));
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void setPinnedStackAnimationListener(final IPinnedStackAnimationListener pinnedStackAnimationListener) {
                if (!this.verifyCaller("setPinnedStackAnimationListener")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mPipUI.setPinnedStackAnimationListener(pinnedStackAnimationListener);
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void setShelfHeight(final boolean b, final int n) {
                if (!this.verifyCaller("setShelfHeight")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mPipUI.setShelfHeight(b, n);
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void setSplitScreenMinimized(final boolean minimized) {
                final Divider divider = OverviewProxyService.this.mDividerOptional.get();
                if (divider != null) {
                    divider.setMinimized(minimized);
                }
            }
            
            public void startAssistant(final Bundle bundle) {
                if (!this.verifyCaller("startAssistant")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post((Runnable)new _$$Lambda$OverviewProxyService$1$daY2UqZd3NsPXv_IP8cYjYZLB70(this, bundle));
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void startScreenPinning(final int n) {
                if (!this.verifyCaller("startScreenPinning")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post((Runnable)new _$$Lambda$OverviewProxyService$1$4SXWj0CMroT_CN5f_JJLswjoG60(this, n));
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            
            public void stopScreenPinning() {
                if (!this.verifyCaller("stopScreenPinning")) {
                    return;
                }
                final long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post((Runnable)_$$Lambda$OverviewProxyService$1$9uERjvGI5cZ0Wh2SqRhoEXg8wYk.INSTANCE);
                }
                finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        };
        this.mDeferredConnectionCallback = new _$$Lambda$OverviewProxyService$53s1j2vSUNo_EjM7u2nSTJl32gM(this);
        this.mLauncherStateChangedReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                OverviewProxyService.this.updateEnabledState();
                OverviewProxyService.this.startConnectionToCurrentUser();
            }
        };
        this.mOverviewServiceConnection = (ServiceConnection)new ServiceConnection() {
            public void onBindingDied(final ComponentName obj) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Binding died of '");
                sb.append(obj);
                sb.append("', try reconnecting");
                Log.w("OverviewProxyService", sb.toString());
                OverviewProxyService.this.mCurrentBoundedUserId = -1;
                OverviewProxyService.this.retryConnectionWithBackoff();
            }
            
            public void onNullBinding(final ComponentName obj) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Null binding of '");
                sb.append(obj);
                sb.append("', try reconnecting");
                Log.w("OverviewProxyService", sb.toString());
                OverviewProxyService.this.mCurrentBoundedUserId = -1;
                OverviewProxyService.this.retryConnectionWithBackoff();
            }
            
            public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
                Log.d("OverviewProxyService", "Overview proxy service connected");
                OverviewProxyService.this.mConnectionBackoffAttempts = 0;
                OverviewProxyService.this.mHandler.removeCallbacks(OverviewProxyService.this.mDeferredConnectionCallback);
                try {
                    binder.linkToDeath(OverviewProxyService.this.mOverviewServiceDeathRcpt, 0);
                    final OverviewProxyService this$0 = OverviewProxyService.this;
                    this$0.mCurrentBoundedUserId = this$0.mDeviceProvisionedController.getCurrentUser();
                    OverviewProxyService.this.mOverviewProxy = IOverviewProxy.Stub.asInterface(binder);
                    final Bundle bundle = new Bundle();
                    bundle.putBinder("extra_sysui_proxy", ((IInterface)OverviewProxyService.this.mSysUiProxy).asBinder());
                    bundle.putFloat("extra_window_corner_radius", OverviewProxyService.this.mWindowCornerRadius);
                    bundle.putBoolean("extra_supports_window_corners", OverviewProxyService.this.mSupportsRoundedCornersOnWindows);
                    try {
                        OverviewProxyService.this.mOverviewProxy.onInitialize(bundle);
                    }
                    catch (RemoteException ex) {
                        OverviewProxyService.this.mCurrentBoundedUserId = -1;
                        Log.e("OverviewProxyService", "Failed to call onInitialize()", (Throwable)ex);
                    }
                    OverviewProxyService.this.dispatchNavButtonBounds();
                    OverviewProxyService.this.updateSystemUiStateFlags();
                    OverviewProxyService.this.notifyConnectionChanged();
                }
                catch (RemoteException ex2) {
                    Log.e("OverviewProxyService", "Lost connection to launcher service", (Throwable)ex2);
                    OverviewProxyService.this.disconnectFromLauncherService();
                    OverviewProxyService.this.retryConnectionWithBackoff();
                }
            }
            
            public void onServiceDisconnected(final ComponentName componentName) {
                OverviewProxyService.this.mCurrentBoundedUserId = -1;
            }
        };
        this.mDeviceProvisionedCallback = new DeviceProvisionedController.DeviceProvisionedListener() {
            @Override
            public void onUserSetupChanged() {
                if (OverviewProxyService.this.mDeviceProvisionedController.isCurrentUserSetup()) {
                    OverviewProxyService.this.internalConnectToCurrentUser();
                }
            }
            
            @Override
            public void onUserSwitched() {
                OverviewProxyService.this.mConnectionBackoffAttempts = 0;
                OverviewProxyService.this.internalConnectToCurrentUser();
            }
        };
        this.mStatusBarWindowCallback = new _$$Lambda$OverviewProxyService$b7uhSpdl46tRQQQT8ZW7Bieyg6A(this);
        this.mOverviewServiceDeathRcpt = (IBinder$DeathRecipient)new _$$Lambda$FF1twVzMKp_FAsQO2IsbqUbCb_s(this);
        this.mContext = mContext;
        this.mPipUI = mPipUI;
        this.mStatusBarOptionalLazy = mStatusBarOptionalLazy;
        this.mHandler = new Handler();
        this.mNavBarController = mNavBarController;
        this.mStatusBarWinController = mStatusBarWinController;
        this.mDeviceProvisionedController = mDeviceProvisionedController;
        this.mConnectionBackoffAttempts = 0;
        this.mDividerOptional = mDividerOptional;
        this.mRecentsComponentName = ComponentName.unflattenFromString(mContext.getString(17039942));
        this.mQuickStepIntent = new Intent("android.intent.action.QUICKSTEP_SERVICE").setPackage(this.mRecentsComponentName.getPackageName());
        this.mWindowCornerRadius = ScreenDecorationsUtils.getWindowCornerRadius(this.mContext.getResources());
        this.mSupportsRoundedCornersOnWindows = ScreenDecorationsUtils.supportsRoundedCornersOnWindows(this.mContext.getResources());
        (this.mSysUiState = mSysUiState).addCallback((SysUiState.SysUiStateCallback)new _$$Lambda$OverviewProxyService$UsZDbsgQ2Qpz6L03F4_TRLuFj_w(this));
        this.mNavBarButtonAlpha = 1.0f;
        this.mNavBarMode = navigationModeController.addListener((NavigationModeController.ModeChangedListener)this);
        this.updateEnabledState();
        this.mDeviceProvisionedController.addCallback(this.mDeviceProvisionedCallback);
        final IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        intentFilter.addDataSchemeSpecificPart(this.mRecentsComponentName.getPackageName(), 0);
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        this.mContext.registerReceiver(this.mLauncherStateChangedReceiver, intentFilter);
        mStatusBarWinController.registerCallback(this.mStatusBarWindowCallback);
        this.mScreenshotHelper = new ScreenshotHelper(mContext);
        commandQueue.addCallback((CommandQueue.Callbacks)new CommandQueue.Callbacks() {
            @Override
            public void onTracingStateChanged(final boolean b) {
                final SysUiState access$3200 = OverviewProxyService.this.mSysUiState;
                access$3200.setFlag(4096, b);
                access$3200.commitUpdate(OverviewProxyService.this.mContext.getDisplayId());
            }
        });
    }
    
    private void disconnectFromLauncherService() {
        if (this.mBound) {
            this.mContext.unbindService(this.mOverviewServiceConnection);
            this.mBound = false;
        }
        final IOverviewProxy mOverviewProxy = this.mOverviewProxy;
        if (mOverviewProxy != null) {
            ((IInterface)mOverviewProxy).asBinder().unlinkToDeath(this.mOverviewServiceDeathRcpt, 0);
            this.mOverviewProxy = null;
            this.notifyNavBarButtonAlphaChanged(1.0f, false);
            this.notifyConnectionChanged();
        }
    }
    
    private void dispatchNavButtonBounds() {
        final IOverviewProxy mOverviewProxy = this.mOverviewProxy;
        if (mOverviewProxy != null) {
            final Region mActiveNavBarRegion = this.mActiveNavBarRegion;
            if (mActiveNavBarRegion != null) {
                try {
                    mOverviewProxy.onActiveNavBarRegionChanges(mActiveNavBarRegion);
                }
                catch (RemoteException ex) {
                    Log.e("OverviewProxyService", "Failed to call onActiveNavBarRegionChanges()", (Throwable)ex);
                }
            }
        }
    }
    
    private void internalConnectToCurrentUser() {
        this.disconnectFromLauncherService();
        if (this.mDeviceProvisionedController.isCurrentUserSetup() && this.isEnabled()) {
            this.mHandler.removeCallbacks(this.mConnectionRunnable);
            final Intent setPackage = new Intent("android.intent.action.QUICKSTEP_SERVICE").setPackage(this.mRecentsComponentName.getPackageName());
            try {
                this.mBound = this.mContext.bindServiceAsUser(setPackage, this.mOverviewServiceConnection, 33554433, UserHandle.of(this.mDeviceProvisionedController.getCurrentUser()));
            }
            catch (SecurityException ex) {
                Log.e("OverviewProxyService", "Unable to bind because of security error", (Throwable)ex);
            }
            if (this.mBound) {
                this.mHandler.postDelayed(this.mDeferredConnectionCallback, 5000L);
            }
            else {
                this.retryConnectionWithBackoff();
            }
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Cannot attempt connection, is setup ");
        sb.append(this.mDeviceProvisionedController.isCurrentUserSetup());
        sb.append(", is enabled ");
        sb.append(this.isEnabled());
        Log.v("OverviewProxyService", sb.toString());
    }
    
    private void notifyAssistantGestureCompletion(final float n) {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; --i) {
            this.mConnectionCallbacks.get(i).onAssistantGestureCompletion(n);
        }
    }
    
    private void notifyAssistantProgress(final float n) {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; --i) {
            this.mConnectionCallbacks.get(i).onAssistantProgress(n);
        }
    }
    
    private void notifyConnectionChanged() {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; --i) {
            this.mConnectionCallbacks.get(i).onConnectionChanged(this.mOverviewProxy != null);
        }
    }
    
    private void notifyNavBarButtonAlphaChanged(final float n, final boolean b) {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; --i) {
            this.mConnectionCallbacks.get(i).onNavBarButtonAlphaChanged(n, b);
        }
    }
    
    private void notifyQuickSwitchToNewTask(final int n) {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; --i) {
            this.mConnectionCallbacks.get(i).onQuickSwitchToNewTask(n);
        }
    }
    
    private void notifyStartAssistant(final Bundle bundle) {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; --i) {
            this.mConnectionCallbacks.get(i).startAssistant(bundle);
        }
    }
    
    private void notifySystemUiStateFlags(final int i) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Notifying sysui state change to overview service: proxy=");
        sb.append(this.mOverviewProxy);
        sb.append(" flags=");
        sb.append(i);
        Log.d("OverviewProxyService", sb.toString());
        try {
            if (this.mOverviewProxy != null) {
                this.mOverviewProxy.onSystemUiStateChanged(i);
            }
        }
        catch (RemoteException ex) {
            Log.e("OverviewProxyService", "Failed to notify sysui state change", (Throwable)ex);
        }
    }
    
    private void onStatusBarStateChanged(final boolean b, final boolean b2, final boolean b3) {
        final SysUiState mSysUiState = this.mSysUiState;
        final boolean b4 = true;
        mSysUiState.setFlag(64, b && !b2);
        mSysUiState.setFlag(512, b && b2 && b4);
        mSysUiState.setFlag(8, b3);
        mSysUiState.commitUpdate(this.mContext.getDisplayId());
    }
    
    private void retryConnectionWithBackoff() {
        if (this.mHandler.hasCallbacks(this.mConnectionRunnable)) {
            return;
        }
        final long lng = (long)Math.min(Math.scalb(1000.0f, this.mConnectionBackoffAttempts), 600000.0f);
        this.mHandler.postDelayed(this.mConnectionRunnable, lng);
        ++this.mConnectionBackoffAttempts;
        final StringBuilder sb = new StringBuilder();
        sb.append("Failed to connect on attempt ");
        sb.append(this.mConnectionBackoffAttempts);
        sb.append(" will try again in ");
        sb.append(lng);
        sb.append("ms");
        Log.w("OverviewProxyService", sb.toString());
    }
    
    private void updateEnabledState() {
        this.mIsEnabled = (this.mContext.getPackageManager().resolveServiceAsUser(this.mQuickStepIntent, 1048576, ActivityManagerWrapper.getInstance().getCurrentUserId()) != null);
    }
    
    private void updateSystemUiStateFlags() {
        final NavigationBarFragment defaultNavigationBarFragment = this.mNavBarController.getDefaultNavigationBarFragment();
        final NavigationBarView navigationBarView = this.mNavBarController.getNavigationBarView(this.mContext.getDisplayId());
        final StringBuilder sb = new StringBuilder();
        sb.append("Updating sysui state flags: navBarFragment=");
        sb.append(defaultNavigationBarFragment);
        sb.append(" navBarView=");
        sb.append(navigationBarView);
        Log.d("OverviewProxyService", sb.toString());
        if (defaultNavigationBarFragment != null) {
            defaultNavigationBarFragment.updateSystemUiStateFlags(-1);
        }
        if (navigationBarView != null) {
            navigationBarView.updatePanelSystemUiStateFlags();
            navigationBarView.updateDisabledSystemUiStateFlags();
        }
        final NotificationShadeWindowController mStatusBarWinController = this.mStatusBarWinController;
        if (mStatusBarWinController != null) {
            mStatusBarWinController.notifyStateChangedCallbacks();
        }
    }
    
    @Override
    public void addCallback(final OverviewProxyListener overviewProxyListener) {
        this.mConnectionCallbacks.add(overviewProxyListener);
        overviewProxyListener.onConnectionChanged(this.mOverviewProxy != null);
        overviewProxyListener.onNavBarButtonAlphaChanged(this.mNavBarButtonAlpha, false);
    }
    
    public void cleanupAfterDeath() {
        if (this.mInputFocusTransferStarted) {
            this.mHandler.post((Runnable)new _$$Lambda$OverviewProxyService$PSR8w04DgkmYl0QS7DaTBJbM_iU(this));
        }
        this.startConnectionToCurrentUser();
        final Divider divider = this.mDividerOptional.get();
        if (divider != null) {
            divider.setMinimized(false);
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("OverviewProxyService state:");
        printWriter.print("  recentsComponentName=");
        printWriter.println(this.mRecentsComponentName);
        printWriter.print("  isConnected=");
        printWriter.println(this.mOverviewProxy != null);
        printWriter.print("  isCurrentUserSetup=");
        printWriter.println(this.mDeviceProvisionedController.isCurrentUserSetup());
        printWriter.print("  connectionBackoffAttempts=");
        printWriter.println(this.mConnectionBackoffAttempts);
        printWriter.print("  quickStepIntent=");
        printWriter.println(this.mQuickStepIntent);
        printWriter.print("  quickStepIntentResolved=");
        printWriter.println(this.isEnabled());
        this.mSysUiState.dump(fileDescriptor, printWriter, array);
        printWriter.print(" mInputFocusTransferStarted=");
        printWriter.println(this.mInputFocusTransferStarted);
    }
    
    public IOverviewProxy getProxy() {
        return this.mOverviewProxy;
    }
    
    public boolean isEnabled() {
        return this.mIsEnabled;
    }
    
    public void notifyAssistantVisibilityChanged(final float n) {
        try {
            if (this.mOverviewProxy != null) {
                this.mOverviewProxy.onAssistantVisibilityChanged(n);
            }
            else {
                Log.e("OverviewProxyService", "Failed to get overview proxy for assistant visibility.");
            }
        }
        catch (RemoteException ex) {
            Log.e("OverviewProxyService", "Failed to call onAssistantVisibilityChanged()", (Throwable)ex);
        }
    }
    
    public void notifyBackAction(final boolean b, final int n, final int n2, final boolean b2, final boolean b3) {
        try {
            if (this.mOverviewProxy != null) {
                this.mOverviewProxy.onBackAction(b, n, n2, b2, b3);
            }
        }
        catch (RemoteException ex) {
            Log.e("OverviewProxyService", "Failed to notify back action", (Throwable)ex);
        }
    }
    
    public void onActiveNavBarRegionChanges(final Region mActiveNavBarRegion) {
        this.mActiveNavBarRegion = mActiveNavBarRegion;
        this.dispatchNavButtonBounds();
    }
    
    @Override
    public void onNavigationModeChanged(final int mNavBarMode) {
        this.mNavBarMode = mNavBarMode;
    }
    
    @Override
    public void removeCallback(final OverviewProxyListener overviewProxyListener) {
        this.mConnectionCallbacks.remove(overviewProxyListener);
    }
    
    public boolean shouldShowSwipeUpUI() {
        return this.isEnabled() && !QuickStepContract.isLegacyMode(this.mNavBarMode);
    }
    
    public void startConnectionToCurrentUser() {
        if (this.mHandler.getLooper() != Looper.myLooper()) {
            this.mHandler.post(this.mConnectionRunnable);
        }
        else {
            this.internalConnectToCurrentUser();
        }
    }
    
    public interface OverviewProxyListener
    {
        default void onAssistantGestureCompletion(final float n) {
        }
        
        default void onAssistantProgress(final float n) {
        }
        
        default void onConnectionChanged(final boolean b) {
        }
        
        default void onNavBarButtonAlphaChanged(final float n, final boolean b) {
        }
        
        default void onOverviewShown(final boolean b) {
        }
        
        default void onQuickSwitchToNewTask(final int n) {
        }
        
        default void startAssistant(final Bundle bundle) {
        }
    }
}
