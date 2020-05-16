// 
// Decompiled by Procyon v0.5.36
// 

package androidx.drawerlayout.widget;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import androidx.customview.view.AbsSavedState;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.os.Parcelable;
import android.annotation.SuppressLint;
import android.view.View$MeasureSpec;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import android.view.KeyEvent;
import android.view.ViewGroup$MarginLayoutParams;
import androidx.core.view.GravityCompat;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.ViewGroup$LayoutParams;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.view.MotionEvent;
import android.content.res.TypedArray;
import androidx.drawerlayout.R$dimen;
import androidx.drawerlayout.R$styleable;
import android.view.WindowInsets;
import android.view.View$OnApplyWindowInsetsListener;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import android.graphics.Paint;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import androidx.customview.widget.ViewDragHelper;
import android.graphics.Matrix;
import android.graphics.Rect;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import android.view.ViewGroup;

public class DrawerLayout extends ViewGroup
{
    static final boolean CAN_HIDE_DESCENDANTS;
    static final int[] LAYOUT_ATTRS;
    private static final boolean SET_DRAWER_SHADOW_FROM_ELEVATION;
    private static final int[] THEME_ATTRS;
    private static boolean sEdgeSizeUsingSystemGestureInsets;
    private final AccessibilityViewCommand mActionDismiss;
    private final ChildAccessibilityDelegate mChildAccessibilityDelegate;
    private Rect mChildHitRect;
    private Matrix mChildInvertedMatrix;
    private boolean mChildrenCanceledTouch;
    private boolean mDrawStatusBarBackground;
    private float mDrawerElevation;
    private int mDrawerState;
    private boolean mFirstLayout;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private Object mLastInsets;
    private final ViewDragCallback mLeftCallback;
    private final ViewDragHelper mLeftDragger;
    private List<DrawerListener> mListeners;
    private int mLockModeEnd;
    private int mLockModeLeft;
    private int mLockModeRight;
    private int mLockModeStart;
    private int mMinDrawerMargin;
    private final ArrayList<View> mNonDrawerViews;
    private final ViewDragCallback mRightCallback;
    private final ViewDragHelper mRightDragger;
    private int mScrimColor;
    private float mScrimOpacity;
    private Paint mScrimPaint;
    private Drawable mShadowEnd;
    private Drawable mShadowLeft;
    private Drawable mShadowLeftResolved;
    private Drawable mShadowRight;
    private Drawable mShadowRightResolved;
    private Drawable mShadowStart;
    private Drawable mStatusBarBackground;
    private CharSequence mTitleLeft;
    private CharSequence mTitleRight;
    
    static {
        final int sdk_INT = Build$VERSION.SDK_INT;
        final boolean b = true;
        THEME_ATTRS = new int[] { 16843828 };
        LAYOUT_ATTRS = new int[] { 16842931 };
        CAN_HIDE_DESCENDANTS = (sdk_INT >= 19);
        SET_DRAWER_SHADOW_FROM_ELEVATION = (sdk_INT >= 21);
        DrawerLayout.sEdgeSizeUsingSystemGestureInsets = (sdk_INT >= 29 && b);
    }
    
    public DrawerLayout(final Context context, final AttributeSet set) {
        this(context, set, R$attr.drawerLayoutStyle);
    }
    
