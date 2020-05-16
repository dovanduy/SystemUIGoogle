// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.stack.ViewState;
import android.view.View$MeasureSpec;
import android.graphics.Canvas;
import java.util.Iterator;
import java.util.List;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.statusbar.StatusIconDisplayable;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.notification.stack.AnimationFilter;
import android.view.View;
import java.util.ArrayList;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.keyguard.AlphaOptimizedLinearLayout;

public class StatusIconContainer extends AlphaOptimizedLinearLayout
{
    private static final AnimationProperties ADD_ICON_PROPERTIES;
    private static final AnimationProperties ANIMATE_ALL_PROPERTIES;
    private static final AnimationProperties X_ANIMATION_PROPERTIES;
    private int mDotPadding;
    private int mIconDotFrameWidth;
    private int mIconSpacing;
    private ArrayList<String> mIgnoredSlots;
    private ArrayList<StatusIconState> mLayoutStates;
    private ArrayList<View> mMeasureViews;
    private boolean mNeedsUnderflow;
    private boolean mShouldRestrictIcons;
    private int mStaticDotDiameter;
    private int mUnderflowStart;
    private int mUnderflowWidth;
    
    static {
        final AnimationProperties add_ICON_PROPERTIES = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;
            
            {
                final AnimationFilter mAnimationFilter = new AnimationFilter();
                mAnimationFilter.animateAlpha();
                this.mAnimationFilter = mAnimationFilter;
            }
            
            @Override
            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        add_ICON_PROPERTIES.setDuration(200L);
        add_ICON_PROPERTIES.setDelay(50L);
        ADD_ICON_PROPERTIES = add_ICON_PROPERTIES;
        final AnimationProperties x_ANIMATION_PROPERTIES = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;
            
            {
                final AnimationFilter mAnimationFilter = new AnimationFilter();
                mAnimationFilter.animateX();
                this.mAnimationFilter = mAnimationFilter;
            }
            
            @Override
            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        x_ANIMATION_PROPERTIES.setDuration(200L);
        X_ANIMATION_PROPERTIES = x_ANIMATION_PROPERTIES;
        final AnimationProperties animate_ALL_PROPERTIES = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;
            
            {
                final AnimationFilter mAnimationFilter = new AnimationFilter();
                mAnimationFilter.animateX();
                mAnimationFilter.animateY();
                mAnimationFilter.animateAlpha();
                mAnimationFilter.animateScale();
                this.mAnimationFilter = mAnimationFilter;
            }
            
            @Override
            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        animate_ALL_PROPERTIES.setDuration(200L);
        ANIMATE_ALL_PROPERTIES = animate_ALL_PROPERTIES;
    }
    
    public StatusIconContainer(final Context context) {
        this(context, null);
    }
    
    public StatusIconContainer(final Context context, final AttributeSet set) {
        super(context, set);
        this.mUnderflowStart = 0;
        this.mShouldRestrictIcons = true;
        this.mLayoutStates = new ArrayList<StatusIconState>();
        this.mMeasureViews = new ArrayList<View>();
        this.mIgnoredSlots = new ArrayList<String>();
        this.initDimens();
        this.setWillNotDraw(true);
    }
    
    private void addIgnoredSlotInternal(final String s) {
        if (!this.mIgnoredSlots.contains(s)) {
            this.mIgnoredSlots.add(s);
        }
    }
    
