// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier.brightline;

import java.util.StringJoiner;
import android.net.Uri;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import android.os.Build;
import java.util.function.Predicate;
import android.app.ActivityManager;
import java.util.Iterator;
import java.io.Writer;
import com.android.internal.util.IndentingPrintWriter;
import java.io.PrintWriter;
import java.util.function.Consumer;
import android.view.MotionEvent;
import java.util.Locale;
import java.util.ArrayList;
import com.android.systemui.statusbar.StatusBarState;
import android.hardware.biometrics.BiometricSourceType;
import com.android.systemui.util.DeviceConfigProxy;
import java.util.ArrayDeque;
import android.util.Log;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.dock.DockManager;
import java.util.List;
import java.util.Queue;
import com.android.systemui.plugins.FalsingManager;

public class BrightLineFalsingManager implements FalsingManager
{
    static final boolean DEBUG;
    private static final Queue<String> RECENT_INFO_LOG;
    private static final Queue<DebugSwipeRecord> RECENT_SWIPES;
    private final List<FalsingClassifier> mClassifiers;
    private final FalsingDataProvider mDataProvider;
    private final DockManager mDockManager;
    private int mIsFalseTouchCalls;
    private boolean mJustUnlockedWithFace;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateCallback;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private MetricsLogger mMetricsLogger;
    private boolean mPreviousResult;
    private final ProximitySensor mProximitySensor;
    private boolean mScreenOn;
    private ProximitySensor.ProximitySensorListener mSensorEventListener;
    private boolean mSessionStarted;
    private boolean mShowingAod;
    private int mState;
    private final StatusBarStateController mStatusBarStateController;
    private StatusBarStateController.StateListener mStatusBarStateListener;
    
    static {
        DEBUG = Log.isLoggable("FalsingManager", 3);
        RECENT_INFO_LOG = new ArrayDeque<String>(41);
        RECENT_SWIPES = new ArrayDeque<DebugSwipeRecord>(21);
    }
    