    public DrawerLayout(Context obtainStyledAttributes, final AttributeSet set, final int n) {
        super(obtainStyledAttributes, set, n);
        this.mChildAccessibilityDelegate = new ChildAccessibilityDelegate();
        this.mScrimColor = -1728053248;
        this.mScrimPaint = new Paint();
        this.mFirstLayout = true;
        this.mLockModeLeft = 3;
        this.mLockModeRight = 3;
        this.mLockModeStart = 3;
        this.mLockModeEnd = 3;
        this.mShadowStart = null;
        this.mShadowEnd = null;
        this.mShadowLeft = null;
        this.mShadowRight = null;
        this.mActionDismiss = new AccessibilityViewCommand() {
            @Override
            public boolean perform(final View view, final CommandArguments commandArguments) {
                if (DrawerLayout.this.isDrawerOpen(view) && DrawerLayout.this.getDrawerLockMode(view) != 2) {
                    DrawerLayout.this.closeDrawer(view);
                    return true;
                }
                return false;
            }
        };
        this.setDescendantFocusability(262144);
        final float density = this.getResources().getDisplayMetrics().density;
        this.mMinDrawerMargin = (int)(64.0f * density + 0.5f);
        final float n2 = density * 400.0f;
        this.mLeftCallback = new ViewDragCallback(3);
        this.mRightCallback = new ViewDragCallback(5);
        (this.mLeftDragger = ViewDragHelper.create(this, 1.0f, (ViewDragHelper.Callback)this.mLeftCallback)).setEdgeTrackingEnabled(1);
        this.mLeftDragger.setMinVelocity(n2);
        this.mLeftCallback.setDragger(this.mLeftDragger);
        (this.mRightDragger = ViewDragHelper.create(this, 1.0f, (ViewDragHelper.Callback)this.mRightCallback)).setEdgeTrackingEnabled(2);
        this.mRightDragger.setMinVelocity(n2);
        this.mRightCallback.setDragger(this.mRightDragger);
        this.setFocusableInTouchMode(true);
        ViewCompat.setImportantForAccessibility((View)this, 1);
        ViewCompat.setAccessibilityDelegate((View)this, new AccessibilityDelegate());
        this.setMotionEventSplittingEnabled(false);
        Label_0343: {
            if (ViewCompat.getFitsSystemWindows((View)this)) {
                if (Build$VERSION.SDK_INT >= 21) {
                    this.setOnApplyWindowInsetsListener((View$OnApplyWindowInsetsListener)new View$OnApplyWindowInsetsListener(this) {
                        public WindowInsets onApplyWindowInsets(final View view, final WindowInsets windowInsets) {
                            ((DrawerLayout)view).setChildInsets(windowInsets, windowInsets.getSystemWindowInsetTop() > 0);
                            return windowInsets.consumeSystemWindowInsets();
                        }
                    });
                    this.setSystemUiVisibility(1280);
                    final TypedArray obtainStyledAttributes2 = obtainStyledAttributes.obtainStyledAttributes(DrawerLayout.THEME_ATTRS);
                    try {
                        this.mStatusBarBackground = obtainStyledAttributes2.getDrawable(0);
                        break Label_0343;
                    }
                    finally {
                        obtainStyledAttributes2.recycle();
                    }
                }
                this.mStatusBarBackground = null;
            }
        }
        obtainStyledAttributes = (Context)obtainStyledAttributes.obtainStyledAttributes(set, R$styleable.DrawerLayout, n, 0);
        try {
            if (((TypedArray)obtainStyledAttributes).hasValue(R$styleable.DrawerLayout_elevation)) {
                this.mDrawerElevation = ((TypedArray)obtainStyledAttributes).getDimension(R$styleable.DrawerLayout_elevation, 0.0f);
            }
            else {
                this.mDrawerElevation = this.getResources().getDimension(R$dimen.def_drawer_elevation);
            }
            ((TypedArray)obtainStyledAttributes).recycle();
            this.mNonDrawerViews = new ArrayList<View>();
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    private boolean dispatchTransformedGenericPointerEvent(MotionEvent transformedMotionEvent, final View view) {
        boolean b;
        if (!view.getMatrix().isIdentity()) {
            transformedMotionEvent = this.getTransformedMotionEvent(transformedMotionEvent, view);
            b = view.dispatchGenericMotionEvent(transformedMotionEvent);
            transformedMotionEvent.recycle();
        }
        else {
            final float n = (float)(this.getScrollX() - view.getLeft());
            final float n2 = (float)(this.getScrollY() - view.getTop());
            transformedMotionEvent.offsetLocation(n, n2);
            b = view.dispatchGenericMotionEvent(transformedMotionEvent);
            transformedMotionEvent.offsetLocation(-n, -n2);
        }
        return b;
    }
    
    private MotionEvent getTransformedMotionEvent(MotionEvent obtain, final View view) {
        final float n = (float)(this.getScrollX() - view.getLeft());
        final float n2 = (float)(this.getScrollY() - view.getTop());
        obtain = MotionEvent.obtain(obtain);
        obtain.offsetLocation(n, n2);
        final Matrix matrix = view.getMatrix();
        if (!matrix.isIdentity()) {
            if (this.mChildInvertedMatrix == null) {
                this.mChildInvertedMatrix = new Matrix();
            }
            matrix.invert(this.mChildInvertedMatrix);
            obtain.transform(this.mChildInvertedMatrix);
        }
        return obtain;
    }
    
    static String gravityToString(final int i) {
        if ((i & 0x3) == 0x3) {
            return "LEFT";
        }
        if ((i & 0x5) == 0x5) {
            return "RIGHT";
        }
        return Integer.toHexString(i);
    }
    
    private static boolean hasOpaqueBackground(final View view) {
        final Drawable background = view.getBackground();
        boolean b = false;
        if (background != null) {
            b = b;
            if (background.getOpacity() == -1) {
                b = true;
            }
        }
        return b;
    }
    
    private boolean hasPeekingDrawer() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            if (((LayoutParams)this.getChildAt(i).getLayoutParams()).isPeeking) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasVisibleDrawer() {
        return this.findVisibleDrawer() != null;
    }
    
    static boolean includeChildForAccessibility(final View view) {
        return ViewCompat.getImportantForAccessibility(view) != 4 && ViewCompat.getImportantForAccessibility(view) != 2;
    }
    
    private boolean isInBoundsOfChild(final float n, final float n2, final View view) {
        if (this.mChildHitRect == null) {
            this.mChildHitRect = new Rect();
        }
        view.getHitRect(this.mChildHitRect);
        return this.mChildHitRect.contains((int)n, (int)n2);
    }
    
    private void mirror(final Drawable drawable, final int n) {
        if (drawable != null && DrawableCompat.isAutoMirrored(drawable)) {
            DrawableCompat.setLayoutDirection(drawable, n);
        }
    }
    
    private Drawable resolveLeftShadow() {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        if (layoutDirection == 0) {
            final Drawable mShadowStart = this.mShadowStart;
            if (mShadowStart != null) {
                this.mirror(mShadowStart, layoutDirection);
                return this.mShadowStart;
            }
        }
        else {
            final Drawable mShadowEnd = this.mShadowEnd;
            if (mShadowEnd != null) {
                this.mirror(mShadowEnd, layoutDirection);
                return this.mShadowEnd;
            }
        }
        return this.mShadowLeft;
    }
    
    private Drawable resolveRightShadow() {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        if (layoutDirection == 0) {
            final Drawable mShadowEnd = this.mShadowEnd;
            if (mShadowEnd != null) {
                this.mirror(mShadowEnd, layoutDirection);
                return this.mShadowEnd;
            }
        }
        else {
            final Drawable mShadowStart = this.mShadowStart;
            if (mShadowStart != null) {
                this.mirror(mShadowStart, layoutDirection);
                return this.mShadowStart;
            }
        }
        return this.mShadowRight;
    }
    
    private void resolveShadowDrawables() {
        if (DrawerLayout.SET_DRAWER_SHADOW_FROM_ELEVATION) {
            return;
        }
        this.mShadowLeftResolved = this.resolveLeftShadow();
        this.mShadowRightResolved = this.resolveRightShadow();
    }
    
    private void updateChildAccessibilityAction(final View view) {
        ViewCompat.removeAccessibilityAction(view, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_DISMISS.getId());
        if (this.isDrawerOpen(view) && this.getDrawerLockMode(view) != 2) {
            ViewCompat.replaceAccessibilityAction(view, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_DISMISS, null, this.mActionDismiss);
        }
    }
    
    private void updateChildrenImportantForAccessibility(final View view, final boolean b) {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if ((!b && !this.isDrawerView(child)) || (b && child == view)) {
                ViewCompat.setImportantForAccessibility(child, 1);
            }
            else {
                ViewCompat.setImportantForAccessibility(child, 4);
            }
        }
    }
    
    public void addFocusables(final ArrayList<View> list, final int n, final int n2) {
        if (this.getDescendantFocusability() == 393216) {
            return;
        }
        final int childCount = this.getChildCount();
        final int n3 = 0;
        int n4;
        for (int i = n4 = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (this.isDrawerView(child)) {
                if (this.isDrawerOpen(child)) {
                    child.addFocusables((ArrayList)list, n, n2);
                    n4 = 1;
                }
            }
            else {
                this.mNonDrawerViews.add(child);
            }
        }
        if (n4 == 0) {
            for (int size = this.mNonDrawerViews.size(), j = n3; j < size; ++j) {
                final View view = this.mNonDrawerViews.get(j);
                if (view.getVisibility() == 0) {
                    view.addFocusables((ArrayList)list, n, n2);
                }
            }
        }
        this.mNonDrawerViews.clear();
    }
    
    public void addView(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        super.addView(view, n, viewGroup$LayoutParams);
        if (this.findOpenDrawer() == null && !this.isDrawerView(view)) {
            ViewCompat.setImportantForAccessibility(view, 1);
        }
        else {
            ViewCompat.setImportantForAccessibility(view, 4);
        }
        if (!DrawerLayout.CAN_HIDE_DESCENDANTS) {
            ViewCompat.setAccessibilityDelegate(view, this.mChildAccessibilityDelegate);
        }
    }
    
    void cancelChildViewTouch() {
        if (!this.mChildrenCanceledTouch) {
            final long uptimeMillis = SystemClock.uptimeMillis();
            final MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                this.getChildAt(i).dispatchTouchEvent(obtain);
            }
            obtain.recycle();
            this.mChildrenCanceledTouch = true;
        }
    }
    
    boolean checkDrawerViewAbsoluteGravity(final View view, final int n) {
        return (this.getDrawerViewAbsoluteGravity(view) & n) == n;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams && super.checkLayoutParams(viewGroup$LayoutParams);
    }
    
    public void closeDrawer(final View view) {
        this.closeDrawer(view, true);
    }
    
