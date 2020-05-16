// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import com.android.internal.logging.UiEventLogger$UiEventEnum;
import android.view.animation.AnimationUtils;
import com.android.systemui.R$attr;
import android.view.ContextThemeWrapper;
import com.android.systemui.R$style;
import android.graphics.PointF;
import android.os.Bundle;
import com.android.systemui.statusbar.notification.row.NotificationSnooze;
import android.view.View$MeasureSpec;
import android.widget.ScrollView;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityEvent;
import com.android.systemui.statusbar.notification.VisibilityLocationProvider;
import android.content.res.Configuration;
import android.view.DisplayCutout;
import android.view.WindowInsets;
import android.content.Intent;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import java.util.function.Consumer;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.util.Assert;
import java.util.Collections;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.internal.graphics.ColorUtils;
import java.util.Collection;
import android.service.notification.NotificationListenerService$RankingMap;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import com.android.systemui.statusbar.policy.HeadsUpUtil;
import com.android.settingslib.Utils;
import android.view.ViewConfiguration;
import com.android.systemui.R$string;
import com.android.systemui.R$layout;
import java.util.List;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;
import android.graphics.Canvas;
import java.util.Iterator;
import android.animation.TimeInterpolator;
import android.animation.TimeAnimator;
import android.content.res.Resources;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import java.util.Objects;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.R$color;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import android.view.View$OnClickListener;
import android.view.LayoutInflater;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import android.view.MotionEvent;
import android.graphics.Point;
import android.util.Log;
import com.android.systemui.Interpolators;
import com.android.systemui.Dependency;
import com.android.internal.statusbar.IStatusBarService$Stub;
import android.os.ServiceManager;
import android.graphics.PorterDuff$Mode;
import android.graphics.PorterDuffXfermode;
import android.util.MathUtils;
import android.graphics.Outline;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import java.util.Comparator;
import android.view.VelocityTracker;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationShelf;
import android.widget.OverScroller;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import android.view.ViewOutlineProvider;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import android.view.animation.Interpolator;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.util.Pair;
import com.android.systemui.statusbar.phone.HeadsUpTouchHelper;
import com.android.systemui.statusbar.phone.HeadsUpAppearanceController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.notification.row.FooterView;
import com.android.systemui.statusbar.notification.row.ForegroundServiceDungeonView;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.plugins.FalsingManager;
import java.util.function.BiConsumer;
import com.android.systemui.ExpandHelper;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.EmptyShadeView;
import com.android.systemui.statusbar.DragDownHelper;
import android.util.DisplayMetrics;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.Animator$AnimatorListener;
import android.animation.ValueAnimator;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.internal.statusbar.IStatusBarService;
import android.view.ViewTreeObserver$OnPreDrawListener;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.HashSet;
import android.view.View;
import java.util.ArrayList;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ScrollAdapter;
import android.view.ViewGroup;

public class NotificationStackScrollLayout extends ViewGroup implements ScrollAdapter, NotificationListContainer, ConfigurationListener, Dumpable, Listener
{
    private boolean mActivateNeedsAnimation;
    private int mActivePointerId;
    private ArrayList<View> mAddedHeadsUpChildren;
    private final boolean mAllowLongPress;
    private final AmbientState mAmbientState;
    private boolean mAnimateBottomOnLayout;
    private boolean mAnimateNextBackgroundBottom;
    private boolean mAnimateNextBackgroundTop;
    private boolean mAnimateNextSectionBoundsChange;
    private ArrayList<AnimationEvent> mAnimationEvents;
    private HashSet<Runnable> mAnimationFinishedRunnables;
    private boolean mAnimationRunning;
    private boolean mAnimationsEnabled;
    private final Rect mBackgroundAnimationRect;
    private final Paint mBackgroundPaint;
    private ViewTreeObserver$OnPreDrawListener mBackgroundUpdater;
    private float mBackgroundXFactor;
    private boolean mBackwardScrollable;
    private final IStatusBarService mBarService;
    private int mBgColor;
    private int mBottomInset;
    private int mBottomMargin;
    private int mCachedBackgroundColor;
    private boolean mChangePositionInProgress;
    boolean mCheckForLeavebehind;
    private boolean mChildTransferInProgress;
    private ArrayList<ExpandableView> mChildrenChangingPositions;
    private HashSet<ExpandableView> mChildrenToAddAnimated;
    private ArrayList<ExpandableView> mChildrenToRemoveAnimated;
    private boolean mChildrenUpdateRequested;
    private ViewTreeObserver$OnPreDrawListener mChildrenUpdater;
    protected boolean mClearAllEnabled;
    private HashSet<ExpandableView> mClearTransientViewsWhenFinished;
    private final Rect mClipRect;
    private int mCollapsedSize;
    private final SysuiColorExtractor mColorExtractor;
    private int mContentHeight;
    private boolean mContinuousBackgroundUpdate;
    private boolean mContinuousShadowUpdate;
    private int mCornerRadius;
    private int mCurrentStackHeight;
    private float mDimAmount;
    private ValueAnimator mDimAnimator;
    private final Animator$AnimatorListener mDimEndListener;
    private ValueAnimator$AnimatorUpdateListener mDimUpdateListener;
    private boolean mDimmedNeedsAnimation;
    private boolean mDisallowDismissInThisMotion;
    private boolean mDisallowScrollingInThisMotion;
    private boolean mDismissAllInProgress;
    private boolean mDismissRtl;
    private final DisplayMetrics mDisplayMetrics;
    private boolean mDontClampNextScroll;
    private boolean mDontReportNextOverScroll;
    private int mDownX;
    private final DragDownHelper.DragDownCallback mDragDownCallback;
    private final DynamicPrivacyController mDynamicPrivacyController;
    protected EmptyShadeView mEmptyShadeView;
    private final NotificationEntryManager mEntryManager;
    private boolean mEverythingNeedsAnimation;
    private ExpandHelper mExpandHelper;
    private ExpandHelper.Callback mExpandHelperCallback;
    private ExpandableView mExpandedGroupView;
    private float mExpandedHeight;
    private ArrayList<BiConsumer<Float, Float>> mExpandedHeightListeners;
    private boolean mExpandedInThisMotion;
    private boolean mExpandingNotification;
    private boolean mFadeNotificationsOnDismiss;
    private FalsingManager mFalsingManager;
    private final FeatureFlags mFeatureFlags;
    private final ForegroundServiceSectionController mFgsSectionController;
    private ForegroundServiceDungeonView mFgsSectionView;
    private Runnable mFinishScrollingCallback;
    protected FooterView mFooterView;
    private boolean mForceNoOverlappingRendering;
    private View mForcedScroll;
    private boolean mForwardScrollable;
    private HashSet<View> mFromMoreCardAdditions;
    private boolean mGenerateChildOrderChangedEvent;
    private long mGoToFullShadeDelay;
    private boolean mGoToFullShadeNeedsAnimation;
    private boolean mGroupExpandedForMeasure;
    private NotificationGroupManager mGroupManager;
    private boolean mHeadsUpAnimatingAway;
    private HeadsUpAppearanceController mHeadsUpAppearanceController;
    private final HeadsUpTouchHelper.Callback mHeadsUpCallback;
    private HashSet<Pair<ExpandableNotificationRow, Boolean>> mHeadsUpChangeAnimations;
    private boolean mHeadsUpGoingAwayAnimationsAllowed;
    private int mHeadsUpInset;
    private HeadsUpManagerPhone mHeadsUpManager;
    private boolean mHideSensitiveNeedsAnimation;
    private Interpolator mHideXInterpolator;
    private boolean mHighPriorityBeforeSpeedBump;
    private NotificationIconAreaController mIconAreaController;
    private boolean mInHeadsUpPinnedMode;
    private int mIncreasedPaddingBetweenElements;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private float mInterpolatedHideAmount;
    private int mIntrinsicContentHeight;
    private int mIntrinsicPadding;
    private boolean mIsBeingDragged;
    private boolean mIsClipped;
    private boolean mIsExpanded;
    private boolean mIsExpansionChanging;
    private final KeyguardBypassController mKeyguardBypassController;
    private int mLastMotionY;
    private float mLastSentAppear;
    private float mLastSentExpandedHeight;
    private float mLinearHideAmount;
    private NotificationLogger.OnChildLocationsChangedListener mListener;
    private final LockscreenGestureLogger mLockscreenGestureLogger;
    private final NotificationLockscreenUserManager.UserChangedListener mLockscreenUserChangeListener;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private ExpandableNotificationRow.LongPressListener mLongPressListener;
    private int mMaxDisplayedNotifications;
    private int mMaxLayoutHeight;
    private float mMaxOverScroll;
    private int mMaxScrollAfterExpand;
    private int mMaxTopPadding;
    private int mMaximumVelocity;
    @VisibleForTesting
    protected final NotificationMenuRowPlugin.OnMenuEventListener mMenuEventListener;
    @VisibleForTesting
    protected final MetricsLogger mMetricsLogger;
    private int mMinInteractionHeight;
    private float mMinTopOverScrollToEscape;
    private int mMinimumVelocity;
    private boolean mNeedViewResizeAnimation;
    private boolean mNeedsAnimation;
    private final NotifCollection mNotifCollection;
    private final NotifPipeline mNotifPipeline;
    private final NotificationSwipeHelper.NotificationCallback mNotificationCallback;
    private final NotificationGutsManager mNotificationGutsManager;
    private NotificationPanelViewController mNotificationPanelController;
    private OnEmptySpaceClickListener mOnEmptySpaceClickListener;
    private final NotificationGroupManager.OnGroupChangeListener mOnGroupChangeListener;
    private OnHeightChangedListener mOnHeightChangedListener;
    private boolean mOnlyScrollingInThisMotion;
    private final ViewOutlineProvider mOutlineProvider;
    private float mOverScrolledBottomPixels;
    private float mOverScrolledTopPixels;
    private int mOverflingDistance;
    private OnOverscrollTopChangedListener mOverscrollTopChangedListener;
    private int mOwnScrollY;
    private int mPaddingBetweenElements;
    private boolean mPanelTracking;
    private boolean mPulsing;
    protected ViewGroup mQsContainer;
    private boolean mQsExpanded;
    private float mQsExpansionFraction;
    private Runnable mReclamp;
    private Runnable mReflingAndAnimateScroll;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private Rect mRequestedClipBounds;
    private final NotificationRoundnessManager mRoundnessManager;
    private ViewTreeObserver$OnPreDrawListener mRunningAnimationUpdater;
    private ScrimController mScrimController;
    private boolean mScrollable;
    private boolean mScrolledToTopOnFirstDown;
    private OverScroller mScroller;
    protected boolean mScrollingEnabled;
    private NotificationSection[] mSections;
    private final NotificationSectionsManager mSectionsManager;
    private ViewTreeObserver$OnPreDrawListener mShadowUpdater;
    private NotificationShelf mShelf;
    private final boolean mShouldDrawNotificationBackground;
    private boolean mShouldShowShelfOnly;
    private int mSidePaddings;
    private float mSlopMultiplier;
    protected final StackScrollAlgorithm mStackScrollAlgorithm;
    private float mStackTranslation;
    private final StackStateAnimator mStateAnimator;
    private final StatusBarStateController.StateListener mStateListener;
    private StatusBar mStatusBar;
    private int mStatusBarHeight;
    private int mStatusBarState;
    private final SysuiStatusBarStateController mStatusbarStateController;
    private final NotificationSwipeHelper mSwipeHelper;
    private ArrayList<View> mSwipedOutViews;
    private boolean mSwipingInProgress;
    private int[] mTempInt2;
    private final ArrayList<Pair<ExpandableNotificationRow, Boolean>> mTmpList;
    private final Rect mTmpRect;
    private ArrayList<ExpandableView> mTmpSortedChildren;
    private int mTopPadding;
    private boolean mTopPaddingNeedsAnimation;
    private float mTopPaddingOverflow;
    private boolean mTouchIsClick;
    private int mTouchSlop;
    private boolean mTrackingHeadsUp;
    protected final UiEventLogger mUiEventLogger;
    private boolean mUsingLightTheme;
    private VelocityTracker mVelocityTracker;
    private Comparator<ExpandableView> mViewPositionComparator;
    private final VisualStabilityManager mVisualStabilityManager;
    private int mWaterfallTopInset;
    private boolean mWillExpand;
    private final ZenModeController mZenController;
    
