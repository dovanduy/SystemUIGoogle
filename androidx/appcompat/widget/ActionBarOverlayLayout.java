// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.view.ViewGroup$MarginLayoutParams;
import android.view.Window$Callback;
import androidx.appcompat.view.menu.MenuPresenter;
import android.view.Menu;
import androidx.appcompat.R$id;
import androidx.core.graphics.Insets;
import android.content.res.Configuration;
import androidx.core.view.ViewCompat;
import android.view.WindowInsets;
import android.os.Build$VERSION;
import android.graphics.Canvas;
import android.view.ViewGroup$LayoutParams;
import android.content.res.TypedArray;
import android.view.View;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.util.AttributeSet;
import android.content.Context;
import androidx.appcompat.R$attr;
import android.graphics.drawable.Drawable;
import android.animation.AnimatorListenerAdapter;
import androidx.core.view.NestedScrollingParentHelper;
import android.widget.OverScroller;
import android.view.ViewPropertyAnimator;
import android.graphics.Rect;
import androidx.core.view.WindowInsetsCompat;
import android.annotation.SuppressLint;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParent;
import android.view.ViewGroup;

@SuppressLint({ "UnknownNullness" })
public class ActionBarOverlayLayout extends ViewGroup implements DecorContentParent, NestedScrollingParent, NestedScrollingParent2, NestedScrollingParent3
{
    static final int[] ATTRS;
    private static final WindowInsetsCompat CONSUMED;
    private int mActionBarHeight;
    ActionBarContainer mActionBarTop;
    private ActionBarVisibilityCallback mActionBarVisibilityCallback;
    private final Runnable mAddActionBarHideOffset;
    boolean mAnimatingForFling;
    private final Rect mBaseContentInsets;
    private WindowInsetsCompat mBaseInnerInsets;
    private final Rect mBaseInnerInsetsRect;
    private ContentFrameLayout mContent;
    private final Rect mContentInsets;
    ViewPropertyAnimator mCurrentActionBarTopAnimator;
    private DecorToolbar mDecorToolbar;
    private OverScroller mFlingEstimator;
    private boolean mHasNonEmbeddedTabs;
    private boolean mHideOnContentScroll;
    private int mHideOnContentScrollReference;
    private boolean mIgnoreWindowContentOverlay;
    private WindowInsetsCompat mInnerInsets;
    private final Rect mInnerInsetsRect;
    private final Rect mLastBaseContentInsets;
    private WindowInsetsCompat mLastBaseInnerInsets;
    private final Rect mLastBaseInnerInsetsRect;
    private WindowInsetsCompat mLastInnerInsets;
    private final Rect mLastInnerInsetsRect;
    private int mLastSystemUiVisibility;
    private boolean mOverlayMode;
    private final NestedScrollingParentHelper mParentHelper;
    private final Runnable mRemoveActionBarHideOffset;
    final AnimatorListenerAdapter mTopAnimatorListener;
    private Drawable mWindowContentOverlay;
    private int mWindowVisibility;
    
    static {
        CONSUMED = new WindowInsetsCompat.Builder().build().consumeSystemWindowInsets().consumeStableInsets().consumeDisplayCutout();
        ATTRS = new int[] { R$attr.actionBarSize, 16842841 };
    }
    
    public ActionBarOverlayLayout(final Context context) {
        this(context, null);
    }
    
