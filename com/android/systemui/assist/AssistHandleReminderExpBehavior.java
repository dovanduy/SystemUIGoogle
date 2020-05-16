// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import android.content.ContentResolver;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import android.content.pm.ResolveInfo;
import java.util.ArrayList;
import android.provider.Settings$Secure;
import java.time.LocalDate;
import android.content.Intent;
import android.app.ActivityManager$RunningTaskInfo;
import java.util.concurrent.TimeUnit;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.model.SysUiState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.recents.OverviewProxyService;
import android.os.Handler;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import androidx.slice.Clock;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.BootCompleteCache;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import dagger.Lazy;

final class AssistHandleReminderExpBehavior implements BehaviorController
{
    private static final String[] DEFAULT_HOME_CHANGE_ACTIONS;
    private static final long DEFAULT_LEARNING_TIME_MS;
    private static final long DEFAULT_SHOW_AND_GO_DELAYED_LONG_DELAY_MS;
    private static final long DEFAULT_SHOW_AND_GO_DELAY_RESET_TIMEOUT_MS;
    private final Lazy<ActivityManagerWrapper> mActivityManagerWrapper;
    private AssistHandleCallbacks mAssistHandleCallbacks;
    private final Lazy<BootCompleteCache> mBootCompleteCache;
    private final BootCompleteCache.BootCompleteListener mBootCompleteListener;
    private final Lazy<BroadcastDispatcher> mBroadcastDispatcher;
    private final Clock mClock;
    private int mConsecutiveTaskSwitches;
    private Context mContext;
    private ComponentName mDefaultHome;
    private final BroadcastReceiver mDefaultHomeBroadcastReceiver;
    private final IntentFilter mDefaultHomeIntentFilter;
    private final DeviceConfigHelper mDeviceConfigHelper;
    private final Handler mHandler;
    private boolean mIsAwake;
    private boolean mIsDozing;
    private boolean mIsLauncherShowing;
    private boolean mIsLearned;
    private boolean mIsNavBarHidden;
    private long mLastLearningTimestamp;
    private long mLearnedHintLastShownEpochDay;
    private int mLearningCount;
    private long mLearningTimeElapsed;
    private boolean mOnLockscreen;
    private final OverviewProxyService.OverviewProxyListener mOverviewProxyListener;
    private final Lazy<OverviewProxyService> mOverviewProxyService;
    private final Lazy<PackageManagerWrapper> mPackageManagerWrapper;
    private final Runnable mResetConsecutiveTaskSwitches;
    private int mRunningTaskId;
    private final Lazy<StatusBarStateController> mStatusBarStateController;
    private final StatusBarStateController.StateListener mStatusBarStateListener;
    private final Lazy<SysUiState> mSysUiFlagContainer;
    private final SysUiState.SysUiStateCallback mSysUiStateCallback;
    private final TaskStackChangeListener mTaskStackChangeListener;
    private final Lazy<WakefulnessLifecycle> mWakefulnessLifecycle;
    private final WakefulnessLifecycle.Observer mWakefulnessLifecycleObserver;
    
    static {
        DEFAULT_LEARNING_TIME_MS = TimeUnit.DAYS.toMillis(10L);
        DEFAULT_SHOW_AND_GO_DELAYED_LONG_DELAY_MS = TimeUnit.SECONDS.toMillis(1L);
        DEFAULT_SHOW_AND_GO_DELAY_RESET_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(3L);
        DEFAULT_HOME_CHANGE_ACTIONS = new String[] { "android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED", "android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED" };
    }
    
