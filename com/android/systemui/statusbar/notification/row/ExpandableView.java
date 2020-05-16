// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.Interpolators;
import android.graphics.Paint;
import java.util.List;
import android.animation.AnimatorListenerAdapter;
import java.util.Iterator;
import android.view.ViewGroup$LayoutParams;
import android.view.View$MeasureSpec;
import android.content.res.Configuration;
import com.android.systemui.statusbar.StatusBarIconView;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.R$dimen;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.notification.stack.ExpandableViewState;
import android.view.ViewGroup;
import android.view.View;
import java.util.ArrayList;
import android.graphics.Rect;
import com.android.systemui.Dumpable;
import android.widget.FrameLayout;

public abstract class ExpandableView extends FrameLayout implements Dumpable
{
    private static Rect mClipRect;
    private int mActualHeight;
    private boolean mChangingPosition;
    protected int mClipBottomAmount;
    private boolean mClipToActualHeight;
    protected int mClipTopAmount;
    protected int mContentShift;
    protected float mContentTransformationAmount;
    private float mContentTranslation;
    protected float mExtraWidthForClipping;
    private boolean mInShelf;
    protected boolean mIsLastChild;
    private ArrayList<View> mMatchParentViews;
    protected int mMinimumHeightForClipping;
    protected OnHeightChangedListener mOnHeightChangedListener;
    private boolean mTransformingInShelf;
    private ViewGroup mTransientContainer;
    private final ExpandableViewState mViewState;
    private boolean mWillBeGone;
    
    static {
        ExpandableView.mClipRect = new Rect();
    }
    
