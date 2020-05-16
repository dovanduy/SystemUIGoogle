// 
// Decompiled by Procyon v0.5.36
// 

package androidx.coordinatorlayout.widget;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import androidx.customview.view.AbsSavedState;
import android.view.ViewParent;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;
import android.view.View$BaseSavedState;
import androidx.core.util.ObjectsCompat;
import android.util.SparseArray;
import android.os.Parcelable;
import android.view.View$MeasureSpec;
import android.view.ViewGroup$MarginLayoutParams;
import android.graphics.Region$Op;
import android.graphics.Canvas;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewTreeObserver$OnPreDrawListener;
import java.util.Collection;
import java.util.HashMap;
import android.text.TextUtils;
import java.util.Collections;
import android.util.Log;
import androidx.core.view.GravityCompat;
import android.view.MotionEvent;
import android.os.SystemClock;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.core.view.ViewCompat;
import androidx.coordinatorlayout.R$style;
import androidx.coordinatorlayout.R$styleable;
import java.util.ArrayList;
import androidx.coordinatorlayout.R$attr;
import androidx.core.util.Pools$SynchronizedPool;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import android.graphics.Paint;
import android.view.ViewGroup$OnHierarchyChangeListener;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.WindowInsetsCompat;
import java.util.List;
import androidx.core.view.OnApplyWindowInsetsListener;
import android.graphics.Rect;
import androidx.core.util.Pools$Pool;
import java.lang.reflect.Constructor;
import java.util.Map;
import android.view.View;
import java.util.Comparator;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParent2;
import android.view.ViewGroup;

public class CoordinatorLayout extends ViewGroup implements NestedScrollingParent2, NestedScrollingParent3
{
    static final Class<?>[] CONSTRUCTOR_PARAMS;
    static final Comparator<View> TOP_SORTED_CHILDREN_COMPARATOR;
    static final String WIDGET_PACKAGE_NAME;
    static final ThreadLocal<Map<String, Constructor<Behavior>>> sConstructors;
    private static final Pools$Pool<Rect> sRectPool;
    private OnApplyWindowInsetsListener mApplyWindowInsetsListener;
    private final int[] mBehaviorConsumed;
    private View mBehaviorTouchView;
    private final DirectedAcyclicGraph<View> mChildDag;
    private final List<View> mDependencySortedChildren;
    private boolean mDisallowInterceptReset;
    private boolean mDrawStatusBarBackground;
    private boolean mIsAttachedToWindow;
    private int[] mKeylines;
    private WindowInsetsCompat mLastInsets;
    private boolean mNeedsPreDrawListener;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private View mNestedScrollingTarget;
    private final int[] mNestedScrollingV2ConsumedCompat;
    ViewGroup$OnHierarchyChangeListener mOnHierarchyChangeListener;
    private OnPreDrawListener mOnPreDrawListener;
    private Paint mScrimPaint;
    private Drawable mStatusBarBackground;
    private final List<View> mTempList1;
    
    static {
        final Package package1 = CoordinatorLayout.class.getPackage();
        String name;
        if (package1 != null) {
            name = package1.getName();
        }
        else {
            name = null;
        }
        WIDGET_PACKAGE_NAME = name;
        if (Build$VERSION.SDK_INT >= 21) {
            TOP_SORTED_CHILDREN_COMPARATOR = new ViewElevationComparator();
        }
        else {
            TOP_SORTED_CHILDREN_COMPARATOR = null;
        }
        CONSTRUCTOR_PARAMS = new Class[] { Context.class, AttributeSet.class };
        sConstructors = new ThreadLocal<Map<String, Constructor<Behavior>>>();
        sRectPool = new Pools$SynchronizedPool<Rect>(12);
    }
    
    public CoordinatorLayout(final Context context, final AttributeSet set) {
        this(context, set, R$attr.coordinatorLayoutStyle);
    }
    
