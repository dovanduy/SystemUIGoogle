// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import com.android.systemui.R$id;
import com.android.systemui.util.leak.RotationUtils;
import com.android.internal.annotations.VisibleForTesting;
import android.text.TextUtils;
import java.util.Locale;
import android.view.View;
import com.android.systemui.HardwareBgDrawable;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;
import android.content.res.Resources$Theme;
import com.android.systemui.R$color;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.MultiListLayout;

public abstract class GlobalActionsLayout extends MultiListLayout
{
    boolean mBackgroundsSet;
    
    public GlobalActionsLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private void setBackgrounds() {
        final ViewGroup listView = this.getListView();
        final HardwareBgDrawable backgroundDrawable = this.getBackgroundDrawable(this.getResources().getColor(R$color.global_actions_grid_background, (Resources$Theme)null));
        if (backgroundDrawable != null) {
            listView.setBackground((Drawable)backgroundDrawable);
        }
        if (this.getSeparatedView() != null) {
            final HardwareBgDrawable backgroundDrawable2 = this.getBackgroundDrawable(this.getResources().getColor(R$color.global_actions_separated_background, (Resources$Theme)null));
            if (backgroundDrawable2 != null) {
                this.getSeparatedView().setBackground((Drawable)backgroundDrawable2);
            }
        }
    }
    
    protected void addToListView(final View view, final boolean b) {
        if (b) {
            this.getListView().addView(view, 0);
        }
        else {
            this.getListView().addView(view);
        }
    }
    
    protected void addToSeparatedView(final View view, final boolean b) {
        final ViewGroup separatedView = this.getSeparatedView();
        if (separatedView != null) {
            if (b) {
                separatedView.addView(view, 0);
            }
            else {
                separatedView.addView(view);
            }
        }
        else {
            this.addToListView(view, b);
        }
    }
    
    protected HardwareBgDrawable getBackgroundDrawable(final int tint) {
        final HardwareBgDrawable hardwareBgDrawable = new HardwareBgDrawable(true, true, this.getContext());
        hardwareBgDrawable.setTint(tint);
        return hardwareBgDrawable;
    }
    
    @VisibleForTesting
    protected int getCurrentLayoutDirection() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault());
    }
    
    @VisibleForTesting
    protected int getCurrentRotation() {
        return RotationUtils.getRotation(super.mContext);
    }
    
    @Override
    protected ViewGroup getListView() {
        return (ViewGroup)this.findViewById(16908298);
    }
    
    @Override
    protected ViewGroup getSeparatedView() {
        return (ViewGroup)this.findViewById(R$id.separated_button);
    }
    
    protected View getWrapper() {
        return this.getChildAt(0);
    }
    
    protected void onMeasure(final int n, final int n2) {
        super.onMeasure(n, n2);
        if (this.getListView() != null && !this.mBackgroundsSet) {
            this.setBackgrounds();
            this.mBackgroundsSet = true;
        }
    }
    
    public void onUpdateList() {
        super.onUpdateList();
        final ViewGroup separatedView = this.getSeparatedView();
        final ViewGroup listView = this.getListView();
        for (int i = 0; i < super.mAdapter.getCount(); ++i) {
            final boolean shouldBeSeparated = super.mAdapter.shouldBeSeparated(i);
            View view;
            if (shouldBeSeparated) {
                view = super.mAdapter.getView(i, (View)null, separatedView);
            }
            else {
                view = super.mAdapter.getView(i, (View)null, listView);
            }
            if (shouldBeSeparated) {
                this.addToSeparatedView(view, false);
            }
            else {
                this.addToListView(view, this.shouldReverseListItems());
            }
        }
    }
    
    protected abstract boolean shouldReverseListItems();
}