    public ExpandableView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mMinimumHeightForClipping = 0;
        this.mExtraWidthForClipping = 0.0f;
        this.mMatchParentViews = new ArrayList<View>();
        this.mClipToActualHeight = true;
        this.mChangingPosition = false;
        this.mViewState = this.createExpandableViewState();
        this.initDimens();
    }
    
    private void initDimens() {
        this.mContentShift = this.getResources().getDimensionPixelSize(R$dimen.shelf_transform_content_shift);
    }
    
    protected void applyContentTransformation(final float n, final float n2) {
    }
    
    public void applyViewState() {
        final ExpandableViewState mViewState = this.mViewState;
        if (!mViewState.gone) {
            mViewState.applyToView((View)this);
        }
    }
    
    public boolean areChildrenExpanded() {
        return false;
    }
    
    protected ExpandableViewState createExpandableViewState() {
        return new ExpandableViewState();
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
    }
    
    public int getActualHeight() {
        return this.mActualHeight;
    }
    
    public void getBoundsOnScreen(final Rect rect, final boolean b) {
        super.getBoundsOnScreen(rect, b);
        if (this.getTop() + this.getTranslationY() < 0.0f) {
            rect.top += (int)(this.getTop() + this.getTranslationY());
        }
        rect.bottom = rect.top + this.getActualHeight();
        rect.top += this.getClipTopAmount();
    }
    
    public int getClipBottomAmount() {
        return this.mClipBottomAmount;
    }
    
    public int getClipTopAmount() {
        return this.mClipTopAmount;
    }
    
    public int getCollapsedHeight() {
        return this.getHeight();
    }
    
    protected float getContentTransformationShift() {
        return (float)this.mContentShift;
    }
    
    public float getContentTranslation() {
        return this.mContentTranslation;
    }
    
    public void getDrawingRect(final Rect rect) {
        super.getDrawingRect(rect);
        rect.left += (int)this.getTranslationX();
        rect.right += (int)this.getTranslationX();
        rect.bottom = (int)(rect.top + this.getTranslationY() + this.getActualHeight());
        rect.top += (int)(this.getTranslationY() + this.getClipTopAmount());
    }
    
    public int getExtraBottomPadding() {
        return 0;
    }
    
    public float getHeaderVisibleAmount() {
        return 1.0f;
    }
    
    public float getIncreasedPaddingAmount() {
        return 0.0f;
    }
    
    public int getIntrinsicHeight() {
        return this.getHeight();
    }
    
    public int getMaxContentHeight() {
        return this.getHeight();
    }
    
    public int getMinHeight() {
        return this.getMinHeight(false);
    }
    
    public int getMinHeight(final boolean b) {
        return this.getHeight();
    }
    
    public float getOutlineAlpha() {
        return 0.0f;
    }
    
    public int getOutlineTranslation() {
        return 0;
    }
    
    public int getPinnedHeadsUpHeight() {
        return this.getIntrinsicHeight();
    }
    
    public int getRelativeStartPadding(View view) {
        final boolean layoutRtl = this.isLayoutRtl();
        int n = 0;
        while (view.getParent() instanceof ViewGroup) {
            final Object o = view.getParent();
            int left;
            if (layoutRtl) {
                left = ((View)o).getWidth() - view.getRight();
            }
            else {
                left = view.getLeft();
            }
            n += left;
            if (o == this) {
                return n;
            }
            view = (View)o;
        }
        return n;
    }
    
    public int getRelativeTopPadding(View view) {
        int n = 0;
        View view2;
        int n2;
        do {
            n2 = n;
            if (!(view.getParent() instanceof ViewGroup)) {
                break;
            }
            n2 = n + view.getTop();
            view2 = (View)view.getParent();
            n = n2;
        } while ((view = view2) != this);
        return n2;
    }
    
    public StatusBarIconView getShelfIcon() {
        return null;
    }
    
    public View getShelfTransformationTarget() {
        return null;
    }
    
    public ViewGroup getTransientContainer() {
        return this.mTransientContainer;
    }
    
    public float getTranslation() {
        return this.getTranslationX();
    }
    
    public ExpandableViewState getViewState() {
        return this.mViewState;
    }
    
    public boolean hasExpandingChild() {
        return false;
    }
    
    public boolean hasNoContentHeight() {
        return false;
    }
    
    public boolean hasOverlappingRendering() {
        return super.hasOverlappingRendering() && this.getActualHeight() <= this.getHeight();
    }
    
    public boolean isAboveShelf() {
        return false;
    }
    
    public boolean isChangingPosition() {
        return this.mChangingPosition;
    }
    
    public boolean isChildInGroup() {
        return false;
    }
    
    public boolean isContentExpandable() {
        return false;
    }
    
    public boolean isExpandAnimationRunning() {
        return false;
    }
    
    public boolean isGroupExpanded() {
        return false;
    }
    
    public boolean isGroupExpansionChanging() {
        return false;
    }
    
    public boolean isHeadsUpAnimatingAway() {
        return false;
    }
    
    public boolean isInShelf() {
        return this.mInShelf;
    }
    
    public boolean isPinned() {
        return false;
    }
    
    public boolean isRemoved() {
        return false;
    }
    
    public boolean isSummaryWithChildren() {
        return false;
    }
    
    public boolean isTransformingIntoShelf() {
        return this.mTransformingInShelf;
    }
    
    public boolean isTransparent() {
        return false;
    }
    
    public boolean mustStayOnScreen() {
        return false;
    }
    
    public boolean needsClippingToShelf() {
        return true;
    }
    
    public void notifyHeightChanged(final boolean b) {
        final OnHeightChangedListener mOnHeightChangedListener = this.mOnHeightChangedListener;
        if (mOnHeightChangedListener != null) {
            mOnHeightChangedListener.onHeightChanged(this, b);
        }
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.initDimens();
    }
    
    public void onHeightReset() {
        final OnHeightChangedListener mOnHeightChangedListener = this.mOnHeightChangedListener;
        if (mOnHeightChangedListener != null) {
            mOnHeightChangedListener.onReset(this);
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.updateClipping();
    }
    
    protected void onMeasure(final int n, int n2) {
        final int size = View$MeasureSpec.getSize(n2);
        final int n3 = this.getPaddingStart() + this.getPaddingEnd();
        final int mode = View$MeasureSpec.getMode(n2);
        final int n4 = n2 = Integer.MAX_VALUE;
        if (mode != 0) {
            n2 = n4;
            if (size != 0) {
                n2 = Math.min(size, Integer.MAX_VALUE);
            }
        }
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(n2, Integer.MIN_VALUE);
        final int childCount = this.getChildCount();
        int i = 0;
        int max = 0;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final ViewGroup$LayoutParams layoutParams = child.getLayoutParams();
                final int height = layoutParams.height;
                if (height != -1) {
                    int measureSpec2;
                    if (height >= 0) {
                        measureSpec2 = View$MeasureSpec.makeMeasureSpec(Math.min(height, n2), 1073741824);
                    }
                    else {
                        measureSpec2 = measureSpec;
                    }
                    child.measure(FrameLayout.getChildMeasureSpec(n, n3, layoutParams.width), measureSpec2);
                    max = Math.max(max, child.getMeasuredHeight());
                }
                else {
                    this.mMatchParentViews.add(child);
                }
            }
            ++i;
        }
        if (mode == 1073741824) {
            n2 = size;
        }
        else {
            n2 = Math.min(n2, max);
        }
        final int measureSpec3 = View$MeasureSpec.makeMeasureSpec(n2, 1073741824);
        for (final View view : this.mMatchParentViews) {
            view.measure(FrameLayout.getChildMeasureSpec(n, n3, view.getLayoutParams().width), measureSpec3);
        }
        this.mMatchParentViews.clear();
        this.setMeasuredDimension(View$MeasureSpec.getSize(n), n2);
    }
    
    public abstract void performAddAnimation(final long p0, final long p1, final boolean p2);
    
    public abstract long performRemoveAnimation(final long p0, final long p1, final float p2, final boolean p3, final float p4, final Runnable p5, final AnimatorListenerAdapter p6);
    
    public boolean pointInView(final float n, final float n2, final float n3) {
        final float n4 = (float)this.mClipTopAmount;
        final float n5 = (float)this.mActualHeight;
        return n >= -n3 && n2 >= n4 - n3 && n < super.mRight - super.mLeft + n3 && n2 < n5 + n3;
    }
    
    public ExpandableViewState resetViewState() {
        this.mViewState.height = this.getIntrinsicHeight();
        this.mViewState.gone = (this.getVisibility() == 8);
        final ExpandableViewState mViewState = this.mViewState;
        mViewState.alpha = 1.0f;
        mViewState.notGoneIndex = -1;
        mViewState.xTranslation = this.getTranslationX();
        final ExpandableViewState mViewState2 = this.mViewState;
        mViewState2.hidden = false;
        mViewState2.scaleX = this.getScaleX();
        this.mViewState.scaleY = this.getScaleY();
        final ExpandableViewState mViewState3 = this.mViewState;
        mViewState3.inShelf = false;
        mViewState3.headsUpIsVisible = false;
        if (this instanceof ExpandableNotificationRow) {
            final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)this;
            final List<ExpandableNotificationRow> notificationChildren = expandableNotificationRow.getNotificationChildren();
            if (expandableNotificationRow.isSummaryWithChildren() && notificationChildren != null) {
                final Iterator<ExpandableNotificationRow> iterator = notificationChildren.iterator();
                while (iterator.hasNext()) {
                    iterator.next().resetViewState();
                }
            }
        }
        return this.mViewState;
    }
    
    public void setActualHeight(final int n) {
        this.setActualHeight(n, true);
    }
    
    public void setActualHeight(final int mActualHeight, final boolean b) {
        this.mActualHeight = mActualHeight;
        this.updateClipping();
        if (b) {
            this.notifyHeightChanged(false);
        }
    }
    
    public void setActualHeightAnimating(final boolean b) {
    }
    
    public void setBelowSpeedBump(final boolean b) {
    }
    
    public void setChangingPosition(final boolean mChangingPosition) {
        this.mChangingPosition = mChangingPosition;
    }
    
    public void setClipBottomAmount(final int mClipBottomAmount) {
        this.mClipBottomAmount = mClipBottomAmount;
        this.updateClipping();
    }
    
    public void setClipToActualHeight(final boolean mClipToActualHeight) {
        this.mClipToActualHeight = mClipToActualHeight;
        this.updateClipping();
    }
    
    public void setClipTopAmount(final int mClipTopAmount) {
        this.mClipTopAmount = mClipTopAmount;
        this.updateClipping();
    }
    
    public void setContentTransformationAmount(final float mContentTransformationAmount, final boolean mIsLastChild) {
        final boolean mIsLastChild2 = this.mIsLastChild;
        int n = true ? 1 : 0;
        final boolean b = mIsLastChild != mIsLastChild2;
        if (this.mContentTransformationAmount == mContentTransformationAmount) {
            n = (false ? 1 : 0);
        }
        this.mIsLastChild = mIsLastChild;
        this.mContentTransformationAmount = mContentTransformationAmount;
        if (((b ? 1 : 0) | n) != 0x0) {
            this.updateContentTransformation();
        }
    }
    
    public void setDimmed(final boolean b, final boolean b2) {
    }
    
    public void setDistanceToTopRoundness(final float n) {
    }
    
    public void setExtraWidthForClipping(final float mExtraWidthForClipping) {
        this.mExtraWidthForClipping = mExtraWidthForClipping;
        this.updateClipping();
    }
    
    public void setFakeShadowIntensity(final float n, final float n2, final int n3, final int n4) {
    }
    
    public void setHeadsUpIsVisible() {
    }
    
    public void setHideSensitive(final boolean b, final boolean b2, final long n, final long n2) {
    }
    
    public void setHideSensitiveForIntrinsicHeight(final boolean b) {
    }
    
    public void setInShelf(final boolean mInShelf) {
        this.mInShelf = mInShelf;
    }
    
    public void setLayerType(final int n, final Paint paint) {
        if (this.hasOverlappingRendering()) {
            super.setLayerType(n, paint);
        }
    }
    
    public void setMinClipTopAmount(final int n) {
    }
    
    public void setMinimumHeightForClipping(final int mMinimumHeightForClipping) {
        this.mMinimumHeightForClipping = mMinimumHeightForClipping;
        this.updateClipping();
    }
    
    public void setOnHeightChangedListener(final OnHeightChangedListener mOnHeightChangedListener) {
        this.mOnHeightChangedListener = mOnHeightChangedListener;
    }
    
    public void setTransformingInShelf(final boolean mTransformingInShelf) {
        this.mTransformingInShelf = mTransformingInShelf;
    }
    
    public void setTransientContainer(final ViewGroup mTransientContainer) {
        this.mTransientContainer = mTransientContainer;
    }
    
    public void setTranslation(final float translationX) {
        this.setTranslationX(translationX);
    }
    
    public void setWillBeGone(final boolean mWillBeGone) {
        this.mWillBeGone = mWillBeGone;
    }
    
    protected boolean shouldClipToActualHeight() {
        return true;
    }
    
    public boolean showingPulsing() {
        return false;
    }
    
    protected void updateClipping() {
        if (this.mClipToActualHeight && this.shouldClipToActualHeight()) {
            final int clipTopAmount = this.getClipTopAmount();
            final int max = Math.max(Math.max(this.getActualHeight() + this.getExtraBottomPadding() - this.mClipBottomAmount, clipTopAmount), this.mMinimumHeightForClipping);
            final int n = (int)(this.mExtraWidthForClipping / 2.0f);
            ExpandableView.mClipRect.set(-n, clipTopAmount, this.getWidth() + n, max);
            this.setClipBounds(ExpandableView.mClipRect);
        }
        else {
            this.setClipBounds((Rect)null);
        }
    }
    
    protected void updateContentTransformation() {
        final float n = -this.mContentTransformationAmount * this.getContentTransformationShift();
        final float interpolation = Interpolators.ALPHA_OUT.getInterpolation(Math.min((1.0f - this.mContentTransformationAmount) / 0.5f, 1.0f));
        float mContentTranslation = n;
        if (this.mIsLastChild) {
            mContentTranslation = n * 0.4f;
        }
        this.applyContentTransformation(interpolation, this.mContentTranslation = mContentTranslation);
    }
    
    public boolean willBeGone() {
        return this.mWillBeGone;
    }
    
    public interface OnHeightChangedListener
    {
        void onHeightChanged(final ExpandableView p0, final boolean p1);
        
        void onReset(final ExpandableView p0);
    }
}
