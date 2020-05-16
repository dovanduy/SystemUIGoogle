// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager2.widget;

import androidx.recyclerview.widget.RecyclerView;
import android.animation.LayoutTransition;
import android.view.ViewGroup;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import java.util.Arrays;
import java.util.Comparator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.ViewGroup$MarginLayoutParams;

final class AnimateLayoutChangeDetector
{
    private static final ViewGroup$MarginLayoutParams ZERO_MARGIN_LAYOUT_PARAMS;
    private LinearLayoutManager mLayoutManager;
    
    static {
        (ZERO_MARGIN_LAYOUT_PARAMS = new ViewGroup$MarginLayoutParams(-1, -1)).setMargins(0, 0, 0, 0);
    }
    
    AnimateLayoutChangeDetector(final LinearLayoutManager mLayoutManager) {
        this.mLayoutManager = mLayoutManager;
    }
    
    private boolean arePagesLaidOutContiguously() {
        final int childCount = ((RecyclerView.LayoutManager)this.mLayoutManager).getChildCount();
        if (childCount == 0) {
            return true;
        }
        final boolean b = this.mLayoutManager.getOrientation() == 0;
        final int[][] a = new int[childCount][2];
        for (int i = 0; i < childCount; ++i) {
            final View child = ((RecyclerView.LayoutManager)this.mLayoutManager).getChildAt(i);
            if (child == null) {
                throw new IllegalStateException("null view contained in the view hierarchy");
            }
            final ViewGroup$LayoutParams layoutParams = child.getLayoutParams();
            ViewGroup$MarginLayoutParams zero_MARGIN_LAYOUT_PARAMS;
            if (layoutParams instanceof ViewGroup$MarginLayoutParams) {
                zero_MARGIN_LAYOUT_PARAMS = (ViewGroup$MarginLayoutParams)layoutParams;
            }
            else {
                zero_MARGIN_LAYOUT_PARAMS = AnimateLayoutChangeDetector.ZERO_MARGIN_LAYOUT_PARAMS;
            }
            final int[] array = a[i];
            int n;
            int n2;
            if (b) {
                n = child.getLeft();
                n2 = zero_MARGIN_LAYOUT_PARAMS.leftMargin;
            }
            else {
                n = child.getTop();
                n2 = zero_MARGIN_LAYOUT_PARAMS.topMargin;
            }
            array[0] = n - n2;
            final int[] array2 = a[i];
            int n3;
            int n4;
            if (b) {
                n3 = child.getRight();
                n4 = zero_MARGIN_LAYOUT_PARAMS.rightMargin;
            }
            else {
                n3 = child.getBottom();
                n4 = zero_MARGIN_LAYOUT_PARAMS.bottomMargin;
            }
            array2[1] = n3 + n4;
        }
        Arrays.sort(a, new Comparator<int[]>(this) {
            @Override
            public int compare(final int[] array, final int[] array2) {
                return array[0] - array2[0];
            }
        });
        for (int j = 1; j < childCount; ++j) {
            if (a[j - 1][1] != a[j][0]) {
                return false;
            }
        }
        final int n5 = a[0][1];
        final int n6 = a[0][0];
        return a[0][0] <= 0 && a[childCount - 1][1] >= n5 - n6;
    }
    
    private boolean hasRunningChangingLayoutTransition() {
        for (int childCount = ((RecyclerView.LayoutManager)this.mLayoutManager).getChildCount(), i = 0; i < childCount; ++i) {
            if (hasRunningChangingLayoutTransition(((RecyclerView.LayoutManager)this.mLayoutManager).getChildAt(i))) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean hasRunningChangingLayoutTransition(final View view) {
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup)view;
            final LayoutTransition layoutTransition = viewGroup.getLayoutTransition();
            if (layoutTransition != null && layoutTransition.isChangingLayout()) {
                return true;
            }
            for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
                if (hasRunningChangingLayoutTransition(viewGroup.getChildAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    boolean mayHaveInterferingAnimations() {
        final boolean pagesLaidOutContiguously = this.arePagesLaidOutContiguously();
        boolean b = true;
        if ((pagesLaidOutContiguously && ((RecyclerView.LayoutManager)this.mLayoutManager).getChildCount() > 1) || !this.hasRunningChangingLayoutTransition()) {
            b = false;
        }
        return b;
    }
}
