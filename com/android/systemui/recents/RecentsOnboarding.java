// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import android.view.animation.DecelerateInterpolator;
import com.android.systemui.shared.system.QuickStepContract;
import android.content.res.Configuration;
import android.animation.TimeInterpolator;
import android.view.animation.AccelerateInterpolator;
import java.io.PrintWriter;
import android.os.SystemProperties;
import android.app.ActivityManager;
import android.os.UserManager;
import com.android.systemui.shared.recents.IOverviewProxy;
import android.os.RemoteException;
import android.view.WindowManager$LayoutParams;
import com.android.systemui.Prefs;
import android.graphics.Paint;
import android.view.ViewGroup$LayoutParams;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.PathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.drawable.shapes.Shape;
import android.graphics.drawable.ShapeDrawable;
import android.view.View$OnClickListener;
import com.android.systemui.R$dimen;
import android.util.TypedValue;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import java.util.Collection;
import java.util.Collections;
import com.android.systemui.R$array;
import java.util.HashSet;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.systemui.Dependency;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.content.ComponentName;
import android.app.ActivityManager$RunningTaskInfo;
import com.android.systemui.R$string;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.view.WindowManager;
import android.widget.TextView;
import com.android.systemui.shared.system.TaskStackChangeListener;
import android.content.BroadcastReceiver;
import android.view.View$OnAttachStateChangeListener;
import android.widget.ImageView;
import android.content.Context;
import java.util.Set;
import android.view.View;
import android.annotation.TargetApi;

@TargetApi(28)
public class RecentsOnboarding
{
    private final View mArrowView;
    private Set<String> mBlacklistedPackages;
    private final Context mContext;
    private final ImageView mDismissView;
    private boolean mHasDismissedQuickScrubTip;
    private boolean mHasDismissedSwipeUpTip;
    private final View mLayout;
    private boolean mLayoutAttachedToWindow;
    private int mNavBarHeight;
    private int mNavBarMode;
    private int mNumAppsLaunchedSinceSwipeUpTipDismiss;
    private final View$OnAttachStateChangeListener mOnAttachStateChangeListener;
    private final int mOnboardingToastArrowRadius;
    private final int mOnboardingToastColor;
    private int mOverviewOpenedCountSinceQuickScrubTipDismiss;
    private OverviewProxyService.OverviewProxyListener mOverviewProxyListener;
    private boolean mOverviewProxyListenerRegistered;
    private final OverviewProxyService mOverviewProxyService;
    private final BroadcastReceiver mReceiver;
    private final TaskStackChangeListener mTaskListener;
    private boolean mTaskListenerRegistered;
    private final TextView mTextView;
    private final WindowManager mWindowManager;
    
