// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.ViewGroup$MarginLayoutParams;
import android.content.res.TypedArray;
import android.view.ViewDebug$IntToString;
import android.view.ViewDebug$ExportedProperty;
import android.view.animation.Transformation;
import android.widget.FrameLayout$LayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.view.View$MeasureSpec;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation$AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.DecelerateInterpolator;
import android.graphics.drawable.Drawable;
import androidx.leanback.R$integer;
import androidx.leanback.R$styleable;
import androidx.leanback.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import java.util.ArrayList;
import android.view.animation.Animation;
import android.widget.FrameLayout;

public class BaseCardView extends FrameLayout
{
    private static final int[] LB_PRESSED_STATE_SET;
    private final int mActivatedAnimDuration;
    private Animation mAnim;
    private final Runnable mAnimationTrigger;
    private int mCardType;
    private boolean mDelaySelectedAnim;
    ArrayList<View> mExtraViewList;
    private int mExtraVisibility;
    float mInfoAlpha;
    float mInfoOffset;
    ArrayList<View> mInfoViewList;
    float mInfoVisFraction;
    private int mInfoVisibility;
    private ArrayList<View> mMainViewList;
    private int mMeasuredHeight;
    private int mMeasuredWidth;
    private final int mSelectedAnimDuration;
    private int mSelectedAnimationDelay;
    
    static {
        LB_PRESSED_STATE_SET = new int[] { 16842919 };
    }
    
    public BaseCardView(final Context context, final AttributeSet set) {
        this(context, set, R$attr.baseCardViewStyle);
    }
    
