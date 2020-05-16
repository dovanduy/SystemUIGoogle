// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import android.os.Build$VERSION;
import android.animation.LayoutTransition;
import android.view.WindowInsets;
import android.graphics.Canvas;
import android.view.ViewGroup$LayoutParams;
import android.content.res.TypedArray;
import android.view.ViewGroup;
import android.os.Bundle;
import androidx.fragment.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import java.util.ArrayList;
import android.widget.FrameLayout;

public final class FragmentContainerView extends FrameLayout
{
    private ArrayList<View> mDisappearingFragmentChildren;
    private boolean mDrawDisappearingViewsFirst;
    private ArrayList<View> mTransitioningFragmentViews;
    
    public FragmentContainerView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public FragmentContainerView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mDrawDisappearingViewsFirst = true;
        if (this.isInEditMode()) {
            return;
        }
        throw new UnsupportedOperationException("FragmentContainerView must be within a FragmentActivity to be instantiated from XML.");
    }
    
    FragmentContainerView(final Context context, final AttributeSet set, final FragmentManager fragmentManager) {
        super(context, set);
        this.mDrawDisappearingViewsFirst = true;
        final String classAttribute = set.getClassAttribute();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.FragmentContainerView);
        String string = classAttribute;
        if (classAttribute == null) {
            string = obtainStyledAttributes.getString(R$styleable.FragmentContainerView_android_name);
        }
        final String string2 = obtainStyledAttributes.getString(R$styleable.FragmentContainerView_android_tag);
        obtainStyledAttributes.recycle();
        final int id = this.getId();
        final Fragment fragmentById = fragmentManager.findFragmentById(id);
        if (string != null && fragmentById == null) {
            if (id <= 0) {
                String string3;
                if (string2 != null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(" with tag ");
                    sb.append(string2);
                    string3 = sb.toString();
                }
                else {
                    string3 = "";
                }
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("FragmentContainerView must have an android:id to add Fragment ");
                sb2.append(string);
                sb2.append(string3);
                throw new IllegalStateException(sb2.toString());
            }
            final Fragment instantiate = fragmentManager.getFragmentFactory().instantiate(context.getClassLoader(), string);
            instantiate.onInflate(context, set, null);
            final FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
            beginTransaction.setReorderingAllowed(true);
            beginTransaction.add((ViewGroup)this, instantiate, string2);
            beginTransaction.commitNowAllowingStateLoss();
        }
    }
    
    private void addDisappearingFragmentView(final View view) {
        if (view.getAnimation() == null) {
            final ArrayList<View> mTransitioningFragmentViews = this.mTransitioningFragmentViews;
            if (mTransitioningFragmentViews == null || !mTransitioningFragmentViews.contains(view)) {
                return;
            }
        }
        if (this.mDisappearingFragmentChildren == null) {
            this.mDisappearingFragmentChildren = new ArrayList<View>();
        }
        this.mDisappearingFragmentChildren.add(view);
    }
    
    public void addView(final View obj, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (FragmentManager.getViewFragment(obj) != null) {
            super.addView(obj, n, viewGroup$LayoutParams);
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Views added to a FragmentContainerView must be associated with a Fragment. View ");
        sb.append(obj);
        sb.append(" is not associated with a Fragment.");
        throw new IllegalStateException(sb.toString());
    }
    
    protected boolean addViewInLayout(final View obj, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams, final boolean b) {
        if (FragmentManager.getViewFragment(obj) != null) {
            return super.addViewInLayout(obj, n, viewGroup$LayoutParams, b);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Views added to a FragmentContainerView must be associated with a Fragment. View ");
        sb.append(obj);
        sb.append(" is not associated with a Fragment.");
        throw new IllegalStateException(sb.toString());
    }
    
    protected void dispatchDraw(final Canvas canvas) {
        if (this.mDrawDisappearingViewsFirst && this.mDisappearingFragmentChildren != null) {
            for (int i = 0; i < this.mDisappearingFragmentChildren.size(); ++i) {
                super.drawChild(canvas, (View)this.mDisappearingFragmentChildren.get(i), this.getDrawingTime());
            }
        }
        super.dispatchDraw(canvas);
    }
    
    protected boolean drawChild(final Canvas canvas, final View o, final long n) {
        if (this.mDrawDisappearingViewsFirst) {
            final ArrayList<View> mDisappearingFragmentChildren = this.mDisappearingFragmentChildren;
            if (mDisappearingFragmentChildren != null && mDisappearingFragmentChildren.size() > 0 && this.mDisappearingFragmentChildren.contains(o)) {
                return false;
            }
        }
        return super.drawChild(canvas, o, n);
    }
    
    public void endViewTransition(final View view) {
        final ArrayList<View> mTransitioningFragmentViews = this.mTransitioningFragmentViews;
        if (mTransitioningFragmentViews != null) {
            mTransitioningFragmentViews.remove(view);
            final ArrayList<View> mDisappearingFragmentChildren = this.mDisappearingFragmentChildren;
            if (mDisappearingFragmentChildren != null && mDisappearingFragmentChildren.remove(view)) {
                this.mDrawDisappearingViewsFirst = true;
            }
        }
        super.endViewTransition(view);
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            this.getChildAt(i).dispatchApplyWindowInsets(new WindowInsets(windowInsets));
        }
        return windowInsets;
    }
    
    public void removeAllViewsInLayout() {
        for (int i = this.getChildCount() - 1; i >= 0; --i) {
            this.addDisappearingFragmentView(this.getChildAt(i));
        }
        super.removeAllViewsInLayout();
    }
    
    protected void removeDetachedView(final View view, final boolean b) {
        if (b) {
            this.addDisappearingFragmentView(view);
        }
        super.removeDetachedView(view, b);
    }
    
    public void removeView(final View view) {
        this.addDisappearingFragmentView(view);
        super.removeView(view);
    }
    
    public void removeViewAt(final int n) {
        this.addDisappearingFragmentView(this.getChildAt(n));
        super.removeViewAt(n);
    }
    
    public void removeViewInLayout(final View view) {
        this.addDisappearingFragmentView(view);
        super.removeViewInLayout(view);
    }
    
    public void removeViews(final int n, final int n2) {
        for (int i = n; i < n + n2; ++i) {
            this.addDisappearingFragmentView(this.getChildAt(i));
        }
        super.removeViews(n, n2);
    }
    
    public void removeViewsInLayout(final int n, final int n2) {
        for (int i = n; i < n + n2; ++i) {
            this.addDisappearingFragmentView(this.getChildAt(i));
        }
        super.removeViewsInLayout(n, n2);
    }
    
    void setDrawDisappearingViewsLast(final boolean mDrawDisappearingViewsFirst) {
        this.mDrawDisappearingViewsFirst = mDrawDisappearingViewsFirst;
    }
    
    public void setLayoutTransition(final LayoutTransition layoutTransition) {
        if (Build$VERSION.SDK_INT < 18) {
            super.setLayoutTransition(layoutTransition);
            return;
        }
        throw new UnsupportedOperationException("FragmentContainerView does not support Layout Transitions or animateLayoutChanges=\"true\".");
    }
    
    public void startViewTransition(final View e) {
        if (e.getParent() == this) {
            if (this.mTransitioningFragmentViews == null) {
                this.mTransitioningFragmentViews = new ArrayList<View>();
            }
            this.mTransitioningFragmentViews.add(e);
        }
        super.startViewTransition(e);
    }
}
