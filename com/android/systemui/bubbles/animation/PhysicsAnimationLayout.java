// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles.animation;

import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.util.Log;
import android.util.Property;
import android.animation.TimeInterpolator;
import android.graphics.Path;
import android.animation.ValueAnimator;
import android.util.FloatProperty;
import android.graphics.PointF;
import java.util.Map;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.List;
import java.util.Set;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import com.android.systemui.R$id;
import java.util.Iterator;
import android.view.ViewGroup$LayoutParams;
import android.animation.ObjectAnimator;
import androidx.dynamicanimation.animation.SpringAnimation;
import android.view.View;
import android.content.Context;
import androidx.dynamicanimation.animation.DynamicAnimation;
import java.util.HashMap;
import android.widget.FrameLayout;

public class PhysicsAnimationLayout extends FrameLayout
{
    protected PhysicsAnimationController mController;
    protected final HashMap<DynamicAnimation.ViewProperty, Runnable> mEndActionForProperty;
    
    public PhysicsAnimationLayout(final Context context) {
        super(context);
        this.mEndActionForProperty = new HashMap<DynamicAnimation.ViewProperty, Runnable>();
    }
    
    private void addViewInternal(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams, final boolean b) {
        super.addView(view, n, viewGroup$LayoutParams);
        final PhysicsAnimationController mController = this.mController;
        if (mController != null && !b) {
            final Iterator<DynamicAnimation.ViewProperty> iterator = mController.getAnimatedProperties().iterator();
            while (iterator.hasNext()) {
                this.setUpAnimationForChild(iterator.next(), view, n);
            }
            this.mController.onChildAdded(view, n);
        }
    }
    
    private SpringAnimation getAnimationAtIndex(final DynamicAnimation.ViewProperty viewProperty, final int n) {
        return this.getAnimationFromView(viewProperty, this.getChildAt(n));
    }
    
    private SpringAnimation getAnimationFromView(final DynamicAnimation.ViewProperty viewProperty, final View view) {
        return (SpringAnimation)view.getTag(this.getTagIdForProperty(viewProperty));
    }
    
