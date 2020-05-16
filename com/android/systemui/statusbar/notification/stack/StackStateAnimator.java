// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import android.animation.Animator$AnimatorListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.view.ViewGroup;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.Iterator;
import com.android.systemui.Interpolators;
import android.view.animation.Interpolator;
import android.util.Property;
import com.android.systemui.R$dimen;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.NotificationShelf;
import java.util.ArrayList;
import android.view.View;
import android.animation.ValueAnimator;
import android.animation.Animator;
import java.util.HashSet;
import android.animation.AnimatorListenerAdapter;
import java.util.Stack;

public class StackStateAnimator
{
    public static final int ANIMATION_DURATION_HEADS_UP_APPEAR_CLOSED;
    private AnimationFilter mAnimationFilter;
    private Stack<AnimatorListenerAdapter> mAnimationListenerPool;
    private final AnimationProperties mAnimationProperties;
    private HashSet<Animator> mAnimatorSet;
    private ValueAnimator mBottomOverScrollAnimator;
    private long mCurrentAdditionalDelay;
    private long mCurrentLength;
    private final int mGoToFullShadeAppearingTranslation;
    private HashSet<View> mHeadsUpAppearChildren;
    private int mHeadsUpAppearHeightBottom;
    private HashSet<View> mHeadsUpDisappearChildren;
    public NotificationStackScrollLayout mHostLayout;
    private ArrayList<View> mNewAddChildren;
    private ArrayList<NotificationStackScrollLayout.AnimationEvent> mNewEvents;
    private boolean mShadeExpanded;
    private NotificationShelf mShelf;
    private int[] mTmpLocation;
    private final ExpandableViewState mTmpState;
    private ValueAnimator mTopOverScrollAnimator;
    private ArrayList<ExpandableView> mTransientViewsToRemove;
    
    static {
        ANIMATION_DURATION_HEADS_UP_APPEAR_CLOSED = (int)(HeadsUpAppearInterpolator.getFractionUntilOvershoot() * 550.0f);
    }
    
    public StackStateAnimator(final NotificationStackScrollLayout mHostLayout) {
        this.mTmpState = new ExpandableViewState();
        this.mNewEvents = new ArrayList<NotificationStackScrollLayout.AnimationEvent>();
        this.mNewAddChildren = new ArrayList<View>();
        this.mHeadsUpAppearChildren = new HashSet<View>();
        this.mHeadsUpDisappearChildren = new HashSet<View>();
        this.mAnimatorSet = new HashSet<Animator>();
        this.mAnimationListenerPool = new Stack<AnimatorListenerAdapter>();
        this.mAnimationFilter = new AnimationFilter();
        this.mTransientViewsToRemove = new ArrayList<ExpandableView>();
        this.mTmpLocation = new int[2];
        this.mHostLayout = mHostLayout;
        this.mGoToFullShadeAppearingTranslation = mHostLayout.getContext().getResources().getDimensionPixelSize(R$dimen.go_to_full_shade_appearing_translation);
        mHostLayout.getContext().getResources().getDimensionPixelSize(R$dimen.pulsing_notification_appear_translation);
        this.mAnimationProperties = new AnimationProperties() {
            @Override
            public AnimationFilter getAnimationFilter() {
                return StackStateAnimator.this.mAnimationFilter;
            }
            
            @Override
            public AnimatorListenerAdapter getAnimationFinishListener(final Property property) {
                return StackStateAnimator.this.getGlobalAnimationFinishedListener();
            }
            
            @Override
            public Interpolator getCustomInterpolator(final View o, final Property obj) {
                if (StackStateAnimator.this.mHeadsUpAppearChildren.contains(o) && View.TRANSLATION_Y.equals(obj)) {
                    return Interpolators.HEADS_UP_APPEAR;
                }
                return null;
            }
            
            @Override
            public boolean wasAdded(final View o) {
                return StackStateAnimator.this.mNewAddChildren.contains(o);
            }
        };
    }
    
