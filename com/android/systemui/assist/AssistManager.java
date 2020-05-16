// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import android.os.SystemClock;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.res.Resources$NotFoundException;
import android.widget.ImageView;
import com.android.internal.app.IVoiceInteractionSessionListener;
import com.android.internal.app.IVoiceInteractionSessionListener$Stub;
import com.android.internal.logging.MetricsLogger;
import android.metrics.LogMaker;
import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import android.os.IBinder;
import android.content.ActivityNotFoundException;
import android.util.Log;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.content.Intent;
import android.app.ActivityOptions;
import com.android.systemui.R$anim;
import android.app.SearchManager;
import android.provider.Settings$Secure;
import android.os.Bundle;
import android.os.Binder;
import com.android.systemui.R$dimen;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.content.ComponentName;
import android.view.WindowManager$LayoutParams;
import com.android.systemui.assist.ui.DefaultUiController;
import android.app.ActivityManager;
import android.os.Handler;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.View;
import android.content.res.Configuration;
import android.os.RemoteException;
import com.android.internal.app.IVoiceInteractionSessionShowCallback$Stub;
import com.android.systemui.recents.OverviewProxyService;
import android.view.WindowManager;
import com.android.systemui.model.SysUiState;
import dagger.Lazy;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.settingslib.applications.InterestingConfigChanges;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.internal.app.AssistUtils;

public class AssistManager
{
    private final AssistDisclosure mAssistDisclosure;
    protected final AssistUtils mAssistUtils;
    private final CommandQueue mCommandQueue;
    private ConfigurationController.ConfigurationListener mConfigurationListener;
    protected final Context mContext;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private final AssistHandleBehaviorController mHandleController;
    private Runnable mHideRunnable;
    private final InterestingConfigChanges mInterestingConfigChanges;
    private final PhoneStateMonitor mPhoneStateMonitor;
    private final boolean mShouldEnableOrb;
    private IVoiceInteractionSessionShowCallback mShowCallback;
    protected final Lazy<SysUiState> mSysUiState;
    private final UiController mUiController;
    private AssistOrbContainer mView;
    private final WindowManager mWindowManager;
    
    public AssistManager(final DeviceProvisionedController mDeviceProvisionedController, final Context mContext, final AssistUtils mAssistUtils, final AssistHandleBehaviorController mHandleController, final CommandQueue mCommandQueue, final PhoneStateMonitor mPhoneStateMonitor, final OverviewProxyService overviewProxyService, final ConfigurationController configurationController, final Lazy<SysUiState> mSysUiState) {
        this.mShowCallback = (IVoiceInteractionSessionShowCallback)new IVoiceInteractionSessionShowCallback$Stub() {
            public void onFailed() throws RemoteException {
                AssistManager.this.mView.post(AssistManager.this.mHideRunnable);
            }
            
            public void onShown() throws RemoteException {
                AssistManager.this.mView.post(AssistManager.this.mHideRunnable);
            }
        };
        this.mHideRunnable = new Runnable() {
            @Override
            public void run() {
                AssistManager.this.mView.removeCallbacks((Runnable)this);
                AssistManager.this.mView.show(false, true);
            }
        };
        this.mConfigurationListener = new ConfigurationController.ConfigurationListener() {
            @Override
            public void onConfigChanged(final Configuration configuration) {
                if (!AssistManager.this.mInterestingConfigChanges.applyNewConfig(AssistManager.this.mContext.getResources())) {
                    return;
                }
                boolean showing;
                if (AssistManager.this.mView != null) {
                    showing = AssistManager.this.mView.isShowing();
                    AssistManager.this.mWindowManager.removeView((View)AssistManager.this.mView);
                }
                else {
                    showing = false;
                }
                final AssistManager this$0 = AssistManager.this;
                this$0.mView = (AssistOrbContainer)LayoutInflater.from(this$0.mContext).inflate(R$layout.assist_orb, (ViewGroup)null);
                AssistManager.this.mView.setVisibility(8);
                AssistManager.this.mView.setSystemUiVisibility(1792);
                AssistManager.this.mWindowManager.addView((View)AssistManager.this.mView, (ViewGroup$LayoutParams)AssistManager.this.getLayoutParams());
                if (showing) {
                    AssistManager.this.mView.show(true, false);
                }
            }
        };
        this.mContext = mContext;
        this.mDeviceProvisionedController = mDeviceProvisionedController;
        this.mCommandQueue = mCommandQueue;
        this.mWindowManager = (WindowManager)mContext.getSystemService("window");
        this.mAssistUtils = mAssistUtils;
        this.mAssistDisclosure = new AssistDisclosure(mContext, new Handler());
        this.mPhoneStateMonitor = mPhoneStateMonitor;
        this.mHandleController = mHandleController;
        configurationController.addCallback(this.mConfigurationListener);
        this.registerVoiceInteractionSessionListener();
        this.mInterestingConfigChanges = new InterestingConfigChanges(-2147482748);
        this.mConfigurationListener.onConfigChanged(mContext.getResources().getConfiguration());
        this.mShouldEnableOrb = (ActivityManager.isLowRamDeviceStatic() ^ true);
        this.mUiController = (UiController)new DefaultUiController(this.mContext);
        this.mSysUiState = mSysUiState;
        overviewProxyService.addCallback((OverviewProxyService.OverviewProxyListener)new OverviewProxyService.OverviewProxyListener() {
            @Override
            public void onAssistantGestureCompletion(final float n) {
                AssistManager.this.onGestureCompletion(n);
            }
            
            @Override
            public void onAssistantProgress(final float n) {
                AssistManager.this.onInvocationProgress(1, n);
            }
        });
    }
    
