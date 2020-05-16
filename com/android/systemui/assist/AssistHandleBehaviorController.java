// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.provider.DeviceConfig$Properties;
import android.util.Log;
import android.content.ComponentName;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.os.SystemClock;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import java.util.concurrent.Executor;
import java.util.Objects;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.phone.NavigationModeController;
import java.util.concurrent.TimeUnit;
import android.os.Handler;
import android.content.Context;
import java.util.Map;
import com.android.internal.app.AssistUtils;
import javax.inject.Provider;
import android.view.accessibility.AccessibilityManager;
import dagger.Lazy;
import com.android.systemui.Dumpable;

public final class AssistHandleBehaviorController implements AssistHandleCallbacks, Dumpable
{
    private static final AssistHandleBehavior DEFAULT_BEHAVIOR;
    private static final long DEFAULT_SHOW_AND_GO_DURATION_MS;
    private final Lazy<AccessibilityManager> mA11yManager;
    private final Provider<AssistHandleViewController> mAssistHandleViewController;
    private final AssistUtils mAssistUtils;
    private final Map<AssistHandleBehavior, BehaviorController> mBehaviorMap;
    private final Context mContext;
    private AssistHandleBehavior mCurrentBehavior;
    private final DeviceConfigHelper mDeviceConfigHelper;
    private final Handler mHandler;
    private long mHandlesLastHiddenAt;
    private boolean mHandlesShowing;
    private final Runnable mHideHandles;
    private boolean mInGesturalMode;
    private final Runnable mShowAndGo;
    private long mShowAndGoEndsAt;
    
    static {
        DEFAULT_SHOW_AND_GO_DURATION_MS = TimeUnit.SECONDS.toMillis(3L);
        DEFAULT_BEHAVIOR = AssistHandleBehavior.REMINDER_EXP;
    }
    
    AssistHandleBehaviorController(final Context mContext, final AssistUtils mAssistUtils, final Handler mHandler, final Provider<AssistHandleViewController> mAssistHandleViewController, final DeviceConfigHelper mDeviceConfigHelper, final Map<AssistHandleBehavior, BehaviorController> mBehaviorMap, final NavigationModeController navigationModeController, final Lazy<AccessibilityManager> ma11yManager, final DumpManager dumpManager) {
        this.mHideHandles = new _$$Lambda$AssistHandleBehaviorController$XubZVLOT9vWCBnL_QqZRgbOELVA(this);
        this.mShowAndGo = new _$$Lambda$AssistHandleBehaviorController$oeveMWAQo5jd5bG1H5Ci7Dy4X74(this);
        this.mHandlesShowing = false;
        this.mCurrentBehavior = AssistHandleBehavior.OFF;
        this.mContext = mContext;
        this.mAssistUtils = mAssistUtils;
        this.mHandler = mHandler;
        this.mAssistHandleViewController = mAssistHandleViewController;
        this.mDeviceConfigHelper = mDeviceConfigHelper;
        this.mBehaviorMap = mBehaviorMap;
        this.mA11yManager = ma11yManager;
        this.mInGesturalMode = QuickStepContract.isGesturalMode(navigationModeController.addListener((NavigationModeController.ModeChangedListener)new _$$Lambda$AssistHandleBehaviorController$UX7PPcltnlTgxyL7MxmLbVmQRcI(this)));
        this.setBehavior(this.getBehaviorMode());
        final DeviceConfigHelper mDeviceConfigHelper2 = this.mDeviceConfigHelper;
        final Handler mHandler2 = this.mHandler;
        Objects.requireNonNull(mHandler2);
        mDeviceConfigHelper2.addOnPropertiesChangedListener(new _$$Lambda$LfzJt661qZfn2w_6SYHFbD3aMy0(mHandler2), (DeviceConfig$OnPropertiesChangedListener)new _$$Lambda$AssistHandleBehaviorController$q1QjkwrdHAyLNN1tG8mZqypuW_0(this));
        dumpManager.registerDumpable("AssistHandleBehavior", this);
    }
    
    private void clearPendingCommands() {
        this.mHandler.removeCallbacks(this.mHideHandles);
        this.mHandler.removeCallbacks(this.mShowAndGo);
        this.mShowAndGoEndsAt = 0L;
    }
    
    private String getBehaviorMode() {
        return this.mDeviceConfigHelper.getString("assist_handles_behavior_mode", AssistHandleBehaviorController.DEFAULT_BEHAVIOR.toString());
    }
    