    public RecentsOnboarding(final Context mContext, final OverviewProxyService mOverviewProxyService) {
        this.mNavBarMode = 0;
        this.mTaskListener = new TaskStackChangeListener() {
            private String mLastPackageName;
            
            private void onAppLaunch() {
                final ActivityManager$RunningTaskInfo runningTask = ActivityManagerWrapper.getInstance().getRunningTask();
                if (runningTask == null) {
                    return;
                }
                if (RecentsOnboarding.this.mBlacklistedPackages.contains(runningTask.baseActivity.getPackageName())) {
                    RecentsOnboarding.this.hide(true);
                    return;
                }
                if (runningTask.baseActivity.getPackageName().equals(this.mLastPackageName)) {
                    return;
                }
                this.mLastPackageName = runningTask.baseActivity.getPackageName();
                if (runningTask.configuration.windowConfiguration.getActivityType() == 1) {
                    final boolean access$100 = RecentsOnboarding.this.hasSeenSwipeUpOnboarding();
                    final boolean access$101 = RecentsOnboarding.this.hasSeenQuickScrubOnboarding();
                    if (access$100 && access$101) {
                        RecentsOnboarding.this.onDisconnectedFromLauncher();
                        return;
                    }
                    if (!access$100) {
                        if (RecentsOnboarding.this.getOpenedOverviewFromHomeCount() >= 3) {
                            boolean b;
                            if (RecentsOnboarding.this.mHasDismissedSwipeUpTip) {
                                final int access$102 = RecentsOnboarding.this.getDismissedSwipeUpOnboardingCount();
                                if (access$102 > 2) {
                                    return;
                                }
                                int n;
                                if (access$102 <= 1) {
                                    n = 5;
                                }
                                else {
                                    n = 40;
                                }
                                RecentsOnboarding.this.mNumAppsLaunchedSinceSwipeUpTipDismiss++;
                                if (RecentsOnboarding.this.mNumAppsLaunchedSinceSwipeUpTipDismiss >= n) {
                                    RecentsOnboarding.this.mNumAppsLaunchedSinceSwipeUpTipDismiss = 0;
                                    b = RecentsOnboarding.this.show(R$string.recents_swipe_up_onboarding);
                                }
                                else {
                                    b = false;
                                }
                            }
                            else {
                                b = RecentsOnboarding.this.show(R$string.recents_swipe_up_onboarding);
                            }
                            if (b) {
                                RecentsOnboarding.this.notifyOnTip(0, 0);
                            }
                        }
                    }
                    else if (RecentsOnboarding.this.getOpenedOverviewCount() >= 10) {
                        boolean b2;
                        if (RecentsOnboarding.this.mHasDismissedQuickScrubTip) {
                            if (RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss >= 10) {
                                RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss = 0;
                                b2 = RecentsOnboarding.this.show(R$string.recents_quick_scrub_onboarding);
                            }
                            else {
                                b2 = false;
                            }
                        }
                        else {
                            b2 = RecentsOnboarding.this.show(R$string.recents_quick_scrub_onboarding);
                        }
                        if (b2) {
                            RecentsOnboarding.this.notifyOnTip(0, 1);
                        }
                    }
                }
                else {
                    RecentsOnboarding.this.hide(false);
                }
            }
            
            @Override
            public void onTaskCreated(final int n, final ComponentName componentName) {
                this.onAppLaunch();
            }
            
            @Override
            public void onTaskMovedToFront(final int n) {
                this.onAppLaunch();
            }
        };
        this.mOverviewProxyListener = new OverviewProxyService.OverviewProxyListener() {
            @Override
            public void onOverviewShown(final boolean b) {
                if (!RecentsOnboarding.this.hasSeenSwipeUpOnboarding() && !b) {
                    RecentsOnboarding.this.setHasSeenSwipeUpOnboarding(true);
                }
                if (b) {
                    RecentsOnboarding.this.incrementOpenedOverviewFromHomeCount();
                }
                RecentsOnboarding.this.incrementOpenedOverviewCount();
                if (RecentsOnboarding.this.getOpenedOverviewCount() >= 10 && RecentsOnboarding.this.mHasDismissedQuickScrubTip) {
                    RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss++;
                }
            }
        };
        this.mOnAttachStateChangeListener = (View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            private final BroadcastDispatcher mBroadcastDispatcher = Dependency.get(BroadcastDispatcher.class);
            
            public void onViewAttachedToWindow(final View view) {
                if (view == RecentsOnboarding.this.mLayout) {
                    this.mBroadcastDispatcher.registerReceiver(RecentsOnboarding.this.mReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
                    RecentsOnboarding.this.mLayoutAttachedToWindow = true;
                    if (view.getTag().equals(R$string.recents_swipe_up_onboarding)) {
                        RecentsOnboarding.this.mHasDismissedSwipeUpTip = false;
                    }
                    else {
                        RecentsOnboarding.this.mHasDismissedQuickScrubTip = false;
                    }
                }
            }
            
            public void onViewDetachedFromWindow(final View view) {
                if (view == RecentsOnboarding.this.mLayout) {
                    RecentsOnboarding.this.mLayoutAttachedToWindow = false;
                    if (view.getTag().equals(R$string.recents_quick_scrub_onboarding)) {
                        RecentsOnboarding.this.mHasDismissedQuickScrubTip = true;
                        if (RecentsOnboarding.this.hasDismissedQuickScrubOnboardingOnce()) {
                            RecentsOnboarding.this.setHasSeenQuickScrubOnboarding(true);
                        }
                        else {
                            RecentsOnboarding.this.setHasDismissedQuickScrubOnboardingOnce(true);
                        }
                        RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss = 0;
                    }
                    this.mBroadcastDispatcher.unregisterReceiver(RecentsOnboarding.this.mReceiver);
                }
            }
        };
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                    RecentsOnboarding.this.hide(false);
                }
            }
        };
        this.mContext = mContext;
        this.mOverviewProxyService = mOverviewProxyService;
        final Resources resources = mContext.getResources();
        this.mWindowManager = (WindowManager)this.mContext.getSystemService("window");
        Collections.addAll(this.mBlacklistedPackages = new HashSet<String>(), resources.getStringArray(R$array.recents_onboarding_blacklisted_packages));
        final View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.recents_onboarding, (ViewGroup)null);
        this.mLayout = inflate;
        this.mTextView = (TextView)inflate.findViewById(R$id.onboarding_text);
        this.mDismissView = (ImageView)this.mLayout.findViewById(R$id.dismiss);
        this.mArrowView = this.mLayout.findViewById(R$id.arrow);
        final TypedValue typedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(16843829, typedValue, true);
        this.mOnboardingToastColor = resources.getColor(typedValue.resourceId);
        this.mOnboardingToastArrowRadius = resources.getDimensionPixelSize(R$dimen.recents_onboarding_toast_arrow_corner_radius);
        this.mLayout.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        this.mDismissView.setOnClickListener((View$OnClickListener)new _$$Lambda$RecentsOnboarding$VU_OZtWyvAx7bVWSUdhKQFeocZE(this));
        final ViewGroup$LayoutParams layoutParams = this.mArrowView.getLayoutParams();
        final ShapeDrawable background = new ShapeDrawable((Shape)TriangleShape.create((float)layoutParams.width, (float)layoutParams.height, false));
        final Paint paint = background.getPaint();
        paint.setColor(this.mOnboardingToastColor);
        paint.setPathEffect((PathEffect)new CornerPathEffect((float)this.mOnboardingToastArrowRadius));
        this.mArrowView.setBackground((Drawable)background);
    }
    
    private int getDismissedSwipeUpOnboardingCount() {
        return Prefs.getInt(this.mContext, "DismissedRecentsSwipeUpOnboardingCount", 0);
    }
    
    private int getOpenedOverviewCount() {
        return Prefs.getInt(this.mContext, "OverviewOpenedCount", 0);
    }
    
    private int getOpenedOverviewFromHomeCount() {
        return Prefs.getInt(this.mContext, "OverviewOpenedFromHomeCount", 0);
    }
    
    private WindowManager$LayoutParams getWindowLayoutParams(final int gravity, final int n) {
        final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(-2, -2, n, -this.mNavBarHeight / 2, 2038, 520, -3);
        windowManager$LayoutParams.privateFlags |= 0x10;
        windowManager$LayoutParams.setTitle((CharSequence)"RecentsOnboarding");
        windowManager$LayoutParams.gravity = gravity;
        return windowManager$LayoutParams;
    }
    
    private boolean hasDismissedQuickScrubOnboardingOnce() {
        return Prefs.getBoolean(this.mContext, "HasDismissedRecentsQuickScrubOnboardingOnce", false);
    }
    
    private boolean hasSeenQuickScrubOnboarding() {
        return Prefs.getBoolean(this.mContext, "HasSeenRecentsQuickScrubOnboarding", false);
    }
    
    private boolean hasSeenSwipeUpOnboarding() {
        return Prefs.getBoolean(this.mContext, "HasSeenRecentsSwipeUpOnboarding", false);
    }
    
    private void incrementOpenedOverviewCount() {
        final int openedOverviewCount = this.getOpenedOverviewCount();
        if (openedOverviewCount >= 10) {
            return;
        }
        this.setOpenedOverviewCount(openedOverviewCount + 1);
    }
    
    private void incrementOpenedOverviewFromHomeCount() {
        final int openedOverviewFromHomeCount = this.getOpenedOverviewFromHomeCount();
        if (openedOverviewFromHomeCount >= 3) {
            return;
        }
        this.setOpenedOverviewFromHomeCount(openedOverviewFromHomeCount + 1);
    }
    
    private void notifyOnTip(final int n, final int n2) {
        try {
            final IOverviewProxy proxy = this.mOverviewProxyService.getProxy();
            if (proxy != null) {
                proxy.onTip(n, n2);
            }
        }
        catch (RemoteException ex) {}
    }
    
    private void setDismissedSwipeUpOnboardingCount(final int n) {
        Prefs.putInt(this.mContext, "DismissedRecentsSwipeUpOnboardingCount", n);
    }
    
    private void setHasDismissedQuickScrubOnboardingOnce(final boolean b) {
        Prefs.putBoolean(this.mContext, "HasDismissedRecentsQuickScrubOnboardingOnce", b);
    }
    
    private void setHasSeenQuickScrubOnboarding(final boolean b) {
        Prefs.putBoolean(this.mContext, "HasSeenRecentsQuickScrubOnboarding", b);
        if (b && this.hasSeenSwipeUpOnboarding()) {
            this.onDisconnectedFromLauncher();
        }
    }
    
    private void setHasSeenSwipeUpOnboarding(final boolean b) {
        Prefs.putBoolean(this.mContext, "HasSeenRecentsSwipeUpOnboarding", b);
        if (b && this.hasSeenQuickScrubOnboarding()) {
            this.onDisconnectedFromLauncher();
        }
    }
    
    private void setOpenedOverviewCount(final int n) {
        Prefs.putInt(this.mContext, "OverviewOpenedCount", n);
    }
    
    private void setOpenedOverviewFromHomeCount(final int n) {
        Prefs.putInt(this.mContext, "OverviewOpenedFromHomeCount", n);
    }
    
    private boolean shouldShow() {
        return SystemProperties.getBoolean("persist.quickstep.onboarding.enabled", !((UserManager)this.mContext.getSystemService((Class)UserManager.class)).isDemoUser() && !ActivityManager.isRunningInTestHarness());
    }
    
    public void dump(final PrintWriter printWriter) {
        printWriter.println("RecentsOnboarding {");
        final StringBuilder sb = new StringBuilder();
        sb.append("      mTaskListenerRegistered: ");
        sb.append(this.mTaskListenerRegistered);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("      mOverviewProxyListenerRegistered: ");
        sb2.append(this.mOverviewProxyListenerRegistered);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("      mLayoutAttachedToWindow: ");
        sb3.append(this.mLayoutAttachedToWindow);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("      mHasDismissedSwipeUpTip: ");
        sb4.append(this.mHasDismissedSwipeUpTip);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("      mHasDismissedQuickScrubTip: ");
        sb5.append(this.mHasDismissedQuickScrubTip);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("      mNumAppsLaunchedSinceSwipeUpTipDismiss: ");
        sb6.append(this.mNumAppsLaunchedSinceSwipeUpTipDismiss);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append("      hasSeenSwipeUpOnboarding: ");
        sb7.append(this.hasSeenSwipeUpOnboarding());
        printWriter.println(sb7.toString());
        final StringBuilder sb8 = new StringBuilder();
        sb8.append("      hasSeenQuickScrubOnboarding: ");
        sb8.append(this.hasSeenQuickScrubOnboarding());
        printWriter.println(sb8.toString());
        final StringBuilder sb9 = new StringBuilder();
        sb9.append("      getDismissedSwipeUpOnboardingCount: ");
        sb9.append(this.getDismissedSwipeUpOnboardingCount());
        printWriter.println(sb9.toString());
        final StringBuilder sb10 = new StringBuilder();
        sb10.append("      hasDismissedQuickScrubOnboardingOnce: ");
        sb10.append(this.hasDismissedQuickScrubOnboardingOnce());
        printWriter.println(sb10.toString());
        final StringBuilder sb11 = new StringBuilder();
        sb11.append("      getOpenedOverviewCount: ");
        sb11.append(this.getOpenedOverviewCount());
        printWriter.println(sb11.toString());
        final StringBuilder sb12 = new StringBuilder();
        sb12.append("      getOpenedOverviewFromHomeCount: ");
        sb12.append(this.getOpenedOverviewFromHomeCount());
        printWriter.println(sb12.toString());
        printWriter.println("    }");
    }
    
    public void hide(final boolean b) {
        if (this.mLayoutAttachedToWindow) {
            if (b) {
                this.mLayout.animate().alpha(0.0f).withLayer().setStartDelay(0L).setDuration(100L).setInterpolator((TimeInterpolator)new AccelerateInterpolator()).withEndAction((Runnable)new _$$Lambda$RecentsOnboarding$qki5o8zqrWEPaWaslagffDePdhg(this)).start();
            }
            else {
                this.mLayout.animate().cancel();
                this.mWindowManager.removeViewImmediate(this.mLayout);
            }
        }
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        if (configuration.orientation != 1) {
            this.hide(false);
        }
    }
    
    public void onConnectedToLauncher() {
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            return;
        }
        if (this.hasSeenSwipeUpOnboarding() && this.hasSeenQuickScrubOnboarding()) {
            return;
        }
        if (!this.mOverviewProxyListenerRegistered) {
            this.mOverviewProxyService.addCallback(this.mOverviewProxyListener);
            this.mOverviewProxyListenerRegistered = true;
        }
        if (!this.mTaskListenerRegistered) {
            ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskListener);
            this.mTaskListenerRegistered = true;
        }
    }
    
    public void onDisconnectedFromLauncher() {
        if (this.mOverviewProxyListenerRegistered) {
            this.mOverviewProxyService.removeCallback(this.mOverviewProxyListener);
            this.mOverviewProxyListenerRegistered = false;
        }
        if (this.mTaskListenerRegistered) {
            ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mTaskListener);
            this.mTaskListenerRegistered = false;
        }
        this.mHasDismissedSwipeUpTip = false;
        this.mHasDismissedQuickScrubTip = false;
        this.mNumAppsLaunchedSinceSwipeUpTipDismiss = 0;
        this.mOverviewOpenedCountSinceQuickScrubTipDismiss = 0;
        this.hide(true);
    }
    
    public void onNavigationModeChanged(final int mNavBarMode) {
        this.mNavBarMode = mNavBarMode;
    }
    
    public void setNavBarHeight(final int mNavBarHeight) {
        this.mNavBarHeight = mNavBarHeight;
    }
    
    public boolean show(int dimensionPixelSize) {
        final boolean shouldShow = this.shouldShow();
        final int n = 0;
        if (!shouldShow) {
            return false;
        }
        this.mDismissView.setTag((Object)dimensionPixelSize);
        this.mLayout.setTag((Object)dimensionPixelSize);
        this.mTextView.setText(dimensionPixelSize);
        final int orientation = this.mContext.getResources().getConfiguration().orientation;
        if (!this.mLayoutAttachedToWindow && orientation == 1) {
            this.mLayout.setSystemUiVisibility(256);
            int n3;
            if (dimensionPixelSize == R$string.recents_swipe_up_onboarding) {
                final int n2 = 81;
                dimensionPixelSize = n;
                n3 = n2;
            }
            else {
                if (this.mContext.getResources().getConfiguration().getLayoutDirection() == 0) {
                    dimensionPixelSize = 3;
                }
                else {
                    dimensionPixelSize = 5;
                }
                n3 = (dimensionPixelSize | 0x50);
                dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.recents_quick_scrub_onboarding_margin_start);
            }
            this.mWindowManager.addView(this.mLayout, (ViewGroup$LayoutParams)this.getWindowLayoutParams(n3, dimensionPixelSize));
            this.mLayout.setAlpha(0.0f);
            this.mLayout.animate().alpha(1.0f).withLayer().setStartDelay(500L).setDuration(300L).setInterpolator((TimeInterpolator)new DecelerateInterpolator()).start();
            return true;
        }
        return false;
    }
}
