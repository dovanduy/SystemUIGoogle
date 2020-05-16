// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import java.util.Iterator;
import java.util.ArrayList;
import android.view.accessibility.AccessibilityManager;
import android.os.Build$VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Rect;
import android.annotation.TargetApi;
import android.view.View$OnLayoutChangeListener;

public class LocationBasedViewTracker implements Runnable, View$OnLayoutChangeListener
{
    @TargetApi(21)
    private static final SelectionLogic A11Y_FOCUS;
    private static final SelectionLogic INPUT_FOCUS;
    private final Rect mFocusRect;
    private final ViewGroup mParent;
    private final SelectionLogic mSelectionLogic;
    
    static {
        INPUT_FOCUS = (SelectionLogic)new SelectionLogic() {
            @Override
            public void selectView(final View view) {
                view.requestFocus();
            }
        };
        A11Y_FOCUS = (SelectionLogic)new SelectionLogic() {
            @Override
            public void selectView(final View view) {
                view.performAccessibilityAction(64, (Bundle)null);
            }
        };
    }
    
    private LocationBasedViewTracker(final ViewGroup mParent, final View view, final SelectionLogic mSelectionLogic) {
        final Rect mFocusRect = new Rect();
        this.mFocusRect = mFocusRect;
        this.mParent = mParent;
        this.mSelectionLogic = mSelectionLogic;
        view.getDrawingRect(mFocusRect);
        mParent.offsetDescendantRectToMyCoords(view, this.mFocusRect);
        this.mParent.addOnLayoutChangeListener((View$OnLayoutChangeListener)this);
        this.mParent.requestLayout();
    }
    
    public static void trackA11yFocus(final ViewGroup viewGroup) {
        if (Build$VERSION.SDK_INT < 21) {
            return;
        }
        if (!((AccessibilityManager)viewGroup.getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            return;
        }
        final ArrayList<View> list = new ArrayList<View>();
        viewGroup.addFocusables((ArrayList)list, 2, 0);
        final View view = null;
        final Iterator<View> iterator = list.iterator();
        View view2;
        do {
            view2 = view;
            if (!iterator.hasNext()) {
                break;
            }
            view2 = iterator.next();
        } while (!view2.isAccessibilityFocused());
        if (view2 != null) {
            new LocationBasedViewTracker(viewGroup, view2, LocationBasedViewTracker.A11Y_FOCUS);
        }
    }
    
    public static void trackInputFocused(final ViewGroup viewGroup) {
        final View focus = viewGroup.findFocus();
        if (focus != null) {
            new LocationBasedViewTracker(viewGroup, focus, LocationBasedViewTracker.INPUT_FOCUS);
        }
    }
    
    public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        this.mParent.removeOnLayoutChangeListener((View$OnLayoutChangeListener)this);
        this.mParent.post((Runnable)this);
    }
    
    @Override
    public void run() {
        final ArrayList<View> list = new ArrayList<View>();
        this.mParent.addFocusables((ArrayList)list, 2, 0);
        final Rect rect = new Rect();
        final Iterator<View> iterator = list.iterator();
        int n = Integer.MAX_VALUE;
        View view = null;
        while (iterator.hasNext()) {
            final View view2 = iterator.next();
            view2.getDrawingRect(rect);
            this.mParent.offsetDescendantRectToMyCoords(view2, rect);
            if (!this.mFocusRect.intersect(rect)) {
                continue;
            }
            final int n2 = Math.abs(this.mFocusRect.left - rect.left) + Math.abs(this.mFocusRect.right - rect.right) + Math.abs(this.mFocusRect.top - rect.top) + Math.abs(this.mFocusRect.bottom - rect.bottom);
            if (n <= n2) {
                continue;
            }
            view = view2;
            n = n2;
        }
        if (view != null) {
            this.mSelectionLogic.selectView(view);
        }
    }
    
    private interface SelectionLogic
    {
        void selectView(final View p0);
    }
}
