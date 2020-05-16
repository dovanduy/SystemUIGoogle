// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.animation.Interpolator;
import android.graphics.Rect;
import android.content.res.TypedArray;
import androidx.leanback.R$styleable;
import android.view.View;
import android.view.KeyEvent;
import android.view.MotionEvent;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseGridView extends RecyclerView
{
    RecyclerListener mChainedRecyclerListener;
    private boolean mHasOverlappingRendering;
    int mInitialPrefetchItemCount;
    final GridLayoutManager mLayoutManager;
    private OnKeyInterceptListener mOnKeyInterceptListener;
    private OnMotionInterceptListener mOnMotionInterceptListener;
    private OnTouchInterceptListener mOnTouchInterceptListener;
    private OnUnhandledKeyListener mOnUnhandledKeyListener;
    private int mPrivateFlag;
    private SmoothScrollByBehavior mSmoothScrollByBehavior;
    
    BaseGridView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mHasOverlappingRendering = true;
        this.mInitialPrefetchItemCount = 4;
        this.setLayoutManager((LayoutManager)(this.mLayoutManager = new GridLayoutManager(this)));
        this.setPreserveFocusAfterLayout(false);
        this.setDescendantFocusability(262144);
        this.setHasFixedSize(true);
        this.setChildrenDrawingOrderEnabled(true);
        this.setWillNotDraw(true);
        this.setOverScrollMode(2);
        ((SimpleItemAnimator)this.getItemAnimator()).setSupportsChangeAnimations(false);
        super.setRecyclerListener((RecyclerListener)new RecyclerListener() {
            @Override
            public void onViewRecycled(final ViewHolder viewHolder) {
                BaseGridView.this.mLayoutManager.onChildRecycled(viewHolder);
                final RecyclerListener mChainedRecyclerListener = BaseGridView.this.mChainedRecyclerListener;
                if (mChainedRecyclerListener != null) {
                    mChainedRecyclerListener.onViewRecycled(viewHolder);
                }
            }
        });
    }
    
    protected boolean dispatchGenericFocusedEvent(final MotionEvent motionEvent) {
        final OnMotionInterceptListener mOnMotionInterceptListener = this.mOnMotionInterceptListener;
        return (mOnMotionInterceptListener != null && mOnMotionInterceptListener.onInterceptMotionEvent(motionEvent)) || super.dispatchGenericFocusedEvent(motionEvent);
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        final OnKeyInterceptListener mOnKeyInterceptListener = this.mOnKeyInterceptListener;
        boolean b = true;
        if (mOnKeyInterceptListener != null && mOnKeyInterceptListener.onInterceptKeyEvent(keyEvent)) {
            return true;
        }
        if (super.dispatchKeyEvent(keyEvent)) {
            return true;
        }
        final OnUnhandledKeyListener mOnUnhandledKeyListener = this.mOnUnhandledKeyListener;
        if (mOnUnhandledKeyListener == null || !mOnUnhandledKeyListener.onUnhandledKey(keyEvent)) {
            b = false;
        }
        return b;
    }
    
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        final OnTouchInterceptListener mOnTouchInterceptListener = this.mOnTouchInterceptListener;
        return (mOnTouchInterceptListener != null && mOnTouchInterceptListener.onInterceptTouchEvent(motionEvent)) || super.dispatchTouchEvent(motionEvent);
    }
    
    public View focusSearch(final int n) {
        if (this.isFocused()) {
            final GridLayoutManager mLayoutManager = this.mLayoutManager;
            final View viewByPosition = ((LayoutManager)mLayoutManager).findViewByPosition(mLayoutManager.getSelection());
            if (viewByPosition != null) {
                return this.focusSearch(viewByPosition, n);
            }
        }
        return super.focusSearch(n);
    }
    
    public int getChildDrawingOrder(final int n, final int n2) {
        return this.mLayoutManager.getChildDrawingOrder(this, n, n2);
    }
    
    public int getSelectedPosition() {
        return this.mLayoutManager.getSelection();
    }
    
    public int getVerticalSpacing() {
        return this.mLayoutManager.getVerticalSpacing();
    }
    
    public boolean hasOverlappingRendering() {
        return this.mHasOverlappingRendering;
    }
    
    void initBaseGridViewAttributes(final Context context, final AttributeSet set) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.lbBaseGridView);
        this.mLayoutManager.setFocusOutAllowed(obtainStyledAttributes.getBoolean(R$styleable.lbBaseGridView_focusOutFront, false), obtainStyledAttributes.getBoolean(R$styleable.lbBaseGridView_focusOutEnd, false));
        this.mLayoutManager.setFocusOutSideAllowed(obtainStyledAttributes.getBoolean(R$styleable.lbBaseGridView_focusOutSideStart, true), obtainStyledAttributes.getBoolean(R$styleable.lbBaseGridView_focusOutSideEnd, true));
        this.mLayoutManager.setVerticalSpacing(obtainStyledAttributes.getDimensionPixelSize(R$styleable.lbBaseGridView_android_verticalSpacing, obtainStyledAttributes.getDimensionPixelSize(R$styleable.lbBaseGridView_verticalMargin, 0)));
        this.mLayoutManager.setHorizontalSpacing(obtainStyledAttributes.getDimensionPixelSize(R$styleable.lbBaseGridView_android_horizontalSpacing, obtainStyledAttributes.getDimensionPixelSize(R$styleable.lbBaseGridView_horizontalMargin, 0)));
        if (obtainStyledAttributes.hasValue(R$styleable.lbBaseGridView_android_gravity)) {
            this.setGravity(obtainStyledAttributes.getInt(R$styleable.lbBaseGridView_android_gravity, 0));
        }
        obtainStyledAttributes.recycle();
    }
    
    final boolean isChildrenDrawingOrderEnabledInternal() {
        return this.isChildrenDrawingOrderEnabled();
    }
    
    protected void onFocusChanged(final boolean b, final int n, final Rect rect) {
        super.onFocusChanged(b, n, rect);
        this.mLayoutManager.onFocusChanged(b, n, rect);
    }
    
    public boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        return (this.mPrivateFlag & 0x1) != 0x1 && this.mLayoutManager.gridOnRequestFocusInDescendants(this, n, rect);
    }
    
    public void onRtlPropertiesChanged(final int n) {
        this.mLayoutManager.onRtlPropertiesChanged(n);
    }
    
    public void removeView(final View view) {
        final boolean b = view.hasFocus() && this.isFocusable();
        if (b) {
            this.mPrivateFlag |= 0x1;
            this.requestFocus();
        }
        super.removeView(view);
        if (b) {
            this.mPrivateFlag ^= 0xFFFFFFFE;
        }
    }
    
    public void removeViewAt(final int n) {
        final boolean hasFocus = this.getChildAt(n).hasFocus();
        if (hasFocus) {
            this.mPrivateFlag |= 0x1;
            this.requestFocus();
        }
        super.removeViewAt(n);
        if (hasFocus) {
            this.mPrivateFlag ^= 0xFFFFFFFE;
        }
    }
    
    @Override
    public void scrollToPosition(final int n) {
        if (this.mLayoutManager.isSlidingChildViews()) {
            this.mLayoutManager.setSelectionWithSub(n, 0, 0);
            return;
        }
        super.scrollToPosition(n);
    }
    
    public void setGravity(final int gravity) {
        this.mLayoutManager.setGravity(gravity);
        this.requestLayout();
    }
    
    public void setOnChildViewHolderSelectedListener(final OnChildViewHolderSelectedListener onChildViewHolderSelectedListener) {
        this.mLayoutManager.setOnChildViewHolderSelectedListener(onChildViewHolderSelectedListener);
    }
    
    public void setSelectedPosition(final int n) {
        this.mLayoutManager.setSelection(n, 0);
    }
    
    public void setSelectedPositionSmooth(final int selectionSmooth) {
        this.mLayoutManager.setSelectionSmooth(selectionSmooth);
    }
    
    public void setWindowAlignment(final int windowAlignment) {
        this.mLayoutManager.setWindowAlignment(windowAlignment);
        this.requestLayout();
    }
    
    @Override
    public void smoothScrollBy(final int n, final int n2) {
        final SmoothScrollByBehavior mSmoothScrollByBehavior = this.mSmoothScrollByBehavior;
        if (mSmoothScrollByBehavior != null) {
            this.smoothScrollBy(n, n2, mSmoothScrollByBehavior.configSmoothScrollByInterpolator(n, n2), this.mSmoothScrollByBehavior.configSmoothScrollByDuration(n, n2));
        }
        else {
            this.smoothScrollBy(n, n2, null, Integer.MIN_VALUE);
        }
    }
    
    @Override
    public void smoothScrollBy(final int n, final int n2, final Interpolator interpolator) {
        final SmoothScrollByBehavior mSmoothScrollByBehavior = this.mSmoothScrollByBehavior;
        if (mSmoothScrollByBehavior != null) {
            this.smoothScrollBy(n, n2, interpolator, mSmoothScrollByBehavior.configSmoothScrollByDuration(n, n2));
        }
        else {
            this.smoothScrollBy(n, n2, interpolator, Integer.MIN_VALUE);
        }
    }
    
    @Override
    public void smoothScrollToPosition(final int n) {
        if (this.mLayoutManager.isSlidingChildViews()) {
            this.mLayoutManager.setSelectionWithSub(n, 0, 0);
            return;
        }
        super.smoothScrollToPosition(n);
    }
    
    public interface OnKeyInterceptListener
    {
        boolean onInterceptKeyEvent(final KeyEvent p0);
    }
    
    public interface OnLayoutCompletedListener
    {
        void onLayoutCompleted(final State p0);
    }
    
    public interface OnMotionInterceptListener
    {
        boolean onInterceptMotionEvent(final MotionEvent p0);
    }
    
    public interface OnTouchInterceptListener
    {
        boolean onInterceptTouchEvent(final MotionEvent p0);
    }
    
    public interface OnUnhandledKeyListener
    {
        boolean onUnhandledKey(final KeyEvent p0);
    }
    
    public interface SmoothScrollByBehavior
    {
        int configSmoothScrollByDuration(final int p0, final int p1);
        
        Interpolator configSmoothScrollByInterpolator(final int p0, final int p1);
    }
}
