// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import android.net.Uri;
import android.view.MotionEvent;
import android.app.ActivityManager;
import java.io.PrintWriter;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.os.PowerManager;
import com.android.systemui.Dependency;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.hardware.biometrics.BiometricSourceType;
import com.android.systemui.statusbar.StatusBarState;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.os.Looper;
import java.util.concurrent.Executor;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.database.ContentObserver;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.os.Handler;
import com.android.systemui.analytics.DataCollector;
import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.plugins.FalsingManager;

public class FalsingManagerImpl implements FalsingManager
{
    private static final int[] CLASSIFIER_SENSORS;
    private static final int[] COLLECTOR_SENSORS;
    private final AccessibilityManager mAccessibilityManager;
    private boolean mBouncerOffOnDown;
    private boolean mBouncerOn;
    private final Context mContext;
    private final DataCollector mDataCollector;
    private boolean mEnforceBouncer;
    private final Handler mHandler;
    private final HumanInteractionClassifier mHumanInteractionClassifier;
    private int mIsFalseTouchCalls;
    private boolean mIsTouchScreen;
    private boolean mJustUnlockedWithFace;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateCallback;
    private MetricsLogger mMetricsLogger;
    private Runnable mPendingWtf;
    private boolean mScreenOn;
    private SensorEventListener mSensorEventListener;
    private final SensorManager mSensorManager;
    private boolean mSessionActive;
    protected final ContentObserver mSettingsObserver;
    private boolean mShowingAod;
    private int mState;
    public StatusBarStateController.StateListener mStatusBarStateListener;
    private final Executor mUiBgExecutor;
    
    static {
        CLASSIFIER_SENSORS = new int[] { 8 };
        COLLECTOR_SENSORS = new int[] { 1, 4, 8, 5, 11 };
    }
    
