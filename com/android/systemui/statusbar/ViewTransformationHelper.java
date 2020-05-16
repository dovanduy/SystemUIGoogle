// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.view.animation.Interpolator;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.view.ViewGroup;
import java.util.Stack;
import java.util.Iterator;
import com.android.systemui.R$id;
import android.animation.ValueAnimator;
import android.view.View;
import android.util.ArraySet;
import android.util.ArrayMap;
import com.android.systemui.statusbar.notification.TransformState;

public class ViewTransformationHelper implements TransformableView, TransformInfo
{
    private static final int TAG_CONTAINS_TRANSFORMED_VIEW;
    private ArrayMap<Integer, CustomTransformation> mCustomTransformations;
    private ArraySet<Integer> mKeysTransformingToSimilar;
    private ArrayMap<Integer, View> mTransformedViews;
    private ValueAnimator mViewTransformationAnimation;
    
    static {
        TAG_CONTAINS_TRANSFORMED_VIEW = R$id.contains_transformed_view;
    }
    
    public ViewTransformationHelper() {
        this.mTransformedViews = (ArrayMap<Integer, View>)new ArrayMap();
        this.mKeysTransformingToSimilar = (ArraySet<Integer>)new ArraySet();
        this.mCustomTransformations = (ArrayMap<Integer, CustomTransformation>)new ArrayMap();
    }
    
    private void abortTransformations() {
        final Iterator<Integer> iterator = this.mTransformedViews.keySet().iterator();
        while (iterator.hasNext()) {
            final TransformState currentState = this.getCurrentState(iterator.next());
            if (currentState != null) {
                currentState.abortTransformation();
                currentState.recycle();
            }
        }
    }
    
    public void addRemainingTransformTypes(View item) {
        final int tag_CONTAINS_TRANSFORMED_VIEW = ViewTransformationHelper.TAG_CONTAINS_TRANSFORMED_VIEW;
        for (int size = this.mTransformedViews.size(), i = 0; i < size; ++i) {
            for (View view = (View)this.mTransformedViews.valueAt(i); view != item.getParent(); view = (View)view.getParent()) {
                view.setTag(tag_CONTAINS_TRANSFORMED_VIEW, (Object)Boolean.TRUE);
            }
        }
        final Stack<View> stack = new Stack<View>();
        stack.push(item);
        while (!stack.isEmpty()) {
            item = stack.pop();
            if (item.getTag(tag_CONTAINS_TRANSFORMED_VIEW) == null) {
                final int id = item.getId();
                if (id != -1) {
                    this.addTransformedView(id, item);
                    continue;
                }
            }
            item.setTag(tag_CONTAINS_TRANSFORMED_VIEW, (Object)null);
            if (item instanceof ViewGroup && !this.mTransformedViews.containsValue((Object)item)) {
                final ViewGroup viewGroup = (ViewGroup)item;
                for (int j = 0; j < viewGroup.getChildCount(); ++j) {
                    stack.push(viewGroup.getChildAt(j));
                }
            }
        }
    }
    
    public void addTransformedView(final int i, final View view) {
        this.mTransformedViews.put((Object)i, (Object)view);
    }
    
    public void addTransformedView(final View view) {
        final int id = view.getId();
        if (id != -1) {
            this.addTransformedView(id, view);
            return;
        }
        throw new IllegalArgumentException("View argument does not have a valid id");
    }
    
    public void addViewTransformingToSimilar(final int i, final View view) {
        this.addTransformedView(i, view);
        this.mKeysTransformingToSimilar.add((Object)i);
    }
    
    public void addViewTransformingToSimilar(final View view) {
        final int id = view.getId();
        if (id != -1) {
            this.addViewTransformingToSimilar(id, view);
            return;
        }
        throw new IllegalArgumentException("View argument does not have a valid id");
    }
    
    public ArraySet<View> getAllTransformingViews() {
        return (ArraySet<View>)new ArraySet(this.mTransformedViews.values());
    }
    
    @Override
    public TransformState getCurrentState(final int n) {
        final View view = (View)this.mTransformedViews.get((Object)n);
        if (view != null && view.getVisibility() != 8) {
            final TransformState from = TransformState.createFrom(view, (TransformState.TransformInfo)this);
            if (this.mKeysTransformingToSimilar.contains((Object)n)) {
                from.setIsSameAsAnyView(true);
            }
            return from;
        }
        return null;
    }
    
    @Override
    public boolean isAnimating() {
        final ValueAnimator mViewTransformationAnimation = this.mViewTransformationAnimation;
        return mViewTransformationAnimation != null && mViewTransformationAnimation.isRunning();
    }
    
    public void reset() {
        this.mTransformedViews.clear();
        this.mKeysTransformingToSimilar.clear();
    }
    
    public void resetTransformedView(final View view) {
        final TransformState from = TransformState.createFrom(view, (TransformState.TransformInfo)this);
        from.setVisible(true, true);
        from.recycle();
    }
    