    public BaseCardView(Context obtainStyledAttributes, final AttributeSet set, int integer) {
        super(obtainStyledAttributes, set, integer);
        this.mAnimationTrigger = new Runnable() {
            @Override
            public void run() {
                BaseCardView.this.animateInfoOffset(true);
            }
        };
        obtainStyledAttributes = (Context)obtainStyledAttributes.obtainStyledAttributes(set, R$styleable.lbBaseCardView, integer, 0);
        try {
            this.mCardType = ((TypedArray)obtainStyledAttributes).getInteger(R$styleable.lbBaseCardView_cardType, 0);
            final Drawable drawable = ((TypedArray)obtainStyledAttributes).getDrawable(R$styleable.lbBaseCardView_cardForeground);
            if (drawable != null) {
                this.setForeground(drawable);
            }
            final Drawable drawable2 = ((TypedArray)obtainStyledAttributes).getDrawable(R$styleable.lbBaseCardView_cardBackground);
            if (drawable2 != null) {
                this.setBackground(drawable2);
            }
            this.mInfoVisibility = ((TypedArray)obtainStyledAttributes).getInteger(R$styleable.lbBaseCardView_infoVisibility, 1);
            integer = ((TypedArray)obtainStyledAttributes).getInteger(R$styleable.lbBaseCardView_extraVisibility, 2);
            if ((this.mExtraVisibility = integer) < this.mInfoVisibility) {
                this.mExtraVisibility = this.mInfoVisibility;
            }
            this.mSelectedAnimationDelay = ((TypedArray)obtainStyledAttributes).getInteger(R$styleable.lbBaseCardView_selectedAnimationDelay, this.getResources().getInteger(R$integer.lb_card_selected_animation_delay));
            this.mSelectedAnimDuration = ((TypedArray)obtainStyledAttributes).getInteger(R$styleable.lbBaseCardView_selectedAnimationDuration, this.getResources().getInteger(R$integer.lb_card_selected_animation_duration));
            this.mActivatedAnimDuration = ((TypedArray)obtainStyledAttributes).getInteger(R$styleable.lbBaseCardView_activatedAnimationDuration, this.getResources().getInteger(R$integer.lb_card_activated_animation_duration));
            ((TypedArray)obtainStyledAttributes).recycle();
            this.mDelaySelectedAnim = true;
            this.mMainViewList = new ArrayList<View>();
            this.mInfoViewList = new ArrayList<View>();
            this.mExtraViewList = new ArrayList<View>();
            this.mInfoOffset = 0.0f;
            this.mInfoVisFraction = this.getFinalInfoVisFraction();
            this.mInfoAlpha = this.getFinalInfoAlpha();
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    private void animateInfoAlpha(final boolean b) {
        this.cancelAnimations();
        if (b) {
            for (int i = 0; i < this.mInfoViewList.size(); ++i) {
                this.mInfoViewList.get(i).setVisibility(0);
            }
        }
        final float n = 1.0f;
        float n2;
        if (b) {
            n2 = 1.0f;
        }
        else {
            n2 = 0.0f;
        }
        if (n2 == this.mInfoAlpha) {
            return;
        }
        final float mInfoAlpha = this.mInfoAlpha;
        float n3;
        if (b) {
            n3 = n;
        }
        else {
            n3 = 0.0f;
        }
        (this.mAnim = new InfoAlphaAnimation(mInfoAlpha, n3)).setDuration((long)this.mActivatedAnimDuration);
        this.mAnim.setInterpolator((Interpolator)new DecelerateInterpolator());
        this.mAnim.setAnimationListener((Animation$AnimationListener)new Animation$AnimationListener() {
            public void onAnimationEnd(final Animation animation) {
                if (BaseCardView.this.mInfoAlpha == 0.0) {
                    for (int i = 0; i < BaseCardView.this.mInfoViewList.size(); ++i) {
                        BaseCardView.this.mInfoViewList.get(i).setVisibility(8);
                    }
                }
            }
            
            public void onAnimationRepeat(final Animation animation) {
            }
            
            public void onAnimationStart(final Animation animation) {
            }
        });
        this.startAnimation(this.mAnim);
    }
    
    private void animateInfoHeight(final boolean b) {
        this.cancelAnimations();
        if (b) {
            for (int i = 0; i < this.mInfoViewList.size(); ++i) {
                this.mInfoViewList.get(i).setVisibility(0);
            }
        }
        float n;
        if (b) {
            n = 1.0f;
        }
        else {
            n = 0.0f;
        }
        if (this.mInfoVisFraction == n) {
            return;
        }
        (this.mAnim = new InfoHeightAnimation(this.mInfoVisFraction, n)).setDuration((long)this.mSelectedAnimDuration);
        this.mAnim.setInterpolator((Interpolator)new AccelerateDecelerateInterpolator());
        this.mAnim.setAnimationListener((Animation$AnimationListener)new Animation$AnimationListener() {
            public void onAnimationEnd(final Animation animation) {
                if (BaseCardView.this.mInfoVisFraction == 0.0f) {
                    for (int i = 0; i < BaseCardView.this.mInfoViewList.size(); ++i) {
                        BaseCardView.this.mInfoViewList.get(i).setVisibility(8);
                    }
                }
            }
            
            public void onAnimationRepeat(final Animation animation) {
            }
            
            public void onAnimationStart(final Animation animation) {
            }
        });
        this.startAnimation(this.mAnim);
    }
    
    private void applyActiveState() {
        if (this.hasInfoRegion()) {
            final int mInfoVisibility = this.mInfoVisibility;
            if (mInfoVisibility == 1) {
                this.setInfoViewVisibility(this.isRegionVisible(mInfoVisibility));
            }
        }
    }
    
    private void applySelectedState(final boolean infoViewVisibility) {
        this.removeCallbacks(this.mAnimationTrigger);
        if (this.mCardType == 3) {
            if (infoViewVisibility) {
                if (!this.mDelaySelectedAnim) {
                    this.post(this.mAnimationTrigger);
                    this.mDelaySelectedAnim = true;
                }
                else {
                    this.postDelayed(this.mAnimationTrigger, (long)this.mSelectedAnimationDelay);
                }
            }
            else {
                this.animateInfoOffset(false);
            }
        }
        else if (this.mInfoVisibility == 2) {
            this.setInfoViewVisibility(infoViewVisibility);
        }
    }
    
    private void findChildrenViews() {
        this.mMainViewList.clear();
        this.mInfoViewList.clear();
        this.mExtraViewList.clear();
        final int childCount = this.getChildCount();
        final boolean b = this.hasInfoRegion() && this.isCurrentRegionVisible(this.mInfoVisibility);
        final boolean b2 = this.hasExtraRegion() && this.mInfoOffset > 0.0f;
        for (int i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child != null) {
                final int viewType = ((LayoutParams)child.getLayoutParams()).viewType;
                int n = 8;
                if (viewType == 1) {
                    child.setAlpha(this.mInfoAlpha);
                    this.mInfoViewList.add(child);
                    if (b) {
                        n = 0;
                    }
                    child.setVisibility(n);
                }
                else if (viewType == 2) {
                    this.mExtraViewList.add(child);
                    if (b2) {
                        n = 0;
                    }
                    child.setVisibility(n);
                }
                else {
                    this.mMainViewList.add(child);
                    child.setVisibility(0);
                }
            }
        }
    }
    
    private boolean hasExtraRegion() {
        return this.mCardType == 3;
    }
    
    private boolean hasInfoRegion() {
        return this.mCardType != 0;
    }
    
    private boolean isCurrentRegionVisible(final int n) {
        boolean b = true;
        if (n == 0) {
            return true;
        }
        if (n == 1) {
            return this.isActivated();
        }
        if (n != 2) {
            return false;
        }
        if (this.mCardType == 2) {
            if (this.mInfoVisFraction <= 0.0f) {
                b = false;
            }
            return b;
        }
        return this.isSelected();
    }
    
    private boolean isRegionVisible(final int n) {
        if (n == 0) {
            return true;
        }
        if (n != 1) {
            return n == 2 && this.isSelected();
        }
        return this.isActivated();
    }
    
    private void setInfoViewVisibility(final boolean b) {
        final int mCardType = this.mCardType;
        final int n = 0;
        if (mCardType == 3) {
            if (b) {
                for (int i = 0; i < this.mInfoViewList.size(); ++i) {
                    this.mInfoViewList.get(i).setVisibility(0);
                }
            }
            else {
                int index = 0;
                int j;
                while (true) {
                    j = n;
                    if (index >= this.mInfoViewList.size()) {
                        break;
                    }
                    this.mInfoViewList.get(index).setVisibility(8);
                    ++index;
                }
                while (j < this.mExtraViewList.size()) {
                    this.mExtraViewList.get(j).setVisibility(8);
                    ++j;
                }
                this.mInfoOffset = 0.0f;
            }
        }
        else if (mCardType == 2) {
            if (this.mInfoVisibility == 2) {
                this.animateInfoHeight(b);
            }
            else {
                for (int k = 0; k < this.mInfoViewList.size(); ++k) {
                    final View view = this.mInfoViewList.get(k);
                    int visibility;
                    if (b) {
                        visibility = 0;
                    }
                    else {
                        visibility = 8;
                    }
                    view.setVisibility(visibility);
                }
            }
        }
        else if (mCardType == 1) {
            this.animateInfoAlpha(b);
        }
    }
    
    void animateInfoOffset(final boolean b) {
        this.cancelAnimations();
        int max = 0;
        if (b) {
            final int measureSpec = View$MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
            final int measureSpec2 = View$MeasureSpec.makeMeasureSpec(0, 0);
            for (int i = max = 0; i < this.mExtraViewList.size(); ++i) {
                final View view = this.mExtraViewList.get(i);
                view.setVisibility(0);
                view.measure(measureSpec, measureSpec2);
                max = Math.max(max, view.getMeasuredHeight());
            }
        }
        final float mInfoOffset = this.mInfoOffset;
        float n;
        if (b) {
            n = (float)max;
        }
        else {
            n = 0.0f;
        }
        (this.mAnim = new InfoOffsetAnimation(mInfoOffset, n)).setDuration((long)this.mSelectedAnimDuration);
        this.mAnim.setInterpolator((Interpolator)new AccelerateDecelerateInterpolator());
        this.mAnim.setAnimationListener((Animation$AnimationListener)new Animation$AnimationListener() {
            public void onAnimationEnd(final Animation animation) {
                if (BaseCardView.this.mInfoOffset == 0.0f) {
                    for (int i = 0; i < BaseCardView.this.mExtraViewList.size(); ++i) {
                        BaseCardView.this.mExtraViewList.get(i).setVisibility(8);
                    }
                }
            }
            
            public void onAnimationRepeat(final Animation animation) {
            }
            
            public void onAnimationStart(final Animation animation) {
            }
        });
        this.startAnimation(this.mAnim);
    }
    
    void cancelAnimations() {
        final Animation mAnim = this.mAnim;
        if (mAnim != null) {
            mAnim.cancel();
            this.mAnim = null;
            this.clearAnimation();
        }
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams;
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }
    
    public LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    protected LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (viewGroup$LayoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams)viewGroup$LayoutParams);
        }
        return new LayoutParams(viewGroup$LayoutParams);
    }
    
    final float getFinalInfoAlpha() {
        float n;
        if (this.mCardType == 1 && this.mInfoVisibility == 2 && !this.isSelected()) {
            n = 0.0f;
        }
        else {
            n = 1.0f;
        }
        return n;
    }
    
    final float getFinalInfoVisFraction() {
        float n;
        if (this.mCardType == 2 && this.mInfoVisibility == 2 && !this.isSelected()) {
            n = 0.0f;
        }
        else {
            n = 1.0f;
        }
        return n;
    }
    
    protected int[] onCreateDrawableState(int i) {
        final int[] onCreateDrawableState = super.onCreateDrawableState(i);
        final int length = onCreateDrawableState.length;
        i = 0;
        int n2;
        int n = n2 = 0;
        while (i < length) {
            if (onCreateDrawableState[i] == 16842919) {
                n = 1;
            }
            if (onCreateDrawableState[i] == 16842910) {
                n2 = 1;
            }
            ++i;
        }
        if (n != 0 && n2 != 0) {
            return View.PRESSED_ENABLED_STATE_SET;
        }
        if (n != 0) {
            return BaseCardView.LB_PRESSED_STATE_SET;
        }
        if (n2 != 0) {
            return View.ENABLED_STATE_SET;
        }
        return View.EMPTY_STATE_SET;
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.removeCallbacks(this.mAnimationTrigger);
        this.cancelAnimations();
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        float n5 = (float)this.getPaddingTop();
        float n6;
        for (int i = 0; i < this.mMainViewList.size(); ++i, n5 = n6) {
            final View view = this.mMainViewList.get(i);
            n6 = n5;
            if (view.getVisibility() != 8) {
                view.layout(this.getPaddingLeft(), (int)n5, this.mMeasuredWidth + this.getPaddingLeft(), (int)(view.getMeasuredHeight() + n5));
                n6 = n5 + view.getMeasuredHeight();
            }
        }
        if (this.hasInfoRegion()) {
            int j = 0;
            float n7 = 0.0f;
            while (j < this.mInfoViewList.size()) {
                n7 += this.mInfoViewList.get(j).getMeasuredHeight();
                ++j;
            }
            final int mCardType = this.mCardType;
            float n9;
            float n10;
            if (mCardType == 1) {
                final float n8 = n9 = n5 - n7;
                n10 = n7;
                if (n8 < 0.0f) {
                    n9 = 0.0f;
                    n10 = n7;
                }
            }
            else if (mCardType == 2) {
                n9 = n5;
                n10 = n7;
                if (this.mInfoVisibility == 2) {
                    n10 = n7 * this.mInfoVisFraction;
                    n9 = n5;
                }
            }
            else {
                n9 = n5 - this.mInfoOffset;
                n10 = n7;
            }
            int index = 0;
            float n11;
            while (true) {
                n11 = n9;
                if (index >= this.mInfoViewList.size()) {
                    break;
                }
                final View view2 = this.mInfoViewList.get(index);
                float n12 = n9;
                float n13 = n10;
                if (view2.getVisibility() != 8) {
                    int measuredHeight;
                    if ((measuredHeight = view2.getMeasuredHeight()) > n10) {
                        measuredHeight = (int)n10;
                    }
                    final int paddingLeft = this.getPaddingLeft();
                    final int n14 = (int)n9;
                    final int mMeasuredWidth = this.mMeasuredWidth;
                    final int paddingLeft2 = this.getPaddingLeft();
                    final float n15 = (float)measuredHeight;
                    final float n16 = n9 + n15;
                    view2.layout(paddingLeft, n14, mMeasuredWidth + paddingLeft2, (int)n16);
                    final float n17 = n10 - n15;
                    n12 = n16;
                    n13 = n17;
                    if (n17 <= 0.0f) {
                        n11 = n16;
                        break;
                    }
                }
                ++index;
                n9 = n12;
                n10 = n13;
            }
            if (this.hasExtraRegion()) {
                float n18;
                for (int k = 0; k < this.mExtraViewList.size(); ++k, n11 = n18) {
                    final View view3 = this.mExtraViewList.get(k);
                    n18 = n11;
                    if (view3.getVisibility() != 8) {
                        view3.layout(this.getPaddingLeft(), (int)n11, this.mMeasuredWidth + this.getPaddingLeft(), (int)(view3.getMeasuredHeight() + n11));
                        n18 = n11 + view3.getMeasuredHeight();
                    }
                }
            }
        }
        this.onSizeChanged(0, 0, n3 - n, n4 - n2);
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int n3 = 0;
        this.mMeasuredWidth = 0;
        this.mMeasuredHeight = 0;
        this.findChildrenViews();
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(0, 0);
        int i = 0;
        int n5;
        int n4 = n5 = i;
        while (i < this.mMainViewList.size()) {
            final View view = this.mMainViewList.get(i);
            int n6 = n5;
            int combineMeasuredStates = n4;
            if (view.getVisibility() != 8) {
                this.measureChild(view, measureSpec, measureSpec);
                this.mMeasuredWidth = Math.max(this.mMeasuredWidth, view.getMeasuredWidth());
                n6 = n5 + view.getMeasuredHeight();
                combineMeasuredStates = View.combineMeasuredStates(n4, view.getMeasuredState());
            }
            ++i;
            n5 = n6;
            n4 = combineMeasuredStates;
        }
        this.setPivotX((float)(this.mMeasuredWidth / 2));
        this.setPivotY((float)(n5 / 2));
        final int measureSpec2 = View$MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
        int n10;
        int n11;
        int n12;
        if (this.hasInfoRegion()) {
            int n7;
            int combineMeasuredStates2;
            int n8;
            for (int j = n7 = 0; j < this.mInfoViewList.size(); ++j, n4 = combineMeasuredStates2, n7 = n8) {
                final View view2 = this.mInfoViewList.get(j);
                combineMeasuredStates2 = n4;
                n8 = n7;
                if (view2.getVisibility() != 8) {
                    this.measureChild(view2, measureSpec2, measureSpec);
                    n8 = n7;
                    if (this.mCardType != 1) {
                        n8 = n7 + view2.getMeasuredHeight();
                    }
                    combineMeasuredStates2 = View.combineMeasuredStates(n4, view2.getMeasuredState());
                }
            }
            if (this.hasExtraRegion()) {
                int n9;
                int index = n9 = 0;
                while (true) {
                    n10 = n4;
                    n11 = n7;
                    n12 = n9;
                    if (index >= this.mExtraViewList.size()) {
                        break;
                    }
                    final View view3 = this.mExtraViewList.get(index);
                    int combineMeasuredStates3 = n4;
                    int n13 = n9;
                    if (view3.getVisibility() != 8) {
                        this.measureChild(view3, measureSpec2, measureSpec);
                        n13 = n9 + view3.getMeasuredHeight();
                        combineMeasuredStates3 = View.combineMeasuredStates(n4, view3.getMeasuredState());
                    }
                    ++index;
                    n4 = combineMeasuredStates3;
                    n9 = n13;
                }
            }
            else {
                n12 = 0;
                n10 = n4;
                n11 = n7;
            }
        }
        else {
            n11 = (n12 = 0);
            n10 = n4;
        }
        int n14 = n3;
        if (this.hasInfoRegion()) {
            n14 = n3;
            if (this.mInfoVisibility == 2) {
                n14 = 1;
            }
        }
        final float n15 = (float)n5;
        float n16 = (float)n11;
        if (n14 != 0) {
            n16 *= this.mInfoVisFraction;
        }
        final float n17 = (float)n12;
        float mInfoOffset;
        if (n14 != 0) {
            mInfoOffset = 0.0f;
        }
        else {
            mInfoOffset = this.mInfoOffset;
        }
        this.mMeasuredHeight = (int)(n15 + n16 + n17 - mInfoOffset);
        this.setMeasuredDimension(View.resolveSizeAndState(this.mMeasuredWidth + this.getPaddingLeft() + this.getPaddingRight(), n, n10), View.resolveSizeAndState(this.mMeasuredHeight + this.getPaddingTop() + this.getPaddingBottom(), n2, n10 << 16));
    }
    
    public void setActivated(final boolean activated) {
        if (activated != this.isActivated()) {
            super.setActivated(activated);
            this.applyActiveState();
        }
    }
    
    public void setSelected(final boolean selected) {
        if (selected != this.isSelected()) {
            super.setSelected(selected);
            this.applySelectedState(this.isSelected());
        }
    }
    
    public boolean shouldDelayChildPressedState() {
        return false;
    }
    
    public String toString() {
        return super.toString();
    }
    
    class AnimationBase extends Animation
    {
        final void mockEnd() {
            this.applyTransformation(1.0f, (Transformation)null);
            BaseCardView.this.cancelAnimations();
        }
        
        final void mockStart() {
            this.getTransformation(0L, (Transformation)null);
        }
    }
    
    final class InfoAlphaAnimation extends AnimationBase
    {
        private float mDelta;
        private float mStartValue;
        
        public InfoAlphaAnimation(final float mStartValue, final float n) {
            this.mStartValue = mStartValue;
            this.mDelta = n - mStartValue;
        }
        
        protected void applyTransformation(final float n, final Transformation transformation) {
            BaseCardView.this.mInfoAlpha = this.mStartValue + n * this.mDelta;
            for (int i = 0; i < BaseCardView.this.mInfoViewList.size(); ++i) {
                BaseCardView.this.mInfoViewList.get(i).setAlpha(BaseCardView.this.mInfoAlpha);
            }
        }
    }
    
    final class InfoHeightAnimation extends AnimationBase
    {
        private float mDelta;
        private float mStartValue;
        
        public InfoHeightAnimation(final float mStartValue, final float n) {
            this.mStartValue = mStartValue;
            this.mDelta = n - mStartValue;
        }
        
        protected void applyTransformation(final float n, final Transformation transformation) {
            final BaseCardView this$0 = BaseCardView.this;
            this$0.mInfoVisFraction = this.mStartValue + n * this.mDelta;
            this$0.requestLayout();
        }
    }
    
    final class InfoOffsetAnimation extends AnimationBase
    {
        private float mDelta;
        private float mStartValue;
        
        public InfoOffsetAnimation(final float mStartValue, final float n) {
            this.mStartValue = mStartValue;
            this.mDelta = n - mStartValue;
        }
        
        protected void applyTransformation(final float n, final Transformation transformation) {
            final BaseCardView this$0 = BaseCardView.this;
            this$0.mInfoOffset = this.mStartValue + n * this.mDelta;
            this$0.requestLayout();
        }
    }
    
    public static class LayoutParams extends FrameLayout$LayoutParams
    {
        @ViewDebug$ExportedProperty(category = "layout", mapping = { @ViewDebug$IntToString(from = 0, to = "MAIN"), @ViewDebug$IntToString(from = 1, to = "INFO"), @ViewDebug$IntToString(from = 2, to = "EXTRA") })
        public int viewType;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.viewType = 0;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.viewType = 0;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.lbBaseCardView_Layout);
            this.viewType = obtainStyledAttributes.getInt(R$styleable.lbBaseCardView_Layout_layout_viewType, 0);
            obtainStyledAttributes.recycle();
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.viewType = 0;
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$MarginLayoutParams)layoutParams);
            this.viewType = 0;
            this.viewType = layoutParams.viewType;
        }
    }
}
