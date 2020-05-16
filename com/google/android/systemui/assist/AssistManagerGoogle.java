// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist;

import com.android.systemui.shared.system.QuickStepContract;
import com.android.internal.app.IVoiceInteractionSessionListener;
import android.os.RemoteException;
import android.os.Bundle;
import com.android.internal.app.IVoiceInteractionSessionListener$Stub;
import com.android.internal.logging.MetricsLogger;
import android.metrics.LogMaker;
import java.util.Objects;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.model.SysUiState;
import dagger.Lazy;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.assist.PhoneStateMonitor;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.assist.AssistHandleBehaviorController;
import com.android.internal.app.AssistUtils;
import android.content.Context;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.os.Handler;
import com.google.android.systemui.assist.uihints.NgaUiController;
import com.google.android.systemui.assist.uihints.NgaMessageHandler;
import com.google.android.systemui.assist.uihints.GoogleDefaultUiController;
import com.google.android.systemui.assist.uihints.AssistantPresenceHandler;
import com.android.systemui.assist.AssistManager;

public class AssistManagerGoogle extends AssistManager
{
    private final AssistantPresenceHandler mAssistantPresenceHandler;
    private boolean mCheckAssistantStatus;
    private final GoogleDefaultUiController mDefaultUiController;
    private boolean mGoogleIsAssistant;
    private int mNavigationMode;
    private boolean mNgaIsAssistant;
    private final NgaMessageHandler mNgaMessageHandler;
    private final NgaUiController mNgaUiController;
    private final Runnable mOnProcessBundle;
    private final OpaEnabledReceiver mOpaEnabledReceiver;
    private boolean mSqueezeSetUp;
    private UiController mUiController;
    private final Handler mUiHandler;
    
    public AssistManagerGoogle(final DeviceProvisionedController deviceProvisionedController, final Context context, final AssistUtils assistUtils, final AssistHandleBehaviorController assistHandleBehaviorController, final NgaUiController mNgaUiController, final CommandQueue commandQueue, final BroadcastDispatcher broadcastDispatcher, final PhoneStateMonitor phoneStateMonitor, final OverviewProxyService overviewProxyService, final OpaEnabledDispatcher opaEnabledDispatcher, final KeyguardUpdateMonitor keyguardUpdateMonitor, final NavigationModeController navigationModeController, final ConfigurationController configurationController, final AssistantPresenceHandler mAssistantPresenceHandler, final NgaMessageHandler mNgaMessageHandler, final Lazy<SysUiState> lazy, final Handler mUiHandler) {
        super(deviceProvisionedController, context, assistUtils, assistHandleBehaviorController, commandQueue, phoneStateMonitor, overviewProxyService, configurationController, lazy);
        this.mCheckAssistantStatus = true;
        this.mUiHandler = mUiHandler;
        this.mOpaEnabledReceiver = new OpaEnabledReceiver(super.mContext, broadcastDispatcher);
        this.addOpaEnabledListener(opaEnabledDispatcher);
        keyguardUpdateMonitor.registerCallback(new KeyguardUpdateMonitorCallback() {
            @Override
            public void onUserSwitching(final int n) {
                AssistManagerGoogle.this.mOpaEnabledReceiver.onUserSwitching(n);
            }
        });
        this.mNgaUiController = mNgaUiController;
        final GoogleDefaultUiController googleDefaultUiController = new GoogleDefaultUiController(context);
        this.mDefaultUiController = googleDefaultUiController;
        this.mUiController = googleDefaultUiController;
        this.mNavigationMode = navigationModeController.addListener((NavigationModeController.ModeChangedListener)new _$$Lambda$AssistManagerGoogle$k2PE_qPUIsmOHQ2_0jIJz3Ie_bA(this));
        (this.mAssistantPresenceHandler = mAssistantPresenceHandler).registerAssistantPresenceChangeListener((AssistantPresenceHandler.AssistantPresenceChangeListener)new _$$Lambda$AssistManagerGoogle$r5RJVCyHmM_pcostcR1_qOCuIRs(this));
        this.mNgaMessageHandler = mNgaMessageHandler;
        this.mOnProcessBundle = new _$$Lambda$AssistManagerGoogle$lZldOw7dlOctXBnygV9UN_1l9vU(this);
    }
    
    private void checkSqueezeGestureStatus() {
        final ContentResolver contentResolver = super.mContext.getContentResolver();
        boolean mSqueezeSetUp = false;
        if (Settings$Secure.getInt(contentResolver, "assist_gesture_setup_complete", 0) == 1) {
            mSqueezeSetUp = true;
        }
        this.mSqueezeSetUp = mSqueezeSetUp;
    }
    
    public void addOpaEnabledListener(final OpaEnabledListener opaEnabledListener) {
        this.mOpaEnabledReceiver.addOpaEnabledListener(opaEnabledListener);
    }
    
    public void dispatchOpaEnabledState() {
        this.mOpaEnabledReceiver.dispatchOpaEnabledState();
    }
    
    public boolean isActiveAssistantNga() {
        return this.mNgaIsAssistant;
    }
    
    public void logStartAssist(int loggingSubType, int ngaAssistant) {
        this.mAssistantPresenceHandler.requestAssistantPresenceUpdate();
        this.mCheckAssistantStatus = false;
        loggingSubType = this.toLoggingSubType(loggingSubType, ngaAssistant);
        ngaAssistant = (this.mAssistantPresenceHandler.isNgaAssistant() ? 1 : 0);
        MetricsLogger.action(new LogMaker(1716).setType(1).setSubtype(ngaAssistant << 8 | loggingSubType));
    }
    
    @Override
    public void onGestureCompletion(final float n) {
        this.mCheckAssistantStatus = true;
        this.mUiController.onGestureCompletion(n / super.mContext.getResources().getDisplayMetrics().density);
    }
    
    @Override
    public void onInvocationProgress(final int n, final float n2) {
        if (n2 == 0.0f || n2 == 1.0f) {
            this.mCheckAssistantStatus = true;
            if (n == 2) {
                this.checkSqueezeGestureStatus();
            }
        }
        if (this.mCheckAssistantStatus) {
            this.mAssistantPresenceHandler.requestAssistantPresenceUpdate();
            this.mCheckAssistantStatus = false;
        }
        if (n != 2 || this.mSqueezeSetUp) {
            this.mUiController.onInvocationProgress(n, n2);
        }
    }
    
    @Override
    protected void registerVoiceInteractionSessionListener() {
        super.mAssistUtils.registerVoiceInteractionSessionListener((IVoiceInteractionSessionListener)new IVoiceInteractionSessionListener$Stub() {
            public void onSetUiHints(final Bundle bundle) {
                final String string = bundle.getString("action");
                if ("show_assist_handles".equals(string)) {
                    AssistManager.this.requestAssistHandles();
                    return;
                }
                if ("set_assist_gesture_constrained".equals(string)) {
                    final SysUiState sysUiState = AssistManagerGoogle.this.mSysUiState.get();
                    sysUiState.setFlag(8192, bundle.getBoolean("should_constrain", false));
                    sysUiState.commitUpdate(0);
                    return;
                }
                AssistManagerGoogle.this.mNgaMessageHandler.processBundle(bundle, AssistManagerGoogle.this.mOnProcessBundle);
            }
            
            public void onVoiceSessionHidden() throws RemoteException {
            }
            
            public void onVoiceSessionShown() throws RemoteException {
            }
        });
    }
    
    public boolean shouldShowOrb() {
        return false;
    }
    
    public boolean shouldUseHomeButtonAnimations() {
        return QuickStepContract.isGesturalMode(this.mNavigationMode) ^ true;
    }
}
