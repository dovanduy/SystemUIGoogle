// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.transition;

import android.transition.Transition$EpicenterCallback;
import android.animation.AnimatorSet;
import android.animation.Animator;
import android.transition.Transition;
import android.transition.Transition$TransitionListener;
import android.transition.TransitionValues;
import android.content.res.TypedArray;
import androidx.leanback.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.transition.Fade;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.animation.TimeInterpolator;
import android.transition.Visibility;

public class FadeAndShortSlide extends Visibility
{
    static final CalculateSlide sCalculateBottom;
    static final CalculateSlide sCalculateEnd;
    static final CalculateSlide sCalculateStart;
    static final CalculateSlide sCalculateStartEnd;
    static final CalculateSlide sCalculateTop;
    private static final TimeInterpolator sDecelerate;
    private float mDistance;
    private Visibility mFade;
    private CalculateSlide mSlideCalculator;
    final CalculateSlide sCalculateTopBottom;
    
    static {
        sDecelerate = (TimeInterpolator)new DecelerateInterpolator();
        sCalculateStart = (CalculateSlide)new CalculateSlide() {
            public float getGoneX(final FadeAndShortSlide fadeAndShortSlide, final ViewGroup viewGroup, final View view, final int[] array) {
                final int layoutDirection = viewGroup.getLayoutDirection();
                boolean b = true;
                if (layoutDirection != 1) {
                    b = false;
                }
                float n;
                if (b) {
                    n = view.getTranslationX() + fadeAndShortSlide.getHorizontalDistance(viewGroup);
                }
                else {
                    n = view.getTranslationX() - fadeAndShortSlide.getHorizontalDistance(viewGroup);
                }
                return n;
            }
        };
        sCalculateEnd = (CalculateSlide)new CalculateSlide() {
            public float getGoneX(final FadeAndShortSlide fadeAndShortSlide, final ViewGroup viewGroup, final View view, final int[] array) {
                final int layoutDirection = viewGroup.getLayoutDirection();
                boolean b = true;
                if (layoutDirection != 1) {
                    b = false;
                }
                float n;
                if (b) {
                    n = view.getTranslationX() - fadeAndShortSlide.getHorizontalDistance(viewGroup);
                }
                else {
                    n = view.getTranslationX() + fadeAndShortSlide.getHorizontalDistance(viewGroup);
                }
                return n;
            }
        };
        sCalculateStartEnd = (CalculateSlide)new CalculateSlide() {
            public float getGoneX(final FadeAndShortSlide fadeAndShortSlide, final ViewGroup viewGroup, final View view, final int[] array) {
                final int n = array[0];
                final int n2 = view.getWidth() / 2;
                viewGroup.getLocationOnScreen(array);
                final Rect epicenter = fadeAndShortSlide.getEpicenter();
                int centerX;
                if (epicenter == null) {
                    centerX = array[0] + viewGroup.getWidth() / 2;
                }
                else {
                    centerX = epicenter.centerX();
                }
                if (n + n2 < centerX) {
                    return view.getTranslationX() - fadeAndShortSlide.getHorizontalDistance(viewGroup);
                }
                return view.getTranslationX() + fadeAndShortSlide.getHorizontalDistance(viewGroup);
            }
        };
        sCalculateBottom = (CalculateSlide)new CalculateSlide() {
            public float getGoneY(final FadeAndShortSlide fadeAndShortSlide, final ViewGroup viewGroup, final View view, final int[] array) {
                return view.getTranslationY() + fadeAndShortSlide.getVerticalDistance(viewGroup);
            }
        };
        sCalculateTop = (CalculateSlide)new CalculateSlide() {
            public float getGoneY(final FadeAndShortSlide fadeAndShortSlide, final ViewGroup viewGroup, final View view, final int[] array) {
                return view.getTranslationY() - fadeAndShortSlide.getVerticalDistance(viewGroup);
            }
        };
    }
    
