// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.os.IBinder;
import android.content.IntentFilter;
import com.android.internal.view.AppearanceRegion;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import java.util.concurrent.Executor;
import java.util.Objects;
import android.provider.DeviceConfig;
import android.provider.Settings$Secure;
import android.text.TextUtils;
import android.content.res.Configuration;
import java.util.List;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.StatusBarManager;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.view.InsetsState;
import android.provider.Settings$Global;
import android.view.View$OnLongClickListener;
import android.view.View$OnTouchListener;
import android.view.View$OnClickListener;
import android.graphics.Rect;
import com.android.systemui.R$dimen;
import com.android.internal.util.LatencyTracker;
import android.app.ActivityManager;
import android.app.IActivityTaskManager;
import android.os.RemoteException;
import com.android.systemui.statusbar.policy.KeyButtonView;
import android.app.ActivityTaskManager;
import android.util.Log;
import android.telecom.TelecomManager;
import android.os.UserHandle;
import android.view.Display;
import android.view.MotionEvent;
import android.hardware.display.DisplayManager;
import android.view.ViewGroup$LayoutParams;
import android.app.Fragment;
import com.android.systemui.R$id;
import android.view.View$OnAttachStateChangeListener;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import com.android.systemui.R$string;
import android.os.Binder;
import android.view.View;
import com.android.systemui.fragments.FragmentHostManager;
import android.content.Intent;
import android.content.Context;
import android.provider.DeviceConfig$Properties;
import android.net.Uri;
import android.os.Looper;
import android.os.Bundle;
import com.android.systemui.shared.system.QuickStepContract;
import android.view.WindowManager;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.model.SysUiState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import dagger.Lazy;
import java.util.function.Consumer;
import com.android.systemui.recents.Recents;
import java.util.Optional;
import com.android.systemui.recents.OverviewProxyService;
import android.view.WindowManager$LayoutParams;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.internal.logging.MetricsLogger;
import java.util.Locale;
import android.os.Handler;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.content.ContentResolver;
import android.content.BroadcastReceiver;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.AutoHideUiElement;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.assist.AssistHandleViewController;
import android.database.ContentObserver;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager$AccessibilityServicesStateChangeListener;
import android.hardware.display.DisplayManager$DisplayListener;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.util.LifecycleFragment;

public class NavigationBarFragment extends LifecycleFragment implements Callbacks, ModeChangedListener, DisplayManager$DisplayListener
{
    private final AccessibilityManager$AccessibilityServicesStateChangeListener mAccessibilityListener;
    private AccessibilityManager mAccessibilityManager;
    private final AccessibilityManagerWrapper mAccessibilityManagerWrapper;
    private int mAppearance;
    private final ContentObserver mAssistContentObserver;
    private AssistHandleViewController mAssistHandlerViewController;
    protected final AssistManager mAssistManager;
    private boolean mAssistantAvailable;
    private final Runnable mAutoDim;
    private AutoHideController mAutoHideController;
    private final AutoHideUiElement mAutoHideUiElement;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mBroadcastReceiver;
    private final CommandQueue mCommandQueue;
    private ContentResolver mContentResolver;
    private int mCurrentRotation;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private int mDisabledFlags1;
    private int mDisabledFlags2;
    public int mDisplayId;
    private final Divider mDivider;
    private boolean mFixedRotationEnabled;
    private final ContentObserver mFixedRotationObserver;
    private boolean mForceNavBarHandleOpaque;
    private boolean mFrozenTasks;
    private final Handler mHandler;
    public boolean mHomeBlockedThisTouch;
    private boolean mIsOnDefaultDisplay;
    private long mLastLockToAppLongPress;
    private int mLayoutDirection;
    private LightBarController mLightBarController;
    private Locale mLocale;
    private final MetricsLogger mMetricsLogger;
    private int mNavBarMode;
    private int mNavigationBarMode;
    protected NavigationBarView mNavigationBarView;
    private int mNavigationBarWindowState;
    private int mNavigationIconHints;
    private final NavigationModeController mNavigationModeController;
    private final NotificationRemoteInputManager mNotificationRemoteInputManager;
    private final DeviceConfig$OnPropertiesChangedListener mOnPropertiesChangedListener;
    private NavigationHandle mOrientationHandle;
    private NavigationBarTransitions.DarkIntensityListener mOrientationHandleIntensityListener;
    private WindowManager$LayoutParams mOrientationParams;
    private final OverviewProxyService.OverviewProxyListener mOverviewProxyListener;
    private OverviewProxyService mOverviewProxyService;
    private final Optional<Recents> mRecentsOptional;
    private final ContextualButton.ContextButtonListener mRotationButtonListener;
    private final Consumer<Integer> mRotationWatcher;
    private int mStartingQuickSwitchRotation;
    private final Lazy<StatusBar> mStatusBarLazy;
    private final StatusBarStateController mStatusBarStateController;
    private SysUiState mSysUiFlagsContainer;
    private TaskStackChangeListener mTasksFrozenListener;
    private boolean mTransientShown;
    private WindowManager mWindowManager;
    
