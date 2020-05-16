// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;

public class BarView extends LinearLayout
{
    private TextView mBarSummary;
    private TextView mBarTitle;
    private View mBarView;
    private ImageView mIcon;
    
    public BarView(final Context context) {
        super(context);
        this.init();
    }
    
    public BarView(final Context context, final AttributeSet set) {
        super(context, set);
        this.init();
        final int color = context.obtainStyledAttributes(new int[] { 16843829 }).getColor(0, 0);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.SettingsBarView);
        final int color2 = obtainStyledAttributes.getColor(R$styleable.SettingsBarView_barColor, color);
        obtainStyledAttributes.recycle();
        this.mBarView.setBackgroundColor(color2);
    }
    
    private void init() {
        LayoutInflater.from(this.getContext()).inflate(R$layout.settings_bar_view, (ViewGroup)this);
        this.setOrientation(1);
        this.setGravity(81);
        this.mBarView = this.findViewById(R$id.bar_view);
        this.mIcon = (ImageView)this.findViewById(R$id.icon_view);
        this.mBarTitle = (TextView)this.findViewById(R$id.bar_title);
        this.mBarSummary = (TextView)this.findViewById(R$id.bar_summary);
    }
    
    CharSequence getSummary() {
        return this.mBarSummary.getText();
    }
    
    CharSequence getTitle() {
        return this.mBarTitle.getText();
    }
    
    void updateView(final BarViewInfo barViewInfo) {
        this.setOnClickListener(barViewInfo.getClickListener());
        this.mBarView.getLayoutParams().height = barViewInfo.getNormalizedHeight();
        this.mIcon.setImageDrawable(barViewInfo.getIcon());
        this.mBarTitle.setText(barViewInfo.getTitle());
        this.mBarSummary.setText(barViewInfo.getSummary());
        final CharSequence contentDescription = barViewInfo.getContentDescription();
        if (!TextUtils.isEmpty(contentDescription) && !TextUtils.equals(barViewInfo.getTitle(), contentDescription)) {
            this.mIcon.setContentDescription(barViewInfo.getContentDescription());
        }
    }
}