    private void adaptDurationWhenGoingToFullShade(final ExpandableView expandableView, final ExpandableViewState expandableViewState, final boolean b, final int n) {
        if (b && this.mAnimationFilter.hasGoToFullShadeEvent) {
            expandableView.setTranslationY(expandableView.getTranslationY() + this.mGoToFullShadeAppearingTranslation);
            this.mAnimationProperties.duration = (long)((float)Math.pow(n, 0.699999988079071) * 100.0f) + 514L;
        }
    }
    
    private boolean applyWithoutAnimation(final ExpandableView expandableView, final ExpandableViewState expandableViewState) {
        if (this.mShadeExpanded) {
            return false;
        }
        if (ViewState.isAnimatingY((View)expandableView)) {
            return false;
        }
        if (this.mHeadsUpDisappearChildren.contains(expandableView) || this.mHeadsUpAppearChildren.contains(expandableView)) {
            return false;
        }
        if (NotificationStackScrollLayout.isPinnedHeadsUp((View)expandableView)) {
            return false;
        }
        expandableViewState.applyToView((View)expandableView);
        return true;
    }
    
    private long calculateChildAnimationDelay(final ExpandableViewState expandableViewState, int animationType) {
        final AnimationFilter mAnimationFilter = this.mAnimationFilter;
        if (mAnimationFilter.hasGoToFullShadeEvent) {
            return this.calculateDelayGoToFullShade(expandableViewState, animationType);
        }
        final long customDelay = mAnimationFilter.customDelay;
        if (customDelay != -1L) {
            return customDelay;
        }
        long n = 0L;
        for (final NotificationStackScrollLayout.AnimationEvent animationEvent : this.mNewEvents) {
            long n2 = 80L;
            animationType = animationEvent.animationType;
            if (animationType != 0) {
                if (animationType != 1) {
                    if (animationType != 2) {
                        continue;
                    }
                    n2 = 32L;
                }
                final int notGoneIndex = expandableViewState.notGoneIndex;
                if (animationEvent.viewAfterChangingView == null) {
                    animationType = 1;
                }
                else {
                    animationType = 0;
                }
                ExpandableView lastChildNotGone;
                if (animationType != 0) {
                    lastChildNotGone = this.mHostLayout.getLastChildNotGone();
                }
                else {
                    lastChildNotGone = (ExpandableView)animationEvent.viewAfterChangingView;
                }
                if (lastChildNotGone == null) {
                    continue;
                }
                final int notGoneIndex2 = lastChildNotGone.getViewState().notGoneIndex;
                if ((animationType = notGoneIndex) >= notGoneIndex2) {
                    animationType = notGoneIndex + 1;
                }
                n = Math.max(Math.max(0, Math.min(2, Math.abs(animationType - notGoneIndex2) - 1)) * n2, n);
            }
            else {
                n = Math.max((2 - Math.max(0, Math.min(2, Math.abs(expandableViewState.notGoneIndex - animationEvent.mChangingView.getViewState().notGoneIndex) - 1))) * 80L, n);
            }
        }
        return n;
    }
    
    private long calculateDelayGoToFullShade(final ExpandableViewState expandableViewState, final int n) {
        final int notGoneIndex = this.mShelf.getNotGoneIndex();
        final float n2 = (float)expandableViewState.notGoneIndex;
        final float n3 = (float)notGoneIndex;
        long n4 = 0L;
        float n5 = n2;
        if (n2 > n3) {
            n4 = 0L + (long)((float)Math.pow(n, 0.699999988079071) * 48.0f * 0.25);
            n5 = n3;
        }
        return n4 + (long)((float)Math.pow(n5, 0.699999988079071) * 48.0f);
    }
    
