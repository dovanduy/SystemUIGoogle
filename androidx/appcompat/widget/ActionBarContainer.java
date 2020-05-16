// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.view.ActionMode;
import android.view.ActionMode$Callback;
import android.view.ViewGroup$LayoutParams;
import android.view.View$MeasureSpec;
import android.view.MotionEvent;
import android.widget.FrameLayout$LayoutParams;
import android.content.res.TypedArray;
import androidx.appcompat.R$id;
import androidx.appcompat.R$styleable;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;

public class ActionBarContainer extends FrameLayout
{
    private View mActionBarView;
    Drawable mBackground;
    private View mContextView;
    private int mHeight;
    boolean mIsSplit;
    boolean mIsStacked;
    private boolean mIsTransitioning;
    Drawable mSplitBackground;
    Drawable mStackedBackground;
    private View mTabContainer;
    
    public ActionBarContainer(final Context context) {
        this(context, null);
    }
    
    public ActionBarContainer(final Context context, final AttributeSet set) {
        super(context, set);
        ViewCompat.setBackground((View)this, new ActionBarBackgroundDrawable(this));
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.ActionBar);
        this.mBackground = obtainStyledAttributes.getDrawable(R$styleable.ActionBar_background);
        this.mStackedBackground = obtainStyledAttributes.getDrawable(R$styleable.ActionBar_backgroundStacked);
        this.mHeight = obtainStyledAttributes.getDimensionPixelSize(R$styleable.ActionBar_height, -1);
        final int id = this.getId();
        final int split_action_bar = R$id.split_action_bar;
        boolean willNotDraw = true;
        if (id == split_action_bar) {
            this.mIsSplit = true;
            this.mSplitBackground = obtainStyledAttributes.getDrawable(R$styleable.ActionBar_backgroundSplit);
        }
        obtainStyledAttributes.recycle();
        Label_0137: {
            if (this.mIsSplit) {
                if (this.mSplitBackground == null) {
                    break Label_0137;
                }
            }
            else if (this.mBackground == null && this.mStackedBackground == null) {
                break Label_0137;
            }
            willNotDraw = false;
        }
        this.setWillNotDraw(willNotDraw);
    }
    
    private int getMeasuredHeightWithMargins(final View view) {
        final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)view.getLayoutParams();
        return view.getMeasuredHeight() + frameLayout$LayoutParams.topMargin + frameLayout$LayoutParams.bottomMargin;
    }
    
    private boolean isCollapsed(final View view) {
        return view == null || view.getVisibility() == 8 || view.getMeasuredHeight() == 0;
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final Drawable mBackground = this.mBackground;
        if (mBackground != null && mBackground.isStateful()) {
            this.mBackground.setState(this.getDrawableState());
        }
        final Drawable mStackedBackground = this.mStackedBackground;
        if (mStackedBackground != null && mStackedBackground.isStateful()) {
            this.mStackedBackground.setState(this.getDrawableState());
        }
        final Drawable mSplitBackground = this.mSplitBackground;
        if (mSplitBackground != null && mSplitBackground.isStateful()) {
            this.mSplitBackground.setState(this.getDrawableState());
        }
    }
    
    public View getTabContainer() {
        return this.mTabContainer;
    }
    
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        final Drawable mBackground = this.mBackground;
        if (mBackground != null) {
            mBackground.jumpToCurrentState();
        }
        final Drawable mStackedBackground = this.mStackedBackground;
        if (mStackedBackground != null) {
            mStackedBackground.jumpToCurrentState();
        }
        final Drawable mSplitBackground = this.mSplitBackground;
        if (mSplitBackground != null) {
            mSplitBackground.jumpToCurrentState();
        }
    }
    
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mActionBarView = this.findViewById(R$id.action_bar);
        this.mContextView = this.findViewById(R$id.action_context_bar);
    }
    
    public boolean onHoverEvent(final MotionEvent motionEvent) {
        super.onHoverEvent(motionEvent);
        return true;
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        return this.mIsTransitioning || super.onInterceptTouchEvent(motionEvent);
    }
    
    public void onLayout(final boolean b, int n, int n2, final int n3, int n4) {
        super.onLayout(b, n, n2, n3, n4);
        final View mTabContainer = this.mTabContainer;
        n4 = 1;
        final int n5 = 0;
        n2 = 0;
        final boolean mIsStacked = mTabContainer != null && mTabContainer.getVisibility() != 8;
        if (mTabContainer != null && mTabContainer.getVisibility() != 8) {
            final int measuredHeight = this.getMeasuredHeight();
            final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)mTabContainer.getLayoutParams();
            final int measuredHeight2 = mTabContainer.getMeasuredHeight();
            final int bottomMargin = frameLayout$LayoutParams.bottomMargin;
            mTabContainer.layout(n, measuredHeight - measuredHeight2 - bottomMargin, n3, measuredHeight - bottomMargin);
        }
        Label_0337: {
            if (this.mIsSplit) {
                final Drawable mSplitBackground = this.mSplitBackground;
                if (mSplitBackground != null) {
                    mSplitBackground.setBounds(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
                    n = n4;
                    break Label_0337;
                }
            }
            else {
                n = n5;
                if (this.mBackground != null) {
                    if (this.mActionBarView.getVisibility() == 0) {
                        this.mBackground.setBounds(this.mActionBarView.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
                    }
                    else {
                        final View mContextView = this.mContextView;
                        if (mContextView != null && mContextView.getVisibility() == 0) {
                            this.mBackground.setBounds(this.mContextView.getLeft(), this.mContextView.getTop(), this.mContextView.getRight(), this.mContextView.getBottom());
                        }
                        else {
                            this.mBackground.setBounds(0, 0, 0, 0);
                        }
                    }
                    n = 1;
                }
                this.mIsStacked = mIsStacked;
                n2 = n;
                if (mIsStacked) {
                    final Drawable mStackedBackground = this.mStackedBackground;
                    n2 = n;
                    if (mStackedBackground != null) {
                        mStackedBackground.setBounds(mTabContainer.getLeft(), mTabContainer.getTop(), mTabContainer.getRight(), mTabContainer.getBottom());
                        n = n4;
                        break Label_0337;
                    }
                }
            }
            n = n2;
        }
        if (n != 0) {
            this.invalidate();
        }
    }
    
    public void onMeasure(int n, int b) {
        int measureSpec = b;
        if (this.mActionBarView == null) {
            measureSpec = b;
            if (View$MeasureSpec.getMode(b) == Integer.MIN_VALUE) {
                final int mHeight = this.mHeight;
                measureSpec = b;
                if (mHeight >= 0) {
                    measureSpec = View$MeasureSpec.makeMeasureSpec(Math.min(mHeight, View$MeasureSpec.getSize(b)), Integer.MIN_VALUE);
                }
            }
        }
        super.onMeasure(n, measureSpec);
        if (this.mActionBarView == null) {
            return;
        }
        b = View$MeasureSpec.getMode(measureSpec);
        final View mTabContainer = this.mTabContainer;
        if (mTabContainer != null && mTabContainer.getVisibility() != 8 && b != 1073741824) {
            if (!this.isCollapsed(this.mActionBarView)) {
                n = this.getMeasuredHeightWithMargins(this.mActionBarView);
            }
            else if (!this.isCollapsed(this.mContextView)) {
                n = this.getMeasuredHeightWithMargins(this.mContextView);
            }
            else {
                n = 0;
            }
            if (b == Integer.MIN_VALUE) {
                b = View$MeasureSpec.getSize(measureSpec);
            }
            else {
                b = Integer.MAX_VALUE;
            }
            this.setMeasuredDimension(this.getMeasuredWidth(), Math.min(n + this.getMeasuredHeightWithMargins(this.mTabContainer), b));
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        return true;
    }
    
    public void setTabContainer(final ScrollingTabContainerView mTabContainer) {
        final View mTabContainer2 = this.mTabContainer;
        if (mTabContainer2 != null) {
            this.removeView(mTabContainer2);
        }
        if ((this.mTabContainer = (View)mTabContainer) != null) {
            this.addView((View)mTabContainer);
            final ViewGroup$LayoutParams layoutParams = mTabContainer.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -2;
            mTabContainer.setAllowCollapse(false);
        }
    }
    
    public void setTransitioning(final boolean mIsTransitioning) {
        this.mIsTransitioning = mIsTransitioning;
        int descendantFocusability;
        if (mIsTransitioning) {
            descendantFocusability = 393216;
        }
        else {
            descendantFocusability = 262144;
        }
        this.setDescendantFocusability(descendantFocusability);
    }
    
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
        final boolean b = visibility == 0;
        final Drawable mBackground = this.mBackground;
        if (mBackground != null) {
            mBackground.setVisible(b, false);
        }
        final Drawable mStackedBackground = this.mStackedBackground;
        if (mStackedBackground != null) {
            mStackedBackground.setVisible(b, false);
        }
        final Drawable mSplitBackground = this.mSplitBackground;
        if (mSplitBackground != null) {
            mSplitBackground.setVisible(b, false);
        }
    }
    
    public ActionMode startActionModeForChild(final View view, final ActionMode$Callback actionMode$Callback) {
        return null;
    }
    
    public ActionMode startActionModeForChild(final View view, final ActionMode$Callback actionMode$Callback, final int n) {
        if (n != 0) {
            return super.startActionModeForChild(view, actionMode$Callback, n);
        }
        return null;
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        return (drawable == this.mBackground && !this.mIsSplit) || (drawable == this.mStackedBackground && this.mIsStacked) || (drawable == this.mSplitBackground && this.mIsSplit) || super.verifyDrawable(drawable);
    }
}