    private void applyIconStates() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            final StatusIconState viewStateFromChild = getViewStateFromChild(child);
            if (viewStateFromChild != null) {
                viewStateFromChild.applyToView(child);
            }
        }
    }
    
    private void calculateIconTranslations() {
        this.mLayoutStates.clear();
        final float n = (float)this.getWidth();
        float n2 = n - this.getPaddingEnd();
        final float a = (float)this.getPaddingStart();
        final int childCount = this.getChildCount();
        int n3 = childCount - 1;
        int n4;
        while (true) {
            n4 = 0;
            if (n3 < 0) {
                break;
            }
            final View child = this.getChildAt(n3);
            final StatusIconDisplayable statusIconDisplayable = (StatusIconDisplayable)child;
            final StatusIconState viewStateFromChild = getViewStateFromChild(child);
            if (statusIconDisplayable.isIconVisible() && !statusIconDisplayable.isIconBlocked() && !this.mIgnoredSlots.contains(statusIconDisplayable.getSlot())) {
                final float xTranslation = n2 - getViewTotalWidth(child);
                viewStateFromChild.visibleState = 0;
                viewStateFromChild.xTranslation = xTranslation;
                this.mLayoutStates.add(0, viewStateFromChild);
                n2 = xTranslation - this.mIconSpacing;
            }
            else {
                viewStateFromChild.visibleState = 2;
            }
            --n3;
        }
        final int size = this.mLayoutStates.size();
        int n5 = 7;
        if (size > 7) {
            n5 = 6;
        }
        this.mUnderflowStart = 0;
        int i = size - 1;
        int n6 = 0;
    Label_0309:
        while (true) {
            while (i >= 0) {
                final StatusIconState statusIconState = this.mLayoutStates.get(i);
                if (this.mNeedsUnderflow) {
                    final int j = i;
                    if (statusIconState.xTranslation < this.mUnderflowWidth + a) {
                        break Label_0309;
                    }
                }
                if (!this.mShouldRestrictIcons || n6 < n5) {
                    this.mUnderflowStart = (int)Math.max(a, statusIconState.xTranslation - this.mUnderflowWidth - this.mIconSpacing);
                    ++n6;
                    --i;
                    continue;
                }
                int j = i;
                if (j != -1) {
                    final int mStaticDotDiameter = this.mStaticDotDiameter;
                    final int mDotPadding = this.mDotPadding;
                    int n7 = this.mUnderflowStart + this.mUnderflowWidth - this.mIconDotFrameWidth;
                    int n8 = 0;
                    while (j >= 0) {
                        final StatusIconState statusIconState2 = this.mLayoutStates.get(j);
                        if (n8 < 1) {
                            statusIconState2.xTranslation = (float)n7;
                            statusIconState2.visibleState = 1;
                            n7 -= mStaticDotDiameter + mDotPadding;
                            ++n8;
                        }
                        else {
                            statusIconState2.visibleState = 2;
                        }
                        --j;
                    }
                }
                if (this.isLayoutRtl()) {
                    for (int k = n4; k < childCount; ++k) {
                        final View child2 = this.getChildAt(k);
                        final StatusIconState viewStateFromChild2 = getViewStateFromChild(child2);
                        viewStateFromChild2.xTranslation = n - viewStateFromChild2.xTranslation - child2.getWidth();
                    }
                }
                return;
            }
            int j = -1;
            continue Label_0309;
        }
    }
    
    private static StatusIconState getViewStateFromChild(final View view) {
        return (StatusIconState)view.getTag(R$id.status_bar_view_state_tag);
    }
    
    private static int getViewTotalMeasuredWidth(final View view) {
        return view.getMeasuredWidth() + view.getPaddingStart() + view.getPaddingEnd();
    }
    
    private static int getViewTotalWidth(final View view) {
        return view.getWidth() + view.getPaddingStart() + view.getPaddingEnd();
    }
    
    private void initDimens() {
        this.mIconDotFrameWidth = this.getResources().getDimensionPixelSize(17105474);
        this.mDotPadding = this.getResources().getDimensionPixelSize(R$dimen.overflow_icon_dot_padding);
        this.mIconSpacing = this.getResources().getDimensionPixelSize(R$dimen.status_bar_system_icon_spacing);
        final int mStaticDotDiameter = this.getResources().getDimensionPixelSize(R$dimen.overflow_dot_radius) * 2;
        this.mStaticDotDiameter = mStaticDotDiameter;
        this.mUnderflowWidth = this.mIconDotFrameWidth + (mStaticDotDiameter + this.mDotPadding) * 0;
    }
    
    private void resetViewStates() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            final StatusIconState viewStateFromChild = getViewStateFromChild(child);
            if (viewStateFromChild != null) {
                viewStateFromChild.initFrom(child);
                viewStateFromChild.alpha = 1.0f;
                viewStateFromChild.hidden = false;
            }
        }
    }
    
    public void addIgnoredSlots(final List<String> list) {
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addIgnoredSlotInternal(iterator.next());
        }
        this.requestLayout();
    }
    
    public boolean isRestrictingIcons() {
        return this.mShouldRestrictIcons;
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
    
    protected void onLayout(final boolean b, int i, int n, int measuredWidth, int measuredHeight) {
        final float n2 = this.getHeight() / 2.0f;
        View child;
        for (i = 0; i < this.getChildCount(); ++i) {
            child = this.getChildAt(i);
            measuredWidth = child.getMeasuredWidth();
            measuredHeight = child.getMeasuredHeight();
            n = (int)(n2 - measuredHeight / 2.0f);
            child.layout(0, n, measuredWidth, measuredHeight + n);
        }
        this.resetViewStates();
        this.calculateIconTranslations();
        this.applyIconStates();
    }
    
    protected void onMeasure(int i, final int n) {
        this.mMeasureViews.clear();
        final int mode = View$MeasureSpec.getMode(i);
        final int size = View$MeasureSpec.getSize(i);
        int childCount;
        StatusIconDisplayable statusIconDisplayable;
        for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            statusIconDisplayable = (StatusIconDisplayable)this.getChildAt(i);
            if (statusIconDisplayable.isIconVisible() && !statusIconDisplayable.isIconBlocked() && !this.mIgnoredSlots.contains(statusIconDisplayable.getSlot())) {
                this.mMeasureViews.add((View)statusIconDisplayable);
            }
        }
        final int size2 = this.mMeasureViews.size();
        int n2;
        if (size2 <= 7) {
            n2 = 7;
        }
        else {
            n2 = 6;
        }
        i = super.mPaddingLeft + super.mPaddingRight;
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(size, 0);
        this.mNeedsUnderflow = (this.mShouldRestrictIcons && size2 > 7);
        int j = 0;
        int n3 = 1;
        while (j < size2) {
            final View view = this.mMeasureViews.get(size2 - j - 1);
            this.measureChild(view, measureSpec, n);
            int mIconSpacing;
            if (j == size2 - 1) {
                mIconSpacing = 0;
            }
            else {
                mIconSpacing = this.mIconSpacing;
            }
            int n5 = 0;
            int n6 = 0;
            Label_0309: {
                int n4;
                if (this.mShouldRestrictIcons) {
                    if (j < n2 && n3 != 0) {
                        n4 = getViewTotalMeasuredWidth(view);
                    }
                    else {
                        n5 = i;
                        if ((n6 = n3) != 0) {
                            n5 = i + this.mUnderflowWidth;
                            n6 = 0;
                        }
                        break Label_0309;
                    }
                }
                else {
                    n4 = getViewTotalMeasuredWidth(view);
                }
                n5 = i + (n4 + mIconSpacing);
                n6 = n3;
            }
            ++j;
            i = n5;
            n3 = n6;
        }
        if (mode == 1073741824) {
            if (!this.mNeedsUnderflow && i > size) {
                this.mNeedsUnderflow = true;
            }
            this.setMeasuredDimension(size, View$MeasureSpec.getSize(n));
        }
        else {
            if (mode == Integer.MIN_VALUE && i > size) {
                this.mNeedsUnderflow = true;
                i = size;
            }
            this.setMeasuredDimension(i, View$MeasureSpec.getSize(n));
        }
    }
    
    public void onViewAdded(final View view) {
        super.onViewAdded(view);
        final StatusIconState statusIconState = new StatusIconState();
        statusIconState.justAdded = true;
        view.setTag(R$id.status_bar_view_state_tag, (Object)statusIconState);
    }
    
    public void onViewRemoved(final View view) {
        super.onViewRemoved(view);
        view.setTag(R$id.status_bar_view_state_tag, (Object)null);
    }
    
    public void setShouldRestrictIcons(final boolean mShouldRestrictIcons) {
        this.mShouldRestrictIcons = mShouldRestrictIcons;
    }
    
    public static class StatusIconState extends ViewState
    {
        float distanceToViewEnd;
        public boolean justAdded;
        public int visibleState;
        
        public StatusIconState() {
            this.visibleState = 0;
            this.justAdded = true;
            this.distanceToViewEnd = -1.0f;
        }
        
        @Override
        public void applyToView(final View view) {
            float n;
            if (view.getParent() instanceof View) {
                n = (float)((View)view.getParent()).getWidth();
            }
            else {
                n = 0.0f;
            }
            final float distanceToViewEnd = n - super.xTranslation;
            if (!(view instanceof StatusIconDisplayable)) {
                return;
            }
            final StatusIconDisplayable statusIconDisplayable = (StatusIconDisplayable)view;
            final AnimationProperties animationProperties = null;
            final boolean b = true;
            boolean b2;
            AnimationProperties animationProperties2;
            if (!this.justAdded && (statusIconDisplayable.getVisibleState() != 2 || this.visibleState != 0)) {
                final int visibleState = statusIconDisplayable.getVisibleState();
                final int visibleState2 = this.visibleState;
                if (visibleState != visibleState2) {
                    if (statusIconDisplayable.getVisibleState() == 0 && this.visibleState == 2) {
                        b2 = false;
                        animationProperties2 = animationProperties;
                    }
                    else {
                        animationProperties2 = StatusIconContainer.ANIMATE_ALL_PROPERTIES;
                        b2 = b;
                    }
                }
                else {
                    animationProperties2 = animationProperties;
                    b2 = b;
                    if (visibleState2 != 2) {
                        animationProperties2 = animationProperties;
                        b2 = b;
                        if (this.distanceToViewEnd != distanceToViewEnd) {
                            animationProperties2 = StatusIconContainer.X_ANIMATION_PROPERTIES;
                            b2 = b;
                        }
                    }
                }
            }
            else {
                super.applyToView(view);
                view.setAlpha(0.0f);
                statusIconDisplayable.setVisibleState(2);
                animationProperties2 = StatusIconContainer.ADD_ICON_PROPERTIES;
                b2 = b;
            }
            statusIconDisplayable.setVisibleState(this.visibleState, b2);
            if (animationProperties2 != null) {
                this.animateTo(view, animationProperties2);
            }
            else {
                super.applyToView(view);
            }
            this.justAdded = false;
            this.distanceToViewEnd = distanceToViewEnd;
        }
    }
}