    private AnimatorListenerAdapter getGlobalAnimationFinishedListener() {
        if (!this.mAnimationListenerPool.empty()) {
            return this.mAnimationListenerPool.pop();
        }
        return new AnimatorListenerAdapter() {
            private boolean mWasCancelled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mWasCancelled = true;
            }
            
            public void onAnimationEnd(final Animator o) {
                StackStateAnimator.this.mAnimatorSet.remove(o);
                if (StackStateAnimator.this.mAnimatorSet.isEmpty() && !this.mWasCancelled) {
                    StackStateAnimator.this.onAnimationFinished();
                }
                StackStateAnimator.this.mAnimationListenerPool.push(this);
            }
            
            public void onAnimationStart(final Animator e) {
                this.mWasCancelled = false;
                StackStateAnimator.this.mAnimatorSet.add(e);
            }
        };
    }
    
    private void initAnimationProperties(final ExpandableView expandableView, final ExpandableViewState expandableViewState, final int n) {
        final boolean wasAdded = this.mAnimationProperties.wasAdded((View)expandableView);
        this.mAnimationProperties.duration = this.mCurrentLength;
        this.adaptDurationWhenGoingToFullShade(expandableView, expandableViewState, wasAdded, n);
        this.mAnimationProperties.delay = 0L;
        if (wasAdded || (this.mAnimationFilter.hasDelays && (expandableViewState.yTranslation != expandableView.getTranslationY() || expandableViewState.zTranslation != expandableView.getTranslationZ() || expandableViewState.alpha != expandableView.getAlpha() || expandableViewState.height != expandableView.getActualHeight() || expandableViewState.clipTopAmount != expandableView.getClipTopAmount()))) {
            this.mAnimationProperties.delay = this.mCurrentAdditionalDelay + this.calculateChildAnimationDelay(expandableViewState, n);
        }
    }
    
    private void onAnimationFinished() {
        this.mHostLayout.onChildAnimationFinished();
        for (final ExpandableView expandableView : this.mTransientViewsToRemove) {
            expandableView.getTransientContainer().removeTransientView((View)expandableView);
        }
        this.mTransientViewsToRemove.clear();
    }
    
    private void processAnimationEvents(final ArrayList<NotificationStackScrollLayout.AnimationEvent> list) {
        for (final NotificationStackScrollLayout.AnimationEvent e : list) {
            final ExpandableView mChangingView = e.mChangingView;
            final int animationType = e.animationType;
            if (animationType == 0) {
                final ExpandableViewState viewState = mChangingView.getViewState();
                if (viewState == null) {
                    continue;
                }
                if (viewState.gone) {
                    continue;
                }
                viewState.applyToView((View)mChangingView);
                this.mNewAddChildren.add((View)mChangingView);
            }
            else {
                int n = 1;
                if (animationType == 1) {
                    if (mChangingView.getVisibility() != 0) {
                        removeTransientView(mChangingView);
                        continue;
                    }
                    float max;
                    if (e.viewAfterChangingView != null) {
                        float n3;
                        final float n2 = n3 = mChangingView.getTranslationY();
                        if (mChangingView instanceof ExpandableNotificationRow) {
                            final View viewAfterChangingView = e.viewAfterChangingView;
                            n3 = n2;
                            if (viewAfterChangingView instanceof ExpandableNotificationRow) {
                                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)mChangingView;
                                final ExpandableNotificationRow expandableNotificationRow2 = (ExpandableNotificationRow)viewAfterChangingView;
                                n3 = n2;
                                if (expandableNotificationRow.isRemoved()) {
                                    n3 = n2;
                                    if (expandableNotificationRow.wasChildInGroupWhenRemoved()) {
                                        n3 = n2;
                                        if (!expandableNotificationRow2.isChildInGroup()) {
                                            n3 = expandableNotificationRow.getTranslationWhenRemoved();
                                        }
                                    }
                                }
                            }
                        }
                        final int actualHeight = mChangingView.getActualHeight();
                        final float yTranslation = ((ExpandableView)e.viewAfterChangingView).getViewState().yTranslation;
                        final float n4 = (float)actualHeight;
                        max = Math.max(Math.min((yTranslation - (n3 + n4 / 2.0f)) * 2.0f / n4, 1.0f), -1.0f);
                    }
                    else {
                        max = -1.0f;
                    }
                    mChangingView.performRemoveAnimation(464L, 0L, max, false, 0.0f, new _$$Lambda$StackStateAnimator$TZG1mUHYcGvJktxtVi9se9juSC8(mChangingView), null);
                }
                else if (animationType == 2) {
                    if (Math.abs(mChangingView.getTranslation()) == mChangingView.getWidth() && mChangingView.getTransientContainer() != null) {
                        mChangingView.getTransientContainer().removeTransientView((View)mChangingView);
                    }
                }
                else if (animationType == 10) {
                    ((ExpandableNotificationRow)mChangingView).prepareExpansionChanged();
                }
                else if (animationType == 11) {
                    this.mTmpState.copyFrom(mChangingView.getViewState());
                    if (e.headsUpFromBottom) {
                        this.mTmpState.yTranslation = (float)this.mHeadsUpAppearHeightBottom;
                    }
                    else {
                        this.mTmpState.yTranslation = 0.0f;
                        mChangingView.performAddAnimation(0L, StackStateAnimator.ANIMATION_DURATION_HEADS_UP_APPEAR_CLOSED, true);
                    }
                    this.mHeadsUpAppearChildren.add((View)mChangingView);
                    this.mTmpState.applyToView((View)mChangingView);
                }
                else if (animationType == 12 || animationType == 13) {
                    this.mHeadsUpDisappearChildren.add((View)mChangingView);
                    Runnable runnable = null;
                    int n5;
                    if (e.animationType == 13) {
                        n5 = 120;
                    }
                    else {
                        n5 = 0;
                    }
                    if (mChangingView.getParent() == null) {
                        this.mHostLayout.addTransientView((View)mChangingView, 0);
                        mChangingView.setTransientContainer(this.mHostLayout);
                        this.mTmpState.initFrom((View)mChangingView);
                        final ExpandableViewState mTmpState = this.mTmpState;
                        mTmpState.yTranslation = 0.0f;
                        this.mAnimationFilter.animateY = true;
                        final AnimationProperties mAnimationProperties = this.mAnimationProperties;
                        mAnimationProperties.delay = n5 + 120;
                        mAnimationProperties.duration = 300L;
                        mTmpState.animateTo((View)mChangingView, mAnimationProperties);
                        runnable = new _$$Lambda$StackStateAnimator$_Pk5aD8YGtEkv3ND7OecxMpqHJ4(mChangingView);
                    }
                    float n8 = 0.0f;
                    Label_0753: {
                        if (mChangingView instanceof ExpandableNotificationRow) {
                            final ExpandableNotificationRow expandableNotificationRow3 = (ExpandableNotificationRow)mChangingView;
                            final boolean b = true ^ expandableNotificationRow3.isDismissed();
                            final NotificationEntry entry = expandableNotificationRow3.getEntry();
                            final StatusBarIconView statusBarIcon = entry.getIcons().getStatusBarIcon();
                            final StatusBarIconView centeredIcon = entry.getIcons().getCenteredIcon();
                            StatusBarIconView statusBarIconView = statusBarIcon;
                            if (centeredIcon != null) {
                                statusBarIconView = statusBarIcon;
                                if (centeredIcon.getParent() != null) {
                                    statusBarIconView = centeredIcon;
                                }
                            }
                            n = (b ? 1 : 0);
                            if (statusBarIconView.getParent() != null) {
                                statusBarIconView.getLocationOnScreen(this.mTmpLocation);
                                final float n6 = (float)this.mTmpLocation[0];
                                final float translationX = statusBarIconView.getTranslationX();
                                final float finalTranslationX = ViewState.getFinalTranslationX((View)statusBarIconView);
                                final float n7 = (float)statusBarIconView.getWidth();
                                this.mHostLayout.getLocationOnScreen(this.mTmpLocation);
                                n8 = n6 - translationX + finalTranslationX + n7 * 0.25f - this.mTmpLocation[0];
                                n = (b ? 1 : 0);
                                break Label_0753;
                            }
                        }
                        n8 = 0.0f;
                    }
                    if (n != 0) {
                        final long performRemoveAnimation = mChangingView.performRemoveAnimation(420L, n5, 0.0f, true, n8, runnable, this.getGlobalAnimationFinishedListener());
                        final AnimationProperties mAnimationProperties2 = this.mAnimationProperties;
                        mAnimationProperties2.delay += performRemoveAnimation;
                    }
                    else if (runnable != null) {
                        runnable.run();
                    }
                }
            }
            this.mNewEvents.add(e);
        }
    }
    
    public static void removeTransientView(final ExpandableView expandableView) {
        if (expandableView.getTransientContainer() != null) {
            expandableView.getTransientContainer().removeTransientView((View)expandableView);
        }
    }
    
    public void animateOverScrollToAmount(final float n, final boolean b, final boolean b2) {
        final float currentOverScrollAmount = this.mHostLayout.getCurrentOverScrollAmount(b);
        if (n == currentOverScrollAmount) {
            return;
        }
        this.cancelOverScrollAnimators(b);
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { currentOverScrollAmount, n });
        ofFloat.setDuration(360L);
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                StackStateAnimator.this.mHostLayout.setOverScrollAmount((float)valueAnimator.getAnimatedValue(), b, false, false, b2);
            }
        });
        ofFloat.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                if (b) {
                    StackStateAnimator.this.mTopOverScrollAnimator = null;
                }
                else {
                    StackStateAnimator.this.mBottomOverScrollAnimator = null;
                }
            }
        });
        ofFloat.start();
        if (b) {
            this.mTopOverScrollAnimator = ofFloat;
        }
        else {
            this.mBottomOverScrollAnimator = ofFloat;
        }
    }
    
    public void cancelOverScrollAnimators(final boolean b) {
        ValueAnimator valueAnimator;
        if (b) {
            valueAnimator = this.mTopOverScrollAnimator;
        }
        else {
            valueAnimator = this.mBottomOverScrollAnimator;
        }
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }
    
    public boolean isRunning() {
        return this.mAnimatorSet.isEmpty() ^ true;
    }
    
    public void setHeadsUpAppearHeightBottom(final int mHeadsUpAppearHeightBottom) {
        this.mHeadsUpAppearHeightBottom = mHeadsUpAppearHeightBottom;
    }
    
    public void setShadeExpanded(final boolean mShadeExpanded) {
        this.mShadeExpanded = mShadeExpanded;
    }
    
    public void setShelf(final NotificationShelf mShelf) {
        this.mShelf = mShelf;
    }
    
    public void startAnimationForEvents(final ArrayList<NotificationStackScrollLayout.AnimationEvent> list, final long mCurrentAdditionalDelay) {
        this.processAnimationEvents(list);
        final int childCount = this.mHostLayout.getChildCount();
        this.mAnimationFilter.applyCombination(this.mNewEvents);
        this.mCurrentAdditionalDelay = mCurrentAdditionalDelay;
        this.mCurrentLength = NotificationStackScrollLayout.AnimationEvent.combineLength(this.mNewEvents);
        int i = 0;
        int n = 0;
        while (i < childCount) {
            final ExpandableView expandableView = (ExpandableView)this.mHostLayout.getChildAt(i);
            final ExpandableViewState viewState = expandableView.getViewState();
            int n2 = n;
            if (viewState != null) {
                n2 = n;
                if (expandableView.getVisibility() != 8) {
                    if (this.applyWithoutAnimation(expandableView, viewState)) {
                        n2 = n;
                    }
                    else {
                        n2 = n;
                        if (this.mAnimationProperties.wasAdded((View)expandableView) && (n2 = n) < 5) {
                            n2 = n + 1;
                        }
                        this.initAnimationProperties(expandableView, viewState, n2);
                        viewState.animateTo((View)expandableView, this.mAnimationProperties);
                    }
                }
            }
            ++i;
            n = n2;
        }
        if (!this.isRunning()) {
            this.onAnimationFinished();
        }
        this.mHeadsUpAppearChildren.clear();
        this.mHeadsUpDisappearChildren.clear();
        this.mNewEvents.clear();
        this.mNewAddChildren.clear();
    }
}
