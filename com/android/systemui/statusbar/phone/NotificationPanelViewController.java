// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.content.res.Configuration;
import android.view.WindowInsets;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import com.android.systemui.R$integer;
import android.os.SystemClock;
import android.graphics.PointF;
import com.android.systemui.statusbar.notification.ViewGroupFadeHelper;
import com.android.systemui.DejankUtils;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.GestureRecorder;
import android.view.ViewPropertyAnimator;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.R$dimen;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import java.util.Collections;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import java.util.Objects;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.keyguard.KeyguardClockSwitch;
import java.util.List;
import android.app.ActivityManager$RunningTaskInfo;
import android.util.Log;
import com.android.systemui.R$string;
import android.util.MathUtils;
import android.graphics.Region;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.MotionEvent;
import android.view.View$OnApplyWindowInsetsListener;
import android.view.View$OnAttachStateChangeListener;
import android.animation.TimeInterpolator;
import com.android.systemui.qs.QSFragment;
import android.view.View$OnLayoutChangeListener;
import android.view.View$OnClickListener;
import android.app.Fragment;
import android.util.Property;
import com.android.systemui.Interpolators;
import android.hardware.biometrics.BiometricSourceType;
import java.util.function.Function;
import java.util.function.BiConsumer;
import com.android.systemui.R$id;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.internal.util.LatencyTracker;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.ArrayList;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import com.android.systemui.plugins.qs.QS;
import com.android.systemui.statusbar.PulseExpansionHandler;
import android.os.PowerManager;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.KeyguardStatusView;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.ConversationNotificationManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.CommandQueue;
import android.animation.ValueAnimator;
import android.view.ViewGroup;
import java.util.function.Consumer;
import android.app.ActivityManager;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import android.graphics.Rect;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;

public class NotificationPanelViewController extends PanelViewController
{
    private static final AnimationProperties CLOCK_ANIMATION_PROPERTIES;
    private static final Rect EMPTY_RECT;
    private static final AnimationProperties KEYGUARD_HUN_PROPERTIES;
    private static final Rect M_DUMMY_DIRTY_RECT;
    private final AnimatableProperty KEYGUARD_HEADS_UP_SHOWING_AMOUNT;
    private final AccessibilityManager mAccessibilityManager;
    private final ActivityManager mActivityManager;
    private boolean mAffordanceHasPreview;
    private KeyguardAffordanceHelper mAffordanceHelper;
    private Consumer<Boolean> mAffordanceLaunchListener;
    private boolean mAllowExpandForSmallExpansion;
    private int mAmbientIndicationBottomPadding;
    private final Runnable mAnimateKeyguardBottomAreaInvisibleEndRunnable;
    private final Runnable mAnimateKeyguardStatusBarInvisibleEndRunnable;
    private final Runnable mAnimateKeyguardStatusViewGoneEndRunnable;
    private final Runnable mAnimateKeyguardStatusViewInvisibleEndRunnable;
    private final Runnable mAnimateKeyguardStatusViewVisibleEndRunnable;
    private boolean mAnimateNextPositionUpdate;
    private int mBarState;
    private ViewGroup mBigClockContainer;
    private boolean mBlockTouches;
    private boolean mBlockingExpansionForCurrentTouch;
    private float mBottomAreaShadeAlpha;
    private final ValueAnimator mBottomAreaShadeAlphaAnimator;
    private final KeyguardClockPositionAlgorithm mClockPositionAlgorithm;
    private final KeyguardClockPositionAlgorithm.Result mClockPositionResult;
    private boolean mClosingWithAlphaFadeOut;
    private boolean mCollapsedOnDown;
    private final CommandQueue mCommandQueue;
    private final ConfigurationController mConfigurationController;
    private final ConfigurationListener mConfigurationListener;
    private boolean mConflictingQsExpansionGesture;
    private final ConversationNotificationManager mConversationNotificationManager;
    private int mDarkIconSize;
    private boolean mDelayShowingKeyguardStatusBar;
    private int mDisplayId;
    private float mDownX;
    private float mDownY;
    private final DozeParameters mDozeParameters;
    private boolean mDozing;
    private boolean mDozingOnDown;
    private float mEmptyDragAmount;
    private final NotificationEntryManager mEntryManager;
    private Runnable mExpandAfterLayoutRunnable;
    private float mExpandOffset;
    private boolean mExpandingFromHeadsUp;
    private final ExpansionCallback mExpansionCallback;
    private boolean mExpectingSynthesizedDown;
    private FalsingManager mFalsingManager;
    private boolean mFirstBypassAttempt;
    private FlingAnimationUtils mFlingAnimationUtils;
    private final FlingAnimationUtils.Builder mFlingAnimationUtilsBuilder;
    private final FragmentHostManager.FragmentListener mFragmentListener;
    private NotificationGroupManager mGroupManager;
    private boolean mHeadsUpAnimatingAway;
    private HeadsUpAppearanceController mHeadsUpAppearanceController;
    private Runnable mHeadsUpExistenceChangedRunnable;
    private int mHeadsUpInset;
    private boolean mHeadsUpPinnedMode;
    private HeadsUpTouchHelper mHeadsUpTouchHelper;
    private final HeightListener mHeightListener;
    private boolean mHideIconsDuringNotificationLaunch;
    private int mIndicationBottomPadding;
    private float mInitialHeightOnTouch;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private final InjectionInflationController mInjectionInflationController;
    private float mInterpolatedDarkAmount;
    private boolean mIsExpanding;
    private boolean mIsFullWidth;
    private boolean mIsLaunchTransitionFinished;
    private boolean mIsLaunchTransitionRunning;
    private final KeyguardAffordanceHelperCallback mKeyguardAffordanceHelperCallback;
    private final KeyguardBypassController mKeyguardBypassController;
    private float mKeyguardHeadsUpShowingAmount;
    private KeyguardIndicationController mKeyguardIndicationController;
    private boolean mKeyguardShowing;
    private KeyguardStatusBarView mKeyguardStatusBar;
    private float mKeyguardStatusBarAnimateAlpha;
    private KeyguardStatusView mKeyguardStatusView;
    private boolean mKeyguardStatusViewAnimating;
    @VisibleForTesting
    final KeyguardUpdateMonitorCallback mKeyguardUpdateCallback;
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    private String mLastCameraLaunchSource;
    private boolean mLastEventSynthesizedDown;
    private int mLastOrientation;
    private float mLastOverscroll;
    private Runnable mLaunchAnimationEndRunnable;
    private boolean mLaunchingAffordance;
    private float mLinearDarkAmount;
    private boolean mListenForHeadsUp;
    private LockscreenGestureLogger mLockscreenGestureLogger;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final MetricsLogger mMetricsLogger;
    private int mNavigationBarBottomHeight;
    private NotificationsQuickSettingsContainer mNotificationContainerParent;
    private NotificationStackScrollLayout mNotificationStackScroller;
    private int mNotificationsHeaderCollideDistance;
    private int mOldLayoutDirection;
    private final OnClickListener mOnClickListener;
    private final OnEmptySpaceClickListener mOnEmptySpaceClickListener;
    private final MyOnHeadsUpChangedListener mOnHeadsUpChangedListener;
    private final OnHeightChangedListener mOnHeightChangedListener;
    private final OnOverscrollTopChangedListener mOnOverscrollTopChangedListener;
    private Runnable mOnReinflationListener;
    private boolean mOnlyAffordanceInThisMotion;
    private int mPanelAlpha;
    private final AnimatableProperty mPanelAlphaAnimator;
    private Runnable mPanelAlphaEndAction;
    private final AnimationProperties mPanelAlphaInPropertiesAnimator;
    private final AnimationProperties mPanelAlphaOutPropertiesAnimator;
    private boolean mPanelExpanded;
    private int mPositionMinSideMargin;
    private final PowerManager mPowerManager;
    private final PulseExpansionHandler mPulseExpansionHandler;
    private boolean mPulsing;
    private QS mQs;
    private boolean mQsAnimatorExpand;
    private boolean mQsExpandImmediate;
    private boolean mQsExpanded;
    private boolean mQsExpandedWhenExpandingStarted;
    private ValueAnimator mQsExpansionAnimator;
    private boolean mQsExpansionEnabled;
    private boolean mQsExpansionFromOverscroll;
    private float mQsExpansionHeight;
    private int mQsFalsingThreshold;
    private FrameLayout mQsFrame;
    private boolean mQsFullyExpanded;
    private int mQsMaxExpansionHeight;
    private int mQsMinExpansionHeight;
    private View mQsNavbarScrim;
    private int mQsNotificationTopPadding;
    private int mQsPeekHeight;
    private boolean mQsScrimEnabled;
    private ValueAnimator mQsSizeChangeAnimator;
    private boolean mQsTouchAboveFalsingThreshold;
    private boolean mQsTracking;
    private VelocityTracker mQsVelocityTracker;
    private final ShadeController mShadeController;
    private int mShelfHeight;
    private boolean mShowEmptyShadeView;
    private boolean mShowIconsWhenExpanded;
    private boolean mShowingKeyguardHeadsUp;
    private int mStackScrollerMeasuringPass;
    private boolean mStackScrollerOverscrolling;
    private final ValueAnimator$AnimatorUpdateListener mStatusBarAnimateAlphaListener;
    private int mStatusBarMinHeight;
    private final StatusBarStateListener mStatusBarStateListener;
    private int mThemeResId;
    private ArrayList<Consumer<ExpandableNotificationRow>> mTrackingHeadsUpListeners;
    private int mTrackingPointer;
    private boolean mTwoFingerQsExpandPossible;
    private final KeyguardUpdateMonitor mUpdateMonitor;
    private boolean mUserSetupComplete;
    private ArrayList<Runnable> mVerticalTranslationListener;
    private final NotificationPanelView mView;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;
    private final ZenModeController mZenModeController;
    private final ZenModeControllerCallback mZenModeControllerCallback;
    
    static {
        M_DUMMY_DIRTY_RECT = new Rect(0, 0, 1, 1);
        EMPTY_RECT = new Rect();
        final AnimationProperties clock_ANIMATION_PROPERTIES = new AnimationProperties();
        clock_ANIMATION_PROPERTIES.setDuration(360L);
        CLOCK_ANIMATION_PROPERTIES = clock_ANIMATION_PROPERTIES;
        final AnimationProperties keyguard_HUN_PROPERTIES = new AnimationProperties();
        keyguard_HUN_PROPERTIES.setDuration(360L);
        KEYGUARD_HUN_PROPERTIES = keyguard_HUN_PROPERTIES;
    }
    
