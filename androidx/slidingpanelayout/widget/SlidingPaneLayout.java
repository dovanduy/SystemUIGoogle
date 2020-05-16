// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slidingpanelayout.widget;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import androidx.customview.view.AbsSavedState;
import android.content.res.TypedArray;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import android.os.Parcelable;
import android.view.View$MeasureSpec;
import android.view.MotionEvent;
import android.util.Log;
import android.view.ViewGroup$MarginLayoutParams;
import android.graphics.Canvas;
import android.view.ViewGroup$LayoutParams;
import android.os.Build$VERSION;
import android.graphics.ColorFilter;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff$Mode;
import android.graphics.Paint;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.graphics.drawable.Drawable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.lang.reflect.Method;
import androidx.customview.widget.ViewDragHelper;
import android.view.ViewGroup;

public class SlidingPaneLayout extends ViewGroup
{
    private boolean mCanSlide;
    private int mCoveredFadeColor;
    private boolean mDisplayListReflectionLoaded;
    final ViewDragHelper mDragHelper;
    private boolean mFirstLayout;
    private Method mGetDisplayList;
    private float mInitialMotionX;
    private float mInitialMotionY;
    boolean mIsUnableToDrag;
    private final int mOverhangSize;
    private PanelSlideListener mPanelSlideListener;
    private int mParallaxBy;
    private float mParallaxOffset;
    final ArrayList<DisableLayerRunnable> mPostedRunnables;
    boolean mPreservedOpenState;
    private Field mRecreateDisplayList;
    private Drawable mShadowDrawableLeft;
    private Drawable mShadowDrawableRight;
    float mSlideOffset;
    int mSlideRange;
    View mSlideableView;
    private int mSliderFadeColor;
    private final Rect mTmpRect;
    