    FalsingManagerImpl(final Context mContext, final Executor mUiBgExecutor) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mEnforceBouncer = false;
        this.mBouncerOn = false;
        this.mBouncerOffOnDown = false;
        this.mSessionActive = false;
        this.mIsTouchScreen = true;
        this.mJustUnlockedWithFace = false;
        this.mState = 0;
        this.mSensorEventListener = (SensorEventListener)new SensorEventListener() {
            public void onAccuracyChanged(final Sensor sensor, final int n) {
                FalsingManagerImpl.this.mDataCollector.onAccuracyChanged(sensor, n);
            }
            
            public void onSensorChanged(final SensorEvent sensorEvent) {
                synchronized (this) {
                    FalsingManagerImpl.this.mDataCollector.onSensorChanged(sensorEvent);
                    FalsingManagerImpl.this.mHumanInteractionClassifier.onSensorChanged(sensorEvent);
                }
            }
        };
        this.mStatusBarStateListener = new StatusBarStateController.StateListener() {
            @Override
            public void onStateChanged(final int n) {
                if (FalsingLog.ENABLED) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("from=");
                    sb.append(StatusBarState.toShortString(FalsingManagerImpl.this.mState));
                    sb.append(" to=");
                    sb.append(StatusBarState.toShortString(n));
                    FalsingLog.i("setStatusBarState", sb.toString());
                }
                FalsingManagerImpl.this.mState = n;
                FalsingManagerImpl.this.updateSessionActive();
            }
        };
        this.mSettingsObserver = new ContentObserver(this.mHandler) {
            public void onChange(final boolean b) {
                FalsingManagerImpl.this.updateConfiguration();
            }
        };
        this.mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onBiometricAuthenticated(final int n, final BiometricSourceType biometricSourceType, final boolean b) {
                if (n == KeyguardUpdateMonitor.getCurrentUser() && biometricSourceType == BiometricSourceType.FACE) {
                    FalsingManagerImpl.this.mJustUnlockedWithFace = true;
                }
            }
        };
        this.mContext = mContext;
        this.mSensorManager = Dependency.get(AsyncSensorManager.class);
        this.mAccessibilityManager = (AccessibilityManager)mContext.getSystemService((Class)AccessibilityManager.class);
        this.mDataCollector = DataCollector.getInstance(this.mContext);
        this.mHumanInteractionClassifier = HumanInteractionClassifier.getInstance(this.mContext);
        this.mUiBgExecutor = mUiBgExecutor;
        this.mScreenOn = ((PowerManager)mContext.getSystemService((Class)PowerManager.class)).isInteractive();
        this.mMetricsLogger = new MetricsLogger();
        this.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("falsing_manager_enforce_bouncer"), false, this.mSettingsObserver, -1);
        this.updateConfiguration();
        Dependency.get(StatusBarStateController.class).addCallback(this.mStatusBarStateListener);
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mKeyguardUpdateCallback);
    }
    
    private void clearPendingWtf() {
        final Runnable mPendingWtf = this.mPendingWtf;
        if (mPendingWtf != null) {
            this.mHandler.removeCallbacks(mPendingWtf);
            this.mPendingWtf = null;
        }
    }
    
    private boolean isEnabled() {
        return this.mHumanInteractionClassifier.isEnabled() || this.mDataCollector.isEnabled();
    }
    
    private void onSessionStart() {
        if (FalsingLog.ENABLED) {
            final StringBuilder sb = new StringBuilder();
            sb.append("classifierEnabled=");
            sb.append(this.isClassifierEnabled());
            FalsingLog.i("onSessionStart", sb.toString());
            this.clearPendingWtf();
        }
        this.mBouncerOn = false;
        this.mSessionActive = true;
        this.mJustUnlockedWithFace = false;
        this.mIsFalseTouchCalls = 0;
        if (this.mHumanInteractionClassifier.isEnabled()) {
            this.registerSensors(FalsingManagerImpl.CLASSIFIER_SENSORS);
        }
        if (this.mDataCollector.isEnabledFull()) {
            this.registerSensors(FalsingManagerImpl.COLLECTOR_SENSORS);
        }
        if (this.mDataCollector.isEnabled()) {
            this.mDataCollector.onFalsingSessionStarted();
        }
    }
    
    private void registerSensors(final int[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            final Sensor defaultSensor = this.mSensorManager.getDefaultSensor(array[i]);
            if (defaultSensor != null) {
                this.mUiBgExecutor.execute(new _$$Lambda$FalsingManagerImpl$VJW_VOVtQGpUmd7AtKlCfAEhBZE(this, defaultSensor));
            }
        }
    }
    
    private boolean sessionEntrypoint() {
        if (!this.mSessionActive && this.shouldSessionBeActive()) {
            this.onSessionStart();
            return true;
        }
        return false;
    }
    
    private void sessionExitpoint(final boolean b) {
        if (this.mSessionActive && (b || !this.shouldSessionBeActive())) {
            this.mSessionActive = false;
            if (this.mIsFalseTouchCalls != 0) {
                if (FalsingLog.ENABLED) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Calls before failure: ");
                    sb.append(this.mIsFalseTouchCalls);
                    FalsingLog.i("isFalseTouchCalls", sb.toString());
                }
                this.mMetricsLogger.histogram("falsing_failure_after_attempts", this.mIsFalseTouchCalls);
                this.mIsFalseTouchCalls = 0;
            }
            this.mUiBgExecutor.execute(new _$$Lambda$FalsingManagerImpl$8SXkW2Wsm8XWKvooYKTPgEEzXnU(this));
        }
    }
    
    private boolean shouldSessionBeActive() {
        final boolean enabled = FalsingLog.ENABLED;
        final boolean enabled2 = this.isEnabled();
        boolean b = true;
        if (!enabled2 || !this.mScreenOn || this.mState != 1 || this.mShowingAod) {
            b = false;
        }
        return b;
    }
    
    private void updateConfiguration() {
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean mEnforceBouncer = false;
        if (Settings$Secure.getInt(contentResolver, "falsing_manager_enforce_bouncer", 0) != 0) {
            mEnforceBouncer = true;
        }
        this.mEnforceBouncer = mEnforceBouncer;
    }
    
    @Override
    public void cleanup() {
        this.mSensorManager.unregisterListener(this.mSensorEventListener);
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        Dependency.get(StatusBarStateController.class).removeCallback(this.mStatusBarStateListener);
        Dependency.get(KeyguardUpdateMonitor.class).removeCallback(this.mKeyguardUpdateCallback);
    }
    
    @Override
    public void dump(final PrintWriter printWriter) {
        printWriter.println("FALSING MANAGER");
        printWriter.print("classifierEnabled=");
        printWriter.println(this.isClassifierEnabled() ? 1 : 0);
        printWriter.print("mSessionActive=");
        printWriter.println(this.mSessionActive ? 1 : 0);
        printWriter.print("mBouncerOn=");
        printWriter.println(this.mSessionActive ? 1 : 0);
        printWriter.print("mState=");
        printWriter.println(StatusBarState.toShortString(this.mState));
        printWriter.print("mScreenOn=");
        printWriter.println(this.mScreenOn ? 1 : 0);
        printWriter.println();
    }
    
    @Override
    public boolean isClassifierEnabled() {
        return this.mHumanInteractionClassifier.isEnabled();
    }
    
    @Override
    public boolean isFalseTouch() {
        if (FalsingLog.ENABLED && !this.mSessionActive && ((PowerManager)this.mContext.getSystemService((Class)PowerManager.class)).isInteractive() && this.mPendingWtf == null) {
            final int enabled = this.isEnabled() ? 1 : 0;
            final int mScreenOn = this.mScreenOn ? 1 : 0;
            final String shortString = StatusBarState.toShortString(this.mState);
            final Throwable t = new Throwable("here");
            final StringBuilder sb = new StringBuilder();
            sb.append("Session is not active, yet there's a query for a false touch.");
            sb.append(" enabled=");
            sb.append(enabled);
            sb.append(" mScreenOn=");
            sb.append(mScreenOn);
            sb.append(" mState=");
            sb.append(shortString);
            sb.append(". Escalating to WTF if screen does not turn on soon.");
            FalsingLog.wLogcat("isFalseTouch", sb.toString());
            final _$$Lambda$FalsingManagerImpl$v5ZF_PRlWWHHEjWpilJxodWNKMI mPendingWtf = new _$$Lambda$FalsingManagerImpl$v5ZF_PRlWWHHEjWpilJxodWNKMI(this, enabled, mScreenOn, shortString, t);
            this.mPendingWtf = mPendingWtf;
            this.mHandler.postDelayed((Runnable)mPendingWtf, 1000L);
        }
        if (ActivityManager.isRunningInUserTestHarness()) {
            return false;
        }
        if (this.mAccessibilityManager.isTouchExplorationEnabled()) {
            return false;
        }
        if (!this.mIsTouchScreen) {
            return false;
        }
        if (this.mJustUnlockedWithFace) {
            return false;
        }
        ++this.mIsFalseTouchCalls;
        final boolean falseTouch = this.mHumanInteractionClassifier.isFalseTouch();
        if (!falseTouch) {
            if (FalsingLog.ENABLED) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Calls before success: ");
                sb2.append(this.mIsFalseTouchCalls);
                FalsingLog.i("isFalseTouchCalls", sb2.toString());
            }
            this.mMetricsLogger.histogram("falsing_success_after_attempts", this.mIsFalseTouchCalls);
            this.mIsFalseTouchCalls = 0;
        }
        return falseTouch;
    }
    
    @Override
    public boolean isReportingEnabled() {
        return this.mDataCollector.isReportingEnabled();
    }
    
    @Override
    public boolean isUnlockingDisabled() {
        return this.mDataCollector.isUnlockingDisabled();
    }
    
    @Override
    public void onAffordanceSwipingAborted() {
        this.mDataCollector.onAffordanceSwipingAborted();
    }
    
    @Override
    public void onAffordanceSwipingStarted(final boolean b) {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onAffordanceSwipingStarted", "");
        }
        if (b) {
            this.mHumanInteractionClassifier.setType(6);
        }
        else {
            this.mHumanInteractionClassifier.setType(5);
        }
        this.mDataCollector.onAffordanceSwipingStarted(b);
    }
    
    @Override
    public void onBouncerHidden() {
        if (FalsingLog.ENABLED) {
            final StringBuilder sb = new StringBuilder();
            sb.append("from=");
            sb.append(this.mBouncerOn ? 1 : 0);
            FalsingLog.i("onBouncerHidden", sb.toString());
        }
        if (this.mBouncerOn) {
            this.mBouncerOn = false;
            this.mDataCollector.onBouncerHidden();
        }
    }
    
    @Override
    public void onBouncerShown() {
        if (FalsingLog.ENABLED) {
            final StringBuilder sb = new StringBuilder();
            sb.append("from=");
            sb.append(this.mBouncerOn ? 1 : 0);
            FalsingLog.i("onBouncerShown", sb.toString());
        }
        if (!this.mBouncerOn) {
            this.mBouncerOn = true;
            this.mDataCollector.onBouncerShown();
        }
    }
    
    @Override
    public void onCameraHintStarted() {
        this.mDataCollector.onCameraHintStarted();
    }
    
    @Override
    public void onCameraOn() {
        this.mDataCollector.onCameraOn();
    }
    
    @Override
    public void onExpansionFromPulseStopped() {
        this.mDataCollector.onExpansionFromPulseStopped();
    }
    
    @Override
    public void onLeftAffordanceHintStarted() {
        this.mDataCollector.onLeftAffordanceHintStarted();
    }
    
    @Override
    public void onLeftAffordanceOn() {
        this.mDataCollector.onLeftAffordanceOn();
    }
    
    @Override
    public void onNotificationActive() {
        this.mDataCollector.onNotificationActive();
    }
    
    @Override
    public void onNotificationDismissed() {
        this.mDataCollector.onNotificationDismissed();
    }
    
    @Override
    public void onNotificationDoubleTap(final boolean b, final float f, final float f2) {
        if (FalsingLog.ENABLED) {
            final StringBuilder sb = new StringBuilder();
            sb.append("accepted=");
            sb.append(b);
            sb.append(" dx=");
            sb.append(f);
            sb.append(" dy=");
            sb.append(f2);
            sb.append(" (px)");
            FalsingLog.i("onNotificationDoubleTap", sb.toString());
        }
        this.mDataCollector.onNotificationDoubleTap();
    }
    
    @Override
    public void onNotificatonStartDismissing() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onNotificatonStartDismissing", "");
        }
        this.mHumanInteractionClassifier.setType(1);
        this.mDataCollector.onNotificatonStartDismissing();
    }
    
    @Override
    public void onNotificatonStartDraggingDown() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onNotificatonStartDraggingDown", "");
        }
        this.mHumanInteractionClassifier.setType(2);
        this.mDataCollector.onNotificatonStartDraggingDown();
    }
    
    @Override
    public void onNotificatonStopDismissing() {
        this.mDataCollector.onNotificatonStopDismissing();
    }
    
    @Override
    public void onNotificatonStopDraggingDown() {
        this.mDataCollector.onNotificatonStopDraggingDown();
    }
    
    @Override
    public void onQsDown() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onQsDown", "");
        }
        this.mHumanInteractionClassifier.setType(0);
        this.mDataCollector.onQsDown();
    }
    
    @Override
    public void onScreenOff() {
        if (FalsingLog.ENABLED) {
            final StringBuilder sb = new StringBuilder();
            sb.append("from=");
            sb.append(this.mScreenOn ? 1 : 0);
            FalsingLog.i("onScreenOff", sb.toString());
        }
        this.mDataCollector.onScreenOff();
        this.sessionExitpoint(this.mScreenOn = false);
    }
    
    @Override
    public void onScreenOnFromTouch() {
        if (FalsingLog.ENABLED) {
            final StringBuilder sb = new StringBuilder();
            sb.append("from=");
            sb.append(this.mScreenOn ? 1 : 0);
            FalsingLog.i("onScreenOnFromTouch", sb.toString());
        }
        this.mScreenOn = true;
        if (this.sessionEntrypoint()) {
            this.mDataCollector.onScreenOnFromTouch();
        }
    }
    
    @Override
    public void onScreenTurningOn() {
        if (FalsingLog.ENABLED) {
            final StringBuilder sb = new StringBuilder();
            sb.append("from=");
            sb.append(this.mScreenOn ? 1 : 0);
            FalsingLog.i("onScreenTurningOn", sb.toString());
            this.clearPendingWtf();
        }
        this.mScreenOn = true;
        if (this.sessionEntrypoint()) {
            this.mDataCollector.onScreenTurningOn();
        }
    }
    
    @Override
    public void onStartExpandingFromPulse() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onStartExpandingFromPulse", "");
        }
        this.mHumanInteractionClassifier.setType(9);
        this.mDataCollector.onStartExpandingFromPulse();
    }
    
    @Override
    public void onSuccessfulUnlock() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onSucccessfulUnlock", "");
        }
        this.mDataCollector.onSucccessfulUnlock();
    }
    
    @Override
    public void onTouchEvent(final MotionEvent motionEvent, final int n, final int n2) {
        if (motionEvent.getAction() == 0) {
            this.mIsTouchScreen = motionEvent.isFromSource(4098);
            this.mBouncerOffOnDown = (this.mBouncerOn ^ true);
        }
        if (this.mSessionActive) {
            if (!this.mBouncerOn) {
                this.mDataCollector.onTouchEvent(motionEvent, n, n2);
            }
            if (this.mBouncerOffOnDown) {
                this.mHumanInteractionClassifier.onTouchEvent(motionEvent);
            }
        }
    }
    
    @Override
    public void onTrackingStarted(final boolean b) {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onTrackingStarted", "");
        }
        final HumanInteractionClassifier mHumanInteractionClassifier = this.mHumanInteractionClassifier;
        int type;
        if (b) {
            type = 8;
        }
        else {
            type = 4;
        }
        mHumanInteractionClassifier.setType(type);
        this.mDataCollector.onTrackingStarted();
    }
    
    @Override
    public void onTrackingStopped() {
        this.mDataCollector.onTrackingStopped();
    }
    
    @Override
    public void onUnlockHintStarted() {
        this.mDataCollector.onUnlockHintStarted();
    }
    
    @Override
    public Uri reportRejectedTouch() {
        if (this.mDataCollector.isEnabled()) {
            return this.mDataCollector.reportRejectedTouch();
        }
        return null;
    }
    
    @Override
    public void setNotificationExpanded() {
        this.mDataCollector.setNotificationExpanded();
    }
    
    @Override
    public void setQsExpanded(final boolean qsExpanded) {
        this.mDataCollector.setQsExpanded(qsExpanded);
    }
    
    @Override
    public void setShowingAod(final boolean mShowingAod) {
        this.mShowingAod = mShowingAod;
        this.updateSessionActive();
    }
    
    @Override
    public boolean shouldEnforceBouncer() {
        return this.mEnforceBouncer;
    }
    
    public void updateSessionActive() {
        if (this.shouldSessionBeActive()) {
            this.sessionEntrypoint();
        }
        else {
            this.sessionExitpoint(false);
        }
    }
}