    private ComponentName getAssistInfo() {
        return this.getAssistInfoForUser(KeyguardUpdateMonitor.getCurrentUser());
    }
    
    private WindowManager$LayoutParams getLayoutParams() {
        final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(-1, this.mContext.getResources().getDimensionPixelSize(R$dimen.assist_orb_scrim_height), 2033, 280, -3);
        windowManager$LayoutParams.token = (IBinder)new Binder();
        windowManager$LayoutParams.gravity = 8388691;
        windowManager$LayoutParams.setTitle((CharSequence)"AssistPreviewPanel");
        windowManager$LayoutParams.softInputMode = 49;
        return windowManager$LayoutParams;
    }
    
    private boolean isVoiceSessionRunning() {
        return this.mAssistUtils.isSessionRunning();
    }
    
    private void maybeSwapSearchIcon(final ComponentName componentName, final boolean b) {
        this.replaceDrawable(this.mView.getOrb().getLogo(), componentName, "com.android.systemui.action_assist_icon", b);
    }
    
    private void showOrb(final ComponentName componentName, final boolean b) {
        this.maybeSwapSearchIcon(componentName, b);
        if (this.mShouldEnableOrb) {
            this.mView.show(true, true);
        }
    }
    
    private void startAssistActivity(final Bundle bundle, final ComponentName component) {
        if (!this.mDeviceProvisionedController.isDeviceProvisioned()) {
            return;
        }
        final CommandQueue mCommandQueue = this.mCommandQueue;
        boolean b = false;
        mCommandQueue.animateCollapsePanels(3, false);
        if (Settings$Secure.getIntForUser(this.mContext.getContentResolver(), "assist_structure_enabled", 1, -2) != 0) {
            b = true;
        }
        final SearchManager searchManager = (SearchManager)this.mContext.getSystemService("search");
        if (searchManager == null) {
            return;
        }
        final Intent assistIntent = searchManager.getAssistIntent(b);
        if (assistIntent == null) {
            return;
        }
        assistIntent.setComponent(component);
        assistIntent.putExtras(bundle);
        if (b && AssistUtils.isDisclosureEnabled(this.mContext)) {
            this.showDisclosure();
        }
        try {
            final ActivityOptions customAnimation = ActivityOptions.makeCustomAnimation(this.mContext, R$anim.search_launch_enter, R$anim.search_launch_exit);
            assistIntent.addFlags(268435456);
            AsyncTask.execute((Runnable)new Runnable() {
                @Override
                public void run() {
                    AssistManager.this.mContext.startActivityAsUser(assistIntent, customAnimation.toBundle(), new UserHandle(-2));
                }
            });
        }
        catch (ActivityNotFoundException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Activity not found for ");
            sb.append(assistIntent.getAction());
            Log.w("AssistManager", sb.toString());
        }
    }
    
    private void startAssistInternal(final Bundle bundle, final ComponentName componentName, final boolean b) {
        if (b) {
            this.startVoiceInteractor(bundle);
        }
        else {
            this.startAssistActivity(bundle, componentName);
        }
    }
    
    private void startVoiceInteractor(final Bundle bundle) {
        this.mAssistUtils.showSessionForActiveService(bundle, 4, this.mShowCallback, (IBinder)null);
    }
    
    public boolean canVoiceAssistBeLaunchedFromKeyguard() {
        return DejankUtils.whitelistIpcs((Supplier<Boolean>)new _$$Lambda$AssistManager$vZFgoIPSjliTIYxUkbQNIugf3SA(this));
    }
    
    public long getAssistHandleShowAndGoRemainingDurationMs() {
        return this.mHandleController.getShowAndGoRemainingTimeMs();
    }
    
    public ComponentName getAssistInfoForUser(final int n) {
        return this.mAssistUtils.getAssistComponentForUser(n);
    }
    
    public ComponentName getVoiceInteractorComponentName() {
        return this.mAssistUtils.getActiveServiceComponentName();
    }
    
    public void hideAssist() {
        this.mAssistUtils.hideCurrentSession();
    }
    
