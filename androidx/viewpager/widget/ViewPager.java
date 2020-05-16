// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager.widget;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import androidx.customview.view.AbsSavedState;
import android.os.Bundle;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import android.content.res.TypedArray;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import java.lang.annotation.Annotation;
import android.database.DataSetObserver;
import android.content.res.Resources$NotFoundException;
import android.view.View$MeasureSpec;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.AccessibilityDelegateCompat;
import android.view.ViewConfiguration;
import android.graphics.Canvas;
import android.view.accessibility.AccessibilityEvent;
import android.view.KeyEvent;
import android.os.SystemClock;
import android.view.SoundEffectConstants;
import android.view.FocusFinder;
import android.util.Log;
import android.view.ViewGroup$LayoutParams;
import java.util.Collections;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.graphics.Paint;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.content.Context;
import android.view.VelocityTracker;
import android.graphics.Rect;
import android.widget.Scroller;
import android.os.Parcelable;
import android.graphics.drawable.Drawable;
import android.widget.EdgeEffect;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import android.view.animation.Interpolator;
import java.util.Comparator;
import android.view.ViewGroup;

public class ViewPager extends ViewGroup
{
    private static final Comparator<ItemInfo> COMPARATOR;
    static final int[] LAYOUT_ATTRS;
    private static final Interpolator sInterpolator;
    private static final ViewPositionComparator sPositionComparator;
    private int mActivePointerId;
    PagerAdapter mAdapter;
    private List<OnAdapterChangeListener> mAdapterChangeListeners;
    private int mBottomPageBounds;
    private boolean mCalledSuper;
    private int mCloseEnough;
    int mCurItem;
    private int mDecorChildCount;
    private int mDefaultGutterSize;
    private boolean mDragInGutterEnabled;
    private int mDrawingOrder;
    private ArrayList<View> mDrawingOrderedChildren;
    private final Runnable mEndScrollRunnable;
    private int mExpectedAdapterCount;
    private long mFakeDragBeginTime;
    private boolean mFakeDragging;
    private boolean mFirstLayout;
    private float mFirstOffset;
    private int mFlingDistance;
    private int mGutterSize;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private OnPageChangeListener mInternalPageChangeListener;
    private boolean mIsBeingDragged;
    private boolean mIsScrollStarted;
    private boolean mIsUnableToDrag;
    private final ArrayList<ItemInfo> mItems;
    private float mLastMotionX;
    private float mLastMotionY;
    private float mLastOffset;
    private EdgeEffect mLeftEdge;
    private Drawable mMarginDrawable;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private PagerObserver mObserver;
    private int mOffscreenPageLimit;
    private OnPageChangeListener mOnPageChangeListener;
    private List<OnPageChangeListener> mOnPageChangeListeners;
    private int mPageMargin;
    private PageTransformer mPageTransformer;
    private int mPageTransformerLayerType;
    private boolean mPopulatePending;
    private Parcelable mRestoredAdapterState;
    private ClassLoader mRestoredClassLoader;
    private int mRestoredCurItem;
    private EdgeEffect mRightEdge;
    private int mScrollState;
    private Scroller mScroller;
    private boolean mScrollingCacheEnabled;
    private final ItemInfo mTempItem;
    private final Rect mTempRect;
    private int mTopPageBounds;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    
    static {
        LAYOUT_ATTRS = new int[] { 16842931 };
        COMPARATOR = new Comparator<ItemInfo>() {
            @Override
            public int compare(final ItemInfo itemInfo, final ItemInfo itemInfo2) {
                return itemInfo.position - itemInfo2.position;
            }
        };
        sInterpolator = (Interpolator)new Interpolator() {
            public float getInterpolation(float n) {
                --n;
                return n * n * n * n * n + 1.0f;
            }
        };
        sPositionComparator = new ViewPositionComparator();
    }
    
    public ViewPager(final Context context) {
        super(context);
        this.mItems = new ArrayList<ItemInfo>();
        this.mTempItem = new ItemInfo();
        this.mTempRect = new Rect();
        this.mRestoredCurItem = -1;
        this.mRestoredAdapterState = null;
        this.mRestoredClassLoader = null;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        this.mOffscreenPageLimit = 1;
        this.mDragInGutterEnabled = true;
        this.mActivePointerId = -1;
        this.mFirstLayout = true;
        this.mEndScrollRunnable = new Runnable() {
            @Override
            public void run() {
                ViewPager.this.setScrollState(0);
                ViewPager.this.populate();
            }
        };
        this.mScrollState = 0;
        this.initViewPager();
    }
    
    public ViewPager(final Context context, final AttributeSet set) {
        super(context, set);
        this.mItems = new ArrayList<ItemInfo>();
        this.mTempItem = new ItemInfo();
        this.mTempRect = new Rect();
        this.mRestoredCurItem = -1;
        this.mRestoredAdapterState = null;
        this.mRestoredClassLoader = null;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        this.mOffscreenPageLimit = 1;
        this.mDragInGutterEnabled = true;
        this.mActivePointerId = -1;
        this.mFirstLayout = true;
        this.mEndScrollRunnable = new Runnable() {
            @Override
            public void run() {
                ViewPager.this.setScrollState(0);
                ViewPager.this.populate();
            }
        };
        this.mScrollState = 0;
        this.initViewPager();
    }
    
    private void calculatePageOffsets(ItemInfo itemInfo, int n, ItemInfo itemInfo2) {
        int count = this.mAdapter.getCount();
        final int clientWidth = this.getClientWidth();
        float n2;
        if (clientWidth > 0) {
            n2 = this.mPageMargin / (float)clientWidth;
        }
        else {
            n2 = 0.0f;
        }
        if (itemInfo2 != null) {
            int i = itemInfo2.position;
            final int position = itemInfo.position;
            if (i < position) {
                int n3 = 0;
                float n4 = itemInfo2.offset + itemInfo2.widthFactor + n2;
                while (true) {
                    final int n5 = i + 1;
                    if (n5 > itemInfo.position || n3 >= this.mItems.size()) {
                        break;
                    }
                    itemInfo2 = this.mItems.get(n3);
                    float offset;
                    while (true) {
                        i = n5;
                        offset = n4;
                        if (n5 <= itemInfo2.position) {
                            break;
                        }
                        i = n5;
                        offset = n4;
                        if (n3 >= this.mItems.size() - 1) {
                            break;
                        }
                        ++n3;
                        itemInfo2 = this.mItems.get(n3);
                    }
                    while (i < itemInfo2.position) {
                        offset += this.mAdapter.getPageWidth(i) + n2;
                        ++i;
                    }
                    itemInfo2.offset = offset;
                    n4 = offset + (itemInfo2.widthFactor + n2);
                }
            }
            else if (i > position) {
                int n6 = this.mItems.size() - 1;
                float offset2 = itemInfo2.offset;
                --i;
                while (i >= itemInfo.position && n6 >= 0) {
                    itemInfo2 = this.mItems.get(n6);
                    int j;
                    float n7;
                    while (true) {
                        j = i;
                        n7 = offset2;
                        if (i >= itemInfo2.position) {
                            break;
                        }
                        j = i;
                        n7 = offset2;
                        if (n6 <= 0) {
                            break;
                        }
                        --n6;
                        itemInfo2 = this.mItems.get(n6);
                    }
                    while (j > itemInfo2.position) {
                        n7 -= this.mAdapter.getPageWidth(j) + n2;
                        --j;
                    }
                    offset2 = n7 - (itemInfo2.widthFactor + n2);
                    itemInfo2.offset = offset2;
                    i = j - 1;
                }
            }
        }
        final int size = this.mItems.size();
        float offset3 = itemInfo.offset;
        final int position2 = itemInfo.position;
        int n8 = position2 - 1;
        float mFirstOffset;
        if (position2 == 0) {
            mFirstOffset = offset3;
        }
        else {
            mFirstOffset = -3.4028235E38f;
        }
        this.mFirstOffset = mFirstOffset;
        final int position3 = itemInfo.position;
        --count;
        float mLastOffset;
        if (position3 == count) {
            mLastOffset = itemInfo.offset + itemInfo.widthFactor - 1.0f;
        }
        else {
            mLastOffset = Float.MAX_VALUE;
        }
        this.mLastOffset = mLastOffset;
        for (int k = n - 1; k >= 0; --k, --n8) {
            itemInfo2 = this.mItems.get(k);
            int position4;
            while (true) {
                position4 = itemInfo2.position;
                if (n8 <= position4) {
                    break;
                }
                offset3 -= this.mAdapter.getPageWidth(n8) + n2;
                --n8;
            }
            offset3 -= itemInfo2.widthFactor + n2;
            itemInfo2.offset = offset3;
            if (position4 == 0) {
                this.mFirstOffset = offset3;
            }
        }
        float offset4 = itemInfo.offset + itemInfo.widthFactor + n2;
        final int n9 = itemInfo.position + 1;
        int l;
        int position5;
        for (l = n + 1, n = n9; l < size; ++l, ++n) {
            itemInfo = this.mItems.get(l);
            while (true) {
                position5 = itemInfo.position;
                if (n >= position5) {
                    break;
                }
                offset4 += this.mAdapter.getPageWidth(n) + n2;
                ++n;
            }
            if (position5 == count) {
                this.mLastOffset = itemInfo.widthFactor + offset4 - 1.0f;
            }
            itemInfo.offset = offset4;
            offset4 += itemInfo.widthFactor + n2;
        }
    }
    