    public SlidingPaneLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public SlidingPaneLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mSliderFadeColor = -858993460;
        this.mFirstLayout = true;
        this.mTmpRect = new Rect();
        this.mPostedRunnables = new ArrayList<DisableLayerRunnable>();
        final float density = context.getResources().getDisplayMetrics().density;
        this.mOverhangSize = (int)(32.0f * density + 0.5f);
        this.setWillNotDraw(false);
        ViewCompat.setAccessibilityDelegate((View)this, new AccessibilityDelegate());
        ViewCompat.setImportantForAccessibility((View)this, 1);
        (this.mDragHelper = ViewDragHelper.create(this, 0.5f, (ViewDragHelper.Callback)new DragHelperCallback())).setMinVelocity(density * 400.0f);
    }
    
    private boolean closePane(final int n) {
        if (!this.mFirstLayout && !this.smoothSlideTo(0.0f, n)) {
            return false;
        }
        this.mPreservedOpenState = false;
        return true;
    }
    
    private void dimChildView(final View view, final float n, final int n2) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (n > 0.0f && n2 != 0) {
            final int n3 = (int)(((0xFF000000 & n2) >>> 24) * n);
            if (layoutParams.dimPaint == null) {
                layoutParams.dimPaint = new Paint();
            }
            layoutParams.dimPaint.setColorFilter((ColorFilter)new PorterDuffColorFilter(n3 << 24 | (n2 & 0xFFFFFF), PorterDuff$Mode.SRC_OVER));
            if (view.getLayerType() != 2) {
                view.setLayerType(2, layoutParams.dimPaint);
            }
            this.invalidateChildRegion(view);
        }
        else if (view.getLayerType() != 0) {
            final Paint dimPaint = layoutParams.dimPaint;
            if (dimPaint != null) {
                dimPaint.setColorFilter((ColorFilter)null);
            }
            final DisableLayerRunnable e = new DisableLayerRunnable(view);
            this.mPostedRunnables.add(e);
            ViewCompat.postOnAnimation((View)this, e);
        }
    }
    
    private boolean openPane(final int n) {
        return (this.mFirstLayout || this.smoothSlideTo(1.0f, n)) && (this.mPreservedOpenState = true);
    }
    
    private void parallaxOtherViews(final float mParallaxOffset) {
        final boolean layoutRtlSupport = this.isLayoutRtlSupport();
        final LayoutParams layoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
        final boolean dimWhenOffset = layoutParams.dimWhenOffset;
        int i = 0;
        boolean b = false;
        Label_0063: {
            if (dimWhenOffset) {
                int n;
                if (layoutRtlSupport) {
                    n = layoutParams.rightMargin;
                }
                else {
                    n = layoutParams.leftMargin;
                }
                if (n <= 0) {
                    b = true;
                    break Label_0063;
                }
            }
            b = false;
        }
        while (i < this.getChildCount()) {
            final View child = this.getChildAt(i);
            if (child != this.mSlideableView) {
                final float mParallaxOffset2 = this.mParallaxOffset;
                final int mParallaxBy = this.mParallaxBy;
                final int n2 = (int)((1.0f - mParallaxOffset2) * mParallaxBy);
                this.mParallaxOffset = mParallaxOffset;
                int n3 = n2 - (int)((1.0f - mParallaxOffset) * mParallaxBy);
                if (layoutRtlSupport) {
                    n3 = -n3;
                }
                child.offsetLeftAndRight(n3);
                if (b) {
                    float n4;
                    if (layoutRtlSupport) {
                        n4 = this.mParallaxOffset - 1.0f;
                    }
                    else {
                        n4 = 1.0f - this.mParallaxOffset;
                    }
                    this.dimChildView(child, n4, this.mCoveredFadeColor);
                }
            }
            ++i;
        }
    }
    
    private static boolean viewIsOpaque(final View view) {
        final boolean opaque = view.isOpaque();
        boolean b = true;
        if (opaque) {
            return true;
        }
        if (Build$VERSION.SDK_INT >= 18) {
            return false;
        }
        final Drawable background = view.getBackground();
        if (background != null) {
            if (background.getOpacity() != -1) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams && super.checkLayoutParams(viewGroup$LayoutParams);
    }
    
    public boolean closePane() {
        return this.closePane(0);
    }
    
    public void computeScroll() {
        if (this.mDragHelper.continueSettling(true)) {
            if (!this.mCanSlide) {
                this.mDragHelper.abort();
                return;
            }
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    void dispatchOnPanelClosed(final View view) {
        final PanelSlideListener mPanelSlideListener = this.mPanelSlideListener;
        if (mPanelSlideListener != null) {
            mPanelSlideListener.onPanelClosed(view);
        }
        this.sendAccessibilityEvent(32);
    }
    
    void dispatchOnPanelOpened(final View view) {
        final PanelSlideListener mPanelSlideListener = this.mPanelSlideListener;
        if (mPanelSlideListener != null) {
            mPanelSlideListener.onPanelOpened(view);
        }
        this.sendAccessibilityEvent(32);
    }
    
    void dispatchOnPanelSlide(final View view) {
        final PanelSlideListener mPanelSlideListener = this.mPanelSlideListener;
        if (mPanelSlideListener != null) {
            mPanelSlideListener.onPanelSlide(view, this.mSlideOffset);
        }
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        Drawable drawable;
        if (this.isLayoutRtlSupport()) {
            drawable = this.mShadowDrawableRight;
        }
        else {
            drawable = this.mShadowDrawableLeft;
        }
        View child;
        if (this.getChildCount() > 1) {
            child = this.getChildAt(1);
        }
        else {
            child = null;
        }
        if (child != null) {
            if (drawable != null) {
                final int top = child.getTop();
                final int bottom = child.getBottom();
                final int intrinsicWidth = drawable.getIntrinsicWidth();
                int right;
                int left;
                if (this.isLayoutRtlSupport()) {
                    right = child.getRight();
                    left = intrinsicWidth + right;
                }
                else {
                    right = (left = child.getLeft()) - intrinsicWidth;
                }
                drawable.setBounds(right, top, left, bottom);
                drawable.draw(canvas);
            }
        }
    }
    
    protected boolean drawChild(final Canvas canvas, final View view, final long n) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int save = canvas.save();
        if (this.mCanSlide && !layoutParams.slideable && this.mSlideableView != null) {
            canvas.getClipBounds(this.mTmpRect);
            if (this.isLayoutRtlSupport()) {
                final Rect mTmpRect = this.mTmpRect;
                mTmpRect.left = Math.max(mTmpRect.left, this.mSlideableView.getRight());
            }
            else {
                final Rect mTmpRect2 = this.mTmpRect;
                mTmpRect2.right = Math.min(mTmpRect2.right, this.mSlideableView.getLeft());
            }
            canvas.clipRect(this.mTmpRect);
        }
        final boolean drawChild = super.drawChild(canvas, view, n);
        canvas.restoreToCount(save);
        return drawChild;
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return (ViewGroup$LayoutParams)new LayoutParams();
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        return (ViewGroup$LayoutParams)new LayoutParams(this.getContext(), set);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        LayoutParams layoutParams;
        if (viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams) {
            layoutParams = new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
        }
        else {
            layoutParams = new LayoutParams(viewGroup$LayoutParams);
        }
        return (ViewGroup$LayoutParams)layoutParams;
    }
    
    void invalidateChildRegion(final View view) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 17) {
            ViewCompat.setLayerPaint(view, ((LayoutParams)view.getLayoutParams()).dimPaint);
            return;
        }
        Label_0163: {
            if (sdk_INT >= 16) {
                if (!this.mDisplayListReflectionLoaded) {
                    try {
                        this.mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class<?>[])null);
                    }
                    catch (NoSuchMethodException ex) {
                        Log.e("SlidingPaneLayout", "Couldn't fetch getDisplayList method; dimming won't work right.", (Throwable)ex);
                    }
                    try {
                        (this.mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList")).setAccessible(true);
                    }
                    catch (NoSuchFieldException ex2) {
                        Log.e("SlidingPaneLayout", "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", (Throwable)ex2);
                    }
                    this.mDisplayListReflectionLoaded = true;
                }
                if (this.mGetDisplayList != null) {
                    final Field mRecreateDisplayList = this.mRecreateDisplayList;
                    if (mRecreateDisplayList != null) {
                        try {
                            mRecreateDisplayList.setBoolean(view, true);
                            this.mGetDisplayList.invoke(view, (Object[])null);
                        }
                        catch (Exception ex3) {
                            Log.e("SlidingPaneLayout", "Error refreshing display list state", (Throwable)ex3);
                        }
                        break Label_0163;
                    }
                }
                view.invalidate();
                return;
            }
        }
        ViewCompat.postInvalidateOnAnimation((View)this, view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }
    
    boolean isDimmed(final View view) {
        final boolean b = false;
        if (view == null) {
            return false;
        }
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        boolean b2 = b;
        if (this.mCanSlide) {
            b2 = b;
            if (layoutParams.dimWhenOffset) {
                b2 = b;
                if (this.mSlideOffset > 0.0f) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    boolean isLayoutRtlSupport() {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        boolean b = true;
        if (layoutDirection != 1) {
            b = false;
        }
        return b;
    }
    
    public boolean isOpen() {
        return !this.mCanSlide || this.mSlideOffset == 1.0f;
    }
    
    public boolean isSlideable() {
        return this.mCanSlide;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
        for (int size = this.mPostedRunnables.size(), i = 0; i < size; ++i) {
            this.mPostedRunnables.get(i).run();
        }
        this.mPostedRunnables.clear();
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        final boolean mCanSlide = this.mCanSlide;
        final boolean b = true;
        if (!mCanSlide && actionMasked == 0 && this.getChildCount() > 1) {
            final View child = this.getChildAt(1);
            if (child != null) {
                this.mPreservedOpenState = (this.mDragHelper.isViewUnder(child, (int)motionEvent.getX(), (int)motionEvent.getY()) ^ true);
            }
        }
        if (!this.mCanSlide || (this.mIsUnableToDrag && actionMasked != 0)) {
            this.mDragHelper.cancel();
            return super.onInterceptTouchEvent(motionEvent);
        }
        if (actionMasked != 3 && actionMasked != 1) {
            boolean b2 = false;
            Label_0251: {
                if (actionMasked != 0) {
                    if (actionMasked == 2) {
                        final float x = motionEvent.getX();
                        final float y = motionEvent.getY();
                        final float abs = Math.abs(x - this.mInitialMotionX);
                        final float abs2 = Math.abs(y - this.mInitialMotionY);
                        if (abs > this.mDragHelper.getTouchSlop() && abs2 > abs) {
                            this.mDragHelper.cancel();
                            this.mIsUnableToDrag = true;
                            return false;
                        }
                    }
                }
                else {
                    this.mIsUnableToDrag = false;
                    final float x2 = motionEvent.getX();
                    final float y2 = motionEvent.getY();
                    this.mInitialMotionX = x2;
                    this.mInitialMotionY = y2;
                    if (this.mDragHelper.isViewUnder(this.mSlideableView, (int)x2, (int)y2) && this.isDimmed(this.mSlideableView)) {
                        b2 = true;
                        break Label_0251;
                    }
                }
                b2 = false;
            }
            boolean b3 = b;
            if (!this.mDragHelper.shouldInterceptTouchEvent(motionEvent)) {
                b3 = (b2 && b);
            }
            return b3;
        }
        this.mDragHelper.cancel();
        return false;
    }
    
    protected void onLayout(final boolean b, int i, int mParallaxBy, int n, int n2) {
        final boolean layoutRtlSupport = this.isLayoutRtlSupport();
        if (layoutRtlSupport) {
            this.mDragHelper.setEdgeTrackingEnabled(2);
        }
        else {
            this.mDragHelper.setEdgeTrackingEnabled(1);
        }
        final int n3 = n - i;
        if (layoutRtlSupport) {
            i = this.getPaddingRight();
        }
        else {
            i = this.getPaddingLeft();
        }
        if (layoutRtlSupport) {
            n2 = this.getPaddingLeft();
        }
        else {
            n2 = this.getPaddingRight();
        }
        final int paddingTop = this.getPaddingTop();
        final int childCount = this.getChildCount();
        if (this.mFirstLayout) {
            float mSlideOffset;
            if (this.mCanSlide && this.mPreservedOpenState) {
                mSlideOffset = 1.0f;
            }
            else {
                mSlideOffset = 0.0f;
            }
            this.mSlideOffset = mSlideOffset;
        }
        mParallaxBy = i;
        for (int j = 0; j < childCount; ++j) {
            final View child = this.getChildAt(j);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                final int measuredWidth = child.getMeasuredWidth();
                Label_0356: {
                    if (layoutParams.slideable) {
                        n = layoutParams.leftMargin;
                        final int rightMargin = layoutParams.rightMargin;
                        final int n4 = n3 - n2;
                        final int mSlideRange = Math.min(i, n4 - this.mOverhangSize) - mParallaxBy - (n + rightMargin);
                        this.mSlideRange = mSlideRange;
                        if (layoutRtlSupport) {
                            n = layoutParams.rightMargin;
                        }
                        else {
                            n = layoutParams.leftMargin;
                        }
                        layoutParams.dimWhenOffset = (mParallaxBy + n + mSlideRange + measuredWidth / 2 > n4);
                        final int n5 = (int)(mSlideRange * this.mSlideOffset);
                        mParallaxBy += n + n5;
                        this.mSlideOffset = n5 / (float)this.mSlideRange;
                        n = 0;
                    }
                    else {
                        if (this.mCanSlide) {
                            mParallaxBy = this.mParallaxBy;
                            if (mParallaxBy != 0) {
                                n = (int)((1.0f - this.mSlideOffset) * mParallaxBy);
                                mParallaxBy = i;
                                break Label_0356;
                            }
                        }
                        mParallaxBy = i;
                        n = 0;
                    }
                }
                int n6;
                if (layoutRtlSupport) {
                    n += n3 - mParallaxBy;
                    n6 = n - measuredWidth;
                }
                else {
                    n6 = mParallaxBy - n;
                    n = n6 + measuredWidth;
                }
                child.layout(n6, paddingTop, n, child.getMeasuredHeight() + paddingTop);
                i += child.getWidth();
            }
        }
        if (this.mFirstLayout) {
            if (this.mCanSlide) {
                if (this.mParallaxBy != 0) {
                    this.parallaxOtherViews(this.mSlideOffset);
                }
                if (((LayoutParams)this.mSlideableView.getLayoutParams()).dimWhenOffset) {
                    this.dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
                }
            }
            else {
                for (i = 0; i < childCount; ++i) {
                    this.dimChildView(this.getChildAt(i), 0.0f, this.mSliderFadeColor);
                }
            }
            this.updateObscuredViewsVisibility(this.mSlideableView);
        }
        this.mFirstLayout = false;
    }
    
    protected void onMeasure(int n, int size) {
        final int mode = View$MeasureSpec.getMode(n);
        final int size2 = View$MeasureSpec.getSize(n);
        final int mode2 = View$MeasureSpec.getMode(size);
        size = View$MeasureSpec.getSize(size);
        int n2;
        int n3;
        if (mode != 1073741824) {
            if (!this.isInEditMode()) {
                throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
            }
            if (mode == Integer.MIN_VALUE) {
                n2 = size2;
                n3 = mode2;
                n = size;
            }
            else {
                n2 = size2;
                n3 = mode2;
                n = size;
                if (mode == 0) {
                    n2 = 300;
                    n3 = mode2;
                    n = size;
                }
            }
        }
        else {
            n2 = size2;
            n3 = mode2;
            n = size;
            if (mode2 == 0) {
                if (!this.isInEditMode()) {
                    throw new IllegalStateException("Height must not be UNSPECIFIED");
                }
                n2 = size2;
                n3 = mode2;
                n = size;
                if (mode2 == 0) {
                    n = 300;
                    n3 = Integer.MIN_VALUE;
                    n2 = size2;
                }
            }
        }
        int b;
        if (n3 != Integer.MIN_VALUE) {
            if (n3 != 1073741824) {
                n = 0;
            }
            else {
                n = n - this.getPaddingTop() - this.getPaddingBottom();
            }
            b = n;
        }
        else {
            b = n - this.getPaddingTop() - this.getPaddingBottom();
            n = 0;
        }
        final int n4 = n2 - this.getPaddingLeft() - this.getPaddingRight();
        final int childCount = this.getChildCount();
        if (childCount > 2) {
            Log.e("SlidingPaneLayout", "onMeasure: More than two child views are not supported.");
        }
        this.mSlideableView = null;
        boolean mCanSlide;
        int i = (mCanSlide = false) ? 1 : 0;
        int b2 = n4;
        float n5 = 0.0f;
        size = n;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            Label_0575: {
                if (child.getVisibility() == 8) {
                    layoutParams.dimWhenOffset = false;
                    n = size;
                }
                else {
                    final float weight = layoutParams.weight;
                    float n6 = n5;
                    if (weight > 0.0f) {
                        n5 = (n6 = n5 + weight);
                        if (layoutParams.width == 0) {
                            n = size;
                            break Label_0575;
                        }
                    }
                    n = layoutParams.leftMargin + layoutParams.rightMargin;
                    final int width = layoutParams.width;
                    int measureSpec = 0;
                    Label_0430: {
                        if (width == -2) {
                            n = View$MeasureSpec.makeMeasureSpec(n4 - n, Integer.MIN_VALUE);
                        }
                        else {
                            if (width != -1) {
                                measureSpec = View$MeasureSpec.makeMeasureSpec(width, 1073741824);
                                break Label_0430;
                            }
                            n = View$MeasureSpec.makeMeasureSpec(n4 - n, 1073741824);
                        }
                        measureSpec = n;
                    }
                    n = layoutParams.height;
                    if (n == -2) {
                        n = View$MeasureSpec.makeMeasureSpec(b, Integer.MIN_VALUE);
                    }
                    else if (n == -1) {
                        n = View$MeasureSpec.makeMeasureSpec(b, 1073741824);
                    }
                    else {
                        n = View$MeasureSpec.makeMeasureSpec(n, 1073741824);
                    }
                    child.measure(measureSpec, n);
                    final int measuredWidth = child.getMeasuredWidth();
                    final int measuredHeight = child.getMeasuredHeight();
                    n = size;
                    if (n3 == Integer.MIN_VALUE && measuredHeight > (n = size)) {
                        n = Math.min(measuredHeight, b);
                    }
                    b2 -= measuredWidth;
                    int slideable;
                    if (b2 < 0) {
                        slideable = 1;
                    }
                    else {
                        slideable = 0;
                    }
                    layoutParams.slideable = (slideable != 0);
                    mCanSlide |= (slideable != 0);
                    if (slideable != 0) {
                        this.mSlideableView = child;
                    }
                    n5 = n6;
                }
            }
            ++i;
            size = n;
        }
        if (mCanSlide || n5 > 0.0f) {
            final int n7 = n4 - this.mOverhangSize;
            for (int j = 0; j < childCount; ++j) {
                final View child2 = this.getChildAt(j);
                if (child2.getVisibility() != 8) {
                    final LayoutParams layoutParams2 = (LayoutParams)child2.getLayoutParams();
                    if (child2.getVisibility() != 8) {
                        if (layoutParams2.width == 0 && layoutParams2.weight > 0.0f) {
                            n = 1;
                        }
                        else {
                            n = 0;
                        }
                        int measuredWidth2;
                        if (n != 0) {
                            measuredWidth2 = 0;
                        }
                        else {
                            measuredWidth2 = child2.getMeasuredWidth();
                        }
                        if (mCanSlide && child2 != this.mSlideableView) {
                            if (layoutParams2.width < 0 && (measuredWidth2 > n7 || layoutParams2.weight > 0.0f)) {
                                if (n != 0) {
                                    n = layoutParams2.height;
                                    if (n == -2) {
                                        n = View$MeasureSpec.makeMeasureSpec(b, Integer.MIN_VALUE);
                                    }
                                    else if (n == -1) {
                                        n = View$MeasureSpec.makeMeasureSpec(b, 1073741824);
                                    }
                                    else {
                                        n = View$MeasureSpec.makeMeasureSpec(n, 1073741824);
                                    }
                                }
                                else {
                                    n = View$MeasureSpec.makeMeasureSpec(child2.getMeasuredHeight(), 1073741824);
                                }
                                child2.measure(View$MeasureSpec.makeMeasureSpec(n7, 1073741824), n);
                            }
                        }
                        else if (layoutParams2.weight > 0.0f) {
                            if (layoutParams2.width == 0) {
                                n = layoutParams2.height;
                                if (n == -2) {
                                    n = View$MeasureSpec.makeMeasureSpec(b, Integer.MIN_VALUE);
                                }
                                else if (n == -1) {
                                    n = View$MeasureSpec.makeMeasureSpec(b, 1073741824);
                                }
                                else {
                                    n = View$MeasureSpec.makeMeasureSpec(n, 1073741824);
                                }
                            }
                            else {
                                n = View$MeasureSpec.makeMeasureSpec(child2.getMeasuredHeight(), 1073741824);
                            }
                            if (mCanSlide) {
                                final int n8 = n4 - (layoutParams2.leftMargin + layoutParams2.rightMargin);
                                final int measureSpec2 = View$MeasureSpec.makeMeasureSpec(n8, 1073741824);
                                if (measuredWidth2 != n8) {
                                    child2.measure(measureSpec2, n);
                                }
                            }
                            else {
                                child2.measure(View$MeasureSpec.makeMeasureSpec(measuredWidth2 + (int)(layoutParams2.weight * Math.max(0, b2) / n5), 1073741824), n);
                            }
                        }
                    }
                }
            }
        }
        this.setMeasuredDimension(n2, size + this.getPaddingTop() + this.getPaddingBottom());
        this.mCanSlide = mCanSlide;
        if (this.mDragHelper.getViewDragState() != 0 && !mCanSlide) {
            this.mDragHelper.abort();
        }
    }
    
    void onPanelDragged(int n) {
        if (this.mSlideableView == null) {
            this.mSlideOffset = 0.0f;
            return;
        }
        final boolean layoutRtlSupport = this.isLayoutRtlSupport();
        final LayoutParams layoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
        final int width = this.mSlideableView.getWidth();
        int n2 = n;
        if (layoutRtlSupport) {
            n2 = this.getWidth() - n - width;
        }
        if (layoutRtlSupport) {
            n = this.getPaddingRight();
        }
        else {
            n = this.getPaddingLeft();
        }
        int n3;
        if (layoutRtlSupport) {
            n3 = layoutParams.rightMargin;
        }
        else {
            n3 = layoutParams.leftMargin;
        }
        final float mSlideOffset = (n2 - (n + n3)) / (float)this.mSlideRange;
        this.mSlideOffset = mSlideOffset;
        if (this.mParallaxBy != 0) {
            this.parallaxOtherViews(mSlideOffset);
        }
        if (layoutParams.dimWhenOffset) {
            this.dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
        }
        this.dispatchOnPanelSlide(this.mSlideableView);
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.isOpen) {
            this.openPane();
        }
        else {
            this.closePane();
        }
        this.mPreservedOpenState = savedState.isOpen;
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        boolean isOpen;
        if (this.isSlideable()) {
            isOpen = this.isOpen();
        }
        else {
            isOpen = this.mPreservedOpenState;
        }
        savedState.isOpen = isOpen;
        return (Parcelable)savedState;
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        if (n != n3) {
            this.mFirstLayout = true;
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (!this.mCanSlide) {
            return super.onTouchEvent(motionEvent);
        }
        this.mDragHelper.processTouchEvent(motionEvent);
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked == 1) {
                if (this.isDimmed(this.mSlideableView)) {
                    final float x = motionEvent.getX();
                    final float y = motionEvent.getY();
                    final float n = x - this.mInitialMotionX;
                    final float n2 = y - this.mInitialMotionY;
                    final int touchSlop = this.mDragHelper.getTouchSlop();
                    if (n * n + n2 * n2 < touchSlop * touchSlop && this.mDragHelper.isViewUnder(this.mSlideableView, (int)x, (int)y)) {
                        this.closePane(0);
                    }
                }
            }
        }
        else {
            final float x2 = motionEvent.getX();
            final float y2 = motionEvent.getY();
            this.mInitialMotionX = x2;
            this.mInitialMotionY = y2;
        }
        return true;
    }
    
    public boolean openPane() {
        return this.openPane(0);
    }
    
    public void requestChildFocus(final View view, final View view2) {
        super.requestChildFocus(view, view2);
        if (!this.isInTouchMode() && !this.mCanSlide) {
            this.mPreservedOpenState = (view == this.mSlideableView);
        }
    }
    
    void setAllChildrenVisible() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 4) {
                child.setVisibility(0);
            }
        }
    }
    
    boolean smoothSlideTo(final float n, int paddingRight) {
        if (!this.mCanSlide) {
            return false;
        }
        final boolean layoutRtlSupport = this.isLayoutRtlSupport();
        final LayoutParams layoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
        if (layoutRtlSupport) {
            paddingRight = this.getPaddingRight();
            paddingRight = (int)(this.getWidth() - (paddingRight + layoutParams.rightMargin + n * this.mSlideRange + this.mSlideableView.getWidth()));
        }
        else {
            paddingRight = (int)(this.getPaddingLeft() + layoutParams.leftMargin + n * this.mSlideRange);
        }
        final ViewDragHelper mDragHelper = this.mDragHelper;
        final View mSlideableView = this.mSlideableView;
        if (mDragHelper.smoothSlideViewTo(mSlideableView, paddingRight, mSlideableView.getTop())) {
            this.setAllChildrenVisible();
            ViewCompat.postInvalidateOnAnimation((View)this);
            return true;
        }
        return false;
    }
    
    void updateObscuredViewsVisibility(final View view) {
        final boolean layoutRtlSupport = this.isLayoutRtlSupport();
        int paddingLeft;
        if (layoutRtlSupport) {
            paddingLeft = this.getWidth() - this.getPaddingRight();
        }
        else {
            paddingLeft = this.getPaddingLeft();
        }
        int paddingLeft2;
        if (layoutRtlSupport) {
            paddingLeft2 = this.getPaddingLeft();
        }
        else {
            paddingLeft2 = this.getWidth() - this.getPaddingRight();
        }
        final int paddingTop = this.getPaddingTop();
        final int height = this.getHeight();
        final int paddingBottom = this.getPaddingBottom();
        int left;
        int right;
        int top;
        int bottom;
        if (view != null && viewIsOpaque(view)) {
            left = view.getLeft();
            right = view.getRight();
            top = view.getTop();
            bottom = view.getBottom();
        }
        else {
            left = 0;
            right = 0;
            top = 0;
            bottom = 0;
        }
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child == view) {
                break;
            }
            if (child.getVisibility() != 8) {
                int a;
                if (layoutRtlSupport) {
                    a = paddingLeft2;
                }
                else {
                    a = paddingLeft;
                }
                final int max = Math.max(a, child.getLeft());
                final int max2 = Math.max(paddingTop, child.getTop());
                int a2;
                if (layoutRtlSupport) {
                    a2 = paddingLeft;
                }
                else {
                    a2 = paddingLeft2;
                }
                final int min = Math.min(a2, child.getRight());
                final int min2 = Math.min(height - paddingBottom, child.getBottom());
                int visibility;
                if (max >= left && max2 >= top && min <= right && min2 <= bottom) {
                    visibility = 4;
                }
                else {
                    visibility = 0;
                }
                child.setVisibility(visibility);
            }
        }
    }
    
    class AccessibilityDelegate extends AccessibilityDelegateCompat
    {
        private final Rect mTmpRect;
        
        AccessibilityDelegate() {
            this.mTmpRect = new Rect();
        }
        
        private void copyNodeInfoNoChildren(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat2) {
            final Rect mTmpRect = this.mTmpRect;
            accessibilityNodeInfoCompat2.getBoundsInScreen(mTmpRect);
            accessibilityNodeInfoCompat.setBoundsInScreen(mTmpRect);
            accessibilityNodeInfoCompat.setVisibleToUser(accessibilityNodeInfoCompat2.isVisibleToUser());
            accessibilityNodeInfoCompat.setPackageName(accessibilityNodeInfoCompat2.getPackageName());
            accessibilityNodeInfoCompat.setClassName(accessibilityNodeInfoCompat2.getClassName());
            accessibilityNodeInfoCompat.setContentDescription(accessibilityNodeInfoCompat2.getContentDescription());
            accessibilityNodeInfoCompat.setEnabled(accessibilityNodeInfoCompat2.isEnabled());
            accessibilityNodeInfoCompat.setClickable(accessibilityNodeInfoCompat2.isClickable());
            accessibilityNodeInfoCompat.setFocusable(accessibilityNodeInfoCompat2.isFocusable());
            accessibilityNodeInfoCompat.setFocused(accessibilityNodeInfoCompat2.isFocused());
            accessibilityNodeInfoCompat.setAccessibilityFocused(accessibilityNodeInfoCompat2.isAccessibilityFocused());
            accessibilityNodeInfoCompat.setSelected(accessibilityNodeInfoCompat2.isSelected());
            accessibilityNodeInfoCompat.setLongClickable(accessibilityNodeInfoCompat2.isLongClickable());
            accessibilityNodeInfoCompat.addAction(accessibilityNodeInfoCompat2.getActions());
            accessibilityNodeInfoCompat.setMovementGranularities(accessibilityNodeInfoCompat2.getMovementGranularities());
        }
        
        public boolean filter(final View view) {
            return SlidingPaneLayout.this.isDimmed(view);
        }
        
        @Override
        public void onInitializeAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName((CharSequence)"androidx.slidingpanelayout.widget.SlidingPaneLayout");
        }
        
        @Override
        public void onInitializeAccessibilityNodeInfo(View child, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            final AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain(accessibilityNodeInfoCompat);
            super.onInitializeAccessibilityNodeInfo(child, obtain);
            this.copyNodeInfoNoChildren(accessibilityNodeInfoCompat, obtain);
            obtain.recycle();
            accessibilityNodeInfoCompat.setClassName("androidx.slidingpanelayout.widget.SlidingPaneLayout");
            accessibilityNodeInfoCompat.setSource(child);
            final ViewParent parentForAccessibility = ViewCompat.getParentForAccessibility(child);
            if (parentForAccessibility instanceof View) {
                accessibilityNodeInfoCompat.setParent((View)parentForAccessibility);
            }
            for (int childCount = SlidingPaneLayout.this.getChildCount(), i = 0; i < childCount; ++i) {
                child = SlidingPaneLayout.this.getChildAt(i);
                if (!this.filter(child) && child.getVisibility() == 0) {
                    ViewCompat.setImportantForAccessibility(child, 1);
                    accessibilityNodeInfoCompat.addChild(child);
                }
            }
        }
        
        @Override
        public boolean onRequestSendAccessibilityEvent(final ViewGroup viewGroup, final View view, final AccessibilityEvent accessibilityEvent) {
            return !this.filter(view) && super.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
        }
    }
    
    private class DisableLayerRunnable implements Runnable
    {
        final View mChildView;
        
        DisableLayerRunnable(final View mChildView) {
            this.mChildView = mChildView;
        }
        
        @Override
        public void run() {
            if (this.mChildView.getParent() == SlidingPaneLayout.this) {
                this.mChildView.setLayerType(0, (Paint)null);
                SlidingPaneLayout.this.invalidateChildRegion(this.mChildView);
            }
            SlidingPaneLayout.this.mPostedRunnables.remove(this);
        }
    }
    
    private class DragHelperCallback extends Callback
    {
        DragHelperCallback() {
        }
        
        @Override
        public int clampViewPositionHorizontal(final View view, int n, int n2) {
            final LayoutParams layoutParams = (LayoutParams)SlidingPaneLayout.this.mSlideableView.getLayoutParams();
            if (SlidingPaneLayout.this.isLayoutRtlSupport()) {
                n2 = SlidingPaneLayout.this.getWidth() - (SlidingPaneLayout.this.getPaddingRight() + layoutParams.rightMargin + SlidingPaneLayout.this.mSlideableView.getWidth());
                n = Math.max(Math.min(n, n2), n2 - SlidingPaneLayout.this.mSlideRange);
            }
            else {
                n2 = SlidingPaneLayout.this.getPaddingLeft() + layoutParams.leftMargin;
                n = Math.min(Math.max(n, n2), SlidingPaneLayout.this.mSlideRange + n2);
            }
            return n;
        }
        
        @Override
        public int clampViewPositionVertical(final View view, final int n, final int n2) {
            return view.getTop();
        }
        
        @Override
        public int getViewHorizontalDragRange(final View view) {
            return SlidingPaneLayout.this.mSlideRange;
        }
        
        @Override
        public void onEdgeDragStarted(final int n, final int n2) {
            final SlidingPaneLayout this$0 = SlidingPaneLayout.this;
            this$0.mDragHelper.captureChildView(this$0.mSlideableView, n2);
        }
        
        @Override
        public void onViewCaptured(final View view, final int n) {
            SlidingPaneLayout.this.setAllChildrenVisible();
        }
        
        @Override
        public void onViewDragStateChanged(final int n) {
            if (SlidingPaneLayout.this.mDragHelper.getViewDragState() == 0) {
                final SlidingPaneLayout this$0 = SlidingPaneLayout.this;
                if (this$0.mSlideOffset == 0.0f) {
                    this$0.updateObscuredViewsVisibility(this$0.mSlideableView);
                    final SlidingPaneLayout this$2 = SlidingPaneLayout.this;
                    this$2.dispatchOnPanelClosed(this$2.mSlideableView);
                    SlidingPaneLayout.this.mPreservedOpenState = false;
                }
                else {
                    this$0.dispatchOnPanelOpened(this$0.mSlideableView);
                    SlidingPaneLayout.this.mPreservedOpenState = true;
                }
            }
        }
        
        @Override
        public void onViewPositionChanged(final View view, final int n, final int n2, final int n3, final int n4) {
            SlidingPaneLayout.this.onPanelDragged(n);
            SlidingPaneLayout.this.invalidate();
        }
        
        @Override
        public void onViewReleased(final View view, final float n, final float n2) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            int n5 = 0;
            Label_0176: {
                if (SlidingPaneLayout.this.isLayoutRtlSupport()) {
                    final int n3 = SlidingPaneLayout.this.getPaddingRight() + layoutParams.rightMargin;
                    int n4 = 0;
                    Label_0079: {
                        if (n >= 0.0f) {
                            n4 = n3;
                            if (n != 0.0f) {
                                break Label_0079;
                            }
                            n4 = n3;
                            if (SlidingPaneLayout.this.mSlideOffset <= 0.5f) {
                                break Label_0079;
                            }
                        }
                        n4 = n3 + SlidingPaneLayout.this.mSlideRange;
                    }
                    n5 = SlidingPaneLayout.this.getWidth() - n4 - SlidingPaneLayout.this.mSlideableView.getWidth();
                }
                else {
                    final int n6 = layoutParams.leftMargin + SlidingPaneLayout.this.getPaddingLeft();
                    final float n7 = fcmpl(n, 0.0f);
                    if (n7 <= 0) {
                        n5 = n6;
                        if (n7 != 0) {
                            break Label_0176;
                        }
                        n5 = n6;
                        if (SlidingPaneLayout.this.mSlideOffset <= 0.5f) {
                            break Label_0176;
                        }
                    }
                    n5 = n6 + SlidingPaneLayout.this.mSlideRange;
                }
            }
            SlidingPaneLayout.this.mDragHelper.settleCapturedViewAt(n5, view.getTop());
            SlidingPaneLayout.this.invalidate();
        }
        
        @Override
        public boolean tryCaptureView(final View view, final int n) {
            return !SlidingPaneLayout.this.mIsUnableToDrag && ((LayoutParams)view.getLayoutParams()).slideable;
        }
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        private static final int[] ATTRS;
        Paint dimPaint;
        boolean dimWhenOffset;
        boolean slideable;
        public float weight;
        
        static {
            ATTRS = new int[] { 16843137 };
        }
        
        public LayoutParams() {
            super(-1, -1);
            this.weight = 0.0f;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.weight = 0.0f;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, LayoutParams.ATTRS);
            this.weight = obtainStyledAttributes.getFloat(0, 0.0f);
            obtainStyledAttributes.recycle();
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.weight = 0.0f;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.weight = 0.0f;
        }
    }
    
    public interface PanelSlideListener
    {
        void onPanelClosed(final View p0);
        
        void onPanelOpened(final View p0);
        
        void onPanelSlide(final View p0, final float p1);
    }
    
    static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        boolean isOpen;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$ClassLoaderCreator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel, null);
                }
                
                public SavedState createFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                    return new SavedState(parcel, null);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState(final Parcel parcel, final ClassLoader classLoader) {
            super(parcel, classLoader);
            this.isOpen = (parcel.readInt() != 0);
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt((int)(this.isOpen ? 1 : 0));
        }
    }
}