    public NavigationBarFragment(final AccessibilityManagerWrapper mAccessibilityManagerWrapper, final DeviceProvisionedController mDeviceProvisionedController, final MetricsLogger mMetricsLogger, final AssistManager mAssistManager, final OverviewProxyService mOverviewProxyService, final NavigationModeController mNavigationModeController, final StatusBarStateController mStatusBarStateController, final SysUiState mSysUiFlagsContainer, final BroadcastDispatcher mBroadcastDispatcher, final CommandQueue mCommandQueue, final Divider mDivider, final Optional<Recents> mRecentsOptional, final Lazy<StatusBar> mStatusBarLazy, final ShadeController shadeController, final NotificationRemoteInputManager mNotificationRemoteInputManager, final Handler mHandler) {
        this.mNavigationBarView = null;
        boolean mAssistantAvailable = false;
        this.mNavigationBarWindowState = 0;
        this.mNavigationIconHints = 0;
        this.mNavBarMode = 0;
        this.mAutoHideUiElement = new AutoHideUiElement() {
            @Override
            public void hide() {
                NavigationBarFragment.this.clearTransient();
            }
            
            @Override
            public boolean isVisible() {
                return NavigationBarFragment.this.isTransientShown();
            }
            
            @Override
            public boolean shouldHideOnTouch() {
                return NavigationBarFragment.this.mNotificationRemoteInputManager.getController().isRemoteInputActive() ^ true;
            }
            
            @Override
            public void synchronizeState() {
                NavigationBarFragment.this.checkNavBarModes();
            }
        };
        this.mOverviewProxyListener = new OverviewProxyService.OverviewProxyListener() {
            @Override
            public void onConnectionChanged(final boolean b) {
                NavigationBarFragment.this.mNavigationBarView.updateStates();
                NavigationBarFragment.this.updateScreenPinningGestures();
                if (b) {
                    final NavigationBarFragment this$0 = NavigationBarFragment.this;
                    this$0.sendAssistantAvailability(this$0.mAssistantAvailable);
                }
            }
            
            @Override
            public void onNavBarButtonAlphaChanged(float n, final boolean b) {
                final boolean swipeUpMode = QuickStepContract.isSwipeUpMode(NavigationBarFragment.this.mNavBarMode);
                final int n2 = 0;
                ButtonDispatcher buttonDispatcher = null;
                boolean access$1000 = false;
                Label_0074: {
                    if (swipeUpMode) {
                        buttonDispatcher = NavigationBarFragment.this.mNavigationBarView.getBackButton();
                    }
                    else {
                        if (QuickStepContract.isGesturalMode(NavigationBarFragment.this.mNavBarMode)) {
                            access$1000 = NavigationBarFragment.this.mForceNavBarHandleOpaque;
                            buttonDispatcher = NavigationBarFragment.this.mNavigationBarView.getHomeHandle();
                            break Label_0074;
                        }
                        buttonDispatcher = null;
                    }
                    access$1000 = false;
                }
                if (buttonDispatcher != null) {
                    int visibility = n2;
                    if (!access$1000) {
                        if (n > 0.0f) {
                            visibility = n2;
                        }
                        else {
                            visibility = 4;
                        }
                    }
                    buttonDispatcher.setVisibility(visibility);
                    if (access$1000) {
                        n = 1.0f;
                    }
                    buttonDispatcher.setAlpha(n, b);
                }
            }
            
            @Override
            public void onQuickSwitchToNewTask(final int n) {
                NavigationBarFragment.this.mStartingQuickSwitchRotation = n;
                NavigationBarFragment.this.orientSecondaryHomeHandle();
            }
            
            @Override
            public void startAssistant(final Bundle bundle) {
                NavigationBarFragment.this.mAssistManager.startAssist(bundle);
            }
        };
        this.mTasksFrozenListener = new TaskStackChangeListener() {
            @Override
            public void onRecentTaskListFrozenChanged(final boolean b) {
                NavigationBarFragment.this.mFrozenTasks = b;
                NavigationBarFragment.this.orientSecondaryHomeHandle();
            }
        };
        this.mOrientationHandleIntensityListener = new NavigationBarTransitions.DarkIntensityListener() {
            @Override
            public void onDarkIntensity(final float darkIntensity) {
                NavigationBarFragment.this.mOrientationHandle.setDarkIntensity(darkIntensity);
            }
        };
        this.mRotationButtonListener = new _$$Lambda$NavigationBarFragment$xnm4oWC06_iZWq_zBbKv8ubGVFU(this);
        this.mAutoDim = new _$$Lambda$NavigationBarFragment$Wf_FUQzkbSdMD9hXKJaXOD_rVSY(this);
        this.mAssistContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(final boolean b, final Uri uri) {
                final boolean b2 = NavigationBarFragment.this.mAssistManager.getAssistInfoForUser(-2) != null;
                if (NavigationBarFragment.this.mAssistantAvailable != b2) {
                    NavigationBarFragment.this.sendAssistantAvailability(b2);
                    NavigationBarFragment.this.mAssistantAvailable = b2;
                }
            }
        };
        this.mFixedRotationObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(final boolean b, final Uri uri) {
                NavigationBarFragment.this.updatedFixedRotation();
            }
        };
        this.mOnPropertiesChangedListener = (DeviceConfig$OnPropertiesChangedListener)new DeviceConfig$OnPropertiesChangedListener() {
            public void onPropertiesChanged(final DeviceConfig$Properties deviceConfig$Properties) {
                if (deviceConfig$Properties.getKeyset().contains("nav_bar_handle_force_opaque")) {
                    NavigationBarFragment.this.mForceNavBarHandleOpaque = deviceConfig$Properties.getBoolean("nav_bar_handle_force_opaque", true);
                }
            }
        };
        this.mAccessibilityListener = (AccessibilityManager$AccessibilityServicesStateChangeListener)new _$$Lambda$NavigationBarFragment$dxES00kAyC8r2RmY9FwTYgUhoj8(this);
        this.mRotationWatcher = (Consumer<Integer>)new _$$Lambda$NavigationBarFragment$JVziusX7tSv19aMDDuLOI_SWKI8(this);
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if ("android.intent.action.SCREEN_OFF".equals(action) || "android.intent.action.SCREEN_ON".equals(action)) {
                    NavigationBarFragment.this.notifyNavigationBarScreenOn();
                    NavigationBarFragment.this.mNavigationBarView.onScreenStateChanged("android.intent.action.SCREEN_ON".equals(action));
                }
                if ("android.intent.action.USER_SWITCHED".equals(action)) {
                    final NavigationBarFragment this$0 = NavigationBarFragment.this;
                    this$0.updateAccessibilityServicesState(this$0.mAccessibilityManager);
                }
            }
        };
        this.mAccessibilityManagerWrapper = mAccessibilityManagerWrapper;
        this.mDeviceProvisionedController = mDeviceProvisionedController;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mMetricsLogger = mMetricsLogger;
        this.mAssistManager = mAssistManager;
        this.mSysUiFlagsContainer = mSysUiFlagsContainer;
        this.mStatusBarLazy = mStatusBarLazy;
        this.mNotificationRemoteInputManager = mNotificationRemoteInputManager;
        if (mAssistManager.getAssistInfoForUser(-2) != null) {
            mAssistantAvailable = true;
        }
        this.mAssistantAvailable = mAssistantAvailable;
        this.mOverviewProxyService = mOverviewProxyService;
        this.mNavigationModeController = mNavigationModeController;
        this.mNavBarMode = mNavigationModeController.addListener((NavigationModeController.ModeChangedListener)this);
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mCommandQueue = mCommandQueue;
        this.mDivider = mDivider;
        this.mRecentsOptional = mRecentsOptional;
        this.mHandler = mHandler;
    }
    
    private static int barMode(final boolean b, final int n) {
        if (b) {
            return 1;
        }
        if ((n & 0x6) == 0x6) {
            return 3;
        }
        if ((n & 0x4) != 0x0) {
            return 6;
        }
        if ((n & 0x2) != 0x0) {
            return 4;
        }
        return 0;
    }
    
    private boolean canShowSecondaryHandle() {
        return this.mFixedRotationEnabled && this.mNavBarMode == 2;
    }
    
    private void checkBarModes() {
        if (this.mIsOnDefaultDisplay) {
            this.mStatusBarLazy.get().checkBarModes();
        }
        else {
            this.checkNavBarModes();
        }
    }
    
    private void clearTransient() {
        if (this.mTransientShown) {
            this.mTransientShown = false;
            this.handleTransientChanged();
        }
    }
    
    public static View create(final Context context, final FragmentHostManager.FragmentListener fragmentListener) {
        final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(-1, -1, 2019, 545521768, -3);
        windowManager$LayoutParams.token = (IBinder)new Binder();
        final StringBuilder sb = new StringBuilder();
        sb.append("NavigationBar");
        sb.append(context.getDisplayId());
        windowManager$LayoutParams.setTitle((CharSequence)sb.toString());
        windowManager$LayoutParams.accessibilityTitle = context.getString(R$string.nav_bar);
        windowManager$LayoutParams.windowAnimations = 0;
        windowManager$LayoutParams.privateFlags |= 0x1000000;
        final View inflate = LayoutInflater.from(context).inflate(R$layout.navigation_bar_window, (ViewGroup)null);
        if (inflate == null) {
            return null;
        }
        inflate.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            final /* synthetic */ NavigationBarFragment val$fragment = FragmentHostManager.get(inflate).create(NavigationBarFragment.class);
            
            public void onViewAttachedToWindow(final View view) {
                final FragmentHostManager value = FragmentHostManager.get(view);
                value.getFragmentManager().beginTransaction().replace(R$id.navigation_bar_frame, (Fragment)this.val$fragment, "NavigationBar").commit();
                value.addTagListener("NavigationBar", fragmentListener);
            }
            
            public void onViewDetachedFromWindow(final View view) {
                FragmentHostManager.removeAndDestroy(view);
                inflate.removeOnAttachStateChangeListener((View$OnAttachStateChangeListener)this);
            }
        });
        ((WindowManager)context.getSystemService((Class)WindowManager.class)).addView(inflate, (ViewGroup$LayoutParams)windowManager$LayoutParams);
        return inflate;
    }
    
    private int deltaRotation(int n, int n2) {
        n2 = (n = n2 - n);
        if (n2 < 0) {
            n = n2 + 4;
        }
        return n;
    }
    
    private void handleTransientChanged() {
        if (this.getView() == null) {
            return;
        }
        final NavigationBarView mNavigationBarView = this.mNavigationBarView;
        if (mNavigationBarView != null) {
            mNavigationBarView.onTransientStateChanged(this.mTransientShown);
        }
        final int barMode = barMode(this.mTransientShown, this.mAppearance);
        if (this.updateBarMode(barMode)) {
            this.mLightBarController.onNavigationBarModeChanged(barMode);
        }
    }
    
    private void initSecondaryHomeHandleForRotation() {
        if (!this.canShowSecondaryHandle()) {
            return;
        }
        ((DisplayManager)this.getContext().getSystemService((Class)DisplayManager.class)).registerDisplayListener((DisplayManager$DisplayListener)this, new Handler(Looper.getMainLooper()));
        this.mOrientationHandle = new VerticalNavigationHandle(this.getContext());
        this.getBarTransitions().addDarkIntensityListener(this.mOrientationHandleIntensityListener);
        final WindowManager$LayoutParams mOrientationParams = new WindowManager$LayoutParams(0, 0, 2024, 545259816, -3);
        this.mOrientationParams = mOrientationParams;
        this.mWindowManager.addView((View)this.mOrientationHandle, (ViewGroup$LayoutParams)mOrientationParams);
        this.mOrientationHandle.setVisibility(8);
    }
    
    private boolean isTransientShown() {
        return this.mTransientShown;
    }
    
    private void notifyNavigationBarScreenOn() {
        this.mNavigationBarView.updateNavButtonIcons();
    }
    
    private void onAccessibilityClick(final View view) {
        final Display display = view.getDisplay();
        final AccessibilityManager mAccessibilityManager = this.mAccessibilityManager;
        int displayId;
        if (display != null) {
            displayId = display.getDisplayId();
        }
        else {
            displayId = 0;
        }
        mAccessibilityManager.notifyAccessibilityButtonClicked(displayId);
    }
    
    private boolean onAccessibilityLongClick(final View view) {
        final Intent intent = new Intent("com.android.internal.intent.action.CHOOSE_ACCESSIBILITY_BUTTON");
        intent.addFlags(268468224);
        intent.putExtra("com.android.internal.intent.extra.SHORTCUT_TYPE", 0);
        view.getContext().startActivityAsUser(intent, UserHandle.CURRENT);
        return true;
    }
    
    private boolean onHomeTouch(final View view, final MotionEvent motionEvent) {
        if (this.mHomeBlockedThisTouch && motionEvent.getActionMasked() != 0) {
            return true;
        }
        final int action = motionEvent.getAction();
        if (action != 0) {
            if (action == 1 || action == 3) {
                this.mStatusBarLazy.get().awakenDreams();
            }
        }
        else {
            this.mHomeBlockedThisTouch = false;
            final TelecomManager telecomManager = (TelecomManager)this.getContext().getSystemService((Class)TelecomManager.class);
            if (telecomManager != null && telecomManager.isRinging() && this.mStatusBarLazy.get().isKeyguardShowing()) {
                Log.i("NavigationBar", "Ignoring HOME; there's a ringing incoming call. No heads up");
                return this.mHomeBlockedThisTouch = true;
            }
        }
        return false;
    }
    
    private boolean onLongPressBackHome(final View view) {
        return this.onLongPressNavigationButtons(view, R$id.back, R$id.home);
    }
    
    private boolean onLongPressBackRecents(final View view) {
        return this.onLongPressNavigationButtons(view, R$id.back, R$id.recent_apps);
    }
    
    private boolean onLongPressNavigationButtons(final View view, int n, final int n2) {
        try {
            final IActivityTaskManager service = ActivityTaskManager.getService();
            final boolean touchExplorationEnabled = this.mAccessibilityManager.isTouchExplorationEnabled();
            final boolean inLockTaskMode = service.isInLockTaskMode();
            Label_0135: {
                if (!inLockTaskMode || touchExplorationEnabled) {
                    break Label_0135;
                }
                try {
                    final long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - this.mLastLockToAppLongPress < 200L) {
                        service.stopSystemLockTaskMode();
                        this.mNavigationBarView.updateNavButtonIcons();
                        return true;
                    }
                    Label_0126: {
                        if (view.getId() == n) {
                            ButtonDispatcher buttonDispatcher;
                            if (n2 == R$id.recent_apps) {
                                buttonDispatcher = this.mNavigationBarView.getRecentsButton();
                            }
                            else {
                                buttonDispatcher = this.mNavigationBarView.getHomeButton();
                            }
                            if (!buttonDispatcher.getCurrentView().isPressed()) {
                                n = 1;
                                break Label_0126;
                            }
                        }
                        n = 0;
                    }
                    this.mLastLockToAppLongPress = currentTimeMillis;
                    // iftrue(Label_0152:, view.getId() != n)
                    // iftrue(Label_0178:, !touchExplorationEnabled || !inLockTaskMode)
                    boolean b;
                    while (true) {
                        if (n != 0) {
                            final KeyButtonView keyButtonView = (KeyButtonView)view;
                            keyButtonView.sendEvent(0, 128);
                            keyButtonView.sendAccessibilityEvent(2);
                            return true;
                        }
                        return false;
                        Block_9: {
                            break Block_9;
                            Label_0202: {
                                b = this.onHomeLongClick(this.mNavigationBarView.getHomeButton().getCurrentView());
                            }
                            return b;
                            Label_0152:
                            service.stopSystemLockTaskMode();
                            this.mNavigationBarView.updateNavButtonIcons();
                            return true;
                        }
                        n = 1;
                        continue;
                    }
                    Label_0178: {
                        b = this.onLongPressRecents();
                    }
                    // iftrue(Label_0221:, view.getId() != n2)
                    // iftrue(Label_0202:, n2 != R$id.recent_apps)
                    return b;
                    Label_0221: {
                        n = 0;
                    }
                }
                finally {}
            }
        }
        catch (RemoteException ex) {
            Log.d("NavigationBar", "Unable to reach activity manager", (Throwable)ex);
        }
        return false;
    }
    
    private boolean onLongPressRecents() {
        return !this.mRecentsOptional.isPresent() && ActivityTaskManager.supportsMultiWindow(this.getContext()) && this.mDivider.getView().getSnapAlgorithm().isSplitScreenFeasible() && !ActivityManager.isLowRamDeviceStatic() && this.mOverviewProxyService.getProxy() == null && this.mStatusBarLazy.get().toggleSplitScreenMode(271, 286);
    }
    
    private boolean onNavigationTouch(final View view, final MotionEvent motionEvent) {
        this.mAutoHideController.checkUserAutoHide(motionEvent);
        return false;
    }
    
    private void onRecentsClick(final View view) {
        if (LatencyTracker.isEnabled(this.getContext())) {
            LatencyTracker.getInstance(this.getContext()).onActionStart(1);
        }
        this.mStatusBarLazy.get().awakenDreams();
        this.mCommandQueue.toggleRecentApps();
    }
    
    private boolean onRecentsTouch(final View view, final MotionEvent motionEvent) {
        final int n = motionEvent.getAction() & 0xFF;
        if (n == 0) {
            this.mCommandQueue.preloadRecentApps();
        }
        else if (n == 3) {
            this.mCommandQueue.cancelPreloadRecentApps();
        }
        else if (n == 1 && !view.isPressed()) {
            this.mCommandQueue.cancelPreloadRecentApps();
        }
        return false;
    }
    
    private void onVerticalChanged(final boolean b) {
        this.mStatusBarLazy.get().setQsScrimEnabled(b ^ true);
    }
    
    private void orientSecondaryHomeHandle() {
        if (!this.canShowSecondaryHandle()) {
            return;
        }
        if (this.mFrozenTasks) {
            final int deltaRotation = this.deltaRotation(this.mCurrentRotation, this.mStartingQuickSwitchRotation);
            final Rect bounds = this.mWindowManager.getCurrentWindowMetrics().getBounds();
            Label_0166: {
                if (deltaRotation != 0) {
                    int gravity = 3;
                    int dimensionPixelSize = 0;
                    int height = 0;
                    Label_0097: {
                        if (deltaRotation != 1) {
                            if (deltaRotation == 2) {
                                break Label_0166;
                            }
                            if (deltaRotation != 3) {
                                height = (dimensionPixelSize = 0);
                                break Label_0097;
                            }
                        }
                        height = bounds.height();
                        dimensionPixelSize = this.getResources().getDimensionPixelSize(R$dimen.navigation_bar_height);
                    }
                    final WindowManager$LayoutParams mOrientationParams = this.mOrientationParams;
                    if (deltaRotation != 1) {
                        gravity = 5;
                    }
                    mOrientationParams.gravity = gravity;
                    final WindowManager$LayoutParams mOrientationParams2 = this.mOrientationParams;
                    mOrientationParams2.height = height;
                    mOrientationParams2.width = dimensionPixelSize;
                    this.mWindowManager.updateViewLayout((View)this.mOrientationHandle, (ViewGroup$LayoutParams)mOrientationParams2);
                    this.mNavigationBarView.setVisibility(8);
                    this.mOrientationHandle.setVisibility(0);
                    return;
                }
            }
            this.resetSecondaryHandle();
            return;
        }
        this.resetSecondaryHandle();
    }
    
    private void prepareNavigationBarView() {
        this.mNavigationBarView.reorient();
        final ButtonDispatcher recentsButton = this.mNavigationBarView.getRecentsButton();
        recentsButton.setOnClickListener((View$OnClickListener)new _$$Lambda$NavigationBarFragment$0mmLLxBq7RxotphHQB_RtYb4SpQ(this));
        recentsButton.setOnTouchListener((View$OnTouchListener)new _$$Lambda$NavigationBarFragment$VEqqEZFjg0f3lWOW2BJ66Oo_2aE(this));
        recentsButton.setLongClickable(true);
        recentsButton.setOnLongClickListener((View$OnLongClickListener)new _$$Lambda$NavigationBarFragment$dtGeJfWz2E4_XAoQgX8peIw4kU8(this));
        this.mNavigationBarView.getBackButton().setLongClickable(true);
        final ButtonDispatcher homeButton = this.mNavigationBarView.getHomeButton();
        homeButton.setOnTouchListener((View$OnTouchListener)new _$$Lambda$NavigationBarFragment$y_1OHmWTpLl8uCcO3A0Am620g94(this));
        homeButton.setOnLongClickListener((View$OnLongClickListener)new _$$Lambda$8vcstZEv0YyG7EUTK_UrsNSFXRo(this));
        final ButtonDispatcher accessibilityButton = this.mNavigationBarView.getAccessibilityButton();
        accessibilityButton.setOnClickListener((View$OnClickListener)new _$$Lambda$NavigationBarFragment$Ylizyb5K7ZQr77j1Ehc8SUjcI6E(this));
        accessibilityButton.setOnLongClickListener((View$OnLongClickListener)new _$$Lambda$NavigationBarFragment$RtBTLxltRKo37YrTKiaCXCxwRDg(this));
        this.updateAccessibilityServicesState(this.mAccessibilityManager);
        this.updateScreenPinningGestures();
    }
    
    private void refreshLayout(final int layoutDirection) {
        final NavigationBarView mNavigationBarView = this.mNavigationBarView;
        if (mNavigationBarView != null) {
            mNavigationBarView.setLayoutDirection(layoutDirection);
        }
    }
    
    private void repositionNavigationBar() {
        final NavigationBarView mNavigationBarView = this.mNavigationBarView;
        if (mNavigationBarView != null) {
            if (mNavigationBarView.isAttachedToWindow()) {
                this.prepareNavigationBarView();
                this.mWindowManager.updateViewLayout((View)this.mNavigationBarView.getParent(), ((View)this.mNavigationBarView.getParent()).getLayoutParams());
            }
        }
    }
    
    private void resetSecondaryHandle() {
        final NavigationHandle mOrientationHandle = this.mOrientationHandle;
        if (mOrientationHandle != null) {
            mOrientationHandle.setVisibility(8);
        }
        final NavigationBarView mNavigationBarView = this.mNavigationBarView;
        if (mNavigationBarView != null) {
            mNavigationBarView.setVisibility(0);
        }
    }
    
    private void sendAssistantAvailability(final boolean b) {
        if (this.mOverviewProxyService.getProxy() != null) {
            try {
                this.mOverviewProxyService.getProxy().onAssistantAvailable(b && QuickStepContract.isGesturalMode(this.mNavBarMode));
            }
            catch (RemoteException ex) {
                Log.w("NavigationBar", "Unable to send assistant availability data to launcher");
            }
        }
    }
    
    private void setDisabled2Flags(final int n) {
        final NavigationBarView mNavigationBarView = this.mNavigationBarView;
        if (mNavigationBarView != null) {
            mNavigationBarView.getRotationButtonController().onDisable2FlagChanged(n);
        }
    }
    
    private boolean shouldDisableNavbarGestures() {
        return !this.mDeviceProvisionedController.isDeviceProvisioned() || (this.mDisabledFlags1 & 0x2000000) != 0x0;
    }
    
    private void updateAccessibilityServicesState(final AccessibilityManager accessibilityManager) {
        boolean b = true;
        final int a11yButtonState = this.getA11yButtonState(new boolean[1]);
        final boolean b2 = (a11yButtonState & 0x10) != 0x0;
        if ((a11yButtonState & 0x20) == 0x0) {
            b = false;
        }
        this.mNavigationBarView.setAccessibilityButtonState(b2, b);
        this.updateSystemUiStateFlags(a11yButtonState);
    }
    
    private boolean updateBarMode(final int mNavigationBarMode) {
        final int mNavigationBarMode2 = this.mNavigationBarMode;
        if (mNavigationBarMode2 != mNavigationBarMode) {
            if (mNavigationBarMode2 == 0 || mNavigationBarMode2 == 6) {
                this.mNavigationBarView.hideRecentsOnboarding();
            }
            this.mNavigationBarMode = mNavigationBarMode;
            this.checkNavBarModes();
            this.mAutoHideController.touchAutoHide();
            return true;
        }
        return false;
    }
    
    private void updateScreenPinningGestures() {
        final NavigationBarView mNavigationBarView = this.mNavigationBarView;
        if (mNavigationBarView == null) {
            return;
        }
        final boolean recentsButtonVisible = mNavigationBarView.isRecentsButtonVisible();
        final ButtonDispatcher backButton = this.mNavigationBarView.getBackButton();
        if (recentsButtonVisible) {
            backButton.setOnLongClickListener((View$OnLongClickListener)new _$$Lambda$NavigationBarFragment$dtGeJfWz2E4_XAoQgX8peIw4kU8(this));
        }
        else {
            backButton.setOnLongClickListener((View$OnLongClickListener)new _$$Lambda$NavigationBarFragment$oZtQ9jE1OTI8AtitIxsN6ETT4sc(this));
        }
    }
    
    private void updatedFixedRotation() {
        final ContentResolver mContentResolver = this.mContentResolver;
        boolean mFixedRotationEnabled = false;
        if (Settings$Global.getInt(mContentResolver, "fixed_rotation_transform", 0) != 0) {
            mFixedRotationEnabled = true;
        }
        this.mFixedRotationEnabled = mFixedRotationEnabled;
        if (!this.canShowSecondaryHandle()) {
            this.resetSecondaryHandle();
        }
    }
    
    @Override
    public void abortTransient(final int n, final int[] array) {
        if (n != this.mDisplayId) {
            return;
        }
        if (!InsetsState.containsType(array, 1)) {
            return;
        }
        this.clearTransient();
    }
    
    public void checkNavBarModes() {
        this.mNavigationBarView.getBarTransitions().transitionTo(this.mNavigationBarMode, this.mStatusBarLazy.get().isDeviceInteractive() && this.mNavigationBarWindowState != 2);
    }
    
    @Override
    public void disable(int n, final int disabledFlags, final int n2, final boolean b) {
        if (n != this.mDisplayId) {
            return;
        }
        n = (0x3600000 & disabledFlags);
        if (n != this.mDisabledFlags1) {
            this.mDisabledFlags1 = n;
            final NavigationBarView mNavigationBarView = this.mNavigationBarView;
            if (mNavigationBarView != null) {
                mNavigationBarView.setDisabledFlags(disabledFlags);
            }
            this.updateScreenPinningGestures();
        }
        if (this.mIsOnDefaultDisplay) {
            n = (n2 & 0x10);
            if (n != this.mDisabledFlags2) {
                this.setDisabled2Flags(this.mDisabledFlags2 = n);
            }
        }
    }
    
    public void disableAnimationsDuringHide(final long n) {
        this.mNavigationBarView.setLayoutTransitionsEnabled(false);
        this.mNavigationBarView.postDelayed((Runnable)new _$$Lambda$NavigationBarFragment$i3mhmHWtnMuMGNO528iejYx75q0(this), n + 448L);
    }
    
    public void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        if (this.mNavigationBarView != null) {
            printWriter.print("  mNavigationBarWindowState=");
            printWriter.println(StatusBarManager.windowStateToString(this.mNavigationBarWindowState));
            printWriter.print("  mNavigationBarMode=");
            printWriter.println(BarTransitions.modeToString(this.mNavigationBarMode));
            StatusBar.dumpBarTransitions(printWriter, "mNavigationBarView", this.mNavigationBarView.getBarTransitions());
        }
        printWriter.print("  mNavigationBarView=");
        final NavigationBarView mNavigationBarView = this.mNavigationBarView;
        if (mNavigationBarView == null) {
            printWriter.println("null");
        }
        else {
            mNavigationBarView.dump(fileDescriptor, printWriter, array);
        }
    }
    
    public void finishBarAnimations() {
        this.mNavigationBarView.getBarTransitions().finishAnimations();
    }
    
    public int getA11yButtonState(final boolean[] array) {
        final List enabledAccessibilityServiceList = this.mAccessibilityManager.getEnabledAccessibilityServiceList(-1);
        final AccessibilityManager mAccessibilityManager = this.mAccessibilityManager;
        int n = 0;
        final int size = mAccessibilityManager.getAccessibilityShortcutTargets(0).size();
        int n2 = enabledAccessibilityServiceList.size() - 1;
        boolean b = false;
        int n3;
        while (true) {
            n3 = 16;
            if (n2 < 0) {
                break;
            }
            final int feedbackType = enabledAccessibilityServiceList.get(n2).feedbackType;
            boolean b2 = b;
            if (feedbackType != 0) {
                b2 = b;
                if (feedbackType != 16) {
                    b2 = true;
                }
            }
            --n2;
            b = b2;
        }
        if (array != null) {
            array[0] = b;
        }
        int n4;
        if (size >= 1) {
            n4 = n3;
        }
        else {
            n4 = 0;
        }
        if (size >= 2) {
            n = 32;
        }
        return n4 | n;
    }
    
    public AssistHandleViewController getAssistHandlerViewController() {
        return this.mAssistHandlerViewController;
    }
    
    public NavigationBarTransitions getBarTransitions() {
        return this.mNavigationBarView.getBarTransitions();
    }
    
    int getNavigationIconHints() {
        return this.mNavigationIconHints;
    }
    
    public boolean isNavBarWindowVisible() {
        return this.mNavigationBarWindowState == 0;
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final Locale locale = this.getContext().getResources().getConfiguration().locale;
        final int layoutDirectionFromLocale = TextUtils.getLayoutDirectionFromLocale(locale);
        if (!locale.equals(this.mLocale) || layoutDirectionFromLocale != this.mLayoutDirection) {
            this.mLocale = locale;
            this.refreshLayout(this.mLayoutDirection = layoutDirectionFromLocale);
        }
        this.repositionNavigationBar();
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.mCommandQueue.observe(this.getLifecycle(), (CommandQueue.Callbacks)this);
        this.mWindowManager = (WindowManager)this.getContext().getSystemService((Class)WindowManager.class);
        this.mAccessibilityManager = (AccessibilityManager)this.getContext().getSystemService((Class)AccessibilityManager.class);
        (this.mContentResolver = this.getContext().getContentResolver()).registerContentObserver(Settings$Secure.getUriFor("assistant"), false, this.mAssistContentObserver, -1);
        this.mContentResolver.registerContentObserver(Settings$Global.getUriFor("fixed_rotation_transform"), false, this.mFixedRotationObserver, -1);
        if (bundle != null) {
            this.mDisabledFlags1 = bundle.getInt("disabled_state", 0);
            this.mDisabledFlags2 = bundle.getInt("disabled2_state", 0);
            this.mAppearance = bundle.getInt("appearance", 0);
            this.mTransientShown = bundle.getBoolean("transient_state", false);
        }
        this.mAccessibilityManagerWrapper.addCallback(this.mAccessibilityListener);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, false);
        this.mForceNavBarHandleOpaque = DeviceConfig.getBoolean("systemui", "nav_bar_handle_force_opaque", true);
        final Handler mHandler = this.mHandler;
        Objects.requireNonNull(mHandler);
        DeviceConfig.addOnPropertiesChangedListener("systemui", (Executor)new _$$Lambda$LfzJt661qZfn2w_6SYHFbD3aMy0(mHandler), this.mOnPropertiesChangedListener);
    }
    
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        return layoutInflater.inflate(R$layout.navigation_bar, viewGroup, false);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mNavigationModeController.removeListener((NavigationModeController.ModeChangedListener)this);
        this.mAccessibilityManagerWrapper.removeCallback(this.mAccessibilityListener);
        this.mContentResolver.unregisterContentObserver(this.mAssistContentObserver);
        this.mContentResolver.unregisterContentObserver(this.mFixedRotationObserver);
        DeviceConfig.removeOnPropertiesChangedListener(this.mOnPropertiesChangedListener);
    }
    
    public void onDestroyView() {
        super.onDestroyView();
        final NavigationBarView mNavigationBarView = this.mNavigationBarView;
        if (mNavigationBarView != null) {
            if (this.mIsOnDefaultDisplay) {
                mNavigationBarView.getBarTransitions().removeDarkIntensityListener((NavigationBarTransitions.DarkIntensityListener)this.mAssistHandlerViewController);
                this.mAssistHandlerViewController = null;
            }
            this.mNavigationBarView.getBarTransitions().destroy();
            this.mNavigationBarView.getLightTransitionsController().destroy(this.getContext());
        }
        this.mOverviewProxyService.removeCallback(this.mOverviewProxyListener);
        this.mBroadcastDispatcher.unregisterReceiver(this.mBroadcastReceiver);
        ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mTasksFrozenListener);
        if (this.mOrientationHandle != null) {
            this.resetSecondaryHandle();
            ((DisplayManager)this.getContext().getSystemService((Class)DisplayManager.class)).unregisterDisplayListener((DisplayManager$DisplayListener)this);
            this.getBarTransitions().removeDarkIntensityListener(this.mOrientationHandleIntensityListener);
            this.mWindowManager.removeView((View)this.mOrientationHandle);
        }
    }
    
    public void onDisplayAdded(final int n) {
    }
    
    public void onDisplayChanged(int rotation) {
        if (!this.canShowSecondaryHandle()) {
            return;
        }
        rotation = this.getContext().getResources().getConfiguration().windowConfiguration.getRotation();
        if (rotation != this.mCurrentRotation) {
            this.mCurrentRotation = rotation;
            this.orientSecondaryHomeHandle();
        }
    }
    
    @Override
    public void onDisplayRemoved(final int n) {
    }
    
    boolean onHomeLongClick(final View view) {
        if (!this.mNavigationBarView.isRecentsButtonVisible() && ActivityManagerWrapper.getInstance().isScreenPinningActive()) {
            return this.onLongPressBackHome(view);
        }
        if (this.shouldDisableNavbarGestures()) {
            return false;
        }
        this.mMetricsLogger.action(239);
        final Bundle bundle = new Bundle();
        bundle.putInt("invocation_type", 5);
        this.mAssistManager.startAssist(bundle);
        this.mStatusBarLazy.get().awakenDreams();
        final NavigationBarView mNavigationBarView = this.mNavigationBarView;
        if (mNavigationBarView != null) {
            mNavigationBarView.abortCurrentGesture();
        }
        return true;
    }
    
    @Override
    public void onNavigationModeChanged(final int mNavBarMode) {
        this.mNavBarMode = mNavBarMode;
        this.updateScreenPinningGestures();
        if (!this.canShowSecondaryHandle()) {
            this.resetSecondaryHandle();
        }
        if (ActivityManagerWrapper.getInstance().getCurrentUserId() != 0) {
            this.mHandler.post((Runnable)new _$$Lambda$NavigationBarFragment$NAJe_hU0PesszyJ9wLZ6PYoS4_0(this));
        }
    }
    
    @Override
    public void onRotationProposal(final int n, final boolean b) {
        final int rotation = this.mNavigationBarView.getDisplay().getRotation();
        final boolean hasDisable2RotateSuggestionFlag = RotationButtonController.hasDisable2RotateSuggestionFlag(this.mDisabledFlags2);
        final RotationButtonController rotationButtonController = this.mNavigationBarView.getRotationButtonController();
        rotationButtonController.getRotationButton();
        if (hasDisable2RotateSuggestionFlag) {
            return;
        }
        rotationButtonController.onRotationProposal(n, rotation, b);
    }
    
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("disabled_state", this.mDisabledFlags1);
        bundle.putInt("disabled2_state", this.mDisabledFlags2);
        bundle.putInt("appearance", this.mAppearance);
        bundle.putBoolean("transient_state", this.mTransientShown);
        final NavigationBarView mNavigationBarView = this.mNavigationBarView;
        if (mNavigationBarView != null) {
            mNavigationBarView.getLightTransitionsController().saveState(bundle);
        }
    }
    
    @Override
    public void onSystemBarAppearanceChanged(final int n, final int mAppearance, final AppearanceRegion[] array, final boolean b) {
        if (n != this.mDisplayId) {
            return;
        }
        boolean updateBarMode = false;
        if (this.mAppearance != mAppearance) {
            this.mAppearance = mAppearance;
            if (this.getView() == null) {
                return;
            }
            updateBarMode = this.updateBarMode(barMode(this.mTransientShown, mAppearance));
        }
        this.mLightBarController.onNavigationBarAppearanceChanged(mAppearance, updateBarMode, this.mNavigationBarMode, b);
    }
    
    public void onViewCreated(final View view, final Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mNavigationBarView = (NavigationBarView)view;
        final Display display = view.getDisplay();
        if (display != null) {
            final int displayId = display.getDisplayId();
            this.mDisplayId = displayId;
            this.mIsOnDefaultDisplay = (displayId == 0);
        }
        this.mNavigationBarView.setComponents(this.mStatusBarLazy.get().getPanelController());
        this.mNavigationBarView.setDisabledFlags(this.mDisabledFlags1);
        this.mNavigationBarView.setOnVerticalChangedListener((NavigationBarView.OnVerticalChangedListener)new _$$Lambda$NavigationBarFragment$eFJm5m1txtISSi8Cx3m3pc8Nvjw(this));
        this.mNavigationBarView.setOnTouchListener((View$OnTouchListener)new _$$Lambda$NavigationBarFragment$X9JO9eLzlFoQkYf8XrZG_l2EMsk(this));
        if (bundle != null) {
            this.mNavigationBarView.getLightTransitionsController().restoreState(bundle);
        }
        this.mNavigationBarView.setNavigationIconHints(this.mNavigationIconHints);
        this.mNavigationBarView.setWindowVisible(this.isNavBarWindowVisible());
        this.updatedFixedRotation();
        this.prepareNavigationBarView();
        this.checkNavBarModes();
        final IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this.mBroadcastReceiver, intentFilter, Handler.getMain(), UserHandle.ALL);
        this.notifyNavigationBarScreenOn();
        this.mOverviewProxyService.addCallback(this.mOverviewProxyListener);
        this.updateSystemUiStateFlags(-1);
        if (this.mIsOnDefaultDisplay) {
            this.mNavigationBarView.getRotateSuggestionButton().setListener(this.mRotationButtonListener);
            final RotationButtonController rotationButtonController = this.mNavigationBarView.getRotationButtonController();
            rotationButtonController.addRotationCallback(this.mRotationWatcher);
            if (display != null && rotationButtonController.isRotationLocked()) {
                rotationButtonController.setRotationLockedAtAngle(display.getRotation());
            }
        }
        else {
            this.mDisabledFlags2 |= 0x10;
        }
        this.setDisabled2Flags(this.mDisabledFlags2);
        if (this.mIsOnDefaultDisplay) {
            this.mAssistHandlerViewController = new AssistHandleViewController(this.mHandler, (View)this.mNavigationBarView);
            this.getBarTransitions().addDarkIntensityListener((NavigationBarTransitions.DarkIntensityListener)this.mAssistHandlerViewController);
        }
        this.initSecondaryHomeHandleForRotation();
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTasksFrozenListener);
    }
    
    public void restoreAppearanceAndTransientState() {
        final int barMode = barMode(this.mTransientShown, this.mAppearance);
        this.mNavigationBarMode = barMode;
        this.checkNavBarModes();
        this.mAutoHideController.touchAutoHide();
        this.mLightBarController.onNavigationBarAppearanceChanged(this.mAppearance, true, barMode, false);
    }
    
    public void setAutoHideController(final AutoHideController mAutoHideController) {
        this.mAutoHideController = mAutoHideController;
        if (mAutoHideController != null) {
            mAutoHideController.setNavigationBar(this.mAutoHideUiElement);
        }
    }
    
    @Override
    public void setImeWindowStatus(int n, final IBinder binder, int mNavigationIconHints, final int n2, final boolean b) {
        if (n != this.mDisplayId) {
            return;
        }
        if ((mNavigationIconHints & 0x2) != 0x0) {
            n = 1;
        }
        else {
            n = 0;
        }
        mNavigationIconHints = this.mNavigationIconHints;
        if ((n2 == 0 || n2 == 1 || n2 == 2) ? (n != 0) : (n2 != 3)) {
            n = mNavigationIconHints;
        }
        else {
            n = (mNavigationIconHints & 0xFFFFFFFE);
        }
        if (b) {
            n |= 0x2;
        }
        else {
            n &= 0xFFFFFFFD;
        }
        if (n == this.mNavigationIconHints) {
            return;
        }
        this.mNavigationIconHints = n;
        final NavigationBarView mNavigationBarView = this.mNavigationBarView;
        if (mNavigationBarView != null) {
            mNavigationBarView.setNavigationIconHints(n);
        }
        this.checkBarModes();
    }
    
    public void setLightBarController(final LightBarController mLightBarController) {
        (this.mLightBarController = mLightBarController).setNavigationBar(this.mNavigationBarView.getLightTransitionsController());
    }
    
    @Override
    public void setWindowState(final int n, final int n2, final int mNavigationBarWindowState) {
        if (n == this.mDisplayId && n2 == 2 && this.mNavigationBarWindowState != mNavigationBarWindowState) {
            this.mNavigationBarWindowState = mNavigationBarWindowState;
            this.updateSystemUiStateFlags(-1);
            final NavigationBarView mNavigationBarView = this.mNavigationBarView;
            if (mNavigationBarView != null) {
                mNavigationBarView.setWindowVisible(this.isNavBarWindowVisible());
            }
        }
    }
    
    @Override
    public void showTransient(final int n, final int[] array) {
        if (n != this.mDisplayId) {
            return;
        }
        if (!InsetsState.containsType(array, 1)) {
            return;
        }
        if (!this.mTransientShown) {
            this.mTransientShown = true;
            this.handleTransientChanged();
        }
    }
    
    public void touchAutoDim() {
        this.getBarTransitions().setAutoDim(false);
        this.mHandler.removeCallbacks(this.mAutoDim);
        final int state = this.mStatusBarStateController.getState();
        if (state != 1 && state != 2) {
            this.mHandler.postDelayed(this.mAutoDim, 2250L);
        }
    }
    
    public void transitionTo(final int n, final boolean b) {
        this.getBarTransitions().transitionTo(n, b);
    }
    
    public void updateSystemUiStateFlags(final int n) {
        int a11yButtonState = n;
        if (n < 0) {
            a11yButtonState = this.getA11yButtonState(null);
        }
        boolean b = false;
        final boolean b2 = (a11yButtonState & 0x10) != 0x0;
        if ((a11yButtonState & 0x20) != 0x0) {
            b = true;
        }
        final SysUiState mSysUiFlagsContainer = this.mSysUiFlagsContainer;
        mSysUiFlagsContainer.setFlag(16, b2);
        mSysUiFlagsContainer.setFlag(32, b);
        mSysUiFlagsContainer.setFlag(2, this.isNavBarWindowVisible() ^ true);
        mSysUiFlagsContainer.commitUpdate(this.mDisplayId);
    }
}
