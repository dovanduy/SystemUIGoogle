// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.transition;

import android.animation.AnimatorListenerAdapter;
import android.transition.TransitionValues;
import android.view.ViewGroup;
import android.animation.Animator$AnimatorPauseListener;
import android.animation.Animator$AnimatorListener;
import android.animation.ObjectAnimator;
import androidx.leanback.R$id;
import android.animation.Animator;
import android.util.Property;
import android.content.res.TypedArray;
import android.view.animation.AnimationUtils;
import androidx.leanback.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.animation.TimeInterpolator;
import android.transition.Visibility;

class SlideKitkat extends Visibility
{
    private static final TimeInterpolator sAccelerate;
    private static final CalculateSlide sCalculateBottom;
    private static final CalculateSlide sCalculateEnd;
    private static final CalculateSlide sCalculateLeft;
    private static final CalculateSlide sCalculateRight;
    private static final CalculateSlide sCalculateStart;
    private static final CalculateSlide sCalculateTop;
    private static final TimeInterpolator sDecelerate;
    private CalculateSlide mSlideCalculator;
    
    static {
        sDecelerate = (TimeInterpolator)new DecelerateInterpolator();
        sAccelerate = (TimeInterpolator)new AccelerateInterpolator();
        sCalculateLeft = (CalculateSlide)new CalculateSlideHorizontal() {
            @Override
            public float getGone(final View view) {
                return view.getTranslationX() - view.getWidth();
            }
        };
        sCalculateTop = (CalculateSlide)new CalculateSlideVertical() {
            @Override
            public float getGone(final View view) {
                return view.getTranslationY() - view.getHeight();
            }
        };
        sCalculateRight = (CalculateSlide)new CalculateSlideHorizontal() {
            @Override
            public float getGone(final View view) {
                return view.getTranslationX() + view.getWidth();
            }
        };
        sCalculateBottom = (CalculateSlide)new CalculateSlideVertical() {
            @Override
            public float getGone(final View view) {
                return view.getTranslationY() + view.getHeight();
            }
        };
        sCalculateStart = (CalculateSlide)new CalculateSlideHorizontal() {
            @Override
            public float getGone(final View view) {
                if (view.getLayoutDirection() == 1) {
                    return view.getTranslationX() + view.getWidth();
                }
                return view.getTranslationX() - view.getWidth();
            }
        };
        sCalculateEnd = (CalculateSlide)new CalculateSlideHorizontal() {
            @Override
            public float getGone(final View view) {
                if (view.getLayoutDirection() == 1) {
                    return view.getTranslationX() - view.getWidth();
                }
                return view.getTranslationX() + view.getWidth();
            }
        };
    }
    
    public SlideKitkat() {
        this.setSlideEdge(80);
    }
    