    private long getShowAndGoDuration() {
        return this.mA11yManager.get().getRecommendedTimeoutMillis((int)this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_duration_ms", AssistHandleBehaviorController.DEFAULT_SHOW_AND_GO_DURATION_MS), 1);
    }
    
    private long getShownFrequencyThreshold() {
        return this.mDeviceConfigHelper.getLong("assist_handles_shown_frequency_threshold_ms", 0L);
    }
    
    private void handleNavigationModeChange(final int n) {
        final boolean gesturalMode = QuickStepContract.isGesturalMode(n);
        if (this.mInGesturalMode == gesturalMode) {
            return;
        }
        this.mInGesturalMode = gesturalMode;
        if (gesturalMode) {
            this.mBehaviorMap.get(this.mCurrentBehavior).onModeActivated(this.mContext, this);
        }
        else {
            this.mBehaviorMap.get(this.mCurrentBehavior).onModeDeactivated();
            this.hide();
        }
    }
    
    private boolean handlesUnblocked(final boolean b) {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        final long mHandlesLastHiddenAt = this.mHandlesLastHiddenAt;
        final boolean b2 = false;
        final boolean b3 = b || elapsedRealtime - mHandlesLastHiddenAt >= this.getShownFrequencyThreshold();
        final ComponentName assistComponentForUser = this.mAssistUtils.getAssistComponentForUser(KeyguardUpdateMonitor.getCurrentUser());
        boolean b4 = b2;
        if (b3) {
            b4 = b2;
            if (assistComponentForUser != null) {
                b4 = true;
            }
        }
        return b4;
    }
    
    private void hideHandles() {
        if (!this.mHandlesShowing) {
            return;
        }
        this.mHandlesShowing = false;
        this.mHandlesLastHiddenAt = SystemClock.elapsedRealtime();
        final AssistHandleViewController assistHandleViewController = this.mAssistHandleViewController.get();
        if (assistHandleViewController == null) {
            Log.w("AssistHandleBehavior", "Couldn't show handles, AssistHandleViewController unavailable");
        }
        else {
            assistHandleViewController.setAssistHintVisible(false);
        }
    }
    
    private void maybeShowHandles(final boolean b) {
        if (this.mHandlesShowing) {
            return;
        }
        if (this.handlesUnblocked(b)) {
            this.mHandlesShowing = true;
            final AssistHandleViewController assistHandleViewController = this.mAssistHandleViewController.get();
            if (assistHandleViewController == null) {
                Log.w("AssistHandleBehavior", "Couldn't show handles, AssistHandleViewController unavailable");
            }
            else {
                assistHandleViewController.setAssistHintVisible(true);
            }
        }
    }
    
    private void setBehavior(final String str) {
        try {
            this.setBehavior(AssistHandleBehavior.valueOf(str));
        }
        catch (IllegalArgumentException | NullPointerException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invalid behavior: ");
            sb.append(str);
            Log.e("AssistHandleBehavior", sb.toString());
        }
    }
    
    private void showAndGoInternal() {
        this.maybeShowHandles(false);
        final long showAndGoDuration = this.getShowAndGoDuration();
        this.mShowAndGoEndsAt = SystemClock.elapsedRealtime() + showAndGoDuration;
        this.mHandler.postDelayed(this.mHideHandles, showAndGoDuration);
    }
    
    boolean areHandlesShowing() {
        return this.mHandlesShowing;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("Current AssistHandleBehaviorController State:");
        final StringBuilder sb = new StringBuilder();
        sb.append("   mHandlesShowing=");
        sb.append(this.mHandlesShowing);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("   mHandlesLastHiddenAt=");
        sb2.append(this.mHandlesLastHiddenAt);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("   mInGesturalMode=");
        sb3.append(this.mInGesturalMode);
        printWriter.println(sb3.toString());
        printWriter.println("   Phenotype Flags:");
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("      assist_handles_show_and_go_duration_ms(a11y modded)=");
        sb4.append(this.getShowAndGoDuration());
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("      assist_handles_shown_frequency_threshold_ms=");
        sb5.append(this.getShownFrequencyThreshold());
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("      assist_handles_behavior_mode=");
        sb6.append(this.getBehaviorMode());
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append("   mCurrentBehavior=");
        sb7.append(this.mCurrentBehavior.toString());
        printWriter.println(sb7.toString());
        this.mBehaviorMap.get(this.mCurrentBehavior).dump(printWriter, "   ");
    }
    
    public long getShowAndGoRemainingTimeMs() {
        return Long.max(this.mShowAndGoEndsAt - SystemClock.elapsedRealtime(), 0L);
    }
    
    @Override
    public void hide() {
        this.clearPendingCommands();
        this.mHandler.post(this.mHideHandles);
    }
    
    void onAssistHandlesRequested() {
        if (this.mInGesturalMode) {
            this.mBehaviorMap.get(this.mCurrentBehavior).onAssistHandlesRequested();
        }
    }
    
    void onAssistantGesturePerformed() {
        this.mBehaviorMap.get(this.mCurrentBehavior).onAssistantGesturePerformed();
    }
    
    void setBehavior(final AssistHandleBehavior mCurrentBehavior) {
        if (this.mCurrentBehavior == mCurrentBehavior) {
            return;
        }
        if (!this.mBehaviorMap.containsKey(mCurrentBehavior)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unsupported behavior requested: ");
            sb.append(mCurrentBehavior.toString());
            Log.e("AssistHandleBehavior", sb.toString());
            return;
        }
        if (this.mInGesturalMode) {
            this.mBehaviorMap.get(this.mCurrentBehavior).onModeDeactivated();
            this.mBehaviorMap.get(mCurrentBehavior).onModeActivated(this.mContext, this);
        }
        this.mCurrentBehavior = mCurrentBehavior;
    }
    
    @VisibleForTesting
    void setInGesturalModeForTest(final boolean mInGesturalMode) {
        this.mInGesturalMode = mInGesturalMode;
    }
    
    @Override
    public void showAndGo() {
        this.clearPendingCommands();
        this.mHandler.post(this.mShowAndGo);
    }
    
    @Override
    public void showAndGoDelayed(final long n, final boolean b) {
        this.clearPendingCommands();
        if (b) {
            this.mHandler.post(this.mHideHandles);
        }
        this.mHandler.postDelayed(this.mShowAndGo, n);
    }
    
    @Override
    public void showAndStay() {
        this.clearPendingCommands();
        this.mHandler.post((Runnable)new _$$Lambda$AssistHandleBehaviorController$jLNVwoO6t8_VWqmD___vvvJFYqA(this));
    }
    
    interface BehaviorController
    {
        default void dump(final PrintWriter printWriter, final String s) {
        }
        
        default void onAssistHandlesRequested() {
        }
        
        default void onAssistantGesturePerformed() {
        }
        
        void onModeActivated(final Context p0, final AssistHandleCallbacks p1);
        
        default void onModeDeactivated() {
        }
    }
}
