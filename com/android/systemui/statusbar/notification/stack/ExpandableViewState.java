// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import android.animation.PropertyValuesHolder;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator$AnimatorListener;
import android.util.Property;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.view.View;
import android.animation.ValueAnimator;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.R$id;

public class ExpandableViewState extends ViewState
{
    private static final int TAG_ANIMATOR_HEIGHT;
    private static final int TAG_ANIMATOR_TOP_INSET;
    private static final int TAG_END_HEIGHT;
    private static final int TAG_END_TOP_INSET;
    private static final int TAG_START_HEIGHT;
    private static final int TAG_START_TOP_INSET;
    public boolean belowSpeedBump;
    public int clipTopAmount;
    public boolean dimmed;
    public boolean headsUpIsVisible;
    public int height;
    public boolean hideSensitive;
    public boolean inShelf;
    public int location;
    public int notGoneIndex;
    
    static {
        TAG_ANIMATOR_HEIGHT = R$id.height_animator_tag;
        TAG_ANIMATOR_TOP_INSET = R$id.top_inset_animator_tag;
        TAG_END_HEIGHT = R$id.height_animator_end_value_tag;
        TAG_END_TOP_INSET = R$id.top_inset_animator_end_value_tag;
        TAG_START_HEIGHT = R$id.height_animator_start_value_tag;
        TAG_START_TOP_INSET = R$id.top_inset_animator_start_value_tag;
    }
    
    public static int getFinalActualHeight(final ExpandableView expandableView) {
        if (expandableView == null) {
            return 0;
        }
        if (ViewState.getChildTag((View)expandableView, ExpandableViewState.TAG_ANIMATOR_HEIGHT) == null) {
            return expandableView.getActualHeight();
        }
        return ViewState.getChildTag((View)expandableView, ExpandableViewState.TAG_END_HEIGHT);
    }
    
    private void startHeightAnimation(final ExpandableView expandableView, final AnimationProperties animationProperties) {
        final int tag_ANIMATOR_HEIGHT = ExpandableViewState.TAG_ANIMATOR_HEIGHT;
        final int tag_END_HEIGHT = ExpandableViewState.TAG_END_HEIGHT;
        final int tag_START_HEIGHT = ExpandableViewState.TAG_START_HEIGHT;
        final Integer n = ViewState.getChildTag((View)expandableView, tag_START_HEIGHT);
        final Integer n2 = ViewState.getChildTag((View)expandableView, tag_END_HEIGHT);
        final int height = this.height;
        if (n2 != null && n2 == height) {
            return;
        }
        final ValueAnimator valueAnimator = ViewState.getChildTag((View)expandableView, tag_ANIMATOR_HEIGHT);
        if (animationProperties.getAnimationFilter().animateHeight) {
            final ValueAnimator ofInt = ValueAnimator.ofInt(new int[] { expandableView.getActualHeight(), height });
            ofInt.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener(this) {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    expandableView.setActualHeight((int)valueAnimator.getAnimatedValue(), false);
                }
            });
            ofInt.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
            ofInt.setDuration(ViewState.cancelAnimatorAndGetNewDuration(animationProperties.duration, valueAnimator));
            if (animationProperties.delay > 0L && (valueAnimator == null || valueAnimator.getAnimatedFraction() == 0.0f)) {
                ofInt.setStartDelay(animationProperties.delay);
            }
            final AnimatorListenerAdapter animationFinishListener = animationProperties.getAnimationFinishListener(null);
            if (animationFinishListener != null) {
                ofInt.addListener((Animator$AnimatorListener)animationFinishListener);
            }
            ofInt.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
                boolean mWasCancelled;
                
                public void onAnimationCancel(final Animator animator) {
                    this.mWasCancelled = true;
                }
                
                public void onAnimationEnd(final Animator animator) {
                    expandableView.setTag(ExpandableViewState.TAG_ANIMATOR_HEIGHT, (Object)null);
                    expandableView.setTag(ExpandableViewState.TAG_START_HEIGHT, (Object)null);
                    expandableView.setTag(ExpandableViewState.TAG_END_HEIGHT, (Object)null);
                    expandableView.setActualHeightAnimating(false);
                    if (!this.mWasCancelled) {
                        final ExpandableView val$child = expandableView;
                        if (val$child instanceof ExpandableNotificationRow) {
                            ((ExpandableNotificationRow)val$child).setGroupExpansionChanging(false);
                        }
                    }
                }
                