    public ActionBarOverlayLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.mWindowVisibility = 0;
        this.mBaseContentInsets = new Rect();
        this.mLastBaseContentInsets = new Rect();
        this.mContentInsets = new Rect();
        this.mBaseInnerInsetsRect = new Rect();
        this.mLastBaseInnerInsetsRect = new Rect();
        this.mInnerInsetsRect = new Rect();
        this.mLastInnerInsetsRect = new Rect();
        final WindowInsetsCompat consumed = ActionBarOverlayLayout.CONSUMED;
        this.mBaseInnerInsets = consumed;
        this.mLastBaseInnerInsets = consumed;
        this.mInnerInsets = consumed;
        this.mLastInnerInsets = consumed;
        this.mTopAnimatorListener = new AnimatorListenerAdapter() {
            public void onAnimationCancel(final Animator animator) {
                final ActionBarOverlayLayout this$0 = ActionBarOverlayLayout.this;
                this$0.mCurrentActionBarTopAnimator = null;
                this$0.mAnimatingForFling = false;
            }
            
            public void onAnimationEnd(final Animator animator) {
                final ActionBarOverlayLayout this$0 = ActionBarOverlayLayout.this;
                this$0.mCurrentActionBarTopAnimator = null;
                this$0.mAnimatingForFling = false;
            }
        };
        this.mRemoveActionBarHideOffset = new Runnable() {
            @Override
            public void run() {
                ActionBarOverlayLayout.this.haltActionBarHideOffsetAnimations();
                final ActionBarOverlayLayout this$0 = ActionBarOverlayLayout.this;
                this$0.mCurrentActionBarTopAnimator = this$0.mActionBarTop.animate().translationY(0.0f).setListener((Animator$AnimatorListener)ActionBarOverlayLayout.this.mTopAnimatorListener);
            }
        };
        this.mAddActionBarHideOffset = new Runnable() {
            @Override
            public void run() {
                ActionBarOverlayLayout.this.haltActionBarHideOffsetAnimations();
                final ActionBarOverlayLayout this$0 = ActionBarOverlayLayout.this;
                this$0.mCurrentActionBarTopAnimator = this$0.mActionBarTop.animate().translationY((float)(-ActionBarOverlayLayout.this.mActionBarTop.getHeight())).setListener((Animator$AnimatorListener)ActionBarOverlayLayout.this.mTopAnimatorListener);
            }
        };
        this.init(context);
        this.mParentHelper = new NestedScrollingParentHelper(this);
    }
    
    private void addActionBarHideOffset() {
        this.haltActionBarHideOffsetAnimations();
        this.mAddActionBarHideOffset.run();
    }
    
    private boolean applyInsets(final View view, final Rect rect, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final boolean b5 = true;
        boolean b6 = false;
        Label_0049: {
            if (b) {
                final int leftMargin = layoutParams.leftMargin;
                final int left = rect.left;
                if (leftMargin != left) {
                    layoutParams.leftMargin = left;
                    b6 = true;
                    break Label_0049;
                }
            }
            b6 = false;
        }
        boolean b7 = b6;
        if (b2) {
            final int topMargin = layoutParams.topMargin;
            final int top = rect.top;
            b7 = b6;
            if (topMargin != top) {
                layoutParams.topMargin = top;
                b7 = true;
            }
        }
        boolean b8 = b7;
        if (b4) {
            final int rightMargin = layoutParams.rightMargin;
            final int right = rect.right;
            b8 = b7;
            if (rightMargin != right) {
                layoutParams.rightMargin = right;
                b8 = true;
            }
        }
        if (b3) {
            final int bottomMargin = layoutParams.bottomMargin;
            final int bottom = rect.bottom;
            if (bottomMargin != bottom) {
                layoutParams.bottomMargin = bottom;
                b8 = b5;
            }
        }
        return b8;
    }
    
    private DecorToolbar getDecorToolbar(final View view) {
        if (view instanceof DecorToolbar) {
            return (DecorToolbar)view;
        }
        if (view instanceof Toolbar) {
            return ((Toolbar)view).getWrapper();
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Can't make a decor toolbar out of ");
        sb.append(view.getClass().getSimpleName());
        throw new IllegalStateException(sb.toString());
    }
    
    private void init(final Context context) {
        final TypedArray obtainStyledAttributes = this.getContext().getTheme().obtainStyledAttributes(ActionBarOverlayLayout.ATTRS);
        final boolean b = false;
        this.mActionBarHeight = obtainStyledAttributes.getDimensionPixelSize(0, 0);
        final Drawable drawable = obtainStyledAttributes.getDrawable(1);
        this.mWindowContentOverlay = drawable;
        this.setWillNotDraw(drawable == null);
        obtainStyledAttributes.recycle();
        boolean mIgnoreWindowContentOverlay = b;
        if (context.getApplicationInfo().targetSdkVersion < 19) {
            mIgnoreWindowContentOverlay = true;
        }
        this.mIgnoreWindowContentOverlay = mIgnoreWindowContentOverlay;
        this.mFlingEstimator = new OverScroller(context);
    }
    
    private void postAddActionBarHideOffset() {
        this.haltActionBarHideOffsetAnimations();
        this.postDelayed(this.mAddActionBarHideOffset, 600L);
    }
    
    private void postRemoveActionBarHideOffset() {
        this.haltActionBarHideOffsetAnimations();
        this.postDelayed(this.mRemoveActionBarHideOffset, 600L);
    }
    
    private void removeActionBarHideOffset() {
        this.haltActionBarHideOffsetAnimations();
        this.mRemoveActionBarHideOffset.run();
    }
    
    private boolean shouldHideActionBarOnFling(final float n) {
        this.mFlingEstimator.fling(0, 0, 0, (int)n, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return this.mFlingEstimator.getFinalY() > this.mActionBarTop.getHeight();
    }
    
    public boolean canShowOverflowMenu() {
        this.pullChildren();
        return this.mDecorToolbar.canShowOverflowMenu();
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams;
    }
    
    public void dismissPopups() {
        this.pullChildren();
        this.mDecorToolbar.dismissPopupMenus();
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        if (this.mWindowContentOverlay != null && !this.mIgnoreWindowContentOverlay) {
            int n;
            if (this.mActionBarTop.getVisibility() == 0) {
                n = (int)(this.mActionBarTop.getBottom() + this.mActionBarTop.getTranslationY() + 0.5f);
            }
            else {
                n = 0;
            }
            this.mWindowContentOverlay.setBounds(0, n, this.getWidth(), this.mWindowContentOverlay.getIntrinsicHeight() + n);
            this.mWindowContentOverlay.draw(canvas);
        }
    }
    
    protected boolean fitSystemWindows(final Rect rect) {
        if (Build$VERSION.SDK_INT >= 21) {
            return super.fitSystemWindows(rect);
        }
        this.pullChildren();
        boolean applyInsets = this.applyInsets((View)this.mActionBarTop, rect, true, true, false, true);
        this.mBaseInnerInsetsRect.set(rect);
        ViewUtils.computeFitSystemWindows((View)this, this.mBaseInnerInsetsRect, this.mBaseContentInsets);
        if (!this.mLastBaseInnerInsetsRect.equals((Object)this.mBaseInnerInsetsRect)) {
            this.mLastBaseInnerInsetsRect.set(this.mBaseInnerInsetsRect);
            applyInsets = true;
        }
        if (!this.mLastBaseContentInsets.equals((Object)this.mBaseContentInsets)) {
            this.mLastBaseContentInsets.set(this.mBaseContentInsets);
            applyInsets = true;
        }
        if (applyInsets) {
            this.requestLayout();
        }
        return true;
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return (ViewGroup$LayoutParams)new LayoutParams(viewGroup$LayoutParams);
    }
    
    public LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    public int getActionBarHideOffset() {
        final ActionBarContainer mActionBarTop = this.mActionBarTop;
        int n;
        if (mActionBarTop != null) {
            n = -(int)mActionBarTop.getTranslationY();
        }
        else {
            n = 0;
        }
        return n;
    }
    
    public int getNestedScrollAxes() {
        return this.mParentHelper.getNestedScrollAxes();
    }
    
    void haltActionBarHideOffsetAnimations() {
        this.removeCallbacks(this.mRemoveActionBarHideOffset);
        this.removeCallbacks(this.mAddActionBarHideOffset);
        final ViewPropertyAnimator mCurrentActionBarTopAnimator = this.mCurrentActionBarTopAnimator;
        if (mCurrentActionBarTopAnimator != null) {
            mCurrentActionBarTopAnimator.cancel();
        }
    }
    
    public boolean hideOverflowMenu() {
        this.pullChildren();
        return this.mDecorToolbar.hideOverflowMenu();
    }
    
    public void initFeature(final int n) {
        this.pullChildren();
        if (n != 2) {
            if (n != 5) {
                if (n == 109) {
                    this.setOverlayMode(true);
                }
            }
            else {
                this.mDecorToolbar.initIndeterminateProgress();
            }
        }
        else {
            this.mDecorToolbar.initProgress();
        }
    }
    
    public boolean isInOverlayMode() {
        return this.mOverlayMode;
    }
    
    public boolean isOverflowMenuShowPending() {
        this.pullChildren();
        return this.mDecorToolbar.isOverflowMenuShowPending();
    }
    
    public boolean isOverflowMenuShowing() {
        this.pullChildren();
        return this.mDecorToolbar.isOverflowMenuShowing();
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        this.pullChildren();
        final WindowInsetsCompat windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(windowInsets);
        int applyInsets = this.applyInsets((View)this.mActionBarTop, new Rect(windowInsetsCompat.getSystemWindowInsetLeft(), windowInsetsCompat.getSystemWindowInsetTop(), windowInsetsCompat.getSystemWindowInsetRight(), windowInsetsCompat.getSystemWindowInsetBottom()), true, true, false, true) ? 1 : 0;
        this.mBaseContentInsets.setEmpty();
        ViewCompat.computeSystemWindowInsets((View)this, this.mBaseInnerInsets, this.mBaseContentInsets);
        final Rect mBaseContentInsets = this.mBaseContentInsets;
        final WindowInsetsCompat inset = windowInsetsCompat.inset(mBaseContentInsets.left, mBaseContentInsets.top, mBaseContentInsets.right, mBaseContentInsets.bottom);
        this.mBaseInnerInsets = inset;
        final boolean equals = this.mLastBaseInnerInsets.equals(inset);
        final int n = 1;
        if (!equals) {
            this.mLastBaseInnerInsets = this.mBaseInnerInsets;
            applyInsets = 1;
        }
        if (!this.mLastBaseContentInsets.equals((Object)this.mBaseContentInsets)) {
            this.mLastBaseContentInsets.set(this.mBaseContentInsets);
            applyInsets = n;
        }
        if (applyInsets != 0) {
            this.requestLayout();
        }
        return ActionBarOverlayLayout.CONSUMED.toWindowInsets();
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.init(this.getContext());
        ViewCompat.requestApplyInsets((View)this);
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.haltActionBarHideOffsetAnimations();
    }
    
    protected void onLayout(final boolean b, int i, int paddingLeft, int childCount, int paddingTop) {
        childCount = this.getChildCount();
        paddingLeft = this.getPaddingLeft();
        paddingTop = this.getPaddingTop();
        View child;
        LayoutParams layoutParams;
        int measuredWidth;
        int measuredHeight;
        int n;
        int n2;
        for (i = 0; i < childCount; ++i) {
            child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                layoutParams = (LayoutParams)child.getLayoutParams();
                measuredWidth = child.getMeasuredWidth();
                measuredHeight = child.getMeasuredHeight();
                n = layoutParams.leftMargin + paddingLeft;
                n2 = layoutParams.topMargin + paddingTop;
                child.layout(n, n2, measuredWidth + n, measuredHeight + n2);
            }
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        this.pullChildren();
        this.measureChildWithMargins((View)this.mActionBarTop, n, 0, n2, 0);
        final LayoutParams layoutParams = (LayoutParams)this.mActionBarTop.getLayoutParams();
        final int max = Math.max(0, this.mActionBarTop.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
        final int max2 = Math.max(0, this.mActionBarTop.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
        final int combineMeasuredStates = View.combineMeasuredStates(0, this.mActionBarTop.getMeasuredState());
        final boolean b = (ViewCompat.getWindowSystemUiVisibility((View)this) & 0x100) != 0x0;
        int n4;
        if (b) {
            final int n3 = n4 = this.mActionBarHeight;
            if (this.mHasNonEmbeddedTabs) {
                n4 = n3;
                if (this.mActionBarTop.getTabContainer() != null) {
                    n4 = n3 + this.mActionBarHeight;
                }
            }
        }
        else if (this.mActionBarTop.getVisibility() != 8) {
            n4 = this.mActionBarTop.getMeasuredHeight();
        }
        else {
            n4 = 0;
        }
        this.mContentInsets.set(this.mBaseContentInsets);
        if (sdk_INT >= 21) {
            this.mInnerInsets = this.mBaseInnerInsets;
        }
        else {
            this.mInnerInsetsRect.set(this.mBaseInnerInsetsRect);
        }
        if (!this.mOverlayMode && !b) {
            final Rect mContentInsets = this.mContentInsets;
            mContentInsets.top += n4;
            mContentInsets.bottom += 0;
            if (sdk_INT >= 21) {
                this.mInnerInsets = this.mInnerInsets.inset(0, n4, 0, 0);
            }
        }
        else if (sdk_INT >= 21) {
            final Insets of = Insets.of(this.mInnerInsets.getSystemWindowInsetLeft(), this.mInnerInsets.getSystemWindowInsetTop() + n4, this.mInnerInsets.getSystemWindowInsetRight(), this.mInnerInsets.getSystemWindowInsetBottom() + 0);
            final WindowInsetsCompat.Builder builder = new WindowInsetsCompat.Builder(this.mInnerInsets);
            builder.setSystemWindowInsets(of);
            this.mInnerInsets = builder.build();
        }
        else {
            final Rect mInnerInsetsRect = this.mInnerInsetsRect;
            mInnerInsetsRect.top += n4;
            mInnerInsetsRect.bottom += 0;
        }
        this.applyInsets((View)this.mContent, this.mContentInsets, true, true, true, true);
        if (sdk_INT >= 21 && !this.mLastInnerInsets.equals(this.mInnerInsets)) {
            final WindowInsetsCompat mInnerInsets = this.mInnerInsets;
            this.mLastInnerInsets = mInnerInsets;
            ViewCompat.dispatchApplyWindowInsets((View)this.mContent, mInnerInsets);
        }
        else if (sdk_INT < 21 && !this.mLastInnerInsetsRect.equals((Object)this.mInnerInsetsRect)) {
            this.mLastInnerInsetsRect.set(this.mInnerInsetsRect);
            this.mContent.dispatchFitSystemWindows(this.mInnerInsetsRect);
        }
        this.measureChildWithMargins((View)this.mContent, n, 0, n2, 0);
        final LayoutParams layoutParams2 = (LayoutParams)this.mContent.getLayoutParams();
        final int max3 = Math.max(max, this.mContent.getMeasuredWidth() + layoutParams2.leftMargin + layoutParams2.rightMargin);
        final int max4 = Math.max(max2, this.mContent.getMeasuredHeight() + layoutParams2.topMargin + layoutParams2.bottomMargin);
        final int combineMeasuredStates2 = View.combineMeasuredStates(combineMeasuredStates, this.mContent.getMeasuredState());
        this.setMeasuredDimension(View.resolveSizeAndState(Math.max(max3 + (this.getPaddingLeft() + this.getPaddingRight()), this.getSuggestedMinimumWidth()), n, combineMeasuredStates2), View.resolveSizeAndState(Math.max(max4 + (this.getPaddingTop() + this.getPaddingBottom()), this.getSuggestedMinimumHeight()), n2, combineMeasuredStates2 << 16));
    }
    
    public boolean onNestedFling(final View view, final float n, final float n2, final boolean b) {
        if (this.mHideOnContentScroll && b) {
            if (this.shouldHideActionBarOnFling(n2)) {
                this.addActionBarHideOffset();
            }
            else {
                this.removeActionBarHideOffset();
            }
            return this.mAnimatingForFling = true;
        }
        return false;
    }
    
    public boolean onNestedPreFling(final View view, final float n, final float n2) {
        return false;
    }
    
    public void onNestedPreScroll(final View view, final int n, final int n2, final int[] array) {
    }
    
    public void onNestedPreScroll(final View view, final int n, final int n2, final int[] array, final int n3) {
        if (n3 == 0) {
            this.onNestedPreScroll(view, n, n2, array);
        }
    }
    
    public void onNestedScroll(final View view, int mHideOnContentScrollReference, final int n, final int n2, final int n3) {
        mHideOnContentScrollReference = this.mHideOnContentScrollReference + n;
        this.setActionBarHideOffset(this.mHideOnContentScrollReference = mHideOnContentScrollReference);
    }
    
    public void onNestedScroll(final View view, final int n, final int n2, final int n3, final int n4, final int n5) {
        if (n5 == 0) {
            this.onNestedScroll(view, n, n2, n3, n4);
        }
    }
    
    public void onNestedScroll(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int[] array) {
        this.onNestedScroll(view, n, n2, n3, n4, n5);
    }
    
    public void onNestedScrollAccepted(final View view, final View view2, final int n) {
        this.mParentHelper.onNestedScrollAccepted(view, view2, n);
        this.mHideOnContentScrollReference = this.getActionBarHideOffset();
        this.haltActionBarHideOffsetAnimations();
        final ActionBarVisibilityCallback mActionBarVisibilityCallback = this.mActionBarVisibilityCallback;
        if (mActionBarVisibilityCallback != null) {
            mActionBarVisibilityCallback.onContentScrollStarted();
        }
    }
    
    public void onNestedScrollAccepted(final View view, final View view2, final int n, final int n2) {
        if (n2 == 0) {
            this.onNestedScrollAccepted(view, view2, n);
        }
    }
    
    public boolean onStartNestedScroll(final View view, final View view2, final int n) {
        return (n & 0x2) != 0x0 && this.mActionBarTop.getVisibility() == 0 && this.mHideOnContentScroll;
    }
    
    public boolean onStartNestedScroll(final View view, final View view2, final int n, final int n2) {
        return n2 == 0 && this.onStartNestedScroll(view, view2, n);
    }
    
    public void onStopNestedScroll(final View view) {
        if (this.mHideOnContentScroll && !this.mAnimatingForFling) {
            if (this.mHideOnContentScrollReference <= this.mActionBarTop.getHeight()) {
                this.postRemoveActionBarHideOffset();
            }
            else {
                this.postAddActionBarHideOffset();
            }
        }
        final ActionBarVisibilityCallback mActionBarVisibilityCallback = this.mActionBarVisibilityCallback;
        if (mActionBarVisibilityCallback != null) {
            mActionBarVisibilityCallback.onContentScrollStopped();
        }
    }
    
    public void onStopNestedScroll(final View view, final int n) {
        if (n == 0) {
            this.onStopNestedScroll(view);
        }
    }
    
    public void onWindowSystemUiVisibilityChanged(final int mLastSystemUiVisibility) {
        if (Build$VERSION.SDK_INT >= 16) {
            super.onWindowSystemUiVisibilityChanged(mLastSystemUiVisibility);
        }
        this.pullChildren();
        final int mLastSystemUiVisibility2 = this.mLastSystemUiVisibility;
        this.mLastSystemUiVisibility = mLastSystemUiVisibility;
        boolean b = false;
        final boolean b2 = (mLastSystemUiVisibility & 0x4) == 0x0;
        if ((mLastSystemUiVisibility & 0x100) != 0x0) {
            b = true;
        }
        final ActionBarVisibilityCallback mActionBarVisibilityCallback = this.mActionBarVisibilityCallback;
        if (mActionBarVisibilityCallback != null) {
            mActionBarVisibilityCallback.enableContentAnimations(b ^ true);
            if (!b2 && b) {
                this.mActionBarVisibilityCallback.hideForSystem();
            }
            else {
                this.mActionBarVisibilityCallback.showForSystem();
            }
        }
        if (((mLastSystemUiVisibility2 ^ mLastSystemUiVisibility) & 0x100) != 0x0 && this.mActionBarVisibilityCallback != null) {
            ViewCompat.requestApplyInsets((View)this);
        }
    }
    
    protected void onWindowVisibilityChanged(final int mWindowVisibility) {
        super.onWindowVisibilityChanged(mWindowVisibility);
        this.mWindowVisibility = mWindowVisibility;
        final ActionBarVisibilityCallback mActionBarVisibilityCallback = this.mActionBarVisibilityCallback;
        if (mActionBarVisibilityCallback != null) {
            mActionBarVisibilityCallback.onWindowVisibilityChanged(mWindowVisibility);
        }
    }
    
    void pullChildren() {
        if (this.mContent == null) {
            this.mContent = (ContentFrameLayout)this.findViewById(R$id.action_bar_activity_content);
            this.mActionBarTop = (ActionBarContainer)this.findViewById(R$id.action_bar_container);
            this.mDecorToolbar = this.getDecorToolbar(this.findViewById(R$id.action_bar));
        }
    }
    
    public void setActionBarHideOffset(int max) {
        this.haltActionBarHideOffsetAnimations();
        max = Math.max(0, Math.min(max, this.mActionBarTop.getHeight()));
        this.mActionBarTop.setTranslationY((float)(-max));
    }
    
    public void setActionBarVisibilityCallback(final ActionBarVisibilityCallback mActionBarVisibilityCallback) {
        this.mActionBarVisibilityCallback = mActionBarVisibilityCallback;
        if (this.getWindowToken() != null) {
            this.mActionBarVisibilityCallback.onWindowVisibilityChanged(this.mWindowVisibility);
            final int mLastSystemUiVisibility = this.mLastSystemUiVisibility;
            if (mLastSystemUiVisibility != 0) {
                this.onWindowSystemUiVisibilityChanged(mLastSystemUiVisibility);
                ViewCompat.requestApplyInsets((View)this);
            }
        }
    }
    
    public void setHasNonEmbeddedTabs(final boolean mHasNonEmbeddedTabs) {
        this.mHasNonEmbeddedTabs = mHasNonEmbeddedTabs;
    }
    
    public void setHideOnContentScrollEnabled(final boolean mHideOnContentScroll) {
        if (mHideOnContentScroll != this.mHideOnContentScroll && !(this.mHideOnContentScroll = mHideOnContentScroll)) {
            this.haltActionBarHideOffsetAnimations();
            this.setActionBarHideOffset(0);
        }
    }
    
    public void setMenu(final Menu menu, final MenuPresenter.Callback callback) {
        this.pullChildren();
        this.mDecorToolbar.setMenu(menu, callback);
    }
    
    public void setMenuPrepared() {
        this.pullChildren();
        this.mDecorToolbar.setMenuPrepared();
    }
    
    public void setOverlayMode(final boolean mOverlayMode) {
        this.mOverlayMode = mOverlayMode;
        this.mIgnoreWindowContentOverlay = (mOverlayMode && this.getContext().getApplicationInfo().targetSdkVersion < 19);
    }
    
    public void setShowingForActionMode(final boolean b) {
    }
    
    public void setWindowCallback(final Window$Callback windowCallback) {
        this.pullChildren();
        this.mDecorToolbar.setWindowCallback(windowCallback);
    }
    
    public void setWindowTitle(final CharSequence windowTitle) {
        this.pullChildren();
        this.mDecorToolbar.setWindowTitle(windowTitle);
    }
    
    public boolean shouldDelayChildPressedState() {
        return false;
    }
    
    public boolean showOverflowMenu() {
        this.pullChildren();
        return this.mDecorToolbar.showOverflowMenu();
    }
    
    public interface ActionBarVisibilityCallback
    {
        void enableContentAnimations(final boolean p0);
        
        void hideForSystem();
        
        void onContentScrollStarted();
        
        void onContentScrollStopped();
        
        void onWindowVisibilityChanged(final int p0);
        
        void showForSystem();
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
        }
    }
}