    public void closeDrawer(final View obj, final boolean b) {
        if (this.isDrawerView(obj)) {
            final LayoutParams layoutParams = (LayoutParams)obj.getLayoutParams();
            if (this.mFirstLayout) {
                layoutParams.onScreen = 0.0f;
                layoutParams.openState = 0;
            }
            else if (b) {
                layoutParams.openState |= 0x4;
                if (this.checkDrawerViewAbsoluteGravity(obj, 3)) {
                    this.mLeftDragger.smoothSlideViewTo(obj, -obj.getWidth(), obj.getTop());
                }
                else {
                    this.mRightDragger.smoothSlideViewTo(obj, this.getWidth(), obj.getTop());
                }
            }
            else {
                this.moveDrawerToOffset(obj, 0.0f);
                this.updateDrawerState(0, obj);
                obj.setVisibility(4);
            }
            this.invalidate();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("View ");
        sb.append(obj);
        sb.append(" is not a sliding drawer");
        throw new IllegalArgumentException(sb.toString());
    }
    
    public void closeDrawers() {
        this.closeDrawers(false);
    }
    
    void closeDrawers(final boolean b) {
        int n;
        int n2;
        for (int childCount = this.getChildCount(), i = n = 0; i < childCount; ++i, n = n2) {
            final View child = this.getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            n2 = n;
            if (this.isDrawerView(child)) {
                if (b && !layoutParams.isPeeking) {
                    n2 = n;
                }
                else {
                    final int width = child.getWidth();
                    boolean b2;
                    if (this.checkDrawerViewAbsoluteGravity(child, 3)) {
                        b2 = this.mLeftDragger.smoothSlideViewTo(child, -width, child.getTop());
                    }
                    else {
                        b2 = this.mRightDragger.smoothSlideViewTo(child, this.getWidth(), child.getTop());
                    }
                    n2 = (n | (b2 ? 1 : 0));
                    layoutParams.isPeeking = false;
                }
            }
        }
        this.mLeftCallback.removeCallbacks();
        this.mRightCallback.removeCallbacks();
        if (n != 0) {
            this.invalidate();
        }
    }
    
    public void computeScroll() {
        final int childCount = this.getChildCount();
        float max = 0.0f;
        for (int i = 0; i < childCount; ++i) {
            max = Math.max(max, ((LayoutParams)this.getChildAt(i).getLayoutParams()).onScreen);
        }
        this.mScrimOpacity = max;
        final boolean continueSettling = this.mLeftDragger.continueSettling(true);
        final boolean continueSettling2 = this.mRightDragger.continueSettling(true);
        if (continueSettling || continueSettling2) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    public boolean dispatchGenericMotionEvent(final MotionEvent motionEvent) {
        if ((motionEvent.getSource() & 0x2) != 0x0 && motionEvent.getAction() != 10 && this.mScrimOpacity > 0.0f) {
            int i = this.getChildCount();
            if (i != 0) {
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                --i;
                while (i >= 0) {
                    final View child = this.getChildAt(i);
                    if (this.isInBoundsOfChild(x, y, child)) {
                        if (!this.isContentView(child)) {
                            if (this.dispatchTransformedGenericPointerEvent(motionEvent, child)) {
                                return true;
                            }
                        }
                    }
                    --i;
                }
            }
            return false;
        }
        return super.dispatchGenericMotionEvent(motionEvent);
    }
    
    void dispatchOnDrawerClosed(View rootView) {
        final LayoutParams layoutParams = (LayoutParams)rootView.getLayoutParams();
        if ((layoutParams.openState & 0x1) == 0x1) {
            layoutParams.openState = 0;
            final List<DrawerListener> mListeners = this.mListeners;
            if (mListeners != null) {
                for (int i = mListeners.size() - 1; i >= 0; --i) {
                    this.mListeners.get(i).onDrawerClosed(rootView);
                }
            }
            this.updateChildrenImportantForAccessibility(rootView, false);
            this.updateChildAccessibilityAction(rootView);
            if (this.hasWindowFocus()) {
                rootView = this.getRootView();
                if (rootView != null) {
                    rootView.sendAccessibilityEvent(32);
                }
            }
        }
    }
    
    void dispatchOnDrawerOpened(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if ((layoutParams.openState & 0x1) == 0x0) {
            layoutParams.openState = 1;
            final List<DrawerListener> mListeners = this.mListeners;
            if (mListeners != null) {
                for (int i = mListeners.size() - 1; i >= 0; --i) {
                    this.mListeners.get(i).onDrawerOpened(view);
                }
            }
            this.updateChildrenImportantForAccessibility(view, true);
            this.updateChildAccessibilityAction(view);
            if (this.hasWindowFocus()) {
                this.sendAccessibilityEvent(32);
            }
        }
    }
    
    void dispatchOnDrawerSlide(final View view, final float n) {
        final List<DrawerListener> mListeners = this.mListeners;
        if (mListeners != null) {
            for (int i = mListeners.size() - 1; i >= 0; --i) {
                this.mListeners.get(i).onDrawerSlide(view, n);
            }
        }
    }
    
    protected boolean drawChild(final Canvas canvas, final View view, final long n) {
        final int height = this.getHeight();
        final boolean contentView = this.isContentView(view);
        int width = this.getWidth();
        final int save = canvas.save();
        int n2 = 0;
        int n3 = width;
        if (contentView) {
            int n4;
            int n5;
            int n6;
            for (int childCount = this.getChildCount(), i = n4 = 0; i < childCount; ++i, width = n5, n4 = n6) {
                final View child = this.getChildAt(i);
                n5 = width;
                n6 = n4;
                if (child != view) {
                    n5 = width;
                    n6 = n4;
                    if (child.getVisibility() == 0) {
                        n5 = width;
                        n6 = n4;
                        if (hasOpaqueBackground(child)) {
                            n5 = width;
                            n6 = n4;
                            if (this.isDrawerView(child)) {
                                if (child.getHeight() < height) {
                                    n5 = width;
                                    n6 = n4;
                                }
                                else if (this.checkDrawerViewAbsoluteGravity(child, 3)) {
                                    final int right = child.getRight();
                                    n5 = width;
                                    if (right > (n6 = n4)) {
                                        n6 = right;
                                        n5 = width;
                                    }
                                }
                                else {
                                    final int left = child.getLeft();
                                    n5 = width;
                                    n6 = n4;
                                    if (left < width) {
                                        n5 = left;
                                        n6 = n4;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            canvas.clipRect(n4, 0, width, this.getHeight());
            n2 = n4;
            n3 = width;
        }
        final boolean drawChild = super.drawChild(canvas, view, n);
        canvas.restoreToCount(save);
        final float mScrimOpacity = this.mScrimOpacity;
        if (mScrimOpacity > 0.0f && contentView) {
            final int mScrimColor = this.mScrimColor;
            this.mScrimPaint.setColor((mScrimColor & 0xFFFFFF) | (int)(((0xFF000000 & mScrimColor) >>> 24) * mScrimOpacity) << 24);
            canvas.drawRect((float)n2, 0.0f, (float)n3, (float)this.getHeight(), this.mScrimPaint);
        }
        else if (this.mShadowLeftResolved != null && this.checkDrawerViewAbsoluteGravity(view, 3)) {
            final int intrinsicWidth = this.mShadowLeftResolved.getIntrinsicWidth();
            final int right2 = view.getRight();
            final float max = Math.max(0.0f, Math.min(right2 / (float)this.mLeftDragger.getEdgeSize(), 1.0f));
            this.mShadowLeftResolved.setBounds(right2, view.getTop(), intrinsicWidth + right2, view.getBottom());
            this.mShadowLeftResolved.setAlpha((int)(max * 255.0f));
            this.mShadowLeftResolved.draw(canvas);
        }
        else if (this.mShadowRightResolved != null && this.checkDrawerViewAbsoluteGravity(view, 5)) {
            final int intrinsicWidth2 = this.mShadowRightResolved.getIntrinsicWidth();
            final int left2 = view.getLeft();
            final float max2 = Math.max(0.0f, Math.min((this.getWidth() - left2) / (float)this.mRightDragger.getEdgeSize(), 1.0f));
            this.mShadowRightResolved.setBounds(left2 - intrinsicWidth2, view.getTop(), left2, view.getBottom());
            this.mShadowRightResolved.setAlpha((int)(max2 * 255.0f));
            this.mShadowRightResolved.draw(canvas);
        }
        return drawChild;
    }
    
    View findDrawerWithGravity(int i) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(i, ViewCompat.getLayoutDirection((View)this));
        int childCount;
        View child;
        for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            child = this.getChildAt(i);
            if ((this.getDrawerViewAbsoluteGravity(child) & 0x7) == (absoluteGravity & 0x7)) {
                return child;
            }
        }
        return null;
    }
    
    View findOpenDrawer() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if ((((LayoutParams)child.getLayoutParams()).openState & 0x1) == 0x1) {
                return child;
            }
        }
        return null;
    }
    
    View findVisibleDrawer() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (this.isDrawerView(child) && this.isDrawerVisible(child)) {
                return child;
            }
        }
        return null;
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return (ViewGroup$LayoutParams)new LayoutParams(-1, -1);
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        return (ViewGroup$LayoutParams)new LayoutParams(this.getContext(), set);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        LayoutParams layoutParams;
        if (viewGroup$LayoutParams instanceof LayoutParams) {
            layoutParams = new LayoutParams((LayoutParams)viewGroup$LayoutParams);
        }
        else if (viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams) {
            layoutParams = new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
        }
        else {
            layoutParams = new LayoutParams(viewGroup$LayoutParams);
        }
        return (ViewGroup$LayoutParams)layoutParams;
    }
    
    public int getDrawerLockMode(int n) {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        if (n != 3) {
            if (n != 5) {
                if (n != 8388611) {
                    if (n == 8388613) {
                        n = this.mLockModeEnd;
                        if (n != 3) {
                            return n;
                        }
                        if (layoutDirection == 0) {
                            n = this.mLockModeRight;
                        }
                        else {
                            n = this.mLockModeLeft;
                        }
                        if (n != 3) {
                            return n;
                        }
                    }
                }
                else {
                    n = this.mLockModeStart;
                    if (n != 3) {
                        return n;
                    }
                    if (layoutDirection == 0) {
                        n = this.mLockModeLeft;
                    }
                    else {
                        n = this.mLockModeRight;
                    }
                    if (n != 3) {
                        return n;
                    }
                }
            }
            else {
                n = this.mLockModeRight;
                if (n != 3) {
                    return n;
                }
                if (layoutDirection == 0) {
                    n = this.mLockModeEnd;
                }
                else {
                    n = this.mLockModeStart;
                }
                if (n != 3) {
                    return n;
                }
            }
        }
        else {
            n = this.mLockModeLeft;
            if (n != 3) {
                return n;
            }
            if (layoutDirection == 0) {
                n = this.mLockModeStart;
            }
            else {
                n = this.mLockModeEnd;
            }
            if (n != 3) {
                return n;
            }
        }
        return 0;
    }
    
    public int getDrawerLockMode(final View obj) {
        if (this.isDrawerView(obj)) {
            return this.getDrawerLockMode(((LayoutParams)obj.getLayoutParams()).gravity);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("View ");
        sb.append(obj);
        sb.append(" is not a drawer");
        throw new IllegalArgumentException(sb.toString());
    }
    
    public CharSequence getDrawerTitle(int absoluteGravity) {
        absoluteGravity = GravityCompat.getAbsoluteGravity(absoluteGravity, ViewCompat.getLayoutDirection((View)this));
        if (absoluteGravity == 3) {
            return this.mTitleLeft;
        }
        if (absoluteGravity == 5) {
            return this.mTitleRight;
        }
        return null;
    }
    
    int getDrawerViewAbsoluteGravity(final View view) {
        return GravityCompat.getAbsoluteGravity(((LayoutParams)view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection((View)this));
    }
    
    float getDrawerViewOffset(final View view) {
        return ((LayoutParams)view.getLayoutParams()).onScreen;
    }
    
    boolean isContentView(final View view) {
        return ((LayoutParams)view.getLayoutParams()).gravity == 0;
    }
    
    public boolean isDrawerOpen(final View obj) {
        if (this.isDrawerView(obj)) {
            final int openState = ((LayoutParams)obj.getLayoutParams()).openState;
            boolean b = true;
            if ((openState & 0x1) != 0x1) {
                b = false;
            }
            return b;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("View ");
        sb.append(obj);
        sb.append(" is not a drawer");
        throw new IllegalArgumentException(sb.toString());
    }
    
    boolean isDrawerView(final View view) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(((LayoutParams)view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(view));
        return (absoluteGravity & 0x3) != 0x0 || (absoluteGravity & 0x5) != 0x0;
    }
    
    public boolean isDrawerVisible(final View obj) {
        if (this.isDrawerView(obj)) {
            return ((LayoutParams)obj.getLayoutParams()).onScreen > 0.0f;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("View ");
        sb.append(obj);
        sb.append(" is not a drawer");
        throw new IllegalArgumentException(sb.toString());
    }
    
    void moveDrawerToOffset(final View view, final float n) {
        final float drawerViewOffset = this.getDrawerViewOffset(view);
        final float n2 = (float)view.getWidth();
        int n3 = (int)(n2 * n) - (int)(drawerViewOffset * n2);
        if (!this.checkDrawerViewAbsoluteGravity(view, 3)) {
            n3 = -n3;
        }
        view.offsetLeftAndRight(n3);
        this.setDrawerViewOffset(view, n);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawStatusBarBackground && this.mStatusBarBackground != null) {
            int systemWindowInsetTop = 0;
            Label_0049: {
                if (Build$VERSION.SDK_INT >= 21) {
                    final Object mLastInsets = this.mLastInsets;
                    if (mLastInsets != null) {
                        systemWindowInsetTop = ((WindowInsets)mLastInsets).getSystemWindowInsetTop();
                        break Label_0049;
                    }
                }
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
        final boolean shouldInterceptTouchEvent = this.mLeftDragger.shouldInterceptTouchEvent(motionEvent);
        final boolean shouldInterceptTouchEvent2 = this.mRightDragger.shouldInterceptTouchEvent(motionEvent);
        final boolean b = true;
        boolean b2;
        if (actionMasked != 0) {
            Label_0087: {
                if (actionMasked != 1) {
                    if (actionMasked != 2) {
                        if (actionMasked != 3) {
                            break Label_0087;
                        }
                    }
                    else {
                        if (this.mLeftDragger.checkTouchSlop(3)) {
                            this.mLeftCallback.removeCallbacks();
                            this.mRightCallback.removeCallbacks();
                        }
                        break Label_0087;
                    }
                }
                this.closeDrawers(true);
                this.mChildrenCanceledTouch = false;
            }
            b2 = false;
        }
        else {
            final float x = motionEvent.getX();
            final float y = motionEvent.getY();
            this.mInitialMotionX = x;
            this.mInitialMotionY = y;
            Label_0158: {
                if (this.mScrimOpacity > 0.0f) {
                    final View topChildUnder = this.mLeftDragger.findTopChildUnder((int)x, (int)y);
                    if (topChildUnder != null && this.isContentView(topChildUnder)) {
                        b2 = true;
                        break Label_0158;
                    }
                }
                b2 = false;
            }
            this.mChildrenCanceledTouch = false;
        }
        boolean b3 = b;
        if (!(shouldInterceptTouchEvent | shouldInterceptTouchEvent2)) {
            b3 = b;
            if (!b2) {
                b3 = b;
                if (!this.hasPeekingDrawer()) {
                    b3 = (this.mChildrenCanceledTouch && b);
                }
            }
        }
        return b3;
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (n == 4 && this.hasVisibleDrawer()) {
            keyEvent.startTracking();
            return true;
        }
        return super.onKeyDown(n, keyEvent);
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        if (n == 4) {
            final View visibleDrawer = this.findVisibleDrawer();
            if (visibleDrawer != null && this.getDrawerLockMode(visibleDrawer) == 0) {
                this.closeDrawers();
            }
            return visibleDrawer != null;
        }
        return super.onKeyUp(n, keyEvent);
    }
    
    protected void onLayout(final boolean b, int visibility, final int n, int i, final int n2) {
        this.mInLayout = true;
        final int n3 = i - visibility;
        int childCount;
        View child;
        LayoutParams layoutParams;
        int measuredWidth;
        int measuredHeight;
        float n4;
        int n5;
        float n6;
        float n7;
        boolean b2;
        int n8;
        int n9;
        int bottomMargin;
        for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                layoutParams = (LayoutParams)child.getLayoutParams();
                if (this.isContentView(child)) {
                    visibility = layoutParams.leftMargin;
                    child.layout(visibility, layoutParams.topMargin, child.getMeasuredWidth() + visibility, layoutParams.topMargin + child.getMeasuredHeight());
                }
                else {
                    measuredWidth = child.getMeasuredWidth();
                    measuredHeight = child.getMeasuredHeight();
                    if (this.checkDrawerViewAbsoluteGravity(child, 3)) {
                        visibility = -measuredWidth;
                        n4 = (float)measuredWidth;
                        n5 = visibility + (int)(layoutParams.onScreen * n4);
                        n6 = (measuredWidth + n5) / n4;
                    }
                    else {
                        n7 = (float)measuredWidth;
                        n5 = n3 - (int)(layoutParams.onScreen * n7);
                        n6 = (n3 - n5) / n7;
                    }
                    b2 = (n6 != layoutParams.onScreen);
                    visibility = (layoutParams.gravity & 0x70);
                    if (visibility != 16) {
                        if (visibility != 80) {
                            visibility = layoutParams.topMargin;
                            child.layout(n5, visibility, measuredWidth + n5, measuredHeight + visibility);
                        }
                        else {
                            visibility = n2 - n;
                            child.layout(n5, visibility - layoutParams.bottomMargin - child.getMeasuredHeight(), measuredWidth + n5, visibility - layoutParams.bottomMargin);
                        }
                    }
                    else {
                        n8 = n2 - n;
                        n9 = (n8 - measuredHeight) / 2;
                        visibility = layoutParams.topMargin;
                        if (n9 >= visibility) {
                            bottomMargin = layoutParams.bottomMargin;
                            visibility = n9;
                            if (n9 + measuredHeight > n8 - bottomMargin) {
                                visibility = n8 - bottomMargin - measuredHeight;
                            }
                        }
                        child.layout(n5, visibility, measuredWidth + n5, measuredHeight + visibility);
                    }
                    if (b2) {
                        this.setDrawerViewOffset(child, n6);
                    }
                    if (layoutParams.onScreen > 0.0f) {
                        visibility = 0;
                    }
                    else {
                        visibility = 4;
                    }
                    if (child.getVisibility() != visibility) {
                        child.setVisibility(visibility);
                    }
                }
            }
        }
        if (DrawerLayout.sEdgeSizeUsingSystemGestureInsets) {
            final WindowInsets rootWindowInsets = this.getRootWindowInsets();
            if (rootWindowInsets != null) {
                final Insets systemGestureInsets = WindowInsetsCompat.toWindowInsetsCompat(rootWindowInsets).getSystemGestureInsets();
                final ViewDragHelper mLeftDragger = this.mLeftDragger;
                mLeftDragger.setEdgeSize(Math.max(mLeftDragger.getDefaultEdgeSize(), systemGestureInsets.left));
                final ViewDragHelper mRightDragger = this.mRightDragger;
                mRightDragger.setEdgeSize(Math.max(mRightDragger.getDefaultEdgeSize(), systemGestureInsets.right));
            }
        }
        this.mInLayout = false;
        this.mFirstLayout = false;
    }
    
    @SuppressLint({ "WrongConstant" })
    protected void onMeasure(final int n, final int n2) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        final int mode = View$MeasureSpec.getMode(n);
        final int mode2 = View$MeasureSpec.getMode(n2);
        int size = View$MeasureSpec.getSize(n);
        final int size2 = View$MeasureSpec.getSize(n2);
        int n3 = 0;
        int n4 = 0;
        Label_0091: {
            if (mode == 1073741824) {
                n3 = size;
                n4 = size2;
                if (mode2 == 1073741824) {
                    break Label_0091;
                }
            }
            if (!this.isInEditMode()) {
                throw new IllegalArgumentException("DrawerLayout must be measured with MeasureSpec.EXACTLY.");
            }
            if (mode == 0) {
                size = 300;
            }
            n3 = size;
            n4 = size2;
            if (mode2 == 0) {
                n4 = 300;
                n3 = size;
            }
        }
        this.setMeasuredDimension(n3, n4);
        final boolean b = this.mLastInsets != null && ViewCompat.getFitsSystemWindows((View)this);
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        final int childCount = this.getChildCount();
        int i = 0;
        int n6;
        int n5 = n6 = i;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (b) {
                    final int absoluteGravity = GravityCompat.getAbsoluteGravity(layoutParams.gravity, layoutDirection);
                    if (ViewCompat.getFitsSystemWindows(child)) {
                        if (sdk_INT >= 21) {
                            final WindowInsets windowInsets = (WindowInsets)this.mLastInsets;
                            WindowInsets windowInsets2;
                            if (absoluteGravity == 3) {
                                windowInsets2 = windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), 0, windowInsets.getSystemWindowInsetBottom());
                            }
                            else {
                                windowInsets2 = windowInsets;
                                if (absoluteGravity == 5) {
                                    windowInsets2 = windowInsets.replaceSystemWindowInsets(0, windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
                                }
                            }
                            child.dispatchApplyWindowInsets(windowInsets2);
                        }
                    }
                    else if (sdk_INT >= 21) {
                        final WindowInsets windowInsets3 = (WindowInsets)this.mLastInsets;
                        WindowInsets windowInsets4;
                        if (absoluteGravity == 3) {
                            windowInsets4 = windowInsets3.replaceSystemWindowInsets(windowInsets3.getSystemWindowInsetLeft(), windowInsets3.getSystemWindowInsetTop(), 0, windowInsets3.getSystemWindowInsetBottom());
                        }
                        else {
                            windowInsets4 = windowInsets3;
                            if (absoluteGravity == 5) {
                                windowInsets4 = windowInsets3.replaceSystemWindowInsets(0, windowInsets3.getSystemWindowInsetTop(), windowInsets3.getSystemWindowInsetRight(), windowInsets3.getSystemWindowInsetBottom());
                            }
                        }
                        layoutParams.leftMargin = windowInsets4.getSystemWindowInsetLeft();
                        layoutParams.topMargin = windowInsets4.getSystemWindowInsetTop();
                        layoutParams.rightMargin = windowInsets4.getSystemWindowInsetRight();
                        layoutParams.bottomMargin = windowInsets4.getSystemWindowInsetBottom();
                    }
                }
                if (this.isContentView(child)) {
                    child.measure(View$MeasureSpec.makeMeasureSpec(n3 - layoutParams.leftMargin - layoutParams.rightMargin, 1073741824), View$MeasureSpec.makeMeasureSpec(n4 - layoutParams.topMargin - layoutParams.bottomMargin, 1073741824));
                }
                else {
                    if (!this.isDrawerView(child)) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Child ");
                        sb.append(child);
                        sb.append(" at index ");
                        sb.append(i);
                        sb.append(" does not have a valid layout_gravity - must be Gravity.LEFT, Gravity.RIGHT or Gravity.NO_GRAVITY");
                        throw new IllegalStateException(sb.toString());
                    }
                    if (DrawerLayout.SET_DRAWER_SHADOW_FROM_ELEVATION) {
                        final float elevation = ViewCompat.getElevation(child);
                        final float mDrawerElevation = this.mDrawerElevation;
                        if (elevation != mDrawerElevation) {
                            ViewCompat.setElevation(child, mDrawerElevation);
                        }
                    }
                    final int n7 = this.getDrawerViewAbsoluteGravity(child) & 0x7;
                    final boolean b2 = n7 == 3;
                    if ((b2 && n5 != 0) || (!b2 && n6 != 0)) {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("Child drawer has absolute gravity ");
                        sb2.append(gravityToString(n7));
                        sb2.append(" but this ");
                        sb2.append("DrawerLayout");
                        sb2.append(" already has a drawer view along that edge");
                        throw new IllegalStateException(sb2.toString());
                    }
                    if (b2) {
                        n5 = 1;
                    }
                    else {
                        n6 = 1;
                    }
                    child.measure(ViewGroup.getChildMeasureSpec(n, this.mMinDrawerMargin + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width), ViewGroup.getChildMeasureSpec(n2, layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height));
                }
            }
            ++i;
        }
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        final int openDrawerGravity = savedState.openDrawerGravity;
        if (openDrawerGravity != 0) {
            final View drawerWithGravity = this.findDrawerWithGravity(openDrawerGravity);
            if (drawerWithGravity != null) {
                this.openDrawer(drawerWithGravity);
            }
        }
        final int lockModeLeft = savedState.lockModeLeft;
        if (lockModeLeft != 3) {
            this.setDrawerLockMode(lockModeLeft, 3);
        }
        final int lockModeRight = savedState.lockModeRight;
        if (lockModeRight != 3) {
            this.setDrawerLockMode(lockModeRight, 5);
        }
        final int lockModeStart = savedState.lockModeStart;
        if (lockModeStart != 3) {
            this.setDrawerLockMode(lockModeStart, 8388611);
        }
        final int lockModeEnd = savedState.lockModeEnd;
        if (lockModeEnd != 3) {
            this.setDrawerLockMode(lockModeEnd, 8388613);
        }
    }
    
    public void onRtlPropertiesChanged(final int n) {
        this.resolveShadowDrawables();
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final LayoutParams layoutParams = (LayoutParams)this.getChildAt(i).getLayoutParams();
            final int openState = layoutParams.openState;
            boolean b = true;
            final boolean b2 = openState == 1;
            if (layoutParams.openState != 2) {
                b = false;
            }
            if (b2 || b) {
                savedState.openDrawerGravity = layoutParams.gravity;
                break;
            }
        }
        savedState.lockModeLeft = this.mLockModeLeft;
        savedState.lockModeRight = this.mLockModeRight;
        savedState.lockModeStart = this.mLockModeStart;
        savedState.lockModeEnd = this.mLockModeEnd;
        return (Parcelable)savedState;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        this.mLeftDragger.processTouchEvent(motionEvent);
        this.mRightDragger.processTouchEvent(motionEvent);
        final int n = motionEvent.getAction() & 0xFF;
        boolean b = false;
        if (n != 0) {
            if (n != 1) {
                if (n == 3) {
                    this.closeDrawers(true);
                    this.mChildrenCanceledTouch = false;
                }
            }
            else {
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                final View topChildUnder = this.mLeftDragger.findTopChildUnder((int)x, (int)y);
                Label_0160: {
                    if (topChildUnder != null && this.isContentView(topChildUnder)) {
                        final float n2 = x - this.mInitialMotionX;
                        final float n3 = y - this.mInitialMotionY;
                        final int touchSlop = this.mLeftDragger.getTouchSlop();
                        if (n2 * n2 + n3 * n3 < touchSlop * touchSlop) {
                            final View openDrawer = this.findOpenDrawer();
                            if (openDrawer != null && this.getDrawerLockMode(openDrawer) != 2) {
                                break Label_0160;
                            }
                        }
                    }
                    b = true;
                }
                this.closeDrawers(b);
            }
        }
        else {
            final float x2 = motionEvent.getX();
            final float y2 = motionEvent.getY();
            this.mInitialMotionX = x2;
            this.mInitialMotionY = y2;
            this.mChildrenCanceledTouch = false;
        }
        return true;
    }
    
