// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.os.AsyncTask;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.stack.AmbientState;
import android.animation.TimeInterpolator;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Collection;
import android.os.Bundle;
import android.view.accessibility.AccessibilityRecord;
import android.view.accessibility.AccessibilityEvent;
import com.android.systemui.plugins.Plugin;
import android.view.KeyEvent;
import com.android.systemui.R$string;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.ViewStub$OnInflateListener;
import com.android.systemui.R$id;
import android.content.res.Configuration;
import android.graphics.Paint;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import android.app.NotificationChannel;
import android.util.ArraySet;
import android.animation.Animator$AnimatorListener;
import android.animation.ObjectAnimator;
import com.android.systemui.statusbar.StatusBarIconView;
import java.util.Arrays;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.InflatedSmartReplies;
import android.graphics.Path;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.view.MotionEvent;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import com.android.systemui.statusbar.notification.stack.ExpandableViewState;
import android.util.MathUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import java.util.List;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import com.android.internal.widget.CachingIconView;
import android.view.NotificationHeaderView;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.Chronometer;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import com.android.settingslib.Utils;
import com.android.systemui.statusbar.phone.StatusBar;
import android.content.res.Resources;
import com.android.systemui.R$bool;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.R$dimen;
import android.os.AsyncTask$Status;
import android.service.notification.StatusBarNotification;
import android.animation.AnimatorListenerAdapter;
import com.android.internal.logging.MetricsLogger;
import android.util.AttributeSet;
import android.content.Context;
import android.util.FloatProperty;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import android.animation.Animator;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import java.util.function.BooleanSupplier;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.function.Consumer;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.plugins.FalsingManager;
import android.view.View$OnClickListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.view.ViewStub;
import com.android.systemui.statusbar.notification.stack.NotificationChildrenContainer;
import android.view.View;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.notification.AboveShelfChangedListener;
import android.util.Property;
import com.android.systemui.statusbar.notification.stack.NotificationListItem;
import com.android.systemui.statusbar.notification.stack.SwipeableView;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.plugins.PluginListener;

public class ExpandableNotificationRow extends ActivatableNotificationView implements PluginListener<NotificationMenuRowPlugin>, SwipeableView, NotificationListItem
{
    private static final long RECENTLY_ALERTED_THRESHOLD_MS;
    private static final Property<ExpandableNotificationRow, Float> TRANSLATE_CONTENT;
    private boolean mAboveShelf;
    private AboveShelfChangedListener mAboveShelfChangedListener;
    private String mAppName;
    private KeyguardBypassController mBypassController;
    private View mChildAfterViewWhenDismissed;
    private boolean mChildIsExpanding;
    private NotificationChildrenContainer mChildrenContainer;
    private ViewStub mChildrenContainerStub;
    private boolean mChildrenExpanded;
    private boolean mEnableNonGroupedNotificationExpand;
    private NotificationEntry mEntry;
    private boolean mExpandAnimationRunning;
    private View$OnClickListener mExpandClickListener;
    private boolean mExpandable;
    private boolean mExpandedWhenPinned;
    private OnExpansionChangedListener mExpansionChangedListener;
    private final Runnable mExpireRecentlyAlertedFlag;
    private FalsingManager mFalsingManager;
    private boolean mForceUnlocked;
    private boolean mGroupExpansionChanging;
    private NotificationGroupManager mGroupManager;
    private View mGroupParentWhenDismissed;
    private NotificationGuts mGuts;
    private ViewStub mGutsStub;
    private boolean mHasUserChangedExpansion;
    private float mHeaderVisibleAmount;
    private Consumer<Boolean> mHeadsUpAnimatingAwayListener;
    private HeadsUpManager mHeadsUpManager;
    private boolean mHeadsupDisappearRunning;
    private boolean mHideSensitiveForIntrinsicHeight;
    private boolean mIconAnimationRunning;
    private int mIconTransformContentShift;
    private NotificationInlineImageResolver mImageResolver;
    private int mIncreasedPaddingBetweenElements;
    private boolean mIsBlockingHelperShowing;
    private boolean mIsChildInGroup;
    private boolean mIsColorized;
    private boolean mIsHeadsUp;
    private boolean mIsLowPriority;
    private boolean mIsPinned;
    private boolean mIsSummaryWithChildren;
    private boolean mIsSystemChildExpanded;
    private boolean mIsSystemExpanded;
    private boolean mJustClicked;
    private boolean mKeepInParent;
    private boolean mLastChronometerRunning;
    private LayoutListener mLayoutListener;
    private NotificationContentView[] mLayouts;
    private ExpansionLogger mLogger;
    private String mLoggingKey;
    private LongPressListener mLongPressListener;
    private int mMaxHeadsUpHeight;
    private int mMaxHeadsUpHeightBeforeN;
    private int mMaxHeadsUpHeightBeforeP;
    private int mMaxHeadsUpHeightIncreased;
    private NotificationMediaManager mMediaManager;
    private NotificationMenuRowPlugin mMenuRow;
    private boolean mMustStayOnScreen;
    private boolean mNeedsRedaction;
    private int mNotificationColor;
    private int mNotificationLaunchHeight;
    private int mNotificationMaxHeight;
    private int mNotificationMinHeight;
    private int mNotificationMinHeightBeforeN;
    private int mNotificationMinHeightBeforeP;
    private int mNotificationMinHeightLarge;
    private int mNotificationMinHeightMedia;
    private ExpandableNotificationRow mNotificationParent;
    private boolean mNotificationTranslationFinished;
    private View$OnClickListener mOnAppOpsClickListener;
    private View$OnClickListener mOnClickListener;
    private Runnable mOnDismissRunnable;
    private OnExpandClickListener mOnExpandClickListener;
    private Runnable mOnIntrinsicHeightReachedRunnable;
    private boolean mOnKeyguard;
    private PeopleNotificationIdentifier mPeopleNotificationIdentifier;
    private NotificationContentView mPrivateLayout;
    private NotificationContentView mPublicLayout;
    private boolean mRemoved;
    private RowContentBindStage mRowContentBindStage;
    private BooleanSupplier mSecureStateProvider;
    private boolean mSensitive;
    private boolean mSensitiveHiddenInGeneral;
    private boolean mShelfIconVisible;
    private boolean mShowGroupBackgroundWhenExpanded;
    private boolean mShowNoBackground;
    private boolean mShowingPublic;
    private boolean mShowingPublicInitialized;
    private StatusBarStateController mStatusbarStateController;
    private SystemNotificationAsyncTask mSystemNotificationAsyncTask;
    private Animator mTranslateAnim;
    private ArrayList<View> mTranslateableViews;
    private float mTranslationWhenRemoved;
    private boolean mUpdateBackgroundOnUpdate;
    private boolean mUseIncreasedCollapsedHeight;
    private boolean mUseIncreasedHeadsUpHeight;
    private boolean mUserExpanded;
    private boolean mUserLocked;
    private boolean mWasChildInGroupWhenRemoved;
    
    static {
        RECENTLY_ALERTED_THRESHOLD_MS = TimeUnit.SECONDS.toMillis(30L);
        TRANSLATE_CONTENT = (Property)new FloatProperty<ExpandableNotificationRow>() {
            public Float get(final ExpandableNotificationRow expandableNotificationRow) {
                return expandableNotificationRow.getTranslation();
            }
            
            public void setValue(final ExpandableNotificationRow expandableNotificationRow, final float translation) {
                expandableNotificationRow.setTranslation(translation);
            }
        };
    }
    
