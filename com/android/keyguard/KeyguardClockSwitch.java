// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.util.MathUtils;
import android.animation.Animator$AnimatorListener;
import com.android.systemui.util.wakelock.KeepAwakeAnimationListener;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.ValueAnimator;
import android.animation.Animator;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.content.res.Resources;
import android.transition.Transition$TransitionListener;
import android.transition.TransitionListenerAdapter;
import com.android.systemui.R$dimen;
import android.transition.TransitionManager;
import android.transition.Fade;
import android.util.Log;
import android.os.Build;
import java.util.TimeZone;
import android.graphics.Paint;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.internal.colorextraction.ColorExtractor$GradientColors;
import android.view.ViewParent;
import android.view.ViewGroup$LayoutParams;
import com.android.internal.colorextraction.ColorExtractor;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.transition.TransitionSet;
import com.android.systemui.R$id;
import android.util.AttributeSet;
import android.content.Context;
import android.transition.Transition;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.widget.FrameLayout;
import android.view.View;
import com.android.internal.colorextraction.ColorExtractor$OnColorsChangedListener;
import android.widget.TextClock;
import com.android.systemui.plugins.ClockPlugin;
import com.android.keyguard.clock.ClockManager;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class KeyguardClockSwitch extends RelativeLayout
{
    private ViewGroup mBigClockContainer;
    private final ClockVisibilityTransition mBoldClockTransition;
    private ClockManager.ClockChangedListener mClockChangedListener;
    private final ClockManager mClockManager;
    private ClockPlugin mClockPlugin;
    private final ClockVisibilityTransition mClockTransition;
    private TextClock mClockView;
    private TextClock mClockViewBold;
    private int[] mColorPalette;
    private final ColorExtractor$OnColorsChangedListener mColorsListener;
    private float mDarkAmount;
    private boolean mHasVisibleNotifications;
    private View mKeyguardStatusArea;
    private boolean mShowingHeader;
    private FrameLayout mSmallClockFrame;
    private final StatusBarStateController.StateListener mStateListener;
    private int mStatusBarState;
    private final StatusBarStateController mStatusBarStateController;
    private boolean mSupportsDarkText;
    private final SysuiColorExtractor mSysuiColorExtractor;
    private final Transition mTransition;
    
    public KeyguardClockSwitch(final Context context, final AttributeSet set, final StatusBarStateController mStatusBarStateController, final SysuiColorExtractor mSysuiColorExtractor, final ClockManager mClockManager) {
        super(context, set);
        this.mStateListener = new StatusBarStateController.StateListener() {
            @Override
            public void onStateChanged(final int n) {
                KeyguardClockSwitch.this.mStatusBarState = n;
                KeyguardClockSwitch.this.updateBigClockVisibility();
            }
        };
        this.mClockChangedListener = new _$$Lambda$KeyguardClockSwitch$H31kNGqlEfE_tZQZgrBtirdKZKc(this);
        this.mColorsListener = (ColorExtractor$OnColorsChangedListener)new _$$Lambda$KeyguardClockSwitch$1K4q2TFTethGttjK4WWfYw_lPoo(this);
        this.mStatusBarStateController = mStatusBarStateController;
        this.mStatusBarState = mStatusBarStateController.getState();
        this.mSysuiColorExtractor = mSysuiColorExtractor;
        this.mClockManager = mClockManager;
        final ClockVisibilityTransition mClockTransition = new ClockVisibilityTransition();
        mClockTransition.setCutoff(0.3f);
        (this.mClockTransition = mClockTransition).addTarget(R$id.default_clock_view);
        final ClockVisibilityTransition mBoldClockTransition = new ClockVisibilityTransition();
        mBoldClockTransition.setCutoff(0.7f);
        (this.mBoldClockTransition = mBoldClockTransition).addTarget(R$id.default_clock_view_bold);
        this.mTransition = (Transition)new TransitionSet().setOrdering(0).addTransition((Transition)this.mClockTransition).addTransition((Transition)this.mBoldClockTransition).setDuration(275L).setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
    }
    
    private void setClockPlugin(final ClockPlugin mClockPlugin) {
        final ClockPlugin mClockPlugin2 = this.mClockPlugin;
        if (mClockPlugin2 != null) {
            final View view = mClockPlugin2.getView();
            if (view != null) {
                final ViewParent parent = view.getParent();
                final FrameLayout mSmallClockFrame = this.mSmallClockFrame;
                if (parent == mSmallClockFrame) {
                    mSmallClockFrame.removeView(view);
                }
            }
            final ViewGroup mBigClockContainer = this.mBigClockContainer;
            if (mBigClockContainer != null) {
                mBigClockContainer.removeAllViews();
                this.updateBigClockVisibility();
            }
            this.mClockPlugin.onDestroyView();
            this.mClockPlugin = null;
        }
        if (mClockPlugin == null) {
            if (this.mShowingHeader) {
                this.mClockView.setVisibility(8);
                this.mClockViewBold.setVisibility(0);
            }
            else {
                this.mClockView.setVisibility(0);
                this.mClockViewBold.setVisibility(4);
            }
            this.mKeyguardStatusArea.setVisibility(0);
            return;
        }
        final View view2 = mClockPlugin.getView();
        if (view2 != null) {
            this.mSmallClockFrame.addView(view2, -1, new ViewGroup$LayoutParams(-1, -2));
            this.mClockView.setVisibility(8);
            this.mClockViewBold.setVisibility(8);
        }
        final View bigClockView = mClockPlugin.getBigClockView();
        if (bigClockView != null) {
            final ViewGroup mBigClockContainer2 = this.mBigClockContainer;
            if (mBigClockContainer2 != null) {
                mBigClockContainer2.addView(bigClockView);
                this.updateBigClockVisibility();
            }
        }
        if (!mClockPlugin.shouldShowStatusArea()) {
            this.mKeyguardStatusArea.setVisibility(8);
        }
        (this.mClockPlugin = mClockPlugin).setStyle(this.getPaint().getStyle());
        this.mClockPlugin.setTextColor(this.getCurrentTextColor());
        this.mClockPlugin.setDarkAmount(this.mDarkAmount);
        final int[] mColorPalette = this.mColorPalette;
        if (mColorPalette != null) {
            this.mClockPlugin.setColorPalette(this.mSupportsDarkText, mColorPalette);
        }
    }
    
    private void updateBigClockAlpha() {
        if (this.mBigClockContainer != null) {
            float mDarkAmount;
            if (this.mHasVisibleNotifications) {
                mDarkAmount = this.mDarkAmount;
            }
            else {
                mDarkAmount = 1.0f;
            }
            this.mBigClockContainer.setAlpha(mDarkAmount);
            if (mDarkAmount == 0.0f) {
                this.mBigClockContainer.setVisibility(4);
            }
            else if (this.mBigClockContainer.getVisibility() == 4) {
                this.mBigClockContainer.setVisibility(0);
            }
        }
    }
    
    private void updateBigClockVisibility() {
        if (this.mBigClockContainer == null) {
            return;
        }
        final int mStatusBarState = this.mStatusBarState;
        final int n = 0;
        int n2 = 1;
        if (mStatusBarState != 1) {
            if (mStatusBarState == 2) {
                n2 = n2;
            }
            else {
                n2 = 0;
            }
        }
        int visibility;
        if (n2 != 0 && this.mBigClockContainer.getChildCount() != 0) {
            visibility = n;
        }
        else {
            visibility = 8;
        }
        if (this.mBigClockContainer.getVisibility() != visibility) {
            this.mBigClockContainer.setVisibility(visibility);
        }
    }
    
    private void updateColors() {
        final ColorExtractor$GradientColors colors = this.mSysuiColorExtractor.getColors(2);
        this.mSupportsDarkText = colors.supportsDarkText();
        final int[] colorPalette = colors.getColorPalette();
        this.mColorPalette = colorPalette;
        final ClockPlugin mClockPlugin = this.mClockPlugin;
        if (mClockPlugin != null) {
            mClockPlugin.setColorPalette(this.mSupportsDarkText, colorPalette);
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("KeyguardClockSwitch:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mClockPlugin: ");
        sb.append(this.mClockPlugin);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mClockView: ");
        sb2.append(this.mClockView);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mClockViewBold: ");
        sb3.append(this.mClockViewBold);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mSmallClockFrame: ");
        sb4.append(this.mSmallClockFrame);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  mBigClockContainer: ");
        sb5.append(this.mBigClockContainer);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  mKeyguardStatusArea: ");
        sb6.append(this.mKeyguardStatusArea);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append("  mDarkAmount: ");
        sb7.append(this.mDarkAmount);
        printWriter.println(sb7.toString());
        final StringBuilder sb8 = new StringBuilder();
        sb8.append("  mShowingHeader: ");
        sb8.append(this.mShowingHeader);
        printWriter.println(sb8.toString());
        final StringBuilder sb9 = new StringBuilder();
        sb9.append("  mSupportsDarkText: ");
        sb9.append(this.mSupportsDarkText);
        printWriter.println(sb9.toString());
        final StringBuilder sb10 = new StringBuilder();
        sb10.append("  mColorPalette: ");
        sb10.append(Arrays.toString(this.mColorPalette));
        printWriter.println(sb10.toString());
    }
    
    ClockManager.ClockChangedListener getClockChangedListener() {
        return this.mClockChangedListener;
    }
    
    public int getCurrentTextColor() {
        return this.mClockView.getCurrentTextColor();
    }
    
    public Paint getPaint() {
        return (Paint)this.mClockView.getPaint();
    }
    
    int getPreferredY(final int n) {
        final ClockPlugin mClockPlugin = this.mClockPlugin;
        if (mClockPlugin != null) {
            return mClockPlugin.getPreferredY(n);
        }
        return n / 2;
    }
    
    StatusBarStateController.StateListener getStateListener() {
        return this.mStateListener;
    }
    
    public float getTextSize() {
        return this.mClockView.getTextSize();
    }
    
    public boolean hasCustomClock() {
        return this.mClockPlugin != null;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mClockManager.addOnClockChangedListener(this.mClockChangedListener);
        this.mStatusBarStateController.addCallback(this.mStateListener);
        this.mSysuiColorExtractor.addOnColorsChangedListener(this.mColorsListener);
        this.updateColors();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mClockManager.removeOnClockChangedListener(this.mClockChangedListener);
        this.mStatusBarStateController.removeCallback(this.mStateListener);
        this.mSysuiColorExtractor.removeOnColorsChangedListener(this.mColorsListener);
        this.setClockPlugin(null);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mClockView = (TextClock)this.findViewById(R$id.default_clock_view);
        this.mClockViewBold = (TextClock)this.findViewById(R$id.default_clock_view_bold);
        this.mSmallClockFrame = (FrameLayout)this.findViewById(R$id.clock_view);
        this.mKeyguardStatusArea = this.findViewById(R$id.keyguard_status_area);
    }
    
    public void onTimeZoneChanged(final TimeZone timeZone) {
        final ClockPlugin mClockPlugin = this.mClockPlugin;
        if (mClockPlugin != null) {
            mClockPlugin.onTimeZoneChanged(timeZone);
        }
    }
    
    public void refresh() {
        this.mClockView.refreshTime();
        this.mClockViewBold.refreshTime();
        final ClockPlugin mClockPlugin = this.mClockPlugin;
        if (mClockPlugin != null) {
            mClockPlugin.onTimeTick();
        }
        if (Build.IS_DEBUGGABLE) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Updating clock: ");
            sb.append((Object)this.mClockView.getText());
            Log.d("KeyguardClockSwitch", sb.toString());
        }
    }
    
    public void setBigClockContainer(final ViewGroup mBigClockContainer) {
        final ClockPlugin mClockPlugin = this.mClockPlugin;
        if (mClockPlugin != null && mBigClockContainer != null) {
            final View bigClockView = mClockPlugin.getBigClockView();
            if (bigClockView != null) {
                mBigClockContainer.addView(bigClockView);
            }
        }
        this.mBigClockContainer = mBigClockContainer;
        this.updateBigClockVisibility();
    }
    
    public void setDarkAmount(final float n) {
        this.mDarkAmount = n;
        final ClockPlugin mClockPlugin = this.mClockPlugin;
        if (mClockPlugin != null) {
            mClockPlugin.setDarkAmount(n);
        }
        this.updateBigClockAlpha();
    }
    
    public void setFormat12Hour(final CharSequence charSequence) {
        this.mClockView.setFormat12Hour(charSequence);
        this.mClockViewBold.setFormat12Hour(charSequence);
    }
    
    public void setFormat24Hour(final CharSequence charSequence) {
        this.mClockView.setFormat24Hour(charSequence);
        this.mClockViewBold.setFormat24Hour(charSequence);
    }
    
    void setHasVisibleNotifications(final boolean mHasVisibleNotifications) {
        if (mHasVisibleNotifications == this.mHasVisibleNotifications) {
            return;
        }
        this.mHasVisibleNotifications = mHasVisibleNotifications;
        if (this.mDarkAmount == 0.0f) {
            final ViewGroup mBigClockContainer = this.mBigClockContainer;
            if (mBigClockContainer != null) {
                TransitionManager.beginDelayedTransition(mBigClockContainer, new Fade().setDuration(275L).addTarget((View)this.mBigClockContainer));
            }
        }
        this.updateBigClockAlpha();
    }
    
    void setKeyguardShowingHeader(final boolean mShowingHeader) {
        if (this.mShowingHeader == mShowingHeader) {
            return;
        }
        this.mShowingHeader = mShowingHeader;
        if (this.hasCustomClock()) {
            return;
        }
        final float n = (float)super.mContext.getResources().getDimensionPixelSize(R$dimen.widget_small_font_size);
        final float n2 = (float)super.mContext.getResources().getDimensionPixelSize(R$dimen.widget_big_font_size);
        this.mClockTransition.setScale(n / n2);
        this.mBoldClockTransition.setScale(n2 / n);
        TransitionManager.endTransitions((ViewGroup)this.mClockView.getParent());
        if (mShowingHeader) {
            this.mTransition.addListener((Transition$TransitionListener)new TransitionListenerAdapter() {
                public void onTransitionEnd(final Transition transition) {
                    super.onTransitionEnd(transition);
                    if (KeyguardClockSwitch.this.mShowingHeader) {
                        KeyguardClockSwitch.this.mClockView.setVisibility(8);
                    }
                    transition.removeListener((Transition$TransitionListener)this);
                }
            });
        }
        TransitionManager.beginDelayedTransition((ViewGroup)this.mClockView.getParent(), this.mTransition);
        final TextClock mClockView = this.mClockView;
        final int n3 = 4;
        int visibility;
        if (mShowingHeader) {
            visibility = 4;
        }
        else {
            visibility = 0;
        }
        mClockView.setVisibility(visibility);
        final TextClock mClockViewBold = this.mClockViewBold;
        int visibility2 = n3;
        if (mShowingHeader) {
            visibility2 = 0;
        }
        mClockViewBold.setVisibility(visibility2);
        final Resources resources = super.mContext.getResources();
        int n4;
        if (mShowingHeader) {
            n4 = R$dimen.widget_vertical_padding_clock;
        }
        else {
            n4 = R$dimen.title_clock_padding;
        }
        final int dimensionPixelSize = resources.getDimensionPixelSize(n4);
        final TextClock mClockView2 = this.mClockView;
        mClockView2.setPadding(mClockView2.getPaddingLeft(), this.mClockView.getPaddingTop(), this.mClockView.getPaddingRight(), dimensionPixelSize);
        final TextClock mClockViewBold2 = this.mClockViewBold;
        mClockViewBold2.setPadding(mClockViewBold2.getPaddingLeft(), this.mClockViewBold.getPaddingTop(), this.mClockViewBold.getPaddingRight(), dimensionPixelSize);
    }
    
    public void setShowCurrentUserTime(final boolean b) {
        this.mClockView.setShowCurrentUserTime(b);
        this.mClockViewBold.setShowCurrentUserTime(b);
    }
    
    public void setTextColor(final int textColor) {
        this.mClockView.setTextColor(textColor);
        this.mClockViewBold.setTextColor(textColor);
        final ClockPlugin mClockPlugin = this.mClockPlugin;
        if (mClockPlugin != null) {
            mClockPlugin.setTextColor(textColor);
        }
    }
    
    public void setTextSize(final int n, final float n2) {
        this.mClockView.setTextSize(n, n2);
    }
    
    private class ClockVisibilityTransition extends Visibility
    {
        private float mCutoff;
        private float mScale;
        
        ClockVisibilityTransition() {
            this.setCutoff(1.0f);
            this.setScale(1.0f);
        }
        
        private void captureVisibility(final TransitionValues transitionValues) {
            transitionValues.values.put("systemui:keyguard:visibility", transitionValues.view.getVisibility());
        }
        
        private Animator createAnimator(final View view, final float n, final int n2, final int n3, final float n4, final float n5) {
            view.setPivotY((float)(view.getHeight() - view.getPaddingBottom()));
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
            ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$KeyguardClockSwitch$ClockVisibilityTransition$0YYk1dKss121y1dzD6OuOcSJduA(n, view, n3, n4, n5));
            ofFloat.addListener((Animator$AnimatorListener)new KeepAwakeAnimationListener(this, KeyguardClockSwitch.this.getContext()) {
                @Override
                public void onAnimationEnd(final Animator animator) {
                    super.onAnimationEnd(animator);
                    animator.removeListener((Animator$AnimatorListener)this);
                }
                
                @Override
                public void onAnimationStart(final Animator animator) {
                    super.onAnimationStart(animator);
                    view.setVisibility(n2);
                }
            });
            this.addListener((Transition$TransitionListener)new TransitionListenerAdapter(this) {
                public void onTransitionEnd(final Transition transition) {
                    view.setVisibility(n3);
                    view.setScaleX(1.0f);
                    view.setScaleY(1.0f);
                    transition.removeListener((Transition$TransitionListener)this);
                }
            });
            return (Animator)ofFloat;
        }
        
        public void captureEndValues(final TransitionValues transitionValues) {
            super.captureStartValues(transitionValues);
            this.captureVisibility(transitionValues);
        }
        
        public void captureStartValues(final TransitionValues transitionValues) {
            super.captureStartValues(transitionValues);
            this.captureVisibility(transitionValues);
        }
        
        public Animator onAppear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
            if (!viewGroup.isShown()) {
                return null;
            }
            return this.createAnimator(view, this.mCutoff, 4, transitionValues2.values.get("systemui:keyguard:visibility"), this.mScale, 1.0f);
        }
        
        public Animator onDisappear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
            if (!viewGroup.isShown()) {
                return null;
            }
            return this.createAnimator(view, 1.0f - this.mCutoff, 0, transitionValues2.values.get("systemui:keyguard:visibility"), 1.0f, this.mScale);
        }
        
        public ClockVisibilityTransition setCutoff(final float mCutoff) {
            this.mCutoff = mCutoff;
            return this;
        }
        
        public ClockVisibilityTransition setScale(final float mScale) {
            this.mScale = mScale;
            return this;
        }
    }
}