    protected static String getReadablePropertyName(final DynamicAnimation.ViewProperty viewProperty) {
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_X)) {
            return "TRANSLATION_X";
        }
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_Y)) {
            return "TRANSLATION_Y";
        }
        if (viewProperty.equals(DynamicAnimation.SCALE_X)) {
            return "SCALE_X";
        }
        if (viewProperty.equals(DynamicAnimation.SCALE_Y)) {
            return "SCALE_Y";
        }
        if (viewProperty.equals(DynamicAnimation.ALPHA)) {
            return "ALPHA";
        }
        return "Unknown animation property.";
    }
    
    private int getTagIdForProperty(final DynamicAnimation.ViewProperty viewProperty) {
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_X)) {
            return R$id.translation_x_dynamicanimation_tag;
        }
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_Y)) {
            return R$id.translation_y_dynamicanimation_tag;
        }
        if (viewProperty.equals(DynamicAnimation.SCALE_X)) {
            return R$id.scale_x_dynamicanimation_tag;
        }
        if (viewProperty.equals(DynamicAnimation.SCALE_Y)) {
            return R$id.scale_y_dynamicanimation_tag;
        }
        if (viewProperty.equals(DynamicAnimation.ALPHA)) {
            return R$id.alpha_dynamicanimation_tag;
        }
        return -1;
    }
    
    private ObjectAnimator getTargetAnimatorFromView(final View view) {
        return (ObjectAnimator)view.getTag(R$id.target_animator_tag);
    }
    
    private void setUpAnimationForChild(final DynamicAnimation.ViewProperty viewProperty, final View view, final int n) {
        final SpringAnimation springAnimation = new SpringAnimation((K)view, (FloatPropertyCompat<K>)viewProperty);
        springAnimation.addUpdateListener((DynamicAnimation.OnAnimationUpdateListener)new _$$Lambda$PhysicsAnimationLayout$zwbjiGEsnfRdNGFmqcdzTxp4TUg(this, view, viewProperty));
        springAnimation.setSpring(this.mController.getSpringForce(viewProperty, view));
        springAnimation.addEndListener((DynamicAnimation.OnAnimationEndListener)new AllAnimationsForPropertyFinishedEndListener(viewProperty));
        view.setTag(this.getTagIdForProperty(viewProperty), (Object)springAnimation);
    }
    
    private void setUpAnimationsForProperty(final DynamicAnimation.ViewProperty viewProperty) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            this.setUpAnimationForChild(viewProperty, this.getChildAt(i), i);
        }
    }
    
    public void addView(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        this.addViewInternal(view, n, viewGroup$LayoutParams, false);
    }
    
    public boolean arePropertiesAnimating(final DynamicAnimation.ViewProperty... array) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            if (this.arePropertiesAnimatingOnView(this.getChildAt(i), array)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean arePropertiesAnimatingOnView(final View view, final DynamicAnimation.ViewProperty... array) {
        final ObjectAnimator targetAnimatorFromView = this.getTargetAnimatorFromView(view);
        for (final DynamicAnimation.ViewProperty viewProperty : array) {
            final SpringAnimation animationFromView = this.getAnimationFromView(viewProperty, view);
            if (animationFromView != null && animationFromView.isRunning()) {
                return true;
            }
            if ((viewProperty.equals(DynamicAnimation.TRANSLATION_X) || viewProperty.equals(DynamicAnimation.TRANSLATION_Y)) && targetAnimatorFromView != null && targetAnimatorFromView.isRunning()) {
                return true;
            }
        }
        return false;
    }
    
    public void cancelAllAnimations() {
        final PhysicsAnimationController mController = this.mController;
        if (mController == null) {
            return;
        }
        this.cancelAllAnimationsOfProperties((DynamicAnimation.ViewProperty[])mController.getAnimatedProperties().toArray(new DynamicAnimation.ViewProperty[0]));
    }
    
    public void cancelAllAnimationsOfProperties(final DynamicAnimation.ViewProperty... array) {
        if (this.mController == null) {
            return;
        }
        for (int i = 0; i < this.getChildCount(); ++i) {
            for (int length = array.length, j = 0; j < length; ++j) {
                final SpringAnimation animationAtIndex = this.getAnimationAtIndex(array[j], i);
                if (animationAtIndex != null) {
                    animationAtIndex.cancel();
                }
            }
        }
    }
    
    public void cancelAnimationsOnView(final View view) {
        final ObjectAnimator targetAnimatorFromView = this.getTargetAnimatorFromView(view);
        if (targetAnimatorFromView != null) {
            targetAnimatorFromView.cancel();
        }
        final Iterator<DynamicAnimation.ViewProperty> iterator = this.mController.getAnimatedProperties().iterator();
        while (iterator.hasNext()) {
            this.getAnimationFromView(iterator.next(), view).cancel();
        }
    }
    
    protected boolean isActiveController(final PhysicsAnimationController physicsAnimationController) {
        return this.mController == physicsAnimationController;
    }
    
    protected boolean isFirstChildXLeftOfCenter(final float n) {
        final int childCount = this.getChildCount();
        boolean b = false;
        if (childCount > 0) {
            b = b;
            if (n + this.getChildAt(0).getWidth() / 2 < this.getWidth() / 2) {
                b = true;
            }
        }
        return b;
    }
    
    public void removeView(final View view) {
        if (this.mController != null) {
            final int indexOfChild = this.indexOfChild(view);
            super.removeView(view);
            this.addTransientView(view, indexOfChild);
            this.mController.onChildRemoved(view, indexOfChild, new _$$Lambda$PhysicsAnimationLayout$VGQ81KsCYiJ_C0alb_wfA2McXCU(this, view));
        }
        else {
            super.removeView(view);
        }
    }
    
    public void removeViewAt(final int n) {
        this.removeView(this.getChildAt(n));
    }
    
    public void reorderView(final View view, final int n) {
        if (view == null) {
            return;
        }
        final int indexOfChild = this.indexOfChild(view);
        super.removeView(view);
        this.addViewInternal(view, n, view.getLayoutParams(), true);
        final PhysicsAnimationController mController = this.mController;
        if (mController != null) {
            mController.onChildReordered(view, indexOfChild, n);
        }
    }
    
    public void setActiveController(final PhysicsAnimationController mController) {
        this.cancelAllAnimations();
        this.mEndActionForProperty.clear();
        (this.mController = mController).setLayout(this);
        final Iterator<DynamicAnimation.ViewProperty> iterator = this.mController.getAnimatedProperties().iterator();
        while (iterator.hasNext()) {
            this.setUpAnimationsForProperty(iterator.next());
        }
    }
    
    protected class AllAnimationsForPropertyFinishedEndListener implements OnAnimationEndListener
    {
        private ViewProperty mProperty;
        
        AllAnimationsForPropertyFinishedEndListener(final ViewProperty mProperty) {
            this.mProperty = mProperty;
        }
        
        @Override
        public void onAnimationEnd(final DynamicAnimation dynamicAnimation, final boolean b, final float n, final float n2) {
            if (!PhysicsAnimationLayout.this.arePropertiesAnimating(this.mProperty) && PhysicsAnimationLayout.this.mEndActionForProperty.containsKey(this.mProperty)) {
                final Runnable runnable = PhysicsAnimationLayout.this.mEndActionForProperty.get(this.mProperty);
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }
    
    abstract static class PhysicsAnimationController
    {
        protected PhysicsAnimationLayout mLayout;
        
        protected PhysicsPropertyAnimator animationForChild(final View view) {
            PhysicsPropertyAnimator physicsPropertyAnimator;
            if ((physicsPropertyAnimator = (PhysicsPropertyAnimator)view.getTag(R$id.physics_animator_tag)) == null) {
                final PhysicsAnimationLayout mLayout = this.mLayout;
                Objects.requireNonNull(mLayout);
                physicsPropertyAnimator = mLayout.new PhysicsPropertyAnimator(view);
                view.setTag(R$id.physics_animator_tag, (Object)physicsPropertyAnimator);
            }
            physicsPropertyAnimator.clearAnimator();
            physicsPropertyAnimator.setAssociatedController(this);
            return physicsPropertyAnimator;
        }
        
        protected PhysicsPropertyAnimator animationForChildAtIndex(final int n) {
            return this.animationForChild(this.mLayout.getChildAt(n));
        }
        
        protected MultiAnimationStarter animationsForChildrenFromIndex(int i, final ChildAnimationConfigurator childAnimationConfigurator) {
            final HashSet set = new HashSet();
            final ArrayList<PhysicsPropertyAnimator> list = new ArrayList<PhysicsPropertyAnimator>();
            while (i < this.mLayout.getChildCount()) {
                final PhysicsPropertyAnimator animationForChildAtIndex = this.animationForChildAtIndex(i);
                childAnimationConfigurator.configureAnimationForChildAtIndex(i, animationForChildAtIndex);
                set.addAll(animationForChildAtIndex.getAnimatedProperties());
                list.add(animationForChildAtIndex);
                ++i;
            }
            return (MultiAnimationStarter)new _$$Lambda$PhysicsAnimationLayout$PhysicsAnimationController$QukG2X_vIQ5QkpRissMu_oS31l0(this, set, list);
        }
        
        abstract Set<DynamicAnimation.ViewProperty> getAnimatedProperties();
        
        abstract int getNextAnimationInChain(final DynamicAnimation.ViewProperty p0, final int p1);
        
        abstract float getOffsetForChainedPropertyAnimation(final DynamicAnimation.ViewProperty p0);
        
        abstract SpringForce getSpringForce(final DynamicAnimation.ViewProperty p0, final View p1);
        
        protected boolean isActiveController() {
            final PhysicsAnimationLayout mLayout = this.mLayout;
            return mLayout != null && this == mLayout.mController;
        }
        
        abstract void onActiveControllerForLayout(final PhysicsAnimationLayout p0);
        
        abstract void onChildAdded(final View p0, final int p1);
        
        abstract void onChildRemoved(final View p0, final int p1, final Runnable p2);
        
        abstract void onChildReordered(final View p0, final int p1, final int p2);
        
        protected void removeEndActionForProperty(final DynamicAnimation.ViewProperty key) {
            this.mLayout.mEndActionForProperty.remove(key);
        }
        
        protected void setEndActionForMultipleProperties(final Runnable runnable, final DynamicAnimation.ViewProperty... array) {
            final _$$Lambda$PhysicsAnimationLayout$PhysicsAnimationController$k470cCDrnNZB7vKHsf7OzOwkMRY $$Lambda$PhysicsAnimationLayout$PhysicsAnimationController$k470cCDrnNZB7vKHsf7OzOwkMRY = new _$$Lambda$PhysicsAnimationLayout$PhysicsAnimationController$k470cCDrnNZB7vKHsf7OzOwkMRY(this, array, runnable);
            for (int length = array.length, i = 0; i < length; ++i) {
                this.setEndActionForProperty($$Lambda$PhysicsAnimationLayout$PhysicsAnimationController$k470cCDrnNZB7vKHsf7OzOwkMRY, array[i]);
            }
        }
        
        protected void setEndActionForProperty(final Runnable value, final DynamicAnimation.ViewProperty key) {
            this.mLayout.mEndActionForProperty.put(key, value);
        }
        
        protected void setLayout(final PhysicsAnimationLayout mLayout) {
            this.onActiveControllerForLayout(this.mLayout = mLayout);
        }
        
        interface ChildAnimationConfigurator
        {
            void configureAnimationForChildAtIndex(final int p0, final PhysicsPropertyAnimator p1);
        }
        
        interface MultiAnimationStarter
        {
            void startAll(final Runnable... p0);
        }
    }
    
    protected class PhysicsPropertyAnimator
    {
        private Map<DynamicAnimation.ViewProperty, Float> mAnimatedProperties;
        private PhysicsAnimationController mAssociatedController;
        private PointF mCurrentPointOnPath;
        private final FloatProperty<PhysicsPropertyAnimator> mCurrentPointOnPathXProperty;
        private final FloatProperty<PhysicsPropertyAnimator> mCurrentPointOnPathYProperty;
        private float mDampingRatio;
        private float mDefaultStartVelocity;
        private Map<DynamicAnimation.ViewProperty, Runnable[]> mEndActionsForProperty;
        private Map<DynamicAnimation.ViewProperty, Float> mInitialPropertyValues;
        private ObjectAnimator mPathAnimator;
        private Runnable[] mPositionEndActions;
        private Map<DynamicAnimation.ViewProperty, Float> mPositionStartVelocities;
        private long mStartDelay;
        private float mStiffness;
        private View mView;
        
        protected PhysicsPropertyAnimator(final View mView) {
            this.mDefaultStartVelocity = -3.4028235E38f;
            this.mStartDelay = 0L;
            this.mDampingRatio = -1.0f;
            this.mStiffness = -1.0f;
            this.mEndActionsForProperty = new HashMap<DynamicAnimation.ViewProperty, Runnable[]>();
            this.mPositionStartVelocities = new HashMap<DynamicAnimation.ViewProperty, Float>();
            this.mAnimatedProperties = new HashMap<DynamicAnimation.ViewProperty, Float>();
            this.mInitialPropertyValues = new HashMap<DynamicAnimation.ViewProperty, Float>();
            this.mCurrentPointOnPath = new PointF();
            this.mCurrentPointOnPathXProperty = new FloatProperty<PhysicsPropertyAnimator>("PathX") {
                public Float get(final PhysicsPropertyAnimator physicsPropertyAnimator) {
                    return PhysicsPropertyAnimator.this.mCurrentPointOnPath.x;
                }
                
                public void setValue(final PhysicsPropertyAnimator physicsPropertyAnimator, final float x) {
                    PhysicsPropertyAnimator.this.mCurrentPointOnPath.x = x;
                }
            };
            this.mCurrentPointOnPathYProperty = new FloatProperty<PhysicsPropertyAnimator>("PathY") {
                public Float get(final PhysicsPropertyAnimator physicsPropertyAnimator) {
                    return PhysicsPropertyAnimator.this.mCurrentPointOnPath.y;
                }
                
                public void setValue(final PhysicsPropertyAnimator physicsPropertyAnimator, final float y) {
                    PhysicsPropertyAnimator.this.mCurrentPointOnPath.y = y;
                }
            };
            this.mView = mView;
        }
        
        private void clearAnimator() {
            this.mInitialPropertyValues.clear();
            this.mAnimatedProperties.clear();
            this.mPositionStartVelocities.clear();
            this.mDefaultStartVelocity = -3.4028235E38f;
            this.mStartDelay = 0L;
            this.mStiffness = -1.0f;
            this.mDampingRatio = -1.0f;
            this.mEndActionsForProperty.clear();
            this.mPathAnimator = null;
            this.mPositionEndActions = null;
        }
        
        private void clearTranslationValues() {
            this.mAnimatedProperties.remove(DynamicAnimation.TRANSLATION_X);
            this.mAnimatedProperties.remove(DynamicAnimation.TRANSLATION_Y);
            this.mInitialPropertyValues.remove(DynamicAnimation.TRANSLATION_X);
            this.mInitialPropertyValues.remove(DynamicAnimation.TRANSLATION_Y);
            PhysicsAnimationLayout.this.mEndActionForProperty.remove(DynamicAnimation.TRANSLATION_X);
            PhysicsAnimationLayout.this.mEndActionForProperty.remove(DynamicAnimation.TRANSLATION_Y);
        }
        
        private void setAssociatedController(final PhysicsAnimationController mAssociatedController) {
            this.mAssociatedController = mAssociatedController;
        }
        
        private void updateValueForChild(final DynamicAnimation.ViewProperty viewProperty, final View view, final float finalPosition) {
            if (view != null) {
                final SpringAnimation springAnimation = (SpringAnimation)view.getTag(PhysicsAnimationLayout.this.getTagIdForProperty(viewProperty));
                final SpringForce spring = springAnimation.getSpring();
                if (spring == null) {
                    return;
                }
                spring.setFinalPosition(finalPosition);
                springAnimation.start();
            }
        }
        
        public PhysicsPropertyAnimator alpha(final float n, final Runnable... array) {
            this.property(DynamicAnimation.ALPHA, n, array);
            return this;
        }
        
        protected void animateValueForChild(final DynamicAnimation.ViewProperty viewProperty, final View view, final float n, final float n2, final long n3, final float n4, final float n5, final Runnable... array) {
            if (view != null) {
                final SpringAnimation springAnimation = (SpringAnimation)view.getTag(PhysicsAnimationLayout.this.getTagIdForProperty(viewProperty));
                if (springAnimation == null) {
                    return;
                }
                if (array != null) {
                    springAnimation.addEndListener((DynamicAnimation.OnAnimationEndListener)new OneTimeEndListener(this) {
                        @Override
                        public void onAnimationEnd(final DynamicAnimation dynamicAnimation, final boolean b, final float n, final float n2) {
                            super.onAnimationEnd(dynamicAnimation, b, n, n2);
                            final Runnable[] val$afterCallbacks = array;
                            for (int length = val$afterCallbacks.length, i = 0; i < length; ++i) {
                                val$afterCallbacks[i].run();
                            }
                        }
                    });
                }
                final SpringForce spring = springAnimation.getSpring();
                if (spring == null) {
                    return;
                }
                final _$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$YrUNYDpshnd98P1tIxCkdc37pTc $$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$YrUNYDpshnd98P1tIxCkdc37pTc = new _$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$YrUNYDpshnd98P1tIxCkdc37pTc(spring, n4, n5, n2, springAnimation, n);
                if (n3 > 0L) {
                    PhysicsAnimationLayout.this.postDelayed((Runnable)$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$YrUNYDpshnd98P1tIxCkdc37pTc, n3);
                }
                else {
                    $$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$YrUNYDpshnd98P1tIxCkdc37pTc.run();
                }
            }
        }
        
        public PhysicsPropertyAnimator followAnimatedTargetAlongPath(final Path path, final int n, final TimeInterpolator interpolator, final Runnable... mPositionEndActions) {
            (this.mPathAnimator = ObjectAnimator.ofFloat((Object)this, (Property)this.mCurrentPointOnPathXProperty, (Property)this.mCurrentPointOnPathYProperty, path)).setDuration((long)n);
            this.mPathAnimator.setInterpolator(interpolator);
            this.mPositionEndActions = mPositionEndActions;
            this.clearTranslationValues();
            return this;
        }
        
        protected Set<DynamicAnimation.ViewProperty> getAnimatedProperties() {
            final HashSet<DynamicAnimation.ViewProperty> set = new HashSet<DynamicAnimation.ViewProperty>((Collection<? extends DynamicAnimation.ViewProperty>)this.mAnimatedProperties.keySet());
            if (this.mPathAnimator != null) {
                set.add(DynamicAnimation.TRANSLATION_X);
                set.add(DynamicAnimation.TRANSLATION_Y);
            }
            return set;
        }
        
        public PhysicsPropertyAnimator position(final float n, final float n2, final Runnable... mPositionEndActions) {
            this.mPositionEndActions = mPositionEndActions;
            this.translationX(n, new Runnable[0]);
            this.translationY(n2, new Runnable[0]);
            return this;
        }
        
        public PhysicsPropertyAnimator property(final DynamicAnimation.ViewProperty viewProperty, final float f, final Runnable... array) {
            this.mAnimatedProperties.put(viewProperty, f);
            this.mEndActionsForProperty.put(viewProperty, array);
            return this;
        }
        
        public PhysicsPropertyAnimator scaleX(final float n, final Runnable... array) {
            this.property(DynamicAnimation.SCALE_X, n, array);
            return this;
        }
        
        public PhysicsPropertyAnimator scaleY(final float n, final Runnable... array) {
            this.property(DynamicAnimation.SCALE_Y, n, array);
            return this;
        }
        
        public void start(final Runnable... array) {
            if (!PhysicsAnimationLayout.this.isActiveController(this.mAssociatedController)) {
                Log.w("Bubbs.PAL", "Only the active animation controller is allowed to start animations. Use PhysicsAnimationLayout#setActiveController to set the active animation controller.");
                return;
            }
            final Set<DynamicAnimation.ViewProperty> animatedProperties = this.getAnimatedProperties();
            if (array != null && array.length > 0) {
                this.mAssociatedController.setEndActionForMultipleProperties(new _$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$iuqdgR2C6CC4Qpac87e6S6WedyM(array), (DynamicAnimation.ViewProperty[])animatedProperties.toArray(new DynamicAnimation.ViewProperty[0]));
            }
            if (this.mPositionEndActions != null) {
                final _$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$3DhSPSm_kLIWL6PRkLpBmJ3MVps $$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$3DhSPSm_kLIWL6PRkLpBmJ3MVps = new _$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$3DhSPSm_kLIWL6PRkLpBmJ3MVps(this, PhysicsAnimationLayout.this.getAnimationFromView(DynamicAnimation.TRANSLATION_X, this.mView), PhysicsAnimationLayout.this.getAnimationFromView(DynamicAnimation.TRANSLATION_Y, this.mView));
                this.mEndActionsForProperty.put(DynamicAnimation.TRANSLATION_X, new Runnable[] { $$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$3DhSPSm_kLIWL6PRkLpBmJ3MVps });
                this.mEndActionsForProperty.put(DynamicAnimation.TRANSLATION_Y, new Runnable[] { $$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$3DhSPSm_kLIWL6PRkLpBmJ3MVps });
            }
            if (this.mPathAnimator != null) {
                this.startPathAnimation();
            }
            for (final DynamicAnimation.ViewProperty key : animatedProperties) {
                if (this.mPathAnimator != null && (key.equals(DynamicAnimation.TRANSLATION_X) || key.equals(DynamicAnimation.TRANSLATION_Y))) {
                    return;
                }
                if (this.mInitialPropertyValues.containsKey(key)) {
                    key.setValue(this.mView, this.mInitialPropertyValues.get(key));
                }
                final SpringForce springForce = PhysicsAnimationLayout.this.mController.getSpringForce(key, this.mView);
                final View mView = this.mView;
                final float floatValue = this.mAnimatedProperties.get(key);
                final float floatValue2 = this.mPositionStartVelocities.getOrDefault(key, this.mDefaultStartVelocity);
                final long mStartDelay = this.mStartDelay;
                float n = this.mStiffness;
                if (n < 0.0f) {
                    n = springForce.getStiffness();
                }
                float n2 = this.mDampingRatio;
                if (n2 < 0.0f) {
                    n2 = springForce.getDampingRatio();
                }
                this.animateValueForChild(key, mView, floatValue, floatValue2, mStartDelay, n, n2, (Runnable[])this.mEndActionsForProperty.get(key));
            }
            this.clearAnimator();
        }
        
        protected void startPathAnimation() {
            final SpringForce springForce = PhysicsAnimationLayout.this.mController.getSpringForce(DynamicAnimation.TRANSLATION_X, this.mView);
            final SpringForce springForce2 = PhysicsAnimationLayout.this.mController.getSpringForce(DynamicAnimation.TRANSLATION_Y, this.mView);
            final long mStartDelay = this.mStartDelay;
            if (mStartDelay > 0L) {
                this.mPathAnimator.setStartDelay(mStartDelay);
            }
            final _$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$VvmQYTYF92KoaeTMVxzFjdA4FFA $$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$VvmQYTYF92KoaeTMVxzFjdA4FFA = new _$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$VvmQYTYF92KoaeTMVxzFjdA4FFA(this);
            this.mPathAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$1Xv4slF4ncwrmkshsfcHipCSgjk($$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$VvmQYTYF92KoaeTMVxzFjdA4FFA));
            this.mPathAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    $$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$VvmQYTYF92KoaeTMVxzFjdA4FFA.run();
                }
                
                public void onAnimationStart(final Animator animator) {
                    final PhysicsPropertyAnimator this$1 = PhysicsPropertyAnimator.this;
                    final DynamicAnimation.ViewProperty translation_X = DynamicAnimation.TRANSLATION_X;
                    final View access$500 = this$1.mView;
                    final float x = PhysicsPropertyAnimator.this.mCurrentPointOnPath.x;
                    final float access$501 = PhysicsPropertyAnimator.this.mDefaultStartVelocity;
                    float n;
                    if (PhysicsPropertyAnimator.this.mStiffness >= 0.0f) {
                        n = PhysicsPropertyAnimator.this.mStiffness;
                    }
                    else {
                        n = springForce.getStiffness();
                    }
                    float n2;
                    if (PhysicsPropertyAnimator.this.mDampingRatio >= 0.0f) {
                        n2 = PhysicsPropertyAnimator.this.mDampingRatio;
                    }
                    else {
                        n2 = springForce.getDampingRatio();
                    }
                    this$1.animateValueForChild(translation_X, access$500, x, access$501, 0L, n, n2, new Runnable[0]);
                    final PhysicsPropertyAnimator this$2 = PhysicsPropertyAnimator.this;
                    final DynamicAnimation.ViewProperty translation_Y = DynamicAnimation.TRANSLATION_Y;
                    final View access$502 = this$2.mView;
                    final float y = PhysicsPropertyAnimator.this.mCurrentPointOnPath.y;
                    final float access$503 = PhysicsPropertyAnimator.this.mDefaultStartVelocity;
                    float n3;
                    if (PhysicsPropertyAnimator.this.mStiffness >= 0.0f) {
                        n3 = PhysicsPropertyAnimator.this.mStiffness;
                    }
                    else {
                        n3 = springForce2.getStiffness();
                    }
                    float n4;
                    if (PhysicsPropertyAnimator.this.mDampingRatio >= 0.0f) {
                        n4 = PhysicsPropertyAnimator.this.mDampingRatio;
                    }
                    else {
                        n4 = springForce2.getDampingRatio();
                    }
                    this$2.animateValueForChild(translation_Y, access$502, y, access$503, 0L, n3, n4, new Runnable[0]);
                }
            });
            final ObjectAnimator access$900 = PhysicsAnimationLayout.this.getTargetAnimatorFromView(this.mView);
            if (access$900 != null) {
                access$900.cancel();
            }
            this.mView.setTag(R$id.target_animator_tag, (Object)this.mPathAnimator);
            this.mPathAnimator.start();
        }
        
        public PhysicsPropertyAnimator translationX(final float n, final Runnable... array) {
            this.mPathAnimator = null;
            this.property(DynamicAnimation.TRANSLATION_X, n, array);
            return this;
        }
        
        public PhysicsPropertyAnimator translationY(final float f, final float n, final Runnable... array) {
            this.mInitialPropertyValues.put(DynamicAnimation.TRANSLATION_Y, f);
            this.translationY(n, array);
            return this;
        }
        
        public PhysicsPropertyAnimator translationY(final float n, final Runnable... array) {
            this.mPathAnimator = null;
            this.property(DynamicAnimation.TRANSLATION_Y, n, array);
            return this;
        }
        
        public PhysicsPropertyAnimator withDampingRatio(final float mDampingRatio) {
            this.mDampingRatio = mDampingRatio;
            return this;
        }
        
        public PhysicsPropertyAnimator withPositionStartVelocities(final float f, final float f2) {
            this.mPositionStartVelocities.put(DynamicAnimation.TRANSLATION_X, f);
            this.mPositionStartVelocities.put(DynamicAnimation.TRANSLATION_Y, f2);
            return this;
        }
        
        public PhysicsPropertyAnimator withStartDelay(final long mStartDelay) {
            this.mStartDelay = mStartDelay;
            return this;
        }
        
        public PhysicsPropertyAnimator withStiffness(final float mStiffness) {
            this.mStiffness = mStiffness;
            return this;
        }
    }
}
