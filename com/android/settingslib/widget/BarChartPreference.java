// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import android.view.View;
import java.util.Arrays;
import android.widget.TextView;
import android.widget.Button;
import androidx.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.content.Context;
import androidx.preference.Preference;

public class BarChartPreference extends Preference
{
    private static final int[] BAR_VIEWS;
    private BarChartInfo mBarChartInfo;
    private boolean mIsLoading;
    private int mMaxBarHeight;
    
    static {
        BAR_VIEWS = new int[] { R$id.bar_view1, R$id.bar_view2, R$id.bar_view3, R$id.bar_view4 };
    }
    
    public BarChartPreference(final Context context, final AttributeSet set) {
        super(context, set);
        this.init();
    }
    
    private void bindChartDetailsView(final PreferenceViewHolder preferenceViewHolder) {
        final Button button = (Button)preferenceViewHolder.findViewById(R$id.bar_chart_details);
        final int details = this.mBarChartInfo.getDetails();
        if (details == 0) {
            button.setVisibility(8);
        }
        else {
            button.setVisibility(0);
            button.setText(details);
            button.setOnClickListener(this.mBarChartInfo.getDetailsOnClickListener());
        }
    }
    
    private void bindChartTitleView(final PreferenceViewHolder preferenceViewHolder) {
        ((TextView)preferenceViewHolder.findViewById(R$id.bar_chart_title)).setText(this.mBarChartInfo.getTitle());
    }
    
    private void init() {
        this.setSelectable(false);
        this.setLayoutResource(R$layout.settings_bar_chart);
        this.mMaxBarHeight = this.getContext().getResources().getDimensionPixelSize(R$dimen.settings_bar_view_max_height);
    }
    
    private void normalizeBarViewHeights() {
        final BarViewInfo[] barViewInfos = this.mBarChartInfo.getBarViewInfos();
        if (barViewInfos != null) {
            if (barViewInfos.length != 0) {
                Arrays.sort(barViewInfos);
                int i = 0;
                final int height = barViewInfos[0].getHeight();
                int n;
                if (height == 0) {
                    n = 0;
                }
                else {
                    n = this.mMaxBarHeight / height;
                }
                while (i < barViewInfos.length) {
                    final BarViewInfo barViewInfo = barViewInfos[i];
                    barViewInfo.setNormalizedHeight(barViewInfo.getHeight() * n);
                    ++i;
                }
            }
        }
    }
    
    private void setEmptyViewVisible(final PreferenceViewHolder preferenceViewHolder, final boolean b) {
        final View viewById = preferenceViewHolder.findViewById(R$id.bar_views_container);
        final TextView textView = (TextView)preferenceViewHolder.findViewById(R$id.empty_view);
        final int emptyText = this.mBarChartInfo.getEmptyText();
        if (emptyText != 0) {
            textView.setText(emptyText);
        }
        final int n = 0;
        int visibility;
        if (b) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        textView.setVisibility(visibility);
        int visibility2 = n;
        if (b) {
            visibility2 = 8;
        }
        viewById.setVisibility(visibility2);
    }
    
    private void updateBarChart(final PreferenceViewHolder preferenceViewHolder) {
        this.normalizeBarViewHeights();
        final BarViewInfo[] barViewInfos = this.mBarChartInfo.getBarViewInfos();
        for (int i = 0; i < 4; ++i) {
            final BarView barView = (BarView)preferenceViewHolder.findViewById(BarChartPreference.BAR_VIEWS[i]);
            if (barViewInfos != null && i < barViewInfos.length) {
                barView.setVisibility(0);
                barView.updateView(barViewInfos[i]);
            }
            else {
                barView.setVisibility(8);
            }
        }
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(true);
        preferenceViewHolder.setDividerAllowedBelow(true);
        this.bindChartTitleView(preferenceViewHolder);
        this.bindChartDetailsView(preferenceViewHolder);
        if (this.mIsLoading) {
            preferenceViewHolder.itemView.setVisibility(4);
            return;
        }
        preferenceViewHolder.itemView.setVisibility(0);
        final BarViewInfo[] barViewInfos = this.mBarChartInfo.getBarViewInfos();
        if (barViewInfos != null && barViewInfos.length != 0) {
            this.setEmptyViewVisible(preferenceViewHolder, false);
            this.updateBarChart(preferenceViewHolder);
            return;
        }
        this.setEmptyViewVisible(preferenceViewHolder, true);
    }
}