    public ExpandableNotificationRow(final Context context, final AttributeSet set) {
        super(context, set);
        this.mNotificationTranslationFinished = false;
        this.mHeaderVisibleAmount = 1.0f;
        this.mLastChronometerRunning = true;
        this.mExpandClickListener = (View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                if (!ExpandableNotificationRow.this.shouldShowPublic() && (!ExpandableNotificationRow.this.mIsLowPriority || ExpandableNotificationRow.this.isExpanded()) && ExpandableNotificationRow.this.mGroupManager.isSummaryOfGroup(ExpandableNotificationRow.this.mEntry.getSbn())) {
                    ExpandableNotificationRow.this.mGroupExpansionChanging = true;
                    final boolean groupExpanded = ExpandableNotificationRow.this.mGroupManager.isGroupExpanded(ExpandableNotificationRow.this.mEntry.getSbn());
                    final boolean toggleGroupExpansion = ExpandableNotificationRow.this.mGroupManager.toggleGroupExpansion(ExpandableNotificationRow.this.mEntry.getSbn());
                    ExpandableNotificationRow.this.mOnExpandClickListener.onExpandClicked(ExpandableNotificationRow.this.mEntry, toggleGroupExpansion);
                    MetricsLogger.action(ExpandableNotificationRow.this.mContext, 408, toggleGroupExpansion);
                    ExpandableNotificationRow.this.onExpansionChanged(true, groupExpanded);
                }
                else if (ExpandableNotificationRow.this.mEnableNonGroupedNotificationExpand) {
                    if (view.isAccessibilityFocused()) {
                        ExpandableNotificationRow.this.mPrivateLayout.setFocusOnVisibilityChange();
                    }
                    boolean userExpanded;
                    if (ExpandableNotificationRow.this.isPinned()) {
                        final boolean b = ExpandableNotificationRow.this.mExpandedWhenPinned ^ true;
                        ExpandableNotificationRow.this.mExpandedWhenPinned = b;
                        userExpanded = b;
                        if (ExpandableNotificationRow.this.mExpansionChangedListener != null) {
                            ExpandableNotificationRow.this.mExpansionChangedListener.onExpansionChanged(b);
                            userExpanded = b;
                        }
                    }
                    else {
                        userExpanded = (ExpandableNotificationRow.this.isExpanded() ^ true);
                        ExpandableNotificationRow.this.setUserExpanded(userExpanded);
                    }
                    ExpandableNotificationRow.this.notifyHeightChanged(true);
                    ExpandableNotificationRow.this.mOnExpandClickListener.onExpandClicked(ExpandableNotificationRow.this.mEntry, userExpanded);
                    MetricsLogger.action(ExpandableNotificationRow.this.mContext, 407, userExpanded);
                }
            }
        };
        this.mSystemNotificationAsyncTask = new SystemNotificationAsyncTask();
        this.mExpireRecentlyAlertedFlag = new _$$Lambda$ExpandableNotificationRow$XUb_nXphAmdEb1BUkM4zkhhpdtg(this);
        this.mImageResolver = new NotificationInlineImageResolver(context, (NotificationInlineImageResolver.ImageCache)new NotificationInlineImageCache());
        this.initDimens();
    }
    
    private void animateShowingPublic(final long n, final long n2, final boolean b) {
        View[] array;
        if (this.mIsSummaryWithChildren) {
            array = new View[] { (View)this.mChildrenContainer };
        }
        else {
            array = new View[] { (View)this.mPrivateLayout };
        }
        final View[] array2 = { (View)this.mPublicLayout };
        View[] array3;
        if (b) {
            array3 = array;
        }
        else {
            array3 = array2;
        }
        if (b) {
            array = array2;
        }
        for (final View view : array3) {
            view.setVisibility(0);
            view.animate().cancel();
            view.animate().alpha(0.0f).setStartDelay(n).setDuration(n2).withEndAction((Runnable)new Runnable(this) {
                @Override
                public void run() {
                    view.setVisibility(4);
                }
            });
        }
        for (final View view2 : array) {
            view2.setVisibility(0);
            view2.setAlpha(0.0f);
            view2.animate().cancel();
            view2.animate().alpha(1.0f).setStartDelay(n).setDuration(n2);
        }
    }
    
    private void applyAudiblyAlertedRecently(final boolean recentlyAudiblyAlerted) {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.setRecentlyAudiblyAlerted(recentlyAudiblyAlerted);
        }
        this.mPrivateLayout.setRecentlyAudiblyAlerted(recentlyAudiblyAlerted);
        this.mPublicLayout.setRecentlyAudiblyAlerted(recentlyAudiblyAlerted);
    }
    
    private void applyChildrenRoundness() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.setCurrentBottomRoundness(this.getCurrentBottomRoundness());
        }
    }
    
    private void cacheIsSystemNotification() {
        final NotificationEntry mEntry = this.mEntry;
        if (mEntry != null && mEntry.mIsSystemNotification == null && this.mSystemNotificationAsyncTask.getStatus() == AsyncTask$Status.PENDING) {
            this.mSystemNotificationAsyncTask.execute((Object[])new Void[0]);
        }
    }
    
    private void doLongClickCallback() {
        this.doLongClickCallback(this.getWidth() / 2, this.getHeight() / 2);
    }
    
    private void doLongClickCallback(final int n, final int n2, final NotificationMenuRowPlugin.MenuItem menuItem) {
        final LongPressListener mLongPressListener = this.mLongPressListener;
        if (mLongPressListener != null && menuItem != null) {
            mLongPressListener.onLongPress((View)this, n, n2, menuItem);
        }
    }
    
    private int getHeadsUpHeight() {
        return this.getShowingLayout().getHeadsUpHeight(false);
    }
    
    private int getPinnedHeadsUpHeight(final boolean b) {
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getIntrinsicHeight();
        }
        if (this.mExpandedWhenPinned) {
            return Math.max(this.getMaxExpandHeight(), this.getHeadsUpHeight());
        }
        if (b) {
            return Math.max(this.getCollapsedHeight(), this.getHeadsUpHeight());
        }
        return this.getHeadsUpHeight();
    }
    
    private void handleIntrinsicHeightReached() {
        if (this.mOnIntrinsicHeightReachedRunnable != null && this.getActualHeight() == this.getIntrinsicHeight()) {
            this.mOnIntrinsicHeightReachedRunnable.run();
            this.mOnIntrinsicHeightReachedRunnable = null;
        }
    }
    
    private boolean hasNoRounding() {
        return this.getCurrentBottomRoundness() == 0.0f && this.getCurrentTopRoundness() == 0.0f;
    }
    
    private void initDimens() {
        this.mNotificationMinHeightBeforeN = NotificationUtils.getFontScaledHeight(super.mContext, R$dimen.notification_min_height_legacy);
        this.mNotificationMinHeightBeforeP = NotificationUtils.getFontScaledHeight(super.mContext, R$dimen.notification_min_height_before_p);
        this.mNotificationMinHeight = NotificationUtils.getFontScaledHeight(super.mContext, R$dimen.notification_min_height);
        this.mNotificationMinHeightLarge = NotificationUtils.getFontScaledHeight(super.mContext, R$dimen.notification_min_height_increased);
        this.mNotificationMinHeightMedia = NotificationUtils.getFontScaledHeight(super.mContext, R$dimen.notification_min_height_media);
        this.mNotificationMaxHeight = NotificationUtils.getFontScaledHeight(super.mContext, R$dimen.notification_max_height);
        this.mMaxHeadsUpHeightBeforeN = NotificationUtils.getFontScaledHeight(super.mContext, R$dimen.notification_max_heads_up_height_legacy);
        this.mMaxHeadsUpHeightBeforeP = NotificationUtils.getFontScaledHeight(super.mContext, R$dimen.notification_max_heads_up_height_before_p);
        this.mMaxHeadsUpHeight = NotificationUtils.getFontScaledHeight(super.mContext, R$dimen.notification_max_heads_up_height);
        this.mMaxHeadsUpHeightIncreased = NotificationUtils.getFontScaledHeight(super.mContext, R$dimen.notification_max_heads_up_height_increased);
        final Resources resources = this.getResources();
        this.mIncreasedPaddingBetweenElements = resources.getDimensionPixelSize(R$dimen.notification_divider_height_increased);
        this.mEnableNonGroupedNotificationExpand = resources.getBoolean(R$bool.config_enableNonGroupedNotificationExpand);
        this.mShowGroupBackgroundWhenExpanded = resources.getBoolean(R$bool.config_showGroupNotificationBgWhenExpanded);
    }
    
    private boolean isBlockingHelperShowingAndCanTranslate() {
        return this.areGutsExposed() && this.mIsBlockingHelperShowing && this.mNotificationTranslationFinished;
    }
    
    private boolean isBypassEnabled() {
        final KeyguardBypassController mBypassController = this.mBypassController;
        return mBypassController == null || mBypassController.getBypassEnabled();
    }
    
    private boolean isColorized() {
        return this.mIsColorized && super.mBgTint != 0;
    }
    
    private boolean isDozing() {
        final StatusBarStateController mStatusbarStateController = this.mStatusbarStateController;
        return mStatusbarStateController != null && mStatusbarStateController.isDozing();
    }
    
    private boolean isSystemChildExpanded() {
        return this.mIsSystemChildExpanded;
    }
    
    private static Boolean isSystemNotification(final Context context, final StatusBarNotification statusBarNotification) {
        final PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(context, statusBarNotification.getUser().getIdentifier());
        Boolean value;
        try {
            value = Utils.isSystemPackage(context.getResources(), packageManagerForUser, packageManagerForUser.getPackageInfo(statusBarNotification.getPackageName(), 64));
        }
        catch (PackageManager$NameNotFoundException ex) {
            Log.e("ExpandableNotifRow", "cacheIsSystemNotification: Could not find package info");
            value = null;
        }
        return value;
    }
    
    private void onChildrenCountChanged() {
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        final boolean mIsSummaryWithChildren = mChildrenContainer != null && mChildrenContainer.getNotificationChildCount() > 0;
        this.mIsSummaryWithChildren = mIsSummaryWithChildren;
        if (mIsSummaryWithChildren && this.mChildrenContainer.getHeaderView() == null) {
            this.mChildrenContainer.recreateNotificationHeader(this.mExpandClickListener);
        }
        this.getShowingLayout().updateBackgroundColor(false);
        this.mPrivateLayout.updateExpandButtons(this.isExpandable());
        this.updateChildrenHeaderAppearance();
        this.updateChildrenVisibility();
        this.applyChildrenRoundness();
    }
    
    private void onExpansionChanged(final boolean b, final boolean b2) {
        boolean b4;
        final boolean b3 = b4 = this.isExpanded();
        Label_0045: {
            if (this.mIsSummaryWithChildren) {
                if (this.mIsLowPriority) {
                    b4 = b3;
                    if (!b2) {
                        break Label_0045;
                    }
                }
                b4 = this.mGroupManager.isGroupExpanded(this.mEntry.getSbn());
            }
        }
        if (b4 != b2) {
            this.updateShelfIconColor();
            final ExpansionLogger mLogger = this.mLogger;
            if (mLogger != null) {
                mLogger.logNotificationExpansion(this.mLoggingKey, b, b4);
            }
            if (this.mIsSummaryWithChildren) {
                this.mChildrenContainer.onExpansionChanged();
            }
            final OnExpansionChangedListener mExpansionChangedListener = this.mExpansionChangedListener;
            if (mExpansionChangedListener != null) {
                mExpansionChangedListener.onExpansionChanged(b4);
            }
        }
    }
    
    private void reInflateViews() {
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null) {
            mChildrenContainer.reInflateViews(this.mExpandClickListener, this.mEntry.getSbn());
        }
        final NotificationGuts mGuts = this.mGuts;
        final int n = 0;
        if (mGuts != null) {
            final int indexOfChild = this.indexOfChild((View)mGuts);
            this.removeView((View)mGuts);
            final NotificationGuts mGuts2 = (NotificationGuts)LayoutInflater.from(super.mContext).inflate(R$layout.notification_guts, (ViewGroup)this, false);
            this.mGuts = mGuts2;
            int visibility;
            if (mGuts.isExposed()) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            mGuts2.setVisibility(visibility);
            this.addView((View)this.mGuts, indexOfChild);
        }
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        View menuView;
        if (mMenuRow == null) {
            menuView = null;
        }
        else {
            menuView = mMenuRow.getMenuView();
        }
        if (menuView != null) {
            final int indexOfChild2 = this.indexOfChild(menuView);
            this.removeView(menuView);
            this.mMenuRow.createMenu((ViewGroup)this, this.mEntry.getSbn());
            this.mMenuRow.setAppName(this.mAppName);
            this.addView(this.mMenuRow.getMenuView(), indexOfChild2);
        }
        final NotificationContentView[] mLayouts = this.mLayouts;
        for (int length = mLayouts.length, i = n; i < length; ++i) {
            final NotificationContentView notificationContentView = mLayouts[i];
            notificationContentView.initView();
            notificationContentView.reInflateViews();
        }
        this.mEntry.getSbn().clearPackageContext();
        this.mRowContentBindStage.getStageParams(this.mEntry).setNeedsReinflation(true);
        this.mRowContentBindStage.requestRebind(this.mEntry, null);
    }
    
    private void setChildIsExpanding(final boolean mChildIsExpanding) {
        this.mChildIsExpanding = mChildIsExpanding;
        this.updateClipping();
        this.invalidate();
    }
    
    private void setChronometerRunning(final boolean b, final NotificationContentView notificationContentView) {
        if (notificationContentView != null) {
            final boolean b2 = b || this.isPinned();
            final View contractedChild = notificationContentView.getContractedChild();
            final View expandedChild = notificationContentView.getExpandedChild();
            final View headsUpChild = notificationContentView.getHeadsUpChild();
            this.setChronometerRunningForChild(b2, contractedChild);
            this.setChronometerRunningForChild(b2, expandedChild);
            this.setChronometerRunningForChild(b2, headsUpChild);
        }
    }
    
    private void setChronometerRunningForChild(final boolean started, View viewById) {
        if (viewById != null) {
            viewById = viewById.findViewById(16908833);
            if (viewById instanceof Chronometer) {
                ((Chronometer)viewById).setStarted(started);
            }
        }
    }
    
    private void setIconAnimationRunning(final boolean b, final NotificationContentView notificationContentView) {
        if (notificationContentView != null) {
            final View contractedChild = notificationContentView.getContractedChild();
            final View expandedChild = notificationContentView.getExpandedChild();
            final View headsUpChild = notificationContentView.getHeadsUpChild();
            this.setIconAnimationRunningForChild(b, contractedChild);
            this.setIconAnimationRunningForChild(b, expandedChild);
            this.setIconAnimationRunningForChild(b, headsUpChild);
        }
    }
    
    private void setIconAnimationRunningForChild(final boolean b, final View view) {
        if (view != null) {
            this.setIconRunning((ImageView)view.findViewById(16908294), b);
            this.setIconRunning((ImageView)view.findViewById(16909351), b);
        }
    }
    
    private void setIconRunning(final ImageView imageView, final boolean b) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AnimationDrawable) {
                final AnimationDrawable animationDrawable = (AnimationDrawable)drawable;
                if (b) {
                    animationDrawable.start();
                }
                else {
                    animationDrawable.stop();
                }
            }
            else if (drawable instanceof AnimatedVectorDrawable) {
                final AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable)drawable;
                if (b) {
                    animatedVectorDrawable.start();
                }
                else {
                    animatedVectorDrawable.stop();
                }
            }
        }
    }
    
    private boolean shouldShowPublic() {
        return this.mSensitive && this.mHideSensitiveForIntrinsicHeight;
    }
    
    private void updateChildAccessibilityImportance(final View view, final boolean b) {
        int importantForAccessibility;
        if (b) {
            importantForAccessibility = 0;
        }
        else {
            importantForAccessibility = 4;
        }
        view.setImportantForAccessibility(importantForAccessibility);
    }
    
    private void updateChildrenVisibility() {
        final boolean mExpandAnimationRunning = this.mExpandAnimationRunning;
        final int n = 0;
        boolean b = false;
        Label_0036: {
            if (mExpandAnimationRunning) {
                final NotificationGuts mGuts = this.mGuts;
                if (mGuts != null && mGuts.isExposed()) {
                    b = true;
                    break Label_0036;
                }
            }
            b = false;
        }
        final NotificationContentView mPrivateLayout = this.mPrivateLayout;
        int visibility;
        if (!this.mShowingPublic && !this.mIsSummaryWithChildren && !b) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        mPrivateLayout.setVisibility(visibility);
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null) {
            int visibility2;
            if (!this.mShowingPublic && this.mIsSummaryWithChildren && !b) {
                visibility2 = n;
            }
            else {
                visibility2 = 4;
            }
            mChildrenContainer.setVisibility(visibility2);
        }
        this.updateLimits();
    }
    
    private void updateClickAndFocus() {
        final boolean childInGroup = this.isChildInGroup();
        final boolean b = false;
        final boolean focusable = !childInGroup || this.isGroupExpanded();
        boolean clickable = b;
        if (this.mOnClickListener != null) {
            clickable = b;
            if (focusable) {
                clickable = true;
            }
        }
        if (this.isFocusable() != focusable) {
            this.setFocusable(focusable);
        }
        if (this.isClickable() != clickable) {
            this.setClickable(clickable);
        }
    }
    
    private void updateContentAccessibilityImportanceForGuts(final boolean b) {
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null) {
            this.updateChildAccessibilityImportance((View)mChildrenContainer, b);
        }
        final NotificationContentView[] mLayouts = this.mLayouts;
        if (mLayouts != null) {
            for (int length = mLayouts.length, i = 0; i < length; ++i) {
                this.updateChildAccessibilityImportance((View)mLayouts[i], b);
            }
        }
        if (b) {
            this.requestAccessibilityFocus();
        }
    }
    
    private void updateContentShiftHeight() {
        final NotificationHeaderView visibleNotificationHeader = this.getVisibleNotificationHeader();
        if (visibleNotificationHeader != null) {
            final CachingIconView icon = visibleNotificationHeader.getIcon();
            this.mIconTransformContentShift = this.getRelativeTopPadding((View)icon) + icon.getHeight();
        }
        else {
            this.mIconTransformContentShift = super.mContentShift;
        }
    }
    
    private void updateIconVisibilities() {
        final boolean childInGroup = this.isChildInGroup();
        int i = 0;
        final boolean b = !childInGroup && this.mShelfIconVisible;
        for (NotificationContentView[] mLayouts = this.mLayouts; i < mLayouts.length; ++i) {
            mLayouts[i].setShelfIconVisible(b);
        }
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null) {
            mChildrenContainer.setShelfIconVisible(b);
        }
    }
    
    private void updateLimits() {
        final NotificationContentView[] mLayouts = this.mLayouts;
        for (int length = mLayouts.length, i = 0; i < length; ++i) {
            this.updateLimitsForView(mLayouts[i]);
        }
    }
    
    private void updateLimitsForView(final NotificationContentView notificationContentView) {
        final View contractedChild = notificationContentView.getContractedChild();
        final int n = 1;
        final boolean b = contractedChild != null && notificationContentView.getContractedChild().getId() != 16909463;
        final boolean b2 = this.mEntry.targetSdk < 24;
        final boolean b3 = this.mEntry.targetSdk < 28;
        final View expandedChild = notificationContentView.getExpandedChild();
        final boolean b4 = expandedChild != null && expandedChild.findViewById(16909139) != null;
        final boolean showCompactMediaSeekbar = this.mMediaManager.getShowCompactMediaSeekbar();
        int n2;
        if (b && b3 && !this.mIsSummaryWithChildren) {
            if (b2) {
                n2 = this.mNotificationMinHeightBeforeN;
            }
            else {
                n2 = this.mNotificationMinHeightBeforeP;
            }
        }
        else if (b4 && showCompactMediaSeekbar) {
            n2 = this.mNotificationMinHeightMedia;
        }
        else if (this.mUseIncreasedCollapsedHeight && notificationContentView == this.mPrivateLayout) {
            n2 = this.mNotificationMinHeightLarge;
        }
        else {
            n2 = this.mNotificationMinHeight;
        }
        int n3;
        if (notificationContentView.getHeadsUpChild() != null && notificationContentView.getHeadsUpChild().getId() != 16909463) {
            n3 = n;
        }
        else {
            n3 = 0;
        }
        int a;
        if (n3 != 0 && b3) {
            if (b2) {
                a = this.mMaxHeadsUpHeightBeforeN;
            }
            else {
                a = this.mMaxHeadsUpHeightBeforeP;
            }
        }
        else if (this.mUseIncreasedHeadsUpHeight && notificationContentView == this.mPrivateLayout) {
            a = this.mMaxHeadsUpHeightIncreased;
        }
        else {
            a = this.mMaxHeadsUpHeight;
        }
        final NotificationViewWrapper visibleWrapper = notificationContentView.getVisibleWrapper(2);
        int max = a;
        if (visibleWrapper != null) {
            max = Math.max(a, visibleWrapper.getMinLayoutHeight());
        }
        notificationContentView.setHeights(n2, max, this.mNotificationMaxHeight);
    }
    
    private void updateNotificationColor() {
        this.mNotificationColor = ContrastColorUtil.resolveContrastColor(super.mContext, this.mEntry.getSbn().getNotification().color, this.getBackgroundColorWithoutTint(), (this.getResources().getConfiguration().uiMode & 0x30) == 0x20);
    }
    
    private void updateRippleAllowed() {
        this.setRippleAllowed(this.isOnKeyguard() || this.mEntry.getSbn().getNotification().contentIntent == null);
    }
    
    public void addChildNotification(final ExpandableNotificationRow expandableNotificationRow, final int n) {
        if (this.mChildrenContainer == null) {
            this.mChildrenContainerStub.inflate();
        }
        this.mChildrenContainer.addNotification(expandableNotificationRow, n);
        this.onChildrenCountChanged();
        expandableNotificationRow.setIsChildInGroup(true, this);
    }
    
    @Override
    public void addChildNotification(final NotificationListItem notificationListItem, final int n) {
        this.addChildNotification((ExpandableNotificationRow)notificationListItem.getView(), n);
    }
    
    public void animateTranslateNotification(final float n) {
        final Animator mTranslateAnim = this.mTranslateAnim;
        if (mTranslateAnim != null) {
            mTranslateAnim.cancel();
        }
        final Animator translateViewAnimator = this.getTranslateViewAnimator(n, null);
        if ((this.mTranslateAnim = translateViewAnimator) != null) {
            translateViewAnimator.start();
        }
    }
    
    @Override
    public boolean applyChildOrder(final List<? extends NotificationListItem> list, final VisualStabilityManager visualStabilityManager, final VisualStabilityManager.Callback callback) {
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        return mChildrenContainer != null && mChildrenContainer.applyChildOrder(list, visualStabilityManager, callback);
    }
    
    public void applyChildrenState() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.applyState();
        }
    }
    
    @Override
    protected void applyContentTransformation(float n, final float n2) {
        super.applyContentTransformation(n, n2);
        if (!super.mIsLastChild) {
            n = 1.0f;
        }
        for (final NotificationContentView notificationContentView : this.mLayouts) {
            notificationContentView.setAlpha(n);
            notificationContentView.setTranslationY(n2);
        }
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null) {
            mChildrenContainer.setAlpha(n);
            this.mChildrenContainer.setTranslationY(n2);
        }
    }
    
    public void applyExpandAnimationParams(final ActivityLaunchAnimator.ExpandAnimationParameters expandAnimationParams) {
        if (expandAnimationParams == null) {
            return;
        }
        final float lerp = MathUtils.lerp(expandAnimationParams.getStartTranslationZ(), (float)this.mNotificationLaunchHeight, Interpolators.FAST_OUT_SLOW_IN.getInterpolation(expandAnimationParams.getProgress(0L, 50L)));
        this.setTranslationZ(lerp);
        final float n = expandAnimationParams.getWidth() - this.getWidth() + MathUtils.lerp(0.0f, super.mOutlineRadius * 2.0f, expandAnimationParams.getProgress());
        this.setExtraWidthForClipping(n);
        final int top = expandAnimationParams.getTop();
        final float interpolation = Interpolators.FAST_OUT_SLOW_IN.getInterpolation(expandAnimationParams.getProgress());
        final int startClipTopAmount = expandAnimationParams.getStartClipTopAmount();
        final ExpandableNotificationRow mNotificationParent = this.mNotificationParent;
        int n2;
        if (mNotificationParent != null) {
            final float translationY = mNotificationParent.getTranslationY();
            n2 = (int)(top - translationY);
            this.mNotificationParent.setTranslationZ(lerp);
            final int parentStartClipTopAmount = expandAnimationParams.getParentStartClipTopAmount();
            if (startClipTopAmount != 0) {
                this.mNotificationParent.setClipTopAmount((int)MathUtils.lerp((float)parentStartClipTopAmount, (float)(parentStartClipTopAmount - startClipTopAmount), interpolation));
            }
            this.mNotificationParent.setExtraWidthForClipping(n);
            this.mNotificationParent.setMinimumHeightForClipping((int)(Math.max((float)expandAnimationParams.getBottom(), this.mNotificationParent.getActualHeight() + translationY - this.mNotificationParent.getClipBottomAmount()) - Math.min((float)expandAnimationParams.getTop(), translationY)));
        }
        else {
            n2 = top;
            if (startClipTopAmount != 0) {
                this.setClipTopAmount((int)MathUtils.lerp((float)startClipTopAmount, 0.0f, interpolation));
                n2 = top;
            }
        }
        this.setTranslationY((float)n2);
        this.setActualHeight(expandAnimationParams.getHeight());
        super.mBackgroundNormal.setExpandAnimationParams(expandAnimationParams);
    }
    
    @Override
    protected void applyRoundness() {
        super.applyRoundness();
        this.applyChildrenRoundness();
    }
    
    @Override
    public boolean areChildrenExpanded() {
        return this.mChildrenExpanded;
    }
    
    public boolean areGutsExposed() {
        final NotificationGuts mGuts = this.mGuts;
        return mGuts != null && mGuts.isExposed();
    }
    
    public boolean canShowHeadsUp() {
        return !this.mOnKeyguard || this.isDozing() || this.isBypassEnabled();
    }
    
    public boolean canViewBeDismissed() {
        return this.mEntry.isClearable() && (!this.shouldShowPublic() || !this.mSensitiveHiddenInGeneral);
    }
    
    @Override
    protected boolean childNeedsClipping(final View view) {
        if (view instanceof NotificationContentView) {
            final NotificationContentView notificationContentView = (NotificationContentView)view;
            if (this.isClippingNeeded()) {
                return true;
            }
            if (!this.hasNoRounding()) {
                final float currentTopRoundness = this.getCurrentTopRoundness();
                boolean b = false;
                final boolean b2 = currentTopRoundness != 0.0f;
                if (this.getCurrentBottomRoundness() != 0.0f) {
                    b = true;
                }
                if (notificationContentView.shouldClipToRounding(b2, b)) {
                    return true;
                }
            }
        }
        else if (view == this.mChildrenContainer) {
            if (this.isClippingNeeded() || !this.hasNoRounding()) {
                return true;
            }
        }
        else if (view instanceof NotificationGuts) {
            return this.hasNoRounding() ^ true;
        }
        return super.childNeedsClipping(view);
    }
    
    public void closeRemoteInput() {
        final NotificationContentView[] mLayouts = this.mLayouts;
        for (int length = mLayouts.length, i = 0; i < length; ++i) {
            mLayouts[i].closeRemoteInput();
        }
    }
    
    public ExpandableViewState createExpandableViewState() {
        return new NotificationViewState();
    }
    
    @Override
    public NotificationMenuRowPlugin createMenu() {
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        if (mMenuRow == null) {
            return null;
        }
        if (mMenuRow.getMenuView() == null) {
            this.mMenuRow.createMenu((ViewGroup)this, this.mEntry.getSbn());
            this.mMenuRow.setAppName(this.mAppName);
            this.addView(this.mMenuRow.getMenuView(), 0, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
        }
        return this.mMenuRow;
    }
    
    @Override
    protected boolean disallowSingleClick(final MotionEvent motionEvent) {
        if (this.areGutsExposed()) {
            return false;
        }
        final float x = motionEvent.getX();
        final float y = motionEvent.getY();
        final NotificationHeaderView visibleNotificationHeader = this.getVisibleNotificationHeader();
        return (visibleNotificationHeader != null && visibleNotificationHeader.isInTouchRect(x - this.getTranslation(), y)) || ((!this.mIsSummaryWithChildren || this.shouldShowPublic()) && this.getShowingLayout().disallowSingleClick(x, y)) || super.disallowSingleClick(motionEvent);
    }
    
    @Override
    public void dismiss(final boolean b) {
        super.dismiss(b);
        this.setLongPressListener(null);
        this.mGroupParentWhenDismissed = (View)this.mNotificationParent;
        this.mChildAfterViewWhenDismissed = null;
        this.mEntry.getIcons().getStatusBarIcon().setDismissed();
        if (this.isChildInGroup()) {
            final List<ExpandableNotificationRow> notificationChildren = this.mNotificationParent.getNotificationChildren();
            final int index = notificationChildren.indexOf(this);
            if (index != -1 && index < notificationChildren.size() - 1) {
                this.mChildAfterViewWhenDismissed = (View)notificationChildren.get(index + 1);
            }
        }
    }
    
    public void doLongClickCallback(final int n, final int n2) {
        this.createMenu();
        final NotificationMenuRowPlugin provider = this.getProvider();
        Object longpressMenuItem;
        if (provider != null) {
            longpressMenuItem = provider.getLongpressMenuItem(super.mContext);
        }
        else {
            longpressMenuItem = null;
        }
        this.doLongClickCallback(n, n2, (NotificationMenuRowPlugin.MenuItem)longpressMenuItem);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        super.dump(fileDescriptor, printWriter, array);
        final StringBuilder sb = new StringBuilder();
        sb.append("  Notification: ");
        sb.append(this.mEntry.getKey());
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("    visibility: ");
        sb2.append(this.getVisibility());
        printWriter.print(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(", alpha: ");
        sb3.append(this.getAlpha());
        printWriter.print(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(", translation: ");
        sb4.append(this.getTranslation());
        printWriter.print(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append(", removed: ");
        sb5.append(this.isRemoved());
        printWriter.print(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append(", expandAnimationRunning: ");
        sb6.append(this.mExpandAnimationRunning);
        printWriter.print(sb6.toString());
        final NotificationContentView showingLayout = this.getShowingLayout();
        final StringBuilder sb7 = new StringBuilder();
        sb7.append(", privateShowing: ");
        sb7.append(showingLayout == this.mPrivateLayout);
        printWriter.print(sb7.toString());
        printWriter.println();
        showingLayout.dump(fileDescriptor, printWriter, array);
        printWriter.print("    ");
        if (this.getViewState() != null) {
            this.getViewState().dump(fileDescriptor, printWriter, array);
        }
        else {
            printWriter.print("no viewState!!!");
        }
        printWriter.println();
        printWriter.println();
        if (this.mIsSummaryWithChildren) {
            printWriter.print("  ChildrenContainer");
            final StringBuilder sb8 = new StringBuilder();
            sb8.append(" visibility: ");
            sb8.append(this.mChildrenContainer.getVisibility());
            printWriter.print(sb8.toString());
            final StringBuilder sb9 = new StringBuilder();
            sb9.append(", alpha: ");
            sb9.append(this.mChildrenContainer.getAlpha());
            printWriter.print(sb9.toString());
            final StringBuilder sb10 = new StringBuilder();
            sb10.append(", translationY: ");
            sb10.append(this.mChildrenContainer.getTranslationY());
            printWriter.print(sb10.toString());
            printWriter.println();
            final List<ExpandableNotificationRow> notificationChildren = this.getNotificationChildren();
            final StringBuilder sb11 = new StringBuilder();
            sb11.append("  Children: ");
            sb11.append(notificationChildren.size());
            printWriter.println(sb11.toString());
            printWriter.println("  {");
            final Iterator<ExpandableNotificationRow> iterator = notificationChildren.iterator();
            while (iterator.hasNext()) {
                iterator.next().dump(fileDescriptor, printWriter, array);
            }
            printWriter.println("  }");
            printWriter.println();
        }
    }
    
    void ensureGutsInflated() {
        if (this.mGuts == null) {
            this.mGutsStub.inflate();
        }
    }
    
    public CharSequence getActiveRemoteInputText() {
        return this.mPrivateLayout.getActiveRemoteInputText();
    }
    
    String getAppName() {
        return this.mAppName;
    }
    
    public View$OnClickListener getAppOpsOnClickListener() {
        return this.mOnAppOpsClickListener;
    }
    
    public View getChildAfterViewWhenDismissed() {
        return this.mChildAfterViewWhenDismissed;
    }
    
    public NotificationChildrenContainer getChildrenContainer() {
        return this.mChildrenContainer;
    }
    
    @Override
    public int getCollapsedHeight() {
        if (this.mIsSummaryWithChildren && !this.shouldShowPublic()) {
            return this.mChildrenContainer.getCollapsedHeight();
        }
        return this.getMinHeight();
    }
    
    @Override
    protected float getContentTransformationShift() {
        return (float)this.mIconTransformContentShift;
    }
    
    @Override
    protected View getContentView() {
        if (this.mIsSummaryWithChildren && !this.shouldShowPublic()) {
            return (View)this.mChildrenContainer;
        }
        return (View)this.getShowingLayout();
    }
    
    @Override
    public Path getCustomClipPath(final View view) {
        if (view instanceof NotificationGuts) {
            return this.getClipPath(true);
        }
        return super.getCustomClipPath(view);
    }
    
    @Override
    public NotificationEntry getEntry() {
        return this.mEntry;
    }
    
    public InflatedSmartReplies.SmartRepliesAndActions getExistingSmartRepliesAndActions() {
        return this.mPrivateLayout.getCurrentSmartRepliesAndActions();
    }
    
    public View getExpandedContentView() {
        return this.getPrivateLayout().getExpandedChild();
    }
    
    @Override
    public int getExtraBottomPadding() {
        if (this.mIsSummaryWithChildren && this.isGroupExpanded()) {
            return this.mIncreasedPaddingBetweenElements;
        }
        return 0;
    }
    
    public View getGroupParentWhenDismissed() {
        return this.mGroupParentWhenDismissed;
    }
    
    public NotificationGuts getGuts() {
        return this.mGuts;
    }
    
    @Override
    public float getHeaderVisibleAmount() {
        return this.mHeaderVisibleAmount;
    }
    
    @Override
    public int getHeadsUpHeightWithoutHeader() {
        if (!this.canShowHeadsUp() || !this.mIsHeadsUp) {
            return this.getCollapsedHeight();
        }
        if (this.mIsSummaryWithChildren && !this.shouldShowPublic()) {
            return this.mChildrenContainer.getCollapsedHeightWithoutHeader();
        }
        return this.getShowingLayout().getHeadsUpHeight(true);
    }
    
    public HeadsUpManager getHeadsUpManager() {
        return this.mHeadsUpManager;
    }
    
    NotificationInlineImageResolver getImageResolver() {
        return this.mImageResolver;
    }
    
    @Override
    public float getIncreasedPaddingAmount() {
        if (this.mIsSummaryWithChildren) {
            if (this.isGroupExpanded()) {
                return 1.0f;
            }
            if (this.isUserLocked()) {
                return this.mChildrenContainer.getIncreasedPaddingAmount();
            }
        }
        else if (this.isColorized() && (!this.mIsLowPriority || this.isExpanded())) {
            return -1.0f;
        }
        return 0.0f;
    }
    
    @Override
    public int getIntrinsicHeight() {
        if (this.isUserLocked()) {
            return this.getActualHeight();
        }
        final NotificationGuts mGuts = this.mGuts;
        if (mGuts != null && mGuts.isExposed()) {
            return this.mGuts.getIntrinsicHeight();
        }
        if (this.isChildInGroup() && !this.isGroupExpanded()) {
            return this.mPrivateLayout.getMinHeight();
        }
        if (this.mSensitive && this.mHideSensitiveForIntrinsicHeight) {
            return this.getMinHeight();
        }
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getIntrinsicHeight();
        }
        if (this.canShowHeadsUp() && this.isHeadsUpState()) {
            if (this.isPinned() || this.mHeadsupDisappearRunning) {
                return this.getPinnedHeadsUpHeight(true);
            }
            if (this.isExpanded()) {
                return Math.max(this.getMaxExpandHeight(), this.getHeadsUpHeight());
            }
            return Math.max(this.getCollapsedHeight(), this.getHeadsUpHeight());
        }
        else {
            if (this.isExpanded()) {
                return this.getMaxExpandHeight();
            }
            return this.getCollapsedHeight();
        }
    }
    
    public boolean getIsNonblockable() {
        final boolean nonblockable = Dependency.get(NotificationBlockingHelperManager.class).isNonblockable(this.mEntry.getSbn().getPackageName(), this.mEntry.getChannel().getId());
        final NotificationEntry mEntry = this.mEntry;
        boolean b = true;
        if (mEntry != null && mEntry.mIsSystemNotification == null) {
            this.mSystemNotificationAsyncTask.cancel(true);
            final NotificationEntry mEntry2 = this.mEntry;
            mEntry2.mIsSystemNotification = isSystemNotification(super.mContext, mEntry2.getSbn());
        }
        final boolean b2 = nonblockable | this.mEntry.getChannel().isImportanceLockedByOEM() | this.mEntry.getChannel().isImportanceLockedByCriticalDeviceFunction();
        if (!b2) {
            final NotificationEntry mEntry3 = this.mEntry;
            if (mEntry3 != null) {
                final Boolean mIsSystemNotification = mEntry3.mIsSystemNotification;
                if (mIsSystemNotification != null && mIsSystemNotification && this.mEntry.getChannel() != null && !this.mEntry.getChannel().isBlockable()) {
                    return b;
                }
            }
        }
        b = b2;
        return b;
    }
    
    public NotificationContentView[] getLayouts() {
        final NotificationContentView[] mLayouts = this.mLayouts;
        return Arrays.copyOf(mLayouts, mLayouts.length);
    }
    
    @Override
    public int getMaxContentHeight() {
        if (this.mIsSummaryWithChildren && !this.shouldShowPublic()) {
            return this.mChildrenContainer.getMaxContentHeight();
        }
        return this.getShowingLayout().getMaxHeight();
    }
    
    public int getMaxExpandHeight() {
        return this.mPrivateLayout.getExpandHeight();
    }
    
    @Override
    public int getMinHeight(final boolean b) {
        if (!b) {
            final NotificationGuts mGuts = this.mGuts;
            if (mGuts != null && mGuts.isExposed()) {
                return this.mGuts.getIntrinsicHeight();
            }
        }
        if (!b && this.canShowHeadsUp() && this.mIsHeadsUp && this.mHeadsUpManager.isTrackingHeadsUp()) {
            return this.getPinnedHeadsUpHeight(false);
        }
        if (this.mIsSummaryWithChildren && !this.isGroupExpanded() && !this.shouldShowPublic()) {
            return this.mChildrenContainer.getMinHeight();
        }
        if (!b && this.canShowHeadsUp() && this.mIsHeadsUp) {
            return this.getHeadsUpHeight();
        }
        return this.getShowingLayout().getMinHeight();
    }
    
    @Override
    public List<ExpandableNotificationRow> getNotificationChildren() {
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        List<ExpandableNotificationRow> notificationChildren;
        if (mChildrenContainer == null) {
            notificationChildren = null;
        }
        else {
            notificationChildren = mChildrenContainer.getNotificationChildren();
        }
        return notificationChildren;
    }
    
    public int getNotificationColor() {
        return this.mNotificationColor;
    }
    
    public NotificationHeaderView getNotificationHeader() {
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getHeaderView();
        }
        return this.mPrivateLayout.getNotificationHeader();
    }
    
    public ExpandableNotificationRow getNotificationParent() {
        return this.mNotificationParent;
    }
    
    public int getNumUniqueChannels() {
        return this.getUniqueChannels().size();
    }
    
    public int getOriginalIconColor() {
        if (this.mIsSummaryWithChildren && !this.shouldShowPublic()) {
            return this.mChildrenContainer.getVisibleHeader().getOriginalIconColor();
        }
        final int originalIconColor = this.getShowingLayout().getOriginalIconColor();
        boolean b = true;
        if (originalIconColor != 1) {
            return originalIconColor;
        }
        final NotificationEntry mEntry = this.mEntry;
        final Context mContext = super.mContext;
        if (!this.mIsLowPriority || this.isExpanded()) {
            b = false;
        }
        return mEntry.getContrastedColor(mContext, b, this.getBackgroundColorWithoutTint());
    }
    
    @Override
    public int getPinnedHeadsUpHeight() {
        return this.getPinnedHeadsUpHeight(true);
    }
    
    public int getPositionOfChild(final ExpandableNotificationRow expandableNotificationRow) {
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getPositionInLinearLayout((View)expandableNotificationRow);
        }
        return 0;
    }
    
    public NotificationContentView getPrivateLayout() {
        return this.mPrivateLayout;
    }
    
    public NotificationMenuRowPlugin getProvider() {
        return this.mMenuRow;
    }
    
    public NotificationContentView getPublicLayout() {
        return this.mPublicLayout;
    }
    
    @Override
    public StatusBarIconView getShelfIcon() {
        return this.getEntry().getIcons().getShelfIcon();
    }
    
    @Override
    public View getShelfTransformationTarget() {
        if (this.mIsSummaryWithChildren && !this.shouldShowPublic()) {
            return (View)this.mChildrenContainer.getVisibleHeader().getIcon();
        }
        return this.getShowingLayout().getShelfTransformationTarget();
    }
    
    public NotificationContentView getShowingLayout() {
        NotificationContentView notificationContentView;
        if (this.shouldShowPublic()) {
            notificationContentView = this.mPublicLayout;
        }
        else {
            notificationContentView = this.mPrivateLayout;
        }
        return notificationContentView;
    }
    
    public HybridNotificationView getSingleLineView() {
        return this.mPrivateLayout.getSingleLineView();
    }
    
    public Animator getTranslateViewAnimator(final float n, final ValueAnimator$AnimatorUpdateListener valueAnimator$AnimatorUpdateListener) {
        final Animator mTranslateAnim = this.mTranslateAnim;
        if (mTranslateAnim != null) {
            mTranslateAnim.cancel();
        }
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)this, (Property)ExpandableNotificationRow.TRANSLATE_CONTENT, new float[] { n });
        if (valueAnimator$AnimatorUpdateListener != null) {
            ofFloat.addUpdateListener(valueAnimator$AnimatorUpdateListener);
        }
        ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            boolean cancelled = false;
            
            public void onAnimationCancel(final Animator animator) {
                this.cancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (ExpandableNotificationRow.this.mIsBlockingHelperShowing) {
                    ExpandableNotificationRow.this.mNotificationTranslationFinished = true;
                }
                if (!this.cancelled && n == 0.0f) {
                    if (ExpandableNotificationRow.this.mMenuRow != null) {
                        ExpandableNotificationRow.this.mMenuRow.resetMenu();
                    }
                    ExpandableNotificationRow.this.mTranslateAnim = null;
                }
            }
        });
        return this.mTranslateAnim = (Animator)ofFloat;
    }
    
    @Override
    public float getTranslation() {
        if (!super.mShouldTranslateContents) {
            return this.getTranslationX();
        }
        if (this.isBlockingHelperShowingAndCanTranslate()) {
            return this.mGuts.getTranslationX();
        }
        final ArrayList<View> mTranslateableViews = this.mTranslateableViews;
        if (mTranslateableViews != null && mTranslateableViews.size() > 0) {
            return this.mTranslateableViews.get(0).getTranslationX();
        }
        return 0.0f;
    }
    
    public float getTranslationWhenRemoved() {
        return this.mTranslationWhenRemoved;
    }
    
    public ArraySet<NotificationChannel> getUniqueChannels() {
        final ArraySet set = new ArraySet();
        set.add((Object)this.mEntry.getChannel());
        if (this.mIsSummaryWithChildren) {
            final List<ExpandableNotificationRow> notificationChildren = this.getNotificationChildren();
            for (int size = notificationChildren.size(), i = 0; i < size; ++i) {
                final ExpandableNotificationRow expandableNotificationRow = notificationChildren.get(i);
                final NotificationChannel channel = expandableNotificationRow.getEntry().getChannel();
                final StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
                if (sbn.getUser().equals((Object)this.mEntry.getSbn().getUser()) && sbn.getPackageName().equals(this.mEntry.getSbn().getPackageName())) {
                    set.add((Object)channel);
                }
            }
        }
        return (ArraySet<NotificationChannel>)set;
    }
    
    @Override
    public View getView() {
        return (View)this;
    }
    
    public ExpandableNotificationRow getViewAtPosition(final float n) {
        ExpandableNotificationRow viewAtPosition = this;
        if (this.mIsSummaryWithChildren) {
            if (!this.mChildrenExpanded) {
                viewAtPosition = this;
            }
            else {
                viewAtPosition = this.mChildrenContainer.getViewAtPosition(n);
                if (viewAtPosition == null) {
                    viewAtPosition = this;
                }
            }
        }
        return viewAtPosition;
    }
    
    public NotificationHeaderView getVisibleNotificationHeader() {
        if (this.mIsSummaryWithChildren && !this.shouldShowPublic()) {
            return this.mChildrenContainer.getVisibleHeader();
        }
        return this.getShowingLayout().getVisibleNotificationHeader();
    }
    
    @Override
    protected boolean handleSlideBack() {
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        if (mMenuRow != null && mMenuRow.isMenuVisible()) {
            this.animateTranslateNotification(0.0f);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean hasExpandingChild() {
        return this.mChildIsExpanding;
    }
    
    @Override
    public boolean hasFinishedInitialization() {
        return this.getEntry().hasFinishedInitialization();
    }
    
    public boolean hasUserChangedExpansion() {
        return this.mHasUserChangedExpansion;
    }
    
    public void initialize(final String mAppName, final String mLoggingKey, final ExpansionLogger mLogger, final KeyguardBypassController mBypassController, final NotificationGroupManager notificationGroupManager, final HeadsUpManager mHeadsUpManager, final RowContentBindStage mRowContentBindStage, final OnExpandClickListener mOnExpandClickListener, final NotificationMediaManager mMediaManager, final OnAppOpsClickListener appOpsOnClickListener, final FalsingManager mFalsingManager, final StatusBarStateController mStatusbarStateController, final PeopleNotificationIdentifier mPeopleNotificationIdentifier) {
        this.mAppName = mAppName;
        if (this.mMenuRow == null) {
            this.mMenuRow = new NotificationMenuRow(super.mContext, mPeopleNotificationIdentifier);
        }
        if (this.mMenuRow.getMenuView() != null) {
            this.mMenuRow.setAppName(this.mAppName);
        }
        this.mLogger = mLogger;
        this.mLoggingKey = mLoggingKey;
        this.mBypassController = mBypassController;
        this.mGroupManager = notificationGroupManager;
        this.mPrivateLayout.setGroupManager(notificationGroupManager);
        this.mHeadsUpManager = mHeadsUpManager;
        this.mRowContentBindStage = mRowContentBindStage;
        this.mOnExpandClickListener = mOnExpandClickListener;
        this.mMediaManager = mMediaManager;
        this.setAppOpsOnClickListener(appOpsOnClickListener);
        this.mFalsingManager = mFalsingManager;
        this.mStatusbarStateController = mStatusbarStateController;
        this.mPeopleNotificationIdentifier = mPeopleNotificationIdentifier;
    }
    
    @Override
    public boolean isAboveShelf() {
        return this.canShowHeadsUp() && (this.mIsPinned || this.mHeadsupDisappearRunning || (this.mIsHeadsUp && this.mAboveShelf) || this.mExpandAnimationRunning || this.mChildIsExpanding);
    }
    
    @Override
    public boolean isBlockingHelperShowing() {
        return this.mIsBlockingHelperShowing;
    }
    
    public boolean isBlockingHelperShowingAndTranslationFinished() {
        return this.mIsBlockingHelperShowing && this.mNotificationTranslationFinished;
    }
    
    @Override
    public boolean isChildInGroup() {
        return this.mNotificationParent != null;
    }
    
    @Override
    public boolean isContentExpandable() {
        return (this.mIsSummaryWithChildren && !this.shouldShowPublic()) || this.getShowingLayout().isContentExpandable();
    }
    
    @Override
    public boolean isDimmable() {
        return this.getShowingLayout().isDimmable() && !this.showingPulsing() && super.isDimmable();
    }
    
    @Override
    public boolean isExpandAnimationRunning() {
        return this.mExpandAnimationRunning;
    }
    
    public boolean isExpandable() {
        final boolean mIsSummaryWithChildren = this.mIsSummaryWithChildren;
        boolean b = true;
        if (mIsSummaryWithChildren && !this.shouldShowPublic()) {
            return this.mChildrenExpanded ^ true;
        }
        if (!this.mEnableNonGroupedNotificationExpand || !this.mExpandable) {
            b = false;
        }
        return b;
    }
    
    public boolean isExpanded() {
        return this.isExpanded(false);
    }
    
    public boolean isExpanded(final boolean b) {
        return (!this.mOnKeyguard || b) && ((!this.hasUserChangedExpansion() && (this.isSystemExpanded() || this.isSystemChildExpanded())) || this.isUserExpanded());
    }
    
    @Override
    public boolean isGroupExpanded() {
        return this.mGroupManager.isGroupExpanded(this.mEntry.getSbn());
    }
    
    @Override
    public boolean isGroupExpansionChanging() {
        if (this.isChildInGroup()) {
            return this.mNotificationParent.isGroupExpansionChanging();
        }
        return this.mGroupExpansionChanging;
    }
    
    public boolean isGroupNotFullyVisible() {
        return this.getClipTopAmount() > 0 || this.getTranslationY() < 0.0f;
    }
    
    public boolean isHeadsUp() {
        return this.mIsHeadsUp;
    }
    
    @Override
    public boolean isHeadsUpAnimatingAway() {
        return this.mHeadsupDisappearRunning;
    }
    
    public boolean isHeadsUpState() {
        return this.mIsHeadsUp || this.mHeadsupDisappearRunning;
    }
    
    public boolean isLowPriority() {
        return this.mIsLowPriority;
    }
    
    public boolean isMediaRow() {
        return this.getExpandedContentView() != null && this.getExpandedContentView().findViewById(16909139) != null;
    }
    
    public boolean isOnKeyguard() {
        return this.mOnKeyguard;
    }
    
    public boolean isOnlyChildInGroup() {
        return this.mGroupManager.isOnlyChildInGroup(this.mEntry.getSbn());
    }
    
    @Override
    public boolean isPinned() {
        return this.mIsPinned;
    }
    
    public boolean isPinnedAndExpanded() {
        return this.isPinned() && this.mExpandedWhenPinned;
    }
    
    @Override
    public boolean isRemoved() {
        return this.mRemoved;
    }
    
    public boolean isShowingIcon() {
        final boolean gutsExposed = this.areGutsExposed();
        boolean b = false;
        if (gutsExposed) {
            return false;
        }
        if (this.getShelfTransformationTarget() != null) {
            b = true;
        }
        return b;
    }
    
    public boolean isSoundEffectsEnabled() {
        final StatusBarStateController mStatusbarStateController = this.mStatusbarStateController;
        boolean b = true;
        boolean b2 = false;
        Label_0045: {
            if (mStatusbarStateController != null && mStatusbarStateController.isDozing()) {
                final BooleanSupplier mSecureStateProvider = this.mSecureStateProvider;
                if (mSecureStateProvider != null && !mSecureStateProvider.getAsBoolean()) {
                    b2 = true;
                    break Label_0045;
                }
            }
            b2 = false;
        }
        if (b2 || !super.isSoundEffectsEnabled()) {
            b = false;
        }
        return b;
    }
    
    @Override
    public boolean isSummaryWithChildren() {
        return this.mIsSummaryWithChildren;
    }
    
    public boolean isSystemExpanded() {
        return this.mIsSystemExpanded;
    }
    
    public boolean isTopLevelChild() {
        return this.getParent() instanceof NotificationStackScrollLayout;
    }
    
    public boolean isUserExpanded() {
        return this.mUserExpanded;
    }
    
    public boolean isUserLocked() {
        return this.mUserLocked && !this.mForceUnlocked;
    }
    
    public boolean keepInParent() {
        return this.mKeepInParent;
    }
    
    public void makeActionsVisibile() {
        this.setUserExpanded(true, true);
        if (this.isChildInGroup()) {
            this.mGroupManager.setGroupExpanded(this.mEntry.getSbn(), true);
        }
        this.notifyHeightChanged(false);
    }
    
    @Override
    public boolean mustStayOnScreen() {
        return this.mIsHeadsUp && this.mMustStayOnScreen;
    }
    
    @Override
    public void notifyHeightChanged(final boolean b) {
        super.notifyHeightChanged(b);
        this.getShowingLayout().requestSelectLayout(b || this.isUserLocked());
    }
    
    @Override
    protected void onAppearAnimationFinished(final boolean b) {
        super.onAppearAnimationFinished(b);
        if (b) {
            final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
            if (mChildrenContainer != null) {
                mChildrenContainer.setAlpha(1.0f);
                this.mChildrenContainer.setLayerType(0, (Paint)null);
            }
            for (final NotificationContentView notificationContentView : this.mLayouts) {
                notificationContentView.setAlpha(1.0f);
                notificationContentView.setLayerType(0, (Paint)null);
            }
        }
        else {
            this.setHeadsUpAnimatingAway(false);
        }
    }
    
    @Override
    protected void onBelowSpeedBumpChanged() {
        this.updateIconVisibilities();
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        if (mMenuRow != null && mMenuRow.getMenuView() != null) {
            this.mMenuRow.onConfigurationChanged();
        }
        final NotificationInlineImageResolver mImageResolver = this.mImageResolver;
        if (mImageResolver != null) {
            mImageResolver.updateMaxImageSizes();
        }
    }
    
    @Override
    public void onDensityOrFontScaleChanged() {
        super.onDensityOrFontScaleChanged();
        this.initDimens();
        this.initBackground();
        this.reInflateViews();
    }
    
    public void onExpandedByGesture(final boolean b) {
        int n;
        if (this.mGroupManager.isSummaryOfGroup(this.mEntry.getSbn())) {
            n = 410;
        }
        else {
            n = 409;
        }
        MetricsLogger.action(super.mContext, n, b);
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mPublicLayout = (NotificationContentView)this.findViewById(R$id.expandedPublic);
        final NotificationContentView mPrivateLayout = (NotificationContentView)this.findViewById(R$id.expanded);
        this.mPrivateLayout = mPrivateLayout;
        final NotificationContentView[] mLayouts = new NotificationContentView[2];
        final int n = 0;
        mLayouts[0] = mPrivateLayout;
        mLayouts[1] = this.mPublicLayout;
        this.mLayouts = mLayouts;
        for (final NotificationContentView notificationContentView : mLayouts) {
            notificationContentView.setExpandClickListener(this.mExpandClickListener);
            notificationContentView.setContainingNotification(this);
        }
        (this.mGutsStub = (ViewStub)this.findViewById(R$id.notification_guts_stub)).setOnInflateListener((ViewStub$OnInflateListener)new ViewStub$OnInflateListener() {
            public void onInflate(final ViewStub viewStub, final View view) {
                ExpandableNotificationRow.this.mGuts = (NotificationGuts)view;
                ExpandableNotificationRow.this.mGuts.setClipTopAmount(ExpandableNotificationRow.this.getClipTopAmount());
                ExpandableNotificationRow.this.mGuts.setActualHeight(ExpandableNotificationRow.this.getActualHeight());
                ExpandableNotificationRow.this.mGutsStub = null;
            }
        });
        (this.mChildrenContainerStub = (ViewStub)this.findViewById(R$id.child_container_stub)).setOnInflateListener((ViewStub$OnInflateListener)new ViewStub$OnInflateListener() {
            public void onInflate(final ViewStub viewStub, final View view) {
                ExpandableNotificationRow.this.mChildrenContainer = (NotificationChildrenContainer)view;
                ExpandableNotificationRow.this.mChildrenContainer.setIsLowPriority(ExpandableNotificationRow.this.mIsLowPriority);
                ExpandableNotificationRow.this.mChildrenContainer.setContainingNotification(ExpandableNotificationRow.this);
                ExpandableNotificationRow.this.mChildrenContainer.onNotificationUpdated();
                final ExpandableNotificationRow this$0 = ExpandableNotificationRow.this;
                if (this$0.mShouldTranslateContents) {
                    this$0.mTranslateableViews.add(ExpandableNotificationRow.this.mChildrenContainer);
                }
            }
        });
        if (super.mShouldTranslateContents) {
            this.mTranslateableViews = new ArrayList<View>();
            for (int j = n; j < this.getChildCount(); ++j) {
                this.mTranslateableViews.add(this.getChildAt(j));
            }
            this.mTranslateableViews.remove(this.mChildrenContainerStub);
            this.mTranslateableViews.remove(this.mGutsStub);
        }
    }
    
    public void onFinishedExpansionChange() {
        this.mGroupExpansionChanging = false;
        this.updateBackgroundForGroupState();
    }
    
    void onGutsClosed() {
        this.updateContentAccessibilityImportanceForGuts(true);
    }
    
    void onGutsOpened() {
        this.resetTranslation();
        this.updateContentAccessibilityImportanceForGuts(false);
    }
    
    public void onInitializeAccessibilityNodeInfoInternal(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_LONG_CLICK);
        if (this.canViewBeDismissed()) {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_DISMISS);
        }
        final boolean shouldShowPublic = this.shouldShowPublic();
        final boolean b = false;
        int contentExpandable = shouldShowPublic ? 1 : 0;
        int n = b ? 1 : 0;
        Label_0099: {
            if (!shouldShowPublic) {
                if (this.mIsSummaryWithChildren) {
                    final boolean b2 = true;
                    if (this.mIsLowPriority) {
                        contentExpandable = (b2 ? 1 : 0);
                        n = (b ? 1 : 0);
                        if (!this.isExpanded()) {
                            break Label_0099;
                        }
                    }
                    n = (this.isGroupExpanded() ? 1 : 0);
                    contentExpandable = (b2 ? 1 : 0);
                }
                else {
                    contentExpandable = (this.mPrivateLayout.isContentExpandable() ? 1 : 0);
                    n = (this.isExpanded() ? 1 : 0);
                }
            }
        }
        if (contentExpandable != 0) {
            if (n != 0) {
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_COLLAPSE);
            }
            else {
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_EXPAND);
            }
        }
        final NotificationMenuRowPlugin provider = this.getProvider();
        if (provider != null && provider.getSnoozeMenuItem(this.getContext()) != null) {
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_snooze, (CharSequence)this.getContext().getResources().getString(R$string.notification_menu_snooze_action)));
        }
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (KeyEvent.isConfirmKey(n)) {
            keyEvent.startTracking();
            return true;
        }
        return super.onKeyDown(n, keyEvent);
    }
    
    public boolean onKeyLongPress(final int n, final KeyEvent keyEvent) {
        if (KeyEvent.isConfirmKey(n)) {
            this.doLongClickCallback();
            return true;
        }
        return false;
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        if (KeyEvent.isConfirmKey(n)) {
            if (!keyEvent.isCanceled()) {
                this.performClick();
            }
            return true;
        }
        return super.onKeyUp(n, keyEvent);
    }
    
    @Override
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        final int intrinsicHeight = this.getIntrinsicHeight();
        super.onLayout(b, n, n2, n3, n4);
        if (intrinsicHeight != this.getIntrinsicHeight() && intrinsicHeight != 0) {
            this.notifyHeightChanged(true);
        }
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        if (mMenuRow != null && mMenuRow.getMenuView() != null) {
            this.mMenuRow.onParentHeightUpdate();
        }
        this.updateContentShiftHeight();
        final LayoutListener mLayoutListener = this.mLayoutListener;
        if (mLayoutListener != null) {
            mLayoutListener.onLayout();
        }
    }
    
    public void onNotificationRankingUpdated() {
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        if (mMenuRow != null) {
            mMenuRow.onNotificationUpdated(this.mEntry.getSbn());
        }
    }
    
    public void onNotificationUpdated() {
        final NotificationContentView[] mLayouts = this.mLayouts;
        for (int length = mLayouts.length, i = 0; i < length; ++i) {
            mLayouts[i].onNotificationUpdated(this.mEntry);
        }
        this.mIsColorized = this.mEntry.getSbn().getNotification().isColorized();
        this.mShowingPublicInitialized = false;
        this.updateNotificationColor();
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        if (mMenuRow != null) {
            mMenuRow.onNotificationUpdated(this.mEntry.getSbn());
            this.mMenuRow.setAppName(this.mAppName);
        }
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.recreateNotificationHeader(this.mExpandClickListener);
            this.mChildrenContainer.onNotificationUpdated();
        }
        if (this.mIconAnimationRunning) {
            this.setIconAnimationRunning(true);
        }
        if (this.mLastChronometerRunning) {
            this.setChronometerRunning(true);
        }
        final ExpandableNotificationRow mNotificationParent = this.mNotificationParent;
        if (mNotificationParent != null) {
            mNotificationParent.updateChildrenHeaderAppearance();
        }
        this.onChildrenCountChanged();
        this.mPublicLayout.updateExpandButtons(true);
        this.updateLimits();
        this.updateIconVisibilities();
        this.updateShelfIconColor();
        this.updateRippleAllowed();
        if (this.mUpdateBackgroundOnUpdate) {
            this.mUpdateBackgroundOnUpdate = false;
            this.updateBackgroundColors();
        }
    }
    
    @Override
    public void onPluginConnected(final NotificationMenuRowPlugin mMenuRow, final Context context) {
        final NotificationMenuRowPlugin mMenuRow2 = this.mMenuRow;
        final boolean b = mMenuRow2 != null && mMenuRow2.getMenuView() != null;
        if (b) {
            this.removeView(this.mMenuRow.getMenuView());
        }
        if (mMenuRow == null) {
            return;
        }
        this.mMenuRow = mMenuRow;
        if (mMenuRow.shouldUseDefaultMenuItems()) {
            final ArrayList<NotificationMenuRowPlugin.MenuItem> menuItems = new ArrayList<NotificationMenuRowPlugin.MenuItem>();
            menuItems.add(NotificationMenuRow.createConversationItem(super.mContext));
            menuItems.add(NotificationMenuRow.createInfoItem(super.mContext));
            menuItems.add(NotificationMenuRow.createSnoozeItem(super.mContext));
            menuItems.add(NotificationMenuRow.createAppOpsItem(super.mContext));
            this.mMenuRow.setMenuItems(menuItems);
        }
        if (b) {
            this.createMenu();
        }
    }
    
    @Override
    public void onPluginDisconnected(final NotificationMenuRowPlugin notificationMenuRowPlugin) {
        final boolean b = this.mMenuRow.getMenuView() != null;
        this.mMenuRow = new NotificationMenuRow(super.mContext, this.mPeopleNotificationIdentifier);
        if (b) {
            this.createMenu();
        }
    }
    
    public boolean onRequestSendAccessibilityEventInternal(final View view, final AccessibilityEvent accessibilityEvent) {
        if (super.onRequestSendAccessibilityEventInternal(view, accessibilityEvent)) {
            final AccessibilityEvent obtain = AccessibilityEvent.obtain();
            this.onInitializeAccessibilityEvent(obtain);
            this.dispatchPopulateAccessibilityEvent(obtain);
            accessibilityEvent.appendRecord((AccessibilityRecord)obtain);
            return true;
        }
        return false;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return (motionEvent.getActionMasked() != 0 || !this.isChildInGroup() || this.isGroupExpanded()) && super.onTouchEvent(motionEvent);
    }
    
    public void onUiModeChanged() {
        this.mUpdateBackgroundOnUpdate = true;
        this.reInflateViews();
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null) {
            final Iterator<ExpandableNotificationRow> iterator = mChildrenContainer.getNotificationChildren().iterator();
            while (iterator.hasNext()) {
                iterator.next().onUiModeChanged();
            }
        }
    }
    
    public boolean performAccessibilityActionInternal(final int n, final Bundle bundle) {
        if (super.performAccessibilityActionInternal(n, bundle)) {
            return true;
        }
        if (n == 32) {
            this.doLongClickCallback();
            return true;
        }
        if (n == 262144 || n == 524288) {
            this.mExpandClickListener.onClick((View)this);
            return true;
        }
        if (n == 1048576) {
            this.performDismissWithBlockingHelper(true);
            return true;
        }
        if (n == R$id.action_snooze && this.getProvider() == null && this.mMenuRow != null) {
            final NotificationMenuRowPlugin.MenuItem snoozeMenuItem = this.createMenu().getSnoozeMenuItem(this.getContext());
            if (snoozeMenuItem != null) {
                this.doLongClickCallback(this.getWidth() / 2, this.getHeight() / 2, snoozeMenuItem);
            }
            return true;
        }
        return false;
    }
    
    public void performDismiss(final boolean b) {
        if (this.isOnlyChildInGroup()) {
            final NotificationEntry logicalGroupSummary = this.mGroupManager.getLogicalGroupSummary(this.mEntry.getSbn());
            if (logicalGroupSummary.isClearable()) {
                logicalGroupSummary.getRow().performDismiss(b);
            }
        }
        this.dismiss(b);
        if (this.mEntry.isClearable()) {
            final Runnable mOnDismissRunnable = this.mOnDismissRunnable;
            if (mOnDismissRunnable != null) {
                mOnDismissRunnable.run();
            }
        }
    }
    
    public boolean performDismissWithBlockingHelper(final boolean b) {
        final boolean perhapsShowBlockingHelper = Dependency.get(NotificationBlockingHelperManager.class).perhapsShowBlockingHelper(this, this.mMenuRow);
        Dependency.get(MetricsLogger.class).count("notification_dismissed", 1);
        this.performDismiss(b);
        return perhapsShowBlockingHelper;
    }
    
    public void performOnIntrinsicHeightReached(final Runnable mOnIntrinsicHeightReachedRunnable) {
        this.mOnIntrinsicHeightReachedRunnable = mOnIntrinsicHeightReachedRunnable;
        this.handleIntrinsicHeightReached();
    }
    
    @Override
    public long performRemoveAnimation(final long n, final long n2, final float n3, final boolean b, final float n4, final Runnable runnable, final AnimatorListenerAdapter animatorListenerAdapter) {
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        if (mMenuRow != null && mMenuRow.isMenuVisible()) {
            final Animator translateViewAnimator = this.getTranslateViewAnimator(0.0f, null);
            if (translateViewAnimator != null) {
                translateViewAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationEnd(final Animator animator) {
                        ActivatableNotificationView.this.performRemoveAnimation(n, n2, n3, b, n4, runnable, animatorListenerAdapter);
                    }
                });
                translateViewAnimator.start();
                return translateViewAnimator.getDuration();
            }
        }
        return super.performRemoveAnimation(n, n2, n3, b, n4, runnable, animatorListenerAdapter);
    }
    
    public void prepareExpansionChanged() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.prepareExpansionChanged();
        }
    }
    
    @Override
    public void removeAllChildren() {
        final ArrayList<ExpandableNotificationRow> list = new ArrayList<ExpandableNotificationRow>(this.mChildrenContainer.getNotificationChildren());
        for (int i = 0; i < list.size(); ++i) {
            final ExpandableNotificationRow expandableNotificationRow = list.get(i);
            if (!expandableNotificationRow.keepInParent()) {
                this.mChildrenContainer.removeNotification(expandableNotificationRow);
                expandableNotificationRow.setIsChildInGroup(false, null);
            }
        }
        this.onChildrenCountChanged();
    }
    
    public void removeChildNotification(final ExpandableNotificationRow expandableNotificationRow) {
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null) {
            mChildrenContainer.removeNotification(expandableNotificationRow);
        }
        this.onChildrenCountChanged();
        expandableNotificationRow.setIsChildInGroup(false, null);
        expandableNotificationRow.setBottomRoundness(0.0f, false);
    }
    
    @Override
    public void removeChildNotification(final NotificationListItem notificationListItem) {
        this.removeChildNotification((ExpandableNotificationRow)notificationListItem.getView());
    }
    
    public void removeListener() {
        this.mLayoutListener = null;
    }
    
    public void reset() {
        this.mShowingPublicInitialized = false;
        this.unDismiss();
        this.resetTranslation();
        this.onHeightReset();
        this.requestLayout();
    }
    
    @Override
    public void resetTranslation() {
        final Animator mTranslateAnim = this.mTranslateAnim;
        if (mTranslateAnim != null) {
            mTranslateAnim.cancel();
        }
        if (!super.mShouldTranslateContents) {
            this.setTranslationX(0.0f);
        }
        else if (this.mTranslateableViews != null) {
            for (int i = 0; i < this.mTranslateableViews.size(); ++i) {
                this.mTranslateableViews.get(i).setTranslationX(0.0f);
            }
            this.invalidateOutline();
            this.getEntry().getIcons().getShelfIcon().setScrollX(0);
        }
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        if (mMenuRow != null) {
            mMenuRow.resetMenu();
        }
    }
    
    public void resetUserExpansion() {
        final boolean expanded = this.isExpanded();
        this.mHasUserChangedExpansion = false;
        this.mUserExpanded = false;
        if (expanded != this.isExpanded()) {
            if (this.mIsSummaryWithChildren) {
                this.mChildrenContainer.onExpansionChanged();
            }
            this.notifyHeightChanged(false);
        }
        this.updateShelfIconColor();
    }
    
    public void setAboveShelf(final boolean mAboveShelf) {
        final boolean aboveShelf = this.isAboveShelf();
        this.mAboveShelf = mAboveShelf;
        if (this.isAboveShelf() != aboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(aboveShelf ^ true);
        }
    }
    
    public void setAboveShelfChangedListener(final AboveShelfChangedListener mAboveShelfChangedListener) {
        this.mAboveShelfChangedListener = mAboveShelfChangedListener;
    }
    
    @Override
    public void setActualHeight(final int n, final boolean b) {
        final int actualHeight = this.getActualHeight();
        final int n2 = 0;
        final boolean b2 = n != actualHeight;
        super.setActualHeight(n, b);
        if (b2 && this.isRemoved()) {
            final ViewGroup viewGroup = (ViewGroup)this.getParent();
            if (viewGroup != null) {
                viewGroup.invalidate();
            }
        }
        final NotificationGuts mGuts = this.mGuts;
        if (mGuts != null && mGuts.isExposed()) {
            this.mGuts.setActualHeight(n);
            return;
        }
        final int max = Math.max(this.getMinHeight(), n);
        final NotificationContentView[] mLayouts = this.mLayouts;
        for (int length = mLayouts.length, i = n2; i < length; ++i) {
            mLayouts[i].setContentHeight(max);
        }
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.setActualHeight(n);
        }
        final NotificationGuts mGuts2 = this.mGuts;
        if (mGuts2 != null) {
            mGuts2.setActualHeight(n);
        }
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        if (mMenuRow != null && mMenuRow.getMenuView() != null) {
            this.mMenuRow.onParentHeightUpdate();
        }
        this.handleIntrinsicHeightReached();
    }
    
    @Override
    public void setActualHeightAnimating(final boolean contentHeightAnimating) {
        final NotificationContentView mPrivateLayout = this.mPrivateLayout;
        if (mPrivateLayout != null) {
            mPrivateLayout.setContentHeightAnimating(contentHeightAnimating);
        }
    }
    
    void setAppOpsOnClickListener(final OnAppOpsClickListener onAppOpsClickListener) {
        this.mOnAppOpsClickListener = (View$OnClickListener)new _$$Lambda$ExpandableNotificationRow$7jXuB4sFjLsVw6Zajpc39FzigT4(this, onAppOpsClickListener);
    }
    
    @Override
    protected void setBackgroundTintColor(final int n) {
        super.setBackgroundTintColor(n);
        final NotificationContentView showingLayout = this.getShowingLayout();
        if (showingLayout != null) {
            showingLayout.setBackgroundTintColor(n);
        }
    }
    
    public void setBlockingHelperShowing(final boolean mIsBlockingHelperShowing) {
        this.mIsBlockingHelperShowing = mIsBlockingHelperShowing;
    }
    
    @VisibleForTesting
    protected void setChildrenContainer(final NotificationChildrenContainer mChildrenContainer) {
        this.mChildrenContainer = mChildrenContainer;
    }
    
    public void setChildrenExpanded(final boolean b, final boolean b2) {
        this.mChildrenExpanded = b;
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null) {
            mChildrenContainer.setChildrenExpanded(b);
        }
        this.updateBackgroundForGroupState();
        this.updateClickAndFocus();
    }
    
    public void setChronometerRunning(final boolean b) {
        this.setChronometerRunning(this.mLastChronometerRunning = b, this.mPrivateLayout);
        this.setChronometerRunning(b, this.mPublicLayout);
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null) {
            final List<ExpandableNotificationRow> notificationChildren = mChildrenContainer.getNotificationChildren();
            for (int i = 0; i < notificationChildren.size(); ++i) {
                notificationChildren.get(i).setChronometerRunning(b);
            }
        }
    }
    
    @Override
    public void setClipBottomAmount(final int n) {
        if (this.mExpandAnimationRunning) {
            return;
        }
        if (n != super.mClipBottomAmount) {
            super.setClipBottomAmount(n);
            final NotificationContentView[] mLayouts = this.mLayouts;
            for (int length = mLayouts.length, i = 0; i < length; ++i) {
                mLayouts[i].setClipBottomAmount(n);
            }
            final NotificationGuts mGuts = this.mGuts;
            if (mGuts != null) {
                mGuts.setClipBottomAmount(n);
            }
        }
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null && !this.mChildIsExpanding) {
            mChildrenContainer.setClipBottomAmount(n);
        }
    }
    
    @Override
    public void setClipToActualHeight(final boolean b) {
        final boolean b2 = false;
        super.setClipToActualHeight(b || this.isUserLocked());
        final NotificationContentView showingLayout = this.getShowingLayout();
        boolean clipToActualHeight = false;
        Label_0049: {
            if (!b) {
                clipToActualHeight = b2;
                if (!this.isUserLocked()) {
                    break Label_0049;
                }
            }
            clipToActualHeight = true;
        }
        showingLayout.setClipToActualHeight(clipToActualHeight);
    }
    
    @Override
    public void setClipTopAmount(final int clipTopAmount) {
        super.setClipTopAmount(clipTopAmount);
        final NotificationContentView[] mLayouts = this.mLayouts;
        for (int length = mLayouts.length, i = 0; i < length; ++i) {
            mLayouts[i].setClipTopAmount(clipTopAmount);
        }
        final NotificationGuts mGuts = this.mGuts;
        if (mGuts != null) {
            mGuts.setClipTopAmount(clipTopAmount);
        }
    }
    
    public void setContentBackground(final int n, final boolean b, final NotificationContentView notificationContentView) {
        if (this.getShowingLayout() == notificationContentView) {
            this.setTintColor(n, b);
        }
    }
    
    public void setDismissRtl(final boolean dismissRtl) {
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        if (mMenuRow != null) {
            mMenuRow.setDismissRtl(dismissRtl);
        }
    }
    
    public void setEntry(final NotificationEntry mEntry) {
        this.mEntry = mEntry;
        this.cacheIsSystemNotification();
    }
    
    public void setExpandAnimationRunning(final boolean expandAnimationRunning) {
        Object o;
        if (this.mIsSummaryWithChildren) {
            o = this.mChildrenContainer;
        }
        else {
            o = this.getShowingLayout();
        }
        final NotificationGuts mGuts = this.mGuts;
        Object mGuts2 = o;
        if (mGuts != null) {
            mGuts2 = o;
            if (mGuts.isExposed()) {
                mGuts2 = this.mGuts;
            }
        }
        if (expandAnimationRunning) {
            ((View)mGuts2).animate().alpha(0.0f).setDuration(67L).setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT);
            this.setAboveShelf(true);
            this.mExpandAnimationRunning = true;
            this.getViewState().cancelAnimations((View)this);
            this.mNotificationLaunchHeight = AmbientState.getNotificationLaunchHeight(this.getContext());
        }
        else {
            this.mExpandAnimationRunning = false;
            this.setAboveShelf(this.isAboveShelf());
            final NotificationGuts mGuts3 = this.mGuts;
            if (mGuts3 != null) {
                mGuts3.setAlpha(1.0f);
            }
            if (mGuts2 != null) {
                ((View)mGuts2).setAlpha(1.0f);
            }
            this.setExtraWidthForClipping(0.0f);
            final ExpandableNotificationRow mNotificationParent = this.mNotificationParent;
            if (mNotificationParent != null) {
                mNotificationParent.setExtraWidthForClipping(0.0f);
                this.mNotificationParent.setMinimumHeightForClipping(0);
            }
        }
        final ExpandableNotificationRow mNotificationParent2 = this.mNotificationParent;
        if (mNotificationParent2 != null) {
            mNotificationParent2.setChildIsExpanding(this.mExpandAnimationRunning);
        }
        this.updateChildrenVisibility();
        this.updateClipping();
        super.mBackgroundNormal.setExpandAnimationRunning(expandAnimationRunning);
    }
    
    public void setExpandable(final boolean mExpandable) {
        this.mExpandable = mExpandable;
        this.mPrivateLayout.updateExpandButtons(this.isExpandable());
    }
    
    public void setGroupExpansionChanging(final boolean mGroupExpansionChanging) {
        this.mGroupExpansionChanging = mGroupExpansionChanging;
    }
    
    public void setGutsView(final NotificationMenuRowPlugin.MenuItem menuItem) {
        if (this.mGuts != null && menuItem.getGutsView() instanceof NotificationGuts.GutsContent) {
            ((NotificationGuts.GutsContent)menuItem.getGutsView()).setGutsParent(this.mGuts);
            this.mGuts.setGutsContent((NotificationGuts.GutsContent)menuItem.getGutsView());
        }
    }
    
    public void setHeaderVisibleAmount(final float headerVisibleAmount) {
        if (this.mHeaderVisibleAmount != headerVisibleAmount) {
            this.mHeaderVisibleAmount = headerVisibleAmount;
            final NotificationContentView[] mLayouts = this.mLayouts;
            for (int length = mLayouts.length, i = 0; i < length; ++i) {
                mLayouts[i].setHeaderVisibleAmount(headerVisibleAmount);
            }
            final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
            if (mChildrenContainer != null) {
                mChildrenContainer.setHeaderVisibleAmount(headerVisibleAmount);
            }
            this.notifyHeightChanged(false);
        }
    }
    
    public void setHeadsUp(final boolean b) {
        final boolean aboveShelf = this.isAboveShelf();
        final int intrinsicHeight = this.getIntrinsicHeight();
        this.mIsHeadsUp = b;
        this.mPrivateLayout.setHeadsUp(b);
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.updateGroupOverflow();
        }
        if (intrinsicHeight != this.getIntrinsicHeight()) {
            this.notifyHeightChanged(false);
        }
        if (b) {
            this.setAboveShelf(this.mMustStayOnScreen = true);
        }
        else if (this.isAboveShelf() != aboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(aboveShelf ^ true);
        }
    }
    
    public void setHeadsUpAnimatingAway(final boolean b) {
        final boolean aboveShelf = this.isAboveShelf();
        final boolean b2 = b != this.mHeadsupDisappearRunning;
        this.mHeadsupDisappearRunning = b;
        this.mPrivateLayout.setHeadsUpAnimatingAway(b);
        if (b2) {
            final Consumer<Boolean> mHeadsUpAnimatingAwayListener = this.mHeadsUpAnimatingAwayListener;
            if (mHeadsUpAnimatingAwayListener != null) {
                mHeadsUpAnimatingAwayListener.accept(b);
            }
        }
        if (this.isAboveShelf() != aboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(aboveShelf ^ true);
        }
    }
    
    public void setHeadsUpAnimatingAwayListener(final Consumer<Boolean> mHeadsUpAnimatingAwayListener) {
        this.mHeadsUpAnimatingAwayListener = mHeadsUpAnimatingAwayListener;
    }
    
    @Override
    public void setHeadsUpIsVisible() {
        super.setHeadsUpIsVisible();
        this.mMustStayOnScreen = false;
    }
    
    @Override
    public void setHideSensitive(final boolean b, final boolean b2, final long n, final long n2) {
        if (this.getVisibility() == 8) {
            return;
        }
        final boolean mShowingPublic = this.mShowingPublic;
        final boolean mSensitive = this.mSensitive;
        int visibility = 0;
        final boolean mShowingPublic2 = mSensitive && b;
        this.mShowingPublic = mShowingPublic2;
        if (this.mShowingPublicInitialized && mShowingPublic2 == mShowingPublic) {
            return;
        }
        if (this.mPublicLayout.getChildCount() == 0) {
            return;
        }
        if (!b2) {
            this.mPublicLayout.animate().cancel();
            this.mPrivateLayout.animate().cancel();
            final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
            if (mChildrenContainer != null) {
                mChildrenContainer.animate().cancel();
                this.mChildrenContainer.setAlpha(1.0f);
            }
            this.mPublicLayout.setAlpha(1.0f);
            this.mPrivateLayout.setAlpha(1.0f);
            final NotificationContentView mPublicLayout = this.mPublicLayout;
            if (!this.mShowingPublic) {
                visibility = 4;
            }
            mPublicLayout.setVisibility(visibility);
            this.updateChildrenVisibility();
        }
        else {
            this.animateShowingPublic(n, n2, this.mShowingPublic);
        }
        this.getShowingLayout().updateBackgroundColor(b2);
        this.mPrivateLayout.updateExpandButtons(this.isExpandable());
        this.updateShelfIconColor();
        this.mShowingPublicInitialized = true;
    }
    
    @Override
    public void setHideSensitiveForIntrinsicHeight(final boolean b) {
        this.mHideSensitiveForIntrinsicHeight = b;
        if (this.mIsSummaryWithChildren) {
            final List<ExpandableNotificationRow> notificationChildren = this.mChildrenContainer.getNotificationChildren();
            for (int i = 0; i < notificationChildren.size(); ++i) {
                notificationChildren.get(i).setHideSensitiveForIntrinsicHeight(b);
            }
        }
    }
    
    public void setIconAnimationRunning(final boolean b) {
        final NotificationContentView[] mLayouts = this.mLayouts;
        final int length = mLayouts.length;
        final int n = 0;
        for (int i = 0; i < length; ++i) {
            this.setIconAnimationRunning(b, mLayouts[i]);
        }
        if (this.mIsSummaryWithChildren) {
            this.setIconAnimationRunningForChild(b, (View)this.mChildrenContainer.getHeaderView());
            this.setIconAnimationRunningForChild(b, (View)this.mChildrenContainer.getLowPriorityHeaderView());
            final List<ExpandableNotificationRow> notificationChildren = this.mChildrenContainer.getNotificationChildren();
            for (int j = n; j < notificationChildren.size(); ++j) {
                notificationChildren.get(j).setIconAnimationRunning(b);
            }
        }
        this.mIconAnimationRunning = b;
    }
    
    public void setIsChildInGroup(final boolean b, ExpandableNotificationRow mNotificationParent) {
        if (this.mExpandAnimationRunning && !b) {
            final ExpandableNotificationRow mNotificationParent2 = this.mNotificationParent;
            if (mNotificationParent2 != null) {
                mNotificationParent2.setChildIsExpanding(false);
                this.mNotificationParent.setExtraWidthForClipping(0.0f);
                this.mNotificationParent.setMinimumHeightForClipping(0);
            }
        }
        if (!b) {
            mNotificationParent = null;
        }
        this.mNotificationParent = mNotificationParent;
        this.mPrivateLayout.setIsChildInGroup(b);
        if (this.mIsChildInGroup != b) {
            this.mIsChildInGroup = b;
            if (!this.isRemoved() && this.mIsLowPriority) {
                this.mRowContentBindStage.getStageParams(this.mEntry).setUseLowPriority(this.mIsLowPriority);
                this.mRowContentBindStage.requestRebind(this.mEntry, null);
            }
        }
        this.resetBackgroundAlpha();
        this.updateBackgroundForGroupState();
        this.updateClickAndFocus();
        if (this.mNotificationParent != null) {
            this.setOverrideTintColor(0, 0.0f);
            this.setDistanceToTopRoundness(-1.0f);
            this.mNotificationParent.updateBackgroundForGroupState();
        }
        this.updateIconVisibilities();
        this.updateBackgroundClipping();
    }
    
    public void setIsLowPriority(final boolean isLowPriority) {
        this.mIsLowPriority = isLowPriority;
        this.mPrivateLayout.setIsLowPriority(isLowPriority);
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null) {
            mChildrenContainer.setIsLowPriority(isLowPriority);
        }
    }
    
    public void setJustClicked(final boolean mJustClicked) {
        this.mJustClicked = mJustClicked;
    }
    
    public void setKeepInParent(final boolean mKeepInParent) {
        this.mKeepInParent = mKeepInParent;
    }
    
    public void setLastAudiblyAlertedMs(long n) {
        if (NotificationUtils.useNewInterruptionModel(super.mContext)) {
            n = System.currentTimeMillis() - n;
            final boolean b = n < ExpandableNotificationRow.RECENTLY_ALERTED_THRESHOLD_MS;
            this.applyAudiblyAlertedRecently(b);
            this.removeCallbacks(this.mExpireRecentlyAlertedFlag);
            if (b) {
                this.postDelayed(this.mExpireRecentlyAlertedFlag, ExpandableNotificationRow.RECENTLY_ALERTED_THRESHOLD_MS - n);
            }
        }
    }
    
    public void setLayoutListener(final LayoutListener mLayoutListener) {
        this.mLayoutListener = mLayoutListener;
    }
    
    public void setLegacy(final boolean legacy) {
        final NotificationContentView[] mLayouts = this.mLayouts;
        for (int length = mLayouts.length, i = 0; i < length; ++i) {
            mLayouts[i].setLegacy(legacy);
        }
    }
    
    public void setLongPressListener(final LongPressListener mLongPressListener) {
        this.mLongPressListener = mLongPressListener;
    }
    
    public void setNeedsRedaction(final boolean mNeedsRedaction) {
        if (this.mNeedsRedaction != mNeedsRedaction) {
            this.mNeedsRedaction = mNeedsRedaction;
            if (!this.isRemoved()) {
                final RowContentBindParams rowContentBindParams = this.mRowContentBindStage.getStageParams(this.mEntry);
                if (mNeedsRedaction) {
                    rowContentBindParams.requireContentViews(8);
                }
                else {
                    rowContentBindParams.markContentViewsFreeable(8);
                }
                this.mRowContentBindStage.requestRebind(this.mEntry, null);
            }
        }
    }
    
    public void setOnClickListener(final View$OnClickListener view$OnClickListener) {
        super.setOnClickListener(view$OnClickListener);
        this.mOnClickListener = view$OnClickListener;
        this.updateClickAndFocus();
    }
    
    void setOnDismissRunnable(final Runnable mOnDismissRunnable) {
        this.mOnDismissRunnable = mOnDismissRunnable;
    }
    
    public void setOnExpansionChangedListener(final OnExpansionChangedListener mExpansionChangedListener) {
        this.mExpansionChangedListener = mExpansionChangedListener;
    }
    
    public void setOnKeyguard(final boolean mOnKeyguard) {
        if (mOnKeyguard != this.mOnKeyguard) {
            final boolean aboveShelf = this.isAboveShelf();
            final boolean expanded = this.isExpanded();
            this.mOnKeyguard = mOnKeyguard;
            this.onExpansionChanged(false, expanded);
            if (expanded != this.isExpanded()) {
                if (this.mIsSummaryWithChildren) {
                    this.mChildrenContainer.updateGroupOverflow();
                }
                this.notifyHeightChanged(false);
            }
            if (this.isAboveShelf() != aboveShelf) {
                this.mAboveShelfChangedListener.onAboveShelfStateChanged(aboveShelf ^ true);
            }
        }
        this.updateRippleAllowed();
    }
    
    public void setPinned(final boolean mIsPinned) {
        final int intrinsicHeight = this.getIntrinsicHeight();
        final boolean aboveShelf = this.isAboveShelf();
        this.mIsPinned = mIsPinned;
        if (intrinsicHeight != this.getIntrinsicHeight()) {
            this.notifyHeightChanged(false);
        }
        if (mIsPinned) {
            this.setIconAnimationRunning(true);
            this.mExpandedWhenPinned = false;
        }
        else if (this.mExpandedWhenPinned) {
            this.setUserExpanded(true);
        }
        this.setChronometerRunning(this.mLastChronometerRunning);
        if (this.isAboveShelf() != aboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(aboveShelf ^ true);
        }
    }
    
    @VisibleForTesting
    protected void setPrivateLayout(final NotificationContentView mPrivateLayout) {
        this.mPrivateLayout = mPrivateLayout;
    }
    
    @VisibleForTesting
    protected void setPublicLayout(final NotificationContentView mPublicLayout) {
        this.mPublicLayout = mPublicLayout;
    }
    
    public void setRemoteInputController(final RemoteInputController remoteInputController) {
        this.mPrivateLayout.setRemoteInputController(remoteInputController);
    }
    
    public void setRemoved() {
        this.mRemoved = true;
        this.mTranslationWhenRemoved = this.getTranslationY();
        this.mWasChildInGroupWhenRemoved = this.isChildInGroup();
        if (this.isChildInGroup()) {
            this.mTranslationWhenRemoved += this.getNotificationParent().getTranslationY();
        }
        final NotificationContentView[] mLayouts = this.mLayouts;
        for (int length = mLayouts.length, i = 0; i < length; ++i) {
            mLayouts[i].setRemoved();
        }
    }
    
    public void setSecureStateProvider(final BooleanSupplier mSecureStateProvider) {
        this.mSecureStateProvider = mSecureStateProvider;
    }
    
    public void setSensitive(final boolean mSensitive, final boolean mSensitiveHiddenInGeneral) {
        this.mSensitive = mSensitive;
        this.mSensitiveHiddenInGeneral = mSensitiveHiddenInGeneral;
    }
    
    public void setShelfIconVisible(final boolean mShelfIconVisible) {
        if (mShelfIconVisible != this.mShelfIconVisible) {
            this.mShelfIconVisible = mShelfIconVisible;
            this.updateIconVisibilities();
        }
    }
    
    public void setSingleLineWidthIndention(final int singleLineWidthIndention) {
        this.mPrivateLayout.setSingleLineWidthIndention(singleLineWidthIndention);
    }
    
    public void setSystemChildExpanded(final boolean mIsSystemChildExpanded) {
        this.mIsSystemChildExpanded = mIsSystemChildExpanded;
    }
    
    public void setSystemExpanded(final boolean mIsSystemExpanded) {
        if (mIsSystemExpanded != this.mIsSystemExpanded) {
            final boolean expanded = this.isExpanded();
            this.mIsSystemExpanded = mIsSystemExpanded;
            this.notifyHeightChanged(false);
            this.onExpansionChanged(false, expanded);
            if (this.mIsSummaryWithChildren) {
                this.mChildrenContainer.updateGroupOverflow();
            }
        }
    }
    
    @Override
    public void setTranslation(final float translationX) {
        if (this.isBlockingHelperShowingAndTranslationFinished()) {
            this.mGuts.setTranslationX(translationX);
            return;
        }
        if (!super.mShouldTranslateContents) {
            this.setTranslationX(translationX);
        }
        else if (this.mTranslateableViews != null) {
            for (int i = 0; i < this.mTranslateableViews.size(); ++i) {
                if (this.mTranslateableViews.get(i) != null) {
                    this.mTranslateableViews.get(i).setTranslationX(translationX);
                }
            }
            this.invalidateOutline();
            this.getEntry().getIcons().getShelfIcon().setScrollX((int)(-translationX));
        }
        final NotificationMenuRowPlugin mMenuRow = this.mMenuRow;
        if (mMenuRow != null && mMenuRow.getMenuView() != null) {
            this.mMenuRow.onParentTranslationUpdate(translationX);
        }
    }
    
    public void setUserExpanded(final boolean b) {
        this.setUserExpanded(b, false);
    }
    
    public void setUserExpanded(final boolean mUserExpanded, final boolean b) {
        this.mFalsingManager.setNotificationExpanded();
        if (this.mIsSummaryWithChildren && !this.shouldShowPublic() && b && !this.mChildrenContainer.showingAsLowPriority()) {
            final boolean groupExpanded = this.mGroupManager.isGroupExpanded(this.mEntry.getSbn());
            this.mGroupManager.setGroupExpanded(this.mEntry.getSbn(), mUserExpanded);
            this.onExpansionChanged(true, groupExpanded);
            return;
        }
        if (mUserExpanded && !this.mExpandable) {
            return;
        }
        final boolean expanded = this.isExpanded();
        this.mHasUserChangedExpansion = true;
        this.mUserExpanded = mUserExpanded;
        this.onExpansionChanged(true, expanded);
        if (!expanded && this.isExpanded() && this.getActualHeight() != this.getIntrinsicHeight()) {
            this.notifyHeightChanged(true);
        }
    }
    
    public void setUserLocked(final boolean userLocked) {
        this.mUserLocked = userLocked;
        this.mPrivateLayout.setUserExpanding(userLocked);
        final NotificationChildrenContainer mChildrenContainer = this.mChildrenContainer;
        if (mChildrenContainer != null) {
            mChildrenContainer.setUserLocked(userLocked);
            if (this.mIsSummaryWithChildren && (userLocked || !this.isGroupExpanded())) {
                this.updateBackgroundForGroupState();
            }
        }
    }
    
    public void setUsesIncreasedCollapsedHeight(final boolean mUseIncreasedCollapsedHeight) {
        this.mUseIncreasedCollapsedHeight = mUseIncreasedCollapsedHeight;
    }
    
    public void setUsesIncreasedHeadsUpHeight(final boolean mUseIncreasedHeadsUpHeight) {
        this.mUseIncreasedHeadsUpHeight = mUseIncreasedHeadsUpHeight;
    }
    
    @Override
    protected boolean shouldClipToActualHeight() {
        return super.shouldClipToActualHeight() && !this.mExpandAnimationRunning;
    }
    
    @Override
    protected boolean shouldHideBackground() {
        return super.shouldHideBackground() || this.mShowNoBackground;
    }
    
    public void showAppOpsIcons(final ArraySet<Integer> set) {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.showAppOpsIcons(set);
        }
        this.mPrivateLayout.showAppOpsIcons(set);
        this.mPublicLayout.showAppOpsIcons(set);
    }
    
    @Override
    public boolean showingPulsing() {
        return this.isHeadsUpState() && (this.isDozing() || (this.mOnKeyguard && this.isBypassEnabled()));
    }
    
    public void startChildAnimation(final AnimationProperties animationProperties) {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.startAnimationToState(animationProperties);
        }
    }
    
    @Override
    public boolean topAmountNeedsClipping() {
        if (this.isGroupExpanded()) {
            return true;
        }
        if (this.isGroupExpansionChanging()) {
            return true;
        }
        if (this.getShowingLayout().shouldClipToRounding(true, false)) {
            return true;
        }
        final NotificationGuts mGuts = this.mGuts;
        return mGuts != null && mGuts.getAlpha() != 0.0f;
    }
    
    public void updateBackgroundForGroupState() {
        final boolean mIsSummaryWithChildren = this.mIsSummaryWithChildren;
        boolean mShowNoBackground = true;
        final int n = 0;
        int i = 0;
        if (mIsSummaryWithChildren) {
            if (this.mShowGroupBackgroundWhenExpanded || !this.isGroupExpanded() || this.isGroupExpansionChanging() || this.isUserLocked()) {
                mShowNoBackground = false;
            }
            this.mShowNoBackground = mShowNoBackground;
            this.mChildrenContainer.updateHeaderForExpansion(mShowNoBackground);
            for (List<ExpandableNotificationRow> notificationChildren = this.mChildrenContainer.getNotificationChildren(); i < notificationChildren.size(); ++i) {
                notificationChildren.get(i).updateBackgroundForGroupState();
            }
        }
        else if (this.isChildInGroup()) {
            final int backgroundColorForExpansionState = this.getShowingLayout().getBackgroundColorForExpansionState();
            int n2 = 0;
            Label_0161: {
                if (!this.isGroupExpanded()) {
                    if (!this.mNotificationParent.isGroupExpansionChanging()) {
                        n2 = n;
                        if (!this.mNotificationParent.isUserLocked()) {
                            break Label_0161;
                        }
                    }
                    n2 = n;
                    if (backgroundColorForExpansionState == 0) {
                        break Label_0161;
                    }
                }
                n2 = 1;
            }
            this.mShowNoBackground = ((n2 ^ 0x1) != 0x0);
        }
        else {
            this.mShowNoBackground = false;
        }
        this.updateOutline();
        this.updateBackground();
    }
    
    @Override
    protected void updateBackgroundTint() {
        super.updateBackgroundTint();
        this.updateBackgroundForGroupState();
        if (this.mIsSummaryWithChildren) {
            final List<ExpandableNotificationRow> notificationChildren = this.mChildrenContainer.getNotificationChildren();
            for (int i = 0; i < notificationChildren.size(); ++i) {
                notificationChildren.get(i).updateBackgroundForGroupState();
            }
        }
    }
    
    public void updateChildrenHeaderAppearance() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.updateChildrenHeaderAppearance();
        }
    }
    
    public void updateChildrenStates(final AmbientState ambientState) {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.updateState(this.getViewState(), ambientState);
        }
    }
    
    @Override
    protected void updateContentTransformation() {
        if (this.mExpandAnimationRunning) {
            return;
        }
        super.updateContentTransformation();
    }
    
    @VisibleForTesting
    void updateShelfIconColor() {
        final StatusBarIconView shelfIcon = this.mEntry.getIcons().getShelfIcon();
        final boolean equals = Boolean.TRUE.equals(shelfIcon.getTag(R$id.icon_is_pre_L));
        int originalIconColor = 0;
        if (!equals || NotificationUtils.isGrayscale(shelfIcon, ContrastColorUtil.getInstance(super.mContext))) {
            originalIconColor = this.getOriginalIconColor();
        }
        shelfIcon.setStaticDrawableColor(originalIconColor);
    }
    
    public boolean wasChildInGroupWhenRemoved() {
        return this.mWasChildInGroupWhenRemoved;
    }
    
    public boolean wasJustClicked() {
        return this.mJustClicked;
    }
    
    public interface ExpansionLogger
    {
        void logNotificationExpansion(final String p0, final boolean p1, final boolean p2);
    }
    
    public interface LayoutListener
    {
        void onLayout();
    }
    
    public interface LongPressListener
    {
        boolean onLongPress(final View p0, final int p1, final int p2, final NotificationMenuRowPlugin.MenuItem p3);
    }
    
    private static class NotificationViewState extends ExpandableViewState
    {
        private void handleFixedTranslationZ(final ExpandableNotificationRow expandableNotificationRow) {
            if (expandableNotificationRow.hasExpandingChild()) {
                super.zTranslation = expandableNotificationRow.getTranslationZ();
                super.clipTopAmount = expandableNotificationRow.getClipTopAmount();
            }
        }
        
        @Override
        public void animateTo(final View view, final AnimationProperties animationProperties) {
            if (view instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
                if (expandableNotificationRow.isExpandAnimationRunning()) {
                    return;
                }
                this.handleFixedTranslationZ(expandableNotificationRow);
                super.animateTo(view, animationProperties);
                expandableNotificationRow.startChildAnimation(animationProperties);
            }
        }
        
        @Override
        public void applyToView(final View view) {
            if (view instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
                if (expandableNotificationRow.isExpandAnimationRunning()) {
                    return;
                }
                this.handleFixedTranslationZ(expandableNotificationRow);
                super.applyToView(view);
                expandableNotificationRow.applyChildrenState();
            }
        }
        
        @Override
        protected void onYTranslationAnimationFinished(final View view) {
            super.onYTranslationAnimationFinished(view);
            if (view instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
                if (expandableNotificationRow.isHeadsUpAnimatingAway()) {
                    expandableNotificationRow.setHeadsUpAnimatingAway(false);
                }
            }
        }
    }
    
    public interface OnAppOpsClickListener
    {
        boolean onClick(final View p0, final int p1, final int p2, final NotificationMenuRowPlugin.MenuItem p3);
    }
    
    public interface OnExpandClickListener
    {
        void onExpandClicked(final NotificationEntry p0, final boolean p1);
    }
    
    public interface OnExpansionChangedListener
    {
        void onExpansionChanged(final boolean p0);
    }
    
    private class SystemNotificationAsyncTask extends AsyncTask<Void, Void, Boolean>
    {
        protected Boolean doInBackground(final Void... array) {
            return isSystemNotification(ExpandableNotificationRow.this.mContext, ExpandableNotificationRow.this.mEntry.getSbn());
        }
        
        protected void onPostExecute(final Boolean mIsSystemNotification) {
            if (ExpandableNotificationRow.this.mEntry != null) {
                ExpandableNotificationRow.this.mEntry.mIsSystemNotification = mIsSystemNotification;
            }
        }
    }
}
