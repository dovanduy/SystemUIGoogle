// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager2.widget;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import android.view.View$BaseSavedState;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.os.Build$VERSION;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.SparseArray;
import android.content.res.TypedArray;
import androidx.viewpager2.R$styleable;
import androidx.viewpager2.adapter.StatefulAdapter;
import android.view.ViewGroup$LayoutParams;
import androidx.core.view.ViewCompat;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

public final class ViewPager2 extends ViewGroup
{
    static boolean sFeatureEnhancedA11yEnabled = true;
    AccessibilityProvider mAccessibilityProvider;
    int mCurrentItem;
    private RecyclerView.AdapterDataObserver mCurrentItemDataSetChangeObserver;
    boolean mCurrentItemDirty;
    private CompositeOnPageChangeCallback mExternalPageChangeCallbacks;
    private FakeDrag mFakeDragger;
    LinearLayoutManager mLayoutManager;
    private int mOffscreenPageLimit;
    private CompositeOnPageChangeCallback mPageChangeEventDispatcher;
    private PageTransformerAdapter mPageTransformerAdapter;
    private PagerSnapHelper mPagerSnapHelper;
    private Parcelable mPendingAdapterState;
    private int mPendingCurrentItem;
    RecyclerView mRecyclerView;
    ScrollEventAdapter mScrollEventAdapter;
    private final Rect mTmpChildRect;
    private final Rect mTmpContainerRect;
    private boolean mUserInputEnabled;
    
    public ViewPager2(final Context context) {
        super(context);
        this.mTmpContainerRect = new Rect();
        this.mTmpChildRect = new Rect();
        this.mExternalPageChangeCallbacks = new CompositeOnPageChangeCallback(3);
        this.mCurrentItemDirty = false;
        this.mCurrentItemDataSetChangeObserver = new DataSetChangeObserver() {
            @Override
            public void onChanged() {
                final ViewPager2 this$0 = ViewPager2.this;
                this$0.mCurrentItemDirty = true;
                this$0.mScrollEventAdapter.notifyDataSetChangeHappened();
            }
        };
        this.mPendingCurrentItem = -1;
        this.mUserInputEnabled = true;
        this.mOffscreenPageLimit = -1;
        this.initialize(context, null);
    }
    
    public ViewPager2(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTmpContainerRect = new Rect();
        this.mTmpChildRect = new Rect();
        this.mExternalPageChangeCallbacks = new CompositeOnPageChangeCallback(3);
        this.mCurrentItemDirty = false;
        this.mCurrentItemDataSetChangeObserver = new DataSetChangeObserver() {
            @Override
            public void onChanged() {
                final ViewPager2 this$0 = ViewPager2.this;
                this$0.mCurrentItemDirty = true;
                this$0.mScrollEventAdapter.notifyDataSetChangeHappened();
            }
        };
        this.mPendingCurrentItem = -1;
        this.mUserInputEnabled = true;
        this.mOffscreenPageLimit = -1;
        this.initialize(context, set);
    }
    
    public ViewPager2(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mTmpContainerRect = new Rect();
        this.mTmpChildRect = new Rect();
        this.mExternalPageChangeCallbacks = new CompositeOnPageChangeCallback(3);
        this.mCurrentItemDirty = false;
        this.mCurrentItemDataSetChangeObserver = new DataSetChangeObserver() {
            @Override
            public void onChanged() {
                final ViewPager2 this$0 = ViewPager2.this;
                this$0.mCurrentItemDirty = true;
                this$0.mScrollEventAdapter.notifyDataSetChangeHappened();
            }
        };
        this.mPendingCurrentItem = -1;
        this.mUserInputEnabled = true;
        this.mOffscreenPageLimit = -1;
        this.initialize(context, set);
    }
    
    public ViewPager2(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mTmpContainerRect = new Rect();
        this.mTmpChildRect = new Rect();
        this.mExternalPageChangeCallbacks = new CompositeOnPageChangeCallback(3);
        this.mCurrentItemDirty = false;
        this.mCurrentItemDataSetChangeObserver = new DataSetChangeObserver() {
            @Override
            public void onChanged() {
                final ViewPager2 this$0 = ViewPager2.this;
                this$0.mCurrentItemDirty = true;
                this$0.mScrollEventAdapter.notifyDataSetChangeHappened();
            }
        };
        this.mPendingCurrentItem = -1;
        this.mUserInputEnabled = true;
        this.mOffscreenPageLimit = -1;
        this.initialize(context, set);
    }
    