    AssistHandleReminderExpBehavior(final Clock mClock, final Handler mHandler, final DeviceConfigHelper mDeviceConfigHelper, final Lazy<StatusBarStateController> mStatusBarStateController, final Lazy<ActivityManagerWrapper> mActivityManagerWrapper, final Lazy<OverviewProxyService> mOverviewProxyService, final Lazy<SysUiState> mSysUiFlagContainer, final Lazy<WakefulnessLifecycle> mWakefulnessLifecycle, final Lazy<PackageManagerWrapper> mPackageManagerWrapper, final Lazy<BroadcastDispatcher> mBroadcastDispatcher, final Lazy<BootCompleteCache> mBootCompleteCache) {
        this.mStatusBarStateListener = new StatusBarStateController.StateListener() {
            @Override
            public void onDozingChanged(final boolean b) {
                AssistHandleReminderExpBehavior.this.handleDozingChanged(b);
            }
            
            @Override
            public void onStateChanged(final int n) {
                AssistHandleReminderExpBehavior.this.handleStatusBarStateChanged(n);
            }
        };
        this.mTaskStackChangeListener = new TaskStackChangeListener() {
            @Override
            public void onTaskCreated(final int n, final ComponentName componentName) {
                AssistHandleReminderExpBehavior.this.handleTaskStackTopChanged(n, componentName);
            }
            
            @Override
            public void onTaskMovedToFront(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
                AssistHandleReminderExpBehavior.this.handleTaskStackTopChanged(activityManager$RunningTaskInfo.taskId, activityManager$RunningTaskInfo.topActivity);
            }
        };
        this.mOverviewProxyListener = new OverviewProxyService.OverviewProxyListener() {
            @Override
            public void onOverviewShown(final boolean b) {
                AssistHandleReminderExpBehavior.this.handleOverviewShown();
            }
        };
        this.mSysUiStateCallback = new _$$Lambda$AssistHandleReminderExpBehavior$V4NCzVQFEFR_zsFBikU8WKQiVok(this);
        this.mWakefulnessLifecycleObserver = new WakefulnessLifecycle.Observer() {
            @Override
            public void onFinishedGoingToSleep() {
                AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(false);
            }
            
            @Override
            public void onFinishedWakingUp() {
                AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(true);
            }
            
            @Override
            public void onStartedGoingToSleep() {
                AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(false);
            }
            
            @Override
            public void onStartedWakingUp() {
                AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(false);
            }
        };
        this.mDefaultHomeBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final AssistHandleReminderExpBehavior this$0 = AssistHandleReminderExpBehavior.this;
                this$0.mDefaultHome = this$0.getCurrentDefaultHome();
            }
        };
        this.mBootCompleteListener = new BootCompleteCache.BootCompleteListener() {
            @Override
            public void onBootComplete() {
                final AssistHandleReminderExpBehavior this$0 = AssistHandleReminderExpBehavior.this;
                this$0.mDefaultHome = this$0.getCurrentDefaultHome();
            }
        };
        this.mResetConsecutiveTaskSwitches = new _$$Lambda$AssistHandleReminderExpBehavior$pwcnWUhYSvHUPTaX_vnnVqcvKYA(this);
        this.mClock = mClock;
        this.mHandler = mHandler;
        this.mDeviceConfigHelper = mDeviceConfigHelper;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mActivityManagerWrapper = mActivityManagerWrapper;
        this.mOverviewProxyService = mOverviewProxyService;
        this.mSysUiFlagContainer = mSysUiFlagContainer;
        this.mWakefulnessLifecycle = mWakefulnessLifecycle;
        this.mPackageManagerWrapper = mPackageManagerWrapper;
        this.mDefaultHomeIntentFilter = new IntentFilter();
        final String[] default_HOME_CHANGE_ACTIONS = AssistHandleReminderExpBehavior.DEFAULT_HOME_CHANGE_ACTIONS;
        for (int length = default_HOME_CHANGE_ACTIONS.length, i = 0; i < length; ++i) {
            this.mDefaultHomeIntentFilter.addAction(default_HOME_CHANGE_ACTIONS[i]);
        }
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mBootCompleteCache = mBootCompleteCache;
    }
    
    private void callbackForCurrentState(final boolean b) {
        this.updateLearningStatus();
        if (this.mIsLearned) {
            this.callbackForLearnedState(b);
        }
        else {
            this.callbackForUnlearnedState();
        }
    }
    
    private void callbackForLearnedState(final boolean b) {
        if (this.mAssistHandleCallbacks == null) {
            return;
        }
        if (this.isFullyAwake() && !this.mIsNavBarHidden && !this.mOnLockscreen && this.getShowWhenTaught()) {
            if (b) {
                final long epochDay = LocalDate.now().toEpochDay();
                if (this.mLearnedHintLastShownEpochDay < epochDay) {
                    final Context mContext = this.mContext;
                    if (mContext != null) {
                        Settings$Secure.putLong(mContext.getContentResolver(), "reminder_exp_learned_hint_last_shown", epochDay);
                    }
                    this.mLearnedHintLastShownEpochDay = epochDay;
                    this.mAssistHandleCallbacks.showAndGo();
                }
            }
        }
        else {
            this.mAssistHandleCallbacks.hide();
        }
    }
    
    private void callbackForUnlearnedState() {
        if (this.mAssistHandleCallbacks == null) {
            return;
        }
        if (this.isFullyAwake() && !this.mIsNavBarHidden && !this.isSuppressed()) {
            if (this.mOnLockscreen) {
                this.mAssistHandleCallbacks.showAndStay();
            }
            else if (this.mIsLauncherShowing) {
                this.mAssistHandleCallbacks.showAndGo();
            }
            else if (this.mConsecutiveTaskSwitches == 1) {
                this.mAssistHandleCallbacks.showAndGoDelayed(this.getShowAndGoDelayedShortDelayMs(), false);
            }
            else {
                this.mAssistHandleCallbacks.showAndGoDelayed(this.getShowAndGoDelayedLongDelayMs(), true);
            }
        }
        else {
            this.mAssistHandleCallbacks.hide();
        }
    }
    
    private ComponentName getCurrentDefaultHome() {
        final ArrayList<ResolveInfo> list = new ArrayList<ResolveInfo>();
        final ComponentName homeActivities = this.mPackageManagerWrapper.get().getHomeActivities(list);
        if (homeActivities != null) {
            return homeActivities;
        }
        int priority = Integer.MIN_VALUE;
        final Iterator<Object> iterator = list.iterator();
        ComponentName componentName = null;
    Label_0042:
        while (true) {
            componentName = null;
            while (iterator.hasNext()) {
                final ResolveInfo resolveInfo = iterator.next();
                final int priority2 = resolveInfo.priority;
                if (priority2 > priority) {
                    componentName = resolveInfo.activityInfo.getComponentName();
                    priority = resolveInfo.priority;
                }
                else {
                    if (priority2 == priority) {
                        continue Label_0042;
                    }
                    continue;
                }
            }
            break;
        }
        return componentName;
    }
    
    private int getLearningCount() {
        return this.mDeviceConfigHelper.getInt("assist_handles_learn_count", 10);
    }
    
    private long getLearningTimeMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_learn_time_ms", AssistHandleReminderExpBehavior.DEFAULT_LEARNING_TIME_MS);
    }
    
    private long getShowAndGoDelayResetTimeoutMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_delay_reset_timeout_ms", AssistHandleReminderExpBehavior.DEFAULT_SHOW_AND_GO_DELAY_RESET_TIMEOUT_MS);
    }
    
    private long getShowAndGoDelayedLongDelayMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_delayed_long_delay_ms", AssistHandleReminderExpBehavior.DEFAULT_SHOW_AND_GO_DELAYED_LONG_DELAY_MS);
    }
    
    private long getShowAndGoDelayedShortDelayMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_delayed_short_delay_ms", 150L);
    }
    
    private boolean getShowWhenTaught() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_show_when_taught", false);
    }
    
    private boolean getSuppressOnApps() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_suppress_on_apps", true);
    }
    
    private boolean getSuppressOnLauncher() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_suppress_on_launcher", false);
    }
    
    private boolean getSuppressOnLockscreen() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_suppress_on_lockscreen", false);
    }
    
    private void handleDozingChanged(final boolean mIsDozing) {
        if (this.mIsDozing == mIsDozing) {
            return;
        }
        this.resetConsecutiveTaskSwitches();
        this.mIsDozing = mIsDozing;
        this.callbackForCurrentState(false);
    }
    
    private void handleOverviewShown() {
        this.resetConsecutiveTaskSwitches();
        this.callbackForCurrentState(false);
    }
    
    private void handleStatusBarStateChanged(final int n) {
        final boolean onLockscreen = this.onLockscreen(n);
        if (this.mOnLockscreen == onLockscreen) {
            return;
        }
        this.resetConsecutiveTaskSwitches();
        this.callbackForCurrentState((this.mOnLockscreen = onLockscreen) ^ true);
    }
    
    private void handleSystemUiStateChanged(final int n) {
        final boolean mIsNavBarHidden = (n & 0x2) != 0x0;
        if (this.mIsNavBarHidden == mIsNavBarHidden) {
            return;
        }
        this.resetConsecutiveTaskSwitches();
        this.mIsNavBarHidden = mIsNavBarHidden;
        this.callbackForCurrentState(false);
    }
    
    private void handleTaskStackTopChanged(final int mRunningTaskId, final ComponentName componentName) {
        if (this.mRunningTaskId != mRunningTaskId) {
            if (componentName != null) {
                this.mRunningTaskId = mRunningTaskId;
                final boolean equals = componentName.equals((Object)this.mDefaultHome);
                this.mIsLauncherShowing = equals;
                if (equals) {
                    this.resetConsecutiveTaskSwitches();
                }
                else {
                    this.rescheduleConsecutiveTaskSwitchesReset();
                    ++this.mConsecutiveTaskSwitches;
                }
                this.callbackForCurrentState(false);
            }
        }
    }
    
    private void handleWakefullnessChanged(final boolean mIsAwake) {
        if (this.mIsAwake == mIsAwake) {
            return;
        }
        this.resetConsecutiveTaskSwitches();
        this.mIsAwake = mIsAwake;
        this.callbackForCurrentState(false);
    }
    
    private boolean isFullyAwake() {
        return this.mIsAwake && !this.mIsDozing;
    }
    
    private boolean isSuppressed() {
        if (this.mOnLockscreen) {
            return this.getSuppressOnLockscreen();
        }
        if (this.mIsLauncherShowing) {
            return this.getSuppressOnLauncher();
        }
        return this.getSuppressOnApps();
    }
    
    private boolean onLockscreen(final int n) {
        boolean b = true;
        if (n != 1) {
            b = (n == 2 && b);
        }
        return b;
    }
    
    private void rescheduleConsecutiveTaskSwitchesReset() {
        this.mHandler.removeCallbacks(this.mResetConsecutiveTaskSwitches);
        this.mHandler.postDelayed(this.mResetConsecutiveTaskSwitches, this.getShowAndGoDelayResetTimeoutMs());
    }
    
    private void resetConsecutiveTaskSwitches() {
        this.mHandler.removeCallbacks(this.mResetConsecutiveTaskSwitches);
        this.mConsecutiveTaskSwitches = 0;
    }
    
    private void updateLearningStatus() {
        if (this.mContext == null) {
            return;
        }
        final long currentTimeMillis = this.mClock.currentTimeMillis();
        this.mLearningTimeElapsed += currentTimeMillis - this.mLastLearningTimestamp;
        this.mLastLearningTimestamp = currentTimeMillis;
        this.mIsLearned = (this.mLearningCount >= this.getLearningCount() || this.mLearningTimeElapsed >= this.getLearningTimeMs());
        this.mHandler.post((Runnable)new _$$Lambda$AssistHandleReminderExpBehavior$b5N62AJXKgTBT_CGtHJhp_XuFas(this));
    }
    
    @Override
    public void dump(final PrintWriter printWriter, final String str) {
        final StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("Current AssistHandleReminderExpBehavior State:");
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append("   mOnLockscreen=");
        sb2.append(this.mOnLockscreen);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append("   mIsDozing=");
        sb3.append(this.mIsDozing);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        sb4.append("   mIsAwake=");
        sb4.append(this.mIsAwake);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append(str);
        sb5.append("   mRunningTaskId=");
        sb5.append(this.mRunningTaskId);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append(str);
        sb6.append("   mDefaultHome=");
        sb6.append(this.mDefaultHome);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append(str);
        sb7.append("   mIsNavBarHidden=");
        sb7.append(this.mIsNavBarHidden);
        printWriter.println(sb7.toString());
        final StringBuilder sb8 = new StringBuilder();
        sb8.append(str);
        sb8.append("   mIsLauncherShowing=");
        sb8.append(this.mIsLauncherShowing);
        printWriter.println(sb8.toString());
        final StringBuilder sb9 = new StringBuilder();
        sb9.append(str);
        sb9.append("   mConsecutiveTaskSwitches=");
        sb9.append(this.mConsecutiveTaskSwitches);
        printWriter.println(sb9.toString());
        final StringBuilder sb10 = new StringBuilder();
        sb10.append(str);
        sb10.append("   mIsLearned=");
        sb10.append(this.mIsLearned);
        printWriter.println(sb10.toString());
        final StringBuilder sb11 = new StringBuilder();
        sb11.append(str);
        sb11.append("   mLastLearningTimestamp=");
        sb11.append(this.mLastLearningTimestamp);
        printWriter.println(sb11.toString());
        final StringBuilder sb12 = new StringBuilder();
        sb12.append(str);
        sb12.append("   mLearningTimeElapsed=");
        sb12.append(this.mLearningTimeElapsed);
        printWriter.println(sb12.toString());
        final StringBuilder sb13 = new StringBuilder();
        sb13.append(str);
        sb13.append("   mLearningCount=");
        sb13.append(this.mLearningCount);
        printWriter.println(sb13.toString());
        final StringBuilder sb14 = new StringBuilder();
        sb14.append(str);
        sb14.append("   mLearnedHintLastShownEpochDay=");
        sb14.append(this.mLearnedHintLastShownEpochDay);
        printWriter.println(sb14.toString());
        final StringBuilder sb15 = new StringBuilder();
        sb15.append(str);
        sb15.append("   mAssistHandleCallbacks present: ");
        sb15.append(this.mAssistHandleCallbacks != null);
        printWriter.println(sb15.toString());
        final StringBuilder sb16 = new StringBuilder();
        sb16.append(str);
        sb16.append("   Phenotype Flags:");
        printWriter.println(sb16.toString());
        final StringBuilder sb17 = new StringBuilder();
        sb17.append(str);
        sb17.append("      ");
        sb17.append("assist_handles_learn_time_ms");
        sb17.append("=");
        sb17.append(this.getLearningTimeMs());
        printWriter.println(sb17.toString());
        final StringBuilder sb18 = new StringBuilder();
        sb18.append(str);
        sb18.append("      ");
        sb18.append("assist_handles_learn_count");
        sb18.append("=");
        sb18.append(this.getLearningCount());
        printWriter.println(sb18.toString());
        final StringBuilder sb19 = new StringBuilder();
        sb19.append(str);
        sb19.append("      ");
        sb19.append("assist_handles_show_and_go_delayed_short_delay_ms");
        sb19.append("=");
        sb19.append(this.getShowAndGoDelayedShortDelayMs());
        printWriter.println(sb19.toString());
        final StringBuilder sb20 = new StringBuilder();
        sb20.append(str);
        sb20.append("      ");
        sb20.append("assist_handles_show_and_go_delayed_long_delay_ms");
        sb20.append("=");
        sb20.append(this.getShowAndGoDelayedLongDelayMs());
        printWriter.println(sb20.toString());
        final StringBuilder sb21 = new StringBuilder();
        sb21.append(str);
        sb21.append("      ");
        sb21.append("assist_handles_show_and_go_delay_reset_timeout_ms");
        sb21.append("=");
        sb21.append(this.getShowAndGoDelayResetTimeoutMs());
        printWriter.println(sb21.toString());
        final StringBuilder sb22 = new StringBuilder();
        sb22.append(str);
        sb22.append("      ");
        sb22.append("assist_handles_suppress_on_lockscreen");
        sb22.append("=");
        sb22.append(this.getSuppressOnLockscreen());
        printWriter.println(sb22.toString());
        final StringBuilder sb23 = new StringBuilder();
        sb23.append(str);
        sb23.append("      ");
        sb23.append("assist_handles_suppress_on_launcher");
        sb23.append("=");
        sb23.append(this.getSuppressOnLauncher());
        printWriter.println(sb23.toString());
        final StringBuilder sb24 = new StringBuilder();
        sb24.append(str);
        sb24.append("      ");
        sb24.append("assist_handles_suppress_on_apps");
        sb24.append("=");
        sb24.append(this.getSuppressOnApps());
        printWriter.println(sb24.toString());
        final StringBuilder sb25 = new StringBuilder();
        sb25.append(str);
        sb25.append("      ");
        sb25.append("assist_handles_show_when_taught");
        sb25.append("=");
        sb25.append(this.getShowWhenTaught());
        printWriter.println(sb25.toString());
    }
    
    @Override
    public void onAssistHandlesRequested() {
        if (this.mAssistHandleCallbacks != null && this.isFullyAwake() && !this.mIsNavBarHidden && !this.mOnLockscreen) {
            this.mAssistHandleCallbacks.showAndGo();
        }
    }
    
    @Override
    public void onAssistantGesturePerformed() {
        final Context mContext = this.mContext;
        if (mContext == null) {
            return;
        }
        final ContentResolver contentResolver = mContext.getContentResolver();
        final int mLearningCount = this.mLearningCount + 1;
        this.mLearningCount = mLearningCount;
        Settings$Secure.putLong(contentResolver, "reminder_exp_learning_event_count", (long)mLearningCount);
    }
    
    @Override
    public void onModeActivated(final Context mContext, final AssistHandleCallbacks mAssistHandleCallbacks) {
        this.mContext = mContext;
        this.mAssistHandleCallbacks = mAssistHandleCallbacks;
        this.mConsecutiveTaskSwitches = 0;
        this.mBootCompleteCache.get().addListener(this.mBootCompleteListener);
        this.mDefaultHome = this.getCurrentDefaultHome();
        this.mBroadcastDispatcher.get().registerReceiver(this.mDefaultHomeBroadcastReceiver, this.mDefaultHomeIntentFilter);
        this.mOnLockscreen = this.onLockscreen(this.mStatusBarStateController.get().getState());
        this.mIsDozing = this.mStatusBarStateController.get().isDozing();
        this.mStatusBarStateController.get().addCallback(this.mStatusBarStateListener);
        final ActivityManager$RunningTaskInfo runningTask = this.mActivityManagerWrapper.get().getRunningTask();
        int taskId;
        if (runningTask == null) {
            taskId = 0;
        }
        else {
            taskId = runningTask.taskId;
        }
        this.mRunningTaskId = taskId;
        this.mActivityManagerWrapper.get().registerTaskStackListener(this.mTaskStackChangeListener);
        this.mOverviewProxyService.get().addCallback(this.mOverviewProxyListener);
        this.mSysUiFlagContainer.get().addCallback(this.mSysUiStateCallback);
        this.mIsAwake = (this.mWakefulnessLifecycle.get().getWakefulness() == 2);
        this.mWakefulnessLifecycle.get().addObserver(this.mWakefulnessLifecycleObserver);
        this.mLearningTimeElapsed = Settings$Secure.getLong(mContext.getContentResolver(), "reminder_exp_learning_time_elapsed", 0L);
        this.mLearningCount = Settings$Secure.getInt(mContext.getContentResolver(), "reminder_exp_learning_event_count", 0);
        this.mLearnedHintLastShownEpochDay = Settings$Secure.getLong(mContext.getContentResolver(), "reminder_exp_learned_hint_last_shown", 0L);
        this.mLastLearningTimestamp = this.mClock.currentTimeMillis();
        this.callbackForCurrentState(false);
    }
    
    @Override
    public void onModeDeactivated() {
        this.mAssistHandleCallbacks = null;
        if (this.mContext != null) {
            this.mBroadcastDispatcher.get().unregisterReceiver(this.mDefaultHomeBroadcastReceiver);
            this.mBootCompleteCache.get().removeListener(this.mBootCompleteListener);
            Settings$Secure.putLong(this.mContext.getContentResolver(), "reminder_exp_learning_time_elapsed", 0L);
            Settings$Secure.putInt(this.mContext.getContentResolver(), "reminder_exp_learning_event_count", 0);
            Settings$Secure.putLong(this.mContext.getContentResolver(), "reminder_exp_learned_hint_last_shown", 0L);
            this.mContext = null;
        }
        this.mStatusBarStateController.get().removeCallback(this.mStatusBarStateListener);
        this.mActivityManagerWrapper.get().unregisterTaskStackListener(this.mTaskStackChangeListener);
        this.mOverviewProxyService.get().removeCallback(this.mOverviewProxyListener);
        this.mSysUiFlagContainer.get().removeCallback(this.mSysUiStateCallback);
        this.mWakefulnessLifecycle.get().removeObserver(this.mWakefulnessLifecycleObserver);
    }
}
