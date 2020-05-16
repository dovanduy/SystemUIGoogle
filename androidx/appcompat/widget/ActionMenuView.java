// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.view.ViewDebug$ExportedProperty;
import android.view.ContextThemeWrapper;
import android.content.res.Configuration;
import android.view.MenuItem;
import androidx.appcompat.view.menu.MenuItemImpl;
import android.view.Menu;
import android.view.accessibility.AccessibilityEvent;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup;
import androidx.appcompat.view.menu.ActionMenuItemView;
import android.view.View$MeasureSpec;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import androidx.appcompat.view.menu.MenuPresenter;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.view.menu.MenuBuilder;

public class ActionMenuView extends LinearLayoutCompat implements ItemInvoker, MenuView
{
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    Callback mMenuBuilderCallback;
    private int mMinCellSize;
    OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;
    
    public ActionMenuView(final Context context) {
        this(context, null);
    }
    
    public ActionMenuView(final Context mPopupContext, final AttributeSet set) {
        super(mPopupContext, set);
        this.setBaselineAligned(false);
        final float density = mPopupContext.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int)(56.0f * density);
        this.mGeneratedItemPadding = (int)(density * 4.0f);
        this.mPopupContext = mPopupContext;
        this.mPopupTheme = 0;
    }
    
    static int measureChildForCells(final View view, final int n, int cellsUsed, int n2, int n3) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(View$MeasureSpec.getSize(n2) - n3, View$MeasureSpec.getMode(n2));
        ActionMenuItemView actionMenuItemView;
        if (view instanceof ActionMenuItemView) {
            actionMenuItemView = (ActionMenuItemView)view;
        }
        else {
            actionMenuItemView = null;
        }
        boolean expandable = true;
        if (actionMenuItemView != null && actionMenuItemView.hasText()) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        n3 = 2;
        if (cellsUsed > 0 && (n2 == 0 || cellsUsed >= 2)) {
            view.measure(View$MeasureSpec.makeMeasureSpec(cellsUsed * n, Integer.MIN_VALUE), measureSpec);
            final int measuredWidth = view.getMeasuredWidth();
            final int n4 = cellsUsed = measuredWidth / n;
            if (measuredWidth % n != 0) {
                cellsUsed = n4 + 1;
            }
            if (n2 != 0 && cellsUsed < 2) {
                cellsUsed = n3;
            }
        }
        else {
            cellsUsed = 0;
        }
        if (layoutParams.isOverflowButton || n2 == 0) {
            expandable = false;
        }
        layoutParams.expandable = expandable;
        layoutParams.cellsUsed = cellsUsed;
        view.measure(View$MeasureSpec.makeMeasureSpec(n * cellsUsed, 1073741824), measureSpec);
        return cellsUsed;
    }
    
    private void onMeasureExactFormat(int i, int max) {
        final int mode = View$MeasureSpec.getMode(max);
        final int size = View$MeasureSpec.getSize(i);
        final int size2 = View$MeasureSpec.getSize(max);
        i = this.getPaddingLeft();
        final int paddingRight = this.getPaddingRight();
        final int n = this.getPaddingTop() + this.getPaddingBottom();
        final int childMeasureSpec = ViewGroup.getChildMeasureSpec(max, n, -2);
        final int n2 = size - (i + paddingRight);
        i = this.mMinCellSize;
        final int n3 = n2 / i;
        if (n3 == 0) {
            this.setMeasuredDimension(n2, 0);
            return;
        }
        final int n4 = i + n2 % i / n3;
        final int childCount = this.getChildCount();
        final int n5 = i = 0;
        int n6 = max = i;
        int n8;
        final int n7 = n8 = max;
        long j = 0L;
        int max2 = n7;
        int n9 = max;
        int k = i;
        max = n5;
        i = n3;
        final int n10 = size2;
        while (k < childCount) {
            final View child = this.getChildAt(k);
            int n11;
            if (child.getVisibility() == 8) {
                n11 = n8;
            }
            else {
                final boolean b = child instanceof ActionMenuItemView;
                ++n9;
                if (b) {
                    final int mGeneratedItemPadding = this.mGeneratedItemPadding;
                    child.setPadding(mGeneratedItemPadding, 0, mGeneratedItemPadding, 0);
                }
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                layoutParams.expanded = false;
                layoutParams.extraPixels = 0;
                layoutParams.cellsUsed = 0;
                layoutParams.expandable = false;
                layoutParams.leftMargin = 0;
                layoutParams.rightMargin = 0;
                layoutParams.preventEdgeOffset = (b && ((ActionMenuItemView)child).hasText());
                int n12;
                if (layoutParams.isOverflowButton) {
                    n12 = 1;
                }
                else {
                    n12 = i;
                }
                final int measureChildForCells = measureChildForCells(child, n4, n12, childMeasureSpec, n);
                max2 = Math.max(max2, measureChildForCells);
                n11 = n8;
                if (layoutParams.expandable) {
                    n11 = n8 + 1;
                }
                if (layoutParams.isOverflowButton) {
                    n6 = 1;
                }
                i -= measureChildForCells;
                max = Math.max(max, child.getMeasuredHeight());
                if (measureChildForCells == 1) {
                    j |= 1 << k;
                }
            }
            ++k;
            n8 = n11;
        }
        final boolean b2 = n6 != 0 && n9 == 2;
        int n13 = 0;
        int n14 = i;
        final boolean b3 = b2;
        final int n15 = n2;
        while (true) {
            while (n8 > 0 && n14 > 0) {
                int n16 = Integer.MAX_VALUE;
                int n17 = 0;
                int l = 0;
                long n18 = 0L;
                while (l < childCount) {
                    final LayoutParams layoutParams2 = (LayoutParams)this.getChildAt(l).getLayoutParams();
                    int n19;
                    long n20;
                    if (!layoutParams2.expandable) {
                        i = n17;
                        n19 = n16;
                        n20 = n18;
                    }
                    else {
                        final int cellsUsed = layoutParams2.cellsUsed;
                        if (cellsUsed < n16) {
                            n20 = 1L << l;
                            n19 = cellsUsed;
                            i = 1;
                        }
                        else {
                            i = n17;
                            n19 = n16;
                            n20 = n18;
                            if (cellsUsed == n16) {
                                i = n17 + 1;
                                n20 = (n18 | 1L << l);
                                n19 = n16;
                            }
                        }
                    }
                    ++l;
                    n17 = i;
                    n16 = n19;
                    n18 = n20;
                }
                i = n13;
                j |= n18;
                if (n17 > n14) {
                    final boolean b4 = n6 == 0 && n9 == 1;
                    int n26;
                    if (n14 > 0 && j != 0L && (n14 < n9 - 1 || b4 || max2 > 1)) {
                        float n21 = (float)Long.bitCount(j);
                        if (!b4) {
                            float n22 = n21;
                            if ((j & 0x1L) != 0x0L) {
                                n22 = n21;
                                if (!((LayoutParams)this.getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                                    n22 = n21 - 0.5f;
                                }
                            }
                            final int n23 = childCount - 1;
                            n21 = n22;
                            if ((j & (long)(1 << n23)) != 0x0L) {
                                n21 = n22;
                                if (!((LayoutParams)this.getChildAt(n23).getLayoutParams()).preventEdgeOffset) {
                                    n21 = n22 - 0.5f;
                                }
                            }
                        }
                        int n24;
                        if (n21 > 0.0f) {
                            n24 = (int)(n14 * n4 / n21);
                        }
                        else {
                            n24 = 0;
                        }
                        int n25 = 0;
                        while (true) {
                            n26 = i;
                            if (n25 >= childCount) {
                                break;
                            }
                            int n27;
                            if ((j & (long)(1 << n25)) == 0x0L) {
                                n27 = i;
                            }
                            else {
                                final View child2 = this.getChildAt(n25);
                                final LayoutParams layoutParams3 = (LayoutParams)child2.getLayoutParams();
                                if (child2 instanceof ActionMenuItemView) {
                                    layoutParams3.extraPixels = n24;
                                    layoutParams3.expanded = true;
                                    if (n25 == 0 && !layoutParams3.preventEdgeOffset) {
                                        layoutParams3.leftMargin = -n24 / 2;
                                    }
                                    n27 = 1;
                                }
                                else if (layoutParams3.isOverflowButton) {
                                    layoutParams3.extraPixels = n24;
                                    layoutParams3.expanded = true;
                                    layoutParams3.rightMargin = -n24 / 2;
                                    n27 = 1;
                                }
                                else {
                                    if (n25 != 0) {
                                        layoutParams3.leftMargin = n24 / 2;
                                    }
                                    n27 = i;
                                    if (n25 != childCount - 1) {
                                        layoutParams3.rightMargin = n24 / 2;
                                        n27 = i;
                                    }
                                }
                            }
                            ++n25;
                            i = n27;
                        }
                    }
                    else {
                        n26 = i;
                    }
                    if (n26 != 0) {
                        View child3;
                        LayoutParams layoutParams4;
                        for (i = 0; i < childCount; ++i) {
                            child3 = this.getChildAt(i);
                            layoutParams4 = (LayoutParams)child3.getLayoutParams();
                            if (layoutParams4.expanded) {
                                child3.measure(View$MeasureSpec.makeMeasureSpec(layoutParams4.cellsUsed * n4 + layoutParams4.extraPixels, 1073741824), childMeasureSpec);
                            }
                        }
                    }
                    if (mode != 1073741824) {
                        i = max;
                    }
                    else {
                        i = n10;
                    }
                    this.setMeasuredDimension(n15, i);
                    return;
                }
                View child4;
                LayoutParams layoutParams5;
                long n28;
                long n29;
                int mGeneratedItemPadding2;
                for (i = 0; i < childCount; ++i) {
                    child4 = this.getChildAt(i);
                    layoutParams5 = (LayoutParams)child4.getLayoutParams();
                    n28 = 1 << i;
                    if ((n18 & n28) == 0x0L) {
                        n29 = j;
                        if (layoutParams5.cellsUsed == n16 + 1) {
                            n29 = (j | n28);
                        }
                        j = n29;
                    }
                    else {
                        if (b3 && layoutParams5.preventEdgeOffset && n14 == 1) {
                            mGeneratedItemPadding2 = this.mGeneratedItemPadding;
                            child4.setPadding(mGeneratedItemPadding2 + n4, 0, mGeneratedItemPadding2, 0);
                        }
                        ++layoutParams5.cellsUsed;
                        layoutParams5.expanded = true;
                        --n14;
                    }
                }
                n13 = 1;
            }
            i = n13;
            continue;
        }
    }
    
    @Override
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams;
    }
    
    public void dismissPopupMenus() {
        final ActionMenuPresenter mPresenter = this.mPresenter;
        if (mPresenter != null) {
            mPresenter.dismissPopupMenus();
        }
    }
    
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        return false;
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        final LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.gravity = 16;
        return layoutParams;
    }
    
    public LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    protected LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (viewGroup$LayoutParams != null) {
            LayoutParams layoutParams;
            if (viewGroup$LayoutParams instanceof LayoutParams) {
                layoutParams = new LayoutParams((LayoutParams)viewGroup$LayoutParams);
            }
            else {
                layoutParams = new LayoutParams(viewGroup$LayoutParams);
            }
            if (layoutParams.gravity <= 0) {
                layoutParams.gravity = 16;
            }
            return layoutParams;
        }
        return this.generateDefaultLayoutParams();
    }
    
    public LayoutParams generateOverflowButtonLayoutParams() {
        final LayoutParams generateDefaultLayoutParams = this.generateDefaultLayoutParams();
        generateDefaultLayoutParams.isOverflowButton = true;
        return generateDefaultLayoutParams;
    }
    
    public Menu getMenu() {
        if (this.mMenu == null) {
            final Context context = this.getContext();
            (this.mMenu = new MenuBuilder(context)).setCallback((MenuBuilder.Callback)new MenuBuilderCallback());
            (this.mPresenter = new ActionMenuPresenter(context)).setReserveOverflow(true);
            final ActionMenuPresenter mPresenter = this.mPresenter;
            MenuPresenter.Callback mActionMenuPresenterCallback = this.mActionMenuPresenterCallback;
            if (mActionMenuPresenterCallback == null) {
                mActionMenuPresenterCallback = new ActionMenuPresenterCallback();
            }
            mPresenter.setCallback(mActionMenuPresenterCallback);
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return (Menu)this.mMenu;
    }
    
    protected boolean hasSupportDividerBeforeChildAt(final int n) {
        final boolean b = false;
        if (n == 0) {
            return false;
        }
        final View child = this.getChildAt(n - 1);
        final View child2 = this.getChildAt(n);
        boolean b2 = b;
        if (n < this.getChildCount()) {
            b2 = b;
            if (child instanceof ActionMenuChildView) {
                b2 = (false | ((ActionMenuChildView)child).needsDividerAfter());
            }
        }
        boolean b3 = b2;
        if (n > 0) {
            b3 = b2;
            if (child2 instanceof ActionMenuChildView) {
                b3 = (b2 | ((ActionMenuChildView)child2).needsDividerBefore());
            }
        }
        return b3;
    }
    
    public boolean hideOverflowMenu() {
        final ActionMenuPresenter mPresenter = this.mPresenter;
        return mPresenter != null && mPresenter.hideOverflowMenu();
    }
    
    @Override
    public void initialize(final MenuBuilder mMenu) {
        this.mMenu = mMenu;
    }
    
    @Override
    public boolean invokeItem(final MenuItemImpl menuItemImpl) {
        return this.mMenu.performItemAction((MenuItem)menuItemImpl, 0);
    }
    
    public boolean isOverflowMenuShowPending() {
        final ActionMenuPresenter mPresenter = this.mPresenter;
        return mPresenter != null && mPresenter.isOverflowMenuShowPending();
    }
    
    public boolean isOverflowMenuShowing() {
        final ActionMenuPresenter mPresenter = this.mPresenter;
        return mPresenter != null && mPresenter.isOverflowMenuShowing();
    }
    
    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final ActionMenuPresenter mPresenter = this.mPresenter;
        if (mPresenter != null) {
            mPresenter.updateMenuView(false);
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.dismissPopupMenus();
    }
    
    @Override
    protected void onLayout(final boolean b, int i, int n, int j, int n2) {
        if (!this.mFormatItems) {
            super.onLayout(b, i, n, j, n2);
            return;
        }
        final int childCount = this.getChildCount();
        final int n3 = (n2 - n) / 2;
        final int dividerWidth = this.getDividerWidth();
        final int n4 = j - i;
        i = n4 - this.getPaddingRight() - this.getPaddingLeft();
        final boolean layoutRtl = ViewUtils.isLayoutRtl((View)this);
        j = 0;
        n2 = 0;
        n = 0;
        while (j < childCount) {
            final View child = this.getChildAt(j);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (layoutParams.isOverflowButton) {
                    final int n5 = n2 = child.getMeasuredWidth();
                    if (this.hasSupportDividerBeforeChildAt(j)) {
                        n2 = n5 + dividerWidth;
                    }
                    final int measuredHeight = child.getMeasuredHeight();
                    int n6;
                    int n7;
                    if (layoutRtl) {
                        n6 = this.getPaddingLeft() + layoutParams.leftMargin;
                        n7 = n6 + n2;
                    }
                    else {
                        n7 = this.getWidth() - this.getPaddingRight() - layoutParams.rightMargin;
                        n6 = n7 - n2;
                    }
                    final int n8 = n3 - measuredHeight / 2;
                    child.layout(n6, n8, n7, measuredHeight + n8);
                    i -= n2;
                    n2 = 1;
                }
                else {
                    i -= child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                    this.hasSupportDividerBeforeChildAt(j);
                    ++n;
                }
            }
            ++j;
        }
        if (childCount == 1 && n2 == 0) {
            final View child2 = this.getChildAt(0);
            n = child2.getMeasuredWidth();
            i = child2.getMeasuredHeight();
            j = n4 / 2 - n / 2;
            n2 = n3 - i / 2;
            child2.layout(j, n2, n + j, i + n2);
            return;
        }
        n -= (n2 ^ 0x1);
        if (n > 0) {
            i /= n;
        }
        else {
            i = 0;
        }
        n2 = Math.max(0, i);
        if (layoutRtl) {
            j = this.getWidth() - this.getPaddingRight();
            View child3;
            LayoutParams layoutParams2;
            int n9;
            int n10;
            for (i = 0; i < childCount; ++i, j = n) {
                child3 = this.getChildAt(i);
                layoutParams2 = (LayoutParams)child3.getLayoutParams();
                n = j;
                if (child3.getVisibility() != 8) {
                    if (layoutParams2.isOverflowButton) {
                        n = j;
                    }
                    else {
                        n9 = j - layoutParams2.rightMargin;
                        n = child3.getMeasuredWidth();
                        j = child3.getMeasuredHeight();
                        n10 = n3 - j / 2;
                        child3.layout(n9 - n, n10, n9, j + n10);
                        n = n9 - (n + layoutParams2.leftMargin + n2);
                    }
                }
            }
        }
        else {
            n = this.getPaddingLeft();
            View child4;
            LayoutParams layoutParams3;
            int measuredHeight2;
            int n11;
            for (i = 0; i < childCount; ++i, n = j) {
                child4 = this.getChildAt(i);
                layoutParams3 = (LayoutParams)child4.getLayoutParams();
                j = n;
                if (child4.getVisibility() != 8) {
                    if (layoutParams3.isOverflowButton) {
                        j = n;
                    }
                    else {
                        j = n + layoutParams3.leftMargin;
                        n = child4.getMeasuredWidth();
                        measuredHeight2 = child4.getMeasuredHeight();
                        n11 = n3 - measuredHeight2 / 2;
                        child4.layout(j, n11, j + n, measuredHeight2 + n11);
                        j += n + layoutParams3.rightMargin + n2;
                    }
                }
            }
        }
    }
    
    @Override
    protected void onMeasure(final int n, final int n2) {
        final boolean mFormatItems = this.mFormatItems;
        int mFormatItems2;
        if (View$MeasureSpec.getMode(n) == 1073741824) {
            mFormatItems2 = 1;
        }
        else {
            mFormatItems2 = 0;
        }
        this.mFormatItems = (mFormatItems2 != 0);
        if ((mFormatItems ? 1 : 0) != mFormatItems2) {
            this.mFormatItemsWidth = 0;
        }
        final int size = View$MeasureSpec.getSize(n);
        if (this.mFormatItems) {
            final MenuBuilder mMenu = this.mMenu;
            if (mMenu != null && size != this.mFormatItemsWidth) {
                this.mFormatItemsWidth = size;
                mMenu.onItemsChanged(true);
            }
        }
        final int childCount = this.getChildCount();
        if (this.mFormatItems && childCount > 0) {
            this.onMeasureExactFormat(n, n2);
        }
        else {
            for (int i = 0; i < childCount; ++i) {
                final LayoutParams layoutParams = (LayoutParams)this.getChildAt(i).getLayoutParams();
                layoutParams.rightMargin = 0;
                layoutParams.leftMargin = 0;
            }
            super.onMeasure(n, n2);
        }
    }
    
    public MenuBuilder peekMenu() {
        return this.mMenu;
    }
    
    public void setExpandedActionViewsExclusive(final boolean expandedActionViewsExclusive) {
        this.mPresenter.setExpandedActionViewsExclusive(expandedActionViewsExclusive);
    }
    
    public void setMenuCallbacks(final MenuPresenter.Callback mActionMenuPresenterCallback, final Callback mMenuBuilderCallback) {
        this.mActionMenuPresenterCallback = mActionMenuPresenterCallback;
        this.mMenuBuilderCallback = mMenuBuilderCallback;
    }
    
    public void setOnMenuItemClickListener(final OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }
    
    public void setOverflowReserved(final boolean mReserveOverflow) {
        this.mReserveOverflow = mReserveOverflow;
    }
    
    public void setPopupTheme(final int mPopupTheme) {
        if (this.mPopupTheme != mPopupTheme) {
            if ((this.mPopupTheme = mPopupTheme) == 0) {
                this.mPopupContext = this.getContext();
            }
            else {
                this.mPopupContext = (Context)new ContextThemeWrapper(this.getContext(), mPopupTheme);
            }
        }
    }
    
    public void setPresenter(final ActionMenuPresenter mPresenter) {
        (this.mPresenter = mPresenter).setMenuView(this);
    }
    
    public boolean showOverflowMenu() {
        final ActionMenuPresenter mPresenter = this.mPresenter;
        return mPresenter != null && mPresenter.showOverflowMenu();
    }
    
    public interface ActionMenuChildView
    {
        boolean needsDividerAfter();
        
        boolean needsDividerBefore();
    }
    
    private static class ActionMenuPresenterCallback implements MenuPresenter.Callback
    {
        ActionMenuPresenterCallback() {
        }
        
        @Override
        public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        }
        
        @Override
        public boolean onOpenSubMenu(final MenuBuilder menuBuilder) {
            return false;
        }
    }
    
    public static class LayoutParams extends LinearLayoutCompat.LayoutParams
    {
        @ViewDebug$ExportedProperty
        public int cellsUsed;
        @ViewDebug$ExportedProperty
        public boolean expandable;
        boolean expanded;
        @ViewDebug$ExportedProperty
        public int extraPixels;
        @ViewDebug$ExportedProperty
        public boolean isOverflowButton;
        @ViewDebug$ExportedProperty
        public boolean preventEdgeOffset;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.isOverflowButton = false;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$LayoutParams)layoutParams);
            this.isOverflowButton = layoutParams.isOverflowButton;
        }
    }
    
    private class MenuBuilderCallback implements Callback
    {
        MenuBuilderCallback() {
        }
        
        @Override
        public boolean onMenuItemSelected(final MenuBuilder menuBuilder, final MenuItem menuItem) {
            final OnMenuItemClickListener mOnMenuItemClickListener = ActionMenuView.this.mOnMenuItemClickListener;
            return mOnMenuItemClickListener != null && mOnMenuItemClickListener.onMenuItemClick(menuItem);
        }
        
        @Override
        public void onMenuModeChange(final MenuBuilder menuBuilder) {
            final Callback mMenuBuilderCallback = ActionMenuView.this.mMenuBuilderCallback;
            if (mMenuBuilderCallback != null) {
                mMenuBuilderCallback.onMenuModeChange(menuBuilder);
            }
        }
    }
    
    public interface OnMenuItemClickListener
    {
        boolean onMenuItemClick(final MenuItem p0);
    }
}