    public NotificationPanelViewController(final NotificationPanelView mView, final InjectionInflationController mInjectionInflationController, final NotificationWakeUpCoordinator mWakeUpCoordinator, final PulseExpansionHandler mPulseExpansionHandler, final DynamicPrivacyController dynamicPrivacyController, final KeyguardBypassController mKeyguardBypassController, final FalsingManager mFalsingManager, final ShadeController mShadeController, final NotificationLockscreenUserManager mLockscreenUserManager, final NotificationEntryManager mEntryManager, final KeyguardStateController keyguardStateController, final StatusBarStateController statusBarStateController, final DozeLog dozeLog, final DozeParameters mDozeParameters, final CommandQueue mCommandQueue, final VibratorHelper vibratorHelper, final LatencyTracker latencyTracker, final PowerManager mPowerManager, final AccessibilityManager mAccessibilityManager, final int mDisplayId, final KeyguardUpdateMonitor mUpdateMonitor, final MetricsLogger mMetricsLogger, final ActivityManager mActivityManager, final ZenModeController mZenModeController, final ConfigurationController mConfigurationController, final FlingAnimationUtils.Builder mFlingAnimationUtilsBuilder, final StatusBarTouchableRegionManager statusBarTouchableRegionManager, final ConversationNotificationManager mConversationNotificationManager) {
        super(mView, mFalsingManager, dozeLog, keyguardStateController, (SysuiStatusBarStateController)statusBarStateController, vibratorHelper, latencyTracker, mFlingAnimationUtilsBuilder, statusBarTouchableRegionManager);
        this.mOnHeightChangedListener = new OnHeightChangedListener();
        this.mOnClickListener = new OnClickListener();
        this.mOnOverscrollTopChangedListener = new OnOverscrollTopChangedListener();
        this.mKeyguardAffordanceHelperCallback = new KeyguardAffordanceHelperCallback();
        this.mOnEmptySpaceClickListener = new OnEmptySpaceClickListener();
        this.mOnHeadsUpChangedListener = new MyOnHeadsUpChangedListener();
        this.mHeightListener = new HeightListener();
        this.mZenModeControllerCallback = new ZenModeControllerCallback();
        this.mConfigurationListener = new ConfigurationListener();
        this.mStatusBarStateListener = new StatusBarStateListener();
        this.mExpansionCallback = new ExpansionCallback();
        this.KEYGUARD_HEADS_UP_SHOWING_AMOUNT = AnimatableProperty.from("KEYGUARD_HEADS_UP_SHOWING_AMOUNT", new _$$Lambda$NotificationPanelViewController$OZSOwGan_FM_WhbUvj9snkcbnX8(this), (Function<View, Float>)new _$$Lambda$NotificationPanelViewController$zLH5b8RJBtRSCf103Z1grf2LCf0(this), R$id.keyguard_hun_animator_tag, R$id.keyguard_hun_animator_end_tag, R$id.keyguard_hun_animator_start_tag);
        this.mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onBiometricAuthenticated(final int n, final BiometricSourceType biometricSourceType, final boolean b) {
                if (NotificationPanelViewController.this.mFirstBypassAttempt && NotificationPanelViewController.this.mUpdateMonitor.isUnlockingWithBiometricAllowed(b)) {
                    NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar = true;
                }
            }
            
            @Override
            public void onBiometricRunningStateChanged(final boolean b, final BiometricSourceType biometricSourceType) {
                final int access$1400 = NotificationPanelViewController.this.mBarState;
                int n = 1;
                if (access$1400 != 1) {
                    if (NotificationPanelViewController.this.mBarState == 2) {
                        n = n;
                    }
                    else {
                        n = 0;
                    }
                }
                if (!b && NotificationPanelViewController.this.mFirstBypassAttempt && n != 0 && !NotificationPanelViewController.this.mDozing && !NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar) {
                    NotificationPanelViewController.this.mFirstBypassAttempt = false;
                    NotificationPanelViewController.this.animateKeyguardStatusBarIn(360L);
                }
            }
            
            @Override
            public void onFinishedGoingToSleep(final int n) {
                final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
                this$0.mFirstBypassAttempt = this$0.mKeyguardBypassController.getBypassEnabled();
                NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar = false;
            }
        };
        this.mQsExpansionEnabled = true;
        this.mClockPositionAlgorithm = new KeyguardClockPositionAlgorithm();
        this.mClockPositionResult = new KeyguardClockPositionAlgorithm.Result();
        this.mQsScrimEnabled = true;
        this.mKeyguardStatusBarAnimateAlpha = 1.0f;
        this.mLastOrientation = -1;
        this.mLastCameraLaunchSource = "lockscreen_affordance";
        this.mHeadsUpExistenceChangedRunnable = new _$$Lambda$NotificationPanelViewController$rvgAK3TYgwqivfcZ2YHWM7JuvzA(this);
        this.mLockscreenGestureLogger = new LockscreenGestureLogger();
        this.mHideIconsDuringNotificationLaunch = true;
        this.mTrackingHeadsUpListeners = new ArrayList<Consumer<ExpandableNotificationRow>>();
        this.mVerticalTranslationListener = new ArrayList<Runnable>();
        this.mPanelAlphaAnimator = AnimatableProperty.from("panelAlpha", (BiConsumer<View, Float>)_$$Lambda$aKsp0zdf_wKFZXD1TonJ2cFEsN4.INSTANCE, (Function<View, Float>)_$$Lambda$SmdYpsZqQm1fpR9OgK3SiEL3pJQ.INSTANCE, R$id.panel_alpha_animator_tag, R$id.panel_alpha_animator_start_tag, R$id.panel_alpha_animator_end_tag);
        final AnimationProperties mPanelAlphaOutPropertiesAnimator = new AnimationProperties();
        mPanelAlphaOutPropertiesAnimator.setDuration(150L);
        mPanelAlphaOutPropertiesAnimator.setCustomInterpolator(this.mPanelAlphaAnimator.getProperty(), Interpolators.ALPHA_OUT);
        this.mPanelAlphaOutPropertiesAnimator = mPanelAlphaOutPropertiesAnimator;
        final AnimationProperties mPanelAlphaInPropertiesAnimator = new AnimationProperties();
        mPanelAlphaInPropertiesAnimator.setDuration(200L);
        mPanelAlphaInPropertiesAnimator.setAnimationEndAction(new _$$Lambda$NotificationPanelViewController$HnNg1uN3kkP2Byw0u02uaOtm_nk(this));
        mPanelAlphaInPropertiesAnimator.setCustomInterpolator(this.mPanelAlphaAnimator.getProperty(), Interpolators.ALPHA_IN);
        this.mPanelAlphaInPropertiesAnimator = mPanelAlphaInPropertiesAnimator;
        this.mKeyguardHeadsUpShowingAmount = 0.0f;
        this.mAnimateKeyguardStatusViewInvisibleEndRunnable = new Runnable() {
            @Override
            public void run() {
                NotificationPanelViewController.this.mKeyguardStatusViewAnimating = false;
                NotificationPanelViewController.this.mKeyguardStatusView.setVisibility(4);
            }
        };
        this.mAnimateKeyguardStatusViewGoneEndRunnable = new Runnable() {
            @Override
            public void run() {
                NotificationPanelViewController.this.mKeyguardStatusViewAnimating = false;
                NotificationPanelViewController.this.mKeyguardStatusView.setVisibility(8);
            }
        };
        this.mAnimateKeyguardStatusViewVisibleEndRunnable = new Runnable() {
            @Override
            public void run() {
                NotificationPanelViewController.this.mKeyguardStatusViewAnimating = false;
            }
        };
        this.mAnimateKeyguardStatusBarInvisibleEndRunnable = new Runnable() {
            @Override
            public void run() {
                NotificationPanelViewController.this.mKeyguardStatusBar.setVisibility(4);
                NotificationPanelViewController.this.mKeyguardStatusBar.setAlpha(1.0f);
                NotificationPanelViewController.this.mKeyguardStatusBarAnimateAlpha = 1.0f;
            }
        };
        this.mStatusBarAnimateAlphaListener = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                NotificationPanelViewController.this.mKeyguardStatusBarAnimateAlpha = (float)valueAnimator.getAnimatedValue();
                NotificationPanelViewController.this.updateHeaderKeyguardAlpha();
            }
        };
        this.mAnimateKeyguardBottomAreaInvisibleEndRunnable = new Runnable() {
            @Override
            public void run() {
                NotificationPanelViewController.this.mKeyguardBottomArea.setVisibility(8);
            }
        };
        this.mFragmentListener = new FragmentHostManager.FragmentListener() {
            @Override
            public void onFragmentViewCreated(final String s, final Fragment fragment) {
                NotificationPanelViewController.this.mQs = (QS)fragment;
                NotificationPanelViewController.this.mQs.setPanelView((QS.HeightListener)NotificationPanelViewController.this.mHeightListener);
                NotificationPanelViewController.this.mQs.setExpandClickListener((View$OnClickListener)NotificationPanelViewController.this.mOnClickListener);
                NotificationPanelViewController.this.mQs.setHeaderClickable(NotificationPanelViewController.this.mQsExpansionEnabled);
                NotificationPanelViewController.this.updateQSPulseExpansion();
                NotificationPanelViewController.this.mQs.setOverscrolling(NotificationPanelViewController.this.mStackScrollerOverscrolling);
                NotificationPanelViewController.this.mQs.getView().addOnLayoutChangeListener((View$OnLayoutChangeListener)new _$$Lambda$NotificationPanelViewController$16$XnoYXKkmoW231iwbj2Y_gqOunzQ(this));
                NotificationPanelViewController.this.mNotificationStackScroller.setQsContainer((ViewGroup)NotificationPanelViewController.this.mQs.getView());
                if (NotificationPanelViewController.this.mQs instanceof QSFragment) {
                    NotificationPanelViewController.this.mKeyguardStatusBar.setQSPanel(((QSFragment)NotificationPanelViewController.this.mQs).getQsPanel());
                }
                NotificationPanelViewController.this.updateQsExpansion();
            }
            
            @Override
            public void onFragmentViewDestroyed(final String s, final Fragment fragment) {
                if (fragment == NotificationPanelViewController.this.mQs) {
                    NotificationPanelViewController.this.mQs = null;
                }
            }
        };
        this.mView = mView;
        this.mMetricsLogger = mMetricsLogger;
        this.mActivityManager = mActivityManager;
        this.mZenModeController = mZenModeController;
        this.mConfigurationController = mConfigurationController;
        this.mFlingAnimationUtilsBuilder = mFlingAnimationUtilsBuilder;
        mView.setWillNotDraw(true);
        this.mInjectionInflationController = mInjectionInflationController;
        this.mFalsingManager = mFalsingManager;
        this.mPowerManager = mPowerManager;
        this.mWakeUpCoordinator = mWakeUpCoordinator;
        this.mAccessibilityManager = mAccessibilityManager;
        this.mView.setAccessibilityPaneTitle((CharSequence)this.determineAccessibilityPaneTitle());
        this.setPanelAlpha(255, false);
        this.mCommandQueue = mCommandQueue;
        this.mDisplayId = mDisplayId;
        this.mPulseExpansionHandler = mPulseExpansionHandler;
        this.mDozeParameters = mDozeParameters;
        mPulseExpansionHandler.setPulseExpandAbortListener(new _$$Lambda$NotificationPanelViewController$iN7P4plRLlpAkFmRApdB8IRWjNM(this));
        this.mThemeResId = this.mView.getContext().getThemeResId();
        this.mKeyguardBypassController = mKeyguardBypassController;
        this.mUpdateMonitor = mUpdateMonitor;
        this.mFirstBypassAttempt = mKeyguardBypassController.getBypassEnabled();
        super.mKeyguardStateController.addCallback((KeyguardStateController.Callback)new KeyguardStateController.Callback() {
            @Override
            public void onKeyguardFadingAwayChanged() {
                if (!NotificationPanelViewController.this.mKeyguardStateController.isKeyguardFadingAway()) {
                    NotificationPanelViewController.this.mFirstBypassAttempt = false;
                    NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar = false;
                }
            }
        });
        dynamicPrivacyController.addListener((DynamicPrivacyController.Listener)new DynamicPrivacyControlListener());
        (this.mBottomAreaShadeAlphaAnimator = ValueAnimator.ofFloat(new float[] { 1.0f, 0.0f })).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$NotificationPanelViewController$LinMYB2Oj2N48himJfZkXl5Ac08(this));
        this.mBottomAreaShadeAlphaAnimator.setDuration(160L);
        this.mBottomAreaShadeAlphaAnimator.setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT);
        this.mShadeController = mShadeController;
        this.mLockscreenUserManager = mLockscreenUserManager;
        this.mEntryManager = mEntryManager;
        this.mConversationNotificationManager = mConversationNotificationManager;
        this.mView.setBackgroundColor(0);
        final OnAttachStateChangeListener onAttachStateChangeListener = new OnAttachStateChangeListener();
        this.mView.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)onAttachStateChangeListener);
        if (this.mView.isAttachedToWindow()) {
            onAttachStateChangeListener.onViewAttachedToWindow((View)this.mView);
        }
        this.mView.setOnApplyWindowInsetsListener((View$OnApplyWindowInsetsListener)new OnApplyWindowInsetsListener());
        this.onFinishInflate();
    }
    
    private void animateKeyguardStatusBarIn(final long duration) {
        this.mKeyguardStatusBar.setVisibility(0);
        this.mKeyguardStatusBar.setAlpha(0.0f);
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        ofFloat.addUpdateListener(this.mStatusBarAnimateAlphaListener);
        ofFloat.setDuration(duration);
        ofFloat.setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
        ofFloat.start();
    }
    
    private void animateKeyguardStatusBarOut() {
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.mKeyguardStatusBar.getAlpha(), 0.0f });
        ofFloat.addUpdateListener(this.mStatusBarAnimateAlphaListener);
        long keyguardFadingAwayDelay;
        if (super.mKeyguardStateController.isKeyguardFadingAway()) {
            keyguardFadingAwayDelay = super.mKeyguardStateController.getKeyguardFadingAwayDelay();
        }
        else {
            keyguardFadingAwayDelay = 0L;
        }
        ofFloat.setStartDelay(keyguardFadingAwayDelay);
        long shortenedFadingAwayDuration;
        if (super.mKeyguardStateController.isKeyguardFadingAway()) {
            shortenedFadingAwayDuration = super.mKeyguardStateController.getShortenedFadingAwayDuration();
        }
        else {
            shortenedFadingAwayDuration = 360L;
        }
        ofFloat.setDuration(shortenedFadingAwayDuration);
        ofFloat.setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
        ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                NotificationPanelViewController.this.mAnimateKeyguardStatusBarInvisibleEndRunnable.run();
            }
        });
        ofFloat.start();
    }
    
    private Rect calculateGestureExclusionRect() {
        final Region calculateTouchableRegion = super.mStatusBarTouchableRegionManager.calculateTouchableRegion();
        Rect rect;
        if (this.isFullyCollapsed() && calculateTouchableRegion != null) {
            rect = calculateTouchableRegion.getBounds();
        }
        else {
            rect = null;
        }
        if (rect == null) {
            rect = NotificationPanelViewController.EMPTY_RECT;
        }
        return rect;
    }
    
    private int calculatePanelHeightQsExpanded() {
        float n = (float)(this.mNotificationStackScroller.getHeight() - this.mNotificationStackScroller.getEmptyBottomMargin() - this.mNotificationStackScroller.getTopPadding());
        if (this.mNotificationStackScroller.getNotGoneChildCount() == 0) {
            n = n;
            if (this.mShowEmptyShadeView) {
                n = (float)this.mNotificationStackScroller.getEmptyShadeViewHeight();
            }
        }
        int a;
        final int n2 = a = this.mQsMaxExpansionHeight;
        if (this.mKeyguardShowing) {
            a = n2 + this.mQsNotificationTopPadding;
        }
        final ValueAnimator mQsSizeChangeAnimator = this.mQsSizeChangeAnimator;
        if (mQsSizeChangeAnimator != null) {
            a = (int)mQsSizeChangeAnimator.getAnimatedValue();
        }
        int stackScrollerPadding;
        if (this.mBarState == 1) {
            stackScrollerPadding = this.mClockPositionResult.stackScrollerPadding;
        }
        else {
            stackScrollerPadding = 0;
        }
        float max;
        if ((max = Math.max(a, stackScrollerPadding) + n + this.mNotificationStackScroller.getTopPaddingOverflow()) > this.mNotificationStackScroller.getHeight()) {
            max = Math.max((float)(a + this.mNotificationStackScroller.getLayoutMinHeight()), (float)this.mNotificationStackScroller.getHeight());
        }
        return (int)max;
    }
    
    private int calculatePanelHeightShade() {
        final int a = (int)(this.mNotificationStackScroller.getHeight() - this.mNotificationStackScroller.getEmptyBottomMargin() + this.mNotificationStackScroller.getTopPaddingOverflow());
        if (this.mBarState == 1) {
            return Math.max(a, this.mClockPositionAlgorithm.getExpandedClockPosition() + this.mKeyguardStatusView.getHeight() + this.mNotificationStackScroller.getIntrinsicContentHeight());
        }
        return a;
    }
    
    private float calculateQsTopPadding() {
        if (this.mKeyguardShowing && (this.mQsExpandImmediate || (this.mIsExpanding && this.mQsExpandedWhenExpandingStarted))) {
            final int keyguardNotificationStaticPadding = this.getKeyguardNotificationStaticPadding();
            int max = this.mQsMaxExpansionHeight + this.mQsNotificationTopPadding;
            if (this.mBarState == 1) {
                max = Math.max(keyguardNotificationStaticPadding, max);
            }
            return (float)(int)MathUtils.lerp((float)this.mQsMinExpansionHeight, (float)max, this.getExpandedFraction());
        }
        final ValueAnimator mQsSizeChangeAnimator = this.mQsSizeChangeAnimator;
        if (mQsSizeChangeAnimator != null) {
            return (float)Math.max((int)mQsSizeChangeAnimator.getAnimatedValue(), this.getKeyguardNotificationStaticPadding());
        }
        if (this.mKeyguardShowing) {
            return MathUtils.lerp((float)this.getKeyguardNotificationStaticPadding(), (float)(this.mQsMaxExpansionHeight + this.mQsNotificationTopPadding), this.getQsExpansionFraction());
        }
        return this.mQsExpansionHeight + this.mQsNotificationTopPadding;
    }
    
    private void cancelQsAnimation() {
        final ValueAnimator mQsExpansionAnimator = this.mQsExpansionAnimator;
        if (mQsExpansionAnimator != null) {
            mQsExpansionAnimator.cancel();
        }
    }
    
    private String determineAccessibilityPaneTitle() {
        final QS mQs = this.mQs;
        if (mQs != null && mQs.isCustomizing()) {
            return super.mResources.getString(R$string.accessibility_desc_quick_settings_edit);
        }
        if (this.mQsExpansionHeight != 0.0f && this.mQsFullyExpanded) {
            return super.mResources.getString(R$string.accessibility_desc_quick_settings);
        }
        if (this.mBarState == 1) {
            return super.mResources.getString(R$string.accessibility_desc_lock_screen);
        }
        return super.mResources.getString(R$string.accessibility_desc_notification_shade);
    }
    
    private boolean flingExpandsQs(final float a) {
        final boolean unlockingDisabled = this.mFalsingManager.isUnlockingDisabled();
        final boolean b = false;
        final boolean b2 = false;
        boolean b3 = b;
        if (!unlockingDisabled) {
            if (this.isFalseTouch()) {
                b3 = b;
            }
            else {
                if (Math.abs(a) < this.mFlingAnimationUtils.getMinVelocityPxPerSecond()) {
                    boolean b4 = b2;
                    if (this.getQsExpansionFraction() > 0.5f) {
                        b4 = true;
                    }
                    return b4;
                }
                b3 = b;
                if (a > 0.0f) {
                    b3 = true;
                }
            }
        }
        return b3;
    }
    
    private void flingQsWithCurrentVelocity(final float n, final boolean b) {
        final float currentQSVelocity = this.getCurrentQSVelocity();
        final boolean flingExpandsQs = this.flingExpandsQs(currentQSVelocity);
        if (flingExpandsQs) {
            this.logQsSwipeDown(n);
        }
        int n2;
        if (flingExpandsQs && !b) {
            n2 = 0;
        }
        else {
            n2 = 1;
        }
        this.flingSettings(currentQSVelocity, n2);
    }
    
    private float getCurrentQSVelocity() {
        final VelocityTracker mQsVelocityTracker = this.mQsVelocityTracker;
        if (mQsVelocityTracker == null) {
            return 0.0f;
        }
        mQsVelocityTracker.computeCurrentVelocity(1000);
        return this.mQsVelocityTracker.getYVelocity();
    }
    
    private float getFadeoutAlpha() {
        if (this.mQsMinExpansionHeight == 0) {
            return 1.0f;
        }
        return (float)Math.pow(Math.max(0.0f, Math.min(this.getExpandedHeight() / this.mQsMinExpansionHeight, 1.0f)), 0.75);
    }
    
    private int getFalsingThreshold() {
        float n;
        if (super.mStatusBar.isWakeUpComingFromTouch()) {
            n = 1.5f;
        }
        else {
            n = 1.0f;
        }
        return (int)(this.mQsFalsingThreshold * n);
    }
    
    private float getKeyguardContentsAlpha() {
        float n;
        float n2;
        if (this.mBarState == 1) {
            n = this.getExpandedHeight();
            n2 = (float)(this.mKeyguardStatusBar.getHeight() + this.mNotificationsHeaderCollideDistance);
        }
        else {
            n = this.getExpandedHeight();
            n2 = (float)this.mKeyguardStatusBar.getHeight();
        }
        return (float)Math.pow(MathUtils.saturate(n / n2), 0.75);
    }
    
    private float getKeyguardHeadsUpShowingAmount() {
        return this.mKeyguardHeadsUpShowingAmount;
    }
    
    private int getKeyguardNotificationStaticPadding() {
        if (!this.mKeyguardShowing) {
            return 0;
        }
        if (!this.mKeyguardBypassController.getBypassEnabled()) {
            return this.mClockPositionResult.stackScrollerPadding;
        }
        final int mHeadsUpInset = this.mHeadsUpInset;
        if (!this.mNotificationStackScroller.isPulseExpanding()) {
            return mHeadsUpInset;
        }
        return (int)MathUtils.lerp((float)mHeadsUpInset, (float)this.mClockPositionResult.stackScrollerPadding, this.mNotificationStackScroller.calculateAppearFractionBypass());
    }
    
    private int getMaxPanelHeightBypass() {
        int n = this.mClockPositionAlgorithm.getExpandedClockPosition() + this.mKeyguardStatusView.getHeight();
        if (this.mNotificationStackScroller.getVisibleNotificationCount() != 0) {
            n += (int)(this.mShelfHeight / 2.0f + this.mDarkIconSize / 2.0f);
        }
        return n;
    }
    
    private int getMaxPanelHeightNonBypass() {
        int a2;
        final int a = a2 = this.mStatusBarMinHeight;
        if (this.mBarState != 1) {
            a2 = a;
            if (this.mNotificationStackScroller.getNotGoneChildCount() == 0) {
                a2 = Math.max(a, (int)(this.mQsMinExpansionHeight + this.getOverExpansionAmount()));
            }
        }
        int b;
        if (!this.mQsExpandImmediate && !this.mQsExpanded && (!this.mIsExpanding || !this.mQsExpandedWhenExpandingStarted) && !this.mPulsing) {
            b = this.calculatePanelHeightShade();
        }
        else {
            b = this.calculatePanelHeightQsExpanded();
        }
        final int max = Math.max(a2, b);
        if (max == 0) {
            final String tag = PanelViewController.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("maxPanelHeight is 0. getOverExpansionAmount(): ");
            sb.append(this.getOverExpansionAmount());
            sb.append(", calculatePanelHeightQsExpanded: ");
            sb.append(this.calculatePanelHeightQsExpanded());
            sb.append(", calculatePanelHeightShade: ");
            sb.append(this.calculatePanelHeightShade());
            sb.append(", mStatusBarMinHeight = ");
            sb.append(this.mStatusBarMinHeight);
            sb.append(", mQsMinExpansionHeight = ");
            sb.append(this.mQsMinExpansionHeight);
            Log.wtf(tag, sb.toString());
        }
        return max;
    }
    
    private float getQsExpansionFraction() {
        final float mQsExpansionHeight = this.mQsExpansionHeight;
        final int mQsMinExpansionHeight = this.mQsMinExpansionHeight;
        return Math.min(1.0f, (mQsExpansionHeight - mQsMinExpansionHeight) / (this.mQsMaxExpansionHeight - mQsMinExpansionHeight));
    }
    
    private int getUnlockedStackScrollerPadding() {
        final QS mQs = this.mQs;
        int height;
        if (mQs != null) {
            height = mQs.getHeader().getHeight();
        }
        else {
            height = 0;
        }
        return height + this.mQsPeekHeight + this.mQsNotificationTopPadding;
    }
    
    private void handleQsDown(final MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 && this.shouldQuickSettingsIntercept(motionEvent.getX(), motionEvent.getY(), -1.0f)) {
            this.mFalsingManager.onQsDown();
            this.mQsTracking = true;
            this.onQsExpansionStarted();
            this.mInitialHeightOnTouch = this.mQsExpansionHeight;
            this.mInitialTouchY = motionEvent.getX();
            this.mInitialTouchX = motionEvent.getY();
            this.notifyExpandingFinished();
        }
    }
    
    private boolean handleQsTouch(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0 && this.getExpandedFraction() == 1.0f && this.mBarState != 1 && !this.mQsExpanded && this.mQsExpansionEnabled) {
            this.mQsTracking = true;
            this.mConflictingQsExpansionGesture = true;
            this.onQsExpansionStarted();
            this.mInitialHeightOnTouch = this.mQsExpansionHeight;
            this.mInitialTouchY = motionEvent.getX();
            this.mInitialTouchX = motionEvent.getY();
        }
        if (!this.isFullyCollapsed()) {
            this.handleQsDown(motionEvent);
        }
        if (!this.mQsExpandImmediate && this.mQsTracking) {
            this.onQsTouch(motionEvent);
            if (!this.mConflictingQsExpansionGesture) {
                return true;
            }
        }
        if (actionMasked == 3 || actionMasked == 1) {
            this.mConflictingQsExpansionGesture = false;
        }
        if (actionMasked == 0 && this.isFullyCollapsed() && this.mQsExpansionEnabled) {
            this.mTwoFingerQsExpandPossible = true;
        }
        if (this.mTwoFingerQsExpandPossible && this.isOpenQsEvent(motionEvent) && motionEvent.getY(motionEvent.getActionIndex()) < this.mStatusBarMinHeight) {
            this.mMetricsLogger.count("panel_open_qs", 1);
            this.mQsExpandImmediate = true;
            this.mNotificationStackScroller.setShouldShowShelfOnly(true);
            this.requestPanelHeightUpdate();
            this.setListening(true);
        }
        return false;
    }
    
    private void initBottomArea() {
        final KeyguardAffordanceHelper keyguardAffordanceHelper = new KeyguardAffordanceHelper((KeyguardAffordanceHelper.Callback)this.mKeyguardAffordanceHelperCallback, this.mView.getContext(), this.mFalsingManager);
        this.mAffordanceHelper = keyguardAffordanceHelper;
        super.mKeyguardBottomArea.setAffordanceHelper(keyguardAffordanceHelper);
        super.mKeyguardBottomArea.setStatusBar(super.mStatusBar);
        super.mKeyguardBottomArea.setUserSetupComplete(this.mUserSetupComplete);
    }
    
    private void initDownStates(final MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.mOnlyAffordanceInThisMotion = false;
            this.mQsTouchAboveFalsingThreshold = this.mQsFullyExpanded;
            this.mDozingOnDown = this.isDozing();
            this.mDownX = motionEvent.getX();
            this.mDownY = motionEvent.getY();
            final boolean fullyCollapsed = this.isFullyCollapsed();
            this.mCollapsedOnDown = fullyCollapsed;
            this.mListenForHeadsUp = (fullyCollapsed && super.mHeadsUpManager.hasPinnedHeadsUp());
            final boolean mExpectingSynthesizedDown = this.mExpectingSynthesizedDown;
            this.mAllowExpandForSmallExpansion = mExpectingSynthesizedDown;
            super.mTouchSlopExceededBeforeDown = mExpectingSynthesizedDown;
            if (mExpectingSynthesizedDown) {
                this.mLastEventSynthesizedDown = true;
            }
            else {
                this.mLastEventSynthesizedDown = false;
            }
        }
        else {
            this.mLastEventSynthesizedDown = false;
        }
    }
    
    private void initVelocityTracker() {
        final VelocityTracker mQsVelocityTracker = this.mQsVelocityTracker;
        if (mQsVelocityTracker != null) {
            mQsVelocityTracker.recycle();
        }
        this.mQsVelocityTracker = VelocityTracker.obtain();
    }
    
    private boolean isFalseTouch() {
        if (!this.mKeyguardAffordanceHelperCallback.needsAntiFalsing()) {
            return false;
        }
        if (this.mFalsingManager.isClassifierEnabled()) {
            return this.mFalsingManager.isFalseTouch();
        }
        return this.mQsTouchAboveFalsingThreshold ^ true;
    }
    
    private boolean isForegroundApp(final String s) {
        final ActivityManager mActivityManager = this.mActivityManager;
        boolean b = true;
        final List runningTasks = mActivityManager.getRunningTasks(1);
        if (runningTasks.isEmpty() || !s.equals(runningTasks.get(0).topActivity.getPackageName())) {
            b = false;
        }
        return b;
    }
    
    private boolean isInQsArea(final float n, final float n2) {
        return n >= this.mQsFrame.getX() && n <= this.mQsFrame.getX() + this.mQsFrame.getWidth() && (n2 <= this.mNotificationStackScroller.getBottomMostNotificationBottom() || n2 <= this.mQs.getView().getY() + this.mQs.getView().getHeight());
    }
    
    private boolean isOnKeyguard() {
        final int mBarState = this.mBarState;
        boolean b = true;
        if (mBarState != 1) {
            b = false;
        }
        return b;
    }
    
    private boolean isOpenQsEvent(final MotionEvent motionEvent) {
        final int pointerCount = motionEvent.getPointerCount();
        final int actionMasked = motionEvent.getActionMasked();
        final boolean b = true;
        final boolean b2 = actionMasked == 5 && pointerCount == 2;
        final boolean b3 = actionMasked == 0 && (motionEvent.isButtonPressed(32) || motionEvent.isButtonPressed(64));
        final boolean b4 = actionMasked == 0 && (motionEvent.isButtonPressed(2) || motionEvent.isButtonPressed(4));
        boolean b5 = b;
        if (!b2) {
            b5 = b;
            if (!b3) {
                b5 = (b4 && b);
            }
        }
        return b5;
    }
    
    private void logQsSwipeDown(final float n) {
        final float currentQSVelocity = this.getCurrentQSVelocity();
        int n2;
        if (this.mBarState == 1) {
            n2 = 193;
        }
        else {
            n2 = 194;
        }
        this.mLockscreenGestureLogger.write(n2, (int)((n - this.mInitialTouchY) / super.mStatusBar.getDisplayDensity()), (int)(currentQSVelocity / super.mStatusBar.getDisplayDensity()));
    }
    
    private void maybeAnimateBottomAreaAlpha() {
        this.mBottomAreaShadeAlphaAnimator.cancel();
        if (this.mBarState == 2) {
            this.mBottomAreaShadeAlphaAnimator.start();
        }
        else {
            this.mBottomAreaShadeAlpha = 1.0f;
        }
    }
    
    private void notifyListenersTrackingHeadsUp(final ExpandableNotificationRow expandableNotificationRow) {
        for (int i = 0; i < this.mTrackingHeadsUpListeners.size(); ++i) {
            this.mTrackingHeadsUpListeners.get(i).accept(expandableNotificationRow);
        }
    }
    
    private void onFinishInflate() {
        this.loadDimens();
        this.mKeyguardStatusBar = (KeyguardStatusBarView)this.mView.findViewById(R$id.keyguard_header);
        this.mKeyguardStatusView = (KeyguardStatusView)this.mView.findViewById(R$id.keyguard_status_view);
        ((KeyguardClockSwitch)this.mView.findViewById(R$id.keyguard_clock_container)).setBigClockContainer(this.mBigClockContainer = (ViewGroup)this.mView.findViewById(R$id.big_clock_container));
        this.mNotificationContainerParent = (NotificationsQuickSettingsContainer)this.mView.findViewById(R$id.notification_container_parent);
        (this.mNotificationStackScroller = (NotificationStackScrollLayout)this.mView.findViewById(R$id.notification_stack_scroller)).setOnHeightChangedListener(this.mOnHeightChangedListener);
        this.mNotificationStackScroller.setOverscrollTopChangedListener((NotificationStackScrollLayout.OnOverscrollTopChangedListener)this.mOnOverscrollTopChangedListener);
        this.mNotificationStackScroller.setOnEmptySpaceClickListener((NotificationStackScrollLayout.OnEmptySpaceClickListener)this.mOnEmptySpaceClickListener);
        final NotificationStackScrollLayout mNotificationStackScroller = this.mNotificationStackScroller;
        Objects.requireNonNull(mNotificationStackScroller);
        this.addTrackingHeadsUpListener(new _$$Lambda$hB_2bxao9PtuBwZm92el8Nt3UKY(mNotificationStackScroller));
        super.mKeyguardBottomArea = (KeyguardBottomAreaView)this.mView.findViewById(R$id.keyguard_bottom_area);
        this.mQsNavbarScrim = this.mView.findViewById(R$id.qs_navbar_scrim);
        this.mLastOrientation = super.mResources.getConfiguration().orientation;
        this.initBottomArea();
        this.mWakeUpCoordinator.setStackScroller(this.mNotificationStackScroller);
        this.mQsFrame = (FrameLayout)this.mView.findViewById(R$id.qs_frame);
        this.mPulseExpansionHandler.setUp(this.mNotificationStackScroller, (PulseExpansionHandler.ExpansionCallback)this.mExpansionCallback, this.mShadeController);
        this.mWakeUpCoordinator.addListener((NotificationWakeUpCoordinator.WakeUpListener)new NotificationWakeUpCoordinator.WakeUpListener() {
            @Override
            public void onFullyHiddenChanged(final boolean b) {
                NotificationPanelViewController.this.updateKeyguardStatusBarForHeadsUp();
            }
            
            @Override
            public void onPulseExpansionChanged(final boolean b) {
                if (NotificationPanelViewController.this.mKeyguardBypassController.getBypassEnabled()) {
                    NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                    NotificationPanelViewController.this.updateQSPulseExpansion();
                }
            }
        });
        this.mView.setRtlChangeListener((NotificationPanelView.RtlChangeListener)new _$$Lambda$NotificationPanelViewController$UjVwGXo83aLB3W0dUJndREhKfQk(this));
    }
    
    private void onQsExpansionStarted() {
        this.onQsExpansionStarted(0);
    }
    
    private boolean onQsIntercept(final MotionEvent motionEvent) {
        int pointerIndex;
        if ((pointerIndex = motionEvent.findPointerIndex(this.mTrackingPointer)) < 0) {
            this.mTrackingPointer = motionEvent.getPointerId(0);
            pointerIndex = 0;
        }
        final float x = motionEvent.getX(pointerIndex);
        final float y = motionEvent.getY(pointerIndex);
        final int actionMasked = motionEvent.getActionMasked();
        boolean b = true;
        int n = 1;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked != 6) {
                            return false;
                        }
                        final int pointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                        if (this.mTrackingPointer == pointerId) {
                            if (motionEvent.getPointerId(0) != pointerId) {
                                n = 0;
                            }
                            this.mTrackingPointer = motionEvent.getPointerId(n);
                            this.mInitialTouchX = motionEvent.getX(n);
                            this.mInitialTouchY = motionEvent.getY(n);
                            return false;
                        }
                        return false;
                    }
                }
                else {
                    final float n2 = y - this.mInitialTouchY;
                    this.trackMovement(motionEvent);
                    if (this.mQsTracking) {
                        this.setQsExpansion(n2 + this.mInitialHeightOnTouch);
                        this.trackMovement(motionEvent);
                        return true;
                    }
                    if (Math.abs(n2) > this.getTouchSlop(motionEvent) && Math.abs(n2) > Math.abs(x - this.mInitialTouchX) && this.shouldQuickSettingsIntercept(this.mInitialTouchX, this.mInitialTouchY, n2)) {
                        this.mQsTracking = true;
                        this.onQsExpansionStarted();
                        this.notifyExpandingFinished();
                        this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                        this.mInitialTouchY = y;
                        this.mInitialTouchX = x;
                        this.mNotificationStackScroller.cancelLongPress();
                        return true;
                    }
                    return false;
                }
            }
            this.trackMovement(motionEvent);
            if (this.mQsTracking) {
                if (motionEvent.getActionMasked() != 3) {
                    b = false;
                }
                this.flingQsWithCurrentVelocity(y, b);
                this.mQsTracking = false;
            }
        }
        else {
            this.mInitialTouchY = y;
            this.mInitialTouchX = x;
            this.initVelocityTracker();
            this.trackMovement(motionEvent);
            if (this.shouldQuickSettingsIntercept(this.mInitialTouchX, this.mInitialTouchY, 0.0f)) {
                this.mView.getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (this.mQsExpansionAnimator != null) {
                this.onQsExpansionStarted();
                this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                this.mQsTracking = true;
                this.mNotificationStackScroller.cancelLongPress();
            }
        }
        return false;
    }
    
    private void onQsTouch(final MotionEvent motionEvent) {
        final int pointerIndex = motionEvent.findPointerIndex(this.mTrackingPointer);
        boolean b = false;
        final int n = 0;
        int n2 = pointerIndex;
        if (pointerIndex < 0) {
            this.mTrackingPointer = motionEvent.getPointerId(0);
            n2 = 0;
        }
        final float y = motionEvent.getY(n2);
        final float x = motionEvent.getX(n2);
        final float n3 = y - this.mInitialTouchY;
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    this.setQsExpansion(this.mInitialHeightOnTouch + n3);
                    if (n3 >= this.getFalsingThreshold()) {
                        this.mQsTouchAboveFalsingThreshold = true;
                    }
                    this.trackMovement(motionEvent);
                    return;
                }
                if (actionMasked != 3) {
                    if (actionMasked != 6) {
                        return;
                    }
                    final int pointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                    if (this.mTrackingPointer == pointerId) {
                        int n4;
                        if (motionEvent.getPointerId(0) != pointerId) {
                            n4 = n;
                        }
                        else {
                            n4 = 1;
                        }
                        final float y2 = motionEvent.getY(n4);
                        final float x2 = motionEvent.getX(n4);
                        this.mTrackingPointer = motionEvent.getPointerId(n4);
                        this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                        this.mInitialTouchY = y2;
                        this.mInitialTouchX = x2;
                    }
                    return;
                }
            }
            this.mQsTracking = false;
            this.mTrackingPointer = -1;
            this.trackMovement(motionEvent);
            if (this.getQsExpansionFraction() != 0.0f || y >= this.mInitialTouchY) {
                if (motionEvent.getActionMasked() == 3) {
                    b = true;
                }
                this.flingQsWithCurrentVelocity(y, b);
            }
            final VelocityTracker mQsVelocityTracker = this.mQsVelocityTracker;
            if (mQsVelocityTracker != null) {
                mQsVelocityTracker.recycle();
                this.mQsVelocityTracker = null;
            }
        }
        else {
            this.mQsTracking = true;
            this.mInitialTouchY = y;
            this.mInitialTouchX = x;
            this.onQsExpansionStarted();
            this.mInitialHeightOnTouch = this.mQsExpansionHeight;
            this.initVelocityTracker();
            this.trackMovement(motionEvent);
        }
    }
    
    private void positionClockAndNotifications() {
        final boolean addOrRemoveAnimationPending = this.mNotificationStackScroller.isAddOrRemoveAnimationPending();
        final boolean b = addOrRemoveAnimationPending || this.mAnimateNextPositionUpdate;
        int intrinsicPadding;
        if (this.mBarState != 1) {
            intrinsicPadding = this.getUnlockedStackScrollerPadding();
        }
        else {
            final int height = this.mView.getHeight();
            final int max = Math.max(this.mIndicationBottomPadding, this.mAmbientIndicationBottomPadding);
            final int clockPreferredY = this.mKeyguardStatusView.getClockPreferredY(height);
            final boolean bypassEnabled = this.mKeyguardBypassController.getBypassEnabled();
            final boolean hasVisibleNotifications = !bypassEnabled && this.mNotificationStackScroller.getVisibleNotificationCount() != 0;
            this.mKeyguardStatusView.setHasVisibleNotifications(hasVisibleNotifications);
            this.mClockPositionAlgorithm.setup(this.mStatusBarMinHeight, height - max, this.mNotificationStackScroller.getIntrinsicContentHeight(), this.getExpandedFraction(), height, (int)(this.mKeyguardStatusView.getHeight() - this.mShelfHeight / 2.0f - this.mDarkIconSize / 2.0f), clockPreferredY, this.hasCustomClock(), hasVisibleNotifications, this.mInterpolatedDarkAmount, this.mEmptyDragAmount, bypassEnabled, this.getUnlockedStackScrollerPadding());
            this.mClockPositionAlgorithm.run(this.mClockPositionResult);
            PropertyAnimator.setProperty(this.mKeyguardStatusView, AnimatableProperty.X, (float)this.mClockPositionResult.clockX, NotificationPanelViewController.CLOCK_ANIMATION_PROPERTIES, b);
            PropertyAnimator.setProperty(this.mKeyguardStatusView, AnimatableProperty.Y, (float)this.mClockPositionResult.clockY, NotificationPanelViewController.CLOCK_ANIMATION_PROPERTIES, b);
            this.updateNotificationTranslucency();
            this.updateClock();
            intrinsicPadding = this.mClockPositionResult.stackScrollerPaddingExpanded;
        }
        this.mNotificationStackScroller.setIntrinsicPadding(intrinsicPadding);
        super.mKeyguardBottomArea.setAntiBurnInOffsetX(this.mClockPositionResult.clockX);
        ++this.mStackScrollerMeasuringPass;
        this.requestScrollerTopPaddingUpdate(addOrRemoveAnimationPending);
        this.mStackScrollerMeasuringPass = 0;
        this.mAnimateNextPositionUpdate = false;
    }
    
    private void reInflateViews() {
        this.updateShowEmptyShadeView();
        final int indexOfChild = this.mView.indexOfChild((View)this.mKeyguardStatusView);
        this.mView.removeView((View)this.mKeyguardStatusView);
        final KeyguardStatusView mKeyguardStatusView = (KeyguardStatusView)this.mInjectionInflationController.injectable(LayoutInflater.from(this.mView.getContext())).inflate(R$layout.keyguard_status_view, (ViewGroup)this.mView, false);
        this.mKeyguardStatusView = mKeyguardStatusView;
        this.mView.addView((View)mKeyguardStatusView, indexOfChild);
        this.mBigClockContainer.removeAllViews();
        ((KeyguardClockSwitch)this.mView.findViewById(R$id.keyguard_clock_container)).setBigClockContainer(this.mBigClockContainer);
        final int indexOfChild2 = this.mView.indexOfChild((View)super.mKeyguardBottomArea);
        this.mView.removeView((View)super.mKeyguardBottomArea);
        (super.mKeyguardBottomArea = (KeyguardBottomAreaView)this.mInjectionInflationController.injectable(LayoutInflater.from(this.mView.getContext())).inflate(R$layout.keyguard_bottom_area, (ViewGroup)this.mView, false)).initFrom(super.mKeyguardBottomArea);
        this.mView.addView((View)super.mKeyguardBottomArea, indexOfChild2);
        this.initBottomArea();
        this.mKeyguardIndicationController.setIndicationArea((ViewGroup)super.mKeyguardBottomArea);
        this.mStatusBarStateListener.onDozeAmountChanged(super.mStatusBarStateController.getDozeAmount(), super.mStatusBarStateController.getInterpolatedDozeAmount());
        final KeyguardStatusBarView mKeyguardStatusBar = this.mKeyguardStatusBar;
        if (mKeyguardStatusBar != null) {
            mKeyguardStatusBar.onThemeChanged();
        }
        this.setKeyguardStatusViewVisibility(this.mBarState, false, false);
        this.setKeyguardBottomAreaVisibility(this.mBarState, false);
        final Runnable mOnReinflationListener = this.mOnReinflationListener;
        if (mOnReinflationListener != null) {
            mOnReinflationListener.run();
        }
    }
    
    private void resetHorizontalPanelPosition() {
        this.setHorizontalPanelTranslation(0.0f);
    }
    
    private void setClosingWithAlphaFadeout(final boolean mClosingWithAlphaFadeOut) {
        this.mClosingWithAlphaFadeOut = mClosingWithAlphaFadeOut;
        this.mNotificationStackScroller.forceNoOverlappingRendering(mClosingWithAlphaFadeOut);
    }
    
    private void setGroupManager(final NotificationGroupManager mGroupManager) {
        this.mGroupManager = mGroupManager;
    }
    
    private void setIsFullWidth(final boolean b) {
        this.mIsFullWidth = b;
        this.mNotificationStackScroller.setIsFullWidth(b);
    }
    
    private void setKeyguardBottomAreaVisibility(final int n, final boolean b) {
        super.mKeyguardBottomArea.animate().cancel();
        if (b) {
            super.mKeyguardBottomArea.animate().alpha(0.0f).setStartDelay(super.mKeyguardStateController.getKeyguardFadingAwayDelay()).setDuration(super.mKeyguardStateController.getShortenedFadingAwayDuration()).setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT).withEndAction(this.mAnimateKeyguardBottomAreaInvisibleEndRunnable).start();
        }
        else if (n != 1 && n != 2) {
            super.mKeyguardBottomArea.setVisibility(8);
        }
        else {
            super.mKeyguardBottomArea.setVisibility(0);
            super.mKeyguardBottomArea.setAlpha(1.0f);
        }
    }
    
    private void setKeyguardHeadsUpShowingAmount(final float mKeyguardHeadsUpShowingAmount) {
        this.mKeyguardHeadsUpShowingAmount = mKeyguardHeadsUpShowingAmount;
        this.updateHeaderKeyguardAlpha();
    }
    
    private void setKeyguardStatusViewVisibility(final int n, final boolean b, final boolean b2) {
        this.mKeyguardStatusView.animate().cancel();
        this.mKeyguardStatusViewAnimating = false;
        if ((!b && this.mBarState == 1 && n != 1) || b2) {
            this.mKeyguardStatusViewAnimating = true;
            this.mKeyguardStatusView.animate().alpha(0.0f).setStartDelay(0L).setDuration(160L).setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT).withEndAction(this.mAnimateKeyguardStatusViewGoneEndRunnable);
            if (b) {
                this.mKeyguardStatusView.animate().setStartDelay(super.mKeyguardStateController.getKeyguardFadingAwayDelay()).setDuration(super.mKeyguardStateController.getShortenedFadingAwayDuration()).start();
            }
        }
        else if (this.mBarState == 2 && n == 1) {
            this.mKeyguardStatusView.setVisibility(0);
            this.mKeyguardStatusViewAnimating = true;
            this.mKeyguardStatusView.setAlpha(0.0f);
            this.mKeyguardStatusView.animate().alpha(1.0f).setStartDelay(0L).setDuration(320L).setInterpolator((TimeInterpolator)Interpolators.ALPHA_IN).withEndAction(this.mAnimateKeyguardStatusViewVisibleEndRunnable);
        }
        else if (n == 1) {
            if (b) {
                this.mKeyguardStatusViewAnimating = true;
                this.mKeyguardStatusView.animate().alpha(0.0f).translationYBy(-this.getHeight() * 0.05f).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_LINEAR_IN).setDuration(125L).setStartDelay(0L).withEndAction(this.mAnimateKeyguardStatusViewInvisibleEndRunnable).start();
            }
            else {
                this.mKeyguardStatusView.setVisibility(0);
                this.mKeyguardStatusView.setAlpha(1.0f);
            }
        }
        else {
            this.mKeyguardStatusView.setVisibility(8);
            this.mKeyguardStatusView.setAlpha(1.0f);
        }
    }
    
    private void setLaunchingAffordance(final boolean b) {
        this.mLaunchingAffordance = b;
        this.mKeyguardAffordanceHelperCallback.getLeftIcon().setLaunchingAffordance(b);
        this.mKeyguardAffordanceHelperCallback.getRightIcon().setLaunchingAffordance(b);
        this.mKeyguardBypassController.setLaunchingAffordance(b);
        final Consumer<Boolean> mAffordanceLaunchListener = this.mAffordanceLaunchListener;
        if (mAffordanceLaunchListener != null) {
            mAffordanceLaunchListener.accept(b);
        }
    }
    
    private void setListening(final boolean b) {
        this.mKeyguardStatusBar.setListening(b);
        final QS mQs = this.mQs;
        if (mQs == null) {
            return;
        }
        mQs.setListening(b);
    }
    
    private void setOverScrolling(final boolean b) {
        this.mStackScrollerOverscrolling = b;
        final QS mQs = this.mQs;
        if (mQs == null) {
            return;
        }
        mQs.setOverscrolling(b);
    }
    
    private void setQsExpanded(final boolean b) {
        if (this.mQsExpanded != b) {
            this.mQsExpanded = b;
            this.updateQsState();
            this.requestPanelHeightUpdate();
            this.mFalsingManager.setQsExpanded(b);
            super.mStatusBar.setQsExpanded(b);
            this.mNotificationContainerParent.setQsExpanded(b);
            this.mPulseExpansionHandler.setQsExpanded(b);
            this.mKeyguardBypassController.setQSExpanded(b);
        }
    }
    
    private void setQsExpansion(float min) {
        min = Math.min(Math.max(min, (float)this.mQsMinExpansionHeight), (float)this.mQsMaxExpansionHeight);
        final int mQsMaxExpansionHeight = this.mQsMaxExpansionHeight;
        final float n = (float)mQsMaxExpansionHeight;
        final int n2 = 0;
        this.mQsFullyExpanded = (min == n && mQsMaxExpansionHeight != 0);
        if (min > this.mQsMinExpansionHeight && !this.mQsExpanded && !this.mStackScrollerOverscrolling && !this.mDozing) {
            this.setQsExpanded(true);
        }
        else if (min <= this.mQsMinExpansionHeight && this.mQsExpanded) {
            this.setQsExpanded(false);
        }
        this.mQsExpansionHeight = min;
        this.updateQsExpansion();
        this.requestScrollerTopPaddingUpdate(false);
        this.updateHeaderKeyguardAlpha();
        final int mBarState = this.mBarState;
        if (mBarState == 2 || mBarState == 1) {
            this.updateKeyguardBottomAreaAlpha();
            this.updateBigClockAlpha();
        }
        if (this.mBarState == 0 && this.mQsExpanded && !this.mStackScrollerOverscrolling && this.mQsScrimEnabled) {
            this.mQsNavbarScrim.setAlpha(this.getQsExpansionFraction());
        }
        if (this.mAccessibilityManager.isEnabled()) {
            this.mView.setAccessibilityPaneTitle((CharSequence)this.determineAccessibilityPaneTitle());
        }
        int i = n2;
        if (!this.mFalsingManager.isUnlockingDisabled()) {
            i = n2;
            if (this.mQsFullyExpanded) {
                i = n2;
                if (this.mFalsingManager.shouldEnforceBouncer()) {
                    super.mStatusBar.executeRunnableDismissingKeyguard(null, null, false, true, false);
                    i = n2;
                }
            }
        }
        while (i < super.mExpansionListeners.size()) {
            final PanelExpansionListener panelExpansionListener = super.mExpansionListeners.get(i);
            final int mQsMaxExpansionHeight2 = this.mQsMaxExpansionHeight;
            if (mQsMaxExpansionHeight2 != 0) {
                min = this.mQsExpansionHeight / mQsMaxExpansionHeight2;
            }
            else {
                min = 0.0f;
            }
            panelExpansionListener.onQsExpansionChanged(min);
            ++i;
        }
    }
    
    private void setStatusBar(final StatusBar statusBar) {
        super.mStatusBar = statusBar;
        super.mKeyguardBottomArea.setStatusBar(statusBar);
    }
    
    private boolean shouldQuickSettingsIntercept(final float n, final float n2, final float n3) {
        final boolean mQsExpansionEnabled = this.mQsExpansionEnabled;
        final boolean b = false;
        if (!mQsExpansionEnabled || this.mCollapsedOnDown || (this.mKeyguardShowing && this.mKeyguardBypassController.getBypassEnabled())) {
            return false;
        }
        Object o = null;
        Label_0080: {
            if (!this.mKeyguardShowing) {
                final QS mQs = this.mQs;
                if (mQs != null) {
                    o = mQs.getHeader();
                    break Label_0080;
                }
            }
            o = this.mKeyguardStatusBar;
        }
        final boolean b2 = n >= this.mQsFrame.getX() && n <= this.mQsFrame.getX() + this.mQsFrame.getWidth() && n2 >= ((View)o).getTop() && n2 <= ((View)o).getBottom();
        if (this.mQsExpanded) {
            if (!b2) {
                boolean b3 = b;
                if (n3 >= 0.0f) {
                    return b3;
                }
                b3 = b;
                if (!this.isInQsArea(n, n2)) {
                    return b3;
                }
            }
            return true;
        }
        return b2;
    }
    
    private void startQsSizeChangeAnimation(int intValue, final int n) {
        final ValueAnimator mQsSizeChangeAnimator = this.mQsSizeChangeAnimator;
        if (mQsSizeChangeAnimator != null) {
            intValue = (int)mQsSizeChangeAnimator.getAnimatedValue();
            this.mQsSizeChangeAnimator.cancel();
        }
        (this.mQsSizeChangeAnimator = ValueAnimator.ofInt(new int[] { intValue, n })).setDuration(300L);
        this.mQsSizeChangeAnimator.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        this.mQsSizeChangeAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                NotificationPanelViewController.this.requestPanelHeightUpdate();
                NotificationPanelViewController.this.mQs.setHeightOverride((int)NotificationPanelViewController.this.mQsSizeChangeAnimator.getAnimatedValue());
            }
        });
        this.mQsSizeChangeAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                NotificationPanelViewController.this.mQsSizeChangeAnimator = null;
            }
        });
        this.mQsSizeChangeAnimator.start();
    }
    
    private void trackMovement(final MotionEvent motionEvent) {
        final VelocityTracker mQsVelocityTracker = this.mQsVelocityTracker;
        if (mQsVelocityTracker != null) {
            mQsVelocityTracker.addMovement(motionEvent);
        }
    }
    
    private void updateBigClockAlpha() {
        float n;
        if (this.isUnlockHintRunning()) {
            n = 0.0f;
        }
        else {
            n = 0.95f;
        }
        this.mBigClockContainer.setAlpha(Math.min(MathUtils.map(n, 1.0f, 0.0f, 1.0f, this.getExpandedFraction()), 1.0f - this.getQsExpansionFraction()));
    }
    
    private void updateClock() {
        if (!this.mKeyguardStatusViewAnimating) {
            this.mKeyguardStatusView.setAlpha(this.mClockPositionResult.clockAlpha);
        }
    }
    
    private void updateDozingVisibilities(final boolean b) {
        super.mKeyguardBottomArea.setDozing(this.mDozing, b);
        if (!this.mDozing && b) {
            this.animateKeyguardStatusBarIn(360L);
        }
    }
    
    private void updateEmptyShadeView() {
        this.mNotificationStackScroller.updateEmptyShadeView(this.mShowEmptyShadeView && !this.mQsExpanded);
    }
    
    private void updateGestureExclusionRect() {
        final Rect calculateGestureExclusionRect = this.calculateGestureExclusionRect();
        final NotificationPanelView mView = this.mView;
        List<Rect> systemGestureExclusionRects;
        if (calculateGestureExclusionRect.isEmpty()) {
            systemGestureExclusionRects = (List<Rect>)Collections.EMPTY_LIST;
        }
        else {
            systemGestureExclusionRects = Collections.singletonList(calculateGestureExclusionRect);
        }
        mView.setSystemGestureExclusionRects((List)systemGestureExclusionRects);
    }
    
    private void updateHeader() {
        if (this.mBarState == 1) {
            this.updateHeaderKeyguardAlpha();
        }
        this.updateQsExpansion();
    }
    
    private void updateHeaderKeyguardAlpha() {
        if (!this.mKeyguardShowing) {
            return;
        }
        final float alpha = Math.min(this.getKeyguardContentsAlpha(), 1.0f - Math.min(1.0f, this.getQsExpansionFraction() * 2.0f)) * this.mKeyguardStatusBarAnimateAlpha * (1.0f - this.mKeyguardHeadsUpShowingAmount);
        this.mKeyguardStatusBar.setAlpha(alpha);
        final boolean mFirstBypassAttempt = this.mFirstBypassAttempt;
        final int n = 0;
        final boolean b = (mFirstBypassAttempt && this.mUpdateMonitor.shouldListenForFace()) || this.mDelayShowingKeyguardStatusBar;
        final KeyguardStatusBarView mKeyguardStatusBar = this.mKeyguardStatusBar;
        int visibility;
        if (alpha != 0.0f && !this.mDozing && !b) {
            visibility = n;
        }
        else {
            visibility = 4;
        }
        mKeyguardStatusBar.setVisibility(visibility);
    }
    
    private void updateHeadsUpVisibility() {
        ((PhoneStatusBarView)super.mBar).setHeadsUpVisible(this.mHeadsUpAnimatingAway || this.mHeadsUpPinnedMode);
    }
    
    private void updateKeyguardBottomAreaAlpha() {
        float n;
        if (this.isUnlockHintRunning()) {
            n = 0.0f;
        }
        else {
            n = 0.95f;
        }
        final float n2 = Math.min(MathUtils.map(n, 1.0f, 0.0f, 1.0f, this.getExpandedFraction()), 1.0f - this.getQsExpansionFraction()) * this.mBottomAreaShadeAlpha;
        super.mKeyguardBottomArea.setAffordanceAlpha(n2);
        final KeyguardBottomAreaView mKeyguardBottomArea = super.mKeyguardBottomArea;
        int importantForAccessibility;
        if (n2 == 0.0f) {
            importantForAccessibility = 4;
        }
        else {
            importantForAccessibility = 0;
        }
        mKeyguardBottomArea.setImportantForAccessibility(importantForAccessibility);
        final View ambientIndicationContainer = super.mStatusBar.getAmbientIndicationContainer();
        if (ambientIndicationContainer != null) {
            ambientIndicationContainer.setAlpha(n2);
        }
    }
    
    private void updateKeyguardStatusBarForHeadsUp() {
        final boolean mShowingKeyguardHeadsUp = this.mKeyguardShowing && this.mHeadsUpAppearanceController.shouldBeVisible();
        if (this.mShowingKeyguardHeadsUp != mShowingKeyguardHeadsUp) {
            this.mShowingKeyguardHeadsUp = mShowingKeyguardHeadsUp;
            final boolean mKeyguardShowing = this.mKeyguardShowing;
            float n = 0.0f;
            if (mKeyguardShowing) {
                final NotificationPanelView mView = this.mView;
                final AnimatableProperty keyguard_HEADS_UP_SHOWING_AMOUNT = this.KEYGUARD_HEADS_UP_SHOWING_AMOUNT;
                if (mShowingKeyguardHeadsUp) {
                    n = 1.0f;
                }
                PropertyAnimator.setProperty(mView, keyguard_HEADS_UP_SHOWING_AMOUNT, n, NotificationPanelViewController.KEYGUARD_HUN_PROPERTIES, true);
            }
            else {
                PropertyAnimator.applyImmediately(this.mView, this.KEYGUARD_HEADS_UP_SHOWING_AMOUNT, 0.0f);
            }
        }
    }
    
    private void updateMaxHeadsUpTranslation() {
        this.mNotificationStackScroller.setHeadsUpBoundaries(this.getHeight(), this.mNavigationBarBottomHeight);
    }
    
    private void updateNotificationTranslucency() {
        float fadeoutAlpha;
        if (this.mClosingWithAlphaFadeOut && !this.mExpandingFromHeadsUp && !super.mHeadsUpManager.hasPinnedHeadsUp()) {
            fadeoutAlpha = this.getFadeoutAlpha();
        }
        else {
            fadeoutAlpha = 1.0f;
        }
        float alpha = fadeoutAlpha;
        if (this.mBarState == 1) {
            alpha = fadeoutAlpha;
            if (!super.mHintAnimationRunning) {
                alpha = fadeoutAlpha;
                if (!this.mKeyguardBypassController.getBypassEnabled()) {
                    alpha = fadeoutAlpha * this.mClockPositionResult.clockAlpha;
                }
            }
        }
        this.mNotificationStackScroller.setAlpha(alpha);
    }
    
    private void updatePanelExpanded() {
        final boolean b = !this.isFullyCollapsed() || this.mExpectingSynthesizedDown;
        if (this.mPanelExpanded != b) {
            super.mHeadsUpManager.setIsPanelExpanded(b);
            super.mStatusBarTouchableRegionManager.setPanelExpanded(b);
            super.mStatusBar.setPanelExpanded(b);
            this.mPanelExpanded = b;
        }
    }
    
    private void updateQSPulseExpansion() {
        final QS mQs = this.mQs;
        if (mQs != null) {
            mQs.setShowCollapsedOnKeyguard(this.mKeyguardShowing && this.mKeyguardBypassController.getBypassEnabled() && this.mNotificationStackScroller.isPulseExpanding());
        }
    }
    
    private void updateQsState() {
        this.mNotificationStackScroller.setQsExpanded(this.mQsExpanded);
        final NotificationStackScrollLayout mNotificationStackScroller = this.mNotificationStackScroller;
        final int mBarState = this.mBarState;
        int visibility = 0;
        mNotificationStackScroller.setScrollingEnabled(mBarState != 1 && (!this.mQsExpanded || this.mQsExpansionFromOverscroll));
        this.updateEmptyShadeView();
        final View mQsNavbarScrim = this.mQsNavbarScrim;
        if (this.mBarState != 0 || !this.mQsExpanded || this.mStackScrollerOverscrolling || !this.mQsScrimEnabled) {
            visibility = 4;
        }
        mQsNavbarScrim.setVisibility(visibility);
        final KeyguardUserSwitcher mKeyguardUserSwitcher = this.mKeyguardUserSwitcher;
        if (mKeyguardUserSwitcher != null && this.mQsExpanded && !this.mStackScrollerOverscrolling) {
            mKeyguardUserSwitcher.hideIfNotSimple(true);
        }
        final QS mQs = this.mQs;
        if (mQs == null) {
            return;
        }
        mQs.setExpanded(this.mQsExpanded);
    }
    
    private void updateShowEmptyShadeView() {
        final int mBarState = this.mBarState;
        boolean b = true;
        if (mBarState == 1 || this.mEntryManager.hasActiveNotifications()) {
            b = false;
        }
        this.showEmptyShadeView(b);
    }
    
    private void updateStatusBarIcons() {
        boolean mShowIconsWhenExpanded;
        final boolean b = mShowIconsWhenExpanded = ((this.isPanelVisibleBecauseOfHeadsUp() || this.isFullWidth()) && this.getExpandedHeight() < this.getOpeningHeight());
        if (b) {
            mShowIconsWhenExpanded = b;
            if (this.isOnKeyguard()) {
                mShowIconsWhenExpanded = false;
            }
        }
        if (mShowIconsWhenExpanded != this.mShowIconsWhenExpanded) {
            this.mShowIconsWhenExpanded = mShowIconsWhenExpanded;
            this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, false);
        }
    }
    
    public void addOnGlobalLayoutListener(final ViewTreeObserver$OnGlobalLayoutListener viewTreeObserver$OnGlobalLayoutListener) {
        this.mView.getViewTreeObserver().addOnGlobalLayoutListener(viewTreeObserver$OnGlobalLayoutListener);
    }
    
    public void addTrackingHeadsUpListener(final Consumer<ExpandableNotificationRow> e) {
        this.mTrackingHeadsUpListeners.add(e);
    }
    
    public void addVerticalTranslationListener(final Runnable e) {
        this.mVerticalTranslationListener.add(e);
    }
    
    public void animateCloseQs(final boolean b) {
        final ValueAnimator mQsExpansionAnimator = this.mQsExpansionAnimator;
        if (mQsExpansionAnimator != null) {
            if (!this.mQsAnimatorExpand) {
                return;
            }
            final float mQsExpansionHeight = this.mQsExpansionHeight;
            mQsExpansionAnimator.cancel();
            this.setQsExpansion(mQsExpansionHeight);
        }
        int n;
        if (b) {
            n = 2;
        }
        else {
            n = 1;
        }
        this.flingSettings(0.0f, n);
    }
    
    public void animateToFullShade(final long n) {
        this.mNotificationStackScroller.goToFullShade(n);
        this.mView.requestLayout();
        this.mAnimateNextPositionUpdate = true;
    }
    
    public void applyExpandAnimationParams(final ActivityLaunchAnimator.ExpandAnimationParameters expandAnimationParameters) {
        float mExpandOffset;
        if (expandAnimationParameters != null) {
            mExpandOffset = (float)expandAnimationParameters.getTopChange();
        }
        else {
            mExpandOffset = 0.0f;
        }
        this.mExpandOffset = mExpandOffset;
        this.updateQsExpansion();
        if (expandAnimationParameters != null) {
            final boolean mHideIconsDuringNotificationLaunch = expandAnimationParameters.getProgress(14L, 100L) == 0.0f;
            if (mHideIconsDuringNotificationLaunch != this.mHideIconsDuringNotificationLaunch && !(this.mHideIconsDuringNotificationLaunch = mHideIconsDuringNotificationLaunch)) {
                this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
            }
        }
    }
    
    public void blockExpansionForCurrentTouch() {
        this.mBlockingExpansionForCurrentTouch = super.mTracking;
    }
    
    public boolean canCameraGestureBeLaunched() {
        final boolean cameraAllowedByAdmin = super.mStatusBar.isCameraAllowedByAdmin();
        final boolean b = false;
        if (!cameraAllowedByAdmin) {
            return false;
        }
        final ResolveInfo resolveCameraIntent = super.mKeyguardBottomArea.resolveCameraIntent();
        String packageName = null;
        Label_0050: {
            if (resolveCameraIntent != null) {
                final ActivityInfo activityInfo = resolveCameraIntent.activityInfo;
                if (activityInfo != null) {
                    packageName = activityInfo.packageName;
                    break Label_0050;
                }
            }
            packageName = null;
        }
        boolean b2 = b;
        if (packageName != null) {
            if (this.mBarState == 0) {
                b2 = b;
                if (this.isForegroundApp(packageName)) {
                    return b2;
                }
            }
            b2 = b;
            if (!this.mAffordanceHelper.isSwipingInProgress()) {
                b2 = true;
            }
        }
        return b2;
    }
    
    public void cancelAnimation() {
        this.mView.animate().cancel();
    }
    
    public void clearNotificationEffects() {
        super.mStatusBar.clearNotificationEffects();
    }
    
    public void closeQs() {
        this.cancelQsAnimation();
        this.setQsExpansion((float)this.mQsMinExpansionHeight);
    }
    
    public void closeQsDetail() {
        this.mQs.closeDetail();
    }
    
    @Override
    public void collapse(final boolean b, final float n) {
        if (!this.canPanelBeCollapsed()) {
            return;
        }
        if (this.mQsExpanded) {
            this.mQsExpandImmediate = true;
            this.mNotificationStackScroller.setShouldShowShelfOnly(true);
        }
        super.collapse(b, n);
    }
    
    public int computeMaxKeyguardNotifications(int i) {
        final float minStackScrollerPadding = this.mClockPositionAlgorithm.getMinStackScrollerPadding();
        final int max = Math.max(1, super.mResources.getDimensionPixelSize(R$dimen.notification_divider_height));
        final NotificationShelf notificationShelf = this.mNotificationStackScroller.getNotificationShelf();
        float n;
        if (notificationShelf.getVisibility() == 8) {
            n = 0.0f;
        }
        else {
            n = (float)(notificationShelf.getIntrinsicHeight() + max);
        }
        float n2 = this.mNotificationStackScroller.getHeight() - minStackScrollerPadding - n - Math.max(this.mIndicationBottomPadding, this.mAmbientIndicationBottomPadding) - this.mKeyguardStatusView.getLogoutButtonHeight();
        int n4;
        int n3 = n4 = 0;
        int n5;
        while (true) {
            n5 = n4;
            if (n3 >= this.mNotificationStackScroller.getChildCount()) {
                break;
            }
            final ExpandableView expandableView = (ExpandableView)this.mNotificationStackScroller.getChildAt(n3);
            if (expandableView instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)expandableView;
                final NotificationGroupManager mGroupManager = this.mGroupManager;
                if (mGroupManager == null || !mGroupManager.isSummaryOfSuppressedGroup(expandableNotificationRow.getEntry().getSbn())) {
                    if (this.mLockscreenUserManager.shouldShowOnKeyguard(expandableNotificationRow.getEntry())) {
                        if (!expandableNotificationRow.isRemoved()) {
                            n2 -= expandableView.getMinHeight(true) + max;
                            if (n2 >= 0.0f && n4 < i) {
                                ++n4;
                            }
                            else {
                                n5 = n4;
                                if (n2 > -n) {
                                    for (i = n3 + 1; i < this.mNotificationStackScroller.getChildCount(); ++i) {
                                        if (this.mNotificationStackScroller.getChildAt(i) instanceof ExpandableNotificationRow) {
                                            return n4;
                                        }
                                    }
                                    n5 = n4 + 1;
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            ++n3;
        }
        return n5;
    }
    
    public OnLayoutChangeListener createLayoutChangeListener() {
        return new OnLayoutChangeListener();
    }
    
    @Override
    protected PanelViewController.OnConfigurationChangedListener createOnConfigurationChangedListener() {
        return new OnConfigurationChangedListener();
    }
    
    public RemoteInputController.Delegate createRemoteInputDelegate() {
        return this.mNotificationStackScroller.createDelegate();
    }
    
    @Override
    protected TouchHandler createTouchHandler() {
        return new TouchHandler() {
            @Override
            public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
                if (NotificationPanelViewController.this.mBlockTouches || (NotificationPanelViewController.this.mQsFullyExpanded && NotificationPanelViewController.this.mQs.onInterceptTouchEvent(motionEvent))) {
                    return false;
                }
                NotificationPanelViewController.this.initDownStates(motionEvent);
                if (NotificationPanelViewController.this.mStatusBar.isBouncerShowing()) {
                    return true;
                }
                if (NotificationPanelViewController.this.mBar.panelEnabled() && NotificationPanelViewController.this.mHeadsUpTouchHelper.onInterceptTouchEvent(motionEvent)) {
                    NotificationPanelViewController.this.mMetricsLogger.count("panel_open", 1);
                    NotificationPanelViewController.this.mMetricsLogger.count("panel_open_peek", 1);
                    return true;
                }
                final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
                return (!this$0.shouldQuickSettingsIntercept(this$0.mDownX, NotificationPanelViewController.this.mDownY, 0.0f) && NotificationPanelViewController.this.mPulseExpansionHandler.onInterceptTouchEvent(motionEvent)) || (!NotificationPanelViewController.this.isFullyCollapsed() && NotificationPanelViewController.this.onQsIntercept(motionEvent)) || super.onInterceptTouchEvent(motionEvent);
            }
            
            @Override
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                final boolean access$4100 = NotificationPanelViewController.this.mBlockTouches;
                boolean b2;
                final boolean b = b2 = false;
                if (!access$4100) {
                    if (NotificationPanelViewController.this.mQs != null && NotificationPanelViewController.this.mQs.isCustomizing()) {
                        b2 = b;
                    }
                    else {
                        if (NotificationPanelViewController.this.mStatusBar.isBouncerShowingScrimmed()) {
                            return false;
                        }
                        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                            NotificationPanelViewController.this.mBlockingExpansionForCurrentTouch = false;
                        }
                        if (NotificationPanelViewController.this.mLastEventSynthesizedDown && motionEvent.getAction() == 1) {
                            NotificationPanelViewController.this.expand(true);
                        }
                        NotificationPanelViewController.this.initDownStates(motionEvent);
                        if (!NotificationPanelViewController.this.mIsExpanding) {
                            final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
                            if (!this$0.shouldQuickSettingsIntercept(this$0.mDownX, NotificationPanelViewController.this.mDownY, 0.0f) && NotificationPanelViewController.this.mPulseExpansionHandler.onTouchEvent(motionEvent)) {
                                return true;
                            }
                        }
                        if (NotificationPanelViewController.this.mListenForHeadsUp && !NotificationPanelViewController.this.mHeadsUpTouchHelper.isTrackingHeadsUp() && NotificationPanelViewController.this.mHeadsUpTouchHelper.onInterceptTouchEvent(motionEvent)) {
                            NotificationPanelViewController.this.mMetricsLogger.count("panel_open_peek", 1);
                        }
                        final boolean b3 = (!NotificationPanelViewController.this.mIsExpanding || NotificationPanelViewController.this.mHintAnimationRunning) && !NotificationPanelViewController.this.mQsExpanded && NotificationPanelViewController.this.mBarState != 0 && !NotificationPanelViewController.this.mDozing && (NotificationPanelViewController.this.mAffordanceHelper.onTouchEvent(motionEvent) | false);
                        if (NotificationPanelViewController.this.mOnlyAffordanceInThisMotion) {
                            return true;
                        }
                        final boolean b4 = b3 | NotificationPanelViewController.this.mHeadsUpTouchHelper.onTouchEvent(motionEvent);
                        if (!NotificationPanelViewController.this.mHeadsUpTouchHelper.isTrackingHeadsUp() && NotificationPanelViewController.this.handleQsTouch(motionEvent)) {
                            return true;
                        }
                        boolean b5 = b4;
                        if (motionEvent.getActionMasked() == 0) {
                            b5 = b4;
                            if (NotificationPanelViewController.this.isFullyCollapsed()) {
                                NotificationPanelViewController.this.mMetricsLogger.count("panel_open", 1);
                                NotificationPanelViewController.this.updateVerticalPanelPosition(motionEvent.getX());
                                b5 = true;
                            }
                        }
                        final boolean onTouch = super.onTouch(view, motionEvent);
                        if (NotificationPanelViewController.this.mDozing && !NotificationPanelViewController.this.mPulsing) {
                            b2 = b;
                            if (!(onTouch | b5)) {
                                return b2;
                            }
                        }
                        b2 = true;
                    }
                }
                return b2;
            }
        };
    }
    
    public void dozeTimeTick() {
        super.mKeyguardBottomArea.dozeTimeTick();
        this.mKeyguardStatusView.dozeTimeTick();
        if (this.mInterpolatedDarkAmount > 0.0f) {
            this.positionClockAndNotifications();
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        super.dump(fileDescriptor, printWriter, array);
        final StringBuilder sb = new StringBuilder();
        sb.append("    gestureExclusionRect: ");
        sb.append(this.calculateGestureExclusionRect());
        printWriter.println(sb.toString());
        final KeyguardStatusBarView mKeyguardStatusBar = this.mKeyguardStatusBar;
        if (mKeyguardStatusBar != null) {
            mKeyguardStatusBar.dump(fileDescriptor, printWriter, array);
        }
        final KeyguardStatusView mKeyguardStatusView = this.mKeyguardStatusView;
        if (mKeyguardStatusView != null) {
            mKeyguardStatusView.dump(fileDescriptor, printWriter, array);
        }
    }
    
    @Override
    public void expand(final boolean b) {
        super.expand(b);
        this.setListening(true);
    }
    
    public void expandWithQs() {
        if (this.mQsExpansionEnabled) {
            this.mQsExpandImmediate = true;
            this.mNotificationStackScroller.setShouldShowShelfOnly(true);
        }
        if (this.isFullyCollapsed()) {
            this.expand(true);
        }
        else {
            this.flingSettings(0.0f, 0);
        }
    }
    
    public void expandWithoutQs() {
        if (this.isQsExpanded()) {
            this.flingSettings(0.0f, 1);
        }
        else {
            this.expand(true);
        }
    }
    
    public ViewPropertyAnimator fadeOut(final long startDelay, final long duration, final Runnable runnable) {
        return this.mView.animate().alpha(0.0f).setStartDelay(startDelay).setDuration(duration).setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT).withLayer().withEndAction(runnable);
    }
    
    public void fling(final float f, final boolean b) {
        final GestureRecorder gestureRecorder = ((PhoneStatusBarView)super.mBar).mBar.getGestureRecorder();
        if (gestureRecorder != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("fling ");
            String str;
            if (f > 0.0f) {
                str = "open";
            }
            else {
                str = "closed";
            }
            sb.append(str);
            final String string = sb.toString();
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("notifications,v=");
            sb2.append(f);
            gestureRecorder.tag(string, sb2.toString());
        }
        super.fling(f, b);
    }
    
    @Override
    protected boolean flingExpands(final float n, final float n2, final float n3, final float n4) {
        boolean flingExpands = super.flingExpands(n, n2, n3, n4);
        if (this.mQsExpansionAnimator != null) {
            flingExpands = true;
        }
        return flingExpands;
    }
    
    public void flingSettings(final float n, final int n2) {
        this.flingSettings(n, n2, null, false);
    }
    
    protected void flingSettings(float n, int n2, final Runnable runnable, final boolean b) {
        float n3 = 0.0f;
        Label_0035: {
            int n4;
            if (n2 != 0) {
                if (n2 != 1) {
                    n3 = 0.0f;
                    break Label_0035;
                }
                n4 = this.mQsMinExpansionHeight;
            }
            else {
                n4 = this.mQsMaxExpansionHeight;
            }
            n3 = (float)n4;
        }
        if (n3 == this.mQsExpansionHeight) {
            if (runnable != null) {
                runnable.run();
            }
            return;
        }
        final boolean mQsAnimatorExpand = n2 == 0;
        if ((n > 0.0f && !mQsAnimatorExpand) || (n < 0.0f && mQsAnimatorExpand)) {
            n = 0.0f;
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.mQsExpansionHeight, n3 });
        if (b) {
            ofFloat.setInterpolator((TimeInterpolator)Interpolators.TOUCH_RESPONSE);
            ofFloat.setDuration(368L);
        }
        else {
            this.mFlingAnimationUtils.apply((Animator)ofFloat, this.mQsExpansionHeight, n3, n);
        }
        if (n2 != 0) {
            ofFloat.setDuration(350L);
        }
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$NotificationPanelViewController$PxDf76v5kbscyhBqkVxRT_vLxqI(this));
        ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                NotificationPanelViewController.this.mNotificationStackScroller.resetCheckSnoozeLeavebehind();
                NotificationPanelViewController.this.mQsExpansionAnimator = null;
                final Runnable val$onFinishRunnable = runnable;
                if (val$onFinishRunnable != null) {
                    val$onFinishRunnable.run();
                }
            }
        });
        ofFloat.start();
        this.mQsExpansionAnimator = ofFloat;
        this.mQsAnimatorExpand = mQsAnimatorExpand;
    }
    
    @Override
    protected void flingToHeight(final float n, final boolean b, final float n2, final float n3, final boolean b2) {
        this.mHeadsUpTouchHelper.notifyFling(b ^ true);
        this.setClosingWithAlphaFadeout(!b && !this.isOnKeyguard() && this.getFadeoutAlpha() == 1.0f);
        super.flingToHeight(n, b, n2, n3, b2);
    }
    
    @Override
    protected boolean fullyExpandedClearAllVisible() {
        return this.mNotificationStackScroller.isFooterViewNotGone() && this.mNotificationStackScroller.isScrolledToBottom() && !this.mQsExpandImmediate;
    }
    
    public ActivatableNotificationView getActivatedChild() {
        return this.mNotificationStackScroller.getActivatedChild();
    }
    
    @Override
    protected int getClearAllHeight() {
        return this.mNotificationStackScroller.getFooterViewHeight();
    }
    
    protected float getHeaderTranslation() {
        if (this.mBarState == 1 && !this.mKeyguardBypassController.getBypassEnabled()) {
            return (float)(-this.mQs.getQsMinExpansionHeight());
        }
        final float calculateAppearFraction = this.mNotificationStackScroller.calculateAppearFraction(super.mExpandedHeight);
        final float n = -this.mQsExpansionHeight;
        float calculateAppearFractionBypass = calculateAppearFraction;
        float n2 = n;
        if (this.mKeyguardBypassController.getBypassEnabled()) {
            calculateAppearFractionBypass = calculateAppearFraction;
            n2 = n;
            if (this.isOnKeyguard()) {
                calculateAppearFractionBypass = calculateAppearFraction;
                n2 = n;
                if (this.mNotificationStackScroller.isPulseExpanding()) {
                    if (!this.mPulseExpansionHandler.isExpanding() && !this.mPulseExpansionHandler.getLeavingLockscreen()) {
                        calculateAppearFractionBypass = 0.0f;
                    }
                    else {
                        calculateAppearFractionBypass = this.mNotificationStackScroller.calculateAppearFractionBypass();
                    }
                    n2 = (float)(-this.mQs.getQsMinExpansionHeight());
                }
            }
        }
        return Math.min(0.0f, MathUtils.lerp(n2, 0.0f, Math.min(1.0f, calculateAppearFractionBypass)) + this.mExpandOffset);
    }
    
    public int getHeight() {
        return this.mView.getHeight();
    }
    
    public KeyguardBottomAreaView getKeyguardBottomAreaView() {
        return super.mKeyguardBottomArea;
    }
    
    @Override
    protected int getMaxPanelHeight() {
        if (this.mKeyguardBypassController.getBypassEnabled() && this.mBarState == 1) {
            return this.getMaxPanelHeightBypass();
        }
        return this.getMaxPanelHeightNonBypass();
    }
    
    public MyOnHeadsUpChangedListener getOnHeadsUpChangedListener() {
        return this.mOnHeadsUpChangedListener;
    }
    
    @Override
    protected float getOpeningHeight() {
        return this.mNotificationStackScroller.getOpeningHeight();
    }
    
    @Override
    protected float getOverExpansionAmount() {
        final float currentOverScrollAmount = this.mNotificationStackScroller.getCurrentOverScrollAmount(true);
        if (Float.isNaN(currentOverScrollAmount)) {
            Log.wtf(PanelViewController.TAG, "OverExpansionAmount is NaN!");
        }
        return currentOverScrollAmount;
    }
    
    @Override
    protected float getOverExpansionPixels() {
        return this.mNotificationStackScroller.getCurrentOverScrolledPixels(true);
    }
    
    @Override
    protected float getPeekHeight() {
        int n;
        if (this.mNotificationStackScroller.getNotGoneChildCount() > 0) {
            n = this.mNotificationStackScroller.getPeekHeight();
        }
        else {
            n = this.mQsMinExpansionHeight;
        }
        return (float)n;
    }
    
    public boolean hasCustomClock() {
        return this.mKeyguardStatusView.hasCustomClock();
    }
    
    public boolean hasPulsingNotifications() {
        return this.mNotificationStackScroller.hasPulsingNotifications();
    }
    
    public boolean hideStatusBarIconsWhenExpanded() {
        if (super.mLaunchingNotification) {
            return this.mHideIconsDuringNotificationLaunch;
        }
        final HeadsUpAppearanceController mHeadsUpAppearanceController = this.mHeadsUpAppearanceController;
        boolean b = false;
        if (mHeadsUpAppearanceController != null && mHeadsUpAppearanceController.shouldBeVisible()) {
            return false;
        }
        if (!this.isFullWidth() || !this.mShowIconsWhenExpanded) {
            b = true;
        }
        return b;
    }
    
    public void initDependencies(final StatusBar statusBar, final NotificationGroupManager groupManager, final NotificationShelf shelf, final NotificationIconAreaController iconAreaController, final ScrimController scrimController) {
        this.setStatusBar(statusBar);
        this.setGroupManager(this.mGroupManager);
        this.mNotificationStackScroller.setNotificationPanelController(this);
        this.mNotificationStackScroller.setIconAreaController(iconAreaController);
        this.mNotificationStackScroller.setStatusBar(statusBar);
        this.mNotificationStackScroller.setGroupManager(groupManager);
        this.mNotificationStackScroller.setShelf(shelf);
        this.mNotificationStackScroller.setScrimController(scrimController);
        this.updateShowEmptyShadeView();
    }
    
    @Override
    protected boolean isClearAllVisible() {
        return this.mNotificationStackScroller.isFooterViewContentVisible();
    }
    
    public boolean isDozing() {
        return this.mDozing;
    }
    
    public boolean isExpanding() {
        return this.mIsExpanding;
    }
    
    public boolean isFullWidth() {
        return this.mIsFullWidth;
    }
    
    @Override
    protected boolean isInContentBounds(final float n, final float n2) {
        final float x = this.mNotificationStackScroller.getX();
        return !this.mNotificationStackScroller.isBelowLastNotification(n - x, n2) && x < n && n < x + this.mNotificationStackScroller.getWidth();
    }
    
    public boolean isInSettings() {
        return this.mQsExpanded;
    }
    
    public boolean isLaunchTransitionFinished() {
        return this.mIsLaunchTransitionFinished;
    }
    
    public boolean isLaunchTransitionRunning() {
        return this.mIsLaunchTransitionRunning;
    }
    
    public boolean isLaunchingAffordanceWithPreview() {
        return this.mLaunchingAffordance && this.mAffordanceHasPreview;
    }
    
    @Override
    protected boolean isPanelVisibleBecauseOfHeadsUp() {
        return (super.mHeadsUpManager.hasPinnedHeadsUp() || this.mHeadsUpAnimatingAway) && this.mBarState == 0;
    }
    
    public boolean isQsDetailShowing() {
        return this.mQs.isShowingDetail();
    }
    
    public boolean isQsExpanded() {
        return this.mQsExpanded;
    }
    
    @Override
    protected boolean isScrolledToBottom() {
        final boolean inSettings = this.isInSettings();
        boolean b2;
        final boolean b = b2 = true;
        if (!inSettings) {
            b2 = b;
            if (this.mBarState != 1) {
                b2 = (this.mNotificationStackScroller.isScrolledToBottom() && b);
            }
        }
        return b2;
    }
    
    @Override
    protected boolean isTrackingBlocked() {
        return (this.mConflictingQsExpansionGesture && this.mQsExpanded) || this.mBlockingExpansionForCurrentTouch;
    }
    
    public void launchCamera(boolean b, final int n) {
        final boolean b2 = true;
        if (n == 1) {
            this.mLastCameraLaunchSource = "power_double_tap";
        }
        else if (n == 0) {
            this.mLastCameraLaunchSource = "wiggle_gesture";
        }
        else if (n == 2) {
            this.mLastCameraLaunchSource = "lift_to_launch_ml";
        }
        else {
            this.mLastCameraLaunchSource = "lockscreen_affordance";
        }
        if (!this.isFullyCollapsed()) {
            this.setLaunchingAffordance(true);
        }
        else {
            b = false;
        }
        this.mAffordanceHasPreview = (super.mKeyguardBottomArea.getRightPreview() != null);
        this.mAffordanceHelper.launchAffordance(b, this.mView.getLayoutDirection() == 1 && b2);
    }
    
    @Override
    protected void loadDimens() {
        super.loadDimens();
        final FlingAnimationUtils.Builder mFlingAnimationUtilsBuilder = this.mFlingAnimationUtilsBuilder;
        mFlingAnimationUtilsBuilder.reset();
        mFlingAnimationUtilsBuilder.setMaxLengthSeconds(0.4f);
        this.mFlingAnimationUtils = mFlingAnimationUtilsBuilder.build();
        this.mStatusBarMinHeight = super.mResources.getDimensionPixelSize(17105471);
        this.mQsPeekHeight = super.mResources.getDimensionPixelSize(R$dimen.qs_peek_height);
        this.mNotificationsHeaderCollideDistance = super.mResources.getDimensionPixelSize(R$dimen.header_notifications_collide_distance);
        this.mClockPositionAlgorithm.loadDimens(super.mResources);
        this.mQsFalsingThreshold = super.mResources.getDimensionPixelSize(R$dimen.qs_falsing_threshold);
        this.mPositionMinSideMargin = super.mResources.getDimensionPixelSize(R$dimen.notification_panel_min_side_margin);
        this.mIndicationBottomPadding = super.mResources.getDimensionPixelSize(R$dimen.keyguard_indication_bottom_padding);
        this.mQsNotificationTopPadding = super.mResources.getDimensionPixelSize(R$dimen.qs_notification_padding);
        this.mShelfHeight = super.mResources.getDimensionPixelSize(R$dimen.notification_shelf_height);
        this.mDarkIconSize = super.mResources.getDimensionPixelSize(R$dimen.status_bar_icon_drawing_size_dark);
        this.mHeadsUpInset = super.mResources.getDimensionPixelSize(17105471) + super.mResources.getDimensionPixelSize(R$dimen.heads_up_status_bar_padding);
    }
    
    public void onAffordanceLaunchEnded() {
        this.setLaunchingAffordance(false);
    }
    
    public void onBouncerPreHideAnimation() {
        this.setKeyguardStatusViewVisibility(this.mBarState, true, false);
    }
    
    @Override
    protected void onClosingFinished() {
        super.onClosingFinished();
        this.resetHorizontalPanelPosition();
        this.setClosingWithAlphaFadeout(false);
    }
    
    @Override
    protected void onExpandingFinished() {
        super.onExpandingFinished();
        this.mNotificationStackScroller.onExpansionStopped();
        super.mHeadsUpManager.onExpandingFinished();
        this.mConversationNotificationManager.onNotificationPanelExpandStateChanged(this.isFullyCollapsed());
        this.mIsExpanding = false;
        if (this.isFullyCollapsed()) {
            DejankUtils.postAfterTraversal(new Runnable() {
                @Override
                public void run() {
                    NotificationPanelViewController.this.setListening(false);
                }
            });
            this.mView.postOnAnimation((Runnable)new Runnable() {
                @Override
                public void run() {
                    NotificationPanelViewController.this.mView.getParent().invalidateChild((View)NotificationPanelViewController.this.mView, NotificationPanelViewController.M_DUMMY_DIRTY_RECT);
                }
            });
        }
        else {
            this.setListening(true);
        }
        this.mQsExpandImmediate = false;
        this.mNotificationStackScroller.setShouldShowShelfOnly(false);
        this.mTwoFingerQsExpandPossible = false;
        this.notifyListenersTrackingHeadsUp(null);
        this.mExpandingFromHeadsUp = false;
        this.setPanelScrimMinFraction(0.0f);
    }
    
    @Override
    protected void onExpandingStarted() {
        super.onExpandingStarted();
        this.mNotificationStackScroller.onExpansionStarted();
        this.mIsExpanding = true;
        this.mQsExpandedWhenExpandingStarted = this.mQsFullyExpanded;
        if (this.mQsExpanded) {
            this.onQsExpansionStarted();
        }
        final QS mQs = this.mQs;
        if (mQs == null) {
            return;
        }
        mQs.setHeaderListening(true);
    }
    
    @Override
    protected void onHeightUpdated(final float n) {
        if (!this.mQsExpanded || this.mQsExpandImmediate || (this.mIsExpanding && this.mQsExpandedWhenExpandingStarted)) {
            if (this.mStackScrollerMeasuringPass <= 2) {
                this.positionClockAndNotifications();
            }
        }
        if (this.mQsExpandImmediate || (this.mQsExpanded && !this.mQsTracking && this.mQsExpansionAnimator == null && !this.mQsExpansionFromOverscroll)) {
            float n2;
            if (this.mKeyguardShowing) {
                n2 = n / this.getMaxPanelHeight();
            }
            else {
                final float n3 = (float)(this.mNotificationStackScroller.getIntrinsicPadding() + this.mNotificationStackScroller.getLayoutMinHeight());
                n2 = (n - n3) / (this.calculatePanelHeightQsExpanded() - n3);
            }
            final int mQsMinExpansionHeight = this.mQsMinExpansionHeight;
            this.setQsExpansion(mQsMinExpansionHeight + n2 * (this.mQsMaxExpansionHeight - mQsMinExpansionHeight));
        }
        this.updateExpandedHeight(n);
        this.updateHeader();
        this.updateNotificationTranslucency();
        this.updatePanelExpanded();
        this.updateGestureExclusionRect();
    }
    
    @Override
    protected boolean onMiddleClicked() {
        final int mBarState = this.mBarState;
        if (mBarState == 0) {
            this.mView.post(super.mPostCollapseRunnable);
            return false;
        }
        if (mBarState == 1) {
            if (!this.mDozingOnDown) {
                if (this.mKeyguardBypassController.getBypassEnabled()) {
                    this.mUpdateMonitor.requestFaceAuth();
                }
                else {
                    this.mLockscreenGestureLogger.write(188, 0, 0);
                    this.startUnlockHintAnimation();
                }
            }
            return true;
        }
        if (mBarState != 2) {
            return true;
        }
        if (!this.mQsExpanded) {
            super.mStatusBarStateController.setState(1);
        }
        return true;
    }
    
    protected void onQsExpansionStarted(final int n) {
        this.cancelQsAnimation();
        this.cancelHeightAnimator();
        final float qsExpansion = this.mQsExpansionHeight - n;
        this.setQsExpansion(qsExpansion);
        this.requestPanelHeightUpdate();
        this.mNotificationStackScroller.checkSnoozeLeavebehind();
        if (qsExpansion == 0.0f) {
            super.mStatusBar.requestFaceAuth();
        }
    }
    
    public void onScreenTurningOn() {
        this.mKeyguardStatusView.dozeTimeTick();
    }
    
    public void onThemeChanged() {
        this.mConfigurationListener.onThemeChanged();
    }
    
    @Override
    protected void onTrackingStarted() {
        this.mFalsingManager.onTrackingStarted(super.mKeyguardStateController.canDismissLockScreen() ^ true);
        super.onTrackingStarted();
        if (this.mQsFullyExpanded) {
            this.mQsExpandImmediate = true;
            this.mNotificationStackScroller.setShouldShowShelfOnly(true);
        }
        final int mBarState = this.mBarState;
        if (mBarState == 1 || mBarState == 2) {
            this.mAffordanceHelper.animateHideLeftRightIcon();
        }
        this.mNotificationStackScroller.onPanelTrackingStarted();
    }
    
    @Override
    protected void onTrackingStopped(final boolean b) {
        this.mFalsingManager.onTrackingStopped();
        super.onTrackingStopped(b);
        if (b) {
            this.mNotificationStackScroller.setOverScrolledPixels(0.0f, true, true);
        }
        this.mNotificationStackScroller.onPanelTrackingStopped();
        if (b) {
            final int mBarState = this.mBarState;
            if ((mBarState == 1 || mBarState == 2) && !super.mHintAnimationRunning) {
                this.mAffordanceHelper.reset(true);
            }
        }
    }
    
    @Override
    protected void onUnlockHintFinished() {
        super.onUnlockHintFinished();
        this.mNotificationStackScroller.setUnlockHintRunning(false);
    }
    
    @Override
    protected void onUnlockHintStarted() {
        super.onUnlockHintStarted();
        this.mNotificationStackScroller.setUnlockHintRunning(true);
    }
    
    public void onUpdateRowStates() {
        this.mNotificationStackScroller.onUpdateRowStates();
    }
    
    public void removeOnGlobalLayoutListener(final ViewTreeObserver$OnGlobalLayoutListener viewTreeObserver$OnGlobalLayoutListener) {
        this.mView.getViewTreeObserver().removeOnGlobalLayoutListener(viewTreeObserver$OnGlobalLayoutListener);
    }
    
    public void removeTrackingHeadsUpListener(final Consumer<ExpandableNotificationRow> o) {
        this.mTrackingHeadsUpListeners.remove(o);
    }
    
    public void removeVerticalTranslationListener(final Runnable o) {
        this.mVerticalTranslationListener.remove(o);
    }
    
    protected void requestScrollerTopPaddingUpdate(final boolean b) {
        this.mNotificationStackScroller.updateTopPadding(this.calculateQsTopPadding(), b);
        if (this.mKeyguardShowing && this.mKeyguardBypassController.getBypassEnabled()) {
            this.updateQsExpansion();
        }
    }
    
    public void resetViewGroupFade() {
        ViewGroupFadeHelper.reset((ViewGroup)this.mView);
    }
    
    @Override
    public void resetViews(final boolean b) {
        this.mIsLaunchTransitionFinished = false;
        this.mBlockTouches = false;
        if (!this.mLaunchingAffordance) {
            this.mAffordanceHelper.reset(false);
            this.mLastCameraLaunchSource = "lockscreen_affordance";
        }
        super.mStatusBar.getGutsManager().closeAndSaveGuts(true, true, true, -1, -1, true);
        if (b) {
            this.animateCloseQs(true);
        }
        else {
            this.closeQs();
        }
        this.mNotificationStackScroller.setOverScrollAmount(0.0f, true, b, b ^ true);
        this.mNotificationStackScroller.resetScrollPosition();
    }
    
    public void runAfterAnimationFinished(final Runnable runnable) {
        this.mNotificationStackScroller.runAfterAnimationFinished(runnable);
    }
    
    public void setActivatedChild(final ActivatableNotificationView activatedChild) {
        this.mNotificationStackScroller.setActivatedChild(activatedChild);
    }
    
    public void setAlpha(final float alpha) {
        this.mView.setAlpha(alpha);
    }
    
    public void setAmbientIndicationBottomPadding(final int mAmbientIndicationBottomPadding) {
        if (this.mAmbientIndicationBottomPadding != mAmbientIndicationBottomPadding) {
            this.mAmbientIndicationBottomPadding = mAmbientIndicationBottomPadding;
            super.mStatusBar.updateKeyguardMaxNotifications();
        }
    }
    
    public void setDozing(final boolean b, final boolean b2, final PointF pointF) {
        if (b == this.mDozing) {
            return;
        }
        this.mView.setDozing(b);
        this.mDozing = b;
        this.mNotificationStackScroller.setDozing(b, b2, pointF);
        super.mKeyguardBottomArea.setDozing(this.mDozing, b2);
        if (b) {
            this.mBottomAreaShadeAlphaAnimator.cancel();
        }
        final int mBarState = this.mBarState;
        if (mBarState == 1 || mBarState == 2) {
            this.updateDozingVisibilities(b2);
        }
        float n;
        if (b) {
            n = 1.0f;
        }
        else {
            n = 0.0f;
        }
        super.mStatusBarStateController.setDozeAmount(n, b2);
    }
    
    public void setEmptyDragAmount(final float emptyDragAmount) {
        this.mExpansionCallback.setEmptyDragAmount(emptyDragAmount);
    }
    
    public void setHeadsUpAnimatingAway(final boolean b) {
        this.mHeadsUpAnimatingAway = b;
        this.mNotificationStackScroller.setHeadsUpAnimatingAway(b);
        this.updateHeadsUpVisibility();
    }
    
    public void setHeadsUpAppearanceController(final HeadsUpAppearanceController mHeadsUpAppearanceController) {
        this.mHeadsUpAppearanceController = mHeadsUpAppearanceController;
    }
    
    @Override
    public void setHeadsUpManager(final HeadsUpManagerPhone headsUpManager) {
        super.setHeadsUpManager(headsUpManager);
        this.mHeadsUpTouchHelper = new HeadsUpTouchHelper(headsUpManager, this.mNotificationStackScroller.getHeadsUpCallback(), this);
    }
    
    protected void setHorizontalPanelTranslation(final float n) {
        this.mNotificationStackScroller.setTranslationX(n);
        this.mQsFrame.setTranslationX(n);
        for (int size = this.mVerticalTranslationListener.size(), i = 0; i < size; ++i) {
            this.mVerticalTranslationListener.get(i).run();
        }
    }
    
    public void setKeyguardIndicationController(final KeyguardIndicationController mKeyguardIndicationController) {
        (this.mKeyguardIndicationController = mKeyguardIndicationController).setIndicationArea((ViewGroup)super.mKeyguardBottomArea);
    }
    
    public void setKeyguardUserSwitcher(final KeyguardUserSwitcher mKeyguardUserSwitcher) {
        this.mKeyguardUserSwitcher = mKeyguardUserSwitcher;
    }
    
    public void setLaunchAffordanceListener(final Consumer<Boolean> mAffordanceLaunchListener) {
        this.mAffordanceLaunchListener = mAffordanceLaunchListener;
    }
    
    public void setLaunchTransitionEndRunnable(final Runnable mLaunchAnimationEndRunnable) {
        this.mLaunchAnimationEndRunnable = mLaunchAnimationEndRunnable;
    }
    
    public void setOnReinflationListener(final Runnable mOnReinflationListener) {
        this.mOnReinflationListener = mOnReinflationListener;
    }
    
    @Override
    protected void setOverExpansion(final float n, final boolean b) {
        if (!this.mConflictingQsExpansionGesture) {
            if (!this.mQsExpandImmediate) {
                if (this.mBarState != 1) {
                    this.mNotificationStackScroller.setOnHeightChangedListener(null);
                    if (b) {
                        this.mNotificationStackScroller.setOverScrolledPixels(n, true, false);
                    }
                    else {
                        this.mNotificationStackScroller.setOverScrollAmount(n, true, false);
                    }
                    this.mNotificationStackScroller.setOnHeightChangedListener(this.mOnHeightChangedListener);
                }
            }
        }
    }
    
    public void setPanelAlpha(final int mPanelAlpha, final boolean b) {
        if (this.mPanelAlpha != mPanelAlpha) {
            this.mPanelAlpha = mPanelAlpha;
            final NotificationPanelView mView = this.mView;
            final AnimatableProperty mPanelAlphaAnimator = this.mPanelAlphaAnimator;
            final float n = (float)mPanelAlpha;
            AnimationProperties animationProperties;
            if (mPanelAlpha == 255) {
                animationProperties = this.mPanelAlphaInPropertiesAnimator;
            }
            else {
                animationProperties = this.mPanelAlphaOutPropertiesAnimator;
            }
            PropertyAnimator.setProperty(mView, mPanelAlphaAnimator, n, animationProperties, b);
        }
    }
    
    public void setPanelAlphaEndAction(final Runnable mPanelAlphaEndAction) {
        this.mPanelAlphaEndAction = mPanelAlphaEndAction;
    }
    
    public void setPanelScrimMinFraction(final float n) {
        super.mBar.panelScrimMinFractionChanged(n);
    }
    
    public void setPulsing(final boolean b) {
        this.mPulsing = b;
        final boolean b2 = !this.mDozeParameters.getDisplayNeedsBlanking() && this.mDozeParameters.getAlwaysOn();
        if (b2) {
            this.mAnimateNextPositionUpdate = true;
        }
        if (!this.mPulsing && !this.mDozing) {
            this.mAnimateNextPositionUpdate = false;
        }
        this.mNotificationStackScroller.setPulsing(b, b2);
        this.mKeyguardStatusView.setPulsing(b);
    }
    
    public void setQsExpansionEnabled(final boolean b) {
        this.mQsExpansionEnabled = b;
        final QS mQs = this.mQs;
        if (mQs == null) {
            return;
        }
        mQs.setHeaderClickable(b);
    }
    
    public void setQsScrimEnabled(final boolean mQsScrimEnabled) {
        final boolean b = this.mQsScrimEnabled != mQsScrimEnabled;
        this.mQsScrimEnabled = mQsScrimEnabled;
        if (b) {
            this.updateQsState();
        }
    }
    
    public void setStatusAccessibilityImportance(final int importantForAccessibility) {
        this.mKeyguardStatusView.setImportantForAccessibility(importantForAccessibility);
    }
    
    @Override
    public void setTouchAndAnimationDisabled(final boolean touchAndAnimationDisabled) {
        super.setTouchAndAnimationDisabled(touchAndAnimationDisabled);
        if (touchAndAnimationDisabled && this.mAffordanceHelper.isSwipingInProgress() && !this.mIsLaunchTransitionRunning) {
            this.mAffordanceHelper.reset(false);
        }
        this.mNotificationStackScroller.setAnimationsEnabled(touchAndAnimationDisabled ^ true);
    }
    
    public void setTrackedHeadsUp(final ExpandableNotificationRow expandableNotificationRow) {
        if (expandableNotificationRow != null) {
            this.notifyListenersTrackingHeadsUp(expandableNotificationRow);
            this.mExpandingFromHeadsUp = true;
        }
    }
    
    public void setUserSetupComplete(final boolean b) {
        this.mUserSetupComplete = b;
        super.mKeyguardBottomArea.setUserSetupComplete(b);
    }
    
    @Override
    protected boolean shouldExpandWhenNotFlinging() {
        final boolean shouldExpandWhenNotFlinging = super.shouldExpandWhenNotFlinging();
        boolean b = true;
        if (shouldExpandWhenNotFlinging) {
            return true;
        }
        if (this.mAllowExpandForSmallExpansion) {
            if (SystemClock.uptimeMillis() - super.mDownTime > 300L) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    @Override
    protected boolean shouldGestureIgnoreXTouchSlop(final float n, final float n2) {
        return this.mAffordanceHelper.isOnAffordanceIcon(n, n2) ^ true;
    }
    
    @Override
    protected boolean shouldGestureWaitForTouchSlop() {
        final boolean mExpectingSynthesizedDown = this.mExpectingSynthesizedDown;
        boolean b = false;
        if (mExpectingSynthesizedDown) {
            return this.mExpectingSynthesizedDown = false;
        }
        if (this.isFullyCollapsed() || this.mBarState != 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    protected boolean shouldUseDismissingAnimation() {
        return this.mBarState != 0 && (super.mKeyguardStateController.canDismissLockScreen() || !this.isTracking());
    }
    
    public void showEmptyShadeView(final boolean mShowEmptyShadeView) {
        this.mShowEmptyShadeView = mShowEmptyShadeView;
        this.updateEmptyShadeView();
    }
    
    public void showTransientIndication(final int n) {
        this.mKeyguardIndicationController.showTransientIndication(n);
    }
    
    @Override
    protected void startUnlockHintAnimation() {
        if (this.mPowerManager.isPowerSaveMode()) {
            this.onUnlockHintStarted();
            this.onUnlockHintFinished();
            return;
        }
        super.startUnlockHintAnimation();
    }
    
    public void startWaitingForOpenPanelGesture() {
        if (!this.isFullyCollapsed()) {
            return;
        }
        this.mExpectingSynthesizedDown = true;
        this.onTrackingStarted();
        this.updatePanelExpanded();
    }
    
    public void stopWaitingForOpenPanelGesture(float n) {
        if (this.mExpectingSynthesizedDown) {
            this.mExpectingSynthesizedDown = false;
            this.maybeVibrateOnOpening();
            if (n > 1.0f) {
                n *= 1000.0f;
            }
            else {
                n = 0.0f;
            }
            this.fling(n, true);
            this.onTrackingStopped(false);
        }
    }
    
    protected void updateExpandedHeight(final float n) {
        if (super.mTracking) {
            this.mNotificationStackScroller.setExpandingVelocity(this.getCurrentExpandVelocity());
        }
        float expandedHeight = n;
        if (this.mKeyguardBypassController.getBypassEnabled()) {
            expandedHeight = n;
            if (this.isOnKeyguard()) {
                expandedHeight = (float)this.getMaxPanelHeightNonBypass();
            }
        }
        this.mNotificationStackScroller.setExpandedHeight(expandedHeight);
        this.updateKeyguardBottomAreaAlpha();
        this.updateBigClockAlpha();
        this.updateStatusBarIcons();
    }
    
    public void updateNotificationViews() {
        this.mNotificationStackScroller.updateSectionBoundaries();
        this.mNotificationStackScroller.updateSpeedBumpIndex();
        this.mNotificationStackScroller.updateFooter();
        this.updateShowEmptyShadeView();
        this.mNotificationStackScroller.updateIconAreaViews();
    }
    
    protected void updateQsExpansion() {
        if (this.mQs == null) {
            return;
        }
        final float qsExpansionFraction = this.getQsExpansionFraction();
        this.mQs.setQsExpansion(qsExpansionFraction, this.getHeaderTranslation());
        this.mQs.getDesiredHeight();
        this.mQs.getQsMinExpansionHeight();
        this.mNotificationStackScroller.setQsExpansionFraction(qsExpansionFraction);
    }
    
    public void updateResources() {
        final int dimensionPixelSize = super.mResources.getDimensionPixelSize(R$dimen.qs_panel_width);
        final int integer = super.mResources.getInteger(R$integer.notification_panel_layout_gravity);
        final FrameLayout$LayoutParams layoutParams = (FrameLayout$LayoutParams)this.mQsFrame.getLayoutParams();
        if (layoutParams.width != dimensionPixelSize || layoutParams.gravity != integer) {
            layoutParams.width = dimensionPixelSize;
            layoutParams.gravity = integer;
            this.mQsFrame.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        }
        final int dimensionPixelSize2 = super.mResources.getDimensionPixelSize(R$dimen.notification_panel_width);
        final FrameLayout$LayoutParams layoutParams2 = (FrameLayout$LayoutParams)this.mNotificationStackScroller.getLayoutParams();
        if (layoutParams2.width != dimensionPixelSize2 || layoutParams2.gravity != integer) {
            layoutParams2.width = dimensionPixelSize2;
            layoutParams2.gravity = integer;
            this.mNotificationStackScroller.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
        }
    }
    
    protected void updateVerticalPanelPosition(final float n) {
        if (this.mNotificationStackScroller.getWidth() * 1.75f > this.mView.getWidth()) {
            this.resetHorizontalPanelPosition();
            return;
        }
        final float a = (float)(this.mPositionMinSideMargin + this.mNotificationStackScroller.getWidth() / 2);
        final float a2 = (float)(this.mView.getWidth() - this.mPositionMinSideMargin - this.mNotificationStackScroller.getWidth() / 2);
        float b = n;
        if (Math.abs(n - this.mView.getWidth() / 2) < this.mNotificationStackScroller.getWidth() / 4) {
            b = (float)(this.mView.getWidth() / 2);
        }
        this.setHorizontalPanelTranslation(Math.min(a2, Math.max(a, b)) - (this.mNotificationStackScroller.getLeft() + this.mNotificationStackScroller.getWidth() / 2));
    }
    
    private class ConfigurationListener implements ConfigurationController.ConfigurationListener
    {
        @Override
        public void onDensityOrFontScaleChanged() {
            NotificationPanelViewController.this.updateShowEmptyShadeView();
        }
        
        @Override
        public void onOverlayChanged() {
            NotificationPanelViewController.this.reInflateViews();
        }
        
        @Override
        public void onThemeChanged() {
            final int themeResId = NotificationPanelViewController.this.mView.getContext().getThemeResId();
            if (NotificationPanelViewController.this.mThemeResId == themeResId) {
                return;
            }
            NotificationPanelViewController.this.mThemeResId = themeResId;
            NotificationPanelViewController.this.reInflateViews();
        }
        
        @Override
        public void onUiModeChanged() {
        }
    }
    
    private class DynamicPrivacyControlListener implements Listener
    {
        @Override
        public void onDynamicPrivacyChanged() {
            if (NotificationPanelViewController.this.mLinearDarkAmount != 0.0f) {
                return;
            }
            NotificationPanelViewController.this.mAnimateNextPositionUpdate = true;
        }
    }
    
    private class ExpansionCallback implements PulseExpansionHandler.ExpansionCallback
    {
        @Override
        public void setEmptyDragAmount(final float n) {
            NotificationPanelViewController.this.mEmptyDragAmount = n * 0.2f;
            NotificationPanelViewController.this.positionClockAndNotifications();
        }
    }
    
    private class HeightListener implements QS.HeightListener
    {
        @Override
        public void onQsHeightChanged() {
            final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
            int desiredHeight;
            if (this$0.mQs != null) {
                desiredHeight = NotificationPanelViewController.this.mQs.getDesiredHeight();
            }
            else {
                desiredHeight = 0;
            }
            this$0.mQsMaxExpansionHeight = desiredHeight;
            if (NotificationPanelViewController.this.mQsExpanded && NotificationPanelViewController.this.mQsFullyExpanded) {
                final NotificationPanelViewController this$2 = NotificationPanelViewController.this;
                this$2.mQsExpansionHeight = (float)this$2.mQsMaxExpansionHeight;
                NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                NotificationPanelViewController.this.requestPanelHeightUpdate();
            }
            if (NotificationPanelViewController.this.mAccessibilityManager.isEnabled()) {
                NotificationPanelViewController.this.mView.setAccessibilityPaneTitle((CharSequence)NotificationPanelViewController.this.determineAccessibilityPaneTitle());
            }
            NotificationPanelViewController.this.mNotificationStackScroller.setMaxTopPadding(NotificationPanelViewController.this.mQsMaxExpansionHeight + NotificationPanelViewController.this.mQsNotificationTopPadding);
        }
    }
    
    private class KeyguardAffordanceHelperCallback implements Callback
    {
        @Override
        public float getAffordanceFalsingFactor() {
            float n;
            if (NotificationPanelViewController.this.mStatusBar.isWakeUpComingFromTouch()) {
                n = 1.5f;
            }
            else {
                n = 1.0f;
            }
            return n;
        }
        
        @Override
        public KeyguardAffordanceView getLeftIcon() {
            KeyguardAffordanceView keyguardAffordanceView;
            if (NotificationPanelViewController.this.mView.getLayoutDirection() == 1) {
                keyguardAffordanceView = NotificationPanelViewController.this.mKeyguardBottomArea.getRightView();
            }
            else {
                keyguardAffordanceView = NotificationPanelViewController.this.mKeyguardBottomArea.getLeftView();
            }
            return keyguardAffordanceView;
        }
        
        @Override
        public View getLeftPreview() {
            View view;
            if (NotificationPanelViewController.this.mView.getLayoutDirection() == 1) {
                view = NotificationPanelViewController.this.mKeyguardBottomArea.getRightPreview();
            }
            else {
                view = NotificationPanelViewController.this.mKeyguardBottomArea.getLeftPreview();
            }
            return view;
        }
        
        @Override
        public float getMaxTranslationDistance() {
            return (float)Math.hypot(NotificationPanelViewController.this.mView.getWidth(), NotificationPanelViewController.this.getHeight());
        }
        
        @Override
        public KeyguardAffordanceView getRightIcon() {
            KeyguardAffordanceView keyguardAffordanceView;
            if (NotificationPanelViewController.this.mView.getLayoutDirection() == 1) {
                keyguardAffordanceView = NotificationPanelViewController.this.mKeyguardBottomArea.getLeftView();
            }
            else {
                keyguardAffordanceView = NotificationPanelViewController.this.mKeyguardBottomArea.getRightView();
            }
            return keyguardAffordanceView;
        }
        
        @Override
        public View getRightPreview() {
            View view;
            if (NotificationPanelViewController.this.mView.getLayoutDirection() == 1) {
                view = NotificationPanelViewController.this.mKeyguardBottomArea.getLeftPreview();
            }
            else {
                view = NotificationPanelViewController.this.mKeyguardBottomArea.getRightPreview();
            }
            return view;
        }
        
        @Override
        public boolean needsAntiFalsing() {
            final int access$1400 = NotificationPanelViewController.this.mBarState;
            boolean b = true;
            if (access$1400 != 1) {
                b = false;
            }
            return b;
        }
        
        @Override
        public void onAnimationToSideEnded() {
            NotificationPanelViewController.this.mIsLaunchTransitionRunning = false;
            NotificationPanelViewController.this.mIsLaunchTransitionFinished = true;
            if (NotificationPanelViewController.this.mLaunchAnimationEndRunnable != null) {
                NotificationPanelViewController.this.mLaunchAnimationEndRunnable.run();
                NotificationPanelViewController.this.mLaunchAnimationEndRunnable = null;
            }
            NotificationPanelViewController.this.mStatusBar.readyForKeyguardDone();
        }
        
        @Override
        public void onAnimationToSideStarted(boolean b, final float n, final float n2) {
            if (NotificationPanelViewController.this.mView.getLayoutDirection() != 1) {
                if (!b) {
                    b = true;
                }
                else {
                    b = false;
                }
            }
            NotificationPanelViewController.this.mIsLaunchTransitionRunning = true;
            NotificationPanelViewController.this.mLaunchAnimationEndRunnable = null;
            final float displayDensity = NotificationPanelViewController.this.mStatusBar.getDisplayDensity();
            final int abs = Math.abs((int)(n / displayDensity));
            final int abs2 = Math.abs((int)(n2 / displayDensity));
            if (b) {
                NotificationPanelViewController.this.mLockscreenGestureLogger.write(190, abs, abs2);
                NotificationPanelViewController.this.mFalsingManager.onLeftAffordanceOn();
                if (NotificationPanelViewController.this.mFalsingManager.shouldEnforceBouncer()) {
                    NotificationPanelViewController.this.mStatusBar.executeRunnableDismissingKeyguard(new _$$Lambda$NotificationPanelViewController$KeyguardAffordanceHelperCallback$bICF6GSeDLbT7pBsxqsaY2Sd_4Y(this), null, true, false, true);
                }
                else {
                    NotificationPanelViewController.this.mKeyguardBottomArea.launchLeftAffordance();
                }
            }
            else {
                if ("lockscreen_affordance".equals(NotificationPanelViewController.this.mLastCameraLaunchSource)) {
                    NotificationPanelViewController.this.mLockscreenGestureLogger.write(189, abs, abs2);
                }
                NotificationPanelViewController.this.mFalsingManager.onCameraOn();
                if (NotificationPanelViewController.this.mFalsingManager.shouldEnforceBouncer()) {
                    NotificationPanelViewController.this.mStatusBar.executeRunnableDismissingKeyguard(new _$$Lambda$NotificationPanelViewController$KeyguardAffordanceHelperCallback$R1jytGxMXTpJHS829GYzcUGilW8(this), null, true, false, true);
                }
                else {
                    final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
                    this$0.mKeyguardBottomArea.launchCamera(this$0.mLastCameraLaunchSource);
                }
            }
            NotificationPanelViewController.this.mStatusBar.startLaunchTransitionTimeout();
            NotificationPanelViewController.this.mBlockTouches = true;
        }
        
        @Override
        public void onIconClicked(final boolean b) {
            final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
            if (this$0.mHintAnimationRunning) {
                return;
            }
            this$0.mHintAnimationRunning = true;
            this$0.mAffordanceHelper.startHintAnimation(b, new _$$Lambda$NotificationPanelViewController$KeyguardAffordanceHelperCallback$sp6x_JxGl2c39DMqfoi0x_FWE1U(this));
            boolean b2 = b;
            if (NotificationPanelViewController.this.mView.getLayoutDirection() == 1) {
                b2 = !b;
            }
            if (b2) {
                NotificationPanelViewController.this.mStatusBar.onCameraHintStarted();
            }
            else if (NotificationPanelViewController.this.mKeyguardBottomArea.isLeftVoiceAssist()) {
                NotificationPanelViewController.this.mStatusBar.onVoiceAssistHintStarted();
            }
            else {
                NotificationPanelViewController.this.mStatusBar.onPhoneHintStarted();
            }
        }
        
        @Override
        public void onSwipingAborted() {
            NotificationPanelViewController.this.mFalsingManager.onAffordanceSwipingAborted();
            NotificationPanelViewController.this.mKeyguardBottomArea.unbindCameraPrewarmService(false);
        }
        
        @Override
        public void onSwipingStarted(final boolean b) {
            NotificationPanelViewController.this.mFalsingManager.onAffordanceSwipingStarted(b);
            boolean b2 = b;
            if (NotificationPanelViewController.this.mView.getLayoutDirection() == 1) {
                b2 = !b;
            }
            if (b2) {
                NotificationPanelViewController.this.mKeyguardBottomArea.bindCameraPrewarmService();
            }
            NotificationPanelViewController.this.mView.requestDisallowInterceptTouchEvent(true);
            NotificationPanelViewController.this.mOnlyAffordanceInThisMotion = true;
            NotificationPanelViewController.this.mQsTracking = false;
        }
    }
    
    private class MyOnHeadsUpChangedListener implements OnHeadsUpChangedListener
    {
        @Override
        public void onHeadsUpPinned(final NotificationEntry notificationEntry) {
            if (!NotificationPanelViewController.this.isOnKeyguard()) {
                NotificationPanelViewController.this.mNotificationStackScroller.generateHeadsUpAnimation(notificationEntry.getHeadsUpAnimationView(), true);
            }
        }
        
        @Override
        public void onHeadsUpPinnedModeChanged(final boolean inHeadsUpPinnedMode) {
            NotificationPanelViewController.this.mNotificationStackScroller.setInHeadsUpPinnedMode(inHeadsUpPinnedMode);
            if (inHeadsUpPinnedMode) {
                NotificationPanelViewController.this.mHeadsUpExistenceChangedRunnable.run();
                NotificationPanelViewController.this.updateNotificationTranslucency();
            }
            else {
                NotificationPanelViewController.this.setHeadsUpAnimatingAway(true);
                NotificationPanelViewController.this.mNotificationStackScroller.runAfterAnimationFinished(NotificationPanelViewController.this.mHeadsUpExistenceChangedRunnable);
            }
            NotificationPanelViewController.this.updateGestureExclusionRect();
            NotificationPanelViewController.this.mHeadsUpPinnedMode = inHeadsUpPinnedMode;
            NotificationPanelViewController.this.updateHeadsUpVisibility();
            NotificationPanelViewController.this.updateKeyguardStatusBarForHeadsUp();
        }
        
        @Override
        public void onHeadsUpStateChanged(final NotificationEntry notificationEntry, final boolean b) {
            NotificationPanelViewController.this.mNotificationStackScroller.generateHeadsUpAnimation(notificationEntry, b);
        }
        
        @Override
        public void onHeadsUpUnPinned(final NotificationEntry notificationEntry) {
            if (NotificationPanelViewController.this.isFullyCollapsed() && notificationEntry.isRowHeadsUp() && !NotificationPanelViewController.this.isOnKeyguard()) {
                NotificationPanelViewController.this.mNotificationStackScroller.generateHeadsUpAnimation(notificationEntry.getHeadsUpAnimationView(), false);
                notificationEntry.setHeadsUpIsVisible();
            }
        }
    }
    
    private class OnApplyWindowInsetsListener implements View$OnApplyWindowInsetsListener
    {
        public WindowInsets onApplyWindowInsets(final View view, final WindowInsets windowInsets) {
            NotificationPanelViewController.this.mNavigationBarBottomHeight = windowInsets.getStableInsetBottom();
            NotificationPanelViewController.this.updateMaxHeadsUpTranslation();
            return windowInsets;
        }
    }
    
    private class OnAttachStateChangeListener implements View$OnAttachStateChangeListener
    {
        public void onViewAttachedToWindow(final View view) {
            FragmentHostManager.get((View)NotificationPanelViewController.this.mView).addTagListener("QS", NotificationPanelViewController.this.mFragmentListener);
            final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
            this$0.mStatusBarStateController.addCallback((StatusBarStateController.StateListener)this$0.mStatusBarStateListener);
            NotificationPanelViewController.this.mZenModeController.addCallback((ZenModeController.Callback)NotificationPanelViewController.this.mZenModeControllerCallback);
            NotificationPanelViewController.this.mConfigurationController.addCallback((ConfigurationController.ConfigurationListener)NotificationPanelViewController.this.mConfigurationListener);
            NotificationPanelViewController.this.mUpdateMonitor.registerCallback(NotificationPanelViewController.this.mKeyguardUpdateCallback);
            NotificationPanelViewController.this.mConfigurationListener.onThemeChanged();
        }
        
        public void onViewDetachedFromWindow(final View view) {
            FragmentHostManager.get((View)NotificationPanelViewController.this.mView).removeTagListener("QS", NotificationPanelViewController.this.mFragmentListener);
            final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
            this$0.mStatusBarStateController.removeCallback((StatusBarStateController.StateListener)this$0.mStatusBarStateListener);
            NotificationPanelViewController.this.mZenModeController.removeCallback((ZenModeController.Callback)NotificationPanelViewController.this.mZenModeControllerCallback);
            NotificationPanelViewController.this.mConfigurationController.removeCallback((ConfigurationController.ConfigurationListener)NotificationPanelViewController.this.mConfigurationListener);
            NotificationPanelViewController.this.mUpdateMonitor.removeCallback(NotificationPanelViewController.this.mKeyguardUpdateCallback);
        }
    }
    
    private class OnClickListener implements View$OnClickListener
    {
        public void onClick(final View view) {
            NotificationPanelViewController.this.onQsExpansionStarted();
            if (NotificationPanelViewController.this.mQsExpanded) {
                NotificationPanelViewController.this.flingSettings(0.0f, 1, null, true);
            }
            else if (NotificationPanelViewController.this.mQsExpansionEnabled) {
                NotificationPanelViewController.this.mLockscreenGestureLogger.write(195, 0, 0);
                NotificationPanelViewController.this.flingSettings(0.0f, 0, null, true);
            }
        }
    }
    
    private class OnConfigurationChangedListener extends PanelViewController.OnConfigurationChangedListener
    {
        @Override
        public void onConfigurationChanged(final Configuration configuration) {
            super.onConfigurationChanged(configuration);
            NotificationPanelViewController.this.mAffordanceHelper.onConfigurationChanged();
            if (configuration.orientation != NotificationPanelViewController.this.mLastOrientation) {
                NotificationPanelViewController.this.resetHorizontalPanelPosition();
            }
            NotificationPanelViewController.this.mLastOrientation = configuration.orientation;
        }
    }
    
    private class OnEmptySpaceClickListener implements NotificationStackScrollLayout.OnEmptySpaceClickListener
    {
        @Override
        public void onEmptySpaceClicked(final float n, final float n2) {
            NotificationPanelViewController.this.onEmptySpaceClick(n);
        }
    }
    
    private class OnHeightChangedListener implements ExpandableView.OnHeightChangedListener
    {
        @Override
        public void onHeightChanged(final ExpandableView expandableView, final boolean b) {
            if (expandableView == null && NotificationPanelViewController.this.mQsExpanded) {
                return;
            }
            if (b && NotificationPanelViewController.this.mInterpolatedDarkAmount == 0.0f) {
                NotificationPanelViewController.this.mAnimateNextPositionUpdate = true;
            }
            final ExpandableView firstChildNotGone = NotificationPanelViewController.this.mNotificationStackScroller.getFirstChildNotGone();
            ExpandableNotificationRow expandableNotificationRow;
            if (firstChildNotGone instanceof ExpandableNotificationRow) {
                expandableNotificationRow = (ExpandableNotificationRow)firstChildNotGone;
            }
            else {
                expandableNotificationRow = null;
            }
            if (expandableNotificationRow != null && (expandableView == expandableNotificationRow || expandableNotificationRow.getNotificationParent() == expandableNotificationRow)) {
                NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
            }
            NotificationPanelViewController.this.requestPanelHeightUpdate();
        }
        
        @Override
        public void onReset(final ExpandableView expandableView) {
        }
    }
    
    private class OnLayoutChangeListener extends PanelViewController.OnLayoutChangeListener
    {
        @Override
        public void onLayoutChange(final View view, int qsMinExpansionHeight, int access$8600, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            DejankUtils.startDetectingBlockingIpcs("NVP#onLayout");
            super.onLayoutChange(view, qsMinExpansionHeight, access$8600, n, n2, n3, n4, n5, n6);
            final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
            this$0.setIsFullWidth(this$0.mNotificationStackScroller.getWidth() == NotificationPanelViewController.this.mView.getWidth());
            NotificationPanelViewController.this.mKeyguardStatusView.setPivotX((float)(NotificationPanelViewController.this.mView.getWidth() / 2));
            NotificationPanelViewController.this.mKeyguardStatusView.setPivotY(NotificationPanelViewController.this.mKeyguardStatusView.getClockTextSize() * 0.34521484f);
            access$8600 = NotificationPanelViewController.this.mQsMaxExpansionHeight;
            if (NotificationPanelViewController.this.mQs != null) {
                final NotificationPanelViewController this$2 = NotificationPanelViewController.this;
                if (this$2.mKeyguardShowing) {
                    qsMinExpansionHeight = 0;
                }
                else {
                    qsMinExpansionHeight = NotificationPanelViewController.this.mQs.getQsMinExpansionHeight();
                }
                this$2.mQsMinExpansionHeight = qsMinExpansionHeight;
                final NotificationPanelViewController this$3 = NotificationPanelViewController.this;
                this$3.mQsMaxExpansionHeight = this$3.mQs.getDesiredHeight();
                NotificationPanelViewController.this.mNotificationStackScroller.setMaxTopPadding(NotificationPanelViewController.this.mQsMaxExpansionHeight + NotificationPanelViewController.this.mQsNotificationTopPadding);
            }
            NotificationPanelViewController.this.positionClockAndNotifications();
            if (NotificationPanelViewController.this.mQsExpanded && NotificationPanelViewController.this.mQsFullyExpanded) {
                final NotificationPanelViewController this$4 = NotificationPanelViewController.this;
                this$4.mQsExpansionHeight = (float)this$4.mQsMaxExpansionHeight;
                NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                NotificationPanelViewController.this.requestPanelHeightUpdate();
                if (NotificationPanelViewController.this.mQsMaxExpansionHeight != access$8600) {
                    final NotificationPanelViewController this$5 = NotificationPanelViewController.this;
                    this$5.startQsSizeChangeAnimation(access$8600, this$5.mQsMaxExpansionHeight);
                }
            }
            else if (!NotificationPanelViewController.this.mQsExpanded) {
                final NotificationPanelViewController this$6 = NotificationPanelViewController.this;
                this$6.setQsExpansion(this$6.mQsMinExpansionHeight + NotificationPanelViewController.this.mLastOverscroll);
            }
            final NotificationPanelViewController this$7 = NotificationPanelViewController.this;
            this$7.updateExpandedHeight(this$7.getExpandedHeight());
            NotificationPanelViewController.this.updateHeader();
            if (NotificationPanelViewController.this.mQsSizeChangeAnimator == null && NotificationPanelViewController.this.mQs != null) {
                NotificationPanelViewController.this.mQs.setHeightOverride(NotificationPanelViewController.this.mQs.getDesiredHeight());
            }
            NotificationPanelViewController.this.updateMaxHeadsUpTranslation();
            NotificationPanelViewController.this.updateGestureExclusionRect();
            if (NotificationPanelViewController.this.mExpandAfterLayoutRunnable != null) {
                NotificationPanelViewController.this.mExpandAfterLayoutRunnable.run();
                NotificationPanelViewController.this.mExpandAfterLayoutRunnable = null;
            }
            DejankUtils.stopDetectingBlockingIpcs("NVP#onLayout");
        }
    }
    
    private class OnOverscrollTopChangedListener implements NotificationStackScrollLayout.OnOverscrollTopChangedListener
    {
        @Override
        public void flingTopOverscroll(final float n, final boolean b) {
            NotificationPanelViewController.this.mLastOverscroll = 0.0f;
            NotificationPanelViewController.this.mQsExpansionFromOverscroll = false;
            final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
            this$0.setQsExpansion(this$0.mQsExpansionHeight);
            final NotificationPanelViewController this$2 = NotificationPanelViewController.this;
            float n2 = n;
            if (!this$2.mQsExpansionEnabled) {
                n2 = n;
                if (b) {
                    n2 = 0.0f;
                }
            }
            int n3;
            if (b && NotificationPanelViewController.this.mQsExpansionEnabled) {
                n3 = 0;
            }
            else {
                n3 = 1;
            }
            this$2.flingSettings(n2, n3, new _$$Lambda$NotificationPanelViewController$OnOverscrollTopChangedListener$6FaWnl4RjuYk8pBm1fvFXqN0qu8(this), false);
        }
        
        @Override
        public void onOverscrollTopChanged(float n, final boolean b) {
            NotificationPanelViewController.this.cancelQsAnimation();
            if (!NotificationPanelViewController.this.mQsExpansionEnabled) {
                n = 0.0f;
            }
            if (n < 1.0f) {
                n = 0.0f;
            }
            final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
            final float n2 = fcmpl(n, 0.0f);
            final boolean b2 = true;
            this$0.setOverScrolling(n2 != 0 && b);
            NotificationPanelViewController.this.mQsExpansionFromOverscroll = (n2 != 0 && b2);
            NotificationPanelViewController.this.mLastOverscroll = n;
            NotificationPanelViewController.this.updateQsState();
            final NotificationPanelViewController this$2 = NotificationPanelViewController.this;
            this$2.setQsExpansion(this$2.mQsMinExpansionHeight + n);
        }
    }
    
    private class StatusBarStateListener implements StateListener
    {
        @Override
        public void onDozeAmountChanged(final float n, final float n2) {
            NotificationPanelViewController.this.mInterpolatedDarkAmount = n2;
            NotificationPanelViewController.this.mLinearDarkAmount = n;
            NotificationPanelViewController.this.mKeyguardStatusView.setDarkAmount(NotificationPanelViewController.this.mInterpolatedDarkAmount);
            final NotificationPanelViewController this$0 = NotificationPanelViewController.this;
            this$0.mKeyguardBottomArea.setDarkAmount(this$0.mInterpolatedDarkAmount);
            NotificationPanelViewController.this.positionClockAndNotifications();
        }
        
        @Override
        public void onStateChanged(int visibility) {
            final boolean goingToFullShade = NotificationPanelViewController.this.mStatusBarStateController.goingToFullShade();
            final boolean keyguardFadingAway = NotificationPanelViewController.this.mKeyguardStateController.isKeyguardFadingAway();
            final int access$1400 = NotificationPanelViewController.this.mBarState;
            final boolean b = visibility == 1;
            NotificationPanelViewController.this.setKeyguardStatusViewVisibility(visibility, keyguardFadingAway, goingToFullShade);
            NotificationPanelViewController.this.setKeyguardBottomAreaVisibility(visibility, goingToFullShade);
            NotificationPanelViewController.this.mBarState = visibility;
            NotificationPanelViewController.this.mKeyguardShowing = b;
            if (access$1400 == 1 && (goingToFullShade || visibility == 2)) {
                NotificationPanelViewController.this.animateKeyguardStatusBarOut();
                long calculateGoingToFullShadeDelay;
                if (NotificationPanelViewController.this.mBarState == 2) {
                    calculateGoingToFullShadeDelay = 0L;
                }
                else {
                    calculateGoingToFullShadeDelay = NotificationPanelViewController.this.mKeyguardStateController.calculateGoingToFullShadeDelay();
                }
                NotificationPanelViewController.this.mQs.animateHeaderSlidingIn(calculateGoingToFullShadeDelay);
            }
            else if (access$1400 == 2 && visibility == 1) {
                NotificationPanelViewController.this.animateKeyguardStatusBarIn(360L);
                NotificationPanelViewController.this.mNotificationStackScroller.resetScrollPosition();
                if (!NotificationPanelViewController.this.mQsExpanded) {
                    NotificationPanelViewController.this.mQs.animateHeaderSlidingOut();
                }
            }
            else {
                NotificationPanelViewController.this.mKeyguardStatusBar.setAlpha(1.0f);
                final KeyguardStatusBarView access$1401 = NotificationPanelViewController.this.mKeyguardStatusBar;
                if (b) {
                    visibility = 0;
                }
                else {
                    visibility = 4;
                }
                access$1401.setVisibility(visibility);
                if (b && access$1400 != NotificationPanelViewController.this.mBarState && NotificationPanelViewController.this.mQs != null) {
                    NotificationPanelViewController.this.mQs.hideImmediately();
                }
            }
            NotificationPanelViewController.this.updateKeyguardStatusBarForHeadsUp();
            if (b) {
                NotificationPanelViewController.this.updateDozingVisibilities(false);
            }
            NotificationPanelViewController.this.updateQSPulseExpansion();
            NotificationPanelViewController.this.maybeAnimateBottomAreaAlpha();
            NotificationPanelViewController.this.resetHorizontalPanelPosition();
            NotificationPanelViewController.this.updateQsState();
        }
    }
    
    private class ZenModeControllerCallback implements Callback
    {
        @Override
        public void onZenChanged(final int n) {
            NotificationPanelViewController.this.updateShowEmptyShadeView();
        }
    }
}