    public void setCustomTransformation(final CustomTransformation customTransformation, final int i) {
        this.mCustomTransformations.put((Object)i, (Object)customTransformation);
    }
    
    @Override
    public void setVisible(final boolean b) {
        final ValueAnimator mViewTransformationAnimation = this.mViewTransformationAnimation;
        if (mViewTransformationAnimation != null) {
            mViewTransformationAnimation.cancel();
        }
        final Iterator<Integer> iterator = this.mTransformedViews.keySet().iterator();
        while (iterator.hasNext()) {
            final TransformState currentState = this.getCurrentState(iterator.next());
            if (currentState != null) {
                currentState.setVisible(b, false);
                currentState.recycle();
            }
        }
    }
    
    @Override
    public void transformFrom(final TransformableView transformableView) {
        final ValueAnimator mViewTransformationAnimation = this.mViewTransformationAnimation;
        if (mViewTransformationAnimation != null) {
            mViewTransformationAnimation.cancel();
        }
        (this.mViewTransformationAnimation = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f })).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                ViewTransformationHelper.this.transformFrom(transformableView, valueAnimator.getAnimatedFraction());
            }
        });
        this.mViewTransformationAnimation.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public boolean mCancelled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (!this.mCancelled) {
                    ViewTransformationHelper.this.setVisible(true);
                }
                else {
                    ViewTransformationHelper.this.abortTransformations();
                }
            }
        });
        this.mViewTransformationAnimation.setInterpolator((TimeInterpolator)Interpolators.LINEAR);
        this.mViewTransformationAnimation.setDuration(360L);
        this.mViewTransformationAnimation.start();
    }
    
    @Override
    public void transformFrom(final TransformableView transformableView, final float n) {
        for (final Integer n2 : this.mTransformedViews.keySet()) {
            final TransformState currentState = this.getCurrentState(n2);
            if (currentState != null) {
                final CustomTransformation customTransformation = (CustomTransformation)this.mCustomTransformations.get((Object)n2);
                if (customTransformation != null && customTransformation.transformFrom(currentState, transformableView, n)) {
                    currentState.recycle();
                }
                else {
                    final TransformState currentState2 = transformableView.getCurrentState(n2);
                    if (currentState2 != null) {
                        currentState.transformViewFrom(currentState2, n);
                        currentState2.recycle();
                    }
                    else {
                        currentState.appear(n, transformableView);
                    }
                    currentState.recycle();
                }
            }
        }
    }
    
    @Override
    public void transformTo(final TransformableView transformableView, final float n) {
        for (final Integer n2 : this.mTransformedViews.keySet()) {
            final TransformState currentState = this.getCurrentState(n2);
            if (currentState != null) {
                final CustomTransformation customTransformation = (CustomTransformation)this.mCustomTransformations.get((Object)n2);
                if (customTransformation != null && customTransformation.transformTo(currentState, transformableView, n)) {
                    currentState.recycle();
                }
                else {
                    final TransformState currentState2 = transformableView.getCurrentState(n2);
                    if (currentState2 != null) {
                        currentState.transformViewTo(currentState2, n);
                        currentState2.recycle();
                    }
                    else {
                        currentState.disappear(n, transformableView);
                    }
                    currentState.recycle();
                }
            }
        }
    }
    
    @Override
    public void transformTo(final TransformableView transformableView, final Runnable runnable) {
        final ValueAnimator mViewTransformationAnimation = this.mViewTransformationAnimation;
        if (mViewTransformationAnimation != null) {
            mViewTransformationAnimation.cancel();
        }
        (this.mViewTransformationAnimation = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f })).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                ViewTransformationHelper.this.transformTo(transformableView, valueAnimator.getAnimatedFraction());
            }
        });
        this.mViewTransformationAnimation.setInterpolator((TimeInterpolator)Interpolators.LINEAR);
        this.mViewTransformationAnimation.setDuration(360L);
        this.mViewTransformationAnimation.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public boolean mCancelled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (!this.mCancelled) {
                    final Runnable val$endRunnable = runnable;
                    if (val$endRunnable != null) {
                        val$endRunnable.run();
                    }
                    ViewTransformationHelper.this.setVisible(false);
                    ViewTransformationHelper.this.mViewTransformationAnimation = null;
                }
                else {
                    ViewTransformationHelper.this.abortTransformations();
                }
            }
        });
        this.mViewTransformationAnimation.start();
    }
    
    public abstract static class CustomTransformation
    {
        public boolean customTransformTarget(final TransformState transformState, final TransformState transformState2) {
            return false;
        }
        
        public Interpolator getCustomInterpolator(final int n, final boolean b) {
            return null;
        }
        
        public boolean initTransformation(final TransformState transformState, final TransformState transformState2) {
            return false;
        }
        
        public abstract boolean transformFrom(final TransformState p0, final TransformableView p1, final float p2);
        
        public abstract boolean transformTo(final TransformState p0, final TransformableView p1, final float p2);
    }
}
