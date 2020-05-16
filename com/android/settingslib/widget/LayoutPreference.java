// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import android.widget.FrameLayout;
import androidx.preference.PreferenceViewHolder;
import android.content.res.TypedArray;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.view.View$OnClickListener;
import androidx.preference.Preference;

public class LayoutPreference extends Preference
{
    private boolean mAllowDividerAbove;
    private boolean mAllowDividerBelow;
    private final View$OnClickListener mClickListener;
    private View mRootView;
    
    public LayoutPreference(final Context context, final AttributeSet set) {
        super(context, set);
        this.mClickListener = (View$OnClickListener)new _$$Lambda$LayoutPreference$A_OWgARxS1B51rTsCCoDBOGYAP0(this);
        this.init(context, set, 0);
    }
    
    private void init(final Context context, final AttributeSet set, int resourceId) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.Preference);
        final int preference_allowDividerAbove = R$styleable.Preference_allowDividerAbove;
        this.mAllowDividerAbove = TypedArrayUtils.getBoolean(obtainStyledAttributes, preference_allowDividerAbove, preference_allowDividerAbove, false);
        final int preference_allowDividerBelow = R$styleable.Preference_allowDividerBelow;
        this.mAllowDividerBelow = TypedArrayUtils.getBoolean(obtainStyledAttributes, preference_allowDividerBelow, preference_allowDividerBelow, false);
        obtainStyledAttributes.recycle();
        final TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(set, R$styleable.Preference, resourceId, 0);
        resourceId = obtainStyledAttributes2.getResourceId(R$styleable.Preference_android_layout, 0);
        if (resourceId != 0) {
            obtainStyledAttributes2.recycle();
            this.setView(LayoutInflater.from(this.getContext()).inflate(resourceId, (ViewGroup)null, false));
            return;
        }
        throw new IllegalArgumentException("LayoutPreference requires a layout to be defined");
    }
    
    private void setView(final View mRootView) {
        this.setLayoutResource(R$layout.layout_preference_frame);
        this.mRootView = mRootView;
        this.setShouldDisableView(false);
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        preferenceViewHolder.itemView.setOnClickListener(this.mClickListener);
        final boolean selectable = this.isSelectable();
        preferenceViewHolder.itemView.setFocusable(selectable);
        preferenceViewHolder.itemView.setClickable(selectable);
        preferenceViewHolder.setDividerAllowedAbove(this.mAllowDividerAbove);
        preferenceViewHolder.setDividerAllowedBelow(this.mAllowDividerBelow);
        final FrameLayout frameLayout = (FrameLayout)preferenceViewHolder.itemView;
        frameLayout.removeAllViews();
        final ViewGroup viewGroup = (ViewGroup)this.mRootView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.mRootView);
        }
        frameLayout.addView(this.mRootView);
    }
}