    public FadeAndShortSlide() {
        this(8388611);
    }
    
    public FadeAndShortSlide(final int slideEdge) {
        this.mFade = (Visibility)new Fade();
        this.mDistance = -1.0f;
        this.sCalculateTopBottom = (CalculateSlide)new CalculateSlide() {
            public float getGoneY(final FadeAndShortSlide fadeAndShortSlide, final ViewGroup viewGroup, final View view, final int[] array) {
                final int n = array[1];
                final int n2 = view.getHeight() / 2;
                viewGroup.getLocationOnScreen(array);
                final Rect epicenter = FadeAndShortSlide.this.getEpicenter();
                int centerY;
                if (epicenter == null) {
                    centerY = array[1] + viewGroup.getHeight() / 2;
                }
                else {
                    centerY = epicenter.centerY();
                }
                if (n + n2 < centerY) {
                    return view.getTranslationY() - fadeAndShortSlide.getVerticalDistance(viewGroup);
                }
                return view.getTranslationY() + fadeAndShortSlide.getVerticalDistance(viewGroup);
            }
        };
        this.setSlideEdge(slideEdge);
    }
    
    public FadeAndShortSlide(final Context context, final AttributeSet set) {
        super(context, set);
        this.mFade = (Visibility)new Fade();
        this.mDistance = -1.0f;
        this.sCalculateTopBottom = (CalculateSlide)new CalculateSlide() {
            public float getGoneY(final FadeAndShortSlide fadeAndShortSlide, final ViewGroup viewGroup, final View view, final int[] array) {
                final int n = array[1];
                final int n2 = view.getHeight() / 2;
                viewGroup.getLocationOnScreen(array);
                final Rect epicenter = FadeAndShortSlide.this.getEpicenter();
                int centerY;
                if (epicenter == null) {
                    centerY = array[1] + viewGroup.getHeight() / 2;
                }
                else {
                    centerY = epicenter.centerY();
                }
                if (n + n2 < centerY) {
                    return view.getTranslationY() - fadeAndShortSlide.getVerticalDistance(viewGroup);
                }
                return view.getTranslationY() + fadeAndShortSlide.getVerticalDistance(viewGroup);
            }
        };
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.lbSlide);
        this.setSlideEdge(obtainStyledAttributes.getInt(R$styleable.lbSlide_lb_slideEdge, 8388611));
        obtainStyledAttributes.recycle();
    }
    
    private void captureValues(final TransitionValues transitionValues) {
        final View view = transitionValues.view;
        final int[] array = new int[2];
        view.getLocationOnScreen(array);
        transitionValues.values.put("android:fadeAndShortSlideTransition:screenPosition", array);
    }
    
    public Transition addListener(final Transition$TransitionListener transition$TransitionListener) {
        this.mFade.addListener(transition$TransitionListener);
        return super.addListener(transition$TransitionListener);
    }
    
    public void captureEndValues(final TransitionValues transitionValues) {
        this.mFade.captureEndValues(transitionValues);
        super.captureEndValues(transitionValues);
        this.captureValues(transitionValues);
    }
    
    public void captureStartValues(final TransitionValues transitionValues) {
        this.mFade.captureStartValues(transitionValues);
        super.captureStartValues(transitionValues);
        this.captureValues(transitionValues);
    }
    
    public Transition clone() {
        final FadeAndShortSlide fadeAndShortSlide = (FadeAndShortSlide)super.clone();
        fadeAndShortSlide.mFade = (Visibility)this.mFade.clone();
        return (Transition)fadeAndShortSlide;
    }
    
    float getHorizontalDistance(final ViewGroup viewGroup) {
        float mDistance = this.mDistance;
        if (mDistance < 0.0f) {
            mDistance = (float)(viewGroup.getWidth() / 4);
        }
        return mDistance;
    }
    
    float getVerticalDistance(final ViewGroup viewGroup) {
        float mDistance = this.mDistance;
        if (mDistance < 0.0f) {
            mDistance = (float)(viewGroup.getHeight() / 4);
        }
        return mDistance;
    }
    
    public Animator onAppear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        if (transitionValues2 == null) {
            return null;
        }
        if (viewGroup == view) {
            return null;
        }
        final int[] array = transitionValues2.values.get("android:fadeAndShortSlideTransition:screenPosition");
        final Animator animation = TranslationAnimationCreator.createAnimation(view, transitionValues2, array[0], array[1], this.mSlideCalculator.getGoneX(this, viewGroup, view, array), this.mSlideCalculator.getGoneY(this, viewGroup, view, array), view.getTranslationX(), view.getTranslationY(), FadeAndShortSlide.sDecelerate, (Transition)this);
        final Animator onAppear = this.mFade.onAppear(viewGroup, view, transitionValues, transitionValues2);
        if (animation == null) {
            return onAppear;
        }
        if (onAppear == null) {
            return animation;
        }
        final AnimatorSet set = new AnimatorSet();
        set.play(animation).with(onAppear);
        return (Animator)set;
    }
    
    public Animator onDisappear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        if (transitionValues == null) {
            return null;
        }
        if (viewGroup == view) {
            return null;
        }
        final int[] array = transitionValues.values.get("android:fadeAndShortSlideTransition:screenPosition");
        final Animator animation = TranslationAnimationCreator.createAnimation(view, transitionValues, array[0], array[1], view.getTranslationX(), view.getTranslationY(), this.mSlideCalculator.getGoneX(this, viewGroup, view, array), this.mSlideCalculator.getGoneY(this, viewGroup, view, array), FadeAndShortSlide.sDecelerate, (Transition)this);
        final Animator onDisappear = this.mFade.onDisappear(viewGroup, view, transitionValues, transitionValues2);
        if (animation == null) {
            return onDisappear;
        }
        if (onDisappear == null) {
            return animation;
        }
        final AnimatorSet set = new AnimatorSet();
        set.play(animation).with(onDisappear);
        return (Animator)set;
    }
    
    public Transition removeListener(final Transition$TransitionListener transition$TransitionListener) {
        this.mFade.removeListener(transition$TransitionListener);
        return super.removeListener(transition$TransitionListener);
    }
    
    public void setEpicenterCallback(final Transition$EpicenterCallback transition$EpicenterCallback) {
        this.mFade.setEpicenterCallback(transition$EpicenterCallback);
        super.setEpicenterCallback(transition$EpicenterCallback);
    }
    
    public void setSlideEdge(final int n) {
        if (n != 48) {
            if (n != 80) {
                if (n != 112) {
                    if (n != 8388611) {
                        if (n != 8388613) {
                            if (n != 8388615) {
                                throw new IllegalArgumentException("Invalid slide direction");
                            }
                            this.mSlideCalculator = FadeAndShortSlide.sCalculateStartEnd;
                        }
                        else {
                            this.mSlideCalculator = FadeAndShortSlide.sCalculateEnd;
                        }
                    }
                    else {
                        this.mSlideCalculator = FadeAndShortSlide.sCalculateStart;
                    }
                }
                else {
                    this.mSlideCalculator = this.sCalculateTopBottom;
                }
            }
            else {
                this.mSlideCalculator = FadeAndShortSlide.sCalculateBottom;
            }
        }
        else {
            this.mSlideCalculator = FadeAndShortSlide.sCalculateTop;
        }
    }
    
    private abstract static class CalculateSlide
    {
        CalculateSlide() {
        }
        
        float getGoneX(final FadeAndShortSlide fadeAndShortSlide, final ViewGroup viewGroup, final View view, final int[] array) {
            return view.getTranslationX();
        }
        
        float getGoneY(final FadeAndShortSlide fadeAndShortSlide, final ViewGroup viewGroup, final View view, final int[] array) {
            return view.getTranslationY();
        }
    }
}
