// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import androidx.customview.view.AbsSavedState;
import java.util.Collections;
import android.os.Bundle;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import android.graphics.Matrix;
import android.view.ViewGroup$MarginLayoutParams;
import android.database.Observable;
import android.os.SystemClock;
import android.animation.LayoutTransition;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.MotionEventCompat;
import android.view.View$MeasureSpec;
import android.view.Display;
import android.content.res.Resources;
import androidx.recyclerview.R$dimen;
import android.graphics.drawable.Drawable;
import android.view.FocusFinder;
import android.widget.OverScroller;
import android.graphics.Canvas;
import android.util.SparseArray;
import androidx.core.os.TraceCompat;
import androidx.core.widget.EdgeEffectCompat;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.os.Parcelable;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import android.view.accessibility.AccessibilityEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import android.view.ViewParent;
import java.lang.ref.WeakReference;
import android.view.ViewGroup$LayoutParams;
import android.content.res.TypedArray;
import android.graphics.drawable.StateListDrawable;
import androidx.recyclerview.R$styleable;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewConfigurationCompat;
import android.view.ViewConfiguration;
import androidx.recyclerview.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Build$VERSION;
import android.view.VelocityTracker;
import android.graphics.RectF;
import android.graphics.Rect;
import androidx.core.view.NestedScrollingChildHelper;
import java.util.List;
import java.util.ArrayList;
import android.widget.EdgeEffect;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import androidx.core.view.NestedScrollingChild;
import android.view.ViewGroup;

public class RecyclerView extends ViewGroup implements NestedScrollingChild
{
    static final boolean ALLOW_SIZE_IN_UNSPECIFIED_SPEC;
    static final boolean ALLOW_THREAD_GAP_WORK;
    private static final boolean FORCE_ABS_FOCUS_SEARCH_DIRECTION;
    static final boolean FORCE_INVALIDATE_DISPLAY_LIST;
    private static final boolean IGNORE_DETACHED_FOCUSED_CHILD;
    private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE;
    private static final int[] NESTED_SCROLLING_ATTRS;
    static final boolean POST_UPDATES_ON_ANIMATION;
    static final Interpolator sQuinticInterpolator;
    RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
    private final AccessibilityManager mAccessibilityManager;
    Adapter mAdapter;
    AdapterHelper mAdapterHelper;
    boolean mAdapterUpdateDuringMeasure;
    private EdgeEffect mBottomGlow;
    private ChildDrawingOrderCallback mChildDrawingOrderCallback;
    ChildHelper mChildHelper;
    boolean mClipToPadding;
    boolean mDataSetHasChangedAfterLayout;
    boolean mDispatchItemsChangedEvent;
    private int mDispatchScrollCounter;
    private int mEatenAccessibilityChangeFlags;
    private EdgeEffectFactory mEdgeEffectFactory;
    boolean mEnableFastScroller;
    boolean mFirstLayoutComplete;
    GapWorker mGapWorker;
    boolean mHasFixedSize;
    private boolean mIgnoreMotionEventTillDown;
    private int mInitialTouchX;
    private int mInitialTouchY;
    private int mInterceptRequestLayoutDepth;
    private OnItemTouchListener mInterceptingOnItemTouchListener;
    boolean mIsAttached;
    ItemAnimator mItemAnimator;
    private ItemAnimatorListener mItemAnimatorListener;
    private Runnable mItemAnimatorRunner;
    final ArrayList<ItemDecoration> mItemDecorations;
    boolean mItemsAddedOrRemoved;
    boolean mItemsChanged;
    private int mLastTouchX;
    private int mLastTouchY;
    LayoutManager mLayout;
    private int mLayoutOrScrollCounter;
    boolean mLayoutSuppressed;
    boolean mLayoutWasDefered;
    private EdgeEffect mLeftGlow;
    private final int mMaxFlingVelocity;
    private final int mMinFlingVelocity;
    private final int[] mMinMaxLayoutPositions;
    private final int[] mNestedOffsets;
    private final RecyclerViewDataObserver mObserver;
    private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
    private OnFlingListener mOnFlingListener;
    private final ArrayList<OnItemTouchListener> mOnItemTouchListeners;
    final List<ViewHolder> mPendingAccessibilityImportanceChange;
    SavedState mPendingSavedState;
    boolean mPostedAnimatorRunner;
    GapWorker.LayoutPrefetchRegistryImpl mPrefetchRegistry;
    private boolean mPreserveFocusAfterLayout;
    final Recycler mRecycler;
    RecyclerListener mRecyclerListener;
    final int[] mReusableIntPair;
    private EdgeEffect mRightGlow;
    private float mScaledHorizontalScrollFactor;
    private float mScaledVerticalScrollFactor;
    private OnScrollListener mScrollListener;
    private List<OnScrollListener> mScrollListeners;
    private final int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState;
    private NestedScrollingChildHelper mScrollingChildHelper;
    final State mState;
    final Rect mTempRect;
    private final Rect mTempRect2;
    final RectF mTempRectF;
    private EdgeEffect mTopGlow;
    private int mTouchSlop;
    final Runnable mUpdateChildViewsRunnable;
    private VelocityTracker mVelocityTracker;
    final ViewFlinger mViewFlinger;
    private final ViewInfoStore.ProcessCallback mViewInfoProcessCallback;
    final ViewInfoStore mViewInfoStore;
    
    static {
        final int sdk_INT = Build$VERSION.SDK_INT;
        NESTED_SCROLLING_ATTRS = new int[] { 16843830 };
        FORCE_INVALIDATE_DISPLAY_LIST = (sdk_INT == 18 || sdk_INT == 19 || sdk_INT == 20);
        ALLOW_SIZE_IN_UNSPECIFIED_SPEC = (sdk_INT >= 23);
        POST_UPDATES_ON_ANIMATION = (sdk_INT >= 16);
        ALLOW_THREAD_GAP_WORK = (sdk_INT >= 21);
        FORCE_ABS_FOCUS_SEARCH_DIRECTION = (sdk_INT <= 15);
        IGNORE_DETACHED_FOCUSED_CHILD = (sdk_INT <= 15);
        final Class<Integer> type = Integer.TYPE;
        LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = new Class[] { Context.class, AttributeSet.class, type, type };
        sQuinticInterpolator = (Interpolator)new Interpolator() {
            public float getInterpolation(float n) {
                --n;
                return n * n * n * n * n + 1.0f;
            }
        };
    }
    
    public RecyclerView(final Context context) {
        this(context, null);
    }
    
    public RecyclerView(final Context context, final AttributeSet set) {
        this(context, set, R$attr.recyclerViewStyle);
    }
    