    private void completeScroll(final boolean b) {
        int n;
        if (this.mScrollState == 2) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n != 0) {
            this.setScrollingCacheEnabled(false);
            if (this.mScroller.isFinished() ^ true) {
                this.mScroller.abortAnimation();
                final int scrollX = this.getScrollX();
                final int scrollY = this.getScrollY();
                final int currX = this.mScroller.getCurrX();
                final int currY = this.mScroller.getCurrY();
                if (scrollX != currX || scrollY != currY) {
                    this.scrollTo(currX, currY);
                    if (currX != scrollX) {
                        this.pageScrolled(currX);
                    }
                }
            }
        }
        this.mPopulatePending = false;
        for (int i = 0; i < this.mItems.size(); ++i) {
            final ItemInfo itemInfo = this.mItems.get(i);
            if (itemInfo.scrolling) {
                itemInfo.scrolling = false;
                n = 1;
            }
        }
        if (n != 0) {
            if (b) {
                ViewCompat.postOnAnimation((View)this, this.mEndScrollRunnable);
            }
            else {
                this.mEndScrollRunnable.run();
            }
        }
    }
    
    private int determineTargetPage(int a, final float n, int max, final int a2) {
        if (Math.abs(a2) > this.mFlingDistance && Math.abs(max) > this.mMinimumVelocity) {
            if (max <= 0) {
                ++a;
            }
        }
        else {
            float n2;
            if (a >= this.mCurItem) {
                n2 = 0.4f;
            }
            else {
                n2 = 0.6f;
            }
            a += (int)(n + n2);
        }
        max = a;
        if (this.mItems.size() > 0) {
            final ItemInfo itemInfo = this.mItems.get(0);
            final ArrayList<ItemInfo> mItems = this.mItems;
            max = Math.max(itemInfo.position, Math.min(a, mItems.get(mItems.size() - 1).position));
        }
        return max;
    }
    
    private void dispatchOnPageScrolled(final int n, final float n2, final int n3) {
        final OnPageChangeListener mOnPageChangeListener = this.mOnPageChangeListener;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(n, n2, n3);
        }
        final List<OnPageChangeListener> mOnPageChangeListeners = this.mOnPageChangeListeners;
        if (mOnPageChangeListeners != null) {
            for (int i = 0; i < mOnPageChangeListeners.size(); ++i) {
                final OnPageChangeListener onPageChangeListener = this.mOnPageChangeListeners.get(i);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(n, n2, n3);
                }
            }
        }
        final OnPageChangeListener mInternalPageChangeListener = this.mInternalPageChangeListener;
        if (mInternalPageChangeListener != null) {
            mInternalPageChangeListener.onPageScrolled(n, n2, n3);
        }
    }
    
    private void dispatchOnPageSelected(final int n) {
        final OnPageChangeListener mOnPageChangeListener = this.mOnPageChangeListener;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(n);
        }
        final List<OnPageChangeListener> mOnPageChangeListeners = this.mOnPageChangeListeners;
        if (mOnPageChangeListeners != null) {
            for (int i = 0; i < mOnPageChangeListeners.size(); ++i) {
                final OnPageChangeListener onPageChangeListener = this.mOnPageChangeListeners.get(i);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(n);
                }
            }
        }
        final OnPageChangeListener mInternalPageChangeListener = this.mInternalPageChangeListener;
        if (mInternalPageChangeListener != null) {
            mInternalPageChangeListener.onPageSelected(n);
        }
    }
    
    private void dispatchOnScrollStateChanged(final int n) {
        final OnPageChangeListener mOnPageChangeListener = this.mOnPageChangeListener;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(n);
        }
        final List<OnPageChangeListener> mOnPageChangeListeners = this.mOnPageChangeListeners;
        if (mOnPageChangeListeners != null) {
            for (int i = 0; i < mOnPageChangeListeners.size(); ++i) {
                final OnPageChangeListener onPageChangeListener = this.mOnPageChangeListeners.get(i);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(n);
                }
            }
        }
        final OnPageChangeListener mInternalPageChangeListener = this.mInternalPageChangeListener;
        if (mInternalPageChangeListener != null) {
            mInternalPageChangeListener.onPageScrollStateChanged(n);
        }
    }
    
    private void enableLayers(final boolean b) {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            int mPageTransformerLayerType;
            if (b) {
                mPageTransformerLayerType = this.mPageTransformerLayerType;
            }
            else {
                mPageTransformerLayerType = 0;
            }
            this.getChildAt(i).setLayerType(mPageTransformerLayerType, (Paint)null);
        }
    }
    
    private void endDrag() {
        this.mIsBeingDragged = false;
        this.mIsUnableToDrag = false;
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
    
    private Rect getChildRectInPagerCoordinates(final Rect rect, final View view) {
        Rect rect2 = rect;
        if (rect == null) {
            rect2 = new Rect();
        }
        if (view == null) {
            rect2.set(0, 0, 0, 0);
            return rect2;
        }
        rect2.left = view.getLeft();
        rect2.right = view.getRight();
        rect2.top = view.getTop();
        rect2.bottom = view.getBottom();
        ViewPager viewPager;
        for (ViewParent viewParent = view.getParent(); viewParent instanceof ViewGroup && viewParent != this; viewParent = viewPager.getParent()) {
            viewPager = (ViewPager)viewParent;
            rect2.left += viewPager.getLeft();
            rect2.right += viewPager.getRight();
            rect2.top += viewPager.getTop();
            rect2.bottom += viewPager.getBottom();
        }
        return rect2;
    }
    
    private int getClientWidth() {
        return this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight();
    }
    
    private ItemInfo infoForCurrentScrollPosition() {
        final int clientWidth = this.getClientWidth();
        float offset = 0.0f;
        float n;
        if (clientWidth > 0) {
            n = this.getScrollX() / (float)clientWidth;
        }
        else {
            n = 0.0f;
        }
        float n2;
        if (clientWidth > 0) {
            n2 = this.mPageMargin / (float)clientWidth;
        }
        else {
            n2 = 0.0f;
        }
        ItemInfo itemInfo = null;
        int i = 0;
        int position = -1;
        int n3 = 1;
        float widthFactor = 0.0f;
        while (i < this.mItems.size()) {
            final ItemInfo itemInfo2 = this.mItems.get(i);
            int n4 = i;
            ItemInfo mTempItem = itemInfo2;
            if (n3 == 0) {
                final int position2 = itemInfo2.position;
                ++position;
                n4 = i;
                mTempItem = itemInfo2;
                if (position2 != position) {
                    mTempItem = this.mTempItem;
                    mTempItem.offset = offset + widthFactor + n2;
                    mTempItem.position = position;
                    mTempItem.widthFactor = this.mAdapter.getPageWidth(position);
                    n4 = i - 1;
                }
            }
            offset = mTempItem.offset;
            final float widthFactor2 = mTempItem.widthFactor;
            if (n3 == 0 && n < offset) {
                return itemInfo;
            }
            if (n < widthFactor2 + offset + n2 || n4 == this.mItems.size() - 1) {
                return mTempItem;
            }
            position = mTempItem.position;
            widthFactor = mTempItem.widthFactor;
            i = n4 + 1;
            n3 = 0;
            itemInfo = mTempItem;
        }
        return itemInfo;
    }
    
    private static boolean isDecorView(final View view) {
        return view.getClass().getAnnotation(DecorView.class) != null;
    }
    
    private boolean isGutterDrag(final float n, final float n2) {
        final boolean mDragInGutterEnabled = this.mDragInGutterEnabled;
        final boolean b = false;
        if (mDragInGutterEnabled) {
            return false;
        }
        if (n >= this.mGutterSize || n2 <= 0.0f) {
            boolean b2 = b;
            if (n <= this.getWidth() - this.mGutterSize) {
                return b2;
            }
            b2 = b;
            if (n2 >= 0.0f) {
                return b2;
            }
        }
        return true;
    }
    
    private void onSecondaryPointerUp(final MotionEvent motionEvent) {
        final int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
            int n;
            if (actionIndex == 0) {
                n = 1;
            }
            else {
                n = 0;
            }
            this.mLastMotionX = motionEvent.getX(n);
            this.mActivePointerId = motionEvent.getPointerId(n);
            final VelocityTracker mVelocityTracker = this.mVelocityTracker;
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }
    
    private boolean pageScrolled(int n) {
        if (this.mItems.size() == 0) {
            if (this.mFirstLayout) {
                return false;
            }
            this.mCalledSuper = false;
            this.onPageScrolled(0, 0.0f, 0);
            if (this.mCalledSuper) {
                return false;
            }
            throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        }
        else {
            final ItemInfo infoForCurrentScrollPosition = this.infoForCurrentScrollPosition();
            final int clientWidth = this.getClientWidth();
            final int mPageMargin = this.mPageMargin;
            final float n2 = (float)mPageMargin;
            final float n3 = (float)clientWidth;
            final float n4 = n2 / n3;
            final int position = infoForCurrentScrollPosition.position;
            final float n5 = (n / n3 - infoForCurrentScrollPosition.offset) / (infoForCurrentScrollPosition.widthFactor + n4);
            n = (int)((clientWidth + mPageMargin) * n5);
            this.mCalledSuper = false;
            this.onPageScrolled(position, n5, n);
            if (this.mCalledSuper) {
                return true;
            }
            throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        }
    }
    
    private boolean performDrag(float mLastMotionX) {
        final float mLastMotionX2 = this.mLastMotionX;
        this.mLastMotionX = mLastMotionX;
        final float n = this.getScrollX() + (mLastMotionX2 - mLastMotionX);
        final float n2 = (float)this.getClientWidth();
        mLastMotionX = this.mFirstOffset * n2;
        float n3 = this.mLastOffset * n2;
        final ArrayList<ItemInfo> mItems = this.mItems;
        final boolean b = false;
        final boolean b2 = false;
        boolean b3 = false;
        final ItemInfo itemInfo = mItems.get(0);
        final ArrayList<ItemInfo> mItems2 = this.mItems;
        final ItemInfo itemInfo2 = mItems2.get(mItems2.size() - 1);
        boolean b4;
        if (itemInfo.position != 0) {
            mLastMotionX = itemInfo.offset * n2;
            b4 = false;
        }
        else {
            b4 = true;
        }
        boolean b5;
        if (itemInfo2.position != this.mAdapter.getCount() - 1) {
            n3 = itemInfo2.offset * n2;
            b5 = false;
        }
        else {
            b5 = true;
        }
        if (n < mLastMotionX) {
            if (b4) {
                this.mLeftEdge.onPull(Math.abs(mLastMotionX - n) / n2);
                b3 = true;
            }
        }
        else {
            b3 = b2;
            mLastMotionX = n;
            if (n > n3) {
                b3 = b;
                if (b5) {
                    this.mRightEdge.onPull(Math.abs(n - n3) / n2);
                    b3 = true;
                }
                mLastMotionX = n3;
            }
        }
        final float mLastMotionX3 = this.mLastMotionX;
        final int n4 = (int)mLastMotionX;
        this.mLastMotionX = mLastMotionX3 + (mLastMotionX - n4);
        this.scrollTo(n4, this.getScrollY());
        this.pageScrolled(n4);
        return b3;
    }
    
    private void recomputeScrollPosition(int n, final int n2, final int n3, final int n4) {
        if (n2 > 0 && !this.mItems.isEmpty()) {
            if (!this.mScroller.isFinished()) {
                this.mScroller.setFinalX(this.getCurrentItem() * this.getClientWidth());
            }
            else {
                this.scrollTo((int)(this.getScrollX() / (float)(n2 - this.getPaddingLeft() - this.getPaddingRight() + n4) * (n - this.getPaddingLeft() - this.getPaddingRight() + n3)), this.getScrollY());
            }
        }
        else {
            final ItemInfo infoForPosition = this.infoForPosition(this.mCurItem);
            float min;
            if (infoForPosition != null) {
                min = Math.min(infoForPosition.offset, this.mLastOffset);
            }
            else {
                min = 0.0f;
            }
            n = (int)(min * (n - this.getPaddingLeft() - this.getPaddingRight()));
            if (n != this.getScrollX()) {
                this.completeScroll(false);
                this.scrollTo(n, this.getScrollY());
            }
        }
    }
    
    private void removeNonDecorViews() {
        int n;
        for (int i = 0; i < this.getChildCount(); i = n + 1) {
            n = i;
            if (!((LayoutParams)this.getChildAt(i).getLayoutParams()).isDecor) {
                this.removeViewAt(i);
                n = i - 1;
            }
        }
    }
    
    private void requestParentDisallowInterceptTouchEvent(final boolean b) {
        final ViewParent parent = this.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(b);
        }
    }
    
    private boolean resetTouch() {
        this.mActivePointerId = -1;
        this.endDrag();
        this.mLeftEdge.onRelease();
        this.mRightEdge.onRelease();
        return this.mLeftEdge.isFinished() || this.mRightEdge.isFinished();
    }
    
    private void scrollToItem(final int n, final boolean b, final int n2, final boolean b2) {
        final ItemInfo infoForPosition = this.infoForPosition(n);
        int n3;
        if (infoForPosition != null) {
            n3 = (int)(this.getClientWidth() * Math.max(this.mFirstOffset, Math.min(infoForPosition.offset, this.mLastOffset)));
        }
        else {
            n3 = 0;
        }
        if (b) {
            this.smoothScrollTo(n3, 0, n2);
            if (b2) {
                this.dispatchOnPageSelected(n);
            }
        }
        else {
            if (b2) {
                this.dispatchOnPageSelected(n);
            }
            this.completeScroll(false);
            this.scrollTo(n3, 0);
            this.pageScrolled(n3);
        }
    }
    
    private void setScrollingCacheEnabled(final boolean mScrollingCacheEnabled) {
        if (this.mScrollingCacheEnabled != mScrollingCacheEnabled) {
            this.mScrollingCacheEnabled = mScrollingCacheEnabled;
        }
    }
    
    private void sortChildDrawingOrder() {
        if (this.mDrawingOrder != 0) {
            final ArrayList<View> mDrawingOrderedChildren = this.mDrawingOrderedChildren;
            if (mDrawingOrderedChildren == null) {
                this.mDrawingOrderedChildren = new ArrayList<View>();
            }
            else {
                mDrawingOrderedChildren.clear();
            }
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                this.mDrawingOrderedChildren.add(this.getChildAt(i));
            }
            Collections.sort(this.mDrawingOrderedChildren, ViewPager.sPositionComparator);
        }
    }
    
    public void addFocusables(final ArrayList<View> list, final int n, final int n2) {
        final int size = list.size();
        final int descendantFocusability = this.getDescendantFocusability();
        if (descendantFocusability != 393216) {
            for (int i = 0; i < this.getChildCount(); ++i) {
                final View child = this.getChildAt(i);
                if (child.getVisibility() == 0) {
                    final ItemInfo infoForChild = this.infoForChild(child);
                    if (infoForChild != null && infoForChild.position == this.mCurItem) {
                        child.addFocusables((ArrayList)list, n, n2);
                    }
                }
            }
        }
        if (descendantFocusability != 262144 || size == list.size()) {
            if (!this.isFocusable()) {
                return;
            }
            if ((n2 & 0x1) == 0x1 && this.isInTouchMode() && !this.isFocusableInTouchMode()) {
                return;
            }
            if (list != null) {
                list.add((View)this);
            }
        }
    }
    
    ItemInfo addNewItem(final int position, final int index) {
        final ItemInfo itemInfo = new ItemInfo();
        itemInfo.position = position;
        itemInfo.object = this.mAdapter.instantiateItem(this, position);
        itemInfo.widthFactor = this.mAdapter.getPageWidth(position);
        if (index >= 0 && index < this.mItems.size()) {
            this.mItems.add(index, itemInfo);
        }
        else {
            this.mItems.add(itemInfo);
        }
        return itemInfo;
    }
    
    public void addOnAdapterChangeListener(final OnAdapterChangeListener onAdapterChangeListener) {
        if (this.mAdapterChangeListeners == null) {
            this.mAdapterChangeListeners = new ArrayList<OnAdapterChangeListener>();
        }
        this.mAdapterChangeListeners.add(onAdapterChangeListener);
    }
    
    public void addTouchables(final ArrayList<View> list) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 0) {
                final ItemInfo infoForChild = this.infoForChild(child);
                if (infoForChild != null && infoForChild.position == this.mCurItem) {
                    child.addTouchables((ArrayList)list);
                }
            }
        }
    }
    
    public void addView(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        ViewGroup$LayoutParams generateLayoutParams = viewGroup$LayoutParams;
        if (!this.checkLayoutParams(viewGroup$LayoutParams)) {
            generateLayoutParams = this.generateLayoutParams(viewGroup$LayoutParams);
        }
        final LayoutParams layoutParams = (LayoutParams)generateLayoutParams;
        final boolean isDecor = layoutParams.isDecor | isDecorView(view);
        layoutParams.isDecor = isDecor;
        if (this.mInLayout) {
            if (layoutParams != null && isDecor) {
                throw new IllegalStateException("Cannot add pager decor view during layout");
            }
            layoutParams.needsMeasure = true;
            this.addViewInLayout(view, n, generateLayoutParams);
        }
        else {
            super.addView(view, n, generateLayoutParams);
        }
    }
    
    public boolean arrowScroll(final int n) {
        final View focus = this.findFocus();
        boolean b = false;
        View view = null;
        Label_0189: {
            Label_0012: {
                if (focus != this) {
                    if ((view = focus) != null) {
                        ViewParent viewParent = focus.getParent();
                        while (true) {
                            while (viewParent instanceof ViewGroup) {
                                if (viewParent == this) {
                                    final boolean b2 = true;
                                    view = focus;
                                    if (!b2) {
                                        final StringBuilder sb = new StringBuilder();
                                        sb.append(focus.getClass().getSimpleName());
                                        for (ViewParent viewParent2 = focus.getParent(); viewParent2 instanceof ViewGroup; viewParent2 = viewParent2.getParent()) {
                                            sb.append(" => ");
                                            sb.append(viewParent2.getClass().getSimpleName());
                                        }
                                        final StringBuilder sb2 = new StringBuilder();
                                        sb2.append("arrowScroll tried to find focus based on non-child current focused view ");
                                        sb2.append(sb.toString());
                                        Log.e("ViewPager", sb2.toString());
                                        break Label_0012;
                                    }
                                    break Label_0189;
                                }
                                else {
                                    viewParent = viewParent.getParent();
                                }
                            }
                            final boolean b2 = false;
                            continue;
                        }
                    }
                    break Label_0189;
                }
            }
            view = null;
        }
        final View nextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup)this, view, n);
        if (nextFocus != null && nextFocus != view) {
            if (n == 17) {
                final int left = this.getChildRectInPagerCoordinates(this.mTempRect, nextFocus).left;
                final int left2 = this.getChildRectInPagerCoordinates(this.mTempRect, view).left;
                if (view != null && left >= left2) {
                    b = this.pageLeft();
                }
                else {
                    b = nextFocus.requestFocus();
                }
            }
            else if (n == 66) {
                final int left3 = this.getChildRectInPagerCoordinates(this.mTempRect, nextFocus).left;
                final int left4 = this.getChildRectInPagerCoordinates(this.mTempRect, view).left;
                if (view != null && left3 <= left4) {
                    b = this.pageRight();
                }
                else {
                    b = nextFocus.requestFocus();
                }
            }
        }
        else if (n != 17 && n != 1) {
            if (n == 66 || n == 2) {
                b = this.pageRight();
            }
        }
        else {
            b = this.pageLeft();
        }
        if (b) {
            this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(n));
        }
        return b;
    }
    
    public boolean beginFakeDrag() {
        if (this.mIsBeingDragged) {
            return false;
        }
        this.mFakeDragging = true;
        this.setScrollState(1);
        this.mLastMotionX = 0.0f;
        this.mInitialMotionX = 0.0f;
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        else {
            mVelocityTracker.clear();
        }
        final long uptimeMillis = SystemClock.uptimeMillis();
        final MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, 0.0f, 0.0f, 0);
        this.mVelocityTracker.addMovement(obtain);
        obtain.recycle();
        this.mFakeDragBeginTime = uptimeMillis;
        return true;
    }
    
    protected boolean canScroll(final View view, final boolean b, final int n, final int n2, final int n3) {
        final boolean b2 = view instanceof ViewGroup;
        final boolean b3 = true;
        if (b2) {
            final ViewGroup viewGroup = (ViewGroup)view;
            final int scrollX = view.getScrollX();
            final int scrollY = view.getScrollY();
            for (int i = viewGroup.getChildCount() - 1; i >= 0; --i) {
                final View child = viewGroup.getChildAt(i);
                final int n4 = n2 + scrollX;
                if (n4 >= child.getLeft() && n4 < child.getRight()) {
                    final int n5 = n3 + scrollY;
                    if (n5 >= child.getTop() && n5 < child.getBottom() && this.canScroll(child, true, n, n4 - child.getLeft(), n5 - child.getTop())) {
                        return true;
                    }
                }
            }
        }
        return b && view.canScrollHorizontally(-n) && b3;
    }
    
    public boolean canScrollHorizontally(final int n) {
        final PagerAdapter mAdapter = this.mAdapter;
        final boolean b = false;
        boolean b2 = false;
        if (mAdapter == null) {
            return false;
        }
        final int clientWidth = this.getClientWidth();
        final int scrollX = this.getScrollX();
        if (n < 0) {
            if (scrollX > (int)(clientWidth * this.mFirstOffset)) {
                b2 = true;
            }
            return b2;
        }
        boolean b3 = b;
        if (n > 0) {
            b3 = b;
            if (scrollX < (int)(clientWidth * this.mLastOffset)) {
                b3 = true;
            }
        }
        return b3;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams && super.checkLayoutParams(viewGroup$LayoutParams);
    }
    
    public void computeScroll() {
        this.mIsScrollStarted = true;
        if (!this.mScroller.isFinished() && this.mScroller.computeScrollOffset()) {
            final int scrollX = this.getScrollX();
            final int scrollY = this.getScrollY();
            final int currX = this.mScroller.getCurrX();
            final int currY = this.mScroller.getCurrY();
            if (scrollX != currX || scrollY != currY) {
                this.scrollTo(currX, currY);
                if (!this.pageScrolled(currX)) {
                    this.mScroller.abortAnimation();
                    this.scrollTo(0, currY);
                }
            }
            ViewCompat.postInvalidateOnAnimation((View)this);
            return;
        }
        this.completeScroll(true);
    }
    
    void dataSetChanged() {
        final int count = this.mAdapter.getCount();
        this.mExpectedAdapterCount = count;
        boolean b = this.mItems.size() < this.mOffscreenPageLimit * 2 + 1 && this.mItems.size() < count;
        int n = this.mCurItem;
        int n2;
        int n3;
        int n4;
        int n5;
        for (int i = n2 = 0; i < this.mItems.size(); i = n4 + 1, n = n3, n2 = n5) {
            final ItemInfo itemInfo = this.mItems.get(i);
            final int itemPosition = this.mAdapter.getItemPosition(itemInfo.object);
            if (itemPosition == -1) {
                n3 = n;
                n4 = i;
                n5 = n2;
            }
            else {
                if (itemPosition == -2) {
                    this.mItems.remove(i);
                    final int n6 = i - 1;
                    int n7;
                    if ((n7 = n2) == 0) {
                        this.mAdapter.startUpdate(this);
                        n7 = 1;
                    }
                    this.mAdapter.destroyItem(this, itemInfo.position, itemInfo.object);
                    final int mCurItem = this.mCurItem;
                    i = n6;
                    n2 = n7;
                    if (mCurItem == itemInfo.position) {
                        n = Math.max(0, Math.min(mCurItem, count - 1));
                        n2 = n7;
                        i = n6;
                    }
                }
                else {
                    final int position = itemInfo.position;
                    n3 = n;
                    n4 = i;
                    n5 = n2;
                    if (position == itemPosition) {
                        continue;
                    }
                    if (position == this.mCurItem) {
                        n = itemPosition;
                    }
                    itemInfo.position = itemPosition;
                }
                b = true;
                n3 = n;
                n4 = i;
                n5 = n2;
            }
        }
        if (n2 != 0) {
            this.mAdapter.finishUpdate(this);
        }
        Collections.sort(this.mItems, ViewPager.COMPARATOR);
        if (b) {
            for (int childCount = this.getChildCount(), j = 0; j < childCount; ++j) {
                final LayoutParams layoutParams = (LayoutParams)this.getChildAt(j).getLayoutParams();
                if (!layoutParams.isDecor) {
                    layoutParams.widthFactor = 0.0f;
                }
            }
            this.setCurrentItemInternal(n, false, true);
            this.requestLayout();
        }
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent) || this.executeKeyEvent(keyEvent);
    }
    
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == 4096) {
            return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        }
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 0) {
                final ItemInfo infoForChild = this.infoForChild(child);
                if (infoForChild != null && infoForChild.position == this.mCurItem && child.dispatchPopulateAccessibilityEvent(accessibilityEvent)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    float distanceInfluenceForSnapDuration(final float n) {
        return (float)Math.sin((n - 0.5f) * 0.47123894f);
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        final int overScrollMode = this.getOverScrollMode();
        int n = 0;
        int n2 = 0;
        Label_0256: {
            Label_0064: {
                if (overScrollMode != 0) {
                    if (overScrollMode == 1) {
                        final PagerAdapter mAdapter = this.mAdapter;
                        if (mAdapter != null && mAdapter.getCount() > 1) {
                            break Label_0064;
                        }
                    }
                    this.mLeftEdge.finish();
                    this.mRightEdge.finish();
                    break Label_0256;
                }
            }
            if (!this.mLeftEdge.isFinished()) {
                final int save = canvas.save();
                final int n3 = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
                final int width = this.getWidth();
                canvas.rotate(270.0f);
                canvas.translate((float)(-n3 + this.getPaddingTop()), this.mFirstOffset * width);
                this.mLeftEdge.setSize(n3, width);
                n2 = ((false | this.mLeftEdge.draw(canvas)) ? 1 : 0);
                canvas.restoreToCount(save);
            }
            n = n2;
            if (!this.mRightEdge.isFinished()) {
                final int save2 = canvas.save();
                final int width2 = this.getWidth();
                final int height = this.getHeight();
                final int paddingTop = this.getPaddingTop();
                final int paddingBottom = this.getPaddingBottom();
                canvas.rotate(90.0f);
                canvas.translate((float)(-this.getPaddingTop()), -(this.mLastOffset + 1.0f) * width2);
                this.mRightEdge.setSize(height - paddingTop - paddingBottom, width2);
                n = (n2 | (this.mRightEdge.draw(canvas) ? 1 : 0));
                canvas.restoreToCount(save2);
            }
        }
        if (n != 0) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final Drawable mMarginDrawable = this.mMarginDrawable;
        if (mMarginDrawable != null && mMarginDrawable.isStateful()) {
            mMarginDrawable.setState(this.getDrawableState());
        }
    }
    
    public void endFakeDrag() {
        if (this.mFakeDragging) {
            if (this.mAdapter != null) {
                final VelocityTracker mVelocityTracker = this.mVelocityTracker;
                mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
                final int n = (int)mVelocityTracker.getXVelocity(this.mActivePointerId);
                this.mPopulatePending = true;
                final int clientWidth = this.getClientWidth();
                final int scrollX = this.getScrollX();
                final ItemInfo infoForCurrentScrollPosition = this.infoForCurrentScrollPosition();
                this.setCurrentItemInternal(this.determineTargetPage(infoForCurrentScrollPosition.position, (scrollX / (float)clientWidth - infoForCurrentScrollPosition.offset) / infoForCurrentScrollPosition.widthFactor, n, (int)(this.mLastMotionX - this.mInitialMotionX)), true, true, n);
            }
            this.endDrag();
            this.mFakeDragging = false;
            return;
        }
        throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
    }
    
    public boolean executeKeyEvent(final KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0) {
            final int keyCode = keyEvent.getKeyCode();
            if (keyCode != 21) {
                if (keyCode != 22) {
                    if (keyCode == 61) {
                        if (keyEvent.hasNoModifiers()) {
                            return this.arrowScroll(2);
                        }
                        if (keyEvent.hasModifiers(1)) {
                            return this.arrowScroll(1);
                        }
                    }
                }
                else {
                    if (keyEvent.hasModifiers(2)) {
                        return this.pageRight();
                    }
                    return this.arrowScroll(66);
                }
            }
            else {
                if (keyEvent.hasModifiers(2)) {
                    return this.pageLeft();
                }
                return this.arrowScroll(17);
            }
        }
        return false;
    }
    
    public void fakeDragBy(float n) {
        if (!this.mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        }
        if (this.mAdapter == null) {
            return;
        }
        this.mLastMotionX += n;
        final float n2 = this.getScrollX() - n;
        final float n3 = (float)this.getClientWidth();
        n = this.mFirstOffset * n3;
        float n4 = this.mLastOffset * n3;
        final ItemInfo itemInfo = this.mItems.get(0);
        final ArrayList<ItemInfo> mItems = this.mItems;
        final ItemInfo itemInfo2 = mItems.get(mItems.size() - 1);
        if (itemInfo.position != 0) {
            n = itemInfo.offset * n3;
        }
        if (itemInfo2.position != this.mAdapter.getCount() - 1) {
            n4 = itemInfo2.offset * n3;
        }
        if (n2 >= n) {
            n = n2;
            if (n2 > n4) {
                n = n4;
            }
        }
        final float mLastMotionX = this.mLastMotionX;
        final int n5 = (int)n;
        this.mLastMotionX = mLastMotionX + (n - n5);
        this.scrollTo(n5, this.getScrollY());
        this.pageScrolled(n5);
        final MotionEvent obtain = MotionEvent.obtain(this.mFakeDragBeginTime, SystemClock.uptimeMillis(), 2, this.mLastMotionX, 0.0f, 0);
        this.mVelocityTracker.addMovement(obtain);
        obtain.recycle();
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return this.generateDefaultLayoutParams();
    }
    
    public PagerAdapter getAdapter() {
        return this.mAdapter;
    }
    
    protected int getChildDrawingOrder(final int n, final int n2) {
        int index = n2;
        if (this.mDrawingOrder == 2) {
            index = n - 1 - n2;
        }
        return ((LayoutParams)this.mDrawingOrderedChildren.get(index).getLayoutParams()).childIndex;
    }
    
    public int getCurrentItem() {
        return this.mCurItem;
    }
    
    ItemInfo infoForAnyChild(View view) {
        while (true) {
            final ViewParent parent = view.getParent();
            if (parent == this) {
                return this.infoForChild(view);
            }
            if (!(parent instanceof View)) {
                return null;
            }
            view = (View)parent;
        }
    }
    
    ItemInfo infoForChild(final View view) {
        for (int i = 0; i < this.mItems.size(); ++i) {
            final ItemInfo itemInfo = this.mItems.get(i);
            if (this.mAdapter.isViewFromObject(view, itemInfo.object)) {
                return itemInfo;
            }
        }
        return null;
    }
    
    ItemInfo infoForPosition(final int n) {
        for (int i = 0; i < this.mItems.size(); ++i) {
            final ItemInfo itemInfo = this.mItems.get(i);
            if (itemInfo.position == n) {
                return itemInfo;
            }
        }
        return null;
    }
    
    void initViewPager() {
        this.setWillNotDraw(false);
        this.setDescendantFocusability(262144);
        this.setFocusable(true);
        final Context context = this.getContext();
        this.mScroller = new Scroller(context, ViewPager.sInterpolator);
        final ViewConfiguration value = ViewConfiguration.get(context);
        final float density = context.getResources().getDisplayMetrics().density;
        this.mTouchSlop = value.getScaledPagingTouchSlop();
        this.mMinimumVelocity = (int)(400.0f * density);
        this.mMaximumVelocity = value.getScaledMaximumFlingVelocity();
        this.mLeftEdge = new EdgeEffect(context);
        this.mRightEdge = new EdgeEffect(context);
        this.mFlingDistance = (int)(25.0f * density);
        this.mCloseEnough = (int)(2.0f * density);
        this.mDefaultGutterSize = (int)(density * 16.0f);
        ViewCompat.setAccessibilityDelegate((View)this, new MyAccessibilityDelegate());
        if (ViewCompat.getImportantForAccessibility((View)this) == 0) {
            ViewCompat.setImportantForAccessibility((View)this, 1);
        }
        ViewCompat.setOnApplyWindowInsetsListener((View)this, new OnApplyWindowInsetsListener() {
            private final Rect mTempRect = new Rect();
            
            @Override
            public WindowInsetsCompat onApplyWindowInsets(final View view, WindowInsetsCompat dispatchApplyWindowInsets) {
                final WindowInsetsCompat onApplyWindowInsets = ViewCompat.onApplyWindowInsets(view, dispatchApplyWindowInsets);
                if (onApplyWindowInsets.isConsumed()) {
                    return onApplyWindowInsets;
                }
                final Rect mTempRect = this.mTempRect;
                mTempRect.left = onApplyWindowInsets.getSystemWindowInsetLeft();
                mTempRect.top = onApplyWindowInsets.getSystemWindowInsetTop();
                mTempRect.right = onApplyWindowInsets.getSystemWindowInsetRight();
                mTempRect.bottom = onApplyWindowInsets.getSystemWindowInsetBottom();
                for (int i = 0; i < ViewPager.this.getChildCount(); ++i) {
                    dispatchApplyWindowInsets = ViewCompat.dispatchApplyWindowInsets(ViewPager.this.getChildAt(i), onApplyWindowInsets);
                    mTempRect.left = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetLeft(), mTempRect.left);
                    mTempRect.top = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetTop(), mTempRect.top);
                    mTempRect.right = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetRight(), mTempRect.right);
                    mTempRect.bottom = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetBottom(), mTempRect.bottom);
                }
                final WindowInsetsCompat.Builder builder = new WindowInsetsCompat.Builder(onApplyWindowInsets);
                builder.setSystemWindowInsets(Insets.of(mTempRect));
                return builder.build();
            }
        });
    }
    
    public boolean isFakeDragging() {
        return this.mFakeDragging;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }
    
    protected void onDetachedFromWindow() {
        this.removeCallbacks(this.mEndScrollRunnable);
        final Scroller mScroller = this.mScroller;
        if (mScroller != null && !mScroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
        super.onDetachedFromWindow();
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (this.mPageMargin > 0 && this.mMarginDrawable != null && this.mItems.size() > 0 && this.mAdapter != null) {
            final int scrollX = this.getScrollX();
            final int width = this.getWidth();
            final float n = (float)this.mPageMargin;
            final float n2 = (float)width;
            final float n3 = n / n2;
            final ArrayList<ItemInfo> mItems = this.mItems;
            int index = 0;
            ItemInfo itemInfo = mItems.get(0);
            float offset = itemInfo.offset;
            for (int size = this.mItems.size(), i = itemInfo.position; i < this.mItems.get(size - 1).position; ++i) {
                while (i > itemInfo.position && index < size) {
                    final ArrayList<ItemInfo> mItems2 = this.mItems;
                    ++index;
                    itemInfo = mItems2.get(index);
                }
                float a;
                if (i == itemInfo.position) {
                    final float offset2 = itemInfo.offset;
                    final float widthFactor = itemInfo.widthFactor;
                    a = (offset2 + widthFactor) * n2;
                    offset = offset2 + widthFactor + n3;
                }
                else {
                    final float pageWidth = this.mAdapter.getPageWidth(i);
                    a = (offset + pageWidth) * n2;
                    offset += pageWidth + n3;
                }
                if (this.mPageMargin + a > scrollX) {
                    this.mMarginDrawable.setBounds(Math.round(a), this.mTopPageBounds, Math.round(this.mPageMargin + a), this.mBottomPageBounds);
                    this.mMarginDrawable.draw(canvas);
                }
                if (a > scrollX + width) {
                    break;
                }
            }
        }
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final int n = motionEvent.getAction() & 0xFF;
        if (n != 3 && n != 1) {
            if (n != 0) {
                if (this.mIsBeingDragged) {
                    return true;
                }
                if (this.mIsUnableToDrag) {
                    return false;
                }
            }
            if (n != 0) {
                if (n != 2) {
                    if (n == 6) {
                        this.onSecondaryPointerUp(motionEvent);
                    }
                }
                else {
                    final int mActivePointerId = this.mActivePointerId;
                    if (mActivePointerId != -1) {
                        final int pointerIndex = motionEvent.findPointerIndex(mActivePointerId);
                        final float x = motionEvent.getX(pointerIndex);
                        final float a = x - this.mLastMotionX;
                        final float abs = Math.abs(a);
                        final float y = motionEvent.getY(pointerIndex);
                        final float abs2 = Math.abs(y - this.mInitialMotionY);
                        final float n2 = fcmpl(a, 0.0f);
                        if (n2 != 0 && !this.isGutterDrag(this.mLastMotionX, a) && this.canScroll((View)this, false, (int)a, (int)x, (int)y)) {
                            this.mLastMotionX = x;
                            this.mLastMotionY = y;
                            this.mIsUnableToDrag = true;
                            return false;
                        }
                        if (abs > this.mTouchSlop && abs * 0.5f > abs2) {
                            this.requestParentDisallowInterceptTouchEvent(this.mIsBeingDragged = true);
                            this.setScrollState(1);
                            float mLastMotionX;
                            if (n2 > 0) {
                                mLastMotionX = this.mInitialMotionX + this.mTouchSlop;
                            }
                            else {
                                mLastMotionX = this.mInitialMotionX - this.mTouchSlop;
                            }
                            this.mLastMotionX = mLastMotionX;
                            this.mLastMotionY = y;
                            this.setScrollingCacheEnabled(true);
                        }
                        else if (abs2 > this.mTouchSlop) {
                            this.mIsUnableToDrag = true;
                        }
                        if (this.mIsBeingDragged && this.performDrag(x)) {
                            ViewCompat.postInvalidateOnAnimation((View)this);
                        }
                    }
                }
            }
            else {
                final float x2 = motionEvent.getX();
                this.mInitialMotionX = x2;
                this.mLastMotionX = x2;
                final float y2 = motionEvent.getY();
                this.mInitialMotionY = y2;
                this.mLastMotionY = y2;
                this.mActivePointerId = motionEvent.getPointerId(0);
                this.mIsUnableToDrag = false;
                this.mIsScrollStarted = true;
                this.mScroller.computeScrollOffset();
                if (this.mScrollState == 2 && Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) > this.mCloseEnough) {
                    this.mScroller.abortAnimation();
                    this.mPopulatePending = false;
                    this.populate();
                    this.requestParentDisallowInterceptTouchEvent(this.mIsBeingDragged = true);
                    this.setScrollState(1);
                }
                else {
                    this.completeScroll(false);
                    this.mIsBeingDragged = false;
                }
            }
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            return this.mIsBeingDragged;
        }
        this.resetTouch();
        return false;
    }
    
    protected void onLayout(final boolean b, int paddingTop, int paddingLeft, int paddingBottom, int paddingRight) {
        final int childCount = this.getChildCount();
        final int n = paddingBottom - paddingTop;
        final int n2 = paddingRight - paddingLeft;
        paddingLeft = this.getPaddingLeft();
        paddingTop = this.getPaddingTop();
        paddingRight = this.getPaddingRight();
        paddingBottom = this.getPaddingBottom();
        final int scrollX = this.getScrollX();
        int i = 0;
        int mDecorChildCount = 0;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            int n3 = paddingLeft;
            int n4 = paddingTop;
            int n5 = paddingRight;
            int n6 = paddingBottom;
            int n7 = mDecorChildCount;
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                n3 = paddingLeft;
                n4 = paddingTop;
                n5 = paddingRight;
                n6 = paddingBottom;
                n7 = mDecorChildCount;
                if (layoutParams.isDecor) {
                    final int gravity = layoutParams.gravity;
                    final int n8 = gravity & 0x7;
                    final int n9 = gravity & 0x70;
                    int n11 = 0;
                    Label_0246: {
                        int max;
                        if (n8 != 1) {
                            if (n8 == 3) {
                                final int n10 = child.getMeasuredWidth() + paddingLeft;
                                n11 = paddingLeft;
                                paddingLeft = n10;
                                break Label_0246;
                            }
                            if (n8 != 5) {
                                final int n12 = paddingLeft;
                                n11 = paddingLeft;
                                paddingLeft = n12;
                                break Label_0246;
                            }
                            max = n - paddingRight - child.getMeasuredWidth();
                            paddingRight += child.getMeasuredWidth();
                        }
                        else {
                            max = Math.max((n - child.getMeasuredWidth()) / 2, paddingLeft);
                        }
                        n11 = max;
                    }
                    int max2;
                    if (n9 != 16) {
                        if (n9 != 48) {
                            if (n9 != 80) {
                                final int n13 = paddingTop;
                                max2 = paddingTop;
                                paddingTop = n13;
                            }
                            else {
                                max2 = n2 - paddingBottom - child.getMeasuredHeight();
                                paddingBottom += child.getMeasuredHeight();
                            }
                        }
                        else {
                            final int n14 = child.getMeasuredHeight() + paddingTop;
                            max2 = paddingTop;
                            paddingTop = n14;
                        }
                    }
                    else {
                        max2 = Math.max((n2 - child.getMeasuredHeight()) / 2, paddingTop);
                    }
                    final int n15 = n11 + scrollX;
                    child.layout(n15, max2, child.getMeasuredWidth() + n15, max2 + child.getMeasuredHeight());
                    n7 = mDecorChildCount + 1;
                    n6 = paddingBottom;
                    n5 = paddingRight;
                    n4 = paddingTop;
                    n3 = paddingLeft;
                }
            }
            ++i;
            paddingLeft = n3;
            paddingTop = n4;
            paddingRight = n5;
            paddingBottom = n6;
            mDecorChildCount = n7;
        }
        for (int j = 0; j < childCount; ++j) {
            final View child2 = this.getChildAt(j);
            if (child2.getVisibility() != 8) {
                final LayoutParams layoutParams2 = (LayoutParams)child2.getLayoutParams();
                if (!layoutParams2.isDecor) {
                    final ItemInfo infoForChild = this.infoForChild(child2);
                    if (infoForChild != null) {
                        final float n16 = (float)(n - paddingLeft - paddingRight);
                        final int n17 = (int)(infoForChild.offset * n16) + paddingLeft;
                        if (layoutParams2.needsMeasure) {
                            layoutParams2.needsMeasure = false;
                            child2.measure(View$MeasureSpec.makeMeasureSpec((int)(n16 * layoutParams2.widthFactor), 1073741824), View$MeasureSpec.makeMeasureSpec(n2 - paddingTop - paddingBottom, 1073741824));
                        }
                        child2.layout(n17, paddingTop, child2.getMeasuredWidth() + n17, child2.getMeasuredHeight() + paddingTop);
                    }
                }
            }
        }
        this.mTopPageBounds = paddingTop;
        this.mBottomPageBounds = n2 - paddingBottom;
        this.mDecorChildCount = mDecorChildCount;
        if (this.mFirstLayout) {
            this.scrollToItem(this.mCurItem, false, 0, false);
        }
        this.mFirstLayout = false;
    }
    
    protected void onMeasure(int measuredWidth, int i) {
        final int n = 0;
        this.setMeasuredDimension(ViewGroup.getDefaultSize(0, measuredWidth), ViewGroup.getDefaultSize(0, i));
        measuredWidth = this.getMeasuredWidth();
        this.mGutterSize = Math.min(measuredWidth / 10, this.mDefaultGutterSize);
        measuredWidth = measuredWidth - this.getPaddingLeft() - this.getPaddingRight();
        i = this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom();
        final int childCount = this.getChildCount();
        int n2 = 0;
        while (true) {
            final int n3 = 1;
            int n4 = 1073741824;
            if (n2 >= childCount) {
                break;
            }
            final View child = this.getChildAt(n2);
            int n5 = measuredWidth;
            int n6 = i;
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                n5 = measuredWidth;
                n6 = i;
                if (layoutParams != null) {
                    n5 = measuredWidth;
                    n6 = i;
                    if (layoutParams.isDecor) {
                        final int gravity = layoutParams.gravity;
                        final int n7 = gravity & 0x7;
                        final int n8 = gravity & 0x70;
                        final boolean b = n8 == 48 || n8 == 80;
                        int n9 = n3;
                        if (n7 != 3) {
                            if (n7 == 5) {
                                n9 = n3;
                            }
                            else {
                                n9 = 0;
                            }
                        }
                        int n10 = Integer.MIN_VALUE;
                        int n11;
                        if (b) {
                            n11 = Integer.MIN_VALUE;
                            n10 = 1073741824;
                        }
                        else if (n9 != 0) {
                            n11 = 1073741824;
                        }
                        else {
                            n11 = Integer.MIN_VALUE;
                        }
                        final int width = layoutParams.width;
                        int n13;
                        int n14;
                        if (width != -2) {
                            int n12;
                            if (width != -1) {
                                n12 = width;
                            }
                            else {
                                n12 = measuredWidth;
                            }
                            n13 = 1073741824;
                            n14 = n12;
                        }
                        else {
                            n14 = measuredWidth;
                            n13 = n10;
                        }
                        final int height = layoutParams.height;
                        int n15;
                        if (height != -2) {
                            if (height != -1) {
                                n15 = height;
                            }
                            else {
                                n15 = i;
                            }
                        }
                        else {
                            final int n16 = i;
                            n4 = n11;
                            n15 = n16;
                        }
                        child.measure(View$MeasureSpec.makeMeasureSpec(n14, n13), View$MeasureSpec.makeMeasureSpec(n15, n4));
                        if (b) {
                            n6 = i - child.getMeasuredHeight();
                            n5 = measuredWidth;
                        }
                        else {
                            n5 = measuredWidth;
                            n6 = i;
                            if (n9 != 0) {
                                n5 = measuredWidth - child.getMeasuredWidth();
                                n6 = i;
                            }
                        }
                    }
                }
            }
            ++n2;
            measuredWidth = n5;
            i = n6;
        }
        View$MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824);
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(i, 1073741824);
        this.mInLayout = true;
        this.populate();
        this.mInLayout = false;
        int childCount2;
        View child2;
        LayoutParams layoutParams2;
        for (childCount2 = this.getChildCount(), i = n; i < childCount2; ++i) {
            child2 = this.getChildAt(i);
            if (child2.getVisibility() != 8) {
                layoutParams2 = (LayoutParams)child2.getLayoutParams();
                if (layoutParams2 == null || !layoutParams2.isDecor) {
                    child2.measure(View$MeasureSpec.makeMeasureSpec((int)(measuredWidth * layoutParams2.widthFactor), 1073741824), measureSpec);
                }
            }
        }
    }
    
    protected void onPageScrolled(int i, float n, int childCount) {
        final int mDecorChildCount = this.mDecorChildCount;
        final int n2 = 0;
        if (mDecorChildCount > 0) {
            final int scrollX = this.getScrollX();
            int paddingLeft = this.getPaddingLeft();
            int paddingRight = this.getPaddingRight();
            final int width = this.getWidth();
            for (int childCount2 = this.getChildCount(), j = 0; j < childCount2; ++j) {
                final View child = this.getChildAt(j);
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (layoutParams.isDecor) {
                    final int n3 = layoutParams.gravity & 0x7;
                    int max;
                    if (n3 != 1) {
                        if (n3 != 3) {
                            if (n3 != 5) {
                                final int n4 = paddingLeft;
                                max = paddingLeft;
                                paddingLeft = n4;
                            }
                            else {
                                max = width - paddingRight - child.getMeasuredWidth();
                                paddingRight += child.getMeasuredWidth();
                            }
                        }
                        else {
                            final int n5 = child.getWidth() + paddingLeft;
                            max = paddingLeft;
                            paddingLeft = n5;
                        }
                    }
                    else {
                        max = Math.max((width - child.getMeasuredWidth()) / 2, paddingLeft);
                    }
                    final int n6 = max + scrollX - child.getLeft();
                    if (n6 != 0) {
                        child.offsetLeftAndRight(n6);
                    }
                }
            }
        }
        this.dispatchOnPageScrolled(i, n, childCount);
        if (this.mPageTransformer != null) {
            final int scrollX2 = this.getScrollX();
            View child2;
            for (childCount = this.getChildCount(), i = n2; i < childCount; ++i) {
                child2 = this.getChildAt(i);
                if (!((LayoutParams)child2.getLayoutParams()).isDecor) {
                    n = (child2.getLeft() - scrollX2) / (float)this.getClientWidth();
                    this.mPageTransformer.transformPage(child2, n);
                }
            }
        }
        this.mCalledSuper = true;
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        int i = this.getChildCount();
        int n2 = -1;
        int n3;
        if ((n & 0x2) != 0x0) {
            n2 = i;
            i = 0;
            n3 = 1;
        }
        else {
            --i;
            n3 = -1;
        }
        while (i != n2) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 0) {
                final ItemInfo infoForChild = this.infoForChild(child);
                if (infoForChild != null && infoForChild.position == this.mCurItem && child.requestFocus(n, rect)) {
                    return true;
                }
            }
            i += n3;
        }
        return false;
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        final PagerAdapter mAdapter = this.mAdapter;
        if (mAdapter != null) {
            mAdapter.restoreState(savedState.adapterState, savedState.loader);
            this.setCurrentItemInternal(savedState.position, false, true);
        }
        else {
            this.mRestoredCurItem = savedState.position;
            this.mRestoredAdapterState = savedState.adapterState;
            this.mRestoredClassLoader = savedState.loader;
        }
    }
    
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.position = this.mCurItem;
        final PagerAdapter mAdapter = this.mAdapter;
        if (mAdapter != null) {
            savedState.adapterState = mAdapter.saveState();
        }
        return (Parcelable)savedState;
    }
    
    protected void onSizeChanged(final int n, int mPageMargin, final int n2, final int n3) {
        super.onSizeChanged(n, mPageMargin, n2, n3);
        if (n != n2) {
            mPageMargin = this.mPageMargin;
            this.recomputeScrollPosition(n, n2, mPageMargin, mPageMargin);
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (this.mFakeDragging) {
            return true;
        }
        final int action = motionEvent.getAction();
        boolean b = false;
        if (action == 0 && motionEvent.getEdgeFlags() != 0) {
            return false;
        }
        final PagerAdapter mAdapter = this.mAdapter;
        if (mAdapter != null && mAdapter.getCount() != 0) {
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            final int n = motionEvent.getAction() & 0xFF;
            Label_0601: {
                if (n != 0) {
                    if (n != 1) {
                        if (n != 2) {
                            if (n != 3) {
                                if (n != 5) {
                                    if (n == 6) {
                                        this.onSecondaryPointerUp(motionEvent);
                                        this.mLastMotionX = motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId));
                                    }
                                }
                                else {
                                    final int actionIndex = motionEvent.getActionIndex();
                                    this.mLastMotionX = motionEvent.getX(actionIndex);
                                    this.mActivePointerId = motionEvent.getPointerId(actionIndex);
                                }
                            }
                            else if (this.mIsBeingDragged) {
                                this.scrollToItem(this.mCurItem, true, 0, false);
                                b = this.resetTouch();
                            }
                        }
                        else {
                            if (!this.mIsBeingDragged) {
                                final int pointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                                if (pointerIndex == -1) {
                                    b = this.resetTouch();
                                    break Label_0601;
                                }
                                final float x = motionEvent.getX(pointerIndex);
                                final float abs = Math.abs(x - this.mLastMotionX);
                                final float y = motionEvent.getY(pointerIndex);
                                final float abs2 = Math.abs(y - this.mLastMotionY);
                                if (abs > this.mTouchSlop && abs > abs2) {
                                    this.requestParentDisallowInterceptTouchEvent(this.mIsBeingDragged = true);
                                    final float mInitialMotionX = this.mInitialMotionX;
                                    float mLastMotionX;
                                    if (x - mInitialMotionX > 0.0f) {
                                        mLastMotionX = mInitialMotionX + this.mTouchSlop;
                                    }
                                    else {
                                        mLastMotionX = mInitialMotionX - this.mTouchSlop;
                                    }
                                    this.mLastMotionX = mLastMotionX;
                                    this.mLastMotionY = y;
                                    this.setScrollState(1);
                                    this.setScrollingCacheEnabled(true);
                                    final ViewParent parent = this.getParent();
                                    if (parent != null) {
                                        parent.requestDisallowInterceptTouchEvent(true);
                                    }
                                }
                            }
                            if (this.mIsBeingDragged) {
                                b = (false | this.performDrag(motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId))));
                            }
                        }
                    }
                    else if (this.mIsBeingDragged) {
                        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
                        mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
                        final int n2 = (int)mVelocityTracker.getXVelocity(this.mActivePointerId);
                        this.mPopulatePending = true;
                        final int clientWidth = this.getClientWidth();
                        final int scrollX = this.getScrollX();
                        final ItemInfo infoForCurrentScrollPosition = this.infoForCurrentScrollPosition();
                        final float n3 = (float)this.mPageMargin;
                        final float n4 = (float)clientWidth;
                        this.setCurrentItemInternal(this.determineTargetPage(infoForCurrentScrollPosition.position, (scrollX / n4 - infoForCurrentScrollPosition.offset) / (infoForCurrentScrollPosition.widthFactor + n3 / n4), n2, (int)(motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId)) - this.mInitialMotionX)), true, true, n2);
                        b = this.resetTouch();
                    }
                }
                else {
                    this.mScroller.abortAnimation();
                    this.mPopulatePending = false;
                    this.populate();
                    final float x2 = motionEvent.getX();
                    this.mInitialMotionX = x2;
                    this.mLastMotionX = x2;
                    final float y2 = motionEvent.getY();
                    this.mInitialMotionY = y2;
                    this.mLastMotionY = y2;
                    this.mActivePointerId = motionEvent.getPointerId(0);
                }
            }
            if (b) {
                ViewCompat.postInvalidateOnAnimation((View)this);
            }
            return true;
        }
        return false;
    }
    
    boolean pageLeft() {
        final int mCurItem = this.mCurItem;
        if (mCurItem > 0) {
            this.setCurrentItem(mCurItem - 1, true);
            return true;
        }
        return false;
    }
    
    boolean pageRight() {
        final PagerAdapter mAdapter = this.mAdapter;
        if (mAdapter != null && this.mCurItem < mAdapter.getCount() - 1) {
            this.setCurrentItem(this.mCurItem + 1, true);
            return true;
        }
        return false;
    }
    
    void populate() {
        this.populate(this.mCurItem);
    }
    
    void populate(int i) {
        final int mCurItem = this.mCurItem;
        ItemInfo infoForPosition;
        if (mCurItem != i) {
            infoForPosition = this.infoForPosition(mCurItem);
            this.mCurItem = i;
        }
        else {
            infoForPosition = null;
        }
        if (this.mAdapter == null) {
            this.sortChildDrawingOrder();
            return;
        }
        if (this.mPopulatePending) {
            this.sortChildDrawingOrder();
            return;
        }
        if (this.getWindowToken() == null) {
            return;
        }
        this.mAdapter.startUpdate(this);
        i = this.mOffscreenPageLimit;
        final int max = Math.max(0, this.mCurItem - i);
        final int count = this.mAdapter.getCount();
        final int min = Math.min(count - 1, this.mCurItem + i);
        if (count == this.mExpectedAdapterCount) {
            i = 0;
            while (true) {
                while (i < this.mItems.size()) {
                    final ItemInfo itemInfo = this.mItems.get(i);
                    final int position = itemInfo.position;
                    final int mCurItem2 = this.mCurItem;
                    if (position >= mCurItem2) {
                        if (position == mCurItem2) {
                            ItemInfo addNewItem = itemInfo;
                            if (itemInfo == null) {
                                addNewItem = itemInfo;
                                if (count > 0) {
                                    addNewItem = this.addNewItem(this.mCurItem, i);
                                }
                            }
                            if (addNewItem != null) {
                                int index = i - 1;
                                ItemInfo itemInfo2;
                                if (index >= 0) {
                                    itemInfo2 = this.mItems.get(index);
                                }
                                else {
                                    itemInfo2 = null;
                                }
                                final int clientWidth = this.getClientWidth();
                                float n;
                                if (clientWidth <= 0) {
                                    n = 0.0f;
                                }
                                else {
                                    n = 2.0f - addNewItem.widthFactor + this.getPaddingLeft() / (float)clientWidth;
                                }
                                int j = this.mCurItem - 1;
                                float n2 = 0.0f;
                                ItemInfo itemInfo3 = itemInfo2;
                                while (j >= 0) {
                                    int n3 = 0;
                                    int n4 = 0;
                                    ItemInfo itemInfo4 = null;
                                    float n5 = 0.0f;
                                    Label_0559: {
                                        Label_0553: {
                                            int n6;
                                            int n7;
                                            if (n2 >= n && j < max) {
                                                if (itemInfo3 == null) {
                                                    break;
                                                }
                                                n3 = i;
                                                n4 = index;
                                                itemInfo4 = itemInfo3;
                                                n5 = n2;
                                                if (j != itemInfo3.position) {
                                                    break Label_0559;
                                                }
                                                n3 = i;
                                                n4 = index;
                                                itemInfo4 = itemInfo3;
                                                n5 = n2;
                                                if (itemInfo3.scrolling) {
                                                    break Label_0559;
                                                }
                                                this.mItems.remove(index);
                                                this.mAdapter.destroyItem(this, j, itemInfo3.object);
                                                --index;
                                                n6 = --i;
                                                n7 = index;
                                                n5 = n2;
                                                if (index >= 0) {
                                                    itemInfo4 = this.mItems.get(index);
                                                    n5 = n2;
                                                    break Label_0553;
                                                }
                                            }
                                            else if (itemInfo3 != null && j == itemInfo3.position) {
                                                final float n8 = n2 + itemInfo3.widthFactor;
                                                --index;
                                                n6 = i;
                                                n7 = index;
                                                n5 = n8;
                                                if (index >= 0) {
                                                    itemInfo4 = this.mItems.get(index);
                                                    n5 = n8;
                                                    break Label_0553;
                                                }
                                            }
                                            else {
                                                final float n9 = n2 + this.addNewItem(j, index + 1).widthFactor;
                                                n6 = ++i;
                                                n7 = index;
                                                n5 = n9;
                                                if (index >= 0) {
                                                    itemInfo4 = this.mItems.get(index);
                                                    n5 = n9;
                                                    break Label_0553;
                                                }
                                            }
                                            itemInfo4 = null;
                                            index = n7;
                                            i = n6;
                                        }
                                        n4 = index;
                                        n3 = i;
                                    }
                                    --j;
                                    i = n3;
                                    index = n4;
                                    itemInfo3 = itemInfo4;
                                    n2 = n5;
                                }
                                float widthFactor = addNewItem.widthFactor;
                                int index2 = i + 1;
                                if (widthFactor < 2.0f) {
                                    ItemInfo itemInfo5;
                                    if (index2 < this.mItems.size()) {
                                        itemInfo5 = this.mItems.get(index2);
                                    }
                                    else {
                                        itemInfo5 = null;
                                    }
                                    float n10;
                                    if (clientWidth <= 0) {
                                        n10 = 0.0f;
                                    }
                                    else {
                                        n10 = this.getPaddingRight() / (float)clientWidth + 2.0f;
                                    }
                                    int mCurItem3 = this.mCurItem;
                                    ItemInfo itemInfo6 = itemInfo5;
                                    while (true) {
                                        final int n11 = mCurItem3 + 1;
                                        if (n11 >= count) {
                                            break;
                                        }
                                        float n12 = 0.0f;
                                        int n13 = 0;
                                        ItemInfo itemInfo7 = null;
                                        Label_0946: {
                                            if (widthFactor >= n10 && n11 > min) {
                                                if (itemInfo6 == null) {
                                                    break;
                                                }
                                                n12 = widthFactor;
                                                n13 = index2;
                                                itemInfo7 = itemInfo6;
                                                if (n11 != itemInfo6.position) {
                                                    break Label_0946;
                                                }
                                                n12 = widthFactor;
                                                n13 = index2;
                                                itemInfo7 = itemInfo6;
                                                if (itemInfo6.scrolling) {
                                                    break Label_0946;
                                                }
                                                this.mItems.remove(index2);
                                                this.mAdapter.destroyItem(this, n11, itemInfo6.object);
                                                n12 = widthFactor;
                                                if ((n13 = index2) < this.mItems.size()) {
                                                    itemInfo7 = this.mItems.get(index2);
                                                    n12 = widthFactor;
                                                    n13 = index2;
                                                    break Label_0946;
                                                }
                                            }
                                            else if (itemInfo6 != null && n11 == itemInfo6.position) {
                                                final float n14 = widthFactor + itemInfo6.widthFactor;
                                                ++index2;
                                                n12 = n14;
                                                if ((n13 = index2) < this.mItems.size()) {
                                                    itemInfo7 = this.mItems.get(index2);
                                                    n12 = n14;
                                                    n13 = index2;
                                                    break Label_0946;
                                                }
                                            }
                                            else {
                                                final ItemInfo addNewItem2 = this.addNewItem(n11, index2);
                                                ++index2;
                                                n12 = widthFactor + addNewItem2.widthFactor;
                                                if ((n13 = index2) < this.mItems.size()) {
                                                    itemInfo7 = this.mItems.get(index2);
                                                    n13 = index2;
                                                    n12 = n12;
                                                    break Label_0946;
                                                }
                                            }
                                            itemInfo7 = null;
                                        }
                                        widthFactor = n12;
                                        index2 = n13;
                                        itemInfo6 = itemInfo7;
                                        mCurItem3 = n11;
                                    }
                                }
                                this.calculatePageOffsets(addNewItem, i, infoForPosition);
                                this.mAdapter.setPrimaryItem(this, this.mCurItem, addNewItem.object);
                            }
                            this.mAdapter.finishUpdate(this);
                            int childCount;
                            View child;
                            LayoutParams layoutParams;
                            ItemInfo infoForChild;
                            for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                                child = this.getChildAt(i);
                                layoutParams = (LayoutParams)child.getLayoutParams();
                                layoutParams.childIndex = i;
                                if (!layoutParams.isDecor && layoutParams.widthFactor == 0.0f) {
                                    infoForChild = this.infoForChild(child);
                                    if (infoForChild != null) {
                                        layoutParams.widthFactor = infoForChild.widthFactor;
                                        layoutParams.position = infoForChild.position;
                                    }
                                }
                            }
                            this.sortChildDrawingOrder();
                            if (this.hasFocus()) {
                                final View focus = this.findFocus();
                                ItemInfo infoForAnyChild;
                                if (focus != null) {
                                    infoForAnyChild = this.infoForAnyChild(focus);
                                }
                                else {
                                    infoForAnyChild = null;
                                }
                                if (infoForAnyChild == null || infoForAnyChild.position != this.mCurItem) {
                                    View child2;
                                    ItemInfo infoForChild2;
                                    for (i = 0; i < this.getChildCount(); ++i) {
                                        child2 = this.getChildAt(i);
                                        infoForChild2 = this.infoForChild(child2);
                                        if (infoForChild2 != null && infoForChild2.position == this.mCurItem && child2.requestFocus(2)) {
                                            break;
                                        }
                                    }
                                }
                            }
                            return;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
                final ItemInfo itemInfo = null;
                continue;
            }
        }
        String str;
        try {
            str = this.getResources().getResourceName(this.getId());
        }
        catch (Resources$NotFoundException ex) {
            str = Integer.toHexString(this.getId());
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: ");
        sb.append(this.mExpectedAdapterCount);
        sb.append(", found: ");
        sb.append(count);
        sb.append(" Pager id: ");
        sb.append(str);
        sb.append(" Pager class: ");
        sb.append(this.getClass());
        sb.append(" Problematic adapter: ");
        sb.append(this.mAdapter.getClass());
        throw new IllegalStateException(sb.toString());
    }
    
    public void removeOnAdapterChangeListener(final OnAdapterChangeListener onAdapterChangeListener) {
        final List<OnAdapterChangeListener> mAdapterChangeListeners = this.mAdapterChangeListeners;
        if (mAdapterChangeListeners != null) {
            mAdapterChangeListeners.remove(onAdapterChangeListener);
        }
    }
    
    public void removeView(final View view) {
        if (this.mInLayout) {
            this.removeViewInLayout(view);
        }
        else {
            super.removeView(view);
        }
    }
    
    public void setAdapter(final PagerAdapter mAdapter) {
        final PagerAdapter mAdapter2 = this.mAdapter;
        final int n = 0;
        if (mAdapter2 != null) {
            mAdapter2.setViewPagerObserver(null);
            this.mAdapter.startUpdate(this);
            for (int i = 0; i < this.mItems.size(); ++i) {
                final ItemInfo itemInfo = this.mItems.get(i);
                this.mAdapter.destroyItem(this, itemInfo.position, itemInfo.object);
            }
            this.mAdapter.finishUpdate(this);
            this.mItems.clear();
            this.removeNonDecorViews();
            this.scrollTo(this.mCurItem = 0, 0);
        }
        final PagerAdapter mAdapter3 = this.mAdapter;
        this.mAdapter = mAdapter;
        this.mExpectedAdapterCount = 0;
        if (mAdapter != null) {
            if (this.mObserver == null) {
                this.mObserver = new PagerObserver();
            }
            this.mAdapter.setViewPagerObserver(this.mObserver);
            this.mPopulatePending = false;
            final boolean mFirstLayout = this.mFirstLayout;
            this.mFirstLayout = true;
            this.mExpectedAdapterCount = this.mAdapter.getCount();
            if (this.mRestoredCurItem >= 0) {
                this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
                this.setCurrentItemInternal(this.mRestoredCurItem, false, true);
                this.mRestoredCurItem = -1;
                this.mRestoredAdapterState = null;
                this.mRestoredClassLoader = null;
            }
            else if (!mFirstLayout) {
                this.populate();
            }
            else {
                this.requestLayout();
            }
        }
        final List<OnAdapterChangeListener> mAdapterChangeListeners = this.mAdapterChangeListeners;
        if (mAdapterChangeListeners != null && !mAdapterChangeListeners.isEmpty()) {
            for (int size = this.mAdapterChangeListeners.size(), j = n; j < size; ++j) {
                this.mAdapterChangeListeners.get(j).onAdapterChanged(this, mAdapter3, mAdapter);
            }
        }
    }
    
    public void setCurrentItem(final int n) {
        this.mPopulatePending = false;
        this.setCurrentItemInternal(n, this.mFirstLayout ^ true, false);
    }
    
    public void setCurrentItem(final int n, final boolean b) {
        this.setCurrentItemInternal(n, b, this.mPopulatePending = false);
    }
    
    void setCurrentItemInternal(final int n, final boolean b, final boolean b2) {
        this.setCurrentItemInternal(n, b, b2, 0);
    }
    
    void setCurrentItemInternal(int i, final boolean b, final boolean b2, final int n) {
        final PagerAdapter mAdapter = this.mAdapter;
        final boolean b3 = false;
        if (mAdapter == null || mAdapter.getCount() <= 0) {
            this.setScrollingCacheEnabled(false);
            return;
        }
        if (!b2 && this.mCurItem == i && this.mItems.size() != 0) {
            this.setScrollingCacheEnabled(false);
            return;
        }
        int mCurItem;
        if (i < 0) {
            mCurItem = 0;
        }
        else if ((mCurItem = i) >= this.mAdapter.getCount()) {
            mCurItem = this.mAdapter.getCount() - 1;
        }
        i = this.mOffscreenPageLimit;
        final int mCurItem2 = this.mCurItem;
        if (mCurItem > mCurItem2 + i || mCurItem < mCurItem2 - i) {
            for (i = 0; i < this.mItems.size(); ++i) {
                this.mItems.get(i).scrolling = true;
            }
        }
        boolean b4 = b3;
        if (this.mCurItem != mCurItem) {
            b4 = true;
        }
        if (this.mFirstLayout) {
            this.mCurItem = mCurItem;
            if (b4) {
                this.dispatchOnPageSelected(mCurItem);
            }
            this.requestLayout();
        }
        else {
            this.populate(mCurItem);
            this.scrollToItem(mCurItem, b, n, b4);
        }
    }
    
    OnPageChangeListener setInternalPageChangeListener(final OnPageChangeListener mInternalPageChangeListener) {
        final OnPageChangeListener mInternalPageChangeListener2 = this.mInternalPageChangeListener;
        this.mInternalPageChangeListener = mInternalPageChangeListener;
        return mInternalPageChangeListener2;
    }
    
    public void setOffscreenPageLimit(final int i) {
        int mOffscreenPageLimit = i;
        if (i < 1) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Requested offscreen page limit ");
            sb.append(i);
            sb.append(" too small; defaulting to ");
            sb.append(1);
            Log.w("ViewPager", sb.toString());
            mOffscreenPageLimit = 1;
        }
        if (mOffscreenPageLimit != this.mOffscreenPageLimit) {
            this.mOffscreenPageLimit = mOffscreenPageLimit;
            this.populate();
        }
    }
    
    @Deprecated
    public void setOnPageChangeListener(final OnPageChangeListener mOnPageChangeListener) {
        this.mOnPageChangeListener = mOnPageChangeListener;
    }
    
    void setScrollState(final int mScrollState) {
        if (this.mScrollState == mScrollState) {
            return;
        }
        this.mScrollState = mScrollState;
        if (this.mPageTransformer != null) {
            this.enableLayers(mScrollState != 0);
        }
        this.dispatchOnScrollStateChanged(mScrollState);
    }
    
    void smoothScrollTo(int a, int n, final int a2) {
        if (this.getChildCount() == 0) {
            this.setScrollingCacheEnabled(false);
            return;
        }
        final Scroller mScroller = this.mScroller;
        int n2;
        if (mScroller != null && !mScroller.isFinished()) {
            if (this.mIsScrollStarted) {
                n2 = this.mScroller.getCurrX();
            }
            else {
                n2 = this.mScroller.getStartX();
            }
            this.mScroller.abortAnimation();
            this.setScrollingCacheEnabled(false);
        }
        else {
            n2 = this.getScrollX();
        }
        final int scrollY = this.getScrollY();
        final int n3 = a - n2;
        n -= scrollY;
        if (n3 == 0 && n == 0) {
            this.completeScroll(false);
            this.populate();
            this.setScrollState(0);
            return;
        }
        this.setScrollingCacheEnabled(true);
        this.setScrollState(2);
        final int clientWidth = this.getClientWidth();
        a = clientWidth / 2;
        final float n4 = (float)Math.abs(n3);
        final float n5 = (float)clientWidth;
        final float min = Math.min(1.0f, n4 * 1.0f / n5);
        final float n6 = (float)a;
        final float distanceInfluenceForSnapDuration = this.distanceInfluenceForSnapDuration(min);
        a = Math.abs(a2);
        if (a > 0) {
            a = Math.round(Math.abs((n6 + distanceInfluenceForSnapDuration * n6) / a) * 1000.0f) * 4;
        }
        else {
            a = (int)((Math.abs(n3) / (n5 * this.mAdapter.getPageWidth(this.mCurItem) + this.mPageMargin) + 1.0f) * 100.0f);
        }
        a = Math.min(a, 600);
        this.mIsScrollStarted = false;
        this.mScroller.startScroll(n2, scrollY, n3, n, a);
        ViewCompat.postInvalidateOnAnimation((View)this);
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mMarginDrawable;
    }
    
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface DecorView {
    }
    
    static class ItemInfo
    {
        Object object;
        float offset;
        int position;
        boolean scrolling;
        float widthFactor;
    }
    
    public static class LayoutParams extends ViewGroup$LayoutParams
    {
        int childIndex;
        public int gravity;
        public boolean isDecor;
        boolean needsMeasure;
        int position;
        float widthFactor;
        
        public LayoutParams() {
            super(-1, -1);
            this.widthFactor = 0.0f;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.widthFactor = 0.0f;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, ViewPager.LAYOUT_ATTRS);
            this.gravity = obtainStyledAttributes.getInteger(0, 48);
            obtainStyledAttributes.recycle();
        }
    }
    
    class MyAccessibilityDelegate extends AccessibilityDelegateCompat
    {
        private boolean canScroll() {
            final PagerAdapter mAdapter = ViewPager.this.mAdapter;
            boolean b = true;
            if (mAdapter == null || mAdapter.getCount() <= 1) {
                b = false;
            }
            return b;
        }
        
        @Override
        public void onInitializeAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName((CharSequence)"androidx.viewpager.widget.ViewPager");
            accessibilityEvent.setScrollable(this.canScroll());
            if (accessibilityEvent.getEventType() == 4096) {
                final PagerAdapter mAdapter = ViewPager.this.mAdapter;
                if (mAdapter != null) {
                    accessibilityEvent.setItemCount(mAdapter.getCount());
                    accessibilityEvent.setFromIndex(ViewPager.this.mCurItem);
                    accessibilityEvent.setToIndex(ViewPager.this.mCurItem);
                }
            }
        }
        
        @Override
        public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.setClassName("androidx.viewpager.widget.ViewPager");
            accessibilityNodeInfoCompat.setScrollable(this.canScroll());
            if (ViewPager.this.canScrollHorizontally(1)) {
                accessibilityNodeInfoCompat.addAction(4096);
            }
            if (ViewPager.this.canScrollHorizontally(-1)) {
                accessibilityNodeInfoCompat.addAction(8192);
            }
        }
        
        @Override
        public boolean performAccessibilityAction(final View view, final int n, final Bundle bundle) {
            if (super.performAccessibilityAction(view, n, bundle)) {
                return true;
            }
            if (n != 4096) {
                if (n != 8192) {
                    return false;
                }
                if (ViewPager.this.canScrollHorizontally(-1)) {
                    final ViewPager this$0 = ViewPager.this;
                    this$0.setCurrentItem(this$0.mCurItem - 1);
                    return true;
                }
                return false;
            }
            else {
                if (ViewPager.this.canScrollHorizontally(1)) {
                    final ViewPager this$2 = ViewPager.this;
                    this$2.setCurrentItem(this$2.mCurItem + 1);
                    return true;
                }
                return false;
            }
        }
    }
    
    public interface OnAdapterChangeListener
    {
        void onAdapterChanged(final ViewPager p0, final PagerAdapter p1, final PagerAdapter p2);
    }
    
    public interface OnPageChangeListener
    {
        void onPageScrollStateChanged(final int p0);
        
        void onPageScrolled(final int p0, final float p1, final int p2);
        
        void onPageSelected(final int p0);
    }
    
    public interface PageTransformer
    {
        void transformPage(final View p0, final float p1);
    }
    
    private class PagerObserver extends DataSetObserver
    {
        PagerObserver() {
        }
        
        public void onChanged() {
            ViewPager.this.dataSetChanged();
        }
        
        public void onInvalidated() {
            ViewPager.this.dataSetChanged();
        }
    }
    
    public static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        Parcelable adapterState;
        ClassLoader loader;
        int position;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$ClassLoaderCreator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel, null);
                }
                
                public SavedState createFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                    return new SavedState(parcel, classLoader);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState(final Parcel parcel, final ClassLoader classLoader) {
            super(parcel, classLoader);
            ClassLoader classLoader2 = classLoader;
            if (classLoader == null) {
                classLoader2 = SavedState.class.getClassLoader();
            }
            this.position = parcel.readInt();
            this.adapterState = parcel.readParcelable(classLoader2);
            this.loader = classLoader2;
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("FragmentPager.SavedState{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" position=");
            sb.append(this.position);
            sb.append("}");
            return sb.toString();
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.position);
            parcel.writeParcelable(this.adapterState, n);
        }
    }
    
    public static class SimpleOnPageChangeListener implements OnPageChangeListener
    {
        @Override
        public void onPageScrollStateChanged(final int n) {
        }
    }
    
    static class ViewPositionComparator implements Comparator<View>
    {
        @Override
        public int compare(final View view, final View view2) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final LayoutParams layoutParams2 = (LayoutParams)view2.getLayoutParams();
            final boolean isDecor = layoutParams.isDecor;
            if (isDecor != layoutParams2.isDecor) {
                int n;
                if (isDecor) {
                    n = 1;
                }
                else {
                    n = -1;
                }
                return n;
            }
            return layoutParams.position - layoutParams2.position;
        }
    }
}
