// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.ViewGroup;
import android.view.View;
import android.graphics.Rect;

class ItemAlignmentFacetHelper
{
    private static Rect sRect;
    
    static {
        ItemAlignmentFacetHelper.sRect = new Rect();
    }
    
    static int getAlignmentPosition(final View view, final ItemAlignmentFacet.ItemAlignmentDef itemAlignmentDef, int n) {
        final GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams)view.getLayoutParams();
        final int mViewId = itemAlignmentDef.mViewId;
        View viewById;
        if (mViewId == 0 || (viewById = view.findViewById(mViewId)) == null) {
            viewById = view;
        }
        final int mOffset = itemAlignmentDef.mOffset;
        if (n == 0) {
            if (view.getLayoutDirection() == 1) {
                if (viewById == view) {
                    n = layoutParams.getOpticalWidth(viewById);
                }
                else {
                    n = viewById.getWidth();
                }
                final int n2 = n -= mOffset;
                if (itemAlignmentDef.mOffsetWithPadding) {
                    final float mOffsetPercent = itemAlignmentDef.mOffsetPercent;
                    if (mOffsetPercent == 0.0f) {
                        n = n2 - viewById.getPaddingRight();
                    }
                    else {
                        n = n2;
                        if (mOffsetPercent == 100.0f) {
                            n = n2 + viewById.getPaddingLeft();
                        }
                    }
                }
                int right = n;
                if (itemAlignmentDef.mOffsetPercent != -1.0f) {
                    int n3;
                    if (viewById == view) {
                        n3 = layoutParams.getOpticalWidth(viewById);
                    }
                    else {
                        n3 = viewById.getWidth();
                    }
                    right = n - (int)(n3 * itemAlignmentDef.mOffsetPercent / 100.0f);
                }
                n = right;
                if (view != viewById) {
                    final Rect sRect = ItemAlignmentFacetHelper.sRect;
                    sRect.right = right;
                    ((ViewGroup)view).offsetDescendantRectToMyCoords(viewById, sRect);
                    n = ItemAlignmentFacetHelper.sRect.right + layoutParams.getOpticalRightInset();
                }
            }
            else {
                n = mOffset;
                if (itemAlignmentDef.mOffsetWithPadding) {
                    final float mOffsetPercent2 = itemAlignmentDef.mOffsetPercent;
                    if (mOffsetPercent2 == 0.0f) {
                        n = mOffset + viewById.getPaddingLeft();
                    }
                    else {
                        n = mOffset;
                        if (mOffsetPercent2 == 100.0f) {
                            n = mOffset - viewById.getPaddingRight();
                        }
                    }
                }
                int left = n;
                if (itemAlignmentDef.mOffsetPercent != -1.0f) {
                    int n4;
                    if (viewById == view) {
                        n4 = layoutParams.getOpticalWidth(viewById);
                    }
                    else {
                        n4 = viewById.getWidth();
                    }
                    left = n + (int)(n4 * itemAlignmentDef.mOffsetPercent / 100.0f);
                }
                n = left;
                if (view != viewById) {
                    final Rect sRect2 = ItemAlignmentFacetHelper.sRect;
                    sRect2.left = left;
                    ((ViewGroup)view).offsetDescendantRectToMyCoords(viewById, sRect2);
                    n = ItemAlignmentFacetHelper.sRect.left - layoutParams.getOpticalLeftInset();
                }
            }
        }
        else {
            n = mOffset;
            if (itemAlignmentDef.mOffsetWithPadding) {
                final float mOffsetPercent3 = itemAlignmentDef.mOffsetPercent;
                if (mOffsetPercent3 == 0.0f) {
                    n = mOffset + viewById.getPaddingTop();
                }
                else {
                    n = mOffset;
                    if (mOffsetPercent3 == 100.0f) {
                        n = mOffset - viewById.getPaddingBottom();
                    }
                }
            }
            int top = n;
            if (itemAlignmentDef.mOffsetPercent != -1.0f) {
                int n5;
                if (viewById == view) {
                    n5 = layoutParams.getOpticalHeight(viewById);
                }
                else {
                    n5 = viewById.getHeight();
                }
                top = n + (int)(n5 * itemAlignmentDef.mOffsetPercent / 100.0f);
            }
            if (view != viewById) {
                final Rect sRect3 = ItemAlignmentFacetHelper.sRect;
                sRect3.top = top;
                ((ViewGroup)view).offsetDescendantRectToMyCoords(viewById, sRect3);
                top = ItemAlignmentFacetHelper.sRect.top - layoutParams.getOpticalTopInset();
            }
            n = top;
            if (itemAlignmentDef.isAlignedToTextViewBaseLine()) {
                n = top + viewById.getBaseline();
            }
        }
        return n;
    }
}