    public NotificationStackScrollLayout(final Context context, final AttributeSet set, final boolean mAllowLongPress, final NotificationRoundnessManager mRoundnessManager, final DynamicPrivacyController mDynamicPrivacyController, final SysuiStatusBarStateController mStatusbarStateController, final HeadsUpManagerPhone mHeadsUpManager, final KeyguardBypassController mKeyguardBypassController, final FalsingManager mFalsingManager, final NotificationLockscreenUserManager mLockscreenUserManager, final NotificationGutsManager mNotificationGutsManager, final ZenModeController mZenController, final NotificationSectionsManager mSectionsManager, final ForegroundServiceSectionController mFgsSectionController, final ForegroundServiceDismissalFeatureController foregroundServiceDismissalFeatureController, final FeatureFlags mFeatureFlags, final NotifPipeline mNotifPipeline, final NotificationEntryManager mEntryManager, final NotifCollection mNotifCollection, final UiEventLogger mUiEventLogger) {
        boolean b = false;
        super(context, set, 0, 0);
        this.mCurrentStackHeight = Integer.MAX_VALUE;
        this.mBackgroundPaint = new Paint();
        this.mActivePointerId = -1;
        this.mBottomInset = 0;
        this.mChildrenToAddAnimated = new HashSet<ExpandableView>();
        this.mAddedHeadsUpChildren = new ArrayList<View>();
        this.mChildrenToRemoveAnimated = new ArrayList<ExpandableView>();
        this.mChildrenChangingPositions = new ArrayList<ExpandableView>();
        this.mFromMoreCardAdditions = new HashSet<View>();
        this.mAnimationEvents = new ArrayList<AnimationEvent>();
        this.mSwipedOutViews = new ArrayList<View>();
        this.mStateAnimator = new StackStateAnimator(this);
        this.mIsExpanded = true;
        this.mChildrenUpdater = (ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener() {
            public boolean onPreDraw() {
                NotificationStackScrollLayout.this.updateForcedScroll();
                NotificationStackScrollLayout.this.updateChildren();
                NotificationStackScrollLayout.this.mChildrenUpdateRequested = false;
                NotificationStackScrollLayout.this.getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
                return true;
            }
        };
        this.mLockscreenUserChangeListener = new NotificationLockscreenUserManager.UserChangedListener() {
            @Override
            public void onUserChanged(final int n) {
                NotificationStackScrollLayout.this.updateSensitiveness(false);
            }
        };
        this.mTempInt2 = new int[2];
        this.mAnimationFinishedRunnables = new HashSet<Runnable>();
        this.mClearTransientViewsWhenFinished = new HashSet<ExpandableView>();
        this.mHeadsUpChangeAnimations = new HashSet<Pair<ExpandableNotificationRow, Boolean>>();
        this.mTmpList = new ArrayList<Pair<ExpandableNotificationRow, Boolean>>();
        this.mRunningAnimationUpdater = (ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener() {
            public boolean onPreDraw() {
                NotificationStackScrollLayout.this.onPreDrawDuringAnimation();
                return true;
            }
        };
        this.mTmpSortedChildren = new ArrayList<ExpandableView>();
        this.mDimEndListener = (Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                NotificationStackScrollLayout.this.mDimAnimator = null;
            }
        };
        this.mDimUpdateListener = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                NotificationStackScrollLayout.this.setDimAmount((float)valueAnimator.getAnimatedValue());
            }
        };
        this.mShadowUpdater = (ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener() {
            public boolean onPreDraw() {
                NotificationStackScrollLayout.this.updateViewShadows();
                return true;
            }
        };
        this.mBackgroundUpdater = (ViewTreeObserver$OnPreDrawListener)new _$$Lambda$NotificationStackScrollLayout$Q8bA_VckgKDEBbXIsfAy3cWAYiM(this);
        this.mViewPositionComparator = new Comparator<ExpandableView>() {
            @Override
            public int compare(final ExpandableView expandableView, final ExpandableView expandableView2) {
                final float n = expandableView.getTranslationY() + expandableView.getActualHeight();
                final float n2 = expandableView2.getTranslationY() + expandableView2.getActualHeight();
                if (n < n2) {
                    return -1;
                }
                if (n > n2) {
                    return 1;
                }
                return 0;
            }
        };
        this.mOutlineProvider = new ViewOutlineProvider() {
            public void getOutline(final View view, final Outline outline) {
                if (NotificationStackScrollLayout.this.mAmbientState.isHiddenAtAll()) {
                    outline.setRoundRect(NotificationStackScrollLayout.this.mBackgroundAnimationRect, MathUtils.lerp(NotificationStackScrollLayout.this.mCornerRadius / 2.0f, (float)NotificationStackScrollLayout.this.mCornerRadius, NotificationStackScrollLayout.this.mHideXInterpolator.getInterpolation((1.0f - NotificationStackScrollLayout.this.mLinearHideAmount) * NotificationStackScrollLayout.this.mBackgroundXFactor)));
                    outline.setAlpha(1.0f - NotificationStackScrollLayout.this.mAmbientState.getHideAmount());
                }
                else {
                    ViewOutlineProvider.BACKGROUND.getOutline(view, outline);
                }
            }
        };
        new PorterDuffXfermode(PorterDuff$Mode.SRC);
        this.mInterpolatedHideAmount = 0.0f;
        this.mLinearHideAmount = 0.0f;
        this.mBackgroundXFactor = 1.0f;
        this.mMaxDisplayedNotifications = -1;
        this.mClipRect = new Rect();
        this.mHeadsUpGoingAwayAnimationsAllowed = true;
        this.mReflingAndAnimateScroll = new _$$Lambda$NotificationStackScrollLayout$Dpz6Zg1EwqGyFLQ68KdTUD2Xa_g(this);
        this.mBackgroundAnimationRect = new Rect();
        this.mExpandedHeightListeners = new ArrayList<BiConsumer<Float, Float>>();
        this.mTmpRect = new Rect();
        this.mBarService = IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
        this.mRemoteInputManager = Dependency.get(NotificationRemoteInputManager.class);
        this.mColorExtractor = Dependency.get(SysuiColorExtractor.class);
        this.mDisplayMetrics = Dependency.get(DisplayMetrics.class);
        this.mLockscreenGestureLogger = Dependency.get(LockscreenGestureLogger.class);
        this.mVisualStabilityManager = Dependency.get(VisualStabilityManager.class);
        this.mHideXInterpolator = Interpolators.FAST_OUT_SLOW_IN;
        this.mReclamp = new Runnable() {
            @Override
            public void run() {
                NotificationStackScrollLayout.this.mScroller.startScroll(NotificationStackScrollLayout.this.mScrollX, NotificationStackScrollLayout.this.mOwnScrollY, 0, NotificationStackScrollLayout.this.getScrollRange() - NotificationStackScrollLayout.this.mOwnScrollY);
                NotificationStackScrollLayout.this.mDontReportNextOverScroll = true;
                NotificationStackScrollLayout.this.mDontClampNextScroll = true;
                NotificationStackScrollLayout.this.animateScroll();
            }
        };
        this.mStateListener = new StatusBarStateController.StateListener() {
            @Override
            public void onStateChanged(final int statusBarState) {
                NotificationStackScrollLayout.this.setStatusBarState(statusBarState);
            }
            
            @Override
            public void onStatePostChange() {
                NotificationStackScrollLayout.this.onStatePostChange();
            }
            
            @Override
            public void onStatePreChange(final int n, final int n2) {
                if (n == 2 && n2 == 1) {
                    NotificationStackScrollLayout.this.requestAnimateEverything();
                }
            }
        };
        this.mMenuEventListener = new NotificationMenuRowPlugin.OnMenuEventListener() {
            @Override
            public void onMenuClicked(final View view, final int n, final int n2, final MenuItem menuItem) {
                if (NotificationStackScrollLayout.this.mLongPressListener == null) {
                    return;
                }
                if (view instanceof ExpandableNotificationRow) {
                    NotificationStackScrollLayout.this.mMetricsLogger.write(((ExpandableNotificationRow)view).getEntry().getSbn().getLogMaker().setCategory(333).setType(4));
                }
                NotificationStackScrollLayout.this.mLongPressListener.onLongPress(view, n, n2, menuItem);
            }
            
            @Override
            public void onMenuReset(final View view) {
                final View translatingParentView = NotificationStackScrollLayout.this.mSwipeHelper.getTranslatingParentView();
                if (translatingParentView != null && view == translatingParentView) {
                    NotificationStackScrollLayout.this.mSwipeHelper.clearExposedMenuView();
                    NotificationStackScrollLayout.this.mSwipeHelper.clearTranslatingParentView();
                    if (view instanceof ExpandableNotificationRow) {
                        NotificationStackScrollLayout.this.mHeadsUpManager.setMenuShown(((ExpandableNotificationRow)view).getEntry(), false);
                    }
                }
            }
            
            @Override
            public void onMenuShown(final View view) {
                if (view instanceof ExpandableNotificationRow) {
                    final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
                    NotificationStackScrollLayout.this.mMetricsLogger.write(expandableNotificationRow.getEntry().getSbn().getLogMaker().setCategory(332).setType(4));
                    NotificationStackScrollLayout.this.mHeadsUpManager.setMenuShown(expandableNotificationRow.getEntry(), true);
                    NotificationStackScrollLayout.this.mSwipeHelper.onMenuShown(view);
                    NotificationStackScrollLayout.this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
                    final NotificationMenuRowPlugin provider = expandableNotificationRow.getProvider();
                    if (provider.shouldShowGutsOnSnapOpen()) {
                        final NotificationMenuRowPlugin.MenuItem menuItemToExposeOnSnap = provider.menuItemToExposeOnSnap();
                        if (menuItemToExposeOnSnap != null) {
                            final Point revealAnimationOrigin = provider.getRevealAnimationOrigin();
                            NotificationStackScrollLayout.this.mNotificationGutsManager.openGuts(view, revealAnimationOrigin.x, revealAnimationOrigin.y, menuItemToExposeOnSnap);
                        }
                        else {
                            Log.e("StackScroller", "Provider has shouldShowGutsOnSnapOpen, but provided no menu item in menuItemtoExposeOnSnap. Skipping.");
                        }
                        NotificationStackScrollLayout.this.resetExposedMenuView(false, true);
                    }
                }
            }
        };
        this.mNotificationCallback = new NotificationSwipeHelper.NotificationCallback() {
            @Override
            public boolean canChildBeDismissed(final View view) {
                return canChildBeDismissed(view);
            }
            
            @Override
            public boolean canChildBeDismissedInDirection(final View view, final boolean b) {
                return this.canChildBeDismissed(view);
            }
            
            @Override
            public View getChildAtPosition(final MotionEvent motionEvent) {
                View access$3600;
                final ExpandableView expandableView = (ExpandableView)(access$3600 = (View)NotificationStackScrollLayout.this.getChildAtPosition(motionEvent.getX(), motionEvent.getY(), (boolean)(1 != 0), (boolean)(0 != 0)));
                if (expandableView instanceof ExpandableNotificationRow) {
                    final ExpandableNotificationRow notificationParent = ((ExpandableNotificationRow)expandableView).getNotificationParent();
                    access$3600 = (View)expandableView;
                    if (notificationParent != null) {
                        access$3600 = (View)expandableView;
                        if (notificationParent.areChildrenExpanded()) {
                            if (!notificationParent.areGutsExposed() && NotificationStackScrollLayout.this.mSwipeHelper.getExposedMenuView() != notificationParent) {
                                access$3600 = (View)expandableView;
                                if (notificationParent.getNotificationChildren().size() != 1) {
                                    return access$3600;
                                }
                                access$3600 = (View)expandableView;
                                if (!notificationParent.getEntry().isClearable()) {
                                    return access$3600;
                                }
                            }
                            access$3600 = (View)notificationParent;
                        }
                    }
                }
                return access$3600;
            }
            
            @Override
            public int getConstrainSwipeStartPosition() {
                final NotificationMenuRowPlugin currentMenuRow = NotificationStackScrollLayout.this.mSwipeHelper.getCurrentMenuRow();
                if (currentMenuRow != null) {
                    return Math.abs(currentMenuRow.getMenuSnapTarget());
                }
                return 0;
            }
            
            @Override
            public float getFalsingThresholdFactor() {
                float n;
                if (NotificationStackScrollLayout.this.mStatusBar.isWakeUpComingFromTouch()) {
                    n = 1.5f;
                }
                else {
                    n = 1.0f;
                }
                return n;
            }
            
            @Override
            public void handleChildViewDismissed(final View e) {
                final NotificationStackScrollLayout this$0 = NotificationStackScrollLayout.this;
                boolean performDismissWithBlockingHelper = false;
                this$0.setSwipingInProgress(false);
                if (NotificationStackScrollLayout.this.mDismissAllInProgress) {
                    return;
                }
                NotificationStackScrollLayout.this.mAmbientState.onDragFinished(e);
                NotificationStackScrollLayout.this.updateContinuousShadowDrawing();
                if (e instanceof ExpandableNotificationRow) {
                    final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)e;
                    if (expandableNotificationRow.isHeadsUp()) {
                        NotificationStackScrollLayout.this.mHeadsUpManager.addSwipedOutNotification(expandableNotificationRow.getEntry().getSbn().getKey());
                    }
                    performDismissWithBlockingHelper = expandableNotificationRow.performDismissWithBlockingHelper(false);
                }
                if (e instanceof PeopleHubView) {
                    NotificationStackScrollLayout.this.mSectionsManager.hidePeopleRow();
                }
                if (!performDismissWithBlockingHelper) {
                    NotificationStackScrollLayout.this.mSwipedOutViews.add(e);
                }
                NotificationStackScrollLayout.this.mFalsingManager.onNotificationDismissed();
                if (NotificationStackScrollLayout.this.mFalsingManager.shouldEnforceBouncer()) {
                    NotificationStackScrollLayout.this.mStatusBar.executeRunnableDismissingKeyguard(null, null, false, true, false);
                }
            }
            
            @Override
            public boolean isAntiFalsingNeeded() {
                return NotificationStackScrollLayout.this.onKeyguard();
            }
            
            @Override
            public void onBeginDrag(final View view) {
                NotificationStackScrollLayout.this.mFalsingManager.onNotificatonStartDismissing();
                NotificationStackScrollLayout.this.setSwipingInProgress(true);
                NotificationStackScrollLayout.this.mAmbientState.onBeginDrag((ExpandableView)view);
                NotificationStackScrollLayout.this.updateContinuousShadowDrawing();
                NotificationStackScrollLayout.this.updateContinuousBackgroundDrawing();
                NotificationStackScrollLayout.this.requestChildrenUpdate();
            }
            
            @Override
            public void onChildDismissed(final View view) {
                if (!(view instanceof ActivatableNotificationView)) {
                    return;
                }
                final ActivatableNotificationView activatableNotificationView = (ActivatableNotificationView)view;
                if (!activatableNotificationView.isDismissed()) {
                    this.handleChildViewDismissed(view);
                }
                final ViewGroup transientContainer = activatableNotificationView.getTransientContainer();
                if (transientContainer != null) {
                    transientContainer.removeTransientView(view);
                }
            }
            
            @Override
            public void onChildSnappedBack(final View view, final float n) {
                NotificationStackScrollLayout.this.mAmbientState.onDragFinished(view);
                NotificationStackScrollLayout.this.updateContinuousShadowDrawing();
                NotificationStackScrollLayout.this.updateContinuousBackgroundDrawing();
                if (view instanceof ExpandableNotificationRow) {
                    final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
                    if (expandableNotificationRow.isPinned() && !this.canChildBeDismissed((View)expandableNotificationRow) && expandableNotificationRow.getEntry().getSbn().getNotification().fullScreenIntent == null) {
                        NotificationStackScrollLayout.this.mHeadsUpManager.removeNotification(expandableNotificationRow.getEntry().getSbn().getKey(), true);
                    }
                }
            }
            
            @Override
            public void onDismiss() {
                NotificationStackScrollLayout.this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
            }
            
            @Override
            public void onDragCancelled(final View view) {
                NotificationStackScrollLayout.this.setSwipingInProgress(false);
                NotificationStackScrollLayout.this.mFalsingManager.onNotificatonStopDismissing();
            }
            
            @Override
            public void onSnooze(final StatusBarNotification statusBarNotification, final int n) {
                NotificationStackScrollLayout.this.mStatusBar.setNotificationSnoozed(statusBarNotification, n);
            }
            
            @Override
            public void onSnooze(final StatusBarNotification statusBarNotification, final SnoozeOption snoozeOption) {
                NotificationStackScrollLayout.this.mStatusBar.setNotificationSnoozed(statusBarNotification, snoozeOption);
            }
            
            @Override
            public boolean shouldDismissQuickly() {
                return NotificationStackScrollLayout.this.isExpanded() && NotificationStackScrollLayout.this.mAmbientState.isFullyAwake();
            }
            
            @Override
            public boolean updateSwipeProgress(final View view, final boolean b, final float n) {
                return NotificationStackScrollLayout.this.mFadeNotificationsOnDismiss ^ true;
            }
        };
        this.mDragDownCallback = new DragDownHelper.DragDownCallback() {
            @Override
            public boolean isDragDownAnywhereEnabled() {
                final int state = NotificationStackScrollLayout.this.mStatusbarStateController.getState();
                boolean b = true;
                if (state != 1 || NotificationStackScrollLayout.this.mKeyguardBypassController.getBypassEnabled()) {
                    b = false;
                }
                return b;
            }
            
            @Override
            public boolean isDragDownEnabledForView(final ExpandableView expandableView) {
                if (this.isDragDownAnywhereEnabled()) {
                    return true;
                }
                if (NotificationStackScrollLayout.this.mDynamicPrivacyController.isInLockedDownShade()) {
                    if (expandableView == null) {
                        return true;
                    }
                    if (expandableView instanceof ExpandableNotificationRow) {
                        return ((ExpandableNotificationRow)expandableView).getEntry().isSensitive();
                    }
                }
                return false;
            }
            
            @Override
            public boolean isFalsingCheckNeeded() {
                final int access$4100 = NotificationStackScrollLayout.this.mStatusBarState;
                boolean b = true;
                if (access$4100 != 1) {
                    b = false;
                }
                return b;
            }
            
            @Override
            public void onCrossedThreshold(final boolean b) {
                NotificationStackScrollLayout.this.setDimmed(b ^ true, true);
            }
            
            @Override
            public void onDragDownReset() {
                NotificationStackScrollLayout.this.setDimmed(true, true);
                NotificationStackScrollLayout.this.resetScrollPosition();
                NotificationStackScrollLayout.this.resetCheckSnoozeLeavebehind();
            }
            
            @Override
            public boolean onDraggedDown(final View view, final int n) {
                if (NotificationStackScrollLayout.this.mStatusBarState == 1 && NotificationStackScrollLayout.this.hasActiveNotifications()) {
                    NotificationStackScrollLayout.this.mLockscreenGestureLogger.write(187, (int)(n / NotificationStackScrollLayout.this.mDisplayMetrics.density), 0);
                    if (!NotificationStackScrollLayout.this.mAmbientState.isDozing() || view != null) {
                        Dependency.get(ShadeController.class).goToLockedShade(view);
                        if (view instanceof ExpandableNotificationRow) {
                            ((ExpandableNotificationRow)view).onExpandedByGesture(true);
                        }
                    }
                    return true;
                }
                if (NotificationStackScrollLayout.this.mDynamicPrivacyController.isInLockedDownShade()) {
                    NotificationStackScrollLayout.this.mStatusbarStateController.setLeaveOpenOnKeyguardHide(true);
                    NotificationStackScrollLayout.this.mStatusBar.dismissKeyguardThenExecute((ActivityStarter.OnDismissAction)_$$Lambda$NotificationStackScrollLayout$16$4jJHIvYLz4tPygD0Enr8OUidlx4.INSTANCE, null, false);
                    return true;
                }
                return false;
            }
            
            @Override
            public void onTouchSlopExceeded() {
                NotificationStackScrollLayout.this.cancelLongPress();
                NotificationStackScrollLayout.this.checkSnoozeLeavebehind();
            }
            
            @Override
            public void setEmptyDragAmount(final float emptyDragAmount) {
                NotificationStackScrollLayout.this.mNotificationPanelController.setEmptyDragAmount(emptyDragAmount);
            }
        };
        this.mHeadsUpCallback = new HeadsUpTouchHelper.Callback() {
            @Override
            public ExpandableView getChildAtRawPosition(final float n, final float n2) {
                return NotificationStackScrollLayout.this.getChildAtRawPosition(n, n2);
            }
            
            @Override
            public Context getContext() {
                return NotificationStackScrollLayout.this.mContext;
            }
            
            @Override
            public boolean isExpanded() {
                return NotificationStackScrollLayout.this.mIsExpanded;
            }
        };
        this.mOnGroupChangeListener = new NotificationGroupManager.OnGroupChangeListener() {
            @Override
            public void onGroupCreatedFromChildren(final NotificationGroup notificationGroup) {
                NotificationStackScrollLayout.this.mStatusBar.requestNotificationUpdate("onGroupCreatedFromChildren");
            }
            
            @Override
            public void onGroupExpansionChanged(final ExpandableNotificationRow expandableNotificationRow, final boolean b) {
                final boolean b2 = !NotificationStackScrollLayout.this.mGroupExpandedForMeasure && NotificationStackScrollLayout.this.mAnimationsEnabled && (NotificationStackScrollLayout.this.mIsExpanded || expandableNotificationRow.isPinned());
                if (b2) {
                    NotificationStackScrollLayout.this.mExpandedGroupView = expandableNotificationRow;
                    NotificationStackScrollLayout.this.mNeedsAnimation = true;
                }
                expandableNotificationRow.setChildrenExpanded(b, b2);
                if (!NotificationStackScrollLayout.this.mGroupExpandedForMeasure) {
                    NotificationStackScrollLayout.this.onHeightChanged(expandableNotificationRow, false);
                }
                NotificationStackScrollLayout.this.runAfterAnimationFinished(new Runnable(this) {
                    @Override
                    public void run() {
                        expandableNotificationRow.onFinishedExpansionChange();
                    }
                });
            }
            
            @Override
            public void onGroupsChanged() {
                NotificationStackScrollLayout.this.mStatusBar.requestNotificationUpdate("onGroupsChanged");
            }
        };
        this.mExpandHelperCallback = new ExpandHelper.Callback() {
            @Override
            public boolean canChildBeExpanded(final View view) {
                if (view instanceof ExpandableNotificationRow) {
                    final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
                    if (expandableNotificationRow.isExpandable() && !expandableNotificationRow.areGutsExposed() && (NotificationStackScrollLayout.this.mIsExpanded || !expandableNotificationRow.isPinned())) {
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public void expansionStateChanged(final boolean b) {
                NotificationStackScrollLayout.this.mExpandingNotification = b;
                if (!NotificationStackScrollLayout.this.mExpandedInThisMotion) {
                    final NotificationStackScrollLayout this$0 = NotificationStackScrollLayout.this;
                    this$0.mMaxScrollAfterExpand = this$0.mOwnScrollY;
                    NotificationStackScrollLayout.this.mExpandedInThisMotion = true;
                }
            }
            
            @Override
            public ExpandableView getChildAtPosition(final float n, final float n2) {
                return NotificationStackScrollLayout.this.getChildAtPosition(n, n2);
            }
            
            @Override
            public ExpandableView getChildAtRawPosition(final float n, final float n2) {
                return NotificationStackScrollLayout.this.getChildAtRawPosition(n, n2);
            }
            
            @Override
            public int getMaxExpandHeight(final ExpandableView expandableView) {
                return expandableView.getMaxContentHeight();
            }
            
            @Override
            public void setExpansionCancelled(final View view) {
                if (view instanceof ExpandableNotificationRow) {
                    ((ExpandableNotificationRow)view).setGroupExpansionChanging(false);
                }
            }
            
            @Override
            public void setUserExpandedChild(final View view, final boolean b) {
                if (view instanceof ExpandableNotificationRow) {
                    final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
                    if (b && NotificationStackScrollLayout.this.onKeyguard()) {
                        expandableNotificationRow.setUserLocked(false);
                        NotificationStackScrollLayout.this.updateContentHeight();
                        NotificationStackScrollLayout.this.notifyHeightChangeListener(expandableNotificationRow);
                        return;
                    }
                    expandableNotificationRow.setUserExpanded(b, true);
                    expandableNotificationRow.onExpandedByGesture(b);
                }
            }
            
            @Override
            public void setUserLockedChild(final View view, final boolean userLocked) {
                if (view instanceof ExpandableNotificationRow) {
                    ((ExpandableNotificationRow)view).setUserLocked(userLocked);
                }
                NotificationStackScrollLayout.this.cancelLongPress();
                NotificationStackScrollLayout.this.requestDisallowInterceptTouchEvent(true);
            }
        };
        final Resources resources = this.getResources();
        this.mAllowLongPress = mAllowLongPress;
        this.mRoundnessManager = mRoundnessManager;
        this.mLockscreenUserManager = mLockscreenUserManager;
        this.mNotificationGutsManager = mNotificationGutsManager;
        (this.mHeadsUpManager = mHeadsUpManager).addListener(mRoundnessManager);
        this.mHeadsUpManager.setAnimationStateHandler((HeadsUpManagerPhone.AnimationStateHandler)new _$$Lambda$2kmwH5TzrEUhlI4yYwStAmSu1DU(this));
        this.mKeyguardBypassController = mKeyguardBypassController;
        this.mFalsingManager = mFalsingManager;
        this.mZenController = mZenController;
        this.mFgsSectionController = mFgsSectionController;
        (this.mSectionsManager = mSectionsManager).initialize(this, LayoutInflater.from(context));
        this.mSectionsManager.setOnClearGentleNotifsClickListener((View$OnClickListener)new _$$Lambda$NotificationStackScrollLayout$FSzmDEARpk_ltemkfReRVTEnBdg(this));
        this.mSections = this.mSectionsManager.createSectionsForBuckets();
        this.mAmbientState = new AmbientState(context, this.mSectionsManager, this.mHeadsUpManager);
        this.mBgColor = context.getColor(R$color.notification_shade_background_color);
        (this.mExpandHelper = new ExpandHelper(this.getContext(), this.mExpandHelperCallback, resources.getDimensionPixelSize(R$dimen.notification_min_height), resources.getDimensionPixelSize(R$dimen.notification_max_height))).setEventSource((View)this);
        this.mExpandHelper.setScrollAdapter(this);
        this.mSwipeHelper = new NotificationSwipeHelper(0, this.mNotificationCallback, this.getContext(), this.mMenuEventListener, this.mFalsingManager);
        this.mStackScrollAlgorithm = this.createStackScrollAlgorithm(context);
        this.initView(context);
        this.mShouldDrawNotificationBackground = resources.getBoolean(R$bool.config_drawNotificationBackground);
        this.mFadeNotificationsOnDismiss = resources.getBoolean(R$bool.config_fadeNotificationsOnDismiss);
        this.mRoundnessManager.setAnimatedChildren(this.mChildrenToAddAnimated);
        this.mRoundnessManager.setOnRoundingChangedCallback(new _$$Lambda$ZNzbjhiYOpIhFG8SoCZYGISAg68(this));
        final NotificationRoundnessManager mRoundnessManager2 = this.mRoundnessManager;
        Objects.requireNonNull(mRoundnessManager2);
        this.addOnExpandedHeightChangedListener(new _$$Lambda$7_f8XxLoO1HD4OWprUeIqEzesjU(mRoundnessManager2));
        this.mLockscreenUserManager.addUserChangedListener(this.mLockscreenUserChangeListener);
        this.setOutlineProvider(this.mOutlineProvider);
        this.addOnExpandedHeightChangedListener(new _$$Lambda$NotificationStackScrollLayout$vvfSYMLkcxgkYfV0xZ33PG0V3KM(Dependency.get(NotificationBlockingHelperManager.class)));
        if (this.mShouldDrawNotificationBackground) {
            b = true;
        }
        this.setWillNotDraw(b ^ true);
        this.mBackgroundPaint.setAntiAlias(true);
        this.mClearAllEnabled = resources.getBoolean(R$bool.config_enableNotificationsClearAll);
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)new _$$Lambda$NotificationStackScrollLayout$Wy8GXHBNQmyobv_GY74nULEilrI(this), "high_priority", "notification_dismiss_rtl");
        this.mFeatureFlags = mFeatureFlags;
        this.mNotifPipeline = mNotifPipeline;
        this.mEntryManager = mEntryManager;
        this.mNotifCollection = mNotifCollection;
        if (mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mNotifPipeline.addCollectionListener(new NotifCollectionListener() {
                @Override
                public void onEntryUpdated(final NotificationEntry notificationEntry) {
                    NotificationStackScrollLayout.this.onEntryUpdated(notificationEntry);
                }
            });
        }
        else {
            this.mEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
                @Override
                public void onPreEntryUpdated(final NotificationEntry notificationEntry) {
                    NotificationStackScrollLayout.this.onEntryUpdated(notificationEntry);
                }
            });
        }
        mDynamicPrivacyController.addListener((DynamicPrivacyController.Listener)this);
        this.mDynamicPrivacyController = mDynamicPrivacyController;
        this.mStatusbarStateController = mStatusbarStateController;
        this.initializeForegroundServiceSection(foregroundServiceDismissalFeatureController);
        this.mUiEventLogger = mUiEventLogger;
    }
    
    private void abortBackgroundAnimators() {
        final NotificationSection[] mSections = this.mSections;
        for (int length = mSections.length, i = 0; i < length; ++i) {
            mSections[i].cancelAnimators();
        }
    }
    
    private void animateDimmed(final boolean b) {
        final ValueAnimator mDimAnimator = this.mDimAnimator;
        if (mDimAnimator != null) {
            mDimAnimator.cancel();
        }
        float n;
        if (b) {
            n = 1.0f;
        }
        else {
            n = 0.0f;
        }
        final float mDimAmount = this.mDimAmount;
        if (n == mDimAmount) {
            return;
        }
        (this.mDimAnimator = TimeAnimator.ofFloat(new float[] { mDimAmount, n })).setDuration(220L);
        this.mDimAnimator.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        this.mDimAnimator.addListener(this.mDimEndListener);
        this.mDimAnimator.addUpdateListener(this.mDimUpdateListener);
        this.mDimAnimator.start();
    }
    
    private void animateScroll() {
        if (this.mScroller.computeScrollOffset()) {
            final int mOwnScrollY = this.mOwnScrollY;
            final int currY = this.mScroller.getCurrY();
            if (mOwnScrollY != currY) {
                final int scrollRange = this.getScrollRange();
                if ((currY < 0 && mOwnScrollY >= 0) || (currY > scrollRange && mOwnScrollY <= scrollRange)) {
                    this.setMaxOverScrollFromCurrentVelocity();
                }
                int max = scrollRange;
                if (this.mDontClampNextScroll) {
                    max = Math.max(scrollRange, mOwnScrollY);
                }
                this.customOverScrollBy(currY - mOwnScrollY, mOwnScrollY, max, (int)this.mMaxOverScroll);
            }
            this.postOnAnimation(this.mReflingAndAnimateScroll);
        }
        else {
            this.mDontClampNextScroll = false;
            final Runnable mFinishScrollingCallback = this.mFinishScrollingCallback;
            if (mFinishScrollingCallback != null) {
                mFinishScrollingCallback.run();
            }
        }
    }
    
    private void applyCurrentState() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            ((ExpandableView)this.getChildAt(i)).applyViewState();
        }
        final NotificationLogger.OnChildLocationsChangedListener mListener = this.mListener;
        if (mListener != null) {
            mListener.onChildLocationsChanged();
        }
        this.runAnimationFinishedRunnables();
        this.setAnimationRunning(false);
        this.updateBackground();
        this.updateViewShadows();
        this.updateClippingToTopRoundedCorner();
    }
    
    private boolean areSectionBoundsAnimating() {
        final NotificationSection[] mSections = this.mSections;
        for (int length = mSections.length, i = 0; i < length; ++i) {
            if (mSections[i].areBoundsAnimating()) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean canChildBeDismissed(final View view) {
        if (view instanceof ExpandableNotificationRow) {
            final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
            return expandableNotificationRow.isBlockingHelperShowingAndTranslationFinished() || (!expandableNotificationRow.areGutsExposed() && expandableNotificationRow.getEntry().hasFinishedInitialization() && expandableNotificationRow.canViewBeDismissed());
        }
        return view instanceof PeopleHubView;
    }
    
    private void clampScrollPosition() {
        final int scrollRange = this.getScrollRange();
        if (scrollRange < this.mOwnScrollY) {
            this.setOwnScrollY(scrollRange);
        }
    }
    
    private void clearHeadsUpDisappearRunning() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            if (child instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)child;
                expandableNotificationRow.setHeadsUpAnimatingAway(false);
                if (expandableNotificationRow.isSummaryWithChildren()) {
                    final Iterator<ExpandableNotificationRow> iterator = expandableNotificationRow.getNotificationChildren().iterator();
                    while (iterator.hasNext()) {
                        iterator.next().setHeadsUpAnimatingAway(false);
                    }
                }
            }
        }
    }
    
    private void clearTemporaryViews() {
        this.clearTemporaryViewsInGroup(this);
        for (int i = 0; i < this.getChildCount(); ++i) {
            final ExpandableView expandableView = (ExpandableView)this.getChildAt(i);
            if (expandableView instanceof ExpandableNotificationRow) {
                this.clearTemporaryViewsInGroup(((ExpandableNotificationRow)expandableView).getChildrenContainer());
            }
        }
    }
    
    private void clearTemporaryViewsInGroup(final ViewGroup viewGroup) {
        while (viewGroup != null && viewGroup.getTransientViewCount() != 0) {
            viewGroup.removeTransientView(viewGroup.getTransientView(0));
        }
    }
    
    private void clearTransient() {
        final Iterator<ExpandableView> iterator = this.mClearTransientViewsWhenFinished.iterator();
        while (iterator.hasNext()) {
            StackStateAnimator.removeTransientView(iterator.next());
        }
        this.mClearTransientViewsWhenFinished.clear();
    }
    
    private void clearUserLockedViews() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final ExpandableView expandableView = (ExpandableView)this.getChildAt(i);
            if (expandableView instanceof ExpandableNotificationRow) {
                ((ExpandableNotificationRow)expandableView).setUserLocked(false);
            }
        }
    }
    
    private void customOverScrollBy(int n, int n2, int n3, final int n4) {
        n2 += n;
        n = -n4;
        n3 += n4;
        boolean b = true;
        if (n2 > n3) {
            n = n3;
        }
        else if (n2 >= n) {
            b = false;
            n = n2;
        }
        this.onCustomOverScrolled(n, b);
    }
    
    private boolean didSectionBoundsChange() {
        final NotificationSection[] mSections = this.mSections;
        for (int length = mSections.length, i = 0; i < length; ++i) {
            if (mSections[i].didBoundsChange()) {
                return true;
            }
        }
        return false;
    }
    
    private void dispatchDownEventToScroller(MotionEvent obtain) {
        obtain = MotionEvent.obtain(obtain);
        obtain.setAction(0);
        this.onScrollTouch(obtain);
        obtain.recycle();
    }
    
    private void drawBackground(final Canvas canvas) {
        final int mSidePaddings = this.mSidePaddings;
        final int width = this.getWidth();
        final int mSidePaddings2 = this.mSidePaddings;
        final NotificationSection[] mSections = this.mSections;
        boolean pulseExpanding = false;
        final int top = mSections[0].getCurrentBounds().top;
        final NotificationSection[] mSections2 = this.mSections;
        final int bottom = mSections2[mSections2.length - 1].getCurrentBounds().bottom;
        final int n = this.getWidth() / 2;
        final int mTopPadding = this.mTopPadding;
        final float n2 = 1.0f - this.mInterpolatedHideAmount;
        final float interpolation = this.mHideXInterpolator.getInterpolation((1.0f - this.mLinearHideAmount) * this.mBackgroundXFactor);
        final float n3 = (float)n;
        final int n4 = (int)MathUtils.lerp(n3, (float)mSidePaddings, interpolation);
        final int n5 = (int)MathUtils.lerp(n3, (float)(width - mSidePaddings2), interpolation);
        final float n6 = (float)mTopPadding;
        final int n7 = (int)MathUtils.lerp(n6, (float)top, n2);
        this.mBackgroundAnimationRect.set(n4, n7, n5, (int)MathUtils.lerp(n6, (float)bottom, n2));
        final NotificationSection[] mSections3 = this.mSections;
        while (true) {
            for (int length = mSections3.length, i = 0; i < length; ++i) {
                if (mSections3[i].getFirstVisibleChild() != null) {
                    final boolean b = true;
                    if (this.mKeyguardBypassController.getBypassEnabled() && this.onKeyguard()) {
                        pulseExpanding = this.isPulseExpanding();
                    }
                    else if (!this.mAmbientState.isDozing() || b) {
                        pulseExpanding = true;
                    }
                    if (pulseExpanding) {
                        this.drawBackgroundRects(canvas, n4, n5, n7, n7 - top);
                    }
                    this.updateClipping();
                    return;
                }
            }
            final boolean b = false;
            continue;
        }
    }
    
    private void drawBackgroundRects(final Canvas canvas, int mCornerRadius, final int n, int mCornerRadius2, final int n2) {
        final int bottom = this.mSections[0].getCurrentBounds().bottom;
        final NotificationSection[] mSections = this.mSections;
        final int length = mSections.length;
        int n3 = mCornerRadius;
        int n4 = n;
        int n5 = bottom + n2;
        int n6 = 0;
        int n7 = 1;
        int n8 = mCornerRadius2;
        while (true) {
            mCornerRadius2 = n;
            if (n6 >= length) {
                break;
            }
            final NotificationSection notificationSection = mSections[n6];
            if (notificationSection.getFirstVisibleChild() == null) {
                mCornerRadius2 = n8;
            }
            else {
                final int n9 = notificationSection.getCurrentBounds().top + n2;
                final int min = Math.min(Math.max(mCornerRadius, notificationSection.getCurrentBounds().left), mCornerRadius2);
                final int max = Math.max(Math.min(mCornerRadius2, notificationSection.getCurrentBounds().right), min);
                Label_0220: {
                    if (n9 - n5 <= 1) {
                        if (n3 == min) {
                            mCornerRadius2 = n8;
                            if (n4 == max) {
                                break Label_0220;
                            }
                        }
                        mCornerRadius2 = n8;
                        if (n7 != 0) {
                            break Label_0220;
                        }
                    }
                    final float n10 = (float)n3;
                    final float n11 = (float)n8;
                    final float n12 = (float)n4;
                    final float n13 = (float)n5;
                    mCornerRadius2 = this.mCornerRadius;
                    canvas.drawRoundRect(n10, n11, n12, n13, (float)mCornerRadius2, (float)mCornerRadius2, this.mBackgroundPaint);
                    mCornerRadius2 = n9;
                }
                n5 = notificationSection.getCurrentBounds().bottom + n2;
                n4 = max;
                n3 = min;
                n7 = 0;
            }
            ++n6;
            n8 = mCornerRadius2;
        }
        final float n14 = (float)n3;
        final float n15 = (float)n8;
        final float n16 = (float)n4;
        final float n17 = (float)n5;
        mCornerRadius = this.mCornerRadius;
        canvas.drawRoundRect(n14, n15, n16, n17, (float)mCornerRadius, (float)mCornerRadius, this.mBackgroundPaint);
    }
    
    private void drawHeadsUpBackground(final Canvas canvas) {
        final int mSidePaddings = this.mSidePaddings;
        final int width = this.getWidth();
        final int mSidePaddings2 = this.mSidePaddings;
        float a = (float)this.getHeight();
        final int childCount = this.getChildCount();
        int i = 0;
        float a2 = 0.0f;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            float min = a;
            float max = a2;
            Label_0185: {
                if (child.getVisibility() != 8) {
                    min = a;
                    max = a2;
                    if (child instanceof ExpandableNotificationRow) {
                        final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)child;
                        if (!expandableNotificationRow.isPinned()) {
                            min = a;
                            max = a2;
                            if (!expandableNotificationRow.isHeadsUpAnimatingAway()) {
                                break Label_0185;
                            }
                        }
                        min = a;
                        max = a2;
                        if (expandableNotificationRow.getTranslation() < 0.0f) {
                            min = a;
                            max = a2;
                            if (expandableNotificationRow.getProvider().shouldShowGutsOnSnapOpen()) {
                                min = Math.min(a, expandableNotificationRow.getTranslationY());
                                max = Math.max(a2, expandableNotificationRow.getTranslationY() + expandableNotificationRow.getActualHeight());
                            }
                        }
                    }
                }
            }
            ++i;
            a = min;
            a2 = max;
        }
        if (a < a2) {
            final float n = (float)mSidePaddings;
            final float n2 = (float)(width - mSidePaddings2);
            final int mCornerRadius = this.mCornerRadius;
            canvas.drawRoundRect(n, a, n2, a2, (float)mCornerRadius, (float)mCornerRadius, this.mBackgroundPaint);
        }
    }
    
    private void endDrag() {
        this.setIsBeingDragged(false);
        this.recycleVelocityTracker();
        if (this.getCurrentOverScrollAmount(true) > 0.0f) {
            this.setOverScrollAmount(0.0f, true, true);
        }
        if (this.getCurrentOverScrollAmount(false) > 0.0f) {
            this.setOverScrollAmount(0.0f, false, true);
        }
    }
    
    private void focusNextViewIfFocused(final View view) {
        if (view instanceof ExpandableNotificationRow) {
            final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
            if (expandableNotificationRow.shouldRefocusOnDismiss()) {
                View view2;
                if ((view2 = expandableNotificationRow.getChildAfterViewWhenDismissed()) == null) {
                    final View groupParentWhenDismissed = expandableNotificationRow.getGroupParentWhenDismissed();
                    float n;
                    if (groupParentWhenDismissed != null) {
                        n = groupParentWhenDismissed.getTranslationY();
                    }
                    else {
                        n = view.getTranslationY();
                    }
                    view2 = this.getFirstChildBelowTranlsationY(n, true);
                }
                if (view2 != null) {
                    view2.requestAccessibilityFocus();
                }
            }
        }
    }
    
    private void generateActivateEvent() {
        if (this.mActivateNeedsAnimation) {
            this.mAnimationEvents.add(new AnimationEvent(null, 4));
        }
        this.mActivateNeedsAnimation = false;
    }
    
    private void generateAllAnimationEvents() {
        this.generateHeadsUpAnimationEvents();
        this.generateChildRemovalEvents();
        this.generateChildAdditionEvents();
        this.generatePositionChangeEvents();
        this.generateTopPaddingEvent();
        this.generateActivateEvent();
        this.generateDimmedEvent();
        this.generateHideSensitiveEvent();
        this.generateGoToFullShadeEvent();
        this.generateViewResizeEvent();
        this.generateGroupExpansionEvent();
        this.generateAnimateEverythingEvent();
    }
    
    private void generateAnimateEverythingEvent() {
        if (this.mEverythingNeedsAnimation) {
            this.mAnimationEvents.add(new AnimationEvent(null, 15));
        }
        this.mEverythingNeedsAnimation = false;
    }
    
    private void generateChildAdditionEvents() {
        for (final ExpandableView o : this.mChildrenToAddAnimated) {
            if (this.mFromMoreCardAdditions.contains(o)) {
                this.mAnimationEvents.add(new AnimationEvent(o, 0, 360L));
            }
            else {
                this.mAnimationEvents.add(new AnimationEvent(o, 0));
            }
        }
        this.mChildrenToAddAnimated.clear();
        this.mFromMoreCardAdditions.clear();
    }
    
    private void generateChildRemovalEvents() {
        for (final ExpandableView expandableView : this.mChildrenToRemoveAnimated) {
            boolean contains = this.mSwipedOutViews.contains(expandableView);
            float n = expandableView.getTranslationY();
            final boolean b = expandableView instanceof ExpandableNotificationRow;
            final int n2 = 0;
            final int n3 = 1;
            boolean b2;
            if (b) {
                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)expandableView;
                if (expandableNotificationRow.isRemoved() && expandableNotificationRow.wasChildInGroupWhenRemoved()) {
                    n = expandableNotificationRow.getTranslationWhenRemoved();
                    b2 = false;
                }
                else {
                    b2 = true;
                }
                contains |= (Math.abs(expandableNotificationRow.getTranslation()) == expandableNotificationRow.getWidth());
            }
            else {
                b2 = true;
            }
            int n4 = contains ? 1 : 0;
            if ((contains ? 1 : 0) == 0) {
                final Rect clipBounds = expandableView.getClipBounds();
                int n5 = n2;
                if (clipBounds != null) {
                    n5 = n2;
                    if (clipBounds.height() == 0) {
                        n5 = 1;
                    }
                }
                if (n5 != 0 && expandableView instanceof ExpandableView) {
                    final ViewGroup transientContainer = expandableView.getTransientContainer();
                    if (transientContainer != null) {
                        transientContainer.removeTransientView((View)expandableView);
                    }
                }
                n4 = n5;
            }
            int n6 = n3;
            if (n4 != 0) {
                n6 = 2;
            }
            final AnimationEvent e = new AnimationEvent(expandableView, n6);
            e.viewAfterChangingView = this.getFirstChildBelowTranlsationY(n, b2);
            this.mAnimationEvents.add(e);
            this.mSwipedOutViews.remove(expandableView);
        }
        this.mChildrenToRemoveAnimated.clear();
    }
    
    private void generateDimmedEvent() {
        if (this.mDimmedNeedsAnimation) {
            this.mAnimationEvents.add(new AnimationEvent(null, 5));
        }
        this.mDimmedNeedsAnimation = false;
    }
    
    private void generateGoToFullShadeEvent() {
        if (this.mGoToFullShadeNeedsAnimation) {
            this.mAnimationEvents.add(new AnimationEvent(null, 7));
        }
        this.mGoToFullShadeNeedsAnimation = false;
    }
    
    private void generateGroupExpansionEvent() {
        if (this.mExpandedGroupView != null) {
            this.mAnimationEvents.add(new AnimationEvent(this.mExpandedGroupView, 10));
            this.mExpandedGroupView = null;
        }
    }
    
    private void generateHeadsUpAnimationEvents() {
        for (final Pair<ExpandableNotificationRow, Boolean> pair : this.mHeadsUpChangeAnimations) {
            final ExpandableNotificationRow o = (ExpandableNotificationRow)pair.first;
            final boolean booleanValue = (boolean)pair.second;
            if (booleanValue != o.isHeadsUp()) {
                continue;
            }
            final int n = 14;
            final boolean pinned = o.isPinned();
            final int n2 = 1;
            final boolean b = false;
            final boolean b2 = pinned && !this.mIsExpanded;
            int n3 = n2;
            if (this.mIsExpanded) {
                if (this.mKeyguardBypassController.getBypassEnabled() && this.onKeyguard() && this.mHeadsUpManager.hasPinnedHeadsUp()) {
                    n3 = n2;
                }
                else {
                    n3 = 0;
                }
            }
            int n4 = 0;
            boolean headsUpFromBottom = false;
            Label_0272: {
                if (n3 != 0 && !booleanValue) {
                    if (o.wasJustClicked()) {
                        n4 = 13;
                    }
                    else {
                        n4 = 12;
                    }
                    headsUpFromBottom = b;
                    if (o.isChildInGroup()) {
                        o.setHeadsUpAnimatingAway(false);
                        continue;
                    }
                }
                else {
                    final ExpandableViewState viewState = o.getViewState();
                    if (viewState == null) {
                        continue;
                    }
                    n4 = n;
                    headsUpFromBottom = b;
                    if (booleanValue) {
                        if (!this.mAddedHeadsUpChildren.contains(o)) {
                            n4 = n;
                            headsUpFromBottom = b;
                            if (!b2) {
                                break Label_0272;
                            }
                        }
                        if (!b2 && !this.shouldHunAppearFromBottom(viewState)) {
                            n4 = 0;
                        }
                        else {
                            n4 = 11;
                        }
                        headsUpFromBottom = (b2 ^ true);
                    }
                }
            }
            final AnimationEvent e = new AnimationEvent(o, n4);
            e.headsUpFromBottom = headsUpFromBottom;
            this.mAnimationEvents.add(e);
        }
        this.mHeadsUpChangeAnimations.clear();
        this.mAddedHeadsUpChildren.clear();
    }
    
    private void generateHideSensitiveEvent() {
        if (this.mHideSensitiveNeedsAnimation) {
            this.mAnimationEvents.add(new AnimationEvent(null, 8));
        }
        this.mHideSensitiveNeedsAnimation = false;
    }
    
    private void generatePositionChangeEvents() {
        final Iterator<ExpandableView> iterator = this.mChildrenChangingPositions.iterator();
        while (iterator.hasNext()) {
            this.mAnimationEvents.add(new AnimationEvent(iterator.next(), 6));
        }
        this.mChildrenChangingPositions.clear();
        if (this.mGenerateChildOrderChangedEvent) {
            this.mAnimationEvents.add(new AnimationEvent(null, 6));
            this.mGenerateChildOrderChangedEvent = false;
        }
    }
    
    private boolean generateRemoveAnimation(final ExpandableView expandableView) {
        if (this.removeRemovedChildFromHeadsUpChangeAnimations((View)expandableView)) {
            this.mAddedHeadsUpChildren.remove(expandableView);
            return false;
        }
        if (this.isClickedHeadsUp((View)expandableView)) {
            this.mClearTransientViewsWhenFinished.add(expandableView);
            return true;
        }
        if (this.mIsExpanded && this.mAnimationsEnabled && !this.isChildInInvisibleGroup((View)expandableView)) {
            if (!this.mChildrenToAddAnimated.contains(expandableView)) {
                this.mChildrenToRemoveAnimated.add(expandableView);
                return this.mNeedsAnimation = true;
            }
            this.mChildrenToAddAnimated.remove(expandableView);
            this.mFromMoreCardAdditions.remove(expandableView);
        }
        return false;
    }
    
    private void generateTopPaddingEvent() {
        if (this.mTopPaddingNeedsAnimation) {
            AnimationEvent e;
            if (this.mAmbientState.isDozing()) {
                e = new AnimationEvent(null, 3, 550L);
            }
            else {
                e = new AnimationEvent(null, 3);
            }
            this.mAnimationEvents.add(e);
        }
        this.mTopPaddingNeedsAnimation = false;
    }
    
    private void generateViewResizeEvent() {
        Label_0078: {
            if (this.mNeedViewResizeAnimation) {
                final Iterator<AnimationEvent> iterator = this.mAnimationEvents.iterator();
                while (true) {
                    while (iterator.hasNext()) {
                        final int animationType = iterator.next().animationType;
                        if (animationType == 13 || animationType == 12) {
                            final boolean b = true;
                            if (!b) {
                                this.mAnimationEvents.add(new AnimationEvent(null, 9));
                            }
                            break Label_0078;
                        }
                    }
                    final boolean b = false;
                    continue;
                }
            }
        }
        this.mNeedViewResizeAnimation = false;
    }
    
    private float getAppearEndPosition() {
        final int notGoneChildCount = this.getNotGoneChildCount();
        int n;
        if (this.mEmptyShadeView.getVisibility() == 8 && notGoneChildCount != 0) {
            if (!this.isHeadsUpTransition() && (!this.mHeadsUpManager.hasPinnedHeadsUp() || this.mAmbientState.isDozing())) {
                n = 0;
                if (notGoneChildCount >= 1) {
                    n = n;
                    if (this.mShelf.getVisibility() != 8) {
                        n = 0 + this.mShelf.getIntrinsicHeight();
                    }
                }
            }
            else {
                n = this.getTopHeadsUpPinnedHeight();
            }
        }
        else {
            n = this.mEmptyShadeView.getHeight();
        }
        int n2;
        if (this.onKeyguard()) {
            n2 = this.mTopPadding;
        }
        else {
            n2 = this.mIntrinsicPadding;
        }
        return (float)(n + n2);
    }
    
    private float getAppearStartPosition() {
        if (this.isHeadsUpTransition()) {
            return (float)(this.mHeadsUpInset + this.getFirstVisibleSection().getFirstVisibleChild().getPinnedHeadsUpHeight());
        }
        return (float)this.getMinExpansionHeight();
    }
    
    private ExpandableView getChildAtPosition(final float n, final float n2) {
        return this.getChildAtPosition(n, n2, true, true);
    }
    
    private ExpandableView getChildAtPosition(final float n, final float n2, final boolean b, final boolean b2) {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final ExpandableView expandableView = (ExpandableView)this.getChildAt(i);
            if (expandableView.getVisibility() == 0) {
                if (!b2 || !(expandableView instanceof StackScrollerDecorView)) {
                    final float translationY = expandableView.getTranslationY();
                    final float n3 = expandableView.getClipTopAmount() + translationY;
                    final float n4 = expandableView.getActualHeight() + translationY - expandableView.getClipBottomAmount();
                    final int width = this.getWidth();
                    if ((n4 - n3 >= this.mMinInteractionHeight || !b) && n2 >= n3 && n2 <= n4 && n >= 0 && n <= width) {
                        if (!(expandableView instanceof ExpandableNotificationRow)) {
                            return expandableView;
                        }
                        final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)expandableView;
                        final NotificationEntry entry = expandableNotificationRow.getEntry();
                        if (this.mIsExpanded || !expandableNotificationRow.isHeadsUp() || !expandableNotificationRow.isPinned() || this.mHeadsUpManager.getTopEntry().getRow() == expandableNotificationRow || this.mGroupManager.getGroupSummary(this.mHeadsUpManager.getTopEntry().getSbn()) == entry) {
                            return expandableNotificationRow.getViewAtPosition(n2 - translationY);
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private List<ActivatableNotificationView> getChildrenWithBackground() {
        final ArrayList<ActivatableNotificationView> list = new ArrayList<ActivatableNotificationView>();
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8 && child instanceof ActivatableNotificationView && child != this.mShelf) {
                list.add((NotificationShelf)child);
            }
        }
        return list;
    }
    
    private float getExpandTranslationStart() {
        return (float)(-this.mTopPadding + this.getMinExpansionHeight() - this.mShelf.getIntrinsicHeight());
    }
    
    private View getFirstChildBelowTranlsationY(final float n, final boolean b) {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final float translationY = child.getTranslationY();
                if (translationY >= n) {
                    return child;
                }
                if (!b && child instanceof ExpandableNotificationRow) {
                    final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)child;
                    if (expandableNotificationRow.isSummaryWithChildren() && expandableNotificationRow.areChildrenExpanded()) {
                        final List<ExpandableNotificationRow> notificationChildren = expandableNotificationRow.getNotificationChildren();
                        for (int j = 0; j < notificationChildren.size(); ++j) {
                            final ExpandableNotificationRow expandableNotificationRow2 = notificationChildren.get(j);
                            if (expandableNotificationRow2.getTranslationY() + translationY >= n) {
                                return (View)expandableNotificationRow2;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private ActivatableNotificationView getFirstChildWithBackground() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8 && child instanceof ActivatableNotificationView && child != this.mShelf) {
                return (NotificationShelf)child;
            }
        }
        return null;
    }
    
    private NotificationSection getFirstVisibleSection() {
        for (final NotificationSection notificationSection : this.mSections) {
            if (notificationSection.getFirstVisibleChild() != null) {
                return notificationSection;
            }
        }
        return null;
    }
    
    private int getImeInset() {
        return Math.max(0, this.mBottomInset - (this.getRootView().getHeight() - this.getHeight()));
    }
    
    private int getIntrinsicHeight(final View view) {
        if (view instanceof ExpandableView) {
            return ((ExpandableView)view).getIntrinsicHeight();
        }
        return view.getHeight();
    }
    
    private ActivatableNotificationView getLastChildWithBackground() {
        for (int i = this.getChildCount() - 1; i >= 0; --i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8 && child instanceof ActivatableNotificationView && child != this.mShelf) {
                return (NotificationShelf)child;
            }
        }
        return null;
    }
    
    private NotificationSection getLastVisibleSection() {
        for (int i = this.mSections.length - 1; i >= 0; --i) {
            final NotificationSection notificationSection = this.mSections[i];
            if (notificationSection.getLastVisibleChild() != null) {
                return notificationSection;
            }
        }
        return null;
    }
    
    private int getLayoutHeight() {
        return Math.min(this.mMaxLayoutHeight, this.mCurrentStackHeight);
    }
    
    private float getRubberBandFactor(final boolean b) {
        if (!b) {
            return 0.35f;
        }
        if (this.mExpandedInThisMotion) {
            return 0.15f;
        }
        if (this.mIsExpansionChanging || this.mPanelTracking) {
            return 0.21f;
        }
        if (this.mScrolledToTopOnFirstDown) {
            return 1.0f;
        }
        return 0.35f;
    }
    
    private int getScrollRange() {
        int mContentHeight;
        final int n = mContentHeight = this.mContentHeight;
        if (!this.isExpanded()) {
            mContentHeight = n;
            if (this.mHeadsUpManager.hasPinnedHeadsUp()) {
                mContentHeight = this.mHeadsUpInset + this.getTopHeadsUpPinnedHeight();
            }
        }
        final int max = Math.max(0, mContentHeight - this.mMaxLayoutHeight);
        final int imeInset = this.getImeInset();
        return max + Math.min(imeInset, Math.max(0, mContentHeight - (this.getHeight() - imeInset)));
    }
    
    private int getTopHeadsUpPinnedHeight() {
        final NotificationEntry topEntry = this.mHeadsUpManager.getTopEntry();
        if (topEntry == null) {
            return 0;
        }
        ExpandableNotificationRow expandableNotificationRow2;
        final ExpandableNotificationRow expandableNotificationRow = expandableNotificationRow2 = topEntry.getRow();
        if (expandableNotificationRow.isChildInGroup()) {
            final NotificationEntry groupSummary = this.mGroupManager.getGroupSummary(expandableNotificationRow.getEntry().getSbn());
            expandableNotificationRow2 = expandableNotificationRow;
            if (groupSummary != null) {
                expandableNotificationRow2 = groupSummary.getRow();
            }
        }
        return expandableNotificationRow2.getPinnedHeadsUpHeight();
    }
    
    private float getTouchSlop(final MotionEvent motionEvent) {
        float n;
        if (motionEvent.getClassification() == 1) {
            n = this.mTouchSlop * this.mSlopMultiplier;
        }
        else {
            n = (float)this.mTouchSlop;
        }
        return n;
    }
    
    private void handleDismissAllClipping() {
        boolean canChildBeDismissed;
        for (int childCount = this.getChildCount(), i = (canChildBeDismissed = false) ? 1 : 0; i < childCount; ++i) {
            final ExpandableView expandableView = (ExpandableView)this.getChildAt(i);
            if (expandableView.getVisibility() != 8) {
                if (this.mDismissAllInProgress && canChildBeDismissed) {
                    expandableView.setMinClipTopAmount(expandableView.getClipTopAmount());
                }
                else {
                    expandableView.setMinClipTopAmount(0);
                }
                canChildBeDismissed = canChildBeDismissed((View)expandableView);
            }
        }
    }
    
    private void handleEmptySpaceClick(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                final float touchSlop = this.getTouchSlop(motionEvent);
                if (this.mTouchIsClick && (Math.abs(motionEvent.getY() - this.mInitialTouchY) > touchSlop || Math.abs(motionEvent.getX() - this.mInitialTouchX) > touchSlop)) {
                    this.mTouchIsClick = false;
                }
            }
        }
        else if (this.mStatusBarState != 1 && this.mTouchIsClick && this.isBelowLastNotification(this.mInitialTouchX, this.mInitialTouchY)) {
            this.mOnEmptySpaceClickListener.onEmptySpaceClicked(this.mInitialTouchX, this.mInitialTouchY);
        }
    }
    
    private boolean hasActiveNotifications() {
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            return this.mNotifPipeline.getShadeList().isEmpty() ^ true;
        }
        return this.mEntryManager.hasActiveNotifications();
    }
    
    private boolean includeChildInDismissAll(final ExpandableNotificationRow expandableNotificationRow, final int n) {
        return canChildBeDismissed((View)expandableNotificationRow) && matchesSelection(expandableNotificationRow, n);
    }
    
    private void inflateEmptyShadeView() {
        final EmptyShadeView emptyShadeView = (EmptyShadeView)LayoutInflater.from(super.mContext).inflate(R$layout.status_bar_no_notifications, (ViewGroup)this, false);
        emptyShadeView.setText(R$string.empty_shade_text);
        this.setEmptyShadeView(emptyShadeView);
    }
    
    private void initDownStates(final MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mExpandedInThisMotion = false;
            this.mOnlyScrollingInThisMotion = (this.mScroller.isFinished() ^ true);
            this.mDisallowScrollingInThisMotion = false;
            this.mDisallowDismissInThisMotion = false;
            this.mTouchIsClick = true;
            this.mInitialTouchX = motionEvent.getX();
            this.mInitialTouchY = motionEvent.getY();
        }
    }
    
    private void initOrResetVelocityTracker() {
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        else {
            mVelocityTracker.clear();
        }
    }
    
    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }
    
    private void initView(final Context context) {
        this.mScroller = new OverScroller(this.getContext());
        this.setDescendantFocusability(262144);
        this.setClipChildren(false);
        final ViewConfiguration value = ViewConfiguration.get(context);
        this.mTouchSlop = value.getScaledTouchSlop();
        this.mSlopMultiplier = value.getScaledAmbiguousGestureMultiplier();
        this.mMinimumVelocity = value.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = value.getScaledMaximumFlingVelocity();
        this.mOverflingDistance = value.getScaledOverflingDistance();
        final Resources resources = context.getResources();
        this.mCollapsedSize = resources.getDimensionPixelSize(R$dimen.notification_min_height);
        this.mStackScrollAlgorithm.initView(context);
        this.mAmbientState.reload(context);
        this.mPaddingBetweenElements = Math.max(1, resources.getDimensionPixelSize(R$dimen.notification_divider_height));
        this.mIncreasedPaddingBetweenElements = resources.getDimensionPixelSize(R$dimen.notification_divider_height_increased);
        this.mMinTopOverScrollToEscape = (float)resources.getDimensionPixelSize(R$dimen.min_top_overscroll_to_qs);
        this.mStatusBarHeight = resources.getDimensionPixelSize(R$dimen.status_bar_height);
        this.mBottomMargin = resources.getDimensionPixelSize(R$dimen.notification_panel_margin_bottom);
        this.mSidePaddings = resources.getDimensionPixelSize(R$dimen.notification_side_paddings);
        this.mMinInteractionHeight = resources.getDimensionPixelSize(R$dimen.notification_min_interaction_height);
        this.mCornerRadius = resources.getDimensionPixelSize(Utils.getThemeAttr(super.mContext, 16844145));
        this.mHeadsUpInset = this.mStatusBarHeight + resources.getDimensionPixelSize(R$dimen.heads_up_status_bar_padding);
    }
    
    private void initializeForegroundServiceSection(final ForegroundServiceDismissalFeatureController foregroundServiceDismissalFeatureController) {
        if (foregroundServiceDismissalFeatureController.isForegroundServiceDismissalEnabled()) {
            this.addView((View)(this.mFgsSectionView = (ForegroundServiceDungeonView)this.mFgsSectionController.createView(LayoutInflater.from(super.mContext))), -1);
        }
    }
    
    private boolean isChildInGroup(final View view) {
        return view instanceof ExpandableNotificationRow && this.mGroupManager.isChildInGroupWithSummary(((ExpandableNotificationRow)view).getEntry().getSbn());
    }
    
    private boolean isChildInInvisibleGroup(final View view) {
        final boolean b = view instanceof ExpandableNotificationRow;
        boolean b3;
        final boolean b2 = b3 = false;
        if (b) {
            final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
            final NotificationEntry groupSummary = this.mGroupManager.getGroupSummary(expandableNotificationRow.getEntry().getSbn());
            b3 = b2;
            if (groupSummary != null) {
                b3 = b2;
                if (groupSummary.getRow() != expandableNotificationRow) {
                    b3 = b2;
                    if (expandableNotificationRow.getVisibility() == 4) {
                        b3 = true;
                    }
                }
            }
        }
        return b3;
    }
    
    private boolean isClickedHeadsUp(final View view) {
        return HeadsUpUtil.isClickedHeadsUpNotification(view);
    }
    
    private boolean isCurrentlyAnimating() {
        return this.mStateAnimator.isRunning();
    }
    
    private boolean isHeadsUp(final View view) {
        return view instanceof ExpandableNotificationRow && ((ExpandableNotificationRow)view).isHeadsUp();
    }
    
    private boolean isHeadsUpTransition() {
        final NotificationSection firstVisibleSection = this.getFirstVisibleSection();
        return this.mTrackingHeadsUp && firstVisibleSection != null && firstVisibleSection.getFirstVisibleChild().isAboveShelf();
    }
    
    private boolean isInContentBounds(final MotionEvent motionEvent) {
        return this.isInContentBounds(motionEvent.getY());
    }
    
    public static boolean isPinnedHeadsUp(final View view) {
        final boolean b = view instanceof ExpandableNotificationRow;
        boolean b3;
        final boolean b2 = b3 = false;
        if (b) {
            final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
            b3 = b2;
            if (expandableNotificationRow.isHeadsUp()) {
                b3 = b2;
                if (expandableNotificationRow.isPinned()) {
                    b3 = true;
                }
            }
        }
        return b3;
    }
    
    private boolean isRubberbanded(final boolean b) {
        return !b || this.mExpandedInThisMotion || this.mIsExpansionChanging || this.mPanelTracking || !this.mScrolledToTopOnFirstDown;
    }
    
    private boolean isScrollingEnabled() {
        return this.mScrollingEnabled;
    }
    
    private static boolean matchesSelection(final ExpandableNotificationRow expandableNotificationRow, final int i) {
        final boolean b = true;
        final boolean b2 = true;
        boolean b3 = b;
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return expandableNotificationRow.getEntry().getBucket() == 4 && b2;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Unknown selection: ");
                sb.append(i);
                throw new IllegalArgumentException(sb.toString());
            }
            else {
                b3 = (expandableNotificationRow.getEntry().getBucket() < 4 && b);
            }
        }
        return b3;
    }
    
    private void notifyAppearChangedListeners() {
        float n;
        float n2;
        if (this.mKeyguardBypassController.getBypassEnabled() && this.onKeyguard()) {
            n = this.calculateAppearFractionBypass();
            n2 = this.getPulseHeight();
        }
        else {
            n = MathUtils.saturate(this.calculateAppearFraction(this.mExpandedHeight));
            n2 = this.mExpandedHeight;
        }
        if (n != this.mLastSentAppear || n2 != this.mLastSentExpandedHeight) {
            this.mLastSentAppear = n;
            this.mLastSentExpandedHeight = n2;
            for (int i = 0; i < this.mExpandedHeightListeners.size(); ++i) {
                this.mExpandedHeightListeners.get(i).accept(n2, n);
            }
        }
    }
    
    private void notifyHeightChangeListener(final ExpandableView expandableView) {
        this.notifyHeightChangeListener(expandableView, false);
    }
    
    private void notifyHeightChangeListener(final ExpandableView expandableView, final boolean b) {
        final OnHeightChangedListener mOnHeightChangedListener = this.mOnHeightChangedListener;
        if (mOnHeightChangedListener != null) {
            mOnHeightChangedListener.onHeightChanged(expandableView, b);
        }
    }
    
    private void notifyOverscrollTopListener(final float n, final boolean b) {
        this.mExpandHelper.onlyObserveMovements(n > 1.0f);
        if (this.mDontReportNextOverScroll) {
            this.mDontReportNextOverScroll = false;
            return;
        }
        final OnOverscrollTopChangedListener mOverscrollTopChangedListener = this.mOverscrollTopChangedListener;
        if (mOverscrollTopChangedListener != null) {
            mOverscrollTopChangedListener.onOverscrollTopChanged(n, b);
        }
    }
    
    private void onCustomOverScrolled(int mOwnScrollY, final boolean b) {
        if (!this.mScroller.isFinished()) {
            this.setOwnScrollY(mOwnScrollY);
            if (b) {
                this.springBack();
            }
            else {
                final float currentOverScrollAmount = this.getCurrentOverScrollAmount(true);
                mOwnScrollY = this.mOwnScrollY;
                if (mOwnScrollY < 0) {
                    this.notifyOverscrollTopListener((float)(-mOwnScrollY), this.isRubberbanded(true));
                }
                else {
                    this.notifyOverscrollTopListener(currentOverScrollAmount, this.isRubberbanded(true));
                }
            }
        }
        else {
            this.setOwnScrollY(mOwnScrollY);
        }
    }
    
    private void onDismissAllAnimationsEnd(final List<ExpandableNotificationRow> list, int i) {
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            if (i == 0) {
                this.mNotifCollection.dismissAllNotifications(this.mLockscreenUserManager.getCurrentUserId());
                return;
            }
            final ArrayList<Pair<NotificationEntry, DismissedByUserStats>> list2 = new ArrayList<Pair<NotificationEntry, DismissedByUserStats>>();
            new ArrayList();
            final int shadeListCount = this.mNotifPipeline.getShadeListCount();
            NotificationEntry entry;
            for (i = 0; i < list.size(); ++i) {
                entry = list.get(i).getEntry();
                list2.add((Pair<NotificationEntry, DismissedByUserStats>)new Pair((Object)entry, (Object)new DismissedByUserStats(3, 1, NotificationVisibility.obtain(entry.getKey(), entry.getRanking().getRank(), shadeListCount, true, NotificationLogger.getNotificationLocation(entry)))));
            }
            this.mNotifCollection.dismissNotifications(list2);
            return;
        }
        else {
            for (final ExpandableNotificationRow expandableNotificationRow : list) {
                if (canChildBeDismissed((View)expandableNotificationRow)) {
                    if (i == 0) {
                        this.mEntryManager.removeNotification(expandableNotificationRow.getEntry().getKey(), null, 3);
                    }
                    else {
                        this.mEntryManager.performRemoveNotification(expandableNotificationRow.getEntry().getSbn(), 3);
                    }
                }
                else {
                    expandableNotificationRow.resetTranslation();
                }
            }
            if (i != 0) {
                return;
            }
        }
        try {
            this.mBarService.onClearAllNotifications(this.mLockscreenUserManager.getCurrentUserId());
        }
        catch (Exception ex) {}
    }
    
    private void onEntryUpdated(final NotificationEntry notificationEntry) {
        if (notificationEntry.rowExists() && !notificationEntry.getSbn().isClearable()) {
            this.snapViewIfNeeded(notificationEntry);
        }
    }
    
    private boolean onInterceptTouchEventScroll(final MotionEvent motionEvent) {
        if (!this.isScrollingEnabled()) {
            return false;
        }
        final int action = motionEvent.getAction();
        if (action == 2 && this.mIsBeingDragged) {
            return true;
        }
        final int n = action & 0xFF;
        if (n != 0) {
            if (n != 1) {
                if (n != 2) {
                    if (n != 3) {
                        if (n != 6) {
                            return this.mIsBeingDragged;
                        }
                        this.onSecondaryPointerUp(motionEvent);
                        return this.mIsBeingDragged;
                    }
                }
                else {
                    final int mActivePointerId = this.mActivePointerId;
                    if (mActivePointerId == -1) {
                        return this.mIsBeingDragged;
                    }
                    final int pointerIndex = motionEvent.findPointerIndex(mActivePointerId);
                    if (pointerIndex == -1) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Invalid pointerId=");
                        sb.append(mActivePointerId);
                        sb.append(" in onInterceptTouchEvent");
                        Log.e("StackScroller", sb.toString());
                        return this.mIsBeingDragged;
                    }
                    final int mLastMotionY = (int)motionEvent.getY(pointerIndex);
                    final int mDownX = (int)motionEvent.getX(pointerIndex);
                    final int abs = Math.abs(mLastMotionY - this.mLastMotionY);
                    final int abs2 = Math.abs(mDownX - this.mDownX);
                    if (abs > this.getTouchSlop(motionEvent) && abs > abs2) {
                        this.setIsBeingDragged(true);
                        this.mLastMotionY = mLastMotionY;
                        this.mDownX = mDownX;
                        this.initVelocityTrackerIfNotExists();
                        this.mVelocityTracker.addMovement(motionEvent);
                        return this.mIsBeingDragged;
                    }
                    return this.mIsBeingDragged;
                }
            }
            this.setIsBeingDragged(false);
            this.mActivePointerId = -1;
            this.recycleVelocityTracker();
            if (this.mScroller.springBack(super.mScrollX, this.mOwnScrollY, 0, 0, 0, this.getScrollRange())) {
                this.animateScroll();
            }
        }
        else {
            final int mLastMotionY2 = (int)motionEvent.getY();
            this.mScrolledToTopOnFirstDown = this.isScrolledToTop();
            if (this.getChildAtPosition(motionEvent.getX(), (float)mLastMotionY2, false, false) == null) {
                this.setIsBeingDragged(false);
                this.recycleVelocityTracker();
            }
            else {
                this.mLastMotionY = mLastMotionY2;
                this.mDownX = (int)motionEvent.getX();
                this.mActivePointerId = motionEvent.getPointerId(0);
                this.initOrResetVelocityTracker();
                this.mVelocityTracker.addMovement(motionEvent);
                this.setIsBeingDragged(this.mScroller.isFinished() ^ true);
            }
        }
        return this.mIsBeingDragged;
    }
    
    private boolean onKeyguard() {
        final int mStatusBarState = this.mStatusBarState;
        boolean b = true;
        if (mStatusBarState != 1) {
            b = false;
        }
        return b;
    }
    
    private void onOverScrollFling(final boolean b, final int n) {
        final OnOverscrollTopChangedListener mOverscrollTopChangedListener = this.mOverscrollTopChangedListener;
        if (mOverscrollTopChangedListener != null) {
            mOverscrollTopChangedListener.flingTopOverscroll((float)n, b);
        }
        this.setOverScrollAmount(0.0f, this.mDontReportNextOverScroll = true, false);
    }
    
    private void onPreDrawDuringAnimation() {
        this.mShelf.updateAppearance();
        this.updateClippingToTopRoundedCorner();
        if (!this.mNeedsAnimation && !this.mChildrenUpdateRequested) {
            this.updateBackground();
        }
    }
    
    private boolean onScrollTouch(final MotionEvent motionEvent) {
        if (!this.isScrollingEnabled()) {
            return false;
        }
        if (this.isInsideQsContainer(motionEvent) && !this.mIsBeingDragged) {
            return false;
        }
        this.mForcedScroll = null;
        this.initVelocityTrackerIfNotExists();
        this.mVelocityTracker.addMovement(motionEvent);
        final int actionMasked = motionEvent.getActionMasked();
        if (motionEvent.findPointerIndex(this.mActivePointerId) == -1 && actionMasked != 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invalid pointerId=");
            sb.append(this.mActivePointerId);
            sb.append(" in onTouchEvent ");
            sb.append(MotionEvent.actionToString(motionEvent.getActionMasked()));
            Log.e("StackScroller", sb.toString());
            return true;
        }
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked != 5) {
                            if (actionMasked == 6) {
                                this.onSecondaryPointerUp(motionEvent);
                                this.mLastMotionY = (int)motionEvent.getY(motionEvent.findPointerIndex(this.mActivePointerId));
                                this.mDownX = (int)motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId));
                            }
                        }
                        else {
                            final int actionIndex = motionEvent.getActionIndex();
                            this.mLastMotionY = (int)motionEvent.getY(actionIndex);
                            this.mDownX = (int)motionEvent.getX(actionIndex);
                            this.mActivePointerId = motionEvent.getPointerId(actionIndex);
                        }
                    }
                    else if (this.mIsBeingDragged && this.getChildCount() > 0) {
                        if (this.mScroller.springBack(super.mScrollX, this.mOwnScrollY, 0, 0, 0, this.getScrollRange())) {
                            this.animateScroll();
                        }
                        this.mActivePointerId = -1;
                        this.endDrag();
                    }
                }
                else {
                    final int pointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex == -1) {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("Invalid pointerId=");
                        sb2.append(this.mActivePointerId);
                        sb2.append(" in onTouchEvent");
                        Log.e("StackScroller", sb2.toString());
                    }
                    else {
                        final int mLastMotionY = (int)motionEvent.getY(pointerIndex);
                        final int n = (int)motionEvent.getX(pointerIndex);
                        final int a = this.mLastMotionY - mLastMotionY;
                        final int abs = Math.abs(n - this.mDownX);
                        final int abs2 = Math.abs(a);
                        final float touchSlop = this.getTouchSlop(motionEvent);
                        int n2 = a;
                        if (!this.mIsBeingDragged) {
                            n2 = a;
                            if (abs2 > touchSlop) {
                                n2 = a;
                                if (abs2 > abs) {
                                    this.setIsBeingDragged(true);
                                    float n3;
                                    if (a > 0) {
                                        n3 = a - touchSlop;
                                    }
                                    else {
                                        n3 = a + touchSlop;
                                    }
                                    n2 = (int)n3;
                                }
                            }
                        }
                        if (this.mIsBeingDragged) {
                            this.mLastMotionY = mLastMotionY;
                            int n4;
                            final int a2 = n4 = this.getScrollRange();
                            if (this.mExpandedInThisMotion) {
                                n4 = Math.min(a2, this.mMaxScrollAfterExpand);
                            }
                            float n5;
                            if (n2 < 0) {
                                n5 = this.overScrollDown(n2);
                            }
                            else {
                                n5 = this.overScrollUp(n2, n4);
                            }
                            if (n5 != 0.0f) {
                                this.customOverScrollBy((int)n5, this.mOwnScrollY, n4, this.getHeight() / 2);
                                this.checkSnoozeLeavebehind();
                            }
                        }
                    }
                }
            }
            else if (this.mIsBeingDragged) {
                final VelocityTracker mVelocityTracker = this.mVelocityTracker;
                mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
                final int a3 = (int)mVelocityTracker.getYVelocity(this.mActivePointerId);
                if (this.shouldOverScrollFling(a3)) {
                    this.onOverScrollFling(true, a3);
                }
                else if (this.getChildCount() > 0) {
                    if (Math.abs(a3) > this.mMinimumVelocity) {
                        if (this.getCurrentOverScrollAmount(true) != 0.0f && a3 <= 0) {
                            this.onOverScrollFling(false, a3);
                        }
                        else {
                            this.fling(-a3);
                        }
                    }
                    else if (this.mScroller.springBack(super.mScrollX, this.mOwnScrollY, 0, 0, 0, this.getScrollRange())) {
                        this.animateScroll();
                    }
                }
                this.mActivePointerId = -1;
                this.endDrag();
            }
        }
        else {
            if (this.getChildCount() == 0 || !this.isInContentBounds(motionEvent)) {
                return false;
            }
            this.setIsBeingDragged(this.mScroller.isFinished() ^ true);
            if (!this.mScroller.isFinished()) {
                this.mScroller.forceFinished(true);
            }
            this.mLastMotionY = (int)motionEvent.getY();
            this.mDownX = (int)motionEvent.getX();
            this.mActivePointerId = motionEvent.getPointerId(0);
        }
        return true;
    }
    
    private void onSecondaryPointerUp(final MotionEvent motionEvent) {
        final int n = (motionEvent.getAction() & 0xFF00) >> 8;
        if (motionEvent.getPointerId(n) == this.mActivePointerId) {
            int n2;
            if (n == 0) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            this.mLastMotionY = (int)motionEvent.getY(n2);
            this.mActivePointerId = motionEvent.getPointerId(n2);
            final VelocityTracker mVelocityTracker = this.mVelocityTracker;
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }
    
    private void onStatePostChange() {
        final boolean onKeyguard = this.onKeyguard();
        final HeadsUpAppearanceController mHeadsUpAppearanceController = this.mHeadsUpAppearanceController;
        if (mHeadsUpAppearanceController != null) {
            mHeadsUpAppearanceController.onStateChanged();
        }
        final SysuiStatusBarStateController sysuiStatusBarStateController = Dependency.get((Class<SysuiStatusBarStateController>)StatusBarStateController.class);
        this.updateSensitiveness(sysuiStatusBarStateController.goingToFullShade());
        this.setDimmed(onKeyguard, sysuiStatusBarStateController.fromShadeLocked());
        this.setExpandingEnabled(onKeyguard ^ true);
        final ActivatableNotificationView activatedChild = this.getActivatedChild();
        this.setActivatedChild(null);
        if (activatedChild != null) {
            activatedChild.makeInactive(false);
        }
        this.updateFooter();
        this.requestChildrenUpdate();
        this.onUpdateRowStates();
        this.mEntryManager.updateNotifications("StatusBar state changed");
        this.updateVisibility();
    }
    
    private void onViewAddedInternal(final ExpandableView expandableView) {
        this.updateHideSensitiveForChild(expandableView);
        expandableView.setOnHeightChangedListener((ExpandableView.OnHeightChangedListener)this);
        this.generateAddAnimation(expandableView, false);
        this.updateAnimationState((View)expandableView);
        this.updateChronometerForChild((View)expandableView);
        if (expandableView instanceof ExpandableNotificationRow) {
            ((ExpandableNotificationRow)expandableView).setDismissRtl(this.mDismissRtl);
        }
    }
    
    private void onViewRemovedInternal(final ExpandableView expandableView, final ViewGroup transientContainer) {
        if (this.mChangePositionInProgress) {
            return;
        }
        expandableView.setOnHeightChangedListener(null);
        this.updateScrollStateForRemovedChild(expandableView);
        if (this.generateRemoveAnimation(expandableView)) {
            if (!this.mSwipedOutViews.contains(expandableView) || Math.abs(expandableView.getTranslation()) != expandableView.getWidth()) {
                transientContainer.addTransientView((View)expandableView, 0);
                expandableView.setTransientContainer(transientContainer);
            }
        }
        else {
            this.mSwipedOutViews.remove(expandableView);
        }
        this.updateAnimationState(false, (View)expandableView);
        this.focusNextViewIfFocused((View)expandableView);
    }
    
    private float overScrollDown(int min) {
        min = Math.min(min, 0);
        final float currentOverScrollAmount = this.getCurrentOverScrollAmount(false);
        float n = min + currentOverScrollAmount;
        final float n2 = 0.0f;
        if (currentOverScrollAmount > 0.0f) {
            this.setOverScrollAmount(n, false, false);
        }
        if (n >= 0.0f) {
            n = 0.0f;
        }
        final float n3 = this.mOwnScrollY + n;
        if (n3 < 0.0f) {
            this.setOverScrolledPixels(this.getCurrentOverScrolledPixels(true) - n3, true, false);
            this.setOwnScrollY(0);
            n = n2;
        }
        return n;
    }
    
    private float overScrollUp(int max, final int ownScrollY) {
        max = Math.max(max, 0);
        final float currentOverScrollAmount = this.getCurrentOverScrollAmount(true);
        final float n = currentOverScrollAmount - max;
        final float n2 = 0.0f;
        if (currentOverScrollAmount > 0.0f) {
            this.setOverScrollAmount(n, true, false);
        }
        float n3;
        if (n < 0.0f) {
            n3 = -n;
        }
        else {
            n3 = 0.0f;
        }
        final float n4 = this.mOwnScrollY + n3;
        final float n5 = (float)ownScrollY;
        if (n4 > n5) {
            if (!this.mExpandedInThisMotion) {
                this.setOverScrolledPixels(this.getCurrentOverScrolledPixels(false) + n4 - n5, false, false);
            }
            this.setOwnScrollY(ownScrollY);
            n3 = n2;
        }
        return n3;
    }
    
    private void performDismissAllAnimations(final ArrayList<View> list, final boolean b, final Runnable runnable) {
        final _$$Lambda$NotificationStackScrollLayout$_wc1B8DN_KitBy8lCSoeeETqd6k $$Lambda$NotificationStackScrollLayout$_wc1B8DN_KitBy8lCSoeeETqd6k = new _$$Lambda$NotificationStackScrollLayout$_wc1B8DN_KitBy8lCSoeeETqd6k(this, b, runnable);
        if (list.isEmpty()) {
            $$Lambda$NotificationStackScrollLayout$_wc1B8DN_KitBy8lCSoeeETqd6k.run();
            return;
        }
        this.setDismissAllInProgress(true);
        int max = 140;
        int i = list.size();
        int n = 180;
        --i;
        while (i >= 0) {
            final View view = list.get(i);
            _$$Lambda$NotificationStackScrollLayout$_wc1B8DN_KitBy8lCSoeeETqd6k $$Lambda$NotificationStackScrollLayout$_wc1B8DN_KitBy8lCSoeeETqd6k2;
            if (i == 0) {
                $$Lambda$NotificationStackScrollLayout$_wc1B8DN_KitBy8lCSoeeETqd6k2 = $$Lambda$NotificationStackScrollLayout$_wc1B8DN_KitBy8lCSoeeETqd6k;
            }
            else {
                $$Lambda$NotificationStackScrollLayout$_wc1B8DN_KitBy8lCSoeeETqd6k2 = null;
            }
            this.dismissViewAnimated(view, $$Lambda$NotificationStackScrollLayout$_wc1B8DN_KitBy8lCSoeeETqd6k2, n, 260L);
            max = Math.max(50, max - 10);
            n += max;
            --i;
        }
    }
    
    private void recycleVelocityTracker() {
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
    
    private void reinflateViews() {
        this.inflateFooterView();
        this.inflateEmptyShadeView();
        this.updateFooter();
        this.mSectionsManager.reinflateViews(LayoutInflater.from(super.mContext));
    }
    
    private boolean removeRemovedChildFromHeadsUpChangeAnimations(final View view) {
        final Iterator<Pair<ExpandableNotificationRow, Boolean>> iterator = this.mHeadsUpChangeAnimations.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            final Pair<ExpandableNotificationRow, Boolean> e = iterator.next();
            final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)e.first;
            final boolean booleanValue = (boolean)e.second;
            if (view == expandableNotificationRow) {
                this.mTmpList.add(e);
                b |= booleanValue;
            }
        }
        if (b) {
            this.mHeadsUpChangeAnimations.removeAll(this.mTmpList);
            ((ExpandableNotificationRow)view).setHeadsUpAnimatingAway(false);
        }
        this.mTmpList.clear();
        return b;
    }
    
    private void requestAnimateEverything() {
        if (this.mIsExpanded && this.mAnimationsEnabled) {
            this.mEverythingNeedsAnimation = true;
            this.mNeedsAnimation = true;
            this.requestChildrenUpdate();
        }
    }
    
    private void requestAnimationOnViewResize(final ExpandableNotificationRow expandableNotificationRow) {
        if (this.mAnimationsEnabled && (this.mIsExpanded || (expandableNotificationRow != null && expandableNotificationRow.isPinned()))) {
            this.mNeedViewResizeAnimation = true;
            this.mNeedsAnimation = true;
        }
    }
    
    private void requestChildrenUpdate() {
        if (!this.mChildrenUpdateRequested) {
            this.getViewTreeObserver().addOnPreDrawListener(this.mChildrenUpdater);
            this.mChildrenUpdateRequested = true;
            this.invalidate();
        }
    }
    
    private void runAnimationFinishedRunnables() {
        final Iterator<Runnable> iterator = this.mAnimationFinishedRunnables.iterator();
        while (iterator.hasNext()) {
            iterator.next().run();
        }
        this.mAnimationFinishedRunnables.clear();
    }
    
    private void setDimAmount(final float mDimAmount) {
        this.mDimAmount = mDimAmount;
        this.updateBackgroundDimming();
    }
    
    private void setIsExpanded(final boolean isExpanded) {
        final boolean b = isExpanded != this.mIsExpanded;
        this.mIsExpanded = isExpanded;
        this.mStackScrollAlgorithm.setIsExpanded(isExpanded);
        this.mAmbientState.setShadeExpanded(isExpanded);
        this.mStateAnimator.setShadeExpanded(isExpanded);
        this.mSwipeHelper.setIsExpanded(isExpanded);
        if (b) {
            this.mWillExpand = false;
            if (!this.mIsExpanded) {
                this.mGroupManager.collapseAllGroups();
                this.mExpandHelper.cancelImmediately();
            }
            this.updateNotificationAnimationStates();
            this.updateChronometers();
            this.requestChildrenUpdate();
        }
    }
    
    private void setMaxLayoutHeight(final int n) {
        this.mMaxLayoutHeight = n;
        this.mShelf.setMaxLayoutHeight(n);
        this.updateAlgorithmHeightAndPadding();
    }
    
    private void setMaxOverScrollFromCurrentVelocity() {
        final float currVelocity = this.mScroller.getCurrVelocity();
        if (currVelocity >= this.mMinimumVelocity) {
            this.mMaxOverScroll = Math.abs(currVelocity) / 1000.0f * this.mOverflingDistance;
        }
    }
    
    private void setOverScrollAmountInternal(float max, final boolean b, final boolean b2, final boolean b3) {
        max = Math.max(0.0f, max);
        if (b2) {
            this.mStateAnimator.animateOverScrollToAmount(max, b, b3);
        }
        else {
            this.setOverScrolledPixels(max / this.getRubberBandFactor(b), b);
            this.mAmbientState.setOverScrollAmount(max, b);
            if (b) {
                this.notifyOverscrollTopListener(max, b3);
            }
            this.requestChildrenUpdate();
        }
    }
    
    private void setOverScrolledPixels(final float n, final boolean b) {
        if (b) {
            this.mOverScrolledTopPixels = n;
        }
        else {
            this.mOverScrolledBottomPixels = n;
        }
    }
    
    private void setOwnScrollY(final int mOwnScrollY) {
        final int mOwnScrollY2 = this.mOwnScrollY;
        if (mOwnScrollY != mOwnScrollY2) {
            final int mScrollX = super.mScrollX;
            this.onScrollChanged(mScrollX, mOwnScrollY, mScrollX, mOwnScrollY2);
            this.mOwnScrollY = mOwnScrollY;
            this.updateOnScrollChange();
        }
    }
    
    private void setRequestedClipBounds(final Rect mRequestedClipBounds) {
        this.mRequestedClipBounds = mRequestedClipBounds;
        this.updateClipping();
    }
    
    private void setStackTranslation(final float n) {
        if (n != this.mStackTranslation) {
            this.mStackTranslation = n;
            this.mAmbientState.setStackTranslation(n);
            this.requestChildrenUpdate();
        }
    }
    
    private void setSwipingInProgress(final boolean mSwipingInProgress) {
        this.mSwipingInProgress = mSwipingInProgress;
        if (mSwipingInProgress) {
            this.requestDisallowInterceptTouchEvent(true);
        }
    }
    
    private void setTopPadding(final int mTopPadding, final boolean b) {
        if (this.mTopPadding != mTopPadding) {
            this.mTopPadding = mTopPadding;
            this.updateAlgorithmHeightAndPadding();
            this.updateContentHeight();
            if (b && this.mAnimationsEnabled && this.mIsExpanded) {
                this.mTopPaddingNeedsAnimation = true;
                this.mNeedsAnimation = true;
            }
            this.requestChildrenUpdate();
            this.notifyHeightChangeListener(null, b);
        }
    }
    
    private boolean shouldHunAppearFromBottom(final ExpandableViewState expandableViewState) {
        return expandableViewState.yTranslation + expandableViewState.height >= this.mAmbientState.getMaxHeadsUpTranslation();
    }
    
    private boolean shouldOverScrollFling(final int n) {
        boolean b = true;
        final float currentOverScrollAmount = this.getCurrentOverScrollAmount(true);
        if (!this.mScrolledToTopOnFirstDown || this.mExpandedInThisMotion || currentOverScrollAmount <= this.mMinTopOverScrollToEscape || n <= 0) {
            b = false;
        }
        return b;
    }
    
    private void snapViewIfNeeded(final NotificationEntry notificationEntry) {
        final ExpandableNotificationRow row = notificationEntry.getRow();
        final boolean b = this.mIsExpanded || isPinnedHeadsUp((View)row);
        if (row.getProvider() != null) {
            float translation;
            if (row.getProvider().isMenuVisible()) {
                translation = row.getTranslation();
            }
            else {
                translation = 0.0f;
            }
            this.mSwipeHelper.snapChildIfNeeded((View)row, b, translation);
        }
    }
    
    private void springBack() {
        final int scrollRange = this.getScrollRange();
        final boolean b = this.mOwnScrollY <= 0;
        final boolean b2 = this.mOwnScrollY >= scrollRange;
        if (b || b2) {
            float n;
            boolean b3;
            if (b) {
                n = (float)(-this.mOwnScrollY);
                this.setOwnScrollY(0);
                this.mDontReportNextOverScroll = true;
                b3 = true;
            }
            else {
                n = (float)(this.mOwnScrollY - scrollRange);
                this.setOwnScrollY(scrollRange);
                b3 = false;
            }
            this.setOverScrollAmount(n, b3, false);
            this.setOverScrollAmount(0.0f, b3, true);
            this.mScroller.forceFinished(true);
        }
    }
    
    private void startAnimationToState() {
        if (this.mNeedsAnimation) {
            this.generateAllAnimationEvents();
            this.mNeedsAnimation = false;
        }
        if (this.mAnimationEvents.isEmpty() && !this.isCurrentlyAnimating()) {
            this.applyCurrentState();
        }
        else {
            this.setAnimationRunning(true);
            this.mStateAnimator.startAnimationForEvents(this.mAnimationEvents, this.mGoToFullShadeDelay);
            this.mAnimationEvents.clear();
            this.updateBackground();
            this.updateViewShadows();
            this.updateClippingToTopRoundedCorner();
        }
        this.mGoToFullShadeDelay = 0L;
    }
    
    private void startBackgroundAnimation() {
        final NotificationSection firstVisibleSection = this.getFirstVisibleSection();
        final NotificationSection lastVisibleSection = this.getLastVisibleSection();
        for (final NotificationSection notificationSection : this.mSections) {
            boolean b;
            if (notificationSection == firstVisibleSection) {
                b = this.mAnimateNextBackgroundTop;
            }
            else {
                b = this.mAnimateNextSectionBoundsChange;
            }
            boolean b2;
            if (notificationSection == lastVisibleSection) {
                b2 = this.mAnimateNextBackgroundBottom;
            }
            else {
                b2 = this.mAnimateNextSectionBoundsChange;
            }
            notificationSection.startBackgroundAnimation(b, b2);
        }
    }
    
    private int targetScrollForView(final ExpandableView expandableView, final int n) {
        final int intrinsicHeight = expandableView.getIntrinsicHeight();
        final int imeInset = this.getImeInset();
        final int height = this.getHeight();
        int n2;
        if (!this.isExpanded() && isPinnedHeadsUp((View)expandableView)) {
            n2 = this.mHeadsUpInset;
        }
        else {
            n2 = this.getTopPadding();
        }
        return n + intrinsicHeight + imeInset - height + n2;
    }
    
    private void updateAlgorithmHeightAndPadding() {
        this.mAmbientState.setLayoutHeight(this.getLayoutHeight());
        this.updateAlgorithmLayoutMinHeight();
        this.mAmbientState.setTopPadding(this.mTopPadding);
    }
    
    private void updateAlgorithmLayoutMinHeight() {
        final AmbientState mAmbientState = this.mAmbientState;
        int layoutMinHeight;
        if (!this.mQsExpanded && !this.isHeadsUpTransition()) {
            layoutMinHeight = 0;
        }
        else {
            layoutMinHeight = this.getLayoutMinHeight();
        }
        mAmbientState.setLayoutMinHeight(layoutMinHeight);
    }
    
    private void updateAnimationState(final View view) {
        this.updateAnimationState((this.mAnimationsEnabled || this.hasPulsingNotifications()) && (this.mIsExpanded || isPinnedHeadsUp(view)), view);
    }
    
    private void updateAnimationState(final boolean iconAnimationRunning, final View view) {
        if (view instanceof ExpandableNotificationRow) {
            ((ExpandableNotificationRow)view).setIconAnimationRunning(iconAnimationRunning);
        }
    }
    
    private void updateBackground() {
        if (!this.mShouldDrawNotificationBackground) {
            return;
        }
        this.updateBackgroundBounds();
        if (this.didSectionBoundsChange()) {
            boolean b = this.mAnimateNextSectionBoundsChange || this.mAnimateNextBackgroundTop || this.mAnimateNextBackgroundBottom || this.areSectionBoundsAnimating();
            if (!this.isExpanded()) {
                this.abortBackgroundAnimators();
                b = false;
            }
            if (b) {
                this.startBackgroundAnimation();
            }
            else {
                final NotificationSection[] mSections = this.mSections;
                for (int length = mSections.length, i = 0; i < length; ++i) {
                    mSections[i].resetCurrentBounds();
                }
                this.invalidate();
            }
        }
        else {
            this.abortBackgroundAnimators();
        }
        this.mAnimateNextBackgroundTop = false;
        this.mAnimateNextBackgroundBottom = false;
        this.mAnimateNextSectionBoundsChange = false;
    }
    
    private void updateBackgroundBounds() {
        final int mSidePaddings = this.mSidePaddings;
        final int width = this.getWidth();
        final int mSidePaddings2 = this.mSidePaddings;
        for (final NotificationSection notificationSection : this.mSections) {
            notificationSection.getBounds().left = mSidePaddings;
            notificationSection.getBounds().right = width - mSidePaddings2;
        }
        if (!this.mIsExpanded) {
            for (final NotificationSection notificationSection2 : this.mSections) {
                notificationSection2.getBounds().top = 0;
                notificationSection2.getBounds().bottom = 0;
            }
            return;
        }
        final NotificationSection lastVisibleSection = this.getLastVisibleSection();
        final int mStatusBarState = this.mStatusBarState;
        final boolean b = true;
        final boolean b2 = mStatusBarState == 1;
        int n;
        if (!b2) {
            n = (int)(this.mTopPadding + this.mStackTranslation);
        }
        else if (lastVisibleSection == null) {
            n = this.mTopPadding;
        }
        else {
            final NotificationSection firstVisibleSection = this.getFirstVisibleSection();
            firstVisibleSection.updateBounds(0, 0, false);
            n = firstVisibleSection.getBounds().top;
        }
        boolean b3 = false;
        Label_0269: {
            if (this.mHeadsUpManager.getAllEntries().count() <= 1L) {
                b3 = b;
                if (this.mAmbientState.isDozing()) {
                    break Label_0269;
                }
                if (this.mKeyguardBypassController.getBypassEnabled() && b2) {
                    b3 = b;
                    break Label_0269;
                }
            }
            b3 = false;
        }
        final NotificationSection[] mSections3 = this.mSections;
        for (int length3 = mSections3.length, k = 0; k < length3; ++k, b3 = false) {
            final NotificationSection notificationSection3 = mSections3[k];
            int n2;
            if (notificationSection3 == lastVisibleSection) {
                n2 = (int)(ViewState.getFinalTranslationY((View)this.mShelf) + this.mShelf.getIntrinsicHeight());
            }
            else {
                n2 = n;
            }
            n = notificationSection3.updateBounds(n, n2, b3);
        }
    }
    
    private void updateBackgroundDimming() {
        if (!this.mShouldDrawNotificationBackground) {
            return;
        }
        final int blendARGB = ColorUtils.blendARGB(this.mBgColor, -1, MathUtils.smoothStep(0.4f, 1.0f, this.mLinearHideAmount));
        if (this.mCachedBackgroundColor != blendARGB) {
            this.mCachedBackgroundColor = blendARGB;
            this.mBackgroundPaint.setColor(blendARGB);
            this.invalidate();
        }
    }
    
    private void updateChildren() {
        this.updateScrollStateForAddedChildren();
        final AmbientState mAmbientState = this.mAmbientState;
        float currVelocity;
        if (this.mScroller.isFinished()) {
            currVelocity = 0.0f;
        }
        else {
            currVelocity = this.mScroller.getCurrVelocity();
        }
        mAmbientState.setCurrentScrollVelocity(currVelocity);
        this.mAmbientState.setScrollY(this.mOwnScrollY);
        this.mStackScrollAlgorithm.resetViewStates(this.mAmbientState);
        if (!this.isCurrentlyAnimating() && !this.mNeedsAnimation) {
            this.applyCurrentState();
        }
        else {
            this.startAnimationToState();
        }
    }
    
    private void updateChronometerForChild(final View view) {
        if (view instanceof ExpandableNotificationRow) {
            ((ExpandableNotificationRow)view).setChronometerRunning(this.mIsExpanded);
        }
    }
    
    private void updateChronometers() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            this.updateChronometerForChild(this.getChildAt(i));
        }
    }
    
    private void updateClippingToTopRoundedCorner() {
        final Float value = this.mTopPadding + this.mStackTranslation + this.mAmbientState.getExpandAnimationTopChange();
        final Float value2 = value + this.mCornerRadius;
        int i = 0;
        int n = 1;
        while (i < this.getChildCount()) {
            final ExpandableView expandableView = (ExpandableView)this.getChildAt(i);
            if (expandableView.getVisibility() != 8) {
                final float translationY = expandableView.getTranslationY();
                final float n2 = expandableView.getActualHeight() + translationY;
                float max;
                if ((n == 0 || !this.isScrolledToTop()) & ((value > translationY && value < n2) || (value2 >= translationY && value2 <= n2))) {
                    max = Math.max(translationY - value, 0.0f);
                }
                else {
                    max = -1.0f;
                }
                expandableView.setDistanceToTopRoundness(max);
                n = 0;
            }
            ++i;
        }
    }
    
    private void updateContentHeight() {
        float n = (float)this.mPaddingBetweenElements;
        final int mMaxDisplayedNotifications = this.mMaxDisplayedNotifications;
        float n2 = 0.0f;
        final int n4;
        final int n3 = n4 = 0;
        int n6;
        int n5 = n6 = n4;
        int n7 = n4;
        int n8 = n3;
        int mIntrinsicContentHeight;
        while (true) {
            mIntrinsicContentHeight = n5;
            if (n8 >= this.getChildCount()) {
                break;
            }
            ExpandableView mShelf = (ExpandableView)this.getChildAt(n8);
            final boolean b = mShelf == this.mFooterView && this.onKeyguard();
            float n9 = n;
            int n10 = n7;
            float n11 = n2;
            mIntrinsicContentHeight = n5;
            int n12 = n6;
            if (mShelf.getVisibility() != 8) {
                n9 = n;
                n10 = n7;
                n11 = n2;
                mIntrinsicContentHeight = n5;
                n12 = n6;
                if (!mShelf.hasNoContentHeight()) {
                    n9 = n;
                    n10 = n7;
                    n11 = n2;
                    mIntrinsicContentHeight = n5;
                    n12 = n6;
                    if (!b) {
                        if (mMaxDisplayedNotifications != -1 && n7 >= mMaxDisplayedNotifications) {
                            mShelf = this.mShelf;
                            n6 = 1;
                        }
                        final float increasedPaddingAmount = mShelf.getIncreasedPaddingAmount();
                        float n13;
                        float n14;
                        if (increasedPaddingAmount >= 0.0f) {
                            n13 = (float)(int)NotificationUtils.interpolate(n, (float)this.mIncreasedPaddingBetweenElements, increasedPaddingAmount);
                            n14 = (float)(int)NotificationUtils.interpolate((float)this.mPaddingBetweenElements, (float)this.mIncreasedPaddingBetweenElements, increasedPaddingAmount);
                        }
                        else {
                            final int n15 = (int)NotificationUtils.interpolate(0.0f, (float)this.mPaddingBetweenElements, 1.0f + increasedPaddingAmount);
                            float n16;
                            if (n2 > 0.0f) {
                                n16 = (float)(int)NotificationUtils.interpolate((float)n15, (float)this.mIncreasedPaddingBetweenElements, n2);
                            }
                            else {
                                n16 = (float)n15;
                            }
                            final float n17 = (float)n15;
                            n13 = n16;
                            n14 = n17;
                        }
                        int n18 = n5;
                        if (n5 != 0) {
                            n18 = (int)(n5 + n13);
                        }
                        mIntrinsicContentHeight = n18 + mShelf.getIntrinsicHeight();
                        n10 = n7 + 1;
                        if (n6 != 0) {
                            break;
                        }
                        n11 = increasedPaddingAmount;
                        n12 = n6;
                        n9 = n14;
                    }
                }
            }
            ++n8;
            n = n9;
            n7 = n10;
            n2 = n11;
            n5 = mIntrinsicContentHeight;
            n6 = n12;
        }
        this.mIntrinsicContentHeight = mIntrinsicContentHeight;
        this.mContentHeight = mIntrinsicContentHeight + Math.max(this.mIntrinsicPadding, this.mTopPadding) + this.mBottomMargin;
        this.updateScrollability();
        this.clampScrollPosition();
        this.mAmbientState.setLayoutMaxHeight(this.mContentHeight);
    }
    
    private void updateContinuousBackgroundDrawing() {
        final boolean mContinuousBackgroundUpdate = !this.mAmbientState.isFullyAwake() && !this.mAmbientState.getDraggedViews().isEmpty();
        if (mContinuousBackgroundUpdate != this.mContinuousBackgroundUpdate) {
            this.mContinuousBackgroundUpdate = mContinuousBackgroundUpdate;
            if (mContinuousBackgroundUpdate) {
                this.getViewTreeObserver().addOnPreDrawListener(this.mBackgroundUpdater);
            }
            else {
                this.getViewTreeObserver().removeOnPreDrawListener(this.mBackgroundUpdater);
            }
        }
    }
    
    private void updateContinuousShadowDrawing() {
        final boolean mContinuousShadowUpdate = this.mAnimationRunning || !this.mAmbientState.getDraggedViews().isEmpty();
        if (mContinuousShadowUpdate != this.mContinuousShadowUpdate) {
            if (mContinuousShadowUpdate) {
                this.getViewTreeObserver().addOnPreDrawListener(this.mShadowUpdater);
            }
            else {
                this.getViewTreeObserver().removeOnPreDrawListener(this.mShadowUpdater);
            }
            this.mContinuousShadowUpdate = mContinuousShadowUpdate;
        }
    }
    
    private void updateDismissRtlSetting(final boolean b) {
        this.mDismissRtl = b;
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            if (child instanceof ExpandableNotificationRow) {
                ((ExpandableNotificationRow)child).setDismissRtl(b);
            }
        }
    }
    
    private void updateFirstAndLastBackgroundViews() {
        final NotificationSection firstVisibleSection = this.getFirstVisibleSection();
        final NotificationSection lastVisibleSection = this.getLastVisibleSection();
        ActivatableNotificationView lastVisibleChild = null;
        ActivatableNotificationView firstVisibleChild;
        if (firstVisibleSection == null) {
            firstVisibleChild = null;
        }
        else {
            firstVisibleChild = firstVisibleSection.getFirstVisibleChild();
        }
        if (lastVisibleSection != null) {
            lastVisibleChild = lastVisibleSection.getLastVisibleChild();
        }
        final ActivatableNotificationView firstChildWithBackground = this.getFirstChildWithBackground();
        final ActivatableNotificationView lastChildWithBackground = this.getLastChildWithBackground();
        final boolean updateFirstAndLastViewsForAllSections = this.mSectionsManager.updateFirstAndLastViewsForAllSections(this.mSections, this.getChildrenWithBackground());
        if (this.mAnimationsEnabled && this.mIsExpanded) {
            final boolean b = true;
            this.mAnimateNextBackgroundTop = (firstChildWithBackground != firstVisibleChild);
            boolean mAnimateNextBackgroundBottom = b;
            if (lastChildWithBackground == lastVisibleChild) {
                mAnimateNextBackgroundBottom = (this.mAnimateBottomOnLayout && b);
            }
            this.mAnimateNextBackgroundBottom = mAnimateNextBackgroundBottom;
            this.mAnimateNextSectionBoundsChange = updateFirstAndLastViewsForAllSections;
        }
        else {
            this.mAnimateNextBackgroundTop = false;
            this.mAnimateNextBackgroundBottom = false;
            this.mAnimateNextSectionBoundsChange = false;
        }
        this.mAmbientState.setLastVisibleBackgroundChild(lastChildWithBackground);
        this.mRoundnessManager.updateRoundedChildren(this.mSections);
        this.mAnimateBottomOnLayout = false;
        this.invalidate();
    }
    
    private void updateForcedScroll() {
        final View mForcedScroll = this.mForcedScroll;
        if (mForcedScroll != null && (!mForcedScroll.hasFocus() || !this.mForcedScroll.isAttachedToWindow())) {
            this.mForcedScroll = null;
        }
        final View mForcedScroll2 = this.mForcedScroll;
        if (mForcedScroll2 != null) {
            final ExpandableView expandableView = (ExpandableView)mForcedScroll2;
            final int positionInLinearLayout = this.getPositionInLinearLayout((View)expandableView);
            final int targetScrollForView = this.targetScrollForView(expandableView, positionInLinearLayout);
            final int intrinsicHeight = expandableView.getIntrinsicHeight();
            final int max = Math.max(0, Math.min(targetScrollForView, this.getScrollRange()));
            final int mOwnScrollY = this.mOwnScrollY;
            if (mOwnScrollY < max || positionInLinearLayout + intrinsicHeight < mOwnScrollY) {
                this.setOwnScrollY(max);
            }
        }
    }
    
    private void updateForwardAndBackwardScrollability() {
        final boolean mScrollable = this.mScrollable;
        final boolean b = true;
        final boolean mForwardScrollable = mScrollable && !this.isScrolledToBottom();
        final boolean mBackwardScrollable = this.mScrollable && !this.isScrolledToTop();
        int n = b ? 1 : 0;
        if (mForwardScrollable == this.mForwardScrollable) {
            if (mBackwardScrollable != this.mBackwardScrollable) {
                n = (b ? 1 : 0);
            }
            else {
                n = 0;
            }
        }
        this.mForwardScrollable = mForwardScrollable;
        this.mBackwardScrollable = mBackwardScrollable;
        if (n != 0) {
            this.sendAccessibilityEvent(2048);
        }
    }
    
    private void updateHideSensitiveForChild(final ExpandableView expandableView) {
        expandableView.setHideSensitiveForIntrinsicHeight(this.mAmbientState.isHideSensitive());
    }
    
    private void updateNotificationAnimationStates() {
        boolean animationsEnabled = this.mAnimationsEnabled || this.hasPulsingNotifications();
        this.mShelf.setAnimationsEnabled(animationsEnabled);
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            animationsEnabled &= (this.mIsExpanded || isPinnedHeadsUp(child));
            this.updateAnimationState(animationsEnabled, child);
        }
    }
    
    private void updateOnScrollChange() {
        this.updateForwardAndBackwardScrollability();
        this.requestChildrenUpdate();
    }
    
    private void updateOwnTranslationZ() {
        float translationZ = 0.0f;
        Label_0046: {
            if (this.mKeyguardBypassController.getBypassEnabled() && this.mAmbientState.isHiddenAtAll()) {
                final ExpandableView firstChildNotGone = this.getFirstChildNotGone();
                if (firstChildNotGone != null && firstChildNotGone.showingPulsing()) {
                    translationZ = firstChildNotGone.getTranslationZ();
                    break Label_0046;
                }
            }
            translationZ = 0.0f;
        }
        this.setTranslationZ(translationZ);
    }
    
    private void updateScrollPositionOnExpandInBottom(final ExpandableView expandableView) {
        if (expandableView instanceof ExpandableNotificationRow && !this.onKeyguard()) {
            final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)expandableView;
            if (expandableNotificationRow.isUserLocked() && expandableNotificationRow != this.getFirstChildNotGone()) {
                if (expandableNotificationRow.isSummaryWithChildren()) {
                    return;
                }
                float n2;
                final float n = n2 = expandableNotificationRow.getTranslationY() + expandableNotificationRow.getActualHeight();
                if (expandableNotificationRow.isChildInGroup()) {
                    n2 = n + expandableNotificationRow.getNotificationParent().getTranslationY();
                }
                final int n3 = this.mMaxLayoutHeight + (int)this.mStackTranslation;
                final NotificationSection lastVisibleSection = this.getLastVisibleSection();
                ActivatableNotificationView lastVisibleChild;
                if (lastVisibleSection == null) {
                    lastVisibleChild = null;
                }
                else {
                    lastVisibleChild = lastVisibleSection.getLastVisibleChild();
                }
                int n4 = n3;
                if (expandableNotificationRow != lastVisibleChild) {
                    n4 = n3;
                    if (this.mShelf.getVisibility() != 8) {
                        n4 = n3 - (this.mShelf.getIntrinsicHeight() + this.mPaddingBetweenElements);
                    }
                }
                final float n5 = (float)n4;
                if (n2 > n5) {
                    this.setOwnScrollY((int)(this.mOwnScrollY + n2 - n5));
                    this.mDisallowScrollingInThisMotion = true;
                }
            }
        }
    }
    
    private void updateScrollStateForAddedChildren() {
        if (this.mChildrenToAddAnimated.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.getChildCount(); ++i) {
            final ExpandableView o = (ExpandableView)this.getChildAt(i);
            if (this.mChildrenToAddAnimated.contains(o)) {
                final int positionInLinearLayout = this.getPositionInLinearLayout((View)o);
                final float increasedPaddingAmount = o.getIncreasedPaddingAmount();
                int n;
                if (increasedPaddingAmount == 1.0f) {
                    n = this.mIncreasedPaddingBetweenElements;
                }
                else if (increasedPaddingAmount == -1.0f) {
                    n = 0;
                }
                else {
                    n = this.mPaddingBetweenElements;
                }
                final int intrinsicHeight = this.getIntrinsicHeight((View)o);
                final int mOwnScrollY = this.mOwnScrollY;
                if (positionInLinearLayout < mOwnScrollY) {
                    this.setOwnScrollY(mOwnScrollY + (intrinsicHeight + n));
                }
            }
        }
        this.clampScrollPosition();
    }
    
    private void updateScrollStateForRemovedChild(final ExpandableView expandableView) {
        final int positionInLinearLayout = this.getPositionInLinearLayout((View)expandableView);
        final float increasedPaddingAmount = expandableView.getIncreasedPaddingAmount();
        float n;
        if (increasedPaddingAmount >= 0.0f) {
            n = NotificationUtils.interpolate((float)this.mPaddingBetweenElements, (float)this.mIncreasedPaddingBetweenElements, increasedPaddingAmount);
        }
        else {
            n = NotificationUtils.interpolate(0.0f, (float)this.mPaddingBetweenElements, increasedPaddingAmount + 1.0f);
        }
        final int n2 = this.getIntrinsicHeight((View)expandableView) + (int)n;
        final int mOwnScrollY = this.mOwnScrollY;
        if (positionInLinearLayout + n2 <= mOwnScrollY) {
            this.setOwnScrollY(mOwnScrollY - n2);
        }
        else if (positionInLinearLayout < mOwnScrollY) {
            this.setOwnScrollY(positionInLinearLayout);
        }
    }
    
    private void updateScrollability() {
        final boolean mScrollable = !this.mQsExpanded && this.getScrollRange() > 0;
        if (mScrollable != this.mScrollable) {
            this.setFocusable(this.mScrollable = mScrollable);
            this.updateForwardAndBackwardScrollability();
        }
    }
    
    private void updateSensitiveness(final boolean b) {
        final boolean anyProfilePublicMode = this.mLockscreenUserManager.isAnyProfilePublicMode();
        if (anyProfilePublicMode != this.mAmbientState.isHideSensitive()) {
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                ((ExpandableView)this.getChildAt(i)).setHideSensitiveForIntrinsicHeight(anyProfilePublicMode);
            }
            this.mAmbientState.setHideSensitive(anyProfilePublicMode);
            if (b && this.mAnimationsEnabled) {
                this.mHideSensitiveNeedsAnimation = true;
                this.mNeedsAnimation = true;
            }
            this.updateContentHeight();
            this.requestChildrenUpdate();
        }
    }
    
    private void updateViewShadows() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final ExpandableView e = (ExpandableView)this.getChildAt(i);
            if (e.getVisibility() != 8) {
                this.mTmpSortedChildren.add(e);
            }
        }
        Collections.sort(this.mTmpSortedChildren, this.mViewPositionComparator);
        ExpandableView expandableView = null;
        ExpandableView expandableView2;
        for (int j = 0; j < this.mTmpSortedChildren.size(); ++j, expandableView = expandableView2) {
            expandableView2 = this.mTmpSortedChildren.get(j);
            final float translationZ = expandableView2.getTranslationZ();
            float translationZ2;
            if (expandableView == null) {
                translationZ2 = translationZ;
            }
            else {
                translationZ2 = expandableView.getTranslationZ();
            }
            final float n = translationZ2 - translationZ;
            if (n > 0.0f && n < 0.1f) {
                expandableView2.setFakeShadowIntensity(n / 0.1f, expandableView.getOutlineAlpha(), (int)(expandableView.getTranslationY() + expandableView.getActualHeight() - expandableView2.getTranslationY() - expandableView.getExtraBottomPadding()), expandableView.getOutlineTranslation());
            }
            else {
                expandableView2.setFakeShadowIntensity(0.0f, 0.0f, 0, 0);
            }
        }
        this.mTmpSortedChildren.clear();
    }
    
    private void updateVisibility() {
        final boolean fullyHidden = this.mAmbientState.isFullyHidden();
        final int n = 0;
        int visibility;
        if (!fullyHidden || !this.onKeyguard()) {
            visibility = n;
        }
        else {
            visibility = 4;
        }
        this.setVisibility(visibility);
    }
    
    public void addContainerView(final View view) {
        Assert.isMainThread();
        this.addView(view);
    }
    
    public void addListItem(final NotificationListItem notificationListItem) {
        this.addContainerView(notificationListItem.getView());
    }
    
    public void addOnExpandedHeightChangedListener(final BiConsumer<Float, Float> e) {
        this.mExpandedHeightListeners.add(e);
    }
    
    public void applyExpandAnimationParams(final ActivityLaunchAnimator.ExpandAnimationParameters expandAnimationParameters) {
        final AmbientState mAmbientState = this.mAmbientState;
        int topChange;
        if (expandAnimationParameters == null) {
            topChange = 0;
        }
        else {
            topChange = expandAnimationParameters.getTopChange();
        }
        mAmbientState.setExpandAnimationTopChange(topChange);
        this.requestChildrenUpdate();
    }
    
    public void bindRow(final ExpandableNotificationRow expandableNotificationRow) {
        expandableNotificationRow.setHeadsUpAnimatingAwayListener(new _$$Lambda$NotificationStackScrollLayout$vyqGeK1IJVIh_l8qPCKfOsTCmEY(this, expandableNotificationRow));
    }
    
    public float calculateAppearFraction(final float n) {
        final float appearEndPosition = this.getAppearEndPosition();
        final float appearStartPosition = this.getAppearStartPosition();
        return (n - appearStartPosition) / (appearEndPosition - appearStartPosition);
    }
    
    public float calculateAppearFractionBypass() {
        return MathUtils.smoothStep(0.0f, (float)this.getIntrinsicPadding(), this.getPulseHeight() - this.getWakeUpHeight());
    }
    
    public void cancelExpandHelper() {
        this.mExpandHelper.cancel();
    }
    
    public void cancelLongPress() {
        this.mSwipeHelper.cancelLongPress();
    }
    
    public void changeViewPosition(final ExpandableView expandableView, int n) {
        Assert.isMainThread();
        if (this.mChangePositionInProgress) {
            throw new IllegalStateException("Reentrant call to changeViewPosition");
        }
        final int indexOfChild = this.indexOfChild((View)expandableView);
        final int n2 = 0;
        if (indexOfChild == -1) {
            n = n2;
            if (expandableView instanceof ExpandableNotificationRow) {
                n = n2;
                if (expandableView.getTransientContainer() != null) {
                    n = 1;
                }
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("Attempting to re-position ");
            String str;
            if (n != 0) {
                str = "transient";
            }
            else {
                str = "";
            }
            sb.append(str);
            sb.append(" view {");
            sb.append(expandableView);
            sb.append("}");
            Log.e("StackScroller", sb.toString());
            return;
        }
        if (expandableView != null && expandableView.getParent() == this && indexOfChild != n) {
            expandableView.setChangingPosition(this.mChangePositionInProgress = true);
            this.removeView((View)expandableView);
            this.addView((View)expandableView, n);
            expandableView.setChangingPosition(false);
            this.mChangePositionInProgress = false;
            if (this.mIsExpanded && this.mAnimationsEnabled && expandableView.getVisibility() != 8) {
                this.mChildrenChangingPositions.add(expandableView);
                this.mNeedsAnimation = true;
            }
        }
    }
    
    public void checkSnoozeLeavebehind() {
        if (this.mCheckForLeavebehind) {
            this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
            this.mCheckForLeavebehind = false;
        }
    }
    
    public void cleanUpViewStateForEntry(final NotificationEntry notificationEntry) {
        if (notificationEntry.getRow() == this.mSwipeHelper.getTranslatingParentView()) {
            this.mSwipeHelper.clearTranslatingParentView();
        }
    }
    
    public void clearChildFocus(final View view) {
        super.clearChildFocus(view);
        if (this.mForcedScroll == view) {
            this.mForcedScroll = null;
        }
    }
    
    @VisibleForTesting
    void clearNotifications(final int n, final boolean b) {
        final int childCount = this.getChildCount();
        final ArrayList list = new ArrayList<View>(childCount);
        final ArrayList list2 = new ArrayList<ExpandableNotificationRow>(childCount);
        for (int i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow e = (ExpandableNotificationRow)child;
                final boolean clipBounds = child.getClipBounds(this.mTmpRect);
                final boolean includeChildInDismissAll = this.includeChildInDismissAll(e, n);
                final int n2 = 1;
                int n3 = 0;
                Label_0168: {
                    if (includeChildInDismissAll) {
                        list2.add(e);
                        if (child.getVisibility() == 0 && (!clipBounds || this.mTmpRect.height() > 0)) {
                            list.add(child);
                            n3 = n2;
                            break Label_0168;
                        }
                    }
                    else if (child.getVisibility() == 0) {
                        n3 = n2;
                        if (!clipBounds) {
                            break Label_0168;
                        }
                        if (this.mTmpRect.height() > 0) {
                            n3 = n2;
                            break Label_0168;
                        }
                    }
                    n3 = 0;
                }
                final List<ExpandableNotificationRow> notificationChildren = e.getNotificationChildren();
                if (notificationChildren != null) {
                    for (final ExpandableNotificationRow expandableNotificationRow : notificationChildren) {
                        if (this.includeChildInDismissAll(e, n)) {
                            list2.add(expandableNotificationRow);
                            if (n3 == 0 || !e.areChildrenExpanded()) {
                                continue;
                            }
                            final boolean clipBounds2 = expandableNotificationRow.getClipBounds(this.mTmpRect);
                            if (expandableNotificationRow.getVisibility() != 0 || (clipBounds2 && this.mTmpRect.height() <= 0)) {
                                continue;
                            }
                            list.add((View)expandableNotificationRow);
                        }
                    }
                }
            }
        }
        this.mUiEventLogger.log(NotificationPanelEvent.fromSelection(n));
        if (list2.isEmpty()) {
            if (b) {
                Dependency.get(ShadeController.class).animateCollapsePanels(0);
            }
            return;
        }
        this.performDismissAllAnimations((ArrayList<View>)list, b, new _$$Lambda$NotificationStackScrollLayout$1JoW9tMXjFe_6yv5uN3FfACI74A(this, list2, n));
    }
    
    public void closeControlsIfOutsideTouch(final MotionEvent motionEvent) {
        final NotificationGuts exposedGuts = this.mNotificationGutsManager.getExposedGuts();
        final NotificationMenuRowPlugin currentMenuRow = this.mSwipeHelper.getCurrentMenuRow();
        Object translatingParentView = this.mSwipeHelper.getTranslatingParentView();
        if (exposedGuts != null && !exposedGuts.getGutsContent().isLeavebehind()) {
            translatingParentView = exposedGuts;
        }
        else if (currentMenuRow == null || !currentMenuRow.isMenuVisible() || translatingParentView == null) {
            translatingParentView = null;
        }
        if (translatingParentView != null && !NotificationSwipeHelper.isTouchInView(motionEvent, (View)translatingParentView)) {
            this.mNotificationGutsManager.closeAndSaveGuts(false, false, true, -1, -1, false);
            this.resetExposedMenuView(true, true);
        }
    }
    
    public boolean containsView(final View view) {
        return view.getParent() == this;
    }
    
    public RemoteInputController.Delegate createDelegate() {
        return new RemoteInputController.Delegate() {
            @Override
            public void lockScrollTo(final NotificationEntry notificationEntry) {
                NotificationStackScrollLayout.this.lockScrollTo((View)notificationEntry.getRow());
            }
            
            @Override
            public void requestDisallowLongPressAndDismiss() {
                NotificationStackScrollLayout.this.requestDisallowLongPress();
                NotificationStackScrollLayout.this.requestDisallowDismiss();
            }
            
            @Override
            public void setRemoteInputActive(final NotificationEntry notificationEntry, final boolean b) {
                NotificationStackScrollLayout.this.mHeadsUpManager.setRemoteInputActive(notificationEntry, b);
                notificationEntry.notifyHeightChanged(true);
                NotificationStackScrollLayout.this.updateFooter();
            }
        };
    }
    
    protected StackScrollAlgorithm createStackScrollAlgorithm(final Context context) {
        return new StackScrollAlgorithm(context, this);
    }
    
    public void dismissViewAnimated(final View view, final Runnable runnable, final int n, final long n2) {
        this.mSwipeHelper.dismissChild(view, 0.0f, runnable, n, true, n2, true);
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final String simpleName = NotificationStackScrollLayout.class.getSimpleName();
        final int n = 0;
        final boolean mPulsing = this.mPulsing;
        String s = "T";
        String s2;
        if (mPulsing) {
            s2 = "T";
        }
        else {
            s2 = "f";
        }
        String s3;
        if (this.mAmbientState.isQsCustomizerShowing()) {
            s3 = "T";
        }
        else {
            s3 = "f";
        }
        String s4;
        if (this.getVisibility() == 0) {
            s4 = "visible";
        }
        else if (this.getVisibility() == 8) {
            s4 = "gone";
        }
        else {
            s4 = "invisible";
        }
        final float alpha = this.getAlpha();
        final int scrollY = this.mAmbientState.getScrollY();
        final int mMaxTopPadding = this.mMaxTopPadding;
        if (!this.mShouldShowShelfOnly) {
            s = "f";
        }
        printWriter.println(String.format("[%s: pulsing=%s qsCustomizerShowing=%s visibility=%s alpha:%f scrollY:%d maxTopPadding:%d showShelfOnly=%s qsExpandFraction=%f]", simpleName, s2, s3, s4, alpha, scrollY, mMaxTopPadding, s, this.mQsExpansionFraction));
        final int childCount = this.getChildCount();
        final StringBuilder sb = new StringBuilder();
        sb.append("  Number of children: ");
        sb.append(childCount);
        printWriter.println(sb.toString());
        printWriter.println();
        for (int i = 0; i < childCount; ++i) {
            final ExpandableView expandableView = (ExpandableView)this.getChildAt(i);
            expandableView.dump(fileDescriptor, printWriter, array);
            if (!(expandableView instanceof ExpandableNotificationRow)) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("  ");
                sb2.append(expandableView.getClass().getSimpleName());
                printWriter.println(sb2.toString());
                final ExpandableViewState viewState = expandableView.getViewState();
                if (viewState == null) {
                    printWriter.println("    no viewState!!!");
                }
                else {
                    printWriter.print("    ");
                    viewState.dump(fileDescriptor, printWriter, array);
                    printWriter.println();
                    printWriter.println();
                }
            }
        }
        final int transientViewCount = this.getTransientViewCount();
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  Transient Views: ");
        sb3.append(transientViewCount);
        printWriter.println(sb3.toString());
        for (int j = 0; j < transientViewCount; ++j) {
            ((ExpandableView)this.getTransientView(j)).dump(fileDescriptor, printWriter, array);
        }
        final ArrayList<ExpandableView> draggedViews = this.mAmbientState.getDraggedViews();
        final int size = draggedViews.size();
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  Dragged Views: ");
        sb4.append(size);
        printWriter.println(sb4.toString());
        for (int k = n; k < size; ++k) {
            draggedViews.get(k).dump(fileDescriptor, printWriter, array);
        }
    }
    
    protected void fling(final int n) {
        if (this.getChildCount() > 0) {
            final float currentOverScrollAmount = this.getCurrentOverScrollAmount(true);
            int n2 = 0;
            final float currentOverScrollAmount2 = this.getCurrentOverScrollAmount(false);
            if (n < 0 && currentOverScrollAmount > 0.0f) {
                this.setOwnScrollY(this.mOwnScrollY - (int)currentOverScrollAmount);
                this.setOverScrollAmount(0.0f, this.mDontReportNextOverScroll = true, false);
                this.mMaxOverScroll = Math.abs(n) / 1000.0f * this.getRubberBandFactor(true) * this.mOverflingDistance + currentOverScrollAmount;
            }
            else if (n > 0 && currentOverScrollAmount2 > 0.0f) {
                this.setOwnScrollY((int)(this.mOwnScrollY + currentOverScrollAmount2));
                this.setOverScrollAmount(0.0f, false, false);
                this.mMaxOverScroll = Math.abs(n) / 1000.0f * this.getRubberBandFactor(false) * this.mOverflingDistance + currentOverScrollAmount2;
            }
            else {
                this.mMaxOverScroll = 0.0f;
            }
            int a = Math.max(0, this.getScrollRange());
            if (this.mExpandedInThisMotion) {
                a = Math.min(a, this.mMaxScrollAfterExpand);
            }
            final OverScroller mScroller = this.mScroller;
            final int mScrollX = super.mScrollX;
            final int mOwnScrollY = this.mOwnScrollY;
            if (!this.mExpandedInThisMotion || mOwnScrollY < 0) {
                n2 = 1073741823;
            }
            mScroller.fling(mScrollX, mOwnScrollY, 1, n, 0, 0, 0, a, 0, n2);
            this.animateScroll();
        }
    }
    
    public void forceNoOverlappingRendering(final boolean mForceNoOverlappingRendering) {
        this.mForceNoOverlappingRendering = mForceNoOverlappingRendering;
    }
    
    public void generateAddAnimation(final ExpandableView expandableView, final boolean b) {
        if (this.mIsExpanded && this.mAnimationsEnabled && !this.mChangePositionInProgress && !this.isFullyHidden()) {
            this.mChildrenToAddAnimated.add(expandableView);
            if (b) {
                this.mFromMoreCardAdditions.add((View)expandableView);
            }
            this.mNeedsAnimation = true;
        }
        if (this.isHeadsUp((View)expandableView) && this.mAnimationsEnabled && !this.mChangePositionInProgress && !this.isFullyHidden()) {
            this.mAddedHeadsUpChildren.add((View)expandableView);
            this.mChildrenToAddAnimated.remove(expandableView);
        }
    }
    
    public void generateChildOrderChangedEvent() {
        if (this.mIsExpanded && this.mAnimationsEnabled) {
            this.mGenerateChildOrderChangedEvent = true;
            this.mNeedsAnimation = true;
            this.requestChildrenUpdate();
        }
    }
    
    public void generateHeadsUpAnimation(final NotificationEntry notificationEntry, final boolean b) {
        this.generateHeadsUpAnimation(notificationEntry.getHeadsUpAnimationView(), b);
    }
    
    public void generateHeadsUpAnimation(final ExpandableNotificationRow expandableNotificationRow, final boolean b) {
        if (this.mAnimationsEnabled && (b || this.mHeadsUpGoingAwayAnimationsAllowed)) {
            this.mHeadsUpChangeAnimations.add((Pair<ExpandableNotificationRow, Boolean>)new Pair((Object)expandableNotificationRow, (Object)b));
            this.mNeedsAnimation = true;
            if (!this.mIsExpanded && !this.mWillExpand && !b) {
                expandableNotificationRow.setHeadsUpAnimatingAway(true);
            }
            this.requestChildrenUpdate();
        }
    }
    
    public ActivatableNotificationView getActivatedChild() {
        return this.mAmbientState.getActivatedChild();
    }
    
    public float getBottomMostNotificationBottom() {
        final int childCount = this.getChildCount();
        float n = 0.0f;
        float n2;
        for (int i = 0; i < childCount; ++i, n = n2) {
            final ExpandableView expandableView = (ExpandableView)this.getChildAt(i);
            if (expandableView.getVisibility() == 8) {
                n2 = n;
            }
            else {
                final float n3 = expandableView.getTranslationY() + expandableView.getActualHeight() - expandableView.getClipBottomAmount();
                n2 = n;
                if (n3 > n) {
                    n2 = n3;
                }
            }
        }
        return n + this.getStackTranslation();
    }
    
    public ExpandableView getChildAtRawPosition(final float n, final float n2) {
        this.getLocationOnScreen(this.mTempInt2);
        final int[] mTempInt2 = this.mTempInt2;
        return this.getChildAtPosition(n - mTempInt2[0], n2 - mTempInt2[1]);
    }
    
    public View getContainerChildAt(final int n) {
        return this.getChildAt(n);
    }
    
    public int getContainerChildCount() {
        return this.getChildCount();
    }
    
    public float getCurrentOverScrollAmount(final boolean b) {
        return this.mAmbientState.getOverScrollAmount(b);
    }
    
    public float getCurrentOverScrolledPixels(final boolean b) {
        float n;
        if (b) {
            n = this.mOverScrolledTopPixels;
        }
        else {
            n = this.mOverScrolledBottomPixels;
        }
        return n;
    }
    
    public DragDownHelper.DragDownCallback getDragDownCallback() {
        return this.mDragDownCallback;
    }
    
    public int getEmptyBottomMargin() {
        return Math.max(this.mMaxLayoutHeight - this.mContentHeight, 0);
    }
    
    public int getEmptyShadeViewHeight() {
        return this.mEmptyShadeView.getHeight();
    }
    
    public ExpandHelper.Callback getExpandHelperCallback() {
        return this.mExpandHelperCallback;
    }
    
    public ExpandableView getFirstChildNotGone() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8 && child != this.mShelf) {
                return (NotificationShelf)child;
            }
        }
        return null;
    }
    
    public int getFooterViewHeight() {
        final FooterView mFooterView = this.mFooterView;
        int n;
        if (mFooterView == null) {
            n = 0;
        }
        else {
            n = this.mPaddingBetweenElements + mFooterView.getHeight();
        }
        return n;
    }
    
    public HeadsUpTouchHelper.Callback getHeadsUpCallback() {
        return this.mHeadsUpCallback;
    }
    
    public View getHostView() {
        return (View)this;
    }
    
    public int getIntrinsicContentHeight() {
        return this.mIntrinsicContentHeight;
    }
    
    public int getIntrinsicPadding() {
        return this.mIntrinsicPadding;
    }
    
    public ExpandableView getLastChildNotGone() {
        for (int i = this.getChildCount() - 1; i >= 0; --i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8 && child != this.mShelf) {
                return (NotificationShelf)child;
            }
        }
        return null;
    }
    
    public int getLayoutMinHeight() {
        if (this.isHeadsUpTransition()) {
            return this.getTopHeadsUpPinnedHeight();
        }
        int intrinsicHeight;
        if (this.mShelf.getVisibility() == 8) {
            intrinsicHeight = 0;
        }
        else {
            intrinsicHeight = this.mShelf.getIntrinsicHeight();
        }
        return intrinsicHeight;
    }
    
    public int getMinExpansionHeight() {
        final int intrinsicHeight = this.mShelf.getIntrinsicHeight();
        final int intrinsicHeight2 = this.mShelf.getIntrinsicHeight();
        final int mStatusBarHeight = this.mStatusBarHeight;
        final int mWaterfallTopInset = this.mWaterfallTopInset;
        return intrinsicHeight - (intrinsicHeight2 - mStatusBarHeight + mWaterfallTopInset) / 2 + mWaterfallTopInset;
    }
    
    public int getNotGoneChildCount() {
        final int childCount = this.getChildCount();
        int i = 0;
        int n = 0;
        while (i < childCount) {
            final ExpandableView expandableView = (ExpandableView)this.getChildAt(i);
            int n2 = n;
            if (expandableView.getVisibility() != 8) {
                n2 = n;
                if (!expandableView.willBeGone()) {
                    n2 = n;
                    if (expandableView != this.mShelf) {
                        n2 = n + 1;
                    }
                }
            }
            ++i;
            n = n2;
        }
        return n;
    }
    
    public NotificationShelf getNotificationShelf() {
        return this.mShelf;
    }
    
    public float getOpeningHeight() {
        if (this.mEmptyShadeView.getVisibility() == 8) {
            return (float)this.getMinExpansionHeight();
        }
        return this.getAppearEndPosition();
    }
    
    public int getPeekHeight() {
        final ExpandableView firstChildNotGone = this.getFirstChildNotGone();
        int n;
        if (firstChildNotGone != null) {
            n = firstChildNotGone.getCollapsedHeight();
        }
        else {
            n = this.mCollapsedSize;
        }
        int intrinsicHeight = 0;
        if (this.getLastVisibleSection() != null) {
            intrinsicHeight = intrinsicHeight;
            if (this.mShelf.getVisibility() != 8) {
                intrinsicHeight = this.mShelf.getIntrinsicHeight();
            }
        }
        return this.mIntrinsicPadding + n + intrinsicHeight;
    }
    
    public int getPositionInLinearLayout(final View view) {
        final boolean childInGroup = this.isChildInGroup(view);
        final ExpandableNotificationRow expandableNotificationRow = null;
        ExpandableNotificationRow expandableNotificationRow2;
        ExpandableNotificationRow notificationParent;
        Object o;
        if (childInGroup) {
            expandableNotificationRow2 = (ExpandableNotificationRow)view;
            o = (notificationParent = expandableNotificationRow2.getNotificationParent());
        }
        else {
            notificationParent = null;
            o = view;
            expandableNotificationRow2 = expandableNotificationRow;
        }
        float n = (float)this.mPaddingBetweenElements;
        float n2 = 0.0f;
        int n3;
        float n4;
        float n5;
        for (int i = n3 = 0; i < this.getChildCount(); ++i, n = n4, n2 = n5) {
            final ExpandableView expandableView = (ExpandableView)this.getChildAt(i);
            final boolean b = expandableView.getVisibility() != 8;
            n4 = n;
            n5 = n2;
            int n6 = n3;
            if (b) {
                n4 = n;
                n5 = n2;
                n6 = n3;
                if (!expandableView.hasNoContentHeight()) {
                    final float increasedPaddingAmount = expandableView.getIncreasedPaddingAmount();
                    float n7;
                    float n8;
                    if (increasedPaddingAmount >= 0.0f) {
                        n7 = (float)(int)NotificationUtils.interpolate(n, (float)this.mIncreasedPaddingBetweenElements, increasedPaddingAmount);
                        n8 = (float)(int)NotificationUtils.interpolate((float)this.mPaddingBetweenElements, (float)this.mIncreasedPaddingBetweenElements, increasedPaddingAmount);
                    }
                    else {
                        final int n9 = (int)NotificationUtils.interpolate(0.0f, (float)this.mPaddingBetweenElements, 1.0f + increasedPaddingAmount);
                        float n10;
                        if (n2 > 0.0f) {
                            n10 = (float)(int)NotificationUtils.interpolate((float)n9, (float)this.mIncreasedPaddingBetweenElements, n2);
                        }
                        else {
                            n10 = (float)n9;
                        }
                        final float n11 = (float)n9;
                        n7 = n10;
                        n8 = n11;
                    }
                    n6 = n3;
                    if (n3 != 0) {
                        n6 = (int)(n3 + n7);
                    }
                    n5 = increasedPaddingAmount;
                    n4 = n8;
                }
            }
            if (expandableView == o) {
                int n12 = n6;
                if (notificationParent != null) {
                    n12 = n6 + notificationParent.getPositionOfChild(expandableNotificationRow2);
                }
                return n12;
            }
            n3 = n6;
            if (b) {
                n3 = n6 + this.getIntrinsicHeight((View)expandableView);
            }
        }
        return 0;
    }
    
    public float getPulseHeight() {
        return this.mAmbientState.getPulseHeight();
    }
    
    public float getStackTranslation() {
        return this.mStackTranslation;
    }
    
    public NotificationSwipeActionHelper getSwipeActionHelper() {
        return this.mSwipeHelper;
    }
    
    public int getTopPadding() {
        return this.mTopPadding;
    }
    
    public float getTopPaddingOverflow() {
        return this.mTopPaddingOverflow;
    }
    
    public ViewGroup getViewParentForNotification(final NotificationEntry notificationEntry) {
        return this;
    }
    
    public int getVisibleNotificationCount() {
        int i = 0;
        int n = 0;
        while (i < this.getChildCount()) {
            final View child = this.getChildAt(i);
            int n2 = n;
            if (child.getVisibility() != 8) {
                n2 = n;
                if (child instanceof ExpandableNotificationRow) {
                    n2 = n + 1;
                }
            }
            ++i;
            n = n2;
        }
        return n;
    }
    
    public float getWakeUpHeight() {
        final ActivatableNotificationView firstChildWithBackground = this.getFirstChildWithBackground();
        if (firstChildWithBackground != null) {
            int n;
            if (this.mKeyguardBypassController.getBypassEnabled()) {
                n = firstChildWithBackground.getHeadsUpHeightWithoutHeader();
            }
            else {
                n = firstChildWithBackground.getCollapsedHeight();
            }
            return (float)n;
        }
        return 0.0f;
    }
    
    public void goToFullShade(final long mGoToFullShadeDelay) {
        this.mGoToFullShadeNeedsAnimation = true;
        this.mGoToFullShadeDelay = mGoToFullShadeDelay;
        this.mNeedsAnimation = true;
        this.requestChildrenUpdate();
    }
    
    public boolean hasActiveClearableNotifications(final int n) {
        if (this.mDynamicPrivacyController.isInLockedDownShade()) {
            return false;
        }
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)child;
                if (expandableNotificationRow.canViewBeDismissed() && matchesSelection(expandableNotificationRow, n)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean hasOverlappingRendering() {
        return !this.mForceNoOverlappingRendering && super.hasOverlappingRendering();
    }
    
    public boolean hasPulsingNotifications() {
        return this.mPulsing;
    }
    
    @VisibleForTesting
    protected void inflateFooterView() {
        final FooterView footerView = (FooterView)LayoutInflater.from(super.mContext).inflate(R$layout.status_bar_notification_footer, (ViewGroup)this, false);
        footerView.setDismissButtonClickListener((View$OnClickListener)new _$$Lambda$NotificationStackScrollLayout$tTmleiEUCQNFZAgE8HJh01E_kkA(this));
        footerView.setManageButtonClickListener((View$OnClickListener)new _$$Lambda$mjP2_ECpzICMymoTPt8MeJd4_PU(this));
        this.setFooterView(footerView);
    }
    
    public boolean isAddOrRemoveAnimationPending() {
        return this.mNeedsAnimation && (!this.mChildrenToAddAnimated.isEmpty() || !this.mChildrenToRemoveAnimated.isEmpty());
    }
    
    public boolean isBelowLastNotification(final float n, final float n2) {
        int i = this.getChildCount();
        boolean b = true;
        --i;
        while (i >= 0) {
            final ExpandableView expandableView = (ExpandableView)this.getChildAt(i);
            if (expandableView.getVisibility() != 8) {
                final float y = expandableView.getY();
                if (y > n2) {
                    return false;
                }
                final boolean b2 = n2 > expandableView.getActualHeight() + y - expandableView.getClipBottomAmount();
                final FooterView mFooterView = this.mFooterView;
                if (expandableView == mFooterView) {
                    if (!b2 && !mFooterView.isOnEmptySpace(n - mFooterView.getX(), n2 - y)) {
                        return false;
                    }
                }
                else {
                    if (expandableView == this.mEmptyShadeView) {
                        return true;
                    }
                    if (!b2) {
                        return false;
                    }
                }
            }
            --i;
        }
        if (n2 <= this.mTopPadding + this.mStackTranslation) {
            b = false;
        }
        return b;
    }
    
    @VisibleForTesting
    boolean isDimmed() {
        return this.mAmbientState.isDimmed();
    }
    
    public boolean isExpanded() {
        return this.mIsExpanded;
    }
    
    public boolean isFooterViewContentVisible() {
        final FooterView mFooterView = this.mFooterView;
        return mFooterView != null && mFooterView.isContentVisible();
    }
    
    public boolean isFooterViewNotGone() {
        final FooterView mFooterView = this.mFooterView;
        return mFooterView != null && mFooterView.getVisibility() != 8 && !this.mFooterView.willBeGone();
    }
    
    public boolean isFullyHidden() {
        return this.mAmbientState.isFullyHidden();
    }
    
    public boolean isInContentBounds(final float n) {
        return n < this.getHeight() - this.getEmptyBottomMargin();
    }
    
    public boolean isInVisibleLocation(final NotificationEntry notificationEntry) {
        final ExpandableNotificationRow row = notificationEntry.getRow();
        final ExpandableViewState viewState = row.getViewState();
        return viewState != null && (viewState.location & 0x5) != 0x0 && row.getVisibility() == 0;
    }
    
    protected boolean isInsideQsContainer(final MotionEvent motionEvent) {
        return motionEvent.getY() < this.mQsContainer.getBottom();
    }
    
    public boolean isPulseExpanding() {
        return this.mAmbientState.isPulseExpanding();
    }
    
    public boolean isScrolledToBottom() {
        return this.mOwnScrollY >= this.getScrollRange();
    }
    
    public boolean isScrolledToTop() {
        return this.mOwnScrollY == 0;
    }
    
    public void lockScrollTo(final View mForcedScroll) {
        if (this.mForcedScroll == mForcedScroll) {
            return;
        }
        this.scrollTo(this.mForcedScroll = mForcedScroll);
    }
    
    public void manageNotifications(final View view) {
        this.mStatusBar.startActivity(new Intent("android.settings.NOTIFICATION_HISTORY"), true, true, 536870912);
    }
    
    public void notifyGroupChildAdded(final View view) {
        this.notifyGroupChildAdded((ExpandableView)view);
    }
    
    public void notifyGroupChildAdded(final ExpandableView expandableView) {
        this.onViewAddedInternal(expandableView);
    }
    
    public void notifyGroupChildRemoved(final View view, final ViewGroup viewGroup) {
        this.notifyGroupChildRemoved((ExpandableView)view, viewGroup);
    }
    
    public void notifyGroupChildRemoved(final ExpandableView expandableView, final ViewGroup viewGroup) {
        this.onViewRemovedInternal(expandableView, viewGroup);
    }
    
    public void notifyHideAnimationStart(final boolean b) {
        final float mInterpolatedHideAmount = this.mInterpolatedHideAmount;
        if (mInterpolatedHideAmount == 0.0f || mInterpolatedHideAmount == 1.0f) {
            float mBackgroundXFactor;
            if (b) {
                mBackgroundXFactor = 1.8f;
            }
            else {
                mBackgroundXFactor = 1.5f;
            }
            this.mBackgroundXFactor = mBackgroundXFactor;
            Interpolator mHideXInterpolator;
            if (b) {
                mHideXInterpolator = Interpolators.FAST_OUT_SLOW_IN_REVERSE;
            }
            else {
                mHideXInterpolator = Interpolators.FAST_OUT_SLOW_IN;
            }
            this.mHideXInterpolator = mHideXInterpolator;
        }
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        this.mBottomInset = windowInsets.getSystemWindowInsetBottom();
        this.mWaterfallTopInset = 0;
        final DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        if (displayCutout != null) {
            this.mWaterfallTopInset = displayCutout.getWaterfallInsets().top;
        }
        if (this.mOwnScrollY > this.getScrollRange()) {
            this.removeCallbacks(this.mReclamp);
            this.postDelayed(this.mReclamp, 50L);
        }
        else {
            final View mForcedScroll = this.mForcedScroll;
            if (mForcedScroll != null) {
                this.scrollTo(mForcedScroll);
            }
        }
        return windowInsets;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Dependency.get((Class<SysuiStatusBarStateController>)StatusBarStateController.class).addCallback(this.mStateListener, 2);
        Dependency.get(ConfigurationController.class).addCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    public void onChildAnimationFinished() {
        this.setAnimationRunning(false);
        this.requestChildrenUpdate();
        this.runAnimationFinishedRunnables();
        this.clearTransient();
        this.clearHeadsUpDisappearRunning();
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mStatusBarHeight = this.getResources().getDimensionPixelOffset(R$dimen.status_bar_height);
        this.mSwipeHelper.setDensityScale(this.getResources().getDisplayMetrics().density);
        this.mSwipeHelper.setPagingTouchSlop((float)ViewConfiguration.get(this.getContext()).getScaledPagingTouchSlop());
        this.initView(this.getContext());
    }
    
    public void onDensityOrFontScaleChanged() {
        this.reinflateViews();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(StatusBarStateController.class).removeCallback(this.mStateListener);
        Dependency.get(ConfigurationController.class).removeCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    protected void onDraw(final Canvas canvas) {
        if (this.mShouldDrawNotificationBackground) {
            final int top = this.mSections[0].getCurrentBounds().top;
            final NotificationSection[] mSections = this.mSections;
            if (top < mSections[mSections.length - 1].getCurrentBounds().bottom || this.mAmbientState.isDozing()) {
                this.drawBackground(canvas);
                return;
            }
        }
        if (this.mInHeadsUpPinnedMode || this.mHeadsUpAnimatingAway) {
            this.drawHeadsUpBackground(canvas);
        }
    }
    
    public void onDynamicPrivacyChanged() {
        if (this.mIsExpanded) {
            this.mAnimateBottomOnLayout = true;
        }
        this.post((Runnable)new _$$Lambda$NotificationStackScrollLayout$hLgUUdiy6wm4RdWHegQc8zQwdaM(this));
    }
    
    public void onExpansionStarted() {
        this.mIsExpansionChanging = true;
        this.mAmbientState.setExpansionChanging(true);
        this.checkSnoozeLeavebehind();
    }
    
    public void onExpansionStopped() {
        this.mIsExpansionChanging = false;
        this.resetCheckSnoozeLeavebehind();
        this.mAmbientState.setExpansionChanging(false);
        if (!this.mIsExpanded) {
            this.resetScrollPosition();
            this.mStatusBar.resetUserExpandedStates();
            this.clearTemporaryViews();
            this.clearUserLockedViews();
            final ArrayList<ExpandableView> draggedViews = this.mAmbientState.getDraggedViews();
            if (draggedViews.size() > 0) {
                draggedViews.clear();
                this.updateContinuousShadowDrawing();
            }
        }
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.inflateEmptyShadeView();
        this.inflateFooterView();
        this.mVisualStabilityManager.setVisibilityLocationProvider(new _$$Lambda$U5xT0qKII52vil_DFEsN5YX5CE0(this));
        if (this.mAllowLongPress) {
            final NotificationGutsManager mNotificationGutsManager = this.mNotificationGutsManager;
            Objects.requireNonNull(mNotificationGutsManager);
            this.setLongPressListener(new _$$Lambda$0lGYUT66Z7cr4TZs4rdZ8M7DQkw(mNotificationGutsManager));
        }
    }
    
    public boolean onGenericMotionEvent(final MotionEvent motionEvent) {
        final boolean scrollingEnabled = this.isScrollingEnabled();
        final int n = 0;
        if (scrollingEnabled && this.mIsExpanded && !this.mSwipingInProgress && !this.mExpandingNotification && !this.mDisallowScrollingInThisMotion) {
            if ((motionEvent.getSource() & 0x2) != 0x0) {
                if (motionEvent.getAction() == 8) {
                    if (!this.mIsBeingDragged) {
                        final float axisValue = motionEvent.getAxisValue(9);
                        if (axisValue != 0.0f) {
                            final int n2 = (int)(axisValue * this.getVerticalScrollFactor());
                            int scrollRange = this.getScrollRange();
                            final int mOwnScrollY = this.mOwnScrollY;
                            final int n3 = mOwnScrollY - n2;
                            if (n3 < 0) {
                                scrollRange = n;
                            }
                            else if (n3 <= scrollRange) {
                                scrollRange = n3;
                            }
                            if (scrollRange != mOwnScrollY) {
                                this.setOwnScrollY(scrollRange);
                                return true;
                            }
                        }
                    }
                }
            }
            return super.onGenericMotionEvent(motionEvent);
        }
        return false;
    }
    
    public void onHeightChanged(final ExpandableView expandableView, final boolean b) {
        this.updateContentHeight();
        this.updateScrollPositionOnExpandInBottom(expandableView);
        this.clampScrollPosition();
        this.notifyHeightChangeListener(expandableView, b);
        final boolean b2 = expandableView instanceof ExpandableNotificationRow;
        ActivatableNotificationView firstVisibleChild = null;
        ExpandableNotificationRow expandableNotificationRow;
        if (b2) {
            expandableNotificationRow = (ExpandableNotificationRow)expandableView;
        }
        else {
            expandableNotificationRow = null;
        }
        final NotificationSection firstVisibleSection = this.getFirstVisibleSection();
        if (firstVisibleSection != null) {
            firstVisibleChild = firstVisibleSection.getFirstVisibleChild();
        }
        if (expandableNotificationRow != null && (expandableNotificationRow == firstVisibleChild || expandableNotificationRow.getNotificationParent() == firstVisibleChild)) {
            this.updateAlgorithmLayoutMinHeight();
        }
        if (b) {
            this.requestAnimationOnViewResize(expandableNotificationRow);
        }
        this.requestChildrenUpdate();
    }
    
    public void onInitializeAccessibilityEventInternal(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEventInternal(accessibilityEvent);
        accessibilityEvent.setScrollable(this.mScrollable);
        accessibilityEvent.setMaxScrollX(super.mScrollX);
        accessibilityEvent.setScrollY(this.mOwnScrollY);
        accessibilityEvent.setMaxScrollY(this.getScrollRange());
    }
    
    public void onInitializeAccessibilityNodeInfoInternal(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        if (this.mScrollable) {
            accessibilityNodeInfo.setScrollable(true);
            if (this.mBackwardScrollable) {
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_SCROLL_BACKWARD);
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_SCROLL_UP);
            }
            if (this.mForwardScrollable) {
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_SCROLL_FORWARD);
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_SCROLL_DOWN);
            }
        }
        accessibilityNodeInfo.setClassName((CharSequence)ScrollView.class.getName());
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        this.initDownStates(motionEvent);
        this.handleEmptySpaceClick(motionEvent);
        final NotificationGuts exposedGuts = this.mNotificationGutsManager.getExposedGuts();
        final boolean mSwipingInProgress = this.mSwipingInProgress;
        final boolean b = false;
        final boolean b2 = !mSwipingInProgress && !this.mOnlyScrollingInThisMotion && exposedGuts == null && this.mExpandHelper.onInterceptTouchEvent(motionEvent);
        final boolean b3 = !mSwipingInProgress && !this.mExpandingNotification && this.onInterceptTouchEventScroll(motionEvent);
        final boolean b4 = !this.mIsBeingDragged && !this.mExpandingNotification && !this.mExpandedInThisMotion && !this.mOnlyScrollingInThisMotion && !this.mDisallowDismissInThisMotion && this.mSwipeHelper.onInterceptTouchEvent(motionEvent);
        final boolean b5 = motionEvent.getActionMasked() == 1;
        if (!NotificationSwipeHelper.isTouchInView(motionEvent, (View)exposedGuts) && b5 && !b4 && !b2 && !b3) {
            this.mCheckForLeavebehind = false;
            this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
        }
        if (motionEvent.getActionMasked() == 1) {
            this.mCheckForLeavebehind = true;
        }
        if (!b4 && !b3 && !b2) {
            final boolean b6 = b;
            if (!super.onInterceptTouchEvent(motionEvent)) {
                return b6;
            }
        }
        return true;
    }
    
    protected void onLayout(final boolean b, int i, final int n, final int n2, final int n3) {
        final float n4 = this.getWidth() / 2.0f;
        View child;
        float n5;
        float n6;
        float n7;
        for (i = 0; i < this.getChildCount(); ++i) {
            child = this.getChildAt(i);
            n5 = (float)child.getMeasuredWidth();
            n6 = (float)child.getMeasuredHeight();
            n7 = n5 / 2.0f;
            child.layout((int)(n4 - n7), 0, (int)(n7 + n4), (int)n6);
        }
        this.setMaxLayoutHeight(this.getHeight());
        this.updateContentHeight();
        this.clampScrollPosition();
        this.requestChildrenUpdate();
        this.updateFirstAndLastBackgroundViews();
        this.updateAlgorithmLayoutMinHeight();
        this.updateOwnTranslationZ();
    }
    
    protected void onMeasure(int i, int n) {
        super.onMeasure(i, n);
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(View$MeasureSpec.getSize(i) - this.mSidePaddings * 2, View$MeasureSpec.getMode(i));
        n = View$MeasureSpec.getSize(n);
        i = 0;
        final int measureSpec2 = View$MeasureSpec.makeMeasureSpec(n, 0);
        for (n = this.getChildCount(); i < n; ++i) {
            this.measureChild(this.getChildAt(i), measureSpec, measureSpec2);
        }
    }
    
    public void onOverlayChanged() {
        final int dimensionPixelSize = super.mContext.getResources().getDimensionPixelSize(Utils.getThemeAttr(super.mContext, 16844145));
        if (this.mCornerRadius != dimensionPixelSize) {
            this.mCornerRadius = dimensionPixelSize;
            this.invalidate();
        }
        this.reinflateViews();
    }
    
    public void onPanelTrackingStarted() {
        this.mPanelTracking = true;
        this.mAmbientState.setPanelTracking(true);
        this.resetExposedMenuView(true, true);
    }
    
    public void onPanelTrackingStopped() {
        this.mPanelTracking = false;
        this.mAmbientState.setPanelTracking(false);
    }
    
    public void onReset(final ExpandableView expandableView) {
        this.updateAnimationState((View)expandableView);
        this.updateChronometerForChild((View)expandableView);
    }
    
    public void onThemeChanged() {
        this.updateDecorViews(this.mColorExtractor.getNeutralColors().supportsDarkText());
        this.updateFooter();
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final NotificationGuts exposedGuts = this.mNotificationGutsManager.getExposedGuts();
        final int actionMasked = motionEvent.getActionMasked();
        final boolean b = true;
        final boolean b2 = actionMasked == 3 || motionEvent.getActionMasked() == 1;
        this.handleEmptySpaceClick(motionEvent);
        final boolean mSwipingInProgress = this.mSwipingInProgress;
        boolean onTouchEvent;
        if (this.mIsExpanded && !mSwipingInProgress && !this.mOnlyScrollingInThisMotion && exposedGuts == null) {
            if (b2) {
                this.mExpandHelper.onlyObserveMovements(false);
            }
            final boolean mExpandingNotification = this.mExpandingNotification;
            final boolean b3 = onTouchEvent = this.mExpandHelper.onTouchEvent(motionEvent);
            if (this.mExpandedInThisMotion) {
                onTouchEvent = b3;
                if (!this.mExpandingNotification) {
                    onTouchEvent = b3;
                    if (mExpandingNotification) {
                        onTouchEvent = b3;
                        if (!this.mDisallowScrollingInThisMotion) {
                            this.dispatchDownEventToScroller(motionEvent);
                            onTouchEvent = b3;
                        }
                    }
                }
            }
        }
        else {
            onTouchEvent = false;
        }
        final boolean b4 = this.mIsExpanded && !mSwipingInProgress && !this.mExpandingNotification && !this.mDisallowScrollingInThisMotion && this.onScrollTouch(motionEvent);
        final boolean b5 = !this.mIsBeingDragged && !this.mExpandingNotification && !this.mExpandedInThisMotion && !this.mOnlyScrollingInThisMotion && !this.mDisallowDismissInThisMotion && this.mSwipeHelper.onTouchEvent(motionEvent);
        if (exposedGuts != null && !NotificationSwipeHelper.isTouchInView(motionEvent, (View)exposedGuts) && exposedGuts.getGutsContent() instanceof NotificationSnooze && ((((NotificationSnooze)exposedGuts.getGutsContent()).isExpanded() && b2) || (!b5 && b4))) {
            this.checkSnoozeLeavebehind();
        }
        if (motionEvent.getActionMasked() == 1) {
            this.mCheckForLeavebehind = true;
        }
        boolean b6 = b;
        if (!b5) {
            b6 = b;
            if (!b4) {
                b6 = b;
                if (!onTouchEvent) {
                    b6 = (super.onTouchEvent(motionEvent) && b);
                }
            }
        }
        return b6;
    }
    
    public void onUiModeChanged() {
        this.mBgColor = super.mContext.getColor(R$color.notification_shade_background_color);
        this.updateBackgroundDimming();
        this.mShelf.onUiModeChanged();
    }
    
    public void onUpdateRowStates() {
        final ForegroundServiceDungeonView mFgsSectionView = this.mFgsSectionView;
        int n = 1;
        if (mFgsSectionView != null) {
            this.changeViewPosition(mFgsSectionView, this.getChildCount() - 1);
            n = 2;
        }
        final FooterView mFooterView = this.mFooterView;
        final int childCount = this.getChildCount();
        final int n2 = n + 1;
        this.changeViewPosition(mFooterView, childCount - n);
        this.changeViewPosition(this.mEmptyShadeView, this.getChildCount() - n2);
        this.changeViewPosition(this.mShelf, this.getChildCount() - (n2 + 1));
    }
    
    public void onViewAdded(final View view) {
        super.onViewAdded(view);
        this.onViewAddedInternal((ExpandableView)view);
    }
    
    public void onViewRemoved(final View view) {
        super.onViewRemoved(view);
        if (!this.mChildTransferInProgress) {
            this.onViewRemovedInternal((ExpandableView)view, this);
        }
    }
    
    public void onWindowFocusChanged(final boolean b) {
        super.onWindowFocusChanged(b);
        if (!b) {
            this.cancelLongPress();
        }
    }
    
    public boolean performAccessibilityActionInternal(int n, final Bundle bundle) {
        if (super.performAccessibilityActionInternal(n, bundle)) {
            return true;
        }
        if (!this.isEnabled()) {
            return false;
        }
        final int n2 = -1;
        int n3 = 0;
        Label_0062: {
            if (n != 4096) {
                n3 = n2;
                if (n == 8192) {
                    break Label_0062;
                }
                n3 = n2;
                if (n == 16908344) {
                    break Label_0062;
                }
                if (n != 16908346) {
                    return false;
                }
            }
            n3 = 1;
        }
        final int height = this.getHeight();
        final int mPaddingBottom = super.mPaddingBottom;
        final int mTopPadding = this.mTopPadding;
        n = super.mPaddingTop;
        n = Math.max(0, Math.min(this.mOwnScrollY + n3 * (height - mPaddingBottom - mTopPadding - n - this.mShelf.getIntrinsicHeight()), this.getScrollRange()));
        final int mOwnScrollY = this.mOwnScrollY;
        if (n != mOwnScrollY) {
            this.mScroller.startScroll(super.mScrollX, mOwnScrollY, 0, n - mOwnScrollY);
            this.animateScroll();
            return true;
        }
        return false;
    }
    
    public void removeContainerView(final View view) {
        Assert.isMainThread();
        this.removeView(view);
    }
    
    public void removeListItem(final NotificationListItem notificationListItem) {
        this.removeContainerView(notificationListItem.getView());
    }
    
    public void removeOnExpandedHeightChangedListener(final BiConsumer<Float, Float> o) {
        this.mExpandedHeightListeners.remove(o);
    }
    
    public void requestDisallowDismiss() {
        this.mDisallowDismissInThisMotion = true;
    }
    
    public void requestDisallowInterceptTouchEvent(final boolean b) {
        super.requestDisallowInterceptTouchEvent(b);
        if (b) {
            this.cancelLongPress();
        }
    }
    
    public void requestDisallowLongPress() {
        this.cancelLongPress();
    }
    
    public void resetCheckSnoozeLeavebehind() {
        this.mCheckForLeavebehind = true;
    }
    
    public void resetExposedMenuView(final boolean b, final boolean b2) {
        this.mSwipeHelper.resetExposedMenuView(b, b2);
    }
    
    public void resetScrollPosition() {
        this.mScroller.abortAnimation();
        this.setOwnScrollY(0);
    }
    
    public void runAfterAnimationFinished(final Runnable e) {
        this.mAnimationFinishedRunnables.add(e);
    }
    
    public boolean scrollTo(final View view) {
        final ExpandableView expandableView = (ExpandableView)view;
        final int positionInLinearLayout = this.getPositionInLinearLayout(view);
        final int targetScrollForView = this.targetScrollForView(expandableView, positionInLinearLayout);
        final int intrinsicHeight = expandableView.getIntrinsicHeight();
        final int mOwnScrollY = this.mOwnScrollY;
        if (mOwnScrollY >= targetScrollForView && positionInLinearLayout + intrinsicHeight >= mOwnScrollY) {
            return false;
        }
        final OverScroller mScroller = this.mScroller;
        final int mScrollX = super.mScrollX;
        final int mOwnScrollY2 = this.mOwnScrollY;
        mScroller.startScroll(mScrollX, mOwnScrollY2, 0, targetScrollForView - mOwnScrollY2);
        this.mDontReportNextOverScroll = true;
        this.animateScroll();
        return true;
    }
    
    public void setActivatedChild(final ActivatableNotificationView activatedChild) {
        this.mAmbientState.setActivatedChild(activatedChild);
        if (this.mAnimationsEnabled) {
            this.mActivateNeedsAnimation = true;
            this.mNeedsAnimation = true;
        }
        this.requestChildrenUpdate();
    }
    
    public void setAnimationRunning(final boolean mAnimationRunning) {
        if (mAnimationRunning != this.mAnimationRunning) {
            if (mAnimationRunning) {
                this.getViewTreeObserver().addOnPreDrawListener(this.mRunningAnimationUpdater);
            }
            else {
                this.getViewTreeObserver().removeOnPreDrawListener(this.mRunningAnimationUpdater);
            }
            this.mAnimationRunning = mAnimationRunning;
            this.updateContinuousShadowDrawing();
        }
    }
    
    public void setAnimationsEnabled(final boolean mAnimationsEnabled) {
        this.mAnimationsEnabled = mAnimationsEnabled;
        this.updateNotificationAnimationStates();
        if (!mAnimationsEnabled) {
            this.mSwipedOutViews.clear();
            this.mChildrenToRemoveAnimated.clear();
            this.clearTemporaryViewsInGroup(this);
        }
    }
    
    public void setChildLocationsChangedListener(final NotificationLogger.OnChildLocationsChangedListener mListener) {
        this.mListener = mListener;
    }
    
    public void setChildTransferInProgress(final boolean mChildTransferInProgress) {
        Assert.isMainThread();
        this.mChildTransferInProgress = mChildTransferInProgress;
    }
    
    public void setDimmed(final boolean b, final boolean b2) {
        final boolean dimmed = b & this.onKeyguard();
        this.mAmbientState.setDimmed(dimmed);
        if (b2 && this.mAnimationsEnabled) {
            this.mDimmedNeedsAnimation = true;
            this.mNeedsAnimation = true;
            this.animateDimmed(dimmed);
        }
        else {
            float dimAmount;
            if (dimmed) {
                dimAmount = 1.0f;
            }
            else {
                dimAmount = 0.0f;
            }
            this.setDimAmount(dimAmount);
        }
        this.requestChildrenUpdate();
    }
    
    public void setDismissAllInProgress(final boolean b) {
        this.mDismissAllInProgress = b;
        this.mAmbientState.setDismissAllInProgress(b);
        this.handleDismissAllClipping();
    }
    
    public void setDozeAmount(final float dozeAmount) {
        this.mAmbientState.setDozeAmount(dozeAmount);
        this.updateContinuousBackgroundDrawing();
        this.requestChildrenUpdate();
    }
    
    public void setDozing(final boolean dozing, final boolean b, final PointF pointF) {
        if (this.mAmbientState.isDozing() == dozing) {
            return;
        }
        this.mAmbientState.setDozing(dozing);
        this.requestChildrenUpdate();
        this.notifyHeightChangeListener(this.mShelf);
    }
    
    public void setEmptyShadeView(final EmptyShadeView mEmptyShadeView) {
        final EmptyShadeView mEmptyShadeView2 = this.mEmptyShadeView;
        int indexOfChild;
        if (mEmptyShadeView2 != null) {
            indexOfChild = this.indexOfChild((View)mEmptyShadeView2);
            this.removeView((View)this.mEmptyShadeView);
        }
        else {
            indexOfChild = -1;
        }
        this.addView((View)(this.mEmptyShadeView = mEmptyShadeView), indexOfChild);
    }
    
    public void setExpandedHeight(float n) {
        this.mExpandedHeight = n;
        final float n2 = 0.0f;
        final boolean b = true;
        this.setIsExpanded(n > 0.0f);
        final float n3 = (float)this.getMinExpansionHeight();
        if (n < n3) {
            final Rect mClipRect = this.mClipRect;
            mClipRect.left = 0;
            mClipRect.right = this.getWidth();
            final Rect mClipRect2 = this.mClipRect;
            mClipRect2.top = 0;
            mClipRect2.bottom = (int)n;
            this.setRequestedClipBounds(mClipRect2);
            n = n3;
        }
        else {
            this.setRequestedClipBounds(null);
        }
        final float appearEndPosition = this.getAppearEndPosition();
        final float appearStartPosition = this.getAppearStartPosition();
        final boolean appearing = n < appearEndPosition && b;
        this.mAmbientState.setAppearing(appearing);
        int pinnedHeadsUpHeight = 0;
        Label_0326: {
            if (!appearing) {
                if (this.mShouldShowShelfOnly) {
                    pinnedHeadsUpHeight = this.mTopPadding + this.mShelf.getIntrinsicHeight();
                    n = n2;
                }
                else {
                    if (this.mQsExpanded) {
                        final int n4 = this.mContentHeight - this.mTopPadding + this.mIntrinsicPadding;
                        pinnedHeadsUpHeight = this.mMaxTopPadding + this.mShelf.getIntrinsicHeight();
                        if (n4 <= pinnedHeadsUpHeight) {
                            n = n2;
                            break Label_0326;
                        }
                        n = NotificationUtils.interpolate((float)n4, (float)pinnedHeadsUpHeight, this.mQsExpansionFraction);
                    }
                    pinnedHeadsUpHeight = (int)n;
                    n = n2;
                }
            }
            else {
                final float calculateAppearFraction = this.calculateAppearFraction(n);
                float interpolate;
                if (calculateAppearFraction >= 0.0f) {
                    interpolate = NotificationUtils.interpolate(this.getExpandTranslationStart(), 0.0f, calculateAppearFraction);
                }
                else {
                    interpolate = n - appearStartPosition + this.getExpandTranslationStart();
                }
                if (this.isHeadsUpTransition()) {
                    pinnedHeadsUpHeight = this.getFirstVisibleSection().getFirstVisibleChild().getPinnedHeadsUpHeight();
                    n = MathUtils.lerp((float)(this.mHeadsUpInset - this.mTopPadding), 0.0f, calculateAppearFraction);
                }
                else {
                    pinnedHeadsUpHeight = (int)(n - interpolate);
                    n = interpolate;
                }
            }
        }
        if (pinnedHeadsUpHeight != this.mCurrentStackHeight) {
            this.mCurrentStackHeight = pinnedHeadsUpHeight;
            this.updateAlgorithmHeightAndPadding();
            this.requestChildrenUpdate();
        }
        this.setStackTranslation(n);
        this.notifyAppearChangedListeners();
    }
    
    public void setExpandingEnabled(final boolean enabled) {
        this.mExpandHelper.setEnabled(enabled);
    }
    
    public void setExpandingNotification(final ExpandableNotificationRow expandingNotification) {
        this.mAmbientState.setExpandingNotification(expandingNotification);
        this.requestChildrenUpdate();
    }
    
    public void setExpandingVelocity(final float expandingVelocity) {
        this.mAmbientState.setExpandingVelocity(expandingVelocity);
    }
    
    public void setFinishScrollingCallback(final Runnable mFinishScrollingCallback) {
        this.mFinishScrollingCallback = mFinishScrollingCallback;
    }
    
    public void setFooterView(final FooterView mFooterView) {
        final FooterView mFooterView2 = this.mFooterView;
        int indexOfChild;
        if (mFooterView2 != null) {
            indexOfChild = this.indexOfChild((View)mFooterView2);
            this.removeView((View)this.mFooterView);
        }
        else {
            indexOfChild = -1;
        }
        this.addView((View)(this.mFooterView = mFooterView), indexOfChild);
    }
    
    public void setGroupManager(final NotificationGroupManager mGroupManager) {
        (this.mGroupManager = mGroupManager).addOnGroupChangeListener(this.mOnGroupChangeListener);
    }
    
    public void setHeadsUpAnimatingAway(final boolean mHeadsUpAnimatingAway) {
        this.mHeadsUpAnimatingAway = mHeadsUpAnimatingAway;
        this.updateClipping();
    }
    
    public void setHeadsUpAppearanceController(final HeadsUpAppearanceController mHeadsUpAppearanceController) {
        this.mHeadsUpAppearanceController = mHeadsUpAppearanceController;
    }
    
    public void setHeadsUpBoundaries(final int headsUpAppearHeightBottom, final int n) {
        this.mAmbientState.setMaxHeadsUpTranslation((float)(headsUpAppearHeightBottom - n));
        this.mStateAnimator.setHeadsUpAppearHeightBottom(headsUpAppearHeightBottom);
        this.requestChildrenUpdate();
    }
    
    public void setHeadsUpGoingAwayAnimationsAllowed(final boolean mHeadsUpGoingAwayAnimationsAllowed) {
        this.mHeadsUpGoingAwayAnimationsAllowed = mHeadsUpGoingAwayAnimationsAllowed;
    }
    
    public void setHideAmount(final float mLinearHideAmount, final float n) {
        this.mLinearHideAmount = mLinearHideAmount;
        this.mInterpolatedHideAmount = n;
        final boolean fullyHidden = this.mAmbientState.isFullyHidden();
        final boolean hiddenAtAll = this.mAmbientState.isHiddenAtAll();
        this.mAmbientState.setHideAmount(n);
        final boolean fullyHidden2 = this.mAmbientState.isFullyHidden();
        final boolean hiddenAtAll2 = this.mAmbientState.isHiddenAtAll();
        if (fullyHidden2 != fullyHidden) {
            this.updateVisibility();
        }
        if (!hiddenAtAll && hiddenAtAll2) {
            this.resetExposedMenuView(true, true);
        }
        if (fullyHidden2 != fullyHidden || hiddenAtAll != hiddenAtAll2) {
            this.invalidateOutline();
        }
        this.updateAlgorithmHeightAndPadding();
        this.updateBackgroundDimming();
        this.requestChildrenUpdate();
        this.updateOwnTranslationZ();
    }
    
    public void setIconAreaController(final NotificationIconAreaController mIconAreaController) {
        this.mIconAreaController = mIconAreaController;
    }
    
    public void setInHeadsUpPinnedMode(final boolean mInHeadsUpPinnedMode) {
        this.mInHeadsUpPinnedMode = mInHeadsUpPinnedMode;
        this.updateClipping();
    }
    
    public void setIntrinsicPadding(final int n) {
        this.mIntrinsicPadding = n;
        this.mAmbientState.setIntrinsicPadding(n);
    }
    
    @VisibleForTesting
    void setIsBeingDragged(final boolean mIsBeingDragged) {
        this.mIsBeingDragged = mIsBeingDragged;
        if (mIsBeingDragged) {
            this.requestDisallowInterceptTouchEvent(true);
            this.cancelLongPress();
            this.resetExposedMenuView(true, true);
        }
    }
    
    public void setIsFullWidth(final boolean panelFullWidth) {
        this.mAmbientState.setPanelFullWidth(panelFullWidth);
    }
    
    public void setLongPressListener(final ExpandableNotificationRow.LongPressListener mLongPressListener) {
        this.mLongPressListener = mLongPressListener;
    }
    
    public void setMaxDisplayedNotifications(final int mMaxDisplayedNotifications) {
        if (this.mMaxDisplayedNotifications != mMaxDisplayedNotifications) {
            this.mMaxDisplayedNotifications = mMaxDisplayedNotifications;
            this.updateContentHeight();
            this.notifyHeightChangeListener(this.mShelf);
        }
    }
    
    public void setMaxTopPadding(final int mMaxTopPadding) {
        this.mMaxTopPadding = mMaxTopPadding;
    }
    
    public void setNotificationPanelController(final NotificationPanelViewController mNotificationPanelController) {
        this.mNotificationPanelController = mNotificationPanelController;
    }
    
    public void setOnEmptySpaceClickListener(final OnEmptySpaceClickListener mOnEmptySpaceClickListener) {
        this.mOnEmptySpaceClickListener = mOnEmptySpaceClickListener;
    }
    
    public void setOnHeightChangedListener(final OnHeightChangedListener mOnHeightChangedListener) {
        this.mOnHeightChangedListener = mOnHeightChangedListener;
    }
    
    public void setOnPulseHeightChangedListener(final Runnable onPulseHeightChangedListener) {
        this.mAmbientState.setOnPulseHeightChangedListener(onPulseHeightChangedListener);
    }
    
    public void setOverScrollAmount(final float n, final boolean b, final boolean b2) {
        this.setOverScrollAmount(n, b, b2, true);
    }
    
    public void setOverScrollAmount(final float n, final boolean b, final boolean b2, final boolean b3) {
        this.setOverScrollAmount(n, b, b2, b3, this.isRubberbanded(b));
    }
    
    public void setOverScrollAmount(final float n, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        if (b3) {
            this.mStateAnimator.cancelOverScrollAnimators(b);
        }
        this.setOverScrollAmountInternal(n, b, b2, b4);
    }
    
    public void setOverScrolledPixels(final float n, final boolean b, final boolean b2) {
        this.setOverScrollAmount(n * this.getRubberBandFactor(b), b, b2, true);
    }
    
    public void setOverscrollTopChangedListener(final OnOverscrollTopChangedListener mOverscrollTopChangedListener) {
        this.mOverscrollTopChangedListener = mOverscrollTopChangedListener;
    }
    
    public float setPulseHeight(final float pulseHeight) {
        this.mAmbientState.setPulseHeight(pulseHeight);
        if (this.mKeyguardBypassController.getBypassEnabled()) {
            this.notifyAppearChangedListeners();
        }
        this.requestChildrenUpdate();
        return Math.max(0.0f, pulseHeight - this.mAmbientState.getInnerHeight(true));
    }
    
    public void setPulsing(final boolean pulsing, final boolean b) {
        if (!this.mPulsing && !pulsing) {
            return;
        }
        this.mPulsing = pulsing;
        this.mAmbientState.setPulsing(pulsing);
        this.mSwipeHelper.setPulsing(pulsing);
        this.updateNotificationAnimationStates();
        this.updateAlgorithmHeightAndPadding();
        this.updateContentHeight();
        this.requestChildrenUpdate();
        this.notifyHeightChangeListener(null, b);
    }
    
    public void setQsContainer(final ViewGroup mQsContainer) {
        this.mQsContainer = mQsContainer;
    }
    
    public void setQsCustomizerShowing(final boolean qsCustomizerShowing) {
        this.mAmbientState.setQsCustomizerShowing(qsCustomizerShowing);
        this.requestChildrenUpdate();
    }
    
    public void setQsExpanded(final boolean mQsExpanded) {
        this.mQsExpanded = mQsExpanded;
        this.updateAlgorithmLayoutMinHeight();
        this.updateScrollability();
    }
    
    public void setQsExpansionFraction(final float mQsExpansionFraction) {
        this.mQsExpansionFraction = mQsExpansionFraction;
    }
    
    public void setScrimController(final ScrimController mScrimController) {
        (this.mScrimController = mScrimController).setScrimBehindChangeRunnable(new _$$Lambda$NotificationStackScrollLayout$EebmavE8B0v9pYEId75j8vvZNvI(this));
    }
    
    public void setScrollingEnabled(final boolean mScrollingEnabled) {
        this.mScrollingEnabled = mScrollingEnabled;
    }
    
    public void setShelf(final NotificationShelf shelf) {
        final NotificationShelf mShelf = this.mShelf;
        int indexOfChild;
        if (mShelf != null) {
            indexOfChild = this.indexOfChild((View)mShelf);
            this.removeView((View)this.mShelf);
        }
        else {
            indexOfChild = -1;
        }
        this.addView((View)(this.mShelf = shelf), indexOfChild);
        this.mAmbientState.setShelf(shelf);
        this.mStateAnimator.setShelf(shelf);
        shelf.bind(this.mAmbientState, this);
    }
    
    public void setShouldShowShelfOnly(final boolean mShouldShowShelfOnly) {
        this.mShouldShowShelfOnly = mShouldShowShelfOnly;
        this.updateAlgorithmLayoutMinHeight();
    }
    
    public void setStatusBar(final StatusBar mStatusBar) {
        this.mStatusBar = mStatusBar;
    }
    
    @VisibleForTesting
    protected void setStatusBarState(final int n) {
        this.mStatusBarState = n;
        this.mAmbientState.setStatusBarState(n);
    }
    
    public void setTrackingHeadsUp(final ExpandableNotificationRow trackingHeadsUp) {
        this.mTrackingHeadsUp = (trackingHeadsUp != null);
        this.mRoundnessManager.setTrackingHeadsUp(trackingHeadsUp);
    }
    
    public void setUnlockHintRunning(final boolean unlockHintRunning) {
        this.mAmbientState.setUnlockHintRunning(unlockHintRunning);
    }
    
    public void setWillExpand(final boolean mWillExpand) {
        this.mWillExpand = mWillExpand;
    }
    
    public boolean shouldDelayChildPressedState() {
        return true;
    }
    
    public void updateClipping() {
        final Rect mRequestedClipBounds = this.mRequestedClipBounds;
        final boolean b = true;
        final boolean mIsClipped = mRequestedClipBounds != null && !this.mInHeadsUpPinnedMode && !this.mHeadsUpAnimatingAway;
        if (this.mIsClipped != mIsClipped) {
            this.mIsClipped = mIsClipped;
        }
        boolean clipToOutline;
        if (this.mAmbientState.isHiddenAtAll()) {
            this.invalidateOutline();
            clipToOutline = b;
            if (this.isFullyHidden()) {
                this.setClipBounds((Rect)null);
                clipToOutline = b;
            }
        }
        else {
            if (mIsClipped) {
                this.setClipBounds(this.mRequestedClipBounds);
            }
            else {
                this.setClipBounds((Rect)null);
            }
            clipToOutline = false;
        }
        this.setClipToOutline(clipToOutline);
    }
    
    public void updateDecorViews(final boolean mUsingLightTheme) {
        if (mUsingLightTheme == this.mUsingLightTheme) {
            return;
        }
        this.mUsingLightTheme = mUsingLightTheme;
        final Context mContext = super.mContext;
        int n;
        if (mUsingLightTheme) {
            n = R$style.Theme_SystemUI_Light;
        }
        else {
            n = R$style.Theme_SystemUI;
        }
        final int colorAttrDefaultColor = Utils.getColorAttrDefaultColor((Context)new ContextThemeWrapper(mContext, n), R$attr.wallpaperTextColor);
        this.mSectionsManager.setHeaderForegroundColor(colorAttrDefaultColor);
        this.mFooterView.setTextColor(colorAttrDefaultColor);
        this.mEmptyShadeView.setTextColor(colorAttrDefaultColor);
    }
    
    public void updateEmptyShadeView(final boolean b) {
        this.mEmptyShadeView.setVisible(b, this.mIsExpanded && this.mAnimationsEnabled);
        final int textResource = this.mEmptyShadeView.getTextResource();
        int text;
        if (this.mZenController.areNotificationsHiddenInShade()) {
            text = R$string.dnd_suppressing_shade_text;
        }
        else {
            text = R$string.empty_shade_text;
        }
        if (textResource != text) {
            this.mEmptyShadeView.setText(text);
        }
    }
    
    @VisibleForTesting
    public void updateFooter() {
        final boolean mClearAllEnabled = this.mClearAllEnabled;
        boolean b = true;
        final boolean b2 = mClearAllEnabled && this.hasActiveClearableNotifications(0);
        if ((!b2 && !this.hasActiveNotifications()) || this.mStatusBarState == 1 || this.mRemoteInputManager.getController().isRemoteInputActive()) {
            b = false;
        }
        this.updateFooterView(b, b2);
    }
    
    public void updateFooterView(final boolean b, final boolean b2) {
        if (this.mFooterView == null) {
            return;
        }
        final boolean b3 = this.mIsExpanded && this.mAnimationsEnabled;
        this.mFooterView.setVisible(b, b3);
        this.mFooterView.setSecondaryVisible(b2, b3);
    }
    
    public void updateIconAreaViews() {
        this.mIconAreaController.updateNotificationIcons();
    }
    
    public void updateSectionBoundaries() {
        this.mSectionsManager.updateSectionBoundaries();
    }
    
    public void updateSpeedBumpIndex() {
        final int childCount = this.getChildCount();
        boolean b = false;
        int n = 0;
        int n3;
        int n2 = n3 = n;
        while (true) {
            boolean b2 = true;
            if (n >= childCount) {
                break;
            }
            final View child = this.getChildAt(n);
            int n4 = n3;
            int n5 = n2;
            if (child.getVisibility() != 8) {
                if (!(child instanceof ExpandableNotificationRow)) {
                    n4 = n3;
                    n5 = n2;
                }
                else {
                    final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)child;
                    ++n2;
                    if (this.mHighPriorityBeforeSpeedBump) {
                        if (expandableNotificationRow.getEntry().getBucket() >= 4) {
                            b2 = false;
                        }
                    }
                    else {
                        b2 = (true ^ expandableNotificationRow.getEntry().isAmbient());
                    }
                    n4 = n3;
                    n5 = n2;
                    if (b2) {
                        n4 = n2;
                        n5 = n2;
                    }
                }
            }
            ++n;
            n3 = n4;
            n2 = n5;
        }
        if (n3 == childCount) {
            b = true;
        }
        this.updateSpeedBumpIndex(n3, b);
    }
    
    public void updateSpeedBumpIndex(final int speedBumpIndex, final boolean b) {
        this.mAmbientState.setSpeedBumpIndex(speedBumpIndex);
    }
    
    public void updateTopPadding(final float n, final boolean b) {
        final int n2 = (int)n;
        final int n3 = this.getLayoutMinHeight() + n2;
        if (n3 > this.getHeight()) {
            this.mTopPaddingOverflow = (float)(n3 - this.getHeight());
        }
        else {
            this.mTopPaddingOverflow = 0.0f;
        }
        this.setTopPadding(n2, b && !this.mKeyguardBypassController.getBypassEnabled());
        this.setExpandedHeight(this.mExpandedHeight);
    }
    
    public void wakeUpFromPulse() {
        this.setPulseHeight(this.getWakeUpHeight());
        final int childCount = this.getChildCount();
        float translationY = -1.0f;
        int i = 0;
        int n = 1;
        while (i < childCount) {
            final ExpandableView expandableView = (ExpandableView)this.getChildAt(i);
            float n2;
            int n3;
            if (expandableView.getVisibility() == 8) {
                n2 = translationY;
                n3 = n;
            }
            else {
                final boolean b = expandableView == this.mShelf;
                if (!(expandableView instanceof ExpandableNotificationRow) && !b) {
                    n2 = translationY;
                    n3 = n;
                }
                else if (expandableView.getVisibility() == 0 && !b) {
                    n2 = translationY;
                    if ((n3 = n) != 0) {
                        n2 = expandableView.getTranslationY() + expandableView.getActualHeight() - this.mShelf.getIntrinsicHeight();
                        n3 = 0;
                    }
                }
                else {
                    n2 = translationY;
                    if ((n3 = n) == 0) {
                        expandableView.setTranslationY(translationY);
                        n3 = n;
                        n2 = translationY;
                    }
                }
            }
            ++i;
            translationY = n2;
            n = n3;
        }
        this.mDimmedNeedsAnimation = true;
    }
    
    static class AnimationEvent
    {
        static AnimationFilter[] FILTERS;
        static int[] LENGTHS;
        final int animationType;
        final AnimationFilter filter;
        boolean headsUpFromBottom;
        final long length;
        final ExpandableView mChangingView;
        View viewAfterChangingView;
        
        static {
            final AnimationFilter animationFilter = new AnimationFilter();
            animationFilter.animateHeight();
            animationFilter.animateTopInset();
            animationFilter.animateY();
            animationFilter.animateZ();
            animationFilter.hasDelays();
            final AnimationFilter animationFilter2 = new AnimationFilter();
            animationFilter2.animateHeight();
            animationFilter2.animateTopInset();
            animationFilter2.animateY();
            animationFilter2.animateZ();
            animationFilter2.hasDelays();
            final AnimationFilter animationFilter3 = new AnimationFilter();
            animationFilter3.animateHeight();
            animationFilter3.animateTopInset();
            animationFilter3.animateY();
            animationFilter3.animateZ();
            animationFilter3.hasDelays();
            final AnimationFilter animationFilter4 = new AnimationFilter();
            animationFilter4.animateHeight();
            animationFilter4.animateTopInset();
            animationFilter4.animateY();
            animationFilter4.animateDimmed();
            animationFilter4.animateZ();
            final AnimationFilter animationFilter5 = new AnimationFilter();
            animationFilter5.animateZ();
            final AnimationFilter animationFilter6 = new AnimationFilter();
            animationFilter6.animateDimmed();
            final AnimationFilter animationFilter7 = new AnimationFilter();
            animationFilter7.animateAlpha();
            animationFilter7.animateHeight();
            animationFilter7.animateTopInset();
            animationFilter7.animateY();
            animationFilter7.animateZ();
            final AnimationFilter animationFilter8 = new AnimationFilter();
            animationFilter8.animateHeight();
            animationFilter8.animateTopInset();
            animationFilter8.animateY();
            animationFilter8.animateDimmed();
            animationFilter8.animateZ();
            animationFilter8.hasDelays();
            final AnimationFilter animationFilter9 = new AnimationFilter();
            animationFilter9.animateHideSensitive();
            final AnimationFilter animationFilter10 = new AnimationFilter();
            animationFilter10.animateHeight();
            animationFilter10.animateTopInset();
            animationFilter10.animateY();
            animationFilter10.animateZ();
            final AnimationFilter animationFilter11 = new AnimationFilter();
            animationFilter11.animateAlpha();
            animationFilter11.animateHeight();
            animationFilter11.animateTopInset();
            animationFilter11.animateY();
            animationFilter11.animateZ();
            final AnimationFilter animationFilter12 = new AnimationFilter();
            animationFilter12.animateHeight();
            animationFilter12.animateTopInset();
            animationFilter12.animateY();
            animationFilter12.animateZ();
            final AnimationFilter animationFilter13 = new AnimationFilter();
            animationFilter13.animateHeight();
            animationFilter13.animateTopInset();
            animationFilter13.animateY();
            animationFilter13.animateZ();
            animationFilter13.hasDelays();
            final AnimationFilter animationFilter14 = new AnimationFilter();
            animationFilter14.animateHeight();
            animationFilter14.animateTopInset();
            animationFilter14.animateY();
            animationFilter14.animateZ();
            animationFilter14.hasDelays();
            final AnimationFilter animationFilter15 = new AnimationFilter();
            animationFilter15.animateHeight();
            animationFilter15.animateTopInset();
            animationFilter15.animateY();
            animationFilter15.animateZ();
            final AnimationFilter animationFilter16 = new AnimationFilter();
            animationFilter16.animateAlpha();
            animationFilter16.animateDimmed();
            animationFilter16.animateHideSensitive();
            animationFilter16.animateHeight();
            animationFilter16.animateTopInset();
            animationFilter16.animateY();
            animationFilter16.animateZ();
            AnimationEvent.FILTERS = new AnimationFilter[] { animationFilter, animationFilter2, animationFilter3, animationFilter4, animationFilter5, animationFilter6, animationFilter7, animationFilter8, animationFilter9, animationFilter10, animationFilter11, animationFilter12, animationFilter13, animationFilter14, animationFilter15, animationFilter16 };
            AnimationEvent.LENGTHS = new int[] { 464, 464, 360, 360, 220, 220, 360, 448, 360, 360, 360, 550, 300, 300, 360, 360 };
        }
        
        AnimationEvent(final ExpandableView expandableView, final int n) {
            this(expandableView, n, AnimationEvent.LENGTHS[n]);
        }
        
        AnimationEvent(final ExpandableView expandableView, final int n, final long n2) {
            this(expandableView, n, n2, AnimationEvent.FILTERS[n]);
        }
        
        AnimationEvent(final ExpandableView mChangingView, final int animationType, final long length, final AnimationFilter filter) {
            AnimationUtils.currentAnimationTimeMillis();
            this.mChangingView = mChangingView;
            this.animationType = animationType;
            this.length = length;
            this.filter = filter;
        }
        
        static long combineLength(final ArrayList<AnimationEvent> list) {
            final int size = list.size();
            long max = 0L;
            for (int i = 0; i < size; ++i) {
                final AnimationEvent animationEvent = list.get(i);
                max = Math.max(max, animationEvent.length);
                if (animationEvent.animationType == 7) {
                    return animationEvent.length;
                }
            }
            return max;
        }
    }
    
    enum NotificationPanelEvent implements UiEventLogger$UiEventEnum
    {
        DISMISS_ALL_NOTIFICATIONS_PANEL(312), 
        DISMISS_SILENT_NOTIFICATIONS_PANEL(314), 
        INVALID(0);
        
        private final int mId;
        
        private NotificationPanelEvent(final int mId) {
            this.mId = mId;
        }
        
        public static UiEventLogger$UiEventEnum fromSelection(final int n) {
            if (n == 0) {
                return (UiEventLogger$UiEventEnum)NotificationPanelEvent.DISMISS_ALL_NOTIFICATIONS_PANEL;
            }
            if (n == 2) {
                return (UiEventLogger$UiEventEnum)NotificationPanelEvent.DISMISS_SILENT_NOTIFICATIONS_PANEL;
            }
            return (UiEventLogger$UiEventEnum)NotificationPanelEvent.INVALID;
        }
        
        public int getId() {
            return this.mId;
        }
    }
    
    public interface OnEmptySpaceClickListener
    {
        void onEmptySpaceClicked(final float p0, final float p1);
    }
    
    public interface OnOverscrollTopChangedListener
    {
        void flingTopOverscroll(final float p0, final boolean p1);
        
        void onOverscrollTopChanged(final float p0, final boolean p1);
    }
}