    public SlideKitkat(final Context context, final AttributeSet set) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.lbSlide);
        this.setSlideEdge(obtainStyledAttributes.getInt(R$styleable.lbSlide_lb_slideEdge, 80));
        final long duration = obtainStyledAttributes.getInt(R$styleable.lbSlide_android_duration, -1);
        if (duration >= 0L) {
            this.setDuration(duration);
        }
        final long startDelay = obtainStyledAttributes.getInt(R$styleable.lbSlide_android_startDelay, -1);
        if (startDelay > 0L) {
            this.setStartDelay(startDelay);
        }
        final int resourceId = obtainStyledAttributes.getResourceId(R$styleable.lbSlide_android_interpolator, 0);
        if (resourceId > 0) {
            this.setInterpolator((TimeInterpolator)AnimationUtils.loadInterpolator(context, resourceId));
        }
        obtainStyledAttributes.recycle();
    }
    
    private Animator createAnimation(final View view, final Property<View, Float> property, float n, final float n2, final float n3, final TimeInterpolator interpolator, final int n4) {
        final float[] array = (float[])view.getTag(R$id.lb_slide_transition_value);
        if (array != null) {
            if (View.TRANSLATION_Y == property) {
                n = array[1];
            }
            else {
                n = array[0];
            }
            view.setTag(R$id.lb_slide_transition_value, (Object)null);
        }
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, (Property)property, new float[] { n, n2 });
        final SlideAnimatorListener slideAnimatorListener = new SlideAnimatorListener(view, property, n3, n2, n4);
        ofFloat.addListener((Animator$AnimatorListener)slideAnimatorListener);
        ofFloat.addPauseListener((Animator$AnimatorPauseListener)slideAnimatorListener);
        ofFloat.setInterpolator(interpolator);
        return (Animator)ofFloat;
    }
    
    public Animator onAppear(final ViewGroup viewGroup, final TransitionValues transitionValues, final int n, final TransitionValues transitionValues2, final int n2) {
        View view;
        if (transitionValues2 != null) {
            view = transitionValues2.view;
        }
        else {
            view = null;
        }
        if (view == null) {
            return null;
        }
        final float here = this.mSlideCalculator.getHere(view);
        return this.createAnimation(view, this.mSlideCalculator.getProperty(), this.mSlideCalculator.getGone(view), here, here, SlideKitkat.sDecelerate, 0);
    }
    
    public Animator onDisappear(final ViewGroup viewGroup, final TransitionValues transitionValues, final int n, final TransitionValues transitionValues2, final int n2) {
        View view;
        if (transitionValues != null) {
            view = transitionValues.view;
        }
        else {
            view = null;
        }
        if (view == null) {
            return null;
        }
        final float here = this.mSlideCalculator.getHere(view);
        return this.createAnimation(view, this.mSlideCalculator.getProperty(), here, this.mSlideCalculator.getGone(view), here, SlideKitkat.sAccelerate, 4);
    }
    
    public void setSlideEdge(final int n) {
        if (n != 3) {
            if (n != 5) {
                if (n != 48) {
                    if (n != 80) {
                        if (n != 8388611) {
                            if (n != 8388613) {
                                throw new IllegalArgumentException("Invalid slide direction");
                            }
                            this.mSlideCalculator = SlideKitkat.sCalculateEnd;
                        }
                        else {
                            this.mSlideCalculator = SlideKitkat.sCalculateStart;
                        }
                    }
                    else {
                        this.mSlideCalculator = SlideKitkat.sCalculateBottom;
                    }
                }
                else {
                    this.mSlideCalculator = SlideKitkat.sCalculateTop;
                }
            }
            else {
                this.mSlideCalculator = SlideKitkat.sCalculateRight;
            }
        }
        else {
            this.mSlideCalculator = SlideKitkat.sCalculateLeft;
        }
    }
    
    private interface CalculateSlide
    {
        float getGone(final View p0);
        
        float getHere(final View p0);
        
        Property<View, Float> getProperty();
    }
    
    private abstract static class CalculateSlideHorizontal implements CalculateSlide
    {
        CalculateSlideHorizontal() {
        }
        
        @Override
        public float getHere(final View view) {
            return view.getTranslationX();
        }
        
        @Override
        public Property<View, Float> getProperty() {
            return (Property<View, Float>)View.TRANSLATION_X;
        }
    }
    
    private abstract static class CalculateSlideVertical implements CalculateSlide
    {
        CalculateSlideVertical() {
        }
        
        @Override
        public float getHere(final View view) {
            return view.getTranslationY();
        }
        
        @Override
        public Property<View, Float> getProperty() {
            return (Property<View, Float>)View.TRANSLATION_Y;
        }
    }
    
    private static class SlideAnimatorListener extends AnimatorListenerAdapter
    {
        private boolean mCanceled;
        private final float mEndValue;
        private final int mFinalVisibility;
        private float mPausedValue;
        private final Property<View, Float> mProp;
        private final float mTerminalValue;
        private final View mView;
        
        public SlideAnimatorListener(final View mView, final Property<View, Float> mProp, final float mTerminalValue, final float mEndValue, final int mFinalVisibility) {
            this.mCanceled = false;
            this.mProp = mProp;
            this.mView = mView;
            this.mTerminalValue = mTerminalValue;
            this.mEndValue = mEndValue;
            this.mFinalVisibility = mFinalVisibility;
            mView.setVisibility(0);
        }
        
        public void onAnimationCancel(final Animator animator) {
            this.mView.setTag(R$id.lb_slide_transition_value, (Object)new float[] { this.mView.getTranslationX(), this.mView.getTranslationY() });
            this.mProp.set((Object)this.mView, (Object)this.mTerminalValue);
            this.mCanceled = true;
        }
        
        public void onAnimationEnd(final Animator animator) {
            if (!this.mCanceled) {
                this.mProp.set((Object)this.mView, (Object)this.mTerminalValue);
            }
            this.mView.setVisibility(this.mFinalVisibility);
        }
        
        public void onAnimationPause(final Animator animator) {
            this.mPausedValue = (float)this.mProp.get((Object)this.mView);
            this.mProp.set((Object)this.mView, (Object)this.mEndValue);
            this.mView.setVisibility(this.mFinalVisibility);
        }
        
        public void onAnimationResume(final Animator animator) {
            this.mProp.set((Object)this.mView, (Object)this.mPausedValue);
            this.mView.setVisibility(0);
        }
    }
}