    public void launchVoiceAssistFromKeyguard() {
        this.mAssistUtils.launchVoiceAssistFromKeyguard();
    }
    
    protected void logStartAssist(final int n, final int n2) {
        MetricsLogger.action(new LogMaker(1716).setType(1).setSubtype(this.toLoggingSubType(n, n2)));
    }
    
    public void onGestureCompletion(final float n) {
        this.mUiController.onGestureCompletion(n);
    }
    
    public void onInvocationProgress(final int n, final float n2) {
        this.mUiController.onInvocationProgress(n, n2);
    }
    
    public void onLockscreenShown() {
        AsyncTask.execute((Runnable)new Runnable() {
            @Override
            public void run() {
                AssistManager.this.mAssistUtils.onLockscreenShown();
            }
        });
    }
    
    protected void registerVoiceInteractionSessionListener() {
        this.mAssistUtils.registerVoiceInteractionSessionListener((IVoiceInteractionSessionListener)new IVoiceInteractionSessionListener$Stub() {
            public void onSetUiHints(final Bundle bundle) {
                final String string = bundle.getString("action");
                if ("show_assist_handles".equals(string)) {
                    AssistManager.this.requestAssistHandles();
                }
                else if ("set_assist_gesture_constrained".equals(string)) {
                    final SysUiState sysUiState = AssistManager.this.mSysUiState.get();
                    sysUiState.setFlag(8192, bundle.getBoolean("should_constrain", false));
                    sysUiState.commitUpdate(0);
                }
            }
            
            public void onVoiceSessionHidden() throws RemoteException {
            }
            
            public void onVoiceSessionShown() throws RemoteException {
            }
        });
    }
    
    public void replaceDrawable(final ImageView imageView, final ComponentName componentName, final String s, final boolean b) {
        if (componentName == null) {
            goto Label_0126;
        }
        try {
            final PackageManager packageManager = this.mContext.getPackageManager();
            Bundle bundle;
            if (b) {
                bundle = packageManager.getServiceInfo(componentName, 128).metaData;
            }
            else {
                bundle = packageManager.getActivityInfo(componentName, 128).metaData;
            }
            if (bundle == null) {
                goto Label_0126;
            }
            final int int1 = bundle.getInt(s);
            if (int1 != 0) {
                imageView.setImageDrawable(packageManager.getResourcesForApplication(componentName.getPackageName()).getDrawable(int1));
                return;
            }
            goto Label_0126;
        }
        catch (Resources$NotFoundException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Failed to swap drawable from ");
            sb.append(componentName.flattenToShortString());
            Log.w("AssistManager", sb.toString(), (Throwable)ex);
        }
        catch (PackageManager$NameNotFoundException ex2) {
            goto Label_0126;
        }
    }
    
    protected void requestAssistHandles() {
        this.mHandleController.onAssistHandlesRequested();
    }
    
    protected boolean shouldShowOrb() {
        return false;
    }
    
    public void showDisclosure() {
        this.mAssistDisclosure.postShow();
    }
    
    public void startAssist(final Bundle bundle) {
        final ComponentName assistInfo = this.getAssistInfo();
        if (assistInfo == null) {
            return;
        }
        final boolean equals = assistInfo.equals((Object)this.getVoiceInteractorComponentName());
        if (!equals || (!this.isVoiceSessionRunning() && this.shouldShowOrb())) {
            this.showOrb(assistInfo, equals);
            final AssistOrbContainer mView = this.mView;
            final Runnable mHideRunnable = this.mHideRunnable;
            long n;
            if (equals) {
                n = 2500L;
            }
            else {
                n = 1000L;
            }
            mView.postDelayed(mHideRunnable, n);
        }
        Bundle bundle2;
        if ((bundle2 = bundle) == null) {
            bundle2 = new Bundle();
        }
        final int int1 = bundle2.getInt("invocation_type", 0);
        if (int1 == 1) {
            this.mHandleController.onAssistantGesturePerformed();
        }
        final int phoneState = this.mPhoneStateMonitor.getPhoneState();
        bundle2.putInt("invocation_phone_state", phoneState);
        bundle2.putLong("invocation_time_ms", SystemClock.elapsedRealtime());
        this.logStartAssist(int1, phoneState);
        this.startAssistInternal(bundle2, assistInfo, equals);
    }
    
    public int toLoggingSubType(final int n) {
        return this.toLoggingSubType(n, this.mPhoneStateMonitor.getPhoneState());
    }
    
    protected final int toLoggingSubType(final int n, final int n2) {
        return ((this.mHandleController.areHandlesShowing() ^ true) ? 1 : 0) | n << 1 | n2 << 4;
    }
    
    public interface UiController
    {
        void hide();
        
        void onGestureCompletion(final float p0);
        
        void onInvocationProgress(final int p0, final float p1);
    }
}