    public RecyclerView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mObserver = new RecyclerViewDataObserver();
        this.mRecycler = new Recycler();
        this.mViewInfoStore = new ViewInfoStore();
        this.mUpdateChildViewsRunnable = new Runnable() {
            @Override
            public void run() {
                final RecyclerView this$0 = RecyclerView.this;
                if (this$0.mFirstLayoutComplete) {
                    if (!this$0.isLayoutRequested()) {
                        final RecyclerView this$2 = RecyclerView.this;
                        if (!this$2.mIsAttached) {
                            this$2.requestLayout();
                            return;
                        }
                        if (this$2.mLayoutSuppressed) {
                            this$2.mLayoutWasDefered = true;
                            return;
                        }
                        this$2.consumePendingUpdateOperations();
                    }
                }
            }
        };
        this.mTempRect = new Rect();
        this.mTempRect2 = new Rect();
        this.mTempRectF = new RectF();
        this.mItemDecorations = new ArrayList<ItemDecoration>();
        this.mOnItemTouchListeners = new ArrayList<OnItemTouchListener>();
        this.mInterceptRequestLayoutDepth = 0;
        this.mDataSetHasChangedAfterLayout = false;
        this.mDispatchItemsChangedEvent = false;
        this.mLayoutOrScrollCounter = 0;
        this.mDispatchScrollCounter = 0;
        this.mEdgeEffectFactory = new EdgeEffectFactory();
        this.mItemAnimator = (ItemAnimator)new DefaultItemAnimator();
        this.mScrollState = 0;
        this.mScrollPointerId = -1;
        this.mScaledHorizontalScrollFactor = Float.MIN_VALUE;
        this.mScaledVerticalScrollFactor = Float.MIN_VALUE;
        final boolean b = true;
        this.mPreserveFocusAfterLayout = true;
        this.mViewFlinger = new ViewFlinger();
        GapWorker.LayoutPrefetchRegistryImpl mPrefetchRegistry;
        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
            mPrefetchRegistry = new GapWorker.LayoutPrefetchRegistryImpl();
        }
        else {
            mPrefetchRegistry = null;
        }
        this.mPrefetchRegistry = mPrefetchRegistry;
        this.mState = new State();
        this.mItemsAddedOrRemoved = false;
        this.mItemsChanged = false;
        this.mItemAnimatorListener = (ItemAnimatorListener)new ItemAnimatorRestoreListener();
        this.mPostedAnimatorRunner = false;
        this.mMinMaxLayoutPositions = new int[2];
        this.mScrollOffset = new int[2];
        this.mNestedOffsets = new int[2];
        this.mReusableIntPair = new int[2];
        this.mPendingAccessibilityImportanceChange = new ArrayList<ViewHolder>();
        this.mItemAnimatorRunner = new Runnable() {
            @Override
            public void run() {
                final ItemAnimator mItemAnimator = RecyclerView.this.mItemAnimator;
                if (mItemAnimator != null) {
                    mItemAnimator.runPendingAnimations();
                }
                RecyclerView.this.mPostedAnimatorRunner = false;
            }
        };
        this.mViewInfoProcessCallback = new ViewInfoStore.ProcessCallback() {
            @Override
            public void processAppeared(final ViewHolder viewHolder, final ItemHolderInfo itemHolderInfo, final ItemHolderInfo itemHolderInfo2) {
                RecyclerView.this.animateAppearance(viewHolder, itemHolderInfo, itemHolderInfo2);
            }
            
            @Override
            public void processDisappeared(final ViewHolder viewHolder, final ItemHolderInfo itemHolderInfo, final ItemHolderInfo itemHolderInfo2) {
                RecyclerView.this.mRecycler.unscrapView(viewHolder);
                RecyclerView.this.animateDisappearance(viewHolder, itemHolderInfo, itemHolderInfo2);
            }
            
            @Override
            public void processPersistent(final ViewHolder viewHolder, final ItemHolderInfo itemHolderInfo, final ItemHolderInfo itemHolderInfo2) {
                viewHolder.setIsRecyclable(false);
                final RecyclerView this$0 = RecyclerView.this;
                if (this$0.mDataSetHasChangedAfterLayout) {
                    if (this$0.mItemAnimator.animateChange(viewHolder, viewHolder, itemHolderInfo, itemHolderInfo2)) {
                        RecyclerView.this.postAnimationRunner();
                    }
                }
                else if (this$0.mItemAnimator.animatePersistence(viewHolder, itemHolderInfo, itemHolderInfo2)) {
                    RecyclerView.this.postAnimationRunner();
                }
            }
            
            @Override
            public void unused(final ViewHolder viewHolder) {
                final RecyclerView this$0 = RecyclerView.this;
                this$0.mLayout.removeAndRecycleView(viewHolder.itemView, this$0.mRecycler);
            }
        };
        this.setScrollContainer(true);
        this.setFocusableInTouchMode(true);
        final ViewConfiguration value = ViewConfiguration.get(context);
        this.mTouchSlop = value.getScaledTouchSlop();
        this.mScaledHorizontalScrollFactor = ViewConfigurationCompat.getScaledHorizontalScrollFactor(value, context);
        this.mScaledVerticalScrollFactor = ViewConfigurationCompat.getScaledVerticalScrollFactor(value, context);
        this.mMinFlingVelocity = value.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = value.getScaledMaximumFlingVelocity();
        this.setWillNotDraw(this.getOverScrollMode() == 2);
        this.mItemAnimator.setListener(this.mItemAnimatorListener);
        this.initAdapterManager();
        this.initChildrenHelper();
        this.initAutofill();
        if (ViewCompat.getImportantForAccessibility((View)this) == 0) {
            ViewCompat.setImportantForAccessibility((View)this, 1);
        }
        this.mAccessibilityManager = (AccessibilityManager)this.getContext().getSystemService("accessibility");
        this.setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.RecyclerView, n, 0);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.RecyclerView, set, obtainStyledAttributes, n, 0);
        final String string = obtainStyledAttributes.getString(R$styleable.RecyclerView_layoutManager);
        if (obtainStyledAttributes.getInt(R$styleable.RecyclerView_android_descendantFocusability, -1) == -1) {
            this.setDescendantFocusability(262144);
        }
        this.mClipToPadding = obtainStyledAttributes.getBoolean(R$styleable.RecyclerView_android_clipToPadding, true);
        final boolean boolean1 = obtainStyledAttributes.getBoolean(R$styleable.RecyclerView_fastScrollEnabled, false);
        this.mEnableFastScroller = boolean1;
        if (boolean1) {
            this.initFastScroller((StateListDrawable)obtainStyledAttributes.getDrawable(R$styleable.RecyclerView_fastScrollVerticalThumbDrawable), obtainStyledAttributes.getDrawable(R$styleable.RecyclerView_fastScrollVerticalTrackDrawable), (StateListDrawable)obtainStyledAttributes.getDrawable(R$styleable.RecyclerView_fastScrollHorizontalThumbDrawable), obtainStyledAttributes.getDrawable(R$styleable.RecyclerView_fastScrollHorizontalTrackDrawable));
        }
        obtainStyledAttributes.recycle();
        this.createLayoutManager(context, string, set, n, 0);
        boolean boolean2 = b;
        if (Build$VERSION.SDK_INT >= 21) {
            final TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(set, RecyclerView.NESTED_SCROLLING_ATTRS, n, 0);
            ViewCompat.saveAttributeDataForStyleable((View)this, context, RecyclerView.NESTED_SCROLLING_ATTRS, set, obtainStyledAttributes2, n, 0);
            boolean2 = obtainStyledAttributes2.getBoolean(0, true);
            obtainStyledAttributes2.recycle();
        }
        this.setNestedScrollingEnabled(boolean2);
    }
    
    static /* synthetic */ void access$000(final RecyclerView recyclerView, final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        recyclerView.attachViewToParent(view, n, viewGroup$LayoutParams);
    }
    
    static /* synthetic */ void access$100(final RecyclerView recyclerView, final int n) {
        recyclerView.detachViewFromParent(n);
    }
    
    static /* synthetic */ boolean access$200(final RecyclerView recyclerView) {
        return recyclerView.awakenScrollBars();
    }
    
    static /* synthetic */ void access$300(final RecyclerView recyclerView, final int n, final int n2) {
        recyclerView.setMeasuredDimension(n, n2);
    }
    
    private void addAnimatingView(final ViewHolder viewHolder) {
        final View itemView = viewHolder.itemView;
        final boolean b = itemView.getParent() == this;
        this.mRecycler.unscrapView(this.getChildViewHolder(itemView));
        if (viewHolder.isTmpDetached()) {
            this.mChildHelper.attachViewToParent(itemView, -1, itemView.getLayoutParams(), true);
        }
        else if (!b) {
            this.mChildHelper.addView(itemView, true);
        }
        else {
            this.mChildHelper.hide(itemView);
        }
    }
    
    private void animateChange(final ViewHolder mShadowingHolder, final ViewHolder mShadowedHolder, final ItemHolderInfo itemHolderInfo, final ItemHolderInfo itemHolderInfo2, final boolean b, final boolean b2) {
        mShadowingHolder.setIsRecyclable(false);
        if (b) {
            this.addAnimatingView(mShadowingHolder);
        }
        if (mShadowingHolder != mShadowedHolder) {
            if (b2) {
                this.addAnimatingView(mShadowedHolder);
            }
            mShadowingHolder.mShadowedHolder = mShadowedHolder;
            this.addAnimatingView(mShadowingHolder);
            this.mRecycler.unscrapView(mShadowingHolder);
            mShadowedHolder.setIsRecyclable(false);
            mShadowedHolder.mShadowingHolder = mShadowingHolder;
        }
        if (this.mItemAnimator.animateChange(mShadowingHolder, mShadowedHolder, itemHolderInfo, itemHolderInfo2)) {
            this.postAnimationRunner();
        }
    }
    
    private void cancelScroll() {
        this.resetScroll();
        this.setScrollState(0);
    }
    
    static void clearNestedRecyclerViewIfNotNested(final ViewHolder viewHolder) {
        final WeakReference<RecyclerView> mNestedRecyclerView = viewHolder.mNestedRecyclerView;
        if (mNestedRecyclerView != null) {
            View view = mNestedRecyclerView.get();
            while (view != null) {
                if (view == viewHolder.itemView) {
                    return;
                }
                final ViewParent parent = view.getParent();
                if (parent instanceof View) {
                    view = (View)parent;
                }
                else {
                    view = null;
                }
            }
            viewHolder.mNestedRecyclerView = null;
        }
    }
    
    private void createLayoutManager(Context ex, String trim, final AttributeSet set, final int i, final int j) {
        if (trim != null) {
            trim = trim.trim();
            if (!trim.isEmpty()) {
                final String fullClassName = this.getFullClassName((Context)ex, trim);
                try {
                    ClassLoader loader;
                    if (this.isInEditMode()) {
                        loader = this.getClass().getClassLoader();
                    }
                    else {
                        loader = ((Context)ex).getClassLoader();
                    }
                    final Class<? extends LayoutManager> subclass = Class.forName(fullClassName, false, loader).asSubclass(LayoutManager.class);
                    final NoSuchMethodException ex2 = null;
                    try {
                        final Constructor<? extends LayoutManager> constructor = subclass.getConstructor(RecyclerView.LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
                        ex = (NoSuchMethodException)new Object[] { ex, set, i, j };
                    }
                    catch (NoSuchMethodException ex) {
                        try {
                            final Constructor<? extends LayoutManager> constructor = subclass.getConstructor((Class<?>[])new Class[0]);
                            ex = ex2;
                            constructor.setAccessible(true);
                            this.setLayoutManager((LayoutManager)constructor.newInstance((Object[])(Object)ex));
                        }
                        catch (NoSuchMethodException cause) {
                            cause.initCause(ex);
                            final StringBuilder sb = new StringBuilder();
                            sb.append(set.getPositionDescription());
                            sb.append(": Error creating LayoutManager ");
                            sb.append(fullClassName);
                            throw new IllegalStateException(sb.toString(), cause);
                        }
                    }
                }
                catch (ClassCastException cause2) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append(set.getPositionDescription());
                    sb2.append(": Class is not a LayoutManager ");
                    sb2.append(fullClassName);
                    throw new IllegalStateException(sb2.toString(), cause2);
                }
                catch (IllegalAccessException cause3) {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append(set.getPositionDescription());
                    sb3.append(": Cannot access non-public constructor ");
                    sb3.append(fullClassName);
                    throw new IllegalStateException(sb3.toString(), cause3);
                }
                catch (InstantiationException cause4) {
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append(set.getPositionDescription());
                    sb4.append(": Could not instantiate the LayoutManager: ");
                    sb4.append(fullClassName);
                    throw new IllegalStateException(sb4.toString(), cause4);
                }
                catch (InvocationTargetException cause5) {
                    final StringBuilder sb5 = new StringBuilder();
                    sb5.append(set.getPositionDescription());
                    sb5.append(": Could not instantiate the LayoutManager: ");
                    sb5.append(fullClassName);
                    throw new IllegalStateException(sb5.toString(), cause5);
                }
                catch (ClassNotFoundException cause6) {
                    final StringBuilder sb6 = new StringBuilder();
                    sb6.append(set.getPositionDescription());
                    sb6.append(": Unable to find LayoutManager ");
                    sb6.append(fullClassName);
                    throw new IllegalStateException(sb6.toString(), cause6);
                }
            }
        }
    }
    
    private boolean didChildRangeChange(final int n, final int n2) {
        this.findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        final int[] mMinMaxLayoutPositions = this.mMinMaxLayoutPositions;
        boolean b = false;
        if (mMinMaxLayoutPositions[0] != n || mMinMaxLayoutPositions[1] != n2) {
            b = true;
        }
        return b;
    }
    
    private void dispatchContentChangedIfNecessary() {
        final int mEatenAccessibilityChangeFlags = this.mEatenAccessibilityChangeFlags;
        this.mEatenAccessibilityChangeFlags = 0;
        if (mEatenAccessibilityChangeFlags != 0 && this.isAccessibilityEnabled()) {
            final AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(2048);
            AccessibilityEventCompat.setContentChangeTypes(obtain, mEatenAccessibilityChangeFlags);
            this.sendAccessibilityEventUnchecked(obtain);
        }
    }
    
    private void dispatchLayoutStep1() {
        final State mState = this.mState;
        boolean mTrackOldChangeHolders = true;
        mState.assertLayoutStep(1);
        this.fillRemainingScrollValues(this.mState);
        this.mState.mIsMeasuring = false;
        this.startInterceptRequestLayout();
        this.mViewInfoStore.clear();
        this.onEnterLayoutOrScroll();
        this.processAdapterUpdatesAndSetAnimationFlags();
        this.saveFocusInfo();
        final State mState2 = this.mState;
        if (!mState2.mRunSimpleAnimations || !this.mItemsChanged) {
            mTrackOldChangeHolders = false;
        }
        mState2.mTrackOldChangeHolders = mTrackOldChangeHolders;
        this.mItemsChanged = false;
        this.mItemsAddedOrRemoved = false;
        final State mState3 = this.mState;
        mState3.mInPreLayout = mState3.mRunPredictiveAnimations;
        mState3.mItemCount = this.mAdapter.getItemCount();
        this.findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        if (this.mState.mRunSimpleAnimations) {
            for (int childCount = this.mChildHelper.getChildCount(), i = 0; i < childCount; ++i) {
                final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!childViewHolderInt.shouldIgnore()) {
                    if (!childViewHolderInt.isInvalid() || this.mAdapter.hasStableIds()) {
                        this.mViewInfoStore.addToPreLayout(childViewHolderInt, this.mItemAnimator.recordPreLayoutInformation(this.mState, childViewHolderInt, ItemAnimator.buildAdapterChangeFlagsForAnimations(childViewHolderInt), childViewHolderInt.getUnmodifiedPayloads()));
                        if (this.mState.mTrackOldChangeHolders && childViewHolderInt.isUpdated() && !childViewHolderInt.isRemoved() && !childViewHolderInt.shouldIgnore() && !childViewHolderInt.isInvalid()) {
                            this.mViewInfoStore.addToOldChangeHolders(this.getChangedHolderKey(childViewHolderInt), childViewHolderInt);
                        }
                    }
                }
            }
        }
        if (this.mState.mRunPredictiveAnimations) {
            this.saveOldPositions();
            final State mState4 = this.mState;
            final boolean mStructureChanged = mState4.mStructureChanged;
            mState4.mStructureChanged = false;
            this.mLayout.onLayoutChildren(this.mRecycler, mState4);
            this.mState.mStructureChanged = mStructureChanged;
            for (int j = 0; j < this.mChildHelper.getChildCount(); ++j) {
                final ViewHolder childViewHolderInt2 = getChildViewHolderInt(this.mChildHelper.getChildAt(j));
                if (!childViewHolderInt2.shouldIgnore()) {
                    if (!this.mViewInfoStore.isInPreLayout(childViewHolderInt2)) {
                        final int buildAdapterChangeFlagsForAnimations = ItemAnimator.buildAdapterChangeFlagsForAnimations(childViewHolderInt2);
                        final boolean hasAnyOfTheFlags = childViewHolderInt2.hasAnyOfTheFlags(8192);
                        int n = buildAdapterChangeFlagsForAnimations;
                        if (!hasAnyOfTheFlags) {
                            n = (buildAdapterChangeFlagsForAnimations | 0x1000);
                        }
                        final ItemHolderInfo recordPreLayoutInformation = this.mItemAnimator.recordPreLayoutInformation(this.mState, childViewHolderInt2, n, childViewHolderInt2.getUnmodifiedPayloads());
                        if (hasAnyOfTheFlags) {
                            this.recordAnimationInfoIfBouncedHiddenView(childViewHolderInt2, recordPreLayoutInformation);
                        }
                        else {
                            this.mViewInfoStore.addToAppearedInPreLayoutHolders(childViewHolderInt2, recordPreLayoutInformation);
                        }
                    }
                }
            }
            this.clearOldPositions();
        }
        else {
            this.clearOldPositions();
        }
        this.onExitLayoutOrScroll();
        this.stopInterceptRequestLayout(false);
        this.mState.mLayoutStep = 2;
    }
    
    private void dispatchLayoutStep2() {
        this.startInterceptRequestLayout();
        this.onEnterLayoutOrScroll();
        this.mState.assertLayoutStep(6);
        this.mAdapterHelper.consumeUpdatesInOnePass();
        this.mState.mItemCount = this.mAdapter.getItemCount();
        this.mState.mDeletedInvisibleItemCountSincePreviousLayout = 0;
        if (this.mPendingSavedState != null && this.mAdapter.canRestoreState()) {
            final Parcelable mLayoutState = this.mPendingSavedState.mLayoutState;
            if (mLayoutState != null) {
                this.mLayout.onRestoreInstanceState(mLayoutState);
            }
            this.mPendingSavedState = null;
        }
        final State mState = this.mState;
        mState.mInPreLayout = false;
        this.mLayout.onLayoutChildren(this.mRecycler, mState);
        final State mState2 = this.mState;
        mState2.mStructureChanged = false;
        mState2.mRunSimpleAnimations = (mState2.mRunSimpleAnimations && this.mItemAnimator != null);
        this.mState.mLayoutStep = 4;
        this.onExitLayoutOrScroll();
        this.stopInterceptRequestLayout(false);
    }
    
    private void dispatchLayoutStep3() {
        this.mState.assertLayoutStep(4);
        this.startInterceptRequestLayout();
        this.onEnterLayoutOrScroll();
        final State mState = this.mState;
        mState.mLayoutStep = 1;
        if (mState.mRunSimpleAnimations) {
            for (int i = this.mChildHelper.getChildCount() - 1; i >= 0; --i) {
                final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!childViewHolderInt.shouldIgnore()) {
                    final long changedHolderKey = this.getChangedHolderKey(childViewHolderInt);
                    final ItemHolderInfo recordPostLayoutInformation = this.mItemAnimator.recordPostLayoutInformation(this.mState, childViewHolderInt);
                    final ViewHolder fromOldChangeHolders = this.mViewInfoStore.getFromOldChangeHolders(changedHolderKey);
                    if (fromOldChangeHolders != null && !fromOldChangeHolders.shouldIgnore()) {
                        final boolean disappearing = this.mViewInfoStore.isDisappearing(fromOldChangeHolders);
                        final boolean disappearing2 = this.mViewInfoStore.isDisappearing(childViewHolderInt);
                        if (disappearing && fromOldChangeHolders == childViewHolderInt) {
                            this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                        }
                        else {
                            final ItemHolderInfo popFromPreLayout = this.mViewInfoStore.popFromPreLayout(fromOldChangeHolders);
                            this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                            final ItemHolderInfo popFromPostLayout = this.mViewInfoStore.popFromPostLayout(childViewHolderInt);
                            if (popFromPreLayout == null) {
                                this.handleMissingPreInfoForChangeError(changedHolderKey, childViewHolderInt, fromOldChangeHolders);
                            }
                            else {
                                this.animateChange(fromOldChangeHolders, childViewHolderInt, popFromPreLayout, popFromPostLayout, disappearing, disappearing2);
                            }
                        }
                    }
                    else {
                        this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                    }
                }
            }
            this.mViewInfoStore.process(this.mViewInfoProcessCallback);
        }
        this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        final State mState2 = this.mState;
        mState2.mPreviousLayoutItemCount = mState2.mItemCount;
        this.mDataSetHasChangedAfterLayout = false;
        this.mDispatchItemsChangedEvent = false;
        mState2.mRunSimpleAnimations = false;
        mState2.mRunPredictiveAnimations = false;
        this.mLayout.mRequestedSimpleAnimations = false;
        final ArrayList<ViewHolder> mChangedScrap = this.mRecycler.mChangedScrap;
        if (mChangedScrap != null) {
            mChangedScrap.clear();
        }
        final LayoutManager mLayout = this.mLayout;
        if (mLayout.mPrefetchMaxObservedInInitialPrefetch) {
            mLayout.mPrefetchMaxCountObserved = 0;
            mLayout.mPrefetchMaxObservedInInitialPrefetch = false;
            this.mRecycler.updateViewCacheSize();
        }
        this.mLayout.onLayoutCompleted(this.mState);
        this.onExitLayoutOrScroll();
        this.stopInterceptRequestLayout(false);
        this.mViewInfoStore.clear();
        final int[] mMinMaxLayoutPositions = this.mMinMaxLayoutPositions;
        if (this.didChildRangeChange(mMinMaxLayoutPositions[0], mMinMaxLayoutPositions[1])) {
            this.dispatchOnScrolled(0, 0);
        }
        this.recoverFocusFromState();
        this.resetFocusInfo();
    }
    
    private boolean dispatchToOnItemTouchListeners(final MotionEvent motionEvent) {
        final OnItemTouchListener mInterceptingOnItemTouchListener = this.mInterceptingOnItemTouchListener;
        if (mInterceptingOnItemTouchListener == null) {
            return motionEvent.getAction() != 0 && this.findInterceptingOnItemTouchListener(motionEvent);
        }
        mInterceptingOnItemTouchListener.onTouchEvent(this, motionEvent);
        final int action = motionEvent.getAction();
        if (action == 3 || action == 1) {
            this.mInterceptingOnItemTouchListener = null;
        }
        return true;
    }
    
    private boolean findInterceptingOnItemTouchListener(final MotionEvent motionEvent) {
        final int action = motionEvent.getAction();
        for (int size = this.mOnItemTouchListeners.size(), i = 0; i < size; ++i) {
            final OnItemTouchListener mInterceptingOnItemTouchListener = this.mOnItemTouchListeners.get(i);
            if (mInterceptingOnItemTouchListener.onInterceptTouchEvent(this, motionEvent) && action != 3) {
                this.mInterceptingOnItemTouchListener = mInterceptingOnItemTouchListener;
                return true;
            }
        }
        return false;
    }
    
    private void findMinMaxChildLayoutPositions(final int[] array) {
        final int childCount = this.mChildHelper.getChildCount();
        if (childCount == 0) {
            array[1] = (array[0] = -1);
            return;
        }
        int n = Integer.MAX_VALUE;
        int n2 = Integer.MIN_VALUE;
        int n3;
        for (int i = 0; i < childCount; ++i, n2 = n3) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (childViewHolderInt.shouldIgnore()) {
                n3 = n2;
            }
            else {
                final int layoutPosition = childViewHolderInt.getLayoutPosition();
                int n4;
                if (layoutPosition < (n4 = n)) {
                    n4 = layoutPosition;
                }
                n = n4;
                if (layoutPosition > (n3 = n2)) {
                    n3 = layoutPosition;
                    n = n4;
                }
            }
        }
        array[0] = n;
        array[1] = n2;
    }
    
    static RecyclerView findNestedRecyclerView(final View view) {
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        if (view instanceof RecyclerView) {
            return (RecyclerView)view;
        }
        final ViewGroup viewGroup = (ViewGroup)view;
        for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
            final RecyclerView nestedRecyclerView = findNestedRecyclerView(viewGroup.getChildAt(i));
            if (nestedRecyclerView != null) {
                return nestedRecyclerView;
            }
        }
        return null;
    }
    
    private View findNextViewToFocus() {
        int mFocusedItemPosition = this.mState.mFocusedItemPosition;
        if (mFocusedItemPosition == -1) {
            mFocusedItemPosition = 0;
        }
        final int itemCount = this.mState.getItemCount();
        for (int i = mFocusedItemPosition; i < itemCount; ++i) {
            final ViewHolder viewHolderForAdapterPosition = this.findViewHolderForAdapterPosition(i);
            if (viewHolderForAdapterPosition == null) {
                break;
            }
            if (viewHolderForAdapterPosition.itemView.hasFocusable()) {
                return viewHolderForAdapterPosition.itemView;
            }
        }
        for (int j = Math.min(itemCount, mFocusedItemPosition) - 1; j >= 0; --j) {
            final ViewHolder viewHolderForAdapterPosition2 = this.findViewHolderForAdapterPosition(j);
            if (viewHolderForAdapterPosition2 == null) {
                return null;
            }
            if (viewHolderForAdapterPosition2.itemView.hasFocusable()) {
                return viewHolderForAdapterPosition2.itemView;
            }
        }
        return null;
    }
    
    static ViewHolder getChildViewHolderInt(final View view) {
        if (view == null) {
            return null;
        }
        return ((LayoutParams)view.getLayoutParams()).mViewHolder;
    }
    
    static void getDecoratedBoundsWithMarginsInt(final View view, final Rect rect) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final Rect mDecorInsets = layoutParams.mDecorInsets;
        rect.set(view.getLeft() - mDecorInsets.left - layoutParams.leftMargin, view.getTop() - mDecorInsets.top - layoutParams.topMargin, view.getRight() + mDecorInsets.right + layoutParams.rightMargin, view.getBottom() + mDecorInsets.bottom + layoutParams.bottomMargin);
    }
    
    private int getDeepestFocusedViewWithId(View focusedChild) {
        int n = focusedChild.getId();
        while (!focusedChild.isFocused() && focusedChild instanceof ViewGroup && focusedChild.hasFocus()) {
            final View view = focusedChild = ((ViewGroup)focusedChild).getFocusedChild();
            if (view.getId() != -1) {
                n = view.getId();
                focusedChild = view;
            }
        }
        return n;
    }
    
    private String getFullClassName(final Context context, final String s) {
        if (s.charAt(0) == '.') {
            final StringBuilder sb = new StringBuilder();
            sb.append(context.getPackageName());
            sb.append(s);
            return sb.toString();
        }
        if (s.contains(".")) {
            return s;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(RecyclerView.class.getPackage().getName());
        sb2.append('.');
        sb2.append(s);
        return sb2.toString();
    }
    
    private NestedScrollingChildHelper getScrollingChildHelper() {
        if (this.mScrollingChildHelper == null) {
            this.mScrollingChildHelper = new NestedScrollingChildHelper((View)this);
        }
        return this.mScrollingChildHelper;
    }
    
    private void handleMissingPreInfoForChangeError(final long n, final ViewHolder obj, final ViewHolder obj2) {
        for (int childCount = this.mChildHelper.getChildCount(), i = 0; i < childCount; ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (childViewHolderInt != obj) {
                if (this.getChangedHolderKey(childViewHolderInt) == n) {
                    final Adapter mAdapter = this.mAdapter;
                    if (mAdapter != null && mAdapter.hasStableIds()) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:");
                        sb.append(childViewHolderInt);
                        sb.append(" \n View Holder 2:");
                        sb.append(obj);
                        sb.append(this.exceptionLabel());
                        throw new IllegalStateException(sb.toString());
                    }
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:");
                    sb2.append(childViewHolderInt);
                    sb2.append(" \n View Holder 2:");
                    sb2.append(obj);
                    sb2.append(this.exceptionLabel());
                    throw new IllegalStateException(sb2.toString());
                }
            }
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("Problem while matching changed view holders with the newones. The pre-layout information for the change holder ");
        sb3.append(obj2);
        sb3.append(" cannot be found but it is necessary for ");
        sb3.append(obj);
        sb3.append(this.exceptionLabel());
        Log.e("RecyclerView", sb3.toString());
    }
    
    private boolean hasUpdatedView() {
        for (int childCount = this.mChildHelper.getChildCount(), i = 0; i < childCount; ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (childViewHolderInt != null) {
                if (!childViewHolderInt.shouldIgnore()) {
                    if (childViewHolderInt.isUpdated()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @SuppressLint({ "InlinedApi" })
    private void initAutofill() {
        if (ViewCompat.getImportantForAutofill((View)this) == 0) {
            ViewCompat.setImportantForAutofill((View)this, 8);
        }
    }
    
    private void initChildrenHelper() {
        this.mChildHelper = new ChildHelper((ChildHelper.Callback)new ChildHelper.Callback() {
            @Override
            public void addView(final View view, final int n) {
                RecyclerView.this.addView(view, n);
                RecyclerView.this.dispatchChildAttached(view);
            }
            
            @Override
            public void attachViewToParent(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
                final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
                if (childViewHolderInt != null) {
                    if (!childViewHolderInt.isTmpDetached() && !childViewHolderInt.shouldIgnore()) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Called attach on a child which is not detached: ");
                        sb.append(childViewHolderInt);
                        sb.append(RecyclerView.this.exceptionLabel());
                        throw new IllegalArgumentException(sb.toString());
                    }
                    childViewHolderInt.clearTmpDetachFlag();
                }
                RecyclerView.access$000(RecyclerView.this, view, n, viewGroup$LayoutParams);
            }
            
            @Override
            public void detachViewFromParent(final int n) {
                final View child = this.getChildAt(n);
                if (child != null) {
                    final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(child);
                    if (childViewHolderInt != null) {
                        if (childViewHolderInt.isTmpDetached() && !childViewHolderInt.shouldIgnore()) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("called detach on an already detached child ");
                            sb.append(childViewHolderInt);
                            sb.append(RecyclerView.this.exceptionLabel());
                            throw new IllegalArgumentException(sb.toString());
                        }
                        childViewHolderInt.addFlags(256);
                    }
                }
                RecyclerView.access$100(RecyclerView.this, n);
            }
            
            @Override
            public View getChildAt(final int n) {
                return RecyclerView.this.getChildAt(n);
            }
            
            @Override
            public int getChildCount() {
                return RecyclerView.this.getChildCount();
            }
            
            @Override
            public ViewHolder getChildViewHolder(final View view) {
                return RecyclerView.getChildViewHolderInt(view);
            }
            
            @Override
            public int indexOfChild(final View view) {
                return RecyclerView.this.indexOfChild(view);
            }
            
            @Override
            public void onEnteredHiddenState(final View view) {
                final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
                if (childViewHolderInt != null) {
                    childViewHolderInt.onEnteredHiddenState(RecyclerView.this);
                }
            }
            
            @Override
            public void onLeftHiddenState(final View view) {
                final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
                if (childViewHolderInt != null) {
                    childViewHolderInt.onLeftHiddenState(RecyclerView.this);
                }
            }
            
            @Override
            public void removeAllViews() {
                for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                    final View child = this.getChildAt(i);
                    RecyclerView.this.dispatchChildDetached(child);
                    child.clearAnimation();
                }
                RecyclerView.this.removeAllViews();
            }
            
            @Override
            public void removeViewAt(final int n) {
                final View child = RecyclerView.this.getChildAt(n);
                if (child != null) {
                    RecyclerView.this.dispatchChildDetached(child);
                    child.clearAnimation();
                }
                RecyclerView.this.removeViewAt(n);
            }
        });
    }
    
    private boolean isPreferredNextFocus(final View view, final View view2, final int i) {
        final boolean b = false;
        final boolean b2 = false;
        final boolean b3 = false;
        final boolean b4 = false;
        final boolean b5 = false;
        final boolean b6 = false;
        boolean b7 = b5;
        if (view2 != null) {
            if (view2 == this) {
                b7 = b5;
            }
            else {
                if (this.findContainingItemView(view2) == null) {
                    return false;
                }
                if (view == null) {
                    return true;
                }
                if (this.findContainingItemView(view) == null) {
                    return true;
                }
                this.mTempRect.set(0, 0, view.getWidth(), view.getHeight());
                this.mTempRect2.set(0, 0, view2.getWidth(), view2.getHeight());
                this.offsetDescendantRectToMyCoords(view, this.mTempRect);
                this.offsetDescendantRectToMyCoords(view2, this.mTempRect2);
                final int layoutDirection = this.mLayout.getLayoutDirection();
                int n = -1;
                int n2;
                if (layoutDirection == 1) {
                    n2 = -1;
                }
                else {
                    n2 = 1;
                }
                final Rect mTempRect = this.mTempRect;
                final int left = mTempRect.left;
                final int left2 = this.mTempRect2.left;
                int n3;
                if ((left < left2 || mTempRect.right <= left2) && this.mTempRect.right < this.mTempRect2.right) {
                    n3 = 1;
                }
                else {
                    final Rect mTempRect2 = this.mTempRect;
                    final int right = mTempRect2.right;
                    final int right2 = this.mTempRect2.right;
                    if ((right > right2 || mTempRect2.left >= right2) && this.mTempRect.left > this.mTempRect2.left) {
                        n3 = -1;
                    }
                    else {
                        n3 = 0;
                    }
                }
                final Rect mTempRect3 = this.mTempRect;
                final int top = mTempRect3.top;
                final int top2 = this.mTempRect2.top;
                if ((top < top2 || mTempRect3.bottom <= top2) && this.mTempRect.bottom < this.mTempRect2.bottom) {
                    n = 1;
                }
                else {
                    final Rect mTempRect4 = this.mTempRect;
                    final int bottom = mTempRect4.bottom;
                    final int bottom2 = this.mTempRect2.bottom;
                    if ((bottom <= bottom2 && mTempRect4.top < bottom2) || this.mTempRect.top <= this.mTempRect2.top) {
                        n = 0;
                    }
                }
                if (i != 1) {
                    if (i == 2) {
                        if (n <= 0) {
                            boolean b8 = b4;
                            if (n != 0) {
                                return b8;
                            }
                            b8 = b4;
                            if (n3 * n2 < 0) {
                                return b8;
                            }
                        }
                        return true;
                    }
                    if (i == 17) {
                        boolean b9 = b3;
                        if (n3 < 0) {
                            b9 = true;
                        }
                        return b9;
                    }
                    if (i == 33) {
                        boolean b10 = b2;
                        if (n < 0) {
                            b10 = true;
                        }
                        return b10;
                    }
                    if (i == 66) {
                        boolean b11 = b;
                        if (n3 > 0) {
                            b11 = true;
                        }
                        return b11;
                    }
                    if (i == 130) {
                        boolean b12 = b6;
                        if (n > 0) {
                            b12 = true;
                        }
                        return b12;
                    }
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Invalid direction: ");
                    sb.append(i);
                    sb.append(this.exceptionLabel());
                    throw new IllegalArgumentException(sb.toString());
                }
                else {
                    if (n >= 0) {
                        b7 = b5;
                        if (n != 0) {
                            return b7;
                        }
                        b7 = b5;
                        if (n3 * n2 > 0) {
                            return b7;
                        }
                    }
                    b7 = true;
                }
            }
        }
        return b7;
    }
    
    private void onPointerUp(final MotionEvent motionEvent) {
        final int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mScrollPointerId) {
            int n;
            if (actionIndex == 0) {
                n = 1;
            }
            else {
                n = 0;
            }
            this.mScrollPointerId = motionEvent.getPointerId(n);
            final int n2 = (int)(motionEvent.getX(n) + 0.5f);
            this.mLastTouchX = n2;
            this.mInitialTouchX = n2;
            final int n3 = (int)(motionEvent.getY(n) + 0.5f);
            this.mLastTouchY = n3;
            this.mInitialTouchY = n3;
        }
    }
    
    private boolean predictiveItemAnimationsEnabled() {
        return this.mItemAnimator != null && this.mLayout.supportsPredictiveItemAnimations();
    }
    
    private void processAdapterUpdatesAndSetAnimationFlags() {
        if (this.mDataSetHasChangedAfterLayout) {
            this.mAdapterHelper.reset();
            if (this.mDispatchItemsChangedEvent) {
                this.mLayout.onItemsChanged(this);
            }
        }
        if (this.predictiveItemAnimationsEnabled()) {
            this.mAdapterHelper.preProcess();
        }
        else {
            this.mAdapterHelper.consumeUpdatesInOnePass();
        }
        final boolean mItemsAddedOrRemoved = this.mItemsAddedOrRemoved;
        final boolean b = false;
        final boolean b2 = mItemsAddedOrRemoved || this.mItemsChanged;
        this.mState.mRunSimpleAnimations = (this.mFirstLayoutComplete && this.mItemAnimator != null && (this.mDataSetHasChangedAfterLayout || b2 || this.mLayout.mRequestedSimpleAnimations) && (!this.mDataSetHasChangedAfterLayout || this.mAdapter.hasStableIds()));
        final State mState = this.mState;
        boolean mRunPredictiveAnimations = b;
        if (mState.mRunSimpleAnimations) {
            mRunPredictiveAnimations = b;
            if (b2) {
                mRunPredictiveAnimations = b;
                if (!this.mDataSetHasChangedAfterLayout) {
                    mRunPredictiveAnimations = b;
                    if (this.predictiveItemAnimationsEnabled()) {
                        mRunPredictiveAnimations = true;
                    }
                }
            }
        }
        mState.mRunPredictiveAnimations = mRunPredictiveAnimations;
    }
    
    private void pullGlows(final float n, final float n2, final float n3, final float n4) {
        final int n5 = 1;
        int n6 = 0;
        Label_0080: {
            if (n2 < 0.0f) {
                this.ensureLeftGlow();
                EdgeEffectCompat.onPull(this.mLeftGlow, -n2 / this.getWidth(), 1.0f - n3 / this.getHeight());
            }
            else {
                if (n2 <= 0.0f) {
                    n6 = 0;
                    break Label_0080;
                }
                this.ensureRightGlow();
                EdgeEffectCompat.onPull(this.mRightGlow, n2 / this.getWidth(), n3 / this.getHeight());
            }
            n6 = 1;
        }
        if (n4 < 0.0f) {
            this.ensureTopGlow();
            EdgeEffectCompat.onPull(this.mTopGlow, -n4 / this.getHeight(), n / this.getWidth());
            n6 = n5;
        }
        else if (n4 > 0.0f) {
            this.ensureBottomGlow();
            EdgeEffectCompat.onPull(this.mBottomGlow, n4 / this.getHeight(), 1.0f - n / this.getWidth());
            n6 = n5;
        }
        if (n6 != 0 || n2 != 0.0f || n4 != 0.0f) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    private void recoverFocusFromState() {
        if (this.mPreserveFocusAfterLayout && this.mAdapter != null && this.hasFocus() && this.getDescendantFocusability() != 393216) {
            if (this.getDescendantFocusability() != 131072 || !this.isFocused()) {
                if (!this.isFocused()) {
                    final View focusedChild = this.getFocusedChild();
                    if (RecyclerView.IGNORE_DETACHED_FOCUSED_CHILD && (focusedChild.getParent() == null || !focusedChild.hasFocus())) {
                        if (this.mChildHelper.getChildCount() == 0) {
                            this.requestFocus();
                            return;
                        }
                    }
                    else if (!this.mChildHelper.isHidden(focusedChild)) {
                        return;
                    }
                }
                final long mFocusedItemId = this.mState.mFocusedItemId;
                final View view = null;
                ViewHolder viewHolderForItemId;
                if (mFocusedItemId != -1L && this.mAdapter.hasStableIds()) {
                    viewHolderForItemId = this.findViewHolderForItemId(this.mState.mFocusedItemId);
                }
                else {
                    viewHolderForItemId = null;
                }
                View view2;
                if (viewHolderForItemId != null && !this.mChildHelper.isHidden(viewHolderForItemId.itemView) && viewHolderForItemId.itemView.hasFocusable()) {
                    view2 = viewHolderForItemId.itemView;
                }
                else {
                    view2 = view;
                    if (this.mChildHelper.getChildCount() > 0) {
                        view2 = this.findNextViewToFocus();
                    }
                }
                if (view2 != null) {
                    final int mFocusedSubChildId = this.mState.mFocusedSubChildId;
                    View view3 = view2;
                    if (mFocusedSubChildId != -1L) {
                        final View viewById = view2.findViewById(mFocusedSubChildId);
                        view3 = view2;
                        if (viewById != null) {
                            view3 = view2;
                            if (viewById.isFocusable()) {
                                view3 = viewById;
                            }
                        }
                    }
                    view3.requestFocus();
                }
            }
        }
    }
    
    private void releaseGlows() {
        final EdgeEffect mLeftGlow = this.mLeftGlow;
        int finished;
        if (mLeftGlow != null) {
            mLeftGlow.onRelease();
            finished = (this.mLeftGlow.isFinished() ? 1 : 0);
        }
        else {
            finished = 0;
        }
        final EdgeEffect mTopGlow = this.mTopGlow;
        int n = finished;
        if (mTopGlow != null) {
            mTopGlow.onRelease();
            n = (finished | (this.mTopGlow.isFinished() ? 1 : 0));
        }
        final EdgeEffect mRightGlow = this.mRightGlow;
        int n2 = n;
        if (mRightGlow != null) {
            mRightGlow.onRelease();
            n2 = (n | (this.mRightGlow.isFinished() ? 1 : 0));
        }
        final EdgeEffect mBottomGlow = this.mBottomGlow;
        int n3 = n2;
        if (mBottomGlow != null) {
            mBottomGlow.onRelease();
            n3 = (n2 | (this.mBottomGlow.isFinished() ? 1 : 0));
        }
        if (n3 != 0) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    private void requestChildOnScreen(final View view, final View view2) {
        View view3;
        if (view2 != null) {
            view3 = view2;
        }
        else {
            view3 = view;
        }
        this.mTempRect.set(0, 0, view3.getWidth(), view3.getHeight());
        final ViewGroup$LayoutParams layoutParams = view3.getLayoutParams();
        if (layoutParams instanceof LayoutParams) {
            final LayoutParams layoutParams2 = (LayoutParams)layoutParams;
            if (!layoutParams2.mInsetsDirty) {
                final Rect mDecorInsets = layoutParams2.mDecorInsets;
                final Rect mTempRect = this.mTempRect;
                mTempRect.left -= mDecorInsets.left;
                mTempRect.right += mDecorInsets.right;
                mTempRect.top -= mDecorInsets.top;
                mTempRect.bottom += mDecorInsets.bottom;
            }
        }
        if (view2 != null) {
            this.offsetDescendantRectToMyCoords(view2, this.mTempRect);
            this.offsetRectIntoDescendantCoords(view, this.mTempRect);
        }
        this.mLayout.requestChildRectangleOnScreen(this, view, this.mTempRect, this.mFirstLayoutComplete ^ true, view2 == null);
    }
    
    private void resetFocusInfo() {
        final State mState = this.mState;
        mState.mFocusedItemId = -1L;
        mState.mFocusedItemPosition = -1;
        mState.mFocusedSubChildId = -1;
    }
    
    private void resetScroll() {
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
        this.stopNestedScroll(0);
        this.releaseGlows();
    }
    
    private void saveFocusInfo() {
        final boolean mPreserveFocusAfterLayout = this.mPreserveFocusAfterLayout;
        final ViewHolder viewHolder = null;
        View focusedChild;
        if (mPreserveFocusAfterLayout && this.hasFocus() && this.mAdapter != null) {
            focusedChild = this.getFocusedChild();
        }
        else {
            focusedChild = null;
        }
        ViewHolder containingViewHolder;
        if (focusedChild == null) {
            containingViewHolder = viewHolder;
        }
        else {
            containingViewHolder = this.findContainingViewHolder(focusedChild);
        }
        if (containingViewHolder == null) {
            this.resetFocusInfo();
        }
        else {
            final State mState = this.mState;
            long itemId;
            if (this.mAdapter.hasStableIds()) {
                itemId = containingViewHolder.getItemId();
            }
            else {
                itemId = -1L;
            }
            mState.mFocusedItemId = itemId;
            final State mState2 = this.mState;
            int mFocusedItemPosition;
            if (this.mDataSetHasChangedAfterLayout) {
                mFocusedItemPosition = -1;
            }
            else if (containingViewHolder.isRemoved()) {
                mFocusedItemPosition = containingViewHolder.mOldPosition;
            }
            else {
                mFocusedItemPosition = containingViewHolder.getAbsoluteAdapterPosition();
            }
            mState2.mFocusedItemPosition = mFocusedItemPosition;
            this.mState.mFocusedSubChildId = this.getDeepestFocusedViewWithId(containingViewHolder.itemView);
        }
    }
    
    private void setAdapterInternal(final Adapter mAdapter, final boolean b, final boolean b2) {
        final Adapter mAdapter2 = this.mAdapter;
        if (mAdapter2 != null) {
            mAdapter2.unregisterAdapterDataObserver(this.mObserver);
            this.mAdapter.onDetachedFromRecyclerView(this);
        }
        if (!b || b2) {
            this.removeAndRecycleViews();
        }
        this.mAdapterHelper.reset();
        final Adapter mAdapter3 = this.mAdapter;
        if ((this.mAdapter = mAdapter) != null) {
            mAdapter.registerAdapterDataObserver(this.mObserver);
            mAdapter.onAttachedToRecyclerView(this);
        }
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            mLayout.onAdapterChanged(mAdapter3, this.mAdapter);
        }
        this.mRecycler.onAdapterChanged(mAdapter3, this.mAdapter, b);
        this.mState.mStructureChanged = true;
    }
    
    private void stopScrollersInternal() {
        this.mViewFlinger.stop();
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            mLayout.stopSmoothScroller();
        }
    }
    
    void absorbGlows(final int n, final int n2) {
        if (n < 0) {
            this.ensureLeftGlow();
            if (this.mLeftGlow.isFinished()) {
                this.mLeftGlow.onAbsorb(-n);
            }
        }
        else if (n > 0) {
            this.ensureRightGlow();
            if (this.mRightGlow.isFinished()) {
                this.mRightGlow.onAbsorb(n);
            }
        }
        if (n2 < 0) {
            this.ensureTopGlow();
            if (this.mTopGlow.isFinished()) {
                this.mTopGlow.onAbsorb(-n2);
            }
        }
        else if (n2 > 0) {
            this.ensureBottomGlow();
            if (this.mBottomGlow.isFinished()) {
                this.mBottomGlow.onAbsorb(n2);
            }
        }
        if (n != 0 || n2 != 0) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    public void addFocusables(final ArrayList<View> list, final int n, final int n2) {
        final LayoutManager mLayout = this.mLayout;
        if (mLayout == null || !mLayout.onAddFocusables(this, list, n, n2)) {
            super.addFocusables((ArrayList)list, n, n2);
        }
    }
    
    public void addItemDecoration(final ItemDecoration itemDecoration) {
        this.addItemDecoration(itemDecoration, -1);
    }
    
    public void addItemDecoration(final ItemDecoration itemDecoration, final int index) {
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            mLayout.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout");
        }
        if (this.mItemDecorations.isEmpty()) {
            this.setWillNotDraw(false);
        }
        if (index < 0) {
            this.mItemDecorations.add(itemDecoration);
        }
        else {
            this.mItemDecorations.add(index, itemDecoration);
        }
        this.markItemDecorInsetsDirty();
        this.requestLayout();
    }
    
    public void addOnChildAttachStateChangeListener(final OnChildAttachStateChangeListener onChildAttachStateChangeListener) {
        if (this.mOnChildAttachStateListeners == null) {
            this.mOnChildAttachStateListeners = new ArrayList<OnChildAttachStateChangeListener>();
        }
        this.mOnChildAttachStateListeners.add(onChildAttachStateChangeListener);
    }
    
    public void addOnItemTouchListener(final OnItemTouchListener e) {
        this.mOnItemTouchListeners.add(e);
    }
    
    public void addOnScrollListener(final OnScrollListener onScrollListener) {
        if (this.mScrollListeners == null) {
            this.mScrollListeners = new ArrayList<OnScrollListener>();
        }
        this.mScrollListeners.add(onScrollListener);
    }
    
    void animateAppearance(final ViewHolder viewHolder, final ItemHolderInfo itemHolderInfo, final ItemHolderInfo itemHolderInfo2) {
        viewHolder.setIsRecyclable(false);
        if (this.mItemAnimator.animateAppearance(viewHolder, itemHolderInfo, itemHolderInfo2)) {
            this.postAnimationRunner();
        }
    }
    
    void animateDisappearance(final ViewHolder viewHolder, final ItemHolderInfo itemHolderInfo, final ItemHolderInfo itemHolderInfo2) {
        this.addAnimatingView(viewHolder);
        viewHolder.setIsRecyclable(false);
        if (this.mItemAnimator.animateDisappearance(viewHolder, itemHolderInfo, itemHolderInfo2)) {
            this.postAnimationRunner();
        }
    }
    
    void assertNotInLayoutOrScroll(final String s) {
        if (!this.isComputingLayout()) {
            if (this.mDispatchScrollCounter > 0) {
                final StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(this.exceptionLabel());
                Log.w("RecyclerView", "Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data. Any method call that might change the structureof the RecyclerView or the adapter contents should be postponed tothe next frame.", (Throwable)new IllegalStateException(sb.toString()));
            }
            return;
        }
        if (s == null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Cannot call this method while RecyclerView is computing a layout or scrolling");
            sb2.append(this.exceptionLabel());
            throw new IllegalStateException(sb2.toString());
        }
        throw new IllegalStateException(s);
    }
    
    boolean canReuseUpdatedViewHolder(final ViewHolder viewHolder) {
        final ItemAnimator mItemAnimator = this.mItemAnimator;
        return mItemAnimator == null || mItemAnimator.canReuseUpdatedViewHolder(viewHolder, viewHolder.getUnmodifiedPayloads());
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams && this.mLayout.checkLayoutParams((LayoutParams)viewGroup$LayoutParams);
    }
    
    void clearOldPositions() {
        for (int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount(), i = 0; i < unfilteredChildCount; ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.clearOldPosition();
            }
        }
        this.mRecycler.clearOldPositions();
    }
    
    public int computeHorizontalScrollExtent() {
        final LayoutManager mLayout = this.mLayout;
        int computeHorizontalScrollExtent = 0;
        if (mLayout == null) {
            return 0;
        }
        if (mLayout.canScrollHorizontally()) {
            computeHorizontalScrollExtent = this.mLayout.computeHorizontalScrollExtent(this.mState);
        }
        return computeHorizontalScrollExtent;
    }
    
    public int computeHorizontalScrollOffset() {
        final LayoutManager mLayout = this.mLayout;
        int computeHorizontalScrollOffset = 0;
        if (mLayout == null) {
            return 0;
        }
        if (mLayout.canScrollHorizontally()) {
            computeHorizontalScrollOffset = this.mLayout.computeHorizontalScrollOffset(this.mState);
        }
        return computeHorizontalScrollOffset;
    }
    
    public int computeHorizontalScrollRange() {
        final LayoutManager mLayout = this.mLayout;
        int computeHorizontalScrollRange = 0;
        if (mLayout == null) {
            return 0;
        }
        if (mLayout.canScrollHorizontally()) {
            computeHorizontalScrollRange = this.mLayout.computeHorizontalScrollRange(this.mState);
        }
        return computeHorizontalScrollRange;
    }
    
    public int computeVerticalScrollExtent() {
        final LayoutManager mLayout = this.mLayout;
        int computeVerticalScrollExtent = 0;
        if (mLayout == null) {
            return 0;
        }
        if (mLayout.canScrollVertically()) {
            computeVerticalScrollExtent = this.mLayout.computeVerticalScrollExtent(this.mState);
        }
        return computeVerticalScrollExtent;
    }
    
    public int computeVerticalScrollOffset() {
        final LayoutManager mLayout = this.mLayout;
        int computeVerticalScrollOffset = 0;
        if (mLayout == null) {
            return 0;
        }
        if (mLayout.canScrollVertically()) {
            computeVerticalScrollOffset = this.mLayout.computeVerticalScrollOffset(this.mState);
        }
        return computeVerticalScrollOffset;
    }
    
    public int computeVerticalScrollRange() {
        final LayoutManager mLayout = this.mLayout;
        int computeVerticalScrollRange = 0;
        if (mLayout == null) {
            return 0;
        }
        if (mLayout.canScrollVertically()) {
            computeVerticalScrollRange = this.mLayout.computeVerticalScrollRange(this.mState);
        }
        return computeVerticalScrollRange;
    }
    
    void considerReleasingGlowsOnScroll(final int n, final int n2) {
        final EdgeEffect mLeftGlow = this.mLeftGlow;
        int finished;
        if (mLeftGlow != null && !mLeftGlow.isFinished() && n > 0) {
            this.mLeftGlow.onRelease();
            finished = (this.mLeftGlow.isFinished() ? 1 : 0);
        }
        else {
            finished = 0;
        }
        final EdgeEffect mRightGlow = this.mRightGlow;
        int n3 = finished;
        if (mRightGlow != null) {
            n3 = finished;
            if (!mRightGlow.isFinished()) {
                n3 = finished;
                if (n < 0) {
                    this.mRightGlow.onRelease();
                    n3 = (finished | (this.mRightGlow.isFinished() ? 1 : 0));
                }
            }
        }
        final EdgeEffect mTopGlow = this.mTopGlow;
        int n4 = n3;
        if (mTopGlow != null) {
            n4 = n3;
            if (!mTopGlow.isFinished()) {
                n4 = n3;
                if (n2 > 0) {
                    this.mTopGlow.onRelease();
                    n4 = (n3 | (this.mTopGlow.isFinished() ? 1 : 0));
                }
            }
        }
        final EdgeEffect mBottomGlow = this.mBottomGlow;
        int n5 = n4;
        if (mBottomGlow != null) {
            n5 = n4;
            if (!mBottomGlow.isFinished()) {
                n5 = n4;
                if (n2 < 0) {
                    this.mBottomGlow.onRelease();
                    n5 = (n4 | (this.mBottomGlow.isFinished() ? 1 : 0));
                }
            }
        }
        if (n5 != 0) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    void consumePendingUpdateOperations() {
        if (!this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout) {
            TraceCompat.beginSection("RV FullInvalidate");
            this.dispatchLayout();
            TraceCompat.endSection();
            return;
        }
        if (!this.mAdapterHelper.hasPendingUpdates()) {
            return;
        }
        if (this.mAdapterHelper.hasAnyUpdateTypes(4) && !this.mAdapterHelper.hasAnyUpdateTypes(11)) {
            TraceCompat.beginSection("RV PartialInvalidate");
            this.startInterceptRequestLayout();
            this.onEnterLayoutOrScroll();
            this.mAdapterHelper.preProcess();
            if (!this.mLayoutWasDefered) {
                if (this.hasUpdatedView()) {
                    this.dispatchLayout();
                }
                else {
                    this.mAdapterHelper.consumePostponedUpdates();
                }
            }
            this.stopInterceptRequestLayout(true);
            this.onExitLayoutOrScroll();
            TraceCompat.endSection();
        }
        else if (this.mAdapterHelper.hasPendingUpdates()) {
            TraceCompat.beginSection("RV FullInvalidate");
            this.dispatchLayout();
            TraceCompat.endSection();
        }
    }
    
    void defaultOnMeasure(final int n, final int n2) {
        this.setMeasuredDimension(LayoutManager.chooseSize(n, this.getPaddingLeft() + this.getPaddingRight(), ViewCompat.getMinimumWidth((View)this)), LayoutManager.chooseSize(n2, this.getPaddingTop() + this.getPaddingBottom(), ViewCompat.getMinimumHeight((View)this)));
    }
    
    void dispatchChildAttached(final View view) {
        final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        this.onChildAttachedToWindow(view);
        final Adapter mAdapter = this.mAdapter;
        if (mAdapter != null && childViewHolderInt != null) {
            mAdapter.onViewAttachedToWindow(childViewHolderInt);
        }
        final List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners = this.mOnChildAttachStateListeners;
        if (mOnChildAttachStateListeners != null) {
            for (int i = mOnChildAttachStateListeners.size() - 1; i >= 0; --i) {
                this.mOnChildAttachStateListeners.get(i).onChildViewAttachedToWindow(view);
            }
        }
    }
    
    void dispatchChildDetached(final View view) {
        final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        this.onChildDetachedFromWindow(view);
        final Adapter mAdapter = this.mAdapter;
        if (mAdapter != null && childViewHolderInt != null) {
            mAdapter.onViewDetachedFromWindow(childViewHolderInt);
        }
        final List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners = this.mOnChildAttachStateListeners;
        if (mOnChildAttachStateListeners != null) {
            for (int i = mOnChildAttachStateListeners.size() - 1; i >= 0; --i) {
                this.mOnChildAttachStateListeners.get(i).onChildViewDetachedFromWindow(view);
            }
        }
    }
    
    void dispatchLayout() {
        if (this.mAdapter == null) {
            Log.e("RecyclerView", "No adapter attached; skipping layout");
            return;
        }
        if (this.mLayout == null) {
            Log.e("RecyclerView", "No layout manager attached; skipping layout");
            return;
        }
        final State mState = this.mState;
        mState.mIsMeasuring = false;
        if (mState.mLayoutStep == 1) {
            this.dispatchLayoutStep1();
            this.mLayout.setExactMeasureSpecsFrom(this);
            this.dispatchLayoutStep2();
        }
        else if (!this.mAdapterHelper.hasUpdates() && this.mLayout.getWidth() == this.getWidth() && this.mLayout.getHeight() == this.getHeight()) {
            this.mLayout.setExactMeasureSpecsFrom(this);
        }
        else {
            this.mLayout.setExactMeasureSpecsFrom(this);
            this.dispatchLayoutStep2();
        }
        this.dispatchLayoutStep3();
    }
    
    public boolean dispatchNestedFling(final float n, final float n2, final boolean b) {
        return this.getScrollingChildHelper().dispatchNestedFling(n, n2, b);
    }
    
    public boolean dispatchNestedPreFling(final float n, final float n2) {
        return this.getScrollingChildHelper().dispatchNestedPreFling(n, n2);
    }
    
    public boolean dispatchNestedPreScroll(final int n, final int n2, final int[] array, final int[] array2) {
        return this.getScrollingChildHelper().dispatchNestedPreScroll(n, n2, array, array2);
    }
    
    public boolean dispatchNestedPreScroll(final int n, final int n2, final int[] array, final int[] array2, final int n3) {
        return this.getScrollingChildHelper().dispatchNestedPreScroll(n, n2, array, array2, n3);
    }
    
    public final void dispatchNestedScroll(final int n, final int n2, final int n3, final int n4, final int[] array, final int n5, final int[] array2) {
        this.getScrollingChildHelper().dispatchNestedScroll(n, n2, n3, n4, array, n5, array2);
    }
    
    public boolean dispatchNestedScroll(final int n, final int n2, final int n3, final int n4, final int[] array) {
        return this.getScrollingChildHelper().dispatchNestedScroll(n, n2, n3, n4, array);
    }
    
    void dispatchOnScrollStateChanged(final int n) {
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            mLayout.onScrollStateChanged(n);
        }
        this.onScrollStateChanged(n);
        final OnScrollListener mScrollListener = this.mScrollListener;
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(this, n);
        }
        final List<OnScrollListener> mScrollListeners = this.mScrollListeners;
        if (mScrollListeners != null) {
            for (int i = mScrollListeners.size() - 1; i >= 0; --i) {
                this.mScrollListeners.get(i).onScrollStateChanged(this, n);
            }
        }
    }
    
    void dispatchOnScrolled(final int n, final int n2) {
        ++this.mDispatchScrollCounter;
        final int scrollX = this.getScrollX();
        final int scrollY = this.getScrollY();
        this.onScrollChanged(scrollX, scrollY, scrollX - n, scrollY - n2);
        this.onScrolled(n, n2);
        final OnScrollListener mScrollListener = this.mScrollListener;
        if (mScrollListener != null) {
            mScrollListener.onScrolled(this, n, n2);
        }
        final List<OnScrollListener> mScrollListeners = this.mScrollListeners;
        if (mScrollListeners != null) {
            for (int i = mScrollListeners.size() - 1; i >= 0; --i) {
                this.mScrollListeners.get(i).onScrolled(this, n, n2);
            }
        }
        --this.mDispatchScrollCounter;
    }
    
    void dispatchPendingImportantForAccessibilityChanges() {
        for (int i = this.mPendingAccessibilityImportanceChange.size() - 1; i >= 0; --i) {
            final ViewHolder viewHolder = this.mPendingAccessibilityImportanceChange.get(i);
            if (viewHolder.itemView.getParent() == this) {
                if (!viewHolder.shouldIgnore()) {
                    final int mPendingAccessibilityState = viewHolder.mPendingAccessibilityState;
                    if (mPendingAccessibilityState != -1) {
                        ViewCompat.setImportantForAccessibility(viewHolder.itemView, mPendingAccessibilityState);
                        viewHolder.mPendingAccessibilityState = -1;
                    }
                }
            }
        }
        this.mPendingAccessibilityImportanceChange.clear();
    }
    
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        this.onPopulateAccessibilityEvent(accessibilityEvent);
        return true;
    }
    
    protected void dispatchRestoreInstanceState(final SparseArray<Parcelable> sparseArray) {
        this.dispatchThawSelfOnly((SparseArray)sparseArray);
    }
    
    protected void dispatchSaveInstanceState(final SparseArray<Parcelable> sparseArray) {
        this.dispatchFreezeSelfOnly((SparseArray)sparseArray);
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        final int size = this.mItemDecorations.size();
        final int n = false ? 1 : 0;
        for (int i = 0; i < size; ++i) {
            this.mItemDecorations.get(i).onDrawOver(canvas, this, this.mState);
        }
        final EdgeEffect mLeftGlow = this.mLeftGlow;
        final int n2 = 1;
        int n3;
        if (mLeftGlow != null && !mLeftGlow.isFinished()) {
            final int save = canvas.save();
            int paddingBottom;
            if (this.mClipToPadding) {
                paddingBottom = this.getPaddingBottom();
            }
            else {
                paddingBottom = 0;
            }
            canvas.rotate(270.0f);
            canvas.translate((float)(-this.getHeight() + paddingBottom), 0.0f);
            final EdgeEffect mLeftGlow2 = this.mLeftGlow;
            n3 = ((mLeftGlow2 != null && mLeftGlow2.draw(canvas)) ? 1 : 0);
            canvas.restoreToCount(save);
        }
        else {
            n3 = 0;
        }
        final EdgeEffect mTopGlow = this.mTopGlow;
        boolean b = n3 != 0;
        if (mTopGlow != null) {
            b = (n3 != 0);
            if (!mTopGlow.isFinished()) {
                final int save2 = canvas.save();
                if (this.mClipToPadding) {
                    canvas.translate((float)this.getPaddingLeft(), (float)this.getPaddingTop());
                }
                final EdgeEffect mTopGlow2 = this.mTopGlow;
                b = ((n3 | ((mTopGlow2 != null && mTopGlow2.draw(canvas)) ? 1 : 0)) != 0x0);
                canvas.restoreToCount(save2);
            }
        }
        final EdgeEffect mRightGlow = this.mRightGlow;
        boolean b2 = b;
        if (mRightGlow != null) {
            b2 = b;
            if (!mRightGlow.isFinished()) {
                final int save3 = canvas.save();
                final int width = this.getWidth();
                int paddingTop;
                if (this.mClipToPadding) {
                    paddingTop = this.getPaddingTop();
                }
                else {
                    paddingTop = 0;
                }
                canvas.rotate(90.0f);
                canvas.translate((float)(-paddingTop), (float)(-width));
                final EdgeEffect mRightGlow2 = this.mRightGlow;
                b2 = (b | (mRightGlow2 != null && mRightGlow2.draw(canvas)));
                canvas.restoreToCount(save3);
            }
        }
        final EdgeEffect mBottomGlow = this.mBottomGlow;
        int n4 = b2 ? 1 : 0;
        if (mBottomGlow != null) {
            n4 = (b2 ? 1 : 0);
            if (!mBottomGlow.isFinished()) {
                final int save4 = canvas.save();
                canvas.rotate(180.0f);
                if (this.mClipToPadding) {
                    canvas.translate((float)(-this.getWidth() + this.getPaddingRight()), (float)(-this.getHeight() + this.getPaddingBottom()));
                }
                else {
                    canvas.translate((float)(-this.getWidth()), (float)(-this.getHeight()));
                }
                final EdgeEffect mBottomGlow2 = this.mBottomGlow;
                int n5 = n;
                if (mBottomGlow2 != null) {
                    n5 = n;
                    if (mBottomGlow2.draw(canvas)) {
                        n5 = 1;
                    }
                }
                n4 = ((b2 ? 1 : 0) | n5);
                canvas.restoreToCount(save4);
            }
        }
        if (n4 == 0 && this.mItemAnimator != null && this.mItemDecorations.size() > 0 && this.mItemAnimator.isRunning()) {
            n4 = n2;
        }
        if (n4 != 0) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    public boolean drawChild(final Canvas canvas, final View view, final long n) {
        return super.drawChild(canvas, view, n);
    }
    
    void ensureBottomGlow() {
        if (this.mBottomGlow != null) {
            return;
        }
        final EdgeEffect edgeEffect = this.mEdgeEffectFactory.createEdgeEffect(this, 3);
        this.mBottomGlow = edgeEffect;
        if (this.mClipToPadding) {
            edgeEffect.setSize(this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight(), this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom());
        }
        else {
            edgeEffect.setSize(this.getMeasuredWidth(), this.getMeasuredHeight());
        }
    }
    
    void ensureLeftGlow() {
        if (this.mLeftGlow != null) {
            return;
        }
        final EdgeEffect edgeEffect = this.mEdgeEffectFactory.createEdgeEffect(this, 0);
        this.mLeftGlow = edgeEffect;
        if (this.mClipToPadding) {
            edgeEffect.setSize(this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom(), this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight());
        }
        else {
            edgeEffect.setSize(this.getMeasuredHeight(), this.getMeasuredWidth());
        }
    }
    
    void ensureRightGlow() {
        if (this.mRightGlow != null) {
            return;
        }
        final EdgeEffect edgeEffect = this.mEdgeEffectFactory.createEdgeEffect(this, 2);
        this.mRightGlow = edgeEffect;
        if (this.mClipToPadding) {
            edgeEffect.setSize(this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom(), this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight());
        }
        else {
            edgeEffect.setSize(this.getMeasuredHeight(), this.getMeasuredWidth());
        }
    }
    
    void ensureTopGlow() {
        if (this.mTopGlow != null) {
            return;
        }
        final EdgeEffect edgeEffect = this.mEdgeEffectFactory.createEdgeEffect(this, 1);
        this.mTopGlow = edgeEffect;
        if (this.mClipToPadding) {
            edgeEffect.setSize(this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight(), this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom());
        }
        else {
            edgeEffect.setSize(this.getMeasuredWidth(), this.getMeasuredHeight());
        }
    }
    
    String exceptionLabel() {
        final StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(super.toString());
        sb.append(", adapter:");
        sb.append(this.mAdapter);
        sb.append(", layout:");
        sb.append(this.mLayout);
        sb.append(", context:");
        sb.append(this.getContext());
        return sb.toString();
    }
    
    final void fillRemainingScrollValues(final State state) {
        if (this.getScrollState() == 2) {
            final OverScroller mOverScroller = this.mViewFlinger.mOverScroller;
            state.mRemainingScrollHorizontal = mOverScroller.getFinalX() - mOverScroller.getCurrX();
            state.mRemainingScrollVertical = mOverScroller.getFinalY() - mOverScroller.getCurrY();
        }
        else {
            state.mRemainingScrollHorizontal = 0;
            state.mRemainingScrollVertical = 0;
        }
    }
    
    public View findChildViewUnder(final float n, final float n2) {
        for (int i = this.mChildHelper.getChildCount() - 1; i >= 0; --i) {
            final View child = this.mChildHelper.getChildAt(i);
            final float translationX = child.getTranslationX();
            final float translationY = child.getTranslationY();
            if (n >= child.getLeft() + translationX && n <= child.getRight() + translationX && n2 >= child.getTop() + translationY && n2 <= child.getBottom() + translationY) {
                return child;
            }
        }
        return null;
    }
    
    public View findContainingItemView(View view) {
        ViewParent viewParent;
        for (viewParent = view.getParent(); viewParent != null && viewParent != this && viewParent instanceof View; viewParent = view.getParent()) {
            view = (View)viewParent;
        }
        if (viewParent != this) {
            view = null;
        }
        return view;
    }
    
    public ViewHolder findContainingViewHolder(View containingItemView) {
        containingItemView = this.findContainingItemView(containingItemView);
        ViewHolder childViewHolder;
        if (containingItemView == null) {
            childViewHolder = null;
        }
        else {
            childViewHolder = this.getChildViewHolder(containingItemView);
        }
        return childViewHolder;
    }
    
    public ViewHolder findViewHolderForAdapterPosition(final int n) {
        final boolean mDataSetHasChangedAfterLayout = this.mDataSetHasChangedAfterLayout;
        ViewHolder viewHolder = null;
        if (mDataSetHasChangedAfterLayout) {
            return null;
        }
        ViewHolder viewHolder2;
        for (int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount(), i = 0; i < unfilteredChildCount; ++i, viewHolder = viewHolder2) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            viewHolder2 = viewHolder;
            if (childViewHolderInt != null) {
                viewHolder2 = viewHolder;
                if (!childViewHolderInt.isRemoved()) {
                    viewHolder2 = viewHolder;
                    if (this.getAdapterPositionInRecyclerView(childViewHolderInt) == n) {
                        if (!this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                            return childViewHolderInt;
                        }
                        viewHolder2 = childViewHolderInt;
                    }
                }
            }
        }
        return viewHolder;
    }
    
    public ViewHolder findViewHolderForItemId(final long n) {
        final Adapter mAdapter = this.mAdapter;
        final ViewHolder viewHolder = null;
        ViewHolder viewHolder2 = null;
        ViewHolder viewHolder3 = viewHolder;
        if (mAdapter != null) {
            if (!mAdapter.hasStableIds()) {
                viewHolder3 = viewHolder;
            }
            else {
                final int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
                int n2 = 0;
                while (true) {
                    viewHolder3 = viewHolder2;
                    if (n2 >= unfilteredChildCount) {
                        break;
                    }
                    final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(n2));
                    ViewHolder viewHolder4 = viewHolder2;
                    if (childViewHolderInt != null) {
                        viewHolder4 = viewHolder2;
                        if (!childViewHolderInt.isRemoved()) {
                            viewHolder4 = viewHolder2;
                            if (childViewHolderInt.getItemId() == n) {
                                if (!this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                                    return childViewHolderInt;
                                }
                                viewHolder4 = childViewHolderInt;
                            }
                        }
                    }
                    ++n2;
                    viewHolder2 = viewHolder4;
                }
            }
        }
        return viewHolder3;
    }
    
    ViewHolder findViewHolderForPosition(final int n, final boolean b) {
        final int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        ViewHolder viewHolder = null;
        ViewHolder viewHolder2;
        for (int i = 0; i < unfilteredChildCount; ++i, viewHolder = viewHolder2) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            viewHolder2 = viewHolder;
            if (childViewHolderInt != null) {
                viewHolder2 = viewHolder;
                if (!childViewHolderInt.isRemoved()) {
                    if (b) {
                        if (childViewHolderInt.mPosition != n) {
                            viewHolder2 = viewHolder;
                            continue;
                        }
                    }
                    else if (childViewHolderInt.getLayoutPosition() != n) {
                        viewHolder2 = viewHolder;
                        continue;
                    }
                    if (!this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                        return childViewHolderInt;
                    }
                    viewHolder2 = childViewHolderInt;
                }
            }
        }
        return viewHolder;
    }
    
    public boolean fling(int n, int n2) {
        final LayoutManager mLayout = this.mLayout;
        final int n3 = 0;
        if (mLayout == null) {
            Log.e("RecyclerView", "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return false;
        }
        if (this.mLayoutSuppressed) {
            return false;
        }
        final boolean canScrollHorizontally = mLayout.canScrollHorizontally();
        final boolean canScrollVertically = this.mLayout.canScrollVertically();
        int a = 0;
        Label_0070: {
            if (canScrollHorizontally) {
                a = n;
                if (Math.abs(n) >= this.mMinFlingVelocity) {
                    break Label_0070;
                }
            }
            a = 0;
        }
        int a2 = 0;
        Label_0092: {
            if (canScrollVertically) {
                a2 = n2;
                if (Math.abs(n2) >= this.mMinFlingVelocity) {
                    break Label_0092;
                }
            }
            a2 = 0;
        }
        if (a == 0 && a2 == 0) {
            return false;
        }
        final float n4 = (float)a;
        final float n5 = (float)a2;
        if (!this.dispatchNestedPreFling(n4, n5)) {
            final boolean b = canScrollHorizontally || canScrollVertically;
            this.dispatchNestedFling(n4, n5, b);
            final OnFlingListener mOnFlingListener = this.mOnFlingListener;
            if (mOnFlingListener != null && mOnFlingListener.onFling(a, a2)) {
                return true;
            }
            if (b) {
                n = n3;
                if (canScrollHorizontally) {
                    n = 1;
                }
                n2 = n;
                if (canScrollVertically) {
                    n2 = (n | 0x2);
                }
                this.startNestedScroll(n2, 1);
                n = this.mMaxFlingVelocity;
                n = Math.max(-n, Math.min(a, n));
                n2 = this.mMaxFlingVelocity;
                n2 = Math.max(-n2, Math.min(a2, n2));
                this.mViewFlinger.fling(n, n2);
                return true;
            }
        }
        return false;
    }
    
    public View focusSearch(final View view, int n) {
        final View onInterceptFocusSearch = this.mLayout.onInterceptFocusSearch(view, n);
        if (onInterceptFocusSearch != null) {
            return onInterceptFocusSearch;
        }
        final Adapter mAdapter = this.mAdapter;
        final int n2 = 1;
        final boolean b = mAdapter != null && this.mLayout != null && !this.isComputingLayout() && !this.mLayoutSuppressed;
        final FocusFinder instance = FocusFinder.getInstance();
        View view2;
        if (b && (n == 2 || n == 1)) {
            int n4;
            if (this.mLayout.canScrollVertically()) {
                int n3;
                if (n == 2) {
                    n3 = 130;
                }
                else {
                    n3 = 33;
                }
                final boolean b2 = (n4 = ((instance.findNextFocus((ViewGroup)this, view, n3) == null) ? 1 : 0)) != 0;
                if (RecyclerView.FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
                    n = n3;
                    n4 = (b2 ? 1 : 0);
                }
            }
            else {
                n4 = 0;
            }
            int n5 = n4;
            int n6 = n;
            if (n4 == 0) {
                n5 = n4;
                n6 = n;
                if (this.mLayout.canScrollHorizontally()) {
                    int n7;
                    if (this.mLayout.getLayoutDirection() == 1 ^ n == 2) {
                        n7 = 66;
                    }
                    else {
                        n7 = 17;
                    }
                    int n8;
                    if (instance.findNextFocus((ViewGroup)this, view, n7) == null) {
                        n8 = n2;
                    }
                    else {
                        n8 = 0;
                    }
                    if (RecyclerView.FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
                        n = n7;
                    }
                    n5 = n8;
                    n6 = n;
                }
            }
            if (n5 != 0) {
                this.consumePendingUpdateOperations();
                if (this.findContainingItemView(view) == null) {
                    return null;
                }
                this.startInterceptRequestLayout();
                this.mLayout.onFocusSearchFailed(view, n6, this.mRecycler, this.mState);
                this.stopInterceptRequestLayout(false);
            }
            view2 = instance.findNextFocus((ViewGroup)this, view, n6);
            n = n6;
        }
        else {
            view2 = instance.findNextFocus((ViewGroup)this, view, n);
            if (view2 == null && b) {
                this.consumePendingUpdateOperations();
                if (this.findContainingItemView(view) == null) {
                    return null;
                }
                this.startInterceptRequestLayout();
                view2 = this.mLayout.onFocusSearchFailed(view, n, this.mRecycler, this.mState);
                this.stopInterceptRequestLayout(false);
            }
        }
        if (view2 == null || view2.hasFocusable()) {
            if (!this.isPreferredNextFocus(view, view2, n)) {
                view2 = super.focusSearch(view, n);
            }
            return view2;
        }
        if (this.getFocusedChild() == null) {
            return super.focusSearch(view, n);
        }
        this.requestChildOnScreen(view2, null);
        return view;
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            return (ViewGroup$LayoutParams)mLayout.generateDefaultLayoutParams();
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("RecyclerView has no LayoutManager");
        sb.append(this.exceptionLabel());
        throw new IllegalStateException(sb.toString());
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            return (ViewGroup$LayoutParams)mLayout.generateLayoutParams(this.getContext(), set);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("RecyclerView has no LayoutManager");
        sb.append(this.exceptionLabel());
        throw new IllegalStateException(sb.toString());
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            return (ViewGroup$LayoutParams)mLayout.generateLayoutParams(viewGroup$LayoutParams);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("RecyclerView has no LayoutManager");
        sb.append(this.exceptionLabel());
        throw new IllegalStateException(sb.toString());
    }
    
    public CharSequence getAccessibilityClassName() {
        return "androidx.recyclerview.widget.RecyclerView";
    }
    
    public Adapter getAdapter() {
        return this.mAdapter;
    }
    
    int getAdapterPositionInRecyclerView(final ViewHolder viewHolder) {
        if (!viewHolder.hasAnyOfTheFlags(524) && viewHolder.isBound()) {
            return this.mAdapterHelper.applyPendingUpdatesToPosition(viewHolder.mPosition);
        }
        return -1;
    }
    
    public int getBaseline() {
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            return mLayout.getBaseline();
        }
        return super.getBaseline();
    }
    
    long getChangedHolderKey(final ViewHolder viewHolder) {
        long itemId;
        if (this.mAdapter.hasStableIds()) {
            itemId = viewHolder.getItemId();
        }
        else {
            itemId = viewHolder.mPosition;
        }
        return itemId;
    }
    
    public int getChildAdapterPosition(final View view) {
        final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        int absoluteAdapterPosition;
        if (childViewHolderInt != null) {
            absoluteAdapterPosition = childViewHolderInt.getAbsoluteAdapterPosition();
        }
        else {
            absoluteAdapterPosition = -1;
        }
        return absoluteAdapterPosition;
    }
    
    protected int getChildDrawingOrder(final int n, final int n2) {
        final ChildDrawingOrderCallback mChildDrawingOrderCallback = this.mChildDrawingOrderCallback;
        if (mChildDrawingOrderCallback == null) {
            return super.getChildDrawingOrder(n, n2);
        }
        return mChildDrawingOrderCallback.onGetChildDrawingOrder(n, n2);
    }
    
    public int getChildLayoutPosition(final View view) {
        final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        int layoutPosition;
        if (childViewHolderInt != null) {
            layoutPosition = childViewHolderInt.getLayoutPosition();
        }
        else {
            layoutPosition = -1;
        }
        return layoutPosition;
    }
    
    public ViewHolder getChildViewHolder(final View obj) {
        final ViewParent parent = obj.getParent();
        if (parent != null && parent != this) {
            final StringBuilder sb = new StringBuilder();
            sb.append("View ");
            sb.append(obj);
            sb.append(" is not a direct child of ");
            sb.append(this);
            throw new IllegalArgumentException(sb.toString());
        }
        return getChildViewHolderInt(obj);
    }
    
    public boolean getClipToPadding() {
        return this.mClipToPadding;
    }
    
    public ItemAnimator getItemAnimator() {
        return this.mItemAnimator;
    }
    
    Rect getItemDecorInsetsForChild(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (!layoutParams.mInsetsDirty) {
            return layoutParams.mDecorInsets;
        }
        if (this.mState.isPreLayout() && (layoutParams.isItemChanged() || layoutParams.isViewInvalid())) {
            return layoutParams.mDecorInsets;
        }
        final Rect mDecorInsets = layoutParams.mDecorInsets;
        mDecorInsets.set(0, 0, 0, 0);
        for (int size = this.mItemDecorations.size(), i = 0; i < size; ++i) {
            this.mTempRect.set(0, 0, 0, 0);
            this.mItemDecorations.get(i).getItemOffsets(this.mTempRect, view, this, this.mState);
            final int left = mDecorInsets.left;
            final Rect mTempRect = this.mTempRect;
            mDecorInsets.left = left + mTempRect.left;
            mDecorInsets.top += mTempRect.top;
            mDecorInsets.right += mTempRect.right;
            mDecorInsets.bottom += mTempRect.bottom;
        }
        layoutParams.mInsetsDirty = false;
        return mDecorInsets;
    }
    
    public LayoutManager getLayoutManager() {
        return this.mLayout;
    }
    
    public int getMinFlingVelocity() {
        return this.mMinFlingVelocity;
    }
    
    long getNanoTime() {
        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
            return System.nanoTime();
        }
        return 0L;
    }
    
    public OnFlingListener getOnFlingListener() {
        return this.mOnFlingListener;
    }
    
    public int getScrollState() {
        return this.mScrollState;
    }
    
    public boolean hasFixedSize() {
        return this.mHasFixedSize;
    }
    
    public boolean hasNestedScrollingParent() {
        return this.getScrollingChildHelper().hasNestedScrollingParent();
    }
    
    public boolean hasPendingAdapterUpdates() {
        return !this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout || this.mAdapterHelper.hasPendingUpdates();
    }
    
    void initAdapterManager() {
        this.mAdapterHelper = new AdapterHelper((AdapterHelper.Callback)new AdapterHelper.Callback() {
            void dispatchUpdate(final UpdateOp updateOp) {
                final int cmd = updateOp.cmd;
                if (cmd != 1) {
                    if (cmd != 2) {
                        if (cmd != 4) {
                            if (cmd == 8) {
                                final RecyclerView this$0 = RecyclerView.this;
                                this$0.mLayout.onItemsMoved(this$0, updateOp.positionStart, updateOp.itemCount, 1);
                            }
                        }
                        else {
                            final RecyclerView this$2 = RecyclerView.this;
                            this$2.mLayout.onItemsUpdated(this$2, updateOp.positionStart, updateOp.itemCount, updateOp.payload);
                        }
                    }
                    else {
                        final RecyclerView this$3 = RecyclerView.this;
                        this$3.mLayout.onItemsRemoved(this$3, updateOp.positionStart, updateOp.itemCount);
                    }
                }
                else {
                    final RecyclerView this$4 = RecyclerView.this;
                    this$4.mLayout.onItemsAdded(this$4, updateOp.positionStart, updateOp.itemCount);
                }
            }
            
            @Override
            public ViewHolder findViewHolder(final int n) {
                final ViewHolder viewHolderForPosition = RecyclerView.this.findViewHolderForPosition(n, true);
                if (viewHolderForPosition == null) {
                    return null;
                }
                if (RecyclerView.this.mChildHelper.isHidden(viewHolderForPosition.itemView)) {
                    return null;
                }
                return viewHolderForPosition;
            }
            
            @Override
            public void markViewHoldersUpdated(final int n, final int n2, final Object o) {
                RecyclerView.this.viewRangeUpdate(n, n2, o);
                RecyclerView.this.mItemsChanged = true;
            }
            
            @Override
            public void offsetPositionsForAdd(final int n, final int n2) {
                RecyclerView.this.offsetPositionRecordsForInsert(n, n2);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }
            
            @Override
            public void offsetPositionsForMove(final int n, final int n2) {
                RecyclerView.this.offsetPositionRecordsForMove(n, n2);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }
            
            @Override
            public void offsetPositionsForRemovingInvisible(final int n, final int n2) {
                RecyclerView.this.offsetPositionRecordsForRemove(n, n2, true);
                final RecyclerView this$0 = RecyclerView.this;
                this$0.mItemsAddedOrRemoved = true;
                final State mState = this$0.mState;
                mState.mDeletedInvisibleItemCountSincePreviousLayout += n2;
            }
            
            @Override
            public void offsetPositionsForRemovingLaidOutOrNewView(final int n, final int n2) {
                RecyclerView.this.offsetPositionRecordsForRemove(n, n2, false);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }
            
            @Override
            public void onDispatchFirstPass(final UpdateOp updateOp) {
                this.dispatchUpdate(updateOp);
            }
            
            @Override
            public void onDispatchSecondPass(final UpdateOp updateOp) {
                this.dispatchUpdate(updateOp);
            }
        });
    }
    
    void initFastScroller(final StateListDrawable stateListDrawable, final Drawable drawable, final StateListDrawable stateListDrawable2, final Drawable drawable2) {
        if (stateListDrawable != null && drawable != null && stateListDrawable2 != null && drawable2 != null) {
            final Resources resources = this.getContext().getResources();
            new FastScroller(this, stateListDrawable, drawable, stateListDrawable2, drawable2, resources.getDimensionPixelSize(R$dimen.fastscroll_default_thickness), resources.getDimensionPixelSize(R$dimen.fastscroll_minimum_range), resources.getDimensionPixelOffset(R$dimen.fastscroll_margin));
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Trying to set fast scroller without both required drawables.");
        sb.append(this.exceptionLabel());
        throw new IllegalArgumentException(sb.toString());
    }
    
    void invalidateGlows() {
        this.mBottomGlow = null;
        this.mTopGlow = null;
        this.mRightGlow = null;
        this.mLeftGlow = null;
    }
    
    public void invalidateItemDecorations() {
        if (this.mItemDecorations.size() == 0) {
            return;
        }
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            mLayout.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout");
        }
        this.markItemDecorInsetsDirty();
        this.requestLayout();
    }
    
    boolean isAccessibilityEnabled() {
        final AccessibilityManager mAccessibilityManager = this.mAccessibilityManager;
        return mAccessibilityManager != null && mAccessibilityManager.isEnabled();
    }
    
    public boolean isAttachedToWindow() {
        return this.mIsAttached;
    }
    
    public boolean isComputingLayout() {
        return this.mLayoutOrScrollCounter > 0;
    }
    
    public final boolean isLayoutSuppressed() {
        return this.mLayoutSuppressed;
    }
    
    public boolean isNestedScrollingEnabled() {
        return this.getScrollingChildHelper().isNestedScrollingEnabled();
    }
    
    void jumpToPositionForSmoothScroller(final int n) {
        if (this.mLayout == null) {
            return;
        }
        this.setScrollState(2);
        this.mLayout.scrollToPosition(n);
        this.awakenScrollBars();
    }
    
    void markItemDecorInsetsDirty() {
        for (int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount(), i = 0; i < unfilteredChildCount; ++i) {
            ((LayoutParams)this.mChildHelper.getUnfilteredChildAt(i).getLayoutParams()).mInsetsDirty = true;
        }
        this.mRecycler.markItemDecorInsetsDirty();
    }
    
    void markKnownViewsInvalid() {
        for (int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount(), i = 0; i < unfilteredChildCount; ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.addFlags(6);
            }
        }
        this.markItemDecorInsetsDirty();
        this.mRecycler.markKnownViewsInvalid();
    }
    
    public void offsetChildrenHorizontal(final int n) {
        for (int childCount = this.mChildHelper.getChildCount(), i = 0; i < childCount; ++i) {
            this.mChildHelper.getChildAt(i).offsetLeftAndRight(n);
        }
    }
    
    public void offsetChildrenVertical(final int n) {
        for (int childCount = this.mChildHelper.getChildCount(), i = 0; i < childCount; ++i) {
            this.mChildHelper.getChildAt(i).offsetTopAndBottom(n);
        }
    }
    
    void offsetPositionRecordsForInsert(final int n, final int n2) {
        for (int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount(), i = 0; i < unfilteredChildCount; ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore() && childViewHolderInt.mPosition >= n) {
                childViewHolderInt.offsetPosition(n2, false);
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForInsert(n, n2);
        this.requestLayout();
    }
    
    void offsetPositionRecordsForMove(final int n, final int n2) {
        final int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        int n3;
        int n4;
        int n5;
        if (n < n2) {
            n3 = -1;
            n4 = n;
            n5 = n2;
        }
        else {
            n5 = n;
            n4 = n2;
            n3 = 1;
        }
        for (int i = 0; i < unfilteredChildCount; ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null) {
                final int mPosition = childViewHolderInt.mPosition;
                if (mPosition >= n4) {
                    if (mPosition <= n5) {
                        if (mPosition == n) {
                            childViewHolderInt.offsetPosition(n2 - n, false);
                        }
                        else {
                            childViewHolderInt.offsetPosition(n3, false);
                        }
                        this.mState.mStructureChanged = true;
                    }
                }
            }
        }
        this.mRecycler.offsetPositionRecordsForMove(n, n2);
        this.requestLayout();
    }
    
    void offsetPositionRecordsForRemove(final int n, final int n2, final boolean b) {
        for (int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount(), i = 0; i < unfilteredChildCount; ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore()) {
                final int mPosition = childViewHolderInt.mPosition;
                if (mPosition >= n + n2) {
                    childViewHolderInt.offsetPosition(-n2, b);
                    this.mState.mStructureChanged = true;
                }
                else if (mPosition >= n) {
                    childViewHolderInt.flagRemovedAndOffsetPosition(n - 1, -n2, b);
                    this.mState.mStructureChanged = true;
                }
            }
        }
        this.mRecycler.offsetPositionRecordsForRemove(n, n2, b);
        this.requestLayout();
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLayoutOrScrollCounter = 0;
        boolean mFirstLayoutComplete = true;
        this.mIsAttached = true;
        if (!this.mFirstLayoutComplete || this.isLayoutRequested()) {
            mFirstLayoutComplete = false;
        }
        this.mFirstLayoutComplete = mFirstLayoutComplete;
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            mLayout.dispatchAttachedToWindow(this);
        }
        this.mPostedAnimatorRunner = false;
        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
            if ((this.mGapWorker = GapWorker.sGapWorker.get()) == null) {
                this.mGapWorker = new GapWorker();
                final Display display = ViewCompat.getDisplay((View)this);
                float n2;
                final float n = n2 = 60.0f;
                if (!this.isInEditMode()) {
                    n2 = n;
                    if (display != null) {
                        final float refreshRate = display.getRefreshRate();
                        n2 = n;
                        if (refreshRate >= 30.0f) {
                            n2 = refreshRate;
                        }
                    }
                }
                final GapWorker mGapWorker = this.mGapWorker;
                mGapWorker.mFrameIntervalNs = (long)(1.0E9f / n2);
                GapWorker.sGapWorker.set(mGapWorker);
            }
            this.mGapWorker.add(this);
        }
    }
    
    public void onChildAttachedToWindow(final View view) {
    }
    
    public void onChildDetachedFromWindow(final View view) {
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final ItemAnimator mItemAnimator = this.mItemAnimator;
        if (mItemAnimator != null) {
            mItemAnimator.endAnimations();
        }
        this.stopScroll();
        this.mIsAttached = false;
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
        }
        this.mPendingAccessibilityImportanceChange.clear();
        this.removeCallbacks(this.mItemAnimatorRunner);
        this.mViewInfoStore.onDetach();
        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
            final GapWorker mGapWorker = this.mGapWorker;
            if (mGapWorker != null) {
                mGapWorker.remove(this);
                this.mGapWorker = null;
            }
        }
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        for (int size = this.mItemDecorations.size(), i = 0; i < size; ++i) {
            this.mItemDecorations.get(i).onDraw(canvas, this, this.mState);
        }
    }
    
    void onEnterLayoutOrScroll() {
        ++this.mLayoutOrScrollCounter;
    }
    
    void onExitLayoutOrScroll() {
        this.onExitLayoutOrScroll(true);
    }
    
    void onExitLayoutOrScroll(final boolean b) {
        final int mLayoutOrScrollCounter = this.mLayoutOrScrollCounter - 1;
        this.mLayoutOrScrollCounter = mLayoutOrScrollCounter;
        if (mLayoutOrScrollCounter < 1) {
            this.mLayoutOrScrollCounter = 0;
            if (b) {
                this.dispatchContentChangedIfNecessary();
                this.dispatchPendingImportantForAccessibilityChanges();
            }
        }
    }
    
    public boolean onGenericMotionEvent(final MotionEvent motionEvent) {
        if (this.mLayout == null) {
            return false;
        }
        if (this.mLayoutSuppressed) {
            return false;
        }
        if (motionEvent.getAction() == 8) {
            float n = 0.0f;
            float n3 = 0.0f;
            Label_0145: {
                float n2 = 0.0f;
                Label_0081: {
                    if ((motionEvent.getSource() & 0x2) == 0x0) {
                        if ((motionEvent.getSource() & 0x400000) != 0x0) {
                            n = motionEvent.getAxisValue(26);
                            if (this.mLayout.canScrollVertically()) {
                                n2 = -n;
                                break Label_0081;
                            }
                            if (this.mLayout.canScrollHorizontally()) {
                                n3 = 0.0f;
                                break Label_0145;
                            }
                        }
                        n3 = (n = 0.0f);
                        break Label_0145;
                    }
                    if (this.mLayout.canScrollVertically()) {
                        n3 = -motionEvent.getAxisValue(9);
                    }
                    else {
                        n3 = 0.0f;
                    }
                    n2 = n3;
                    if (this.mLayout.canScrollHorizontally()) {
                        n = motionEvent.getAxisValue(10);
                        break Label_0145;
                    }
                }
                final float n4 = 0.0f;
                n3 = n2;
                n = n4;
            }
            if (n3 != 0.0f || n != 0.0f) {
                this.scrollByInternal((int)(n * this.mScaledHorizontalScrollFactor), (int)(n3 * this.mScaledVerticalScrollFactor), motionEvent);
            }
        }
        return false;
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final boolean mLayoutSuppressed = this.mLayoutSuppressed;
        boolean b = false;
        if (mLayoutSuppressed) {
            return false;
        }
        this.mInterceptingOnItemTouchListener = null;
        if (this.findInterceptingOnItemTouchListener(motionEvent)) {
            this.cancelScroll();
            return true;
        }
        final LayoutManager mLayout = this.mLayout;
        if (mLayout == null) {
            return false;
        }
        final int canScrollHorizontally = mLayout.canScrollHorizontally() ? 1 : 0;
        final boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        final int actionMasked = motionEvent.getActionMasked();
        final int actionIndex = motionEvent.getActionIndex();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked != 5) {
                            if (actionMasked == 6) {
                                this.onPointerUp(motionEvent);
                            }
                        }
                        else {
                            this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
                            final int n = (int)(motionEvent.getX(actionIndex) + 0.5f);
                            this.mLastTouchX = n;
                            this.mInitialTouchX = n;
                            final int n2 = (int)(motionEvent.getY(actionIndex) + 0.5f);
                            this.mLastTouchY = n2;
                            this.mInitialTouchY = n2;
                        }
                    }
                    else {
                        this.cancelScroll();
                    }
                }
                else {
                    final int pointerIndex = motionEvent.findPointerIndex(this.mScrollPointerId);
                    if (pointerIndex < 0) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Error processing scroll; pointer index for id ");
                        sb.append(this.mScrollPointerId);
                        sb.append(" not found. Did any MotionEvents get skipped?");
                        Log.e("RecyclerView", sb.toString());
                        return false;
                    }
                    final int mLastTouchX = (int)(motionEvent.getX(pointerIndex) + 0.5f);
                    final int mLastTouchY = (int)(motionEvent.getY(pointerIndex) + 0.5f);
                    if (this.mScrollState != 1) {
                        final int mInitialTouchX = this.mInitialTouchX;
                        final int mInitialTouchY = this.mInitialTouchY;
                        boolean b2;
                        if (canScrollHorizontally && Math.abs(mLastTouchX - mInitialTouchX) > this.mTouchSlop) {
                            this.mLastTouchX = mLastTouchX;
                            b2 = true;
                        }
                        else {
                            b2 = false;
                        }
                        boolean b3 = b2;
                        if (canScrollVertically) {
                            b3 = b2;
                            if (Math.abs(mLastTouchY - mInitialTouchY) > this.mTouchSlop) {
                                this.mLastTouchY = mLastTouchY;
                                b3 = true;
                            }
                        }
                        if (b3) {
                            this.setScrollState(1);
                        }
                    }
                }
            }
            else {
                this.mVelocityTracker.clear();
                this.stopNestedScroll(0);
            }
        }
        else {
            if (this.mIgnoreMotionEventTillDown) {
                this.mIgnoreMotionEventTillDown = false;
            }
            this.mScrollPointerId = motionEvent.getPointerId(0);
            final int n3 = (int)(motionEvent.getX() + 0.5f);
            this.mLastTouchX = n3;
            this.mInitialTouchX = n3;
            final int n4 = (int)(motionEvent.getY() + 0.5f);
            this.mLastTouchY = n4;
            this.mInitialTouchY = n4;
            if (this.mScrollState == 2) {
                this.getParent().requestDisallowInterceptTouchEvent(true);
                this.setScrollState(1);
                this.stopNestedScroll(1);
            }
            final int[] mNestedOffsets = this.mNestedOffsets;
            mNestedOffsets[mNestedOffsets[1] = 0] = 0;
            int n6;
            final int n5 = n6 = canScrollHorizontally;
            if (canScrollVertically) {
                n6 = (n5 | 0x2);
            }
            this.startNestedScroll(n6, 0);
        }
        if (this.mScrollState == 1) {
            b = true;
        }
        return b;
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        TraceCompat.beginSection("RV OnLayout");
        this.dispatchLayout();
        TraceCompat.endSection();
        this.mFirstLayoutComplete = true;
    }
    
    protected void onMeasure(final int n, final int n2) {
        final LayoutManager mLayout = this.mLayout;
        if (mLayout == null) {
            this.defaultOnMeasure(n, n2);
            return;
        }
        final boolean autoMeasureEnabled = mLayout.isAutoMeasureEnabled();
        final int n3 = 0;
        if (autoMeasureEnabled) {
            final int mode = View$MeasureSpec.getMode(n);
            final int mode2 = View$MeasureSpec.getMode(n2);
            this.mLayout.onMeasure(this.mRecycler, this.mState, n, n2);
            int n4 = n3;
            if (mode == 1073741824) {
                n4 = n3;
                if (mode2 == 1073741824) {
                    n4 = 1;
                }
            }
            if (n4 != 0 || this.mAdapter == null) {
                return;
            }
            if (this.mState.mLayoutStep == 1) {
                this.dispatchLayoutStep1();
            }
            this.mLayout.setMeasureSpecs(n, n2);
            this.mState.mIsMeasuring = true;
            this.dispatchLayoutStep2();
            this.mLayout.setMeasuredDimensionFromChildren(n, n2);
            if (this.mLayout.shouldMeasureTwice()) {
                this.mLayout.setMeasureSpecs(View$MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), 1073741824), View$MeasureSpec.makeMeasureSpec(this.getMeasuredHeight(), 1073741824));
                this.mState.mIsMeasuring = true;
                this.dispatchLayoutStep2();
                this.mLayout.setMeasuredDimensionFromChildren(n, n2);
            }
        }
        else {
            if (this.mHasFixedSize) {
                this.mLayout.onMeasure(this.mRecycler, this.mState, n, n2);
                return;
            }
            if (this.mAdapterUpdateDuringMeasure) {
                this.startInterceptRequestLayout();
                this.onEnterLayoutOrScroll();
                this.processAdapterUpdatesAndSetAnimationFlags();
                this.onExitLayoutOrScroll();
                final State mState = this.mState;
                if (mState.mRunPredictiveAnimations) {
                    mState.mInPreLayout = true;
                }
                else {
                    this.mAdapterHelper.consumeUpdatesInOnePass();
                    this.mState.mInPreLayout = false;
                }
                this.stopInterceptRequestLayout(this.mAdapterUpdateDuringMeasure = false);
            }
            else if (this.mState.mRunPredictiveAnimations) {
                this.setMeasuredDimension(this.getMeasuredWidth(), this.getMeasuredHeight());
                return;
            }
            final Adapter mAdapter = this.mAdapter;
            if (mAdapter != null) {
                this.mState.mItemCount = mAdapter.getItemCount();
            }
            else {
                this.mState.mItemCount = 0;
            }
            this.startInterceptRequestLayout();
            this.mLayout.onMeasure(this.mRecycler, this.mState, n, n2);
            this.stopInterceptRequestLayout(false);
            this.mState.mInPreLayout = false;
        }
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        return !this.isComputingLayout() && super.onRequestFocusInDescendants(n, rect);
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        final SavedState mPendingSavedState = (SavedState)parcelable;
        this.mPendingSavedState = mPendingSavedState;
        super.onRestoreInstanceState(mPendingSavedState.getSuperState());
        this.requestLayout();
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        final SavedState mPendingSavedState = this.mPendingSavedState;
        if (mPendingSavedState != null) {
            savedState.copyFrom(mPendingSavedState);
        }
        else {
            final LayoutManager mLayout = this.mLayout;
            if (mLayout != null) {
                savedState.mLayoutState = mLayout.onSaveInstanceState();
            }
            else {
                savedState.mLayoutState = null;
            }
        }
        return (Parcelable)savedState;
    }
    
    public void onScrollStateChanged(final int n) {
    }
    
    public void onScrolled(final int n, final int n2) {
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        if (n != n3 || n2 != n4) {
            this.invalidateGlows();
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final boolean mLayoutSuppressed = this.mLayoutSuppressed;
        final boolean b = false;
        if (mLayoutSuppressed || this.mIgnoreMotionEventTillDown) {
            return false;
        }
        if (this.dispatchToOnItemTouchListeners(motionEvent)) {
            this.cancelScroll();
            return true;
        }
        final LayoutManager mLayout = this.mLayout;
        if (mLayout == null) {
            return false;
        }
        final int canScrollHorizontally = mLayout.canScrollHorizontally() ? 1 : 0;
        final boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        final int actionMasked = motionEvent.getActionMasked();
        final int actionIndex = motionEvent.getActionIndex();
        if (actionMasked == 0) {
            final int[] mNestedOffsets = this.mNestedOffsets;
            mNestedOffsets[mNestedOffsets[1] = 0] = 0;
        }
        final MotionEvent obtain = MotionEvent.obtain(motionEvent);
        final int[] mNestedOffsets2 = this.mNestedOffsets;
        obtain.offsetLocation((float)mNestedOffsets2[0], (float)mNestedOffsets2[1]);
        int n = 0;
        Label_1037: {
            if (actionMasked != 0) {
                if (actionMasked != 1) {
                    if (actionMasked != 2) {
                        if (actionMasked != 3) {
                            if (actionMasked != 5) {
                                if (actionMasked != 6) {
                                    n = (b ? 1 : 0);
                                }
                                else {
                                    this.onPointerUp(motionEvent);
                                    n = (b ? 1 : 0);
                                }
                            }
                            else {
                                this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
                                final int n2 = (int)(motionEvent.getX(actionIndex) + 0.5f);
                                this.mLastTouchX = n2;
                                this.mInitialTouchX = n2;
                                final int n3 = (int)(motionEvent.getY(actionIndex) + 0.5f);
                                this.mLastTouchY = n3;
                                this.mInitialTouchY = n3;
                                n = (b ? 1 : 0);
                            }
                        }
                        else {
                            this.cancelScroll();
                            n = (b ? 1 : 0);
                        }
                    }
                    else {
                        final int pointerIndex = motionEvent.findPointerIndex(this.mScrollPointerId);
                        if (pointerIndex < 0) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Error processing scroll; pointer index for id ");
                            sb.append(this.mScrollPointerId);
                            sb.append(" not found. Did any MotionEvents get skipped?");
                            Log.e("RecyclerView", sb.toString());
                            return false;
                        }
                        final int n4 = (int)(motionEvent.getX(pointerIndex) + 0.5f);
                        final int n5 = (int)(motionEvent.getY(pointerIndex) + 0.5f);
                        final int n6 = this.mLastTouchX - n4;
                        final int n7 = this.mLastTouchY - n5;
                        int n8 = n6;
                        int n9 = n7;
                        if (this.mScrollState != 1) {
                            int n10 = n6;
                            int n11 = 0;
                            boolean b2 = false;
                            Label_0452: {
                                if (canScrollHorizontally != 0) {
                                    if (n6 > 0) {
                                        n11 = Math.max(0, n6 - this.mTouchSlop);
                                    }
                                    else {
                                        n11 = Math.min(0, n6 + this.mTouchSlop);
                                    }
                                    n10 = n11;
                                    if (n11 != 0) {
                                        b2 = true;
                                        break Label_0452;
                                    }
                                }
                                b2 = false;
                                n11 = n10;
                            }
                            int n12 = n7;
                            boolean b3 = b2;
                            if (canScrollVertically) {
                                int n13;
                                if (n7 > 0) {
                                    n13 = Math.max(0, n7 - this.mTouchSlop);
                                }
                                else {
                                    n13 = Math.min(0, n7 + this.mTouchSlop);
                                }
                                n12 = n13;
                                b3 = b2;
                                if (n13 != 0) {
                                    b3 = true;
                                    n12 = n13;
                                }
                            }
                            n8 = n11;
                            n9 = n12;
                            if (b3) {
                                this.setScrollState(1);
                                n9 = n12;
                                n8 = n11;
                            }
                        }
                        final int n14 = n8;
                        n = (b ? 1 : 0);
                        if (this.mScrollState == 1) {
                            final int[] mReusableIntPair = this.mReusableIntPair;
                            mReusableIntPair[1] = (mReusableIntPair[0] = 0);
                            int n15;
                            if (canScrollHorizontally != 0) {
                                n15 = n14;
                            }
                            else {
                                n15 = 0;
                            }
                            int n16;
                            if (canScrollVertically) {
                                n16 = n9;
                            }
                            else {
                                n16 = 0;
                            }
                            int n17 = n14;
                            int n18 = n9;
                            if (this.dispatchNestedPreScroll(n15, n16, this.mReusableIntPair, this.mScrollOffset, 0)) {
                                final int[] mReusableIntPair2 = this.mReusableIntPair;
                                n17 = n14 - mReusableIntPair2[0];
                                n18 = n9 - mReusableIntPair2[1];
                                final int[] mNestedOffsets3 = this.mNestedOffsets;
                                final int n19 = mNestedOffsets3[0];
                                final int[] mScrollOffset = this.mScrollOffset;
                                mNestedOffsets3[0] = n19 + mScrollOffset[0];
                                mNestedOffsets3[1] += mScrollOffset[1];
                                this.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            final int[] mScrollOffset2 = this.mScrollOffset;
                            this.mLastTouchX = n4 - mScrollOffset2[0];
                            this.mLastTouchY = n5 - mScrollOffset2[1];
                            int n20;
                            if (canScrollHorizontally != 0) {
                                n20 = n17;
                            }
                            else {
                                n20 = 0;
                            }
                            int n21;
                            if (canScrollVertically) {
                                n21 = n18;
                            }
                            else {
                                n21 = 0;
                            }
                            if (this.scrollByInternal(n20, n21, motionEvent)) {
                                this.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            n = (b ? 1 : 0);
                            if (this.mGapWorker != null) {
                                if (n17 == 0) {
                                    n = (b ? 1 : 0);
                                    if (n18 == 0) {
                                        break Label_1037;
                                    }
                                }
                                this.mGapWorker.postFromTraversal(this, n17, n18);
                                n = (b ? 1 : 0);
                            }
                        }
                    }
                }
                else {
                    this.mVelocityTracker.addMovement(obtain);
                    this.mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaxFlingVelocity);
                    float n22;
                    if (canScrollHorizontally != 0) {
                        n22 = -this.mVelocityTracker.getXVelocity(this.mScrollPointerId);
                    }
                    else {
                        n22 = 0.0f;
                    }
                    float n23;
                    if (canScrollVertically) {
                        n23 = -this.mVelocityTracker.getYVelocity(this.mScrollPointerId);
                    }
                    else {
                        n23 = 0.0f;
                    }
                    if ((n22 == 0.0f && n23 == 0.0f) || !this.fling((int)n22, (int)n23)) {
                        this.setScrollState(0);
                    }
                    this.resetScroll();
                    n = 1;
                }
            }
            else {
                this.mScrollPointerId = motionEvent.getPointerId(0);
                final int n24 = (int)(motionEvent.getX() + 0.5f);
                this.mLastTouchX = n24;
                this.mInitialTouchX = n24;
                final int n25 = (int)(motionEvent.getY() + 0.5f);
                this.mLastTouchY = n25;
                this.mInitialTouchY = n25;
                int n27;
                final int n26 = n27 = canScrollHorizontally;
                if (canScrollVertically) {
                    n27 = (n26 | 0x2);
                }
                this.startNestedScroll(n27, 0);
                n = (b ? 1 : 0);
            }
        }
        if (n == 0) {
            this.mVelocityTracker.addMovement(obtain);
        }
        obtain.recycle();
        return true;
    }
    
    void postAnimationRunner() {
        if (!this.mPostedAnimatorRunner && this.mIsAttached) {
            ViewCompat.postOnAnimation((View)this, this.mItemAnimatorRunner);
            this.mPostedAnimatorRunner = true;
        }
    }
    
    void processDataSetCompletelyChanged(final boolean b) {
        this.mDispatchItemsChangedEvent |= b;
        this.mDataSetHasChangedAfterLayout = true;
        this.markKnownViewsInvalid();
    }
    
    void recordAnimationInfoIfBouncedHiddenView(final ViewHolder viewHolder, final ItemHolderInfo itemHolderInfo) {
        viewHolder.setFlags(0, 8192);
        if (this.mState.mTrackOldChangeHolders && viewHolder.isUpdated() && !viewHolder.isRemoved() && !viewHolder.shouldIgnore()) {
            this.mViewInfoStore.addToOldChangeHolders(this.getChangedHolderKey(viewHolder), viewHolder);
        }
        this.mViewInfoStore.addToPreLayout(viewHolder, itemHolderInfo);
    }
    
    void removeAndRecycleViews() {
        final ItemAnimator mItemAnimator = this.mItemAnimator;
        if (mItemAnimator != null) {
            mItemAnimator.endAnimations();
        }
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            mLayout.removeAndRecycleAllViews(this.mRecycler);
            this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        }
        this.mRecycler.clear();
    }
    
    boolean removeAnimatingView(final View view) {
        this.startInterceptRequestLayout();
        final boolean removeViewIfHidden = this.mChildHelper.removeViewIfHidden(view);
        if (removeViewIfHidden) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
            this.mRecycler.unscrapView(childViewHolderInt);
            this.mRecycler.recycleViewHolderInternal(childViewHolderInt);
        }
        this.stopInterceptRequestLayout(removeViewIfHidden ^ true);
        return removeViewIfHidden;
    }
    
    protected void removeDetachedView(final View view, final boolean b) {
        final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        if (childViewHolderInt != null) {
            if (childViewHolderInt.isTmpDetached()) {
                childViewHolderInt.clearTmpDetachFlag();
            }
            else if (!childViewHolderInt.shouldIgnore()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Called removeDetachedView with a view which is not flagged as tmp detached.");
                sb.append(childViewHolderInt);
                sb.append(this.exceptionLabel());
                throw new IllegalArgumentException(sb.toString());
            }
        }
        view.clearAnimation();
        this.dispatchChildDetached(view);
        super.removeDetachedView(view, b);
    }
    
    public void removeItemDecoration(final ItemDecoration o) {
        final LayoutManager mLayout = this.mLayout;
        if (mLayout != null) {
            mLayout.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout");
        }
        this.mItemDecorations.remove(o);
        if (this.mItemDecorations.isEmpty()) {
            this.setWillNotDraw(this.getOverScrollMode() == 2);
        }
        this.markItemDecorInsetsDirty();
        this.requestLayout();
    }
    
    public void removeOnChildAttachStateChangeListener(final OnChildAttachStateChangeListener onChildAttachStateChangeListener) {
        final List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners = this.mOnChildAttachStateListeners;
        if (mOnChildAttachStateListeners == null) {
            return;
        }
        mOnChildAttachStateListeners.remove(onChildAttachStateChangeListener);
    }
    
    public void removeOnItemTouchListener(final OnItemTouchListener o) {
        this.mOnItemTouchListeners.remove(o);
        if (this.mInterceptingOnItemTouchListener == o) {
            this.mInterceptingOnItemTouchListener = null;
        }
    }
    
    public void removeOnScrollListener(final OnScrollListener onScrollListener) {
        final List<OnScrollListener> mScrollListeners = this.mScrollListeners;
        if (mScrollListeners != null) {
            mScrollListeners.remove(onScrollListener);
        }
    }
    
    void repositionShadowingViews() {
        for (int childCount = this.mChildHelper.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.mChildHelper.getChildAt(i);
            final ViewHolder childViewHolder = this.getChildViewHolder(child);
            if (childViewHolder != null) {
                final ViewHolder mShadowingHolder = childViewHolder.mShadowingHolder;
                if (mShadowingHolder != null) {
                    final View itemView = mShadowingHolder.itemView;
                    final int left = child.getLeft();
                    final int top = child.getTop();
                    if (left != itemView.getLeft() || top != itemView.getTop()) {
                        itemView.layout(left, top, itemView.getWidth() + left, itemView.getHeight() + top);
                    }
                }
            }
        }
    }
    
    public void requestChildFocus(final View view, final View view2) {
        if (!this.mLayout.onRequestChildFocus(this, this.mState, view, view2) && view2 != null) {
            this.requestChildOnScreen(view, view2);
        }
        super.requestChildFocus(view, view2);
    }
    
    public boolean requestChildRectangleOnScreen(final View view, final Rect rect, final boolean b) {
        return this.mLayout.requestChildRectangleOnScreen(this, view, rect, b);
    }
    
    public void requestDisallowInterceptTouchEvent(final boolean b) {
        for (int size = this.mOnItemTouchListeners.size(), i = 0; i < size; ++i) {
            this.mOnItemTouchListeners.get(i).onRequestDisallowInterceptTouchEvent(b);
        }
        super.requestDisallowInterceptTouchEvent(b);
    }
    
    public void requestLayout() {
        if (this.mInterceptRequestLayoutDepth == 0 && !this.mLayoutSuppressed) {
            super.requestLayout();
        }
        else {
            this.mLayoutWasDefered = true;
        }
    }
    
    void saveOldPositions() {
        for (int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount(), i = 0; i < unfilteredChildCount; ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.saveOldPosition();
            }
        }
    }
    
    public void scrollBy(int n, int n2) {
        final LayoutManager mLayout = this.mLayout;
        if (mLayout == null) {
            Log.e("RecyclerView", "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return;
        }
        if (this.mLayoutSuppressed) {
            return;
        }
        final boolean canScrollHorizontally = mLayout.canScrollHorizontally();
        final boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (canScrollHorizontally || canScrollVertically) {
            if (!canScrollHorizontally) {
                n = 0;
            }
            if (!canScrollVertically) {
                n2 = 0;
            }
            this.scrollByInternal(n, n2, null);
        }
    }
    
    boolean scrollByInternal(final int n, final int n2, final MotionEvent motionEvent) {
        this.consumePendingUpdateOperations();
        final Adapter mAdapter = this.mAdapter;
        final boolean b = true;
        int n5;
        int n6;
        int n7;
        int n8;
        if (mAdapter != null) {
            final int[] mReusableIntPair = this.mReusableIntPair;
            mReusableIntPair[1] = (mReusableIntPair[0] = 0);
            this.scrollStep(n, n2, mReusableIntPair);
            final int[] mReusableIntPair2 = this.mReusableIntPair;
            final int n3 = mReusableIntPair2[0];
            final int n4 = n5 = mReusableIntPair2[1];
            n6 = n3;
            n7 = n - n3;
            n8 = n2 - n4;
        }
        else {
            final int n10;
            final int n9 = n10 = 0;
            n7 = (n8 = n10);
            n6 = n10;
            n5 = n9;
        }
        if (!this.mItemDecorations.isEmpty()) {
            this.invalidate();
        }
        final int[] mReusableIntPair3 = this.mReusableIntPair;
        mReusableIntPair3[1] = (mReusableIntPair3[0] = 0);
        this.dispatchNestedScroll(n6, n5, n7, n8, this.mScrollOffset, 0, mReusableIntPair3);
        final int[] mReusableIntPair4 = this.mReusableIntPair;
        final int n11 = mReusableIntPair4[0];
        final int n12 = mReusableIntPair4[1];
        final boolean b2 = mReusableIntPair4[0] != 0 || mReusableIntPair4[1] != 0;
        final int mLastTouchX = this.mLastTouchX;
        final int[] mScrollOffset = this.mScrollOffset;
        this.mLastTouchX = mLastTouchX - mScrollOffset[0];
        this.mLastTouchY -= mScrollOffset[1];
        final int[] mNestedOffsets = this.mNestedOffsets;
        mNestedOffsets[0] += mScrollOffset[0];
        mNestedOffsets[1] += mScrollOffset[1];
        if (this.getOverScrollMode() != 2) {
            if (motionEvent != null && !MotionEventCompat.isFromSource(motionEvent, 8194)) {
                this.pullGlows(motionEvent.getX(), (float)(n7 - n11), motionEvent.getY(), (float)(n8 - n12));
            }
            this.considerReleasingGlowsOnScroll(n, n2);
        }
        if (n6 != 0 || n5 != 0) {
            this.dispatchOnScrolled(n6, n5);
        }
        if (!this.awakenScrollBars()) {
            this.invalidate();
        }
        boolean b3 = b;
        if (!b2) {
            b3 = b;
            if (n6 == 0) {
                b3 = (n5 != 0 && b);
            }
        }
        return b3;
    }
    
    void scrollStep(int scrollHorizontallyBy, int scrollVerticallyBy, final int[] array) {
        this.startInterceptRequestLayout();
        this.onEnterLayoutOrScroll();
        TraceCompat.beginSection("RV Scroll");
        this.fillRemainingScrollValues(this.mState);
        if (scrollHorizontallyBy != 0) {
            scrollHorizontallyBy = this.mLayout.scrollHorizontallyBy(scrollHorizontallyBy, this.mRecycler, this.mState);
        }
        else {
            scrollHorizontallyBy = 0;
        }
        if (scrollVerticallyBy != 0) {
            scrollVerticallyBy = this.mLayout.scrollVerticallyBy(scrollVerticallyBy, this.mRecycler, this.mState);
        }
        else {
            scrollVerticallyBy = 0;
        }
        TraceCompat.endSection();
        this.repositionShadowingViews();
        this.onExitLayoutOrScroll();
        this.stopInterceptRequestLayout(false);
        if (array != null) {
            array[0] = scrollHorizontallyBy;
            array[1] = scrollVerticallyBy;
        }
    }
    
    public void scrollTo(final int n, final int n2) {
        Log.w("RecyclerView", "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
    }
    
    public void scrollToPosition(final int n) {
        if (this.mLayoutSuppressed) {
            return;
        }
        this.stopScroll();
        final LayoutManager mLayout = this.mLayout;
        if (mLayout == null) {
            Log.e("RecyclerView", "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return;
        }
        mLayout.scrollToPosition(n);
        this.awakenScrollBars();
    }
    
    public void sendAccessibilityEventUnchecked(final AccessibilityEvent accessibilityEvent) {
        if (this.shouldDeferAccessibilityEvent(accessibilityEvent)) {
            return;
        }
        super.sendAccessibilityEventUnchecked(accessibilityEvent);
    }
    
    public void setAccessibilityDelegateCompat(final RecyclerViewAccessibilityDelegate mAccessibilityDelegate) {
        ViewCompat.setAccessibilityDelegate((View)this, this.mAccessibilityDelegate = mAccessibilityDelegate);
    }
    
    public void setAdapter(final Adapter adapter) {
        this.setLayoutFrozen(false);
        this.setAdapterInternal(adapter, false, true);
        this.processDataSetCompletelyChanged(false);
        this.requestLayout();
    }
    
    public void setChildDrawingOrderCallback(final ChildDrawingOrderCallback mChildDrawingOrderCallback) {
        if (mChildDrawingOrderCallback == this.mChildDrawingOrderCallback) {
            return;
        }
        this.mChildDrawingOrderCallback = mChildDrawingOrderCallback;
        this.setChildrenDrawingOrderEnabled(mChildDrawingOrderCallback != null);
    }
    
    boolean setChildImportantForAccessibilityInternal(final ViewHolder viewHolder, final int mPendingAccessibilityState) {
        if (this.isComputingLayout()) {
            viewHolder.mPendingAccessibilityState = mPendingAccessibilityState;
            this.mPendingAccessibilityImportanceChange.add(viewHolder);
            return false;
        }
        ViewCompat.setImportantForAccessibility(viewHolder.itemView, mPendingAccessibilityState);
        return true;
    }
    
    public void setClipToPadding(final boolean mClipToPadding) {
        if (mClipToPadding != this.mClipToPadding) {
            this.invalidateGlows();
        }
        super.setClipToPadding(this.mClipToPadding = mClipToPadding);
        if (this.mFirstLayoutComplete) {
            this.requestLayout();
        }
    }
    
    public void setHasFixedSize(final boolean mHasFixedSize) {
        this.mHasFixedSize = mHasFixedSize;
    }
    
    public void setItemAnimator(final ItemAnimator mItemAnimator) {
        final ItemAnimator mItemAnimator2 = this.mItemAnimator;
        if (mItemAnimator2 != null) {
            mItemAnimator2.endAnimations();
            this.mItemAnimator.setListener(null);
        }
        if ((this.mItemAnimator = mItemAnimator) != null) {
            mItemAnimator.setListener(this.mItemAnimatorListener);
        }
    }
    
    public void setItemViewCacheSize(final int viewCacheSize) {
        this.mRecycler.setViewCacheSize(viewCacheSize);
    }
    
    @Deprecated
    public void setLayoutFrozen(final boolean b) {
        this.suppressLayout(b);
    }
    
    public void setLayoutManager(final LayoutManager layoutManager) {
        if (layoutManager == this.mLayout) {
            return;
        }
        this.stopScroll();
        if (this.mLayout != null) {
            final ItemAnimator mItemAnimator = this.mItemAnimator;
            if (mItemAnimator != null) {
                mItemAnimator.endAnimations();
            }
            this.mLayout.removeAndRecycleAllViews(this.mRecycler);
            this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
            this.mRecycler.clear();
            if (this.mIsAttached) {
                this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
            }
            this.mLayout.setRecyclerView(null);
            this.mLayout = null;
        }
        else {
            this.mRecycler.clear();
        }
        this.mChildHelper.removeAllViewsUnfiltered();
        this.mLayout = layoutManager;
        if (layoutManager != null) {
            if (layoutManager.mRecyclerView != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("LayoutManager ");
                sb.append(layoutManager);
                sb.append(" is already attached to a RecyclerView:");
                sb.append(layoutManager.mRecyclerView.exceptionLabel());
                throw new IllegalArgumentException(sb.toString());
            }
            layoutManager.setRecyclerView(this);
            if (this.mIsAttached) {
                this.mLayout.dispatchAttachedToWindow(this);
            }
        }
        this.mRecycler.updateViewCacheSize();
        this.requestLayout();
    }
    
    @Deprecated
    public void setLayoutTransition(final LayoutTransition layoutTransition) {
        if (Build$VERSION.SDK_INT < 18) {
            if (layoutTransition == null) {
                this.suppressLayout(false);
                return;
            }
            if (layoutTransition.getAnimator(0) == null && layoutTransition.getAnimator(1) == null && layoutTransition.getAnimator(2) == null && layoutTransition.getAnimator(3) == null && layoutTransition.getAnimator(4) == null) {
                this.suppressLayout(true);
                return;
            }
        }
        if (layoutTransition == null) {
            super.setLayoutTransition((LayoutTransition)null);
            return;
        }
        throw new IllegalArgumentException("Providing a LayoutTransition into RecyclerView is not supported. Please use setItemAnimator() instead for animating changes to the items in this RecyclerView");
    }
    
    public void setNestedScrollingEnabled(final boolean nestedScrollingEnabled) {
        this.getScrollingChildHelper().setNestedScrollingEnabled(nestedScrollingEnabled);
    }
    
    public void setOnFlingListener(final OnFlingListener mOnFlingListener) {
        this.mOnFlingListener = mOnFlingListener;
    }
    
    public void setPreserveFocusAfterLayout(final boolean mPreserveFocusAfterLayout) {
        this.mPreserveFocusAfterLayout = mPreserveFocusAfterLayout;
    }
    
    public void setRecyclerListener(final RecyclerListener mRecyclerListener) {
        this.mRecyclerListener = mRecyclerListener;
    }
    
    void setScrollState(final int mScrollState) {
        if (mScrollState == this.mScrollState) {
            return;
        }
        if ((this.mScrollState = mScrollState) != 2) {
            this.stopScrollersInternal();
        }
        this.dispatchOnScrollStateChanged(mScrollState);
    }
    
    public void setScrollingTouchSlop(final int i) {
        final ViewConfiguration value = ViewConfiguration.get(this.getContext());
        if (i != 0) {
            if (i == 1) {
                this.mTouchSlop = value.getScaledPagingTouchSlop();
                return;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("setScrollingTouchSlop(): bad argument constant ");
            sb.append(i);
            sb.append("; using default value");
            Log.w("RecyclerView", sb.toString());
        }
        this.mTouchSlop = value.getScaledTouchSlop();
    }
    
    boolean shouldDeferAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        final boolean computingLayout = this.isComputingLayout();
        final int n = 0;
        if (computingLayout) {
            int contentChangeTypes;
            if (accessibilityEvent != null) {
                contentChangeTypes = AccessibilityEventCompat.getContentChangeTypes(accessibilityEvent);
            }
            else {
                contentChangeTypes = 0;
            }
            if (contentChangeTypes == 0) {
                contentChangeTypes = n;
            }
            this.mEatenAccessibilityChangeFlags |= contentChangeTypes;
            return true;
        }
        return false;
    }
    
    public void smoothScrollBy(final int n, final int n2) {
        this.smoothScrollBy(n, n2, null);
    }
    
    public void smoothScrollBy(final int n, final int n2, final Interpolator interpolator) {
        this.smoothScrollBy(n, n2, interpolator, Integer.MIN_VALUE);
    }
    
    public void smoothScrollBy(final int n, final int n2, final Interpolator interpolator, final int n3) {
        this.smoothScrollBy(n, n2, interpolator, n3, false);
    }
    
    void smoothScrollBy(int n, int n2, final Interpolator interpolator, final int n3, final boolean b) {
        final LayoutManager mLayout = this.mLayout;
        if (mLayout == null) {
            Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return;
        }
        if (this.mLayoutSuppressed) {
            return;
        }
        final boolean canScrollHorizontally = mLayout.canScrollHorizontally();
        final int n4 = 0;
        int n5 = n;
        if (!canScrollHorizontally) {
            n5 = 0;
        }
        if (!this.mLayout.canScrollVertically()) {
            n2 = 0;
        }
        if (n5 != 0 || n2 != 0) {
            if (n3 != Integer.MIN_VALUE && n3 <= 0) {
                n = 0;
            }
            else {
                n = 1;
            }
            if (n != 0) {
                if (b) {
                    n = n4;
                    if (n5 != 0) {
                        n = 1;
                    }
                    int n6 = n;
                    if (n2 != 0) {
                        n6 = (n | 0x2);
                    }
                    this.startNestedScroll(n6, 1);
                }
                this.mViewFlinger.smoothScrollBy(n5, n2, n3, interpolator);
            }
            else {
                this.scrollBy(n5, n2);
            }
        }
    }
    
    public void smoothScrollToPosition(final int n) {
        if (this.mLayoutSuppressed) {
            return;
        }
        final LayoutManager mLayout = this.mLayout;
        if (mLayout == null) {
            Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return;
        }
        mLayout.smoothScrollToPosition(this, this.mState, n);
    }
    
    void startInterceptRequestLayout() {
        final int mInterceptRequestLayoutDepth = this.mInterceptRequestLayoutDepth + 1;
        this.mInterceptRequestLayoutDepth = mInterceptRequestLayoutDepth;
        if (mInterceptRequestLayoutDepth == 1 && !this.mLayoutSuppressed) {
            this.mLayoutWasDefered = false;
        }
    }
    
    public boolean startNestedScroll(final int n) {
        return this.getScrollingChildHelper().startNestedScroll(n);
    }
    
    public boolean startNestedScroll(final int n, final int n2) {
        return this.getScrollingChildHelper().startNestedScroll(n, n2);
    }
    
    void stopInterceptRequestLayout(final boolean b) {
        if (this.mInterceptRequestLayoutDepth < 1) {
            this.mInterceptRequestLayoutDepth = 1;
        }
        if (!b && !this.mLayoutSuppressed) {
            this.mLayoutWasDefered = false;
        }
        if (this.mInterceptRequestLayoutDepth == 1) {
            if (b && this.mLayoutWasDefered && !this.mLayoutSuppressed && this.mLayout != null && this.mAdapter != null) {
                this.dispatchLayout();
            }
            if (!this.mLayoutSuppressed) {
                this.mLayoutWasDefered = false;
            }
        }
        --this.mInterceptRequestLayoutDepth;
    }
    
    public void stopNestedScroll() {
        this.getScrollingChildHelper().stopNestedScroll();
    }
    
    public void stopNestedScroll(final int n) {
        this.getScrollingChildHelper().stopNestedScroll(n);
    }
    
    public void stopScroll() {
        this.setScrollState(0);
        this.stopScrollersInternal();
    }
    
    public final void suppressLayout(final boolean b) {
        if (b != this.mLayoutSuppressed) {
            this.assertNotInLayoutOrScroll("Do not suppressLayout in layout or scroll");
            if (!b) {
                this.mLayoutSuppressed = false;
                if (this.mLayoutWasDefered && this.mLayout != null && this.mAdapter != null) {
                    this.requestLayout();
                }
                this.mLayoutWasDefered = false;
            }
            else {
                final long uptimeMillis = SystemClock.uptimeMillis();
                this.onTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0));
                this.mLayoutSuppressed = true;
                this.mIgnoreMotionEventTillDown = true;
                this.stopScroll();
            }
        }
    }
    
    void viewRangeUpdate(final int n, final int n2, final Object o) {
        for (int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount(), i = 0; i < unfilteredChildCount; ++i) {
            final View unfilteredChild = this.mChildHelper.getUnfilteredChildAt(i);
            final ViewHolder childViewHolderInt = getChildViewHolderInt(unfilteredChild);
            if (childViewHolderInt != null) {
                if (!childViewHolderInt.shouldIgnore()) {
                    final int mPosition = childViewHolderInt.mPosition;
                    if (mPosition >= n && mPosition < n + n2) {
                        childViewHolderInt.addFlags(2);
                        childViewHolderInt.addChangePayload(o);
                        ((LayoutParams)unfilteredChild.getLayoutParams()).mInsetsDirty = true;
                    }
                }
            }
        }
        this.mRecycler.viewRangeUpdate(n, n2);
    }
    
    public abstract static class Adapter<VH extends ViewHolder>
    {
        private boolean mHasStableIds;
        private final AdapterDataObservable mObservable;
        private StateRestorationPolicy mStateRestorationPolicy;
        
        public Adapter() {
            this.mObservable = new AdapterDataObservable();
            this.mHasStableIds = false;
            this.mStateRestorationPolicy = StateRestorationPolicy.ALLOW;
        }
        
        public final void bindViewHolder(final VH vh, final int mPosition) {
            final boolean b = vh.mBindingAdapter == null;
            if (b) {
                vh.mPosition = mPosition;
                if (this.hasStableIds()) {
                    vh.mItemId = this.getItemId(mPosition);
                }
                ((ViewHolder)vh).setFlags(1, 519);
                TraceCompat.beginSection("RV OnBindView");
            }
            (vh.mBindingAdapter = (Adapter<? extends ViewHolder>)this).onBindViewHolder((ViewHolder)vh, mPosition, ((ViewHolder)vh).getUnmodifiedPayloads());
            if (b) {
                ((ViewHolder)vh).clearPayload();
                final ViewGroup$LayoutParams layoutParams = vh.itemView.getLayoutParams();
                if (layoutParams instanceof LayoutParams) {
                    ((LayoutParams)layoutParams).mInsetsDirty = true;
                }
                TraceCompat.endSection();
            }
        }
        
        boolean canRestoreState() {
            final int n = RecyclerView$7.$SwitchMap$androidx$recyclerview$widget$RecyclerView$Adapter$StateRestorationPolicy[this.mStateRestorationPolicy.ordinal()];
            boolean b = false;
            if (n != 1) {
                if (n != 2) {
                    return true;
                }
                b = b;
                if (this.getItemCount() > 0) {
                    b = true;
                }
            }
            return b;
        }
        
        public final VH createViewHolder(final ViewGroup viewGroup, final int mItemViewType) {
            try {
                TraceCompat.beginSection("RV CreateView");
                final ViewHolder onCreateViewHolder = this.onCreateViewHolder(viewGroup, mItemViewType);
                if (onCreateViewHolder.itemView.getParent() == null) {
                    onCreateViewHolder.mItemViewType = mItemViewType;
                    return (VH)onCreateViewHolder;
                }
                throw new IllegalStateException("ViewHolder views must not be attached when created. Ensure that you are not passing 'true' to the attachToRoot parameter of LayoutInflater.inflate(..., boolean attachToRoot)");
            }
            finally {
                TraceCompat.endSection();
            }
        }
        
        public int findRelativeAdapterPositionIn(final Adapter<? extends ViewHolder> adapter, final ViewHolder viewHolder, final int n) {
            if (adapter == this) {
                return n;
            }
            return -1;
        }
        
        public abstract int getItemCount();
        
        public long getItemId(final int n) {
            return -1L;
        }
        
        public int getItemViewType(final int n) {
            return 0;
        }
        
        public final boolean hasObservers() {
            return this.mObservable.hasObservers();
        }
        
        public final boolean hasStableIds() {
            return this.mHasStableIds;
        }
        
        public final void notifyDataSetChanged() {
            this.mObservable.notifyChanged();
        }
        
        public final void notifyItemChanged(final int n) {
            this.mObservable.notifyItemRangeChanged(n, 1);
        }
        
        public final void notifyItemChanged(final int n, final Object o) {
            this.mObservable.notifyItemRangeChanged(n, 1, o);
        }
        
        public final void notifyItemInserted(final int n) {
            this.mObservable.notifyItemRangeInserted(n, 1);
        }
        
        public final void notifyItemMoved(final int n, final int n2) {
            this.mObservable.notifyItemMoved(n, n2);
        }
        
        public final void notifyItemRangeChanged(final int n, final int n2, final Object o) {
            this.mObservable.notifyItemRangeChanged(n, n2, o);
        }
        
        public final void notifyItemRangeInserted(final int n, final int n2) {
            this.mObservable.notifyItemRangeInserted(n, n2);
        }
        
        public final void notifyItemRangeRemoved(final int n, final int n2) {
            this.mObservable.notifyItemRangeRemoved(n, n2);
        }
        
        public final void notifyItemRemoved(final int n) {
            this.mObservable.notifyItemRangeRemoved(n, 1);
        }
        
        public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        }
        
        public abstract void onBindViewHolder(final VH p0, final int p1);
        
        public void onBindViewHolder(final VH vh, final int n, final List<Object> list) {
            this.onBindViewHolder(vh, n);
        }
        
        public abstract VH onCreateViewHolder(final ViewGroup p0, final int p1);
        
        public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        }
        
        public boolean onFailedToRecycleView(final VH vh) {
            return false;
        }
        
        public void onViewAttachedToWindow(final VH vh) {
        }
        
        public void onViewDetachedFromWindow(final VH vh) {
        }
        
        public void onViewRecycled(final VH vh) {
        }
        
        public void registerAdapterDataObserver(final AdapterDataObserver adapterDataObserver) {
            this.mObservable.registerObserver((Object)adapterDataObserver);
        }
        
        public void setHasStableIds(final boolean mHasStableIds) {
            if (!this.hasObservers()) {
                this.mHasStableIds = mHasStableIds;
                return;
            }
            throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
        }
        
        public void unregisterAdapterDataObserver(final AdapterDataObserver adapterDataObserver) {
            this.mObservable.unregisterObserver((Object)adapterDataObserver);
        }
        
        public enum StateRestorationPolicy
        {
            ALLOW, 
            PREVENT, 
            PREVENT_WHEN_EMPTY;
        }
    }
    
    static class AdapterDataObservable extends Observable<AdapterDataObserver>
    {
        public boolean hasObservers() {
            return super.mObservers.isEmpty() ^ true;
        }
        
        public void notifyChanged() {
            for (int i = super.mObservers.size() - 1; i >= 0; --i) {
                ((AdapterDataObserver)super.mObservers.get(i)).onChanged();
            }
        }
        
        public void notifyItemMoved(final int n, final int n2) {
            for (int i = super.mObservers.size() - 1; i >= 0; --i) {
                ((AdapterDataObserver)super.mObservers.get(i)).onItemRangeMoved(n, n2, 1);
            }
        }
        
        public void notifyItemRangeChanged(final int n, final int n2) {
            this.notifyItemRangeChanged(n, n2, null);
        }
        
        public void notifyItemRangeChanged(final int n, final int n2, final Object o) {
            for (int i = super.mObservers.size() - 1; i >= 0; --i) {
                ((AdapterDataObserver)super.mObservers.get(i)).onItemRangeChanged(n, n2, o);
            }
        }
        
        public void notifyItemRangeInserted(final int n, final int n2) {
            for (int i = super.mObservers.size() - 1; i >= 0; --i) {
                ((AdapterDataObserver)super.mObservers.get(i)).onItemRangeInserted(n, n2);
            }
        }
        
        public void notifyItemRangeRemoved(final int n, final int n2) {
            for (int i = super.mObservers.size() - 1; i >= 0; --i) {
                ((AdapterDataObserver)super.mObservers.get(i)).onItemRangeRemoved(n, n2);
            }
        }
    }
    
    public abstract static class AdapterDataObserver
    {
        public void onChanged() {
        }
        
        public void onItemRangeChanged(final int n, final int n2) {
        }
        
        public void onItemRangeChanged(final int n, final int n2, final Object o) {
            this.onItemRangeChanged(n, n2);
        }
        
        public void onItemRangeInserted(final int n, final int n2) {
        }
        
        public void onItemRangeMoved(final int n, final int n2, final int n3) {
        }
        
        public void onItemRangeRemoved(final int n, final int n2) {
        }
    }
    
    public interface ChildDrawingOrderCallback
    {
        int onGetChildDrawingOrder(final int p0, final int p1);
    }
    
    public static class EdgeEffectFactory
    {
        protected EdgeEffect createEdgeEffect(final RecyclerView recyclerView, final int n) {
            return new EdgeEffect(recyclerView.getContext());
        }
    }
    
    public abstract static class ItemAnimator
    {
        private long mAddDuration;
        private long mChangeDuration;
        private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners;
        private ItemAnimatorListener mListener;
        private long mMoveDuration;
        private long mRemoveDuration;
        
        public ItemAnimator() {
            this.mListener = null;
            this.mFinishedListeners = new ArrayList<ItemAnimatorFinishedListener>();
            this.mAddDuration = 120L;
            this.mRemoveDuration = 120L;
            this.mMoveDuration = 250L;
            this.mChangeDuration = 250L;
        }
        
        static int buildAdapterChangeFlagsForAnimations(final ViewHolder viewHolder) {
            final int n = viewHolder.mFlags & 0xE;
            if (viewHolder.isInvalid()) {
                return 4;
            }
            int n2 = n;
            if ((n & 0x4) == 0x0) {
                final int oldPosition = viewHolder.getOldPosition();
                final int absoluteAdapterPosition = viewHolder.getAbsoluteAdapterPosition();
                n2 = n;
                if (oldPosition != -1) {
                    n2 = n;
                    if (absoluteAdapterPosition != -1) {
                        n2 = n;
                        if (oldPosition != absoluteAdapterPosition) {
                            n2 = (n | 0x800);
                        }
                    }
                }
            }
            return n2;
        }
        
        public abstract boolean animateAppearance(final ViewHolder p0, final ItemHolderInfo p1, final ItemHolderInfo p2);
        
        public abstract boolean animateChange(final ViewHolder p0, final ViewHolder p1, final ItemHolderInfo p2, final ItemHolderInfo p3);
        
        public abstract boolean animateDisappearance(final ViewHolder p0, final ItemHolderInfo p1, final ItemHolderInfo p2);
        
        public abstract boolean animatePersistence(final ViewHolder p0, final ItemHolderInfo p1, final ItemHolderInfo p2);
        
        public abstract boolean canReuseUpdatedViewHolder(final ViewHolder p0);
        
        public boolean canReuseUpdatedViewHolder(final ViewHolder viewHolder, final List<Object> list) {
            return this.canReuseUpdatedViewHolder(viewHolder);
        }
        
        public final void dispatchAnimationFinished(final ViewHolder viewHolder) {
            this.onAnimationFinished(viewHolder);
            final ItemAnimatorListener mListener = this.mListener;
            if (mListener != null) {
                mListener.onAnimationFinished(viewHolder);
            }
        }
        
        public final void dispatchAnimationsFinished() {
            for (int size = this.mFinishedListeners.size(), i = 0; i < size; ++i) {
                this.mFinishedListeners.get(i).onAnimationsFinished();
            }
            this.mFinishedListeners.clear();
        }
        
        public abstract void endAnimation(final ViewHolder p0);
        
        public abstract void endAnimations();
        
        public long getAddDuration() {
            return this.mAddDuration;
        }
        
        public long getChangeDuration() {
            return this.mChangeDuration;
        }
        
        public long getMoveDuration() {
            return this.mMoveDuration;
        }
        
        public long getRemoveDuration() {
            return this.mRemoveDuration;
        }
        
        public abstract boolean isRunning();
        
        public final boolean isRunning(final ItemAnimatorFinishedListener e) {
            final boolean running = this.isRunning();
            if (e != null) {
                if (!running) {
                    e.onAnimationsFinished();
                }
                else {
                    this.mFinishedListeners.add(e);
                }
            }
            return running;
        }
        
        public ItemHolderInfo obtainHolderInfo() {
            return new ItemHolderInfo();
        }
        
        public void onAnimationFinished(final ViewHolder viewHolder) {
        }
        
        public ItemHolderInfo recordPostLayoutInformation(final State state, final ViewHolder from) {
            final ItemHolderInfo obtainHolderInfo = this.obtainHolderInfo();
            obtainHolderInfo.setFrom(from);
            return obtainHolderInfo;
        }
        
        public ItemHolderInfo recordPreLayoutInformation(final State state, final ViewHolder from, final int n, final List<Object> list) {
            final ItemHolderInfo obtainHolderInfo = this.obtainHolderInfo();
            obtainHolderInfo.setFrom(from);
            return obtainHolderInfo;
        }
        
        public abstract void runPendingAnimations();
        
        void setListener(final ItemAnimatorListener mListener) {
            this.mListener = mListener;
        }
        
        public void setMoveDuration(final long mMoveDuration) {
            this.mMoveDuration = mMoveDuration;
        }
        
        public interface ItemAnimatorFinishedListener
        {
            void onAnimationsFinished();
        }
        
        interface ItemAnimatorListener
        {
            void onAnimationFinished(final ViewHolder p0);
        }
        
        public static class ItemHolderInfo
        {
            public int left;
            public int top;
            
            public ItemHolderInfo setFrom(final ViewHolder viewHolder) {
                this.setFrom(viewHolder, 0);
                return this;
            }
            
            public ItemHolderInfo setFrom(final ViewHolder viewHolder, final int n) {
                final View itemView = viewHolder.itemView;
                this.left = itemView.getLeft();
                this.top = itemView.getTop();
                itemView.getRight();
                itemView.getBottom();
                return this;
            }
        }
    }
    
    private class ItemAnimatorRestoreListener implements ItemAnimatorListener
    {
        ItemAnimatorRestoreListener() {
        }
        
        @Override
        public void onAnimationFinished(final ViewHolder viewHolder) {
            viewHolder.setIsRecyclable(true);
            if (viewHolder.mShadowedHolder != null && viewHolder.mShadowingHolder == null) {
                viewHolder.mShadowedHolder = null;
            }
            viewHolder.mShadowingHolder = null;
            if (!viewHolder.shouldBeKeptAsChild() && !RecyclerView.this.removeAnimatingView(viewHolder.itemView) && viewHolder.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(viewHolder.itemView, false);
            }
        }
    }
    
    public abstract static class ItemDecoration
    {
        @Deprecated
        public void getItemOffsets(final Rect rect, final int n, final RecyclerView recyclerView) {
            rect.set(0, 0, 0, 0);
        }
        
        public void getItemOffsets(final Rect rect, final View view, final RecyclerView recyclerView, final State state) {
            this.getItemOffsets(rect, ((LayoutParams)view.getLayoutParams()).getViewLayoutPosition(), recyclerView);
        }
        
        @Deprecated
        public void onDraw(final Canvas canvas, final RecyclerView recyclerView) {
        }
        
        public void onDraw(final Canvas canvas, final RecyclerView recyclerView, final State state) {
            this.onDraw(canvas, recyclerView);
        }
        
        @Deprecated
        public void onDrawOver(final Canvas canvas, final RecyclerView recyclerView) {
        }
        
        public void onDrawOver(final Canvas canvas, final RecyclerView recyclerView, final State state) {
            this.onDrawOver(canvas, recyclerView);
        }
    }
    
    public abstract static class LayoutManager
    {
        boolean mAutoMeasure;
        ChildHelper mChildHelper;
        private int mHeight;
        private int mHeightMode;
        ViewBoundsCheck mHorizontalBoundCheck;
        private final ViewBoundsCheck.Callback mHorizontalBoundCheckCallback;
        boolean mIsAttachedToWindow;
        private boolean mItemPrefetchEnabled;
        private boolean mMeasurementCacheEnabled;
        int mPrefetchMaxCountObserved;
        boolean mPrefetchMaxObservedInInitialPrefetch;
        RecyclerView mRecyclerView;
        boolean mRequestedSimpleAnimations;
        SmoothScroller mSmoothScroller;
        ViewBoundsCheck mVerticalBoundCheck;
        private final ViewBoundsCheck.Callback mVerticalBoundCheckCallback;
        private int mWidth;
        private int mWidthMode;
        
        public LayoutManager() {
            this.mHorizontalBoundCheckCallback = new ViewBoundsCheck.Callback() {
                @Override
                public View getChildAt(final int n) {
                    return LayoutManager.this.getChildAt(n);
                }
                
                @Override
                public int getChildEnd(final View view) {
                    return LayoutManager.this.getDecoratedRight(view) + ((LayoutParams)view.getLayoutParams()).rightMargin;
                }
                
                @Override
                public int getChildStart(final View view) {
                    return LayoutManager.this.getDecoratedLeft(view) - ((LayoutParams)view.getLayoutParams()).leftMargin;
                }
                
                @Override
                public int getParentEnd() {
                    return LayoutManager.this.getWidth() - LayoutManager.this.getPaddingRight();
                }
                
                @Override
                public int getParentStart() {
                    return LayoutManager.this.getPaddingLeft();
                }
            };
            this.mVerticalBoundCheckCallback = new ViewBoundsCheck.Callback() {
                @Override
                public View getChildAt(final int n) {
                    return LayoutManager.this.getChildAt(n);
                }
                
                @Override
                public int getChildEnd(final View view) {
                    return LayoutManager.this.getDecoratedBottom(view) + ((LayoutParams)view.getLayoutParams()).bottomMargin;
                }
                
                @Override
                public int getChildStart(final View view) {
                    return LayoutManager.this.getDecoratedTop(view) - ((LayoutParams)view.getLayoutParams()).topMargin;
                }
                
                @Override
                public int getParentEnd() {
                    return LayoutManager.this.getHeight() - LayoutManager.this.getPaddingBottom();
                }
                
                @Override
                public int getParentStart() {
                    return LayoutManager.this.getPaddingTop();
                }
            };
            this.mHorizontalBoundCheck = new ViewBoundsCheck(this.mHorizontalBoundCheckCallback);
            this.mVerticalBoundCheck = new ViewBoundsCheck(this.mVerticalBoundCheckCallback);
            this.mRequestedSimpleAnimations = false;
            this.mIsAttachedToWindow = false;
            this.mAutoMeasure = false;
            this.mMeasurementCacheEnabled = true;
            this.mItemPrefetchEnabled = true;
        }
        
        private void addViewInt(final View view, final int n, final boolean b) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (!b && !childViewHolderInt.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(childViewHolderInt);
            }
            else {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(childViewHolderInt);
            }
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            if (!childViewHolderInt.wasReturnedFromScrap() && !childViewHolderInt.isScrap()) {
                if (view.getParent() == this.mRecyclerView) {
                    final int indexOfChild = this.mChildHelper.indexOfChild(view);
                    int childCount;
                    if ((childCount = n) == -1) {
                        childCount = this.mChildHelper.getChildCount();
                    }
                    if (indexOfChild == -1) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:");
                        sb.append(this.mRecyclerView.indexOfChild(view));
                        sb.append(this.mRecyclerView.exceptionLabel());
                        throw new IllegalStateException(sb.toString());
                    }
                    if (indexOfChild != childCount) {
                        this.mRecyclerView.mLayout.moveView(indexOfChild, childCount);
                    }
                }
                else {
                    this.mChildHelper.addView(view, n, false);
                    layoutParams.mInsetsDirty = true;
                    final SmoothScroller mSmoothScroller = this.mSmoothScroller;
                    if (mSmoothScroller != null && mSmoothScroller.isRunning()) {
                        this.mSmoothScroller.onChildAttachedToWindow(view);
                    }
                }
            }
            else {
                if (childViewHolderInt.isScrap()) {
                    childViewHolderInt.unScrap();
                }
                else {
                    childViewHolderInt.clearReturnedFromScrapFlag();
                }
                this.mChildHelper.attachViewToParent(view, n, view.getLayoutParams(), false);
            }
            if (layoutParams.mPendingInvalidate) {
                childViewHolderInt.itemView.invalidate();
                layoutParams.mPendingInvalidate = false;
            }
        }
        
        public static int chooseSize(int a, final int n, final int n2) {
            final int mode = View$MeasureSpec.getMode(a);
            a = View$MeasureSpec.getSize(a);
            if (mode != Integer.MIN_VALUE) {
                if (mode != 1073741824) {
                    a = Math.max(n, n2);
                }
                return a;
            }
            return Math.min(a, Math.max(n, n2));
        }
        
        private void detachViewInternal(final int n, final View view) {
            this.mChildHelper.detachViewFromParent(n);
        }
        
        public static int getChildMeasureSpec(int max, int n, final int n2, int n3, final boolean b) {
            max = Math.max(0, max - n2);
            Label_0100: {
                Label_0059: {
                    if (b) {
                        if (n3 < 0) {
                            if (n3 != -1) {
                                break Label_0100;
                            }
                            if (n == Integer.MIN_VALUE) {
                                break Label_0059;
                            }
                            if (n == 0) {
                                break Label_0100;
                            }
                            if (n != 1073741824) {
                                break Label_0100;
                            }
                            break Label_0059;
                        }
                    }
                    else if (n3 < 0) {
                        if (n3 == -1) {
                            break Label_0059;
                        }
                        if (n3 != -2) {
                            break Label_0100;
                        }
                        if (n != Integer.MIN_VALUE && n != 1073741824) {
                            n = 0;
                            n3 = max;
                            return View$MeasureSpec.makeMeasureSpec(n3, n);
                        }
                        n = Integer.MIN_VALUE;
                        n3 = max;
                        return View$MeasureSpec.makeMeasureSpec(n3, n);
                    }
                    n = 1073741824;
                    return View$MeasureSpec.makeMeasureSpec(n3, n);
                }
                n3 = max;
                return View$MeasureSpec.makeMeasureSpec(n3, n);
            }
            n = (n3 = 0);
            return View$MeasureSpec.makeMeasureSpec(n3, n);
        }
        
        private int[] getChildRectangleOnScreenScrollAmount(final View view, final Rect rect) {
            final int paddingLeft = this.getPaddingLeft();
            final int paddingTop = this.getPaddingTop();
            final int width = this.getWidth();
            final int paddingRight = this.getPaddingRight();
            final int height = this.getHeight();
            final int paddingBottom = this.getPaddingBottom();
            final int n = view.getLeft() + rect.left - view.getScrollX();
            final int n2 = view.getTop() + rect.top - view.getScrollY();
            final int width2 = rect.width();
            final int height2 = rect.height();
            final int n3 = n - paddingLeft;
            int a = Math.min(0, n3);
            final int n4 = n2 - paddingTop;
            int n5 = Math.min(0, n4);
            final int n6 = width2 + n - (width - paddingRight);
            final int max = Math.max(0, n6);
            final int max2 = Math.max(0, height2 + n2 - (height - paddingBottom));
            if (this.getLayoutDirection() == 1) {
                if (max != 0) {
                    a = max;
                }
                else {
                    a = Math.max(a, n6);
                }
            }
            else if (a == 0) {
                a = Math.min(n3, max);
            }
            if (n5 == 0) {
                n5 = Math.min(n4, max2);
            }
            return new int[] { a, n5 };
        }
        
        public static Properties getProperties(final Context context, final AttributeSet set, final int n, final int n2) {
            final Properties properties = new Properties();
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.RecyclerView, n, n2);
            properties.orientation = obtainStyledAttributes.getInt(R$styleable.RecyclerView_android_orientation, 1);
            properties.spanCount = obtainStyledAttributes.getInt(R$styleable.RecyclerView_spanCount, 1);
            properties.reverseLayout = obtainStyledAttributes.getBoolean(R$styleable.RecyclerView_reverseLayout, false);
            properties.stackFromEnd = obtainStyledAttributes.getBoolean(R$styleable.RecyclerView_stackFromEnd, false);
            obtainStyledAttributes.recycle();
            return properties;
        }
        
        private boolean isFocusedChildVisibleAfterScrolling(final RecyclerView recyclerView, final int n, final int n2) {
            final View focusedChild = recyclerView.getFocusedChild();
            if (focusedChild == null) {
                return false;
            }
            final int paddingLeft = this.getPaddingLeft();
            final int paddingTop = this.getPaddingTop();
            final int width = this.getWidth();
            final int paddingRight = this.getPaddingRight();
            final int height = this.getHeight();
            final int paddingBottom = this.getPaddingBottom();
            final Rect mTempRect = this.mRecyclerView.mTempRect;
            this.getDecoratedBoundsWithMargins(focusedChild, mTempRect);
            return mTempRect.left - n < width - paddingRight && mTempRect.right - n > paddingLeft && mTempRect.top - n2 < height - paddingBottom && mTempRect.bottom - n2 > paddingTop;
        }
        
        private static boolean isMeasurementUpToDate(final int n, int size, final int n2) {
            final int mode = View$MeasureSpec.getMode(size);
            size = View$MeasureSpec.getSize(size);
            final boolean b = false;
            boolean b2 = false;
            if (n2 > 0 && n != n2) {
                return false;
            }
            if (mode == Integer.MIN_VALUE) {
                boolean b3 = b;
                if (size >= n) {
                    b3 = true;
                }
                return b3;
            }
            if (mode == 0) {
                return true;
            }
            if (mode != 1073741824) {
                return false;
            }
            if (size == n) {
                b2 = true;
            }
            return b2;
        }
        
        private void scrapOrRecycleView(final Recycler recycler, final int n, final View view) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.shouldIgnore()) {
                return;
            }
            if (childViewHolderInt.isInvalid() && !childViewHolderInt.isRemoved() && !this.mRecyclerView.mAdapter.hasStableIds()) {
                this.removeViewAt(n);
                recycler.recycleViewHolderInternal(childViewHolderInt);
            }
            else {
                this.detachViewAt(n);
                recycler.scrapView(view);
                this.mRecyclerView.mViewInfoStore.onViewDetached(childViewHolderInt);
            }
        }
        
        public void addDisappearingView(final View view) {
            this.addDisappearingView(view, -1);
        }
        
        public void addDisappearingView(final View view, final int n) {
            this.addViewInt(view, n, true);
        }
        
        public void addView(final View view) {
            this.addView(view, -1);
        }
        
        public void addView(final View view, final int n) {
            this.addViewInt(view, n, false);
        }
        
        public void assertNotInLayoutOrScroll(final String s) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (mRecyclerView != null) {
                mRecyclerView.assertNotInLayoutOrScroll(s);
            }
        }
        
        public void attachView(final View view, final int n) {
            this.attachView(view, n, (LayoutParams)view.getLayoutParams());
        }
        
        public void attachView(final View view, final int n, final LayoutParams layoutParams) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(childViewHolderInt);
            }
            else {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(childViewHolderInt);
            }
            this.mChildHelper.attachViewToParent(view, n, (ViewGroup$LayoutParams)layoutParams, childViewHolderInt.isRemoved());
        }
        
        public void calculateItemDecorationsForChild(final View view, final Rect rect) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (mRecyclerView == null) {
                rect.set(0, 0, 0, 0);
                return;
            }
            rect.set(mRecyclerView.getItemDecorInsetsForChild(view));
        }
        
        public boolean canScrollHorizontally() {
            return false;
        }
        
        public boolean canScrollVertically() {
            return false;
        }
        
        public boolean checkLayoutParams(final LayoutParams layoutParams) {
            return layoutParams != null;
        }
        
        public void collectAdjacentPrefetchPositions(final int n, final int n2, final State state, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        }
        
        public void collectInitialPrefetchPositions(final int n, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        }
        
        public int computeHorizontalScrollExtent(final State state) {
            return 0;
        }
        
        public int computeHorizontalScrollOffset(final State state) {
            return 0;
        }
        
        public int computeHorizontalScrollRange(final State state) {
            return 0;
        }
        
        public int computeVerticalScrollExtent(final State state) {
            return 0;
        }
        
        public int computeVerticalScrollOffset(final State state) {
            return 0;
        }
        
        public int computeVerticalScrollRange(final State state) {
            return 0;
        }
        
        public void detachAndScrapAttachedViews(final Recycler recycler) {
            for (int i = this.getChildCount() - 1; i >= 0; --i) {
                this.scrapOrRecycleView(recycler, i, this.getChildAt(i));
            }
        }
        
        public void detachAndScrapView(final View view, final Recycler recycler) {
            this.scrapOrRecycleView(recycler, this.mChildHelper.indexOfChild(view), view);
        }
        
        public void detachViewAt(final int n) {
            this.detachViewInternal(n, this.getChildAt(n));
        }
        
        void dispatchAttachedToWindow(final RecyclerView recyclerView) {
            this.mIsAttachedToWindow = true;
            this.onAttachedToWindow(recyclerView);
        }
        
        void dispatchDetachedFromWindow(final RecyclerView recyclerView, final Recycler recycler) {
            this.mIsAttachedToWindow = false;
            this.onDetachedFromWindow(recyclerView, recycler);
        }
        
        public View findContainingItemView(View containingItemView) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (mRecyclerView == null) {
                return null;
            }
            containingItemView = mRecyclerView.findContainingItemView(containingItemView);
            if (containingItemView == null) {
                return null;
            }
            if (this.mChildHelper.isHidden(containingItemView)) {
                return null;
            }
            return containingItemView;
        }
        
        public View findViewByPosition(final int n) {
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                final View child = this.getChildAt(i);
                final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(child);
                if (childViewHolderInt != null) {
                    if (childViewHolderInt.getLayoutPosition() == n && !childViewHolderInt.shouldIgnore() && (this.mRecyclerView.mState.isPreLayout() || !childViewHolderInt.isRemoved())) {
                        return child;
                    }
                }
            }
            return null;
        }
        
        public abstract LayoutParams generateDefaultLayoutParams();
        
        public LayoutParams generateLayoutParams(final Context context, final AttributeSet set) {
            return new LayoutParams(context, set);
        }
        
        public LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            if (viewGroup$LayoutParams instanceof LayoutParams) {
                return new LayoutParams((LayoutParams)viewGroup$LayoutParams);
            }
            if (viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams) {
                return new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
            }
            return new LayoutParams(viewGroup$LayoutParams);
        }
        
        public int getBaseline() {
            return -1;
        }
        
        public int getBottomDecorationHeight(final View view) {
            return ((LayoutParams)view.getLayoutParams()).mDecorInsets.bottom;
        }
        
        public View getChildAt(final int n) {
            final ChildHelper mChildHelper = this.mChildHelper;
            View child;
            if (mChildHelper != null) {
                child = mChildHelper.getChildAt(n);
            }
            else {
                child = null;
            }
            return child;
        }
        
        public int getChildCount() {
            final ChildHelper mChildHelper = this.mChildHelper;
            int childCount;
            if (mChildHelper != null) {
                childCount = mChildHelper.getChildCount();
            }
            else {
                childCount = 0;
            }
            return childCount;
        }
        
        public boolean getClipToPadding() {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            return mRecyclerView != null && mRecyclerView.mClipToPadding;
        }
        
        public int getColumnCountForAccessibility(final Recycler recycler, final State state) {
            return -1;
        }
        
        public int getDecoratedBottom(final View view) {
            return view.getBottom() + this.getBottomDecorationHeight(view);
        }
        
        public void getDecoratedBoundsWithMargins(final View view, final Rect rect) {
            RecyclerView.getDecoratedBoundsWithMarginsInt(view, rect);
        }
        
        public int getDecoratedLeft(final View view) {
            return view.getLeft() - this.getLeftDecorationWidth(view);
        }
        
        public int getDecoratedMeasuredHeight(final View view) {
            final Rect mDecorInsets = ((LayoutParams)view.getLayoutParams()).mDecorInsets;
            return view.getMeasuredHeight() + mDecorInsets.top + mDecorInsets.bottom;
        }
        
        public int getDecoratedMeasuredWidth(final View view) {
            final Rect mDecorInsets = ((LayoutParams)view.getLayoutParams()).mDecorInsets;
            return view.getMeasuredWidth() + mDecorInsets.left + mDecorInsets.right;
        }
        
        public int getDecoratedRight(final View view) {
            return view.getRight() + this.getRightDecorationWidth(view);
        }
        
        public int getDecoratedTop(final View view) {
            return view.getTop() - this.getTopDecorationHeight(view);
        }
        
        public View getFocusedChild() {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (mRecyclerView == null) {
                return null;
            }
            final View focusedChild = mRecyclerView.getFocusedChild();
            if (focusedChild != null && !this.mChildHelper.isHidden(focusedChild)) {
                return focusedChild;
            }
            return null;
        }
        
        public int getHeight() {
            return this.mHeight;
        }
        
        public int getHeightMode() {
            return this.mHeightMode;
        }
        
        public int getItemCount() {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            Adapter adapter;
            if (mRecyclerView != null) {
                adapter = mRecyclerView.getAdapter();
            }
            else {
                adapter = null;
            }
            int itemCount;
            if (adapter != null) {
                itemCount = adapter.getItemCount();
            }
            else {
                itemCount = 0;
            }
            return itemCount;
        }
        
        public int getLayoutDirection() {
            return ViewCompat.getLayoutDirection((View)this.mRecyclerView);
        }
        
        public int getLeftDecorationWidth(final View view) {
            return ((LayoutParams)view.getLayoutParams()).mDecorInsets.left;
        }
        
        public int getMinimumHeight() {
            return ViewCompat.getMinimumHeight((View)this.mRecyclerView);
        }
        
        public int getMinimumWidth() {
            return ViewCompat.getMinimumWidth((View)this.mRecyclerView);
        }
        
        public int getPaddingBottom() {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            int paddingBottom;
            if (mRecyclerView != null) {
                paddingBottom = mRecyclerView.getPaddingBottom();
            }
            else {
                paddingBottom = 0;
            }
            return paddingBottom;
        }
        
        public int getPaddingLeft() {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            int paddingLeft;
            if (mRecyclerView != null) {
                paddingLeft = mRecyclerView.getPaddingLeft();
            }
            else {
                paddingLeft = 0;
            }
            return paddingLeft;
        }
        
        public int getPaddingRight() {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            int paddingRight;
            if (mRecyclerView != null) {
                paddingRight = mRecyclerView.getPaddingRight();
            }
            else {
                paddingRight = 0;
            }
            return paddingRight;
        }
        
        public int getPaddingTop() {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            int paddingTop;
            if (mRecyclerView != null) {
                paddingTop = mRecyclerView.getPaddingTop();
            }
            else {
                paddingTop = 0;
            }
            return paddingTop;
        }
        
        public int getPosition(final View view) {
            return ((LayoutParams)view.getLayoutParams()).getViewLayoutPosition();
        }
        
        public int getRightDecorationWidth(final View view) {
            return ((LayoutParams)view.getLayoutParams()).mDecorInsets.right;
        }
        
        public int getRowCountForAccessibility(final Recycler recycler, final State state) {
            return -1;
        }
        
        public int getSelectionModeForAccessibility(final Recycler recycler, final State state) {
            return 0;
        }
        
        public int getTopDecorationHeight(final View view) {
            return ((LayoutParams)view.getLayoutParams()).mDecorInsets.top;
        }
        
        public void getTransformedBoundingBox(final View view, final boolean b, final Rect rect) {
            if (b) {
                final Rect mDecorInsets = ((LayoutParams)view.getLayoutParams()).mDecorInsets;
                rect.set(-mDecorInsets.left, -mDecorInsets.top, view.getWidth() + mDecorInsets.right, view.getHeight() + mDecorInsets.bottom);
            }
            else {
                rect.set(0, 0, view.getWidth(), view.getHeight());
            }
            if (this.mRecyclerView != null) {
                final Matrix matrix = view.getMatrix();
                if (matrix != null && !matrix.isIdentity()) {
                    final RectF mTempRectF = this.mRecyclerView.mTempRectF;
                    mTempRectF.set(rect);
                    matrix.mapRect(mTempRectF);
                    rect.set((int)Math.floor(mTempRectF.left), (int)Math.floor(mTempRectF.top), (int)Math.ceil(mTempRectF.right), (int)Math.ceil(mTempRectF.bottom));
                }
            }
            rect.offset(view.getLeft(), view.getTop());
        }
        
        public int getWidth() {
            return this.mWidth;
        }
        
        public int getWidthMode() {
            return this.mWidthMode;
        }
        
        boolean hasFlexibleChildInBothOrientations() {
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                final ViewGroup$LayoutParams layoutParams = this.getChildAt(i).getLayoutParams();
                if (layoutParams.width < 0 && layoutParams.height < 0) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean hasFocus() {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            return mRecyclerView != null && mRecyclerView.hasFocus();
        }
        
        public boolean isAttachedToWindow() {
            return this.mIsAttachedToWindow;
        }
        
        public boolean isAutoMeasureEnabled() {
            return this.mAutoMeasure;
        }
        
        public final boolean isItemPrefetchEnabled() {
            return this.mItemPrefetchEnabled;
        }
        
        public boolean isLayoutHierarchical(final Recycler recycler, final State state) {
            return false;
        }
        
        public boolean isSmoothScrolling() {
            final SmoothScroller mSmoothScroller = this.mSmoothScroller;
            return mSmoothScroller != null && mSmoothScroller.isRunning();
        }
        
        public boolean isViewPartiallyVisible(final View view, final boolean b, final boolean b2) {
            final boolean b3 = this.mHorizontalBoundCheck.isViewWithinBoundFlags(view, 24579) && this.mVerticalBoundCheck.isViewWithinBoundFlags(view, 24579);
            if (b) {
                return b3;
            }
            return b3 ^ true;
        }
        
        public void layoutDecoratedWithMargins(final View view, final int n, final int n2, final int n3, final int n4) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final Rect mDecorInsets = layoutParams.mDecorInsets;
            view.layout(n + mDecorInsets.left + layoutParams.leftMargin, n2 + mDecorInsets.top + layoutParams.topMargin, n3 - mDecorInsets.right - layoutParams.rightMargin, n4 - mDecorInsets.bottom - layoutParams.bottomMargin);
        }
        
        public void measureChildWithMargins(final View view, int childMeasureSpec, int childMeasureSpec2) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final Rect itemDecorInsetsForChild = this.mRecyclerView.getItemDecorInsetsForChild(view);
            final int left = itemDecorInsetsForChild.left;
            final int right = itemDecorInsetsForChild.right;
            final int top = itemDecorInsetsForChild.top;
            final int bottom = itemDecorInsetsForChild.bottom;
            childMeasureSpec = getChildMeasureSpec(this.getWidth(), this.getWidthMode(), this.getPaddingLeft() + this.getPaddingRight() + layoutParams.leftMargin + layoutParams.rightMargin + (childMeasureSpec + (left + right)), layoutParams.width, this.canScrollHorizontally());
            childMeasureSpec2 = getChildMeasureSpec(this.getHeight(), this.getHeightMode(), this.getPaddingTop() + this.getPaddingBottom() + layoutParams.topMargin + layoutParams.bottomMargin + (childMeasureSpec2 + (top + bottom)), layoutParams.height, this.canScrollVertically());
            if (this.shouldMeasureChild(view, childMeasureSpec, childMeasureSpec2, layoutParams)) {
                view.measure(childMeasureSpec, childMeasureSpec2);
            }
        }
        
        public void moveView(final int i, final int n) {
            final View child = this.getChildAt(i);
            if (child != null) {
                this.detachViewAt(i);
                this.attachView(child, n);
                return;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot move a child from non-existing index:");
            sb.append(i);
            sb.append(this.mRecyclerView.toString());
            throw new IllegalArgumentException(sb.toString());
        }
        
        public void offsetChildrenHorizontal(final int n) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (mRecyclerView != null) {
                mRecyclerView.offsetChildrenHorizontal(n);
            }
        }
        
        public void offsetChildrenVertical(final int n) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (mRecyclerView != null) {
                mRecyclerView.offsetChildrenVertical(n);
            }
        }
        
        public void onAdapterChanged(final Adapter adapter, final Adapter adapter2) {
        }
        
        public boolean onAddFocusables(final RecyclerView recyclerView, final ArrayList<View> list, final int n, final int n2) {
            return false;
        }
        
        public void onAttachedToWindow(final RecyclerView recyclerView) {
        }
        
        @Deprecated
        public void onDetachedFromWindow(final RecyclerView recyclerView) {
        }
        
        public void onDetachedFromWindow(final RecyclerView recyclerView, final Recycler recycler) {
            this.onDetachedFromWindow(recyclerView);
        }
        
        public View onFocusSearchFailed(final View view, final int n, final Recycler recycler, final State state) {
            return null;
        }
        
        public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            this.onInitializeAccessibilityEvent(mRecyclerView.mRecycler, mRecyclerView.mState, accessibilityEvent);
        }
        
        public void onInitializeAccessibilityEvent(final Recycler recycler, final State state, final AccessibilityEvent accessibilityEvent) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (mRecyclerView != null) {
                if (accessibilityEvent != null) {
                    boolean scrollable;
                    final boolean b = scrollable = true;
                    if (!mRecyclerView.canScrollVertically(1)) {
                        scrollable = b;
                        if (!this.mRecyclerView.canScrollVertically(-1)) {
                            scrollable = b;
                            if (!this.mRecyclerView.canScrollHorizontally(-1)) {
                                scrollable = (this.mRecyclerView.canScrollHorizontally(1) && b);
                            }
                        }
                    }
                    accessibilityEvent.setScrollable(scrollable);
                    final Adapter mAdapter = this.mRecyclerView.mAdapter;
                    if (mAdapter != null) {
                        accessibilityEvent.setItemCount(mAdapter.getItemCount());
                    }
                }
            }
        }
        
        void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            this.onInitializeAccessibilityNodeInfo(mRecyclerView.mRecycler, mRecyclerView.mState, accessibilityNodeInfoCompat);
        }
        
        public void onInitializeAccessibilityNodeInfo(final Recycler recycler, final State state, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            if (this.mRecyclerView.canScrollVertically(-1) || this.mRecyclerView.canScrollHorizontally(-1)) {
                accessibilityNodeInfoCompat.addAction(8192);
                accessibilityNodeInfoCompat.setScrollable(true);
            }
            if (this.mRecyclerView.canScrollVertically(1) || this.mRecyclerView.canScrollHorizontally(1)) {
                accessibilityNodeInfoCompat.addAction(4096);
                accessibilityNodeInfoCompat.setScrollable(true);
            }
            accessibilityNodeInfoCompat.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(this.getRowCountForAccessibility(recycler, state), this.getColumnCountForAccessibility(recycler, state), this.isLayoutHierarchical(recycler, state), this.getSelectionModeForAccessibility(recycler, state)));
        }
        
        void onInitializeAccessibilityNodeInfoForItem(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt != null && !childViewHolderInt.isRemoved() && !this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                final RecyclerView mRecyclerView = this.mRecyclerView;
                this.onInitializeAccessibilityNodeInfoForItem(mRecyclerView.mRecycler, mRecyclerView.mState, view, accessibilityNodeInfoCompat);
            }
        }
        
        public void onInitializeAccessibilityNodeInfoForItem(final Recycler recycler, final State state, final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        }
        
        public View onInterceptFocusSearch(final View view, final int n) {
            return null;
        }
        
        public void onItemsAdded(final RecyclerView recyclerView, final int n, final int n2) {
        }
        
        public void onItemsChanged(final RecyclerView recyclerView) {
        }
        
        public void onItemsMoved(final RecyclerView recyclerView, final int n, final int n2, final int n3) {
        }
        
        public void onItemsRemoved(final RecyclerView recyclerView, final int n, final int n2) {
        }
        
        public void onItemsUpdated(final RecyclerView recyclerView, final int n, final int n2) {
        }
        
        public void onItemsUpdated(final RecyclerView recyclerView, final int n, final int n2, final Object o) {
            this.onItemsUpdated(recyclerView, n, n2);
        }
        
        public void onLayoutChildren(final Recycler recycler, final State state) {
            Log.e("RecyclerView", "You must override onLayoutChildren(Recycler recycler, State state) ");
        }
        
        public void onLayoutCompleted(final State state) {
        }
        
        public void onMeasure(final Recycler recycler, final State state, final int n, final int n2) {
            this.mRecyclerView.defaultOnMeasure(n, n2);
        }
        
        @Deprecated
        public boolean onRequestChildFocus(final RecyclerView recyclerView, final View view, final View view2) {
            return this.isSmoothScrolling() || recyclerView.isComputingLayout();
        }
        
        public boolean onRequestChildFocus(final RecyclerView recyclerView, final State state, final View view, final View view2) {
            return this.onRequestChildFocus(recyclerView, view, view2);
        }
        
        public void onRestoreInstanceState(final Parcelable parcelable) {
        }
        
        public Parcelable onSaveInstanceState() {
            return null;
        }
        
        public void onScrollStateChanged(final int n) {
        }
        
        void onSmoothScrollerStopped(final SmoothScroller smoothScroller) {
            if (this.mSmoothScroller == smoothScroller) {
                this.mSmoothScroller = null;
            }
        }
        
        boolean performAccessibilityAction(final int n, final Bundle bundle) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            return this.performAccessibilityAction(mRecyclerView.mRecycler, mRecyclerView.mState, n, bundle);
        }
        
        public boolean performAccessibilityAction(final Recycler recycler, final State state, int n, final Bundle bundle) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (mRecyclerView == null) {
                return false;
            }
            int n2 = 0;
            Label_0169: {
                Label_0167: {
                    int n3;
                    if (n != 4096) {
                        if (n != 8192) {
                            n = (n2 = 0);
                            break Label_0169;
                        }
                        if (mRecyclerView.canScrollVertically(-1)) {
                            n = -(this.getHeight() - this.getPaddingTop() - this.getPaddingBottom());
                        }
                        else {
                            n = 0;
                        }
                        n2 = n;
                        if (!this.mRecyclerView.canScrollHorizontally(-1)) {
                            break Label_0167;
                        }
                        n3 = -(this.getWidth() - this.getPaddingLeft() - this.getPaddingRight());
                    }
                    else {
                        if (mRecyclerView.canScrollVertically(1)) {
                            n = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
                        }
                        else {
                            n = 0;
                        }
                        n2 = n;
                        if (!this.mRecyclerView.canScrollHorizontally(1)) {
                            break Label_0167;
                        }
                        n3 = this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
                    }
                    final int n4 = n;
                    n = n3;
                    n2 = n4;
                    break Label_0169;
                }
                n = 0;
            }
            if (n2 == 0 && n == 0) {
                return false;
            }
            this.mRecyclerView.smoothScrollBy(n, n2, null, Integer.MIN_VALUE, true);
            return true;
        }
        
        boolean performAccessibilityActionForItem(final View view, final int n, final Bundle bundle) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            return this.performAccessibilityActionForItem(mRecyclerView.mRecycler, mRecyclerView.mState, view, n, bundle);
        }
        
        public boolean performAccessibilityActionForItem(final Recycler recycler, final State state, final View view, final int n, final Bundle bundle) {
            return false;
        }
        
        public void removeAndRecycleAllViews(final Recycler recycler) {
            for (int i = this.getChildCount() - 1; i >= 0; --i) {
                if (!RecyclerView.getChildViewHolderInt(this.getChildAt(i)).shouldIgnore()) {
                    this.removeAndRecycleViewAt(i, recycler);
                }
            }
        }
        
        void removeAndRecycleScrapInt(final Recycler recycler) {
            final int scrapCount = recycler.getScrapCount();
            for (int i = scrapCount - 1; i >= 0; --i) {
                final View scrapView = recycler.getScrapViewAt(i);
                final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(scrapView);
                if (!childViewHolderInt.shouldIgnore()) {
                    childViewHolderInt.setIsRecyclable(false);
                    if (childViewHolderInt.isTmpDetached()) {
                        this.mRecyclerView.removeDetachedView(scrapView, false);
                    }
                    final ItemAnimator mItemAnimator = this.mRecyclerView.mItemAnimator;
                    if (mItemAnimator != null) {
                        mItemAnimator.endAnimation(childViewHolderInt);
                    }
                    childViewHolderInt.setIsRecyclable(true);
                    recycler.quickRecycleScrapView(scrapView);
                }
            }
            recycler.clearScrap();
            if (scrapCount > 0) {
                this.mRecyclerView.invalidate();
            }
        }
        
        public void removeAndRecycleView(final View view, final Recycler recycler) {
            this.removeView(view);
            recycler.recycleView(view);
        }
        
        public void removeAndRecycleViewAt(final int n, final Recycler recycler) {
            final View child = this.getChildAt(n);
            this.removeViewAt(n);
            recycler.recycleView(child);
        }
        
        public boolean removeCallbacks(final Runnable runnable) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            return mRecyclerView != null && mRecyclerView.removeCallbacks(runnable);
        }
        
        public void removeView(final View view) {
            this.mChildHelper.removeView(view);
        }
        
        public void removeViewAt(final int n) {
            if (this.getChildAt(n) != null) {
                this.mChildHelper.removeViewAt(n);
            }
        }
        
        public boolean requestChildRectangleOnScreen(final RecyclerView recyclerView, final View view, final Rect rect, final boolean b) {
            return this.requestChildRectangleOnScreen(recyclerView, view, rect, b, false);
        }
        
        public boolean requestChildRectangleOnScreen(final RecyclerView recyclerView, final View view, final Rect rect, final boolean b, final boolean b2) {
            final int[] childRectangleOnScreenScrollAmount = this.getChildRectangleOnScreenScrollAmount(view, rect);
            final int n = childRectangleOnScreenScrollAmount[0];
            final int n2 = childRectangleOnScreenScrollAmount[1];
            if ((!b2 || this.isFocusedChildVisibleAfterScrolling(recyclerView, n, n2)) && (n != 0 || n2 != 0)) {
                if (b) {
                    recyclerView.scrollBy(n, n2);
                }
                else {
                    recyclerView.smoothScrollBy(n, n2);
                }
                return true;
            }
            return false;
        }
        
        public void requestLayout() {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (mRecyclerView != null) {
                mRecyclerView.requestLayout();
            }
        }
        
        public void requestSimpleAnimationsInNextLayout() {
            this.mRequestedSimpleAnimations = true;
        }
        
        public int scrollHorizontallyBy(final int n, final Recycler recycler, final State state) {
            return 0;
        }
        
        public void scrollToPosition(final int n) {
        }
        
        public int scrollVerticallyBy(final int n, final Recycler recycler, final State state) {
            return 0;
        }
        
        void setExactMeasureSpecsFrom(final RecyclerView recyclerView) {
            this.setMeasureSpecs(View$MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), 1073741824), View$MeasureSpec.makeMeasureSpec(recyclerView.getHeight(), 1073741824));
        }
        
        public final void setItemPrefetchEnabled(final boolean mItemPrefetchEnabled) {
            if (mItemPrefetchEnabled != this.mItemPrefetchEnabled) {
                this.mItemPrefetchEnabled = mItemPrefetchEnabled;
                this.mPrefetchMaxCountObserved = 0;
                final RecyclerView mRecyclerView = this.mRecyclerView;
                if (mRecyclerView != null) {
                    mRecyclerView.mRecycler.updateViewCacheSize();
                }
            }
        }
        
        void setMeasureSpecs(int n, final int n2) {
            this.mWidth = View$MeasureSpec.getSize(n);
            n = View$MeasureSpec.getMode(n);
            this.mWidthMode = n;
            if (n == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC) {
                this.mWidth = 0;
            }
            this.mHeight = View$MeasureSpec.getSize(n2);
            n = View$MeasureSpec.getMode(n2);
            if ((this.mHeightMode = n) == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC) {
                this.mHeight = 0;
            }
        }
        
        public void setMeasuredDimension(final int n, final int n2) {
            RecyclerView.access$300(this.mRecyclerView, n, n2);
        }
        
        public void setMeasuredDimension(final Rect rect, final int n, final int n2) {
            this.setMeasuredDimension(chooseSize(n, rect.width() + this.getPaddingLeft() + this.getPaddingRight(), this.getMinimumWidth()), chooseSize(n2, rect.height() + this.getPaddingTop() + this.getPaddingBottom(), this.getMinimumHeight()));
        }
        
        void setMeasuredDimensionFromChildren(final int n, final int n2) {
            final int childCount = this.getChildCount();
            if (childCount == 0) {
                this.mRecyclerView.defaultOnMeasure(n, n2);
                return;
            }
            int i = 0;
            int n3 = Integer.MIN_VALUE;
            int n5;
            int n4 = n5 = Integer.MAX_VALUE;
            int n6 = Integer.MIN_VALUE;
            while (i < childCount) {
                final View child = this.getChildAt(i);
                final Rect mTempRect = this.mRecyclerView.mTempRect;
                this.getDecoratedBoundsWithMargins(child, mTempRect);
                final int left = mTempRect.left;
                int n7;
                if (left < (n7 = n4)) {
                    n7 = left;
                }
                final int right = mTempRect.right;
                int n8;
                if (right > (n8 = n3)) {
                    n8 = right;
                }
                final int top = mTempRect.top;
                int n9;
                if (top < (n9 = n5)) {
                    n9 = top;
                }
                final int bottom = mTempRect.bottom;
                int n10;
                if (bottom > (n10 = n6)) {
                    n10 = bottom;
                }
                ++i;
                n3 = n8;
                n6 = n10;
                n4 = n7;
                n5 = n9;
            }
            this.mRecyclerView.mTempRect.set(n4, n5, n3, n6);
            this.setMeasuredDimension(this.mRecyclerView.mTempRect, n, n2);
        }
        
        void setRecyclerView(final RecyclerView mRecyclerView) {
            if (mRecyclerView == null) {
                this.mRecyclerView = null;
                this.mChildHelper = null;
                this.mWidth = 0;
                this.mHeight = 0;
            }
            else {
                this.mRecyclerView = mRecyclerView;
                this.mChildHelper = mRecyclerView.mChildHelper;
                this.mWidth = mRecyclerView.getWidth();
                this.mHeight = mRecyclerView.getHeight();
            }
            this.mWidthMode = 1073741824;
            this.mHeightMode = 1073741824;
        }
        
        boolean shouldMeasureChild(final View view, final int n, final int n2, final LayoutParams layoutParams) {
            return view.isLayoutRequested() || !this.mMeasurementCacheEnabled || !isMeasurementUpToDate(view.getWidth(), n, layoutParams.width) || !isMeasurementUpToDate(view.getHeight(), n2, layoutParams.height);
        }
        
        boolean shouldMeasureTwice() {
            return false;
        }
        
        boolean shouldReMeasureChild(final View view, final int n, final int n2, final LayoutParams layoutParams) {
            return !this.mMeasurementCacheEnabled || !isMeasurementUpToDate(view.getMeasuredWidth(), n, layoutParams.width) || !isMeasurementUpToDate(view.getMeasuredHeight(), n2, layoutParams.height);
        }
        
        public void smoothScrollToPosition(final RecyclerView recyclerView, final State state, final int n) {
            Log.e("RecyclerView", "You must override smoothScrollToPosition to support smooth scrolling");
        }
        
        public void startSmoothScroll(final SmoothScroller mSmoothScroller) {
            final SmoothScroller mSmoothScroller2 = this.mSmoothScroller;
            if (mSmoothScroller2 != null && mSmoothScroller != mSmoothScroller2 && mSmoothScroller2.isRunning()) {
                this.mSmoothScroller.stop();
            }
            (this.mSmoothScroller = mSmoothScroller).start(this.mRecyclerView, this);
        }
        
        void stopSmoothScroller() {
            final SmoothScroller mSmoothScroller = this.mSmoothScroller;
            if (mSmoothScroller != null) {
                mSmoothScroller.stop();
            }
        }
        
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
        
        public interface LayoutPrefetchRegistry
        {
            void addPosition(final int p0, final int p1);
        }
        
        public static class Properties
        {
            public int orientation;
            public boolean reverseLayout;
            public int spanCount;
            public boolean stackFromEnd;
        }
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        final Rect mDecorInsets;
        boolean mInsetsDirty;
        boolean mPendingInvalidate;
        ViewHolder mViewHolder;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$LayoutParams)layoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }
        
        @Deprecated
        public int getViewAdapterPosition() {
            return this.mViewHolder.getBindingAdapterPosition();
        }
        
        public int getViewLayoutPosition() {
            return this.mViewHolder.getLayoutPosition();
        }
        
        public boolean isItemChanged() {
            return this.mViewHolder.isUpdated();
        }
        
        public boolean isItemRemoved() {
            return this.mViewHolder.isRemoved();
        }
        
        public boolean isViewInvalid() {
            return this.mViewHolder.isInvalid();
        }
        
        public boolean viewNeedsUpdate() {
            return this.mViewHolder.needsUpdate();
        }
    }
    
    public interface OnChildAttachStateChangeListener
    {
        void onChildViewAttachedToWindow(final View p0);
        
        void onChildViewDetachedFromWindow(final View p0);
    }
    
    public abstract static class OnFlingListener
    {
        public abstract boolean onFling(final int p0, final int p1);
    }
    
    public interface OnItemTouchListener
    {
        boolean onInterceptTouchEvent(final RecyclerView p0, final MotionEvent p1);
        
        void onRequestDisallowInterceptTouchEvent(final boolean p0);
        
        void onTouchEvent(final RecyclerView p0, final MotionEvent p1);
    }
    
    public abstract static class OnScrollListener
    {
        public void onScrollStateChanged(final RecyclerView recyclerView, final int n) {
        }
        
        public void onScrolled(final RecyclerView recyclerView, final int n, final int n2) {
        }
    }
    
    public static class RecycledViewPool
    {
        private int mAttachCount;
        SparseArray<ScrapData> mScrap;
        
        public RecycledViewPool() {
            this.mScrap = (SparseArray<ScrapData>)new SparseArray();
            this.mAttachCount = 0;
        }
        
        private ScrapData getScrapDataForType(final int n) {
            ScrapData scrapData;
            if ((scrapData = (ScrapData)this.mScrap.get(n)) == null) {
                scrapData = new ScrapData();
                this.mScrap.put(n, (Object)scrapData);
            }
            return scrapData;
        }
        
        void attach() {
            ++this.mAttachCount;
        }
        
        public void clear() {
            for (int i = 0; i < this.mScrap.size(); ++i) {
                ((ScrapData)this.mScrap.valueAt(i)).mScrapHeap.clear();
            }
        }
        
        void detach() {
            --this.mAttachCount;
        }
        
        void factorInBindTime(final int n, final long n2) {
            final ScrapData scrapDataForType = this.getScrapDataForType(n);
            scrapDataForType.mBindRunningAverageNs = this.runningAverage(scrapDataForType.mBindRunningAverageNs, n2);
        }
        
        void factorInCreateTime(final int n, final long n2) {
            final ScrapData scrapDataForType = this.getScrapDataForType(n);
            scrapDataForType.mCreateRunningAverageNs = this.runningAverage(scrapDataForType.mCreateRunningAverageNs, n2);
        }
        
        public ViewHolder getRecycledView(int i) {
            final ScrapData scrapData = (ScrapData)this.mScrap.get(i);
            if (scrapData != null && !scrapData.mScrapHeap.isEmpty()) {
                final ArrayList<ViewHolder> mScrapHeap = scrapData.mScrapHeap;
                for (i = mScrapHeap.size() - 1; i >= 0; --i) {
                    if (!mScrapHeap.get(i).isAttachedToTransitionOverlay()) {
                        return mScrapHeap.remove(i);
                    }
                }
            }
            return null;
        }
        
        void onAdapterChanged(final Adapter adapter, final Adapter adapter2, final boolean b) {
            if (adapter != null) {
                this.detach();
            }
            if (!b && this.mAttachCount == 0) {
                this.clear();
            }
            if (adapter2 != null) {
                this.attach();
            }
        }
        
        public void putRecycledView(final ViewHolder e) {
            final int itemViewType = e.getItemViewType();
            final ArrayList<ViewHolder> mScrapHeap = this.getScrapDataForType(itemViewType).mScrapHeap;
            if (((ScrapData)this.mScrap.get(itemViewType)).mMaxScrap <= mScrapHeap.size()) {
                return;
            }
            e.resetInternal();
            mScrapHeap.add(e);
        }
        
        long runningAverage(final long n, final long n2) {
            if (n == 0L) {
                return n2;
            }
            return n / 4L * 3L + n2 / 4L;
        }
        
        boolean willBindInTime(final int n, final long n2, final long n3) {
            final long mBindRunningAverageNs = this.getScrapDataForType(n).mBindRunningAverageNs;
            return mBindRunningAverageNs == 0L || n2 + mBindRunningAverageNs < n3;
        }
        
        boolean willCreateInTime(final int n, final long n2, final long n3) {
            final long mCreateRunningAverageNs = this.getScrapDataForType(n).mCreateRunningAverageNs;
            return mCreateRunningAverageNs == 0L || n2 + mCreateRunningAverageNs < n3;
        }
        
        static class ScrapData
        {
            long mBindRunningAverageNs;
            long mCreateRunningAverageNs;
            int mMaxScrap;
            final ArrayList<ViewHolder> mScrapHeap;
            
            ScrapData() {
                this.mScrapHeap = new ArrayList<ViewHolder>();
                this.mMaxScrap = 5;
                this.mCreateRunningAverageNs = 0L;
                this.mBindRunningAverageNs = 0L;
            }
        }
    }
    
    public final class Recycler
    {
        final ArrayList<ViewHolder> mAttachedScrap;
        final ArrayList<ViewHolder> mCachedViews;
        ArrayList<ViewHolder> mChangedScrap;
        RecycledViewPool mRecyclerPool;
        private int mRequestedCacheMax;
        private final List<ViewHolder> mUnmodifiableAttachedScrap;
        private ViewCacheExtension mViewCacheExtension;
        int mViewCacheMax;
        
        public Recycler() {
            this.mAttachedScrap = new ArrayList<ViewHolder>();
            this.mChangedScrap = null;
            this.mCachedViews = new ArrayList<ViewHolder>();
            this.mUnmodifiableAttachedScrap = Collections.unmodifiableList((List<? extends ViewHolder>)this.mAttachedScrap);
            this.mRequestedCacheMax = 2;
            this.mViewCacheMax = 2;
        }
        
        private void attachAccessibilityDelegateOnBind(final ViewHolder viewHolder) {
            if (RecyclerView.this.isAccessibilityEnabled()) {
                final View itemView = viewHolder.itemView;
                if (ViewCompat.getImportantForAccessibility(itemView) == 0) {
                    ViewCompat.setImportantForAccessibility(itemView, 1);
                }
                final RecyclerViewAccessibilityDelegate mAccessibilityDelegate = RecyclerView.this.mAccessibilityDelegate;
                if (mAccessibilityDelegate == null) {
                    return;
                }
                final AccessibilityDelegateCompat itemDelegate = mAccessibilityDelegate.getItemDelegate();
                if (itemDelegate instanceof RecyclerViewAccessibilityDelegate.ItemDelegate) {
                    ((RecyclerViewAccessibilityDelegate.ItemDelegate)itemDelegate).saveOriginalDelegate(itemView);
                }
                ViewCompat.setAccessibilityDelegate(itemView, itemDelegate);
            }
        }
        
        private void invalidateDisplayListInt(final ViewGroup viewGroup, final boolean b) {
            for (int i = viewGroup.getChildCount() - 1; i >= 0; --i) {
                final View child = viewGroup.getChildAt(i);
                if (child instanceof ViewGroup) {
                    this.invalidateDisplayListInt((ViewGroup)child, true);
                }
            }
            if (!b) {
                return;
            }
            if (viewGroup.getVisibility() == 4) {
                viewGroup.setVisibility(0);
                viewGroup.setVisibility(4);
            }
            else {
                final int visibility = viewGroup.getVisibility();
                viewGroup.setVisibility(4);
                viewGroup.setVisibility(visibility);
            }
        }
        
        private void invalidateDisplayListInt(final ViewHolder viewHolder) {
            final View itemView = viewHolder.itemView;
            if (itemView instanceof ViewGroup) {
                this.invalidateDisplayListInt((ViewGroup)itemView, false);
            }
        }
        
        private boolean tryBindViewHolderByDeadline(final ViewHolder viewHolder, final int n, final int mPreLayoutPosition, long nanoTime) {
            viewHolder.mBindingAdapter = null;
            viewHolder.mOwnerRecyclerView = RecyclerView.this;
            final int itemViewType = viewHolder.getItemViewType();
            final long nanoTime2 = RecyclerView.this.getNanoTime();
            if (nanoTime != Long.MAX_VALUE && !this.mRecyclerPool.willBindInTime(itemViewType, nanoTime2, nanoTime)) {
                return false;
            }
            RecyclerView.this.mAdapter.bindViewHolder(viewHolder, n);
            nanoTime = RecyclerView.this.getNanoTime();
            this.mRecyclerPool.factorInBindTime(viewHolder.getItemViewType(), nanoTime - nanoTime2);
            this.attachAccessibilityDelegateOnBind(viewHolder);
            if (RecyclerView.this.mState.isPreLayout()) {
                viewHolder.mPreLayoutPosition = mPreLayoutPosition;
            }
            return true;
        }
        
        void addViewHolderToRecycledViewPool(final ViewHolder viewHolder, final boolean b) {
            RecyclerView.clearNestedRecyclerViewIfNotNested(viewHolder);
            final View itemView = viewHolder.itemView;
            final RecyclerViewAccessibilityDelegate mAccessibilityDelegate = RecyclerView.this.mAccessibilityDelegate;
            if (mAccessibilityDelegate != null) {
                final AccessibilityDelegateCompat itemDelegate = mAccessibilityDelegate.getItemDelegate();
                AccessibilityDelegateCompat andRemoveOriginalDelegateForItem;
                if (itemDelegate instanceof RecyclerViewAccessibilityDelegate.ItemDelegate) {
                    andRemoveOriginalDelegateForItem = ((RecyclerViewAccessibilityDelegate.ItemDelegate)itemDelegate).getAndRemoveOriginalDelegateForItem(itemView);
                }
                else {
                    andRemoveOriginalDelegateForItem = null;
                }
                ViewCompat.setAccessibilityDelegate(itemView, andRemoveOriginalDelegateForItem);
            }
            if (b) {
                this.dispatchViewRecycled(viewHolder);
            }
            viewHolder.mBindingAdapter = null;
            viewHolder.mOwnerRecyclerView = null;
            this.getRecycledViewPool().putRecycledView(viewHolder);
        }
        
        public void clear() {
            this.mAttachedScrap.clear();
            this.recycleAndClearCachedViews();
        }
        
        void clearOldPositions() {
            final int size = this.mCachedViews.size();
            final int n = 0;
            for (int i = 0; i < size; ++i) {
                this.mCachedViews.get(i).clearOldPosition();
            }
            for (int size2 = this.mAttachedScrap.size(), j = 0; j < size2; ++j) {
                this.mAttachedScrap.get(j).clearOldPosition();
            }
            final ArrayList<ViewHolder> mChangedScrap = this.mChangedScrap;
            if (mChangedScrap != null) {
                for (int size3 = mChangedScrap.size(), k = n; k < size3; ++k) {
                    this.mChangedScrap.get(k).clearOldPosition();
                }
            }
        }
        
        void clearScrap() {
            this.mAttachedScrap.clear();
            final ArrayList<ViewHolder> mChangedScrap = this.mChangedScrap;
            if (mChangedScrap != null) {
                mChangedScrap.clear();
            }
        }
        
        public int convertPreLayoutPositionToPostLayout(final int i) {
            if (i < 0 || i >= RecyclerView.this.mState.getItemCount()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("invalid position ");
                sb.append(i);
                sb.append(". State item count is ");
                sb.append(RecyclerView.this.mState.getItemCount());
                sb.append(RecyclerView.this.exceptionLabel());
                throw new IndexOutOfBoundsException(sb.toString());
            }
            if (!RecyclerView.this.mState.isPreLayout()) {
                return i;
            }
            return RecyclerView.this.mAdapterHelper.findPositionOffset(i);
        }
        
        void dispatchViewRecycled(final ViewHolder viewHolder) {
            final RecyclerListener mRecyclerListener = RecyclerView.this.mRecyclerListener;
            if (mRecyclerListener != null) {
                mRecyclerListener.onViewRecycled(viewHolder);
            }
            final Adapter mAdapter = RecyclerView.this.mAdapter;
            if (mAdapter != null) {
                mAdapter.onViewRecycled(viewHolder);
            }
            final RecyclerView this$0 = RecyclerView.this;
            if (this$0.mState != null) {
                this$0.mViewInfoStore.removeViewHolder(viewHolder);
            }
        }
        
        ViewHolder getChangedScrapViewForPosition(int i) {
            final ArrayList<ViewHolder> mChangedScrap = this.mChangedScrap;
            if (mChangedScrap != null) {
                final int size = mChangedScrap.size();
                if (size != 0) {
                    final int n = 0;
                    for (int j = 0; j < size; ++j) {
                        final ViewHolder viewHolder = this.mChangedScrap.get(j);
                        if (!viewHolder.wasReturnedFromScrap() && viewHolder.getLayoutPosition() == i) {
                            viewHolder.addFlags(32);
                            return viewHolder;
                        }
                    }
                    if (RecyclerView.this.mAdapter.hasStableIds()) {
                        i = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
                        if (i > 0 && i < RecyclerView.this.mAdapter.getItemCount()) {
                            final long itemId = RecyclerView.this.mAdapter.getItemId(i);
                            ViewHolder viewHolder2;
                            for (i = n; i < size; ++i) {
                                viewHolder2 = this.mChangedScrap.get(i);
                                if (!viewHolder2.wasReturnedFromScrap() && viewHolder2.getItemId() == itemId) {
                                    viewHolder2.addFlags(32);
                                    return viewHolder2;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
        
        RecycledViewPool getRecycledViewPool() {
            if (this.mRecyclerPool == null) {
                this.mRecyclerPool = new RecycledViewPool();
            }
            return this.mRecyclerPool;
        }
        
        int getScrapCount() {
            return this.mAttachedScrap.size();
        }
        
        public List<ViewHolder> getScrapList() {
            return this.mUnmodifiableAttachedScrap;
        }
        
        ViewHolder getScrapOrCachedViewForId(final long n, final int n2, final boolean b) {
            for (int i = this.mAttachedScrap.size() - 1; i >= 0; --i) {
                final ViewHolder viewHolder = this.mAttachedScrap.get(i);
                if (viewHolder.getItemId() == n && !viewHolder.wasReturnedFromScrap()) {
                    if (n2 == viewHolder.getItemViewType()) {
                        viewHolder.addFlags(32);
                        if (viewHolder.isRemoved() && !RecyclerView.this.mState.isPreLayout()) {
                            viewHolder.setFlags(2, 14);
                        }
                        return viewHolder;
                    }
                    if (!b) {
                        this.mAttachedScrap.remove(i);
                        RecyclerView.this.removeDetachedView(viewHolder.itemView, false);
                        this.quickRecycleScrapView(viewHolder.itemView);
                    }
                }
            }
            for (int j = this.mCachedViews.size() - 1; j >= 0; --j) {
                final ViewHolder viewHolder2 = this.mCachedViews.get(j);
                if (viewHolder2.getItemId() == n && !viewHolder2.isAttachedToTransitionOverlay()) {
                    if (n2 == viewHolder2.getItemViewType()) {
                        if (!b) {
                            this.mCachedViews.remove(j);
                        }
                        return viewHolder2;
                    }
                    if (!b) {
                        this.recycleCachedViewAt(j);
                        return null;
                    }
                }
            }
            return null;
        }
        
        ViewHolder getScrapOrHiddenOrCachedHolderForPosition(int indexOfChild, final boolean b) {
            final int size = this.mAttachedScrap.size();
            final int n = 0;
            for (int i = 0; i < size; ++i) {
                final ViewHolder viewHolder = this.mAttachedScrap.get(i);
                if (!viewHolder.wasReturnedFromScrap() && viewHolder.getLayoutPosition() == indexOfChild && !viewHolder.isInvalid() && (RecyclerView.this.mState.mInPreLayout || !viewHolder.isRemoved())) {
                    viewHolder.addFlags(32);
                    return viewHolder;
                }
            }
            if (!b) {
                final View hiddenNonRemovedView = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(indexOfChild);
                if (hiddenNonRemovedView != null) {
                    final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(hiddenNonRemovedView);
                    RecyclerView.this.mChildHelper.unhide(hiddenNonRemovedView);
                    indexOfChild = RecyclerView.this.mChildHelper.indexOfChild(hiddenNonRemovedView);
                    if (indexOfChild != -1) {
                        RecyclerView.this.mChildHelper.detachViewFromParent(indexOfChild);
                        this.scrapView(hiddenNonRemovedView);
                        childViewHolderInt.addFlags(8224);
                        return childViewHolderInt;
                    }
                    final StringBuilder sb = new StringBuilder();
                    sb.append("layout index should not be -1 after unhiding a view:");
                    sb.append(childViewHolderInt);
                    sb.append(RecyclerView.this.exceptionLabel());
                    throw new IllegalStateException(sb.toString());
                }
            }
            for (int size2 = this.mCachedViews.size(), j = n; j < size2; ++j) {
                final ViewHolder viewHolder2 = this.mCachedViews.get(j);
                if (!viewHolder2.isInvalid() && viewHolder2.getLayoutPosition() == indexOfChild && !viewHolder2.isAttachedToTransitionOverlay()) {
                    if (!b) {
                        this.mCachedViews.remove(j);
                    }
                    return viewHolder2;
                }
            }
            return null;
        }
        
        View getScrapViewAt(final int index) {
            return this.mAttachedScrap.get(index).itemView;
        }
        
        public View getViewForPosition(final int n) {
            return this.getViewForPosition(n, false);
        }
        
        View getViewForPosition(final int n, final boolean b) {
            return this.tryGetViewHolderForPositionByDeadline(n, b, Long.MAX_VALUE).itemView;
        }
        
        void markItemDecorInsetsDirty() {
            for (int size = this.mCachedViews.size(), i = 0; i < size; ++i) {
                final LayoutParams layoutParams = (LayoutParams)this.mCachedViews.get(i).itemView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.mInsetsDirty = true;
                }
            }
        }
        
        void markKnownViewsInvalid() {
            for (int size = this.mCachedViews.size(), i = 0; i < size; ++i) {
                final ViewHolder viewHolder = this.mCachedViews.get(i);
                if (viewHolder != null) {
                    viewHolder.addFlags(6);
                    viewHolder.addChangePayload(null);
                }
            }
            final Adapter mAdapter = RecyclerView.this.mAdapter;
            if (mAdapter == null || !mAdapter.hasStableIds()) {
                this.recycleAndClearCachedViews();
            }
        }
        
        void offsetPositionRecordsForInsert(final int n, final int n2) {
            for (int size = this.mCachedViews.size(), i = 0; i < size; ++i) {
                final ViewHolder viewHolder = this.mCachedViews.get(i);
                if (viewHolder != null && viewHolder.mPosition >= n) {
                    viewHolder.offsetPosition(n2, true);
                }
            }
        }
        
        void offsetPositionRecordsForMove(final int n, final int n2) {
            int n3;
            int n4;
            int n5;
            if (n < n2) {
                n3 = -1;
                n4 = n;
                n5 = n2;
            }
            else {
                n3 = 1;
                n5 = n;
                n4 = n2;
            }
            for (int size = this.mCachedViews.size(), i = 0; i < size; ++i) {
                final ViewHolder viewHolder = this.mCachedViews.get(i);
                if (viewHolder != null) {
                    final int mPosition = viewHolder.mPosition;
                    if (mPosition >= n4) {
                        if (mPosition <= n5) {
                            if (mPosition == n) {
                                viewHolder.offsetPosition(n2 - n, false);
                            }
                            else {
                                viewHolder.offsetPosition(n3, false);
                            }
                        }
                    }
                }
            }
        }
        
        void offsetPositionRecordsForRemove(final int n, final int n2, final boolean b) {
            for (int i = this.mCachedViews.size() - 1; i >= 0; --i) {
                final ViewHolder viewHolder = this.mCachedViews.get(i);
                if (viewHolder != null) {
                    final int mPosition = viewHolder.mPosition;
                    if (mPosition >= n + n2) {
                        viewHolder.offsetPosition(-n2, b);
                    }
                    else if (mPosition >= n) {
                        viewHolder.addFlags(8);
                        this.recycleCachedViewAt(i);
                    }
                }
            }
        }
        
        void onAdapterChanged(final Adapter adapter, final Adapter adapter2, final boolean b) {
            this.clear();
            this.getRecycledViewPool().onAdapterChanged(adapter, adapter2, b);
        }
        
        void quickRecycleScrapView(final View view) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            childViewHolderInt.mScrapContainer = null;
            childViewHolderInt.mInChangeScrap = false;
            childViewHolderInt.clearReturnedFromScrapFlag();
            this.recycleViewHolderInternal(childViewHolderInt);
        }
        
        void recycleAndClearCachedViews() {
            for (int i = this.mCachedViews.size() - 1; i >= 0; --i) {
                this.recycleCachedViewAt(i);
            }
            this.mCachedViews.clear();
            if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
            }
        }
        
        void recycleCachedViewAt(final int n) {
            this.addViewHolderToRecycledViewPool(this.mCachedViews.get(n), true);
            this.mCachedViews.remove(n);
        }
        
        public void recycleView(final View view) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(view, false);
            }
            if (childViewHolderInt.isScrap()) {
                childViewHolderInt.unScrap();
            }
            else if (childViewHolderInt.wasReturnedFromScrap()) {
                childViewHolderInt.clearReturnedFromScrapFlag();
            }
            this.recycleViewHolderInternal(childViewHolderInt);
            if (RecyclerView.this.mItemAnimator != null && !childViewHolderInt.isRecyclable()) {
                RecyclerView.this.mItemAnimator.endAnimation(childViewHolderInt);
            }
        }
        
        void recycleViewHolderInternal(final ViewHolder viewHolder) {
            final boolean scrap = viewHolder.isScrap();
            boolean b = false;
            final int n = 0;
            final int n2 = 1;
            if (scrap || viewHolder.itemView.getParent() != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Scrapped or attached views may not be recycled. isScrap:");
                sb.append(viewHolder.isScrap());
                sb.append(" isAttached:");
                if (viewHolder.itemView.getParent() != null) {
                    b = true;
                }
                sb.append(b);
                sb.append(RecyclerView.this.exceptionLabel());
                throw new IllegalArgumentException(sb.toString());
            }
            if (viewHolder.isTmpDetached()) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Tmp detached view should be removed from RecyclerView before it can be recycled: ");
                sb2.append(viewHolder);
                sb2.append(RecyclerView.this.exceptionLabel());
                throw new IllegalArgumentException(sb2.toString());
            }
            if (!viewHolder.shouldIgnore()) {
                final boolean doesTransientStatePreventRecycling = viewHolder.doesTransientStatePreventRecycling();
                final Adapter mAdapter = RecyclerView.this.mAdapter;
                int n4;
                int n5;
                if ((mAdapter == null || !doesTransientStatePreventRecycling || !mAdapter.onFailedToRecycleView(viewHolder)) && !viewHolder.isRecyclable()) {
                    final int n3 = 0;
                    n4 = n;
                    n5 = n3;
                }
                else {
                    if (this.mViewCacheMax > 0 && !viewHolder.hasAnyOfTheFlags(526)) {
                        int size;
                        final int n6 = size = this.mCachedViews.size();
                        if (n6 >= this.mViewCacheMax && (size = n6) > 0) {
                            this.recycleCachedViewAt(0);
                            size = n6 - 1;
                        }
                        int index = size;
                        if (RecyclerView.ALLOW_THREAD_GAP_WORK && (index = size) > 0) {
                            index = size;
                            if (!RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(viewHolder.mPosition)) {
                                --size;
                                while (size >= 0 && RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(this.mCachedViews.get(size).mPosition)) {
                                    --size;
                                }
                                index = size + 1;
                            }
                        }
                        this.mCachedViews.add(index, viewHolder);
                        n4 = 1;
                    }
                    else {
                        n4 = 0;
                    }
                    if (n4 == 0) {
                        this.addViewHolderToRecycledViewPool(viewHolder, true);
                        n5 = n2;
                    }
                    else {
                        n5 = 0;
                    }
                }
                RecyclerView.this.mViewInfoStore.removeViewHolder(viewHolder);
                if (n4 == 0 && n5 == 0 && doesTransientStatePreventRecycling) {
                    viewHolder.mBindingAdapter = null;
                    viewHolder.mOwnerRecyclerView = null;
                }
                return;
            }
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle.");
            sb3.append(RecyclerView.this.exceptionLabel());
            throw new IllegalArgumentException(sb3.toString());
        }
        
        void scrapView(final View view) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (!childViewHolderInt.hasAnyOfTheFlags(12) && childViewHolderInt.isUpdated() && !RecyclerView.this.canReuseUpdatedViewHolder(childViewHolderInt)) {
                if (this.mChangedScrap == null) {
                    this.mChangedScrap = new ArrayList<ViewHolder>();
                }
                childViewHolderInt.setScrapContainer(this, true);
                this.mChangedScrap.add(childViewHolderInt);
            }
            else {
                if (childViewHolderInt.isInvalid() && !childViewHolderInt.isRemoved() && !RecyclerView.this.mAdapter.hasStableIds()) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool.");
                    sb.append(RecyclerView.this.exceptionLabel());
                    throw new IllegalArgumentException(sb.toString());
                }
                childViewHolderInt.setScrapContainer(this, false);
                this.mAttachedScrap.add(childViewHolderInt);
            }
        }
        
        public void setViewCacheSize(final int mRequestedCacheMax) {
            this.mRequestedCacheMax = mRequestedCacheMax;
            this.updateViewCacheSize();
        }
        
        ViewHolder tryGetViewHolderForPositionByDeadline(final int n, final boolean b, final long n2) {
            if (n >= 0 && n < RecyclerView.this.mState.getItemCount()) {
                final boolean preLayout = RecyclerView.this.mState.isPreLayout();
                final boolean b2 = true;
                ViewHolder viewHolder = null;
                int n3 = 0;
                Label_0070: {
                    if (preLayout) {
                        final ViewHolder changedScrapViewForPosition = this.getChangedScrapViewForPosition(n);
                        if ((viewHolder = changedScrapViewForPosition) != null) {
                            n3 = 1;
                            viewHolder = changedScrapViewForPosition;
                            break Label_0070;
                        }
                    }
                    else {
                        viewHolder = null;
                    }
                    n3 = 0;
                }
                ViewHolder scrapOrHiddenOrCachedHolderForPosition = viewHolder;
                int n4 = n3;
                if (viewHolder == null) {
                    final ViewHolder viewHolder2 = scrapOrHiddenOrCachedHolderForPosition = this.getScrapOrHiddenOrCachedHolderForPosition(n, b);
                    n4 = n3;
                    if (viewHolder2 != null) {
                        if (!this.validateViewHolderForOffsetPosition(viewHolder2)) {
                            if (!b) {
                                viewHolder2.addFlags(4);
                                if (viewHolder2.isScrap()) {
                                    RecyclerView.this.removeDetachedView(viewHolder2.itemView, false);
                                    viewHolder2.unScrap();
                                }
                                else if (viewHolder2.wasReturnedFromScrap()) {
                                    viewHolder2.clearReturnedFromScrapFlag();
                                }
                                this.recycleViewHolderInternal(viewHolder2);
                            }
                            scrapOrHiddenOrCachedHolderForPosition = null;
                            n4 = n3;
                        }
                        else {
                            n4 = 1;
                            scrapOrHiddenOrCachedHolderForPosition = viewHolder2;
                        }
                    }
                }
                ViewHolder viewHolder3 = scrapOrHiddenOrCachedHolderForPosition;
                int n5 = n4;
                int n6 = 0;
                ViewHolder viewHolder5 = null;
                Label_0747: {
                    if (scrapOrHiddenOrCachedHolderForPosition == null) {
                        final int positionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(n);
                        if (positionOffset < 0 || positionOffset >= RecyclerView.this.mAdapter.getItemCount()) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Inconsistency detected. Invalid item position ");
                            sb.append(n);
                            sb.append("(offset:");
                            sb.append(positionOffset);
                            sb.append(").state:");
                            sb.append(RecyclerView.this.mState.getItemCount());
                            sb.append(RecyclerView.this.exceptionLabel());
                            throw new IndexOutOfBoundsException(sb.toString());
                        }
                        final int itemViewType = RecyclerView.this.mAdapter.getItemViewType(positionOffset);
                        ViewHolder scrapOrCachedViewForId = scrapOrHiddenOrCachedHolderForPosition;
                        n6 = n4;
                        if (RecyclerView.this.mAdapter.hasStableIds()) {
                            final ViewHolder viewHolder4 = scrapOrCachedViewForId = this.getScrapOrCachedViewForId(RecyclerView.this.mAdapter.getItemId(positionOffset), itemViewType, b);
                            n6 = n4;
                            if (viewHolder4 != null) {
                                viewHolder4.mPosition = positionOffset;
                                n6 = 1;
                                scrapOrCachedViewForId = viewHolder4;
                            }
                        }
                        Object childViewHolder;
                        if ((childViewHolder = scrapOrCachedViewForId) == null) {
                            final ViewCacheExtension mViewCacheExtension = this.mViewCacheExtension;
                            childViewHolder = scrapOrCachedViewForId;
                            if (mViewCacheExtension != null) {
                                final View viewForPositionAndType = mViewCacheExtension.getViewForPositionAndType(this, n, itemViewType);
                                childViewHolder = scrapOrCachedViewForId;
                                if (viewForPositionAndType != null) {
                                    childViewHolder = RecyclerView.this.getChildViewHolder(viewForPositionAndType);
                                    if (childViewHolder == null) {
                                        final StringBuilder sb2 = new StringBuilder();
                                        sb2.append("getViewForPositionAndType returned a view which does not have a ViewHolder");
                                        sb2.append(RecyclerView.this.exceptionLabel());
                                        throw new IllegalArgumentException(sb2.toString());
                                    }
                                    if (((ViewHolder)childViewHolder).shouldIgnore()) {
                                        final StringBuilder sb3 = new StringBuilder();
                                        sb3.append("getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view.");
                                        sb3.append(RecyclerView.this.exceptionLabel());
                                        throw new IllegalArgumentException(sb3.toString());
                                    }
                                }
                            }
                        }
                        ViewHolder recycledView;
                        if ((recycledView = (ViewHolder)childViewHolder) == null) {
                            recycledView = this.getRecycledViewPool().getRecycledView(itemViewType);
                            if (recycledView != null) {
                                recycledView.resetInternal();
                                if (RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST) {
                                    this.invalidateDisplayListInt(recycledView);
                                }
                            }
                        }
                        viewHolder3 = recycledView;
                        n5 = n6;
                        if (recycledView == null) {
                            final long nanoTime = RecyclerView.this.getNanoTime();
                            if (n2 != Long.MAX_VALUE && !this.mRecyclerPool.willCreateInTime(itemViewType, nanoTime, n2)) {
                                return null;
                            }
                            final RecyclerView this$0 = RecyclerView.this;
                            viewHolder5 = this$0.mAdapter.createViewHolder(this$0, itemViewType);
                            if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                                final RecyclerView nestedRecyclerView = RecyclerView.findNestedRecyclerView(viewHolder5.itemView);
                                if (nestedRecyclerView != null) {
                                    viewHolder5.mNestedRecyclerView = new WeakReference<RecyclerView>(nestedRecyclerView);
                                }
                            }
                            this.mRecyclerPool.factorInCreateTime(itemViewType, RecyclerView.this.getNanoTime() - nanoTime);
                            break Label_0747;
                        }
                    }
                    viewHolder5 = viewHolder3;
                    n6 = n5;
                }
                if (n6 != 0 && !RecyclerView.this.mState.isPreLayout() && viewHolder5.hasAnyOfTheFlags(8192)) {
                    viewHolder5.setFlags(0, 8192);
                    if (RecyclerView.this.mState.mRunSimpleAnimations) {
                        final int buildAdapterChangeFlagsForAnimations = ItemAnimator.buildAdapterChangeFlagsForAnimations(viewHolder5);
                        final RecyclerView this$2 = RecyclerView.this;
                        RecyclerView.this.recordAnimationInfoIfBouncedHiddenView(viewHolder5, this$2.mItemAnimator.recordPreLayoutInformation(this$2.mState, viewHolder5, buildAdapterChangeFlagsForAnimations | 0x1000, viewHolder5.getUnmodifiedPayloads()));
                    }
                }
                boolean tryBindViewHolderByDeadline = false;
                Label_0932: {
                    if (RecyclerView.this.mState.isPreLayout() && viewHolder5.isBound()) {
                        viewHolder5.mPreLayoutPosition = n;
                    }
                    else if (!viewHolder5.isBound() || viewHolder5.needsUpdate() || viewHolder5.isInvalid()) {
                        tryBindViewHolderByDeadline = this.tryBindViewHolderByDeadline(viewHolder5, RecyclerView.this.mAdapterHelper.findPositionOffset(n), n, n2);
                        break Label_0932;
                    }
                    tryBindViewHolderByDeadline = false;
                }
                final ViewGroup$LayoutParams layoutParams = viewHolder5.itemView.getLayoutParams();
                LayoutParams layoutParams2;
                if (layoutParams == null) {
                    layoutParams2 = (LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
                    viewHolder5.itemView.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
                }
                else if (!RecyclerView.this.checkLayoutParams(layoutParams)) {
                    layoutParams2 = (LayoutParams)RecyclerView.this.generateLayoutParams(layoutParams);
                    viewHolder5.itemView.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
                }
                else {
                    layoutParams2 = (LayoutParams)layoutParams;
                }
                layoutParams2.mViewHolder = viewHolder5;
                layoutParams2.mPendingInvalidate = (n6 != 0 && tryBindViewHolderByDeadline && b2);
                return viewHolder5;
            }
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("Invalid item position ");
            sb4.append(n);
            sb4.append("(");
            sb4.append(n);
            sb4.append("). Item count:");
            sb4.append(RecyclerView.this.mState.getItemCount());
            sb4.append(RecyclerView.this.exceptionLabel());
            throw new IndexOutOfBoundsException(sb4.toString());
        }
        
        void unscrapView(final ViewHolder viewHolder) {
            if (viewHolder.mInChangeScrap) {
                this.mChangedScrap.remove(viewHolder);
            }
            else {
                this.mAttachedScrap.remove(viewHolder);
            }
            viewHolder.mScrapContainer = null;
            viewHolder.mInChangeScrap = false;
            viewHolder.clearReturnedFromScrapFlag();
        }
        
        void updateViewCacheSize() {
            final LayoutManager mLayout = RecyclerView.this.mLayout;
            int mPrefetchMaxCountObserved;
            if (mLayout != null) {
                mPrefetchMaxCountObserved = mLayout.mPrefetchMaxCountObserved;
            }
            else {
                mPrefetchMaxCountObserved = 0;
            }
            this.mViewCacheMax = this.mRequestedCacheMax + mPrefetchMaxCountObserved;
            for (int n = this.mCachedViews.size() - 1; n >= 0 && this.mCachedViews.size() > this.mViewCacheMax; --n) {
                this.recycleCachedViewAt(n);
            }
        }
        
        boolean validateViewHolderForOffsetPosition(final ViewHolder obj) {
            if (obj.isRemoved()) {
                return RecyclerView.this.mState.isPreLayout();
            }
            final int mPosition = obj.mPosition;
            if (mPosition < 0 || mPosition >= RecyclerView.this.mAdapter.getItemCount()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Inconsistency detected. Invalid view holder adapter position");
                sb.append(obj);
                sb.append(RecyclerView.this.exceptionLabel());
                throw new IndexOutOfBoundsException(sb.toString());
            }
            final boolean preLayout = RecyclerView.this.mState.isPreLayout();
            boolean b = false;
            if (!preLayout && RecyclerView.this.mAdapter.getItemViewType(obj.mPosition) != obj.getItemViewType()) {
                return false;
            }
            if (RecyclerView.this.mAdapter.hasStableIds()) {
                if (obj.getItemId() == RecyclerView.this.mAdapter.getItemId(obj.mPosition)) {
                    b = true;
                }
                return b;
            }
            return true;
        }
        
        void viewRangeUpdate(final int n, final int n2) {
            for (int i = this.mCachedViews.size() - 1; i >= 0; --i) {
                final ViewHolder viewHolder = this.mCachedViews.get(i);
                if (viewHolder != null) {
                    final int mPosition = viewHolder.mPosition;
                    if (mPosition >= n && mPosition < n2 + n) {
                        viewHolder.addFlags(2);
                        this.recycleCachedViewAt(i);
                    }
                }
            }
        }
    }
    
    public interface RecyclerListener
    {
        void onViewRecycled(final ViewHolder p0);
    }
    
    private class RecyclerViewDataObserver extends AdapterDataObserver
    {
        RecyclerViewDataObserver() {
        }
        
        @Override
        public void onChanged() {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            final RecyclerView this$0 = RecyclerView.this;
            this$0.processDataSetCompletelyChanged(this$0.mState.mStructureChanged = true);
            if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
                RecyclerView.this.requestLayout();
            }
        }
        
        @Override
        public void onItemRangeChanged(final int n, final int n2, final Object o) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(n, n2, o)) {
                this.triggerUpdateProcessor();
            }
        }
        
        @Override
        public void onItemRangeInserted(final int n, final int n2) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(n, n2)) {
                this.triggerUpdateProcessor();
            }
        }
        
        @Override
        public void onItemRangeMoved(final int n, final int n2, final int n3) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(n, n2, n3)) {
                this.triggerUpdateProcessor();
            }
        }
        
        @Override
        public void onItemRangeRemoved(final int n, final int n2) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(n, n2)) {
                this.triggerUpdateProcessor();
            }
        }
        
        void triggerUpdateProcessor() {
            if (RecyclerView.POST_UPDATES_ON_ANIMATION) {
                final RecyclerView this$0 = RecyclerView.this;
                if (this$0.mHasFixedSize && this$0.mIsAttached) {
                    ViewCompat.postOnAnimation((View)this$0, this$0.mUpdateChildViewsRunnable);
                    return;
                }
            }
            final RecyclerView this$2 = RecyclerView.this;
            this$2.mAdapterUpdateDuringMeasure = true;
            this$2.requestLayout();
        }
    }
    
    public static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        Parcelable mLayoutState;
        
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
        
        SavedState(final Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            if (classLoader == null) {
                classLoader = LayoutManager.class.getClassLoader();
            }
            this.mLayoutState = parcel.readParcelable(classLoader);
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        void copyFrom(final SavedState savedState) {
            this.mLayoutState = savedState.mLayoutState;
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeParcelable(this.mLayoutState, 0);
        }
    }
    
    public abstract static class SmoothScroller
    {
        private LayoutManager mLayoutManager;
        private boolean mPendingInitialRun;
        private RecyclerView mRecyclerView;
        private final Action mRecyclingAction;
        private boolean mRunning;
        private boolean mStarted;
        private int mTargetPosition;
        private View mTargetView;
        
        public SmoothScroller() {
            this.mTargetPosition = -1;
            this.mRecyclingAction = new Action(0, 0);
        }
        
        public PointF computeScrollVectorForPosition(final int n) {
            final LayoutManager layoutManager = this.getLayoutManager();
            if (layoutManager instanceof ScrollVectorProvider) {
                return ((ScrollVectorProvider)layoutManager).computeScrollVectorForPosition(n);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("You should override computeScrollVectorForPosition when the LayoutManager does not implement ");
            sb.append(ScrollVectorProvider.class.getCanonicalName());
            Log.w("RecyclerView", sb.toString());
            return null;
        }
        
        public View findViewByPosition(final int n) {
            return this.mRecyclerView.mLayout.findViewByPosition(n);
        }
        
        public int getChildCount() {
            return this.mRecyclerView.mLayout.getChildCount();
        }
        
        public int getChildPosition(final View view) {
            return this.mRecyclerView.getChildLayoutPosition(view);
        }
        
        public LayoutManager getLayoutManager() {
            return this.mLayoutManager;
        }
        
        public int getTargetPosition() {
            return this.mTargetPosition;
        }
        
        public boolean isPendingInitialRun() {
            return this.mPendingInitialRun;
        }
        
        public boolean isRunning() {
            return this.mRunning;
        }
        
        protected void normalize(final PointF pointF) {
            final float x = pointF.x;
            final float y = pointF.y;
            final float n = (float)Math.sqrt(x * x + y * y);
            pointF.x /= n;
            pointF.y /= n;
        }
        
        void onAnimation(final int n, final int n2) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (this.mTargetPosition == -1 || mRecyclerView == null) {
                this.stop();
            }
            if (this.mPendingInitialRun && this.mTargetView == null && this.mLayoutManager != null) {
                final PointF computeScrollVectorForPosition = this.computeScrollVectorForPosition(this.mTargetPosition);
                if (computeScrollVectorForPosition != null && (computeScrollVectorForPosition.x != 0.0f || computeScrollVectorForPosition.y != 0.0f)) {
                    mRecyclerView.scrollStep((int)Math.signum(computeScrollVectorForPosition.x), (int)Math.signum(computeScrollVectorForPosition.y), null);
                }
            }
            this.mPendingInitialRun = false;
            final View mTargetView = this.mTargetView;
            if (mTargetView != null) {
                if (this.getChildPosition(mTargetView) == this.mTargetPosition) {
                    this.onTargetFound(this.mTargetView, mRecyclerView.mState, this.mRecyclingAction);
                    this.mRecyclingAction.runIfNecessary(mRecyclerView);
                    this.stop();
                }
                else {
                    Log.e("RecyclerView", "Passed over target position while smooth scrolling.");
                    this.mTargetView = null;
                }
            }
            if (this.mRunning) {
                this.onSeekTargetStep(n, n2, mRecyclerView.mState, this.mRecyclingAction);
                final boolean hasJumpTarget = this.mRecyclingAction.hasJumpTarget();
                this.mRecyclingAction.runIfNecessary(mRecyclerView);
                if (hasJumpTarget && this.mRunning) {
                    this.mPendingInitialRun = true;
                    mRecyclerView.mViewFlinger.postOnAnimation();
                }
            }
        }
        
        protected void onChildAttachedToWindow(final View mTargetView) {
            if (this.getChildPosition(mTargetView) == this.getTargetPosition()) {
                this.mTargetView = mTargetView;
            }
        }
        
        protected abstract void onSeekTargetStep(final int p0, final int p1, final State p2, final Action p3);
        
        protected abstract void onStart();
        
        protected abstract void onStop();
        
        protected abstract void onTargetFound(final View p0, final State p1, final Action p2);
        
        public void setTargetPosition(final int mTargetPosition) {
            this.mTargetPosition = mTargetPosition;
        }
        
        void start(final RecyclerView mRecyclerView, final LayoutManager mLayoutManager) {
            mRecyclerView.mViewFlinger.stop();
            if (this.mStarted) {
                final StringBuilder sb = new StringBuilder();
                sb.append("An instance of ");
                sb.append(this.getClass().getSimpleName());
                sb.append(" was started more than once. Each instance of");
                sb.append(this.getClass().getSimpleName());
                sb.append(" is intended to only be used once. You should create a new instance for each use.");
                Log.w("RecyclerView", sb.toString());
            }
            this.mRecyclerView = mRecyclerView;
            this.mLayoutManager = mLayoutManager;
            final int mTargetPosition = this.mTargetPosition;
            if (mTargetPosition != -1) {
                mRecyclerView.mState.mTargetPosition = mTargetPosition;
                this.mRunning = true;
                this.mPendingInitialRun = true;
                this.mTargetView = this.findViewByPosition(this.getTargetPosition());
                this.onStart();
                this.mRecyclerView.mViewFlinger.postOnAnimation();
                this.mStarted = true;
                return;
            }
            throw new IllegalArgumentException("Invalid target position");
        }
        
        protected final void stop() {
            if (!this.mRunning) {
                return;
            }
            this.mRunning = false;
            this.onStop();
            this.mRecyclerView.mState.mTargetPosition = -1;
            this.mTargetView = null;
            this.mTargetPosition = -1;
            this.mPendingInitialRun = false;
            this.mLayoutManager.onSmoothScrollerStopped(this);
            this.mLayoutManager = null;
            this.mRecyclerView = null;
        }
        
        public static class Action
        {
            private boolean mChanged;
            private int mConsecutiveUpdates;
            private int mDuration;
            private int mDx;
            private int mDy;
            private Interpolator mInterpolator;
            private int mJumpToPosition;
            
            public Action(final int n, final int n2) {
                this(n, n2, Integer.MIN_VALUE, null);
            }
            
            public Action(final int mDx, final int mDy, final int mDuration, final Interpolator mInterpolator) {
                this.mJumpToPosition = -1;
                this.mChanged = false;
                this.mConsecutiveUpdates = 0;
                this.mDx = mDx;
                this.mDy = mDy;
                this.mDuration = mDuration;
                this.mInterpolator = mInterpolator;
            }
            
            private void validate() {
                if (this.mInterpolator != null && this.mDuration < 1) {
                    throw new IllegalStateException("If you provide an interpolator, you must set a positive duration");
                }
                if (this.mDuration >= 1) {
                    return;
                }
                throw new IllegalStateException("Scroll duration must be a positive number");
            }
            
            boolean hasJumpTarget() {
                return this.mJumpToPosition >= 0;
            }
            
            public void jumpTo(final int mJumpToPosition) {
                this.mJumpToPosition = mJumpToPosition;
            }
            
            void runIfNecessary(final RecyclerView recyclerView) {
                final int mJumpToPosition = this.mJumpToPosition;
                if (mJumpToPosition >= 0) {
                    this.mJumpToPosition = -1;
                    recyclerView.jumpToPositionForSmoothScroller(mJumpToPosition);
                    this.mChanged = false;
                    return;
                }
                if (this.mChanged) {
                    this.validate();
                    recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
                    if (++this.mConsecutiveUpdates > 10) {
                        Log.e("RecyclerView", "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary");
                    }
                    this.mChanged = false;
                }
                else {
                    this.mConsecutiveUpdates = 0;
                }
            }
            
            public void update(final int mDx, final int mDy, final int mDuration, final Interpolator mInterpolator) {
                this.mDx = mDx;
                this.mDy = mDy;
                this.mDuration = mDuration;
                this.mInterpolator = mInterpolator;
                this.mChanged = true;
            }
        }
        
        public interface ScrollVectorProvider
        {
            PointF computeScrollVectorForPosition(final int p0);
        }
    }
    
    public static class State
    {
        private SparseArray<Object> mData;
        int mDeletedInvisibleItemCountSincePreviousLayout;
        long mFocusedItemId;
        int mFocusedItemPosition;
        int mFocusedSubChildId;
        boolean mInPreLayout;
        boolean mIsMeasuring;
        int mItemCount;
        int mLayoutStep;
        int mPreviousLayoutItemCount;
        int mRemainingScrollHorizontal;
        int mRemainingScrollVertical;
        boolean mRunPredictiveAnimations;
        boolean mRunSimpleAnimations;
        boolean mStructureChanged;
        int mTargetPosition;
        boolean mTrackOldChangeHolders;
        
        public State() {
            this.mTargetPosition = -1;
            this.mPreviousLayoutItemCount = 0;
            this.mDeletedInvisibleItemCountSincePreviousLayout = 0;
            this.mLayoutStep = 1;
            this.mItemCount = 0;
            this.mStructureChanged = false;
            this.mInPreLayout = false;
            this.mTrackOldChangeHolders = false;
            this.mIsMeasuring = false;
            this.mRunSimpleAnimations = false;
            this.mRunPredictiveAnimations = false;
        }
        
        void assertLayoutStep(final int i) {
            if ((this.mLayoutStep & i) != 0x0) {
                return;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("Layout state should be one of ");
            sb.append(Integer.toBinaryString(i));
            sb.append(" but it is ");
            sb.append(Integer.toBinaryString(this.mLayoutStep));
            throw new IllegalStateException(sb.toString());
        }
        
        public boolean didStructureChange() {
            return this.mStructureChanged;
        }
        
        public int getItemCount() {
            int mItemCount;
            if (this.mInPreLayout) {
                mItemCount = this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout;
            }
            else {
                mItemCount = this.mItemCount;
            }
            return mItemCount;
        }
        
        public int getRemainingScrollHorizontal() {
            return this.mRemainingScrollHorizontal;
        }
        
        public int getRemainingScrollVertical() {
            return this.mRemainingScrollVertical;
        }
        
        public int getTargetScrollPosition() {
            return this.mTargetPosition;
        }
        
        public boolean hasTargetScrollPosition() {
            return this.mTargetPosition != -1;
        }
        
        public boolean isPreLayout() {
            return this.mInPreLayout;
        }
        
        void prepareForNestedPrefetch(final Adapter adapter) {
            this.mLayoutStep = 1;
            this.mItemCount = adapter.getItemCount();
            this.mInPreLayout = false;
            this.mTrackOldChangeHolders = false;
            this.mIsMeasuring = false;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("State{mTargetPosition=");
            sb.append(this.mTargetPosition);
            sb.append(", mData=");
            sb.append(this.mData);
            sb.append(", mItemCount=");
            sb.append(this.mItemCount);
            sb.append(", mIsMeasuring=");
            sb.append(this.mIsMeasuring);
            sb.append(", mPreviousLayoutItemCount=");
            sb.append(this.mPreviousLayoutItemCount);
            sb.append(", mDeletedInvisibleItemCountSincePreviousLayout=");
            sb.append(this.mDeletedInvisibleItemCountSincePreviousLayout);
            sb.append(", mStructureChanged=");
            sb.append(this.mStructureChanged);
            sb.append(", mInPreLayout=");
            sb.append(this.mInPreLayout);
            sb.append(", mRunSimpleAnimations=");
            sb.append(this.mRunSimpleAnimations);
            sb.append(", mRunPredictiveAnimations=");
            sb.append(this.mRunPredictiveAnimations);
            sb.append('}');
            return sb.toString();
        }
        
        public boolean willRunPredictiveAnimations() {
            return this.mRunPredictiveAnimations;
        }
    }
    
    public abstract static class ViewCacheExtension
    {
        public abstract View getViewForPositionAndType(final Recycler p0, final int p1, final int p2);
    }
    
    class ViewFlinger implements Runnable
    {
        private boolean mEatRunOnAnimationRequest;
        Interpolator mInterpolator;
        private int mLastFlingX;
        private int mLastFlingY;
        OverScroller mOverScroller;
        private boolean mReSchedulePostAnimationCallback;
        
        ViewFlinger() {
            this.mInterpolator = RecyclerView.sQuinticInterpolator;
            this.mEatRunOnAnimationRequest = false;
            this.mReSchedulePostAnimationCallback = false;
            this.mOverScroller = new OverScroller(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
        }
        
        private int computeScrollDuration(int n, int a, int n2, int n3) {
            final int abs = Math.abs(n);
            final int abs2 = Math.abs(a);
            final boolean b = abs > abs2;
            n2 = (int)Math.sqrt(n2 * n2 + n3 * n3);
            a = (int)Math.sqrt(n * n + a * a);
            final RecyclerView this$0 = RecyclerView.this;
            if (b) {
                n = this$0.getWidth();
            }
            else {
                n = this$0.getHeight();
            }
            n3 = n / 2;
            final float n4 = (float)a;
            final float n5 = (float)n;
            final float min = Math.min(1.0f, n4 * 1.0f / n5);
            final float n6 = (float)n3;
            final float distanceInfluenceForSnapDuration = this.distanceInfluenceForSnapDuration(min);
            if (n2 > 0) {
                n = Math.round(Math.abs((n6 + distanceInfluenceForSnapDuration * n6) / n2) * 1000.0f) * 4;
            }
            else {
                if (b) {
                    n = abs;
                }
                else {
                    n = abs2;
                }
                n = (int)((n / n5 + 1.0f) * 300.0f);
            }
            return Math.min(n, 2000);
        }
        
        private float distanceInfluenceForSnapDuration(final float n) {
            return (float)Math.sin((n - 0.5f) * 0.47123894f);
        }
        
        private void internalPostOnAnimation() {
            RecyclerView.this.removeCallbacks((Runnable)this);
            ViewCompat.postOnAnimation((View)RecyclerView.this, this);
        }
        
        public void fling(final int n, final int n2) {
            RecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            final Interpolator mInterpolator = this.mInterpolator;
            final Interpolator sQuinticInterpolator = RecyclerView.sQuinticInterpolator;
            if (mInterpolator != sQuinticInterpolator) {
                this.mInterpolator = sQuinticInterpolator;
                this.mOverScroller = new OverScroller(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
            }
            this.mOverScroller.fling(0, 0, n, n2, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            this.postOnAnimation();
        }
        
        void postOnAnimation() {
            if (this.mEatRunOnAnimationRequest) {
                this.mReSchedulePostAnimationCallback = true;
            }
            else {
                this.internalPostOnAnimation();
            }
        }
        
        @Override
        public void run() {
            final RecyclerView this$0 = RecyclerView.this;
            if (this$0.mLayout == null) {
                this.stop();
                return;
            }
            this.mReSchedulePostAnimationCallback = false;
            this.mEatRunOnAnimationRequest = true;
            this$0.consumePendingUpdateOperations();
            final OverScroller mOverScroller = this.mOverScroller;
            if (mOverScroller.computeScrollOffset()) {
                final int currX = mOverScroller.getCurrX();
                final int currY = mOverScroller.getCurrY();
                final int n = currX - this.mLastFlingX;
                final int n2 = currY - this.mLastFlingY;
                this.mLastFlingX = currX;
                this.mLastFlingY = currY;
                final RecyclerView this$2 = RecyclerView.this;
                final int[] mReusableIntPair = this$2.mReusableIntPair;
                mReusableIntPair[1] = (mReusableIntPair[0] = 0);
                int n3 = n;
                int n4 = n2;
                if (this$2.dispatchNestedPreScroll(n, n2, mReusableIntPair, null, 1)) {
                    final int[] mReusableIntPair2 = RecyclerView.this.mReusableIntPair;
                    n3 = n - mReusableIntPair2[0];
                    n4 = n2 - mReusableIntPair2[1];
                }
                if (RecyclerView.this.getOverScrollMode() != 2) {
                    RecyclerView.this.considerReleasingGlowsOnScroll(n3, n4);
                }
                final RecyclerView this$3 = RecyclerView.this;
                int n9;
                int n10;
                int n11;
                if (this$3.mAdapter != null) {
                    final int[] mReusableIntPair3 = this$3.mReusableIntPair;
                    mReusableIntPair3[1] = (mReusableIntPair3[0] = 0);
                    this$3.scrollStep(n3, n4, mReusableIntPair3);
                    final RecyclerView this$4 = RecyclerView.this;
                    final int[] mReusableIntPair4 = this$4.mReusableIntPair;
                    final int n5 = mReusableIntPair4[0];
                    final int n6 = mReusableIntPair4[1];
                    final int n7 = n3 - n5;
                    final int n8 = n4 - n6;
                    final SmoothScroller mSmoothScroller = this$4.mLayout.mSmoothScroller;
                    n3 = n7;
                    n9 = n6;
                    n10 = n5;
                    n11 = n8;
                    if (mSmoothScroller != null) {
                        n3 = n7;
                        n9 = n6;
                        n10 = n5;
                        n11 = n8;
                        if (!mSmoothScroller.isPendingInitialRun()) {
                            n3 = n7;
                            n9 = n6;
                            n10 = n5;
                            n11 = n8;
                            if (mSmoothScroller.isRunning()) {
                                final int itemCount = RecyclerView.this.mState.getItemCount();
                                if (itemCount == 0) {
                                    mSmoothScroller.stop();
                                    n3 = n7;
                                    n9 = n6;
                                    n10 = n5;
                                    n11 = n8;
                                }
                                else if (mSmoothScroller.getTargetPosition() >= itemCount) {
                                    mSmoothScroller.setTargetPosition(itemCount - 1);
                                    mSmoothScroller.onAnimation(n5, n6);
                                    n3 = n7;
                                    n9 = n6;
                                    n10 = n5;
                                    n11 = n8;
                                }
                                else {
                                    mSmoothScroller.onAnimation(n5, n6);
                                    n3 = n7;
                                    n9 = n6;
                                    n10 = n5;
                                    n11 = n8;
                                }
                            }
                        }
                    }
                }
                else {
                    final int n12;
                    n9 = (n12 = 0);
                    n11 = n4;
                    n10 = n12;
                }
                if (!RecyclerView.this.mItemDecorations.isEmpty()) {
                    RecyclerView.this.invalidate();
                }
                final RecyclerView this$5 = RecyclerView.this;
                final int[] mReusableIntPair5 = this$5.mReusableIntPair;
                mReusableIntPair5[1] = (mReusableIntPair5[0] = 0);
                this$5.dispatchNestedScroll(n10, n9, n3, n11, null, 1, mReusableIntPair5);
                final int[] mReusableIntPair6 = RecyclerView.this.mReusableIntPair;
                final int n13 = n3 - mReusableIntPair6[0];
                final int n14 = n11 - mReusableIntPair6[1];
                if (n10 != 0 || n9 != 0) {
                    RecyclerView.this.dispatchOnScrolled(n10, n9);
                }
                if (!RecyclerView.access$200(RecyclerView.this)) {
                    RecyclerView.this.invalidate();
                }
                final boolean b = mOverScroller.getCurrX() == mOverScroller.getFinalX();
                final boolean b2 = mOverScroller.getCurrY() == mOverScroller.getFinalY();
                boolean b3 = false;
                Label_0632: {
                    Label_0630: {
                        if (!mOverScroller.isFinished()) {
                            if (b || n13 != 0) {
                                if (b2) {
                                    break Label_0630;
                                }
                                if (n14 != 0) {
                                    break Label_0630;
                                }
                            }
                            b3 = false;
                            break Label_0632;
                        }
                    }
                    b3 = true;
                }
                final SmoothScroller mSmoothScroller2 = RecyclerView.this.mLayout.mSmoothScroller;
                if ((mSmoothScroller2 == null || !mSmoothScroller2.isPendingInitialRun()) && b3) {
                    if (RecyclerView.this.getOverScrollMode() != 2) {
                        int n15 = (int)mOverScroller.getCurrVelocity();
                        int n16;
                        if (n13 < 0) {
                            n16 = -n15;
                        }
                        else if (n13 > 0) {
                            n16 = n15;
                        }
                        else {
                            n16 = 0;
                        }
                        if (n14 < 0) {
                            n15 = -n15;
                        }
                        else if (n14 <= 0) {
                            n15 = 0;
                        }
                        RecyclerView.this.absorbGlows(n16, n15);
                    }
                    if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                        RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
                    }
                }
                else {
                    this.postOnAnimation();
                    final RecyclerView this$6 = RecyclerView.this;
                    final GapWorker mGapWorker = this$6.mGapWorker;
                    if (mGapWorker != null) {
                        mGapWorker.postFromTraversal(this$6, n10, n9);
                    }
                }
            }
            final SmoothScroller mSmoothScroller3 = RecyclerView.this.mLayout.mSmoothScroller;
            if (mSmoothScroller3 != null && mSmoothScroller3.isPendingInitialRun()) {
                mSmoothScroller3.onAnimation(0, 0);
            }
            this.mEatRunOnAnimationRequest = false;
            if (this.mReSchedulePostAnimationCallback) {
                this.internalPostOnAnimation();
            }
            else {
                RecyclerView.this.setScrollState(0);
                RecyclerView.this.stopNestedScroll(1);
            }
        }
        
        public void smoothScrollBy(final int n, final int n2, final int n3, final Interpolator interpolator) {
            int computeScrollDuration = n3;
            if (n3 == Integer.MIN_VALUE) {
                computeScrollDuration = this.computeScrollDuration(n, n2, 0, 0);
            }
            Interpolator sQuinticInterpolator;
            if ((sQuinticInterpolator = interpolator) == null) {
                sQuinticInterpolator = RecyclerView.sQuinticInterpolator;
            }
            if (this.mInterpolator != sQuinticInterpolator) {
                this.mInterpolator = sQuinticInterpolator;
                this.mOverScroller = new OverScroller(RecyclerView.this.getContext(), sQuinticInterpolator);
            }
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            RecyclerView.this.setScrollState(2);
            this.mOverScroller.startScroll(0, 0, n, n2, computeScrollDuration);
            if (Build$VERSION.SDK_INT < 23) {
                this.mOverScroller.computeScrollOffset();
            }
            this.postOnAnimation();
        }
        
        public void stop() {
            RecyclerView.this.removeCallbacks((Runnable)this);
            this.mOverScroller.abortAnimation();
        }
    }
    
    public abstract static class ViewHolder
    {
        private static final List<Object> FULLUPDATE_PAYLOADS;
        public final View itemView;
        Adapter<? extends ViewHolder> mBindingAdapter;
        int mFlags;
        boolean mInChangeScrap;
        private int mIsRecyclableCount;
        long mItemId;
        int mItemViewType;
        WeakReference<RecyclerView> mNestedRecyclerView;
        int mOldPosition;
        RecyclerView mOwnerRecyclerView;
        List<Object> mPayloads;
        int mPendingAccessibilityState;
        int mPosition;
        int mPreLayoutPosition;
        Recycler mScrapContainer;
        ViewHolder mShadowedHolder;
        ViewHolder mShadowingHolder;
        List<Object> mUnmodifiedPayloads;
        private int mWasImportantForAccessibilityBeforeHidden;
        
        static {
            FULLUPDATE_PAYLOADS = Collections.emptyList();
        }
        
        public ViewHolder(final View itemView) {
            this.mPosition = -1;
            this.mOldPosition = -1;
            this.mItemId = -1L;
            this.mItemViewType = -1;
            this.mPreLayoutPosition = -1;
            this.mShadowedHolder = null;
            this.mShadowingHolder = null;
            this.mPayloads = null;
            this.mUnmodifiedPayloads = null;
            this.mIsRecyclableCount = 0;
            this.mScrapContainer = null;
            this.mInChangeScrap = false;
            this.mWasImportantForAccessibilityBeforeHidden = 0;
            this.mPendingAccessibilityState = -1;
            if (itemView != null) {
                this.itemView = itemView;
                return;
            }
            throw new IllegalArgumentException("itemView may not be null");
        }
        
        private void createPayloadsIfNeeded() {
            if (this.mPayloads == null) {
                final ArrayList<Object> list = new ArrayList<Object>();
                this.mPayloads = list;
                this.mUnmodifiedPayloads = Collections.unmodifiableList((List<?>)list);
            }
        }
        
        void addChangePayload(final Object o) {
            if (o == null) {
                this.addFlags(1024);
            }
            else if ((0x400 & this.mFlags) == 0x0) {
                this.createPayloadsIfNeeded();
                this.mPayloads.add(o);
            }
        }
        
        void addFlags(final int n) {
            this.mFlags |= n;
        }
        
        void clearOldPosition() {
            this.mOldPosition = -1;
            this.mPreLayoutPosition = -1;
        }
        
        void clearPayload() {
            final List<Object> mPayloads = this.mPayloads;
            if (mPayloads != null) {
                mPayloads.clear();
            }
            this.mFlags &= 0xFFFFFBFF;
        }
        
        void clearReturnedFromScrapFlag() {
            this.mFlags &= 0xFFFFFFDF;
        }
        
        void clearTmpDetachFlag() {
            this.mFlags &= 0xFFFFFEFF;
        }
        
        boolean doesTransientStatePreventRecycling() {
            return (this.mFlags & 0x10) == 0x0 && ViewCompat.hasTransientState(this.itemView);
        }
        
        void flagRemovedAndOffsetPosition(final int mPosition, final int n, final boolean b) {
            this.addFlags(8);
            this.offsetPosition(n, b);
            this.mPosition = mPosition;
        }
        
        public final int getAbsoluteAdapterPosition() {
            final RecyclerView mOwnerRecyclerView = this.mOwnerRecyclerView;
            if (mOwnerRecyclerView == null) {
                return -1;
            }
            return mOwnerRecyclerView.getAdapterPositionInRecyclerView(this);
        }
        
        @Deprecated
        public final int getAdapterPosition() {
            return this.getBindingAdapterPosition();
        }
        
        public final int getBindingAdapterPosition() {
            if (this.mBindingAdapter == null) {
                return -1;
            }
            final RecyclerView mOwnerRecyclerView = this.mOwnerRecyclerView;
            if (mOwnerRecyclerView == null) {
                return -1;
            }
            final Adapter adapter = mOwnerRecyclerView.getAdapter();
            if (adapter == null) {
                return -1;
            }
            final int adapterPositionInRecyclerView = this.mOwnerRecyclerView.getAdapterPositionInRecyclerView(this);
            if (adapterPositionInRecyclerView == -1) {
                return -1;
            }
            return adapter.findRelativeAdapterPositionIn((Adapter)this.mBindingAdapter, this, adapterPositionInRecyclerView);
        }
        
        public final long getItemId() {
            return this.mItemId;
        }
        
        public final int getItemViewType() {
            return this.mItemViewType;
        }
        
        public final int getLayoutPosition() {
            int n;
            if ((n = this.mPreLayoutPosition) == -1) {
                n = this.mPosition;
            }
            return n;
        }
        
        public final int getOldPosition() {
            return this.mOldPosition;
        }
        
        List<Object> getUnmodifiedPayloads() {
            if ((this.mFlags & 0x400) != 0x0) {
                return ViewHolder.FULLUPDATE_PAYLOADS;
            }
            final List<Object> mPayloads = this.mPayloads;
            if (mPayloads != null && mPayloads.size()) {
                return this.mUnmodifiedPayloads;
            }
            return ViewHolder.FULLUPDATE_PAYLOADS;
        }
        
        boolean hasAnyOfTheFlags(final int n) {
            return (this.mFlags & n) != 0x0;
        }
        
        boolean isAdapterPositionUnknown() {
            return (this.mFlags & 0x200) != 0x0 || this.isInvalid();
        }
        
        boolean isAttachedToTransitionOverlay() {
            return this.itemView.getParent() != null && this.itemView.getParent() != this.mOwnerRecyclerView;
        }
        
        boolean isBound() {
            final int mFlags = this.mFlags;
            boolean b = true;
            if ((mFlags & 0x1) == 0x0) {
                b = false;
            }
            return b;
        }
        
        boolean isInvalid() {
            return (this.mFlags & 0x4) != 0x0;
        }
        
        public final boolean isRecyclable() {
            return (this.mFlags & 0x10) == 0x0 && !ViewCompat.hasTransientState(this.itemView);
        }
        
        boolean isRemoved() {
            return (this.mFlags & 0x8) != 0x0;
        }
        
        boolean isScrap() {
            return this.mScrapContainer != null;
        }
        
        boolean isTmpDetached() {
            return (this.mFlags & 0x100) != 0x0;
        }
        
        boolean isUpdated() {
            return (this.mFlags & 0x2) != 0x0;
        }
        
        boolean needsUpdate() {
            return (this.mFlags & 0x2) != 0x0;
        }
        
        void offsetPosition(final int n, final boolean b) {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
            if (this.mPreLayoutPosition == -1) {
                this.mPreLayoutPosition = this.mPosition;
            }
            if (b) {
                this.mPreLayoutPosition += n;
            }
            this.mPosition += n;
            if (this.itemView.getLayoutParams() != null) {
                ((LayoutParams)this.itemView.getLayoutParams()).mInsetsDirty = true;
            }
        }
        
        void onEnteredHiddenState(final RecyclerView recyclerView) {
            final int mPendingAccessibilityState = this.mPendingAccessibilityState;
            if (mPendingAccessibilityState != -1) {
                this.mWasImportantForAccessibilityBeforeHidden = mPendingAccessibilityState;
            }
            else {
                this.mWasImportantForAccessibilityBeforeHidden = ViewCompat.getImportantForAccessibility(this.itemView);
            }
            recyclerView.setChildImportantForAccessibilityInternal(this, 4);
        }
        
        void onLeftHiddenState(final RecyclerView recyclerView) {
            recyclerView.setChildImportantForAccessibilityInternal(this, this.mWasImportantForAccessibilityBeforeHidden);
            this.mWasImportantForAccessibilityBeforeHidden = 0;
        }
        
        void resetInternal() {
            this.mFlags = 0;
            this.mPosition = -1;
            this.mOldPosition = -1;
            this.mItemId = -1L;
            this.mPreLayoutPosition = -1;
            this.mIsRecyclableCount = 0;
            this.mShadowedHolder = null;
            this.mShadowingHolder = null;
            this.clearPayload();
            this.mWasImportantForAccessibilityBeforeHidden = 0;
            this.mPendingAccessibilityState = -1;
            RecyclerView.clearNestedRecyclerViewIfNotNested(this);
        }
        
        void saveOldPosition() {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
        }
        
        void setFlags(final int n, final int n2) {
            this.mFlags = ((n & n2) | (this.mFlags & n2));
        }
        
        public final void setIsRecyclable(final boolean b) {
            int mIsRecyclableCount = this.mIsRecyclableCount;
            if (b) {
                --mIsRecyclableCount;
            }
            else {
                ++mIsRecyclableCount;
            }
            this.mIsRecyclableCount = mIsRecyclableCount;
            if (mIsRecyclableCount < 0) {
                this.mIsRecyclableCount = 0;
                final StringBuilder sb = new StringBuilder();
                sb.append("isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for ");
                sb.append(this);
                Log.e("View", sb.toString());
            }
            else if (!b && mIsRecyclableCount == 1) {
                this.mFlags |= 0x10;
            }
            else if (b && this.mIsRecyclableCount == 0) {
                this.mFlags &= 0xFFFFFFEF;
            }
        }
        
        void setScrapContainer(final Recycler mScrapContainer, final boolean mInChangeScrap) {
            this.mScrapContainer = mScrapContainer;
            this.mInChangeScrap = mInChangeScrap;
        }
        
        boolean shouldBeKeptAsChild() {
            return (this.mFlags & 0x10) != 0x0;
        }
        
        boolean shouldIgnore() {
            return (this.mFlags & 0x80) != 0x0;
        }
        
        @Override
        public String toString() {
            String simpleName;
            if (this.getClass().isAnonymousClass()) {
                simpleName = "ViewHolder";
            }
            else {
                simpleName = this.getClass().getSimpleName();
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(simpleName);
            sb.append("{");
            sb.append(Integer.toHexString(this.hashCode()));
            sb.append(" position=");
            sb.append(this.mPosition);
            sb.append(" id=");
            sb.append(this.mItemId);
            sb.append(", oldPos=");
            sb.append(this.mOldPosition);
            sb.append(", pLpos:");
            sb.append(this.mPreLayoutPosition);
            final StringBuilder sb2 = new StringBuilder(sb.toString());
            if (this.isScrap()) {
                sb2.append(" scrap ");
                String str;
                if (this.mInChangeScrap) {
                    str = "[changeScrap]";
                }
                else {
                    str = "[attachedScrap]";
                }
                sb2.append(str);
            }
            if (this.isInvalid()) {
                sb2.append(" invalid");
            }
            if (!this.isBound()) {
                sb2.append(" unbound");
            }
            if (this.needsUpdate()) {
                sb2.append(" update");
            }
            if (this.isRemoved()) {
                sb2.append(" removed");
            }
            if (this.shouldIgnore()) {
                sb2.append(" ignored");
            }
            if (this.isTmpDetached()) {
                sb2.append(" tmpDetached");
            }
            if (!this.isRecyclable()) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append(" not recyclable(");
                sb3.append(this.mIsRecyclableCount);
                sb3.append(")");
                sb2.append(sb3.toString());
            }
            if (this.isAdapterPositionUnknown()) {
                sb2.append(" undefined adapter position");
            }
            if (this.itemView.getParent() == null) {
                sb2.append(" no parent");
            }
            sb2.append("}");
            return sb2.toString();
        }
        
        void unScrap() {
            this.mScrapContainer.unscrapView(this);
        }
        
        boolean wasReturnedFromScrap() {
            return (this.mFlags & 0x20) != 0x0;
        }
    }
}