    public CoordinatorLayout(final Context context, final AttributeSet set, int i) {
        super(context, set, i);
        this.mDependencySortedChildren = new ArrayList<View>();
        this.mChildDag = new DirectedAcyclicGraph<View>();
        this.mTempList1 = new ArrayList<View>();
        new ArrayList();
        this.mBehaviorConsumed = new int[2];
        this.mNestedScrollingV2ConsumedCompat = new int[2];
        this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        final int n = 0;
        TypedArray typedArray;
        if (i == 0) {
            typedArray = context.obtainStyledAttributes(set, R$styleable.CoordinatorLayout, 0, R$style.Widget_Support_CoordinatorLayout);
        }
        else {
            typedArray = context.obtainStyledAttributes(set, R$styleable.CoordinatorLayout, i, 0);
        }
        if (i == 0) {
            ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.CoordinatorLayout, set, typedArray, 0, R$style.Widget_Support_CoordinatorLayout);
        }
        else {
            ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.CoordinatorLayout, set, typedArray, i, 0);
        }
        i = typedArray.getResourceId(R$styleable.CoordinatorLayout_keylines, 0);
        if (i != 0) {
            final Resources resources = context.getResources();
            this.mKeylines = resources.getIntArray(i);
            final float density = resources.getDisplayMetrics().density;
            int length;
            int[] mKeylines;
            for (length = this.mKeylines.length, i = n; i < length; ++i) {
                mKeylines = this.mKeylines;
                mKeylines[i] *= (int)density;
            }
        }
        this.mStatusBarBackground = typedArray.getDrawable(R$styleable.CoordinatorLayout_statusBarBackground);
        typedArray.recycle();
        this.setupForInsets();
        super.setOnHierarchyChangeListener((ViewGroup$OnHierarchyChangeListener)new HierarchyChangeListener());
        if (ViewCompat.getImportantForAccessibility((View)this) == 0) {
            ViewCompat.setImportantForAccessibility((View)this, 1);
        }
    }
    
    private static Rect acquireTempRect() {
        Rect rect;
        if ((rect = CoordinatorLayout.sRectPool.acquire()) == null) {
            rect = new Rect();
        }
        return rect;
    }
    
    private void cancelInterceptBehaviors() {
        final int childCount = this.getChildCount();
        MotionEvent motionEvent = null;
        MotionEvent obtain;
        for (int i = 0; i < childCount; ++i, motionEvent = obtain) {
            final View child = this.getChildAt(i);
            final Behavior behavior = ((LayoutParams)child.getLayoutParams()).getBehavior();
            obtain = motionEvent;
            if (behavior != null) {
                if ((obtain = motionEvent) == null) {
                    final long uptimeMillis = SystemClock.uptimeMillis();
                    obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                }
                behavior.onInterceptTouchEvent(this, child, obtain);
            }
        }
        if (motionEvent != null) {
            motionEvent.recycle();
        }
    }
    
    private static int clamp(final int n, final int n2, final int n3) {
        if (n < n2) {
            return n2;
        }
        if (n > n3) {
            return n3;
        }
        return n;
    }
    
    private void constrainChildRect(final LayoutParams layoutParams, final Rect rect, final int n, final int n2) {
        final int width = this.getWidth();
        final int height = this.getHeight();
        final int max = Math.max(this.getPaddingLeft() + layoutParams.leftMargin, Math.min(rect.left, width - this.getPaddingRight() - n - layoutParams.rightMargin));
        final int max2 = Math.max(this.getPaddingTop() + layoutParams.topMargin, Math.min(rect.top, height - this.getPaddingBottom() - n2 - layoutParams.bottomMargin));
        rect.set(max, max2, n + max, n2 + max2);
    }
    
    private WindowInsetsCompat dispatchApplyWindowInsetsToBehaviors(WindowInsetsCompat windowInsetsCompat) {
        if (windowInsetsCompat.isConsumed()) {
            return windowInsetsCompat;
        }
        int n = 0;
        final int childCount = this.getChildCount();
        WindowInsetsCompat windowInsetsCompat2;
        while (true) {
            windowInsetsCompat2 = windowInsetsCompat;
            if (n >= childCount) {
                break;
            }
            final View child = this.getChildAt(n);
            WindowInsetsCompat onApplyWindowInsets = windowInsetsCompat;
            if (ViewCompat.getFitsSystemWindows(child)) {
                final Behavior behavior = ((LayoutParams)child.getLayoutParams()).getBehavior();
                onApplyWindowInsets = windowInsetsCompat;
                if (behavior != null) {
                    windowInsetsCompat = (onApplyWindowInsets = behavior.onApplyWindowInsets(this, child, windowInsetsCompat));
                    if (windowInsetsCompat.isConsumed()) {
                        windowInsetsCompat2 = windowInsetsCompat;
                        break;
                    }
                }
            }
            ++n;
            windowInsetsCompat = onApplyWindowInsets;
        }
        return windowInsetsCompat2;
    }
    
    private void getDesiredAnchoredChildRectWithoutConstraints(int n, final Rect rect, final Rect rect2, final LayoutParams layoutParams, final int n2, final int n3) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(resolveAnchoredChildGravity(layoutParams.gravity), n);
        final int absoluteGravity2 = GravityCompat.getAbsoluteGravity(resolveGravity(layoutParams.anchorGravity), n);
        final int n4 = absoluteGravity & 0x7;
        final int n5 = absoluteGravity & 0x70;
        n = (absoluteGravity2 & 0x7);
        final int n6 = absoluteGravity2 & 0x70;
        if (n != 1) {
            if (n != 5) {
                n = rect.left;
            }
            else {
                n = rect.right;
            }
        }
        else {
            n = rect.left + rect.width() / 2;
        }
        int n7;
        if (n6 != 16) {
            if (n6 != 80) {
                n7 = rect.top;
            }
            else {
                n7 = rect.bottom;
            }
        }
        else {
            n7 = rect.top + rect.height() / 2;
        }
        int n8;
        if (n4 != 1) {
            n8 = n;
            if (n4 != 5) {
                n8 = n - n2;
            }
        }
        else {
            n8 = n - n2 / 2;
        }
        if (n5 != 16) {
            n = n7;
            if (n5 != 80) {
                n = n7 - n3;
            }
        }
        else {
            n = n7 - n3 / 2;
        }
        rect2.set(n8, n, n2 + n8, n3 + n);
    }
    
    private int getKeyline(final int n) {
        final int[] mKeylines = this.mKeylines;
        if (mKeylines == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("No keylines defined for ");
            sb.append(this);
            sb.append(" - attempted index lookup ");
            sb.append(n);
            Log.e("CoordinatorLayout", sb.toString());
            return 0;
        }
        if (n >= 0 && n < mKeylines.length) {
            return mKeylines[n];
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Keyline index ");
        sb2.append(n);
        sb2.append(" out of range for ");
        sb2.append(this);
        Log.e("CoordinatorLayout", sb2.toString());
        return 0;
    }
    
    private void getTopSortedChildren(final List<View> list) {
        list.clear();
        final boolean childrenDrawingOrderEnabled = this.isChildrenDrawingOrderEnabled();
        final int childCount = this.getChildCount();
        for (int i = childCount - 1; i >= 0; --i) {
            int childDrawingOrder;
            if (childrenDrawingOrderEnabled) {
                childDrawingOrder = this.getChildDrawingOrder(childCount, i);
            }
            else {
                childDrawingOrder = i;
            }
            list.add(this.getChildAt(childDrawingOrder));
        }
        final Comparator<View> top_SORTED_CHILDREN_COMPARATOR = CoordinatorLayout.TOP_SORTED_CHILDREN_COMPARATOR;
        if (top_SORTED_CHILDREN_COMPARATOR != null) {
            Collections.sort((List<Object>)list, (Comparator<? super Object>)top_SORTED_CHILDREN_COMPARATOR);
        }
    }
    
    private boolean hasDependencies(final View view) {
        return this.mChildDag.hasOutgoingEdges(view);
    }
    
    private void layoutChild(final View view, final int n) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final Rect acquireTempRect = acquireTempRect();
        acquireTempRect.set(this.getPaddingLeft() + layoutParams.leftMargin, this.getPaddingTop() + layoutParams.topMargin, this.getWidth() - this.getPaddingRight() - layoutParams.rightMargin, this.getHeight() - this.getPaddingBottom() - layoutParams.bottomMargin);
        if (this.mLastInsets != null && ViewCompat.getFitsSystemWindows((View)this) && !ViewCompat.getFitsSystemWindows(view)) {
            acquireTempRect.left += this.mLastInsets.getSystemWindowInsetLeft();
            acquireTempRect.top += this.mLastInsets.getSystemWindowInsetTop();
            acquireTempRect.right -= this.mLastInsets.getSystemWindowInsetRight();
            acquireTempRect.bottom -= this.mLastInsets.getSystemWindowInsetBottom();
        }
        final Rect acquireTempRect2 = acquireTempRect();
        GravityCompat.apply(resolveGravity(layoutParams.gravity), view.getMeasuredWidth(), view.getMeasuredHeight(), acquireTempRect, acquireTempRect2, n);
        view.layout(acquireTempRect2.left, acquireTempRect2.top, acquireTempRect2.right, acquireTempRect2.bottom);
        releaseTempRect(acquireTempRect);
        releaseTempRect(acquireTempRect2);
    }
    
    private void layoutChildWithAnchor(final View view, final View view2, final int n) {
        final Rect acquireTempRect = acquireTempRect();
        final Rect acquireTempRect2 = acquireTempRect();
        try {
            this.getDescendantRect(view2, acquireTempRect);
            this.getDesiredAnchoredChildRect(view, n, acquireTempRect, acquireTempRect2);
            view.layout(acquireTempRect2.left, acquireTempRect2.top, acquireTempRect2.right, acquireTempRect2.bottom);
        }
        finally {
            releaseTempRect(acquireTempRect);
            releaseTempRect(acquireTempRect2);
        }
    }
    
    private void layoutChildWithKeyline(final View view, int max, int max2) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(resolveKeylineGravity(layoutParams.gravity), max2);
        final int n = absoluteGravity & 0x7;
        final int n2 = absoluteGravity & 0x70;
        final int width = this.getWidth();
        final int height = this.getHeight();
        final int measuredWidth = view.getMeasuredWidth();
        final int measuredHeight = view.getMeasuredHeight();
        int n3 = max;
        if (max2 == 1) {
            n3 = width - max;
        }
        max = this.getKeyline(n3) - measuredWidth;
        max2 = 0;
        if (n != 1) {
            if (n == 5) {
                max += measuredWidth;
            }
        }
        else {
            max += measuredWidth / 2;
        }
        if (n2 != 16) {
            if (n2 == 80) {
                max2 = measuredHeight + 0;
            }
        }
        else {
            max2 = 0 + measuredHeight / 2;
        }
        max = Math.max(this.getPaddingLeft() + layoutParams.leftMargin, Math.min(max, width - this.getPaddingRight() - measuredWidth - layoutParams.rightMargin));
        max2 = Math.max(this.getPaddingTop() + layoutParams.topMargin, Math.min(max2, height - this.getPaddingBottom() - measuredHeight - layoutParams.bottomMargin));
        view.layout(max, max2, measuredWidth + max, measuredHeight + max2);
    }
    
    private MotionEvent obtainCancelEvent(MotionEvent obtain) {
        obtain = MotionEvent.obtain(obtain);
        obtain.setAction(3);
        return obtain;
    }
    
    private void offsetChildByInset(final View view, final Rect rect, int n) {
        if (!ViewCompat.isLaidOut(view)) {
            return;
        }
        if (view.getWidth() > 0) {
            if (view.getHeight() > 0) {
                final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
                final Behavior behavior = layoutParams.getBehavior();
                final Rect acquireTempRect = acquireTempRect();
                final Rect acquireTempRect2 = acquireTempRect();
                acquireTempRect2.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                if (behavior != null && behavior.getInsetDodgeRect(this, view, acquireTempRect)) {
                    if (!acquireTempRect2.contains(acquireTempRect)) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Rect should be within the child's bounds. Rect:");
                        sb.append(acquireTempRect.toShortString());
                        sb.append(" | Bounds:");
                        sb.append(acquireTempRect2.toShortString());
                        throw new IllegalArgumentException(sb.toString());
                    }
                }
                else {
                    acquireTempRect.set(acquireTempRect2);
                }
                releaseTempRect(acquireTempRect2);
                if (acquireTempRect.isEmpty()) {
                    releaseTempRect(acquireTempRect);
                    return;
                }
                final int absoluteGravity = GravityCompat.getAbsoluteGravity(layoutParams.dodgeInsetEdges, n);
                final int n2 = 1;
                Label_0254: {
                    if ((absoluteGravity & 0x30) == 0x30) {
                        n = acquireTempRect.top - layoutParams.topMargin - layoutParams.mInsetOffsetY;
                        final int top = rect.top;
                        if (n < top) {
                            this.setInsetOffsetY(view, top - n);
                            n = 1;
                            break Label_0254;
                        }
                    }
                    n = 0;
                }
                int n3 = n;
                if ((absoluteGravity & 0x50) == 0x50) {
                    final int n4 = this.getHeight() - acquireTempRect.bottom - layoutParams.bottomMargin + layoutParams.mInsetOffsetY;
                    final int bottom = rect.bottom;
                    n3 = n;
                    if (n4 < bottom) {
                        this.setInsetOffsetY(view, n4 - bottom);
                        n3 = 1;
                    }
                }
                if (n3 == 0) {
                    this.setInsetOffsetY(view, 0);
                }
                Label_0385: {
                    if ((absoluteGravity & 0x3) == 0x3) {
                        n = acquireTempRect.left - layoutParams.leftMargin - layoutParams.mInsetOffsetX;
                        final int left = rect.left;
                        if (n < left) {
                            this.setInsetOffsetX(view, left - n);
                            n = 1;
                            break Label_0385;
                        }
                    }
                    n = 0;
                }
                if ((absoluteGravity & 0x5) == 0x5) {
                    final int n5 = this.getWidth() - acquireTempRect.right - layoutParams.rightMargin + layoutParams.mInsetOffsetX;
                    final int right = rect.right;
                    if (n5 < right) {
                        this.setInsetOffsetX(view, n5 - right);
                        n = n2;
                    }
                }
                if (n == 0) {
                    this.setInsetOffsetX(view, 0);
                }
                releaseTempRect(acquireTempRect);
            }
        }
    }
    
    static Behavior parseBehavior(final Context context, final AttributeSet set, final String s) {
        if (TextUtils.isEmpty((CharSequence)s)) {
            return null;
        }
        String s2;
        if (s.startsWith(".")) {
            final StringBuilder sb = new StringBuilder();
            sb.append(context.getPackageName());
            sb.append(s);
            s2 = sb.toString();
        }
        else if (s.indexOf(46) >= 0) {
            s2 = s;
        }
        else {
            s2 = s;
            if (!TextUtils.isEmpty((CharSequence)CoordinatorLayout.WIDGET_PACKAGE_NAME)) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(CoordinatorLayout.WIDGET_PACKAGE_NAME);
                sb2.append('.');
                sb2.append(s);
                s2 = sb2.toString();
            }
        }
        try {
            Map<String, Constructor<Behavior>> value;
            if ((value = CoordinatorLayout.sConstructors.get()) == null) {
                value = new HashMap<String, Constructor<Behavior>>();
                CoordinatorLayout.sConstructors.set(value);
            }
            Constructor<?> constructor;
            if ((constructor = value.get(s2)) == null) {
                constructor = Class.forName(s2, false, context.getClassLoader()).getConstructor(CoordinatorLayout.CONSTRUCTOR_PARAMS);
                constructor.setAccessible(true);
                value.put(s2, (Constructor<Behavior>)constructor);
            }
            return constructor.newInstance(context, set);
        }
        catch (Exception cause) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("Could not inflate Behavior subclass ");
            sb3.append(s2);
            throw new RuntimeException(sb3.toString(), cause);
        }
    }
    
    private boolean performEvent(final Behavior behavior, final View view, final MotionEvent motionEvent, final int n) {
        if (n == 0) {
            return behavior.onInterceptTouchEvent(this, view, motionEvent);
        }
        if (n == 1) {
            return behavior.onTouchEvent(this, view, motionEvent);
        }
        throw new IllegalArgumentException();
    }
    
    private boolean performIntercept(final MotionEvent motionEvent, final int n) {
        final int actionMasked = motionEvent.getActionMasked();
        final List<View> mTempList1 = this.mTempList1;
        this.getTopSortedChildren(mTempList1);
        final int size = mTempList1.size();
        MotionEvent motionEvent2 = null;
        int n2 = 0;
        int n4;
        int n3 = n4 = n2;
        MotionEvent motionEvent3;
        int n5;
        while (true) {
            motionEvent3 = motionEvent2;
            n5 = n3;
            if (n2 >= size) {
                break;
            }
            final View mBehaviorTouchView = mTempList1.get(n2);
            final LayoutParams layoutParams = (LayoutParams)mBehaviorTouchView.getLayoutParams();
            final Behavior behavior = layoutParams.getBehavior();
            MotionEvent obtainCancelEvent;
            int n6;
            int n7;
            if ((n3 != 0 || n4 != 0) && actionMasked != 0) {
                obtainCancelEvent = motionEvent2;
                n6 = n3;
                n7 = n4;
                if (behavior != null) {
                    if ((obtainCancelEvent = motionEvent2) == null) {
                        obtainCancelEvent = this.obtainCancelEvent(motionEvent);
                    }
                    this.performEvent(behavior, mBehaviorTouchView, obtainCancelEvent, n);
                    n6 = n3;
                    n7 = n4;
                }
            }
            else {
                motionEvent3 = motionEvent2;
                n5 = n3;
                if (n4 == 0) {
                    motionEvent3 = motionEvent2;
                    if ((n5 = n3) == 0) {
                        motionEvent3 = motionEvent2;
                        n5 = n3;
                        if (behavior != null) {
                            final boolean performEvent = this.performEvent(behavior, mBehaviorTouchView, motionEvent, n);
                            motionEvent3 = motionEvent2;
                            if ((n5 = (performEvent ? 1 : 0)) != 0) {
                                this.mBehaviorTouchView = mBehaviorTouchView;
                                motionEvent3 = motionEvent2;
                                n5 = (performEvent ? 1 : 0);
                                if (actionMasked != 3) {
                                    motionEvent3 = motionEvent2;
                                    n5 = (performEvent ? 1 : 0);
                                    if (actionMasked != 1) {
                                        int n8 = 0;
                                        while (true) {
                                            motionEvent3 = motionEvent2;
                                            n5 = (performEvent ? 1 : 0);
                                            if (n8 >= n2) {
                                                break;
                                            }
                                            final View view = mTempList1.get(n8);
                                            final Behavior behavior2 = ((LayoutParams)view.getLayoutParams()).getBehavior();
                                            MotionEvent obtainCancelEvent2 = motionEvent2;
                                            if (behavior2 != null) {
                                                if ((obtainCancelEvent2 = motionEvent2) == null) {
                                                    obtainCancelEvent2 = this.obtainCancelEvent(motionEvent);
                                                }
                                                this.performEvent(behavior2, view, obtainCancelEvent2, n);
                                            }
                                            ++n8;
                                            motionEvent2 = obtainCancelEvent2;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                final boolean didBlockInteraction = layoutParams.didBlockInteraction();
                final boolean blockingInteractionBelow = layoutParams.isBlockingInteractionBelow(this, mBehaviorTouchView);
                final boolean b = blockingInteractionBelow && !didBlockInteraction;
                obtainCancelEvent = motionEvent3;
                n6 = n5;
                n7 = (b ? 1 : 0);
                if (blockingInteractionBelow) {
                    obtainCancelEvent = motionEvent3;
                    n6 = n5;
                    if ((n7 = (b ? 1 : 0)) == 0) {
                        break;
                    }
                }
            }
            ++n2;
            motionEvent2 = obtainCancelEvent;
            n3 = n6;
            n4 = n7;
        }
        mTempList1.clear();
        if (motionEvent3 != null) {
            motionEvent3.recycle();
        }
        return n5 != 0;
    }
    
    private void prepareChildren() {
        this.mDependencySortedChildren.clear();
        this.mChildDag.clear();
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            final LayoutParams resolvedLayoutParams = this.getResolvedLayoutParams(child);
            resolvedLayoutParams.findAnchorView(this, child);
            this.mChildDag.addNode(child);
            for (int j = 0; j < childCount; ++j) {
                if (j != i) {
                    final View child2 = this.getChildAt(j);
                    if (resolvedLayoutParams.dependsOn(this, child, child2)) {
                        if (!this.mChildDag.contains(child2)) {
                            this.mChildDag.addNode(child2);
                        }
                        this.mChildDag.addEdge(child2, child);
                    }
                }
            }
        }
        this.mDependencySortedChildren.addAll(this.mChildDag.getSortedList());
        Collections.reverse(this.mDependencySortedChildren);
    }
    
    private static void releaseTempRect(final Rect rect) {
        rect.setEmpty();
        CoordinatorLayout.sRectPool.release(rect);
    }
    
    private void resetTouchBehaviors() {
        final View mBehaviorTouchView = this.mBehaviorTouchView;
        if (mBehaviorTouchView != null) {
            final Behavior behavior = ((LayoutParams)mBehaviorTouchView.getLayoutParams()).getBehavior();
            if (behavior != null) {
                final long uptimeMillis = SystemClock.uptimeMillis();
                final MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                behavior.onTouchEvent(this, this.mBehaviorTouchView, obtain);
                obtain.recycle();
            }
            this.mBehaviorTouchView = null;
        }
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            ((LayoutParams)this.getChildAt(i).getLayoutParams()).resetTouchBehaviorTracking();
        }
        this.mDisallowInterceptReset = false;
    }
    
    private static int resolveAnchoredChildGravity(final int n) {
        int n2 = n;
        if (n == 0) {
            n2 = 17;
        }
        return n2;
    }
    
    private static int resolveGravity(int n) {
        int n2 = n;
        if ((n & 0x7) == 0x0) {
            n2 = (n | 0x800003);
        }
        n = n2;
        if ((n2 & 0x70) == 0x0) {
            n = (n2 | 0x30);
        }
        return n;
    }
    
    private static int resolveKeylineGravity(final int n) {
        int n2 = n;
        if (n == 0) {
            n2 = 8388661;
        }
        return n2;
    }
    
    private void setInsetOffsetX(final View view, final int mInsetOffsetX) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int mInsetOffsetX2 = layoutParams.mInsetOffsetX;
        if (mInsetOffsetX2 != mInsetOffsetX) {
            ViewCompat.offsetLeftAndRight(view, mInsetOffsetX - mInsetOffsetX2);
            layoutParams.mInsetOffsetX = mInsetOffsetX;
        }
    }
    
    private void setInsetOffsetY(final View view, final int mInsetOffsetY) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int mInsetOffsetY2 = layoutParams.mInsetOffsetY;
        if (mInsetOffsetY2 != mInsetOffsetY) {
            ViewCompat.offsetTopAndBottom(view, mInsetOffsetY - mInsetOffsetY2);
            layoutParams.mInsetOffsetY = mInsetOffsetY;
        }
    }
    
    private void setupForInsets() {
        if (Build$VERSION.SDK_INT < 21) {
            return;
        }
        if (ViewCompat.getFitsSystemWindows((View)this)) {
            if (this.mApplyWindowInsetsListener == null) {
                this.mApplyWindowInsetsListener = new OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(final View view, final WindowInsetsCompat windowInsets) {
                        return CoordinatorLayout.this.setWindowInsets(windowInsets);
                    }
                };
            }
            ViewCompat.setOnApplyWindowInsetsListener((View)this, this.mApplyWindowInsetsListener);
            this.setSystemUiVisibility(1280);
        }
        else {
            ViewCompat.setOnApplyWindowInsetsListener((View)this, null);
        }
    }
    
    void addPreDrawListener() {
        if (this.mIsAttachedToWindow) {
            if (this.mOnPreDrawListener == null) {
                this.mOnPreDrawListener = new OnPreDrawListener();
            }
            this.getViewTreeObserver().addOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this.mOnPreDrawListener);
        }
        this.mNeedsPreDrawListener = true;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams && super.checkLayoutParams(viewGroup$LayoutParams);
    }
    
    protected boolean drawChild(final Canvas canvas, final View view, final long n) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final Behavior mBehavior = layoutParams.mBehavior;
        if (mBehavior != null) {
            final float scrimOpacity = mBehavior.getScrimOpacity(this, view);
            if (scrimOpacity > 0.0f) {
                if (this.mScrimPaint == null) {
                    this.mScrimPaint = new Paint();
                }
                this.mScrimPaint.setColor(layoutParams.mBehavior.getScrimColor(this, view));
                this.mScrimPaint.setAlpha(clamp(Math.round(scrimOpacity * 255.0f), 0, 255));
                final int save = canvas.save();
                if (view.isOpaque()) {
                    canvas.clipRect((float)view.getLeft(), (float)view.getTop(), (float)view.getRight(), (float)view.getBottom(), Region$Op.DIFFERENCE);
                }
                canvas.drawRect((float)this.getPaddingLeft(), (float)this.getPaddingTop(), (float)(this.getWidth() - this.getPaddingRight()), (float)(this.getHeight() - this.getPaddingBottom()), this.mScrimPaint);
                canvas.restoreToCount(save);
            }
        }
        return super.drawChild(canvas, view, n);
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final int[] drawableState = this.getDrawableState();
        final Drawable mStatusBarBackground = this.mStatusBarBackground;
        int n = 0;
        if (mStatusBarBackground != null) {
            n = n;
            if (mStatusBarBackground.isStateful()) {
                n = ((false | mStatusBarBackground.setState(drawableState)) ? 1 : 0);
            }
        }
        if (n != 0) {
            this.invalidate();
        }
    }
    
    void ensurePreDrawListener() {
        final int childCount = this.getChildCount();
        final boolean b = false;
        int n = 0;
        boolean b2;
        while (true) {
            b2 = b;
            if (n >= childCount) {
                break;
            }
            if (this.hasDependencies(this.getChildAt(n))) {
                b2 = true;
                break;
            }
            ++n;
        }
        if (b2 != this.mNeedsPreDrawListener) {
            if (b2) {
                this.addPreDrawListener();
            }
            else {
                this.removePreDrawListener();
            }
        }
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
        if (viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams) {
            return new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
        }
        return new LayoutParams(viewGroup$LayoutParams);
    }
    
    void getChildRect(final View view, final boolean b, final Rect rect) {
        if (!view.isLayoutRequested() && view.getVisibility() != 8) {
            if (b) {
                this.getDescendantRect(view, rect);
            }
            else {
                rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            }
            return;
        }
        rect.setEmpty();
    }
    
    final List<View> getDependencySortedChildren() {
        this.prepareChildren();
        return Collections.unmodifiableList((List<? extends View>)this.mDependencySortedChildren);
    }
    
    void getDescendantRect(final View view, final Rect rect) {
        ViewGroupUtils.getDescendantRect(this, view, rect);
    }
    
    void getDesiredAnchoredChildRect(final View view, final int n, final Rect rect, final Rect rect2) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int measuredWidth = view.getMeasuredWidth();
        final int measuredHeight = view.getMeasuredHeight();
        this.getDesiredAnchoredChildRectWithoutConstraints(n, rect, rect2, layoutParams, measuredWidth, measuredHeight);
        this.constrainChildRect(layoutParams, rect2, measuredWidth, measuredHeight);
    }
    
    void getLastChildRect(final View view, final Rect rect) {
        rect.set(((LayoutParams)view.getLayoutParams()).getLastChildRect());
    }
    
    public int getNestedScrollAxes() {
        return this.mNestedScrollingParentHelper.getNestedScrollAxes();
    }
    
    LayoutParams getResolvedLayoutParams(View o) {
        final LayoutParams layoutParams = (LayoutParams)((View)o).getLayoutParams();
        if (!layoutParams.mBehaviorResolved) {
            if (o instanceof AttachedBehavior) {
                final Behavior behavior = ((AttachedBehavior)o).getBehavior();
                if (behavior == null) {
                    Log.e("CoordinatorLayout", "Attached behavior class is null");
                }
                layoutParams.setBehavior(behavior);
                layoutParams.mBehaviorResolved = true;
            }
            else {
                Class<?> clazz = o.getClass();
                o = null;
                while (clazz != null) {
                    final DefaultBehavior defaultBehavior = clazz.getAnnotation(DefaultBehavior.class);
                    if ((o = defaultBehavior) != null) {
                        break;
                    }
                    clazz = clazz.getSuperclass();
                    o = defaultBehavior;
                }
                if (o != null) {
                    try {
                        layoutParams.setBehavior((Behavior)((DefaultBehavior)o).value().getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]));
                    }
                    catch (Exception ex) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Default behavior class ");
                        sb.append(((DefaultBehavior)o).value().getName());
                        sb.append(" could not be instantiated. Did you forget a default constructor?");
                        Log.e("CoordinatorLayout", sb.toString(), (Throwable)ex);
                    }
                }
                layoutParams.mBehaviorResolved = true;
            }
        }
        return layoutParams;
    }
    
    protected int getSuggestedMinimumHeight() {
        return Math.max(super.getSuggestedMinimumHeight(), this.getPaddingTop() + this.getPaddingBottom());
    }
    
    protected int getSuggestedMinimumWidth() {
        return Math.max(super.getSuggestedMinimumWidth(), this.getPaddingLeft() + this.getPaddingRight());
    }
    
    void offsetChildToAnchor(final View view, int n) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.mAnchorView != null) {
            final Rect acquireTempRect = acquireTempRect();
            final Rect acquireTempRect2 = acquireTempRect();
            final Rect acquireTempRect3 = acquireTempRect();
            this.getDescendantRect(layoutParams.mAnchorView, acquireTempRect);
            final int n2 = 0;
            this.getChildRect(view, false, acquireTempRect2);
            final int measuredWidth = view.getMeasuredWidth();
            final int measuredHeight = view.getMeasuredHeight();
            this.getDesiredAnchoredChildRectWithoutConstraints(n, acquireTempRect, acquireTempRect3, layoutParams, measuredWidth, measuredHeight);
            Label_0108: {
                if (acquireTempRect3.left == acquireTempRect2.left) {
                    n = n2;
                    if (acquireTempRect3.top == acquireTempRect2.top) {
                        break Label_0108;
                    }
                }
                n = 1;
            }
            this.constrainChildRect(layoutParams, acquireTempRect3, measuredWidth, measuredHeight);
            final int n3 = acquireTempRect3.left - acquireTempRect2.left;
            final int n4 = acquireTempRect3.top - acquireTempRect2.top;
            if (n3 != 0) {
                ViewCompat.offsetLeftAndRight(view, n3);
            }
            if (n4 != 0) {
                ViewCompat.offsetTopAndBottom(view, n4);
            }
            if (n != 0) {
                final Behavior behavior = layoutParams.getBehavior();
                if (behavior != null) {
                    behavior.onDependentViewChanged(this, view, layoutParams.mAnchorView);
                }
            }
            releaseTempRect(acquireTempRect);
            releaseTempRect(acquireTempRect2);
            releaseTempRect(acquireTempRect3);
        }
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.resetTouchBehaviors();
        if (this.mNeedsPreDrawListener) {
            if (this.mOnPreDrawListener == null) {
                this.mOnPreDrawListener = new OnPreDrawListener();
            }
            this.getViewTreeObserver().addOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this.mOnPreDrawListener);
        }
        if (this.mLastInsets == null && ViewCompat.getFitsSystemWindows((View)this)) {
            ViewCompat.requestApplyInsets((View)this);
        }
        this.mIsAttachedToWindow = true;
    }
    
    final void onChildViewsChanged(final int n) {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        final int size = this.mDependencySortedChildren.size();
        final Rect acquireTempRect = acquireTempRect();
        final Rect acquireTempRect2 = acquireTempRect();
        final Rect acquireTempRect3 = acquireTempRect();
        for (int i = 0; i < size; ++i) {
            final View view = this.mDependencySortedChildren.get(i);
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            if (n != 0 || view.getVisibility() != 8) {
                for (int j = 0; j < i; ++j) {
                    if (layoutParams.mAnchorDirectChild == this.mDependencySortedChildren.get(j)) {
                        this.offsetChildToAnchor(view, layoutDirection);
                    }
                }
                this.getChildRect(view, true, acquireTempRect2);
                if (layoutParams.insetEdge != 0 && !acquireTempRect2.isEmpty()) {
                    final int absoluteGravity = GravityCompat.getAbsoluteGravity(layoutParams.insetEdge, layoutDirection);
                    final int n2 = absoluteGravity & 0x70;
                    if (n2 != 48) {
                        if (n2 == 80) {
                            acquireTempRect.bottom = Math.max(acquireTempRect.bottom, this.getHeight() - acquireTempRect2.top);
                        }
                    }
                    else {
                        acquireTempRect.top = Math.max(acquireTempRect.top, acquireTempRect2.bottom);
                    }
                    final int n3 = absoluteGravity & 0x7;
                    if (n3 != 3) {
                        if (n3 == 5) {
                            acquireTempRect.right = Math.max(acquireTempRect.right, this.getWidth() - acquireTempRect2.left);
                        }
                    }
                    else {
                        acquireTempRect.left = Math.max(acquireTempRect.left, acquireTempRect2.right);
                    }
                }
                if (layoutParams.dodgeInsetEdges != 0 && view.getVisibility() == 0) {
                    this.offsetChildByInset(view, acquireTempRect, layoutDirection);
                }
                if (n != 2) {
                    this.getLastChildRect(view, acquireTempRect3);
                    if (acquireTempRect3.equals((Object)acquireTempRect2)) {
                        continue;
                    }
                    this.recordLastChildRect(view, acquireTempRect2);
                }
                for (int k = i + 1; k < size; ++k) {
                    final View view2 = this.mDependencySortedChildren.get(k);
                    final LayoutParams layoutParams2 = (LayoutParams)view2.getLayoutParams();
                    final Behavior behavior = layoutParams2.getBehavior();
                    if (behavior != null && behavior.layoutDependsOn(this, view2, view)) {
                        if (n == 0 && layoutParams2.getChangedAfterNestedScroll()) {
                            layoutParams2.resetChangedAfterNestedScroll();
                        }
                        else {
                            boolean onDependentViewChanged;
                            if (n != 2) {
                                onDependentViewChanged = behavior.onDependentViewChanged(this, view2, view);
                            }
                            else {
                                behavior.onDependentViewRemoved(this, view2, view);
                                onDependentViewChanged = true;
                            }
                            if (n == 1) {
                                layoutParams2.setChangedAfterNestedScroll(onDependentViewChanged);
                            }
                        }
                    }
                }
            }
        }
        releaseTempRect(acquireTempRect);
        releaseTempRect(acquireTempRect2);
        releaseTempRect(acquireTempRect3);
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.resetTouchBehaviors();
        if (this.mNeedsPreDrawListener && this.mOnPreDrawListener != null) {
            this.getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this.mOnPreDrawListener);
        }
        final View mNestedScrollingTarget = this.mNestedScrollingTarget;
        if (mNestedScrollingTarget != null) {
            this.onStopNestedScroll(mNestedScrollingTarget);
        }
        this.mIsAttachedToWindow = false;
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawStatusBarBackground && this.mStatusBarBackground != null) {
            final WindowInsetsCompat mLastInsets = this.mLastInsets;
            int systemWindowInsetTop;
            if (mLastInsets != null) {
                systemWindowInsetTop = mLastInsets.getSystemWindowInsetTop();
            }
            else {
                systemWindowInsetTop = 0;
            }
            if (systemWindowInsetTop > 0) {
                this.mStatusBarBackground.setBounds(0, 0, this.getWidth(), systemWindowInsetTop);
                this.mStatusBarBackground.draw(canvas);
            }
        }
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.resetTouchBehaviors();
        }
        final boolean performIntercept = this.performIntercept(motionEvent, 0);
        if (actionMasked == 1 || actionMasked == 3) {
            this.mBehaviorTouchView = null;
            this.resetTouchBehaviors();
        }
        return performIntercept;
    }
    
    protected void onLayout(final boolean b, int i, int size, int layoutDirection, final int n) {
        layoutDirection = ViewCompat.getLayoutDirection((View)this);
        View view;
        Behavior behavior;
        for (size = this.mDependencySortedChildren.size(), i = 0; i < size; ++i) {
            view = this.mDependencySortedChildren.get(i);
            if (view.getVisibility() != 8) {
                behavior = ((LayoutParams)view.getLayoutParams()).getBehavior();
                if (behavior == null || !behavior.onLayoutChild(this, view, layoutDirection)) {
                    this.onLayoutChild(view, layoutDirection);
                }
            }
        }
    }
    
    public void onLayoutChild(final View view, final int n) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (!layoutParams.checkAnchorChanged()) {
            final View mAnchorView = layoutParams.mAnchorView;
            if (mAnchorView != null) {
                this.layoutChildWithAnchor(view, mAnchorView, n);
            }
            else {
                final int keyline = layoutParams.keyline;
                if (keyline >= 0) {
                    this.layoutChildWithKeyline(view, keyline, n);
                }
                else {
                    this.layoutChild(view, n);
                }
            }
            return;
        }
        throw new IllegalStateException("An anchor may not be changed after CoordinatorLayout measurement begins before layout is complete.");
    }
    
    protected void onMeasure(final int n, final int n2) {
        this.prepareChildren();
        this.ensurePreDrawListener();
        final int paddingLeft = this.getPaddingLeft();
        final int paddingTop = this.getPaddingTop();
        final int paddingRight = this.getPaddingRight();
        final int paddingBottom = this.getPaddingBottom();
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        final boolean b = layoutDirection == 1;
        final int mode = View$MeasureSpec.getMode(n);
        final int size = View$MeasureSpec.getSize(n);
        final int mode2 = View$MeasureSpec.getMode(n2);
        final int size2 = View$MeasureSpec.getSize(n2);
        int a = this.getSuggestedMinimumWidth();
        int a2 = this.getSuggestedMinimumHeight();
        final boolean b2 = this.mLastInsets != null && ViewCompat.getFitsSystemWindows((View)this);
        final int size3 = this.mDependencySortedChildren.size();
        int combineMeasuredStates = 0;
        int n3 = 0;
        int n4 = paddingLeft;
        while (true) {
            final int n5 = n4;
            if (n3 >= size3) {
                break;
            }
            final View view = this.mDependencySortedChildren.get(n3);
            if (view.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
                final int keyline = layoutParams.keyline;
                int n7 = 0;
                Label_0302: {
                    if (keyline >= 0 && mode != 0) {
                        final int keyline2 = this.getKeyline(keyline);
                        final int n6 = GravityCompat.getAbsoluteGravity(resolveKeylineGravity(layoutParams.gravity), layoutDirection) & 0x7;
                        if ((n6 == 3 && !b) || (n6 == 5 && b)) {
                            n7 = Math.max(0, size - paddingRight - keyline2);
                            break Label_0302;
                        }
                        if ((n6 == 5 && !b) || (n6 == 3 && b)) {
                            n7 = Math.max(0, keyline2 - n5);
                            break Label_0302;
                        }
                    }
                    n7 = 0;
                }
                final int n8 = combineMeasuredStates;
                int measureSpec;
                int measureSpec2;
                if (b2 && !ViewCompat.getFitsSystemWindows(view)) {
                    final int systemWindowInsetLeft = this.mLastInsets.getSystemWindowInsetLeft();
                    final int systemWindowInsetRight = this.mLastInsets.getSystemWindowInsetRight();
                    final int systemWindowInsetTop = this.mLastInsets.getSystemWindowInsetTop();
                    final int systemWindowInsetBottom = this.mLastInsets.getSystemWindowInsetBottom();
                    measureSpec = View$MeasureSpec.makeMeasureSpec(size - (systemWindowInsetLeft + systemWindowInsetRight), mode);
                    measureSpec2 = View$MeasureSpec.makeMeasureSpec(size2 - (systemWindowInsetTop + systemWindowInsetBottom), mode2);
                }
                else {
                    measureSpec = n;
                    measureSpec2 = n2;
                }
                final Behavior behavior = layoutParams.getBehavior();
                if (behavior == null || !behavior.onMeasureChild(this, view, measureSpec, n7, measureSpec2, 0)) {
                    this.onMeasureChild(view, measureSpec, n7, measureSpec2, 0);
                }
                a = Math.max(a, paddingLeft + paddingRight + view.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
                a2 = Math.max(a2, paddingTop + paddingBottom + view.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
                combineMeasuredStates = View.combineMeasuredStates(n8, view.getMeasuredState());
            }
            ++n3;
            n4 = n5;
        }
        this.setMeasuredDimension(View.resolveSizeAndState(a, n, 0xFF000000 & combineMeasuredStates), View.resolveSizeAndState(a2, n2, combineMeasuredStates << 16));
    }
    
    public void onMeasureChild(final View view, final int n, final int n2, final int n3, final int n4) {
        this.measureChildWithMargins(view, n, n2, n3, n4);
    }
    
    public boolean onNestedFling(final View view, final float n, final float n2, final boolean b) {
        boolean b2;
        boolean b3;
        for (int childCount = this.getChildCount(), i = (b2 = false) ? 1 : 0; i < childCount; ++i, b2 = b3) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 8) {
                b3 = b2;
            }
            else {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (!layoutParams.isNestedScrollAccepted(0)) {
                    b3 = b2;
                }
                else {
                    final Behavior behavior = layoutParams.getBehavior();
                    b3 = b2;
                    if (behavior != null) {
                        b3 = (b2 | behavior.onNestedFling(this, child, view, n, n2, b));
                    }
                }
            }
        }
        if (b2) {
            this.onChildViewsChanged(1);
        }
        return b2;
    }
    
    public boolean onNestedPreFling(final View view, final float n, final float n2) {
        boolean b;
        boolean b2;
        for (int childCount = this.getChildCount(), i = (b = false) ? 1 : 0; i < childCount; ++i, b = b2) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 8) {
                b2 = b;
            }
            else {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (!layoutParams.isNestedScrollAccepted(0)) {
                    b2 = b;
                }
                else {
                    final Behavior behavior = layoutParams.getBehavior();
                    b2 = b;
                    if (behavior != null) {
                        b2 = (b | behavior.onNestedPreFling(this, child, view, n, n2));
                    }
                }
            }
        }
        return b;
    }
    
    public void onNestedPreScroll(final View view, final int n, final int n2, final int[] array) {
        this.onNestedPreScroll(view, n, n2, array, 0);
    }
    
    public void onNestedPreScroll(final View view, final int n, final int n2, final int[] array, final int n3) {
        final int childCount = this.getChildCount();
        final int n5;
        int n4 = n5 = 0;
        int n7;
        int n6 = n7 = n5;
        int n8;
        int n9;
        for (int i = n5; i < childCount; ++i, n7 = n8, n6 = n9) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 8) {
                n8 = n7;
                n9 = n6;
            }
            else {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (!layoutParams.isNestedScrollAccepted(n3)) {
                    n8 = n7;
                    n9 = n6;
                }
                else {
                    final Behavior behavior = layoutParams.getBehavior();
                    n8 = n7;
                    n9 = n6;
                    if (behavior != null) {
                        final int[] mBehaviorConsumed = this.mBehaviorConsumed;
                        mBehaviorConsumed[1] = (mBehaviorConsumed[0] = 0);
                        behavior.onNestedPreScroll(this, child, view, n, n2, mBehaviorConsumed, n3);
                        int n10;
                        if (n > 0) {
                            n10 = Math.max(n7, this.mBehaviorConsumed[0]);
                        }
                        else {
                            n10 = Math.min(n7, this.mBehaviorConsumed[0]);
                        }
                        n8 = n10;
                        if (n2 > 0) {
                            n9 = Math.max(n6, this.mBehaviorConsumed[1]);
                        }
                        else {
                            n9 = Math.min(n6, this.mBehaviorConsumed[1]);
                        }
                        n4 = 1;
                    }
                }
            }
        }
        array[0] = n7;
        array[1] = n6;
        if (n4 != 0) {
            this.onChildViewsChanged(1);
        }
    }
    
    public void onNestedScroll(final View view, final int n, final int n2, final int n3, final int n4) {
        this.onNestedScroll(view, n, n2, n3, n4, 0);
    }
    
    public void onNestedScroll(final View view, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.onNestedScroll(view, n, n2, n3, n4, 0, this.mNestedScrollingV2ConsumedCompat);
    }
    
    public void onNestedScroll(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int[] array) {
        final int childCount = this.getChildCount();
        final int n7;
        final int n6 = n7 = 0;
        int n9;
        int n8 = n9 = n7;
        int i = n7;
        int n10 = n6;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            int n11;
            int n12;
            if (child.getVisibility() == 8) {
                n11 = n9;
                n12 = n8;
            }
            else {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (!layoutParams.isNestedScrollAccepted(n5)) {
                    n11 = n9;
                    n12 = n8;
                }
                else {
                    final Behavior behavior = layoutParams.getBehavior();
                    n11 = n9;
                    n12 = n8;
                    if (behavior != null) {
                        final int[] mBehaviorConsumed = this.mBehaviorConsumed;
                        mBehaviorConsumed[1] = (mBehaviorConsumed[0] = 0);
                        behavior.onNestedScroll(this, child, view, n, n2, n3, n4, n5, mBehaviorConsumed);
                        int n13;
                        if (n3 > 0) {
                            n13 = Math.max(n9, this.mBehaviorConsumed[0]);
                        }
                        else {
                            n13 = Math.min(n9, this.mBehaviorConsumed[0]);
                        }
                        final int n14 = n13;
                        if (n4 > 0) {
                            n12 = Math.max(n8, this.mBehaviorConsumed[1]);
                        }
                        else {
                            n12 = Math.min(n8, this.mBehaviorConsumed[1]);
                        }
                        n10 = 1;
                        n11 = n14;
                    }
                }
            }
            ++i;
            n9 = n11;
            n8 = n12;
        }
        array[0] += n9;
        array[1] += n8;
        if (n10 != 0) {
            this.onChildViewsChanged(1);
        }
    }
    
    public void onNestedScrollAccepted(final View view, final View view2, final int n) {
        this.onNestedScrollAccepted(view, view2, n, 0);
    }
    
    public void onNestedScrollAccepted(final View view, final View mNestedScrollingTarget, final int n, final int n2) {
        this.mNestedScrollingParentHelper.onNestedScrollAccepted(view, mNestedScrollingTarget, n, n2);
        this.mNestedScrollingTarget = mNestedScrollingTarget;
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            if (layoutParams.isNestedScrollAccepted(n2)) {
                final Behavior behavior = layoutParams.getBehavior();
                if (behavior != null) {
                    behavior.onNestedScrollAccepted(this, child, view, mNestedScrollingTarget, n, n2);
                }
            }
        }
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        final SparseArray<Parcelable> behaviorStates = savedState.behaviorStates;
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            final int id = child.getId();
            final Behavior behavior = this.getResolvedLayoutParams(child).getBehavior();
            if (id != -1 && behavior != null) {
                final Parcelable parcelable2 = (Parcelable)behaviorStates.get(id);
                if (parcelable2 != null) {
                    behavior.onRestoreInstanceState(this, child, parcelable2);
                }
            }
        }
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        final SparseArray behaviorStates = new SparseArray();
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            final int id = child.getId();
            final Behavior behavior = ((LayoutParams)child.getLayoutParams()).getBehavior();
            if (id != -1 && behavior != null) {
                final Parcelable onSaveInstanceState = behavior.onSaveInstanceState(this, child);
                if (onSaveInstanceState != null) {
                    behaviorStates.append(id, (Object)onSaveInstanceState);
                }
            }
        }
        savedState.behaviorStates = (SparseArray<Parcelable>)behaviorStates;
        return (Parcelable)savedState;
    }
    
    public boolean onStartNestedScroll(final View view, final View view2, final int n) {
        return this.onStartNestedScroll(view, view2, n, 0);
    }
    
    public boolean onStartNestedScroll(final View view, final View view2, final int n, final int n2) {
        boolean b;
        for (int childCount = this.getChildCount(), i = (b = false) ? 1 : 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                final Behavior behavior = layoutParams.getBehavior();
                if (behavior != null) {
                    final boolean onStartNestedScroll = behavior.onStartNestedScroll(this, child, view, view2, n, n2);
                    b |= onStartNestedScroll;
                    layoutParams.setNestedScrollAccepted(n2, onStartNestedScroll);
                }
                else {
                    layoutParams.setNestedScrollAccepted(n2, false);
                }
            }
        }
        return b;
    }
    
    public void onStopNestedScroll(final View view) {
        this.onStopNestedScroll(view, 0);
    }
    
    public void onStopNestedScroll(final View view, final int n) {
        this.mNestedScrollingParentHelper.onStopNestedScroll(view, n);
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            if (layoutParams.isNestedScrollAccepted(n)) {
                final Behavior behavior = layoutParams.getBehavior();
                if (behavior != null) {
                    behavior.onStopNestedScroll(this, child, view, n);
                }
                layoutParams.resetNestedScroll(n);
                layoutParams.resetChangedAfterNestedScroll();
            }
        }
        this.mNestedScrollingTarget = null;
    }
    
    public boolean onTouchEvent(MotionEvent obtainCancelEvent) {
        final int actionMasked = obtainCancelEvent.getActionMasked();
        final View mBehaviorTouchView = this.mBehaviorTouchView;
        final boolean b = false;
        int n;
        int n2;
        if (mBehaviorTouchView != null) {
            final Behavior behavior = ((LayoutParams)mBehaviorTouchView.getLayoutParams()).getBehavior();
            if (behavior != null) {
                n = (behavior.onTouchEvent(this, this.mBehaviorTouchView, obtainCancelEvent) ? 1 : 0);
                n2 = (b ? 1 : 0);
            }
            else {
                n = 0;
                n2 = (b ? 1 : 0);
            }
        }
        else {
            final boolean b2 = (n = (this.performIntercept(obtainCancelEvent, 1) ? 1 : 0)) != 0;
            n2 = (b ? 1 : 0);
            if (actionMasked != 0) {
                n = (b2 ? 1 : 0);
                n2 = (b ? 1 : 0);
                if (b2) {
                    n2 = 1;
                    n = (b2 ? 1 : 0);
                }
            }
        }
        boolean b3;
        if (this.mBehaviorTouchView != null && actionMasked != 3) {
            b3 = (n != 0);
            if (n2 != 0) {
                obtainCancelEvent = this.obtainCancelEvent(obtainCancelEvent);
                super.onTouchEvent(obtainCancelEvent);
                obtainCancelEvent.recycle();
                b3 = (n != 0);
            }
        }
        else {
            b3 = ((n | (super.onTouchEvent(obtainCancelEvent) ? 1 : 0)) != 0x0);
        }
        if (actionMasked == 1 || actionMasked == 3) {
            this.mBehaviorTouchView = null;
            this.resetTouchBehaviors();
        }
        return b3;
    }
    
    void recordLastChildRect(final View view, final Rect lastChildRect) {
        ((LayoutParams)view.getLayoutParams()).setLastChildRect(lastChildRect);
    }
    
    void removePreDrawListener() {
        if (this.mIsAttachedToWindow && this.mOnPreDrawListener != null) {
            this.getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this.mOnPreDrawListener);
        }
        this.mNeedsPreDrawListener = false;
    }
    
    public boolean requestChildRectangleOnScreen(final View view, final Rect rect, final boolean b) {
        final Behavior behavior = ((LayoutParams)view.getLayoutParams()).getBehavior();
        return (behavior != null && behavior.onRequestChildRectangleOnScreen(this, view, rect, b)) || super.requestChildRectangleOnScreen(view, rect, b);
    }
    
    public void requestDisallowInterceptTouchEvent(final boolean b) {
        super.requestDisallowInterceptTouchEvent(b);
        if (b && !this.mDisallowInterceptReset) {
            if (this.mBehaviorTouchView == null) {
                this.cancelInterceptBehaviors();
            }
            this.resetTouchBehaviors();
            this.mDisallowInterceptReset = true;
        }
    }
    
    public void setFitsSystemWindows(final boolean fitsSystemWindows) {
        super.setFitsSystemWindows(fitsSystemWindows);
        this.setupForInsets();
    }
    
    public void setOnHierarchyChangeListener(final ViewGroup$OnHierarchyChangeListener mOnHierarchyChangeListener) {
        this.mOnHierarchyChangeListener = mOnHierarchyChangeListener;
    }
    
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
        final boolean b = visibility == 0;
        final Drawable mStatusBarBackground = this.mStatusBarBackground;
        if (mStatusBarBackground != null && mStatusBarBackground.isVisible() != b) {
            this.mStatusBarBackground.setVisible(b, false);
        }
    }
    
    final WindowInsetsCompat setWindowInsets(final WindowInsetsCompat mLastInsets) {
        WindowInsetsCompat dispatchApplyWindowInsetsToBehaviors = mLastInsets;
        if (!ObjectsCompat.equals(this.mLastInsets, mLastInsets)) {
            this.mLastInsets = mLastInsets;
            final boolean b = true;
            final boolean mDrawStatusBarBackground = mLastInsets != null && mLastInsets.getSystemWindowInsetTop() > 0;
            this.mDrawStatusBarBackground = mDrawStatusBarBackground;
            this.setWillNotDraw(!mDrawStatusBarBackground && this.getBackground() == null && b);
            dispatchApplyWindowInsetsToBehaviors = this.dispatchApplyWindowInsetsToBehaviors(mLastInsets);
            this.requestLayout();
        }
        return dispatchApplyWindowInsetsToBehaviors;
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mStatusBarBackground;
    }
    
    public interface AttachedBehavior
    {
        Behavior getBehavior();
    }
    
    public abstract static class Behavior<V extends View>
    {
        public Behavior() {
        }
        
        public Behavior(final Context context, final AttributeSet set) {
        }
        
        public boolean blocksInteractionBelow(final CoordinatorLayout coordinatorLayout, final V v) {
            return this.getScrimOpacity(coordinatorLayout, v) > 0.0f;
        }
        
        public boolean getInsetDodgeRect(final CoordinatorLayout coordinatorLayout, final V v, final Rect rect) {
            return false;
        }
        
        public int getScrimColor(final CoordinatorLayout coordinatorLayout, final V v) {
            return -16777216;
        }
        
        public float getScrimOpacity(final CoordinatorLayout coordinatorLayout, final V v) {
            return 0.0f;
        }
        
        public boolean layoutDependsOn(final CoordinatorLayout coordinatorLayout, final V v, final View view) {
            return false;
        }
        
        public WindowInsetsCompat onApplyWindowInsets(final CoordinatorLayout coordinatorLayout, final V v, final WindowInsetsCompat windowInsetsCompat) {
            return windowInsetsCompat;
        }
        
        public void onAttachedToLayoutParams(final LayoutParams layoutParams) {
        }
        
        public boolean onDependentViewChanged(final CoordinatorLayout coordinatorLayout, final V v, final View view) {
            return false;
        }
        
        public void onDependentViewRemoved(final CoordinatorLayout coordinatorLayout, final V v, final View view) {
        }
        
        public void onDetachedFromLayoutParams() {
        }
        
        public boolean onInterceptTouchEvent(final CoordinatorLayout coordinatorLayout, final V v, final MotionEvent motionEvent) {
            return false;
        }
        
        public boolean onLayoutChild(final CoordinatorLayout coordinatorLayout, final V v, final int n) {
            return false;
        }
        
        public boolean onMeasureChild(final CoordinatorLayout coordinatorLayout, final V v, final int n, final int n2, final int n3, final int n4) {
            return false;
        }
        
        public boolean onNestedFling(final CoordinatorLayout coordinatorLayout, final V v, final View view, final float n, final float n2, final boolean b) {
            return false;
        }
        
        public boolean onNestedPreFling(final CoordinatorLayout coordinatorLayout, final V v, final View view, final float n, final float n2) {
            return false;
        }
        
        @Deprecated
        public void onNestedPreScroll(final CoordinatorLayout coordinatorLayout, final V v, final View view, final int n, final int n2, final int[] array) {
        }
        
        public void onNestedPreScroll(final CoordinatorLayout coordinatorLayout, final V v, final View view, final int n, final int n2, final int[] array, final int n3) {
            if (n3 == 0) {
                this.onNestedPreScroll(coordinatorLayout, v, view, n, n2, array);
            }
        }
        
        @Deprecated
        public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final V v, final View view, final int n, final int n2, final int n3, final int n4) {
        }
        
        @Deprecated
        public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final V v, final View view, final int n, final int n2, final int n3, final int n4, final int n5) {
            if (n5 == 0) {
                this.onNestedScroll(coordinatorLayout, v, view, n, n2, n3, n4);
            }
        }
        
        public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final V v, final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int[] array) {
            array[0] += n3;
            array[1] += n4;
            this.onNestedScroll(coordinatorLayout, v, view, n, n2, n3, n4, n5);
        }
        
        @Deprecated
        public void onNestedScrollAccepted(final CoordinatorLayout coordinatorLayout, final V v, final View view, final View view2, final int n) {
        }
        
        public void onNestedScrollAccepted(final CoordinatorLayout coordinatorLayout, final V v, final View view, final View view2, final int n, final int n2) {
            if (n2 == 0) {
                this.onNestedScrollAccepted(coordinatorLayout, v, view, view2, n);
            }
        }
        
        public boolean onRequestChildRectangleOnScreen(final CoordinatorLayout coordinatorLayout, final V v, final Rect rect, final boolean b) {
            return false;
        }
        
        public void onRestoreInstanceState(final CoordinatorLayout coordinatorLayout, final V v, final Parcelable parcelable) {
        }
        
        public Parcelable onSaveInstanceState(final CoordinatorLayout coordinatorLayout, final V v) {
            return (Parcelable)View$BaseSavedState.EMPTY_STATE;
        }
        
        @Deprecated
        public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final V v, final View view, final View view2, final int n) {
            return false;
        }
        
        public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final V v, final View view, final View view2, final int n, final int n2) {
            return n2 == 0 && this.onStartNestedScroll(coordinatorLayout, v, view, view2, n);
        }
        
        @Deprecated
        public void onStopNestedScroll(final CoordinatorLayout coordinatorLayout, final V v, final View view) {
        }
        
        public void onStopNestedScroll(final CoordinatorLayout coordinatorLayout, final V v, final View view, final int n) {
            if (n == 0) {
                this.onStopNestedScroll(coordinatorLayout, v, view);
            }
        }
        
        public boolean onTouchEvent(final CoordinatorLayout coordinatorLayout, final V v, final MotionEvent motionEvent) {
            return false;
        }
    }
    
    @Deprecated
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DefaultBehavior {
        Class<? extends Behavior> value();
    }
    
    private class HierarchyChangeListener implements ViewGroup$OnHierarchyChangeListener
    {
        HierarchyChangeListener() {
        }
        
        public void onChildViewAdded(final View view, final View view2) {
            final ViewGroup$OnHierarchyChangeListener mOnHierarchyChangeListener = CoordinatorLayout.this.mOnHierarchyChangeListener;
            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(view, view2);
            }
        }
        
        public void onChildViewRemoved(final View view, final View view2) {
            CoordinatorLayout.this.onChildViewsChanged(2);
            final ViewGroup$OnHierarchyChangeListener mOnHierarchyChangeListener = CoordinatorLayout.this.mOnHierarchyChangeListener;
            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(view, view2);
            }
        }
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        public int anchorGravity;
        public int dodgeInsetEdges;
        public int gravity;
        public int insetEdge;
        public int keyline;
        View mAnchorDirectChild;
        int mAnchorId;
        View mAnchorView;
        Behavior mBehavior;
        boolean mBehaviorResolved;
        private boolean mDidAcceptNestedScrollNonTouch;
        private boolean mDidAcceptNestedScrollTouch;
        private boolean mDidBlockInteraction;
        private boolean mDidChangeAfterNestedScroll;
        int mInsetOffsetX;
        int mInsetOffsetY;
        final Rect mLastChildRect;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.mBehaviorResolved = false;
            this.gravity = 0;
            this.anchorGravity = 0;
            this.keyline = -1;
            this.mAnchorId = -1;
            this.insetEdge = 0;
            this.dodgeInsetEdges = 0;
            this.mLastChildRect = new Rect();
        }
        
        LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.mBehaviorResolved = false;
            this.gravity = 0;
            this.anchorGravity = 0;
            this.keyline = -1;
            this.mAnchorId = -1;
            this.insetEdge = 0;
            this.dodgeInsetEdges = 0;
            this.mLastChildRect = new Rect();
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.CoordinatorLayout_Layout);
            this.gravity = obtainStyledAttributes.getInteger(R$styleable.CoordinatorLayout_Layout_android_layout_gravity, 0);
            this.mAnchorId = obtainStyledAttributes.getResourceId(R$styleable.CoordinatorLayout_Layout_layout_anchor, -1);
            this.anchorGravity = obtainStyledAttributes.getInteger(R$styleable.CoordinatorLayout_Layout_layout_anchorGravity, 0);
            this.keyline = obtainStyledAttributes.getInteger(R$styleable.CoordinatorLayout_Layout_layout_keyline, -1);
            this.insetEdge = obtainStyledAttributes.getInt(R$styleable.CoordinatorLayout_Layout_layout_insetEdge, 0);
            this.dodgeInsetEdges = obtainStyledAttributes.getInt(R$styleable.CoordinatorLayout_Layout_layout_dodgeInsetEdges, 0);
            final boolean hasValue = obtainStyledAttributes.hasValue(R$styleable.CoordinatorLayout_Layout_layout_behavior);
            this.mBehaviorResolved = hasValue;
            if (hasValue) {
                this.mBehavior = CoordinatorLayout.parseBehavior(context, set, obtainStyledAttributes.getString(R$styleable.CoordinatorLayout_Layout_layout_behavior));
            }
            obtainStyledAttributes.recycle();
            final Behavior mBehavior = this.mBehavior;
            if (mBehavior != null) {
                mBehavior.onAttachedToLayoutParams(this);
            }
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.mBehaviorResolved = false;
            this.gravity = 0;
            this.anchorGravity = 0;
            this.keyline = -1;
            this.mAnchorId = -1;
            this.insetEdge = 0;
            this.dodgeInsetEdges = 0;
            this.mLastChildRect = new Rect();
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.mBehaviorResolved = false;
            this.gravity = 0;
            this.anchorGravity = 0;
            this.keyline = -1;
            this.mAnchorId = -1;
            this.insetEdge = 0;
            this.dodgeInsetEdges = 0;
            this.mLastChildRect = new Rect();
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$MarginLayoutParams)layoutParams);
            this.mBehaviorResolved = false;
            this.gravity = 0;
            this.anchorGravity = 0;
            this.keyline = -1;
            this.mAnchorId = -1;
            this.insetEdge = 0;
            this.dodgeInsetEdges = 0;
            this.mLastChildRect = new Rect();
        }
        
        private void resolveAnchorView(final View obj, final CoordinatorLayout coordinatorLayout) {
            View viewById = coordinatorLayout.findViewById(this.mAnchorId);
            this.mAnchorView = viewById;
            if (viewById != null) {
                if (viewById != coordinatorLayout) {
                    ViewParent viewParent = viewById.getParent();
                    while (viewParent != coordinatorLayout && viewParent != null) {
                        if (viewParent == obj) {
                            if (coordinatorLayout.isInEditMode()) {
                                this.mAnchorDirectChild = null;
                                this.mAnchorView = null;
                                return;
                            }
                            throw new IllegalStateException("Anchor must not be a descendant of the anchored view");
                        }
                        else {
                            if (viewParent instanceof View) {
                                viewById = (View)viewParent;
                            }
                            viewParent = viewParent.getParent();
                        }
                    }
                    this.mAnchorDirectChild = viewById;
                    return;
                }
                if (coordinatorLayout.isInEditMode()) {
                    this.mAnchorDirectChild = null;
                    this.mAnchorView = null;
                    return;
                }
                throw new IllegalStateException("View can not be anchored to the the parent CoordinatorLayout");
            }
            else {
                if (coordinatorLayout.isInEditMode()) {
                    this.mAnchorDirectChild = null;
                    this.mAnchorView = null;
                    return;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Could not find CoordinatorLayout descendant view with id ");
                sb.append(coordinatorLayout.getResources().getResourceName(this.mAnchorId));
                sb.append(" to anchor view ");
                sb.append(obj);
                throw new IllegalStateException(sb.toString());
            }
        }
        
        private boolean shouldDodge(final View view, final int n) {
            final int absoluteGravity = GravityCompat.getAbsoluteGravity(((LayoutParams)view.getLayoutParams()).insetEdge, n);
            return absoluteGravity != 0 && (GravityCompat.getAbsoluteGravity(this.dodgeInsetEdges, n) & absoluteGravity) == absoluteGravity;
        }
        
        private boolean verifyAnchorView(final View view, final CoordinatorLayout coordinatorLayout) {
            if (this.mAnchorView.getId() != this.mAnchorId) {
                return false;
            }
            View mAnchorView = this.mAnchorView;
            for (ViewParent viewParent = mAnchorView.getParent(); viewParent != coordinatorLayout; viewParent = viewParent.getParent()) {
                if (viewParent == null || viewParent == view) {
                    this.mAnchorDirectChild = null;
                    this.mAnchorView = null;
                    return false;
                }
                if (viewParent instanceof View) {
                    mAnchorView = (View)viewParent;
                }
            }
            this.mAnchorDirectChild = mAnchorView;
            return true;
        }
        
        boolean checkAnchorChanged() {
            return this.mAnchorView == null && this.mAnchorId != -1;
        }
        
        boolean dependsOn(final CoordinatorLayout coordinatorLayout, final View view, final View view2) {
            if (view2 != this.mAnchorDirectChild && !this.shouldDodge(view2, ViewCompat.getLayoutDirection((View)coordinatorLayout))) {
                final Behavior mBehavior = this.mBehavior;
                if (mBehavior == null || !mBehavior.layoutDependsOn(coordinatorLayout, view, view2)) {
                    return false;
                }
            }
            return true;
        }
        
        boolean didBlockInteraction() {
            if (this.mBehavior == null) {
                this.mDidBlockInteraction = false;
            }
            return this.mDidBlockInteraction;
        }
        
        View findAnchorView(final CoordinatorLayout coordinatorLayout, final View view) {
            if (this.mAnchorId == -1) {
                this.mAnchorDirectChild = null;
                return this.mAnchorView = null;
            }
            if (this.mAnchorView == null || !this.verifyAnchorView(view, coordinatorLayout)) {
                this.resolveAnchorView(view, coordinatorLayout);
            }
            return this.mAnchorView;
        }
        
        public Behavior getBehavior() {
            return this.mBehavior;
        }
        
        boolean getChangedAfterNestedScroll() {
            return this.mDidChangeAfterNestedScroll;
        }
        
        Rect getLastChildRect() {
            return this.mLastChildRect;
        }
        
        boolean isBlockingInteractionBelow(final CoordinatorLayout coordinatorLayout, final View view) {
            final boolean mDidBlockInteraction = this.mDidBlockInteraction;
            if (mDidBlockInteraction) {
                return true;
            }
            final Behavior mBehavior = this.mBehavior;
            return this.mDidBlockInteraction = ((mBehavior != null && mBehavior.blocksInteractionBelow(coordinatorLayout, view)) | mDidBlockInteraction);
        }
        
        boolean isNestedScrollAccepted(final int n) {
            if (n != 0) {
                return n == 1 && this.mDidAcceptNestedScrollNonTouch;
            }
            return this.mDidAcceptNestedScrollTouch;
        }
        
        void resetChangedAfterNestedScroll() {
            this.mDidChangeAfterNestedScroll = false;
        }
        
        void resetNestedScroll(final int n) {
            this.setNestedScrollAccepted(n, false);
        }
        
        void resetTouchBehaviorTracking() {
            this.mDidBlockInteraction = false;
        }
        
        public void setBehavior(final Behavior mBehavior) {
            final Behavior mBehavior2 = this.mBehavior;
            if (mBehavior2 != mBehavior) {
                if (mBehavior2 != null) {
                    mBehavior2.onDetachedFromLayoutParams();
                }
                this.mBehavior = mBehavior;
                this.mBehaviorResolved = true;
                if (mBehavior != null) {
                    mBehavior.onAttachedToLayoutParams(this);
                }
            }
        }
        
        void setChangedAfterNestedScroll(final boolean mDidChangeAfterNestedScroll) {
            this.mDidChangeAfterNestedScroll = mDidChangeAfterNestedScroll;
        }
        
        void setLastChildRect(final Rect rect) {
            this.mLastChildRect.set(rect);
        }
        
        void setNestedScrollAccepted(final int n, final boolean b) {
            if (n != 0) {
                if (n == 1) {
                    this.mDidAcceptNestedScrollNonTouch = b;
                }
            }
            else {
                this.mDidAcceptNestedScrollTouch = b;
            }
        }
    }
    
    class OnPreDrawListener implements ViewTreeObserver$OnPreDrawListener
    {
        public boolean onPreDraw() {
            CoordinatorLayout.this.onChildViewsChanged(0);
            return true;
        }
    }
    
    protected static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        SparseArray<Parcelable> behaviorStates;
        
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
        
        public SavedState(final Parcel parcel, final ClassLoader classLoader) {
            super(parcel, classLoader);
            final int int1 = parcel.readInt();
            final int[] array = new int[int1];
            parcel.readIntArray(array);
            final Parcelable[] parcelableArray = parcel.readParcelableArray(classLoader);
            this.behaviorStates = (SparseArray<Parcelable>)new SparseArray(int1);
            for (int i = 0; i < int1; ++i) {
                this.behaviorStates.append(array[i], (Object)parcelableArray[i]);
            }
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            final SparseArray<Parcelable> behaviorStates = this.behaviorStates;
            int i = 0;
            int size;
            if (behaviorStates != null) {
                size = behaviorStates.size();
            }
            else {
                size = 0;
            }
            parcel.writeInt(size);
            final int[] array = new int[size];
            final Parcelable[] array2 = new Parcelable[size];
            while (i < size) {
                array[i] = this.behaviorStates.keyAt(i);
                array2[i] = (Parcelable)this.behaviorStates.valueAt(i);
                ++i;
            }
            parcel.writeIntArray(array);
            parcel.writeParcelableArray(array2, n);
        }
    }
    
    static class ViewElevationComparator implements Comparator<View>
    {
        @Override
        public int compare(final View view, final View view2) {
            final float z = ViewCompat.getZ(view);
            final float z2 = ViewCompat.getZ(view2);
            if (z > z2) {
                return -1;
            }
            if (z < z2) {
                return 1;
            }
            return 0;
        }
    }
}