    public BrightLineFalsingManager(final FalsingDataProvider mDataProvider, final KeyguardUpdateMonitor mKeyguardUpdateMonitor, final ProximitySensor mProximitySensor, final DeviceConfigProxy deviceConfigProxy, final DockManager mDockManager, final StatusBarStateController mStatusBarStateController) {
        this.mSensorEventListener = new _$$Lambda$BrightLineFalsingManager$DCb2WK5QgVL78Az07qEbZU0x84o(this);
        this.mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onBiometricAuthenticated(final int n, final BiometricSourceType biometricSourceType, final boolean b) {
                if (n == KeyguardUpdateMonitor.getCurrentUser() && biometricSourceType == BiometricSourceType.FACE) {
                    BrightLineFalsingManager.this.mJustUnlockedWithFace = true;
                }
            }
        };
        this.mPreviousResult = false;
        this.mStatusBarStateListener = new StatusBarStateController.StateListener() {
            @Override
            public void onStateChanged(final int n) {
                final StringBuilder sb = new StringBuilder();
                sb.append("StatusBarState=");
                sb.append(StatusBarState.toShortString(n));
                BrightLineFalsingManager.logDebug(sb.toString());
                BrightLineFalsingManager.this.mState = n;
                BrightLineFalsingManager.this.updateSessionActive();
            }
        };
        this.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
        this.mDataProvider = mDataProvider;
        this.mProximitySensor = mProximitySensor;
        this.mDockManager = mDockManager;
        this.mStatusBarStateController = mStatusBarStateController;
        mKeyguardUpdateMonitor.registerCallback(this.mKeyguardUpdateCallback);
        this.mStatusBarStateController.addCallback(this.mStatusBarStateListener);
        this.mState = this.mStatusBarStateController.getState();
        this.mMetricsLogger = new MetricsLogger();
        this.mClassifiers = new ArrayList<FalsingClassifier>();
        final DistanceClassifier distanceClassifier = new DistanceClassifier(this.mDataProvider, deviceConfigProxy);
        final ProximityClassifier proximityClassifier = new ProximityClassifier(distanceClassifier, this.mDataProvider, deviceConfigProxy);
        this.mClassifiers.add(new PointerCountClassifier(this.mDataProvider));
        this.mClassifiers.add(new TypeClassifier(this.mDataProvider));
        this.mClassifiers.add(new DiagonalClassifier(this.mDataProvider, deviceConfigProxy));
        this.mClassifiers.add(distanceClassifier);
        this.mClassifiers.add(proximityClassifier);
        this.mClassifiers.add(new ZigZagClassifier(this.mDataProvider, deviceConfigProxy));
    }
    
    static void logDebug(final String s) {
        logDebug(s, null);
    }
    
    static void logDebug(final String s, final Throwable t) {
        if (BrightLineFalsingManager.DEBUG) {
            Log.d("FalsingManager", s, t);
        }
    }
    
    static void logInfo(final String s) {
        Log.i("FalsingManager", s);
        BrightLineFalsingManager.RECENT_INFO_LOG.add(s);
        while (BrightLineFalsingManager.RECENT_INFO_LOG.size() > 40) {
            BrightLineFalsingManager.RECENT_INFO_LOG.remove();
        }
    }
    
    private void onProximityEvent(final ProximitySensor.ProximityEvent proximityEvent) {
        this.mClassifiers.forEach(new _$$Lambda$BrightLineFalsingManager$_d89p1tVOz6Jf4LOgqm74DRgw1s(proximityEvent));
    }
    
    private void registerSensors() {
        this.mProximitySensor.register(this.mSensorEventListener);
    }
    
    private void sessionEnd() {
        if (this.mSessionStarted) {
            logDebug("Ending Session");
            this.mSessionStarted = false;
            this.unregisterSensors();
            this.mDataProvider.onSessionEnd();
            this.mClassifiers.forEach((Consumer<? super Object>)_$$Lambda$47wU6WxQ_76Gt_ecwypSCrFl04Q.INSTANCE);
            final int mIsFalseTouchCalls = this.mIsFalseTouchCalls;
            if (mIsFalseTouchCalls != 0) {
                this.mMetricsLogger.histogram("falsing_failure_after_attempts", mIsFalseTouchCalls);
                this.mIsFalseTouchCalls = 0;
            }
        }
    }
    
    private void sessionStart() {
        if (!this.mSessionStarted && this.shouldSessionBeActive()) {
            logDebug("Starting Session");
            this.mSessionStarted = true;
            this.mJustUnlockedWithFace = false;
            this.registerSensors();
            this.mClassifiers.forEach((Consumer<? super Object>)_$$Lambda$HclOlu42IVtKALxwbwHP3Y1rdRk.INSTANCE);
        }
    }
    
    private boolean shouldSessionBeActive() {
        final boolean mScreenOn = this.mScreenOn;
        boolean b = true;
        if (!mScreenOn || this.mState != 1 || this.mShowingAod) {
            b = false;
        }
        return b;
    }
    
    private void unregisterSensors() {
        this.mProximitySensor.unregister(this.mSensorEventListener);
    }
    
    private void updateInteractionType(final int n) {
        final StringBuilder sb = new StringBuilder();
        sb.append("InteractionType: ");
        sb.append(n);
        logDebug(sb.toString());
        this.mDataProvider.setInteractionType(n);
    }
    
    private void updateSessionActive() {
        if (this.shouldSessionBeActive()) {
            this.sessionStart();
        }
        else {
            this.sessionEnd();
        }
    }
    
    @Override
    public void cleanup() {
        this.unregisterSensors();
        this.mKeyguardUpdateMonitor.removeCallback(this.mKeyguardUpdateCallback);
        this.mStatusBarStateController.removeCallback(this.mStatusBarStateListener);
    }
    
    @Override
    public void dump(final PrintWriter printWriter) {
        final IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter((Writer)printWriter, "  ");
        indentingPrintWriter.println("BRIGHTLINE FALSING MANAGER");
        indentingPrintWriter.print("classifierEnabled=");
        indentingPrintWriter.println((int)(this.isClassifierEnabled() ? 1 : 0));
        indentingPrintWriter.print("mJustUnlockedWithFace=");
        indentingPrintWriter.println((int)(this.mJustUnlockedWithFace ? 1 : 0));
        indentingPrintWriter.print("isDocked=");
        indentingPrintWriter.println((int)(this.mDockManager.isDocked() ? 1 : 0));
        indentingPrintWriter.print("width=");
        indentingPrintWriter.println(this.mDataProvider.getWidthPixels());
        indentingPrintWriter.print("height=");
        indentingPrintWriter.println(this.mDataProvider.getHeightPixels());
        indentingPrintWriter.println();
        if (BrightLineFalsingManager.RECENT_SWIPES.size() != 0) {
            indentingPrintWriter.println("Recent swipes:");
            indentingPrintWriter.increaseIndent();
            final Iterator<DebugSwipeRecord> iterator = BrightLineFalsingManager.RECENT_SWIPES.iterator();
            while (iterator.hasNext()) {
                indentingPrintWriter.println(iterator.next().getString());
                indentingPrintWriter.println();
            }
            indentingPrintWriter.decreaseIndent();
        }
        else {
            indentingPrintWriter.println("No recent swipes");
        }
        indentingPrintWriter.println();
        indentingPrintWriter.println("Recent falsing info:");
        indentingPrintWriter.increaseIndent();
        final Iterator<String> iterator2 = BrightLineFalsingManager.RECENT_INFO_LOG.iterator();
        while (iterator2.hasNext()) {
            indentingPrintWriter.println((String)iterator2.next());
        }
        indentingPrintWriter.println();
    }
    
    @Override
    public boolean isClassifierEnabled() {
        return true;
    }
    
    @Override
    public boolean isFalseTouch() {
        if (!this.mDataProvider.isDirty()) {
            return this.mPreviousResult;
        }
        this.mPreviousResult = (!ActivityManager.isRunningInUserTestHarness() && !this.mJustUnlockedWithFace && !this.mDockManager.isDocked() && this.mClassifiers.stream().anyMatch(new _$$Lambda$BrightLineFalsingManager$a2Ll__HVGMZ_iA7riIG6wQYElYM(this)));
        final StringBuilder sb = new StringBuilder();
        sb.append("Is false touch? ");
        sb.append(this.mPreviousResult);
        logDebug(sb.toString());
        if (Build.IS_ENG || Build.IS_USERDEBUG) {
            BrightLineFalsingManager.RECENT_SWIPES.add(new DebugSwipeRecord(this.mPreviousResult, this.mDataProvider.getInteractionType(), (List<XYDt>)this.mDataProvider.getRecentMotionEvents().stream().map((Function<? super Object, ?>)_$$Lambda$BrightLineFalsingManager$CaQ6cuS9SHkQ1By76SF5W8vub7I.INSTANCE).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList())));
            while (BrightLineFalsingManager.RECENT_SWIPES.size() > 40) {
                final DebugSwipeRecord debugSwipeRecord = BrightLineFalsingManager.RECENT_SWIPES.remove();
            }
        }
        return this.mPreviousResult;
    }
    
    @Override
    public boolean isReportingEnabled() {
        return false;
    }
    
    @Override
    public boolean isUnlockingDisabled() {
        return false;
    }
    
    @Override
    public void onAffordanceSwipingAborted() {
    }
    
    @Override
    public void onAffordanceSwipingStarted(final boolean b) {
        int n;
        if (b) {
            n = 6;
        }
        else {
            n = 5;
        }
        this.updateInteractionType(n);
    }
    
    @Override
    public void onBouncerHidden() {
        if (this.mSessionStarted) {
            this.registerSensors();
        }
    }
    
    @Override
    public void onBouncerShown() {
        this.unregisterSensors();
    }
    
    @Override
    public void onCameraHintStarted() {
    }
    
    @Override
    public void onCameraOn() {
    }
    
    @Override
    public void onExpansionFromPulseStopped() {
    }
    
    @Override
    public void onLeftAffordanceHintStarted() {
    }
    
    @Override
    public void onLeftAffordanceOn() {
    }
    
    @Override
    public void onNotificationActive() {
    }
    
    @Override
    public void onNotificationDismissed() {
    }
    
    @Override
    public void onNotificationDoubleTap(final boolean b, final float n, final float n2) {
    }
    
    @Override
    public void onNotificatonStartDismissing() {
        this.updateInteractionType(1);
    }
    
    @Override
    public void onNotificatonStartDraggingDown() {
        this.updateInteractionType(2);
    }
    
    @Override
    public void onNotificatonStopDismissing() {
    }
    
    @Override
    public void onNotificatonStopDraggingDown() {
    }
    
    @Override
    public void onQsDown() {
        this.updateInteractionType(0);
    }
    
    @Override
    public void onScreenOff() {
        this.mScreenOn = false;
        this.updateSessionActive();
    }
    
    @Override
    public void onScreenOnFromTouch() {
        this.onScreenTurningOn();
    }
    
    @Override
    public void onScreenTurningOn() {
        this.mScreenOn = true;
        this.updateSessionActive();
    }
    
    @Override
    public void onStartExpandingFromPulse() {
        this.updateInteractionType(9);
    }
    
    @Override
    public void onSuccessfulUnlock() {
        final int mIsFalseTouchCalls = this.mIsFalseTouchCalls;
        if (mIsFalseTouchCalls != 0) {
            this.mMetricsLogger.histogram("falsing_success_after_attempts", mIsFalseTouchCalls);
            this.mIsFalseTouchCalls = 0;
        }
        this.sessionEnd();
    }
    
    @Override
    public void onTouchEvent(final MotionEvent motionEvent, final int n, final int n2) {
        this.mDataProvider.onMotionEvent(motionEvent);
        this.mClassifiers.forEach(new _$$Lambda$BrightLineFalsingManager$dqBt_Gf6PUXlUGyEertsddqo7Kg(motionEvent));
    }
    
    @Override
    public void onTrackingStarted(final boolean b) {
        int n;
        if (b) {
            n = 8;
        }
        else {
            n = 4;
        }
        this.updateInteractionType(n);
    }
    
    @Override
    public void onTrackingStopped() {
    }
    
    @Override
    public void onUnlockHintStarted() {
    }
    
    @Override
    public Uri reportRejectedTouch() {
        return null;
    }
    
    @Override
    public void setNotificationExpanded() {
    }
    
    @Override
    public void setQsExpanded(final boolean b) {
        if (b) {
            this.unregisterSensors();
        }
        else if (this.mSessionStarted) {
            this.registerSensors();
        }
    }
    
    @Override
    public void setShowingAod(final boolean mShowingAod) {
        this.mShowingAod = mShowingAod;
        this.updateSessionActive();
    }
    
    @Override
    public boolean shouldEnforceBouncer() {
        return false;
    }
    
    private static class DebugSwipeRecord
    {
        private final int mInteractionType;
        private final boolean mIsFalse;
        private final List<XYDt> mRecentMotionEvents;
        
        DebugSwipeRecord(final boolean mIsFalse, final int mInteractionType, final List<XYDt> mRecentMotionEvents) {
            this.mIsFalse = mIsFalse;
            this.mInteractionType = mInteractionType;
            this.mRecentMotionEvents = mRecentMotionEvents;
        }
        
        String getString() {
            final StringJoiner stringJoiner = new StringJoiner(",");
            final StringJoiner add = stringJoiner.add(Integer.toString(1));
            String newElement;
            if (this.mIsFalse) {
                newElement = "1";
            }
            else {
                newElement = "0";
            }
            add.add(newElement).add(Integer.toString(this.mInteractionType));
            final Iterator<XYDt> iterator = this.mRecentMotionEvents.iterator();
            while (iterator.hasNext()) {
                stringJoiner.add(iterator.next().toString());
            }
            return stringJoiner.toString();
        }
    }
    
    private static class XYDt
    {
        private final int mDT;
        private final int mX;
        private final int mY;
        
        XYDt(final int mx, final int my, final int mdt) {
            this.mX = mx;
            this.mY = my;
            this.mDT = mdt;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.mX);
            sb.append(",");
            sb.append(this.mY);
            sb.append(",");
            sb.append(this.mDT);
            return sb.toString();
        }
    }
}