    private RecyclerView.OnChildAttachStateChangeListener enforceChildFillListener() {
        return new RecyclerView.OnChildAttachStateChangeListener(this) {
            @Override
            public void onChildViewAttachedToWindow(final View view) {
                final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
                if (layoutParams.width == -1 && layoutParams.height == -1) {
                    return;
                }
                throw new IllegalStateException("Pages must fill the whole ViewPager2 (use match_parent)");
            }
            
            @Override
            public void onChildViewDetachedFromWindow(final View view) {
            }
        };
    }
    
    private void initialize(final Context context, final AttributeSet set) {
        AccessibilityProvider mAccessibilityProvider;
        if (ViewPager2.sFeatureEnhancedA11yEnabled) {
            mAccessibilityProvider = new PageAwareAccessibilityProvider();
        }
        else {
            mAccessibilityProvider = new BasicAccessibilityProvider();
        }
        this.mAccessibilityProvider = mAccessibilityProvider;
        (this.mRecyclerView = new RecyclerViewImpl(context)).setId(ViewCompat.generateViewId());
        this.mRecyclerView.setDescendantFocusability(131072);
        final LinearLayoutManagerImpl linearLayoutManagerImpl = new LinearLayoutManagerImpl(context);
        this.mLayoutManager = linearLayoutManagerImpl;
        this.mRecyclerView.setLayoutManager((RecyclerView.LayoutManager)linearLayoutManagerImpl);
        this.mRecyclerView.setScrollingTouchSlop(1);
        this.setOrientation(context, set);
        this.mRecyclerView.setLayoutParams(new ViewGroup$LayoutParams(-1, -1));
        this.mRecyclerView.addOnChildAttachStateChangeListener(this.enforceChildFillListener());
        final ScrollEventAdapter mScrollEventAdapter = new ScrollEventAdapter(this);
        this.mScrollEventAdapter = mScrollEventAdapter;
        this.mFakeDragger = new FakeDrag(this, mScrollEventAdapter, this.mRecyclerView);
        (this.mPagerSnapHelper = new PagerSnapHelperImpl()).attachToRecyclerView(this.mRecyclerView);
        this.mRecyclerView.addOnScrollListener((RecyclerView.OnScrollListener)this.mScrollEventAdapter);
        final CompositeOnPageChangeCallback compositeOnPageChangeCallback = new CompositeOnPageChangeCallback(3);
        this.mPageChangeEventDispatcher = compositeOnPageChangeCallback;
        this.mScrollEventAdapter.setOnPageChangeCallback(compositeOnPageChangeCallback);
        final OnPageChangeCallback onPageChangeCallback = new OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(final int n) {
                if (n == 0) {
                    ViewPager2.this.updateCurrentItem();
                }
            }
            
            @Override
            public void onPageSelected(final int mCurrentItem) {
                final ViewPager2 this$0 = ViewPager2.this;
                if (this$0.mCurrentItem != mCurrentItem) {
                    this$0.mCurrentItem = mCurrentItem;
                    this$0.mAccessibilityProvider.onSetNewCurrentItem();
                }
            }
        };
        final OnPageChangeCallback onPageChangeCallback2 = new OnPageChangeCallback() {
            @Override
            public void onPageSelected(final int n) {
                ViewPager2.this.clearFocus();
                if (ViewPager2.this.hasFocus()) {
                    ViewPager2.this.mRecyclerView.requestFocus(2);
                }
            }
        };
        this.mPageChangeEventDispatcher.addOnPageChangeCallback(onPageChangeCallback);
        this.mPageChangeEventDispatcher.addOnPageChangeCallback(onPageChangeCallback2);
        this.mAccessibilityProvider.onInitialize(this.mPageChangeEventDispatcher, this.mRecyclerView);
        this.mPageChangeEventDispatcher.addOnPageChangeCallback(this.mExternalPageChangeCallbacks);
        final PageTransformerAdapter mPageTransformerAdapter = new PageTransformerAdapter(this.mLayoutManager);
        this.mPageTransformerAdapter = mPageTransformerAdapter;
        this.mPageChangeEventDispatcher.addOnPageChangeCallback(mPageTransformerAdapter);
        final RecyclerView mRecyclerView = this.mRecyclerView;
        this.attachViewToParent((View)mRecyclerView, 0, mRecyclerView.getLayoutParams());
    }
    
    private void registerCurrentItemDataSetTracker(final RecyclerView.Adapter<?> adapter) {
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mCurrentItemDataSetChangeObserver);
        }
    }
    
    private void restorePendingState() {
        if (this.mPendingCurrentItem == -1) {
            return;
        }
        final RecyclerView.Adapter adapter = this.getAdapter();
        if (adapter == null) {
            return;
        }
        final Parcelable mPendingAdapterState = this.mPendingAdapterState;
        if (mPendingAdapterState != null) {
            if (adapter instanceof StatefulAdapter) {
                ((StatefulAdapter)adapter).restoreState(mPendingAdapterState);
            }
            this.mPendingAdapterState = null;
        }
        final int max = Math.max(0, Math.min(this.mPendingCurrentItem, adapter.getItemCount() - 1));
        this.mCurrentItem = max;
        this.mPendingCurrentItem = -1;
        this.mRecyclerView.scrollToPosition(max);
        this.mAccessibilityProvider.onRestorePendingState();
    }
    
    private void setOrientation(final Context context, final AttributeSet set) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.ViewPager2);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.ViewPager2, set, obtainStyledAttributes, 0, 0);
        try {
            this.setOrientation(obtainStyledAttributes.getInt(R$styleable.ViewPager2_android_orientation, 0));
        }
        finally {
            obtainStyledAttributes.recycle();
        }
    }
    
    private void unregisterCurrentItemDataSetTracker(final RecyclerView.Adapter<?> adapter) {
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(this.mCurrentItemDataSetChangeObserver);
        }
    }
    
    public boolean canScrollHorizontally(final int n) {
        return this.mRecyclerView.canScrollHorizontally(n);
    }
    
    public boolean canScrollVertically(final int n) {
        return this.mRecyclerView.canScrollVertically(n);
    }
    
    protected void dispatchRestoreInstanceState(final SparseArray<Parcelable> sparseArray) {
        final Parcelable parcelable = (Parcelable)sparseArray.get(this.getId());
        if (parcelable instanceof SavedState) {
            final int mRecyclerViewId = ((SavedState)parcelable).mRecyclerViewId;
            sparseArray.put(this.mRecyclerView.getId(), sparseArray.get(mRecyclerViewId));
            sparseArray.remove(mRecyclerViewId);
        }
        super.dispatchRestoreInstanceState((SparseArray)sparseArray);
        this.restorePendingState();
    }
    
    public CharSequence getAccessibilityClassName() {
        if (this.mAccessibilityProvider.handlesGetAccessibilityClassName()) {
            return this.mAccessibilityProvider.onGetAccessibilityClassName();
        }
        return super.getAccessibilityClassName();
    }
    
    public RecyclerView.Adapter getAdapter() {
        return this.mRecyclerView.getAdapter();
    }
    
    public int getCurrentItem() {
        return this.mCurrentItem;
    }
    
    public int getOffscreenPageLimit() {
        return this.mOffscreenPageLimit;
    }
    
    public int getOrientation() {
        return this.mLayoutManager.getOrientation();
    }
    
    int getPageSize() {
        final RecyclerView mRecyclerView = this.mRecyclerView;
        int n;
        int n2;
        if (this.getOrientation() == 0) {
            n = mRecyclerView.getWidth() - mRecyclerView.getPaddingLeft();
            n2 = mRecyclerView.getPaddingRight();
        }
        else {
            n = mRecyclerView.getHeight() - mRecyclerView.getPaddingTop();
            n2 = mRecyclerView.getPaddingBottom();
        }
        return n - n2;
    }
    
    public int getScrollState() {
        return this.mScrollEventAdapter.getScrollState();
    }
    
    public boolean isFakeDragging() {
        return this.mFakeDragger.isFakeDragging();
    }
    
    boolean isRtl() {
        final int layoutDirection = ((RecyclerView.LayoutManager)this.mLayoutManager).getLayoutDirection();
        boolean b = true;
        if (layoutDirection != 1) {
            b = false;
        }
        return b;
    }
    
    public boolean isUserInputEnabled() {
        return this.mUserInputEnabled;
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        this.mAccessibilityProvider.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        final int measuredWidth = this.mRecyclerView.getMeasuredWidth();
        final int measuredHeight = this.mRecyclerView.getMeasuredHeight();
        this.mTmpContainerRect.left = this.getPaddingLeft();
        this.mTmpContainerRect.right = n3 - n - this.getPaddingRight();
        this.mTmpContainerRect.top = this.getPaddingTop();
        this.mTmpContainerRect.bottom = n4 - n2 - this.getPaddingBottom();
        Gravity.apply(8388659, measuredWidth, measuredHeight, this.mTmpContainerRect, this.mTmpChildRect);
        final RecyclerView mRecyclerView = this.mRecyclerView;
        final Rect mTmpChildRect = this.mTmpChildRect;
        mRecyclerView.layout(mTmpChildRect.left, mTmpChildRect.top, mTmpChildRect.right, mTmpChildRect.bottom);
        if (this.mCurrentItemDirty) {
            this.updateCurrentItem();
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        this.measureChild((View)this.mRecyclerView, n, n2);
        final int measuredWidth = this.mRecyclerView.getMeasuredWidth();
        final int measuredHeight = this.mRecyclerView.getMeasuredHeight();
        final int measuredState = this.mRecyclerView.getMeasuredState();
        this.setMeasuredDimension(ViewGroup.resolveSizeAndState(Math.max(measuredWidth + (this.getPaddingLeft() + this.getPaddingRight()), this.getSuggestedMinimumWidth()), n, measuredState), ViewGroup.resolveSizeAndState(Math.max(measuredHeight + (this.getPaddingTop() + this.getPaddingBottom()), this.getSuggestedMinimumHeight()), n2, measuredState << 16));
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mPendingCurrentItem = savedState.mCurrentItem;
        this.mPendingAdapterState = savedState.mAdapterState;
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mRecyclerViewId = this.mRecyclerView.getId();
        int mCurrentItem;
        if ((mCurrentItem = this.mPendingCurrentItem) == -1) {
            mCurrentItem = this.mCurrentItem;
        }
        savedState.mCurrentItem = mCurrentItem;
        final Parcelable mPendingAdapterState = this.mPendingAdapterState;
        if (mPendingAdapterState != null) {
            savedState.mAdapterState = mPendingAdapterState;
        }
        else {
            final RecyclerView.Adapter adapter = this.mRecyclerView.getAdapter();
            if (adapter instanceof StatefulAdapter) {
                savedState.mAdapterState = ((StatefulAdapter)adapter).saveState();
            }
        }
        return (Parcelable)savedState;
    }
    
    public void onViewAdded(final View view) {
        final StringBuilder sb = new StringBuilder();
        sb.append(ViewPager2.class.getSimpleName());
        sb.append(" does not support direct child views");
        throw new IllegalStateException(sb.toString());
    }
    
    public boolean performAccessibilityAction(final int n, final Bundle bundle) {
        if (this.mAccessibilityProvider.handlesPerformAccessibilityAction(n, bundle)) {
            return this.mAccessibilityProvider.onPerformAccessibilityAction(n, bundle);
        }
        return super.performAccessibilityAction(n, bundle);
    }
    
    public void registerOnPageChangeCallback(final OnPageChangeCallback onPageChangeCallback) {
        this.mExternalPageChangeCallbacks.addOnPageChangeCallback(onPageChangeCallback);
    }
    
    public void setAdapter(final RecyclerView.Adapter adapter) {
        final RecyclerView.Adapter adapter2 = this.mRecyclerView.getAdapter();
        this.mAccessibilityProvider.onDetachAdapter(adapter2);
        this.unregisterCurrentItemDataSetTracker(adapter2);
        this.mRecyclerView.setAdapter(adapter);
        this.mCurrentItem = 0;
        this.restorePendingState();
        this.mAccessibilityProvider.onAttachAdapter(adapter);
        this.registerCurrentItemDataSetTracker(adapter);
    }
    
    public void setCurrentItem(final int n) {
        this.setCurrentItem(n, true);
    }
    
    public void setCurrentItem(final int n, final boolean b) {
        if (!this.isFakeDragging()) {
            this.setCurrentItemInternal(n, b);
            return;
        }
        throw new IllegalStateException("Cannot change current item when ViewPager2 is fake dragging");
    }
    
    void setCurrentItemInternal(int n, final boolean b) {
        final RecyclerView.Adapter adapter = this.getAdapter();
        if (adapter == null) {
            if (this.mPendingCurrentItem != -1) {
                this.mPendingCurrentItem = Math.max(n, 0);
            }
            return;
        }
        if (adapter.getItemCount() <= 0) {
            return;
        }
        final int min = Math.min(Math.max(n, 0), adapter.getItemCount() - 1);
        if (min == this.mCurrentItem && this.mScrollEventAdapter.isIdle()) {
            return;
        }
        if (min == this.mCurrentItem && b) {
            return;
        }
        double relativeScrollPosition = this.mCurrentItem;
        this.mCurrentItem = min;
        this.mAccessibilityProvider.onSetNewCurrentItem();
        if (!this.mScrollEventAdapter.isIdle()) {
            relativeScrollPosition = this.mScrollEventAdapter.getRelativeScrollPosition();
        }
        this.mScrollEventAdapter.notifyProgrammaticScroll(min, b);
        if (!b) {
            this.mRecyclerView.scrollToPosition(min);
            return;
        }
        final double n2 = min;
        if (Math.abs(n2 - relativeScrollPosition) > 3.0) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (n2 > relativeScrollPosition) {
                n = min - 3;
            }
            else {
                n = min + 3;
            }
            mRecyclerView.scrollToPosition(n);
            final RecyclerView mRecyclerView2 = this.mRecyclerView;
            mRecyclerView2.post((Runnable)new SmoothScrollToPosition(min, mRecyclerView2));
        }
        else {
            this.mRecyclerView.smoothScrollToPosition(min);
        }
    }
    
    public void setLayoutDirection(final int layoutDirection) {
        super.setLayoutDirection(layoutDirection);
        this.mAccessibilityProvider.onSetLayoutDirection();
    }
    
    public void setOrientation(final int orientation) {
        this.mLayoutManager.setOrientation(orientation);
        this.mAccessibilityProvider.onSetOrientation();
    }
    
    void updateCurrentItem() {
        final PagerSnapHelper mPagerSnapHelper = this.mPagerSnapHelper;
        if (mPagerSnapHelper == null) {
            throw new IllegalStateException("Design assumption violated.");
        }
        final View snapView = mPagerSnapHelper.findSnapView(this.mLayoutManager);
        if (snapView == null) {
            return;
        }
        final int position = ((RecyclerView.LayoutManager)this.mLayoutManager).getPosition(snapView);
        if (position != this.mCurrentItem && this.getScrollState() == 0) {
            this.mPageChangeEventDispatcher.onPageSelected(position);
        }
        this.mCurrentItemDirty = false;
    }
    
    private abstract class AccessibilityProvider
    {
        private AccessibilityProvider(final ViewPager2 viewPager2) {
        }
        
        boolean handlesGetAccessibilityClassName() {
            return false;
        }
        
        boolean handlesLmPerformAccessibilityAction(final int n) {
            return false;
        }
        
        boolean handlesPerformAccessibilityAction(final int n, final Bundle bundle) {
            return false;
        }
        
        boolean handlesRvGetAccessibilityClassName() {
            return false;
        }
        
        void onAttachAdapter(final RecyclerView.Adapter<?> adapter) {
        }
        
        void onDetachAdapter(final RecyclerView.Adapter<?> adapter) {
        }
        
        String onGetAccessibilityClassName() {
            throw new IllegalStateException("Not implemented.");
        }
        
        void onInitialize(final CompositeOnPageChangeCallback compositeOnPageChangeCallback, final RecyclerView recyclerView) {
        }
        
        void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        }
        
        void onLmInitializeAccessibilityNodeInfo(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        }
        
        void onLmInitializeAccessibilityNodeInfoForItem(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        }
        
        boolean onLmPerformAccessibilityAction(final int n) {
            throw new IllegalStateException("Not implemented.");
        }
        
        boolean onPerformAccessibilityAction(final int n, final Bundle bundle) {
            throw new IllegalStateException("Not implemented.");
        }
        
        void onRestorePendingState() {
        }
        
        CharSequence onRvGetAccessibilityClassName() {
            throw new IllegalStateException("Not implemented.");
        }
        
        void onRvInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        }
        
        void onSetLayoutDirection() {
        }
        
        void onSetNewCurrentItem() {
        }
        
        void onSetOrientation() {
        }
    }
    
    class BasicAccessibilityProvider extends AccessibilityProvider
    {
        public boolean handlesLmPerformAccessibilityAction(final int n) {
            return (n == 8192 || n == 4096) && !ViewPager2.this.isUserInputEnabled();
        }
        
        public boolean handlesRvGetAccessibilityClassName() {
            return true;
        }
        
        public void onLmInitializeAccessibilityNodeInfo(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            if (!ViewPager2.this.isUserInputEnabled()) {
                accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD);
                accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD);
                accessibilityNodeInfoCompat.setScrollable(false);
            }
        }
        
        public boolean onLmPerformAccessibilityAction(final int n) {
            if (this.handlesLmPerformAccessibilityAction(n)) {
                return false;
            }
            throw new IllegalStateException();
        }
        
        public CharSequence onRvGetAccessibilityClassName() {
            if (this.handlesRvGetAccessibilityClassName()) {
                return "androidx.viewpager.widget.ViewPager";
            }
            throw new IllegalStateException();
        }
    }
    
    private abstract static class DataSetChangeObserver extends AdapterDataObserver
    {
        @Override
        public final void onItemRangeChanged(final int n, final int n2) {
            ((RecyclerView.AdapterDataObserver)this).onChanged();
        }
        
        @Override
        public final void onItemRangeChanged(final int n, final int n2, final Object o) {
            ((RecyclerView.AdapterDataObserver)this).onChanged();
        }
        
        @Override
        public final void onItemRangeInserted(final int n, final int n2) {
            ((RecyclerView.AdapterDataObserver)this).onChanged();
        }
        
        @Override
        public final void onItemRangeMoved(final int n, final int n2, final int n3) {
            ((RecyclerView.AdapterDataObserver)this).onChanged();
        }
        
        @Override
        public final void onItemRangeRemoved(final int n, final int n2) {
            ((RecyclerView.AdapterDataObserver)this).onChanged();
        }
    }
    
    private class LinearLayoutManagerImpl extends LinearLayoutManager
    {
        LinearLayoutManagerImpl(final Context context) {
            super(context);
        }
        
        @Override
        protected void calculateExtraLayoutSpace(final State state, final int[] array) {
            final int offscreenPageLimit = ViewPager2.this.getOffscreenPageLimit();
            if (offscreenPageLimit == -1) {
                super.calculateExtraLayoutSpace(state, array);
                return;
            }
            array[1] = (array[0] = ViewPager2.this.getPageSize() * offscreenPageLimit);
        }
        
        @Override
        public void onInitializeAccessibilityNodeInfo(final Recycler recycler, final State state, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(recycler, state, accessibilityNodeInfoCompat);
            ViewPager2.this.mAccessibilityProvider.onLmInitializeAccessibilityNodeInfo(accessibilityNodeInfoCompat);
        }
        
        @Override
        public void onInitializeAccessibilityNodeInfoForItem(final Recycler recycler, final State state, final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            ViewPager2.this.mAccessibilityProvider.onLmInitializeAccessibilityNodeInfoForItem(view, accessibilityNodeInfoCompat);
        }
        
        @Override
        public boolean performAccessibilityAction(final Recycler recycler, final State state, final int n, final Bundle bundle) {
            if (ViewPager2.this.mAccessibilityProvider.handlesLmPerformAccessibilityAction(n)) {
                return ViewPager2.this.mAccessibilityProvider.onLmPerformAccessibilityAction(n);
            }
            return super.performAccessibilityAction(recycler, state, n, bundle);
        }
        
        @Override
        public boolean requestChildRectangleOnScreen(final RecyclerView recyclerView, final View view, final Rect rect, final boolean b, final boolean b2) {
            return false;
        }
    }
    
    public abstract static class OnPageChangeCallback
    {
        public void onPageScrollStateChanged(final int n) {
        }
        
        public void onPageScrolled(final int n, final float n2, final int n3) {
        }
        
        public void onPageSelected(final int n) {
        }
    }
    
    class PageAwareAccessibilityProvider extends AccessibilityProvider
    {
        private final AccessibilityViewCommand mActionPageBackward;
        private final AccessibilityViewCommand mActionPageForward;
        private RecyclerView.AdapterDataObserver mAdapterDataObserver;
        
        PageAwareAccessibilityProvider() {
            this.mActionPageForward = new AccessibilityViewCommand() {
                @Override
                public boolean perform(final View view, final CommandArguments commandArguments) {
                    PageAwareAccessibilityProvider.this.setCurrentItemFromAccessibilityCommand(((ViewPager2)view).getCurrentItem() + 1);
                    return true;
                }
            };
            this.mActionPageBackward = new AccessibilityViewCommand() {
                @Override
                public boolean perform(final View view, final CommandArguments commandArguments) {
                    PageAwareAccessibilityProvider.this.setCurrentItemFromAccessibilityCommand(((ViewPager2)view).getCurrentItem() - 1);
                    return true;
                }
            };
        }
        
        private void addCollectionInfo(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            final RecyclerView.Adapter adapter = ViewPager2.this.getAdapter();
            int itemCount = 1;
            int itemCount2;
            if (adapter != null) {
                if (ViewPager2.this.getOrientation() == 1) {
                    itemCount = ViewPager2.this.getAdapter().getItemCount();
                    itemCount2 = 1;
                }
                else {
                    itemCount2 = ViewPager2.this.getAdapter().getItemCount();
                }
            }
            else {
                itemCount2 = (itemCount = 0);
            }
            accessibilityNodeInfoCompat.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(itemCount, itemCount2, false, 0));
        }
        
        private void addCollectionItemInfo(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            final int orientation = ViewPager2.this.getOrientation();
            int position = 0;
            int position2;
            if (orientation == 1) {
                position2 = ((RecyclerView.LayoutManager)ViewPager2.this.mLayoutManager).getPosition(view);
            }
            else {
                position2 = 0;
            }
            if (ViewPager2.this.getOrientation() == 0) {
                position = ((RecyclerView.LayoutManager)ViewPager2.this.mLayoutManager).getPosition(view);
            }
            accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(position2, 1, position, 1, false, false));
        }
        
        private void addScrollActions(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            final RecyclerView.Adapter adapter = ViewPager2.this.getAdapter();
            if (adapter == null) {
                return;
            }
            final int itemCount = adapter.getItemCount();
            if (itemCount != 0) {
                if (ViewPager2.this.isUserInputEnabled()) {
                    if (ViewPager2.this.mCurrentItem > 0) {
                        accessibilityNodeInfoCompat.addAction(8192);
                    }
                    if (ViewPager2.this.mCurrentItem < itemCount - 1) {
                        accessibilityNodeInfoCompat.addAction(4096);
                    }
                    accessibilityNodeInfoCompat.setScrollable(true);
                }
            }
        }
        
        public boolean handlesGetAccessibilityClassName() {
            return true;
        }
        
        public boolean handlesPerformAccessibilityAction(final int n, final Bundle bundle) {
            return n == 8192 || n == 4096;
        }
        
        public void onAttachAdapter(final RecyclerView.Adapter<?> adapter) {
            this.updatePageAccessibilityActions();
            if (adapter != null) {
                adapter.registerAdapterDataObserver(this.mAdapterDataObserver);
            }
        }
        
        public void onDetachAdapter(final RecyclerView.Adapter<?> adapter) {
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(this.mAdapterDataObserver);
            }
        }
        
        public String onGetAccessibilityClassName() {
            if (this.handlesGetAccessibilityClassName()) {
                return "androidx.viewpager.widget.ViewPager";
            }
            throw new IllegalStateException();
        }
        
        public void onInitialize(final CompositeOnPageChangeCallback compositeOnPageChangeCallback, final RecyclerView recyclerView) {
            ViewCompat.setImportantForAccessibility((View)recyclerView, 2);
            this.mAdapterDataObserver = new DataSetChangeObserver() {
                @Override
                public void onChanged() {
                    PageAwareAccessibilityProvider.this.updatePageAccessibilityActions();
                }
            };
            if (ViewCompat.getImportantForAccessibility((View)ViewPager2.this) == 0) {
                ViewCompat.setImportantForAccessibility((View)ViewPager2.this, 1);
            }
        }
        
        public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
            final AccessibilityNodeInfoCompat wrap = AccessibilityNodeInfoCompat.wrap(accessibilityNodeInfo);
            this.addCollectionInfo(wrap);
            if (Build$VERSION.SDK_INT >= 16) {
                this.addScrollActions(wrap);
            }
        }
        
        @Override
        void onLmInitializeAccessibilityNodeInfoForItem(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            this.addCollectionItemInfo(view, accessibilityNodeInfoCompat);
        }
        
        public boolean onPerformAccessibilityAction(int currentItemFromAccessibilityCommand, final Bundle bundle) {
            if (this.handlesPerformAccessibilityAction(currentItemFromAccessibilityCommand, bundle)) {
                if (currentItemFromAccessibilityCommand == 8192) {
                    currentItemFromAccessibilityCommand = ViewPager2.this.getCurrentItem() - 1;
                }
                else {
                    currentItemFromAccessibilityCommand = ViewPager2.this.getCurrentItem() + 1;
                }
                this.setCurrentItemFromAccessibilityCommand(currentItemFromAccessibilityCommand);
                return true;
            }
            throw new IllegalStateException();
        }
        
        public void onRestorePendingState() {
            this.updatePageAccessibilityActions();
        }
        
        public void onRvInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.setSource((View)ViewPager2.this);
            accessibilityEvent.setClassName((CharSequence)this.onGetAccessibilityClassName());
        }
        
        public void onSetLayoutDirection() {
            this.updatePageAccessibilityActions();
        }
        
        public void onSetNewCurrentItem() {
            this.updatePageAccessibilityActions();
        }
        
        public void onSetOrientation() {
            this.updatePageAccessibilityActions();
        }
        
        void setCurrentItemFromAccessibilityCommand(final int n) {
            if (ViewPager2.this.isUserInputEnabled()) {
                ViewPager2.this.setCurrentItemInternal(n, true);
            }
        }
        
        void updatePageAccessibilityActions() {
            final ViewPager2 this$0 = ViewPager2.this;
            int n = 16908360;
            ViewCompat.removeAccessibilityAction((View)this$0, 16908360);
            ViewCompat.removeAccessibilityAction((View)this$0, 16908361);
            ViewCompat.removeAccessibilityAction((View)this$0, 16908358);
            ViewCompat.removeAccessibilityAction((View)this$0, 16908359);
            if (ViewPager2.this.getAdapter() == null) {
                return;
            }
            final int itemCount = ViewPager2.this.getAdapter().getItemCount();
            if (itemCount == 0) {
                return;
            }
            if (!ViewPager2.this.isUserInputEnabled()) {
                return;
            }
            if (ViewPager2.this.getOrientation() == 0) {
                final boolean rtl = ViewPager2.this.isRtl();
                int n2;
                if (rtl) {
                    n2 = 16908360;
                }
                else {
                    n2 = 16908361;
                }
                if (rtl) {
                    n = 16908361;
                }
                if (ViewPager2.this.mCurrentItem < itemCount - 1) {
                    ViewCompat.replaceAccessibilityAction((View)this$0, new AccessibilityNodeInfoCompat.AccessibilityActionCompat(n2, null), null, this.mActionPageForward);
                }
                if (ViewPager2.this.mCurrentItem > 0) {
                    ViewCompat.replaceAccessibilityAction((View)this$0, new AccessibilityNodeInfoCompat.AccessibilityActionCompat(n, null), null, this.mActionPageBackward);
                }
            }
            else {
                if (ViewPager2.this.mCurrentItem < itemCount - 1) {
                    ViewCompat.replaceAccessibilityAction((View)this$0, new AccessibilityNodeInfoCompat.AccessibilityActionCompat(16908359, null), null, this.mActionPageForward);
                }
                if (ViewPager2.this.mCurrentItem > 0) {
                    ViewCompat.replaceAccessibilityAction((View)this$0, new AccessibilityNodeInfoCompat.AccessibilityActionCompat(16908358, null), null, this.mActionPageBackward);
                }
            }
        }
    }
    
    public interface PageTransformer
    {
        void transformPage(final View p0, final float p1);
    }
    
    private class PagerSnapHelperImpl extends PagerSnapHelper
    {
        PagerSnapHelperImpl() {
        }
        
        @Override
        public View findSnapView(final LayoutManager layoutManager) {
            View snapView;
            if (ViewPager2.this.isFakeDragging()) {
                snapView = null;
            }
            else {
                snapView = super.findSnapView(layoutManager);
            }
            return snapView;
        }
    }
    
    private class RecyclerViewImpl extends RecyclerView
    {
        RecyclerViewImpl(final Context context) {
            super(context);
        }
        
        @Override
        public CharSequence getAccessibilityClassName() {
            if (ViewPager2.this.mAccessibilityProvider.handlesRvGetAccessibilityClassName()) {
                return ViewPager2.this.mAccessibilityProvider.onRvGetAccessibilityClassName();
            }
            return super.getAccessibilityClassName();
        }
        
        public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            accessibilityEvent.setFromIndex(ViewPager2.this.mCurrentItem);
            accessibilityEvent.setToIndex(ViewPager2.this.mCurrentItem);
            ViewPager2.this.mAccessibilityProvider.onRvInitializeAccessibilityEvent(accessibilityEvent);
        }
        
        @Override
        public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
            return ViewPager2.this.isUserInputEnabled() && super.onInterceptTouchEvent(motionEvent);
        }
        
        @SuppressLint({ "ClickableViewAccessibility" })
        @Override
        public boolean onTouchEvent(final MotionEvent motionEvent) {
            return ViewPager2.this.isUserInputEnabled() && super.onTouchEvent(motionEvent);
        }
    }
    
    static class SavedState extends View$BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        Parcelable mAdapterState;
        int mCurrentItem;
        int mRecyclerViewId;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$ClassLoaderCreator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return this.createFromParcel(parcel, null);
                }
                
                public SavedState createFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                    SavedState savedState;
                    if (Build$VERSION.SDK_INT >= 24) {
                        savedState = new SavedState(parcel, classLoader);
                    }
                    else {
                        savedState = new SavedState(parcel);
                    }
                    return savedState;
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState(final Parcel parcel) {
            super(parcel);
            this.readValues(parcel, null);
        }
        
        SavedState(final Parcel parcel, final ClassLoader classLoader) {
            super(parcel, classLoader);
            this.readValues(parcel, classLoader);
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        private void readValues(final Parcel parcel, final ClassLoader classLoader) {
            this.mRecyclerViewId = parcel.readInt();
            this.mCurrentItem = parcel.readInt();
            this.mAdapterState = parcel.readParcelable(classLoader);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.mRecyclerViewId);
            parcel.writeInt(this.mCurrentItem);
            parcel.writeParcelable(this.mAdapterState, n);
        }
    }
    
    private static class SmoothScrollToPosition implements Runnable
    {
        private final int mPosition;
        private final RecyclerView mRecyclerView;
        
        SmoothScrollToPosition(final int mPosition, final RecyclerView mRecyclerView) {
            this.mPosition = mPosition;
            this.mRecyclerView = mRecyclerView;
        }
        
        @Override
        public void run() {
            this.mRecyclerView.smoothScrollToPosition(this.mPosition);
        }
    }
}