    public void openDrawer(final View view) {
        this.openDrawer(view, true);
    }
    
    public void openDrawer(final View obj, final boolean b) {
        if (this.isDrawerView(obj)) {
            final LayoutParams layoutParams = (LayoutParams)obj.getLayoutParams();
            if (this.mFirstLayout) {
                layoutParams.onScreen = 1.0f;
                layoutParams.openState = 1;
                this.updateChildrenImportantForAccessibility(obj, true);
                this.updateChildAccessibilityAction(obj);
            }
            else if (b) {
                layoutParams.openState |= 0x2;
                if (this.checkDrawerViewAbsoluteGravity(obj, 3)) {
                    this.mLeftDragger.smoothSlideViewTo(obj, 0, obj.getTop());
                }
                else {
                    this.mRightDragger.smoothSlideViewTo(obj, this.getWidth() - obj.getWidth(), obj.getTop());
                }
            }
            else {
                this.moveDrawerToOffset(obj, 1.0f);
                this.updateDrawerState(0, obj);
                obj.setVisibility(0);
            }
            this.invalidate();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("View ");
        sb.append(obj);
        sb.append(" is not a sliding drawer");
        throw new IllegalArgumentException(sb.toString());
    }
    
    public void requestDisallowInterceptTouchEvent(final boolean b) {
        super.requestDisallowInterceptTouchEvent(b);
        if (b) {
            this.closeDrawers(true);
        }
    }
    
    public void requestLayout() {
        if (!this.mInLayout) {
            super.requestLayout();
        }
    }
    
    public void setChildInsets(final Object mLastInsets, final boolean mDrawStatusBarBackground) {
        this.mLastInsets = mLastInsets;
        this.mDrawStatusBarBackground = mDrawStatusBarBackground;
        this.setWillNotDraw(!mDrawStatusBarBackground && this.getBackground() == null);
        this.requestLayout();
    }
    
    public void setDrawerLockMode(final int n, final int n2) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(n2, ViewCompat.getLayoutDirection((View)this));
        if (n2 != 3) {
            if (n2 != 5) {
                if (n2 != 8388611) {
                    if (n2 == 8388613) {
                        this.mLockModeEnd = n;
                    }
                }
                else {
                    this.mLockModeStart = n;
                }
            }
            else {
                this.mLockModeRight = n;
            }
        }
        else {
            this.mLockModeLeft = n;
        }
        if (n != 0) {
            ViewDragHelper viewDragHelper;
            if (absoluteGravity == 3) {
                viewDragHelper = this.mLeftDragger;
            }
            else {
                viewDragHelper = this.mRightDragger;
            }
            viewDragHelper.cancel();
        }
        if (n != 1) {
            if (n == 2) {
                final View drawerWithGravity = this.findDrawerWithGravity(absoluteGravity);
                if (drawerWithGravity != null) {
                    this.openDrawer(drawerWithGravity);
                }
            }
        }
        else {
            final View drawerWithGravity2 = this.findDrawerWithGravity(absoluteGravity);
            if (drawerWithGravity2 != null) {
                this.closeDrawer(drawerWithGravity2);
            }
        }
    }
    