                public void onAnimationStart(final Animator animator) {
                    this.mWasCancelled = false;
                }
            });
            ViewState.startAnimator((Animator)ofInt, animationFinishListener);
            expandableView.setTag(tag_ANIMATOR_HEIGHT, (Object)ofInt);
            expandableView.setTag(tag_START_HEIGHT, (Object)expandableView.getActualHeight());
            expandableView.setTag(tag_END_HEIGHT, (Object)height);
            expandableView.setActualHeightAnimating(true);
            return;
        }
        if (valueAnimator != null) {
            final PropertyValuesHolder[] values = valueAnimator.getValues();
            final int i = n + (height - n2);
            values[0].setIntValues(new int[] { i, height });
            expandableView.setTag(tag_START_HEIGHT, (Object)i);
            expandableView.setTag(tag_END_HEIGHT, (Object)height);
            valueAnimator.setCurrentPlayTime(valueAnimator.getCurrentPlayTime());
            return;
        }
        expandableView.setActualHeight(height, false);
    }
    
    private void startInsetAnimation(final ExpandableView expandableView, final AnimationProperties animationProperties) {
        final int tag_ANIMATOR_TOP_INSET = ExpandableViewState.TAG_ANIMATOR_TOP_INSET;
        final int tag_END_TOP_INSET = ExpandableViewState.TAG_END_TOP_INSET;
        final int tag_START_TOP_INSET = ExpandableViewState.TAG_START_TOP_INSET;
        final Integer n = ViewState.getChildTag((View)expandableView, tag_START_TOP_INSET);
        final Integer n2 = ViewState.getChildTag((View)expandableView, tag_END_TOP_INSET);
        final int clipTopAmount = this.clipTopAmount;
        if (n2 != null && n2 == clipTopAmount) {
            return;
        }
        final ValueAnimator valueAnimator = ViewState.getChildTag((View)expandableView, tag_ANIMATOR_TOP_INSET);
        if (animationProperties.getAnimationFilter().animateTopInset) {
            final ValueAnimator ofInt = ValueAnimator.ofInt(new int[] { expandableView.getClipTopAmount(), clipTopAmount });
            ofInt.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener(this) {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    expandableView.setClipTopAmount((int)valueAnimator.getAnimatedValue());
                }
            });
            ofInt.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
            ofInt.setDuration(ViewState.cancelAnimatorAndGetNewDuration(animationProperties.duration, valueAnimator));
            if (animationProperties.delay > 0L && (valueAnimator == null || valueAnimator.getAnimatedFraction() == 0.0f)) {
                ofInt.setStartDelay(animationProperties.delay);
            }
            final AnimatorListenerAdapter animationFinishListener = animationProperties.getAnimationFinishListener(null);
            if (animationFinishListener != null) {
                ofInt.addListener((Animator$AnimatorListener)animationFinishListener);
            }
            ofInt.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
                public void onAnimationEnd(final Animator animator) {
                    expandableView.setTag(ExpandableViewState.TAG_ANIMATOR_TOP_INSET, (Object)null);
                    expandableView.setTag(ExpandableViewState.TAG_START_TOP_INSET, (Object)null);
                    expandableView.setTag(ExpandableViewState.TAG_END_TOP_INSET, (Object)null);
                }
            });
            ViewState.startAnimator((Animator)ofInt, animationFinishListener);
            expandableView.setTag(tag_ANIMATOR_TOP_INSET, (Object)ofInt);
            expandableView.setTag(tag_START_TOP_INSET, (Object)expandableView.getClipTopAmount());
            expandableView.setTag(tag_END_TOP_INSET, (Object)clipTopAmount);
            return;
        }
        if (valueAnimator != null) {
            final PropertyValuesHolder[] values = valueAnimator.getValues();
            final int i = n + (clipTopAmount - n2);
            values[0].setIntValues(new int[] { i, clipTopAmount });
            expandableView.setTag(tag_START_TOP_INSET, (Object)i);
            expandableView.setTag(tag_END_TOP_INSET, (Object)clipTopAmount);
            valueAnimator.setCurrentPlayTime(valueAnimator.getCurrentPlayTime());
            return;
        }
        expandableView.setClipTopAmount(clipTopAmount);
    }
    
    @Override
    public void animateTo(final View view, final AnimationProperties animationProperties) {
        super.animateTo(view, animationProperties);
        if (!(view instanceof ExpandableView)) {
            return;
        }
        final ExpandableView expandableView = (ExpandableView)view;
        final AnimationFilter animationFilter = animationProperties.getAnimationFilter();
        if (this.height != expandableView.getActualHeight()) {
            this.startHeightAnimation(expandableView, animationProperties);
        }
        else {
            this.abortAnimation(view, ExpandableViewState.TAG_ANIMATOR_HEIGHT);
        }
        if (this.clipTopAmount != expandableView.getClipTopAmount()) {
            this.startInsetAnimation(expandableView, animationProperties);
        }
        else {
            this.abortAnimation(view, ExpandableViewState.TAG_ANIMATOR_TOP_INSET);
        }
        expandableView.setDimmed(this.dimmed, animationFilter.animateDimmed);
        expandableView.setBelowSpeedBump(this.belowSpeedBump);
        expandableView.setHideSensitive(this.hideSensitive, animationFilter.animateHideSensitive, animationProperties.delay, animationProperties.duration);
        if (animationProperties.wasAdded(view) && !super.hidden) {
            expandableView.performAddAnimation(animationProperties.delay, animationProperties.duration, false);
        }
        if (!expandableView.isInShelf() && this.inShelf) {
            expandableView.setTransformingInShelf(true);
        }
        expandableView.setInShelf(this.inShelf);
        if (this.headsUpIsVisible) {
            expandableView.setHeadsUpIsVisible();
        }
    }
    
    @Override
    public void applyToView(final View view) {
        super.applyToView(view);
        if (view instanceof ExpandableView) {
            final ExpandableView expandableView = (ExpandableView)view;
            final int actualHeight = expandableView.getActualHeight();
            final int height = this.height;
            if (actualHeight != height) {
                expandableView.setActualHeight(height, false);
            }
            expandableView.setDimmed(this.dimmed, false);
            expandableView.setHideSensitive(this.hideSensitive, false, 0L, 0L);
            expandableView.setBelowSpeedBump(this.belowSpeedBump);
            final float n = (float)expandableView.getClipTopAmount();
            final int clipTopAmount = this.clipTopAmount;
            if (n != clipTopAmount) {
                expandableView.setClipTopAmount(clipTopAmount);
            }
            expandableView.setTransformingInShelf(false);
            expandableView.setInShelf(this.inShelf);
            if (this.headsUpIsVisible) {
                expandableView.setHeadsUpIsVisible();
            }
        }
    }
    
    @Override
    public void cancelAnimations(final View view) {
        super.cancelAnimations(view);
        final Animator animator = ViewState.getChildTag(view, ExpandableViewState.TAG_ANIMATOR_HEIGHT);
        if (animator != null) {
            animator.cancel();
        }
        final Animator animator2 = ViewState.getChildTag(view, ExpandableViewState.TAG_ANIMATOR_TOP_INSET);
        if (animator2 != null) {
            animator2.cancel();
        }
    }
    
    @Override
    public void copyFrom(final ViewState viewState) {
        super.copyFrom(viewState);
        if (viewState instanceof ExpandableViewState) {
            final ExpandableViewState expandableViewState = (ExpandableViewState)viewState;
            this.height = expandableViewState.height;
            this.dimmed = expandableViewState.dimmed;
            this.hideSensitive = expandableViewState.hideSensitive;
            this.belowSpeedBump = expandableViewState.belowSpeedBump;
            this.clipTopAmount = expandableViewState.clipTopAmount;
            this.notGoneIndex = expandableViewState.notGoneIndex;
            this.location = expandableViewState.location;
            this.headsUpIsVisible = expandableViewState.headsUpIsVisible;
        }
    }
}