    void setDrawerViewOffset(final View view, final float onScreen) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (onScreen == layoutParams.onScreen) {
            return;
        }
        this.dispatchOnDrawerSlide(view, layoutParams.onScreen = onScreen);
    }
    
    void updateDrawerState(int i, final View view) {
        final int viewDragState = this.mLeftDragger.getViewDragState();
        final int viewDragState2 = this.mRightDragger.getViewDragState();
        final int n = 2;
        int mDrawerState;
        if (viewDragState != 1 && viewDragState2 != 1) {
            mDrawerState = n;
            if (viewDragState != 2) {
                if (viewDragState2 == 2) {
                    mDrawerState = n;
                }
                else {
                    mDrawerState = 0;
                }
            }
        }
        else {
            mDrawerState = 1;
        }
        if (view != null && i == 0) {
            final float onScreen = ((LayoutParams)view.getLayoutParams()).onScreen;
            if (onScreen == 0.0f) {
                this.dispatchOnDrawerClosed(view);
            }
            else if (onScreen == 1.0f) {
                this.dispatchOnDrawerOpened(view);
            }
        }
        if (mDrawerState != this.mDrawerState) {
            this.mDrawerState = mDrawerState;
            final List<DrawerListener> mListeners = this.mListeners;
            if (mListeners != null) {
                for (i = mListeners.size() - 1; i >= 0; --i) {
                    this.mListeners.get(i).onDrawerStateChanged(mDrawerState);
                }
            }
        }
    }
    
    class AccessibilityDelegate extends AccessibilityDelegateCompat
    {
        private final Rect mTmpRect;
        
        AccessibilityDelegate() {
            this.mTmpRect = new Rect();
        }
        
        private void addChildrenForAccessibility(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, final ViewGroup viewGroup) {
            for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
                final View child = viewGroup.getChildAt(i);
                if (DrawerLayout.includeChildForAccessibility(child)) {
                    accessibilityNodeInfoCompat.addChild(child);
                }
            }
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
            accessibilityNodeInfoCompat.setFocused(accessibilityNodeInfoCompat2.isFocused());
            accessibilityNodeInfoCompat.setAccessibilityFocused(accessibilityNodeInfoCompat2.isAccessibilityFocused());
            accessibilityNodeInfoCompat.setSelected(accessibilityNodeInfoCompat2.isSelected());
            accessibilityNodeInfoCompat.addAction(accessibilityNodeInfoCompat2.getActions());
        }
        
        @Override
        public boolean dispatchPopulateAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            if (accessibilityEvent.getEventType() == 32) {
                final List text = accessibilityEvent.getText();
                final View visibleDrawer = DrawerLayout.this.findVisibleDrawer();
                if (visibleDrawer != null) {
                    final CharSequence drawerTitle = DrawerLayout.this.getDrawerTitle(DrawerLayout.this.getDrawerViewAbsoluteGravity(visibleDrawer));
                    if (drawerTitle != null) {
                        text.add(drawerTitle);
                    }
                }
                return true;
            }
            return super.dispatchPopulateAccessibilityEvent(view, accessibilityEvent);
        }
        
        @Override
        public void onInitializeAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName((CharSequence)"androidx.drawerlayout.widget.DrawerLayout");
        }
        
        @Override
        public void onInitializeAccessibilityNodeInfo(final View source, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            if (DrawerLayout.CAN_HIDE_DESCENDANTS) {
                super.onInitializeAccessibilityNodeInfo(source, accessibilityNodeInfoCompat);
            }
            else {
                final AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain(accessibilityNodeInfoCompat);
                super.onInitializeAccessibilityNodeInfo(source, obtain);
                accessibilityNodeInfoCompat.setSource(source);
                final ViewParent parentForAccessibility = ViewCompat.getParentForAccessibility(source);
                if (parentForAccessibility instanceof View) {
                    accessibilityNodeInfoCompat.setParent((View)parentForAccessibility);
                }
                this.copyNodeInfoNoChildren(accessibilityNodeInfoCompat, obtain);
                obtain.recycle();
                this.addChildrenForAccessibility(accessibilityNodeInfoCompat, (ViewGroup)source);
            }
            accessibilityNodeInfoCompat.setClassName("androidx.drawerlayout.widget.DrawerLayout");
            accessibilityNodeInfoCompat.setFocusable(false);
            accessibilityNodeInfoCompat.setFocused(false);
            accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_FOCUS);
            accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLEAR_FOCUS);
        }
        
        @Override
        public boolean onRequestSendAccessibilityEvent(final ViewGroup viewGroup, final View view, final AccessibilityEvent accessibilityEvent) {
            return (DrawerLayout.CAN_HIDE_DESCENDANTS || DrawerLayout.includeChildForAccessibility(view)) && super.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
        }
    }
    
    static final class ChildAccessibilityDelegate extends AccessibilityDelegateCompat
    {
        @Override
        public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            if (!DrawerLayout.includeChildForAccessibility(view)) {
                accessibilityNodeInfoCompat.setParent(null);
            }
        }
    }
    
    public interface DrawerListener
    {
        void onDrawerClosed(final View p0);
        
        void onDrawerOpened(final View p0);
        
        void onDrawerSlide(final View p0, final float p1);
        
        void onDrawerStateChanged(final int p0);
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        public int gravity;
        boolean isPeeking;
        float onScreen;
        int openState;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.gravity = 0;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.gravity = 0;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, DrawerLayout.LAYOUT_ATTRS);
            this.gravity = obtainStyledAttributes.getInt(0, 0);
            obtainStyledAttributes.recycle();
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.gravity = 0;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.gravity = 0;
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$MarginLayoutParams)layoutParams);
            this.gravity = 0;
            this.gravity = layoutParams.gravity;
        }
    }
    
    protected static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int lockModeEnd;
        int lockModeLeft;
        int lockModeRight;
        int lockModeStart;
        int openDrawerGravity;
        
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
            this.openDrawerGravity = 0;
            this.openDrawerGravity = parcel.readInt();
            this.lockModeLeft = parcel.readInt();
            this.lockModeRight = parcel.readInt();
            this.lockModeStart = parcel.readInt();
            this.lockModeEnd = parcel.readInt();
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
            this.openDrawerGravity = 0;
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.openDrawerGravity);
            parcel.writeInt(this.lockModeLeft);
            parcel.writeInt(this.lockModeRight);
            parcel.writeInt(this.lockModeStart);
            parcel.writeInt(this.lockModeEnd);
        }
    }
    
    private class ViewDragCallback extends Callback
    {
        private final int mAbsGravity;
        private ViewDragHelper mDragger;
        private final Runnable mPeekRunnable;
        
        ViewDragCallback(final int mAbsGravity) {
            this.mPeekRunnable = new Runnable() {
                @Override
                public void run() {
                    ViewDragCallback.this.peekDrawer();
                }
            };
            this.mAbsGravity = mAbsGravity;
        }
        
        private void closeOtherDrawer() {
            final int mAbsGravity = this.mAbsGravity;
            int n = 3;
            if (mAbsGravity == 3) {
                n = 5;
            }
            final View drawerWithGravity = DrawerLayout.this.findDrawerWithGravity(n);
            if (drawerWithGravity != null) {
                DrawerLayout.this.closeDrawer(drawerWithGravity);
            }
        }
        
        @Override
        public int clampViewPositionHorizontal(final View view, final int n, int width) {
            if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                return Math.max(-view.getWidth(), Math.min(n, 0));
            }
            width = DrawerLayout.this.getWidth();
            return Math.max(width - view.getWidth(), Math.min(n, width));
        }
        
        @Override
        public int clampViewPositionVertical(final View view, final int n, final int n2) {
            return view.getTop();
        }
        
        @Override
        public int getViewHorizontalDragRange(final View view) {
            int width;
            if (DrawerLayout.this.isDrawerView(view)) {
                width = view.getWidth();
            }
            else {
                width = 0;
            }
            return width;
        }
        
        @Override
        public void onEdgeDragStarted(final int n, final int n2) {
            View view;
            if ((n & 0x1) == 0x1) {
                view = DrawerLayout.this.findDrawerWithGravity(3);
            }
            else {
                view = DrawerLayout.this.findDrawerWithGravity(5);
            }
            if (view != null && DrawerLayout.this.getDrawerLockMode(view) == 0) {
                this.mDragger.captureChildView(view, n2);
            }
        }
        
        @Override
        public boolean onEdgeLock(final int n) {
            return false;
        }
        
        @Override
        public void onEdgeTouched(final int n, final int n2) {
            DrawerLayout.this.postDelayed(this.mPeekRunnable, 160L);
        }
        
        @Override
        public void onViewCaptured(final View view, final int n) {
            ((LayoutParams)view.getLayoutParams()).isPeeking = false;
            this.closeOtherDrawer();
        }
        
        @Override
        public void onViewDragStateChanged(final int n) {
            DrawerLayout.this.updateDrawerState(n, this.mDragger.getCapturedView());
        }
        
        @Override
        public void onViewPositionChanged(final View view, int visibility, int width, final int n, final int n2) {
            width = view.getWidth();
            float n3;
            if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                n3 = (float)(visibility + width);
            }
            else {
                n3 = (float)(DrawerLayout.this.getWidth() - visibility);
            }
            final float n4 = n3 / width;
            DrawerLayout.this.setDrawerViewOffset(view, n4);
            if (n4 == 0.0f) {
                visibility = 4;
            }
            else {
                visibility = 0;
            }
            view.setVisibility(visibility);
            DrawerLayout.this.invalidate();
        }
        
        @Override
        public void onViewReleased(final View view, final float n, float drawerViewOffset) {
            drawerViewOffset = DrawerLayout.this.getDrawerViewOffset(view);
            final int width = view.getWidth();
            int n3 = 0;
            Label_0109: {
                if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                    final float n2 = fcmpl(n, 0.0f);
                    if (n2 <= 0 && (n2 != 0 || drawerViewOffset <= 0.5f)) {
                        n3 = -width;
                    }
                    else {
                        n3 = 0;
                    }
                }
                else {
                    final int width2 = DrawerLayout.this.getWidth();
                    if (n >= 0.0f) {
                        n3 = width2;
                        if (n != 0.0f) {
                            break Label_0109;
                        }
                        n3 = width2;
                        if (drawerViewOffset <= 0.5f) {
                            break Label_0109;
                        }
                    }
                    n3 = width2 - width;
                }
            }
            this.mDragger.settleCapturedViewAt(n3, view.getTop());
            DrawerLayout.this.invalidate();
        }
        
        void peekDrawer() {
            final int edgeSize = this.mDragger.getEdgeSize();
            final int mAbsGravity = this.mAbsGravity;
            int n = 0;
            final boolean b = mAbsGravity == 3;
            View view;
            int n2;
            if (b) {
                view = DrawerLayout.this.findDrawerWithGravity(3);
                if (view != null) {
                    n = -view.getWidth();
                }
                n2 = n + edgeSize;
            }
            else {
                view = DrawerLayout.this.findDrawerWithGravity(5);
                n2 = DrawerLayout.this.getWidth() - edgeSize;
            }
            if (view != null && ((b && view.getLeft() < n2) || (!b && view.getLeft() > n2)) && DrawerLayout.this.getDrawerLockMode(view) == 0) {
                final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
                this.mDragger.smoothSlideViewTo(view, n2, view.getTop());
                layoutParams.isPeeking = true;
                DrawerLayout.this.invalidate();
                this.closeOtherDrawer();
                DrawerLayout.this.cancelChildViewTouch();
            }
        }
        
        public void removeCallbacks() {
            DrawerLayout.this.removeCallbacks(this.mPeekRunnable);
        }
        
        public void setDragger(final ViewDragHelper mDragger) {
            this.mDragger = mDragger;
        }
        
        @Override
        public boolean tryCaptureView(final View view, final int n) {
            return DrawerLayout.this.isDrawerView(view) && DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, this.mAbsGravity) && DrawerLayout.this.getDrawerLockMode(view) == 0;
        }
    }
}
